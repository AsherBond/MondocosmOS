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
package org.jdesktop.wonderland.client.contextmenu.cell;

import java.util.HashSet;
import java.util.Set;
import org.jdesktop.wonderland.client.cell.*;
import org.jdesktop.wonderland.client.contextmenu.spi.ContextMenuFactorySPI;

/**
 * A cell component which provides cell specific ContextMenu items to the
 * ContextMenu system. Users of this component can add and remove context menu
 * factories.
 *
 * @author paulby
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ContextMenuComponent extends CellComponent {

    private Set<ContextMenuFactorySPI> factories = null;
    private boolean showStandardMenuItems = true;

    public ContextMenuComponent(Cell cell) {
        super(cell);
        factories = new HashSet();
    }

    /**
     * Add a context menu factory for this cell component. If the factory
     * already exists, this method does nothing.
     *
     * @param factory The context menu factory to add
     */
    public void addContextMenuFactory(ContextMenuFactorySPI factory) {
        synchronized (factories) {
            factories.add(factory);
        }
    }

    /**
     * Remove the given context menu factory from the cell component. This
     * change will not effect a menu that is currently being displayed,
     * but will be applied next time the menu is displayed
     * 
     * @param factory The context menu factory to remove
     */
    public void removeContextMenuFactory(ContextMenuFactorySPI factory) {
        synchronized (factories) {
            factories.remove(factory);
        }
    }

    /**
     * Sets whether the context menu should show the stanard menu items, in
     * addition to any menu items this Cell component adds. By default, this
     * value is 'true'.
     *
     * @param show True to display the standard context menu items, false to
     * not display the standard context menu items
     */
    public void setShowStandardMenuItems(boolean show) {
        showStandardMenuItems = show;
    }

    /**
     * Returns true if the context menu should display the standard menu items
     * for this Cell, false to not display them.
     *
     * @return True to display the standard context menu items
     */
    public boolean isShowStandardMenuItems() {
        return showStandardMenuItems;
    }

    /**
     * Returns an array of context menu factories.
     *
     * @return An array of context menu items
     */
    public ContextMenuFactorySPI[] getContextMenuFactories() {
        synchronized (factories) {
            return factories.toArray(new ContextMenuFactorySPI[] {});
        }
    }
}
