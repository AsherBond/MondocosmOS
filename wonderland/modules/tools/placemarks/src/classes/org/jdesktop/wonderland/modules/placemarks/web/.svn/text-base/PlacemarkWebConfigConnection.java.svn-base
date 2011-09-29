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
package org.jdesktop.wonderland.modules.placemarks.web;

import org.jdesktop.wonderland.client.comms.BaseConnection;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.modules.placemarks.api.common.Placemark;
import org.jdesktop.wonderland.modules.placemarks.common.PlacemarkConfigConnectionType;
import org.jdesktop.wonderland.modules.placemarks.common.PlacemarkNewMessage;
import org.jdesktop.wonderland.modules.placemarks.common.PlacemarkRemoveMessage;

/**
 * A client-side connection to send and receive messages that a Placemark has
 * been added or removed, to be used by a servlet to send messages.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class PlacemarkWebConfigConnection extends BaseConnection {

    public ConnectionType getConnectionType() {
        return PlacemarkConfigConnectionType.CONNECTION_TYPE;
    }

    @Override
    public void handleMessage(Message message) {
        // The web does not listen for any messages
    }

    /**
     * Tells the Placemark Configuration channel that a placemark has been added
     *
     * @param placemark The placemark to add
     */
    public void addPlacemark(Placemark placemark) {
        // Send a message to all other clients that a placemark has been added
        super.send(new PlacemarkNewMessage(placemark));
    }

    /**
     * Tells the Placemark Configuration channel that a placemark has been removed
     *
     * @param placemark The placemark to remove
     */
    public void removePlacemark(Placemark placemark) {
        // Send a message to all other clients that a placemark has been removed
        super.send(new PlacemarkRemoveMessage(placemark));
    }
}
