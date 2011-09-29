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

package org.jdesktop.wonderland.client.assetmgr.modules;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import org.jdesktop.wonderland.client.assetmgr.AssetCache.CachePolicy;
import org.jdesktop.wonderland.client.assetmgr.AssetManager;
import org.jdesktop.wonderland.client.assetmgr.AssetStream;
import org.jdesktop.wonderland.common.AssetURI;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.common.ModuleURI;

/**
 * An asset stream specifically for assets contained within modules.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@InternalAPI
public class ModuleAssetStream extends AssetStream {

    private int contentLength = -1;
    private String checksum = null;
    private InputStream inputStream = null;

    /**
     * Constructor that take the response code and id of the asset
     */
    public ModuleAssetStream(AssetResponse response, AssetURI uri) {
        super(response, uri, null);
    }

    /**
     * Constructor that take the response code, id of the asset and base URL
     * from which to download the asset.
     */
    public ModuleAssetStream(AssetResponse response, AssetURI uri,
            String checksum, String baseURL) {

        super(response, uri, stripTrailingSlash(baseURL));
        this.checksum = checksum;
    }

    /**
     * @inheritDoc()
     */
    @Override
    public CachePolicy getCachePolicy() {
        return CachePolicy.MULTIPLE;
    }

    /**
     * @inheritDoc()
     */
    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * @inheritDoc()
     */
    @Override
    public void open() {
        // Fetch the input stream, last modification date, and content length
        try {
            String url = getAssetURL();
            URLConnection urlConnection = new URL(url).openConnection();
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
            contentLength = urlConnection.getContentLength();
        } catch (IOException excp) {
            logger.log(Level.WARNING, "Unable to open asset " +
                    getAssetURI().toExternalForm(), excp);
            inputStream = null;
        }
    }

    /**
     * @inheritDoc()
     */
    @Override
    public void close() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException excp) {
            // Just ignore this exception
        }
    }

    /**
     * @inheritDoc()
     */
    @Override
    public int getContentLength() {
        return contentLength;
    }

    /**
     * @inheritDoc()
     */
    @Override
    public String getChecksum() {
        return checksum;
    }

    /**
     * Returns the complete url of where to find the asset
     */
    private String getAssetURL() {
        // Make sure the base URL of the repository from which to download is
        // not invalid, if so, then return null
        String baseURL = getBaseURL();
        if (baseURL == null) {
            return null;
        }

        // Assume the asset is of type ModuleURI and construct the URL based
        // upon the relative path of the asset
        ModuleURI moduleURI = (ModuleURI) getAssetURI();
        String path = moduleURI.getRelativePathInModule();
        String url = AssetManager.encodeSpaces(baseURL + "/" + path);
        return url;
    }

    /**
     * Strips the trailing '/' if it exists on the string.
     */
    private static String stripTrailingSlash(String str) {
        if (str.endsWith("/") == true) {
            return str.substring(0, str.length() - 1);
        }
        return str;
    }
}
