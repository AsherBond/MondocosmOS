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

import com.jme.bounding.BoundingVolume;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellComponent;
import org.jdesktop.wonderland.client.cell.ProximityComponent;
import org.jdesktop.wonderland.client.cell.ProximityListener;
import org.jdesktop.wonderland.client.cell.annotation.UsesCellComponent;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellStatus;

/**
 * Client-side container component. Registers the cell as a parent when
 * the client enters.
 * 
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class ContainerComponent extends CellComponent
        implements ProximityListener
{
    private static Logger logger =
            Logger.getLogger(ContainerComponent.class.getName());

    @UsesCellComponent
    private ProximityComponent prox;

    public ContainerComponent(Cell cell) {
        super(cell);
    }

    @Override
    protected void setStatus(CellStatus status, boolean increasing) {
        super.setStatus(status, increasing);

        // get the activation bounds from the cell we are part of
        BoundingVolume[] bounds = new BoundingVolume[] {
            this.cell.getLocalBounds()
        };

        if (increasing && status == CellStatus.ACTIVE) {
            prox.addProximityListener(this, bounds);
        } else if (!increasing && status == CellStatus.INACTIVE) {
            prox.removeProximityListener(this);

            // issue 923 - make sure to unregister when the component
            // is removed
            ContainerClientPlugin.getRegistry().unrequestParent(cell);
        }
    }

    public void viewEnterExit(boolean entered, Cell cell, CellID viewCellID,
                              BoundingVolume proximityVolume, int proximityIndex)
    {
        // register as a parent
        if (entered) {
            ContainerClientPlugin.getRegistry().requestParent(cell);
        } else {
            ContainerClientPlugin.getRegistry().unrequestParent(cell);
        }
    }
}
