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
package org.jdesktop.wonderland.modules.audiomanager.common.messages;

import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;

import org.jdesktop.wonderland.common.ExperimentalAPI;

import org.jdesktop.wonderland.common.messages.Message;

/**
 * Message indicating whether someone is muted or unmuted
 * @author jprovino
 */
@ExperimentalAPI
public class PlayerInRangeMessage extends Message {

    private String playerID;
    private String playerInRangeID;
    private boolean isInRange;

    public PlayerInRangeMessage(String playerID, String playerInRangeID, boolean isInRange) {
	this.playerID = playerID;
	this.playerInRangeID = playerInRangeID;
	this.isInRange = isInRange;
    }

    public String getPlayerID() {
	return playerID;
    }

    public String getPlayerInRangeID() {
	return playerInRangeID;
    }

    public boolean isInRange() {
	return isInRange;
    }

}
