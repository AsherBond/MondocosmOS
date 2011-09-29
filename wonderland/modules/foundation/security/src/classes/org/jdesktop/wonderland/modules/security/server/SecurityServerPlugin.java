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

package org.jdesktop.wonderland.modules.security.server;

import com.sun.sgs.app.AppContext;
import java.io.Serializable;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.annotation.Plugin;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.modules.security.server.service.CellResourceManagerInternal;
import org.jdesktop.wonderland.server.ServerPlugin;
import org.jdesktop.wonderland.server.cell.CellComponentMO;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.ComponentChangeListenerSrv;
import org.jdesktop.wonderland.server.cell.CellParentChangeListenerSrv;
import org.jdesktop.wonderland.server.spatial.CellMOListener;
import org.jdesktop.wonderland.server.spatial.UniverseManager;

/**
 * A server plugin that maintains the security cache for all cells.  It does
 * this by adding some small and stateless listeners to each cell as the cell
 * is created.  These listeners are used to update the cache as properties
 * of the cell like parent and number of components change.
 *
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
@Plugin
public class SecurityServerPlugin implements ServerPlugin
{
    private static final Logger logger =
            Logger.getLogger(SecurityServerPlugin.class.getName());

    public void initialize() {
        // add our listener to the universe manager
        UniverseManager um = AppContext.getManager(UniverseManager.class);
        um.addCellListener(new SecurityCellMOListener());
    }

    /**
     * A listener that updates the cache as properties of the cell like
     * parent and number of components change.
     */
    private static final class SecurityCellMOListener
            implements CellMOListener, CellParentChangeListenerSrv,
                       ComponentChangeListenerSrv, Serializable
    {
        public void cellAdded(CellMO cell) {
            cell.addParentChangeListener(this);
            cell.addComponentChangeListener(this);
        }

        public void parentChanged(CellMO cell, CellMO parent) {
            CellID parentCellID = null;
            if (parent != null) {
                parentCellID = parent.getCellID();
            }

            // make sure this cell's parent is up-to-date
            CellResourceManagerInternal crmi =
                    AppContext.getManager(CellResourceManagerInternal.class);
            crmi.updateCellResource(cell.getCellID(), parentCellID);
        }

        public void componentChanged(CellMO cell, ChangeType type,
                                     CellComponentMO component)
        {
            // make sure this cell's actions are up-to-date
            CellResourceManagerInternal crmi =
                    AppContext.getManager(CellResourceManagerInternal.class);
            switch (type) {
                case ADDED:
                    crmi.updateCellResource(cell.getCellID(), component, true);
                    break;
                case REMOVED:
                    crmi.updateCellResource(cell.getCellID(), component, false);
                    break;
            }
        }

        public void cellRemoved(CellMO cell) {
            // make sure this cell is removed from the service cache
            CellResourceManagerInternal crmi =
                    AppContext.getManager(CellResourceManagerInternal.class);
            crmi.invalidateCellResource(cell.getCellID());
        }
    }
}
