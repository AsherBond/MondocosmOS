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

import java.io.IOException;

import java.util.logging.Logger;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;

import org.jdesktop.wonderland.common.cell.CellChannelConnectionType;
import org.jdesktop.wonderland.common.cell.CallID;

import org.jdesktop.wonderland.common.cell.messages.CellMessage;

import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;

import org.jdesktop.wonderland.modules.audiomanager.common.AudioManagerConnectionType;

import org.jdesktop.wonderland.modules.audiomanager.common.messages.audio.CallEndedMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.audio.CallEstablishedMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.audio.CallMigrateMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.audio.CallSpeakingMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.audio.CallMutedMessage;

import org.jdesktop.wonderland.server.WonderlandContext;

import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

import com.sun.mpk20.voicelib.app.AudioGroup;
import com.sun.mpk20.voicelib.app.AudioGroupPlayerInfo;
import com.sun.mpk20.voicelib.app.AudioGroupPlayerInfo.ChatType;
import com.sun.mpk20.voicelib.app.Call;
import com.sun.mpk20.voicelib.app.CallSetup;
import com.sun.mpk20.voicelib.app.Player;
import com.sun.mpk20.voicelib.app.PlayerSetup;
import com.sun.mpk20.voicelib.app.Spatializer;
import com.sun.mpk20.voicelib.app.VoiceManager;
import com.sun.mpk20.voicelib.app.VoiceManagerParameters;

import com.sun.voip.client.connector.CallStatus;

import com.sun.mpk20.voicelib.app.ManagedCallStatusListener;

/**
 *
 * @author jprovino
 */
public class AudioCallStatusListener implements ManagedCallStatusListener {

    private static final Logger logger =
            Logger.getLogger(AudioCallStatusListener.class.getName());

    private WonderlandClientID clientID;
    private String callID;
    private boolean isExternalCall;

    private boolean done;

    public AudioCallStatusListener(WonderlandClientID clientID, String callID) {
	this(clientID, callID, false);
    }

    public AudioCallStatusListener(WonderlandClientID clientID, String callID,
	    boolean isExternalCall) {

	this.clientID = clientID;
	this.callID = callID;
	this.isExternalCall = isExternalCall;

	AppContext.getManager(VoiceManager.class).addCallStatusListener(this, callID);
    }

    public void done() {
	if (done) {
	    return;
	}

	done = true;

	AppContext.getManager(VoiceManager.class).removeCallStatusListener(this, callID);
    }

    public void callStatusChanged(CallStatus status) {
	if (done) {
	    return;
	}

	logger.finer("AudioCallStatusListener got call status:  " + status);

	String callId = status.getCallId();

	if (callId == null) {
	    logger.warning("No callId in status:  " + status);
	    return;
	}

	int code = status.getCode();

	VoiceManager vm = AppContext.getManager(VoiceManager.class);

	AudioGroup audioGroup;

	Call call = vm.getCall(callId);

	Player player = vm.getPlayer(callId);

	AudioGroup secretAudioGroup;

	WonderlandClientSender sender = WonderlandContext.getCommsManager().getSender(AudioManagerConnectionType.CONNECTION_TYPE);

	switch (code) {
	case CallStatus.ESTABLISHED:
	    if (player == null) {
		logger.warning("Couldn't find player for " + status);
		return;
	    }

	    //vm.dump("all");
	    player.setPrivateMixes(true);

	    sender.send(new CallEstablishedMessage(callID));
	    break;

	case CallStatus.MUTED:
	    sender.send(new CallMutedMessage(callID, true));
	    break;

	case CallStatus.UNMUTED:
	    sender.send(new CallMutedMessage(callID, false));
	    break;

        case CallStatus.STARTEDSPEAKING:
	    if (player == null) {
		logger.warning("Couldn't find player for " + status);
		return;
	    }

	    secretAudioGroup = getSecretAudioGroup(player);

	    if (playerIsChatting(player)) {
		VoiceChatHandler.getInstance().setSpeaking(player, callID, true, secretAudioGroup);
	    }

	    if (secretAudioGroup != null) {
		return;
	    }

	    sender.send(new CallSpeakingMessage(callID, true));
            break;

        case CallStatus.STOPPEDSPEAKING:
	    if (player == null) {
		logger.warning("Couldn't find player for " + status);
		return;
	    }

	    secretAudioGroup = getSecretAudioGroup(player);

	    if (playerIsChatting(player)) {
		VoiceChatHandler.getInstance().setSpeaking(player, callID, false, secretAudioGroup);
	    }

	    if (secretAudioGroup != null) {
		return;
	    }

	    sender.send(new CallSpeakingMessage(callID, false));
            break;

	case CallStatus.MIGRATED:
	    if (isExternalCall) {
		return;
	    }

	    sender = WonderlandContext.getCommsManager().getSender(CellChannelConnectionType.CLIENT_TYPE);
	    sender.send(clientID, new CallMigrateMessage(callID, true));
	    break;

	case CallStatus.MIGRATE_FAILED:
	    if (isExternalCall) {
		return;
	    }

	    sender.send(clientID, new CallMigrateMessage(callID, false));
	    break;

	case CallStatus.ENDED:
	    if (done) {
		return;
	    }

if (false) {
	    if (player != null) {
	        AudioGroup[] audioGroups = player.getAudioGroups();

	        for (int i = 0; i < audioGroups.length; i++) {
		    audioGroups[i].removePlayer(player);
	        }

		if (player.getSetup().isOutworlder) {
	            vm.removePlayer(player);
		}
	    } else {
		logger.warning("Couldn't find player for " + status);
	    } 
}

	    done();
	    sender.send(new CallEndedMessage(callID, status.getOption("Reason")));
            break;
	  
	case CallStatus.BRIDGE_OFFLINE:
            logger.info("Bridge offline: " + status);
		// XXX need a way to tell the voice manager to reset all of the private mixes.
	    Call c = vm.getCall(callId);

	    if (callId == null || callId.length() == 0) {
                /*
                 * After the last BRIDGE_OFFLINE notification
                 * we have to tell the voice manager to restore
                 * all the pm's for live players.
                 */
                logger.fine("Restoring private mixes...");
	    } else {
		if (c == null) {
		    logger.warning("No call for " + callId);
		    break;
		}

		Player p = c.getPlayer();

		if (p == null) {
		    logger.warning("No player for " + callId);
		    break;
		}

		try {
		    c.end(true);
		} catch (IOException e) {
		    logger.warning("Unable to end call " + callId);
		}

		try {
		    AudioManagerConnectionHandler.setupCall(
		 	callId, c.getSetup(), -p.getX(), p.getY(), p.getZ(), p.getOrientation());
		} catch (IOException e) {
		    logger.warning("Unable to setupCall " + c + " "
			+ e.getMessage());
		}
	    }

            break;
        }
    }

    private boolean playerIsChatting(Player player) {
	VoiceManager vm = AppContext.getManager(VoiceManager.class);

	VoiceManagerParameters parameters = vm.getVoiceManagerParameters();

	AudioGroup[] audioGroups = player.getAudioGroups();

	for (int i = 0; i < audioGroups.length; i++) {
	    if (audioGroups[i].equals(parameters.livePlayerAudioGroup) == false &&
	    	    audioGroups[i].equals(parameters.stationaryPlayerAudioGroup) == false) {

		return true;
	    }
	}

	return false;
    }

    private AudioGroup getSecretAudioGroup(Player player) {
	AudioGroup[] audioGroups = player.getAudioGroups();

	for (int i = 0; i < audioGroups.length; i++) {
            AudioGroupPlayerInfo info = audioGroups[i].getPlayerInfo(player);

            if (info.chatType == AudioGroupPlayerInfo.ChatType.SECRET) {
		return audioGroups[i];
	    }
	}

	return null;
    }

}
