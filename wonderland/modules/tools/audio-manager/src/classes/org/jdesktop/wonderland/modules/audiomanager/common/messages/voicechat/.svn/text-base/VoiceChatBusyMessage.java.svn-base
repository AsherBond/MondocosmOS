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

import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;

/**
 *
 * @author jprovino
 */
public class VoiceChatBusyMessage extends VoiceChatMessage {
    
    private PresenceInfo caller;
    private PresenceInfo callee;
    private ChatType chatType;

    public VoiceChatBusyMessage(String group, PresenceInfo caller, 
	    PresenceInfo callee, ChatType chatType) {

	super(group);

	this.caller = caller;
	this.callee = callee;
	this.chatType = chatType;
    }

    public PresenceInfo getCaller() {
	return caller;
    }

    public PresenceInfo getCallee() {
	return callee;
    }

    public ChatType getChatType() {
	return chatType;
    }
    
}
