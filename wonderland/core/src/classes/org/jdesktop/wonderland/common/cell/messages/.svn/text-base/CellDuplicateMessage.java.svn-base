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

import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.security.ChildrenAction;
import org.jdesktop.wonderland.common.security.annotation.Actions;

/**
 * Message sent to duplicate a cell hierarchy.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
@Actions(ChildrenAction.class)
public class CellDuplicateMessage extends CellEditMessage {
    /** The ID of the cell to duplicate */
    private CellID cellID;

    /** The new name of the duplicated cell */
    private String cellName = "";

    /**
     * Create a new cell delete message for the given cellID.
     * 
     * @param cellID the id of the cell
     */
    public CellDuplicateMessage(CellID cellID, String cellName) {
        super(EditType.DUPLICATE_CELL);
        this.cellID = cellID;
        this.cellName = cellName;
    }
    
    /**
     * Get the ID of the cell
     * 
     * @return The unique cell ID
     */
    public CellID getCellID() {
        return this.cellID;
    }

    /**
     * Returns the desired name of the new cell.
     *
     * @return The name of the new (duplicated) cell
     */
    public String getCellName() {
        return cellName;
    }
}
