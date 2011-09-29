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
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.security.ChildrenAction;
import org.jdesktop.wonderland.common.cell.security.ModifyAction;
import org.jdesktop.wonderland.common.security.annotation.Actions;

/**
 * Message sent to change the parent of a cell.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
@Actions({ModifyAction.class, ChildrenAction.class})
public class CellReparentMessage extends CellEditMessage {

    private CellID parentCellID = null;
    private CellID cellID = null;
    private CellTransform childTransform = null;
    
    /**
     * Create a reparent message to the given cellID of the cell and its new
     * parent.
     *
     * @param cellID the id of the cell
     * @param parentID the id of the new parent cell
     */
    public CellReparentMessage(CellID cellID, CellID parentCellID, CellTransform childTransform) {
        super(EditType.REPARENT_CELL);
        this.cellID = cellID;
        this.parentCellID = parentCellID;
        this.childTransform = childTransform;
    }

    /**
     * Returns the ID of the cell.
     *
     * @return the cellID
     */
    public CellID getCellID() {
        return cellID;
    }

    /**
     * Returns the ID of the cell of the parent
     * 
     * @return the parent cellID
     */
    public CellID getParentCellID() {
        return parentCellID;
    }

    /**
     * Return the new child transform, can be null
     * @return
     */
    public CellTransform getChildCellTransform() {
        return childTransform;
    }
}
