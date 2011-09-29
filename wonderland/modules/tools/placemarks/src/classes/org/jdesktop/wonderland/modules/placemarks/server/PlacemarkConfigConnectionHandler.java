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
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.common.security.Action;
import org.jdesktop.wonderland.modules.placemarks.api.server.PlacemarkRegistrySrvFactory;
import org.jdesktop.wonderland.modules.placemarks.api.common.Placemark;
import org.jdesktop.wonderland.modules.placemarks.common.PlacemarkConfigConnectionType;
import org.jdesktop.wonderland.modules.placemarks.common.PlacemarkNewMessage;
import org.jdesktop.wonderland.modules.placemarks.common.PlacemarkRemoveMessage;
import org.jdesktop.wonderland.modules.placemarks.common.PlacemarksMessage;
import org.jdesktop.wonderland.modules.security.server.service.GroupMemberResource;
import org.jdesktop.wonderland.server.comms.ClientConnectionHandler;
import org.jdesktop.wonderland.server.comms.SecureClientConnectionHandler;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;
import org.jdesktop.wonderland.server.security.Resource;

/**
 * Handles Placemark config messages from the client.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
class PlacemarkConfigConnectionHandler implements SecureClientConnectionHandler, Serializable {

    private static Logger logger = Logger.getLogger(PlacemarkConfigConnectionHandler.class.getName());

    public ConnectionType getConnectionType() {
        return PlacemarkConfigConnectionType.CONNECTION_TYPE;
    }

    public void registered(WonderlandClientSender sender) {
        // ignore
    }

    public Resource checkConnect(WonderlandClientID clientID, Properties properties) {
        // anyone can connect
        return null;
    }

    public void clientConnected(WonderlandClientSender sender,
            WonderlandClientID clientID, Properties properties)
    {
        // send the client the current set of server-registered placemarks
        Set<Placemark> placemarks = PlacemarkRegistrySrvFactory.getInstance().getAllPlacemarks();
        sender.send(clientID, new PlacemarksMessage(placemarks));
    }

    public void connectionRejected(WonderlandClientID clientID) {
        // this will never happen
        throw new UnsupportedOperationException("Not supported.");
    }

    public void clientDisconnected(WonderlandClientSender sender,
            WonderlandClientID clientID) {
        // ignore
    }

    public Resource checkMessage(WonderlandClientID clientID, Message message) {
        // only allow messages from administrators
        return new GroupMemberResource("admin");
    }

    public void messageReceived(WonderlandClientSender sender,
            WonderlandClientID clientID, Message message)
    {
        // handle messages from the web server
        if (message instanceof PlacemarkNewMessage ||
                message instanceof PlacemarkRemoveMessage)
        {
            sender.send(message);
        } else {
            // unexepected message type
            throw new IllegalArgumentException("Unexpected message of type " +
                                               message.getClass().getSimpleName());
        }
    }

    public boolean messageRejected(WonderlandClientSender sender,
                                   WonderlandClientID clientID,
                                   Message message, Set<Action> requested,
                                   Set<Action> granted)
    {
        // let the system send an error message
        return true;
    }
}
