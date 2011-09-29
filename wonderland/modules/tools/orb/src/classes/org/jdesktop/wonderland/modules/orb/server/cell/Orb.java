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
package org.jdesktop.wonderland.modules.orb.server.cell;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.ManagedObject;

import java.io.Serializable;

import java.util.logging.Logger;

import org.jdesktop.wonderland.server.cell.CellComponentMO;
import org.jdesktop.wonderland.server.cell.CellManagerMO;
import org.jdesktop.wonderland.server.cell.CellMOFactory;

import org.jdesktop.wonderland.modules.orb.common.OrbCellServerState;

import org.jdesktop.wonderland.modules.orb.server.cell.OrbCellMO;

import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.MultipleParentException;

import com.jme.bounding.BoundingVolume;

import com.jme.math.Vector3f;

import com.sun.mpk20.voicelib.app.Call;
import com.sun.mpk20.voicelib.app.CallSetup;
import com.sun.mpk20.voicelib.app.Player;
import com.sun.mpk20.voicelib.app.VirtualPlayer;

/**
 * Spawn an orb at a specified location
 * @author jprovino
 */
public class Orb implements ManagedObject, Serializable {

    private static final Logger logger =
        Logger.getLogger(Orb.class.getName());
     
    private ManagedReference<OrbCellMO> orbCellMORef;

    private String id;
    private String username;
    private VirtualPlayer vp;

    private CellID cellID;

    private int useCount = 1;

    public Orb(String id, String username, String callID, Vector3f center, 
	    double size, boolean simulateCalls) {

	this(id, username, callID, center, size, simulateCalls, null, new String[0]);
    }    

    public Orb(VirtualPlayer vp, Vector3f center, double size, String callID,
	    String[] bystanders) {

	// XXX need to make user name unique
	// But then InCallDialog will show the wrong name.
	this(vp.getId(), vp.realPlayer.getCall().getSetup().cp.getName(), callID,
	center, .1, false, vp, bystanders);
    }

    private Orb(String orbID, String username, String callID, Vector3f center, 
	    double size, boolean simulateCalls, VirtualPlayer vp, String[] bystanders) {

	this.id = id;
	this.username = username;
	this.vp = vp;

	logger.fine("orb center :  " + center + " size " + size);

        String cellType = 
	    "org.jdesktop.wonderland.modules.orb.server.cell.OrbCellMO";

	OrbCellMO orbCellMO;

	if (vp != null) {
            orbCellMO = (OrbCellMO) CellMOFactory.loadCellMO(cellType, 
	        center, (float) size, username, callID, simulateCalls, vp, bystanders);
	} else {
            orbCellMO = (OrbCellMO) CellMOFactory.loadCellMO(cellType, 
	        center, (float) size, username, callID, simulateCalls);
	}

	if (orbCellMO == null) {
	    logger.warning("Unable to spawn orb");
	    return;
	}

	cellID = orbCellMO.getCellID();

	try {
            orbCellMO.setServerState(new OrbCellServerState());
        } catch (ClassCastException e) {
            logger.warning("Error setting up new cell " +
                orbCellMO.getName() + " of type " +
                orbCellMO.getClass() + e.getMessage());
            return;
        }

	try {
	    CellManagerMO.getCellManager().insertCellInWorld(orbCellMO);
	} catch (MultipleParentException e) {
	    logger.warning("Can't insert orb in world:  " + e.getMessage());
	    return;
	}
	
	orbCellMORef = AppContext.getDataManager().createReference(orbCellMO);
    }

    public void setUsername(String username) {
	this.username = username;

	orbCellMORef.get().setUsername(username);
    }

    public void setBystanders(String[] bystanders) {
	orbCellMORef.get().setBystanders(bystanders);
    }

    public String getID() {
	return id;
    }

    public CellID getCellID() {
	return cellID;
    }

    public OrbCellMO getOrbCellMO() {
	return orbCellMORef.get();
    }

    public VirtualPlayer getVirtualPlayer() {
	return vp;
    }

    public int addToUseCount(int n) {
	useCount += n;
	return useCount;
    }

    public void addComponent(CellComponentMO component) {
	getOrbCellMO().addComponent(component);
    }

    public void done() {
	if (orbCellMORef == null) {
	    return;
	}

	OrbCellMO orbCellMO = orbCellMORef.get();

	orbCellMORef = null;

	CellManagerMO.getCellManager().removeCellFromWorld(orbCellMO);

	orbCellMO.endCall();
    }

    public String toString() {
	return username;
    }

}
