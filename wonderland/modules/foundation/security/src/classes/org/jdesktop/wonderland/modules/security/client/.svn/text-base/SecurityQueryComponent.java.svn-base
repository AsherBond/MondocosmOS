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
package org.jdesktop.wonderland.modules.security.client;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellComponent;
import org.jdesktop.wonderland.client.cell.ChannelComponent;
import org.jdesktop.wonderland.client.cell.ChannelComponent.ComponentMessageReceiver;
import org.jdesktop.wonderland.client.cell.annotation.UsesCellComponent;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.common.messages.ResponseMessage;
import org.jdesktop.wonderland.common.security.Action;
import org.jdesktop.wonderland.modules.security.common.ActionDTO;
import org.jdesktop.wonderland.modules.security.common.messages.PermissionsChangedMessage;
import org.jdesktop.wonderland.modules.security.common.messages.PermissionsRequestMessage;
import org.jdesktop.wonderland.modules.security.common.messages.PermissionsResponseMessage;

/**
 * Client-side representation of the security component contains the
 * permissions for this particular client.
 * @author jkaplan
 */
public class SecurityQueryComponent extends CellComponent {
    private static final Logger logger =
            Logger.getLogger(SecurityQueryComponent.class.getName());

    /** 
     * The channel to listen for messages over
     */
    @UsesCellComponent
    private ChannelComponent channel;

    /**
     * The message receiver to handle messages, or null if listeners
     * are not registered
     */
    private SecurityMessageReceiver receiver = null;

    public SecurityQueryComponent(Cell cell) {
        super (cell);
    }

    @Override
    protected void setStatus(CellStatus status, boolean increasing) {
        super.setStatus(status, increasing);

        switch (status) {
            case ACTIVE:
                if (increasing) {
                    if (receiver == null) {
                        receiver = new SecurityMessageReceiver();
                        channel.addMessageReceiver(PermissionsChangedMessage.class,
                                                   receiver);
                        invalidate(cell, true);
                    }
                } else {
                    channel.removeMessageReceiver(PermissionsChangedMessage.class);
                    receiver = null;
                    invalidate(cell, true);
                }
                break;
        }
    }

    /**
     * Get this user's permissions for the given cell.
     * @param cellID the id of the cell to request permissions for, or null
     * to request permissions for this cell.
     * @return the set of permissions for this user
     */
    public synchronized Set<Action> getPermissions(CellID cellID)
        throws InterruptedException
    {
        Set<Action> out = new LinkedHashSet<Action>();

        // request the permissions from the server
        PermissionsRequestMessage prm = new PermissionsRequestMessage(cellID);
        ResponseMessage rm = channel.sendAndWait(new PermissionsRequestMessage());
        if (rm instanceof PermissionsResponseMessage) {
            for (ActionDTO a : ((PermissionsResponseMessage) rm).getGranted()) {
                out.add(a.getAction());
            }
        }
        
        return out;
    }

    /**
     * Invalidate the permissions for this cell.  Go ahead and invalidate
     * the permissions on any child that is relying on this cell for
     * permissions.
     * @param cell the cell to invalidate
     * @param force to force invalidation, even if the cell has a
     * SecurityQueryComponent
     */
    protected void invalidate(Cell cell, boolean force) {
        // stop walking if we find a child with its own query component
        if (!force && cell.getComponent(SecurityQueryComponent.class) != null) {
            return;
        }

        // invalidate the current cache
        SecurityComponent sc = cell.getComponent(SecurityComponent.class);
        if (sc != null) {
            sc.invalidate();
        }

        // now recursively invalidate any children
        for (Cell child : cell.getChildren()) {
            invalidate(child, false);
        }
    }
    

    @Override
    public void setClientState(CellComponentClientState clientState) {
        super.setClientState(clientState);
    }

    class SecurityMessageReceiver implements ComponentMessageReceiver {
        public void messageReceived(CellMessage message) {
            if (message instanceof PermissionsChangedMessage) {
                // reset our view of granted permissions.  The next time someone
                // requests them, they will be re-fetched from the server.
                invalidate(cell, true);
            }
        }
    }
}
