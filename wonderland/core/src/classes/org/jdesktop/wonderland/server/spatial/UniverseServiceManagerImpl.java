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
package org.jdesktop.wonderland.server.spatial;

import com.jme.bounding.BoundingVolume;
import com.sun.sgs.kernel.KernelRunnable;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.TransformChangeListenerSrv;
import org.jdesktop.wonderland.server.spatial.ViewUpdateListener;
import org.jdesktop.wonderland.server.cell.view.ViewCellMO;

/**
 *
 * @author paulby
 */
public class UniverseServiceManagerImpl implements UniverseManager {

    private UniverseService service;

    public UniverseServiceManagerImpl(UniverseService service) {
        this.service = service;
    }

    public void addChild(CellMO parent, CellMO child) {
        service.addChild(parent, child);
    }

    public void createCell(CellMO cellMO, boolean notify) {
        service.createCell(cellMO, notify);
    }

    public void revalidateCell(CellMO cellMO) {
        service.revalidateCell(cellMO);
    }

    public void removeCell(CellMO cellMO) {
        service.removeCell(cellMO);
    }

    public void removeChild(CellMO parent, CellMO child) {
        service.removeChild(parent, child);
    }

    public void addRootToUniverse(CellMO rootCellMO) {
        service.addRootToUniverse(rootCellMO);
    }

    public void removeRootFromUniverse(CellMO rootCellMO) {
        service.removeRootFromUniverse(rootCellMO);
    }

    public void setLocalTransform(CellMO cell, CellTransform localTransform) {
        service.setLocalTransform(cell, localTransform);
    }

    public CellTransform getWorldTransform(CellMO cell, CellTransform result) {
        return service.getWorldTransform(cell, result);
    }

    public BoundingVolume getWorldBounds(CellMO cell, BoundingVolume result) {
        return service.getWorldBounds(cell, result);
    }

    public void viewLogin(ViewCellMO viewCell) {
        service.viewLogin(viewCell);
    }

    public void viewRevalidate(ViewCellMO viewCell) {
        service.viewRevalidate(viewCell);
    }

    public void viewLogout(ViewCellMO viewCell) {
        service.viewLogout(viewCell);
    }

    public void addTransformChangeListener(CellMO cell, TransformChangeListenerSrv listener) {
        service.addTransformChangeListener(cell, listener);
    }

    public void removeTransformChangeListener(CellMO cell, TransformChangeListenerSrv listener) {
        service.removeTransformChangeListener(cell, listener);
    }

    public void addViewUpdateListener(CellMO cell, ViewUpdateListener viewUpdateListener) {
        service.addViewUpdateListener(cell, viewUpdateListener);
    }

    public void removeViewUpdateListener(CellMO cell, ViewUpdateListener viewUpdateListener) {
        service.removeViewUpdateListener(cell, viewUpdateListener);
    }

    public void scheduleOnTransaction(Runnable runnable) {
        service.scheduleOnTransaction(runnable);
    }

    public void scheduleTask(UniverseKernelRunnable task) {
        service.scheduleTask(task);
    }

    public void runTxnRunnable(UniverseTxnRunnable runnable) {
        service.runTxnRunnable(runnable);
    }

    public void addCellListener(CellMOListener listener) {
        service.addCellListener(listener);
    }

    public void removeCellListener(CellMOListener listener) {
        service.removeCellListener(listener);
    }
}
