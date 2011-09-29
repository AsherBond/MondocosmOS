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
package org.jdesktop.wonderland.modules.appbase.client;

import java.lang.reflect.Constructor;
import org.jdesktop.wonderland.common.annotation.Plugin;
import org.jdesktop.wonderland.client.ClientPlugin;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.BaseClientPlugin;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.client.login.PrimaryServerListener;
import org.jdesktop.wonderland.modules.appbase.client.cell.view.View2DCellFactory;

/**
 * An object which is created during the user client login process in order to initialize the 
 * app base for that user client.
 *
 * @author deronj
 */
@ExperimentalAPI
@Plugin
public class AppClientPlugin extends BaseClientPlugin {

    private static final Logger logger = Logger.getLogger(AppClientPlugin.class.getName());

    /** The default cell view factory to use. */
    private final String VIEW2DCELL_FACTORY_CLASS_DEFAULT = 
        "org.jdesktop.wonderland.modules.appbase.client.cell.view.viewdefault.View2DCellFactoryDefault";

    /** All client plugins must have a no-arg constructor. */
    public AppClientPlugin () {}

    /** {@inheritDoc} */
    @Override
    public void initialize(ServerSessionManager sessionManager) {
        super.initialize(sessionManager);
        initAppBaseUserClient();
    }

    /** {@inheritDoc} */
    @Override
    public void cleanup() {
        // Right now there is nothing to do
    }

    /**
     * Called to initialize the app base for a user client on client startup.
     */
    public void initAppBaseUserClient () {
        initCellViewFactory();
    }

    /**
     * Initialize the app base cell view factory for a user client.
     */
    public void initCellViewFactory() {

        // TODO: later on we might allow the default view factory to be overridden by the user,
        // via a config file or property.

        ClassLoader classLoader = getClass().getClassLoader();
        View2DCellFactory view2DCellFactory = null;
        try {
            Class clazz = Class.forName(VIEW2DCELL_FACTORY_CLASS_DEFAULT, true, classLoader);
            Constructor constructor = clazz.getConstructor();
            view2DCellFactory = (View2DCellFactory) constructor.newInstance();
        } catch(Exception e) {
            logger.severe("Error instantiating app base 2D cell view factory " + 
                          VIEW2DCELL_FACTORY_CLASS_DEFAULT + ", Exception = " + e);
        }
        view2DCellFactory.initialize();

        if (view2DCellFactory == null) {
            logger.severe("Error instantiating app base 2D view cell factory " + 
                          VIEW2DCELL_FACTORY_CLASS_DEFAULT);
        } else {
            App2D.setView2DCellFactory(view2DCellFactory);
        }
    }
}
