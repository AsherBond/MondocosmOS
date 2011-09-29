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

import org.jdesktop.wonderland.modules.audiomanager.common.messages.AudioTreatmentEnterExitMessage;

import com.sun.mpk20.voicelib.app.AudioGroup;
import com.sun.mpk20.voicelib.app.AudioGroupListener;
import com.sun.mpk20.voicelib.app.AudioGroupPlayerInfo;
import com.sun.mpk20.voicelib.app.AudioGroupSetup;
import com.sun.mpk20.voicelib.app.DefaultSpatializer;
import com.sun.mpk20.voicelib.app.FullVolumeSpatializer;
import com.sun.mpk20.voicelib.app.Player;
import com.sun.mpk20.voicelib.app.Treatment;
import com.sun.mpk20.voicelib.app.VoiceManager;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedObject;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.cell.CallID;
import org.jdesktop.wonderland.common.cell.CellChannelConnectionType;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.server.WonderlandContext;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.ProximityListenerSrv;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;
import com.jme.bounding.BoundingVolume;

import org.jdesktop.wonderland.modules.audiomanager.common.AudioManagerConnectionType;

import java.io.Serializable;

/**
 * @author jprovino
 */
public class AudioTreatmentProximityListener implements ProximityListenerSrv, 
	ManagedObject, Serializable {

    private static final Logger logger =
            Logger.getLogger(AudioTreatmentProximityListener.class.getName());

    private CellID cellID;
    private String name;
    private String treatmentId;

    private int numberInRange;

    public AudioTreatmentProximityListener(CellMO cellMO, Treatment treatment) {
	cellID = cellMO.getCellID();
        name = cellMO.getName();
	treatmentId = treatment.getId();
    }

    public void viewEnterExit(boolean entered, CellID cellID,
            CellID viewCellID, BoundingVolume proximityVolume,
            int proximityIndex) {

	logger.fine("viewEnterExit:  " + entered + " cellID " + cellID
	    + " viewCellID " + viewCellID);

	if (entered) {
	    cellEntered();
	} else {
	    cellExited();
	}
    }

    public void cellEntered() {
	numberInRange++;

	if (numberInRange > 1) {
	    return;
	}

	logger.fine("Restarting treatment...");

	Treatment treatment = AppContext.getManager(VoiceManager.class).getTreatment(treatmentId);

	if (treatment == null) {
	    logger.warning("No treatment for " + treatmentId);
	    return;
	}

	//System.out.println("Cell entered, restarting input treatment " + treatment);
	treatment.restart(false);
    }

    public void cellExited() {
	numberInRange--;

	if (numberInRange != 0) {
	    return;
	}

	logger.fine("Pausing treatment...");

	Treatment treatment = AppContext.getManager(VoiceManager.class).getTreatment(treatmentId);

	if (treatment == null) {
	    logger.warning("No treatment for " + treatmentId);
	    return;
	}

	//System.out.println("Cell exited , pausing input treatment " + treatment);
	treatment.restart(true);
    }

}
