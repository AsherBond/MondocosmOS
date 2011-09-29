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

import com.jme.scene.Node;
import java.util.logging.Logger;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.Cell.RendererType;
import org.jdesktop.wonderland.client.cell.CellComponent;
import org.jdesktop.wonderland.client.cell.ComponentChangeListener;
import org.jdesktop.wonderland.client.cell.ComponentChangeListener.ChangeType;
import org.jdesktop.wonderland.client.cell.MovableComponent;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.input.EventClassListener;
import org.jdesktop.wonderland.client.input.InputManager;
import org.jdesktop.wonderland.client.jme.CellRefComponent;
import org.jdesktop.wonderland.client.jme.cellrenderer.CellRendererJME;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.messages.CellServerComponentMessage;
import org.jdesktop.wonderland.common.messages.ErrorMessage;
import org.jdesktop.wonderland.common.messages.ResponseMessage;
import org.jdesktop.wonderland.modules.affordances.client.event.AffordanceRemoveEvent;
import org.jdesktop.wonderland.modules.affordances.client.jme.Affordance;

/**
 * A client-side cell component base class for affordance components.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public abstract class AffordanceCellComponent extends CellComponent {
    protected static Logger logger = Logger.getLogger(AffordanceCellComponent.class.getName());
    private AffordanceCloseListener listener = null;
    private float size = 1.5f;
    protected Affordance affordance = null;
    protected MovableComponent movableComp = null;

    public AffordanceCellComponent(Cell cell) {
        super(cell);

        // Listen for the global event to turn off all affordances
        listener = new AffordanceCloseListener();
        InputManager.inputManager().addGlobalEventListener(listener);
    }

    @Override
    protected void setStatus(CellStatus status, boolean increasing) {

        // If we are making this component active, then create the affordance,
        // if the first time through.
        super.setStatus(status, increasing);
        if (increasing && status == CellStatus.ACTIVE) {
            // Add a cell ref component to the entity. This will let us associated
            // the entity with the cell and make it easy to detect when we click
            // off of the cell
            CellRefComponent refComponent = new CellRefComponent(cell);
            affordance.addComponent(CellRefComponent.class, refComponent);

            // First try to add the movable component. The presence of the
            // movable component will determine when we actually add the
            // affordance to the scene graph. We do this in a separate thread
            // because adding the movable component is a synchronous call (it
            // waits for a response message. That would block the thread calling
            // the setStatus() method. This won't work since this thread cannot
            // be blocked when adding the component.
            new Thread() {
                @Override
                public void run() {
                    checkMovableComponent();
                    if (movableComp == null) {
                        addMovableComponent();
                    }
                    addAffordanceToScene();
                }
            }.start();
        }
        else if (!increasing && status == CellStatus.DISK) {
            // Remove the affordance and clean it up. Set it to null, because
            // it must be created again when the Cell becomes visible.
            affordance.dispose();
            affordance = null;
        }
    }

    /**
     * Adds the affordance to the scene graph.
     */
    public void addAffordanceToScene() {
        // We should add a listener just in case the movable component gets
        // added. We add the listener first, just in case the component gets
        // added inbetween the time we check and the time we add the listener.
        if (movableComp == null) {
            cell.addComponentChangeListener(new ComponentChangeListener() {
                public void componentChanged(Cell cell, ChangeType type, CellComponent component) {
                    if (type == ChangeType.ADDED && component instanceof MovableComponent) {
                        checkMovableComponent();
                        updateSceneGraph();
                    }
                }
            });
        }

        // Recheck whether the movable component exists here. If so, then add
        // to the scene graph right away.
        checkMovableComponent();
        if (movableComp != null) {
            updateSceneGraph();
        }

    }

    /**
     * Checks whether the movable component exists and sets it if so.
     */
    private synchronized void checkMovableComponent() {
        if (movableComp == null) {
            movableComp = cell.getComponent(MovableComponent.class);
        }
    }

    /**
     * Sets the size of the affordance. The size is a floating point value where
     * 1.0 designates the same size of the cell.
     *
     * @param size The new size of the affordance
     */
    public void setSize(float size) {
        this.size = size;
        affordance.setSize(size);
    }

    /**
     * Returns the current size of the afforance.
     *
     * @return The size of the affordance
     */
    public float getSize() {
        return this.size;
    }

    /**
     * Remove the affordance component from the cell
     */
    public void remove() {
        // Remove the listener for the event that causes all affordances to
        // disappear and then remove this component itself. This will cause
        // the affordance to be removed in setStatus()
        InputManager.inputManager().removeGlobalEventListener(listener);
        cell.removeComponent(getClass());
    }

    /**
     * Returns the scene root for the Cell's scene graph
     */
    protected Node getSceneGraphRoot() {
        CellRendererJME renderer = (CellRendererJME)
                cell.getCellRenderer(RendererType.RENDERER_JME);
        RenderComponent cellRC = (RenderComponent)
                renderer.getEntity().getComponent(RenderComponent.class);
        return cellRC.getSceneRoot();
    }

    /**
     * Adds the movable component, assumes it does not already exist.
     */
    private void addMovableComponent() {

        // Go ahead and try to add the affordance. If we cannot, then log an
        // error and return.
        CellID cellID = cell.getCellID();
        String className = "org.jdesktop.wonderland.server.cell.MovableComponentMO";
        CellServerComponentMessage cscm =
                CellServerComponentMessage.newAddMessage(cellID, className);
        ResponseMessage response = cell.sendCellMessageAndWait(cscm);
        if (response instanceof ErrorMessage) {
            logger.warning("Unable to add movable component for Cell" +
                    cell.getName() + " with ID " + cell.getCellID());
        }
    }

    /* True if the affordance has been added to the scene graph. */
    private boolean addedToSceneGraph = false;

    /**
     * Updates the scene graph to add this affordance
     */
    private synchronized void updateSceneGraph() {
        // This method is synchronized and we keep track of whether it has been
        // added in a boolean. This ensures that it only gets added once.
        if (addedToSceneGraph == false) {
            affordance.setVisible(true);
            addedToSceneGraph = true;
        }
    }

    /**
     * Inner class that listens for an event signalling that the afforance
     * should remove itself
     */
    class AffordanceCloseListener extends EventClassListener {

        @Override
        public Class[] eventClassesToConsume() {
            return new Class[] { AffordanceRemoveEvent.class };
        }


        @Override
        public void commitEvent(Event event) {
            // Just tell the affordance to remove itself
            remove();
        }
    }
}
