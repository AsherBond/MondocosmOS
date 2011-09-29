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
package org.jdesktop.wonderland.modules.audiomanager.common.messages.audio;

import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;

import org.jdesktop.wonderland.common.ExperimentalAPI;

import org.jdesktop.wonderland.common.messages.Message;

/**
 * Message indicating whether someone started or stopped speaking.
 * @author jprovino
 */
@ExperimentalAPI
public class CallMutedMessage extends Message {

    private String callID;
    private boolean isMuted;

    public CallMutedMessage(String callID, boolean isMuted) {
	this.callID = callID;
	this.isMuted = isMuted;
    }

    public String getCallID() {
	return callID;
    }

    public boolean isMuted() {
	return isMuted;
    }

}
