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
import com.sun.sgs.app.Task;
import java.io.Serializable;
import org.jdesktop.wonderland.common.cell.CellTransform;

/**
 * A Darkstar task used to notify the TransformChangeListeners that an update
 * has occured to a cells transform
 * 
 * @author paulby
 * @deprecated
 */
class TransformChangeNotifierTask implements Task, Serializable {

    private ManagedReference<TransformChangeListenerSrv> listenerRef;
    private ManagedReference<CellMO> cellRef;
    private CellTransform local;
    private CellTransform local2VW;
    private BoundingVolume worldBounds;
    
    TransformChangeNotifierTask(ManagedReference<TransformChangeListenerSrv> listenerRef, 
                                ManagedReference<CellMO> cellRef,
                                CellTransform local, 
                                CellTransform local2VW) {
        this.listenerRef = listenerRef;
        this.local = local;
        this.local2VW = local2VW;
    }
    
    public void run() throws Exception {
        listenerRef.get().transformChanged(cellRef, local, local2VW);
    }

}
