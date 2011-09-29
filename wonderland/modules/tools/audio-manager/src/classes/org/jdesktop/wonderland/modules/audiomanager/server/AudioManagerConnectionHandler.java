/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., All Rights Reserved
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

import org.jdesktop.wonderland.common.messages.Message;

import org.jdesktop.wonderland.modules.audiomanager.common.AudioManagerConnectionType;

import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;

import org.jdesktop.wonderland.modules.audiomanager.common.messages.AudioVolumeMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.ChangeUsernameAliasMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.GetPlayersInRangeRequestMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.GetPlayersInRangeResponseMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.GetVoiceBridgeRequestMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.GetVoiceBridgeResponseMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.MuteCallRequestMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.PlaceCallRequestMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.PlayTreatmentRequestMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.TransferCallRequestMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.UDPPortTestMessage;

import org.jdesktop.wonderland.modules.audiomanager.common.messages.audio.EndCallMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.audio.CallEndedMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.audio.CallMigrateMessage;

import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatMessage;

import org.jdesktop.wonderland.common.comms.ConnectionType;

import org.jdesktop.wonderland.server.cell.CellManagerMO;
import org.jdesktop.wonderland.server.cell.CellMO;

import org.jdesktop.wonderland.server.comms.ClientConnectionHandler;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

import org.jdesktop.wonderland.server.comms.WonderlandClientID;

import java.io.Serializable;

import java.util.logging.Logger;

import java.util.Properties;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.util.ScalableHashMap;

import com.sun.mpk20.voicelib.app.AudioGroupPlayerInfo;
import com.sun.mpk20.voicelib.app.BridgeInfo;
import com.sun.mpk20.voicelib.app.Call;
import com.sun.mpk20.voicelib.app.CallSetup;
import com.sun.mpk20.voicelib.app.Player;
import com.sun.mpk20.voicelib.app.PlayerSetup;
import com.sun.mpk20.voicelib.app.Spatializer;
import com.sun.mpk20.voicelib.app.VoiceManager;
import com.sun.mpk20.voicelib.app.VoiceManagerParameters;

import com.sun.voip.CallParticipant;

import java.io.IOException;

import java.math.BigInteger;

/**
 * Audio Manager
 * 
 * @author jprovino
 */
public class AudioManagerConnectionHandler implements ClientConnectionHandler, 
	ManagedObject, Serializable {

    private static final Logger logger =
            Logger.getLogger(AudioManagerConnectionHandler.class.getName());

    private ManagedReference<ScalableHashMap<BigInteger, String>> sessionCallIDMapRef;

    private ManagedReference<ScalableHashMap<String, ManagedReference<AudioCallStatusListener>>> 
	callIDListenerMapRef;

    private static ManagedReference<AudioManagerConnectionHandler> handlerRef;

    public static AudioManagerConnectionHandler getInstance() {
        if (handlerRef == null) {
            AudioManagerConnectionHandler handler = new AudioManagerConnectionHandler();
	    handlerRef = AppContext.getDataManager().createReference(handler);
	    return handler;
        }

        return handlerRef.get();
    }

    private AudioManagerConnectionHandler() {
        super();

	ScalableHashMap<BigInteger, String> sessionCallIDMap = new ScalableHashMap();

	sessionCallIDMapRef = AppContext.getDataManager().createReference(sessionCallIDMap);

	ScalableHashMap<String, ManagedReference<AudioCallStatusListener>> callIDListenerMap = new ScalableHashMap();

	callIDListenerMapRef = AppContext.getDataManager().createReference(callIDListenerMap);
    }

    public ConnectionType getConnectionType() {
        return AudioManagerConnectionType.CONNECTION_TYPE;
    }

    public void registered(WonderlandClientSender sender) {
        logger.fine("Audio Server manager connection registered");
    }

    public void clientConnected(WonderlandClientSender sender,
            WonderlandClientID clientID, Properties properties) {

        logger.fine("client connected...");
    }

    public void messageReceived(WonderlandClientSender sender,
            WonderlandClientID clientID, Message message) {

        VoiceManager vm = AppContext.getManager(VoiceManager.class); 

	if (message instanceof GetPlayersInRangeRequestMessage) {
	    GetPlayersInRangeRequestMessage msg = (GetPlayersInRangeRequestMessage) message;

	    Player player = vm.getPlayer(msg.getPlayerID());

	    if (player == null) {
		logger.warning("No player for " + msg.getPlayerID());
		return;

	    }

	    Player[] playersInRange = player.getPlayersInRange();

	    String[] playerIDList = new String[playersInRange.length];

	    for (int i = 0; i < playersInRange.length; i++) {
		playerIDList[i] = playersInRange[i].getId();		
	    }
	
	    sender.send(clientID, new GetPlayersInRangeResponseMessage(msg.getPlayerID(),
		playerIDList));

	    return;
	}

        if (message instanceof UDPPortTestMessage) {
	    UDPPortTestMessage msg = (UDPPortTestMessage) message;

	    try {
	        vm.testUDPPort(msg.getHost(), msg.getPort(), msg.getDuration());
	    } catch (IOException e) {
		logger.warning("Unable to test udp port " + msg.getPort()
		    + ": " + e.getMessage());
	    }

	    return;
	}

        if (message instanceof GetVoiceBridgeRequestMessage) {
            logger.fine("Got GetVoiceBridgeMessage");

	    String callID = ((GetVoiceBridgeRequestMessage) message).getCallID();

	    if (callID != null) {
		logger.info("Ending existing call " + callID);

		Call call = vm.getCall(callID);

		if (call != null) {
		    try {
		        call.end(false);
		    } catch (IOException e) {
			logger.info("Unable to end call " + call
			   + " " + e.getMessage());
		    }
		} else {
		    logger.info("Can't find call for " + callID);
		}
	    }

            BridgeInfo bridgeInfo;

            try {
                bridgeInfo = vm.getVoiceBridge();

                logger.info("Sending voice bridge info '" + bridgeInfo + "'");
            } catch (IOException e) {
                logger.warning("unable to get voice bridge:  " + e.getMessage());
                return;
            }

            sender.send(clientID, new GetVoiceBridgeResponseMessage(bridgeInfo.toString()));
            return;
        }

        if (message instanceof PlaceCallRequestMessage) {
            logger.fine("Got PlaceCallMessage from " + clientID);

            placeCall(clientID, (PlaceCallRequestMessage) message);
            return;
        }

	if (message instanceof EndCallMessage) {
	    EndCallMessage msg = (EndCallMessage) message;

            String callID = msg.getCallID();

            Call call = vm.getCall(callID);

            if (call == null) {
                logger.fine("Unable to end call " + callID);
                return;
            }

	    try {
		vm.endCall(call, true);
            } catch (IOException e) {
                logger.warning(
                    "Unable to end call " + call + ":  " + e.getMessage());
            }

	    sender.send(new CallEndedMessage(msg.getCallID(), msg.getReason()));
	    return;
	}

        if (message instanceof MuteCallRequestMessage) {
            MuteCallRequestMessage msg = (MuteCallRequestMessage) message;

            String callID = msg.getCallID();

            Call call = vm.getCall(callID);

            if (call == null) {
                logger.info("Unable to mute/unmute call " + callID);
                return;
            }

            try {
                call.mute(msg.isMuted());
            } catch (IOException e) {
                logger.warning("Unable to mute/unmute call " + callID + ": " + e.getMessage());
                return;
            }

            return;
        }

        if (message instanceof TransferCallRequestMessage) {
            TransferCallRequestMessage msg = (TransferCallRequestMessage) message;

            String callID = msg.getPresenceInfo().getCallID();

            Call call = vm.getCall(callID);

            if (call == null) {
                if (msg.getCancel() == true) {
                    return;
                }

                double x = 0;
                double y = 0;
                double z = 0;
                double orientation = 0;

                Player player = vm.getPlayer(callID);

                if (player != null) {
                    x = -player.getX();
                    y = player.getY();
                    z = player.getZ();
                    orientation = player.getOrientation();
                }

                placeCall(clientID, new PlaceCallRequestMessage(msg.getPresenceInfo(), 
		    msg.getPhoneNumber(), x, y, z, orientation, true));
                return;
            }

            CallParticipant cp = call.getSetup().cp;

            if (msg.getCancel() == true) {
                try {
                    call.transfer(cp, true);
                } catch (IOException e) {
                    logger.warning("Unable to cancel call transfer:  " + e.getMessage());
                }
                return;
            }

            if (msg.getPhoneNumber().equals(cp.getPhoneNumber())) {
		sender.send(clientID, new CallMigrateMessage(msg.getPresenceInfo().getCallID(), true));
                return;
            }

            cp.setPhoneNumber(msg.getPhoneNumber());

            setJoinConfirmation(cp);

            try {
                call.transfer(cp, false);
            } catch (IOException e) {
                logger.warning("Unable to transfer call:  " + e.getMessage());
            }
            return;
        }

	if (message instanceof AudioVolumeMessage) {
	    handleAudioVolume(sender, clientID, (AudioVolumeMessage) message);
	    return;
	}

	if (message instanceof ChangeUsernameAliasMessage) {
	    sender.send(message);
	    return;
	}

        if (message instanceof VoiceChatMessage) {
            VoiceChatHandler.getInstance().processVoiceChatMessage(sender, clientID,
                    (VoiceChatMessage) message);
            return;
        }

        if (message instanceof PlayTreatmentRequestMessage) {
            PlayTreatmentRequestMessage msg = (PlayTreatmentRequestMessage) message;

            Call call = vm.getCall(msg.getCallID());

            if (call == null) {
                logger.warning("No call for " + msg.getCallID());
                return;
            }

            try {
                call.playTreatment(msg.getTreatment());
            } catch (IOException e) {
                logger.warning("Unable to play treatment " + msg.getTreatment() 
		    + " to call " + call + ": " + e.getMessage());
            }
            return;
        }

        throw new UnsupportedOperationException("Unknown message:  " + message);
    }

    private static final String JOIN_SOUND = "joinBELL.au";

    private void placeCall(WonderlandClientID clientID, PlaceCallRequestMessage msg) {
        PresenceInfo info = msg.getPresenceInfo();

        CellMO cellMO = CellManagerMO.getCellManager().getCell(info.getCellID());

        AudioParticipantComponentMO audioParticipantComponentMO =
                cellMO.getComponent(AudioParticipantComponentMO.class);

        if (audioParticipantComponentMO == null) {
            logger.warning("Cell " + cellMO.getCellID() + " doesn't have an AudioParticipantComponent!");
            return;
        }

        CallSetup setup = new CallSetup();

        CallParticipant cp = new CallParticipant();

        setup.cp = cp;

        String callID = info.getCallID();

        if (callID == null) {
            logger.fine("Can't place call to " + msg.getSipURL() + ".  No cell for " + callID);
            return;
        }

        VoiceManager vm = AppContext.getManager(VoiceManager.class);

	Call call = vm.getCall(callID);

	if (call != null) {
	    call.getSetup().ended = true;  // make it look like it ended already
	}
	
	ScalableHashMap<String, ManagedReference<AudioCallStatusListener>> callIDListenerMap = callIDListenerMapRef.get();

	ManagedReference<AudioCallStatusListener> audioCallStatusListenerRef = callIDListenerMap.remove(callID);

	if (audioCallStatusListenerRef != null) {
	    audioCallStatusListenerRef.get().done();
	}

        AudioCallStatusListener audioCallStatusListener = new AudioCallStatusListener(clientID, callID);

	audioCallStatusListenerRef = AppContext.getDataManager().createReference(audioCallStatusListener);

	callIDListenerMap.put(callID, audioCallStatusListenerRef);

        cp.setCallId(callID);
        cp.setName(info.getUserID().getUsername());
        cp.setPhoneNumber(msg.getSipURL());

        setJoinConfirmation(cp);

	cp.setCallEstablishedTreatment(JOIN_SOUND);
        cp.setConferenceId(vm.getVoiceManagerParameters().conferenceId);
        cp.setVoiceDetection(true);
        cp.setDtmfDetection(true);
        cp.setVoiceDetectionWhileMuted(true);
        cp.setHandleSessionProgress(true);

        sessionCallIDMapRef.get().put(clientID.getID(), callID);

        try {
            setupCall(callID, setup, msg.getX(), msg.getY(), msg.getZ(), msg.getDirection());
        } catch (IOException e) {
            logger.warning("Unable to place call " + cp + " " + e.getMessage());
            sessionCallIDMapRef.get().remove(clientID.getID());
        }
    }

    private void setJoinConfirmation(CallParticipant cp) {
        if (cp.getPhoneNumber().startsWith("sip:")) {
            return;
        }

        cp.setJoinConfirmationTimeout(90);

        String callAnsweredTreatment = System.getProperty(
                "com.sun.sgs.impl.app.voice.CALL_ANSWERED_TREATMENT");

        if (callAnsweredTreatment == null || callAnsweredTreatment.length() == 0) {
            callAnsweredTreatment = "dialtojoin.au";
        }

        cp.setCallAnsweredTreatment(callAnsweredTreatment);
        cp.setCallEstablishedTreatment("joinCLICK.au");
    }

    public static void setupCall(String callID, CallSetup setup, double x,
            double y, double z, double direction) throws IOException {

        VoiceManager vm = AppContext.getManager(VoiceManager.class);

        Player p = vm.getPlayer(callID);

        Call call;

        call = vm.createCall(callID, setup);

        callID = call.getId();

        PlayerSetup ps = new PlayerSetup();

        if (p == null) {
            ps.x = x;
            ps.y = y;
            ps.z = z;
        } else {
            ps.x = p.getSetup().x;
            ps.y = p.getSetup().y;
            ps.z = p.getSetup().z;
        }

        ps.orientation = direction;
        ps.isLivePlayer = true;

        Player player = vm.createPlayer(callID, ps);

        call.setPlayer(player);
        player.setCall(call);

        vm.getVoiceManagerParameters().livePlayerAudioGroup.addPlayer(player,
                new AudioGroupPlayerInfo(true, AudioGroupPlayerInfo.ChatType.PUBLIC));

        AudioGroupPlayerInfo info =
                new AudioGroupPlayerInfo(false, AudioGroupPlayerInfo.ChatType.PUBLIC);

        info.defaultSpeakingAttenuation = 0;

        vm.getVoiceManagerParameters().stationaryPlayerAudioGroup.addPlayer(player, info);
    }

    private void handleAudioVolume(WonderlandClientSender sender, WonderlandClientID clientID,
	    AudioVolumeMessage msg) {

	String softphoneCallID = msg.getSoftphoneCallID();

	String otherCallID = msg.getOtherCallID();

        double volume = msg.getVolume();

        logger.fine("GOT Volume message:  call " + softphoneCallID
	    + " volume " + volume + " other callID " + otherCallID);

        logger.fine("GOT Volume message:  call " + softphoneCallID
	    + " volume " + volume + " other callID " + otherCallID);

        VoiceManager vm = AppContext.getManager(VoiceManager.class);

        Player softphonePlayer = vm.getPlayer(softphoneCallID);

        if (softphonePlayer == null) {
            logger.warning("Can't find softphone player, callID " + softphoneCallID);
            return;
        }

        if (softphoneCallID.equals(otherCallID)) {
	    if (msg.isSetVolume() == false) {
		msg.setVolume(softphonePlayer.getMasterVolume());
		sender.send(clientID, msg);
		return;
	    }

            softphonePlayer.setMasterVolume(volume);
            return;
        }

        Player player = vm.getPlayer(otherCallID);

 	if (player == null) {
            logger.warning("Can't find player for callID " + otherCallID);
	    return;
        } 

	if (msg.isSetVolume() == false) {
	    Spatializer spatializer = softphonePlayer.getPrivateSpatializer(player);
	    msg.setVolume(spatializer.getAttenuator());
	    sender.send(clientID, msg);
	    logger.fine("Sending vol message " + msg.getVolume());
	    return;
	}

	if (volume == 1.0) {
	    softphonePlayer.removePrivateSpatializer(player);
	    return;
	}

	VoiceManagerParameters parameters = vm.getVoiceManagerParameters();

        Spatializer spatializer;

	spatializer = player.getPublicSpatializer();

	if (spatializer != null) {
	    spatializer = (Spatializer) spatializer.clone();
	} else {
	    if (player.getSetup().isLivePlayer) {
		spatializer = (Spatializer) parameters.livePlayerSpatializer.clone();
	    } else {
		spatializer = (Spatializer) parameters.stationarySpatializer.clone();
	    }
	}

        spatializer.setAttenuator(volume);

	logger.fine("Setting pm " + spatializer);
        softphonePlayer.setPrivateSpatializer(player, spatializer);
    }

    public void clientDisconnected(WonderlandClientSender sender, WonderlandClientID clientID) {
        BigInteger sessionID = clientID.getID();

        String callID = sessionCallIDMapRef.get().get(sessionID);

        if (callID == null) {
            logger.warning("Unable to find callID for client session " + sessionID);
            return;
        }

        sessionCallIDMapRef.get().remove(sessionID);

        VoiceManager vm = AppContext.getManager(VoiceManager.class);

        Call call = vm.getCall(callID);

        if (call == null) {
            logger.fine("Can't find call for " + callID);

            Player player = vm.getPlayer(callID);

            if (player != null) {
                vm.removePlayer(player);
            }
            return;
        }

        try {
            AppContext.getManager(VoiceManager.class).endCall(call, true);
        } catch (IOException e) {
            logger.warning("Unable to end call " + call + " " + e.getMessage());
        }
    }

}

