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
package org.jdesktop.wonderland.modules.placemarks.server;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jdesktop.wonderland.modules.placemarks.api.server.PlacemarkRegistrySrv;
import org.jdesktop.wonderland.modules.placemarks.api.common.Placemark;

/**
 * The placemarks registry manages a list of user and system-wide placemarks
 * registered.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class PlacemarkRegistrySrvImpl 
        implements PlacemarkRegistrySrv, ManagedObject, Serializable
{   
    // A set of system-wide placemarks
    private final Set<Placemark> placemarkSet = new HashSet<Placemark>();

    // A list of listeners for changes to the placemarks
    private final Set<PlacemarkListenerSrv> listeners =
            new CopyOnWriteArraySet<PlacemarkListenerSrv>();

    /** Default constructor */
    public PlacemarkRegistrySrvImpl() {
    }
     
    /**
     * Registers a Placemark
     * @param placemark The new Placemark
     */
    public void registerPlacemark(Placemark placemark) {
        placemarkSet.add(placemark);
        firePlacemarkAddedListener(placemark);
    }

    /**
     * Removes a Placemark
     * @param placemark The Placemark to remove
     */
    public void unregisterPlacemark(Placemark placemark) {
        placemarkSet.remove(placemark);
        firePlacemarkRemovedListener(placemark);
        
    }
    
    /**
     * Returns a set of all placemarks. If no
     * placemarks are registered, returns an empty set.
     * @return A set of registered placemarks
     */
    public Set<Placemark> getAllPlacemarks() {
        return Collections.unmodifiableSet(placemarkSet);
    }

    /**
     * Adds a new listener for changes to the Placemark registry. If this listener
     * is already present, this method does nothing.
     *
     * @param listener The new listener to add
     */
    public void addPlacemarkRegistryListener(PlacemarkListenerSrv listener) {
        if (listener instanceof ManagedObject) {
            listener = new ManagedPlacemarkListenerSrv(listener);
        }

        listeners.add(listener);
    }

    /**
     * Removes a listener for changes to the Placemark registry. If this listener
     * is not present, this method does nothing.
     *
     * @param listener The listener to remove
     */
    public void removePlacemarkListener(PlacemarkListenerSrv listener) {
        if (listener instanceof ManagedObject) {
            listener = new ManagedPlacemarkListenerSrv(listener);
        }

        listeners.remove(listener);
    }

    /**
     * Sends an event to all registered listeners that a placemark has been
     * added.
     */
    private void firePlacemarkAddedListener(Placemark placemark) {
        for (PlacemarkListenerSrv listener : listeners) {
            listener.placemarkAdded(placemark);
        }
    }

    /**
     * Sends an event to all registered listeners that a placemark has been
     * added.
     */
    private void firePlacemarkRemovedListener(Placemark placemark) {
        for (PlacemarkListenerSrv listener : listeners) {
            listener.placemarkRemoved(placemark);
        }
    }

    /**
     * Wrapper for managed object listener
     */
    private static class ManagedPlacemarkListenerSrv
            implements PlacemarkListenerSrv
    {
        private ManagedReference<PlacemarkListenerSrv> listenerRef;

        public ManagedPlacemarkListenerSrv(PlacemarkListenerSrv listener) {
            listenerRef = AppContext.getDataManager().createReference(listener);
        }

        public void placemarkAdded(Placemark placemark) {
            listenerRef.get().placemarkAdded(placemark);
        }

        public void placemarkRemoved(Placemark placemark) {
            listenerRef.get().placemarkRemoved(placemark);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ManagedPlacemarkListenerSrv other = (ManagedPlacemarkListenerSrv) obj;
            if (this.listenerRef != other.listenerRef &&
                    (this.listenerRef == null ||
                    !this.listenerRef.equals(other.listenerRef)))
            {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + (this.listenerRef != null ? this.listenerRef.hashCode() : 0);
            return hash;
        }
    }
}
