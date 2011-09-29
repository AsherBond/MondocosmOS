/**
 * Open Wonderland
 *
 * Copyright (c) 2010 - 2011, Open Wonderland Foundation, All Rights Reserved
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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.assetmgr.AssetStream.AssetResponse;
import org.jdesktop.wonderland.client.assetmgr.TrackingInputStream.ProgressListener;
import org.jdesktop.wonderland.client.assetmgr.content.WlContentAssetRepositoryFactory;
import org.jdesktop.wonderland.client.assetmgr.http.WlHttpAssetRepositoryFactory;
import org.jdesktop.wonderland.client.assetmgr.modules.ModuleAssetRepositoryFactory;
import org.jdesktop.wonderland.common.AssetType;
import org.jdesktop.wonderland.common.AssetURI;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.ThreadManager;

/**
 * AssetManager provides services for downloading and maintaining the latest
 * version of asset data for the system. Primary use is for Images (Textures) and
 * geometry files of various types.
 * 
 * @author paulby
 */
@ExperimentalAPI
public class AssetManager {
    
    private static final Logger logger = Logger.getLogger(AssetManager.class.getName());
    private final AssetFactory assetFactory;
    private final Set<AssetProgressListener> progressListeners =
            new CopyOnWriteArraySet<AssetProgressListener>();

    private AssetCache assetCache;

    // A map that maintains protocols and the asset repository factories that
    // handle them.
    private static final Map<String, Class<? extends AssetRepositoryFactory>> protocolFactoryMap =
            new HashMap<String, Class<? extends AssetRepositoryFactory>>();
    static {
        protocolFactoryMap.put("wla", ModuleAssetRepositoryFactory.class);
        protocolFactoryMap.put("wlhttp", WlHttpAssetRepositoryFactory.class);
        protocolFactoryMap.put("wlcontent", WlContentAssetRepositoryFactory.class);
    };

    /*
     * A map of assets currently being loaded, where the key is the unique ID
     * of the asset and the value is the loader reponsible for loading it
     */
    private final HashMap<AssetID, AssetLoader> loadingAssets;
    
    /*
     * A map of assets already loaded by the asset manager, where the key is the
     * unique ID of the asset and the value is the Asset object itself.
     */
    private final HashMap<AssetID, Asset> loadedAssets;
    
    /* The number of threads to use for each of the downloading service */
    private static final int NUMBER_THREADS = 10;    
    private final ExecutorService downloadService = Executors.newFixedThreadPool(AssetManager.NUMBER_THREADS);
    
    /* Receive updates every 10 KB during downloads */
    private static final int UPDATE_BYTE_INTERVAL = 1024 * 10;
    
    /* Number of bytes to read as chunks from the network */
    private static final int NETWORK_CHUNK_SIZE = 50 * 1024;
    
    private AssetManager() {
        assetFactory = new AssetFactory();
        loadingAssets = new HashMap<AssetID, AssetLoader>();
        loadedAssets = new HashMap<AssetID, Asset>();
    }
    
    /**
     * AssetManagerHolder holds the single instance of the AssetManager class.
     * It is loaded upon the first call to AssetManager.getAssetManager(). 
     */
    private static class AssetManagerHolder {
        private final static AssetManager assetManager = new AssetManager();
    }
    
    /**
     * Return the singleton AssetManager.
     * 
     * @return An instance of the AssetManager class
     */
    public static AssetManager getAssetManager() {
        return AssetManagerHolder.assetManager;
    }

    /**
     * Get the asset cache associated with this asset manager.
     * @return the cache associated with this manager
     */
    public synchronized AssetCache getAssetCache() {
        if (assetCache == null) {
            try {
                assetCache = new AssetCache(assetFactory);
            } catch (java.lang.Exception excp) {
                logger.log(Level.WARNING, "Unable to create Asset Cache", excp);
            }
        }

        return assetCache;
    }

    /**
     * Close the asset cache associated with this asset manager. The cache
     * will be reopened on the next call to getAssetCache()
     */
    public synchronized void closeAssetCache() {
        if (assetCache != null) {
            assetCache.close();
            assetCache = null;
        }
    }

    /**
     * Add a progress listener for asset loading.
     *
     * @param listener The asset progress listener
     */
    public void addProgressListener(AssetProgressListener listener) {
        progressListeners.add(listener);
    }

    /**
     * Remove a progress listener for asset loading.
     *
     * @param listener the asset progress listener to remove
     */
    public void removeProgressListener(AssetProgressListener listener) {
        progressListeners.remove(listener);
    }

    /**
     * Fetches the asset from the Asset Manager. If the asset is not in the
     * local cache, then it will be downloaded and cached. This method returns
     * immediately with an Asset object that represents the asset being
     * downloaded or fetched from the cache. Upon error, this method returns
     * null.
     * <p>
     * To receive an event when the asset is ready, attach a listener to the
     * asset.
     *
     * @param assetURI The URI of the asset to fetch
     * @return An Asset object
     */
    public Asset getAsset(AssetURI assetURI) {
        // Fetch the factory that is responsible for the protocol of the given
        // URI
        String protocol = assetURI.getProtocol();
        if (protocol == null) {
            logger.warning("Unable to find protocol for " + assetURI);
            return null;
        }

        Class clazz = protocolFactoryMap.get(protocol);
        if (clazz == null) {
            logger.warning("Unable to find factory for " + assetURI);
            return null;
        }

        try {
            Constructor constructor = clazz.getConstructor(AssetURI.class);
            AssetRepositoryFactory factory = (AssetRepositoryFactory)constructor.newInstance(assetURI);
            return getAsset(assetURI, factory);
        } catch (Exception excp) {
            logger.log(Level.WARNING, "Unable to create factory " +
                    assetURI, excp);
            return null;
        }
    }

    /**
     * Fetches the asset from the Asset Manager. If the asset is not in the
     * local cache, then it will be downloaded and cached. This method returns
     * immediately with an Asset object that represents the asset being
     * downloaded or fetched from the cache. Upon error, this method returns
     * null.
     * <p>
     * To receive an event when the asset is ready, attach a listener to the
     * asset.
     * <p>
     * This method also takes the factory that is responsible for fetching the
     * asset from some server.
     *
     * @param assetURI The URI of the asset to fetch
     * @return An Asset object
     */
    public Asset getAsset(AssetURI assetURI, AssetRepositoryFactory factory) {

        synchronized(loadingAssets) {
            // Formulate the id (uri + checksum) of the asset we wish to download.
            // We need this to see if we are already downloading the same asset.
            String checksum = factory.getDesiredChecksum();
            AssetID assetID = new AssetID(assetURI, checksum);

            logger.fine("Getting asset " + assetURI.toExternalForm());
            logger.fine("Desired checksum " + checksum);

            // Check to see if the asset is currently being downloaded. We use
            // the Asset object as a key -- which lets us uniquely identify an
            // asset based upon its URI and checksum.
            if (loadingAssets.containsKey(assetID) == true) {
                logger.fine("We are already downloading asset " + assetURI);
                return loadingAssets.get(assetID).getAsset();
            }

            synchronized (loadedAssets) {

                // Otherwise, see if the asset has already been loaded. An
                // equivalent Asset object is in the list of loaded assets. We
                // only take the loaded asset immediately if we do not care
                // to check again whether we really do have the latest asset.
                // (e.g. in HTTP if-modified-since)
                if (factory.isAlwaysDownload() == false) {
                    if (loadedAssets.containsKey(assetID) == true) {
                    logger.fine("Asset has already been downloaded " + assetURI);
                        return loadedAssets.get(assetID);
                    }
                }

                // Submit a request to download the asset from the server
                // asynchronous. We immediately return the Asset object here
                logger.fine("Spawning service to download asset " + assetURI);
                Asset asset = assetFactory.assetFactory(AssetType.FILE, assetID);

                AssetLoader loader = new AssetLoader(asset, factory);
                loadingAssets.put(assetID, loader);
                Future f = downloadService.submit(loader);
                loader.setFuture(f);

                return asset;
            }
        }
    }

    /**
     * Wait for the specified asset to load. This method will return once
     * the asset is either loaded, or the load fails.
     * 
     * If the load is successful true is returned, otherwise false is returned
     * 
     * @param asset
     * @return true if asset is ready, false if there was a failure
     */
    public boolean waitForAsset(Asset asset) {
        try {
            AssetLoader loader;

            synchronized (loadingAssets) {
                AssetID assetID = new AssetID(asset.getAssetURI(), asset.getChecksum());
                loader = loadingAssets.get(assetID);
            }

            /*
             * Fetch the class that is currently loading an asset. If it is null,
             * there is none, so return true. This is situation is a bit odd,
             * but happens when the asset has already been downloaded. Hence
             * we return true.
             */
            logger.fine("Waiting for asset loader to return for " + asset.getAssetURI());
            if (loader == null) {
                return true;
            }
            
            Object o = loader.getFuture().get();
            logger.fine("Waiting for asset finished got " + o + " for asset " + asset.getAssetURI());
           
            if (o == null) {
                // Load failed
                return false;
            }
            return true;
        } catch (InterruptedException ex) {
            //Logger.getLogger(AssetManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            //Logger.getLogger(AssetManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    /**
     * Unload the asset from memory
     * @param asset
     */
    public void unloadAsset(Asset asset) {
        synchronized(loadedAssets) {
            AssetID assetID = new AssetID(asset.getAssetURI(), asset.getChecksum());
            loadedAssets.remove(assetID);
            asset.unloaded();
        }
    }
    
    /**
     * Delete the asset from the local cache
     * @param asset
     */
    public void deleteAsset(Asset asset) {
        synchronized(loadedAssets) {
            AssetID assetID = new AssetID(asset.getAssetURI(), asset.getChecksum());
            loadedAssets.remove(assetID);
            asset.unloaded();
            try {
                getAssetCache().deleteAsset(asset);
            } catch (AssetCacheException excp) {
                logger.log(Level.WARNING, "Unable to delete asset from the " +
                        " cache " + assetID.toString(), excp);
            }
        }
    }

    /**
     * Synchronously download an asset from a server, given the input stream
     * to read the asset, and the Asset object.
     */
    private void loadAssetFromServer(Asset asset, AssetStream assetStream) throws IOException {

        // Surround the input stream with a tracking input stream and register
        // a listener that associates the tracking stream with the asset.
        try {
            TrackingInputStream in = new TrackingInputStream(assetStream.getInputStream());
            StreamProgressListener listener = new StreamProgressListener(asset);
            in.setListener(listener, AssetManager.UPDATE_BYTE_INTERVAL, assetStream.getContentLength());

            // We will put the download bits into the desired cache file. Create
            // that now and open an output stream to it.
            String checksum = assetStream.getChecksum();
            AssetID assetID = new AssetID(asset.getAssetURI(), checksum);
            String cacheFile = getAssetCache().getAssetCacheFileName(assetID);
            File file = new File(cacheFile);
            if (file.canWrite() == false) {
                makeDirectory(file);
            }

            // set the cache file immediately, so it available during download
            // for streaming applications
            asset.setLocalCacheFile(file);

            OutputStream out = new BufferedOutputStream(new FileOutputStream(file));

            // Loop through and download the data in network-size chunks and
            // write to the cache file.
            byte buf[] = new byte[NETWORK_CHUNK_SIZE];
            int c = in.read(buf);
            while (c > 0) {
                out.write(buf, 0, c);
                c = in.read(buf);
            }
            in.close();
            out.close();

            // Tell the listeners that the asset has been successfully downloaded
            fireDownloadCompleted(asset);

            // We tell the asset stream that we are done, and then fetch the
            // checksum of the asset and put it in the asset. We need to update
            // the checksum here in case it has changed (e.g. in the case of
            // HTTP where the checksum is the last-modified date returned from
            // the HTTP GET.
            assetStream.close();
            asset.setChecksum(assetStream.getChecksum());
            asset.setBaseURL(assetStream.getBaseURL());

            logger.fine("Downloaded asset with checksum " +
                    asset.getChecksum() + " for asset " + assetID.getAssetURI());
            logger.fine("Downloaded asset to file " + file.getAbsolutePath() +
                    " for asset " + assetID.getAssetURI());
        } catch (IOException excp) {
            // Tell the listeners that the asset has failed
            fireDownloadFailed(asset);
            throw excp;
        }
    }

    /**
     * Notify listeners of download progress
     * @param asset the asset that has progress
     * @param readBytes the bytes read
     * @param percent the percent of the total
     */
    protected void fireDownloadProgress(Asset asset, int readBytes, int percent) {
        // notify per-asset listeners
        asset.setDownloadProgress(readBytes, percent);
        
        // notify global listeners
        for (AssetProgressListener listener : progressListeners) {
            listener.downloadProgress(asset, readBytes, percent);
        }
    }

    /**
     * Notify listeners that a download has completed
     * @param asset the asset that completed
     */
    protected void fireDownloadCompleted(Asset asset) {
        for (AssetProgressListener listener : progressListeners) {
            listener.downloadCompleted(asset);
        }
    }

    /**
     * Notify listeners that a download has completed
     * @param asset the asset that completed
     */
    protected void fireDownloadFailed(Asset asset) {
        for (AssetProgressListener listener : progressListeners) {
            listener.downloadCompleted(asset);
        }
    }
    /**
     * Make the directory in which this file will go.
     *
     * Removes the trailing filename from File and creates the directory
     * 
     */
    private synchronized void makeDirectory(File file) throws IOException {
        // Method synchronized to avoid problems where lots of calls can cause
        // a failure of the canWrite() check
        String f = file.getAbsolutePath();
        File dir = new File(f.substring(0, f.lastIndexOf(File.separator)));
        dir.mkdirs();
        if (!dir.canWrite()) {
            logger.severe("Unable to create cache dir " + dir.getAbsolutePath());
            throw new IOException("Failed to Create cache dir " + dir.getAbsolutePath());
        }
    }
    
    /**
     * Utility routine that attempts to load the asset from the cache and sets
     * the success or failure information in the asset. Returns the asset
     * upon success, and null upon failure.
     */
    private Asset loadAssetFromCache(Asset asset, String originalChecksum) {
        AssetURI assetURI = asset.getAssetURI();
        String uriString = assetURI.toExternalForm();
        String checksum = asset.getChecksum();

        // Attempt to load the asset from the cache. If it fails, then
        // we set the failure information and notify any listeners and
        // return.
        logger.fine("Loading asset from cache for asset " + uriString);
        if (asset.loadLocal() == false) {
            assetFailed(asset, "Unable to load asset from local cache");
            return null;
        }

        // Otherwise, we remove the asset from the list of loading assets
        // and place in the list of loaded assets.
        synchronized (loadingAssets) {
            synchronized (loadedAssets) {

                // We need to remove the asset using the original checksum from
                // the "loading" list. The checksum may have changed after we
                // have downloaded (e.g. HTTP if-modified-since)
                AssetID originalID = new AssetID(assetURI, originalChecksum);

                logger.fine("Removing from loading assets with uri=" + uriString +
                        ", old checksum=" + originalChecksum);
                logger.fine("Does asset exist in loading list? " +
                        loadingAssets.containsKey(originalID));

                loadingAssets.remove(originalID);

                // Next we put the new asset ID into the "loaded" list
                AssetID assetID = new AssetID(assetURI, checksum);

                logger.fine("Adding to loaded assets with uri=" + uriString +
                        ", new checksum=" + checksum);

                loadedAssets.put(assetID, asset);
                assetSuccess(asset);
                logger.fine("Got asset from cache, put on loaded list " + uriString);
                return asset;
            }
        }
    }
    
    /**
     * Replaces all of the spaces (' ') in a URI string with '%20'
     */
    public static String encodeSpaces(String uri) {
        StringBuilder sb = new StringBuilder(uri);
        int index = 0;
        while ((index = sb.indexOf(" ", index )) != -1) {
            // If we find a space at position 'index', then replace the space
            // and update the value of 'index'. The value of 'index' should be
            // the next character after the replaced '%20', which is index + 3
            sb.replace(index, index + 1, "%20");
            index += 3;
        }
        return sb.toString();
    }

    /**
     * Sets the asset to indicate a loading failure given the string reason
     * why and notifies all of the listeners.
     */
    private void assetFailed(Asset asset, String reason) {
        asset.setDownloadFailure(reason);
    }

    /**
     * Sets the asset to indicate a loading success and notifies all of the
     * listeners
     */
    private void assetSuccess(Asset asset) {
        asset.setDownloadSuccess();
    }

    /**
     * Used to load assets in parallel. This class implements the Callable
     * interface and is run inside of a Java Executer. The class can load
     * assets from both a remote repository and the local file cache, as given
     * by the 'server' flag.
     */
    class AssetLoader implements Callable {
        /* The asset to load */
        private final Asset asset;

        /* The factory which tells us how to download the asset */
        private final AssetRepositoryFactory factory;
        
        /* Object reflecting the results of the asynchronous operation */
        private Future future = null;

        /**
         * Load a given asset, either from local cache or the server.
         * 
         * @param asset The asset to load
         * @param server true loads from server, false for client local cache
         */
        public AssetLoader(Asset asset, AssetRepositoryFactory factory) {
            this.asset = asset;
            this.factory = factory;
        }
        
        /**
         * Return the asset this loader is loading
         * 
         * @return The asset
         */
        public Asset getAsset() {
            return this.asset;
        }
        
        /**
         * Returns the object representing the state of the asynchronous task
         *
         * @return The Future status object
         */
        Future getFuture() {
            return this.future;
        }

        /**
         * Sets the object representing the state of the asynchronous task.
         * Typically this is called by the thread that kicks off the task and
         * sets the Future object returns by the Java Executer service.
         * 
         * @param future The Future status object
         */
        void setFuture(Future future) {
            this.future = future;
        }
        
        /**
         * Called by the asynchronous task service to attempt to load the asset.
         * Returns the asset upon success, null upn failure.
         * 
         * @return Upon success returns the asset, null upon failure
         * @throws java.lang.Exception
         */
        public Object call() throws Exception {
            try {
                // Do the asset download from the server. If the asset is
                // already cached then doAssetDownload() will detect this. The
                // failure information of the asset download is set here as is
                // notifying the asset ready listeners.
                Object ret = doAssetDownload();
                return ret;
            } catch (java.lang.Exception excp) {
                logger.log(Level.WARNING, "Exception in call()", excp);
                throw excp;
            }
        }

        /**
         * Downloads the asset from the server and returns the asset upon success
         * or null upon failure
         */
        private Object doAssetDownload() {
            AssetURI assetURI = asset.getAssetURI();
            String uriString = assetURI.toExternalForm();

            // Keep a copy of the original asset checksum. Sometimes (e.g. in
            // the case of HTTP if-modified-since) the "checksum" can change
            // after we have downloaded. We need to make sure we remove the
            // proper thing from the "loading" and "loaded" lists.
            String originalChecksum = asset.getChecksum();
            
            // Using the repository factory, fetch the list of repositories
            // from which to fetch the asset. It is up to each of the
            // individual repositories to determine whether the asset is
            // already cached or not.
            AssetRepository repositories[] = factory.getAssetRepositories();
            logger.fine("Got a list of repositories " + repositories +
                    " for asset " + uriString);

            for (AssetRepository repository : repositories) {
                logger.fine("Seeing if repository " + repository.toString() +
                        " has asset " + assetURI);

                // Try to open the output stream. If the repository tells
                // us we already have the most up-to-date version, then we
                // simply return that. Otherwise, we attempt to download
                // the asset.
                AssetStream stream = repository.openAssetStream(assetURI);
                AssetResponse response = stream.getResponse();
                logger.fine("Got an asset stream with response " + response +
                        " for asset " + uriString);

                if (response == AssetResponse.ASSET_CACHED) {
                    // The asset is already cache, so we just return that
                    // version. We first need to set up the location of the
                    // cache file first
                    AssetID assetID = new AssetID(assetURI, asset.getChecksum());
                    asset.setLocalCacheFile(new File(getAssetCache().getAssetCacheFileName(assetID)));
                    return loadAssetFromCache(asset, originalChecksum);
                }
                else if (response == AssetResponse.STREAM_READY) {

                    // The asset stream is ready to be downloaded, so we go
                    // ahead and download the asset. Once we do that we
                    // need to add the asset to the cache and then fetch
                    // it from the cache.
                    try {
                        stream.open();
                        loadAssetFromServer(asset, stream);
                        getAssetCache().addAsset(asset, stream.getCachePolicy());
                        stream.close();
                        return loadAssetFromCache(asset, originalChecksum);
                    } catch (java.io.IOException excp) {
                        logger.log(Level.WARNING, "Failed to download asset " +
                                "from this stream " + uriString, excp);
                        continue;
                    } catch (AssetCacheException excp) {
                        logger.log(Level.WARNING, "Failed to cache downloaded" +
                                " asset " + uriString, excp);
                        continue;
                    }
                }
                else {
                    // We did not find a valid repository to load from,
                    // so we will just go into the next one
                    continue;
                }
            }

            // if we got here, the asset was not loaded from any of the
            // repositories, so it has failed
            asset.setDownloadFailure("Unable to load from any repositories");
            return null;
        }
    }

    /**
     * A class that implements the progress listener for a tracking stream,
     * and also associates an Asset. Signals the AssetManager's progress
     * listener
     */
    private class StreamProgressListener implements ProgressListener {
        private Asset asset = null;

        public StreamProgressListener(Asset asset) {
            this.asset = asset;
        }

        public void downloadProgress(int readBytes, int percentage) {
            // notify the listeners of progress
            fireDownloadProgress(asset, readBytes, percentage);
        }
    }

    /**
     * Used to indicate the status of an asset that is being downloaded
     */
    @ExperimentalAPI
    public interface AssetProgressListener {
        /**
         * Updates the amount the asset has been downloaded.
         *
         * @param asset The Asset being downloaded
         * @param readBytes The number of bytes that have been ready
         * @param percentage The percentage of the bytes read, or -1 if unknown
         */
        public void downloadProgress(Asset asset, int readBytes, int percentage);

        /**
         * Indicates the download of the asset has failed
         *
         * @param asset The Asset whose download has failed
         */
        public void downloadFailed(Asset asset);

        /**
         * Indicates the download of the asset has finished successfull.
         *
         * @param asset The Asset whose download has completed
         */
        public void downloadCompleted(Asset asset);
    }

    /* URLs to download */
    private static final String uris[] = {
        "wla://phone/conference_phone.png",
        "wla://phone/conference_phone.png",
        "wla://phone/conference_phone.png",
        "wla://phone/conference_phone.png",
        "wlhttp://localhost:8080/webdav/content/modules/installed/palette/client/palette-client.jar",
        "wlhttp://localhost:8080/webdav/content/modules/installed/palette/client/palette-client.jar",
        "wlhttp://localhost:8080/webdav/content/modules/installed/palette/client/palette-client.jar",
        "wlhttp://localhost:8080/webdav/content/modules/installed/palette/client/palette-client.jar",
        "wlcontent://users/J/reload_architecture.png",
        "wlcontent://users/J/reload_architecture.png",
        "wlcontent://users/J/reload_architecture.png",
        "wlcontent://users/J/reload_architecture.png",

    };

    public static void downloadFile() throws java.net.URISyntaxException {
        final Thread threads[] = new Thread[uris.length];
        for (int i = 0; i < uris.length; i++) {
            final int j = i;
            threads[i] = new Thread(ThreadManager.getThreadGroup(), "AssetMgrDownloader") {
                @Override
                public void run() {
                    Logger logger = Logger.getLogger(AssetManager.class.getName());
                    AssetManager assetManager = AssetManager.getAssetManager();

                    // Create the AssetURI and Asset to use to load. Just
                    // use the localhost:8080 as the server name and port
                    AssetURI assetURI = AssetURI.uriFactory(uris[j]);
                    assetURI.setServerHostAndPort("localhost:8080");

                    // Wait for the asset to load and print out the result
                    Asset asset = assetManager.getAsset(assetURI);
                    assetManager.waitForAsset(asset);
                    logger.fine("Failure info: " + asset.getFailureInfo());
                    if (asset.getLocalCacheFile() == null) {
                        logger.fine("Local Cache File: null");
                    }
                    else {
                        logger.fine("Local Cache File: " + asset.getLocalCacheFile().getAbsolutePath());
                    }
                    logger.fine("Done with: " + assetURI.toString());
                }
            };
            threads[i].start();
        }

        for (int i = 0; i < uris.length; i++) {
            try {
                threads[i].join();
            } catch (java.lang.InterruptedException excp) {
                logger.log(Level.WARNING, "Thread is interrupted", excp);
            }
        }
    }

    public static void main(String[] args) throws URISyntaxException {
        AssetManager.downloadFile();
    }

}
