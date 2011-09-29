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
package org.jdesktop.wonderland.clientlistenertest.client;

import java.util.logging.Logger;
import org.jdesktop.wonderland.client.comms.BaseConnection;
import org.jdesktop.wonderland.client.comms.LoginParameters;
import org.jdesktop.wonderland.client.comms.WonderlandServerInfo;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.comms.WonderlandSessionImpl;
import org.jdesktop.wonderland.clientlistenertest.common.TestClientType;
import org.jdesktop.wonderland.clientlistenertest.common.TestMessageThree;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.Message;

/**
 * Simple client
 * @author jkaplan
 */
public class ClientMain {

    /** logger */
    private static final Logger logger =
            Logger.getLogger(ClientMain.class.getName());
    
    // the server info
    private WonderlandServerInfo serverInfo;
    
    // whether we are done
    boolean finished = false;

    /**
     * Create a new client
     * @param serverInfo the information about the server
     */
    public ClientMain(WonderlandServerInfo serverInfo) {
        this.serverInfo = serverInfo;
    }

    /**
     * Run the test
     */
    public void runTest() throws Exception {
        // read the username and properties from files
        String username = System.getProperty("sgs.user", "sample");
        String password = System.getProperty("sgs.password", "sample");

        // create the session & login
        WonderlandSession session = new WonderlandSessionImpl(serverInfo);
        session.login(new LoginParameters(username, password.toCharArray()));
           
        logger.info("Login suceeded");
     
        // attach client
        session.connect(new TestOneClient());
        
        // wait for end-session message
        waitForFinish();
    }
    
    
    public synchronized void waitForFinish() throws InterruptedException {
        while (!finished) {
            wait();
        }
    }
    
    protected synchronized void setFinished() {
        finished = true;
        notify();
    }
    
    public static void main(String[] args) {
        // read server and port from properties
        String server = System.getProperty("sgs.server", "locahost");
        int port = Integer.parseInt(System.getProperty("sgs.port", "1139"));

        // create a login information object
        WonderlandServerInfo serverInfo = new WonderlandServerInfo(server, port);

        // the main client
        ClientMain cm = new ClientMain(serverInfo);

        try {
            cm.runTest();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    class TestOneClient extends BaseConnection {
        public ConnectionType getConnectionType() {
            return TestClientType.CLIENT_ONE_TYPE;
        }
  
        @Override
        public void handleMessage(Message message) {
            logger.info("Received message: " + message);
        
            if (message instanceof TestMessageThree) {
                setFinished();
            }
        }
    }
}
