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

import com.jme.math.Quaternion;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.modules.affordances.client.jme.RotateAffordance;
import org.jdesktop.wonderland.modules.affordances.client.jme.RotateAffordance.RotationListener;

/**
 * A client-side cell component for rotate affordances
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class RotateAffordanceCellComponent extends AffordanceCellComponent {

    private RotateAffordanceListener listener = null;

    public RotateAffordanceCellComponent(Cell cell) {
        super(cell);
        listener = new RotateAffordanceListener();
    }

    /**
     * @inheritDoc()
     */
    @Override
    protected void setStatus(CellStatus status, boolean increasing) {
        // If we are making the affordance ACTIVE, we want to create the
        // visual affordance Entity. We must do this *before* we call the
        // super.setStatus() method which relies upon a valid affordance
        if (status == CellStatus.ACTIVE) {
            if (increasing) {
                affordance = new RotateAffordance(getSceneGraphRoot());
                ((RotateAffordance)affordance).addRotationListener(listener);
            } else {
                ((RotateAffordance)affordance).removeRotationListener(listener);
            }
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
    private class RotateAffordanceListener implements RotationListener {

        private Quaternion rotationOnPress = null;

        /**
         * @inheritDoc()
         */
        public void rotationPerformed(Quaternion rotation) {
            // Move the cell via the moveable comopnent
            CellTransform transform = cell.getLocalTransform();
            Quaternion newRotation = rotationOnPress.mult(rotation);
            transform.setRotation(newRotation);
            movableComp.localMoveRequest(transform);
        }

        /**
         * @inheritDoc()
         */
        public void rotationStarted() {
            CellTransform transform = cell.getLocalTransform();
            rotationOnPress = transform.getRotation(null);
        }
    }
}
