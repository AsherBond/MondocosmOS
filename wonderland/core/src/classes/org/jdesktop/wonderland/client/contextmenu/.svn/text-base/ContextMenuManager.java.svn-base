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
package org.jdesktop.wonderland.client.contextmenu;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.contextmenu.annotation.ContextMenuFactory;
import org.jdesktop.wonderland.client.contextmenu.spi.ContextMenuFactorySPI;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.client.login.PrimaryServerListener;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.utils.ScannedClassLoader;

/**
 * Manages the entries on the context menu. The visual implementation of the
 * context menu is relegated to a module that interacts with this class.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ContextMenuManager {

    private List<ContextMenuFactorySPI> factoryList = null;
    private Set<ContextMenuListener> listeners = null;

    /** Constructor */
    public ContextMenuManager() {
        // We just use a normal linked list here. This means a couple things.
        // First, we have to make sure it is accessed in a thread-safe manner,
        // and second, the object returned by getContextMenuFactoryList() should
        // not be modified by the callers.
        factoryList = new LinkedList();

        // Initialize the list of context menu listeners. Accesses to this list
        // must be synchronized.
        listeners = new HashSet();

        // Listen for changes in the primary login session. If one already
        // exists then this will dispatch to the listener immediately. In
        // such a case, look for all of the @ContextMenuEntry annotation classes
        // and update factoryList.
        LoginManager.addPrimaryServerListener(new PrimaryServerListener() {
            public void primaryServer(ServerSessionManager server) {
                synchronized (factoryList) {
                    // Remove any previously existing items in the list.
                    factoryList.clear();

                    if (server != null) {
                        // Look for classes annotated with @ContextMenuEntry, and
                        // add them to the factory List
                        ScannedClassLoader cl = server.getClassloader();
                        Iterator<ContextMenuFactorySPI> it = cl.getAll(
                                ContextMenuFactory.class, ContextMenuFactorySPI.class);
                        while (it.hasNext() == true) {
                            factoryList.add(it.next());
                        }
                    }
                }

                synchronized (listeners) {
                    // Remove any existing listeners too
                    listeners.clear();
                }
            }
        });
    }
    
    /**
     * Singleton to hold instance of ContextMenu. This holder class is loaded
     * on the first execution of ContextMenu.getContextMenu().
     */
    private static class ContextMenuManagerHolder {
        private final static ContextMenuManager manager = new ContextMenuManager();
    }

    /**
     * Returns a single instance of this class
     * <p>
     * @return Single instance of this class.
     */
    public static final ContextMenuManager getContextMenuManager() {
        return ContextMenuManagerHolder.manager;
    }

    /**
     * Returns a list of context menu factories that generate items. This callers
     * of this method should not modify the returned List, but instead create
     * a copy of it first.
     *
     * @return A list of ContextMenuSPI objects
     */
    public List<ContextMenuFactorySPI> getContextMenuFactoryList() {
        // We need to synchronize on the factoryList object just in case it
        // is being updated when the primary login server changes.
        synchronized (factoryList) {
            return factoryList;
        }
    }


    /**
     * Adds a listener for context display events. If the listener already
     * exists, this method does nothing.
     *
     * @param listener The listener to add
     */
    public void addContextMenuListener(ContextMenuListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Removes the given listener for context menu display events. If the
     * listener does not exist, then this method does nothing.
     *
     * @param listener The listener to remove
     */
    public void removeContextMenuListener(ContextMenuListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Dispatches an event to all listeners that a context menu is about to
     * be displayed.
     *
     * @param event The ContextEvent that caused the menu to be displayed
     */
    public void fireContextMenuEvent(ContextMenuEvent event) {
        synchronized (listeners) {
          final Cell cell = event.getPrimaryCell();
          for (ContextMenuListener listener : listeners) {
              listener.contextMenuDisplayed(event);
          }
        }
    }
}
