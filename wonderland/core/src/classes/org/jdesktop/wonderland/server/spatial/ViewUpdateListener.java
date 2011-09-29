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
package org.jdesktop.wonderland.server.spatial;

import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellTransform;

/**
 * This listener is used by cells to get notification when a view which is
 * displaying a cell is updaetd
 *
 * @author paulby
 */
public interface ViewUpdateListener {
    /**
     * Notification that a view that is displaying this cell logged in.
     * @param cellID the cell that is being monitored
     * @param viewCellID the view that has this cell in its cache
     */
    public void viewLoggedIn(CellID cell, CellID viewCellID);

    /**
     * This may be called concurrently from multiple threads, so implentations must
     * be thread safe
     * @param cell
     * @param viewCellID
     * @param viewWorldTransform
     */
    public void viewTransformChanged(CellID cell, CellID viewCellID, CellTransform viewWorldTransform);

    /**
     * Notification that a view logged out
     * @param cell the cell that is being monitored
     * @param viewCellID the view cell that logged out
     */
    public void viewLoggedOut(CellID cell, CellID viewCellID);
}
