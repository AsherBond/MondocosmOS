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
package org.jdesktop.wonderland.server;

import org.jdesktop.wonderland.common.InternalAPI;

/**
 * Server plugins can implement this interface to add new functionality to
 * the server.  The initialize method of the service will be called during
 * the intialization of WonderlandBoot.  It is guaranteed to be called
 * after the WonderlandContext has been initialized, so plugins can use
 * the WonderlandContext to get access to server resources.
 * 
 * @author jkaplan
 */
@InternalAPI
public interface ServerPlugin {
    /** intialize this plugin */
    public void initialize();
}
