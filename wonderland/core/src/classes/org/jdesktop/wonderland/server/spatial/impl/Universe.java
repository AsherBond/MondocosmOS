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
package org.jdesktop.wonderland.server.spatial.impl;

import org.jdesktop.wonderland.server.cell.TransformChangeListenerSrv;
import org.jdesktop.wonderland.server.spatial.ViewUpdateListener;
import org.jdesktop.wonderland.server.spatial.*;
import com.sun.sgs.auth.Identity;
import java.math.BigInteger;
import org.jdesktop.wonderland.common.cell.CellID;

/**
 *
 * @author paulby
 */
public interface Universe {

    /**
     * Add a SpatialCell (and all it's children) to the universe
     * @param cell
     */
    public void addRootSpatialCell(CellID cellID, Identity identity);

    public void addTransformChangeListener(CellID cellID, TransformChangeListenerSrv listener);

    public void addViewUpdateListener(CellID cellID, ViewUpdateListener viewUpdateListener);

    /**
     * Remove the root cell (and all it's children)
     * @param cell
     */
//    public void removeRootSpatialCell(CellID cellID, Identity identity);

    /**
     * Create a Spatial cell
     * @param id
     * @param cellCacheId the id of the view cell cache if the cell is a ViewCell, otherwise null
     * @param identity
     * @return
     */
    public SpatialCell createSpatialCell(CellID id, BigInteger dsID, Class cellClass);

    public void revalidateCell(CellID id);

    public void removeCell(CellID id);

    public SpatialCell getSpatialCell(CellID cellID);

    public void removeTransformChangeListener(CellID cellID, TransformChangeListenerSrv listener);

    public void removeViewUpdateListener(CellID cellID, ViewUpdateListener viewUpdateListener);

    public void viewLogin(CellID viewCellId, BigInteger cellCacheId, Identity identity);

    public void viewRevalidate(CellID viewCellId);

    public void viewLogout(CellID viewCellId, Identity identity);

}
