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
package org.jdesktop.wonderland.client.cell.utils.spi;

import org.jdesktop.wonderland.client.cell.registry.spi.CellFactorySPI;
import org.jdesktop.wonderland.client.cell.utils.CellCreationException;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * An interface that determine which Cell to use for creation. This is used
 * if more than one Cell exists to support a given extension.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
public interface CellSelectionSPI {
    /**
     * Returns the Cell factory that creates the Cell based upon the given
     * extension. It is up to this method to determine the proper Cell factory
     * if the system supports more than one Cell per file extension type. Returns
     * null if the action was Canceled and throws CellCreationException if not
     * Cell type supports the extension.
     *
     * @param extension The file extension
     * @return A CellFactorySPI to use that supports the given extension
     * @throw CellCreationException If no Cell supports the given extension
     */
    public CellFactorySPI getCellSelection(String extension) throws CellCreationException;
}
