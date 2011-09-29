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
package org.jdesktop.wonderland.modules.containercell.client;

import java.util.LinkedList;
import java.util.List;
import org.jdesktop.wonderland.client.BaseClientPlugin;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.utils.CellCreationParentRegistry;
import org.jdesktop.wonderland.client.cell.utils.spi.CellCreationParentSPI;
import org.jdesktop.wonderland.common.annotation.Plugin;

/**
 * Plugin that manages all registered container cells to determine the
 * current cell creation parent.
 *
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
@Plugin
public class ContainerClientPlugin extends BaseClientPlugin {
    @Override
    protected void activate() {
        CellCreationParentRegistry.register(getRegistry());
    }

    @Override
    protected void deactivate() {
        // issue 920 - be sure to unregister
        CellCreationParentRegistry.unregister(getRegistry());
    }

    public static ContainerClientRegistry getRegistry() {
        return RegistrySingleton.INSTANCE;
    }

    public static class ContainerClientRegistry implements CellCreationParentSPI {
        private final List<Cell> parentList = new LinkedList<Cell>();

        ContainerClientRegistry() {}

        public synchronized Cell getCellCreationParent() {
            if (parentList.isEmpty()) {
                return null;
            }

            return parentList.get(0);
        }

        public synchronized void requestParent(Cell cell) {
            if (parentList.contains(cell)) {
                return;
            }

            // add new entries to the beginning of the list
            parentList.add(0, cell);
        }

        public synchronized void unrequestParent(Cell cell) {
            parentList.remove(cell);
        }
    }

    private static class RegistrySingleton {
        private static ContainerClientRegistry INSTANCE =
                new ContainerClientRegistry();
    }
}
