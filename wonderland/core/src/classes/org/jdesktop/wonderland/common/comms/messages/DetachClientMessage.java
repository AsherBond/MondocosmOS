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
package org.jdesktop.wonderland.common.comms.messages;

import org.jdesktop.wonderland.common.messages.Message;

/**
 * Message used to request that a client disconnect from a session
 * @author jkaplan
 */
public class DetachClientMessage extends Message {
    /** the ID of the client to disconnect */
    private short clientID;
    
    /** 
     * Create a new DetachClientMessage
     * @param clientID the client ID to detach
     */
    public DetachClientMessage(short clientID) {
        this.clientID = clientID;
    }
    
    /**
     * Get the client ID
     * @return the client ID
     */
    public short getClientID() {
        return clientID;
    }
}
