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
package org.jdesktop.wonderland.modules.appbase.client.view;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.logging.Logger;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.input.EventClassListener;
import org.jdesktop.wonderland.client.jme.input.MouseButtonEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseDraggedEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEnterExitEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.modules.appbase.client.ControlArb;
import org.jdesktop.wonderland.modules.appbase.client.App2D;
import javax.swing.SwingUtilities;

/**
 * Generic View2D event handler.
 *
 * @author deronj
 */
@InternalAPI
public class Gui2D {

    private static final Logger logger = Logger.getLogger(Gui2D.class.getName());

    /** The type of actions that user events can generate */
    public enum ActionType {

        TOGGLE_CONTROL,
        MOVE_CAMERA_TO_BEST_VIEW,
        MOVE_AVATAR_TO_BEST_VIEW,
        MOVE_WINDOW_TO_BEST_VIEW,
        CLOSE_BUTTON_ENTER,
        CLOSE_BUTTON_EXIT,
        CLOSE_BUTTON_PRESSED,
        DRAG_START,
        DRAG_UPDATE,
        DRAG_FINISH,
        TO_FRONT;
    };

    /** A basic action object */
    public static class Action {

        /** The type of the action */
        public ActionType type;

        /**
         * Create a new instance of Action.
         *
         * @param type The type of the action.
         */
        public Action(ActionType type) {
            this.type = type;
        }
    }

    /** The possible view configuration GUI states */
    protected enum ConfigState {

        /** No user interaction is underway */
        IDLE,
        /** The user has started a mouse drag */
        DRAG_ACTIVE,
        /** The user has actually dragged the mouse */
        DRAGGING
    };

    /** The possible view configuration drag types */
    protected enum ConfigDragType {

        /** The user is dragging the mouse to move the view within its current plane */
        MOVING_PLANAR,
        /** The user is dragging the mouse to move the view in the Z direction to its current plane */
        MOVING_Z,
        /** The user is dragging the mouse to rotate the view around its Y axis */
            // TODO        ROTATING_Y
    };
    /** The view configuration GUI state */
    protected ConfigState configState = ConfigState.IDLE;
    /** The view configuration drag type (only valid when configState != IDLE */
    protected ConfigDragType configDragType;
    /** The intersection point on the entity over which the button was pressed, in world coordinates. */
    protected Vector3f dragStartWorld;
    /** The intersection point in parent local coordinates. */
    protected Vector3f dragStartLocal;
    /** The screen coordinates of the button press event. */
    protected Point dragStartScreen;
    /** The amount that the cursor has been dragged in local coordinates. */
    protected Vector3f dragVectorLocal;

    /** A listener for 3D mouse events */
    protected EventClassListener mouseListener;

    /** This Gui's view */
    protected View2DEntity view;

    /** The entities to which this GUI's listeners are attached. */
    private LinkedList<Entity> attachedToEntities = new LinkedList<Entity>();

    /**
     * Create a new instance of Gui2D.
     *
     * @param view The view associated with the component that uses this Gui.
     */
    public Gui2D(View2DEntity view) {
        this.view = view;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void cleanup() {
        for (Entity entity : attachedToEntities) {
            detachMouseListener(entity);
            detachKeyListener(entity);
        }
        attachedToEntities.clear();

        mouseListener = null;
        view = null;
    }

    /**
     * Returns this GUI's view.
     */
    public View2DEntity getView () {
        return view;
    }

    /**
     * Attach this GUI controller's event listeners to the given entity.
     */
    public synchronized void attachEventListeners(Entity entity) {
        attachMouseListener(entity);
        attachKeyListener(entity);
        attachedToEntities.add(entity);
    }

    /**
     * Detach this GUI controller's event listeners from the entity to which it is attached.
     */
    public synchronized void detachEventListeners(Entity entity) {
        detachMouseListener(entity);
        detachKeyListener(entity);
        attachedToEntities.remove(entity);
    }

    /**
     * Start listening to mouse events from this entity.
     */
    protected void attachMouseListener(Entity entity) {
        mouseListener = new MouseListener();
        mouseListener.addToEntity(entity);
    }

    /**
     * Stop listening to mouse events from this entity.
     */
    protected void detachMouseListener(Entity entity) {
        if (mouseListener != null && entity != null) {
            mouseListener.removeFromEntity(entity);
        }
    }

    /**
     * Start listening to key events from this entity.
     */
    protected void attachKeyListener(Entity entity) {
    }

    /**
     * Stop listening to keyboard events from this entity.
     */
    protected void detachKeyListener(Entity entity) {
    }

    /** A basic listener for 3D mouse events */
    protected class MouseListener extends EventClassListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public Class[] eventClassesToConsume() {
            return new Class[]{MouseEvent3D.class};
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void commitEvent(final Event event) {
            if (view != null) {
                SwingUtilities.invokeLater(new Runnable () {
                    public void run () {
                        if (view != null) {
                            view.deliverEvent(view.getWindow(), (MouseEvent3D) event);
                        }
                    }
                });
            }
        }
    }

    /** 
     * Determine if this 3D mouse event provokes a miscellaneous action. That is, one of:
     * <br><br>
     *    + Move camera to best view
     * <br>
     *    + Move avatar to best view
     * <br>
     *    + Move window to best view
     * <br>
     *    + Change control 
     *
     * @param me The AWT event for this 3D mouse event.
     * @param me3d The 3D mouse event.
     */
    protected Action determineIfMiscAction(MouseEvent me, MouseEvent3D me3d) {

        // Is this the Take Control or Release Control event?
        if (isChangeControlEvent(me)) {

            // Ignore any enter/exit events that LG generates for the click event
            if (me3d instanceof MouseEnterExitEvent3D) {
                return null;
            }

            return new Action(ActionType.TOGGLE_CONTROL);
        }

        return null;
    }

    /**
     * Is this the event which takes or releases control of an app group (which for this LAF is Shift-Left-click)?
     */
    public static boolean isChangeControlEvent(MouseEvent me) {
        // Note: this used to be MOUSE_CLICKED. But in order to fix 246 we need
        // FrameHeaderSwing.ConsumeOnControlListener to work and for this to work
        // I found that I needed the control changed event to be pressed, not clicked.
        // I don't know why. See the doc in FrameHeaderSwing.ConsumeOnControlListener.consumesEvent
        // for more info.
        return me.getID() == MouseEvent.MOUSE_PRESSED &&
                me.getButton() == MouseEvent.BUTTON1 &&
                (me.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0;
    }

    /** 
     * Perform miscellaneous action. (Refer to determineIfMiscAction)
     *
     * @param action The miscellaneous action the given event provokes.
     * @param me The AWT event for this 3D mouse event.
     * @param me3d The 3D mouse event.
     */
    protected void performMiscAction(Action action, MouseEvent me, MouseEvent3D me3d) {
        //logger.severe("Gui misc action = " + action.type);

        switch (action.type) {

            case TOGGLE_CONTROL:
                ControlArb controlArb = view.getWindow().getApp().getControlArb();
                if (controlArb.hasControl()) {
                    logger.info("Release control");
                    controlArb.releaseControl();
                } else {
                    logger.info("Take control");
                    controlArb.takeControl();
                }
                break;
        }
    }

    /** 
     * Determine if this is a window configuration action. That is, one of:
     * <br><br>
     *     + Planar move (move within the cell local z=0 plane).
     * <br>
     *     + Z move (move along the cell local z axis).
     * <br>
     *     + Y rotation.
     *
     * @param me The AWT event for this 3D mouse event.
     * @param me3d The 3D mouse event.
     */
    protected Action determineIfConfigAction(MouseEvent me, MouseEvent3D me3d) {
        Action action = null;

        switch (me.getID()) {

        case MouseEvent.MOUSE_PRESSED:
            MouseButtonEvent3D buttonEvent = (MouseButtonEvent3D) me3d;
            if (configState == ConfigState.IDLE && 
                me.getButton() == MouseEvent.BUTTON1 &&
                me.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK) {

                // Remember: the move occurs in parent coords
                View2DEntity parentView = (View2DEntity) view.getParent();
                if (parentView == null) {
                    // Note: we don't yet support dragging of primaries
                    return null;
                }

                configState = ConfigState.DRAG_ACTIVE;
                action = new Action(ActionType.DRAG_START);
                dragStartScreen = new Point(me.getX(), me.getY());
                dragStartWorld = buttonEvent.getIntersectionPointWorld();
                configDragType = ConfigDragType.MOVING_PLANAR;

                dragStartLocal = parentView.getNode().worldToLocal(dragStartWorld, new Vector3f());
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

                // Convert from world to parent coordinates.
                Node viewNode = ((View2DEntity)view.getParent()).getNode();
                Vector3f curWorld = dragStartWorld.add(dragVectorWorld, new Vector3f());
                Vector3f curLocal = viewNode.worldToLocal(curWorld, new Vector3f());
                dragVectorLocal = curLocal.subtract(dragStartLocal);
            }
            return action;

        case MouseEvent.MOUSE_RELEASED:
            if (me.getButton() == MouseEvent.BUTTON1) {
                if (configState == ConfigState.DRAGGING) {
                    // Note: the misc action ToggleControl may produce an mouse
                    // press/release without a drag in between so only perform
                    // a dragfinish if an actual drag occurred between the
                    // mouse press and release
                    action = new Action(ActionType.DRAG_FINISH);

                } else if (configDragType == ConfigDragType.MOVING_PLANAR &&
                           me.getModifiersEx() == 0) {

                    // Note: A bit of uncleanliness: Even though we haven't been
                    // actually dragging we still need to restore the cursor to
                    // its original form.
                    view.userMovePlanarFinish();
                }

                configState = ConfigState.IDLE;
                // Note: the coordinates for WL mouse release events are invalid.
                // So we just use the coordinates from the last drag or press.
            }
            return action;
        }

        return null;
    }

    /** 
     * Perform a view configuration action. That is, one of:
     * <br><br>
     *     + Planar move (move within the cell local z=0 plane).
     * <br>
     *     + Z move (move along the cell local z axis).
     * <br>
     *     + Y rotation.
     *
     * @param action The configuration action the given event provokes.
     * @param me The AWT event for this 3D mouse event.
     * @param me3d The 3D mouse event.
     */
    protected void performConfigAction(Action action, MouseEvent me, MouseEvent3D me3d) {

        switch (action.type) {

        case DRAG_START:
            switch (configDragType) {
            case MOVING_PLANAR:
                App2D.invokeLater(new Runnable() {
                    public void run () {
                        view.userMovePlanarStart();
                    }
                });
                break;
            }
            break;

        case DRAG_UPDATE:
            switch (configDragType) {
            case MOVING_PLANAR:
                App2D.invokeLater(new Runnable() {
                    public void run () {
                        view.userMovePlanarUpdate(new Vector2f(dragVectorLocal.x, dragVectorLocal.y));
                    }
                });
                break;
            }
            break;

        case DRAG_FINISH:
            switch (configDragType) {
            case MOVING_PLANAR:
                App2D.invokeLater(new Runnable() {
                    public void run () {
                        view.userMovePlanarFinish();
                    }
                });
                break;
            }
            break;

        default:
            throw new RuntimeException("Unrecognized action");
        }
    }

    /**
     * Determine if this is an action which moves the window to the front of the stack.
     *
     * @param me The AWT event corresponding to a 3D mouse event which has been received.
     */
    protected Action determineIfToFrontAction(MouseEvent me) {
        if (me.getID() == MouseEvent.MOUSE_CLICKED &&
                me.getButton() == MouseEvent.BUTTON1 &&
                me.getModifiersEx() == 0) {
            return new Action(ActionType.TO_FRONT);
        } else {
            return null;
        }
    }
}
