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
package org.jdesktop.wonderland.client.login;

import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * This listener is registered with the LoginManager to be notified when the
 * primary server changes. With Wonderland federation, a client may be
 * connected to multiple servers at a time.  At any time, however, a single
 * server is considered the "primary" server.  This is the server that should
 * be reflected in non-spatial parts of the UI like menus.
 * <p>
 * This listener will be notified when the primary server changes.  Note that
 * the primary server may be null if the client disconnects from all servers.
 *
 * @author kaplanj
 */
@ExperimentalAPI
public interface PrimaryServerListener {
    /**
     * Called when the primary server changes
     * @param server the session manager for managing sessions connected
     * to the given server
     */
    public void primaryServer(ServerSessionManager server);
}
