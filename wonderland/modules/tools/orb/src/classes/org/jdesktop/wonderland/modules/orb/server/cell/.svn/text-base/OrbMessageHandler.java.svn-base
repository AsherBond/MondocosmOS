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
package org.jdesktop.wonderland.modules.orb.server.cell;

import com.sun.mpk20.voicelib.app.Call;
import com.sun.mpk20.voicelib.app.DefaultSpatializer;
import com.sun.mpk20.voicelib.app.Player;
import com.sun.mpk20.voicelib.app.VoiceManager;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;

import com.sun.voip.client.connector.CallStatus;

import org.jdesktop.wonderland.common.cell.CellChannelConnectionType;

import org.jdesktop.wonderland.server.WonderlandContext;

import org.jdesktop.wonderland.server.comms.CommsManager;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

import java.io.IOException;
import java.io.Serializable;

import java.util.logging.Logger;

import java.util.concurrent.ConcurrentHashMap;

import org.jdesktop.wonderland.common.cell.messages.CellMessage;

import org.jdesktop.wonderland.common.messages.Message;

import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.state.CellClientState;
import org.jdesktop.wonderland.common.cell.state.CellServerState;

import org.jdesktop.wonderland.server.cell.AbstractComponentMessageReceiver;
import org.jdesktop.wonderland.server.cell.ChannelComponentMO;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.CellManagerMO;

import org.jdesktop.wonderland.server.UserManager;

import com.jme.math.Vector3f;

import com.sun.sgs.app.ManagedObject;
import org.jdesktop.wonderland.modules.orb.common.messages.OrbAttachMessage;
import org.jdesktop.wonderland.modules.orb.common.messages.OrbEndCallMessage;
import org.jdesktop.wonderland.modules.orb.common.messages.OrbMuteCallMessage;
import org.jdesktop.wonderland.modules.orb.common.messages.OrbChangeNameMessage;
import org.jdesktop.wonderland.modules.orb.common.messages.OrbChangePositionMessage;
import org.jdesktop.wonderland.modules.orb.common.messages.OrbSetVolumeMessage;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;

/**
 * A server cell that provides Orb functionality
 * @author jprovino
 */
public class OrbMessageHandler extends AbstractComponentMessageReceiver
	implements ManagedObject, Serializable {

    private static final Logger logger =
        Logger.getLogger(OrbCellMO.class.getName());
     
    private CellID cellID;

    private CellID hostCellID;

    private String username;

    private String callID;

    private boolean simulateCalls;

    private ManagedReference<OrbStatusListener> orbStatusListenerRef;

    public OrbMessageHandler(OrbCellMO orbCellMO, String username, 
	    String callID, boolean simulateCalls) {

	super(orbCellMO);

	cellID = orbCellMO.getCellID();

	this.username = username;
	this.callID = callID;
	this.simulateCalls = simulateCalls;

	logger.info("Username is " + username + " Call id is " + callID 
	    + " simulateCalls " + simulateCalls);

        OrbStatusListener orbStatusListener = new OrbStatusListener(orbCellMO, callID);

        orbStatusListenerRef =  AppContext.getDataManager().createReference(orbStatusListener);

        ChannelComponentMO channelComponentMO = getChannelComponent();

        channelComponentMO.addMessageReceiver(OrbAttachMessage.class, this);
        channelComponentMO.addMessageReceiver(OrbEndCallMessage.class, this);
        channelComponentMO.addMessageReceiver(OrbMuteCallMessage.class, this);
        channelComponentMO.addMessageReceiver(OrbChangeNameMessage.class, this);
        channelComponentMO.addMessageReceiver(OrbChangePositionMessage.class, this);
        channelComponentMO.addMessageReceiver(OrbSetVolumeMessage.class, this);
    }

    public void done() {
	ChannelComponentMO channelComponentMO = getChannelComponent();

	channelComponentMO.removeMessageReceiver(OrbAttachMessage.class);
	channelComponentMO.removeMessageReceiver(OrbEndCallMessage.class);
	channelComponentMO.removeMessageReceiver(OrbMuteCallMessage.class);
	channelComponentMO.removeMessageReceiver(OrbChangeNameMessage.class);
	channelComponentMO.removeMessageReceiver(OrbChangePositionMessage.class);
	channelComponentMO.removeMessageReceiver(OrbSetVolumeMessage.class);

	orbStatusListenerRef.get().endCall(callID);
    }

    public void messageReceived(WonderlandClientSender sender, 
	    WonderlandClientID clientID, CellMessage message) {

	logger.finest("got message " + message);

	VoiceManager vm = AppContext.getManager(VoiceManager.class);

	Call call = null;

	Player player = null;

	if (simulateCalls == false) {
	    call = vm.getCall(callID);

	    if (call == null) {
	        logger.warning("Can't find call for " + callID + " " + message);
	        return;
	    }

	    player = vm.getPlayer(callID);
	}

	if (message instanceof OrbEndCallMessage) {
	    if (call != null) {
	        try {
	            vm.endCall(call, true);
	        } catch (IOException e) {
		    logger.warning("Unable to end call " + call + ": " 
		        + e.getMessage());
	        }
 	    } else {
		orbStatusListenerRef.get().endCall(callID);
	    }

	    sender.send(message);
	    return;
	}

	if (message instanceof OrbMuteCallMessage) {
	    if (call != null) {
	        try {
	            call.mute(((OrbMuteCallMessage)message).isMuted());
	        } catch (IOException e) {
		    logger.warning("Unable to mute call " + call + ": " 
		        + e.getMessage());
		    return;
	        }
	    }

	    sender.send(message);
	    return;
	}

	if (message instanceof OrbChangeNameMessage) {
	    username = ((OrbChangeNameMessage) message).getName();
	    sender.send(message);
	    return;
	}

	if (message instanceof OrbChangePositionMessage) {
	    OrbChangePositionMessage msg = (OrbChangePositionMessage) message;

	    if (player == null) {
		return;
	    }

	    Vector3f position = msg.getPosition();

	    player.moved(position.getX(), position.getY(), position.getZ(),
		player.getOrientation());
	    return;
	}

	if (message instanceof OrbSetVolumeMessage) {
	    if (player == null) {
		logger.warning("no player for " + callID);
		return;
	    }

	    OrbSetVolumeMessage msg = (OrbSetVolumeMessage) message;

	    String softphoneCallID = msg.getSoftphoneCallID();

	    Player softphonePlayer = vm.getPlayer(softphoneCallID);

	    if (softphonePlayer == null) {
		logger.warning("Can't find Player for softphone " + softphoneCallID);
		return;
	    }

	    DefaultSpatializer spatializer = (DefaultSpatializer)
		vm.getVoiceManagerParameters().livePlayerSpatializer.clone();

	    double volume = msg.getVolume();

	    spatializer.setAttenuator(volume);

	    if (volume == 1) {
	        softphonePlayer.removePrivateSpatializer(player);
	    } else {
	        softphonePlayer.setPrivateSpatializer(player, spatializer);
	    }

	    logger.fine("player " + player + " sp " + spatializer + " v " + volume);
	    return;
 	}
	
	if (message instanceof OrbAttachMessage) {
	    OrbAttachMessage msg = (OrbAttachMessage) message;
	    
	    boolean isAttached = msg.isAttached();

	    if (isAttached && msg.getHostCellID() == null) {
		/*
		 * If hostCellID is null, the client is asking 
		 * us to tell it if there is a host for this orb.
		 */
	   	sender.send(clientID, new OrbAttachMessage(
		    msg.getCellID(), null, false));
		return;
	    }

	    logger.fine("Orb attached to " + msg.getHostCellID()
	    	+ " is " + msg.isAttached());

	    if (msg.isAttached()) {
		hostCellID = msg.getHostCellID();
		orbStatusListenerRef.get().setHostCellID(hostCellID);
	    } else {
		orbStatusListenerRef.get().removeCallStatusListener(msg.getHostCellID());		
		hostCellID = null;
	    }

	    sender.send(message);
	    return;
	}

	logger.warning("Unknown message:  " + message);
    }

    public void setUsername(String username) {
	this.username = username;
    }

}
