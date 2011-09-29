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
package org.jdesktop.wonderland.modules.avatarbase.client.registry;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode.Type;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;

/**
 * A collection of static utility methods to help with avatar configuration.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class AvatarConfigUtils {
   
    private static Logger logger = Logger.getLogger(AvatarConfigUtils.class.getName());
    
    // The name of the configuration file to store the avatar configuration
    // settings
    private final static String CONFIG_FILENAME = "avatar_settings.xml";

    /**
     * Loads and returns the avatar configuration settings from the given
     * content collection in the user's local repository.
     *
     * @param c The ContentCollection to load the settings
     * @return The avatar configuration settings
     * @throw ContentRepositoryException Upon error reading the avatar settings
     * @throw JAXBException Upon error reading the avatar settings
     */
    public static AvatarConfigSettings loadConfigSettings(ContentCollection c)
            throws JAXBException, ContentRepositoryException {

        // Check to see if the CONFIG_FILENAME file resource exists, log an
        // error and return a new AvatarConfigSettings object if necessary.
        ContentResource resource = (ContentResource) c.getChild(CONFIG_FILENAME);
        if (resource == null) {
            logger.warning("Unable to find " + CONFIG_FILENAME + " in " + c.getPath());
            return new AvatarConfigSettings();
        }

        Reader r = new InputStreamReader(resource.getInputStream());
        return AvatarConfigSettings.decode(r);
    }

    /**
     * Saves the given avatar configuration settings to the user's local
     * repository.
     *
     * @param c The ContentCollection to save the settings
     * @param settings The settings to save
     * @throw ContentRepositoryException Upon error writing the avatar settings
     * @throw JAXBException Upon error writing the avatar settings
     */
    public static void saveConfigSettings(ContentCollection c, AvatarConfigSettings settings)
            throws JAXBException, ContentRepositoryException {

        // Check to see if the CONFIG_FILENAME file resource exists, creating
        // it if necessary
        ContentResource resource = (ContentResource)c.getChild(CONFIG_FILENAME);
        if (resource == null) {
            resource = (ContentResource)c.createChild(CONFIG_FILENAME, Type.RESOURCE);
        }

        // Write the given settings as an XML stream to the output file
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Writer w = new OutputStreamWriter(os);
        settings.encode(w);
        resource.put(os.toByteArray());
    }
}
