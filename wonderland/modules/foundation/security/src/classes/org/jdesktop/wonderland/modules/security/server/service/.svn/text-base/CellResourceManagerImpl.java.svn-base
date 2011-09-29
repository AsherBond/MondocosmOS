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
import org.jdesktop.wonderland.server.security.Resource;

/**
 * implementation of the CellResourceManager interface
 * @author jkaplan
 */
public class CellResourceManagerImpl implements CellResourceManagerInternal {
    private CellResourceService service;

    public CellResourceManagerImpl(CellResourceService service) {
        this.service = service;
    }

    public Resource getCellResource(CellID cellID) {
        return service.getCellResource(cellID);
    }

    public Set<Action> getActions(CellID cellID) {
        return service.getActions(cellID);
    }

    public void updateCellResource(CellID cellID, Set<Principal> owners,
                                   SortedSet<Permission> permissions)
    {
        service.updateCellResource(cellID, owners, permissions);
    }

    public void updateCellResource(CellID cellID, CellID parentID)
    {
        service.updateCellResource(cellID, parentID);
    }

    public void updateCellResource(CellID cellID, CellComponentMO component,
                                   boolean added)
    {
        service.updateCellResource(cellID, component, added);
    }

    public void invalidateCellResource(CellID cellID) {
        service.invalidateCellResource(cellID);
    }
}
