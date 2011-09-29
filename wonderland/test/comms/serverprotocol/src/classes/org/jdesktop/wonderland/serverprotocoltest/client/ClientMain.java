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
package org.jdesktop.wonderland.serverprotocoltest.client;

import com.sun.sgs.client.ClientChannel;
import com.sun.sgs.client.ClientChannelListener;
import com.sun.sgs.client.simple.SimpleClient;
import com.sun.sgs.client.simple.SimpleClientListener;
import java.io.IOException;
import java.net.PasswordAuthentication;
import java.nio.ByteBuffer;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.comms.WonderlandServerInfo;
import org.jdesktop.wonderland.common.comms.ProtocolVersion;
import org.jdesktop.wonderland.common.comms.SessionInternalConnectionType;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.common.messages.MessageID;
import org.jdesktop.wonderland.common.messages.MessagePacker;
import org.jdesktop.wonderland.common.messages.MessagePacker.PackerException;
import org.jdesktop.wonderland.common.messages.MessagePacker.ReceivedMessage;
import org.jdesktop.wonderland.common.messages.OKMessage;
import org.jdesktop.wonderland.common.messages.ProtocolSelectionMessage;
import org.jdesktop.wonderland.common.messages.ResponseMessage;
import org.jdesktop.wonderland.serverprotocoltest.common.BadProtocolVersion;
import org.jdesktop.wonderland.serverprotocoltest.common.TestProtocolVersion;

/**
 * Simple client
 * @author jkaplan
 */
public class ClientMain {

    /** logger */
    private static final Logger logger =
            Logger.getLogger(ClientMain.class.getName());    // the server info
    private WonderlandServerInfo serverInfo;    // whether we are done
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

        testGoodSession(username, password);
        testBadSession(username, password);
    }

    public void testGoodSession(final String username, final String password)
            throws IOException, InterruptedException
    {
        logger.info("Test good session");

        // create the client & login
        GoodSessionListener listener = new GoodSessionListener(username, password);
        SimpleClient sc = new SimpleClient(listener);
        listener.setSession(sc);
        
        // log in
        Properties props = new Properties();
        props.setProperty("host", serverInfo.getHostname());
        props.setProperty("port", Integer.toString(serverInfo.getSgsPort()));
        sc.login(props);

        boolean success = listener.waitForLogin();
        assert success : "Good login failed";
        
        sc.logout(true);
    }

    public void testBadSession(String username, String password) 
        throws IOException, InterruptedException
    {
        logger.info("Test bad session");
        
        // create the client & login
        BadSessionListener listener = new BadSessionListener(username, password);
        SimpleClient sc = new SimpleClient(listener);
        listener.setSession(sc);
        
        // log in
        Properties props = new Properties();
        props.setProperty("host", serverInfo.getHostname());
        props.setProperty("port", Integer.toString(serverInfo.getSgsPort()));
        sc.login(props);

        boolean success = listener.waitForLogin();
        assert !success : "Bad login succeeded";
        
        sc.logout(true);
    }

    
    class GoodSessionListener extends TestListener {
        
        public GoodSessionListener(String username, String password) {
            super (username, password);
        }

        @Override
        protected String getProtocolName() {
            return TestProtocolVersion.PROTOCOL_NAME;
        }

        @Override
        protected ProtocolVersion getProtocolVersion() {
            return TestProtocolVersion.VERSION;
        }
    }

     class BadSessionListener extends TestListener {
        
        public BadSessionListener(String username, String password) {
            super (username, password);
        }

        @Override
        protected String getProtocolName() {
            return BadProtocolVersion.PROTOCOL_NAME;
        }

        @Override
        protected ProtocolVersion getProtocolVersion() {
            return BadProtocolVersion.VERSION;
        }
    }
    
    abstract class TestListener implements SimpleClientListener {
        private SimpleClient session;  
        private String username;
        private String password;
        private boolean loginInProgress = true;
        private boolean loginSuccess = false;
        private MessageID messageID;
        
        public TestListener(String username, String password) {
            this.username = username;
            this.password = password;
        }
        
        public void setSession(SimpleClient session) {
            this.session = session;
        }
        
        protected SimpleClient getSession() {
            return session;
        }
        
        public synchronized boolean waitForLogin() throws InterruptedException {
            while (loginInProgress) {
                wait();
            }
            
            return loginSuccess;
        }
        
        protected abstract String getProtocolName();
        protected abstract ProtocolVersion getProtocolVersion();
        
        public PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password.toCharArray());
        }

        public void loggedIn() {
            // send the protocol selection message
            ProtocolSelectionMessage psm = 
                    new ProtocolSelectionMessage(getProtocolName(),
                                                 getProtocolVersion());
            messageID = psm.getMessageID();
            
            try {
                getSession().send(MessagePacker.pack(psm, 
                        SessionInternalConnectionType.SESSION_INTERNAL_CLIENT_ID));
            } catch (PackerException pe) {
                logger.log(Level.WARNING, "Error packing message", pe);
                loginFailed("Error packing message");
            } catch (IOException ioe) {
                logger.log(Level.WARNING, "Error sending message", ioe);
                loginFailed("Error sending message");
            }
        }

        public synchronized void loginFailed(String reason) {
            loginInProgress = false;
            loginSuccess = false;
            notifyAll();
        }

        public synchronized void disconnected(boolean force, String reason) {
            loginInProgress = false;
            loginSuccess = false;
            notifyAll();
        }
        
        public ClientChannelListener joinedChannel(ClientChannel channel) {
            throw new UnsupportedOperationException("Not supported.");
        }

        public void receivedMessage(ByteBuffer message) { 
            Message m = null;
            
            try {
                ReceivedMessage rm = MessagePacker.unpack(message);
                m = rm.getMessage();
           
                // check the response
                assert m instanceof ResponseMessage : "Received invalid response " + m; 
                assert m.getMessageID().equals(messageID) : "Bad ID in response " + m;
            } catch (PackerException pe) {
                logger.log(Level.WARNING, "Error unpacking", pe);
                assert false : "Error unpacking message";
            }
            
            synchronized (this) {
                loginInProgress = false;
                
                // figure out if we succeeded
                loginSuccess = (m instanceof OKMessage);
                
                notifyAll();
            }
        }

        public void reconnecting() {
            throw new UnsupportedOperationException("Not supported.");
        }

        public void reconnected() {
            throw new UnsupportedOperationException("Not supported.");
        }
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
}
