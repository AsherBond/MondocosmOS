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
package org.jdesktop.wonderland.modules.sharedstate.common;

/**
 * An event that notifies listeners when a shared value has changed. This class
 * contains the shared data common to both the client and the server. Specific
 * client-side and server-side information is contained in client and server
 * specific extensions.
 * @author jkaplan
 */
public abstract class SharedMapEvent {
    /** the map where the change occured */
    private SharedMap map;

    /** the name of the property that changed */
    private String propertyName;

    /** the old value of the property */
    private SharedData oldValue;

    /** the new value of the property */
    private SharedData newValue;

    /**
     * Create a new event to notify clients of a change.
     * @param map the map where the change occured.
     * if the change was initiated by the server.
     * @param propertyName the name of the property that changed.
     * @param oldvalue the old value of the property.
     * @param newValue the new value of the property.
     */
    protected SharedMapEvent(SharedMap map, String propertyName,
                             SharedData oldValue, SharedData newValue)
    {
        this.map = map;
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /**
     * Get the map where the change occurred
     * @return the map where the change occured
     */
    public SharedMap getMap() {
        return map;
    }

    /**
     * Get the name of the property that changed
     * @return the name of the property that changed
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Get the old value of the property
     * @return the old value of the property
     */
    public SharedData getOldValue() {
        return oldValue;
    }

    /**
     * Get the new value of the property
     * @return the new value of the property
     */
    public SharedData getNewValue() {
        return newValue;
    }
}
