/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
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

package org.jdesktop.wonderland.client.assetmgr.http;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import org.jdesktop.wonderland.client.assetmgr.Asset;
import org.jdesktop.wonderland.client.assetmgr.AssetCache;
import org.jdesktop.wonderland.client.assetmgr.AssetCacheException;
import org.jdesktop.wonderland.client.assetmgr.AssetManager;
import org.jdesktop.wonderland.client.assetmgr.AssetRepository;
import org.jdesktop.wonderland.client.assetmgr.AssetRepositoryFactory;
import org.jdesktop.wonderland.common.AssetURI;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.common.WlHttpURI;

/**
 * A factory that returns an ordered list of asset repositories for an asset
 * that is contained within a module.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@InternalAPI
public class WlHttpAssetRepositoryFactory extends AssetRepositoryFactory {
    private static final List<HttpChecksum> CHECKSUMS =
            new ArrayList<HttpChecksum>();
    static {
       CHECKSUMS.add(new IfModifiedSinceHttpChecksum());
       CHECKSUMS.add(new ETagHttpChecksum());
    }

    private final String checksum;

    public WlHttpAssetRepositoryFactory(AssetURI assetURI) {
        super(assetURI);
        checksum = getChecksumFromDB(assetURI);
        isAlwaysDownload = true;
    }

    /**
     * @inheritDoc()
     */
    @Override
    public String getDesiredChecksum() {
        return checksum;
    }

    /**
     * @inheritDoc()
     */
    @Override
    public AssetRepository[] getAssetRepositories() {
        // Create the one asset repository for the base URL of the server
        AssetRepository[] assetRepositories = new AssetRepository[1];
        WlHttpURI uri = (WlHttpURI)getAssetURI();
        String baseURL = uri.getBaseURL();
        assetRepositories[0] = new WlHttpAssetRepository(baseURL, getDesiredChecksum());

        return assetRepositories;
    }

    /**
     * Get an HttpChecksum from a String, or return null if the given String
     * doesn't map to a checksum
     */
    static HttpChecksum getChecksumFor(String string) {
        if (string == null || string.trim().length() == 0) {
            return null;
        }

        for (HttpChecksum c : CHECKSUMS) {
            if (string.trim().startsWith(c.getPrefix())) {
                return c;
            }
        }

        return null;
    }

    /**
     * Get a String checksum for the given connection
     */
    static String getChecksumFor(HttpURLConnection connection) {
        for (HttpChecksum c : CHECKSUMS) {
            String checksum = c.get(connection);
            if (checksum != null) {
                return checksum;
            }
        }

        return null;
    }

    /**
     * Asks the asset database for the checksum
     */
    private String getChecksumFromDB(AssetURI assetURI) {
        // Query the asset database for an entry with this URI, looking for
        // the most recent checksum. This assumes that, although we are returned
        // a list of assets, there is only one asset, because it is the policy
        // to have only one wlhttp entry per cache.
        AssetCache assetCache = AssetManager.getAssetManager().getAssetCache();
        List<Asset> assetList = null;
        try {
            assetList = assetCache.getAssetList(assetURI);
        } catch (AssetCacheException excp) {
        }
        
        if (assetList.size() > 1) {
            logger.warning("Found more than one asset for " +
                    assetURI.toExternalForm());
        }
        if (assetList.size() == 0) {
            logger.fine("Unable to find asset in cache for " +
                    assetURI.toExternalForm());
            return null;
        }
        Asset asset = assetList.get(0);
        return asset.getChecksum();
    }

    /**
     * Internal interface for handling checksums, which may be if-modified-since
     * or etags.
     */
    interface HttpChecksum {
        /**
         * Get the prefix for this type. The return value from get() should
         * start with prefix.
         */
        public String getPrefix();

        /**
         * Get the checksum from an HttpURLConnection, or return null if no
         * checksum of this type exists on the given connection
         */
        public String get(HttpURLConnection connection);

        /**
         * Set the checksum in a request
         */
        public void set(HttpURLConnection connection, String checksum);
    }

    /**
     * Implementation for if-modified-since headers
     */
    static class IfModifiedSinceHttpChecksum implements HttpChecksum {
        private final String PREFIX = "If-Modified-Since:";

        public String getPrefix() {
            return PREFIX;
        }

        public String get(HttpURLConnection connection) {
            if (connection.getIfModifiedSince() > 0) {
                return PREFIX + connection.getIfModifiedSince();
            }

            return null;
        }

        public void set(HttpURLConnection connection, String checksum) {
            long lastModified = Long.parseLong(checksum.substring(PREFIX.length()));
            connection.setIfModifiedSince(lastModified);
        }
    }

    /**
     * Implementation for if-modified-since headers
     */
    static class ETagHttpChecksum implements HttpChecksum {
        private final String PREFIX = "ETag:";

        public String getPrefix() {
            return PREFIX;
        }

        public String get(HttpURLConnection connection) {
            if (connection.getHeaderField("ETag") != null) {
                return PREFIX + connection.getHeaderField("ETag");
            }

            return null;
        }

        public void set(HttpURLConnection connection, String checksum) {
            String etag = checksum.substring(PREFIX.length());
            connection.setRequestProperty("If-None-Match", etag);
        }
    }
}
