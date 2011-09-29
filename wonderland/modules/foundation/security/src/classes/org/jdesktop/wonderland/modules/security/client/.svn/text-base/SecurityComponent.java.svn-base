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

import java.util.Set;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellComponent;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.common.security.Action;

/**
 * Client-side representation of the security component contains the
 * permissions for this particular client.  This component is automatically
 * added to every cell by the SecurityComponentClientPlugin. This component
 * doesn't do any communication directly.  Instead, it walks the tree
 * of cells to find a parent cell with a SecurityQueryComponent, and uses
 * that to make the server-side request.
 *
 * The SecurityQueryComponent is added when a server-side security component is
 * added to the cell.
 * @author jkaplan
 */
public class SecurityComponent extends CellComponent {
    private static final Logger logger =
            Logger.getLogger(SecurityComponent.class.getName());

    /** If false, there is no security on the given cell. */
    private boolean secure = true;

    /**
     * The set of permissions this user has for this cell.  If this value
     * is null, it means the permissions are not yet calculated.
     */
    private Set<Action> granted;

    public SecurityComponent(Cell cell) {
        super (cell);
    }

    /**
     * Determine if the permissions have been set, or if they need to be
     * requested
     */
    public synchronized boolean hasPermissions() {
        return (!secure || granted != null);
    }

    /**
     * Return whether or not the user has permissions to execute the given
     * action on a cell.
     * @param action the action to check for
     * @return true of the current client has permission for the given action,
     * or false if not
     */
    public synchronized boolean getPermission(Action action)
        throws InterruptedException
    {
        // if there is no security, don't bother checking
        if (!secure) {
            return true;
        }

        // make sure the permissions are loaded
        if (granted != null || loadPermissions()) {
            // return true if the action is in the granted set
            return granted.contains(action);
        }

        // if we got here, it means we were unable to load any permissions
        return true;
    }

    /**
     * Get this user's permissions from the server, and cache them
     * @return the set of permissions for this user, or null if the
     * permissions are not calculated
     */
    protected synchronized boolean loadPermissions()
        throws InterruptedException
    {
        // find the security query component
        SecurityQueryComponent query = findQueryComponent();
        if (query == null) {
            // nothing to ask, so there is no security
            secure = false;
            return false; 
        }
        
        // get the permissions from the query component
        granted = query.getPermissions(cell.getCellID());
        return true;
    }

    /**
     * Invalidate the cached resources.
     */
    protected synchronized void invalidate() {
        granted = null;
        secure = true;
    }

    /**
     * Walk up the tree of cells to find the first parent with a
     * SecurityQueryComponent.
     * @return the SecurityQueryComponent of the nearest parent, or null
     * if no parent has a SecurityQueryComponent.
     */
    protected SecurityQueryComponent findQueryComponent() {
        SecurityQueryComponent out = null;

        for (Cell curCell = cell; curCell != null; curCell = curCell.getParent()) {
            out = curCell.getComponent(SecurityQueryComponent.class);
            if (out != null) {
                break;
            }
        }

        return out;
    }

    @Override
    public void setClientState(CellComponentClientState clientState) {
        super.setClientState(clientState);
    }
}
