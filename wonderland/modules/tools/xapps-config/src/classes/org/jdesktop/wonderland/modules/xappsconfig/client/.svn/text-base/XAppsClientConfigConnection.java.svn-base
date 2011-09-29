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
package org.jdesktop.wonderland.modules.xappsconfig.client;

import java.util.HashSet;
import java.util.Set;
import org.jdesktop.wonderland.client.comms.BaseConnection;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.modules.xappsconfig.common.XAppsConfigConnectionType;
import org.jdesktop.wonderland.modules.xappsconfig.common.XAppsNewMessage;
import org.jdesktop.wonderland.modules.xappsconfig.common.XAppsRemoveMessage;

/**
 * A client-side connection to send and receive messages that an X11 App has
 * been added or removed.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class XAppsClientConfigConnection extends BaseConnection {

    private Set<XAppsConfigListener> listeners = new HashSet();

    public ConnectionType getConnectionType() {
        return XAppsConfigConnectionType.CONNECTION_TYPE;
    }

    @Override
    public void handleMessage(Message message) {
        // If we received a message that there is a new X11 App, then inform
        // all of the listeners.
        if (message instanceof XAppsNewMessage) {
            String appName = ((XAppsNewMessage)message).getAppName();
            String command = ((XAppsNewMessage)message).getCommand();
            synchronized (listeners) {
                for (XAppsConfigListener listener : listeners) {
                    listener.xappAdded(appName, command);
                }
            }
            return;
        }

        // If we received a message that an X11 App has been removed, then tell
        // all of the listeners.
        if (message instanceof XAppsRemoveMessage) {
            String appName = ((XAppsRemoveMessage) message).getAppName();
            synchronized (listeners) {
                for (XAppsConfigListener listener : listeners) {
                    listener.xappRemoved(appName);
                }
            }
            return;
        }
    }

    /**
     * Adds a new listener for X11 App config messages. If the listener is already
     * present, this method does nothing.
     *
     * @param listener The listener to add
     */
    public void addX11AppConfigListener(XAppsConfigListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a listener for X11 App config messages. If the listener is not
     * present, this method does nothing.
     *
     * @param listener The listener to remove
     */
    public void removeX11AppConfigListener(XAppsConfigListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Listener for X11 App config messages
     */
    public interface XAppsConfigListener {
        /**
         * An X11 App has been added to the configuration.
         *
         * @param appName The name of the app
         * @param command The command to launch the app
         */
        public void xappAdded(String appName, String command);

        /**
         * An X11 App has been removed from the configuration.
         *
         * @param appName The name of the app
         */
        public void xappRemoved(String appName);
    }
}
