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
package org.jdesktop.wonderland.server.cell;

import com.jme.bounding.BoundingVolume;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.service.DataService;
import java.io.Serializable;
import java.math.BigInteger;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.server.spatial.UniverseKernelRunnable;
import org.jdesktop.wonderland.server.spatial.UniverseService;

/**
 * A Darkstar task used to notify the ProximityListeners that an update
 * has occured to a cells transform
 * 
 * @author paulby
 */
class ProximityNotifierTask implements UniverseKernelRunnable, Serializable {

    private BigInteger listenerRefID;
    private boolean enter;
    private BoundingVolume proximityVolume;
    private int proximityIndex;
    private CellID viewCellID;
    private CellID cellID;
    private DataService dataService;
    
    ProximityNotifierTask(ManagedReference<ProximityListenerSrv> listenerRef,
                                boolean enter,
                                  BoundingVolume proximityVolume,
                                  int proximityIndex,
                                  CellID cellID,
                                  CellID viewCellID) {
        this.listenerRefID = listenerRef.getId();
        this.enter = enter;
        this.proximityVolume = proximityVolume;
        this.proximityIndex = proximityIndex;
        this.cellID = cellID;
        this.viewCellID = viewCellID;
    }
    
    public void run() throws Exception {
        ((ProximityListenerSrv)dataService.createReferenceForId(listenerRefID).get()).viewEnterExit(enter, cellID, viewCellID, proximityVolume, proximityIndex);
    }

    public String getBaseTaskType() {
        return UniverseService.class.getName()+"_ProximityNotifierTask";
    }

    public void setDataService(DataService mgr) {
        this.dataService = mgr;
    }

}
