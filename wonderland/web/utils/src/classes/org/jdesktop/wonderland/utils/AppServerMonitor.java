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
package org.jdesktop.wonderland.utils;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Monitor the app server
 * @author jkaplan
 */
public class AppServerMonitor {
    private Set<AppServerStartupListener> listeners =
            new CopyOnWriteArraySet<AppServerStartupListener>();

    private boolean startupComplete = false;

    /** 
     * Constructor is protected -- use getInstance() instead
     */
    protected AppServerMonitor() {
    }

    /**
     * Get an instance of AppServerMonitor
     * @return an AppServerMonitor instance
     */
    public static AppServerMonitor getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Find out whether startup is complete or not
     * @return true if startup is complete, or false if not
     */
    public synchronized boolean isStartupComplete() {
        return startupComplete;
    }

    /**
     * Add a listener
     * @param listener the listener to add
     */
    public void addListener(AppServerStartupListener listener) {
        listeners.add(listener);
    }

    /**
     * Remove a listener
     * @param listener the listener to remove
     */
    public void removeListener(AppServerStartupListener listener) {
        listeners.remove(listener);
    }

    /**
     * Fire an event notifying listeners that the server has started up
     */
    public void fireStartupComplete() {
        synchronized (this) {
            if (isStartupComplete()) {
                return;
            }
            
            startupComplete = true;
        }

        for (AppServerStartupListener l : listeners) {
            l.startupComplete();
        }
    }

    public interface AppServerStartupListener {
        /**
         * Notification that the app server has completed the startup
         * process
         */
        public void startupComplete();
    }

    /**
     * SingletonHolder is loaded on the first execution of
     * AppServerMonitor.getInstance()
     * or the first access to AppServerMonitor.INSTANCE , not before.
     */
    private static class SingletonHolder {

        private final static AppServerMonitor INSTANCE = new AppServerMonitor();
    }
}
