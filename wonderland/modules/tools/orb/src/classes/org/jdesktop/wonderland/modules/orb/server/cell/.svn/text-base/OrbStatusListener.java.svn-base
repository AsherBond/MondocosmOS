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
package org.jdesktop.wonderland.modules.orb.server.cell;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ClientSession;

import com.sun.mpk20.voicelib.app.Call;
import com.sun.mpk20.voicelib.app.ManagedCallStatusListener;
import com.sun.mpk20.voicelib.app.Player;
import com.sun.mpk20.voicelib.app.VoiceManager;

import org.jdesktop.wonderland.common.cell.CallID;
import org.jdesktop.wonderland.common.cell.CellID;

import com.sun.voip.client.connector.CallStatus;

import org.jdesktop.wonderland.server.WonderlandContext;

import org.jdesktop.wonderland.server.cell.CellManagerMO;

import org.jdesktop.wonderland.server.comms.CommsManager;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

import org.jdesktop.wonderland.modules.orb.common.messages.OrbAttachMessage;
import org.jdesktop.wonderland.modules.orb.common.messages.OrbSpeakingMessage;
import org.jdesktop.wonderland.modules.orb.common.messages.OrbEndCallMessage;
import org.jdesktop.wonderland.modules.orb.common.messages.OrbMuteCallMessage;

import org.jdesktop.wonderland.common.cell.CellChannelConnectionType;

import java.io.IOException;
import java.io.Serializable;

import java.util.logging.Logger;

public class OrbStatusListener implements ManagedCallStatusListener,
        Serializable {

    private static final Logger logger =
        Logger.getLogger(OrbStatusListener.class.getName());

    private CellID orbCellID;
    private CellID hostCellID;

    private String callID;

    public OrbStatusListener(OrbCellMO orbCellMO, String callID) {
	this.callID = callID;

	orbCellID = orbCellMO.getCellID();

	AppContext.getManager(VoiceManager.class).addCallStatusListener(this, callID);
    }

    public void setHostCellID(CellID hostCellID) {
	this.hostCellID = hostCellID;
    }

    public void removeCallStatusListener(CellID hostCellID) {
	AppContext.getManager(VoiceManager.class).removeCallStatusListener(this, 
	    CallID.getCallID(hostCellID));
    }

    private boolean muteMessageSpoken;
    private boolean isMuted;

    private boolean ended;

    public void callStatusChanged(CallStatus status) {
	logger.finest("Orb Status:  " + status);
        
	String callID = status.getCallId();

	if (callID == null || callID.length() == 0) {
	    logger.finest("Missing call id in status:  " + status);
	    return;
	}

        CommsManager cm = WonderlandContext.getCommsManager();

        WonderlandClientSender sender = cm.getSender(CellChannelConnectionType.CLIENT_TYPE);

        switch (status.getCode()) {
	case CallStatus.DTMF_KEY:
	    handleDtmfKey(status);
	    break;
	
        case CallStatus.MUTED:
            sender.send(new OrbMuteCallMessage(orbCellID, true));
            break;

        case CallStatus.UNMUTED:
            sender.send(new OrbMuteCallMessage(orbCellID, false));
            break;

        case CallStatus.STARTEDSPEAKING:            
	    if (isMuted && muteMessageSpoken == false) {
		muteMessageSpoken = true;

		VoiceManager vm = AppContext.getManager(VoiceManager.class);

		Call call = vm.getCall(callID);

	 	try {
                    call.playTreatment("tts:You are muted.");
                    call.playTreatment("tts:Press star, three to unmute");
		} catch (IOException e) {
		    logger.warning("unable to play you are muted treatment to " + call
			+ ":  " + e.getMessage());
		}
	    }

            sender.send(new OrbSpeakingMessage(orbCellID, true));
            break;
            
        case CallStatus.STOPPEDSPEAKING:
            sender.send(new OrbSpeakingMessage(orbCellID, false));
            break;
            
	case CallStatus.TREATMENTDONE:
	    String treatment = status.getOption("Treatment");

	    logger.fine("Treatment done:  " + treatment);

	    if (treatment.equals(help)) {
		//VoiceHandler voiceHandler = VoiceHandlerImpl.getInstance();
	        //voiceHandler.setListenAttenuator(callID, listenAttenuator);
		playingHelp = false;
	    }

	    break;

        case CallStatus.ENDED: 
	    if (ended) {
		return;
	    }

	    System.out.println("Orb ended " + callID + " this call id " + this.callID);

	    if (this.callID.equals(callID)) {
	        endCall(callID);
                sender.send(new OrbEndCallMessage(orbCellID));
	    } else {
		if (hostCellID != null) {
                    sender.send(new OrbAttachMessage(orbCellID, hostCellID, false));
		}
	    }
	    break;
	}
    }

    public void endCall(String callID) {
	if (ended) {
	    return;
	}

	ended = true;

	AppContext.getManager(VoiceManager.class).removeCallStatusListener(this, callID);

	CellManagerMO.getCellManager().removeCellFromWorld(
	    CellManagerMO.getCellManager().getCell(orbCellID));
    }
 	
    private boolean starPressed;

    private boolean playingHelp;
    private double listenAttenuator;

    private String help = "tts:star, pound, lists the number of people in range."
        + " star, two, mutes your call."
        + " star, three, unmutes your call."
        + " star, eight, lowers the volume you hear."
        + " star, nine, raises the volume you hear.";

    private void handleDtmfKey(CallStatus status) {
	String callID = status.getCallId();

	if (callID == null) {
	    return;
	}

	String dtmfKey = status.getDtmfKey();

	if (dtmfKey.equals("*")) {
	    starPressed = true;
	    return;
	}

	if (starPressed == false) {
	    return;
	}

	starPressed = false;

	VoiceManager vm = AppContext.getManager(VoiceManager.class);

	Call call = vm.getCall(callID);

	if (call == null) {
	    logger.warning("No Call for " + callID);
	    return;
	}

	if (dtmfKey.equals("4")) {
	    if (playingHelp) {
		return;
	    }

	    playingHelp = true;

	    //listenAttenuator = voiceHandler.getListenAttenuator(callID);
	    //voiceHandler.setListenAttenuator(callID, listenAttenuator / 3);

	    try {
	        call.playTreatment(help);
	    } catch (IOException e) {
		logger.warning("unable to play help treatment to " + call
		    + ":  " + e.getMessage());
	    }
	    return;
	}
    
	if (dtmfKey.equals("6")) {
	    try {
	        call.playTreatment("tts:Mary had a little lamb, little lamb, little lamb.");
	        call.playTreatment("tts:Mary had a little lamb, its fleece was white as snow.");
	    } catch (IOException e) {
		logger.warning("unable to play treatment to " + call
		    + ":  " + e.getMessage());
	    }

	    return;
	}

	if (dtmfKey.equals("7")) {
	    try {
	        call.playTreatment("tts:And everywhere that Mary went, the lamb was sure to go.");
	    } catch (IOException e) {
		logger.warning("unable to play treatment to " + call
		    + ":  " + e.getMessage());
	    }
	    return;
	}

	if (dtmfKey.equals("#")) {
	    Player p = vm.getPlayer(callID);

	    if (p == null) {
		logger.warning("No Player for " + callID);
		return;
	    }

	    int n = p.getPlayersInRange().length;

	    try {
                if (n == 0) {
                    call.playTreatment("tts:There is no one in range.");
                } else if (n == 1) {
                    call.playTreatment("tts:There is one person in range.");
                } else {
                    call.playTreatment("tts:There are " + n + " people in range.");
	        }
	    } catch (IOException e) {
		logger.warning("unable to play treatment with players in rang to " + call
		    + ":  " + e.getMessage());
	    }

	    return;
	}

	if (dtmfKey.equals("2")) {
	    try {
	        call.mute(true);
	    } catch (IOException e) {
		logger.warning("Unable to mute call " + call
		    + ":  " + e.getMessage());
	    }

	    isMuted = true;

	    OrbMuteCallMessage orbMuteCallMessage = new OrbMuteCallMessage(orbCellID, true);

            // send to everybody cellChannel.send(cellChannel.getSessions(), orbMessage.getBytes());            
	    try {
	        call.playTreatment("tts:muted");
	    } catch (IOException e) {
		logger.warning("unable to play muted treatment to " + call
		    + ":  " + e.getMessage());
	    }
	    return;
	}

	if (dtmfKey.equals("3")) {
	    try {
	        call.mute(false);
	    } catch (IOException e) {
		logger.warning("Unable to unmute call " + call
		    + ":  " + e.getMessage());
	    }

	    isMuted = false;

	    try {
	        call.playTreatment("tts:un muted");
	    } catch (IOException e) {
		logger.warning("unable to play unmuted treatment to " + call
		    + ":  " + e.getMessage());
	    }
	    OrbMuteCallMessage orbUnmuteCallMessage = new OrbMuteCallMessage(orbCellID, false);

            // send to everybody cellChannel.send(cellChannel.getSessions(), orbMessage.getBytes());            
	    return;
	}

	if (dtmfKey.equals("8")) {
	    double volume = 0; //voiceHandler.getListenAttenuator(callID);
	    volume -= .2;

	    if (volume < 0) {
	        volume = 0;
	    }

	    logger.finer("decrease volume to " + volume);
	    //voiceHandler.setListenAttenuator(callID, volume);
	    return;
	}

	if (dtmfKey.equals("9")) {
	    double volume = 0; ///voiceHandler.getListenAttenuator(callID);
	    volume += .2;

	    logger.finer("increase volume to " + volume);
	    //voiceHandler.setListenAttenuator(callID, volume);
	    return;
	}
    }
}
