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
package org.jdesktop.wonderland.modules.palette.client.dnd;

import java.awt.datatransfer.DataFlavor;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.utils.CellCreationException;
import org.jdesktop.wonderland.client.cell.utils.CellUtils;
import org.jdesktop.wonderland.client.jme.dnd.spi.DataFlavorHandlerSPI;
import org.jdesktop.wonderland.client.jme.input.DropTargetDropEvent3D;
import org.jdesktop.wonderland.client.jme.input.DropTargetEvent3D;
import org.jdesktop.wonderland.common.cell.state.CellServerState;

/**
 * Handles data flavors of serialized CellServerState classes, registered with
 * the drag-and-drop manager when items are dragged from the Cell Palette into
 * the world.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class CellPaletteDataFlavorHandler implements DataFlavorHandlerSPI {

    private final DataFlavor dataFlavor = new DataFlavor(CellServerState.class, "CellServerState");

    /**
     * @inheritDoc()
     */
    public DataFlavor[] getDataFlavors() {
        return new DataFlavor[] { dataFlavor };
    }

    /**
     * @inheritDoc()
     */
    public boolean accept(DropTargetEvent3D dtde, DataFlavor flavor) {
        // just accept everything passed our way
        return true;
    }

    public String getFileExtension(DropTargetEvent3D dtde, DataFlavor flavor) {
        return null;
    }

    /**
     * @inheritDoc()
     */
    public void handleDrop(DropTargetDropEvent3D dtde, DataFlavor flavor) {
        try {
            // Fetch the CellServerState from the dropped transferable and
            // create an instance of the cell on the server
            CellServerState state = (CellServerState) dtde.getTransferData(dataFlavor);

            // Create the new cell at a distance away from the avatar
            CellUtils.createCell(state);

        } catch (CellCreationException ex) {
            Logger.getLogger(CellPaletteDataFlavorHandler.class.getName()).log(Level.WARNING, null, ex);
        }
    }

    /**
     * @inheritDoc()
     */
    public void handleImport(DropTargetDropEvent3D dtde, DataFlavor flavor, ImportResultListener listener) {
        // import not supported
        listener.importFailure(null, "Import not supported");
    }
}
