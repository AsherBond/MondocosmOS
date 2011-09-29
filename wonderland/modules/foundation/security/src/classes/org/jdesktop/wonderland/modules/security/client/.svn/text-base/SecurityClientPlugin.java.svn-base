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
package org.jdesktop.wonderland.modules.security.client;

import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.ClientPlugin;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellCache;
import org.jdesktop.wonderland.client.cell.CellCache.CellCacheListener;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.client.login.SessionLifecycleListener;
import org.jdesktop.wonderland.common.annotation.Plugin;
import org.jdesktop.wonderland.common.cell.CellID;

/**
 * Automatically add a SecurityComponent to all cells
 * @author jkaplan
 */
@Plugin
public class SecurityClientPlugin 
        implements ClientPlugin, SessionLifecycleListener, CellCacheListener
{
    private ServerSessionManager sessionManager;

    public void initialize(ServerSessionManager sessionManager) {
        this.sessionManager = sessionManager;
        sessionManager.addLifecycleListener(this);
    }

    public void cleanup() {
        sessionManager.removeLifecycleListener(this);
    }

    public void sessionCreated(WonderlandSession session) {
        CellCache cache = ClientContext.getCellCache(session);
        cache.addCellCacheListener(this);
    }

    public void primarySession(WonderlandSession session) {}

    public void cellLoaded(CellID cellID, Cell cell) {
        cell.addComponent(new SecurityComponent(cell));
    }

    public void cellLoadFailed(CellID cellID, String className,
                               CellID parentCellID, Throwable cause)
    {}

    public void cellUnloaded(CellID cellID, Cell cell) {}
}
