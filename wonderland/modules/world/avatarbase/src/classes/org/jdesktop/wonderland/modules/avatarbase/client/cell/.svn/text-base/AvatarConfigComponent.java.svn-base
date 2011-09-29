/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.modules.avatarbase.client.cell;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellComponent;
import org.jdesktop.wonderland.client.cell.ChannelComponent;
import org.jdesktop.wonderland.client.cell.ChannelComponent.ComponentMessageReceiver;
import org.jdesktop.wonderland.client.cell.annotation.UsesCellComponent;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.AvatarConfigComponentClientState;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.AvatarConfigInfo;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.messages.AvatarConfigMessage;

/**
 * A Cell component that represents the current avatar configured by the system.
 * It listens for messages to change the avatar configuration.
 *
 * @author paulby
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class AvatarConfigComponent extends CellComponent {

    private static Logger logger = Logger.getLogger(AvatarConfigComponent.class.getName());

    // The current avatar configuration, null if none is set
    private AvatarConfigInfo avatarConfigInfo = null;

    // If a request to change the avatar has been made, but the component has
    // not yet been initialized, then this info stores the initial value to be
    // applied when the component becomes ACTIVE.
    private AvatarConfigInfo pendingChange = null;

    @UsesCellComponent
    protected ChannelComponent channelComp;
    protected ChannelComponent.ComponentMessageReceiver msgReceiver = null;

    // A set of listeners that receive notification if the configuration of
    // the avatar has changed.
    private Set<AvatarConfigChangeListener> listenerSet = new HashSet();

    /** Constructor */
    public AvatarConfigComponent(Cell cell) {
        super(cell);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setClientState(CellComponentClientState clientState) {
        super.setClientState(clientState);
        avatarConfigInfo = ((AvatarConfigComponentClientState)clientState).getAvatarConfigInfo();
    
        // send a config change event
        if (status.ordinal() >= CellStatus.ACTIVE.ordinal()) {
            fireAvatarConfigChangeEvent(
                new AvatarConfigMessage(AvatarConfigMessage.ActionType.APPLY, 
                                        avatarConfigInfo));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setStatus(CellStatus status, boolean increasing) {
        super.setStatus(status, increasing);

        // If we are back in the disk state, then attempt to remove the message
        // receiver.
        if (status == CellStatus.DISK && increasing == false) {
            if (msgReceiver != null && channelComp != null) {
                channelComp.removeMessageReceiver(AvatarConfigMessage.class);
                msgReceiver = null;
            }
        }
        else if (status == CellStatus.ACTIVE && increasing == true) {
            // If we are being made active, then attempt to add the message
            // receiver on the channel.
            if (msgReceiver == null) {
                msgReceiver = new ComponentMessageReceiver() {
                    public void messageReceived(CellMessage message) {
                        handleConfigMessage((AvatarConfigMessage) message);
                    }
                };
                channelComp.addMessageReceiver(AvatarConfigMessage.class, msgReceiver);

                // If there is any pending update to the config (that happened
                // before the ACTIVE state, then apply it now.
                synchronized (this) {
                    if (pendingChange != null) {
                        channelComp.send(AvatarConfigMessage.newRequestMessage(pendingChange));
                        pendingChange = null;
                    }
                }
            }
        }
    }

    /**
     * Returns the current avatar configuration information in-use.
     *
     * @return The avatar configuration information
     */
    public AvatarConfigInfo getAvatarConfigInfo() {
        return avatarConfigInfo;
    }

    /**
     * Sets the new avatar configuration information and informs all of the
     * other clients of the change. If 'isLocal' is true, then do not inform
     * any other clients of the change, simply update the avatar locally.
     *
     * @param info The new avatar configuration information
     * @param isLocal True if we just want to update the avatar locally
     */
    public void requestAvatarConfigInfo(AvatarConfigInfo info, boolean isLocal) {

        // Otherwise, request a configuration update. If this component is not
        // in the active state, then set the request as 'pending'.
        synchronized (this) {
            if (isLocal == false) {
                if (channelComp == null) {
                    pendingChange = info;
                    return;
                }
                channelComp.send(AvatarConfigMessage.newRequestMessage(info));
            }
            else {
                // If we just want to change the avatar configuration locally,
                // then just fake a changed event
                fireAvatarConfigChangeEvent(AvatarConfigMessage.newRequestMessage(info));
            }
        }
    }

    /**
     * Adds a listener for avatar change events. If the listener already exists
     * this method does nothing.
     *
     * @param l The listener to add
     */
    public void addAvatarConfigChangeListener(AvatarConfigChangeListener l) {
        synchronized (listenerSet) {
            listenerSet.add(l);
        }
    }

    /**
     * Removes a listener for avatar change events. If the listener does not
     * exist, this method does nothing.
     *
     * @param l The listener to remove
     */
    public void removeAvatarConfigChangeListener(AvatarConfigChangeListener l) {
        synchronized (listenerSet) {
            listenerSet.remove(l);
        }
    }

    /**
     * Sends an event to all listeners that the avatar configuration has
     * changed.
     *
     * @param message The avatar configuration message
     */
    private void fireAvatarConfigChangeEvent(AvatarConfigMessage message) {
        synchronized (listenerSet) {
            for (AvatarConfigChangeListener l : listenerSet) {
                l.avatarConfigChanged(message);
            }
        }
    }

    /**
     * Handles when this cell component recieves a configuration message from
     * the server.
     * @param msg
     */
    private void handleConfigMessage(AvatarConfigMessage message) {
        avatarConfigInfo = message.getAvatarConfigInfo();
        fireAvatarConfigChangeEvent(message);
    }

    /**
     * Listener interface to notify that an avatar has changed.
     */
    public interface AvatarConfigChangeListener {
        /**
         * An avatar configuration has changed.
         * @param msg The configuration message
         */
        public void avatarConfigChanged(AvatarConfigMessage message);
    }
}
