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
import java.util.Set;

/**
 * Represents an abstract item in the context menu of a certain type. Each
 * item has a name and state (ENABLED/DISABLED). The ContextMenuItem has an
 * event mechanism to indicate when some aspect of the context menu item has
 * been changed and needs to be repainted.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public abstract class ContextMenuItem {

    private boolean isEnabled = true;
    private String label = null;
    private Set<MenuItemRepaintListener> repaintListenerSet = null;

    public ContextMenuItem(String label) {
        this.label = label;
        repaintListenerSet = new HashSet();
    }

    /**
     * Returns true if the context menu item is enabled, false if not.
     *
     * @return True if enabled, false if not
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Sets whether the context menu item is enabled.
     *
     * @param isEnabled True to enabled the menu item, false to not
     */
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
    
    /**
     * Returns the label of the menu item.
     *
     * @return The String menu item label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label of the menu item.
     *
     * @param label The new label of the menu item
     */
    public void setLabel(String label) {
        this.label = label;
    }
    
    /**
     * Adds a new listener for events when the menu item has been changed. Only
     * a single instance of the menu item repaint listener may be added.
     *
     * @param listener A new MenuItemRepaintListener
     */
    public void addMenuItemRepaintListener(MenuItemRepaintListener listener) {
        synchronized (repaintListenerSet) {
            repaintListenerSet.add(listener);
        }
    }

    /**
     * Removes a listener for the menu item changed events. If the listener does
     * not exist, this method does nothing.
     *
     * @param listener The listener to remove, if it exists
     */
    public void removeMenuItemRepaintListener(MenuItemRepaintListener listener) {
        synchronized (repaintListenerSet) {
            repaintListenerSet.remove(listener);
        }
    }

    /**
     * Tells all listeners that the menu item has been changed.
     */
    public void fireMenuItemRepaintListeners() {
        synchronized (repaintListenerSet) {
            for (MenuItemRepaintListener listener : repaintListenerSet) {
                listener.repaintMenuItem(this);
            }
        }
    }

    /**
     * A listener that informs when the menu item needs to be visually updated.
     */
    public interface MenuItemRepaintListener {
        /**
         * Indicates that the menu item has been changed and needs to be
         * updated.
         *
         * @param menuItem The menu item associated with the repaint
         */
        public void repaintMenuItem(ContextMenuItem menuItem);
    }
}
