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
package org.jdesktop.wonderland.modules.avatarbase.server.cell;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.AvatarConfigComponentClientState;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.AvatarConfigComponentServerState;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.AvatarConfigInfo;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.messages.AvatarConfigMessage;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.messages.AvatarConfigMessage.ActionType;
import org.jdesktop.wonderland.server.cell.CellComponentMO;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.ChannelComponentMO;
import org.jdesktop.wonderland.server.cell.ChannelComponentMO.ComponentMessageReceiver;
import org.jdesktop.wonderland.server.cell.annotation.UsesCellComponentMO;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

/**
 * A server-side Cell component that represents the current avatar configuration.
 *
 * @author paulby
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class AvatarConfigComponentMO extends CellComponentMO {

    private static Logger logger = Logger.getLogger(AvatarConfigComponentMO.class.getName());

    @UsesCellComponentMO(ChannelComponentMO.class)
    protected ManagedReference<ChannelComponentMO> channelComponentRef = null;

    // The avatar configuration information
    private AvatarConfigInfo avatarConfigInfo = null;

    /** Constructor */
    public AvatarConfigComponentMO(CellMO cell) {
        super(cell);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getClientClass() {
        return "org.jdesktop.wonderland.modules.avatarbase.client.cell.AvatarConfigComponent";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLive(boolean live) {
        boolean oldLive = isLive();
        super.setLive(live);

        // If we are not changing the state then just return
        if (oldLive == live) {
            return;
        }

        // Otherwise, either add or remove the message receiver to listen for
        // avatar configuration events
        ChannelComponentMO channel = channelComponentRef.getForUpdate();
        if (live) {
            AvatarConfigMessageReceiver recv = new AvatarConfigMessageReceiver(this);
            channel.addMessageReceiver(AvatarConfigMessage.class, recv);
        } else {
            channel.removeMessageReceiver(AvatarConfigMessage.class);
        }
    }

    /**
     * Handles the avatar configuration message requests from the client.
     */
    private void handleMessage(WonderlandClientID clientID, AvatarConfigMessage msg) {

        // Update the current value for the avatar configuration, and if the
        // component is live, then send a message to all clients.
        avatarConfigInfo = msg.getAvatarConfigInfo();
        if (isLive() == true) {
            ChannelComponentMO channel = channelComponentRef.getForUpdate();
            AvatarConfigMessage resp = new AvatarConfigMessage(ActionType.APPLY, avatarConfigInfo);
            channel.sendAll(clientID, resp);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CellComponentClientState getClientState(CellComponentClientState clientState,
            WonderlandClientID clientID, ClientCapabilities capabilities) {

        if (clientState == null) {
            clientState = new AvatarConfigComponentClientState();
        }
        ((AvatarConfigComponentClientState)clientState).setAvatarConfigInfo(avatarConfigInfo);
        return super.getClientState(clientState, clientID, capabilities);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CellComponentServerState getServerState(CellComponentServerState state) {
        if (state == null) {
            state = new AvatarConfigComponentServerState();
        }
        ((AvatarConfigComponentServerState)state).setAvatarConfigInfo(avatarConfigInfo);
        return super.getServerState(state);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setServerState(CellComponentServerState state) {
        super.setServerState(state);
        avatarConfigInfo = ((AvatarConfigComponentServerState)state).getAvatarConfigInfo();
    }

    /**
     * Handles messages for the avatar configuration.
     */
    private static class AvatarConfigMessageReceiver implements ComponentMessageReceiver {

        // Reference to the component associated with this message handler
        private ManagedReference<AvatarConfigComponentMO> compRef;

        public AvatarConfigMessageReceiver(AvatarConfigComponentMO comp) {
            compRef = AppContext.getDataManager().createReference(comp);
        }

        public void messageReceived(WonderlandClientSender sender,
                WonderlandClientID clientID, CellMessage message) {


            AvatarConfigMessage ent = (AvatarConfigMessage) message;
            switch (ent.getActionType()) {

                case REQUEST:
                    // TODO check permisions
                    compRef.getForUpdate().handleMessage(clientID, ent);
                    break;

                case APPLY:
                    logger.severe("Server should never receive APPLY messages");
                    break;
            }
        }

        /**
         * Record the message -- part of the event recording mechanism.
         * Nothing more than the message is recorded in this implementation,
         * delegate it to the recorder manager
         */
        public void recordMessage(WonderlandClientSender sender,
                WonderlandClientID clientID, CellMessage message) {
            
//            RecorderManager.getDefaultManager().recordMessage(sender, clientID, message);
        }
    }
}
