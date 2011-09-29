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
package org.jdesktop.wonderland.modules.darkstar.api.weblib;

/**
 * A factory for creating the DarkstarWebLogin object
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class DarkstarWebLoginFactory {
    /** the default class to instantiate */
    private static final String LOGIN_FACTORY_DEFAULT =
            "org.jdesktop.wonderland.modules.darkstar.server.DarkstarWebLoginImpl";
    
    /** the system property for changing the implementation */
    private static final String LOGIN_FACTORY_PROP =
            DarkstarWebLoginFactory.class.getSimpleName() + ".LoginFactory";
    
    /**
     * Get an instance of the DarkstarWebLogin class
     * @return a shared instance of Darkstar web login
     */
    public static DarkstarWebLogin getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Holder for the singleton instance
     */
    private static final class SingletonHolder {
        private static final DarkstarWebLogin INSTANCE;

        static {
            String factoryClass = System.getProperty(LOGIN_FACTORY_PROP, LOGIN_FACTORY_DEFAULT);

            try {
                Class<DarkstarWebLogin> c = (Class<DarkstarWebLogin>) Class.forName(factoryClass);
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
