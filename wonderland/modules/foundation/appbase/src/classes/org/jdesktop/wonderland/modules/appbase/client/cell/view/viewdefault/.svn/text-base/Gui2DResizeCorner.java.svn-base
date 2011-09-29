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
package org.jdesktop.wonderland.modules.appbase.client.cell.view.viewdefault;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import java.awt.Point;
import java.awt.event.MouseEvent;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.jme.input.MouseButtonEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseDraggedEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEnterExitEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.view.View2DEntity;
import org.jdesktop.wonderland.modules.appbase.client.view.Gui2D;
import org.jdesktop.wonderland.modules.appbase.client.App2D;

/**
 * The GUI code for the frame resize corner.
 *
 * @author deronj
 */
@ExperimentalAPI
class Gui2DResizeCorner extends Gui2DSide {

    /** The associated resize corner component */
    protected FrameResizeCorner resizeCorner;

    /** Whether the mouse is over the resize corner. */
    private boolean mouseIsInside;

    /** 
     * Create a new instance of Gui2DResizeCorner.
     *
     * @param view The view associated with the component that uses this Gui.
     */
    public Gui2DResizeCorner(View2DCell view) {
        super(view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        super.cleanup();
        resizeCorner = null;
    }

    /**
     * Specify the resize corner component for which this Gui provides behavior.
     *
     * @param resizeCorner The resize corner component.
     */
    public void setComponent(FrameResizeCorner resizeCorner) {
        this.resizeCorner = resizeCorner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void attachMouseListener(Entity entity) {
        mouseListener = new ResizeCornerMouseListener(entity);
        mouseListener.addToEntity(entity);
    }

    /**
     * The mouse listener for this GUI.
     */
    protected class ResizeCornerMouseListener extends Gui2DSide.SideMouseListener {

        public ResizeCornerMouseListener (Entity entity) {
            super(entity);
        }

        /**
         * Called when a 3D event has occurred.
         */
        @Override
        public void commitEvent(Event event) {
            MouseEvent3D me3d = (MouseEvent3D) event;

            if (me3d instanceof MouseEnterExitEvent3D) {
                mouseIsInside = ((MouseEnterExitEvent3D) me3d).isEnter();
            }

            // Process change control event even if not enabled
            if (Gui2D.isChangeControlEvent((MouseEvent)me3d.getAwtEvent())) {

                boolean prevHasControl = view.getWindow().getApp().getControlArb().hasControl();

                super.commitEvent(event);
                
                // Note: cannot check the control arb directly for whether it has control
                // because the above commitEvent uses App2D.invokeLater (this is asynchronous).
                if (!prevHasControl) {
                    resizeCorner.setMouseInside(mouseIsInside);
                }
                return;
            }

            // Do nothing if not enabled
            if (!resizeCorner.isEnabled()) {
                return;
            }

            if (me3d instanceof MouseEnterExitEvent3D &&
                view.getWindow().getApp().getControlArb().hasControl()) {
                resizeCorner.setMouseInside(mouseIsInside);
            }

            super.commitEvent(event);
        }
    }

    /**
     * Determine if this is a window configuration action.
     * are only recognized when the user has control of the window.
     *
     * @param me The AWT event for this 3D mouse event.
     * @param me3d The 3D mouse event.
     */
    @Override
    protected Action determineIfConfigAction(MouseEvent me, MouseEvent3D me3d) {
        Action action = determineIfToFrontAction(me);
        if (action != null) {
            return action;
        }

        return determineIfResizeAction(me, me3d);
    }

    /**
     * Perform the window configuration action.
     *
     * @param action The configuration action the given event provokes.
     * @param me The AWT event for this 3D mouse event.
     * @param me3d The 3D mouse event.
     */
    @Override
    protected void performConfigAction(Action action, MouseEvent me, MouseEvent3D me3d) {
        if (action.type == ActionType.TO_FRONT) {
            App2D.invokeLater(new Runnable() {
                public void run () {
                    view.getWindow().restackToTop();
                }
            });
            return;
        }

        performResizeAction(action, me, me3d);
    }

    /** 
     * Determine if this is a resize drag action.
     * @param me The AWT event for this 3D mouse event.
     * @param me3d The 3D mouse event.
     */
    protected Action determineIfResizeAction(MouseEvent me, MouseEvent3D me3d) {
        Action action = null;

        switch (me.getID()) {

        case MouseEvent.MOUSE_PRESSED:
            MouseButtonEvent3D buttonEvent = (MouseButtonEvent3D) me3d;
            if (configState == ConfigState.IDLE && 
                me.getButton() == MouseEvent.BUTTON1 &&
                me.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK) {

                configState = ConfigState.DRAG_ACTIVE;
                action = new Action(ActionType.DRAG_START);
                dragStartScreen = new Point(me.getX(), me.getY());
                dragStartWorld = buttonEvent.getIntersectionPointWorld();
                dragStartLocal = view.getNode().worldToLocal(dragStartWorld, new Vector3f());
            }
            return action;

        case MouseEvent.MOUSE_DRAGGED:
            if (configState == ConfigState.DRAG_ACTIVE ||
                configState == ConfigState.DRAGGING) {
                action = new Action(ActionType.DRAG_UPDATE);
                configState = ConfigState.DRAGGING;

                MouseDraggedEvent3D dragEvent = (MouseDraggedEvent3D) me3d;
                Vector3f dragVectorWorld = dragEvent.getDragVectorWorld(dragStartWorld, dragStartScreen,
                                                                        new Vector3f());

                // Convert from world to local coordinates.
                Node viewNode = view.getNode();
                Vector3f curWorld = dragStartWorld.add(dragVectorWorld, new Vector3f());
                Vector3f curLocal = viewNode.worldToLocal(curWorld, new Vector3f());
                dragVectorLocal = curLocal.subtract(dragStartLocal);
            }
            return action;

        case MouseEvent.MOUSE_RELEASED:
            if (me.getButton() == MouseEvent.BUTTON1) {
                action = new Action(ActionType.DRAG_FINISH);
                configState = ConfigState.IDLE;
                // Note: the coordinates for WL mouse release events are invalid.
                // So we just use the coordinates from the last drag or press.
            }
            return action;
        }

        return null;
    }

    /** 
     * Perform a resize action.
     * @param action The configuration action the given event provokes.
     * @param me The AWT event for this 3D mouse event.
     * @param me3d The 3D mouse event.
     */
    protected void performResizeAction(Action action, MouseEvent me, MouseEvent3D me3d) {

        switch (action.type) {

        case DRAG_START:
            App2D.invokeLater(new Runnable() {
                public void run () {
                    ((View2DEntity)view).userResizeStart();
                }
            });
            break;

        case DRAG_UPDATE:
            App2D.invokeLater(new Runnable() {
                public void run () {
                    ((View2DEntity)view).userResizeUpdate(new Vector2f(dragVectorLocal.x, dragVectorLocal.y));
                }
            });
            break;

        case DRAG_FINISH:
            App2D.invokeLater(new Runnable() {
                public void run () {
                    ((View2DEntity)view).userResizeFinish();
                }
            });
            break;

        default:
            throw new RuntimeException("Unrecognized action");
        }
    }
}
