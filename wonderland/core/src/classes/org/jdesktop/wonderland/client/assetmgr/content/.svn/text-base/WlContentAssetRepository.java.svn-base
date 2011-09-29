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

package org.jdesktop.wonderland.client.assetmgr.content;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.assetmgr.AssetManager;
import org.jdesktop.wonderland.client.assetmgr.AssetRepository;
import org.jdesktop.wonderland.client.assetmgr.AssetStream;
import org.jdesktop.wonderland.client.assetmgr.AssetStream.AssetResponse;
import org.jdesktop.wonderland.common.AssetURI;
import org.jdesktop.wonderland.common.ContentURI;
import org.jdesktop.wonderland.common.InternalAPI;

/**
 * The WlHttpAssetRepository implements the AssetRepository and represents a
 * repository that serves assets via a web server through http.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@InternalAPI
public class WlContentAssetRepository implements AssetRepository {

    private static Logger logger = Logger.getLogger(WlContentAssetRepository.class.getName());
    private String baseURL = null;
    private long lastModified = -1;

    /**
     * Constructor, takes the base URL associated with the repository. It also
     * takes the last modified date of the checksum currently cached, -1 if
     * not present.
     */
    public WlContentAssetRepository(String baseURL, long lastModified) {
        this.baseURL = baseURL;
        this.lastModified = lastModified;
    }

    /*
     * @inheritDoc()
     */
    public AssetStream openAssetStream(AssetURI assetURI) {
        // If there is no last modified date, then log a warning. We will
        // still fetch whatever asset we find on the server.
        if (lastModified == -1) {
            logger.fine("Opening asset stream, no last modified date for " +
                    "asset " + assetURI.toExternalForm());
        }

        // Find out the URL of the asset to download, make sure it is not null
        String urlString = getURL(assetURI);
        if (urlString == null) {
            logger.warning("Unable to get URL for asset " + assetURI.toExternalForm());
            return new WlContentAssetStream(AssetResponse.ASSET_INVALID, assetURI);
        }

        logger.fine("For URI " + urlString + " last modified " + lastModified);
        
        // Open the URL to the asset stream, giving the last modified date if
        // we have one. We fetch the response back from the server and set
        // whether the asset is already cached or whether we should download
        // it.
        URLConnection urlConnection = null;
        int response;
        try {
            URL url = new URL(urlString);
            urlConnection = url.openConnection();
            if (lastModified != -1) {
                urlConnection.setIfModifiedSince(lastModified);
            }
            urlConnection.connect();
            response = ((HttpURLConnection) urlConnection).getResponseCode();
        } catch (IOException excp) {
            logger.log(Level.WARNING, "Unable to open URL for asset " +
                    urlString, excp);
            return new WlContentAssetStream(AssetResponse.ASSET_INVALID, assetURI);
        }

        // Check to see whether the resopnse is HTTP_NOT_MODIFIED and use
        // the cached version if so. If we are ok, then create an asset stream
        // ready for download.
        if (response == HttpURLConnection.HTTP_NOT_MODIFIED) {
            logger.fine("Opening asset stream, asset is already cached " +
                    assetURI.toExternalForm());
            return new WlContentAssetStream(AssetResponse.ASSET_CACHED, assetURI);
        }
        else if (response == HttpURLConnection.HTTP_OK) {
            logger.fine("Opening asset stream with base url " + baseURL +
                    " for asset " + assetURI.toExternalForm());
            return new WlContentAssetStream(AssetResponse.STREAM_READY, assetURI,
                    urlConnection, baseURL);
        }
        else {
            logger.warning("Unable to open URL for asset " + urlString +
                    " response " + response);
            return new WlContentAssetStream(AssetResponse.ASSET_INVALID, assetURI);
        }
    }

    /**
     * Returns the full url corresonding to the asset.
     */
    private String getURL(AssetURI assetURI) {
        // Make sure the base URL of the repository from which to download is
        // not invalid, if so, then return null
        if (baseURL == null) {
            return null;
        }

        // Otherwise, formulate the URL to open. We need to add the base url
        // of the repository to the relative path of the asset.
        ContentURI contentURI = (ContentURI)assetURI;
        String path = contentURI.getRelativePath();
        String url = AssetManager.encodeSpaces(baseURL + path);
        return url;
    }
}
