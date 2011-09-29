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
package org.jdesktop.wonderland.common.cell.messages;

import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.common.cell.CellID;

/**
 * Message informing a client to unload a cell from memory. Data associated with
 * unloaded cells remains in the asset manager disk cache. Use DELETE_CELL to
 * delete related assets.
 * 
 * @author paulby
 */
@InternalAPI
public class CellHierarchyUnloadMessage extends CellHierarchyMessage {
    
    /**
     * Creates a new instance of CellHierarchyMessage 
     *
     * 
     * @param cellClassName Fully qualified classname for cell
     */
    public CellHierarchyUnloadMessage(CellID cellID) {
        this.cellID = cellID;
        msgType = ActionType.UNLOAD_CELL;
    }
    
    public CellHierarchyUnloadMessage() {
        msgType = ActionType.UNLOAD_CELL;
    }
}
