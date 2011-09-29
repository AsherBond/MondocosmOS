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
import org.jdesktop.wonderland.client.cell.utils.CellPlacementUtils;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.modules.affordances.client.jme.TranslateAffordance;
import org.jdesktop.wonderland.modules.affordances.client.jme.TranslateAffordance.TranslationListener;

/**
 * A client-side cell component for translate affordances
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class TranslateAffordanceCellComponent extends AffordanceCellComponent {

    private TranslateAffordanceListener listener = null;

    public TranslateAffordanceCellComponent(Cell cell) {
        super(cell);
        listener = new TranslateAffordanceListener();
    }

    /**
     * @inheritDoc()
     */
    @Override
    public void setStatus(CellStatus status, boolean increasing) {
        // If we are making the affordance ACTIVE, we want to create the
        // visual affordance Entity. We must do this *before* we call the
        // super.setStatus() method which relies upon a valid affordance
        if (increasing && status == CellStatus.ACTIVE) {
            // Create the affordance. Register a listener for all translation
            // events for the affordance and update the translation of the
            // movable component
            affordance = new TranslateAffordance(getSceneGraphRoot());
            ((TranslateAffordance)affordance).addTranslationListener(listener);
        }
        else if (!increasing && status == CellStatus.DISK) {
            ((TranslateAffordance)affordance).removeTranslationListener(listener);
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
    private class TranslateAffordanceListener implements TranslationListener {

        private Vector3f translationOnPress = null;

        /**
         * @inheritDoc()
         */
        public void translationPerformed(Vector3f translation) {

            // We must convert the translation in world coordinates to local
            // coordinates of the Cell. First find the Cell Transform of the
            // parent Cell (if there is one) and the world root transform.
            CellTransform cellWorldTransform = new CellTransform();
            if (cell.getParent() != null) {
                cellWorldTransform = cell.getParent().getWorldTransform();
                cellWorldTransform.setTranslation(new Vector3f());
            }
            CellTransform worldTransform = new CellTransform();

            // Formulate a new transform that just has the new world translation
            // of the Cell.
            CellTransform transform = new CellTransform(null, translation);

            // Convert into a Cell's local coordinations.
            CellTransform newTransform = CellPlacementUtils.transform(
                    transform, worldTransform, cellWorldTransform);

            // Find out how much to add to the transform. This is done in
            // world coordinates.
            Vector3f newTranslation = translationOnPress.add(newTransform.getTranslation(null));

            // Set the translation back on the Cell using the movable component
            CellTransform cellTransform = cell.getLocalTransform();
            cellTransform.setTranslation(newTranslation);
            movableComp.localMoveRequest(cellTransform);
        }

        /**
         * @inheritDoc()
         */
        public void translationStarted() {
            CellTransform transform = cell.getLocalTransform();
            translationOnPress = transform.getTranslation(null);
        }
    }
}
