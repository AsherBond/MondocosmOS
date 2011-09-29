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

import java.util.Iterator;
import java.util.LinkedList;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.view.View2D;
import org.jdesktop.wonderland.modules.appbase.client.view.View2DDisplayer;

/**
 * Provides storage for all of the views of all of the windows of an app. An app can have multiple
 * windows, and each of these can, in turn, have multiple views. You can add both windows and 
 * displayers (that is, <code>View2DDisplayers</code>) to the view set. When you add a window to a
 * a view set, a view is created for that window in every displayer in the set. When you add 
 * a displayer to a view set, a view is created in that displayer for every window in the set.
 * Note: A window must belong to only one view set at a time. A displayer must belong to only one view
 * set at a time. (Not enforced).
 *
 * @author deronj
 */
@ExperimentalAPI
public class View2DSet {

    /** The app to which this view set belongs. */
    private App2D app;

    /** The displayers in the set. */
    private LinkedList<View2DDisplayer> displayers = new LinkedList<View2DDisplayer>();

    /** The windows in the set. */
    private LinkedList<Window2D> windows = new LinkedList<Window2D>();

    /**
     * Create a new instance of View2DSet.
     * @param app The app to which this view set belongs.
     */
    public View2DSet (App2D app) {
        this.app = app;
    }

    /**
     * Clean up resources.
     */
    public void cleanup () {
        if (app == null) return;
        synchronized (app.getAppCleanupLock()) {
            synchronized (this) {
                for (View2DDisplayer displayer : displayers) {
                    displayer.destroyAllViews();
                }
                displayers.clear();

                LinkedList<Window2D> toRemoveList = (LinkedList<Window2D>) windows.clone();
                for (Window2D window : toRemoveList) {
                    remove(window);
                }
                windows.clear();
                toRemoveList.clear();
                app = null;
            }
        }
    }

    /**
     * Add a displayer to this view set. A view in that displayer is created for each window in the set.
     * Nothing happens if the displayer is already in the set.
     */
    public synchronized void add (View2DDisplayer displayer) {
        if (displayers.contains(displayer)) return;
        displayers.add(displayer);
        displayerCreateViewsForAllWindows(displayer);
    }

    /**
     * Removes a displayer from the set. All views associated with the displayer are destroyed.
     * Nothing happens if the displayer is not in the set.
     */
    public void remove (View2DDisplayer displayer) {
        if (app == null) return;
        synchronized (app.getAppCleanupLock()) {
            synchronized (this) {
                if (displayers.remove(displayer)) {
                    displayer.destroyAllViews();
                }
            }
        }
    }

    /**
     * Add a window to the view set. A view for that window is created for each displayer in the set.
     * Nothing happens if the window is already in the set.
     */
    public synchronized void add (Window2D window) {
        if (windows.contains(window)) return;
        windows.add(window);
        windowCreateViewsForAllDisplayers(window);
    }

    /**
     * Removes a window from the set. All views associated with the window are destroyed.
     * Nothing happens if the window is not in the set.
     */
    public void remove (Window2D window) {
        if (app == null) return;
        synchronized (app.getAppCleanupLock()) {
            synchronized (this) {
                window.removeViewsAll();
                windows.remove(window);
            }
        }
    }

    /**
     * Returns the number of windows in this view set.
     */
    public synchronized int getNumWindows () {
        return windows.size();
    }

    /**
     * Returns an iterator over all displayers in this view set.
     */
    public synchronized Iterator<View2DDisplayer> getDisplayers () {
        return displayers.iterator();
    }

    /**
     * Returns an iterator over all windows in this view set.
     */
    public synchronized Iterator<Window2D> getWindows () {
        return windows.iterator();
    }

    /**
     * Creates a view associated with this displayer for all windows in the set.
     */
    private void displayerCreateViewsForAllWindows (View2DDisplayer displayer) {
        for (Window2D window : windows) {
            if (!window.isZombie()) {
                View2D view = displayer.createView(window);
            }
        }
    }

    /**
     * Creates a view associated with this displayer for all displayers in the set.
     */
    private void windowCreateViewsForAllDisplayers (Window2D window) {
        for (View2DDisplayer displayer : displayers) {
            View2D view = displayer.createView(window);
        }
    }
}
