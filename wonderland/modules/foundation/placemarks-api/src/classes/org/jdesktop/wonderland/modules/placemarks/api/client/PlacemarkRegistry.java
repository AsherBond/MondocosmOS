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
package org.jdesktop.wonderland.modules.placemarks.api.client;

import java.util.Set;
import org.jdesktop.wonderland.client.jme.MainFrame.PlacemarkType;
import org.jdesktop.wonderland.modules.placemarks.api.common.Placemark;

/**
 * The placemarks registry manages a list of user and system placemarks
 * registered. Management placemarks are not expected through the registration
 * process and they are managed directly within the placemark plugin.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public interface PlacemarkRegistry {

    /**
     * Registers a Placemark given its type (USER or SYSTEM).
     * 
     * @param placemark The new Placemark
     * @param type Either SYSTEM or USER
     */
    public void registerPlacemark(Placemark placemark, PlacemarkType type);

    /**
     * Removes a Placemark given its type (USER or SYSTEM).
     *
     * @param placemark The Placemark to remove
     * @param type Either SYSTEM or USER
     */
    public void unregisterPlacemark(Placemark placemark, PlacemarkType type);

    /**
     * Returns a set of all placemarks given the type (USER or SYTEM). If no
     * placemarks are registered, returns an empty set.
     *
     * @param type Either USER or SYSTEM
     * @return A set of registered placemarks
     */
    public Set<Placemark> getAllPlacemarks(PlacemarkType type);

    /**
     * Adds a new listener for changes to the Placemark registry. If this listener
     * is already present, this method does nothing.
     *
     * @param listener The new listener to add
     */
    public void addPlacemarkRegistryListener(PlacemarkListener listener);

    /**
     * Removes a listener for changes to the Placemark registry. If this listener
     * is not present, this method does nothing.
     *
     * @param listener The listener to remove
     */
    public void removePlacemarkListener(PlacemarkListener listener);

    /**
     * A listener indicating that a change has happened to the set of registered
     * placemarks.
     */
    public interface PlacemarkListener {
        /**
         * A Placemark has been added.
         *
         * @param placemark The Placemark added
         * @param type Either USER or SYSTEM
         */
        public void placemarkAdded(Placemark placemark, PlacemarkType type);

        /**
         * A Placemark has been removed.
         *
         * @param placemark The Placemark removed
         * @param type Either USER or SYSTEM
         */
        public void placemarkRemoved(Placemark placemark, PlacemarkType type);
    }
}