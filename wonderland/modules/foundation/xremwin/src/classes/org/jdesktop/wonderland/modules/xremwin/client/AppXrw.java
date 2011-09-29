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

import com.jme.math.Vector2f;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.modules.appbase.client.AppConventional;
import org.jdesktop.wonderland.modules.appbase.client.ControlArb;
import org.jdesktop.wonderland.modules.appbase.client.Window2D;

/**
 * An X11 app which receives its window contents from the Xremwin server.
 * This is the superclass for both AppXrwMaster and AppXrwSlave.
 *
 * @author deronj
 */
@InternalAPI
public abstract class AppXrw extends AppConventional {

    /** The logger for app.modules.xremwin */
    static final Logger logger = Logger.getLogger(AppXrw.class.getName());
    /** A mapping of wids to the corresponding windows */
    final HashMap<Integer, WindowXrw> widToWindow = new HashMap<Integer, WindowXrw>();
    /** The Xremwin protocol interpreter -- Set it subclass constructor */
    protected ClientXrw client;

    /** 
     ** The order in which top-level windows have been made visible in this app, in the order 
     ** least recently shown to most recently shown. (Note: this is opposite the order of 0.4).
     */
    private LinkedList<WindowXrw> windowVisibleOrder = new LinkedList<WindowXrw>();

    // TODO: temporary: until winTransientFor: used to determine parents of popups
    private WindowXrw currentPointerWindow;

    /**
     * Create a instance of AppXRW with a generated ID.
     *
     * @param appName The name of the app.
     * @param controlArb The control arbiter to use. null means that all users can control at the same time.
     * @param pixelScale The size of the window pixels.
     */
    public AppXrw(String appName, ControlArb controlArb, Vector2f pixelScale) {
        super(appName, controlArb, pixelScale);
    }

    /**
     * Create a new WindowXrw instance and its "World" view.
     *
     * @param app The application to which this window belongs.
     * @param x The X11 x coordinate of the top-left corner window.
     * @param y The X11 y coordinate of the top-left corner window.
     * @param borderWidth The X11 border width.
     * @param decorated Whether the window is decorated with a frame.
     * @param wid The X11 window ID.
     */
    public WindowXrw createWindow(int x, int y, int width, int height, int borderWidth, boolean decorated,
            int wid) {
        WindowXrw window = null;
        try {
            window = new WindowXrw(this, x, y, width, height, borderWidth, decorated, getPixelScale(), wid);
            return window;
        } catch (InstantiationException ex) {
            return null;
        }
    }

    /**
     * Clean up resources.
     */
    @Override
    public void cleanup() {
        super.cleanup();

        // Note: we may not always receive explicit DestroyWindow messages
        // for all windows. So destroy any that are left over.
        LinkedList<WindowXrw> toRemove = new LinkedList<WindowXrw>();
        synchronized (widToWindow) {
            for (int wid : widToWindow.keySet()) {
                WindowXrw window = getWindowForWid(wid);
                if (window != null) {
                    toRemove.add(window);
                }
            }
            widToWindow.clear();
        }
        for (WindowXrw window : toRemove) {
            window.cleanup();
        }

        windowVisibleOrder.clear();
    }

    /**
     * Returns the client.
     */
    @InternalAPI
    public ClientXrw getClient() {
        return client;
    }

    /**
     * Get the window this window is a transient for.
     * Returns 0 if the window isn't a transient.
     *
     * @param wid The window whose transient window we want.
     * @return The window ID of the window which is transient
     * for the given window.
     */
    int getTransientForWid(int wid) {
        // TODO: implement
        return 0;
    }

    /**
     * Return whether this app is the master or a slave.
     */
    public abstract boolean isMaster();

    /**
     * Track when the visibility of a window changes so we can determine order in which 
     * windows are made visible. This is because the oldest made visible window is chosen as
     * the primary window. Popup windows and windows of unknown type are ignored by this method.
     */
    void trackWindowVisibility(WindowXrw window) {
        if (!window.isDecorated()) {
            return;
        }

        windowVisibleOrder.remove(window);

        if (window.isVisibleApp()) {

            // TODO: temporary: until winTransientFor: used to determine parents of popups
            // Fix for bug 43: we've got to initialize the current pointer window with something 
            // on the slave side
            if (!isMaster() && getCurrentPointerWindow() == null) {
                setCurrentPointerWindow(window);
            }

            // Remember that this window has been shown. Remove to the end of the list.
            windowVisibleOrder.addLast(window);
            logger.info("Most recently visible window = " + window);
        }

        selectPrimaryWindow();
    }

    /** {@inheritDoc} */
    @Override
    public void removeWindow(Window2D window) {
        super.removeWindow(window);

        // TODO: temporary: until winTransientFor: used to determine parents of popups
        if (currentPointerWindow == window) {
            currentPointerWindow = null;
        }

        selectPrimaryWindow();
    }

    // TODO: temporary: until winTransientFor: used to determine parents of popups
    // NOTE: this is called on the EDT
    public void setCurrentPointerWindow(WindowXrw window) {
        currentPointerWindow = window;
        logger.info("Current pointer window = " + window);
    }

    // TODO: temporary: until winTransientFor: used to determine parents of popups
    public WindowXrw getCurrentPointerWindow() {
        return currentPointerWindow;
    }

    private void selectPrimaryWindow() {
        WindowXrw primaryWindow = (WindowXrw) getPrimaryWindow();
        logger.info("selectPrimaryWindow: current primary = " + primaryWindow);
        if (primaryWindow == null || !primaryWindow.isVisibleApp()) {
            // Select the oldest made visible top-level window as the new primary window
            WindowXrw oldestVisibleWindow = null;
            try {
                oldestVisibleWindow = windowVisibleOrder.getFirst();
            } catch (NoSuchElementException ex) {
            }
            logger.info("oldestVisibleWindow = " + oldestVisibleWindow);
            if (oldestVisibleWindow != null) {
                try {
                    if (primaryWindow != null) {
                        // Old primary is invisible. Since (currently) only one primary window is 
                        // allowed at a time we must make the old primary non-primary
                        // TODO: someday: maybe allow more than one primary at a time
                        setPrimaryWindow(null);
                        primaryWindow.setType(Window2D.Type.SECONDARY, true);
                    }
                    oldestVisibleWindow.setType(Window2D.Type.PRIMARY);               
                    logger.info("Made oldest visible window primary");
                    logger.info("New primary window = " + oldestVisibleWindow);
                } catch (IllegalStateException ise) {
                    RuntimeException re = new RuntimeException("Making window " + oldestVisibleWindow.getWid() + 
                                                               " primary caused exception");
                    re.initCause(ise);
                    throw re;
                }
            }
        }
    }

    WindowXrw getWindowForWid (int wid) {
        return widToWindow.get(wid);
    }

    void addWindow (int wid, WindowXrw window) {
        synchronized (widToWindow) {
            widToWindow.put(wid, window);
        }
    }

    void removeWindow (int wid) {
        synchronized (widToWindow) {
            widToWindow.remove(wid);
        }
    }
}


