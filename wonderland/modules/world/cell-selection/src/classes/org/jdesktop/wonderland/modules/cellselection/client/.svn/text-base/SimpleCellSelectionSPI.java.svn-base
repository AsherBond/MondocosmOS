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
package org.jdesktop.wonderland.modules.cellselection.client;

import java.util.Set;
import javax.swing.JFrame;
import org.jdesktop.wonderland.client.cell.registry.CellRegistry;
import org.jdesktop.wonderland.client.cell.registry.spi.CellFactorySPI;
import org.jdesktop.wonderland.client.cell.utils.CellCreationException;
import org.jdesktop.wonderland.client.cell.utils.spi.CellSelectionSPI;
import org.jdesktop.wonderland.client.jme.JmeClientMain;

/**
 * A simple implementation of CellSelectionSPI that presents a dialog box that
 * queries for the proper Cell to use if more than one Cell supports the given
 * extension.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class SimpleCellSelectionSPI implements CellSelectionSPI {

    /**
     * @{@inheritDoc}
     */
    public CellFactorySPI getCellSelection(String extension) throws CellCreationException {
        // Find the list of Cells that support the given extension. If none,
        // then throw an exception.
        CellRegistry registry = CellRegistry.getCellRegistry();
        Set<CellFactorySPI> factorySet = registry.getCellFactoriesByExtension(extension);
        if (factorySet == null || factorySet.size() == 0) {
            throw new CellCreationException();
        }

        // If there is only one, then just return it, since there is no choices
        // to be made.
        if (factorySet.size() == 1) {
            return factorySet.iterator().next();
        }

        // Otherwise, present a choice to the user. Currently, this selection
        // is not remembered by the system (although future implementations
        // can remember it). We also need to place this in the AWT Event Thread
        // since we are displaying a dialog, but need to wait for its completion
        JFrame frame = JmeClientMain.getFrame().getFrame();
        CellSelectionJDialog dialog = new CellSelectionJDialog(frame, true);
        dialog.setFactorySet(factorySet);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);

        // Wait for the dialog to return. If the status is cancel, simply return
        // null.
        int result = dialog.getReturnStatus();
        if (result == CellSelectionJDialog.RET_CANCEL) {
            return null;
        }

        // Otherwise, fetch the selected Cell factory and return it
        return dialog.getSelectedCellFactory();
    }
}
