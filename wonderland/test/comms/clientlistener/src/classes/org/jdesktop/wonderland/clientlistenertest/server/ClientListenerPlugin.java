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
package org.jdesktop.wonderland.clientlistenertest.server;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.Task;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Logger;
import org.jdesktop.wonderland.clientlistenertest.common.TestClientType;
import org.jdesktop.wonderland.clientlistenertest.common.TestMessageOne;
import org.jdesktop.wonderland.clientlistenertest.common.TestMessageThree;
import org.jdesktop.wonderland.clientlistenertest.common.TestMessageTwo;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.server.ServerPlugin;
import org.jdesktop.wonderland.server.WonderlandContext;
import org.jdesktop.wonderland.server.comms.ClientConnectionHandler;
import org.jdesktop.wonderland.server.comms.CommsManager;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

/**
 * Sample plugin that doesn't do anything
 * @author jkaplan
 */
public class ClientListenerPlugin implements ServerPlugin {
    private static final Logger logger =
            Logger.getLogger(ClientListenerPlugin.class.getName());
    
    public void initialize() {
        CommsManager cm = WonderlandContext.getCommsManager();
        cm.registerClientHandler(new TestClientHandler());
    }
    
    static class TestClientHandler
            implements ClientConnectionHandler, ManagedObject, Serializable
    {
        public ConnectionType getConnectionType() {
            return TestClientType.CLIENT_ONE_TYPE;
        }

        public void registered(WonderlandClientSender sender) {
        }

        public void clientConnected(WonderlandClientSender sender,
                                    ClientSession session,
                                    Properties properties) 
        {
            logger.info("Session connected: " + session);
            
            // send message over session channel
            sender.send(session, new TestMessageOne("Attach: TestOne (private)"));
            
            // send message over all-clients channel
            sender.send(new TestMessageTwo("Attach: TestTwo"));
            
            // now schedule a task to send more messages
            AppContext.getTaskManager().scheduleTask(new SendTask(sender, 
                                                                  session));
        }

        public void messageReceived(WonderlandClientSender sender,
                                    ClientSession session, 
                                    Message message)
        {
            // ignore
        }

        public void clientDisconnected(WonderlandClientSender sender,
                                       ClientSession session) {
            // ignore
        }
        
        static class SendTask implements Task, Serializable {
            private WonderlandClientSender sender;
            private ManagedReference<ClientSession> sessionRef;
            
            public SendTask(WonderlandClientSender sender, 
                            ClientSession session) 
            {
                this.sender = sender;
                
                DataManager dm = AppContext.getDataManager();
                sessionRef = dm.createReference(session);
            }

            public void run() throws Exception {
                sender.send(new TestMessageOne("Task: TestOne"));
                sender.send(sessionRef.get(), new TestMessageThree("Task: TestThree (private)", 42));
            }   
        }
    }
}
