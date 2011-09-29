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
import java.util.LinkedList;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.jme.input.MouseEnterExitEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.view.Gui2D;
import javax.swing.SwingUtilities;

/**
 * The GUI code for the frame close button.
 *
 * @author deronj
 */
@ExperimentalAPI
class Gui2DCloseButton extends Gui2D {

    /** The associated close button */
    protected FrameCloseButton closeButton;

    /**
     * Create a new instance of Gui2DInterior.
     *
     * @param view The view associated with the component that uses this Gui.
     */
    public Gui2DCloseButton(View2DCell view) {
        super(view);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        super.cleanup();
        closeButton = null;
    }

    /**
     * Specify the FrameCloseButton component for which this Gui provides behavior.
     *
     * @param closeButton The close button component.
     */
    public void setComponent(FrameCloseButton closeButton) {
        this.closeButton = closeButton;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void attachMouseListener(Entity entity) {
        mouseListener = new CloseButtonMouseListener();
        mouseListener.addToEntity(entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void detachMouseListener(Entity entity) {
        if (mouseListener != null && entity != null) {
            mouseListener.removeFromEntity(entity);
        }
    }

    /**
     * The listener for mouse events.
     */
    protected class CloseButtonMouseListener extends Gui2D.MouseListener {

        /**
         * {@inheritDoc}
         */
        @Override
        public void commitEvent(Event event) {
            Action action;

            MouseEvent3D me3d = (MouseEvent3D) event;
            MouseEvent me = (MouseEvent) me3d.getAwtEvent();

            // Support closing only when user has control
            action = determineIfCloseAction(me, me3d);
            if (action != null) {
                performCloseAction(action);
                return;
            }

            // Note: config events are not recognized on the close button

            action = determineIfMiscAction(me, me3d);
            if (action != null) {
                performMiscAction(action, me, me3d);
                return;
            }
        }
    }

    /**
     * Determine whether this is a close button action. Close button actions
     * are only recognized when the user has control of the window.
     *
     * @param me The AWT event for this 3D mouse event.
     * @param me3d The 3D mouse event.
     */
    protected Action determineIfCloseAction(MouseEvent me, MouseEvent3D me3d) {
        if (!view.getWindow().getApp().getControlArb().hasControl()) {
            return null;
        }

        if (me3d instanceof MouseEnterExitEvent3D) {
            if (((MouseEnterExitEvent3D) me3d).isEnter()) {
                return new Action(ActionType.CLOSE_BUTTON_ENTER);
            } else {
                return new Action(ActionType.CLOSE_BUTTON_EXIT);
            }
        }

        if (me.getID() == MouseEvent.MOUSE_CLICKED &&
                me.getButton() == MouseEvent.BUTTON1) {
            return new Action(ActionType.CLOSE_BUTTON_PRESSED);
        }

        return null;
    }

    /**
     * Process the close button action.
     *
     * @param action The close action the given event provokes.
     * @param me The AWT event for this 3D mouse event.
     * @param me3d The 3D mouse event.
     */
    protected void performCloseAction(Action action) {
        switch (action.type) {

            case CLOSE_BUTTON_ENTER:
                closeButton.setMouseInside(true);
                break;

            case CLOSE_BUTTON_EXIT:
                closeButton.setMouseInside(true);
                break;

            case CLOSE_BUTTON_PRESSED:
                SwingUtilities.invokeLater(new Runnable () {
                    public void run () {
                        notifyAllListeners();
                    }
                });
                break;
        }
    }

    /**
     * Notify all listeners that the close button has been pressed.
     */
    protected void notifyAllListeners() {
        LinkedList<Frame2DCell.CloseListener> listeners = closeButton.getCloseListeners();
        for (Frame2DCell.CloseListener listener : listeners) {
            listener.close();
        }
    }
}
