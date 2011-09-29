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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.logging.Level;
import org.jdesktop.wonderland.client.assetmgr.AssetCache.CachePolicy;
import org.jdesktop.wonderland.client.assetmgr.AssetStream;
import org.jdesktop.wonderland.common.AssetURI;
import org.jdesktop.wonderland.common.InternalAPI;

/**
 * An asset stream for assets served by web servers via http.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@InternalAPI
public class WlContentAssetStream extends AssetStream {

    private URLConnection urlConnection = null;
    private InputStream inputStream = null;
    private long lastModified = -1;
    private int contentLength = -1;

    /**
     * Constructor that take the response code and uri of the asset. Also takes
     * the last modification date of the asset currently known.
     */
    public WlContentAssetStream(AssetResponse response, AssetURI uri) {
        super(response, uri, null);
    }

    /**
     * Constructor that take the response code, uri of the asset and base URL
     * from which to download the asset.
     */
    public WlContentAssetStream(AssetResponse response, AssetURI uri,
            URLConnection urlConnection, String baseURL) {

        super(response, uri, stripTrailingSlash(baseURL));
        this.urlConnection = urlConnection;
    }

    /**
     * @inheritDoc()
     */
    @Override
    public CachePolicy getCachePolicy() {
        return CachePolicy.SINGLE;
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
            inputStream = new BufferedInputStream(urlConnection.getInputStream());
        } catch (IOException excp) {
            logger.log(Level.WARNING, "Unable to open asset " +
                    getAssetURI().toExternalForm(), excp);
            inputStream = null;
        }
        lastModified = urlConnection.getLastModified();
        contentLength = urlConnection.getContentLength();

        logger.fine("Opening asset stream last modified " + lastModified +
                " content length " + contentLength);
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
        return "" + lastModified;
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
