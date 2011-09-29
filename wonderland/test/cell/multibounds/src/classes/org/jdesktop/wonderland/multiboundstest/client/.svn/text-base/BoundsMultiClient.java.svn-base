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
package org.jdesktop.wonderland.multiboundstest.client;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.cell.view.LocalAvatar;
import org.jdesktop.wonderland.client.comms.LoginParameters;
import org.jdesktop.wonderland.client.comms.SessionStatusListener;
import org.jdesktop.wonderland.client.comms.WonderlandServerInfo;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.comms.WonderlandSession.Status;
import org.jdesktop.wonderland.client.comms.CellClientSession;

/**
 *
 * @author jkaplan
 */
public class BoundsMultiClient
        implements SessionStatusListener
{
    /** a logger */
    private static final Logger logger = 
            Logger.getLogger(BoundsMultiClient.class.getName());
    
    // properties
    private Properties props;
    
    // standard properties
    private static final String SERVER_NAME_PROP = "sgs.server";
    private static final String SERVER_PORT_PROP = "sgs.port";
    private static final String USER_NAME_PROP   = "multibounds.username";
    private static final String GROUP_SIZE_PROP  = "multibounds.group.size";
    private static final String NUM_GROUPS_PROP  = "multibounds.group.num";
    private static final String SEPARATION_PROP  = "multibounds.group.separation";
    private static final String IDLE_PROP        = "multibounds.idle";
    private static final String X_PROP           = "multibounds.x";
    private static final String Y_PROP           = "multibounds.y";
    private static final String Z_PROP           = "multibounds.z";
    
            
    // default values
    private static final String SERVER_NAME_DEFAULT = "localhost";
    private static final String SERVER_PORT_DEFAULT = "1139";
    private static final String USER_NAME_DEFAULT   = "test";
    private static final String GROUP_SIZE_DEFAULT  = "3";
    private static final String NUM_GROUPS_DEFAULT  = "5";
    private static final String SEPARATION_DEFAULT  = "7";
    private static final String IDLE_DEFAULT        = "0";
    private static final String X_DEFAULT           = "0";
    private static final String Y_DEFAULT           = "0";
    private static final String Z_DEFAULT           = "10";
    
    /** the name of this client */
    private String name;
    
    /** the mover thread */
    private MoverThread mover;
    
    
    private static int groupSize;       // Number of users in each group
    private static int numGroups;       // Total number of groups
    private static int percentageIdle;  // Not implemented
    private static int startedClients = 0; // Number of clients currently started
    private static float groupSeparation;  // Distance between each group
    
    public BoundsMultiClient(Properties props,
                             WonderlandServerInfo server, 
                             LoginParameters login) 
        throws Exception
    {
        this.props = props;
        this.name = login.getUserName();
        
        int group = startedClients/groupSize;
        
        System.err.println("USER "+startedClients+" group "+group);
        
        startedClients++;
        
        // login
        CellClientSession session = new CellClientSession(server);
        session.addSessionStatusListener(this);
        session.login(login);
        
        logger.info(getName() + " login succeeded");
        
        LocalAvatar avatar = session.getLocalAvatar();
        
        // pick a direction to move
//        int dir = (int) Math.random() * 10;
//        dir = 5;
//        if (dir < 2) {
//            mover = new RandomMover(avatar);
//        } else if (dir < 6) {
//            mover = new XMover(avatar);
//        } else {
//            mover = new YMover(avatar);
//        }
        
        
        // load initial coordinates
        float x = Float.parseFloat(props.getProperty(X_PROP, X_DEFAULT));
        float y = Float.parseFloat(props.getProperty(Y_PROP, Y_DEFAULT));
        float z = Float.parseFloat(props.getProperty(Z_PROP, Z_DEFAULT));
       
        mover = new GroupMover(new Vector3f(x + (group*groupSeparation), y, z), 
                               avatar);
        
        mover.start();
    }
    
    public String getName() {
        return name;
    }
    
    public void sessionStatusChanged(WonderlandSession session, 
                                     Status status)
    {
        logger.info(getName() + " change session status: " + status);
        if (status == Status.DISCONNECTED  && mover != null) {
            mover.quit();
        }
    }
    
    public void waitForFinish() throws InterruptedException {
        if (mover == null) {
            return;
        }
        
        // wait for the thread to end
        mover.join();
    }
        
    public static void main(String[] args) {
        int buildNumber = Integer.parseInt(args[0]);
        
        // load properties
        Properties props;
        if (args.length == 2) {
            props = loadProperties(args[1]);
        } else {
            props = loadProperties(null);
        }
        
        String serverName = props.getProperty(SERVER_NAME_PROP,
                                              SERVER_NAME_DEFAULT);
        String serverPort = props.getProperty(SERVER_PORT_PROP,
                                              SERVER_PORT_DEFAULT);
        String userName   = props.getProperty(USER_NAME_PROP,
                                              USER_NAME_DEFAULT);
       
        // create server login information
        WonderlandServerInfo server = new WonderlandServerInfo(serverName, 
                                               Integer.parseInt(serverPort));

        // read setup properties
        groupSize = Integer.parseInt(props.getProperty(GROUP_SIZE_PROP, 
                                                       GROUP_SIZE_DEFAULT));
        numGroups = Integer.parseInt(props.getProperty(NUM_GROUPS_PROP, 
                                                       NUM_GROUPS_DEFAULT));
        groupSeparation = Float.parseFloat(props.getProperty(SEPARATION_PROP,
                                                             SEPARATION_DEFAULT));
        percentageIdle = Integer.parseInt(props.getProperty(IDLE_PROP,
                                                            IDLE_DEFAULT));
                
        int count = groupSize * numGroups;
        
        BoundsMultiClient[] bmc = new BoundsMultiClient[count];
        
        for (int i = 0; i < count; i++) {
            LoginParameters login = 
                    new LoginParameters(userName + buildNumber+ "_" + i, 
                                        "test".toCharArray());
            
            try {
                bmc[i] = new BoundsMultiClient(props, server, login);
                Thread.sleep(1500);
            } catch (Exception ex) {
                logger.log(Level.WARNING, "Error logging in", ex);
            }
        }
        
        // wait for each client 
        try {   
            for (BoundsMultiClient client : bmc) {
                client.waitForFinish();
            }
        } catch (InterruptedException ie) {
        }
    }

    private static Properties loadProperties(String fileName) {
        // start with the system properties
        Properties props = new Properties(System.getProperties());
    
        // load the given file
        if (fileName != null) {
            try {
                props.load(new FileInputStream(fileName));
            } catch (IOException ioe) {
                logger.log(Level.WARNING, "Error reading properties from " +
                           fileName, ioe);
            }
        }
        
        return props;
    }
    
    abstract class MoverThread extends Thread {
        protected Vector3f location = new Vector3f();
        private Quaternion orientation = null;
        private LocalAvatar avatar;
        private boolean quit = false;
        private long sleepTime = 200;
                
        public MoverThread(LocalAvatar avatar) {
            this.avatar = avatar;
            
        }

        public synchronized boolean isQuit() {
            return quit;
        }
        
        public synchronized void quit() {
            this.quit = true;
        }
        
        @Override
        public void run() {
            randomPosition();
                            
            while(!isQuit()) {
                nextPosition();
                avatar.localMoveRequest(location, orientation);
                try {
                    sleep(sleepTime);
                } catch (InterruptedException ex) {
                    Logger.getLogger(BoundsMultiClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        
        protected void randomPosition() {
            location.x = FastMath.rand.nextFloat()*50;
            location.z = FastMath.rand.nextFloat()*50;
        }
        
        protected abstract void nextPosition();
        
    }
    
    class RandomMover extends MoverThread {
        public RandomMover(LocalAvatar avatar) {
            super (avatar);
        }
        
        @Override
        protected void nextPosition() {
            randomPosition();
        }
    }
    
    class XMover extends MoverThread {
        private int dir = 1;
        public XMover(LocalAvatar avatar) {
            super (avatar);
        }
        
        @Override
        protected void nextPosition() {
            location.x += dir;
            dir *= -1;
        }
    }
    
    class YMover extends MoverThread {
        public YMover(LocalAvatar avatar) {
            super (avatar);
        }
        
        @Override
        protected void nextPosition() {
            location.y = Math.abs(location.y++) % 50;
        }
    }
    
    class GroupMover extends MoverThread {
        private Vector3f groupCenter;
        private int dir = 1;
        
        public GroupMover(Vector3f groupCenter, LocalAvatar avatar) {
            super(avatar);
            this.groupCenter = groupCenter;
        }

        @Override
        protected void nextPosition() {
            location.x += dir;
            dir *= -1;
        }
        
        @Override
        protected void randomPosition() {
            // Not so random
            location.set(groupCenter);
        }
    }

    
}
