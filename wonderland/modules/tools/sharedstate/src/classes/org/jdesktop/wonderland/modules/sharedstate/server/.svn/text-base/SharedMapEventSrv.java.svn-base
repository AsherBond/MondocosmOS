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
package org.jdesktop.wonderland.modules.sharedstate.server;

import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.modules.sharedstate.common.SharedData;
import org.jdesktop.wonderland.modules.sharedstate.common.SharedMapEvent;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;

/**
 * An event that notifies the server when a shared value has changed.
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class SharedMapEventSrv extends SharedMapEvent {
    /** the map where the change occured */
    private SharedMapSrv map;

    /** the id of the initiator of the change */
    private WonderlandClientID senderID;

    /** the source message */
    private CellMessage source;

    /**
     * Create a new event to notify clients of a change.
     * @param map the map where the change occured.
     * @param senderID the id of the initiator of the change, or null
     * if the change was initiated by the server.
     * @param propertyName the name of the property that changed.
     * @param oldvalue the old value of the property.
     * @param newValue the new value of the property.
     */
    public SharedMapEventSrv(SharedMapSrv map, WonderlandClientID senderID,
                             CellMessage source, String propertyName,
                             SharedData oldValue, SharedData newValue)
    {
        super (map, propertyName, oldValue, newValue);

        this.map = map;
        this.senderID = senderID;
        this.source = source;
    }

    /**
     * Get the map where the change occurred. This returns a server-specific
     * SharedMapSrv object.
     * @return the map where the change occured
     */
    @Override
    public SharedMapSrv getMap() {
        return map;
    }

    /**
     * Get the id of the initiator of the change
     * @return the id of the initiator of the change
     */
    public WonderlandClientID getSenderID() {
        return senderID;
    }

    /**
     * Get the source message that caused this change.
     * @return the source message, or null if the change was originated
     * without a message.
     */
    public CellMessage getSourceMessage() {
        return source;
    }
}
