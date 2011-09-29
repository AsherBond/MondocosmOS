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

import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.assetmgr.AssetCache;
import org.jdesktop.wonderland.client.assetmgr.AssetCacheException;
import org.jdesktop.wonderland.client.assetmgr.AssetID;
import org.jdesktop.wonderland.client.assetmgr.AssetManager;
import org.jdesktop.wonderland.client.assetmgr.AssetRepository;
import org.jdesktop.wonderland.client.assetmgr.AssetStream;
import org.jdesktop.wonderland.client.assetmgr.AssetStream.AssetResponse;
import org.jdesktop.wonderland.common.AssetURI;
import org.jdesktop.wonderland.common.InternalAPI;

/**
 * The ModuleAssetRepository implements the AssetRepository and represents a
 * repository that serves assets from the Wonderland module system.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@InternalAPI
public class ModuleAssetRepository implements AssetRepository {

    private static Logger logger = Logger.getLogger(ModuleAssetRepository.class.getName());

    /* The base URL of the repository from which to fetch assets */
    private String baseURL = null;

    /* The desired checksum of the asset */
    private String desiredChecksum = null;

    /**
     * Constructor, takes the base URL associated with the repository. It also
     * takes the desired checksum of the asset
     */
    public ModuleAssetRepository(String baseURL, String desiredChecksum) {
        this.baseURL = baseURL;
        this.desiredChecksum = desiredChecksum;
    }

    /*
     * @inheritDoc()
     */
    public AssetStream openAssetStream(AssetURI assetURI) {
        String uriString = assetURI.toExternalForm();

        // If there is no desired checksum, it means that the module has
        // not been installed properly. For now, we just return an error, but
        // we could handle this different (e.g. just return whatever asset we
        // can find
        AssetID assetID = new AssetID(assetURI, desiredChecksum);
        if (desiredChecksum == null) {
            logger.warning("Opening asset stream, no checkum for asset " +
                    uriString);
            return new ModuleAssetStream(AssetResponse.ASSET_INVALID, assetURI);
        }

        // Next check whether the asset is already cached. If so, return a stream
        // that indicates such
        if (isAssetCached(assetID) == true) {
            logger.fine("Opening asset stream, asset is already cached " +
                    uriString);
            return new ModuleAssetStream(AssetResponse.ASSET_CACHED, assetURI);
        }

        // Otherwise, create a stream with the base URL, desired checksum,
        // and asset id. Return it.
        logger.fine("Opening asset stream with base url " + baseURL +
                " for asset " + uriString);
        
        return new ModuleAssetStream(AssetResponse.STREAM_READY, assetURI,
                desiredChecksum, baseURL);
    }

    /**
     * Checks whether the asset is already cached and returns true if so,
     * false if not
     */
    private boolean isAssetCached(AssetID assetID) {
        // Using the asset URI and the desired checksum check whether the asset
        // is in the database (cached)
        AssetCache assetCache = AssetManager.getAssetManager().getAssetCache();
        try {
            return assetCache.isCached(assetID);
        } catch (AssetCacheException excp) {
            logger.log(Level.WARNING, "Unable to check cache for asset " +
                    assetID.toString(), excp);
            return false;
        }
    }
}
