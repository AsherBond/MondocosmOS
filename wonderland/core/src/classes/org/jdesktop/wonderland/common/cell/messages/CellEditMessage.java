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
import org.jdesktop.wonderland.common.messages.Message;

/**
 * Message sent to edit the cell hierarchy.  This message cannot be
 * used directly, but is the superclass of a number of other specific
 * messages.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
public abstract class CellEditMessage extends Message {

    /** The message type */
    private EditType editType;
    
    /** Enumeration of kinds of editing */
    public enum EditType {
        CREATE_CELL, DELETE_CELL, DUPLICATE_CELL, REPARENT_CELL
    };
    
    /**
     * Create a new cell message to the given cellID of the parent.
     * 
     * @param parentID the id of the parent cell
     */
    protected CellEditMessage(EditType editType) {
        this.editType = editType;
    }

    public EditType getEditType() {
        return editType;
    }

    public void setEditType(EditType editType) {
        this.editType = editType;
    }
}
