/*
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

import java.util.LinkedList;
import java.util.List;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.contextmenu.cell.ContextMenuComponent;
import org.jdesktop.wonderland.client.contextmenu.spi.ContextMenuFactorySPI;

/**
 * Wrapped by a ContextMenuEvents, stores the temporary settings of the
 * context menu.
 *
 * Context menu listeners can adjust whether standard menu items
 * (from the ContextMenuManager) and cell-standard menu items (from the cell's
 * ContextMenuComponent) are displayed using booleans, and add new menu items.
 *
 * None of the changes made will carry over to the next display of
 * the context menu. To make a persistent change, operate on the ContextMenuManager
 * or ContextMenuComponent.
 * 
 * @author mabonner
 */
public class ContextMenuInvocationSettings {

    /** whether to display global standard menu items from the ContextMenuManager */
    private boolean displayStandard = true;
    /** whether to display cell-specific standard menu items from the cell's ContextMenuComponent */
    private boolean displayCellStandard = true;
    /** temporary menu name, defaults to cell's name or "Menu" if cell == null */
    private String menuName;
    /** list of temporary factories, displayed if not empty */
    private List<ContextMenuFactorySPI> factoryList = null;

    public ContextMenuInvocationSettings(Cell cell) {

        ContextMenuComponent cmc = null;
        if (cell != null) {
            menuName = cell.getName();
            // Look for the context component on the current Cell, we need to
            // intialize state for this event
            cmc = cell.getComponent(ContextMenuComponent.class);
        } else {
            menuName = "Menu";
        }



        if (cmc != null) {
            displayStandard = cmc.isShowStandardMenuItems();
        }
    }

    /**
     * Add a new menu item to be displayed for this context menu opening only
     * @param factory the factory to add the temporary item
     */
    public void addTempFactory(ContextMenuFactorySPI factory) {
        if (factoryList == null) {
            factoryList = new LinkedList();
        }
        factoryList.add(factory);
    }

    public boolean isDisplayCellStandard() {
        return displayCellStandard;
    }

    public boolean isDisplayStandard() {
        return displayStandard;
    }

    public List<ContextMenuFactorySPI> getFactoryList() {
        return factoryList;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setDisplayCellStandard(boolean displayCellStandard) {
        this.displayCellStandard = displayCellStandard;
    }

    public void setDisplayStandard(boolean displayStandard) {
        this.displayStandard = displayStandard;
    }

    public void setFactoryList(List<ContextMenuFactorySPI> factoryList) {
        this.factoryList = factoryList;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public boolean isDisplayTemporaryFactories() {
        if (factoryList != null) {
            return true;
        }
        return false;
    }
}
