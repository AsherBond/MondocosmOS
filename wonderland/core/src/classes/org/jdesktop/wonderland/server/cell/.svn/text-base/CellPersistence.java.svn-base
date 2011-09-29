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
package org.jdesktop.wonderland.server.cell;

import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.NameNotBoundException;
import com.sun.sgs.app.util.ScalableHashSet;
import java.util.Set;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.server.spatial.UniverseManager;

/**
 * A class that manages the long-term persistence of Wonderland cells using
 * cell importers and cell exporters to read from WFS.
 * <p>
 * This internal API is used from the Universe service to reload cells during
 * a warm start, or write them out during a cold start.
 * @author jkaplan
 */
@InternalAPI
public class CellPersistence {
    private static Logger logger =
            Logger.getLogger(CellPersistence.class.getName());

    /** singleton.  Use getInstance() instead */
    public CellPersistence() {
    }

    /**
     * Get the set of all CellIds to reload. The returned set guarantees that
     * the iterator over the set is Serializable, so the reloading can be
     * split across multiple transactions safely.  It is assumed that the set
     * of cells will not change during the course of iterating over the
     * CellIDs.
     * @return the set of cell IDs, as a ScalableHashSet or similar set with
     * a serializable, transaction safe iterator
     */
    public Set<CellID> getRootCellIDs() {
        try {
            return CellManagerMO.getCellManager().getRootCells();
        } catch (NameNotBoundException nnbe) {
            // no cell manager has been created.  This is fine, it means
            // the CellManager has never been initialized, so no cells
            // have been loaded.  We just return an empty set of cellIDs
            // in this case
            return new ScalableHashSet<CellID>();
        }
    }

    /**
     * Reload a root cell into the given universe
     * @param cellID the cellID of the root cell to load
     * @param universe the universe to load the cell into
     * @param dataService the data service to use when loading the cell
     * @return true if the cell was found and reloaded, or false if not
     */
    public boolean reloadCell(CellID cellID, UniverseManager universe) {
        // resolve the cell ID into an actual cell
        CellMO cell = CellManagerMO.getCell(cellID);
        if (cell == null) {
            return false;
        }

        // insert the cell and all its children into the universe
        doInsert(cell, universe);

        // record a new root cell with the universe
        if (!cellID.equals(CellID.getEnvironmentCellID())) {
            universe.addRootToUniverse(cell);
        }

        // all set
        return true;
    }

    /**
     * Insert a cell and all of its children into the universe
     * @param cell the cell to insert
     */
    void doInsert(CellMO cell, UniverseManager universe) {
        if (!cell.isLive()) {
            return;
        }

        // add this cell to the universe
        cell.addToUniverse(universe, false);

        // now update all children
        for (ManagedReference<CellMO> childRef : cell.getAllChildrenRefs()) {
            doInsert(childRef.get(), universe);
        }
    }
}
