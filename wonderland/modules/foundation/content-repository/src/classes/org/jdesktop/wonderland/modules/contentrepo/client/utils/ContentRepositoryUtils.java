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
package org.jdesktop.wonderland.modules.contentrepo.client.utils;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.AssetURI;
import org.jdesktop.wonderland.common.ContentURI;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepository;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepositoryRegistry;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;

/**
 * A set of utility routines for content repositories.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ContentRepositoryUtils {

    private static Logger logger = Logger.getLogger(ContentRepositoryUtils.class.getName());
    
    /**
     * From the given URI, returns the ContentNode object to which it
     * corresponds in the content repository. If the URI does not correspond
     * to the content repository returns null. Takes the server session to which
     * the client is connected, null to use the primary server session.
     *
     * @param uri The URI of the asset
     * @return The ContentNode of the uri
     */
    public static ContentNode findContentNode(ServerSessionManager session, String uri) {
        // Try to form the uri into an AssetURI class. If it can't return
        // log an error and return null;
        AssetURI assetURI = AssetURI.uriFactory(uri);
        if (assetURI == null) {
            logger.warning("Unable to parse uri " + uri);
            return null;
        }

        // The URI needs to correspond to a content repository URI
        if (!(assetURI instanceof ContentURI)) {
            logger.warning("URI is not a content uri " + uri);
            return null;
        }
        ContentURI contentURI = (ContentURI)assetURI;
        String root = contentURI.getRoot();
        String assetPath = contentURI.getAssetPath();

        // If the given server session is null, use the primary one
        if (session == null) {
            session = LoginManager.getPrimary();
        }

        // Fetch the content repository for the server session
        ContentRepositoryRegistry registry = ContentRepositoryRegistry.getInstance();
        ContentRepository contentrepo = registry.getRepository(session);

        // Munge the asset path to include the system or user root prefix
        String totalPath = root + "/" + assetPath;
        try {
            // Fetch the content node based upon the path from the root and
            // return
            ContentCollection rootCollection = contentrepo.getRoot();
            return rootCollection.getChild(totalPath);
        } catch (ContentRepositoryException excp) {
            // Log an error and return
            logger.log(Level.WARNING, "Unable to find content node " + totalPath, excp);
            return null;
        }
    }
}
