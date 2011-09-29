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
package org.jdesktop.wonderland.modules.avatarbase.client.basic;

import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarCharacter;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.spi.AvatarSPI;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.AvatarConfigInfo;

/**
 * The most basic avatar available in the system, a simple COLLADA model that
 * cannot be deleted/configured and serves as the "default" avatar.
 * <p>
 * This avatar takes a relative URL of a ".dep" file (deployed model) from a
 * common base URL that describes the avatar.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class BasicAvatar implements AvatarSPI {

    // The name of the avatar that appears in the list of all avatars
    private String avatarName = null;

    // The relative URL of the avatar .dep file
    private String avatarURL = null;

    /** Default constructor */
    public BasicAvatar(String avatarName, String avatarURL) {
        this.avatarName = avatarName;
        this.avatarURL = avatarURL;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isHighResolution() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return avatarName;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canDelete() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void delete() {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConfigure() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void configure() {
        // Do nothing
    }

    /**
     * {@inheritDoc}
     */
    public WlAvatarCharacter getAvatarCharacter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * {@inheritDoc}
     */
    public AvatarConfigInfo getAvatarConfigInfo(ServerSessionManager session) {
        return new AvatarConfigInfo(avatarURL, BasicAvatarLoaderFactory.class.getName());
    }
}
