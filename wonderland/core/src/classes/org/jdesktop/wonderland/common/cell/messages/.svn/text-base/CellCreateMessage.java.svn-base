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
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.security.annotation.Actions;

/**
 * Message sent to add a cell hierarchy.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
@Actions(ChildrenAction.class)
public class CellCreateMessage extends CellEditMessage {
    /** The ID of the cell of the parent */
    private CellID parentCellID;
   
    /** A class of the setup information */
    private CellServerState setup;
    
    /**
     * Create a new cell message to the given cellID of the parent and uri of
     * asset to associated with the new cell.
     * 
     * @param parentID the id of the parent cell
     */
    public CellCreateMessage(CellID parentCellID, CellServerState setup) {
        super(EditType.CREATE_CELL);
        this.parentCellID = parentCellID;
        this.setup = setup;
    }
    
    /**
     * Get the ID of the cell of the parent
     * 
     * @return the parent cellID
     */
    public CellID getParentCellID() {
        return this.parentCellID;
    }
    
    public CellServerState getCellSetup() {
        return setup;
    }

    public void setCellSetup(CellServerState setup) {
        this.setup = setup;
    }
}
