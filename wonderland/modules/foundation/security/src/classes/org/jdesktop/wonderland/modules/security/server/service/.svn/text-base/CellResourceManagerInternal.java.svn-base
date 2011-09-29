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
package org.jdesktop.wonderland.modules.security.server.service;

import java.util.Set;
import java.util.SortedSet;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.security.Action;
import org.jdesktop.wonderland.modules.security.common.Permission;
import org.jdesktop.wonderland.modules.security.common.Principal;
import org.jdesktop.wonderland.server.cell.CellComponentMO;
import org.jdesktop.wonderland.server.cell.CellResourceManager;

/**
 * Interal operations for the CellResourceManager used by the security
 * component.
 * @author jkaplan
 */
public interface CellResourceManagerInternal extends CellResourceManager {
    /**
     * Get the actions associated with a given cell.  If the cell is not
     * in the cache, a new cache entry will be created.
     * @param cellID the id of the cell to get actions for
     * @return the actions associated with the cell id, or null if no cell is
     * associated with the given id
     */
    public Set<Action> getActions(CellID cellID);

    /**
     * Update a particular resource in the cache.  If the resource is not
     * cached, this update will be ignored.
     * @param cellID the id of the cell to update
     * @param owners the updated owner set
     * @param permissions the update permission set
     */
    public void updateCellResource(CellID cellID, Set<Principal> owners,
                                   SortedSet<Permission> permissions);

    /**
     * Update a cached resource when the resource's parent has changed.  If
     * the resource is not cached, this update will be ignored.
     * @param cellID the id of the cell to update
     * @param parentID the cellID of the new parent cell (or null if this
     * cell no longer has a parent). 
     */
    public void updateCellResource(CellID cellID, CellID parentID);

    /**
     * Indicate that the components of the given resource have changed.
     * If the cell is cached, this method will recalculate the set of
     * actions for the cell based on the new set of components
     * @param cellID the id of the cell to update
     * @param component the component that changed
     * @param added true if the component was added, or false if it
     * was removed
     */
    public void updateCellResource(CellID cellID, CellComponentMO component,
                                   boolean added);

    /**
     * Remove a particular cell from the cache.  It will be reloaded next
     * time a security check is requested.
     * @param cellID the cell id to update
     */
    public void invalidateCellResource(CellID cellID);
}
