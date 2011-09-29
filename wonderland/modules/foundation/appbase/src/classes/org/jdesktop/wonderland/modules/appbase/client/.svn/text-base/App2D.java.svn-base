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

import com.jme.math.Vector2f;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.modules.appbase.client.cell.view.View2DCellFactory;
import org.jdesktop.wonderland.modules.appbase.client.view.View2DDisplayer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.LinkedList;

/**
 * The generic 2D application superclass. All 2D apps in Wonderland have this
 * root class.
 * <br><br>
 * 2D apps provide a window stack in which to arrange their visible windows.
 * This stack is a list. The top window in the stack is first in the list.
 * The bottom window is the last in the list. 
 * <br><br>
 * The stack position index of the top window is N-1, where N is the number of windows. 
 * The stack position of the bottom window is 0.
 *
 * In order to display this app you must add a displayer to it. Once added, as windows of the
 * app become visible they will be displayed in the displayer.
 *
 * @author deronj
 */
@ExperimentalAPI
public abstract class App2D {

    private static final Logger logger = Logger.getLogger(App2D.class.getName());

    // TODO: Part 1: temporary. This gives the ability to disable app-specific placement.
    // The other part of the boolean is in XAppCellFactory.
    public static final boolean doAppInitialPlacement = true;

    /** All of the apps which have been created by this client. */
    private static final LinkedList<App2D> apps = new LinkedList<App2D>();

    /** The global default appbase View2DCell factory.*/
    private static View2DCellFactory view2DCellFactory;

    /** The window stack for this app */
    private WindowStack stack = new WindowStack();

    /** The world size of pixels */
    protected Vector2f pixelScale;

    /** The primary window of this app. */
    private Window2D primaryWindow;

    /** The list of all windows created by this app */
    protected LinkedList<Window2D> windows = new LinkedList<Window2D>();

    /** The control arbiter for this app. */
    protected ControlArb controlArb;

    /** The focus entity of the app. */
    protected Entity focusEntity;

    /** The name of the app. */
    private String name;

    /** The set of all views of the windows of this app. */
    private View2DSet viewSet;

    /** Whether to display the app in the HUD. */
    private boolean showInHUD;

    /** The displayer for apps-in-HUD. */
    private HUDDisplayer hudDisplayer;

    /**
     * The first-visible initializer for this app. If non-null, this will perform
     * some sort of initialization for the app the first time a window is made visible.
     */
    private FirstVisibleInitializer fvi;

    /**
     * False if shutdown() has been called. If true, the app base is running. If false, it
     * has been shut down by a Java runtime hook.
     */
    private static boolean isAppBaseRunning = true;

    /**
     * There are some deadlock situations during app cleanup. I would prefer to address this
     * by replacing all app base locks with a single app-wide lock, but that is to big a change
     * to do at this point. For now, we'll just use this lock to force the cleanup process to be
     * single threaded.
     */
    private final Integer appCleanupLock = new Integer(0);

    /**
     * A singleton thread which the app base uses to offload things from the Render Thread that
     * shouldn't be done on the EDT. This thread provides serialization of execution
     * like the EDT, so it can run MT unsafe code. But, unlike the EDT, it is okay to 
     * acquire appbase object locks on this thread. 
     */
    private static Thread invoker;

    /** A queue used by the invoker thread. */
    //private static LinkedBlockingQueue<Runnable> invokeLaterQueue = new LinkedBlockingQueue<Runnable>();
    private static LinkedList<Runnable> invokeLaterQueue = new LinkedList<Runnable>();

    /** A flag which stops the invoker thread. */
    private static boolean stopInvoker = false;

    // Register the appbase shutdown hook
    static {

        Runtime.getRuntime().addShutdownHook(new Thread("App Base Shutdown Hook") {
            @Override
            public void run() { App2D.shutdown(); }
        });

        // Start the invoker thread
        invoker = new Thread(new Invoker(), "Invoker Thread for App Base");
        invoker.start();
    }

    /**
     * Set the default View2DCell factory to be used for all apps in this client. (Called by 
     * AppClientPlugin.initialize).
     */
    static void setView2DCellFactory (View2DCellFactory vFactory) {
        view2DCellFactory = vFactory;
    }

    /** Returns the default View2DCell factory. */
    public static View2DCellFactory getView2DCellFactory () {
        return view2DCellFactory;
    }

    /** Returns whether the app is running in a SAS Provider. */
    public static boolean isInSas () {
        return getView2DCellFactory() == null;
    }

    /**
     * Create a new instance of App2D with a default name..
     *
     * @param controlArb The control arbiter to use. Must be non-null.
     * @param pixelScale The size of the window pixels in world coordinates.
     */
    public App2D(ControlArb controlArb, Vector2f pixelScale) {
        this(null, controlArb, pixelScale);
    }

    /**
     * Create a new instance of App2D with the given name.
     *
     * @param name The name of the app.
     * @param controlArb The control arbiter to use. Must be non-null.
     * @param pixelScale The size of the window pixels in world coordinates.
     */
    public App2D(String name, ControlArb controlArb, Vector2f pixelScale) {
        this.name = name;
        if (controlArb == null) {
            throw new RuntimeException("controlArb argument must be non-null.");
        }
        this.controlArb = controlArb;
        this.pixelScale = pixelScale;
        focusEntity = new Entity("App focus entity for app " + getName());

        synchronized(apps) {
            apps.add(this);
        }

        viewSet = new View2DSet(this);
    }

    /**
     * Deallocate resources.
     *
     * THREAD USAGE NOTE: This is sometimes called on the EDT (e.g.HeaderPanel close button)
     * and sometimes called off the EDT (e.g. App2DCell.setStatus, SasXreminProviderMain.stop).
     * Do not call this while holding any app base locks.
     */
    public void cleanup() {

        // Must be done outside the app cleanup lock.
        if (controlArb != null) {
            controlArb.cleanup();
            controlArb = null;
        }

        synchronized (appCleanupLock) {
            setShowInHUD(false);
            viewSet.cleanup();
            stack.cleanup();

            LinkedList<Window2D> windowsCopy;
            synchronized (this) {
                windowsCopy = (LinkedList<Window2D>) windows.clone();
                windows.clear();
            }

            for (Window2D window : windowsCopy) {
                window.cleanup();
            }

            pixelScale = null;
        }

        synchronized(apps) {
            apps.remove(this);
        }
    }

    // Signal the invoker to stop and wake it up so it actually stops.
    private static void stopInvokerThread () {
        stopInvoker = true;
        invokeLater(new Runnable () {
            public void run () {
                // Do nothing
            }
        });
        invoker = null;
    }

    /** INTERNAL ONLY. */
    @InternalAPI
    public Object getAppCleanupLock () {
        return appCleanupLock;
    }

    /** 
     * Returns the pixel scale 
     */
    public Vector2f getPixelScale() {
        // Note: pixelScale may be null (e.g. on the SAS)
        if (pixelScale == null) return null;
        return new Vector2f(pixelScale);
    }

    WindowStack getWindowStack () {
        return stack;
    }

    /**
     * Add a new displayer to this app. This displays all existing windows of the
     * app in the displayer.
     */
    public void addDisplayer (View2DDisplayer displayer) {
        viewSet.add(displayer);
    }

    /**
     * Remove a displayer to this app.
     */
    public void removeDisplayer (View2DDisplayer displayer) {
        synchronized (appCleanupLock) {
            viewSet.remove(displayer);
        }
    }

    /**
     * Returns an iterator over all the displayers of this app.
     */
    public Iterator<View2DDisplayer> getDisplayers() {
        return viewSet.getDisplayers();
    }
    /**
     * Add a window to this app. 
     * @param window The window to add.
     */
    public void addWindow(Window2D window) {
        synchronized (this) {
            windows.add(window);
        }
        viewSet.add(window);
    }

    /**
     * Remove the given window from this app.
     * @param window The window to remove.
     */
    public void removeWindow(Window2D window) {
        synchronized (appCleanupLock) {
            viewSet.remove(window);
            synchronized (this) {
                windows.remove(window);
            }
            if (window == primaryWindow) {
                setPrimaryWindow(null);
            }
        }
    }

    /**
     * Returns the number of windows in this app.
     */
    public int getNumWindows () {
        return windows.size();
    }

    /**
     * Specify the primary window. This also reparents existing secondaries to this new primary.
     */
    public void setPrimaryWindow (Window2D primaryWindow) {
        if (this.primaryWindow == primaryWindow) return;

        /* Make current primary window secondary
         TODO: not yet supported
        if (this.primaryWindow != null) {
            this.primaryWindow.setType(Window2D.Type.SECONDARY);
        }
        */

        this.primaryWindow = primaryWindow;
        logger.info("set primary window to " + primaryWindow);

        LinkedList<Window2D> windowsCopy;
        synchronized (this) {
            windowsCopy = (LinkedList<Window2D>) windows.clone();
        }
            
        for (Window2D window : windowsCopy) {
            if (window.getType() == Window2D.Type.SECONDARY) {
                window.setParent(primaryWindow);
            }
        }
    }

    /**
     * Returns the primary window.
     */
    public Window2D getPrimaryWindow () {
        return primaryWindow;
    }

    /**
     * Recalculate the stack order based on the desired Z orders of the windows in the stack.
     * Used during slave synchronization of conventional apps.
     */
    public void updateSlaveWindows () {
        stack.restackFromDesiredZOrders();
        changedStackAllWindows();
    }

    /**
     * Tell all windows that their stack order may have changed.
     */
    private void changedStackAllWindows () {
        LinkedList<Window2D> windowsCopy;
        synchronized (this) {
            windowsCopy = (LinkedList<Window2D>) windows.clone();
        }
            
        for (Window2D window : windowsCopy) {
            window.changedStack();
        }
    }

    /**
     * Tell all non-coplanar windows (except the argument window) that their stack order may have changed.
     */
    public void changedStackAllWindowsExcept (Window2D windowExcept) {
        LinkedList<Window2D> windowsCopy;
        synchronized (this) {
            windowsCopy = (LinkedList<Window2D>) windows.clone();
        }
            
        for (Window2D window : windowsCopy) {
            if (window != windowExcept) {
                if (!window.isCoplanar()) {
                    window.changedStack();
                }
            }
        }
    }

    /**
     * Returns the focus entity of the app.
     */
    @InternalAPI
    public Entity getFocusEntity () {
        return focusEntity;
    }

    /**
     * Returns the Control Arbiter for this app.
     * If this is null the app supports fine-grained control swapping.
     * That is, the app accepts user events from different users equally
     * on a first-come first-served basis.
     */
    public ControlArb getControlArb() {
        return controlArb;
    }

    /**
     * Returns an iterator over all the windows of this app.
     */
    public Iterator<Window2D> getWindows() {
        return windows.iterator();
    }

    /**
     * Returns the name of the app.
     */
    public String getName () {
        if (name == null) {
            return "(Unnamed App)";
        } else {
            return name;
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString () {
        return getName();
    }

    /** 
     * Executed by the JVM shutdown process. 
     *
     * THREAD USAGE NOTE: No appbase locks are held during this call. 
     */
    private static void shutdown () {
        logger.info("Shutting down app base...");
        isAppBaseRunning = false;

        // Note: I tried to run this in a synchronized block, but it hung.
        LinkedList<App2D> appsCopy = (LinkedList<App2D>) apps.clone();
        for (App2D app : appsCopy) {
            logger.info("Shutting down app " + app);
            app.cleanup();
        }
        logger.info("Done shutting down apps.");

        apps.clear();
        stopInvokerThread();

        logger.info("Done shutting down app base.");
    }

    /**
     * Returns whether the app base is running.
     */
    public static boolean isAppBaseRunning () {
        return isAppBaseRunning;
    }

    /**
     * Specifies whether to also display this app in the HUD. Note: this is in addition to 
     * displaying the app in the world.
     */
    public synchronized void setShowInHUD (boolean showInHUD) {
        if (this.showInHUD == showInHUD) return;
        this.showInHUD = showInHUD;
        if (showInHUD) {
            hudDisplayer = new HUDDisplayer(this);
            viewSet.add(hudDisplayer);
        } else {
            viewSet.remove(hudDisplayer);
            hudDisplayer.cleanup();
            hudDisplayer = null;
        }
    }

    /**
     * Returns true if the app is currently shown in the HUD.
     */
    public boolean isShownInHUD () {
        return showInHUD;
    }

    /**
     * Specify a first-visible initializer for this app. If non-null, this will perform
     * some sort of initialization for the app the first time a window is made visible.
     */
    public void setFirstVisibleInitializer (FirstVisibleInitializer fvi) {
        this.fvi = fvi;
        logger.info("attached fvi to app " + this + ", fvi = " + fvi);
    }

    /**
     * Return the first-visible initializer for this app. 
     */
    public FirstVisibleInitializer getFirstVisibleInitializer () {
        return fvi;
    }

    /**
     * Invoked in the sas xremwin provider to guaranteed that the app is completely
     * stopped, including the app processes.
     *
     * THREAD USAGE NOTE: Called from a darkstar comm thread in the SAS. No appbase locks are held 
     * during this call. 
     */
    public void stop () {

        LinkedList<Window2D> windowsCopy;
        synchronized (this) {
            windowsCopy = (LinkedList<Window2D>) windows.clone();
        }
            
        for (Window2D window : windowsCopy) {
            window.closeUser(true);
        }

        cleanup();
    }

    /**
     * Invoke the given runnable later (at some point in time). The app base uses
     * this to offload things from the Render Thread that shouldn't be done on the EDT. 
     * This provides serialization of execution like the EDT, so it can run MT unsafe code.
     * But, unlike the SwingUtilities.invokeLater, it is okay to acquire appbase object 
     * locks on this thread.
     */
    public static void invokeLater (Runnable runnable) {
        synchronized (invokeLaterQueue) {
            invokeLaterQueue.addLast(runnable);
            invokeLaterQueue.notifyAll();
        }
    }

    static long start;
    static long stop;

    private static class Invoker implements Runnable {
        public void run () {
            while (!stopInvoker) {
                try {
                    //Runnable runnable = invokeLaterQueue.take();
                    Runnable runnable = null;
                    synchronized (invokeLaterQueue) {
                        while (invokeLaterQueue.size() <= 0 && !stopInvoker) {
                            invokeLaterQueue.wait();
                        }
                        if (!stopInvoker) {
                            runnable = invokeLaterQueue.getFirst();
                            invokeLaterQueue.remove(0);
                        }
                    }
                    if (runnable != null) {
                        runnable.run();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    stopInvoker = true;
                }
            }
            logger.info("App Base Invoker Thread stopped");
        }
    }
}
