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
package org.jdesktop.wonderland.modules.xappsconfig.server;

import org.jdesktop.wonderland.common.annotation.Plugin;
import org.jdesktop.wonderland.server.ServerPlugin;
import org.jdesktop.wonderland.server.WonderlandContext;
import org.jdesktop.wonderland.server.comms.CommsManager;

/**
 * Server-side plugin for the X11 Apps config channel.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@Plugin
public class XAppsConfigServerPlugin implements ServerPlugin {

    public void initialize() {
        // Register a handler for x11 app config connections
        CommsManager cm = WonderlandContext.getCommsManager();
        cm.registerClientHandler(new XAppsConfigConnectionHandler());   
    }
}
