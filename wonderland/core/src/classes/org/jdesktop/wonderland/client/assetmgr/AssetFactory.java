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

import java.util.logging.Logger;
import org.jdesktop.wonderland.client.assetmgr.AssetDB.AssetDBRecord;
import org.jdesktop.wonderland.common.AssetType;
import org.jdesktop.wonderland.common.AssetURI;
import org.jdesktop.wonderland.common.InternalAPI;

/**
 * Generates Asset classes based upon the type.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@InternalAPI
public class AssetFactory {

    private static Logger logger = Logger.getLogger(AssetFactory.class.getName());

    /**
     * Factory for creating assets of the required type, takes the type of
     * asset desired (given by the AssetType enumeration) and the unique URI
     * that describes the asset
     *
     * @param assetType The type of the asset
     * @param assetID The unique ID describing the asset
     * @return The new Asset, or null upon error
     */
    public Asset assetFactory(AssetType assetType, AssetID assetID) {
        switch(assetType) {
            case FILE :
                return new AssetFile(assetID);

            case OTHER :
                throw new RuntimeException("Not implemented");
        }

        return null;
    }

    /**
     * Factory for creating assets of the required type, given the asset database
     * record.
     *
     * @param assetRecord The database record of the asset
     * @return The new Asset, or null upon error
     */
    public Asset assetFactory(AssetDBRecord assetRecord) {
        // Use the known set of AssetURI classes to generate one
        AssetURI assetURI = AssetURI.uriFactory(assetRecord.assetURI);
        if (assetURI == null) {
            logger.warning("Unable to find AssetURI class for " + assetRecord.assetURI);
            return null;
        }

        // Create the Asset class now
        AssetID assetID = new AssetID(assetURI, assetRecord.checksum);
        AssetType assetType = AssetType.valueOf(assetRecord.type);
        Asset asset = assetFactory(assetType, assetID);
        asset.setChecksum(assetRecord.checksum);
        asset.setBaseURL(assetRecord.baseURL);
        return asset;
    }
}
