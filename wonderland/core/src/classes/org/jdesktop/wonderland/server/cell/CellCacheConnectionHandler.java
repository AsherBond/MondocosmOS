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
package org.jdesktop.wonderland.server.cell;

import org.jdesktop.wonderland.server.cell.view.AvatarCellMO;
import java.io.Serializable;
import java.util.Properties;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.common.cell.CellCacheConnectionType;
import org.jdesktop.wonderland.common.cell.messages.ViewCreateResponseMessage;
import org.jdesktop.wonderland.common.cell.messages.CellHierarchyMessage;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.ErrorMessage;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.server.UserMO;
import org.jdesktop.wonderland.server.WonderlandContext;
import org.jdesktop.wonderland.server.comms.ClientConnectionHandler;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

/**
 * Handler for the cell cache connections
 * @author paulby
 */
@InternalAPI
class CellCacheConnectionHandler implements ClientConnectionHandler, Serializable {
    
    private  String viewID=null;
    private static final Logger logger = Logger.getLogger(CellCacheConnectionHandler.class.getName());
    private Properties connectionProperties;
    
    protected static final ConnectionType CLIENT_TYPE =
            CellCacheConnectionType.CLIENT_TYPE;

    public ConnectionType getConnectionType() {
        return CLIENT_TYPE;
    }

    public void registered(WonderlandClientSender sender) {
        // ignore
    }
    
    public void clientConnected(WonderlandClientSender sender,
                                WonderlandClientID clientID,
                                Properties properties)   
    {
        connectionProperties = properties;
        // Nothing to do, setup is done when we get the SET_VIEW
        // message
    }

    public void clientDisconnected(WonderlandClientSender sender,
                                   WonderlandClientID clientID)
    {
        UserMO user = WonderlandContext.getUserManager().getUser(clientID);
        AvatarCellMO avatar = user.getAvatar(clientID, viewID);
        if (avatar == null) {
            logger.severe("clientDetached has null avatar for session");
            return;
        }
        
        avatar.detach();    // Detach avatar from world

        AvatarCellCacheMO acc = avatar.getCellCache();
        acc.logout(clientID);
    }

    public void messageReceived(WonderlandClientSender sender,
                                WonderlandClientID clientID,
                                Message message)
    {
        if (message instanceof CellHierarchyMessage) {
            messageReceived(sender, clientID, (CellHierarchyMessage) message);
        } else {
            sender.send(clientID, new ErrorMessage(message.getMessageID(),
                        "Unexpected message type: " + message.getClass()));
        }
    }
  
    /**
     * When a cell message is received, dispatch it to the appropriate cell.
     * If the cell does not exist, send back an error message.
     * @param message the cell message
     * @param sender the message sender to send responses to
     */
    public void messageReceived(WonderlandClientSender sender,
                                WonderlandClientID clientID,
                                CellHierarchyMessage message)
    {        
        switch(message.getActionType()) {
            case SET_VIEW :
                // TODO - should not assume this is an avatar, could be a camera
                ViewCreateResponseMessage response = createAvatar(sender, 
                                                                    clientID,
                                                                    message);
                sender.send(clientID, response);
                
                break;
            default :
                logger.severe("Unexpected message in CellCacheClientHandler "+message.getActionType());
                sender.send(clientID, new ErrorMessage(message.getMessageID(),
                        "Unexpected message in CellCacheClientHandler: " +
                        message.getActionType()));
                break;
        }
    }
    
    private ViewCreateResponseMessage createAvatar(WonderlandClientSender sender,
                                                     WonderlandClientID clientID,
                                                     CellHierarchyMessage msg) {
        UserMO user = WonderlandContext.getUserManager().getUser(clientID);
        AvatarCellMO avatar = user.getAvatar(clientID, msg.getViewID());
        if (avatar == null) {
            user.getReference().getForUpdate(); // Mark for update
            avatar = new AvatarCellMO(user, clientID);
            viewID = msg.getViewID();
            user.putAvatar(clientID, viewID, avatar);
        }
        
        // Set the properties for this connection in the cell cache
        avatar.getCellCache().setConnectionProperties(connectionProperties);
        
        avatar.getCellCache().login(sender, clientID);
        
        return new ViewCreateResponseMessage(msg.getMessageID(), avatar.getCellID());
    }
    
    /**
     * Get the channel used for sending to all clients of this type
     * @return the channel to send to all clients
     */
    public static WonderlandClientSender getSender() {
        return WonderlandContext.getCommsManager().getSender(CLIENT_TYPE);
    }

}
