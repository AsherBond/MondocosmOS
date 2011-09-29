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

import java.awt.event.MouseEvent;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.view.Gui2D;
import org.jdesktop.wonderland.modules.appbase.client.App2D;

/**
 * The GUI code for a side of a frame.
 *
 * @author deronj
 */
@ExperimentalAPI
class Gui2DSide extends Gui2D {

    /** For Debug: A listener for key events 
    protected SideKeyListener keyListener;
    */

    /** 
     * Create a new instance of Gui2DSide.
     *
     * @param view The view for which the Gui provides behavior.
     */
    Gui2DSide(View2DCell view) {
        super(view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void attachMouseListener(Entity entity) {
        mouseListener = new SideMouseListener(entity);
        mouseListener.addToEntity(entity);
    }

    /**
     * The mouse listener for this GUI.
     */
    protected class SideMouseListener extends Gui2D.MouseListener {

        // The entity to which this listener is attached.
        private Entity entity;

        /** Create a new instance of SideMouseListener. */
        public SideMouseListener (Entity entity) {
            this.entity = entity;
        }

        /** {@inheritDoc} */
        @Override
        public void commitEvent(Event event) {
            Action action;

            MouseEvent3D me3d = (MouseEvent3D) event;
            MouseEvent me = (MouseEvent) me3d.getAwtEvent();

            // We only recognize some events on the border when user has control
            if (view == null || view.getWindow() == null ||
                view.getWindow().getApp() == null || view.getWindow().getApp().getControlArb() == null) {
                return;
            }                
            if (view.getWindow().getApp().getControlArb().hasControl()) {

                // Is this a Window menu event?
                if (me.getID() == MouseEvent.MOUSE_PRESSED &&
                    me.getButton() == MouseEvent.BUTTON3 &&
                    me.getModifiersEx() == MouseEvent.BUTTON3_DOWN_MASK) {
                    ((View2DCell)getView()).getWindow().displayWindowMenu(entity, me);
                    return;
                }

                action = determineIfConfigAction(me, me3d);
                if (action != null) {
                    performConfigAction(action, me, me3d);
                    return;
                }
            }

            action = determineIfMiscAction(me, me3d);
            if (action != null) {
                performMiscAction(action, me, me3d);
                return;
            }
        }

        /** {@inheritDoc} */
        public boolean propagatesToParent (Event event) {
            return false;
        }
    }

    /**
     * For Debug: Register this Gui's key listener on the given entity.
     */
    @Override
    protected void attachKeyListener(Entity entity) {
        // For Debug
        //keyListener = new SideKeyListener();
        //keyListener.addToEntity(entity);
    }

    /**
     * For Debug: Remove this Gui's key listener from its assigned entity.
     */
    @Override
    protected void detachKeyListener(Entity entity) {
        // For Debug
        //if (keyListener != null && entity != null) {
        //    keyListener.removeFromEntity(entity);
        //}
    }

    /**
     * Determine if this is a window configuration action.
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

        return super.determineIfConfigAction(me, me3d);
    }

    /**
     * Perform the window configuration action.
     * @param action The configuration action the given event provokes.
     * @param me The AWT event for this 3D mouse event.
     * @param me3d The 3D mouse event.
     */
    @Override
    protected void performConfigAction(Action action, MouseEvent me, MouseEvent3D me3d) {
        if (action.type == ActionType.TO_FRONT) {
            App2D.invokeLater(new Runnable() {
                public void run () {
                    Gui2DSide.this.view.getWindow().restackToTop();
                }
            });
            return;
        }

        super.performConfigAction(action, me, me3d);
    }
}
