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
package org.jdesktop.wonderland.client.cell.asset;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.AssetURI;

/**
 * Utilities for dealing with assets on the client.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class AssetUtils {

    private static Logger logger = Logger.getLogger(AssetUtils.class.getName());

    /**
     * Given a URI as an asset and the cell the asset is being used in, returns
     * a URL that can be used to download the asset.
     *
     * @param uri The asset URI
     * @return A URL for the asset
     * @throw MalformedURLException Upon error forming the URL
     */
    public static URL getAssetURL(String uri, Cell cell) throws MalformedURLException {
        // First try to form an AssetURI class given the uri string. If none
        // exists, then return the uri directly as a URL
        AssetURI assetURI = AssetURI.uriFactory(uri);
        if (assetURI == null) {
            logger.warning("Unable to find AssetURI object for " + uri);
            return new URL(uri);
        }

        // From the cell, find out what the associated wonderland session is
        // and fetch the server host name and port from that
        WonderlandSession session = cell.getCellCache().getSession();
        ServerSessionManager manager = session.getSessionManager();
        if (manager == null) {
            logger.warning("Unable to find manager for session " + session);
            return new URL(uri);
        }
        String serverHostAndPort = manager.getServerNameAndPort();

        // Annotate the URI with the host name and port from the session and
        // return as a URL
        assetURI.setServerHostAndPort(serverHostAndPort);
        return assetURI.toURL();
    }

    /**
     * Given a URI as an asset, returns a URL that can be used to download the
     * asset. This method uses the current primary server session as the host
     * name and port for the asset, if applicable.
     *
     * @param uri The asset URI
     * @return A URL for the asset
     * @throw MalformedURLException Upon error forming the URL
     */
    public static URL getAssetURL(String uri) throws MalformedURLException {
        // First try to form an AssetURI class given the uri string. If none
        // exists, then return the uri directly as a URL
        AssetURI assetURI = AssetURI.uriFactory(uri);
        if (assetURI == null) {
            logger.warning("Unable to find AssetURI object for " + uri);
            return new URL(uri);
        }

        // Use the primary login session to determine the server host and port
        // name
        ServerSessionManager manager = LoginManager.getPrimary();
        if (manager == null) {
            logger.warning("No primary login session for " + uri);
            return new URL(uri);
        }
        String serverHostAndPort = manager.getServerNameAndPort();

        // Annotate the URI with the host name and port from the session and
        // return as a URL
        assetURI.setServerHostAndPort(serverHostAndPort);
        return assetURI.toURL();
    }

    /**
     * Given a URI as an asset and the server name and port (as server:port),
     * returns a URL that can be used to download the asset.
     *
     * @param uri The asset URI
     * @param serverNameAndPort The server:port of the current server
     * @return A URL for the asset
     * @throw MalformedURLException Upon error forming the URL
     */
    public static URL getAssetURL(String uri, String serverNameAndPort) throws MalformedURLException {
        // First try to form an AssetURI class given the uri string. If none
        // exists, then return the uri directly as a URL
        AssetURI assetURI = AssetURI.uriFactory(uri);
        if (assetURI == null) {
            logger.warning("Unable to find AssetURI object for " + uri);
            return new URL(uri);
        }

        // Annotate the URI with the host name and port from the session and
        // return as a URL
        assetURI.setServerHostAndPort(serverNameAndPort);
        return assetURI.toURL();
    }
}
