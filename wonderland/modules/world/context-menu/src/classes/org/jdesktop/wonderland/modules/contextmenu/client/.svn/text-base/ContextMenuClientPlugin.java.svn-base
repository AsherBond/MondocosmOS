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
package org.jdesktop.wonderland.modules.contextmenu.client;

import org.jdesktop.wonderland.client.BaseClientPlugin;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.annotation.Plugin;
import org.jdesktop.wonderland.modules.contextmenu.client.ui.SwingContextMenu;

/**
 * Client-side plugin to initialize Swing-based implementation of the Context
 * Menu.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@Plugin
public class ContextMenuClientPlugin extends BaseClientPlugin {
    private SwingContextMenu menu = null;

    @Override
    public void initialize(ServerSessionManager loginInfo) {
        this.menu = new SwingContextMenu();

        super.initialize(loginInfo);
    }

    @Override
    protected void activate() {
        menu.register();
    }

    @Override
    protected void deactivate() {
        menu.unregister();
    }
}
