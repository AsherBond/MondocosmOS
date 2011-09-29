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

package org.jdesktop.wonderland.client.assetmgr;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Special FileInputStream that takes an asset and uses the local file
 * from that asset.
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
public class AssetInputStream extends FileInputStream {
    private final Asset asset;

    public AssetInputStream(Asset asset) throws FileNotFoundException {
        super (asset.getLocalCacheFile());

        this.asset = asset;
    }

    /**
     * Get the asset associated with this stream
     */
    public Asset getAsset() {
        return asset;
    }
}
