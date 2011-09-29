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

import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * Interface to a specific implementation of a cell creation parent. A
 * CellCreationParentSPI implementation determines what cell should be
 * used as a newly-created cell's parent cell.
 * 
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
@ExperimentalAPI
public interface CellCreationParentSPI {
    /**
     * Determine the current parent cell. Any time a cell creation is
     * requested, all CellCreationParentSPI implementations will be'
     * queried in registration order.  The first non-null return value
     * will be used as the cell's parent.  If there are no
     * CellCreationParentSPIs or all of them return null, then the
     * cell will be rooted at the origin.
     * @return the cell to use as a parent for this cell creation
     */
    public Cell getCellCreationParent();
}
