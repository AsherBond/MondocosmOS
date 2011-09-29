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

import java.util.Set;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.messages.MessageID;
import org.jdesktop.wonderland.common.wfs.WorldRoot;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

/**
 * Implementation of CellExportManager.  This just forwards everything to
 * the service.
 * @author jkaplan
 * @author Bernard Horan
 */
public class CellExportManagerImpl implements CellExportManager {
    private CellExportService service;

    public CellExportManagerImpl(CellExportService service) {
        this.service = service;
    }

    public void createSnapshot(String name, SnapshotCreationListener listener) {
        service.createSnapshot(name, listener);
    }

    public void exportCells(WorldRoot worldRoot, Set<CellID> cellIDs,
                            CellExportListener listener, boolean recordCellIDs)
    {
        service.exportCells(worldRoot, cellIDs, listener, recordCellIDs);
    }

    public void createRecording(String name, Set<CellID> cells, RecordingCreationListener listener) {
        service.createRecording(name, cells, listener);
    }

    public void listRecordings(MessageID messageID, WonderlandClientSender sender, WonderlandClientID clientID, ListRecordingsListener listener) {
        service.listRecordings(messageID, sender, clientID, listener);
    }
}
