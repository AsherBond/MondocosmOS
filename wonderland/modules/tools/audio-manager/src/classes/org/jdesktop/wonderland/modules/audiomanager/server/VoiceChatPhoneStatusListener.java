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
package org.jdesktop.wonderland.modules.audiomanager.server;

import com.sun.sgs.app.ManagedReference;

import org.jdesktop.wonderland.modules.audiomanager.common.AudioManagerConnectionType;

import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatCallEndedMessage;

import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;

import com.sun.mpk20.voicelib.app.Call;
import com.sun.mpk20.voicelib.app.ManagedCallStatusListener;
import com.sun.mpk20.voicelib.app.Player;
import com.sun.mpk20.voicelib.app.VoiceManager;

import com.sun.sgs.app.AppContext;

import com.sun.voip.client.connector.CallStatus;

import org.jdesktop.wonderland.server.WonderlandContext;

import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

import org.jdesktop.wonderland.server.comms.CommsManager;

import org.jdesktop.wonderland.common.cell.CellChannelConnectionType;

import java.io.IOException;
import java.io.Serializable;

import java.util.logging.Logger;

import java.util.concurrent.ConcurrentHashMap;

import org.jdesktop.wonderland.common.messages.Message;

import org.jdesktop.wonderland.common.cell.CellTransform;

import org.jdesktop.wonderland.server.cell.CellMO;

import com.jme.math.Vector3f;

/**
 * @author jprovino
 */
public class VoiceChatPhoneStatusListener implements ManagedCallStatusListener, Serializable {

    private static final Logger logger =
        Logger.getLogger(VoiceChatPhoneStatusListener.class.getName());
     
    private String group;
    private PresenceInfo presenceInfo;
    private String externalCallID;
    private WonderlandClientID clientID;

    private boolean ended;

    public VoiceChatPhoneStatusListener(String group, PresenceInfo presenceInfo, 
	    String externalCallID, WonderlandClientID clientID) {

	this.group = group;
	this.presenceInfo = presenceInfo;
	this.externalCallID = externalCallID;
	this.clientID = clientID;

	AppContext.getManager(VoiceManager.class).addCallStatusListener(this, 
	    externalCallID);

	new AudioCallStatusListener(clientID, externalCallID, true);
    }

    public void callStatusChanged(CallStatus status) {    
	if (ended) {
	    return;
	}

	logger.finer("got status " + status);

	VoiceManager vm = AppContext.getManager(VoiceManager.class);

	switch (status.getCode()) {
        case CallStatus.ESTABLISHED:
	    stopRinging(vm);
	    break;

        case CallStatus.ENDED:
	    stopRinging(vm);
	    vm.removeCallStatusListener(this);
                
	    ended = true;

            WonderlandClientSender sender = WonderlandContext.getCommsManager().getSender(
	        AudioManagerConnectionType.CONNECTION_TYPE);

            sender.send(clientID, new VoiceChatCallEndedMessage(group, presenceInfo, 
		status.getOption("Reason")));
	    break;
	}
    }

    private void stopRinging(VoiceManager vm) {
        //Stop the ringing
	Call softphoneCall = vm.getCall(presenceInfo.getCallID());

	if (softphoneCall != null) {
	    try {
                softphoneCall.stopTreatment("ring_tone.au");
	    } catch (IOException e) {
		logger.warning("Unable to stop treatment " + softphoneCall + ":  "
		    + e.getMessage());
	    }
	}
    }

}
