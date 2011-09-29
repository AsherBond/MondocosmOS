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
package org.jdesktop.wonderland.servermanager.common;

import org.jdesktop.wonderland.common.messages.MessageID;
import org.jdesktop.wonderland.common.messages.ResponseMessage;

/**
 * Response to a periodic query of the server
 * @author jkaplan
 */
public class PingResponseMessage extends ResponseMessage {
    private long sentTime;
    
    public PingResponseMessage(PingRequestMessage req) {
        this (req.getMessageID(), req.getSentTime());
    }
    
    public PingResponseMessage(MessageID id, long sentTime) {
        super (id);
        
        this.sentTime = sentTime;
    }
    
    public long getSentTime() {
        return sentTime;
    }
}
