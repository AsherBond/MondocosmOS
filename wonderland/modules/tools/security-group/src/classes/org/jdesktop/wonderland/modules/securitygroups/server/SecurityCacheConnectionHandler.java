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
package org.jdesktop.wonderland.modules.securitygroups.server;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;
import java.util.Properties;
import java.util.Set;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.Message;
import java.io.Serializable;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.messages.OKMessage;
import org.jdesktop.wonderland.common.security.Action;
import org.jdesktop.wonderland.modules.security.server.service.GroupMemberResource;
import org.jdesktop.wonderland.modules.securitygroups.common.InvalidateGroupMessage;
import org.jdesktop.wonderland.modules.securitygroups.common.SecurityCacheConnectionType;
import org.jdesktop.wonderland.server.UserMO;
import org.jdesktop.wonderland.server.UserManager;
import org.jdesktop.wonderland.server.cell.view.AvatarCellMO;
import org.jdesktop.wonderland.server.comms.SecureClientConnectionHandler;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;
import org.jdesktop.wonderland.server.security.Resource;
import org.jdesktop.wonderland.server.spatial.UniverseManager;

/**
 * Listener for testing secure connections.  Only the administrator may
 * connect.
 */
public class SecurityCacheConnectionHandler 
        implements SecureClientConnectionHandler, Serializable
{
    private static final Logger logger =
            Logger.getLogger(SecurityCacheConnectionHandler.class.getName());

    public SecurityCacheConnectionHandler() {
        super();
    }

    public Resource checkConnect(WonderlandClientID clientID,
                                 Properties properties)
    {
        return new GroupMemberResource("admin");
    }

    public ConnectionType getConnectionType() {
        return SecurityCacheConnectionType.CONNECTION_TYPE;
    }

    public void registered(WonderlandClientSender sender) {
        logger.warning("Security cache connection registered");
    }

    public void clientConnected(WonderlandClientSender sender, 
                                WonderlandClientID clientID,
                                Properties properties) 
    {
        logger.warning("Security cache " + clientID + " connected");
    }

    public void connectionRejected(WonderlandClientID clientID) {
        logger.warning("Security cache " + clientID + " rejected");
    }

    public Resource checkMessage(WonderlandClientID clientID, Message message) {
        return null;
    }

    public void messageReceived(WonderlandClientSender sender, 
                                WonderlandClientID clientID,
                                Message message) 
    {
        logger.warning("Security cache message " + message + " received from " +
                       " sender " + clientID);

        InvalidateGroupMessage igm = (InvalidateGroupMessage) message;

        // invalidate all users identified in the message
        WebServiceUserPrincipalResolver resolver =
                WebServiceUserPrincipalResolver.getInstance();
        for (String username : igm.getUsernames()) {
            resolver.invalidate(username);
        }

        // invalidate the cell cache for each user in the message
        UniverseManager um = AppContext.getManager(UniverseManager.class);
        for (String username : igm.getUsernames()) {
            UserMO user = UserManager.getUserMO(username);
            if (user != null) {
                for (ManagedReference<AvatarCellMO> avatars : user.getAllAvatars()) {
                    um.viewRevalidate(avatars.get());
                }
            }
        }
        // send an OK back to the sender
        sender.send(clientID, new OKMessage(message.getMessageID()));
    }


    public boolean messageRejected(WonderlandClientSender sender,
                                   WonderlandClientID clientID,
                                   Message message,
                                   Set<Action> requested,
                                   Set<Action> granted)
    {
        logger.warning("Security cache message " + message + " rejected from " +
                       " sender " + clientID);
        return true;
    }

    public void clientDisconnected(WonderlandClientSender sender, WonderlandClientID clientID) {
        logger.warning("Security cache disconnected");
    }
}
