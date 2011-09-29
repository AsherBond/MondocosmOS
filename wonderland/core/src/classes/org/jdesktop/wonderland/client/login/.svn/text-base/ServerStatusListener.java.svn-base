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

/**
 * A listener that will be notified when a SessionManager connects or
 * disconnects
 * @author jkaplan
 */
public interface ServerStatusListener {
    /**
     * Notification that a connection is in progress with the given
     * status.
     */
    public void connecting(ServerSessionManager manager, String message);

    /**
     * Notification the the server session manager has connected
     * @param sessionManager the session manager that connected
     */
    public void connected(ServerSessionManager sessionManager);

    /**
     * Notification that the server session manager has disconnected
     * @param sessionManager the session manager that disconnected
     */
    public void disconnected(ServerSessionManager sessionManager);
}
