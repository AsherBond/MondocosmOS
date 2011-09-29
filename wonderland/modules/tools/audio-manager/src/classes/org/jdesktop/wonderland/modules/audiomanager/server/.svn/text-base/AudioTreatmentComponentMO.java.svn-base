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

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingCapsule;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.bounding.OrientedBoundingBox;

import com.jme.math.Vector3f;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedReference;

import com.sun.sgs.kernel.KernelRunnable;

import com.sun.sgs.service.NonDurableTransactionParticipant;
import com.sun.sgs.service.Transaction;

import java.io.InputStreamReader;
import java.io.IOException;
import java.io.Serializable;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.jdesktop.wonderland.server.cell.annotation.UsesCellComponentMO;

import org.jdesktop.wonderland.common.cell.messages.CellMessage;

import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;

import org.jdesktop.wonderland.server.cell.AbstractComponentMessageReceiver;
import org.jdesktop.wonderland.server.cell.CellComponentMO;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.CellManagerMO;
import org.jdesktop.wonderland.server.cell.CellParentChangeListenerSrv;
import org.jdesktop.wonderland.server.cell.ChannelComponentMO;
import org.jdesktop.wonderland.server.cell.ProximityComponentMO;

import org.jdesktop.wonderland.modules.audiomanager.common.AudioManagerConnectionType;
import org.jdesktop.wonderland.modules.audiomanager.common.AudioTreatmentComponentClientState;
import org.jdesktop.wonderland.modules.audiomanager.common.AudioTreatmentComponentServerState;
import org.jdesktop.wonderland.modules.audiomanager.common.AudioTreatmentComponentServerState.PlayWhen;
import org.jdesktop.wonderland.modules.audiomanager.common.AudioTreatmentComponentServerState.TreatmentType;

import org.jdesktop.wonderland.modules.audiomanager.common.messages.AudioTreatmentDoneMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.AudioTreatmentEndedMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.AudioTreatmentEstablishedMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.AudioTreatmentMenuChangeMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.AudioTreatmentRequestMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.AudioVolumeMessage;

import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;

import com.sun.voip.client.connector.CallStatus;
import com.sun.voip.client.connector.CallStatusListener;

import com.sun.mpk20.voicelib.app.Call;
import com.sun.mpk20.voicelib.app.AmbientSpatializer;
import com.sun.mpk20.voicelib.app.DefaultSpatializer;
import com.sun.mpk20.voicelib.app.FalloffFunction;
import com.sun.mpk20.voicelib.app.FullVolumeSpatializer;
import com.sun.mpk20.voicelib.app.ManagedCallStatusListener;
import com.sun.mpk20.voicelib.app.Player;
import com.sun.mpk20.voicelib.app.Spatializer;
import com.sun.mpk20.voicelib.app.Treatment;
import com.sun.mpk20.voicelib.app.TreatmentCreatedListener;
import com.sun.mpk20.voicelib.app.TreatmentGroup;
import com.sun.mpk20.voicelib.app.TreatmentSetup;
import com.sun.mpk20.voicelib.app.VoiceManager;
import com.sun.mpk20.voicelib.app.VoiceManagerParameters;
import com.sun.sgs.app.Task;

import org.jdesktop.wonderland.common.cell.CallID;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;

import org.jdesktop.wonderland.common.checksums.Checksum;
import org.jdesktop.wonderland.common.checksums.ChecksumList;
import org.jdesktop.wonderland.server.WonderlandContext;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

/**
 *
 * @author jprovino
 */
public class AudioTreatmentComponentMO extends AudioParticipantComponentMO
	implements CellParentChangeListenerSrv, ManagedCallStatusListener {

    private static final Logger logger =
            Logger.getLogger(AudioTreatmentComponentMO.class.getName());

    private static final String ASSET_PREFIX = "wonderland-web-asset/asset/";

    private String groupId = "";
    private TreatmentType treatmentType;
    private String[] treatments = new String[0];
    private double volume = 1;
    private PlayWhen playWhen = PlayWhen.ALWAYS;
    private boolean playOnce = false;
    private double extent = 10;
    private boolean useCellBounds = false;
    private double fullVolumeAreaPercent = 25;
    private boolean distanceAttenuated = true;
    private double falloff = 50;
    private boolean showBounds = false;

    private BoundingVolume audioBounds;

    private boolean treatmentCreated = false;

    /** the channel from that cell */
    @UsesCellComponentMO(ChannelComponentMO.class)
    private ManagedReference<ChannelComponentMO> channelRef;

    private CellID cellID;

    private static String serverURL;

    static {
        serverURL = System.getProperty("wonderland.web.server.url");
    }

    /**
     * Create a AudioTreatmentComponent for the given cell. The cell must already
     * have a ChannelComponent otherwise this method will throw an IllegalStateException
     * @param cell
     */
    public AudioTreatmentComponentMO(CellMO cellMO) {
        super(cellMO);

	cellID = cellMO.getCellID();

        // The AudioTreatment Component depends upon the Proximity Component.
        // We add this component as a dependency if it does not yet exist
        if (cellMO.getComponent(ProximityComponentMO.class) == null) {
            cellMO.addComponent(new ProximityComponentMO(cellMO));
        }

        cellMO.addParentChangeListener(this);
	//System.out.println("Added parent change listener");
    }

    @Override
    public void setServerState(CellComponentServerState serverState) {
	super.setServerState(serverState);

	if (isLive()) {
	    cleanup();
	}

        AudioTreatmentComponentServerState state = (AudioTreatmentComponentServerState) serverState;

        groupId = state.getGroupId();

	//if (groupId == null || groupId.length() == 0) {
	//    groupId = CallID.getCallID(cellID);
	//}

	treatmentType = state.getTreatmentType();

        treatments = state.getTreatments();

	volume = state.getVolume();

	playWhen = state.getPlayWhen();

	playOnce = state.getPlayOnce();

	extent = state.getExtent();

	useCellBounds = state.getUseCellBounds();

	fullVolumeAreaPercent = state.getFullVolumeAreaPercent();

 	distanceAttenuated = state.getDistanceAttenuated();

	falloff = state.getFalloff();

	showBounds = state.getShowBounds();

	if (isLive()) {
	    initialize();
	}
    }

    public double getVolume() {
	return volume;
    }

    @Override
    public CellComponentServerState getServerState(CellComponentServerState serverState) {
        AudioTreatmentComponentServerState state = (AudioTreatmentComponentServerState) serverState;

        if (state == null) {
            state = new AudioTreatmentComponentServerState();

	    //if (groupId == null || groupId.length() == 0) {
	    //    groupId = CallID.getCallID(cellID);
	    //}

            state.setGroupId(groupId);
	    state.setTreatmentType(treatmentType);
            state.setTreatments(treatments);
	    state.setVolume(volume);
	    state.setPlayWhen(playWhen);
	    state.setPlayOnce(playOnce);
	    state.setExtent(extent);
	    state.setUseCellBounds(useCellBounds);
	    state.setFullVolumeAreaPercent(fullVolumeAreaPercent);
	    state.setDistanceAttenuated(distanceAttenuated);
	    state.setFalloff(falloff);
	    state.setShowBounds(showBounds);
        }

        return super.getServerState(state);
    }

    @Override
    public CellComponentClientState getClientState(
            CellComponentClientState clientState,
            WonderlandClientID clientID,
            ClientCapabilities capabilities) {

	AudioTreatmentComponentClientState state = (AudioTreatmentComponentClientState) clientState;

	if (state == null) {
	    state = new AudioTreatmentComponentClientState();

	    state.groupId = groupId;
	    state.treatmentType = treatmentType;
	    state.treatments = treatments;
	    state.volume = volume;
	    state.playWhen = playWhen;
	    state.playOnce = playOnce;
	    state.extent = extent;
	    state.useCellBounds = useCellBounds;
	    state.fullVolumeAreaPercent = fullVolumeAreaPercent;
	    state.distanceAttenuated = distanceAttenuated;
	    state.falloff = falloff;
	    state.showBounds = showBounds;
	}

        return super.getClientState(state, clientID, capabilities);
    }

    @Override
    public void setLive(boolean live) {
        super.setLive(live);

	//System.out.println("AudiotTreatmentComponent Set live " + live);

        //ChannelComponentMO channelComponent = (ChannelComponentMO) cellRef.get().getComponent(ChannelComponentMO.class);

        ChannelComponentMO channelComponent = channelRef.get();

        if (live == false) {
            channelComponent.removeMessageReceiver(AudioTreatmentMenuChangeMessage.class);
            channelComponent.removeMessageReceiver(AudioTreatmentRequestMessage.class);
            channelComponent.removeMessageReceiver(AudioVolumeMessage.class);
	    removeProximityListener();

	    //cellRef.get().removeParentChangeListener(this);
	    //System.out.println("Removed parent change listener");
	    
	    cleanup();
            return;
        }

        ComponentMessageReceiverImpl receiver = new ComponentMessageReceiverImpl(cellRef.get());

        channelComponent.addMessageReceiver(AudioTreatmentMenuChangeMessage.class, receiver);
        channelComponent.addMessageReceiver(AudioTreatmentRequestMessage.class, receiver);
        channelComponent.addMessageReceiver(AudioVolumeMessage.class, receiver);

	initialize();
    }

    private void initialize() {
        // make sure this isn't a duplicate start
        if (!isLive() || treatmentCreated) {
            logger.warning("Not initializing treatment. isLive: " + isLive() +
                           " created: " + treatmentCreated);
            return;
        }

        // make sure there is a treatment to start
	if (treatments.length == 0) {
	    /*
	     * The AudioTreatmentComponent hasn't been configured yet.
	     */
	    logger.info("Not starting treatment:  groupID " + groupId + " treatments.length " 
		+ treatments.length);

	    return;
	}

        // OWL issue #60: if the world bounds for this object haven't been
        // calculated yet, schedule a task to retry the initialization in a bit.
        if (cellRef.get().getWorldTransform(null) == null) {
            logger.warning("Not initializing treatment: world bounds not set");
            AppContext.getTaskManager().scheduleTask(new TreatmentRetryTask(this),
                                                     1000);

            return;
        }

        VoiceManager vm = AppContext.getManager(VoiceManager.class);

        TreatmentGroup group = null;

	if (groupId != null && groupId.length() > 0) {
	    group = vm.createTreatmentGroup(groupId);
	}
	
        for (int i = 0; i < treatments.length; i++) {
            TreatmentSetup setup = new TreatmentSetup();

	    setup.treatmentCreatedListener = new TreatmentCreatedListenerImpl(cellID);

	    setup.spatializer = getSpatializer(false);

            String treatment = treatments[i];

            String treatmentId = CallID.getCallID(cellID);

	    String pattern = "wlcontent://";

            if (treatment.startsWith(pattern)) {
                /*
                 * We need to create a URL
                 */
                String path = treatment.substring(pattern.length());

                URL url;

                try {
                    path = path.replaceAll(" ", "%20");

                    url = new URL(new URL(serverURL), "webdav/content/" + path);

                    treatment = url.toString();
                } catch (MalformedURLException e) {
                    logger.warning("bad url:  " + e.getMessage());
                    return;
		}
	    } else {
	        pattern = "wls://";

	        if (treatment.startsWith(pattern)) {
                    /*
                     * We need to create a URL from wls:<module>/path
                     */
                    treatment = treatment.substring(pattern.length());  // skip past wls://

                    int ix = treatment.indexOf("/");

                    if (ix < 0) {
                        logger.warning("Bad treatment:  " + treatments[i]);
                        continue;
                    }

                    String moduleName = treatment.substring(0, ix);

                    String path = treatment.substring(ix + 1);

                    logger.fine("Module:  " + moduleName + " treatment " + treatment);

                    URL url;

                    try {
			path = path.replaceAll(" ", "%20");

                        url = new URL(new URL(serverURL),
                            "webdav/content/modules/installed/" + moduleName + "/audio/" + path);

                        treatment = url.toString();
                        logger.fine("Treatment: " + treatment);
                    } catch (MalformedURLException e) {
                        logger.warning("bad url:  " + e.getMessage());
                        continue;
                    }
                }
	    }

            setup.treatment = treatment;

	    vm.addCallStatusListener(this, treatmentId);

            if (setup.treatment == null || setup.treatment.length() == 0) {
                logger.warning("Invalid treatment '" + setup.treatment + "'");
                continue;
            }

            // OWL issue #60: make sure to use world location, not local
            // location
            Vector3f location = cellRef.get().getWorldTransform(null).getTranslation(null);

            setup.x = location.getX();
            setup.y = location.getY();
            setup.z = location.getZ();

            logger.info("Starting treatment " + setup.treatment + " at (" + setup.x 
		+ ":" + setup.y + ":" + setup.z + ")");

            System.out.println("Starting treatment " + setup.treatment + " at (" + setup.x 
		+ ":" + setup.y + ":" + setup.z + ")");

            try {
		Treatment t = vm.createTreatment(treatmentId, setup);

		if (group != null) {
                    group.addTreatment(t);
		}

		if (playWhen.equals(PlayWhen.ALWAYS) == false) {
		    t.pause(true);
		}

	        if (playWhen.equals(PlayWhen.FIRST_IN_RANGE)) {
	            addProximityListener(t);
	        }

                treatmentCreated = true;
            } catch (IOException e) {
                logger.warning("Unable to create treatment " + setup.treatment + e.getMessage());
                return;
            }
        }
    }

    static class TreatmentRetryTask implements Task, Serializable {
        private final ManagedReference<AudioTreatmentComponentMO> componentRef;

        TreatmentRetryTask(AudioTreatmentComponentMO component) {
            componentRef = AppContext.getDataManager().createReference(component);
        }

        public void run() throws Exception {
            componentRef.get().initialize();
        }
    }

    static class TreatmentCreatedListenerImpl implements TreatmentCreatedListener {

	private CellID cellID;

	public TreatmentCreatedListenerImpl(CellID cellID) {
	    this.cellID = cellID;
	}

        public void treatmentCreated(Treatment treatment, Player player) {
	    CellMO cellMO = CellManagerMO.getCellManager().getCell(cellID);

	    if (cellMO == null) {
		logger.warning("No cellMO for " + cellID);
		return;
	    }

            AudioTreatmentComponentMO audioTreatmentComponentMO = 
		cellMO.getComponent(AudioTreatmentComponentMO.class);

	    checkForParentWithCOS(cellMO, audioTreatmentComponentMO);
        }
    }

    public void setSpatializer(boolean inConeOfSilence) {
	String callID = CallID.getCallID(cellID);

	Player player = AppContext.getManager(VoiceManager.class).getPlayer(callID);
	  
	if (player == null) {
	    logger.warning("no player for " + callID);
	    return;
	}

	player.setPublicSpatializer(getSpatializer(inConeOfSilence));
    }

    private Spatializer getSpatializer(boolean inConeOfSilence) {
	float cellRadius = getCellRadius();

	double extent = this.extent;
	    
	if (extent == 0) {
	    extent = cellRadius;
	}

	CellMO cellMO = cellRef.get();

	BoundingVolume bounds = cellMO.getWorldBounds();

	if (useCellBounds) {
	    audioBounds = bounds;

	    if (bounds instanceof BoundingBox) {
	        System.out.println("BoundingBox:  " + bounds);
	        return getSpatializer((BoundingBox) bounds);
	    }

	    double radius = ((BoundingSphere) bounds).getRadius();
	    System.out.println("Using cell bounds " + radius);
	    return new FullVolumeSpatializer(radius);
	} 

	if (inConeOfSilence) {
	    if (bounds instanceof BoundingSphere) {
	        double radius = ((BoundingSphere) bounds).getRadius();

	        if (extent > radius) {
		    extent = radius;
		    System.out.println("Limiting extent to " + extent);
	        }     
	    }

	    audioBounds = new BoundingSphere((float) extent, new Vector3f());

	    Spatializer spatializer = new FullVolumeSpatializer(extent);
	    spatializer.setAttenuator(volume);
	    return spatializer;
	} else {
            audioBounds = new BoundingSphere((float) extent, new Vector3f());
        }
	
	double fullVolumeRadius = fullVolumeAreaPercent / 100. * extent;

	double falloff = .92 + ((50 - this.falloff) * ((1 - .92) / 50));

	if (falloff >= 1) {
	    falloff = .999;
	}

	logger.warning("id " + groupId + " cellRadius " + cellRadius 
	    + " extent " + extent + " use cell bounds " + useCellBounds 
	    + " fvr " + fullVolumeRadius + " falloff " 
	    + falloff + " volume " + volume);

        if (distanceAttenuated == true) {
            DefaultSpatializer spatializer = new DefaultSpatializer();

            spatializer.setFullVolumeRadius(fullVolumeRadius);

            spatializer.setZeroVolumeRadius(extent);

	    spatializer.setAttenuator(volume);

	    FalloffFunction falloffFunction = spatializer.getFalloffFunction();

	    falloffFunction.setFalloff(falloff);

	    spatializer.setAttenuator(volume);

	    return spatializer;
        } 

	Spatializer spatializer = new FullVolumeSpatializer(extent);
	spatializer.setAttenuator(volume);
	return spatializer;
    }

    private Spatializer getSpatializer(BoundingBox bounds) {
	BoundingBox boundingBox = (BoundingBox) bounds;

	Vector3f center = boundingBox.getCenter();
	Vector3f extent = boundingBox.getExtent(null);

	double lowerLeftX = center.getX() - extent.getX();
        double lowerLeftY = center.getY() - extent.getY();
	double lowerLeftZ = center.getZ() - extent.getZ();

	double upperRightX = center.getX() + extent.getX();
        double upperRightY = center.getY() + extent.getY();
	double upperRightZ = center.getZ() + extent.getZ();

	return new AmbientSpatializer(lowerLeftX, lowerLeftY, lowerLeftZ,
	    upperRightX, upperRightY, upperRightZ);
    }

    private void cleanup() {
	treatmentCreated = false;

	CellMO parent = cellRef.get();

	while (parent != null) {
            ConeOfSilenceComponentMO coneOfSilenceComponentMO = 
	        parent.getComponent(ConeOfSilenceComponentMO.class);

	    if (coneOfSilenceComponentMO != null) {
	        coneOfSilenceComponentMO.removeAudioTreatmentComponentMO(cellRef.get(), this);
		break;
	    } 

	    parent = parent.getParent();
	}

        VoiceManager vm = AppContext.getManager(VoiceManager.class);

        TreatmentGroup group = null;

	if (groupId != null) {
	    group = vm.getTreatmentGroup(groupId);
	}

	if (group == null) {
	    Treatment treatment = vm.getTreatment(CallID.getCallID(cellID));

	    if (treatment == null) {
	  	//System.out.println("No treatment for " + CallID.getCallID(cellID));
		return;
	    }

	    endTreatment(treatment);
	    return;
	}

	Treatment[] treatments = group.getTreatments().values().toArray(new Treatment[0]);

	for (int i = 0; i < treatments.length; i++) {
	    //System.out.println("Ending treatment:  " + treatments[i]);
	    endTreatment(treatments[i]);
	    group.removeTreatment(treatments[i]);
	}

	try {
	    vm.removeTreatmentGroup(group);
	} catch (IOException e) {
	    logger.warning("Unable to remove treatment group " + group);
	}

	vm.dump("all");
    }

    private void endTreatment(Treatment treatment) {
	Call call = treatment.getCall();
	
	if (call == null) {
	    logger.warning("No call for treatment " + treatment);
	    return;
	}

	//System.out.println("Ending call for treatment " + treatment);

	try {
	    call.end(false);
	} catch (IOException e) {
	    logger.warning("Unable to end call " + call + ":  " + e.getMessage());
	}
    }

    public CellMO getCell() {
        return cellRef.get();
    }

    @Override
    protected String getClientClass() {
        return "org.jdesktop.wonderland.modules.audiomanager.client.AudioTreatmentComponent";
    }

    public void parentChanged(CellMO cellMO, CellMO parent) {
	//System.out.println("parent changed... isLive " + isLive());

	if (isLive() == false) {
	    return;
	}

	checkForParentWithCOS(cellMO, this);
    }

    private static void checkForParentWithCOS(CellMO cellMO, AudioTreatmentComponentMO audioTreatmentComponentMO) {
	CellMO child = cellMO;

	while (cellMO != null) {
            ConeOfSilenceComponentMO coneOfSilenceComponentMO = 
	        cellMO.getComponent(ConeOfSilenceComponentMO.class);

	    if (coneOfSilenceComponentMO != null) {
	        coneOfSilenceComponentMO.addAudioTreatmentComponentMO(child, audioTreatmentComponentMO);
		break;
	    } 

	    cellMO = cellMO.getParent();
	}
    }

    public void callStatusChanged(CallStatus callStatus) {
        String callId = callStatus.getCallId();

        if (callId == null) {
            logger.warning("No callId in callStatus:  " + callStatus);
            return;
        }

        switch (callStatus.getCode()) {
	case CallStatus.ESTABLISHED:
	    channelRef.get().sendAll(null, new AudioTreatmentEstablishedMessage(cellID, callId));
            break;

        case CallStatus.TREATMENTDONE:
	    logger.info("TREATMENT DONE, playOnce " + playOnce);

	    if (playOnce == true) {
		channelRef.get().sendAll(null, new AudioTreatmentDoneMessage(cellID, callId));
	    }
	    break;

	case CallStatus.ENDED:
	    channelRef.get().sendAll(null, new AudioTreatmentEndedMessage(cellID, callId,
		callStatus.getOption("Reason")));
	    break;
        }
    }

    private static class ComponentMessageReceiverImpl extends AbstractComponentMessageReceiver {
        private CellID cellID;

        public ComponentMessageReceiverImpl(CellMO cellMO) {
            super(cellMO);

	    cellID = cellMO.getCellID();
        }

        public void messageReceived(WonderlandClientSender sender, WonderlandClientID clientID,
                CellMessage message) {

            if (message instanceof AudioTreatmentRequestMessage) {
                AudioTreatmentRequestMessage msg = (AudioTreatmentRequestMessage) message;
                logger.fine("Got AudioTreatmentRequestMessage, startTreatment=" 
		    + msg.restartTreatment());

            	String treatmentId = CallID.getCallID(cellID);

        	Treatment treatment = null;

		treatment = AppContext.getManager(VoiceManager.class).getTreatment(treatmentId);

		if (treatment == null) {
		    logger.warning("Can't find treatment " + treatmentId);
		    return;
		}

		logger.fine("restart " + msg.restartTreatment() + " pause " + msg.isPaused());

		if (msg.restartTreatment()) {
		    treatment.restart(msg.isPaused());
		} else {
		    treatment.pause(msg.isPaused());
		}
                return;
            }

	    if (message instanceof AudioVolumeMessage) {
		handleAudioVolume(sender, clientID, (AudioVolumeMessage) message, getCell());
		return;
	    }

            logger.warning("Unknown message:  " + message);
        }

        private void handleAudioVolume(WonderlandClientSender sender, 
		WonderlandClientID clientID, AudioVolumeMessage msg, CellMO cellMO) {

	    String softphoneCallID = msg.getSoftphoneCallID();

	    String otherCallID = msg.getOtherCallID();

            double volume = msg.getVolume();

            logger.fine("GOT Volume message:  call " + softphoneCallID
	        + " volume " + volume + " other callID " + otherCallID);

            VoiceManager vm = AppContext.getManager(VoiceManager.class);

            Player softphonePlayer = vm.getPlayer(softphoneCallID);

            if (softphonePlayer == null) {
                logger.warning("Can't find softphone player, callID " 
		    + softphoneCallID);
                return;
            }

            Player player = vm.getPlayer(otherCallID);

 	    if (player == null) {
                logger.warning("Can't find player for callID " + otherCallID);
	        return;
            } 

	    if (msg.isSetVolume() == false) {
                AudioTreatmentComponentMO audioTreatmentComponentMO = 
		    cellMO.getComponent(AudioTreatmentComponentMO.class);

		msg.setVolume(audioTreatmentComponentMO.getVolume());
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

            softphonePlayer.setPrivateSpatializer(player, spatializer);
	}
    }

    private ManagedReference<AudioTreatmentProximityListener> proximityListener;

    private void addProximityListener(Treatment treatment) {
        // Fetch the proximity component, we will need this below. If it does
        // not exist (it should), then log an error
        ProximityComponentMO component = cellRef.get().getComponent(ProximityComponentMO.class);

        if (component == null) {
            logger.warning("The AudioTreatment Component does not have a " +
                    "Proximity Component for Cell ID " + cellID);
            return;
        }

        // We are making this component live, add a listener to the proximity component.
	BoundingVolume[] bounds = new BoundingVolume[1];

        bounds[0] = audioBounds;

        AudioTreatmentProximityListener proximityListener = 
	    new AudioTreatmentProximityListener(cellRef.get(), treatment);

	this.proximityListener = AppContext.getDataManager().createReference(proximityListener);
	
        component.addProximityListener(proximityListener, bounds);
    }

    private void removeProximityListener() {
	if (proximityListener != null) {
            ProximityComponentMO component = cellRef.get().getComponent(ProximityComponentMO.class);
	    component.removeProximityListener(proximityListener.get());
	}
    }

    private float getCellRadius() {
	BoundingVolume bounds = cellRef.get().getLocalBounds();

	logger.warning("Cell bounds:  " + bounds);

	float cellRadius;

	if (bounds instanceof BoundingSphere) {
	    cellRadius = ((BoundingSphere) bounds).getRadius();
	} else if (bounds instanceof BoundingBox) {
	    Vector3f extent = new Vector3f();
	    extent = ((BoundingBox) bounds).getExtent(extent);

	    cellRadius = getMax(extent);
	} else if (bounds instanceof BoundingCapsule) {
	    cellRadius = ((BoundingCapsule) bounds).getRadius();
	} else if (bounds instanceof OrientedBoundingBox) {
	    Vector3f extent = ((OrientedBoundingBox) bounds).getExtent();
	    cellRadius = getMax(extent);
	} else {
	    cellRadius = 5;
	}

	return cellRadius;
    }

    private float getMax(Vector3f extent) {
	float max = extent.getX();

	if (extent.getY() > max) {
	    max = extent.getY();
	}

	if (extent.getZ() > max) {
	    max = extent.getZ();
	}

	return max;
    }

    /**
     * Asks the web server for the module's checksum information given the
     * unique name of the module and a particular asset type, returns null if
     * the module does not exist or upon some general I/O error.
     * 
     * @param serverURL The base web server URL
     * @param moduleName The unique name of a module
     * @param assetType The name of the asset type (art, audio, client, etc.)
     * @return The checksum information for a module
     */
    public static ChecksumList fetchAssetChecksums(String serverURL,
            String moduleName, String assetType) {

        try {
            /* Open an HTTP connection to the Jersey RESTful service */
            String uriPart = moduleName + "/checksums/get/" + assetType;
            URL url = new URL(new URL(serverURL), ASSET_PREFIX + uriPart);
            logger.fine("fetchAssetChecksums:  " + url.toString());
            return ChecksumList.decode(new InputStreamReader(url.openStream()));
        } catch (java.lang.Exception e) {
            /* Log an error and return null */
            logger.warning("[MODULES] FETCH CHECKSUMS Failed " + e.getMessage());
            return null;
        }
    }

}
