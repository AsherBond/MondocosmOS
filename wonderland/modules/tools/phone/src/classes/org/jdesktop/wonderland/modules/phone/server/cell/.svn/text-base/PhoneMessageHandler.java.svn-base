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
package org.jdesktop.wonderland.modules.phone.server.cell;

import org.jdesktop.wonderland.modules.orb.server.cell.Orb;


import org.jdesktop.wonderland.modules.phone.common.CallListing;
import org.jdesktop.wonderland.modules.phone.common.PhoneInfo;

import org.jdesktop.wonderland.modules.phone.common.messages.CallEndedResponseMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.EndCallMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.EndCallResponseMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.JoinCallMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.JoinCallResponseMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.LockUnlockMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.LockUnlockResponseMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.PlaceCallMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.PlaceCallResponseMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.PlayTreatmentMessage;
import org.jdesktop.wonderland.modules.phone.common.messages.PhoneControlMessage;

import com.sun.mpk20.voicelib.app.AudioGroup;
import com.sun.mpk20.voicelib.app.AudioGroupPlayerInfo;
import com.sun.mpk20.voicelib.app.AudioGroupSetup;
import com.sun.mpk20.voicelib.app.Call;
import com.sun.mpk20.voicelib.app.CallSetup;
import com.sun.mpk20.voicelib.app.FullVolumeSpatializer;
import com.sun.mpk20.voicelib.app.Player;
import com.sun.mpk20.voicelib.app.PlayerSetup;
import com.sun.mpk20.voicelib.app.VoiceManager;
import com.sun.mpk20.voicelib.app.VoiceManagerParameters;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedObject;

import com.sun.voip.CallParticipant;

import org.jdesktop.wonderland.common.cell.messages.CellMessage;

import org.jdesktop.wonderland.server.cell.AbstractComponentMessageReceiver;
import org.jdesktop.wonderland.server.cell.ChannelComponentMO;

import org.jdesktop.wonderland.server.comms.WonderlandClientSender;


import java.io.IOException;
import java.io.Serializable;

import java.util.logging.Logger;








import com.jme.math.Vector3f;

import org.jdesktop.wonderland.server.comms.WonderlandClientID;

/**
 * A server cell that provides conference phone functionality
 * @author jprovino
 */
public class PhoneMessageHandler extends AbstractComponentMessageReceiver
	implements ManagedObject, Serializable {

    private static final Logger logger =
        Logger.getLogger(PhoneMessageHandler.class.getName());
     
    private int callNumber = 0;

    public PhoneMessageHandler(PhoneCellMO phoneCellMO) {
	super(phoneCellMO);

        ChannelComponentMO channelComponentMO = getChannelComponent();

        channelComponentMO.addMessageReceiver(EndCallMessage.class, this);
        channelComponentMO.addMessageReceiver(JoinCallMessage.class, this);
        channelComponentMO.addMessageReceiver(LockUnlockMessage.class, this);
        channelComponentMO.addMessageReceiver(PlaceCallMessage.class, this);
        channelComponentMO.addMessageReceiver(PlayTreatmentMessage.class, this);
    }

    public void done() {
	ChannelComponentMO channelComponentMO = getChannelComponent();

	channelComponentMO.removeMessageReceiver(EndCallMessage.class);
	channelComponentMO.removeMessageReceiver(JoinCallMessage.class);
	channelComponentMO.removeMessageReceiver(LockUnlockMessage.class);
	channelComponentMO.removeMessageReceiver(PlaceCallMessage.class);
	channelComponentMO.removeMessageReceiver(PlayTreatmentMessage.class);
    }

    public void messageReceived(final WonderlandClientSender sender, 
	    final WonderlandClientID clientID, final CellMessage message) {

	PhoneControlMessage msg = (PhoneControlMessage) message;

	logger.fine("got message " + msg);

	PhoneCellMO phoneCellMO = (PhoneCellMO) getCell();

	if (message instanceof LockUnlockMessage) {
	    LockUnlockMessage m = (LockUnlockMessage) message;

	    boolean successful = true;

	    PhoneInfo phoneInfo = phoneCellMO.getPhoneInfo();

	    if (m.getPassword() != null) {
		String password = System.getProperty("wonderland.phone.password");

		if (password == null || password.length() == 0) {
	    	    password = phoneInfo.password;
		}

		successful = m.getPassword().equals(password);
	    }

	    if (successful) {
		phoneInfo.locked = !phoneInfo.locked;
	        phoneInfo.keepUnlocked = m.keepUnlocked();
	    }

	    logger.fine("locked " + phoneInfo.locked + " successful " 
		+ successful + " pw " + m.getPassword());

            LockUnlockResponseMessage response = 
		new LockUnlockResponseMessage(phoneCellMO.getCellID(), phoneInfo.locked, successful);

	    sender.send(response);
	    return;
        }

	VoiceManager vm = AppContext.getManager(VoiceManager.class);

        CallListing listing = msg.getCallListing();
              
	String externalCallID = getExternalCallID(listing);

	Call externalCall = vm.getCall(externalCallID);

	Player externalPlayer = null;

	if (externalCall != null) {
	    externalPlayer = externalCall.getPlayer();
	}

	String softphoneCallID = listing.getSoftphoneCallID();

	Call softphoneCall = null;

	Player softphonePlayer = null;

	AudioGroup audioGroup = null;

	String audioGroupId = null;

	VoiceManagerParameters parameters = vm.getVoiceManagerParameters();

	if (softphoneCallID != null) {
	    softphoneCall = vm.getCall(softphoneCallID);

	    if (softphoneCall != null) {
	        softphonePlayer = softphoneCall.getPlayer();
	    }
        
	    audioGroupId = softphoneCallID + "_" + externalCallID;

	    audioGroup = vm.getAudioGroup(audioGroupId);
	}

	logger.fine("EXTERNAL CALLID IS " + externalCallID + " " + msg
	    + " softphone callID " + softphoneCallID + " softphone call " 
	    + softphoneCall + " softphone player " + softphonePlayer);

	if (message instanceof PlayTreatmentMessage) {
	    PlayTreatmentMessage m = (PlayTreatmentMessage) message;

	    logger.fine("play treatment " + m.getTreatment() 
		+ " to " + listing.getExternalCallID() + " echo " + m.echo());

            if (listing.simulateCalls() == true) {
		return;
	    }

	    try {
		externalCall.playTreatment(m.getTreatment());
	    } catch (IOException e) {
		logger.warning("Unable to play treatment to " + externalCall + ":  "
		    + e.getMessage());
	    }

	    if (m.echo() == false) {
		return;
	    }

	    logger.fine("echoing treatment to " + softphoneCallID);

	    try {
		softphoneCall.playTreatment(m.getTreatment());
	    } catch (IOException e) {
		logger.warning("Unable to play treatment to " + softphoneCall + ":  "
		    + e.getMessage());
		sender.send(clientID, new CallEndedResponseMessage(
                    phoneCellMO.getCellID(), listing, true, "Softphone is not connected!"));
                return;
	    }

	    return;
	}

	if (msg instanceof PlaceCallMessage) {
            //Our phone cell is asking us to begin a new call.

	    if (listing.simulateCalls() == false) {
		relock(sender);
	    }

	    logger.fine("Got place call message " + externalCallID);

	    PlayerSetup playerSetup = new PlayerSetup();
	    //playerSetup.x =  translation.x;
	    //playerSetup.y =  translation.y;
	    //playerSetup.z =  translation.z;
	    playerSetup.isOutworlder = true;
	    playerSetup.isLivePlayer = true;

            if (listing.simulateCalls() == false) {
	        PhoneStatusListener phoneStatusListener = 
		    new PhoneStatusListener(phoneCellMO, listing, clientID);

	        if (softphoneCall == null || softphonePlayer == null) {
		    logger.warning("Softphone player is not connected!");
            	    sender.send(clientID, new CallEndedResponseMessage(
			phoneCellMO.getCellID(), listing, false, 
			"Softphone is not connected!"));
		    return;
	        }

		CallSetup setup = new CallSetup();
	
		CallParticipant cp = new CallParticipant();

		setup.cp = cp;

		setup.externalOutgoingCall = true;

		try {
		    setup.bridgeInfo = vm.getVoiceBridge();
	 	} catch (IOException e) {
		    logger.warning("Unable to get voice bridge for call " + cp + ":  "
			+ e.getMessage());
		    return;
		}

		cp.setPhoneNumber(listing.getContactNumber());
		cp.setName(listing.getContactName());
		cp.setCallId(externalCallID);
		cp.setConferenceId(parameters.conferenceId);
		cp.setVoiceDetection(true);
		cp.setDtmfDetection(true);
		cp.setVoiceDetectionWhileMuted(true);
		cp.setHandleSessionProgress(true);

		try {
                    externalCall = vm.createCall(externalCallID, setup);
	 	} catch (IOException e) {
		    logger.warning("Unable to create call " + cp + ":  "
			+ e.getMessage());
		    return;
		}

	    	externalPlayer = vm.createPlayer(externalCallID, playerSetup);

		externalCall.setPlayer(externalPlayer);

		externalPlayer.setCall(externalCall);

                if (listing.isPrivate()) {
		    /*
		     * Allow caller and callee to hear each other
		     */
		    AudioGroupSetup audioGroupSetup = new AudioGroupSetup();
		    audioGroupSetup.spatializer = new FullVolumeSpatializer();

		    audioGroup = vm.createAudioGroup(audioGroupId, audioGroupSetup);
		    audioGroup.addPlayer(externalPlayer, 
		        new AudioGroupPlayerInfo(true, 
		        AudioGroupPlayerInfo.ChatType.EXCLUSIVE));
		    audioGroup.addPlayer(softphonePlayer, 
		        new AudioGroupPlayerInfo(true, 
		        AudioGroupPlayerInfo.ChatType.EXCLUSIVE));
		} else {
		    AudioGroup defaultLivePlayerAudioGroup = 
		        parameters.livePlayerAudioGroup;

		    defaultLivePlayerAudioGroup.addPlayer(externalPlayer, 
		        new AudioGroupPlayerInfo(true, 
		        AudioGroupPlayerInfo.ChatType.PUBLIC));

		    AudioGroup defaultStationaryPlayerAudioGroup = 
		        parameters.stationaryPlayerAudioGroup;

		    defaultStationaryPlayerAudioGroup.addPlayer(externalPlayer, 
		        new AudioGroupPlayerInfo(false, 
		        AudioGroupPlayerInfo.ChatType.PUBLIC));
		}

		logger.fine("done with audio groups");
            }
            
	    if (externalCall != null) {
	        externalCallID = externalCall.getId();
	    }

	    logger.fine("Setting actual call id to " + externalCallID);

	    listing.setExternalCallID(externalCallID);  // set actual call Id

            //Check implicit privacy settings
            if (listing.isPrivate()) {
                /** HARRISNOTE: We need our client name later in order to 
                 * setup private spatializers. But because we didn't know 
                 * our proper client name in the PhoneCell, we update the 
                 * callListing now that we do.
                 **/
		listing.setPrivateClientName(externalCallID);

                /*
		 * Set the call audio to whisper mode until the caller 
		 * chooses to join the call.
		 */
                if (listing.simulateCalls() == false) {
                    //Mute the two participants to the outside world
                    logger.fine("attenuate other groups");
		    softphonePlayer.attenuateOtherGroups(audioGroup, 0, 0);
                    logger.fine("back from attenuate other groups");
                }
            } else {
		Vector3f center = new Vector3f();

		phoneCellMO.getWorldBounds().getCenter(center);

        	center.setY((float).5);

                new Orb(listing.getContactName(), listing.getContactName(), 
		    externalCallID, center, .1, listing.simulateCalls());
	    }

            if (listing.simulateCalls() == false) {
                //Place the calls audio at the phones position
           	Vector3f location = new Vector3f();

                location = phoneCellMO.getWorldTransform(null).getTranslation(location);

                externalPlayer.moved(location.x, location.y, location.z, 0);
            }
          
            /*
	     * Send PLACE_CALL_RESPONSE message back to all the clients 
	     * to signal success.
	     */
            sender.send(clientID, new PlaceCallResponseMessage(
		phoneCellMO.getCellID(), listing, true));

	    logger.fine("back from notifying user");
	    return;
	}

	if (msg instanceof JoinCallMessage) {
            //Our phone cell wants us to join the call into the world.
            
            if (listing.simulateCalls() == false) {
                //Stop any current ringing.
	        try {
                    softphoneCall.stopTreatment("ring_tone.au");
	        } catch (IOException e) {
		    logger.fine("Unable to stop treatment to " + softphoneCall + ":  "
		        + e.getMessage());
	        }

		AudioGroup defaultLivePlayerAudioGroup = parameters.livePlayerAudioGroup;

		defaultLivePlayerAudioGroup.addPlayer(externalPlayer, 
		    new AudioGroupPlayerInfo(true, 
		    AudioGroupPlayerInfo.ChatType.PUBLIC));

		AudioGroup defaultStationaryPlayerAudioGroup = parameters.stationaryPlayerAudioGroup;

		defaultStationaryPlayerAudioGroup.addPlayer(externalPlayer, 
		    new AudioGroupPlayerInfo(false, 
		    AudioGroupPlayerInfo.ChatType.PUBLIC));

	        softphonePlayer.attenuateOtherGroups(audioGroup, 
		    AudioGroup.DEFAULT_SPEAKING_ATTENUATION,
		    AudioGroup.DEFAULT_LISTEN_ATTENUATION);

		audioGroup.removePlayer(externalPlayer);
		audioGroup.removePlayer(softphonePlayer);
	        vm.removeAudioGroup(audioGroup);
            }
            
            listing.setPrivateClientName("");
              
            //Inform the PhoneCells that the call has been joined successfully
            sender.send(clientID, new JoinCallResponseMessage(
		phoneCellMO.getCellID(), listing, true));
            
	    Vector3f center = new Vector3f();

	    phoneCellMO.getWorldBounds().getCenter(center);

            center.setY((float).5);

            new Orb(listing.getContactName(), listing.getContactName(), externalCallID, 
		center, .1, false);
	    return;
	}

	if (msg instanceof EndCallMessage) {
	    logger.fine("simulate is " + listing.simulateCalls() 
		+ " external call " + externalCall);

            if (listing.simulateCalls() == false) {
		relock(sender);

		if (externalCall != null) {
		    try {
                        vm.endCall(externalCall, true);
	            } catch (IOException e) {
		        logger.warning(
			    "Unable to end call " + externalCall + ":  "
		            + e.getMessage());
	            }
		}

		if (audioGroup != null) {
	            vm.removeAudioGroup(audioGroup);

                    if (listing.isPrivate()) {
	        	softphonePlayer.attenuateOtherGroups(audioGroup, 
			    AudioGroup.DEFAULT_SPEAKING_ATTENUATION,
		    	    AudioGroup.DEFAULT_LISTEN_ATTENUATION);
	            }
		} 
            } 
            
            //Send SUCCESS to phone cell
            sender.send(clientID, new EndCallResponseMessage(
		phoneCellMO.getCellID(), listing, true, 
		"User requested call end"));
	    return;
        } 

	logger.fine("Uknown message type:  " + msg);
    }
   
    private void relock(WonderlandClientSender sender) {
	PhoneCellMO phoneCellMO = (PhoneCellMO) getCell();

	PhoneInfo phoneInfo = phoneCellMO.getPhoneInfo();

	if (phoneInfo.keepUnlocked == false && phoneInfo.locked == false) {
	    phoneInfo.locked = true;

            LockUnlockResponseMessage response = new LockUnlockResponseMessage(phoneCellMO.getCellID(), true, true);

            sender.send(response);
	}
    }

    private String getExternalCallID(CallListing listing) {
	String externalCallID = listing.getExternalCallID();

	if (externalCallID != null && externalCallID.length() > 0) {
	    logger.fine("using existing call id " + externalCallID);
	    return externalCallID;
	}

	synchronized (this) {
	    callNumber++;
	
            listing.setExternalCallID(getCell().getCellID() + "_" + callNumber);

	    return listing.getExternalCallID();
	}
    }

}
