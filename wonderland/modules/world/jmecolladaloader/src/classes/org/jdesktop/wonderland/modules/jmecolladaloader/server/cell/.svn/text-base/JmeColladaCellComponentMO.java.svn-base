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

package org.jdesktop.wonderland.modules.jmecolladaloader.server.cell;

import java.util.logging.Logger;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.ComponentLookupClass;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.modules.jmecolladaloader.common.cell.state.JmeColladaCellComponentClientState;
import org.jdesktop.wonderland.modules.jmecolladaloader.common.cell.state.JmeColladaCellComponentServerState;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.ModelCellComponentMO;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;

/**
 *
 * @author paulby
 */
@ComponentLookupClass(ModelCellComponentMO.class)
public class JmeColladaCellComponentMO extends ModelCellComponentMO {

    public JmeColladaCellComponentMO(CellMO cell) {
        super(cell);
    }

    @Override
    protected String getClientClass() {
        return "org.jdesktop.wonderland.modules.jmecolladaloader.client.cell.JmeColladaCellComponent";
    }

    @Override
    public void setServerState(CellComponentServerState state) {
        if (!(state instanceof JmeColladaCellComponentServerState)) {
            Logger.getLogger(this.getClass().getName()).warning("Incorrect server state passed to setServerState "+state.getClass().getName());
            return;
        }
        this.serverState = (JmeColladaCellComponentServerState) state;
    }

    @Override
    public CellComponentServerState getServerState(CellComponentServerState state) {
        return serverState.clone(state);
    }

    @Override
    public CellComponentClientState getClientState(CellComponentClientState state,
            WonderlandClientID clientID,
            ClientCapabilities capabilities) {

        // If the given cellClientState is null, create a new one
        if (state == null) {
            state = new JmeColladaCellComponentClientState();
        }

        ((JmeColladaCellComponentServerState)serverState).setClientState((JmeColladaCellComponentClientState)state);

        return state;
    }
}
