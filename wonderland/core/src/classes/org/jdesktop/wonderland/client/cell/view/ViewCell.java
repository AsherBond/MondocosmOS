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
package org.jdesktop.wonderland.client.cell.view;

import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellCache;
import org.jdesktop.wonderland.client.cell.MovableAvatarComponent;
import org.jdesktop.wonderland.client.cell.MovableComponent;
import org.jdesktop.wonderland.client.comms.CellClientSession;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.CellTransform;

import org.jdesktop.wonderland.common.cell.state.CellClientState;

import org.jdesktop.wonderland.common.cell.view.ViewCellClientState;

import org.jdesktop.wonderland.common.auth.WonderlandIdentity;

/**
 * ViewCell defines the view into the virtual world for a specific window
 * on a client. A client may have many ViewCells instanstantiated, however
 * there is a 1-1 correlation between the ViewCell and a rendering of the
 * virtual world.
 * 
 * @author paulby
 */
@ExperimentalAPI
public class ViewCell extends Cell {
    
    private WonderlandIdentity identity;

    private MovableAvatarComponent movableComp=null;

    public ViewCell(CellID cellID, CellCache cellCache) {
        super(cellID, cellCache);
    }

    @Override
    protected void setStatus(CellStatus status,boolean increasing) {
        super.setStatus(status, increasing);

        switch(status) {
            case ACTIVE :
                if (increasing) {
                    // TODO, check this is the local avatar
                    CellClientSession s = (CellClientSession)getCellCache().getSession();
                    if (s.getCellCacheConnection().getViewCellID().equals(getCellID()))
                        s.getLocalAvatar().viewCellConfigured(getCellID());
                    
                    MovableComponent mc = getComponent(MovableComponent.class);
                    if (mc != null && mc instanceof MovableAvatarComponent) {
                        movableComp = (MovableAvatarComponent) getComponent(MovableComponent.class);
                    }
                }
                break;
            case INACTIVE :
                if (!increasing) {
                    CellClientSession s = (CellClientSession)getCellCache().getSession();
                    if (s.getCellCacheConnection().getViewCellID().equals(getCellID()))
                        s.getLocalAvatar().viewCellConfigured(null);
                    movableComp = null;
                }
                break;
        }
    }

    @Override
    public void setClientState(CellClientState cellClientState) {
        super.setClientState(cellClientState);

        identity = ((ViewCellClientState) cellClientState).getIdentity();
    }

    public WonderlandIdentity getIdentity() {
        return identity;
    }


    /**
     * Convenience method, simply calls moveableComponent.localMoveRequest
     * @param transform
     */
    public void localMoveRequest(CellTransform transform) {
        if (movableComp!=null)
            movableComp.localMoveRequest(transform, MovableAvatarComponent.NO_TRIGGER, false, null, null);
    }
}
