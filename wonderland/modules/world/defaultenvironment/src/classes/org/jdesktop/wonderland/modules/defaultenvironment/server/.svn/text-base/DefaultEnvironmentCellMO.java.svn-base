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

import com.jme.bounding.BoundingSphere;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.modules.defaultenvironment.common.DefaultEnvironmentCellServerState;
import org.jdesktop.wonderland.server.cell.EnvironmentCellMO;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;

/**
 * CellMO for default environment
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
public class DefaultEnvironmentCellMO extends EnvironmentCellMO {
    public DefaultEnvironmentCellMO() {
        setLocalTransform(new CellTransform());
        setLocalBounds(new BoundingSphere());
        setName("environment");
    }

    @Override
    protected String getClientCellClassName(WonderlandClientID clientID,
                                            ClientCapabilities capabilities)
    {
        return "org.jdesktop.wonderland.modules.defaultenvironment.client.DefaultEnvironmentCell";
    }

    @Override
    public CellServerState getServerState(CellServerState setup) {
        if (setup == null) {
            setup = new DefaultEnvironmentCellServerState();
        }

        return super.getServerState(setup);
    }


}
