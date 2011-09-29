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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellStatus;

/**
 * Global services for client cells. The manager will only
 * report information on currently loaded Cells.
 * 
 * @author paulby
 */
@ExperimentalAPI
public class CellManager {

    private static CellManager cellManager = null;
    
//    private Set<CellMoveListener> moveListeners=Collections.synchronizedSet(new LinkedHashSet());            
;

    private Set<CellStatusChangeListener> statusChangeListeners = new CopyOnWriteArraySet();
    
    private CellManager() {
    }
    
    public static CellManager getCellManager() {
        if (cellManager==null)
            cellManager = new CellManager();
        return cellManager;
    }

    /**
     * Notify the cellStatusChangeListeners that the cell status has been updated.
     * @param cell
     * @param status
     */
    void notifyCellStatusChange(Cell cell, CellStatus status) {
        for(CellStatusChangeListener listener : statusChangeListeners)
            listener.cellStatusChanged(cell, status);
    }
    
    /**
     * Add a CellStatusChangeListener for notification of the change of status
     * of any cell in the system. Listeners are notifed prior to the status change
     * actually taking place.
     * @param listener to be added
     */
    public void addCellStatusChangeListener(CellStatusChangeListener listener) {
        statusChangeListeners.add(listener);
    }
    
    /**
     * Remove the CellStatusChangeListener
     * @param listener to be removed
     */
    public void removeCellStatusChangeListener(CellStatusChangeListener listener) {
        statusChangeListeners.remove(listener);
    }
    
}
