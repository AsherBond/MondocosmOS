/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

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

import com.jme.scene.Node;
import com.jme.util.resource.ResourceLocator;
import imi.character.CharacterParams;
import imi.character.MaleAvatarParams;
import imi.scene.PMatrix;
import imi.scene.PScene;
import imi.scene.polygonmodel.PPolygonMesh;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.asset.AssetUtils;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.artimport.DeployedModel;
import org.jdesktop.wonderland.client.jme.artimport.LoaderManager;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarCharacter;
import org.jdesktop.wonderland.modules.avatarbase.client.loader.spi.AvatarLoaderSPI;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.AvatarConfigInfo;

/**
 * Loads basic (static Collada model) avatars on the client and generates an
 * avatar character from it.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class BasicAvatarLoader implements AvatarLoaderSPI {

    private static Logger logger = Logger.getLogger(BasicAvatarLoader.class.getName());

    // The default avatar model to use if a null configuration URL is given
    private static final String DEFAULT_URL =
            "default-avatars/maleCartoonAvatar.dae/maleCartoonAvatar.dae.gz.dep";

    // The base URL of where to find all of the avatar assets
    private static final String BASE_URL = "wla://avatarbaseart/";
    
    /**
     * {@inheritDoc}
     */
    public WlAvatarCharacter getAvatarCharacter(Cell avatarCell,
            String userName, AvatarConfigInfo info) {

        WorldManager wm = ClientContextJME.getWorldManager();
        CharacterParams attributes = new MaleAvatarParams(userName);

        // Formulate the configuration URL to load the info. If null, then use
        // some default.
        String avatarURL = DEFAULT_URL;
        if (info != null && info.getAvatarConfigURL() != null) {
            avatarURL = info.getAvatarConfigURL();
        }

        // Formulate the base URL for all default avatar assets. Annotate it
        // with the server:port of the primary session.
        String baseURL = null;
        try {
            URL tmpURL = AssetUtils.getAssetURL(BASE_URL, avatarCell);
            baseURL = tmpURL.toExternalForm();
        } catch (MalformedURLException ex) {
            logger.log(Level.WARNING, "Unable to form base url", ex);
            return null;
        }

        // Setup simple model, needs to actually have something to
        // play well with the system
        PScene simpleScene = new PScene(ClientContextJME.getWorldManager());
        simpleScene.addMeshInstance(new PPolygonMesh("PlaceholderMesh"), new PMatrix());
        attributes.setUseSimpleStaticModel(true, simpleScene);
        attributes.setBaseURL(baseURL);
        
        // Turn of animations for the simple avatar
        attributes.setAnimateBody(false);
        attributes.setAnimateFace(false);
 
        // Load the avatar using the new collada loading manager
        Node node = null;
        try {
            URL url = new URL(baseURL + avatarURL);
            DeployedModel dm = LoaderManager.getLoaderManager().getLoaderFromDeployment(url);
            node = dm.getModelLoader().loadDeployedModel(dm, null);
            node.setLocalScale(0.22f);
        } catch (MalformedURLException excp) {
            logger.log(Level.WARNING, "Unable to for .dep URL", excp);
            return null;
        } catch (IOException excp) {
            logger.log(Level.WARNING, "Error loading avatar model", excp);
            return null;
        }

        // Create the avatar character, but don't add it to the world just yet.
        WlAvatarCharacter.WlAvatarCharacterBuilder builder =
                new WlAvatarCharacter.WlAvatarCharacterBuilder(attributes, wm);
        builder.addEntity(false);
        builder.setSimpleStaticGeometry(node);

        return builder.build();
    }

    /**
     * A texture resource locator that looks relative to the path of the model.
     * 
     * Hack for the binary loader, this will need to be made general purpose once
     * we implement a core binary loader
     */
    class RelativeResourceLocator implements ResourceLocator {

        private String modulename;
        private String path;
        private String protocol;
        private Cell viewCell;

        /**
         * Locate resources for the given file
         * @param url
         */
        public RelativeResourceLocator(URL url, Cell viewCell) {
            // The modulename can either be in the "user info" field or the
            // "host" field. If "user info" is null, then use the host name.
            this.viewCell = viewCell;
            if (url.getUserInfo() == null) {
                modulename = url.getHost();
            }
            else {
                modulename = url.getUserInfo();
            }
            path = url.getPath();
            path = path.substring(0, path.lastIndexOf('/')+1);
            protocol = url.getProtocol();
        }

        public URL locateResource(String resource) {
            try {
                String urlStr = trimUrlStr(protocol + "://" + modulename + path + ".." + resource);
                URL url = AssetUtils.getAssetURL(urlStr, viewCell);
                return url;
            } catch (MalformedURLException ex) {
                logger.log(Level.SEVERE, "Unable to locateResource "+resource, ex);
                return null;
            }
        }

        /**
         * Trim ../ from url
         */
        private String trimUrlStr(String urlStr) {
            int pos = urlStr.indexOf("/../");
            if (pos == -1)
                return urlStr;

            StringBuilder buf = new StringBuilder(urlStr);
            int start = pos;
            while (buf.charAt(--start) != '/') {
            }
            buf.replace(start, pos + 4, "/");
            return buf.toString();
        }
    }
}
