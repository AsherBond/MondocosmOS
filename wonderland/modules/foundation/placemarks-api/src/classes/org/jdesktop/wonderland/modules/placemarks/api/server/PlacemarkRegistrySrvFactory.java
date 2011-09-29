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

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.NameNotBoundException;
import org.jdesktop.wonderland.modules.placemarks.api.client.*;

/**
 * A factory for creating the PlacemarkRegistrySrv object
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class PlacemarkRegistrySrvFactory {
    /** the default class to instantiate */
    private static final String PLACEMARK_FACTORY_DEFAULT =
            "org.jdesktop.wonderland.modules.placemarks.server.PlacemarkRegistrySrvImpl";
    
    /** the system property for changing the implementation */
    private static final String PLACEMARK_FACTORY_PROP =
            PlacemarkRegistrySrvFactory.class.getSimpleName() + "PlacemarkRegistrySrvFactory";

    /** the binding to store the singleton object in */
    private static final String BINDING_NAME =
            PlacemarkRegistrySrvFactory.class.getName();

    /**
     * Get an instance of the PlacemarkRegistrySrv class
     * @return a shared instance of Darkstar web login
     */
    public static PlacemarkRegistrySrv getInstance() {
        PlacemarkRegistrySrv out;
        DataManager dm = AppContext.getDataManager();

        try {
            out = (PlacemarkRegistrySrv) dm.getBinding(BINDING_NAME);
        } catch (NameNotBoundException nnbe) {
            out = createInstance();
            dm.setBinding(BINDING_NAME, out);
        }

        return out;
    }

    private static PlacemarkRegistrySrv createInstance() {
        String factoryClass = System.getProperty(PLACEMARK_FACTORY_PROP,
                                                 PLACEMARK_FACTORY_DEFAULT);

        try {
            Class<PlacemarkRegistrySrv> c = (Class<PlacemarkRegistrySrv>)
                    Class.forName(factoryClass);
            return c.newInstance();
        } catch (InstantiationException ex) {
            throw new RuntimeException(ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException(ex);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }
}
