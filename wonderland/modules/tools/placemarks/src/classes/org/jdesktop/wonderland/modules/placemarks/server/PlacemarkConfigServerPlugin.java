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
package org.jdesktop.wonderland.modules.placemarks.server;

import java.io.Serializable;
import org.jdesktop.wonderland.common.annotation.Plugin;
import org.jdesktop.wonderland.modules.placemarks.api.common.Placemark;
import org.jdesktop.wonderland.modules.placemarks.common.PlacemarkConfigConnectionType;
import org.jdesktop.wonderland.modules.placemarks.common.PlacemarkNewMessage;
import org.jdesktop.wonderland.modules.placemarks.common.PlacemarkRemoveMessage;
import org.jdesktop.wonderland.modules.placemarks.api.server.PlacemarkRegistrySrv.PlacemarkListenerSrv;
import org.jdesktop.wonderland.modules.placemarks.api.server.PlacemarkRegistrySrvFactory;
import org.jdesktop.wonderland.server.ServerPlugin;
import org.jdesktop.wonderland.server.WonderlandContext;
import org.jdesktop.wonderland.server.comms.CommsManager;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

/**
 * Server-side plugin for the Placemarks config channel.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@Plugin
public class PlacemarkConfigServerPlugin implements ServerPlugin {

    public void initialize() {
        // Register a handler for placemark config connections
        CommsManager cm = WonderlandContext.getCommsManager();
        cm.registerClientHandler(new PlacemarkConfigConnectionHandler());

        // Register a listener that will be notifed of placemark changes
        PlacemarkUpdateListener l = new PlacemarkUpdateListener();
        PlacemarkRegistrySrvFactory.getInstance().addPlacemarkRegistryListener(l);
    }

    private static class PlacemarkUpdateListener
            implements PlacemarkListenerSrv, Serializable
    {

        public void placemarkAdded(Placemark placemark) {
            getConnection().send(new PlacemarkNewMessage(placemark));
        }

        public void placemarkRemoved(Placemark placemark) {
            getConnection().send(new PlacemarkRemoveMessage(placemark));
        }

        protected WonderlandClientSender getConnection() {
            CommsManager cm = WonderlandContext.getCommsManager();
            return cm.getSender(PlacemarkConfigConnectionType.CONNECTION_TYPE);
        }
    }
}
