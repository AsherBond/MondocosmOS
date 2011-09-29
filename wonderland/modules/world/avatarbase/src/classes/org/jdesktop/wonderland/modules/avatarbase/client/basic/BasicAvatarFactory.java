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

import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.AvatarRegistry;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.annotation.AvatarFactory;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.spi.AvatarFactorySPI;

/**
 * Basic avatar factory generates the most basic (default) avatar.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@AvatarFactory
public class BasicAvatarFactory implements AvatarFactorySPI {

    private static final ResourceBundle BUNDLE =
            ResourceBundle.getBundle("org/jdesktop/wonderland/modules/" +
            "avatarbase/client/resources/Bundle");

    // The set of basic avatars
    private static Set<BasicAvatar> basicAvatarSet = null;
    
    // A hard-coded list of relative URLs of the basic avatar artwork
    private static String AVATARS[][] = {
        {
            BUNDLE.getString("Cartoon_Male"),
            "default-avatars/maleCartoonAvatar.dae/maleCartoonAvatar.dae.gz.dep"
        },
        {
            BUNDLE.getString("Cartoon_Female"),
            "default-avatars/femaleCartoonAvatar.dae/femaleCartoonAvatar.dae.gz.dep"
        },
        {
            BUNDLE.getString("Toy_Male"),
            "default-avatars/maleToyAvatar.dae/maleToyAvatar.dae.gz.dep"
        },
        {
            BUNDLE.getString("Toy_Female"),
            "default-avatars/femaleToyAvatar.dae/femaleToyAvatar.dae.gz.dep"
        }
    };

    /**
     * {@inheritDoc}
     */
    public void registerAvatars(ServerSessionManager session) {
        // Create the set of basic avatars from the hard-coded list of URLs
        AvatarRegistry registry = AvatarRegistry.getAvatarRegistry();
        basicAvatarSet = new HashSet<BasicAvatar>();
        for (int i = 0; i < AVATARS.length; i++) {
            BasicAvatar avatar = new BasicAvatar(AVATARS[i][0], AVATARS[i][1]);
            basicAvatarSet.add(avatar);
            registry.registerAvatar(avatar, i == 0);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void unregisterAvatars(ServerSessionManager session) {
        // Look through and unregistry all of the basic avatars
        AvatarRegistry registry = AvatarRegistry.getAvatarRegistry();
        for (BasicAvatar avatar : basicAvatarSet) {
            registry.unregisterAvatar(avatar);
        }
        basicAvatarSet.clear();
        basicAvatarSet = null;
    }
}
