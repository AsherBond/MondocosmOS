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
package org.jdesktop.wonderland.client.cell.utils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.utils.spi.CellCreationParentSPI;

/**
 * A registry for CellCreationParentSPI objects.
 *
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class CellCreationParentRegistry {
    private static Logger logger =
            Logger.getLogger(CellCreationParentRegistry.class.getName());

    /** the list of registered spi objects */
    private final List<CellCreationParentSPI> spiList =
            new CopyOnWriteArrayList<CellCreationParentSPI>();


    /**
     * Add a cell creation parent
     * @param spi the spi to add
     */
    public static void register(CellCreationParentSPI spi) {
        getInstance().add(spi);
    }

    /**
     * Remove a cell creation parent
     * @param spi the spi to remove
     */
    public static void unregister(CellCreationParentSPI spi) {
        getInstance().remove(spi);
    }

    /**
     * Get the current creation parent
     * @return the cell to use as a parent for cell creation, or null
     * to use the root
     */
    public static Cell getCellCreationParent() {
        return getInstance().getParent();
    }

    /**
     * Get the singleton registry instance
     * @return the singleton instance
     */
    private static CellCreationParentRegistry getInstance() {
        return RegistrySingleton.INSTANCE;
    }

    /**
     * Singleton implementation
     */
    CellCreationParentRegistry() {}

    /**
     * Add an SPI object
     * @param spi the spi object
     */
    private void add(CellCreationParentSPI spi) {
        // issue 783: add to the start of the list rather than the end
        spiList.add(0, spi);
    }

    /**
     * Remove an SPI object
     * @param spi the spi object
     */
    private void remove(CellCreationParentSPI spi) {
        spiList.remove(spi);
    }

    /**
     * Get the parent by iterating through SPI objects until a non-null
     * result is found.
     * @return the cell parent, or null if no SPI objects returned a value
     */
    private Cell getParent() {
        Cell out = null;

        for (CellCreationParentSPI spi : spiList) {
            out = spi.getCellCreationParent();
            if (out != null) {
                return out;
            }
        }

        return out;
    }

    private static final class RegistrySingleton {
        private static final CellCreationParentRegistry INSTANCE =
                new CellCreationParentRegistry();
    }
}
