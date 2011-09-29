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
package org.jdesktop.wonderland.modules.viewproperties.client;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.client.jme.ViewProperties;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepositoryRegistry;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode.Type;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;

/**
 * A utility class to load and save view properties information. These are
 * loaded during started and saved by the View Properties dialog.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ViewPropertiesUtils {

    private static Logger logger = Logger.getLogger(ViewPropertiesUtils.class.getName());
    private static String PROPS_FILE = "viewproperties.xml";

    /**
     * Returns the view properties stored in the user's local repository. If
     * no such properties file exist, returns a default ViewProperties object.
     *
     * @return A ViewProperties object
     * @throw ContentRepositoryException Upon error reading viewproperties.xml
     * @throw JAXBException Upon error parsing viewproperties.xml
     */
    public static ViewProperties loadViewProperties()
            throws ContentRepositoryException, JAXBException {

        // Find the viewproperites.xml file and parse as a ViewProperties object.
        ContentCollection c = getUserContentRepository();
        ContentResource resource = (ContentResource)c.getChild(PROPS_FILE);
        if (resource == null) {
            logger.warning("Unable to find " + PROPS_FILE + " in " + c.getPath());
            return new ViewProperties();
        }
        
        Reader r = new InputStreamReader(resource.getInputStream());
        return ViewProperties.decode(r);
    }

    /**
     * Saves the given view properties to the user's local repository.
     *
     * @param viewProperties The set of properties to save
     * @throw ContentRepositoryException Upon error writing viewproperties.xml
     * @throw JAXBException Upon error writing viewproperties.xml
     */
    public static void saveViewProperties(ViewProperties viewProperties)
            throws ContentRepositoryException, JAXBException {

        // Find the viewproperites.xml file, creating it if necessary.
        ContentCollection c = getUserContentRepository();
        ContentResource resource = (ContentResource)c.getChild(PROPS_FILE);
        if (resource == null) {
            resource = (ContentResource)c.createChild(PROPS_FILE, Type.RESOURCE);
        }

        // Write the new list to the resource
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Writer w = new OutputStreamWriter(os);
        viewProperties.encode(w);
        resource.put(os.toByteArray());
    }

    /**
     * Returns the user's local content repository.
     *
     * @param The user local repository
     */
    private static ContentCollection getUserContentRepository()
            throws ContentRepositoryException {

        ContentRepositoryRegistry registry = ContentRepositoryRegistry.getInstance();
        return registry.getLocalRepository();
    }
}
