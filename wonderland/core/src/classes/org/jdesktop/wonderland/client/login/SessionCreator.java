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

import org.jdesktop.wonderland.client.comms.WonderlandServerInfo;
import org.jdesktop.wonderland.client.comms.WonderlandSession;

/**
 * An interface that can be used with the LoginManager to control how a
 * WonderlandSession is created.
 * @author jkaplan
 */
public interface SessionCreator<T extends WonderlandSession> {
    /**
     * Create a new WonderlandSession for the given server and classloader.
     * This gives the user-interface a hook to listen for session-related
     * events.
     * @param sessionManager the server session manager that is creating
     * this session
     * @param serverInfo the information about the server to connect to
     * @param loader the classloader with all modules loaded
     * @return the newly created Wonderland session
     */
    public T createSession(ServerSessionManager sessionManager,
                           WonderlandServerInfo serverInfo,
                           ClassLoader loader);
}
