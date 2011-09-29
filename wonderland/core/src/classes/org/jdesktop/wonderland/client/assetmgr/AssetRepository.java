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

import org.jdesktop.wonderland.common.AssetURI;
import org.jdesktop.wonderland.common.InternalAPI;

/**
 * The AssetRepository interface represents a single instance of a server from
 * which assets can be downloaded. Its single method openAssetStream() returns
 * an AssetStream object which can be used to download data or indicate that
 * the cached version already exists in the asset cache. It is up to the
 * implementation of this class to determine this for its particular kind of
 * asset.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@InternalAPI
public interface AssetRepository {

    /**
     * Attempts to open an input stream to the given asset. This method is
     * responsible for checking whether the asset is already cached and the
     * desired checksum (or other identity information) for the asset. It
     * returns an AssetStream object in response, either containing the input
     * stream, or whether the repository is invalid or whether the asset is
     * already cached.
     *
     * @param assetURI The URI of the asset to load
     * @return An AssetStream object indicating the result
     */
    public AssetStream openAssetStream(AssetURI assetURI);
}
