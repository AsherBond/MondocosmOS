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
package org.jdesktop.wonderland.server.cell;

import com.jme.bounding.BoundingVolume;
import com.sun.sgs.app.ManagedReference;
import java.io.Serializable;
import org.jdesktop.wonderland.common.cell.CellTransform;

/**
 * Listener for tracking cell transform changes. If the implementation of this
 * interface is a ManagedObject then transormChanged is called from within a
 * darkstar transaction. If it does not implement ManageObject then the
 * listener is called from outside the Darkstar transaction system.
 * 
 * @author paulby
 */
public interface TransformChangeListenerSrv extends Serializable {

    /**
     * Called when the cells transform has changed.
     * 
     * @param cell
     */
    public void transformChanged(ManagedReference<CellMO> cellRef, final CellTransform localTransform, final CellTransform worldTransform);
}
