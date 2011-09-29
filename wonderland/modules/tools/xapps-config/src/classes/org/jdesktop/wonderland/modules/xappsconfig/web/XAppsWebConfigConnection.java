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
package org.jdesktop.wonderland.modules.xappsconfig.web;

import java.util.logging.Logger;
import org.jdesktop.wonderland.client.comms.BaseConnection;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.modules.xappsconfig.common.XAppsConfigConnectionType;
import org.jdesktop.wonderland.modules.xappsconfig.common.XAppsNewMessage;
import org.jdesktop.wonderland.modules.xappsconfig.common.XAppsRemoveMessage;

/**
 * A client-side connection to send and receive messages that an X11 App has
 * been added or removed, to be used by a servlet to send messages.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class XAppsWebConfigConnection extends BaseConnection {

    public ConnectionType getConnectionType() {
        return XAppsConfigConnectionType.CONNECTION_TYPE;
    }

    @Override
    public void handleMessage(Message message) {
        // The web does not listen for any messages
    }

    /**
     * Tells the X11 App Configuration channel that an app has been added
     *
     * @param appName The name of the app
     * @param command The command to launch the app
     */
    public void addX11App(String appName, String command) {
        // Send a message to all other clients that an app has been added
        Logger logger = Logger.getLogger(XAppsWebConfigConnection.class.getName());
        super.send(new XAppsNewMessage(appName, command));
    }

    /**
     * Tells the X11 App Configuration channel that an app has been removed
     *
     * @param appName The name of the app
     */
    public void removeX11App(String appName) {
        // Send a message to all other clients that an app has been removed
        super.send(new XAppsRemoveMessage(appName));
    }
}
