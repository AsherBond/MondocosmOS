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
package org.jdesktop.wonderland.server.cell.view;

import org.jdesktop.wonderland.server.cell.*;
import com.jme.bounding.BoundingVolume;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.server.cell.annotation.DependsOnCellComponentMO;

/**
 * ViewCell defines the view into the virtual world. Each view cell is
 * associated with a particular cell cache object. Any time the view
 * cell moves, the associated cache will be updated with the set of cells
 * that are in range or out of range of the view cell.  The cache can then
 * send those messages to an associated client or can do other things like
 * record the messages
 * 
 * @author paulby
 */
@ExperimentalAPI
@DependsOnCellComponentMO(MovableComponentMO.class)
public abstract class ViewCellMO extends CellMO {
    public ViewCellMO() {
        super();
    }
    
    public ViewCellMO(BoundingVolume localBounds, CellTransform transform) {
        super(localBounds, transform);
    }

    /**
     * Return the cell cache managed object for this view, or null if there
     * is no associated cache.
     * 
     * @return the cell cache for this view, or null
     */
    public abstract ViewCellCacheMO getCellCache();
}
