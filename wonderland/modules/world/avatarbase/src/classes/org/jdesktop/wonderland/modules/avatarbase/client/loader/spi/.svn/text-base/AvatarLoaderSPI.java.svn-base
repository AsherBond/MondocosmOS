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
package org.jdesktop.wonderland.modules.avatarbase.client.loader.spi;

import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarCharacter;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.AvatarConfigInfo;

/**
 * An avatar loader loads an avatar given its configuration information, which
 * is typically an XML configuration file on the server. This allows different
 * types of avatars to be loaded differently.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public interface AvatarLoaderSPI {

    /**
     * Loads an avatar and returns the character to which it corresponds.
     *
     * @param avatarCell The Cell of the avatar
     * @param userName The user name of the avatar
     * @param info The avatar configuration info
     * @return The avatar character
     */
    public WlAvatarCharacter getAvatarCharacter(Cell avatarCell,
            String userName, AvatarConfigInfo info);
}
