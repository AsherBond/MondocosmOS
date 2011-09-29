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
package org.jdesktop.wonderland.common.messages;

import java.io.IOException;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * An exception extracting a message.  This exception includes the messageID
 * of the message in error, in order to help when sending error responses.
 * @author jkaplan
 */
@ExperimentalAPI
public class ExtractMessageException extends IOException {
    /** the extracted message id */
    private MessageID messageID;
    
    /** the extracted client id */
    private short clientID;
    
    /**
     * Creates a new instance of <code>ExtractMessageException</code> with the
     * given extracted message ID and client ID.
     * @param messageID the extracted message id
     * @param clientID the extracted client id
     */
    public ExtractMessageException(MessageID messageID, short clientID) {
        this (messageID, clientID, null);
    }

    /**
     * Constructs an instance of <code>ExtractMessageException</code> with the 
     * specified cause, message ID and client ID.
     * @param messageID the extracted message id
     * @param clientID the extracted client id
     * @param cause the cause of this error
     */
    public ExtractMessageException(MessageID messageID, short clientID,
                                   Throwable cause)
    {
        initCause(cause);
        
        this.messageID = messageID;
        this.clientID = clientID;
    }

    /**
     * Get the MessageID of the message that could not be extracted
     * @return the messageID
     */
    public MessageID getMessageID() {
        return messageID;
    }
    
    /**
     * Get the clientID of the message that could not be extracted
     * @return the clientID
     */
    public short getClientID() {
        return clientID;
    }
}
