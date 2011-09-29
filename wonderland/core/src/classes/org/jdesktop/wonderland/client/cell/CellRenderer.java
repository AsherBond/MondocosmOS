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
package org.jdesktop.wonderland.client.cell;

import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.CellTransform;

/**
 *
 * Provides an interface to the cell rendering code. This abstraction allows
 * for both 2D and 3D renderers for a cells content.
 * 
 * TODO Add Heuristic based LOD listeners
 * TODO Add view distance listeners
 * TODO Frustum enter/exit listeners
 * 
 * @author paulby
 */
@ExperimentalAPI
public interface CellRenderer {
    
    /**
     * The cell has moved, the transform is the cell position in world coordinates
     * @param localTransfrm the cell local transform
     */
    public void cellTransformUpdate(CellTransform localTransform);

    /**
     * Notify the renderer of a cell status change
     * @param status
     */
    public void setStatus(CellStatus status,boolean increasing);

    /**
     * Return the current status of the CellRenderer
     * @return
     */
    public CellStatus getStatus();
}
