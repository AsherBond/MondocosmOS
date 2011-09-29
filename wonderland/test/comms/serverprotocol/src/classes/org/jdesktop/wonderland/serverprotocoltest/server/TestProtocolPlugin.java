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
package org.jdesktop.wonderland.serverprotocoltest.server;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.Task;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.comms.ProtocolVersion;
import org.jdesktop.wonderland.server.ServerPlugin;
import org.jdesktop.wonderland.server.WonderlandContext;
import org.jdesktop.wonderland.server.comms.CommsManager;
import org.jdesktop.wonderland.server.comms.CommunicationsProtocol;
import org.jdesktop.wonderland.serverprotocoltest.common.TestProtocolVersion;

/**
 * Sample plugin that doesn't do anything
 * @author jkaplan
 */
public class TestProtocolPlugin implements ServerPlugin {
    private static final Logger logger =
            Logger.getLogger(TestProtocolPlugin.class.getName());
    
    public void initialize() {
        CommsManager cm = WonderlandContext.getCommsManager();
        cm.registerProtocol(new TestProtocol());
    
        // check if the protocol exists
        AppContext.getTaskManager().scheduleTask(new CheckProtocolTask());
    }
    
    public static class TestProtocol 
            implements CommunicationsProtocol, Serializable
    {
        private ProtocolVersion version = TestProtocolVersion.VERSION;
        
        public String getName() {
            return TestProtocolVersion.PROTOCOL_NAME;
        }

        public ProtocolVersion getVersion() {
            return version;
        }

        public ClientSessionListener createSessionListener(ClientSession session, 
                                                           ProtocolVersion version) 
        {
            return new TestProtocolSessionListener(session);
        }
    }
    
    public static class CheckProtocolTask implements Task, Serializable {
        public void run() throws Exception {
            logger.info("Run check protocol task");
            
            CommsManager cm = WonderlandContext.getCommsManager();
            CommunicationsProtocol cp = cm.getProtocol(TestProtocolVersion.PROTOCOL_NAME);
            
            assert cp != null;
            assert cp.getName().equals(TestProtocolVersion.PROTOCOL_NAME);
            assert cp.getVersion().equals(TestProtocolVersion.VERSION);
        }
    }
    
    public static class TestProtocolSessionListener
            implements ClientSessionListener, Serializable
    {
        private static final Logger logger =
                Logger.getLogger(TestProtocolSessionListener.class.getName());
        
        private ManagedReference<ClientSession> sessionRef;

        private TestProtocolSessionListener(ClientSession session) {
            logger.info("New session for " + session.getName());
            
            DataManager dm = AppContext.getDataManager();
            sessionRef = dm.createReference(session);
        }
            
        public void receivedMessage(ByteBuffer data) {
            logger.info("Received " + data.remaining() + " bytes from " +
                        getSession().getName() + ".");
        }

        public void disconnected(boolean forced) {
            logger.info("Session " + getSession().getName() + " disconnected.");
        } 
        
        private ClientSession getSession() {
            return sessionRef.get();
        }
    }
}
