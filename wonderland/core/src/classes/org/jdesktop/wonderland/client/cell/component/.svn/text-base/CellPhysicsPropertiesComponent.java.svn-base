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
package org.jdesktop.wonderland.client.cell.component;

import org.jdesktop.wonderland.client.cell.*;
import org.jdesktop.wonderland.common.cell.component.state.CellPhysicsPropertiesComponentClientState;
import org.jdesktop.wonderland.common.cell.component.state.CellPhysicsPropertiesComponentServerState;
import org.jdesktop.wonderland.common.cell.component.state.PhysicsProperties;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;

/**
 *
 * @author paulby
 */
public class CellPhysicsPropertiesComponent extends CellComponent {

    private CellPhysicsPropertiesComponentClientState clientState = null;

    public final static String DEFAULT_NAME = CellPhysicsPropertiesComponentServerState.DEFAULT_NAME;

    public CellPhysicsPropertiesComponent(Cell cell) {
        super(cell);
    }

    public PhysicsProperties getPhysicsProperties(String entityName) {
        synchronized(this) {
            if (clientState==null)
                return null;
            return clientState.getPhyiscsProperties(entityName);
        }
    }

    @Override
    public void setClientState(CellComponentClientState clientState) {
        System.err.println("PHYSICS CLIENT STATE UPDATE ");
        synchronized(this) {
            this.clientState = (CellPhysicsPropertiesComponentClientState) clientState;
            System.err.println("PHYSISCS "+this.clientState.getPhyiscsProperties(DEFAULT_NAME));
        }
    }
}
