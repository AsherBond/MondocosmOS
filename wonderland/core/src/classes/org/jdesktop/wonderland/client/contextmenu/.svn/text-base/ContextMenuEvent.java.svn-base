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

import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.scenemanager.event.ContextEvent;

/**
 * Wraps a ContextMenuInvocationSettings object that allows clients to make
 * current-invocation only changes to the context menu.
 *
 * Context menu listeners can adjust whether standard menu items
 * (from the ContextMenuManager) and cell-standard menu items (from the cell's
 * ContextMenuComponent) are displayed using booleans, and add new menu items.
 *
 * None of the changes made will carry over to the next display of
 * the context menu. To make a persistent change, operate on the ContextMenuManager
 * or ContextMenuComponent.
 *
 * Note that this these settings are seen and can be modified by any context
 * menu listener.
 *
 * @author mabonner
 */
public class ContextMenuEvent extends ContextEvent {
    private ContextEvent source;
    private ContextMenuInvocationSettings settings;

    public ContextMenuEvent(ContextEvent evt, Cell cell) {
        super(evt.getEntityList(), evt.getMouseEvent());

        this.source = evt;
        this.settings = new ContextMenuInvocationSettings(cell);
    }

    public ContextMenuInvocationSettings getSettings() {
        return settings;
    }

    public ContextEvent getSource() {
        return source;
    }
}
