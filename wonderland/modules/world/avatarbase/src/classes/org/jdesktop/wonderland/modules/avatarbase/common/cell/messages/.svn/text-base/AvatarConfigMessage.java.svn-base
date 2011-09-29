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
package org.jdesktop.wonderland.modules.avatarbase.common.cell.messages;

import org.jdesktop.wonderland.modules.avatarbase.common.cell.AvatarConfigInfo;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;

/**
 * A message indicating that the avatar model has been updated. This message
 * provides information to find the proper avatar loader on the client and
 * load the avatar.
 *
 * @author paulby
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class AvatarConfigMessage extends CellMessage {

    /**
     * Enumeration of the type of message:
     * REQUEST: A request from the client to change the avatar configuration
     * APPLY: A response sent to all clients to change the avatar configuration
     */
    public enum ActionType { REQUEST, APPLY };
    private ActionType actionType;

    // The configuration information necessary to configure the avatar
    private AvatarConfigInfo avatarConfigInfo = null;

    /** Constructor */
    public AvatarConfigMessage(ActionType actionType, AvatarConfigInfo info) {
        this.actionType = actionType;
        this.avatarConfigInfo = info;
    }

    /**
     * Returns the avatar configuration information.
     * @return The avatar config info
     */
    public AvatarConfigInfo getAvatarConfigInfo() {
        return avatarConfigInfo;
    }

    /**
     * Returns the type of the action: REQUEST or APPLY.
     * @return Either REQUEST or APPLY
     */
    public ActionType getActionType() {
        return actionType;
    }

    /**
     * Given a request message, return appropriate apply message.
     *
     * @param msg The original avatar info request message
     * @return An apply message corresponding to the request
     */
    public static AvatarConfigMessage newApplyMessage(AvatarConfigMessage msg) {
        return new AvatarConfigMessage(ActionType.APPLY, msg.getAvatarConfigInfo());
    }

    /**
     * Returns an avatar configuration request message given the configuration
     * information.
     * 
     * @param info The new avatar configuration information
     * @return A request message corresponding
     */
    public static AvatarConfigMessage newRequestMessage(AvatarConfigInfo info) {
        return new AvatarConfigMessage(ActionType.REQUEST, info);
    }
}
