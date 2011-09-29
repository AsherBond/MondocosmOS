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
package org.jdesktop.wonderland.client;

import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.InternalAPI;

/**
 * Client plugins can implement this interface to add new functionality to
 * the Wonderland client.  The initialize method of the service will be called
 * during the intialization of the client Main.  It is guaranteed to be called
 * after theclient context has been initialized, so plugins can use
 * the context to get access to server resources.
 * 
 * @author kaplanj
 */
@InternalAPI
public interface ClientPlugin {
    /**
     * Intialize this plugin.  The plugin can use static object in the
     * client such as the ClientContext to register itself with the
     * Wonderland environment.  In addition, a plugin is given the 
     * LoginManager for the WonderlandServer it is associated with.
     * <p>
     * Plugins are loaded after the client logs in to the given server,
     * but before any WonderlandSessions have been created.  Plugins that
     * need to use a WonderlandSession can either create their own or
     * (more typically) install a SessionLifecyclyListener to wait for
     * session creation.
     *  
     * @param loginManager the loginManager representing the server this
     * plugin is connected to.
     */
    public void initialize(ServerSessionManager loginInfo);

    /**
     * Clean up any resources used by this plugin.  This is called when
     * the client's login for the ServerSessionManager lapses due to logout,
     * timeout, or other reasons.  Plugins should clean up any registrations
     * such as SessionLifecycleListeners or PrimaryServerListeners.
     */
    public void cleanup();
}
