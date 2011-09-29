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

import com.jme.bounding.BoundingVolume;
import org.jdesktop.wonderland.common.cell.CellID;

/**
 * Listener for the view entering/exiting the proximity bounds of a cell.
 * Enter/Exit is triggered when the origin of the ViewCell enters/exits the
 * bounds of a cell.
 * 
 * @see ProximityComponent for more details
 * 
 * @author paulby
 */
public interface ProximityListener {

    /**
     * The origin of the view cell for this client has entered or exited a proximity bounds
     * @param entered true if this is an enter event, false if its exit
     * @param cell the cell associated with the proximity listener
     * @param proximityVolume the bounding volume entered/exited
     * @param proximityIndex the index of the bounding volume in the ProximityComponent
     */
    public void viewEnterExit(boolean entered, Cell cell, CellID viewCellID, BoundingVolume proximityVolume, int proximityIndex);
    
}
