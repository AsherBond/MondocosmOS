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

import org.jdesktop.wonderland.modules.orb.server.cell.Orb;

import java.lang.reflect.Method;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.jdesktop.wonderland.common.cell.CellChannelConnectionType;
import org.jdesktop.wonderland.common.cell.CallID;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellTransform;

import org.jdesktop.wonderland.modules.audiomanager.common.AudioManagerConnectionType;

import org.jdesktop.wonderland.modules.audiomanager.common.messages.audio.EndCallMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.audio.CallSpeakingMessage;

import org.jdesktop.wonderland.modules.audiomanager.common.messages.PlayerInRangeMessage;

import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatBusyMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatDialOutMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatEndMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatInfoRequestMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatInfoResponseMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatHoldMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatJoinMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatJoinAcceptedMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatLeaveMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatMessage.ChatType;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatJoinRequestMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatTransientMemberMessage;

import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;

import org.jdesktop.wonderland.server.WonderlandContext;
import org.jdesktop.wonderland.server.UserManager;
import org.jdesktop.wonderland.server.UserMO;

import org.jdesktop.wonderland.server.cell.CellManagerMO;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.ProximityComponentMO;

import org.jdesktop.wonderland.server.cell.view.AvatarCellMO;

import org.jdesktop.wonderland.server.comms.CommsManager;
import org.jdesktop.wonderland.server.comms.CommsManagerFactory;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;

import java.util.logging.Logger;

import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

import org.jdesktop.wonderland.common.auth.WonderlandIdentity;

import com.sun.mpk20.voicelib.app.AudioGroup;
import com.sun.mpk20.voicelib.app.AudioGroupListener;
import com.sun.mpk20.voicelib.app.AudioGroupSetup;
import com.sun.mpk20.voicelib.app.AudioGroupPlayerInfo;
import com.sun.mpk20.voicelib.app.Call;
import com.sun.mpk20.voicelib.app.CallSetup;
import com.sun.mpk20.voicelib.app.DefaultSpatializer;
import com.sun.mpk20.voicelib.app.FullVolumeSpatializer;
import com.sun.mpk20.voicelib.app.Player;
import com.sun.mpk20.voicelib.app.PlayerInRangeListener;
import com.sun.mpk20.voicelib.app.PlayerSetup;
import com.sun.mpk20.voicelib.app.Spatializer;
import com.sun.mpk20.voicelib.app.VirtualPlayer;
import com.sun.mpk20.voicelib.app.VirtualPlayerListener;
import com.sun.mpk20.voicelib.app.VoiceManager;
import com.sun.mpk20.voicelib.app.VoiceManagerParameters;

import java.io.IOException;
import java.io.Serializable;

import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;

import com.jme.math.Vector3f;

/**
 * @author jprovino
 */
public class VoiceChatHandler implements AudioGroupListener, VirtualPlayerListener, 
	PlayerInRangeListener, Serializable {

    private static final Logger logger =
	Logger.getLogger(VoiceChatHandler.class.getName());
    
    private static final String ORB_MAP_NAME = "VoiceChatOrbMap";

    private static VoiceChatHandler voiceChatHandler;

    public static VoiceChatHandler getInstance() {
	if (voiceChatHandler == null) {
	    voiceChatHandler = new VoiceChatHandler();
	}

	return voiceChatHandler;
    }

    class ManagedOrbMap extends ConcurrentHashMap<String, ManagedReference<Orb>> implements ManagedObject {

	private static final long serialVersionUID = 1;

    }

    private VoiceChatHandler() {
	AppContext.getDataManager().setBinding(ORB_MAP_NAME, new ManagedOrbMap());
    }

    /*
     * members of the group should get speaking indications no matter where they are.
     */
    public void setSpeaking(Player player, String callID, boolean isSpeaking, AudioGroup secretAudioGroup) {
	WonderlandClientSender sender = 
	    WonderlandContext.getCommsManager().getSender(AudioManagerConnectionType.CONNECTION_TYPE);

	if (secretAudioGroup != null) {
	    setSpeaking(sender, secretAudioGroup.getId(), callID, isSpeaking);    
	    return;
	}

	AudioGroup[] audioGroups = player.getAudioGroups();

	for (int i = 0; i < audioGroups.length; i++) {
	     setSpeaking(sender, audioGroups[i].getId(), callID, isSpeaking);    
	}
    }

    private void setSpeaking(WonderlandClientSender sender, String audioGroupID, String callID,
	    boolean isSpeaking) {

	PresenceInfo[] chatters = getChatters(audioGroupID);

	if (chatters == null) {
	    return;
	}

	for (int i = 0; i < chatters.length; i++) {
	    if (chatters[i].getClientID() == null) {
		/*
		 * It's an outworlder.
		 */
		continue;
	    }

	    WonderlandClientID id =
               CommsManagerFactory.getCommsManager().getWonderlandClientID(chatters[i].getClientID());

	    if (id == null) {
		logger.warning("No ClientID for " + chatters[i]);
		continue;
	    }

	    sender.send(id, new CallSpeakingMessage(callID, isSpeaking));
	}
    }

    public void processVoiceChatMessage(WonderlandClientSender sender, 
	    WonderlandClientID clientID, VoiceChatMessage message) {

	String group = message.getGroup();

	if (message instanceof VoiceChatInfoRequestMessage) {
	    sendVoiceChatInfo(sender, clientID, group);
	    return;
	}

	if (message instanceof VoiceChatBusyMessage) {
	    VoiceChatBusyMessage msg = (VoiceChatBusyMessage) message;

	    CommsManager cm = CommsManagerFactory.getCommsManager();
	    
            WonderlandClientID id = cm.getWonderlandClientID(msg.getCaller().getClientID());

            if (id == null) {
                logger.warning("No WonderlandClientID for caller "
                    + msg.getCaller());
                return;
            }

	    sendVoiceChatBusyMessage(sender, id, msg);
	    return;
	}

        VoiceManager vm = AppContext.getManager(VoiceManager.class);

	if (group == null) {
	    logger.warning("Invalid audio group 'null'");
	    return;
	}

	AudioGroup audioGroup = vm.getAudioGroup(group);

	if (message instanceof VoiceChatLeaveMessage) {
	    if (audioGroup == null) {
		logger.info("audioGroup is null");
		return;
	    }

	    VoiceChatLeaveMessage msg = (VoiceChatLeaveMessage) message;

	    Player player = vm.getPlayer(msg.getCallee().getCallID());

	    if (player == null) {
		logger.warning("No player for " + msg.getCallee());

	        if (audioGroup.getNumberOfPlayers() == 0) {
		    endVoiceChat(vm, audioGroup);  // cleanup
	        }
		return;
	    }
	    
	    setCOSSpeaking(player, msg.getCOSName(), true);

	    AudioGroupPlayerInfo info = audioGroup.getPlayerInfo(player);

	    if (info == null) {
		return;   // not in group
	    }

	    /*
	     * XXX If the player to be removed from the group is
	     * in range of another player which is in a public chat,
	     * then rather than removing the player, re-add it
	     * as a transient member.
	     */
	    if (isInRangeOfPublicChat(audioGroup, player)) {
    		addPlayerToAudioGroup(audioGroup, player, msg.getCallee(), 
		    ChatType.PUBLIC, true);

		return;
	    }

	    removePlayerFromAudioGroup(audioGroup, player);

	    if (audioGroup.getNumberOfPlayers() <= 1) {
		endVoiceChat(vm, audioGroup);
	    } 

	    CallSetup callSetup = player.getCall().getSetup();

	    if (callSetup.incomingCall || callSetup.externalOutgoingCall) {
	        addPlayerToAudioGroup(
		    vm.getVoiceManagerParameters().livePlayerAudioGroup, 
		    player, msg.getCallee(), ChatType.PUBLIC);

	        addPlayerToAudioGroup(
		    vm.getVoiceManagerParameters().stationaryPlayerAudioGroup,
		    player, msg.getCallee(), ChatType.PUBLIC);
	    }
	    
	    return;
	}

	if (message instanceof VoiceChatEndMessage) {
	    if (audioGroup == null) {
		logger.info("audioGroup is null");
		return;
	    }

	    endVoiceChat(vm, audioGroup);
	    return;
	}

	if (message instanceof VoiceChatJoinAcceptedMessage) {
	    if (audioGroup == null) {
		logger.warning("Join accepted:  Audio group " + group + " no longer exists");
		return;
	    }

	    VoiceChatJoinAcceptedMessage msg = (VoiceChatJoinAcceptedMessage) message;

	    Player player = vm.getPlayer(msg.getCallee().getCallID());

	    if (player == null) {
		logger.warning("No player for " + msg.getCallee().getCallID());
		return;
	    }

	    addPlayerToAudioGroup(audioGroup, player, msg.getCallee(), msg.getChatType());

	    sender.send(msg);
	    return;
	}

	if (message instanceof VoiceChatHoldMessage) {
	    VoiceChatHoldMessage msg = (VoiceChatHoldMessage) message;

	    if (audioGroup == null) {
		logger.warning("Hold:  Audio group " + group + " no longer exists");
		return;
	    }
	
	    Player player = vm.getPlayer(msg.getCallee().getCallID());

	    if (player == null) {
		logger.warning("No player for " + msg.getCallee().getCallID());
		return;
	    }

	    setCOSSpeaking(player, msg.getCOSName(), msg.isOnHold());

	    AudioGroupPlayerInfo playerInfo = audioGroup.getPlayerInfo(player);

	    if (playerInfo == null) {
		logger.warning("No player info for " + player);
		return;
	    }
	
	    if (msg.isOnHold()) {
		playerInfo.isSpeaking = false;
		playerInfo.listenAttenuation = msg.getVolume();
	    } else {
		playerInfo.isSpeaking = true;
		playerInfo.speakingAttenuation = AudioGroup.DEFAULT_SPEAKING_ATTENUATION;
		playerInfo.listenAttenuation = AudioGroup.DEFAULT_LISTEN_ATTENUATION;
	    }

	    updateAttenuation(player);
	    sender.send(clientID, msg);
	    return;
	}

	if (message instanceof VoiceChatDialOutMessage) {
	    VoiceChatPhoneMessageHandler.getInstance().dialOut(
		sender, clientID, (VoiceChatDialOutMessage) message);
	    return;
	}

	if (message instanceof VoiceChatJoinMessage == false) {
	    logger.warning("Invalid message type " + message);
	    return;
	}

	VoiceChatJoinMessage msg = (VoiceChatJoinMessage) message;

	if (audioGroup == null) {
	    AudioGroupSetup setup = new AudioGroupSetup();
	    setup.spatializer = new FullVolumeSpatializer();
	    setup.spatializer.setAttenuator(Spatializer.DEFAULT_MAXIMUM_VOLUME);
	    setup.virtualPlayerListener = this;
	    setup.audioGroupListener = this;
	    audioGroup = vm.createAudioGroup(group, setup);
	}

	PresenceInfo[] calleeList = msg.getCalleeList();

	PresenceInfo caller = msg.getCaller();

	if (msg.getChatType() != null) {
	    Player player = vm.getPlayer(caller.getCallID());

	    if (player == null) {
		logger.warning("No Player for " + caller.getCallID());
		return;
	    }

	    boolean added = addPlayerToAudioGroup(audioGroup, player, caller, msg.getChatType());

	    if (added) {
	        sender.send(new VoiceChatJoinAcceptedMessage(group, caller, msg.getChatType()));
	    }

	    if (added == false && (calleeList == null || calleeList.length == 0)) {
	        endVoiceChat(vm, audioGroup);
	        return;
	    }
	}

	logger.info("Request to join AudioGroup " + group + " caller " + caller);

	if (calleeList == null || calleeList.length == 0) {
	    return;
	}

	for (int i = 0; i < calleeList.length; i++) {
	    PresenceInfo info = calleeList[i];

	    String callID = info.getCallID();

	    Player player = vm.getPlayer(callID);

	    if (player == null) {
		logger.warning("No player for callID " + callID);
		continue;
	    }

	    if (info.getClientID() == null) {
		/*
		 * This is an outworlder.  We automatically join them to the group
	 	 * The InCallDialog can be used to change the privacy setting
		 * and to remove the outworlder from the chat.
		 */
	        addPlayerToAudioGroup(audioGroup, player, info, msg.getChatType());
	        sender.send(new VoiceChatJoinAcceptedMessage(group, info, msg.getChatType()));
	    	continue;
	    }

	    AudioGroupPlayerInfo playerInfo = audioGroup.getPlayerInfo(player);

	    if (playerInfo != null && sameChatType(playerInfo.chatType, msg.getChatType())) {
		logger.fine("Player " + info
		    + " is already in audio group " + audioGroup);

		if (playerInfo.isTransientMember == false) {
		    continue;
		}
	    }

            WonderlandClientID id = 
	       CommsManagerFactory.getCommsManager().getWonderlandClientID(info.getClientID());

	    if (id == null) {
		logger.warning("No WonderlandClientID for " + info);
		continue;
	    }

	    Call call = player.getCall();

	    if (call != null) {
		try {
		    call.playTreatment("audioGroupInvite.au");
		} catch (IOException e) {
		    logger.warning("Unable to play audioGroupInvite.au:  "
			+ e.getMessage());
		}
	    }

	    logger.warning("Asking " + info + " to join audio group " 
		+ group + " chatType " + msg.getChatType());

	    requestPlayerJoinAudioGroup(sender, id, group, caller,
		calleeList, msg.getChatType());
	}
    }

    private void setCOSSpeaking(Player player, String COSName, boolean isSpeaking) {
	logger.fine("SET COSSPEAKING " + player.getId() + " COSName " + COSName
	    + " isSpeaking " + isSpeaking);

	if (COSName == null) {
	    return;
	}

	AudioGroup COSAudioGroup = AppContext.getManager(VoiceManager.class).getAudioGroup(COSName);

	if (COSAudioGroup == null) {
	    logger.warning("No COS audio group for " + COSName);
	    return;
	}

	COSAudioGroup.setSpeaking(player, isSpeaking);
    }

    private boolean isInRangeOfPublicChat(AudioGroup audioGroup, Player player) {
	Player[] players = audioGroup.getPlayers();

	for (int i = 0; i < players.length; i++) {
	    AudioGroupPlayerInfo info = audioGroup.getPlayerInfo(players[i]);

	    if (info == null || info.chatType.equals(AudioGroupPlayerInfo.ChatType.PUBLIC) == false) {
		continue;
	    }

	    if (isInRange(players[i].getPlayersInRange(), player)) {
		return true;
	    }
	}

	return false;
    }

    private boolean isInRange(Player[] playersInRange, Player player) {
	for (int i = 0; i < playersInRange.length; i++) {
	    if (playersInRange[i].equals(player)) {
		return true;
	    }
	}

	return false;
    }

    public static void updateAttenuation(Player player) {
	VoiceManager vm = AppContext.getManager(VoiceManager.class);

	AudioGroup livePlayerAudioGroup = vm.getVoiceManagerParameters().livePlayerAudioGroup;

	AudioGroup stationaryPlayerAudioGroup = vm.getVoiceManagerParameters().stationaryPlayerAudioGroup;

	AudioGroup[] audioGroups = player.getAudioGroups();

	AudioGroup nonPublicAudioGroup = null;

	AudioGroupPlayerInfo playerInfo;

	for (int i = 0; i < audioGroups.length; i++) {
	    AudioGroup audioGroup = audioGroups[i];

	    playerInfo = audioGroup.getPlayerInfo(player);

	    if (playerInfo != null && playerInfo.isSpeaking && 
		    playerInfo.chatType.equals(AudioGroupPlayerInfo.ChatType.PUBLIC) == false) {

		nonPublicAudioGroup = audioGroup;
		break;
	    }
	}

	playerInfo = livePlayerAudioGroup.getPlayerInfo(player);

	if (playerInfo != null) {
	    if (nonPublicAudioGroup != null && playerInfo.isTransientMember == false) {
	        livePlayerAudioGroup.setSpeaking(player, false);
	        livePlayerAudioGroup.setListenAttenuation(player, AudioGroup.MINIMAL_LISTEN_ATTENUATION);
	    } else {
                livePlayerAudioGroup.setSpeaking(player, true);
                livePlayerAudioGroup.setSpeakingAttenuation(player, AudioGroup.DEFAULT_SPEAKING_ATTENUATION);
                livePlayerAudioGroup.setListenAttenuation(player, AudioGroup.DEFAULT_LISTEN_ATTENUATION);
	    }
	}

	if (stationaryPlayerAudioGroup.getPlayerInfo(player) != null) {
	    if (nonPublicAudioGroup != null) {
	        stationaryPlayerAudioGroup.setListenAttenuation(player, AudioGroup.MINIMAL_LISTEN_ATTENUATION);
	    } else {
                stationaryPlayerAudioGroup.setListenAttenuation(player, AudioGroup.DEFAULT_LISTEN_ATTENUATION);
	    }
	}

	/*
	 * If we're in a nonPublicAudioGroup, we also have to attenuate the other groups we're listening to.
	 */
	double attenuation;

	if (nonPublicAudioGroup != null) {
	    attenuation = AudioGroup.MINIMAL_LISTEN_ATTENUATION;
	} else {
	    attenuation = AudioGroup.DEFAULT_LISTEN_ATTENUATION;
	}

	for (int i = 0; i < audioGroups.length; i++) {
	    AudioGroup audioGroup = audioGroups[i];

	    if (audioGroup.equals(livePlayerAudioGroup) || audioGroup.equals(stationaryPlayerAudioGroup)) {
		continue;
	    }

	    playerInfo = audioGroup.getPlayerInfo(player);

	    if (playerInfo.chatType.equals(ChatType.PUBLIC) && playerInfo.listenAttenuation != 0) {
		audioGroup.setListenAttenuation(player, attenuation);
	    }
	}

	player.setPrivateMixes(true);
    }

    private ConcurrentHashMap<String, PresenceInfo> playerMap = new ConcurrentHashMap();

    public boolean addPlayerToAudioGroup(AudioGroup audioGroup, Player player, 
	    PresenceInfo presenceInfo, ChatType chatType) {

	return addPlayerToAudioGroup(audioGroup, player, presenceInfo, chatType, false);
    }

    public boolean addPlayerToAudioGroup(AudioGroup audioGroup, Player player, 
	    PresenceInfo presenceInfo, ChatType chatType, boolean isTransientMember) {

	AudioGroupPlayerInfo playerInfo = audioGroup.getPlayerInfo(player);

	if (playerInfo != null && sameChatType(playerInfo.chatType, chatType)) {
	    logger.fine("Player " + playerInfo
		+ " is already in audio group " + audioGroup.getId());

	    if (playerInfo.isTransientMember == false) {
	        return true;
	    }
	}

	logger.fine("Adding player " + player.getId() + " type " + chatType);
	logger.warning("Adding player " + player.getId() + " type " + chatType);

	playerInfo = new AudioGroupPlayerInfo(true, getChatType(chatType));
	playerInfo.speakingAttenuation = AudioGroup.DEFAULT_SPEAKING_ATTENUATION;
	playerInfo.listenAttenuation = AudioGroup.DEFAULT_LISTEN_ATTENUATION;
	playerInfo.isTransientMember = isTransientMember;

	audioGroup.addPlayer(player, playerInfo);

        player.addPlayerInRangeListener(this);

	if (presenceInfo != null) {
	    playerMap.put(player.getId(), presenceInfo);
	}
	return true;
    }

    private void requestPlayerJoinAudioGroup(WonderlandClientSender sender,
	    WonderlandClientID clientID, String group, PresenceInfo caller, 
	    PresenceInfo[] calleeList, ChatType chatType) {

	VoiceChatMessage message = new VoiceChatJoinRequestMessage(group, 
	    caller, getChatters(group), chatType);

	logger.fine("Sending VoiceChatJoinRequestMessage to clientID " + clientID);
        sender.send(clientID, message);
    }

    public void playerAdded(AudioGroup audioGroup, Player player, AudioGroupPlayerInfo info) {
	logger.warning("Player added " + player + " group " + audioGroup + " info " + info);

	WonderlandClientSender sender = 
	    WonderlandContext.getCommsManager().getSender(AudioManagerConnectionType.CONNECTION_TYPE);

	if (sender == null) {
	    logger.warning("Unable to send voice chat info to client.  Sender is null.");
	    return;
	}

	updateAttenuation(player);

	if (info.isTransientMember) {
	    /*
	     * We don't necessarily have the presence info for the player so we have
	     * to send the call ID.
	     */
	    sender.send(new VoiceChatTransientMemberMessage(audioGroup.getId(),
	        player.getId(), true));
	} else {
	    handleBystanders(audioGroup, player, info.chatType);
	    
	    sendVoiceChatInfo(sender, audioGroup.getId());
	}
    }

    private ConcurrentHashMap<CellID, VoiceChatProximityListener> proximityListeners =
	new ConcurrentHashMap();

    private void addProximityListener(Player player, PresenceInfo info) {
	VoiceChatProximityListener proximityListener = proximityListeners.get(info.getCellID());

	if (proximityListener != null) {
	    return;
	}

	CellMO cellMO = CellManagerMO.getCellManager().getCell(info.getCellID());

        if (cellMO.getComponent(ProximityComponentMO.class) == null) {
            cellMO.addComponent(new ProximityComponentMO(cellMO));
        }

	ProximityComponentMO component = cellMO.getComponent(ProximityComponentMO.class);

        BoundingVolume[] bounds = new BoundingVolume[1];

	AudioGroup ag = AppContext.getManager(VoiceManager.class).getVoiceManagerParameters().livePlayerAudioGroup;

	DefaultSpatializer spatializer = (DefaultSpatializer) ag.getSetup().spatializer;

        bounds[0] = new BoundingSphere((float) spatializer.getZeroVolumeRadius(), new Vector3f());

	logger.warning("Bounds for " + info.getCellID() + " " + bounds[0]);

        //proximityListener = new VoiceChatProximityListener(info);

	//proximityListeners.put(info.cellID, proximityListener);

        //component.addProximityListener(proximityListener, bounds);
    }

    /*
     * If this player is going PRIVATE, remove transient members 
     * which are no longer in range of any PUBLIC player in the group.
     *
     * Similarly, if this player is going public, we need to deal with
     * players in range.
     */
    private void handleBystanders(AudioGroup group, Player player, AudioGroupPlayerInfo.ChatType chatType) {
	Player[] playersInRange = player.getPlayersInRange();

	AudioGroupPlayerInfo info = group.getPlayerInfo(player);

	PresenceInfo presenceInfo = playerMap.get(player.getId());

	if (chatType.equals(AudioGroupPlayerInfo.ChatType.PUBLIC)) {
	    for (int i = 0; i < playersInRange.length; i++) {
		addBystander(group, player, playersInRange[i]);
	    }

	    if (info != null && info.isTransientMember == false) {
	        if (presenceInfo != null) {
		    //addProximityListener(player, presenceInfo);
	        } else {
		    logger.warning("Unable to set proximity listener for " + player.getId());
	        }
	    } else {
		logger.warning("No AudioGroupPlayerInfo for " + player + " group " + group);
	    }
	} else {
	    for (int i = 0; i < playersInRange.length; i++) {
		removeBystander(group, player, playersInRange[i]);
	    }

	    if (info != null && info.isTransientMember == false) {
	        if (presenceInfo != null) {
	            //removeProximityListener(presenceInfo);
	        } else {
		    logger.warning("Unable to set proximity listener for " + player.getId());
	        }
	    } else {
		logger.warning("No AudioGroupPlayerInfo for " + player + " group " + group);
	    }
	}
    }

    private void addBystander(AudioGroup group, Player player, Player playerInRange) {
	AudioGroupPlayerInfo info = group.getPlayerInfo(playerInRange);

	if (info != null) {
	    logger.fine("In range player is already in group " + group.getId() + ": " + playerInRange.getId());
	    return;
	}

	addTransientMember(group, player, playerInRange);
	AudioGroup ag = AppContext.getManager(VoiceManager.class).getVoiceManagerParameters().livePlayerAudioGroup;

	DefaultSpatializer spatializer = (DefaultSpatializer) ag.getSetup().spatializer;
		
	playerInRange.setPrivateSpatializer(player, new FullVolumeSpatializer(spatializer.getZeroVolumeRadius()));
    }

    private void removeBystander(AudioGroup group, Player player, Player playerInRange) {
	AudioGroupPlayerInfo info = group.getPlayerInfo(playerInRange);

	if (info == null) {
	    logger.warning("In range player is not in group " + group.getId() + ": " + playerInRange.getId());
	    return;
	}

	if (info.isTransientMember == false) {
	    return;
	}

	removeTransientMember(group, player, playerInRange);
	playerInRange.removePrivateSpatializer(player);
    }
    
    private void addTransientMember(AudioGroup group, Player player, Player playerInRange) {
	AudioGroupPlayerInfo info = group.getPlayerInfo(player);

	logger.warning("Add transient member:  " + playerInRange.getId()
	    + " because it's in range of " + player.getId() + " info " + info);

	if (info.chatType.equals(AudioGroupPlayerInfo.ChatType.PUBLIC) == false) {
	    logger.fine("Add transient:  Not Public");
	    return;
	}

	AudioGroupPlayerInfo inRangePlayerInfo = group.getPlayerInfo(playerInRange);

	if (inRangePlayerInfo != null && inRangePlayerInfo.isTransientMember == false) {
	    logger.fine("Add transient member:  " + player.getId() + " is already a member");
	    return;
	}

	addPlayerToAudioGroup(group, playerInRange, null, ChatType.PUBLIC, true);
    }

    private void removeTransientMember(AudioGroup group, Player player, Player playerInRange) {
	AudioGroupPlayerInfo info = group.getPlayerInfo(playerInRange);

	logger.fine("Remove transient member " + playerInRange.getId());

	if (info == null || info.isTransientMember == false) {
	    logger.fine("not removing non-transient member " + playerInRange.getId()
		+ " info " + info);
            return;
        }

	/*
	 * Only remove the bystander if it is not in range of any member speaking publicly.
	 */
	Player[] players = playerInRange.getPlayersInRange();

	for (int i = 0; i < players.length; i++) {
	    if (players[i].equals(player)) {
		logger.fine("Skipping " + player.getId());
		continue;
	    }

	    logger.fine(playerInRange.getId() + " has in range " + players[i].getId());

	    info = group.getPlayerInfo(players[i]);    

	    if (info == null || info.isTransientMember) {
		continue;
	    }

	    if (info.chatType.equals(AudioGroupPlayerInfo.ChatType.PUBLIC)) {
	        logger.fine("player " + players[i].getId() 
		    + " is in range of transient player " + playerInRange.getId());
		return;
	    }
	}

	removePlayerFromAudioGroup(group, playerInRange);
	logger.warning("Removed transient member " + playerInRange.getId());
    }

    private void sendVoiceChatBusyMessage(WonderlandClientSender sender,
	    WonderlandClientID clientID, VoiceChatBusyMessage message) {

	logger.fine(message.getCallee() + " sending busy message to " 
	    + message.getCaller());

        sender.send(clientID, message);
    }

    public void playerRemoved(AudioGroup audioGroup, Player player, AudioGroupPlayerInfo info) {
	logger.fine("Player removed " + player + " group " + audioGroup.getId() + " info " + info);

	WonderlandClientSender sender = 
	    WonderlandContext.getCommsManager().getSender(AudioManagerConnectionType.CONNECTION_TYPE);

	updateAttenuation(player);

        if (info != null && info.isTransientMember) {
            /*
             * We don't necessarily have the presence info for the player so we have
             * to send the call ID.
             */
            sender.send(new VoiceChatTransientMemberMessage(audioGroup.getId(),
                player.getId(), false));
	}

	PresenceInfo presenceInfo = playerMap.remove(player.getId());

	if (presenceInfo == null) {
	    logger.warning("No presence Info for " + player.getId());
	    return;
	}

	handleBystanders(audioGroup, player, AudioGroupPlayerInfo.ChatType.PRIVATE);

	sender.send(new VoiceChatLeaveMessage(audioGroup.getId(), presenceInfo, null));
    }

    private void removeProximityListener(PresenceInfo info) {
	VoiceChatProximityListener proximityListener = proximityListeners.get(info.getCellID());

	if (proximityListener == null) {
	    logger.warning("No proximity listener for " + info.getCellID());
	    return;
	}

	CellMO cellMO = CellManagerMO.getCellManager().getCell(info.getCellID());

	ProximityComponentMO component = cellMO.getComponent(ProximityComponentMO.class);
	component.removeProximityListener(proximityListener);
    }

    private void sendVoiceChatInfo(WonderlandClientSender sender, String group) {
	PresenceInfo[] chatters = getChatters(group);

	if (chatters == null || chatters.length == 0) {
	    logger.fine("No chatters in " + group);
	    return;
	}

	VoiceManager vm = AppContext.getManager(VoiceManager.class);

	AudioGroup livePlayerAudioGroup = vm.getVoiceManagerParameters().livePlayerAudioGroup;

	AudioGroup stationaryPlayerAudioGroup = vm.getVoiceManagerParameters().stationaryPlayerAudioGroup;

	if (group.equals(livePlayerAudioGroup.getId()) || group.equals(stationaryPlayerAudioGroup.getId())) {
	    return;
	}

	CommsManager cm = CommsManagerFactory.getCommsManager();
	    
        VoiceChatInfoResponseMessage message = new VoiceChatInfoResponseMessage(group, chatters);

	for (int i = 0; i < chatters.length; i++) {
	    if (chatters[i].getClientID() == null) {
		/*
		 * It's an outworlder.
		 */
		continue;
	    }

            WonderlandClientID clientID = cm.getWonderlandClientID(chatters[i].getClientID());

	    if (clientID == null) {
		logger.warning("Can't find WonderlandClientID for " + chatters[i]);
		continue;
	    }

            sender.send(clientID, message);
	}
    }

    private void sendVoiceChatInfo(WonderlandClientSender sender,
	    WonderlandClientID clientID, String group) {

 	PresenceInfo[] chatters = getChatters(group);

	if (chatters == null || chatters.length == 0) {
	    return;
	}

        sender.send(clientID, new VoiceChatInfoResponseMessage(group, chatters));
    }

    private PresenceInfo[] getChatters(String group) {
	return getChatters(group, null);
    }

    private PresenceInfo[] getChatters(String group, ChatType chatType) {
	VoiceManager vm = AppContext.getManager(VoiceManager.class);

	AudioGroup audioGroup = vm.getAudioGroup(group);

	if (audioGroup == null) {
	    logger.fine("Can't find audio group " + group);
	    return null;
	}

	AudioGroup livePlayerAudioGroup = vm.getVoiceManagerParameters().livePlayerAudioGroup;

        AudioGroup stationaryPlayerAudioGroup = vm.getVoiceManagerParameters().stationaryPlayerAudioGroup;

	if (audioGroup.equals(livePlayerAudioGroup) || audioGroup.equals(stationaryPlayerAudioGroup)) {
	    return null;
	}

	ArrayList<PresenceInfo> chatters = new ArrayList();

	Player[] players = audioGroup.getPlayers();

	for (int i = 0; i < players.length; i++) {
	    Player player = players[i];

	    PresenceInfo info = playerMap.get(player.getId());
	
	    if (info == null) {
		logger.fine("Unable to find presence info for " 
		    + player.getId() + " group " + group);
		continue;
	    }
		
	    if (chatType == null || audioGroup.getPlayerInfo(player).chatType == 
		    getChatType(chatType)) {

	        chatters.add(info);
	    }
	}

	return chatters.toArray(new PresenceInfo[0]);
    }

    private void removePlayerFromAudioGroup(AudioGroup audioGroup, 
	    Player player) {

        player.removePlayerInRangeListener(this);

	AudioGroupPlayerInfo playerInfo = audioGroup.getPlayerInfo(player);

	audioGroup.removePlayer(player);

	updateAttenuation(player);

	// XXX If a player can be in more than one public audio group
	// then the player must have a separate list of virtual calls
	// for each audio group.
    }

    private void endVoiceChat(VoiceManager vm, AudioGroup audioGroup) {
	Player[] players = audioGroup.getPlayers();

	for (int i = 0; i < players.length; i++) {
	    Player player = players[i];
	    
	    removePlayerFromAudioGroup(audioGroup, player);
	}

	vm.removeAudioGroup(audioGroup);
    }

    public void virtualPlayerAdded(VirtualPlayer vp) {
	if (vp.realPlayer.getCall().getSetup().ended) {
	    logger.warning("Call ended unexpectedly! " + vp);
	    return;
	}

        ManagedOrbMap orbMap = (ManagedOrbMap) AppContext.getDataManager().getBinding(ORB_MAP_NAME);

	ManagedReference<Orb> orbRef = orbMap.get(vp.getId());

	if (orbRef != null) {
	    orbRef.get().addToUseCount(1);
	    return;
	}

	Vector3f center = new Vector3f((float) -vp.playerWithVirtualPlayer.getX(), (float) 2.3, 
	    (float) vp.playerWithVirtualPlayer.getZ());

	Orb orb;

	CallSetup callSetup = vp.playerWithVirtualPlayer.getCall().getSetup();

	if (callSetup.incomingCall || callSetup.externalOutgoingCall) {
	    /*
	     * Don't create virtual orb's for outworlders
	     */
	    return;
	}

	orb = new Orb(vp, center, .1, vp.realPlayer.getId(), new String[0]);

	orb.addComponent(new AudioParticipantComponentMO(orb.getOrbCellMO()));

	orbMap.put(vp.getId(), AppContext.getDataManager().createReference(orb));

	logger.warning("virtualPlayerAdded:  " + vp + " Center " + center); 
    }

    public void virtualPlayersRemoved(VirtualPlayer[] virtualPlayers) {
	for (int i = 0; i < virtualPlayers.length; i++) {
	    VirtualPlayer vp = virtualPlayers[i];

            ManagedOrbMap orbMap = (ManagedOrbMap) AppContext.getDataManager().getBinding(ORB_MAP_NAME);

	    ManagedReference<Orb> orbRef = orbMap.get(vp.getId());

	    logger.info("removing " + vp);

	    if (orbRef == null) {
		logger.warning("No orb for " + vp);
		return;
	    }

	    Orb orb = orbRef.get();

	    if (orb.addToUseCount(-1) == 0) {
		logger.fine("Removing " + vp.getId() + " from orbs");
		orbMap.remove(vp.getId());

		vp.realPlayer.setPrivateMixes(true);
	        orb.done();
	    }
	}
    }

    private void moveVirtualPlayers(Player player, double x, double y, double z, 
	    double direction) {

    }

    public void playerInRange(Player player, Player playerInRange, boolean isInRange) {
	if (isInRange) {
	    logger.warning("Player " + playerInRange.getId() + " is in range of "
	        + player.getId());
	} else {
	    logger.warning("Player " + playerInRange.getId() + " is out of range of "
	        + player.getId());
	}

	if (playerInRange.getSetup().isVirtualPlayer) {
	    return;
	}

	AudioGroup[] groups = player.getAudioGroups();

	for (int i = 0; i < groups.length; i++) {
	    AudioGroup group = groups[i];

	    AudioGroupPlayerInfo playerInfo = group.getPlayerInfo(player);

	    if (playerInfo == null || playerInfo.chatType.equals(AudioGroupPlayerInfo.ChatType.PUBLIC) == false) {
		logger.fine("player not chatting publicly in " + group.getId() + " " 
		    + player.getId() + " info " + playerInfo);
		continue;
	    }

	    if (isInRange) {
		addBystander(group, player, playerInRange);
	    } else {
		removeBystander(group, player, playerInRange);
	    }
	}

	WonderlandClientSender sender = 
	    WonderlandContext.getCommsManager().getSender(AudioManagerConnectionType.CONNECTION_TYPE);

	sender.send(new PlayerInRangeMessage(player.getId(), playerInRange.getId(), isInRange));
    }

    /*
     * XXX sameChatType() getChatType() are here because the voicelib is not accessible to
     * common and client code so VoiceChatMessages have their own enum for ChatType.
     */
    public static boolean sameChatType(AudioGroupPlayerInfo.ChatType playerChatType, ChatType chatType) {
	if (playerChatType == AudioGroupPlayerInfo.ChatType.PUBLIC && chatType == ChatType.PUBLIC) {
	    return true;
	}

	if (playerChatType == AudioGroupPlayerInfo.ChatType.PRIVATE && chatType == ChatType.PRIVATE) {
	    return true;
	}

	if (playerChatType == AudioGroupPlayerInfo.ChatType.SECRET && chatType == ChatType.SECRET) {
	    return true;
	}

	if (playerChatType == AudioGroupPlayerInfo.ChatType.EXCLUSIVE && chatType == ChatType.EXCLUSIVE) {
	    return true;
	}

	return false;
    }

    public static AudioGroupPlayerInfo.ChatType getChatType(ChatType chatType) {
	if (chatType == ChatType.PRIVATE) {
	    return AudioGroupPlayerInfo.ChatType.PRIVATE;
	}

	if (chatType == ChatType.SECRET) {
	    return AudioGroupPlayerInfo.ChatType.SECRET;
	}

	if (chatType == ChatType.EXCLUSIVE) {
	    return AudioGroupPlayerInfo.ChatType.EXCLUSIVE;
	}

	if (chatType == ChatType.PUBLIC) {
	    return AudioGroupPlayerInfo.ChatType.PUBLIC;
	}

	return AudioGroupPlayerInfo.ChatType.PRIVATE;
    }

}
