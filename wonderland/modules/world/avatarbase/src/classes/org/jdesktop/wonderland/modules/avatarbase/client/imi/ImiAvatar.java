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

import imi.character.CharacterParams;
import imi.character.Manipulator;
import java.awt.Color;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.cell.asset.AssetUtils;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.WonderlandCharacterParams.ColorConfigElement;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.WonderlandCharacterParams.ConfigElement;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.WonderlandCharacterParams.ConfigType;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarCharacter;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.spi.AvatarSPI;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.AvatarConfigInfo;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;

/**
 * Represents an IMI avatar configured on the system. It consists of a pointer
 * to the XML configuration file within the user's local repository that has
 * all of the configuration information, the version of the avatar configuration,
 * and the name of the avatar.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ImiAvatar implements AvatarSPI {

    private static Logger logger = Logger.getLogger(ImiAvatar.class.getName());
    private ContentResource resource = null;
    private int version = 0;
    private String avatarName = null;
    private static final String EXTENSION = ".xml";

    // The set of configuration parameters that are used by the configuration
    // UI
    private WonderlandCharacterParams params = null;

    /**
     * Constructor, takes the pointer to the existing configuration on disk.
     *
     * @param resource The XML file within the user's local repository
     */
    public ImiAvatar(ContentResource resource) {
        this.resource = resource;
        version = getAvatarVersion(resource.getName());
        int i = resource.getName().lastIndexOf('_');
        if (i == -1) {
            avatarName = resource.getName();
        }
        else {
            avatarName = resource.getName().substring(0, i);
        }
    }

    /**
     * Constructor for a new "blank" avatar, taking the name and version.
     *
     * @param avatarName The name of the new avatar
     * @param version The version of the new avatar
     */
    public ImiAvatar(String avatarName, int version) {
        this.avatarName = avatarName;
        this.version = version;
        this.resource = null;
    }

    /**
     * Factory method to create a new avatar given its name.
     *
     * @param avatarName The name of the new avatar
     */
    public static ImiAvatar createAvatar(String avatarName) {
        // Create a new ImiAvatar object with the avatar name and version
        ImiAvatar avatar = new ImiAvatar(avatarName, 1);

        // Start with some blank avatar attributes
        try {
            avatar.params = WonderlandCharacterParams.loadMale();
        } catch (java.io.IOException excp) {
            logger.log(Level.WARNING, "Unable to generate new avatar", excp);
            return null;
        }
        return avatar;
    }

    /**
     * Returns the content resource associated with this avatar, or null if
     * none yet exists.
     *
     * @return The avatar ContentResource
     */
    public ContentResource getResource() {
        return resource;
    }
    
    public void setResource(ContentResource resource) {
        this.resource = resource;
    }

    /**
     * Return the file name corresponding to the avatar's configuration file.
     *
     * @return The configuration file name
     */
    public String getFilename() {
        return avatarName + "_" + version + EXTENSION;
    }

    /**
     * Returns the version number.
     *
     * @return The version
     */
    public int getVersion() {
        return version;
    }
    
    /**
     * Sets the version number.
     *
     * @param version The new version number
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Sets the name of the avatar.
     *
     * @param avatarName The new avatar name
     */
    public void setName(String avatarName) {
        this.avatarName = avatarName;
    }
    
    /**
     * Increment and return the version number.
     *
     * @return The new, incremented version number
     */
    public int incrementVersion() {
        version++;
        return version;
    }

    private int getAvatarVersion(String filename) {
        int underscore = filename.lastIndexOf('_');
        int ext = filename.lastIndexOf('.');

        if (underscore == -1 || ext == -1)
            return -1;

        String verStr = filename.substring(underscore + 1, ext);

        try {
            return Integer.parseInt(verStr);
        } catch (NumberFormatException e) {
            return -1;
        }
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
    public boolean isHighResolution() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canDelete() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void delete() {
        // Tell the IMI avatar manager to delete the avatar from the
        // server.
        ImiAvatarConfigManager.getImiAvatarConfigManager().deleteAvatar(this);
    }

    /**
     * {@inheritDoc}
     */
    public boolean canConfigure() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public void configure() {
        // Fetch the configuration dialog. There is only a single instance of
        // it. Spawn the call to setAvatar() in a new thread, since it may
        // take a while.
        final ImiAvatarDetailsJDialog dialog =
                ImiAvatarDetailsJDialog.getImiAvatarDetailsJDialog();
        dialog.setVisible(true);
        new Thread() {
            @Override
            public void run() {
                dialog.setAvatar(ImiAvatar.this);
            }
        }.start();
    }

    /**
     * Returns the avatar character given the set of parameters.
     */
    public static WlAvatarCharacter getAvatarCharacter(WonderlandCharacterParams params) {
        // Generate the base url for the avatar configuration. We must annotate
        // the base URL.
        String baseURL = null;
        try {
            String uri = "wla://avatarbaseart/";
            baseURL = AssetUtils.getAssetURL(uri).toString();
        } catch (MalformedURLException excp) {
            logger.log(Level.WARNING, "Unable to annotate URI", excp);
            return null;
        }

        // Generate the set of character attributes, and create a new avatar
        // character from that.
        CharacterParams cp = params.getCharacterParams();
        cp.setBaseURL(baseURL);

        WorldManager wm = ClientContextJME.getWorldManager();
        WlAvatarCharacter wlc = new WlAvatarCharacter.WlAvatarCharacterBuilder(cp, wm).addEntity(false).build();

        // We need to manually apply all of the colors if they are present
        ConfigElement ce = params.getElement(ConfigType.HAIR_COLOR);
        if (ce != null) {
            float r = ((ColorConfigElement)ce).getR();
            float g = ((ColorConfigElement)ce).getG();
            float b = ((ColorConfigElement)ce).getB();
            Manipulator.setHairColor(wlc, new Color(r, g, b));
        }
        ce = params.getElement(ConfigType.PANTS_COLOR);
        if (ce != null) {
            float r = ((ColorConfigElement)ce).getR();
            float g = ((ColorConfigElement)ce).getG();
            float b = ((ColorConfigElement)ce).getB();
            Manipulator.setPantsColor(wlc, new Color(r, g, b));
        }
        ce = params.getElement(ConfigType.SHIRT_COLOR);
        if (ce != null) {
            float r = ((ColorConfigElement)ce).getR();
            float g = ((ColorConfigElement)ce).getG();
            float b = ((ColorConfigElement)ce).getB();
            Manipulator.setShirtColor(wlc, new Color(r, g, b));
        }
        ce = params.getElement(ConfigType.SHOE_COLOR);
        if (ce != null) {
            float r = ((ColorConfigElement)ce).getR();
            float g = ((ColorConfigElement)ce).getG();
            float b = ((ColorConfigElement)ce).getB();
            Manipulator.setShoesColor(wlc, new Color(r, g, b));
        }
        ce = params.getElement(ConfigType.SKIN_COLOR);
        if (ce != null) {
            float r = ((ColorConfigElement)ce).getR();
            float g = ((ColorConfigElement)ce).getG();
            float b = ((ColorConfigElement)ce).getB();
            Manipulator.setSkinTone(wlc, new Color(r, g, b));
        }
        return wlc;
   }

    /**
     * {@inheritDoc}
     */
    public AvatarConfigInfo getAvatarConfigInfo(ServerSessionManager session) {
        // Ask the configuration manager to the conversion to the server URL
        ImiAvatarConfigManager m = ImiAvatarConfigManager.getImiAvatarConfigManager();
        try {
            String url = m.getAvatarURL(session, this).toExternalForm();
            String className = ImiAvatarLoaderFactory.class.getName();
            return new AvatarConfigInfo(url, className);
        } catch (InterruptedException excp) {
            logger.log(Level.WARNING, "Fetch of avatar URL was interrupted", excp);
            return null;
        }
    }

    /**
     * Returns the IMI configuration GUI set of attributes. If there is no
     * parameters available and 'generate' is true, then attempt to load the
     * parameters from the local resource (if not null).
     *
     * @return The set of configurable attributes
     */
    public WonderlandCharacterParams getAvatarParams(boolean generate) {
        // Generate the params by loading in the CharacterParams XML file from
        // the resource.
        if (params == null && resource != null && generate == true) {
            URL url = null;
            try {
                url = resource.getURL();
            } catch (ContentRepositoryException excp) {
                logger.log(Level.WARNING, "Unable to create url from resource " +
                        resource.getPath(), excp);
                return null;
            }

            logger.info("Generating params from url " + url);
            
            // Create character, fetch the IMI CharacterParams and generate a
            // WonderlandCharacterParams from that.
            WlAvatarCharacter wlchar = createAvatarCharacter(url);
            CharacterParams cp = wlchar.getCharacterParams();
            WonderlandCharacterParams wcp = null;
            try {
                if (cp.isMale() == true) {
                    wcp = WonderlandCharacterParams.loadMale();
                }
                else {
                    wcp = WonderlandCharacterParams.loadFemale();
                }
            } catch (java.io.IOException excp) {
                logger.log(Level.WARNING, "Unable to load male/female params", excp);
                return null;
            }
            wcp.setCharacterParams(cp);
            params = wcp;
        }
        return params;
    }

    /**
     * Sets the set of GUI configuration parameters for the character. This
     * does not actually update the appearance of the avatar yet.
     *
     * @param params The new set of parameters for the avatar
     */
    public void setAvatarParams(WonderlandCharacterParams params) {
        this.params = params;
    }

    /**
     * Builds and returns a WlAvatarCharacter based upon a URL of a configuration
     * file.
     */
    private WlAvatarCharacter createAvatarCharacter(URL url) {
        // Generate the base url for the avatar configuration. We must annotate
        // the base URL.
        String baseURL = null;
        try {
            String uri = "wla://avatarbaseart/";
            baseURL = AssetUtils.getAssetURL(uri).toString();
        } catch (MalformedURLException excp) {
            logger.log(Level.WARNING, "Unable to annotate URI", excp);
            return null;
        }

        WorldManager wm = ClientContextJME.getWorldManager();
        return new WlAvatarCharacter.WlAvatarCharacterBuilder(url, wm).baseURL(baseURL).addEntity(false).build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        try {
            return avatarName + " : " + version + "  " +
                    (resource == null ? "null" : resource.getURL().toExternalForm());
        } catch (ContentRepositoryException ex) {
            return avatarName + " : " + version + "  nullURL";
        }
    }
}
