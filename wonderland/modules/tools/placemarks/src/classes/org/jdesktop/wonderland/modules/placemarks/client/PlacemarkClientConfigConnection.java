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
package org.jdesktop.wonderland.modules.placemarks.client;

import java.util.HashSet;
import java.util.Set;
import org.jdesktop.wonderland.client.comms.BaseConnection;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.modules.placemarks.api.common.Placemark;
import org.jdesktop.wonderland.modules.placemarks.common.PlacemarkConfigConnectionType;
import org.jdesktop.wonderland.modules.placemarks.common.PlacemarkNewMessage;
import org.jdesktop.wonderland.modules.placemarks.common.PlacemarkRemoveMessage;
import org.jdesktop.wonderland.modules.placemarks.common.PlacemarksMessage;

/**
 * A client-side connection to send and receive messages that a Placemark has
 * been added or removed.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class PlacemarkClientConfigConnection extends BaseConnection {

    private Set<PlacemarkConfigListener> listeners = new HashSet();

    public ConnectionType getConnectionType() {
        return PlacemarkConfigConnectionType.CONNECTION_TYPE;
    }

    @Override
    public void handleMessage(Message message) {
        // If we received a message that there is a new Placemark, then inform
        // all of the listeners.
        if (message instanceof PlacemarkNewMessage) {
            Placemark placemark = ((PlacemarkNewMessage)message).getPlacemark();
            synchronized (listeners) {
                for (PlacemarkConfigListener listener : listeners) {
                    listener.placemarkAdded(placemark);
                }
            }
            return;
        }

        // If we received a message that a placemark has been removed, then tell
        // all of the listeners.
        if (message instanceof PlacemarkRemoveMessage) {
            Placemark placemark = ((PlacemarkRemoveMessage) message).getPlacemark();
            synchronized (listeners) {
                for (PlacemarkConfigListener listener : listeners) {
                    listener.placemarkRemoved(placemark);
                }
            }
            return;
        }

        // If we receive a message with a number of placemarks, add them
        // all
        if (message instanceof PlacemarksMessage) {
            for (Placemark placemark : ((PlacemarksMessage) message).getPlacemarks()) {
                synchronized (listeners) {
                    for (PlacemarkConfigListener listener : listeners) {
                        listener.placemarkAdded(placemark);
                    }
                }
            }
        }
    }

    /**
     * Adds a new listener for Placemark config messages. If the listener is
     * already present, this method does nothing.
     *
     * @param listener The listener to add
     */
    public void addPlacemarkConfigListener(PlacemarkConfigListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a listener for Placemark config messages. If the listener is not
     * present, this method does nothing.
     *
     * @param listener The listener to remove
     */
    public void removePlacemarkConfigListener(PlacemarkConfigListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Listener for Placemark config messages
     */
    public interface PlacemarkConfigListener {
        /**
         * A Placemark has been added to the configuration.
         *
         * @param placemark The placemark that has been added
         */
        public void placemarkAdded(Placemark placemark);

        /**
         * A Placemark has been removed from the configuration.
         *
         * @param placemark The placemark that has been removed
         */
        public void placemarkRemoved(Placemark placemark);
    }
}
