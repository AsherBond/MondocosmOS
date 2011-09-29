/**
 * Open Wonderland
 *
 * Copyright (c) 2011, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */
package org.jdesktop.wonderland.server.cell;

import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.messages.CellClientComponentMessage;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.common.cell.state.InteractionComponentClientState;
import org.jdesktop.wonderland.common.cell.state.InteractionComponentServerState;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;

/**
 * Component that determines how user can interact with a cell. The server class
 * acts as a holder for values that are used in the client.
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
public class InteractionComponentMO extends CellComponentMO {
    private boolean collidable = true;
    private boolean selectable = true;

    public InteractionComponentMO(CellMO cell) {
        super(cell);
    }

    @Override
    public CellComponentClientState getClientState(CellComponentClientState state,
                                                   WonderlandClientID clientID,
                                                   ClientCapabilities capabilities)
    {
        if (state == null) {
            state = new InteractionComponentClientState();
        }

        ((InteractionComponentClientState) state).setCollidable(collidable);
        ((InteractionComponentClientState) state).setSelectable(selectable);

        return super.getClientState(state, clientID, capabilities);
    }

    @Override
    public CellComponentServerState getServerState(CellComponentServerState state) {
        if (state == null) {
            state = new InteractionComponentServerState();
        }

        ((InteractionComponentServerState) state).setCollidable(collidable);
        ((InteractionComponentServerState) state).setSelectable(selectable);

        return super.getServerState(state);
    }

    @Override
    public void setServerState(CellComponentServerState state) {
        collidable = ((InteractionComponentServerState) state).isCollidable();
        selectable = ((InteractionComponentServerState) state).isSelectable();

        super.setServerState(state);
    }

    @Override
    protected String getClientClass() {
        return "org.jdesktop.wonderland.client.cell.InteractionComponent";
    }

    public void setCollidable(boolean collidable) {
        this.collidable = collidable;

        if (isLive()) {
            sendUpdate();
        }
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    
        if (isLive()) {
            sendUpdate();
        }
    }

    /** Notify clients that the state has been updated */
    protected void sendUpdate() {
        // send an add component message to all clients. Since the component
        // already exists, this will just update its server state
        CellMessage out = CellClientComponentMessage.newAddMessage(cellID,
                getClientClass(), getClientState(null, null, null));
        cellRef.get().sendCellMessage(null, out);
    }
}
