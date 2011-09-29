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
package org.jdesktop.wonderland.modules.placemarks.api.server;

import java.util.Set;
import org.jdesktop.wonderland.modules.placemarks.api.common.Placemark;

/**
 * The placemarks registry manages a list of system-wide placemarks registered.
 *
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public interface PlacemarkRegistrySrv {
    /**
     * Registers a Placemark
     * @param placemark The new Placemark
     */
    public void registerPlacemark(Placemark placemark);

    /**
     * Removes a Placemark
     * @param placemark The Placemark to remove
     */
    public void unregisterPlacemark(Placemark placemark);

    /**
     * Returns a set of all placemarks
     * @return A set of registered placemarks
     */
    public Set<Placemark> getAllPlacemarks();

    /**
     * Adds a new listener for changes to the Placemark registry. If this listener
     * is already present, this method does nothing.
     *
     * @param listener The new listener to add
     */
    public void addPlacemarkRegistryListener(PlacemarkListenerSrv listener);

    /**
     * Removes a listener for changes to the Placemark registry. If this listener
     * is not present, this method does nothing.
     *
     * @param listener The listener to remove
     */
    public void removePlacemarkListener(PlacemarkListenerSrv listener);

    /**
     * A listener indicating that a change has happened to the set of registered
     * placemarks.
     */
    public interface PlacemarkListenerSrv {
        /**
         * A Placemark has been added.
         * @param placemark The Placemark added
         */
        public void placemarkAdded(Placemark placemark);

        /**
         * A Placemark has been removed.
         * @param placemark The Placemark removed
         */
        public void placemarkRemoved(Placemark placemark);
    }
}