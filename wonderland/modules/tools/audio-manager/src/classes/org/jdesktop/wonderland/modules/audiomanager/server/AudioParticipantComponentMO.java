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

import java.util.ArrayList;

import java.util.logging.Logger;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;

import org.jdesktop.wonderland.common.cell.CellChannelConnectionType;
import org.jdesktop.wonderland.common.cell.CallID;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;


import org.jdesktop.wonderland.common.cell.messages.CellMessage;

import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;

import org.jdesktop.wonderland.modules.audiomanager.common.AudioParticipantComponentClientState;
import org.jdesktop.wonderland.modules.audiomanager.common.AudioParticipantComponentServerState;

import org.jdesktop.wonderland.server.WonderlandContext;

import org.jdesktop.wonderland.server.cell.AbstractComponentMessageReceiver;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.CellComponentMO;
import org.jdesktop.wonderland.server.cell.ChannelComponentMO;
import org.jdesktop.wonderland.server.cell.TransformChangeListenerSrv;

import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

import com.sun.mpk20.voicelib.app.Player;
import com.sun.mpk20.voicelib.app.VoiceManager;

import com.jme.math.Vector3f;

import com.sun.voip.client.connector.CallStatus;

import org.jdesktop.wonderland.modules.orb.server.cell.OrbCellMO;

import com.sun.voip.client.connector.CallStatusListener;

/**
 *
 * @author jprovino
 */
public class AudioParticipantComponentMO extends CellComponentMO {

    private static final Logger logger =
            Logger.getLogger(AudioParticipantComponentMO.class.getName());

    private MyTransformChangeListener myTransformChangeListener;

    private CellID cellID;

    private WonderlandClientID clientID;

    /**
     * Create a AudioParticipantComponent for the given cell. 
     * @param cell
     */
    public AudioParticipantComponentMO(CellMO cellMO) {
        super(cellMO);

	cellID = cellMO.getCellID();
    }

    @Override
    public void setServerState(CellComponentServerState serverState) {
        super.setServerState(serverState);

        // Fetch the component-specific state and set member variables
        AudioParticipantComponentServerState state = (AudioParticipantComponentServerState) serverState;
    }

    @Override
    public CellComponentServerState getServerState(CellComponentServerState serverState) {
        AudioParticipantComponentServerState state = (AudioParticipantComponentServerState) serverState;

        if (state == null) {
            state = new AudioParticipantComponentServerState(false, false);
        }

        return super.getServerState(state);
    }

    @Override
    public CellComponentClientState getClientState(
            CellComponentClientState clientState,
            WonderlandClientID clientID,
            ClientCapabilities capabilities) {

	if (clientState == null) {
	    clientState = new AudioParticipantComponentClientState(false, false);
	}

	this.clientID = clientID;
	return super.getClientState(clientState, clientID, capabilities);
    }

    @Override
    public void setLive(boolean live) {
	super.setLive(live);

        ChannelComponentMO channelComponent = (ChannelComponentMO)
            cellRef.get().getComponent(ChannelComponentMO.class);

	if (live == false) {
	    if (myTransformChangeListener != null) {
	        cellRef.get().removeTransformChangeListener(myTransformChangeListener);
		myTransformChangeListener = null;
	    }

	    return;
	}

	myTransformChangeListener = new MyTransformChangeListener();

	CellMO cellMO = cellRef.get();

	cellMO.addTransformChangeListener(myTransformChangeListener);
    }

    protected String getClientClass() {
	return "org.jdesktop.wonderland.modules.audiomanager.client.AudioParticipantComponent";
    }

    private static class ComponentMessageReceiverImpl extends AbstractComponentMessageReceiver {

        private ManagedReference<AudioParticipantComponentMO> compRef;

        public ComponentMessageReceiverImpl(ManagedReference<CellMO> cellRef,
                AudioParticipantComponentMO comp) {

            super(cellRef.get());

            compRef = AppContext.getDataManager().createReference(comp);
        }

        public void messageReceived(WonderlandClientSender sender, 
	        WonderlandClientID clientID, CellMessage message) {
        }
    }

    public void addCallStatusListener(CallStatusListener listener) {
        addCallStatusListener(listener, null);
    }

    public void addCallStatusListener(CallStatusListener listener, String callID) {
        AppContext.getManager(VoiceManager.class).addCallStatusListener(listener, callID);
    }

    public void removeCallStatusListener(CallStatusListener listener) {
        removeCallStatusListener(listener, null);
    }

    public void removeCallStatusListener(CallStatusListener listener, String callID) {
        AppContext.getManager(VoiceManager.class).removeCallStatusListener(listener, callID);
    }

    static class MyTransformChangeListener implements TransformChangeListenerSrv {

        public void transformChanged(ManagedReference<CellMO> cellRef, 
	        final CellTransform localTransform, final CellTransform localToWorldTransform) {

	    logger.finest("TRANSFORM CHANGED:  " + cellRef.get().getCellID() + " local "
		+ localTransform);

	    logger.fine("localTransform " + localTransform + " world " 
	        + localToWorldTransform);

	    String callID;

	    CellMO cellMO = cellRef.get();

	    if (cellMO instanceof OrbCellMO) {
		callID = ((OrbCellMO) cellMO).getCallID();
	    } else {
	        callID = CallID.getCallID(cellRef.get().getCellID());
	    }

	    float[] angles = new float[3];

	    localToWorldTransform.getRotation(null).toAngles(angles);

	    double angle = Math.toDegrees(angles[1]) % 360 + 90;

	    Vector3f location = localToWorldTransform.getTranslation(null);
	
	    Player player = 
		AppContext.getManager(VoiceManager.class).getPlayer(callID);

	    //AudioTreatmentComponentMO component = 
	    //	cellRef.get().getComponent(AudioTreatmentComponentMO.class);

	    //if (component != null) {
	    //    component.transformChanged(location, angle);   // let subclasses know
	    //}

	    if (player == null) {
	        logger.info("can't find player for " + callID);
		return;
	    }

	    player.moved(location.getX(), location.getY(), location.getZ(), angle);

	    logger.finest("PLAYER MOVED " + player + " x " + location.getX()
	    	+ " y " + location.getY() + " z " + location.getZ()
	    	+ " angle " + angle);
        }

    }

}
