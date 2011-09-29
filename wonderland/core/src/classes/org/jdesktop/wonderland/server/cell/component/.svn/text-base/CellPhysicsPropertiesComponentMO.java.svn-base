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
package org.jdesktop.wonderland.server.cell.component;

import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.component.state.CellPhysicsPropertiesComponentClientState;
import org.jdesktop.wonderland.common.cell.component.state.CellPhysicsPropertiesComponentServerState;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.server.cell.*;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;

/**
 *
 * @author paulby
 */
public class CellPhysicsPropertiesComponentMO extends CellComponentMO {

    private CellPhysicsPropertiesComponentServerState physicsState = new CellPhysicsPropertiesComponentServerState();

    public CellPhysicsPropertiesComponentMO(CellMO cell) {
        super(cell);
    }


    @Override
    protected String getClientClass() {
        return "org.jdesktop.wonderland.client.cell.component.CellPhysicsPropertiesComponent";
    }

    @Override
    public CellComponentClientState getClientState(CellComponentClientState clientState,
            WonderlandClientID clientID,
            ClientCapabilities capabilities) {

        // If the given cellClientState is null, create a new one
        if (clientState == null) {
            clientState = new CellPhysicsPropertiesComponentClientState();
        }

        physicsState.getClientState(clientState);

        return clientState;
    }

    @Override
    public void setServerState(CellComponentServerState state) {
        physicsState = (CellPhysicsPropertiesComponentServerState) state;
    }

    /**
     * Returns the server state information currently configured in the
     * component. If the state argument is non-null, fill in that object and
     * return it. If the state argument is null, create a new state object.
     *
     * @param state The state object, if null, creates one.
     * @return The current server state information
     */
    public CellComponentServerState getServerState(CellComponentServerState state) {
        CellPhysicsPropertiesComponentServerState ret = (CellPhysicsPropertiesComponentServerState) state;

        return physicsState.clone(ret);
    }
}
