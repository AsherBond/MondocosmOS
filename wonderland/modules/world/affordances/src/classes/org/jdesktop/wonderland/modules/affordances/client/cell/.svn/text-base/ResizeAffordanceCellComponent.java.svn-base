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
package org.jdesktop.wonderland.modules.affordances.client.cell;

import com.jme.math.Vector3f;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.modules.affordances.client.jme.ResizeAffordance;
import org.jdesktop.wonderland.modules.affordances.client.jme.ResizeAffordance.ResizingListener;

/**
 * A client-side cell component for resize affordances
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ResizeAffordanceCellComponent extends AffordanceCellComponent {

    private ResizingAffordanceListener listener = null;

    public ResizeAffordanceCellComponent(Cell cell) {
        super(cell);
        listener = new ResizingAffordanceListener();
    }

    /**
     * @inheritDoc()
     */
    @Override
    protected void setStatus(CellStatus status, boolean increasing) {
        // If we are making the affordance ACTIVE, we want to create the
        // visual affordance Entity. We must do this *before* we call the
        // super.setStatus() method which relies upon a valid affordance
        if (increasing && status == CellStatus.ACTIVE) {
            affordance = new ResizeAffordance(getSceneGraphRoot());
            ((ResizeAffordance)affordance).addResizingListener(listener);
        }
        else if (!increasing && status == CellStatus.DISK) {
            ((ResizeAffordance)affordance).removeResizingListener(listener);
        }

        // Now call the super setStatus() method after we've created the
        // affordance
        super.setStatus(status, increasing);
    }

    /**
     * Listener that handles events back from the affordance. This listener
     * assumes the movable component is already present on the Cell when this
     * event happens
     */
    private class ResizingAffordanceListener implements ResizingListener {

        private float scalingOnPress = 0.0f;

        /**
         * @inheritDoc()
         */
        public void resizingPerformed(float scale) {
            // Move the cell via the moveable comopnent
            CellTransform transform = cell.getLocalTransform();
            float newResizing = scalingOnPress * scale;
            transform.setScaling(newResizing);
            movableComp.localMoveRequest(transform);
        }

        /**
         * @inheritDoc()
         */
        public void resizingStarted() {
            CellTransform transform = cell.getLocalTransform();
            scalingOnPress = transform.getScaling();
        }
    }
}
