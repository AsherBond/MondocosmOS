/**
 * Open Wonderland
 *
 * Copyright (c) 2011, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

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
package org.jdesktop.wonderland.modules.placemarks.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.jdesktop.wonderland.client.jme.MainFrame.PlacemarkType;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.client.login.PrimaryServerListener;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.modules.placemarks.api.client.PlacemarkRegistry;
import org.jdesktop.wonderland.modules.placemarks.api.common.Placemark;

/**
 * The placemarks registry manages a list of user and system-wide placemarks
 * registered.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class PlacemarkRegistryImpl implements PlacemarkRegistry, PrimaryServerListener {
    
    // A map of user and system-wide placemarks, where the key is the placemark
    // name.
    private Map<String, Placemark> systemPlacemarkMap = null;
    private Map<String, Placemark> userPlacemarkMap = null;

    // A set of user and system-wide placemarks
    private Set<Placemark> systemPlacemarkSet = null;
    private Set<Placemark> userPlacemarkSet = null;

    // A list of listeners for changes to the placemarks
    private Set<PlacemarkListener> listeners = new HashSet();

    /** Default constructor */
    public PlacemarkRegistryImpl() {
        systemPlacemarkMap = new HashMap();
        userPlacemarkMap = new HashMap();
        systemPlacemarkSet = new HashSet();
        userPlacemarkSet = new HashSet();

        // Listen for changes to the server. We will need to clear out the
        // system-wide placemarks whenever we connect to a new server.
        LoginManager.addPrimaryServerListener(this);
    }
     
    /**
     * Registers a Placemark given its type (USER or SYSTEM).
     * 
     * @param placemark The new Placemark
     * @param type Either SYSTEM or USER
     */
    public void registerPlacemark(Placemark placemark, PlacemarkType type) {
        // Add the factory to the list and then notify listeners of the change
        synchronized (this) {
            switch (type) {

                case SYSTEM:
                    systemPlacemarkMap.put(placemark.getName(), placemark);
                    systemPlacemarkSet.add(placemark);
                    break;

                case USER:
                    userPlacemarkMap.put(placemark.getName(), placemark);
                    userPlacemarkSet.add(placemark);
                    break;
            }
            firePlacemarkAddedListener(placemark, type);
        }
    }

    /**
     * Removes a Placemark given its type (USER or SYSTEM).
     *
     * @param placemark The Placemark to remove
     * @param type Either SYSTEM or USER
     */
    public void unregisterPlacemark(Placemark placemark, PlacemarkType type) {
        // Remove the factory to the list and then notify listeners of the change
        synchronized (this) {
            switch (type) {
                case SYSTEM:
                    systemPlacemarkMap.remove(placemark.getName());
                    systemPlacemarkSet.remove(placemark);
                    break;

                case USER:
                    userPlacemarkMap.remove(placemark.getName());
                    userPlacemarkSet.remove(placemark);
                    break;
            }
            firePlacemarkRemovedListener(placemark, type);
        }
    }
    
    /**
     * Returns a set of all placemarks given the type (USER or SYTEM). If no
     * placemarks are registered, returns an empty set.
     *
     * @param type Either USER or SYSTEM
     * @return A set of registered placemarks
     */
    public Set<Placemark> getAllPlacemarks(PlacemarkType type) {
        synchronized (this) {
            switch (type) {
                case SYSTEM:
                    return new HashSet(systemPlacemarkSet);

                case USER:
                    return new HashSet(userPlacemarkSet);
            }
            return new HashSet();
        }
    }

    /**
     * Notification that the primary server has changed. Update our maps
     * accordingly.
     *
     * @param server the new primary server (may be null)
     */
    public void primaryServer(ServerSessionManager server) {
        // Make a copy of the set of system-wide placemarks. Remove them. We
        // need to make a copy to avoid concurrent modification exceptions.
        Set<Placemark> placemarkSet = new HashSet(systemPlacemarkSet);
        for (Placemark placemark : placemarkSet) {
            systemPlacemarkSet.remove(placemark);
            systemPlacemarkMap.remove(placemark.getName());
            firePlacemarkRemovedListener(placemark, PlacemarkType.SYSTEM);
        }
    }

    /**
     * Adds a new listener for changes to the Placemark registry. If this listener
     * is already present, this method does nothing.
     *
     * @param listener The new listener to add
     */
    public void addPlacemarkRegistryListener(PlacemarkListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a listener for changes to the Placemark registry. If this listener
     * is not present, this method does nothing.
     *
     * @param listener The listener to remove
     */
    public void removePlacemarkListener(PlacemarkListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Sends an event to all registered listeners that a placemark has been
     * added.
     */
    private void firePlacemarkAddedListener(Placemark placemark, PlacemarkType type) {
        synchronized (listeners) {
            for (PlacemarkListener listener : listeners) {
                listener.placemarkAdded(placemark, type);
            }
        }
    }

    /**
     * Sends an event to all registered listeners that a placemark has been
     * added.
     */
    private void firePlacemarkRemovedListener(Placemark placemark, PlacemarkType type) {
        synchronized (listeners) {
            for (PlacemarkListener listener : listeners) {
                listener.placemarkRemoved(placemark, type);
            }
        }
    }
}
