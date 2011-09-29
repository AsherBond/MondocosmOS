/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

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
package org.jdesktop.wonderland.modules.xremwin.client;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.IOException;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.ControlArbSingle;
import org.jdesktop.wonderland.modules.appbase.client.Window2D;

/**
 * The Xremwin ControlArb class. 
 *
 * TODO: someday: This currently doesn't implement polite control 
 * arbitration--control is simply stolen.
 *
 * @author deronj
 */
@ExperimentalAPI
public class ControlArbXrw extends ControlArbSingle {

    /** The default take control politeness mode */
    private static boolean TAKE_CONTROL_IMPOLITE = true;
    /** The server the client talks to */
    protected ServerProxy serverProxy;
    /** A take control request is pending */
    protected boolean takeControlPending;
    /** The politeness of the pending take control */
    protected boolean takeControlPendingImpolite;
   
    /** 
     * It is okay to send events when this is enabled. This not 
     * necessarily the same as hasControl. For example, we allow
     * events to be sent to the server while a take control is
     * pending (this is called "event-ahead"). If control is 
     * subsequently refused the event-ahead events will be ignored.
     */
    protected boolean eventsEnabled;

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        super.cleanup();

        eventsEnabled = false;
        takeControlPending = false;
        takeControlPendingImpolite = false;
        serverProxy = null;
    }

    /**
     * Attach a server proxy to this control arb. The control arb forwards events to the server proxy.
     * @param serverProxy The server proxy to which to attach this ControlArbXrw.
     */
    public void setServerProxy(ServerProxy serverProxy) {
        this.serverProxy = serverProxy;
    }

    /**
     * {@inheritDoc}
     * THREAD USAGE NOTE: Must be called on EDT.
     */
    @Override
    public void takeControlPerform() {
        if (!hasControl()) {
            super.takeControlPerform();
            take(TAKE_CONTROL_IMPOLITE);
        }
    }

    /**
     * {@inheritDoc}
     * THREAD USAGE NOTE: Must be called on EDT.
     */
    @Override
    public void releaseControlPerform() {
        if (hasControl()) {
            release();
            super.releaseControlPerform();
        }
    }

    /**
     * Make sure the main canvas always has focus. The main canvas needs focus
     * for key presses to be delivered to the Xrw windows.
     * @param hasControl whether or not we have control.
     */
    @Override
    protected void updateKeyFocus(boolean hasControl) {
        JmeClientMain.getFrame().getCanvas().setFocusable(true);
    }

    /**
     * Take control with the specified politeness. Tell the server that this user 
     * wants to take control of the app. If this succeeds we will receive a message 
     * from the server indicating success. At that time the setController method of this 
     * controlArb will be called with the user name of this client. 
     *
     * THREAD USAGE NOTE: Must be called on EDT.
     */
    private void take(boolean impolite) {
        if (serverProxy == null) return;

        AppXrw.logger.info("Enter take");

        // Enable our client to send events to the server ("event ahead").
        // If control is refused the events will just be ignored.
        eventsEnabled = true;

        takeControlPending = true;
        takeControlPendingImpolite = impolite;

        try {
            serverProxy.writeTakeControl(impolite);
        } catch (IOException ex) {
            eventsEnabled = false;
            takeControlPending = false;
        }
    }

    /**
     * Tell the server to release control.
     *
     * THREAD USAGE NOTE: Must be called on EDT.
     */
    private void release() {
        if (serverProxy == null) return;

        AppXrw.logger.info("Enter release");

        eventsEnabled = false;
        takeControlPending = false;

        try {
            if (serverProxy != null) {
                serverProxy.writeReleaseControl();
            }
        } catch (IOException ex) {
        }

        setController(null);
    }

    /** 
     * The server has refused our request for control. If our first
     * attempt was polite get confirmation from the user to continue.
     *
     * THREAD USAGE NOTE: Must be called on EDT.
     */
    void controlRefused() {
        if (serverProxy == null) return;

        AppXrw.logger.info("Control refused");

        String currentController = serverProxy.getControllingUser();

        if (!takeControlPending) {
            // We weren't expecting this. We shouldn't have control.
            // Make sure we don't have it
            controlError("refused", currentController);
            releaseControl();
            return;
        }

        if (takeControlPending && !takeControlPendingImpolite) {
            if (takeControlConfirm(currentController)) {
                // User confirmed. Try again, this time steal control.
                take(true);
                return;
            }
        }

        releaseControl();
        takeControlPending = false;
        eventsEnabled = false;
        appControl = false;

        setController(currentController);
    }

    /** 
     * The server has told us that our request for control has succeeded. 
     *
     * THREAD USAGE NOTE: Must be called on EDT.
     */
    void controlGained() {
        AppXrw.logger.info("Control gained");

        String currentController = serverProxy.getControllingUser();

        if (!takeControlPending) {
            // We weren't expecting this. We shouldn't have control. Give it up.
            controlError("refused", currentController);
            releaseControl();
            return;
        }

        takeControlPending = false;
        appControl = true;

        setController(currentController);
    }

    /**
     * The server has taken control away from us.
     *
     * THREAD USAGE NOTE: Must be called on EDT.
     */
    void controlLost() {
        AppXrw.logger.info("Control lost");
        super.releaseControlPerform();
        takeControlPending = false;
        eventsEnabled = false;
        appControl = false;
        setController(null);
        updateControl();
    }

    // TODO: not yet implemented
    private boolean takeControlConfirm(String controller) {
        // TODO: bring up dialog message:
        // "User <controller>) already has control. Take control anyway?
        // With buttons yes/no
        // Return true if yes, false if no
        return false;
    }

    /**
     * Report an error in control handling. This indicates an 
     * internal error in app.base.
     */
    private static void controlError(String errTypeStr, String currentController) {
        AppXrw.logger.warning("TakeControl: control was " + errTypeStr + " when we didn't ask for it.");
        AppXrw.logger.warning("TakeControl: current controller = " + currentController);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deliverEvent(Window2D window, KeyEvent event) {
        if (!eventsEnabled || serverProxy == null) {
            return;
        }

        // The server doesn't care about typed events
        if (event.getID() == KeyEvent.KEY_TYPED) {
            return;
        }

        try {
            AppXrw.logger.finer("Write key event to server");
            // Note: server doesn't care about the window which generated key events
            if (serverProxy != null) {
                serverProxy.writeEvent(event);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deliverEvent(Window2D window, MouseEvent event) {
        if (!eventsEnabled || serverProxy == null) {
            return;
        }

        // The Xremwin server doesn't care about clicked events. Ignore them.
        if (event.getID() == MouseEvent.MOUSE_CLICKED) {
            return;
        }

        if (serverProxy == null) return;

        try {
            if (event instanceof MouseWheelEvent) {
                AppXrw.logger.finer("Write mouse wheel event to server");
                serverProxy.writeWheelEvent(((WindowXrw) window).getWid(), (MouseWheelEvent) event);
            } else {
                AppXrw.logger.finer("Write mouse event to server");
                serverProxy.writeEvent(((WindowXrw) window).getWid(), event);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
