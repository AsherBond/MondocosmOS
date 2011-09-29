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
package org.jdesktop.wonderland.modules.presencemanager.server;

import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.common.messages.ErrorMessage;
import org.jdesktop.wonderland.modules.presencemanager.common.messages.CellLocationRequestMessage;
import org.jdesktop.wonderland.modules.presencemanager.common.messages.CellLocationResponseMessage;
import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;
import org.jdesktop.wonderland.modules.presencemanager.common.PresenceManagerConnectionType;
import org.jdesktop.wonderland.modules.presencemanager.common.messages.ClientConnectMessage;
import org.jdesktop.wonderland.modules.presencemanager.common.messages.ClientConnectResponseMessage;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.CellManagerMO;
import org.jdesktop.wonderland.server.comms.ClientConnectionHandler;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;
import java.util.logging.Logger;
import java.util.Properties;
import java.io.Serializable;
import java.util.Collection;
import org.jdesktop.wonderland.modules.presencemanager.common.messages.PresenceInfoChangeAliasMessage;

/**
 * Presence Manager
 * 
 * @author jprovino
 */
public class PresenceManagerConnectionHandler implements 
	ClientConnectionHandler, Serializable
{
    private static final Logger logger =
            Logger.getLogger(PresenceManagerConnectionHandler.class.getName());

    public PresenceManagerConnectionHandler() {
        super();
    }

    public ConnectionType getConnectionType() {
        return PresenceManagerConnectionType.CONNECTION_TYPE;
    }

    public void registered(WonderlandClientSender sender) {
	logger.fine("Presence Server manager connection registered");
    }

    public void clientConnected(WonderlandClientSender sender, 
	    WonderlandClientID clientID, Properties properties)
    {
	logger.fine("client connected...");
    }

    public void messageReceived(WonderlandClientSender sender, 
	    WonderlandClientID clientID, Message message) {

        logger.fine("[PresenceManagerConnectionHandler] received message " + message);

        if (message instanceof ClientConnectMessage) {
            handleClientConnect(sender, clientID, (ClientConnectMessage) message);
        } else if (message instanceof PresenceInfoChangeAliasMessage) {
            handleChangeAlias(sender, clientID, (PresenceInfoChangeAliasMessage) message);
        } else if (message instanceof CellLocationRequestMessage) {
            handleLocationRequest(sender, clientID, (CellLocationRequestMessage) message);
        } else {
            throw new UnsupportedOperationException("Unknown message: " + message);
        }
    }

    private void handleClientConnect(WonderlandClientSender sender,
	    WonderlandClientID clientID, ClientConnectMessage message)
    {
        PresenceManagerSrv pm = PresenceManagerSrvFactory.getInstance();

        // create the presence info for this user
        pm.addPresenceInfo(sender, null, clientID.getID(), message.getAvatarCellID());

        // make sure to broadcase in-range notificaitons to the player
        pm.enableInRangeNotification(clientID.getID(), clientID);

        // Send back all of the PresenceInfo data to the new client
        Collection<PresenceInfo> allInfo = pm.getAllPresenceInfo();
        sender.send(clientID, new ClientConnectResponseMessage(message.getMessageID(),
                    allInfo.toArray(new PresenceInfo[0])));
    }
    
    private void handleChangeAlias(WonderlandClientSender sender,
	    WonderlandClientID clientID, PresenceInfoChangeAliasMessage message)
    {
        PresenceManagerSrv pm = PresenceManagerSrvFactory.getInstance();
        pm.setUsernameAlias(clientID.getID(), message.getAlias());
    }

    private void handleLocationRequest(WonderlandClientSender sender,
	    WonderlandClientID clientID, CellLocationRequestMessage message)
    {
        CellID cellID = ((CellLocationRequestMessage) message).getRequestCellID();
        CellMO cell = CellManagerMO.getCell(cellID);
        if (cell == null || !cell.isLive()) {
            sender.send(clientID, new ErrorMessage(message.getMessageID(),
                        "Cell " + cellID + " not found"));
        } else {
            sender.send(clientID, new CellLocationResponseMessage(message.getMessageID(),
                        cell.getWorldTransform(null).getTranslation(null)));
        }
    }

    public void clientDisconnected(WonderlandClientSender sender, WonderlandClientID clientID) {
	logger.fine("client disconnected " + clientID.getID());

        PresenceManagerSrv pm = PresenceManagerSrvFactory.getInstance();
        pm.removePresenceInfo(clientID.getID());
    }

    private void dump(String msg) {
        PresenceManagerSrv pm = PresenceManagerSrvFactory.getInstance();

	System.out.println("\n========  " + msg);

        for (PresenceInfo info : pm.getAllPresenceInfo()) {
	    System.out.println("PI: " + info);
        }

	System.out.println("========  " + msg + "\n");
    }
}
