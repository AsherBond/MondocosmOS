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

import java.util.logging.Logger;
import org.jdesktop.wonderland.common.cell.CallID;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;

import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.CellComponentMO;
import org.jdesktop.wonderland.server.cell.ProximityComponentMO;

import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

import org.jdesktop.wonderland.modules.audiomanager.common.ConeOfSilenceComponentServerState;
import org.jdesktop.wonderland.modules.audiomanager.common.ConeOfSilenceComponentServerState.COSBoundsType;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;

import com.jme.math.Vector3f;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedReference;

/**
 *
 * @author jprovino
 */
public class ConeOfSilenceComponentMO extends CellComponentMO {

    private static final Logger logger =
            Logger.getLogger(ConeOfSilenceComponentMO.class.getName());

    private static final String DEFAULT_NAME = "Cone_of_Silence";

    private String name = DEFAULT_NAME;

    private COSBoundsType boundsType = COSBoundsType.CELL_BOUNDS;

    private Vector3f bounds = new Vector3f(1.6f, 1.6f, 1.6f);

    private boolean showBounds = false;

    private double outsideAudioVolume = 0;

    private ManagedReference<ConeOfSilenceProximityListener> proximityListenerRef;

    /**
     * Create a ConeOfSilenceComponent for the given cell. 
     * @param cell
     */
    public ConeOfSilenceComponentMO(CellMO cellMO) {
        super(cellMO);

        // The Cone of Silence Components depends upon the Proximity Component.
        // We add this component as a dependency if it does not yet exist
        if (cellMO.getComponent(ProximityComponentMO.class) == null) {
            cellMO.addComponent(new ProximityComponentMO(cellMO));
        }
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public void setServerState(CellComponentServerState serverState) {
        super.setServerState(serverState);

        // Fetch the component-specific state and set member variables
        ConeOfSilenceComponentServerState cs = (ConeOfSilenceComponentServerState) serverState;

	if (name == null) {
	    name = DEFAULT_NAME;
	} else {
	    name = cs.getName();
	}

	String appendName = "-" + cellRef.get().getCellID();

	if (name.indexOf(appendName) < 0) {
	    name += "-" + cellRef.get().getCellID();
	}

	boundsType = cs.getBoundsType();

	bounds = cs.getBounds();

	showBounds = cs.getShowBounds();

	outsideAudioVolume = cs.getOutsideAudioVolume();

	addProximityListener(isLive());
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public CellComponentServerState getServerState(CellComponentServerState serverState) {
	ConeOfSilenceComponentServerState state = (ConeOfSilenceComponentServerState) serverState;

        // Create the proper server state object if it does not yet exist
        if (state == null) {
            state = new ConeOfSilenceComponentServerState();
        }

	if (name == null) {
	    name = DEFAULT_NAME;
	}

	String appendName = "-" + cellRef.get().getCellID();

	if (name.indexOf(appendName) < 0) {
	    name += "-" + cellRef.get().getCellID();
	}

        state.setName(name);
	state.setBoundsType(boundsType);
	state.setBounds(bounds);
	state.setShowBounds(showBounds);
        state.setOutsideAudioVolume(outsideAudioVolume);

        return super.getServerState(state);
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public CellComponentClientState getClientState(CellComponentClientState state,
            WonderlandClientID clientID,
            ClientCapabilities capabilities) {

        // TODO: Create own client state object?
        return super.getClientState(state, clientID, capabilities);
    }

    /**
     * @{inheritDoc}
     */
    @Override
    protected String getClientClass() {
        return "org.jdesktop.wonderland.modules.audiomanager.client.ConeOfSilenceComponent";
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public void setLive(boolean live) {
        super.setLive(live);

	//System.out.println("CONE Set live " + live);

	addProximityListener(live);

	AudioTreatmentComponentMO audioTreatmentComponentMO =
	    cellRef.get().getComponent(AudioTreatmentComponentMO.class);

	if (audioTreatmentComponentMO != null) {
	    if (live) {
		addAudioTreatmentComponentMO(cellRef.get(), audioTreatmentComponentMO);
	    } else {
		removeAudioTreatmentComponentMO(cellRef.get(), audioTreatmentComponentMO);
	    }
	}
    }

    public void addAudioTreatmentComponentMO(CellMO cellMO,
	    AudioTreatmentComponentMO audioTreatmentComponentMO) {

	//System.out.println("Adding audio to COS for " + 
	//    CallID.getCallID(cellMO.getCellID()));

	if (proximityListenerRef == null) {
	    return;
	}

	audioTreatmentComponentMO.setSpatializer(true);
	proximityListenerRef.get().cellEntered(cellMO.getCellID());
    }

    public void removeAudioTreatmentComponentMO(CellMO cellMO, 
	    AudioTreatmentComponentMO audioTreatmentComponentMO) {

	//System.out.println("removing audio to COS for " + 
	//    CallID.getCallID(cellMO.getCellID()));

	if (proximityListenerRef == null) {
	    return;
	}

	audioTreatmentComponentMO.setSpatializer(false);
	proximityListenerRef.get().cellExited(cellMO.getCellID());
    }

    private void addProximityListener(boolean live) {
        // Fetch the proximity component, we will need this below. If it does
        // not exist (it should), then log an error
        ProximityComponentMO component = cellRef.get().getComponent(ProximityComponentMO.class);
        if (component == null) {
            logger.warning("The Cone of Silence Component does not have a " +
                    "Proximity Component for Cell ID " + cellID);
            return;
        }

	if (proximityListenerRef != null) {
	    ConeOfSilenceProximityListener proximityListener = proximityListenerRef.get();
	    proximityListener.remove();
            component.removeProximityListener(proximityListener);
	    proximityListenerRef = null;
            //System.out.println("Removing proximity listener...");
        }

        // If we are making this component live, then add a listener to the proximity component.
        if (live == true) {
            BoundingVolume[] boundingVolume = new BoundingVolume[1];

	    //System.out.println("BOUNDS TYPE " + boundsType);

	    if (boundsType.equals(COSBoundsType.CELL_BOUNDS)) {
		boundingVolume[0] = cellRef.get().getLocalBounds();
		logger.warning("COS Using cell local bounds:  " + boundingVolume[0]);
		System.out.println("COS Using cell local bounds:  " + boundingVolume[0]);
	    } else if (boundsType.equals(COSBoundsType.BOX)) {
                boundingVolume[0] = new BoundingBox(new Vector3f(), bounds.getX(), 
		    bounds.getY(), bounds.getZ());
		logger.warning("COS Using specified BOX:  " + boundingVolume[0]);
		System.out.println("COS Using specified BOX:  " + boundingVolume[0]);
	    } else {
                boundingVolume[0] = new BoundingSphere(bounds.getX(), new Vector3f());
		logger.warning("COS Using specified radius:  " + boundingVolume[0]);
		System.out.println("COS Using specified radius:  " + boundingVolume[0]);
	    }

            ConeOfSilenceProximityListener proximityListener = 
		new ConeOfSilenceProximityListener(cellRef.get(), name, outsideAudioVolume);

	    proximityListenerRef = AppContext.getDataManager().createReference(proximityListener);
            component.addProximityListener(proximityListener, boundingVolume);
        } 
    }

}
