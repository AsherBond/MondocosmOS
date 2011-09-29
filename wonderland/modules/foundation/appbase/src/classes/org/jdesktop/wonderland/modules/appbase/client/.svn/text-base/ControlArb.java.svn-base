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
package org.jdesktop.wonderland.modules.appbase.client;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * A user input control arbiter. Implementations of this interface
 * decide how application control is to be delegated among users.
 * All ControlArbs support the notion of one or more users controlling
 * an app and the rest of the users not controlling the app. When
 * a user has control of an app user input events are sent from
 * app windows to the app. When a user does not control an app 
 * user input events are ignored by its windows.
 *
 * @author deronj
 */
@ExperimentalAPI
public abstract class ControlArb {

    private static final Logger logger = Logger.getLogger(ControlArb.class.getName());

    /** A list of controllers in the Wonderland client session */
    private static final LinkedList<ControlArb> controlArbs = new LinkedList<ControlArb>();
    /** A list of components to notify of a state change in the control arb */
    protected final LinkedList<ControlChangeListener> listeners = new LinkedList<ControlChangeListener>();
    /** The application controlled by this arbiter */
    protected App2D app;
    /** Has the user enabled app control? */
    protected boolean appControl;
    /** When false, this control arb will be released during a call to releaseControlAll. */
    private boolean releaseWithAll = true;

    /** 
     * The interface that components interested in being notified of a state change in the control arb 
     * must implement.
     */
    public interface ControlChangeListener {

        /**
         * The state of a control arb you are subscribed to may have changed. The state of whether this user 
         * has control or the current set of controlling users may have changed.
         *
         * @param controlArb The control arb that changed.
         */
        public void updateControl(ControlArb controlArb);
    }

    /** 
     * Create a new instance of ControlArb.
     */
    public ControlArb() {
        synchronized (controlArbs) {
            controlArbs.add(this);
        }
    }

    /**
     * Clean up resources held.
     *
     * THREAD USAGE NOTE: Called only by App2D.cleanup. This is sometimes called on the EDT 
     * (e.g.HeaderPanel close button)and sometimes called off the EDT (e.g. App2DCell.setStatus, 
     * SasXreminProviderMain.stop). Do not call this while holding any app base locks.
     */
    public void cleanup() {
        if (app == null) return;

        synchronized (app.getAppCleanupLock()) {
            synchronized (controlArbs) {
                controlArbs.remove(this);
            }
            listeners.clear();
        }

        // Must be done outside the app cleanup lock.
        if (hasControl()) {
            releaseControl();
        }
    }

    /**
     * Specify the app the controlArb controls.
     *
     * @param app The app.
     */
    public void setApp(App2D app) {
        this.app = app;
    }

    /**
     * Return the app the controlArb controls.
     */
    public App2D getApp() {
        return app;
    }

    /**
     * This attribute controls whether this control arb is released as a part
     * of the <code>releaseControlAll</code> method. If the argument is true,
     * this control arb is released along with all others. Otherwise it is 
     * not released. Default: true. You should set this to false for a 
     * control arb you want to always have control. For example: the HUD control arb.
     */
    public void setReleaseWithAll (boolean releaseWithAll) {
        this.releaseWithAll = releaseWithAll;
    }

    /**
     * Returns the value of the releaseWithAll attribute.
     */
    public boolean getReleaseWithAll () {
        return releaseWithAll;
    }

    /**
     * Is the user of this client currently an controller of the ControlArb's app?
     */
    public boolean hasControl() {
        return appControl;
    }

    /**
     * Tell the arbiter that this user is take control of the app.
     * Note that the attempt to take control may be refused. This method
     * doesn't report whether the attempt succeeded or failed--this is
     * is reported via the controller change listener.
     *
     * Note: depending on the implementation, this may cause other users with control 
     * to lose it.
     *
     * THREAD USAGE NOTE: Can be called either on the EDT or off it.
     */
    public void takeControl () {
        if (app == null) return;
        if (!app.isInSas()) {
            if (SwingUtilities.isEventDispatchThread()) {
                takeControlPerform();
            } else {
                try {
                    SwingUtilities.invokeLater(new Runnable () {
                        public void run () {
                            takeControlPerform();
                        }
                    });
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    /**
     * THREAD USAGE NOTE: Same as takeControl, but must always be called on the EDT.
     */
    public void takeControlPerform() {
        if (!hasControl()) {
            logger.info("Took control");
            appControl = true;
            updateControl();
        } 
    }

    /**
     * Tell the arbiter that you are releasing control of the app.
     *
     * THREAD USAGE NOTE: Can be called either on the EDT or off it.
     */
    public void releaseControl () {
        if (app == null) return;
        if (!app.isInSas()) {
            if (SwingUtilities.isEventDispatchThread()) {
                releaseControlPerform();
            } else {
                try {
                    SwingUtilities.invokeLater(new Runnable () {
                        public void run () {
                            releaseControlPerform();
                        }
                    });
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    /**
     * THREAD USAGE NOTE: Same as takeControl, but must always be called on the EDT.
     */
    public void releaseControlPerform() {
        if (hasControl()) {
            appControl = false;
            logger.info("Released control");
            updateControl();
        }
    }

    /**
     * Release control of all applications in the Wonderland client session.
     * THREAD USAGE NOTE: This is called on the App invoker thread.
     */
    public static void releaseControlAll() {

        // This method is implemented this way because releasing control might cause the 
        // removeControlAllButton to disappear. Currently, the HUD actually (and probably 
        // spuriously creates a window when this happens. This can result in a new HUD app,
        // and a new control arb being created. This results in a ConcurrentComodification
        // exception

        LinkedList<ControlArb> controlArbsCopy;
        synchronized (controlArbs) {
            controlArbsCopy = (LinkedList<ControlArb>) controlArbs.clone();
        }
        for (ControlArb controlArb : controlArbsCopy) {
            if (controlArb.getReleaseWithAll() && controlArb.hasControl()) {
                // Note: this will go back on the EDT to perform its task.
                controlArb.releaseControl();
            }
        }
    }

    /**
     * Returns the current controlling users.
     * @return An array of user names who are currently controlling this control arb's app.
     * This array is null if there are currently no controlling users.
     */
    public String[] getControllers() {
        return null;
    }

    /**
     * Add a control change listener. The listener will be called on the EDT.
     * 
     * @param listener The control change listener.
     */
    public synchronized void addListener(ControlChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a controller change listener.
     * 
     * @param listener The control change listener.
     */
    public void removeListener(ControlChangeListener listener) {
        if (app == null) return;
        synchronized (app.getAppCleanupLock()) {
            synchronized (this) {
                listeners.remove(listener);
            }
        }
    }

    /**
     * Returns an iterator over all control change listeners.
     */
    public Iterator<ControlChangeListener> getListeners() {
        return listeners.iterator();
    }

    /**
     * Send a key event to an app window, if the user has control.
     * NOTE: on the slave, this must be called on the EDT.
     *
     * @param window The window to which to send the event.
     * @param event The event to send.
     */
    public void deliverEvent(Window2D window, KeyEvent event) {
        if (hasControl()) {
            window.deliverEvent(event);
        }
    }

    /**
     * Send a mouse event to an app window, if the user has control.
     * NOTE: on the slave, this must be called on the EDT.
     *
     * @param window The window to which to send the event.
     * @param event The event to send.
     */
    public void deliverEvent(Window2D window, MouseEvent event) {
        if (hasControl()) {
            window.deliverEvent(event);
        }
    }

    /**
     * Informs the control change listeners that the control arb state has been updated.
     * THREAD USAGE NOTE: Must be called on the EDT.
     */
    protected void updateControl() {
        LinkedList<ControlChangeListener> listenersCopy;
        synchronized (listeners) {
            listenersCopy = (LinkedList<ControlChangeListener>) listeners.clone();
        }
        for (ControlChangeListener listener : listenersCopy) {
            listener.updateControl(this);
        }
    }

    /**
     * Informs the control change listeners of all control arbs that the state has been updated.
     * THREAD USAGE NOTE: Must be called on the EDT.
     */
    protected static void updateControlAll() {
        synchronized (controlArbs) {
            for (ControlArb controlArb : controlArbs) {
                controlArb.updateControl();
            }
        }
    }
}
