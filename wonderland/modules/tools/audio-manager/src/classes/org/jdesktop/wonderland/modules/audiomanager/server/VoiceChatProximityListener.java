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

import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;

import java.util.logging.Logger;

import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.ProximityListenerSrv;

import com.jme.bounding.BoundingVolume;

import java.io.Serializable;

import com.sun.sgs.app.ManagedObject;

/**
 * @author jprovino
 */
public class VoiceChatProximityListener implements ProximityListenerSrv, 
	ManagedObject, Serializable {

    private static final Logger logger =
            Logger.getLogger(VoiceChatProximityListener.class.getName());

    private PresenceInfo presenceInfo;

    public VoiceChatProximityListener(PresenceInfo presenceInfo) {
	this.presenceInfo = presenceInfo;
    }

    public void viewEnterExit(boolean entered, CellID cellID,
            CellID viewCellID, BoundingVolume proximityVolume,
            int proximityIndex) {

	logger.fine("viewEnterExit:  " + entered + " cellID " + cellID
	    + " viewCellID " + viewCellID);

	logger.info("viewEnterExit:  " + entered + " cellID " + cellID
	    + " viewCellID " + viewCellID + " presenceInfo " + presenceInfo);
    }

}
