/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.server.cell;

import java.io.Serializable;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.AppContext;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;

/**
 *
 * @author paulby
 */
public abstract class CellComponentMO implements ManagedObject, Serializable {
    protected ManagedReference<CellMO> cellRef;
    protected CellID cellID;

    /* True if the component is live, false if not */
    private boolean isLive = false;

    public CellComponentMO(CellMO cell) {        
        this.cellRef = AppContext.getDataManager().createReference(cell);
        cellID = cell.getCellID();
    }
    
    protected void setLive(boolean live) {
        this.isLive = live;
    }

    /**
     * Returns true if the component is live, false if not.
     *
     * @return True if the component is live
     */
    public boolean isLive() {
        return isLive;
    }
    
    /**
     * Sets the server state for the cell component
     * 
     * @param state the properties to setup with
     */
    public void setServerState(CellComponentServerState state) {
        // Do nothing by default
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
        // Do nothing by default
        return state;
    }

    /**
     * Returns the client-side state of the cell component . If the state argument
     * is null, then the method should create an appropriate class, otherwise,
     * the method should just fill in details in the class. Returns the client-
     * side state class
     *
     * @param state If null, create a new object
     * @param clientID The unique ID of the client
     * @param capabilities The client capabilities
     */
    public CellComponentClientState getClientState(CellComponentClientState state,
            WonderlandClientID clientID,
            ClientCapabilities capabilities) {

        // If the given cellClientState is null, create a new one
        if (state == null) {
            state = new CellComponentClientState();
        }
        return state;
    }

    /**
     * If this component has a client side component then return the fully
     * qualified name of the client class. If there is no client portion to this
     * component, return null.
     * @return
     */
    protected abstract String getClientClass();
}
