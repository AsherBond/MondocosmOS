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

import org.jdesktop.wonderland.common.messages.MessageID;
import org.jdesktop.wonderland.common.messages.ResponseMessage;

/**
 * Message used to respond to an attach client request.  Incudes the
 * short client ID that must be pre-pended to all messages using this client
 * type.
 * @author jkaplan
 */
public class AttachedClientMessage extends ResponseMessage {
    /** the ID to use when sending messages to this client */
    private short clientID;
    
    /** 
     * Create a new AttachedClientMessage
     * @param messageID the id of the message this is a response to
     * @param clientID the client ID to use in future communications
     */
    public AttachedClientMessage(MessageID messageID, short clientID) {
        super(messageID);
        
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
