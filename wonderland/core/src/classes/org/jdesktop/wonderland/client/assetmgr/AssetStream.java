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

package org.jdesktop.wonderland.client.assetmgr;

import java.io.InputStream;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.assetmgr.AssetCache.CachePolicy;
import org.jdesktop.wonderland.common.AssetURI;
import org.jdesktop.wonderland.common.InternalAPI;

/**
 * The AssetStream class represents a stream from which the asset manager can
 * download data, or get an indication that the asset already exists cached
 * within the system.
 * <p>
 * This abstract base class is subclassed by protocol-specific handlers, for
 * example, wla (for module assets), that compute the proper checksums once the
 * asset data is download.
 * <p>
 * For asset streams with response of STREAM_READY, the connect() method must
 * be called first, before calls to getChecksum(), getContentLength() and
 * getInputStream(). Calls to these methods before the connect() method is
 * called will return invalid results. The close() method should be called
 * when complete.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@InternalAPI
public abstract class AssetStream {

    /**
     * An enumeration that indicates the status of the stream:
     *
     * STREAM_READY: An input stream is available to be read of the asset data
     * ASSET_CACHED: The desired asset is already cached by the asset manager
     * ASSET_INVALID: The repository does not contain the proper asset
     */
    public enum AssetResponse {
        STREAM_READY,
        ASSET_CACHED,
        ASSET_INVALID
    };

    protected static Logger logger = Logger.getLogger(AssetStream.class.getName());
    private AssetResponse response = null;
    private AssetURI assetURI = null;
    private String baseURL = null;

    /**
     * Constructs an AssetStream with the given response cached asset id or
     * asset input stream
     */
    protected AssetStream(AssetResponse response, AssetURI uri, String baseURL) {
        this.response = response;
        this.assetURI = uri;
        this.baseURL = baseURL;
    }


    /**
     * Returns the base URL from which the asset will be downloaded, or null
     * if the asset is not found or is alredy cached.
     *
     * @return The base URL of the asset to download
     */
    public String getBaseURL() {
        return baseURL;
    }

    /**
     * Returns the asset uri associated with this stream.
     *
     * @return The AssetURI object
     */
    public AssetURI getAssetURI() {
        return assetURI;
    }

    /**
     * Returns the response of the stream to creation: whether the asset is
     * already cached (ASSET_CACHED), whether the stream is invalid (ASSET_INVALID)
     * or whether the asset is ready to be downloaded (STREAM_READY)
     * 
     * @return The AssetResponse enumeration
     */
    public AssetResponse getResponse() {
        return response;
    }

    /**
     * Returns an input stream suitable to read the data, or null if the asset
     * is already cached or the repository contains invalid data.
     *
     * @return An InputStream object
     */
    public abstract InputStream getInputStream();

    /**
     * Opens the asset stream for reading. This method must be called before
     * getInputStream(), getChecksum(), and getContentLength() methods.
     */
    public abstract void open();

    /**
     * Invoked when the user of this class is done reading data from the stream
     * and wants the AssetStream to do any cleanup.
     */
    public abstract void close();

    /**
     * Returns the asset checksum (a string that identifies the asset) after
     * the open() method has been called.
     *
     * @return A String checksum
     */
    public abstract String getChecksum();

    /**
     * Returns the length of the content (bytes) in the stream. Returns -1 if
     * the content lenght is not know. This method must be called after the
     * open() method is invoked.
     *
     * @return The length of content in the stream.
     */
    public abstract int getContentLength();

    /**
     * Returns the desired cache policy for the asset: whether to allow multiple
     * copies of the same asset uri or only a single instance.
     *
     * @return The CachePolicy
     */
    public abstract CachePolicy getCachePolicy();
}
