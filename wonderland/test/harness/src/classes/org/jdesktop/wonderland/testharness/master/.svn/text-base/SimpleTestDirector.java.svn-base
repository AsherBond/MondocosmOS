/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * Sun designates this particular file as subject to the "Classpath" 
 * exception as provided by Sun in the License file that accompanied 
 * this code.
 */
package org.jdesktop.wonderland.testharness.master;

import com.jme.math.Vector3f;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.testharness.common.ClientLoginRequest;
import org.jdesktop.wonderland.testharness.common.TestReply;
import org.jdesktop.wonderland.testharness.manager.common.CommsHandler;
import org.jdesktop.wonderland.testharness.manager.common.ManagerMessage;
import org.jdesktop.wonderland.testharness.manager.common.SimpleTestDirectorMessage;
import org.jdesktop.wonderland.testharness.manager.common.SimpleTestDirectorMessage.UserActionType;
import org.jdesktop.wonderland.testharness.master.SlaveConnection.SlaveConnectionListener;

/**
 *
 * @author paulby
 */
public class SimpleTestDirector implements TestDirector {

    private ArrayList<SlaveInfo> slaves = new ArrayList();
    private final HashMap<String, User> users = new LinkedHashMap();

    private String audioFile = null;

    private Logger logger = Logger.getLogger(SimpleTestDirector.class.getName());
    
    private int targetUsers = 1;
    private int slaveCount = 0; // Slaves currently in use by this director

    private static final String USER_MANAGER_PROP = "usermanager";
    private static final String USER_MANAGER_DEFAULT = 
            "org.jdesktop.wonderland.testharness.master.GroupUserManagerImpl";
    private final UserManager userManager;

    private SlaveAllocator allocator = new RoundRobinSlaveAllocator();

    private CommsHandler commsHandler;
    
    public SimpleTestDirector(CommsHandler commsHandler, Properties props) {
        this.commsHandler = commsHandler;

        // create the user manager
        String umClass = props.getProperty(USER_MANAGER_PROP, USER_MANAGER_DEFAULT);
        userManager = createObject(umClass, UserManager.class);
        System.out.println("Initialized user manager: " + umClass + " " + userManager);
        userManager.initialize(props);

        audioFile = props.getProperty("slave.audio.file");

        commsHandler.addMessageListener(SimpleTestDirectorMessage.class, new CommsHandler.MessageListener() {

            public void messageReceived(ManagerMessage msg) {
                assert(msg instanceof SimpleTestDirectorMessage);
                SimpleTestDirectorMessage message = (SimpleTestDirectorMessage) msg;
                
                System.err.println("TestDirector received "+message.getMessageType());
                
                switch(message.getMessageType()) {
                    case REQUEST_STATUS :
                        sendUIUpdate();
                        String[] usernames = new String[users.size()];
                        UserActionType[] currentActions = new UserActionType[users.size()];
                        int i=0;
                        for(User u : users.values()) {
                            usernames[i] = u.getUsername();
                            currentActions[i] = u.getCurrentAction();
                            i++;
                        }
                        sendUIMessage(SimpleTestDirectorMessage.newUserListMessage(usernames, currentActions));
                        break;
                    case USER_COUNT :
                        targetUsers = message.getDesiredUserCount();
                        adjustUsers();
                        sendUIUpdate();
                        break;
                    case CHANGE_ALLOCATOR:
                        changeAllocator(message.getAllocatorName(),
                                        message.getProperties());
                        break;
                    case USER_ACTION_CHANGE_REQUEST :
                        User user = users.get(message.getUsername());
                        if (user!=null) {
                            userManager.changeUserAction(user, message.getUserAction());
                        }
                        break;
                    default :
                        System.err.println("Unexepected message type "+message.getMessageType());
                }

            }
        });
    }

    private <T> T createObject(String name, Class<T> clazz) {
        try {
            Class<T> create = (Class<T>) Class.forName(name);
            return create.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void sendUIUpdate() {
        try {
            commsHandler.send(SimpleTestDirectorMessage.newUIUpdate(users.size(), targetUsers));
        } catch (IOException ex) {
            Logger.getLogger(SimpleTestDirector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sendUIMessage(SimpleTestDirectorMessage msg) {
        try {
            commsHandler.send(msg);
        } catch (IOException ex) {
            Logger.getLogger(SimpleTestDirector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void changeAllocator(String allocatorName,
                                 Properties props)
    {
        SlaveAllocator out = null;

        if (allocatorName.equalsIgnoreCase("RoundRobin")) {
            out = new RoundRobinSlaveAllocator();
        } else if (allocatorName.equalsIgnoreCase("Fixed")) {
            out = new FixedMaximumSlaveAllocator();
        } else {
            Logger.getLogger(SimpleTestDirector.class.getName()).warning("Unknown allocator " +
                                                                         allocatorName);
            return;
        }

        out.configure(props);
        allocator = out;
    }

    private void adjustUsers() {
        System.err.println("adjustUsers "+targetUsers+"  "+users.size());
        // add users if necessary
        while (targetUsers > users.size() && addUser()) {
            // do nothing -- addUser() did the actual work
            try { Thread.sleep(500); } catch (InterruptedException ie) {}
        }

        // remove users if necessary
        while (targetUsers < users.size() && removeUser()) {
            // do nothing -- removeUser() did the actual work
            try { Thread.sleep(500); } catch (InterruptedException ie) {}
        }
    }

    private boolean addUser() {
        synchronized(users) {
            SlaveInfo slave = allocator.findSlave();
            System.err.println("ADDING USER TO SLAVE "+slave);
            if (slave == null) {
                return false;
            }

            User user = createUser(slave);
            slave.add(user);
            sendUIMessage(SimpleTestDirectorMessage.newUserAddedMessage(user.getUsername(), true));
        }
        return true;
    }

    private boolean removeUser() {
        synchronized(users) {
            User firstUser = users.values().iterator().next();
            firstUser.disconnect();
            destroyUser(firstUser);
        
            SlaveInfo slave = findSlave(firstUser);
            if (slave != null) {
                slave.remove(firstUser);
            }
        }


        return true;
    }

    /**
     * Find the slave containing the given user, or return null if no
     * slave contains the current user.
     * @param user the user to look for
     * @return the slave containing the given user
     */
    private SlaveInfo findSlave(User user) {
        for (SlaveInfo slave : slaves) {
            if (slave.contains(user)) {
                return slave;
            }
        }
        
        return null;
    }

    public boolean slaveJoined(SlaveConnection slaveConnection) {
        SlaveInfo slaveInfo = new SlaveInfo(slaveConnection);
        slaves.add(slaveInfo);
        slaveCount++;

        adjustUsers();
        sendUIUpdate();

        return true; // We used the slave so return true
    }
    
    private User createUser(SlaveInfo slaveInfo) {
        UserContextImpl context = new UserContextImpl(slaveInfo);
        User user = userManager.createUser(UsernameManager.getUniqueUsername(), context);
        users.put(user.getUsername(), user);
        String serverURL = MasterMain.getMaster().getSgsServerName();
        
        Properties props = new Properties();
        props.setProperty("serverURL", serverURL);
//        ClientLoginRequest lr = new ClientLoginRequest("client3D.Client3DSim", props,
//                                           user.getUsername());
        props.setProperty("testharness.actorPort", Integer.toString(context.getActorPort()));

        if (audioFile != null) {
            props.setProperty("slave.audio.file", audioFile);
        }

        ClientLoginRequest lr = new ClientLoginRequest("webstart.WebstartClientWrapper", props,
                                           user.getUsername());
        System.err.println("Send login request "+user.getUsername());
        slaveInfo.getConnection().send(lr);

        return user;
    }

    private void destroyUser(User user) {
        userManager.destroyUser(user);
        users.remove(user.getUsername());

        System.err.println("Removing " + user.getUsername());
        sendUIMessage(SimpleTestDirectorMessage.newUserAddedMessage(user.getUsername(), false));
    }
    
    class UserContextImpl implements UserContext {
        private SlaveInfo slaveInfo;
        private int actorPort;

        public UserContextImpl(SlaveInfo slaveInfo) {
            this.slaveInfo = slaveInfo;
            this.actorPort = slaveInfo.getNextActorPort();
        }

        public SlaveConnection getConnection() {
            return slaveInfo.getConnection();
        }

        private int getActorPort() {
            return actorPort;
        }

        public void sendUIMessage(SimpleTestDirectorMessage msg) {
            SimpleTestDirector.this.sendUIMessage(msg);
        }

        public void cleanup() {
            slaveInfo.freeActorPort(actorPort);
        }
    }
    
    class SlaveInfo extends ArrayList<User> implements SlaveConnectionListener, SlaveConnection.TestReplyListener {
        private SlaveConnection slaveConnection;

        private final LinkedList<Integer> freeActorPorts = new LinkedList<Integer>();
        private int nextActorPort = 15432;
        
        public SlaveInfo(SlaveConnection slaveConnection) {
            this.slaveConnection = slaveConnection;

            slaveConnection.addConnectionListener(this);
            slaveConnection.addReplyListener(this);
        }
        
        public SlaveConnection getConnection() {
            return slaveConnection;
        }

        public int getUserCount() {
            return size();
        }

        public void disconnected(SlaveConnection connection) {
            slaves.remove(this);
            slaveCount--;

            for (User user : this) {
                user.getContext().cleanup();
                destroyUser(user);
            }

            sendUIUpdate();
        }

        public int getNextActorPort() {
            synchronized(freeActorPorts) {
                Integer port = freeActorPorts.peekFirst();
                if (port!=null) {
                    freeActorPorts.removeFirst();
                    return port.intValue();
                }

                return nextActorPort++;
            }
        }

        /**
         * Return the specified port to the free list, so it can be reused
         * @param port
         */
        public void freeActorPort(int port) {
            synchronized(freeActorPorts) {
                freeActorPorts.add(port);
            }
        }

        public void received(TestReply reply) {
            System.err.println("SlaveInfo received "+reply);
            User u = users.get(reply.getUsername());
            if (u==null) {
                logger.warning("Reply for unknown user "+reply.getUsername());
            } else {
                u.processReply(reply);
            }
        }
    }

    interface SlaveAllocator {
        /** configure with the given properties */
        public void configure(Properties props);

        /** find the next available slave */
        public SlaveInfo findSlave();
    }

    class RoundRobinSlaveAllocator implements SlaveAllocator {
        public void configure(Properties props) {
            return;
        }

        public SlaveInfo findSlave() {
            if (slaves.isEmpty()) {
                return null;
            }

            // first, go through and find the maximum number of
            // users on any one slave
            int max = 0;
            for (SlaveInfo slave : slaves) {
                if (slave.size() > max) {
                    max = slave.size();
                }
            }

            // now find the the first slave with fewer than max
            // clients
            SlaveInfo out = null;
            for (SlaveInfo slave : slaves) {
                if (slave.size() < max) {
                    out = slave;
                    break;
                }
            }

            // no luck -- just pick the first
            if (out == null) {
                out = slaves.get(0);
            }
            
            // all set
            return out;
        }
    }

    class FixedMaximumSlaveAllocator implements SlaveAllocator {
        private int max = 10;

        public void configure(Properties props) {
            if (props.containsKey("max")) {
                max = Integer.parseInt(props.getProperty("max"));
            }
        }

        public SlaveInfo findSlave() {
            for (SlaveInfo slave : slaves) {
                if (slave.size() < max) {
                    return slave;
                }
            }

            return null;
        }
    }
}
