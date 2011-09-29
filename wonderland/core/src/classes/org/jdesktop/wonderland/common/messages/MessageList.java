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

import java.util.LinkedList;
import java.util.List;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * An aggregation of individual messages into a message list
 * @author paulby
 */
@ExperimentalAPI
public class MessageList extends Message {
    
    private LinkedList<Message> messages = new LinkedList();
    
    /**
     * Add a message to the list of aggregated messages
     * 
     * @param message
     */
    public void addMessage(Message message) {
        messages.add(message);
    }
    
    /**
     * Get the aggregated set of messages. Returns the underlying list, so 
     * users should take care if they modify the list
     * 
     * @return
     */
    public List<Message> getMessages() {
        return messages;
    }
    
    /**
     * Return the number of messages in the list
     * @return
     */
    public int size() {
        return messages.size();
    }
}
