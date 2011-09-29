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
import org.jdesktop.wonderland.common.cell.security.ModifyAction;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.security.annotation.Actions;

/**
 * Message sent to set the cell server state for a cell given its
 * unique ID.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
@Actions(ModifyAction.class)
public class CellServerStateSetMessage extends CellMessage {
    private CellServerState serverState;

    /**
     * Constructor that takes the unique ID of the cell and the server state
     * (used to set the state).
     * 
     * @param cellID The id of the cell
     * @param serverState state
     */
    public CellServerStateSetMessage(CellID cellID, CellServerState serverState) {
        super(cellID);

        this.serverState = serverState;
    }

    /**
     * Get the server state from this message
     * @return the cell server state
     */
    public CellServerState getCellServerState() {
        return serverState;
    }
}
