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

import org.jdesktop.wonderland.client.cell.utils.spi.CellSelectionSPI;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * A registry for an object that helps decide which Cell to use upon creation.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
public class CellSelectionRegistry {

    // The sole cell selection spi object
    private CellSelectionSPI cellSelectionSPI = null;

    /**
     * Singleton implementation
     */
    private CellSelectionRegistry() {
    }

    /**
     * The singleton holder class for the CellSelectionRegistry object
     */
    private static final class RegistrySingleton {
        private static final CellSelectionRegistry INSTANCE =
                new CellSelectionRegistry();
    }

    /**
     * Get the singleton registry instance
     * @return the singleton instance
     */
    private static CellSelectionRegistry getInstance() {
        return RegistrySingleton.INSTANCE;
    }

    /**
     * Registers a CellSelectionSPI object to determine which Cell to use for
     * creation. Currently, the system only supports one such object.
     *
     * @param spi The CellSelectionSPI object to add
     */
    public static synchronized void register(CellSelectionSPI spi) {
        getInstance().cellSelectionSPI = spi;
    }

    /**
     * Removes a CellSelectoinSPI object.
     *
     * @param spi The CellSelectionSPI object to remove
     */
    public static synchronized void unregister(CellSelectionSPI spi) {
        if (getInstance().cellSelectionSPI == spi) {
            getInstance().cellSelectionSPI = null;
        }
    }

    /**
     * Returns the CellSelectionSPI object to use to determine which Cell to
     * create or null if none has been set.
     *
     * @return The CellSelectionSPI object
     */
    public static synchronized CellSelectionSPI getCellSelectionSPI() {
        return getInstance().cellSelectionSPI;
    }
}
