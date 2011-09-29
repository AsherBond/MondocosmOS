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
package org.jdesktop.wonderland.client.cell;

import java.util.Collection;
import org.jdesktop.wonderland.client.cell.view.ViewCell;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellID;

/**
 * The defining interface for the client Cell Cache
 * 
 * @author paulby
 */
@ExperimentalAPI
public interface CellCache {

    /**
     * Return the cell with the specified id, or null if no such cell exists.
     *
     * @param cellId the id of the cell to return
     * @return the cell with the specified ID.
     */
    public Cell getCell(CellID cellId);

    /**
     * Get the singleton environment cell for the world represented by
     * this cache
     *
     * @return the environment cell, or null if there is no environment cell
     */
    public EnvironmentCell getEnvironmentCell();

    /**
     * Return the session with which this cell cache is associated. 
     * 
     * @return the session for this cache
     */
    public WonderlandSession getSession();
    
    /**
     * Set the view cell for this cache
     * 
     * @param viewCell the view cell
     */
    public void setViewCell(ViewCell viewCell);
    
    /**
     * Returns the ViewCell for this cache
     */
    public ViewCell getViewCell();
    
    /**
     * Returns a collection of root cells in this cache. The returned collection
     * is a copy of the internal collection.
     * 
     * @return the RootCells for this cache
     */
    public Collection<Cell> getRootCells();
    
    /**
     * Get the CellChannelConnection
     * @return the CellChannelConnection for this CellCache
     */
    public CellChannelConnection getCellChannelConnection();

    /**
     * Add a cell cache listener
     * @param listener the listener to add
     */
    public void addCellCacheListener(CellCacheListener listener);

    /**
     * Remove a cell cache listener
     * @param listener the listener to remove
     */
    public void removeCellCacheListener(CellCacheListener listener);

    /**
     * Get the CellStatistics for cells in this cache
     * @return the cell statistics
     */
    public CellStatistics getStatistics();

    /**
     * A listener that will be notified when cells are loaded or unloaded.
     */
    public interface CellCacheListener {
        /**
         * Notification that a cell has been loaded.  This will be sent
         * after the cell has been loaded, and its client state has been
         * set, but before the cell's status has been changed from DISK
         * to anything else.
         * @param cellID the id of the loaded cell
         * @param cell the cell that was loaded
         */
        public void cellLoaded(CellID cellID, Cell cell);

        /**
         * Notification that a cell failed to load.
         * @param cellID the id of the cell that didn't load
         * @param className the name of the cell that didn't load
         * @param parentCellID the id of the cell's parent
         * @param cause the reason the cell didn't load
         */
        public void cellLoadFailed(CellID cellID, String className,
                                   CellID parentCellID, Throwable cause);

        /**
         * Notification that a cell was unloaded.  This will be called after
         * the cell has been unloaded, but before its status has been changed.
         * @param cellID the id of the unloaded cell
         * @param cell the cell that was unloaded
         */
        public void cellUnloaded(CellID cellID, Cell cell);

    }
}
