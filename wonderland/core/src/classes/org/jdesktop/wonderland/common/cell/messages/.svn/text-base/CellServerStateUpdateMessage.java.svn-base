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

import java.util.Set;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.security.ModifyAction;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.security.annotation.Actions;

/**
 * Message sent to update a cell and cell component server state for a cell
 * given its unique ID. This method is used to update the cell server state
 * and individual cell component server states.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
@Actions(ModifyAction.class)
public class CellServerStateUpdateMessage extends CellMessage {
    private CellServerState cellState = null;
    private Set<CellComponentServerState> componentStateSet = null;

    /**
     * Constructor that takes the unique ID of the cell and the server state
     * (used to set the state) and a set of cell component server states.
     * 
     * @param cellID The id of the cell
     * @param serverState state
     */
    public CellServerStateUpdateMessage(CellID cellID,
            CellServerState cellState,
            Set<CellComponentServerState> componentStateSet) {

        super(cellID);

        this.cellState = cellState;
        this.componentStateSet = componentStateSet;
    }

    /**
     * Get the server state from this message. If this server state is null,
     * then the cell's server state object should not be updated (although
     * some of its component server state's may still be updated.
     *
     * @return the cell server state
     */
    public CellServerState getCellServerState() {
        return cellState;
    }

    /**
     * Get the set of cell component server states from this message. If this
     * set is null (or empty), then no cell component states are updated. Only
     * the states in this set are updated; all other cell component server
     * states are left alone.
     *
     * @return the set of cell component server states
     */
    public Set<CellComponentServerState> getCellComponentServerStateSet() {
        return componentStateSet;
    }
}
