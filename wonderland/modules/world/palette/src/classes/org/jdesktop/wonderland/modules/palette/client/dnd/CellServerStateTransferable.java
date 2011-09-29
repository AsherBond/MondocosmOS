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
package org.jdesktop.wonderland.modules.palette.client.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import org.jdesktop.wonderland.client.cell.registry.spi.CellFactorySPI;
import org.jdesktop.wonderland.common.cell.state.CellServerState;

/**
 * A Transferable used for drag-and-drop that corresponds to a serialized object
 * for CellServerState.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class CellServerStateTransferable implements Transferable {

    private CellFactorySPI cellFactory = null;
    private boolean cellServerStateSet = false;
    private CellServerState cellServerState = null;
    private static DataFlavor dataFlavor = new DataFlavor(CellServerState.class, "CellServerState");

    /**
     * Constructor, takes the Cell Factory as an argument
     */
    public CellServerStateTransferable(CellFactorySPI cellFactory) {
        this.cellFactory = cellFactory;
    }

    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { dataFlavor };
    }

    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return dataFlavor.equals(flavor);
    }

    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(dataFlavor) == false) {
            throw new UnsupportedFlavorException(flavor);
        }

        // Make sure we only call getDefaultCellServerState() once even if
        // the drag and drop mechanism calls it twice.
        synchronized (cellFactory) {
            if (cellServerStateSet == false) {
                cellServerState = cellFactory.getDefaultCellServerState(null);
                cellServerStateSet = true;
                return cellServerState;
            }
            return cellServerState;
        }
    }
}
