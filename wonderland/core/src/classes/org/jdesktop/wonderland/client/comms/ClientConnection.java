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
package org.jdesktop.wonderland.client.comms;

import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.Message;

/**
 * This class provides the client side instance of a particular Wonderland
 * service. All interaction with a service on a given server are handled
 * by this a ClientConnection.
 * <p>
 * The client starts out in the DISCONNECTED status, meaning it is not associated
 * with any WonderlandSession.  Once a client is connected to a session,
 * it is able to communicate with the server.
 * 
 * @author kaplanj
 */
@ExperimentalAPI
public interface ClientConnection {
    /** status of this listener */
    public enum Status { DISCONNECTED, CONNECTED };
    
    /**
     * Get the type this client represents.
     * @return the type of client
     */
    public ConnectionType getConnectionType();
    
    /**
     * Get the session this client is connected to
     * @return the session this client is connected to, or null if
     * the client is not connected to a session.
     */
    public WonderlandSession getSession();
    
    /**
     * Get the status of this client
     * @return the status of the client
     */
    public Status getStatus();
    
    /**
     * Notify this client that it is connected to given session
     * @param session the session the client is now connected to
     */
    public void connected(WonderlandSession session);
    
    /**
     * Notify this client that it is disconnected from the current session
     */
    public void disconnected();
    
    /**
     * Handle a message sent to this client
     * @param message the message
     */
    public void messageReceived(Message message);
}
