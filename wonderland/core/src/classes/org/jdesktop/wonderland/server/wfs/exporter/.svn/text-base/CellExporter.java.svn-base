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
package org.jdesktop.wonderland.server.wfs.exporter;

import com.sun.sgs.app.AppContext;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.wfs.WorldRoot;
import org.jdesktop.wonderland.server.cell.CellManagerMO;
import org.jdesktop.wonderland.server.wfs.exporter.CellExportManager.CellExportListener;
import org.jdesktop.wonderland.server.wfs.exporter.CellExportManager.CellExportResult;
import org.jdesktop.wonderland.server.wfs.exporter.CellExportManager.SnapshotCreationListener;


/**
 * The CellImporter class is responsible for loading a WFS from the HTTP-based
 * WFS service.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class CellExporter implements Serializable, SnapshotCreationListener,
                                     CellExportListener
{
    /* The logger for the wfs exporter */
    static final Logger logger = Logger.getLogger(CellExporter.class.getName());
    
    /** Default Constructor */
    public CellExporter() {
    }

    /**
     * Export all the cells in the current world to a snapshot with the given
     * name.  Use null to create a snapshot with a default name
     * @param name the name or null
     */
    public void export(String name) {
        // get the export service
        CellExportManager em = AppContext.getManager(CellExportManager.class);

        // first, create a new snapshot.  The rest of the export will happen
        // in the snapshotCreated() method of the listener
        em.createSnapshot(name, this);
    }    
    
    public void snapshotCreated(WorldRoot worldRoot) {
        // Fetch the cell manager and the set of root cells
        CellManagerMO cellManagerMO = CellManagerMO.getCellManager();
        Set<CellID> rootCells = cellManagerMO.getRootCells();

        // Add the environment cell
        rootCells.add(CellID.getEnvironmentCellID());

        // export those cells
        CellExportManager em = AppContext.getManager(CellExportManager.class);
        em.exportCells(worldRoot, rootCells, this, false);
    }

    public void snapshotFailed(String reason, Throwable cause) {
        logger.log(Level.WARNING, "Error creating snapshot: " + reason, cause);
    }

    public void exportResult(Map<CellID, CellExportResult> results) {
        int successCount = 0;
        int errorCount = 0;
        
        for (Map.Entry<CellID, CellExportResult> e : results.entrySet()) {
            CellID id = e.getKey();
            CellExportResult res = e.getValue();
            
            if (res.isSuccess()) {
                successCount++;
            } else {
                errorCount++;
                logger.log(Level.WARNING, "Error exporting " + id + ": " + 
                           res.getFailureReason(), res.getFailureCause());
            }
        }
        
        logger.warning("Exported " + successCount + " cells.  " + errorCount +
                    " errors detected.");
    }
}
