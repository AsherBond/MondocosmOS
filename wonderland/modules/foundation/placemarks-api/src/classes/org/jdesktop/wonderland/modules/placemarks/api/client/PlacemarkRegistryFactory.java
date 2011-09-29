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

/**
 * A factory for creating the PlacemarkRegistry object
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class PlacemarkRegistryFactory {
    /** the default class to instantiate */
    private static final String PLACEMARK_FACTORY_DEFAULT =
            "org.jdesktop.wonderland.modules.placemarks.client.PlacemarkRegistryImpl";
    
    /** the system property for changing the implementation */
    private static final String PLACEMARK_FACTORY_PROP =
            PlacemarkRegistryFactory.class.getSimpleName() + "PlacemarkRegistryFactory";
    
    /**
     * Get an instance of the PlacemarkRegistrySrv class
     * @return a shared instance of Darkstar web login
     */
    public static PlacemarkRegistry getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Holder for the singleton instance
     */
    private static final class SingletonHolder {
        private static final PlacemarkRegistry INSTANCE;

        static {
            String factoryClass = System.getProperty(PLACEMARK_FACTORY_PROP,
                                                     PLACEMARK_FACTORY_DEFAULT);

            try {
                Class<PlacemarkRegistry> c = (Class<PlacemarkRegistry>) Class.forName(factoryClass);
                INSTANCE = c.newInstance();
            } catch (InstantiationException ex) {
                throw new RuntimeException(ex);
            } catch (IllegalAccessException ex) {
                throw new RuntimeException(ex);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
