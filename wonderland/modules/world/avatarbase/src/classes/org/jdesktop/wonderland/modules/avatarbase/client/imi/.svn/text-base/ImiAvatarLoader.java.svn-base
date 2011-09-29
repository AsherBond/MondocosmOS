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
package org.jdesktop.wonderland.modules.avatarbase.client.imi;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.asset.AssetUtils;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarCharacter;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarCharacter.WlAvatarCharacterBuilder;
import org.jdesktop.wonderland.modules.avatarbase.client.loader.spi.AvatarLoaderSPI;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.AvatarConfigInfo;

/**
 * Loads IMI avatars on the client and generates an avatar character from it.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ImiAvatarLoader implements AvatarLoaderSPI {

    private static Logger logger = Logger.getLogger(ImiAvatarLoader.class.getName());

    /**
     * {@inheritDoc}
     */
    public WlAvatarCharacter getAvatarCharacter(Cell avatarCell,
            String userName, AvatarConfigInfo info) {
        
        // Formulate the configuration URL to load the info
        URL configURL = null;
        try {
            configURL = new URL(info.getAvatarConfigURL());
        } catch (MalformedURLException ex) {
            logger.log(Level.WARNING, "Unable to form config url " +
                    info.getAvatarConfigURL(), ex);
            return null;
        }

        // Formulate the base URL for all IMI avatar assets
        String baseURL = null;
        try {
            ServerSessionManager manager = avatarCell.getCellCache().getSession().getSessionManager();
            String serverHostAndPort = manager.getServerNameAndPort();
            URL tmpURL = AssetUtils.getAssetURL("wla://avatarbaseart/", serverHostAndPort);
            baseURL = tmpURL.toExternalForm();
        } catch (MalformedURLException ex) {
            logger.log(Level.WARNING, "Unable to form base url", ex);
            return null;
        }

        WorldManager wm = ClientContextJME.getWorldManager();
        return new WlAvatarCharacterBuilder(configURL, wm).baseURL(baseURL).addEntity(false).build();
    }
}