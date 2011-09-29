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
package org.jdesktop.wonderland.server.comms;

import java.util.Properties;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.Message;

/**
 * Handles client connections to the given client type.
 * <p>
 * ClientConnectionHandlers are stored in the Darkstar data store, so must be
 * either Serializable or a ManagedObject.  If a handler is a ManagedObject,
 * only a single copy of the handler will exist, and all messages will
 * be forwarded to this object.  If the handler is not a managed object,
 * a separate copy of the handler will be created in each WonderlandSession
 * that connects a client of the given type.  It is recommended that
 * handlers that expect a large number messages be Serializable.
 * 
 * @author jkaplan
 */
@ExperimentalAPI
public interface ClientConnectionHandler {
    /**
     * Get the type of connection this handler deals with
     * @return the connection types this handler can be used for
     */
    public ConnectionType getConnectionType();
    
    /**
     * Called when the handler is registered with the CommsManager
     * @param sender the WonderlandClientSender that can be used to
     * send to all clients of the given type
     */
    public void registered(WonderlandClientSender sender);
    
    /**
     * Handle when a new session connectes to this handler.  A session 
     * connects when a client calls <code>WonderlandSession.connect()</code>.
     * @param sender the sender that can be used to send to clients
     * of this handler
     * @param clientID the ID of the session that connected
     * @param properties the properties of the connection, or an
     * empty property object if the client didn't send any properties
     */
    public void clientConnected(WonderlandClientSender sender,
                                WonderlandClientID clientID,
                                Properties properties);
    
    /**
     * Handle a message from a client
     * @param sender the sender that can be used to send to clients of
     * this handler
     * @param clientID the ID of the session that connected
     * @param message the message that was generated
     */
    public void messageReceived(WonderlandClientSender sender,
                                WonderlandClientID clientID,
                                Message message);
    
    /**
     * Handle when a session disconnects from this handler
     * @param sender the sender that can be used to send to clients
     * of this handler
     * @param clientID the ID of the session that connected
     */
    public void clientDisconnected(WonderlandClientSender sender,
                                   WonderlandClientID clientID);
}
