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

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.LogControl;
import org.jdesktop.wonderland.testharness.manager.common.CommsHandler;
import org.jdesktop.wonderland.testharness.manager.common.CommsHandler.MessageListener;
import org.jdesktop.wonderland.testharness.manager.common.ManagerMessage;
import org.jdesktop.wonderland.testharness.manager.common.MasterStatus;
import org.jdesktop.wonderland.testharness.master.SlaveConnection.SlaveConnectionListener;

/**
 *
 * @author paulby
 */
public class MasterMain {
    
    public static final int MANAGER_PORT = 5567;

    private final Set<SlaveConnection> activeSlaves = new HashSet();
    private final Set<SlaveConnection> passiveSlaves = new HashSet();
    
    private Properties props;
    
    private String sgsServerName;
    private int sgsServerPort;
    
    // standard properties
    private static final String SERVER_NAME_PROP = "sgs.server";
    private static final String SERVER_PORT_PROP = "sgs.port";
    
            
    // default values
    private static final String SERVER_NAME_DEFAULT = "localhost";
    private static final String SERVER_PORT_DEFAULT = "1139";
    
    private static MasterMain masterMain=null;
    
    private ManagerController manager;
    
    private int slaveID = 0;
    
    static {
        new LogControl(MasterMain.class, "/org/jdesktop/wonderland/testharness/master/resources/logging.properties");
    }
    
    public MasterMain(String[] args) {
        masterMain = this;
        if (args.length<2) {
            System.err.println("Usage: SlaveMain <master port> <property filename>");
            System.exit(1);
        }
        
        int masterPort = Integer.parseInt(args[0]);
        
        props = loadProperties(args[1]);
        sgsServerName = props.getProperty(SERVER_NAME_PROP,
                                              SERVER_NAME_DEFAULT);
        sgsServerPort = Integer.valueOf(props.getProperty(SERVER_PORT_PROP,
                                              SERVER_PORT_DEFAULT));
        
        manager = new ManagerController();
        manager.start();
        
        TestDirector director = new SimpleTestDirector(manager, props);
        manager.addTestDirector(director);
        try {
            ServerSocket serverSocket = new ServerSocket(masterPort);
            while(true) {
                Socket s = serverSocket.accept();
                SlaveConnection slaveController = new SlaveConnection(s, slaveID++); 
                slaveController.addConnectionListener(new SlaveConnectionListener() {
                    public void disconnected(SlaveConnection connection) {
                        slaveLeft(connection);
                    }
                });

                if (director.slaveJoined(slaveController)) {
                    // Director is using the slave
                    synchronized(activeSlaves) {
                        activeSlaves.add(slaveController);
                    }
                    slaveController.setDirector(director);
                } else {
                    // Director did not want slave
                    synchronized(passiveSlaves) {
                        passiveSlaves.add(slaveController);
                    }
                }
                manager.sendStatusMessage(activeSlaves.size(), passiveSlaves.size());
            }
        } catch (IOException ex) {
            Logger.getLogger(MasterMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /**
     * The test director is requesting another slave. This call returns immediately
     * but at some point in the future the TestDirectors slaveJoined(..) method will
     * be called with a slave.
     * @param testDirector
     */
    public void requestSlave(final TestDirector testDirector) {
        new Thread() {
            @Override
            public void run() {
                synchronized(passiveSlaves) {
                    Iterator<SlaveConnection> it = passiveSlaves.iterator();
                    if (it.hasNext()) {
                        SlaveConnection c = it.next();
                        if (testDirector.slaveJoined(c)) {
                            passiveSlaves.remove(c);
                            synchronized(activeSlaves) {
                                activeSlaves.add(c);
                                manager.sendStatusMessage(activeSlaves.size(), passiveSlaves.size());
                            }
                        }
                    }
                }
                
            }
        }.start();
    }
    
    public static MasterMain getMaster() {
        return masterMain;
    }
    
    public String getSgsServerName() {
        return sgsServerName;
    }
    
    public int getSgsPort() {
        return sgsServerPort;
    }
    
    /**
     * Called when a slave connection is lost
     * @param slaveConnection
     */
    void slaveLeft(SlaveConnection slaveConnection) {
        if (slaveConnection.getDirector()==null) {
            passiveSlaves.remove(slaveConnection);
        } else {
            activeSlaves.remove(slaveConnection);
        }
        manager.sendStatusMessage(activeSlaves.size(), passiveSlaves.size());
    }
    
    private static Properties loadProperties(String fileName) {
        // start with the system properties
        Properties props = new Properties(System.getProperties());
    
        // load the given file
        if (fileName != null) {
            try {
                props.load(new FileInputStream(fileName));
            } catch (IOException ioe) {
                Logger.getAnonymousLogger().log(Level.WARNING, "Error reading properties from " +
                           fileName, ioe);
            }
        }
        
        return props;
    }
    
    /**
     * Manage the connected managers
     */
    class ManagerController extends Thread implements CommsHandler {
        private final HashSet<ManagerConnection> connections = new HashSet();
        private final ArrayList<TestDirector> testDirectors = new ArrayList();
        private final HashMap<Class, LinkedList<MessageListener>> messageListeners = new HashMap();
        
        private MasterStatus lastStatusMessage = null;
        
        public ManagerController() {
            
        }
        
        @Override
        public void run() {
            try {
                ServerSocket ss = new ServerSocket(MANAGER_PORT);
                while (true) {
                    try {
                        System.err.println("Listening for manager connection");
                        Socket s = ss.accept();
                        ManagerConnection connection = new ManagerConnection(s);
                        
                        Set<Map.Entry<Class, LinkedList<MessageListener>>> set = messageListeners.entrySet();
                        for(Map.Entry<Class, LinkedList<MessageListener>> entry : set) {
                            for(MessageListener listener : entry.getValue()) {
                                connection.addMessageListener(entry.getKey(), listener);
                            }
                        }
                        synchronized(connection) {
                            connections.add(connection);
                        }
                        if (lastStatusMessage != null) {
                            connection.send(lastStatusMessage);
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(MasterMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(MasterMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        void addTestDirector(TestDirector testDirector) {
            testDirectors.add(testDirector);
        }
        
        void sendStatusMessage(int activeSlaves, int passiveSlaves) {
            lastStatusMessage = new MasterStatus(activeSlaves, passiveSlaves);
            try {
                send(lastStatusMessage);
            } catch (IOException ex) {
                Logger.getLogger(MasterMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        /**
         * Notification that a manager has closed
         * @param manager
         */
        void managerClosed(ManagerConnection manager) {
            synchronized(connections) {
                connections.remove(manager);
            }
        }

        public void send(ManagerMessage message) throws IOException {
            synchronized(connections) {
                for(ManagerConnection l : connections)
                    l.send(message);               
            }
        }

        public void addMessageListener(Class msgClass, MessageListener listener) {
            LinkedList<MessageListener> list = messageListeners.get(msgClass);
            if (list==null) {
                list = new LinkedList();
                messageListeners.put(msgClass, list);
            }
            list.add(listener);
            
            synchronized(connections) {
                for(ManagerConnection l : connections)
                    l.addMessageListener(msgClass, listener);               
            }
        }
    }
    
    class ManagerConnection extends Thread {
        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private boolean done = false;
        private HashMap<Class, LinkedList<MessageListener>> messageListeners = new HashMap();
        
        public ManagerConnection(Socket s) {
            socket = s;
            System.out.println("Started ManagerListener");
            try {
                in = new ObjectInputStream(socket.getInputStream());
                out = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException ex) {
                Logger.getLogger(MasterMain.class.getName()).log(Level.SEVERE, null, ex);
            }
            start();
        }
        
        @Override
        public void run() {

            while (!done) {
                try {
                    ManagerMessage msg = (ManagerMessage) in.readObject();
                    System.out.println("RECEIVED "+msg);
                    LinkedList<MessageListener> listeners = messageListeners.get(msg.getClass());
                    if (listeners!=null) {
                        for(MessageListener l : listeners)
                            l.messageReceived(msg);
                    }
                    
                } catch(EOFException eofe) {
                    manager.managerClosed(this);
                    done=true;
                } catch (IOException ex) {
                    Logger.getLogger(MasterMain.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(MasterMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        private void send(ManagerMessage msg) {
            synchronized(out) {
                System.out.println("MasterMain sending "+msg);
                try {
                    out.writeObject(msg);
                } catch (IOException ex) {
                    Logger.getLogger(MasterMain.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        /**
         * Add listener for messages to this director
         * @param testDirector
         */
        void addMessageListener(Class msgClass, MessageListener testDirector) {
            LinkedList<MessageListener> list = messageListeners.get(msgClass);
            if (list==null) {
                list = new LinkedList();
                messageListeners.put(msgClass, list);
                System.out.println("REGISTERING "+msgClass);
            }
            list.add(testDirector);
        }
    }
    
    public static void main(String[] args) {
        new MasterMain(args);
    }
}
