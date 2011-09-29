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
package org.jdesktop.wonderland.modules.avatarbase.client.registry.spi;

import org.jdesktop.wonderland.client.login.ServerSessionManager;

/**
 * An AvatarFactorySPI class initializes and registers avatars in the system.
 * Each class of avatar (e.g. default, IMI, third-party) may want to achieve
 * this in its own fashion.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public interface AvatarFactorySPI {

    /**
     * Registers avatars in the system. This method blocks until all avatars
     * have been loaded and registered.
     *
     * @param session The current server session
     */
    public void registerAvatars(ServerSessionManager session);

    /**
     * Unregisters avatars in the system when a server session disconnects.
     *
     * @param session The server session that has just ended
     */
    public void unregisterAvatars(ServerSessionManager session);
}
