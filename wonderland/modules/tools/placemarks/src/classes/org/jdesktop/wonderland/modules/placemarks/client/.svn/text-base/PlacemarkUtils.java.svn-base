/**
 * Open Wonderland
 *
 * Copyright (c) 2011, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.modules.placemarks.client;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepositoryRegistry;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode.Type;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;
import org.jdesktop.wonderland.modules.placemarks.api.common.Placemark;
import org.jdesktop.wonderland.modules.placemarks.common.PlacemarkList;

/**
 * A utility class to load and save User placemark information. This is used to
 * populate the Placemarks main menu item (User section).
 * <p>
 * User placemark information is stored in the user's local webdav directory
 * (under placemarks/). Each registry entry has a unique placemark name, that
 * also serves as the name of the file. (User-specific and System placemarks
 * (created through the Placemark capability) may have the same names).
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class PlacemarkUtils {

    private static Logger logger = Logger.getLogger(PlacemarkUtils.class.getName());

    /**
     * Returns a list of user-specific placemarks.
     *
     * @return A PlacemarkList object
     */
    public static PlacemarkList getUserPlacemarkList() {
        // First fetch the content collection of user placemarks.
        // Upon error, log an error and return an empty list. Then fetch the
        // items from this collection
        try {
            ContentCollection sysNode = getUserPlacemarksContentCollection();
            if (sysNode == null) {
                return new PlacemarkList();
            }
            return getItemList(sysNode);
        } catch (ContentRepositoryException excp) {
            logger.log(Level.WARNING, "Unable to get user placemarks", excp);
            return new PlacemarkList();
        } catch (JAXBException excp) {
            logger.log(Level.WARNING, "Unable to parse user placemarks", excp);
            return new PlacemarkList();
        }
    }

    /**
     * Returns a list of placemarks given the directory in which to find the
     * placemarks.xml file.
     *
     * @param collection The collection to read placemarks.xml
     * @return A PlacemarkList object
     * @throw ContentRepositoryException Upon error reading placemarks.xml
     * @throw JAXBException Upon error parsing placemarks.xml
     */
    private static PlacemarkList getItemList(ContentCollection collection)
            throws ContentRepositoryException, JAXBException {

        // Find the placemarks.xml file and parse as a PlacemarkList object.
        ContentResource resource = (ContentResource)collection.getChild("placemarks.xml");
        if (resource == null) {
            logger.warning("Unable to find placemarks.xml in " + collection.getPath());
            return new PlacemarkList();
        }
        
        Reader r = new InputStreamReader(resource.getInputStream());
        PlacemarkList out = PlacemarkList.decode(r);
    
        // Filtering out placemarks that are not for the server we're logged on.
        // getPlacemarksAsList() returns a copy, so we can safely remove 
        // placemarks without ConcurrentModificationExceptions 
        String serverURL = LoginManager.getPrimary().getServerURL();
        for (Placemark p : out.getPlacemarksAsList()) {
           if (!p.getUrl().equals(serverURL)) {
               out.removePlacemark(p.getName());
           }
        }
        
        return out;
    }

    /**
     * Writes a list of placemarks given the directory in which to find the
     * placemarks.xml file.
     *
     * @param collection The collection to writer placemarks.xml
     * @param placemarkList The new set of placemarks
     * @return A PlacemarkList object
     * @throw ContentRepositoryException Upon error reading placemarks.xml
     * @throw JAXBException Upon error parsing placemarks.xml
     */
    private static void setItemList(ContentCollection collection, PlacemarkList placemarkList)
            throws ContentRepositoryException, JAXBException {

        // Find the placemarks.xml file, creating it if necessary.
        ContentResource resource = (ContentResource)collection.getChild("placemarks.xml");
        if (resource == null) {
            resource = (ContentResource)collection.createChild("placemarks.xml", Type.RESOURCE);
        }

        // Write the new list to the resource
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Writer w = new OutputStreamWriter(os);
        placemarkList.encode(w);
        resource.put(os.toByteArray());
    }

    /**
     * Adds a Placemark to the user's content collection. Overwrites any existing
     * Placemark with the same name.
     *
     * @param placemark The Placemark to add
     * @throw ContentRepositoryException Upon error accessing the content repo
     * @throw JAXBException Upon error writing the information
     */
    public static void addUserPlacemark(Placemark placemark)
            throws ContentRepositoryException, JAXBException {

        // First fetch the PlacemarkList for the user and add the new Placemark
        // and write the new list out
        PlacemarkList placemarkList = getUserPlacemarkList();
        placemarkList.addPlacemark(placemark);
        ContentCollection c = getUserPlacemarksContentCollection();
        setItemList(c, placemarkList);
    }

    /**
     * Removes a placemark from the user's content collection. If the entry is
     * not present, this method does nothing.
     *
     * @param name The name of the placemark to remove
     * @throw ContentRepositoryException Upon error accessing the content repo
     * @throw JAXBException Upon error writing the information
     */
    public static void removeUserPlacemark(String name)
            throws ContentRepositoryException, JAXBException {

        // First fetch the PlacemarkList for the user and remove the Placemark
        // and write the new list out
        PlacemarkList placemarkList = getUserPlacemarkList();
        placemarkList.removePlacemark(name);
        ContentCollection c = getUserPlacemarksContentCollection();
        setItemList(c, placemarkList);
    }

    /**
     * Returns the content collection for the user-specific placemarks. This
     * method creates the directory if it does not yet exist.
     *
     * @throw ContentRepositoryException Upon error reading or creating
     * @return The ContentConnection holding the user placemarks
     */
    private static ContentCollection getUserPlacemarksContentCollection()
            throws ContentRepositoryException {

        ContentCollection userRoot = getUserContentRepository();
        ContentCollection dir = (ContentCollection)userRoot.getChild("placemarks");
        if (dir == null) {
            dir = (ContentCollection)userRoot.createChild("placemarks", Type.COLLECTION);
        }
        return dir;
    }

    /**
     * Returns the user's content repository for the primary session, creating
     * it if it does not yet exist.
     *
     * @param The user ContentCollection for the current primary session
     */
    private static ContentCollection getUserContentRepository()
            throws ContentRepositoryException {

        ContentRepositoryRegistry registry = ContentRepositoryRegistry.getInstance();
        return registry.getLocalRepository();
    }
}
