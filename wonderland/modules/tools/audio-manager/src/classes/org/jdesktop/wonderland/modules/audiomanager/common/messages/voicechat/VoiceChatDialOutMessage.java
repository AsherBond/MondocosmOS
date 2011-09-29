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
package org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat;

import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatMessage.ChatType;

import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;

/**
 *
 * @author jprovino
 */
public class VoiceChatDialOutMessage extends VoiceChatMessage {   
    
    String group;
    String softphoneCallID;
    ChatType chatType;
    PresenceInfo callee;
    String phoneNumber;

    public VoiceChatDialOutMessage(String group, String softphoneCallID, ChatType chatType, 
	    PresenceInfo callee, String phoneNumber) {

	super(group);

	this.softphoneCallID = softphoneCallID;
	this.chatType = chatType;
	this.callee = callee;
	this.phoneNumber = phoneNumber;	
    }

    public ChatType getChatType() {
	return chatType;
    }

    public String getSoftphoneCallID() {
	return softphoneCallID;
    }

    public PresenceInfo getCallee() {
	return callee;
    }

    public String getPhoneNumber() {
	return phoneNumber;
    }

}
