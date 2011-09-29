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
package org.jdesktop.wonderland.server.cell;

import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.MultipleParentException;


/**
 * A cell that represents the environment (lights, skybox, etc). Each
 * server will have no more than one environment cell, which has a static
 * ID.
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
public abstract class EnvironmentCellMO extends CellMO {
    	
    /** Default constructor, used when cell is created via WFS */
    public EnvironmentCellMO() {
    }

    @Override
    public void setLive(boolean live) {
        super.setLive(live);
    }
    
    @Override
    public void addChild(CellMO child) throws MultipleParentException {
        // cannot add children to the environment cell
        throw new RuntimeException("No children allowed on environment cell");
    }

    @Override
    public CellID getCellID() {
        return CellID.getEnvironmentCellID();
    }
}
