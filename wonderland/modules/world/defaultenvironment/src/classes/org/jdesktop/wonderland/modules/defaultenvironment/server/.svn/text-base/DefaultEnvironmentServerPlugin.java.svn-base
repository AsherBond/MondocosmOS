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
package org.jdesktop.wonderland.modules.defaultenvironment.server;

import com.sun.sgs.app.ManagedObject;
import java.io.Serializable;
import org.jdesktop.wonderland.common.annotation.Plugin;
import org.jdesktop.wonderland.server.ServerPlugin;
import org.jdesktop.wonderland.server.cell.CellManagerMO;
import org.jdesktop.wonderland.server.cell.CellManagerMO.EnvironmentCellCreator;
import org.jdesktop.wonderland.server.cell.EnvironmentCellMO;

/**
 * Server plugin for default environment cell
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
@Plugin
public class DefaultEnvironmentServerPlugin implements ServerPlugin {

    public void initialize() {
        CellManagerMO.getCellManager().registerEnvironmentCellCreator(
                new DefaultEnvironmentCellCreator());
    }

    static class DefaultEnvironmentCellCreator
            implements EnvironmentCellCreator, ManagedObject, Serializable
    {

        public EnvironmentCellMO createEnvironmentCell() {
            return new DefaultEnvironmentCellMO();
        }
    }
}
