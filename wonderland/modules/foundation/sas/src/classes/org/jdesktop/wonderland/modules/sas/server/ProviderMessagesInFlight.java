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
package org.jdesktop.wonderland.modules.sas.server;

import com.sun.sgs.app.ManagedObject;
import java.io.Serializable;
import java.util.HashMap;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.messages.MessageID;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * There is one one instance of this data structure for every provider. It is used to store
 * the app launch messages which have been sent to a provider whose result we have not yet
 * been told by the provider.
 *
 * @author deronj
 */

// TODO: must have a timeout on how long messages live in this
@ExperimentalAPI
public class ProviderMessagesInFlight implements ManagedObject, Serializable {

    private static final Logger logger = Logger.getLogger(ProviderMessagesInFlight.class.getName());

    class MessageInfo implements Serializable {
        public ManagedReference providerRef;
        public CellID cellID;
        public MessageInfo (ManagedReference providerRef, CellID cellID) {
            this.providerRef = providerRef;
            this.cellID = cellID;
        }
    }

    private HashMap<MessageID,MessageInfo> messageMap = new HashMap<MessageID,MessageInfo>();

    void addMessageInfo (MessageID msgID, ManagedReference providerRef, CellID cellID) {
        MessageInfo msgInfo = new MessageInfo(providerRef, cellID);
        messageMap.put(msgID, msgInfo);
        AppContext.getDataManager().markForUpdate(this);
    }

    void removeMessageInfo (MessageID msgID) {
        messageMap.remove(msgID);
        AppContext.getDataManager().markForUpdate(this);
    }

    MessageInfo getMessageInfo (MessageID msgID) {
        return messageMap.get(msgID);
    }

    /**
     * Removes all messages that are in-flight for a given cell and provider.
     */
    void removeMessagesForCellAndProvider (ManagedReference providerRef, CellID cellID) {
        LinkedList<MessageID> removeList = new LinkedList<MessageID>();
        for (MessageID msgID : messageMap.keySet()) {
            MessageInfo messageInfo = messageMap.get(msgID);
            if (messageInfo.cellID.equals(cellID) && 
                messageInfo.providerRef.getId().equals(providerRef.getId())) {
                removeList.add(msgID);
            }
        }        
        for (MessageID msgIDToRemove : removeList) {
            messageMap.remove(msgIDToRemove);
        }
        removeList = null;
        AppContext.getDataManager().markForUpdate(this);
    }

    /**
     * Removes all messages that are in-flight to the given provider.
     */
    void removeMessagesForProvider (ManagedReference providerRef) {
        LinkedList<MessageID> removeList = new LinkedList<MessageID>();
        for (MessageID msgID : messageMap.keySet()) {
            MessageInfo messageInfo = messageMap.get(msgID);
            if (messageInfo.providerRef.getId().equals(providerRef.getId())) {
                removeList.add(msgID);
            }
        }        
        for (MessageID msgIDToRemove : removeList) {
            messageMap.remove(msgIDToRemove);
        }
        removeList = null;
        AppContext.getDataManager().markForUpdate(this);
    }

    // Given an an app identified by a provider and a cell, returns the launch message ID
    // for that app. TODO: someday: assumes only one app launched per cell.
    MessageID getLaunchMessageIDForCellAndProvider (ManagedReference providerRef, CellID cellID) {
        for (MessageID msgID : messageMap.keySet()) {
            MessageInfo messageInfo = messageMap.get(msgID);
            if (messageInfo.providerRef.getId().equals(providerRef.getId()) && 
                messageInfo.cellID.equals(cellID)) {
                return msgID;
            }
        }        
        return null;
    }
}
