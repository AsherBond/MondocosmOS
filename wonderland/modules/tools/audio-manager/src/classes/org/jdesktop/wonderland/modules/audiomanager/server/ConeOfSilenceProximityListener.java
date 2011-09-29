/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

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

import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;

import org.jdesktop.wonderland.modules.audiomanager.common.messages.ConeOfSilenceEnterExitMessage;

import com.sun.mpk20.voicelib.app.AudioGroup;
import com.sun.mpk20.voicelib.app.AudioGroupListener;
import com.sun.mpk20.voicelib.app.AudioGroupPlayerInfo;
import com.sun.mpk20.voicelib.app.AudioGroupSetup;
import com.sun.mpk20.voicelib.app.FullVolumeSpatializer;
import com.sun.mpk20.voicelib.app.Player;
import com.sun.mpk20.voicelib.app.Spatializer;
import com.sun.mpk20.voicelib.app.VoiceManager;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedObject;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.cell.CallID;
import org.jdesktop.wonderland.common.cell.CellChannelConnectionType;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.server.WonderlandContext;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.CellManagerMO;
import org.jdesktop.wonderland.server.cell.ProximityListenerSrv;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;
import com.jme.bounding.BoundingVolume;
import com.sun.sgs.app.ManagedReference;

import org.jdesktop.wonderland.modules.audiomanager.common.AudioManagerConnectionType;

import java.io.Serializable;
import java.util.logging.Level;

import org.jdesktop.wonderland.common.cell.security.ViewAction;
import org.jdesktop.wonderland.common.security.Action;
import org.jdesktop.wonderland.server.cell.CellResourceManager;
import org.jdesktop.wonderland.server.security.ActionMap;
import org.jdesktop.wonderland.server.security.Resource;
import org.jdesktop.wonderland.server.security.ResourceMap;
import org.jdesktop.wonderland.server.security.SecureTask;
import org.jdesktop.wonderland.server.security.SecurityManager;

import org.jdesktop.wonderland.modules.orb.common.OrbCellServerState;

import org.jdesktop.wonderland.modules.orb.server.cell.OrbCellMO;

/**
 * A server cell that provides conference coneofsilence functionality
 * @author jprovino
 */
public class ConeOfSilenceProximityListener implements ProximityListenerSrv, 
	AudioGroupListener, ManagedObject, Serializable {

    private static final Logger logger =
            Logger.getLogger(ConeOfSilenceProximityListener.class.getName());

    private CellID cellID;
    private String name;
    private double outsideAudioVolume;
    private boolean entered;

    public ConeOfSilenceProximityListener(CellMO cellMO, String name, 
	    double outsideAudioVolume) {

        this.name = name;
	this.outsideAudioVolume = outsideAudioVolume;

	cellID = cellMO.getCellID();
    }

    private String getCallID(CellID viewCellID) {
	CellMO viewCellMO = CellManagerMO.getCell(viewCellID);

	if (viewCellMO instanceof OrbCellMO) {
	    return ((OrbCellMO) viewCellMO).getCallID();
	}

	return CallID.getCallID(viewCellID);
    }

    public void viewEnterExit(boolean entered, CellID cellID,
            CellID viewCellID, BoundingVolume proximityVolume,
            int proximityIndex) {

	logger.info("viewEnterExit:  " + entered + " cellID " + cellID
	    + " viewCellID " + viewCellID);

	this.entered = entered;

	if (entered) {
	    cellEntered(viewCellID);
	} else {
	    cellExited(viewCellID);
	}
    }

    public void cellEntered(CellID viewCellID) {
	cellEntered(cellID, getCallID(viewCellID));
    }

    private void cellEntered(CellID cellID, String callId) {
        // get the security manager
        SecurityManager security = AppContext.getManager(SecurityManager.class);
        CellResourceManager crm = AppContext.getManager(CellResourceManager.class);

        // create a request
        Action viewAction = new ViewAction();
        Resource resource = crm.getCellResource(this.cellID);
        if (resource != null) {
            // there is security on this cell perform the enter notification
            // securely
            ActionMap am = new ActionMap(resource, new Action[] { viewAction });
            ResourceMap request = new ResourceMap();
            request.put(resource.getId(), am);

            // perform the security check
            security.doSecure(request, new CellEnteredTask(this, resource.getId(),
                              callId));
        } else {
            // no security, just make the call directly
            cellEntered(callId);
        }
    }

    private static class CellEnteredTask implements SecureTask, Serializable {
        private final ManagedReference<ConeOfSilenceProximityListener> listenerRef;
        private final String resourceID;
        private final String callId;

        public CellEnteredTask(ConeOfSilenceProximityListener listener,
                               String resourceID, String callId)
        {
            this.listenerRef = AppContext.getDataManager().createReference(listener);
            this.resourceID = resourceID;
            this.callId = callId;
        }

        public void run(ResourceMap granted) {
            ActionMap am = granted.get(resourceID);
            if (am != null && !am.isEmpty()) {
                // request was granted -- the user has permission to
                // enter the COS
                listenerRef.get().cellEntered(callId);
            } else {
                logger.warning("Access denied to enter Cone of Silence");
            }
        }
    }

    private void cellEntered(String callId) {
        /*
         * A cell has entered the ConeOfSilence cell.
         * Set the public and incoming spatializers for the cell to be
         * the zero volume spatializer.
         * Set a private spatializer for the given fullVolume radius
         * for all the other avatars in the cell.
         * For each cell already in the cell, set a private spatializer
         * for this cell.
         */
        VoiceManager vm = AppContext.getManager(VoiceManager.class);

        Player player = vm.getPlayer(callId);

        logger.warning(callId + " entered cone " + name + " player " + player);

        if (player == null) {
            logger.warning("Can't find player for " + callId);
            return;
        }

        AudioGroup audioGroup = vm.getAudioGroup(name);

        if (audioGroup == null) {
	    AudioGroupSetup setup = new AudioGroupSetup();

	    setup.audioGroupListener = this;

	    setup.spatializer = new FullVolumeSpatializer();

            setup.spatializer.setAttenuator(Spatializer.DEFAULT_MAXIMUM_VOLUME);

	    //System.out.println("Creating audio group for " + name);

	    audioGroup = vm.createAudioGroup(name, setup);
        }

	//System.out.println("CONE PROX Player:  " + player);

	boolean isSpeaking = (inPrivateChat(audioGroup, player) == false);

        audioGroup.addPlayer(player, new AudioGroupPlayerInfo(isSpeaking,
                AudioGroupPlayerInfo.ChatType.PRIVATE));
        
	WonderlandClientSender sender =
            WonderlandContext.getCommsManager().getSender(AudioManagerConnectionType.CONNECTION_TYPE);

	sender.send(new ConeOfSilenceEnterExitMessage(name, callId, true));
    }

    private boolean inPrivateChat(AudioGroup audioGroup, Player player) {
	AudioGroup[] audioGroups = player.getAudioGroups();

	for (int i = 0; i < audioGroups.length; i++) {
	    AudioGroupPlayerInfo info = audioGroups[i].getPlayerInfo(player);

	    if (info == null || info.chatType.equals(AudioGroupPlayerInfo.ChatType.PUBLIC) == false) {
		return true;
	    }
	}
	
	return false;
    }

    public void playerAdded(AudioGroup audioGroup, Player player, AudioGroupPlayerInfo info) {
	//System.out.println("Player added:  " + player);

	logger.fine("Attenuate other groups to " + outsideAudioVolume + " name " + name);

	//System.out.println("Attenuate other groups to " + outsideAudioVolume + " name " + name);

	Player p = AppContext.getManager(VoiceManager.class).getPlayer(player.getId());

	if (player.toString().equals(p.toString()) == false) {
	    System.out.println("WRONG player!");
	    player = p;
	}

	player.attenuateOtherGroups(audioGroup, 0, outsideAudioVolume);
    }

    public void cellExited(CellID viewCellID) {
        cellExited(getCallID(viewCellID));
    }

    private void cellExited(String callId) {
        logger.warning(callId + " exited cone " + name + " avatar cell ID " + callId);

        VoiceManager vm = AppContext.getManager(VoiceManager.class);

        AudioGroup audioGroup = vm.getAudioGroup(name);

        if (audioGroup == null) {
            logger.warning("No audio group " + name);
            return;
        }

        Player player = vm.getPlayer(callId);

        if (player == null) {
            logger.warning("Can't find player for " + callId);
            return;
        }

        audioGroup.removePlayer(player);

	WonderlandClientSender sender =
            WonderlandContext.getCommsManager().getSender(AudioManagerConnectionType.CONNECTION_TYPE);

	sender.send(new ConeOfSilenceEnterExitMessage(name, callId, false));
    }

    public void playerRemoved(AudioGroup audioGroup, Player player, AudioGroupPlayerInfo info) {
	VoiceChatHandler.updateAttenuation(player);

	if (entered) {
	    entered = false;

	    WonderlandClientSender sender =
                WonderlandContext.getCommsManager().getSender(AudioManagerConnectionType.CONNECTION_TYPE);

	    sender.send(new ConeOfSilenceEnterExitMessage(name, player.getId(), false));
	}
    }

    public void remove() {
        VoiceManager vm = AppContext.getManager(VoiceManager.class);

        AudioGroup audioGroup = vm.getAudioGroup(name);

	logger.warning("Remove " + audioGroup + " name " + name);

	if (audioGroup == null) {
	    return;
	}

	vm.removeAudioGroup(audioGroup);
    }

}
