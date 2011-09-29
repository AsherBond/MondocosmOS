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

import java.io.File;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.assetmgr.AssetDB.AssetDBRecord;
import org.jdesktop.wonderland.common.AssetType;
import org.jdesktop.wonderland.common.AssetURI;
import org.jdesktop.wonderland.common.InternalAPI;

/**
 * Manages the assets that are cached by the Asset Manager.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@InternalAPI
public class AssetCache {

    /**
     * Asset cache policy:
     * SINGLE: Only a single copy of a uri is permitted in the cache
     * MULTIPLE: Multiple copies of a uri are permitted in the cache
     */
    public enum CachePolicy { SINGLE, MULTIPLE };

    private static Logger logger = Logger.getLogger(AssetCache.class.getName());
    private AssetFactory assetFactory = null;
    private AssetDB assetDB = null;
    private File cacheDir = null;

    /** Default constructor */
    public AssetCache(AssetFactory factory) throws AssetDBException {
        assetDB = new AssetDB();
        assetFactory = factory;
        cacheDir = new File(ClientContext.getUserDirectory(), "cache");
    }

    /**
     * Returns the AssetDB associated with this asset manager
     *
     * @return An AssetDB object
     */
    public AssetDB getAssetDB() {
        return assetDB;
    }


    /**
     * Returns the name of the directory in which the assets are cache.
     *
     * @return The asset manager cache directory
     */
    public String getCacheDirectory() {
        return cacheDir.getPath();
    }

    /**
     * Returns true if the given asset is cached, false if not.
     *
     * @param asset The asset to check whether it is cached
     * @return True if the asset is cached, false if not
     * @throw AssetCacheException Upon error accessing the cache
     */
    public boolean isCached(AssetID assetID) throws AssetCacheException {
        try {
            return assetDB.getAsset(assetID) != null;
        } catch (SQLException excp) {
            logger.log(Level.WARNING, "Failed to check asset db cache for " +
                    "asset uri " + assetID.toString(), excp);
            throw new AssetCacheException("Failed to check asset db cace for " +
                    "asset uri " + assetID.toString());
        }
    }

    /**
     * Returns a list of assets for the given uri. Returns an empty list if
     * none are present, throws AssetCacheException upon error
     *
     * @param assetURI The uri of the asset list to fetch
     * @return A list of assets
     * @throws AssetCacheException Upon error accessing the cache
     */
    public List<Asset> getAssetList(AssetURI assetURI) throws AssetCacheException {
        // Fetch the list of records from the asset database for the uri.
        List<Asset> assetList = new LinkedList();
        List<AssetDBRecord> recordList = null;
        try {
            recordList = assetDB.getAssetList(assetURI);
        } catch (SQLException excp) {
            logger.log(Level.WARNING, "Failed to check asset db cache for " +
                    "asset uri " + assetURI.toExternalForm(), excp);
            throw new AssetCacheException("Failed to check asset db cace for " +
                    "asset uri " + assetURI.toExternalForm());
        }

        // Convert into a list of assets and put in the list
        for (AssetDBRecord record : recordList) {
            // Generate a new AssetURI class based upon the string uri, if
            // it exists
            AssetURI recordURI = AssetURI.uriFactory(record.assetURI);
            if (recordURI == null) {
                continue;
            }

            // Now generate a new Asset class
            AssetType assetType = AssetType.valueOf(record.type);
            AssetID assetID = new AssetID(recordURI, record.checksum);
            Asset asset = assetFactory.assetFactory(assetType, assetID);
            assetList.add(asset);
        }
        return assetList;
    }

    /**
     * Adds an asset to the cache, according to the given cache policy. This
     * method assumes the asset has already been placed in the proper cache
     * file.
     *
     * @param asset The asset to add to the cache
     * @param policy The cache policy (SINGLE versus MULTIPLE)
     * @throw AssetCacheException Upon error accessing the cache
     */
    public void addAsset(Asset asset, CachePolicy policy) throws AssetCacheException {
        // If the cache policy is "SINGLE" we have to remove all other assets
        // with the same uri from the database and cache. If the cache policy
        // is "MULTIPLE", then we just add the asset to the cache database.
        if (policy == CachePolicy.SINGLE) {

            // Fetch the list of all of the assets in the database. Loop
            // through each asset, any for any that doesn't match the given
            // asset, remove from the database and delete the cache file.
            String checksum = asset.getChecksum();
            List<AssetDBRecord> assetList = null;
            try {
                assetList = assetDB.getAssetList(asset.getAssetURI());
            } catch (SQLException excp) {
                logger.log(Level.WARNING, "Failed to check asset db cache for " +
                    "asset uri " + asset.getAssetURI(), excp);
                throw new AssetCacheException("Failed to check asset db cache" +
                    " for asset uri " + asset.getAssetURI());
            }

            for (AssetDBRecord assetRecord : assetList) {
                if (checksum.equals(assetRecord.checksum) == true) {
                    continue;
                }

                // Create the asset using the asset factory. We also must
                // explicitly set the cache file
                Asset dbAsset = assetFactory.assetFactory(assetRecord);
                AssetID assetID = new AssetID(dbAsset.getAssetURI(), dbAsset.getChecksum());
                dbAsset.setLocalCacheFile(new File(getAssetCacheFileName(assetID)));
                deleteAsset(dbAsset);
            }
        }

        // Add the new asset into the system, whether the policy is SINGLE or
        // MULTIPLE
        AssetDBRecord assetRecord = new AssetDBRecord();
        assetRecord.assetURI = asset.getAssetURI().toExternalForm();
        assetRecord.checksum = asset.getChecksum();
        assetRecord.type = asset.getType().toString();
        assetRecord.baseURL = asset.getBaseURL();
        File file = asset.getLocalCacheFile();
        if (file != null) {
            assetRecord.size = file.length();
        }

        try {
            assetDB.addAsset(assetRecord);
        } catch (SQLException excp) {
            logger.log(Level.WARNING, "Failed to add asset db cache for " +
                    "asset uri " + asset.getAssetURI(), excp);
            throw new AssetCacheException("Failed to add asset db cache" +
                    " for asset uri " + asset.getAssetURI());
        }
    }

    /**
     * Removes an asset from the cache. This the asset is not present, this
     * method does nothing.
     *
     * @param asset The asset to remove from the cache
     */
    public void deleteAsset(Asset asset) throws AssetCacheException {
        logger.warning("Deleting asset from cache " +
                asset.getAssetURI().toExternalForm() + " " + asset.getChecksum());
        
        AssetID assetID = new AssetID(asset.getAssetURI(), asset.getChecksum());
        try {
            asset.getLocalCacheFile().delete();
            assetDB.deleteAsset(assetID);
        } catch (SQLException excp) {
            logger.log(Level.WARNING, "Failed to remove asset db cache for " +
                    "asset uri " + asset.getAssetURI(), excp);
                throw new AssetCacheException("Failed to remove asset db cache" +
                    " for asset uri " + asset.getAssetURI());
        }
    }

    /**
     * Given the unique ID for the asset, return the name of its cache file.
     * This method accounts for the structure of the cache imposed because of
     * different sorts of uri's.
     *
     * @param assetID The unique id (uri + checksum) of the asset
     * @return An absolute path name of the asset
     */
   String getAssetCacheFileName(AssetID assetID) {
        String basePath = getCacheDirectory();
        String relativePath = assetID.getAssetURI().getRelativeCachePath();
        String checksum = assetID.getChecksum();

        // make sure the checksum only contains valid characters
        checksum = checksum.replaceAll("[^\\w\\d\\.]+", "_");

        return basePath + File.separator + relativePath + File.separator + checksum;
    }

    /**
     * Close this cache and its underlying database
     */
    void close() {
       assetDB.disconnect();
    }
}
