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
package org.jdesktop.wonderland.modules.darkstar.api.weblib;

import org.jdesktop.wonderland.client.login.ServerSessionManager;

/**
 * Singleton for managing login to the Darkstar server from the web server.
 * @author jkaplan
 */
public interface DarkstarWebLogin {
    /**
     * Add a listener that will be notified when Darkstar servers start and
     * stop. On addition, the listener will be immediately notified of all
     * existing servers.
     * @param listener the listener to add
     */
    public void addDarkstarServerListener(DarkstarServerListener listener);

    /**
     * Remove a listener that will be notified of server stops and starts.
     * @param listener the listener to remove
     */
    public void removeDarkstarServerListener(DarkstarServerListener listener);

    /**
     * Listener to notify of server connects and disconnects
     */
    public interface DarkstarServerListener {
        /**
         * Notification that the server has started up
         * @param runner the DarkstarRunnerImpl that started up
         * @param sessionManager a server sesssion manager that can be
         * used to connect to this server
         */
        public void serverStarted(DarkstarRunner runner,
                                  ServerSessionManager sessionManager);

        /**
         * Notification that the server has shut down
         * @param runner the DarkstarRunnerImpl that shut down
         */
        public void serverStopped(DarkstarRunner runner);
    }
}
