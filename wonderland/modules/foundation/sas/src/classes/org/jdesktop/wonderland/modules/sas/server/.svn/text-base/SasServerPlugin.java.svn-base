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
package org.jdesktop.wonderland.modules.sas.server;

import java.util.logging.Logger;
import org.jdesktop.wonderland.common.annotation.Plugin;
import org.jdesktop.wonderland.modules.appbase.server.cell.AppConventionalCellMO;
import org.jdesktop.wonderland.server.ServerPlugin;
import org.jdesktop.wonderland.server.WonderlandContext;
import org.jdesktop.wonderland.server.comms.CommsManager;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * Server Plugin to initialize the SAS on the server.
 * @author deronj
 */
@ExperimentalAPI
@Plugin
public class SasServerPlugin implements ServerPlugin {

    private static final Logger logger = Logger.getLogger(SasServerPlugin.class.getName());
        
    public void initialize() {

        logger.severe("***** SasServerPlugin: start initialization");

        // Tell the app base to call us to launch conventional server apps
        SasServer server = new SasServer();
        AppConventionalCellMO.registerAppServerLauncher(server);

        // Register the Provider connection
        CommsManager cm = WonderlandContext.getCommsManager();
        cm.registerClientHandler(new SasProviderConnectionHandler(server));

        logger.severe("***** SasServerPlugin: initialization complete");
    }
}
