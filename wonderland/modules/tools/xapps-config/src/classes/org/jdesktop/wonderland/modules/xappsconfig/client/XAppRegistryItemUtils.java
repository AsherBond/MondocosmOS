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
package org.jdesktop.wonderland.modules.xappsconfig.client;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepositoryRegistry;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode.Type;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;
import org.jdesktop.wonderland.modules.xappsconfig.common.XAppRegistryItem;

/**
 * A utility class to load and save X App registry item information. This is
 * used to populate Cell Palettes to launch various X Apps.
 * <p>
 * There are two places X App registry item information is stored: in the
 * user's webdav directory (under x-apps/) and system-wide (under x-apps/). Each
 * registry entry has a unique app name, that also serves as the name of the
 * file. (User-specific and system-wide X Apps may have the same name across
 * each).
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class XAppRegistryItemUtils {

    private static Logger logger = Logger.getLogger(XAppRegistryItemUtils.class.getName());

    /**
     * Returns a list of system-wide X App Registry items. If no items are
     * present, returns an empty list.
     *
     * @return A List of XAppRegistryItem objects
     */
    public static List<XAppRegistryItem> getSystemXAppRegistryItemList() {
        // First fetch the content collection of the system-wide X Apps items.
        // Upon error, log an error and return an empty list. Then fetch the
        // items from this collection
        try {
            ContentCollection sysNode = getSystemXAppContentCollection();
            if (sysNode == null) {
                return new LinkedList<XAppRegistryItem>();
            }
            return getItemList(sysNode);
        } catch (ContentRepositoryException excp) {
            logger.log(Level.WARNING, "Unable to get system-wide x-apps", excp);
            return new LinkedList<XAppRegistryItem>();
        }
    }

    /**
     * Returns a list of user-specific X App Registry items. If no items are
     * present, returns an empty list.
     *
     * @return A List of XAppRegistryItem objects
     */
    public static List<XAppRegistryItem> getUserXAppRegistryItemList() {
        // First fetch the content collection of the user-specific X Apps items.
        // Upon error, log an error and return an empty list. Then fetch the
        // items from this collection
        try {
            ContentCollection userNode = getUserXAppContentCollection();
            if (userNode == null) {
                return new LinkedList<XAppRegistryItem>();
            }
            return getItemList(userNode);
        } catch (ContentRepositoryException excp) {
            logger.log(Level.WARNING, "Unable to get user x-apps", excp);
            return new LinkedList<XAppRegistryItem>();
        }
    }

    /**
     * Returns a list of X Apps Registry items. If no items are present, returns
     * an empty list.
     *
     * @param collection The collection to read the X App items from
     * @return A List of XAppRegistryItem objects
     */
    private static List<XAppRegistryItem> getItemList(ContentCollection collection) {
        List<XAppRegistryItem> itemList = new LinkedList();

        // Find the list of content resources within this directory. For each
        // parse the XML and store it in the list.
        List<ContentNode> children = null;
        try {
            children = collection.getChildren();
        } catch (ContentRepositoryException excp) {
            logger.log(Level.WARNING, "Unable to get x-apps children", excp);
            return itemList;
        }

        // Loop through all of the children and attempt to parse them and add
        // to the list.
        for (ContentNode node : children) {
            // Make sure the node is a resource, if not, we ignore the directory
            if (node instanceof ContentCollection) {
                logger.warning("Found content collection " + node.getPath() +
                        " in x-apps collection, ignorning.");
                continue;
            }
            ContentResource resource = (ContentResource)node;
            try {
                XAppRegistryItem item = parseResource(resource);
                itemList.add(item);
            } catch (ContentRepositoryException excp) {
                logger.log(Level.WARNING, "Unable to read entry in x-apps: " +
                        node.getPath(), excp);
            } catch (JAXBException excp) {
                logger.log(Level.WARNING, "Unable to parse entry in x-apps: " +
                        node.getPath(), excp);
            }
        }
        return itemList;
    }

    /**
     * Given a resource in a content collection, parse the XML as an X App
     * registry item (XAppRegistryItem) class.
     *
     * @param resource The content resource to parse as XML
     */
    private static XAppRegistryItem parseResource(ContentResource resource)
            throws ContentRepositoryException, JAXBException {

        // Fetch the input stream (and write as a Reader) of the resource. Just
        // pass along the exceptions to the caller
        Reader reader = new InputStreamReader(resource.getInputStream());
        return XAppRegistryItem.decode(reader);
    }

    /**
     * Adds an X App Registry item to the user's content collection. Overwrites
     * any existing entry, if there is one.
     *
     * @param item The XAppRegistryItem to add
     * @throw ContentRepositoryException Upon error accessing the content repo
     * @throw JAXBException Upon error writing the information
     */
    public static void addUserXAppRegistryItem(XAppRegistryItem item)
            throws ContentRepositoryException, JAXBException {

        // First fetch the content collection of the user-specific X Apps items
        // and look for the file <app name>.xml.
        ContentCollection userNode = getUserXAppContentCollection();
        String nodeName = item.getAppName() + ".xml";
        ContentNode appNode = userNode.getChild(nodeName);
        if (appNode == null) {
            appNode = userNode.createChild(nodeName, Type.RESOURCE);
        }
        ContentResource resource = (ContentResource)appNode;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Writer w = new OutputStreamWriter(os);
        item.encode(w);
        byte b[] = os.toByteArray();
        resource.put(b);
    }

    /**
     * removes an X App Registry item from the user's content collection. If
     * the entry is not present, this method does nothing.
     *
     * @param item The XAppRegistryItem to remove
     * @throw ContentRepositoryException Upon error accessing the content repo
     */
    public static void removeUserXAppRegistryItem(XAppRegistryItem item)
            throws ContentRepositoryException {

        // First fetch the content collection of the user-specific X Apps items
        // and look for the file <app name>.xml.
        ContentCollection userNode = getUserXAppContentCollection();
        String nodeName = item.getAppName() + ".xml";
        ContentNode appNode = userNode.getChild(nodeName);
        if (appNode != null) {
            userNode.removeChild(nodeName);
        }
    }

    /**
     * Returns the content collection for the system-wide X App Registry items.
     * This method creates the directory if it does not yet exist.
     *
     * @throw ContentRepositoryException Upon error reading or creating
     * @return The ContentConnection holding the system-wide X App items
     */
    private static ContentCollection getSystemXAppContentCollection()
            throws ContentRepositoryException {

        ContentCollection sysRoot = getSystemContentRepository();
        ContentCollection dir = (ContentCollection)sysRoot.getChild("x-apps");
        return dir;
    }

    /**
     * Returns the content collection for the user-specific X App Registry items.
     * This method creates the directory if it does not yet exist.
     *
     * @throw ContentRepositoryException Upon error reading or creating
     * @return The ContentConnection holding the system-wide X App items
     */
    private static ContentCollection getUserXAppContentCollection()
            throws ContentRepositoryException {

        ContentCollection userRoot = getUserContentRepository();
        ContentCollection dir = (ContentCollection)userRoot.getChild("x-apps");
        if (dir == null) {
            dir = (ContentCollection)userRoot.createChild("x-apps", Type.COLLECTION);
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

    /**
     * Returns the system-wide content repository for the primary session.
     *
     * @param The system-wide ContentCollection for the current primary session
     */
    private static ContentCollection getSystemContentRepository()
            throws ContentRepositoryException {

        ServerSessionManager session = LoginManager.getPrimary();
        ContentRepositoryRegistry registry = ContentRepositoryRegistry.getInstance();
        return registry.getRepository(session).getSystemRoot();
    }
}
