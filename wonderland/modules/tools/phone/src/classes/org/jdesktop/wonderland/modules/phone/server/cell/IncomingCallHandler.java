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

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;

import org.jdesktop.wonderland.common.cell.CellID;

import org.jdesktop.wonderland.server.cell.CellManagerMO;
import org.jdesktop.wonderland.server.cell.CellMO;

import com.sun.voip.client.connector.CallStatus;
import com.sun.voip.client.connector.CallStatusListener;

import com.sun.mpk20.voicelib.app.AudioGroup;
import com.sun.mpk20.voicelib.app.AudioGroupSetup;
import com.sun.mpk20.voicelib.app.AudioGroupPlayerInfo;
import com.sun.mpk20.voicelib.app.Call;
import com.sun.mpk20.voicelib.app.CallSetup;
import com.sun.mpk20.voicelib.app.Player;
import com.sun.mpk20.voicelib.app.PlayerSetup;
import com.sun.mpk20.voicelib.app.Spatializer;
import com.sun.mpk20.voicelib.app.DefaultSpatializer;
import com.sun.mpk20.voicelib.app.ManagedCallBeginEndListener;
import com.sun.mpk20.voicelib.app.ManagedCallStatusListener;
import com.sun.mpk20.voicelib.app.VoiceManager;
import com.sun.mpk20.voicelib.app.VoiceManagerParameters;

import com.sun.voip.CallParticipant;

import com.sun.voip.client.connector.CallStatus;
import com.sun.voip.client.connector.CallStatusListener;

import org.jdesktop.wonderland.common.cell.CellTransform;

import org.jdesktop.wonderland.modules.phone.common.PhoneInfo;

import java.math.BigInteger;

import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;

import java.util.concurrent.ConcurrentHashMap;

import java.util.logging.Logger;

import com.jme.math.Vector3f;

/**
 * Listen for incoming calls, play treatments to prompt the caller for 
 * the phone to call.  Transfer the call to that phone.
 */
public class IncomingCallHandler implements ManagedCallBeginEndListener,
	ManagedCallStatusListener, Serializable {

    /** a logger */
    private static final Logger logger =
            Logger.getLogger(IncomingCallHandler.class.getName());

    private static int defaultCallAnswerTimeout = 90;  // 90 seconds

    private ConcurrentHashMap<String, CallHandler> callTable = new ConcurrentHashMap();

    private static final int DEFAULT_TIMEOUT = 30000;
    private static int timeout = DEFAULT_TIMEOUT;

    private ConcurrentHashMap<String, Phone> phoneMap = new ConcurrentHashMap();

    private ArrayList<Phone> phoneList = new ArrayList();

    private static IncomingCallHandler incomingCallHandler;

    private class Phone implements Serializable {
	public ManagedReference<CellMO> phoneCellRef;
	public PhoneInfo phoneInfo;

	public Phone(ManagedReference<CellMO> phoneCellRef, PhoneInfo phoneInfo) {
	    this.phoneCellRef = phoneCellRef;
	    this.phoneInfo = phoneInfo;
	}
    }

    /**
     * Constructor.
     */
    private IncomingCallHandler() {
    }

    public static IncomingCallHandler getInstance() {
	if (incomingCallHandler != null) {
	    return incomingCallHandler;
	}

	VoiceManager vm = AppContext.getManager(VoiceManager.class);

	incomingCallHandler = new IncomingCallHandler();

	vm.addCallStatusListener(incomingCallHandler);

	vm.addCallBeginEndListener(incomingCallHandler);

	return incomingCallHandler;
    }

    public void done() {
	VoiceManager vm = AppContext.getManager(VoiceManager.class);
	vm.removeCallStatusListener(incomingCallHandler);
	vm.removeCallBeginEndListener(incomingCallHandler);
    }

    public void addPhone(CellID phoneCellID, PhoneInfo phoneInfo) {

	logger.info("adding phone " + phoneInfo.phoneNumber + " "
	    + phoneInfo.phoneLocation + " zero volume radius " 
	    + phoneInfo.zeroVolumeRadius + " full volume radius " 
	    + phoneInfo.fullVolumeRadius + " locked " + phoneInfo.locked 
	    + " keepUnlocked " + phoneInfo.keepUnlocked);

	ManagedReference<CellMO> phoneCellRef = 
	    AppContext.getDataManager().createReference(
	    CellManagerMO.getCell(phoneCellID));

	AudioGroupSetup setup = new AudioGroupSetup();

	/*
	 * Provide Outworlder with full volume for an
	 * extended radius.
	 */
        DefaultSpatializer extendedRadiusSpatializer = new DefaultSpatializer();

	extendedRadiusSpatializer.setZeroVolumeRadius(
	    phoneInfo.zeroVolumeRadius);

	extendedRadiusSpatializer.setFullVolumeRadius(
	    phoneInfo.fullVolumeRadius);

	setup.spatializer = extendedRadiusSpatializer;
   
	Phone phone = new Phone(phoneCellRef, phoneInfo);

	phoneMap.put(phoneInfo.phoneNumber, phone);

	/*
	 * Add phoneInfo to the right place in the list so that
	 * the phone numbers are increasing.
	 */
	int n;

	try {
	    n = Integer.parseInt(phoneInfo.phoneNumber);
	} catch (NumberFormatException e) {
	    phoneList.add(phone);
	    return;
	}

	for (int i = 0; i < phoneList.size(); i++) {
	    Phone p = phoneList.get(i);

	    int nn;

	    try {
		nn = Integer.parseInt(p.phoneInfo.phoneNumber);
	    } catch (NumberFormatException e) {
	       	phoneList.add(phone);
		return;
	    }

	    if (n < nn) {
	       	phoneList.add(i, phone);
		return;
	    }
	}

	phoneList.add(phone);
    }
	
    /*
     * Called when a call is established or ended.
     */
    public void callBeginEndNotification(CallStatus status) {
	logger.finer("got status " + status);

	String callId = status.getCallId();

	int code = status.getCode();

	if (callId == null) {
	    return;	// initial message doesn't have a CallId.
	}

	CallHandler callHandler;

	synchronized(callTable) {
	    callHandler = callTable.get(callId);
	}

	if (callHandler != null) {
	    callHandler.callStatusChanged(status);

            if (code == CallStatus.ENDED) {
	        synchronized(callTable) {
	            callTable.remove(callId);
	        }

	        callHandler.removeCallStatusListener(this);

		try {
	    	    Call call = callHandler.getCall();

		    if (call == null) {
			logger.info("Can't find call for " + callId);
			return;
		    }

		    call.end(false);
		} catch (IOException e) {
		    logger.warning("Unable to end call for " + callId + ": " + e.getMessage());
		}
	    }
	    return;
	}

	if (code != CallStatus.ESTABLISHED) {
	    return;
	}

	String incomingCall = status.getOption("IncomingCall");

	if (incomingCall == null || incomingCall.equals("false")) {
	    return;		// it's not an incoming call
	}

	/*
	 * New incoming call
	 */
	VoiceManager vm = AppContext.getManager(VoiceManager.class);
	
	CallSetup setup = new CallSetup();
	setup.incomingCall = true;

	setup.cp = new CallParticipant();	
	setup.cp.setCallId(callId);
	setup.cp.setConferenceId(vm.getVoiceManagerParameters().conferenceId);

	String callInfo = status.getCallInfo();

	/*
	 * When a call is internal from our PBX caller ID is
	 *
	 * <last name>,<first name>@<10 digit phone number>@<gateway IP address>
	 *
	 * When we get a call from an outside line, the caller ID is
	 *
	 * sip:<10 digit phone number>@<gateway IP address>@<10 digit phone number>@<gateway IP address>
	 */
	String name;
	String phoneNumber;

	if (callInfo.startsWith("sip:")) {
	    callInfo = callInfo.substring(4);
	    String[] tokens = callInfo.split("@");

	    name = tokens[0];
	    phoneNumber = callInfo;
	} else {
	    int ix = callInfo.indexOf("@");

	    if (ix > 0) {
	        name = callInfo.substring(0, ix);

	        String[] tokens = name.split(",");

	        if (tokens.length == 2) {
		    name = tokens[1] + " " + tokens[0];
	        } else {
		    name = callInfo;
	        }

	        phoneNumber = callInfo.substring(ix + 1);
	    } else {
		name = callInfo;
		phoneNumber = callInfo;
	    }
	}

	setup.cp.setPhoneNumber(phoneNumber);
	setup.cp.setName(name);

	Call call;

	try {
	    call = vm.createCall(callId, setup);
	} catch (IOException e) {
	    logger.warning("Unable to create call " + callId + ": " + e.getMessage());
	    return;
	}

	callHandler = new CallHandler(call, status, timeout);

	synchronized(callTable) {
	    callTable.put(callId, callHandler);
	}
    }

    public void callStatusChanged(CallStatus status) {
	String callId = status.getCallId();

	CallHandler callHandler;

	synchronized(callTable) {
	    callHandler = callTable.get(callId);
	}

	if (callHandler == null) {
	    return;
	}

	callHandler.callStatusChanged(status);
    }

    private class CallHandler extends Thread implements Serializable {

	CallStatus establishedStatus;

        private String phoneNumber = "";

 	private Phone phone;

	private int attemptCount = 0;
    
	private int state;

	private int nextPhoneIndex;

        private static final int WAITING_FOR_PHONE_NUMBER = 1;
        private static final int SELECTING_PHONE_NUMBER   = 2;
        private static final int ESTABLISHED		  = 3;
	private static final int CALL_ENDED		  = 4;

	private static final String ENTER_PHONE_NUMBER = 
	    "enter_meeting_code.au";

	private static final String BAD_PHONE_NUMBER = 
	    "bad_meeting_code_1.au";

	private static final String INCOMING_TIMEOUT =
	    "incoming_timeout.au";

	private static final String JOIN_CLICK = "joinCLICK.au";

        private String lastMessagePlayed;

 	private String callId;

	public CallHandler(Call call, CallStatus establishedStatus, int timeout) {
	    this.establishedStatus = establishedStatus;

	    callId = call.getId();

	    logger.info("New Call Handler for call " + callId);

	    state = WAITING_FOR_PHONE_NUMBER;

	    playWaitingForPhoneNumber(call);

	    // TODO The thread must run as a darkstar transaction!
	    //start();
	}

	public Call getCall() {
	    return AppContext.getManager(VoiceManager.class).getCall(callId);
	}
	
	public void addCallStatusListener(ManagedCallStatusListener listener) {
	    AppContext.getManager(VoiceManager.class).addCallStatusListener(listener,
		callId);
	}

	public void removeCallStatusListener(ManagedCallStatusListener listener) {
	    AppContext.getManager(VoiceManager.class).removeCallStatusListener(listener,
		callId);
	}

	private void playWaitingForPhoneNumber(Call call) {
	    playTreatment(call, "enter_phone_number.au");
	}

	private void playWaitingForPhoneNumber() {
	    playTreatment("enter_phone_number.au");
	}

	private Vector3f getLocation(ManagedReference<CellMO> phoneCellRef) {
	    Vector3f location = new Vector3f();

	    return phoneCellRef.get().getWorldTransform(null).getTranslation(location);
	}

	public void run() {
	    /*
	     * Timeout handler to re-prompt user
	     */
	    long startTime = System.currentTimeMillis();

	    while (state == WAITING_FOR_PHONE_NUMBER) {
		int currentState = state;

		try {
		    Thread.sleep(timeout);

		    logger.info("state is  " + state + " for call " + getCall());

		    if (state != WAITING_FOR_PHONE_NUMBER) {
			break;
		    }

		    if (currentState == state) {
			if (System.currentTimeMillis() - startTime >=
			        defaultCallAnswerTimeout * 1000) {
			    
			    playTreatment(INCOMING_TIMEOUT);

			    //
			    // TODO (maybe)
			    //
			    // We'd like to wait until the treatment is done
			    // before cancelling the call.
			    // Need a way to specify an end treatment after
			    // the call is started.
			    //
			    try {
				Thread.sleep(5000);	
			    } catch (InterruptedException e) {
			    }

			    cancelCall();
			    break;
			}

			/*
			 * FIX ME:  This has to become a darkstar transaction!
			 */
			playTreatment(lastMessagePlayed);
		    }
		} catch (InterruptedException e) {
		    logger.warning("Interrupted!");
		}
	    }
	}

        private void cancelCall() {
	    Call call = getCall();

	    if (call == null) {
		logger.info("Can't find call for " + callId);
		return;
	    }

	    try {
	        call.end(true);
	    } catch (IOException e) {
		logger.warning("Unable to end call " + call + ": " + e.getMessage());
	    }
	}

	private void playInitialTreatment(Call call, String treatment) {
	}

	private void playTreatment(String treatment) {
	    Call call = getCall();

	    if (call == null) {
		logger.info("Can't find call for " + callId);
		return;
	    }

	    playTreatment(call, treatment);
	}

	private void playTreatment(Call call, String treatment) {
	    try {
	        call.playTreatment(treatment);
	    } catch (IOException e) {
		logger.warning("Unable to play treatment to call " + call + ": " + e.getMessage());
	    }

	    lastMessagePlayed = treatment;
	}

	public void callStatusChanged(CallStatus status) {
	    logger.fine("got status " + status);

	    int code = status.getCode();

            if (code == CallStatus.ENDED) {
                logger.fine("Call ended...");
		state = CALL_ENDED;
                return;
            }

            int ix;

	    /*
	     * We're only interested in dtmf keys
	     */
            if (code != CallStatus.DTMF_KEY) {
		return;
	    }

            String dtmfKey = status.getDtmfKey();
	    
	    if (state == WAITING_FOR_PHONE_NUMBER) {
		getPhoneNumber(dtmfKey);
	    } else if (state == SELECTING_PHONE_NUMBER) {
		selectPhoneNumber(dtmfKey);
	    }
        }

 	private void handleOption(String dtmfKey) {
	    if (dtmfKey.equals("4")) {
		playTreatment("pound.au");
		playTreatment("star.au");
		playTreatment("mute.au");
		playTreatment("unmute.au");
		playTreatment("less_volume.au");
		playTreatment("more_volume.au");

	        if (state == WAITING_FOR_PHONE_NUMBER) {
		    playWaitingForPhoneNumber();
		}

		return;
	    }
	}

	private void getPhoneNumber(String dtmfKey) {
            if (!dtmfKey.equals("#")) {
                phoneNumber += dtmfKey;  // accumulate phoneNumber
                return;
            }

	    if (phoneNumber.length() == 0) {
		state = SELECTING_PHONE_NUMBER;
		selectPhoneNumber();
		return;
	    }

            attemptCount++;

	    phone = getPhone();

            if (phone != null) {
		Vector3f location = getLocation(phone.phoneCellRef);

                logger.fine("Found phone for " + phoneNumber + " at " + location);

                if (transferCall() == true) {
		    return;
                }
            }

	    playTreatment(BAD_PHONE_NUMBER);

            phoneNumber = "";
	}

	private void selectPhoneNumber() {
	    /*
	     * List the phone information and the number of people near by.
	     * Press # to skip or the phone number to select.
	     */
	    nextPhoneIndex = 0;
	    selectPhoneNumber(null);
	}
	
	private void selectPhoneNumber(String dtmfKey) {
	    if (dtmfKey != null) {
		phone = getPhone();

		if ((phone = getPhone(dtmfKey)) != null) {
                    if (transferCall() == false) {
			playTreatment("cant_transfer.au");
		    }

		    return;
		} else if (!dtmfKey.equals("#")) {
		    logger.fine("Unrecognized response:  " + dtmfKey);
		    playTreatment("unrecognized_respone.au");
		    return;
		}

		nextPhoneIndex++;
	    }

	    if (nextPhoneIndex >= phoneList.size()) {
		// play treatment saying there are no more phones
		playTreatment("no_more_phones.au");

		state = WAITING_FOR_PHONE_NUMBER;
		playWaitingForPhoneNumber();
		return;
	    }

	    phone = phoneList.get(nextPhoneIndex);

	    PhoneInfo phoneInfo = phone.phoneInfo;

	    Vector3f location = getLocation(phone.phoneCellRef);

            int n = AppContext.getManager(VoiceManager.class).getNumberOfPlayersInRange(
                location.getX(), location.getY(), location.getZ());

	    /*
	     * Play treatment saying the phone number and number of
	     * people in range.   <Press phoneNumber> to select, # to go to the next.
	     */
	    if (n == 0) {
	        playTreatment("phone_number.au;tts:" + phoneInfo.phoneNumber
		    + ";no_one.au");
	    } else if (n == 1) {
		playTreatment("phone_number.au;tts:" + phoneInfo.phoneNumber
		    + ";one_person.au");
	    } else {
		playTreatment("phone_number.au;tts:" + phoneInfo.phoneNumber
		    + ";has.au" + n + ";people_in_range.au");
	    }

	    if (phoneInfo.phoneLocation != null) {
		playTreatment(phoneInfo.phoneLocation);
	    } else {
		playTreatment("unknown_location.au");
	    }

	    String s;

	    if (phoneInfo.phoneNumber.equals("1")) {
		s = "select_phone1.au;select_next.au";
	    } else if (phoneInfo.phoneNumber.equals("2")) {
		s = "select_phone2.au;select_next.au";
	    } else if (phoneInfo.phoneNumber.equals("3")) {
		s = "select_phone3.au;select_next.au";
	    } else if (phoneInfo.phoneNumber.equals("4")) {
		s = "select_phone4.au;select_next.au";
	    } else if (phoneInfo.phoneNumber.equals("5")) {
		s = "select_phone5.au;select_next.au";
	    } else {
	        s = "tts:Press " + phoneInfo.phoneNumber + " to select this phone "
		    + "or pound, to skip to the next phone";
	    }

	    playTreatment(s);
	}

	/*
	 * Find the conference
         */
	private Phone getPhone() {
            logger.fine("Looking for phoneNumber: " + phoneNumber);

	    return getPhone(phoneNumber);
	}

	private Phone getPhone(String phoneNumber) {
	    return phoneMap.get(phoneNumber);
	}

	private boolean transferCall() {
	    Call call = getCall();

	    if (call == null) {
		logger.info("Can't find call for " + callId);
		return false;
	    }

	    try {
		PhoneInfo phoneInfo = phone.phoneInfo;

                logger.info("Transferring call " + call
		    + " to phone " + phoneInfo.phoneNumber);

		Vector3f location = getLocation(phone.phoneCellRef);

		PlayerSetup setup = new PlayerSetup();
		setup.x = location.getX();
		setup.y = location.getY();
		setup.z = location.getZ();
		setup.isOutworlder = true;
		setup.isLivePlayer = true;

		VoiceManager vm = AppContext.getManager(VoiceManager.class);

		Player externalPlayer = vm.createPlayer(call.getId(), setup);

		call.setPlayer(externalPlayer);
		externalPlayer.setCall(call);

		VoiceManagerParameters parameters = vm.getVoiceManagerParameters();

                AudioGroup defaultLivePlayerAudioGroup = parameters.livePlayerAudioGroup;

                AudioGroupPlayerInfo groupInfo = new AudioGroupPlayerInfo(true,
                    AudioGroupPlayerInfo.ChatType.PUBLIC);

		groupInfo.defaultListenAttenuation = 1.0;

                defaultLivePlayerAudioGroup.addPlayer(externalPlayer, groupInfo);

		AudioGroup defaultStationaryPlayerAudioGroup = parameters.stationaryPlayerAudioGroup;

                defaultStationaryPlayerAudioGroup.addPlayer(externalPlayer,
                    new AudioGroupPlayerInfo(false,
                    AudioGroupPlayerInfo.ChatType.PUBLIC));

                call.mute(false);

		call.transferToConference(parameters.conferenceId);
		
		String s;

		if (phoneInfo.phoneNumber.equals("1")) {
		    s = "xfer_phone1.au";
		} else if (phoneInfo.phoneNumber.equals("2")) {
		    s = "xfer_phone2.au";
		} else if (phoneInfo.phoneNumber.equals("3")) {
		    s = "xfer_phone3.au";
		} else if (phoneInfo.phoneNumber.equals("4")) {
		    s = "xfer_phone4.au";
		} else if (phoneInfo.phoneNumber.equals("5")) {
		    s = "xfer_phone5.au";
		} else {
		    s = "tts:transferring call to Phone number " 
		        + phoneInfo.phoneNumber;
		}

		playTreatment(s);

		String info = establishedStatus.getCallInfo();

		String phoneNumber = call.getId();

		if (info != null) {
		    String[] tokens = info.split("@");

		    if (info.startsWith("sip:")) {
		        phoneNumber = tokens[2];
		    } else {
		        phoneNumber = tokens[1];
		    }
		}

		playTreatment("help.au");
		playTreatment(JOIN_CLICK);
        
	 	Vector3f center = new Vector3f();

		phone.phoneCellRef.get().getWorldBounds().getCenter(center);

        	center.setY((float).5);

		String username = call.getSetup().cp.getName();

		if (username == null) {
		    username = call.getId();
		}

		new Orb(username, username, call.getId(), center, .1, false);

		state = ESTABLISHED;
            } catch (IOException e) {
                logger.warning(e.getMessage());
                return false;
            }

	    return true;
        }
    }

}
