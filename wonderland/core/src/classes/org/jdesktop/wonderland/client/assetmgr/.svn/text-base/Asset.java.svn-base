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
package org.jdesktop.wonderland.client.assetmgr;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jdesktop.wonderland.client.assetmgr.AssetManager.AssetProgressListener;
import org.jdesktop.wonderland.common.AssetType;
import org.jdesktop.wonderland.common.AssetURI;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * The Asset class represents an asset (e.g. artwork) in the system. An asset
 * is uniquely identified by a combination of its URI (see AssetURI class) and
 * an optional checksum. Assets with no checksum are considered the same asset.
 * <p>
 * Each asset has a type: typically, either file, image, or model and given by
 * the AssetType enumeration.
 * <p>
 * The url gives the full URL from which the asset was downloaded
 * <p>
 * @author paulby
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
public abstract class Asset<T> {
    // possible asset status
    public enum Status { SCHEDULED, DOWNLOADING, DOWNLOADED, FAILED };

    protected AssetType type = null;
    protected AssetURI assetURI = null;
    protected File localCacheFile = null;
    protected String checksum = null;
    protected String baseURL = null;
    
    // current status
    private Status status = Status.SCHEDULED;

    // if the asset is in the "downloading" state, this will be the
    // percentage complete
    private int downloadBytes;
    private int downloadPercent;

    // if the asset is in the "failed" state, this will be the reason
    private String failureInfo = null;

    // status listeners
    private final Collection<AssetProgressListener> listeners =
            new CopyOnWriteArraySet<AssetProgressListener>();

    /**
     * Constructor that takes the unique URI as an argument.
     * 
     * @param assetURI The unique identifying asset URI.
     */
    public Asset(AssetID assetID) {
        this.assetURI = assetID.getAssetURI();
        this.checksum = assetID.getChecksum();
    }

    /**
     * Returns the asset type, typically either a file, image, or model.
     * 
     * @return The type of asset
     */
    public AssetType getType() {
        return type;
    }

    /**
     * Returns the unique URI describing the asset.
     * 
     * @return The unique URI describing the asset
     */
    public AssetURI getAssetURI() {
        return this.assetURI;
    }
    
    /**
     * Return the file containing the local cache of the asset
     * 
     * @return
     */
    public File getLocalCacheFile() {
        return localCacheFile;
    }

    void setLocalCacheFile(File localCacheFile) {
        this.localCacheFile = localCacheFile;
    }

    /**
     * Returns the local cache file as a URL, or null if the asset is not
     * cached.
     *
     * @return The local cache file as a URL
     * @throw MalformedURLException If the URL is malformed
     */
    public URL getLocalCacheFileAsURL() throws MalformedURLException {
        if (localCacheFile != null) {
            String fname = AssetManager.encodeSpaces(localCacheFile.getAbsolutePath());
            return new URL("file://" + fname);
        }
        return null;
    }


    /**
     * Get the checksum of this file in the local cache.
     * @return
     */
    public String getChecksum() {
        return checksum;
    }

    void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    /**
     * Returns the base URL from which the asset was downloaded, null if unknown
     *
     * @return The base URL
     */
    public String getBaseURL() {
        return baseURL;
    }

    void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }
    
    @Override
    public String toString() {
        return "(" + this.getAssetURI().toString() + " @ " + this.checksum + ")";
    }
    
    /**
     * Called whenever the asset has been downloaded from the server
     */
    abstract void postProcess();

    /**
     * Load and return an asset from the local cache. Multiple instances
     * of the same asset can be shared this call will implement the necessary
     * sharing.
     * 
     * Returns true if load was succesful, otherwise returns false.
     */
    abstract boolean loadLocal();
    
    /**
     * Asset has been unloaded, cleanup.
     */
    abstract void unloaded();
    
    /**
     * Return the asset
     * @return
     */
    public abstract T getAsset();


    public void addAssetProgressListener(AssetProgressListener listener) {
        switch (getStatus()) {
            case DOWNLOADED:
                // shortcut if we are already downloaded
                listener.downloadCompleted(this);
                break;

            case FAILED:
                // shortcut if we have already failed
                listener.downloadFailed(this);
                break;

            case DOWNLOADING:
                // report current status
                listener.downloadProgress(this, getDownloadBytes(), getDownloadPercent());
                listeners.add(listener);
                break;

            case SCHEDULED:
                listeners.add(listener);
                break;
        }
    }

    public void removeAssetProgressListener(AssetProgressListener listener) {
        listeners.remove(listener);
    }

    protected void fireStatusChanged(Status status) {
        for (AssetProgressListener l : listeners) {
            switch (status) {
                case DOWNLOADING:
                    l.downloadProgress(this, getDownloadBytes(), getDownloadPercent());
                    break;

                case DOWNLOADED:
                    l.downloadCompleted(this);
                    break;

                case FAILED:
                    l.downloadFailed(this);
                    break;
            }
        }
    }

    public synchronized Status getStatus() {
        return status;
    }
    
    protected synchronized void setStatus(Status status) {
        this.status = status;
    }

    protected void setDownloadProgress(int downloadBytes,
                                       int downloadPercent)
    {
        synchronized (this) {
            if (getStatus() == Status.DOWNLOADED || getStatus() == Status.FAILED) {
                throw new IllegalStateException("Can't download when status is " +
                                                getStatus());
            }

            setStatus(Status.DOWNLOADING);

            this.downloadBytes = downloadBytes;
            this.downloadPercent = downloadPercent;
        }

        fireStatusChanged(Status.DOWNLOADING);
    }

    public synchronized int getDownloadBytes() {
        return downloadBytes;
    }

    public synchronized int getDownloadPercent() {
        return downloadPercent;
    }

    protected void setDownloadSuccess() {
        synchronized (this) {
            if (getStatus() == Status.DOWNLOADED || getStatus() == Status.FAILED) {
                throw new IllegalStateException("Can't succeed when status is " +
                                                getStatus());
            }

            setStatus(Status.DOWNLOADED);
        }

        fireStatusChanged(Status.DOWNLOADED);
    }

    public synchronized String getFailureInfo() {
        return failureInfo;
    }

    protected synchronized void setDownloadFailure(String failureInfo) {
        synchronized (this) {
            if (getStatus() == Status.DOWNLOADED || getStatus() == Status.FAILED) {
                throw new IllegalStateException("Can't fail when status is " + 
                                                getStatus());
            }
        
            setStatus(Status.FAILED);
            this.failureInfo = failureInfo;
        }

        fireStatusChanged(Status.FAILED);
    }
}
