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
package org.jdesktop.wonderland.client.protocols.wlcontent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import org.jdesktop.wonderland.client.assetmgr.Asset;
import org.jdesktop.wonderland.client.assetmgr.AssetInputStream;
import org.jdesktop.wonderland.client.assetmgr.AssetManager;
import org.jdesktop.wonderland.common.ContentURI;

/**
 * The WlHttpURLConnection class is the URL connection to URLs that have the
 * 'wlcontent' protocol. There are used to handle assets in the system content
 * repository
 * 
 * @author paulby
 */
public class WlContentURLConnection extends URLConnection {

    /**
     * Constructor, takes the 'wlcontent' protocol URL as an argument
     * 
     * @param url A URL with a 'wlcontent' protocol
     */
    public WlContentURLConnection(URL url) {
        super(url);
    }
    
    @Override
    public void connect() throws IOException {
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        try {
            // First create a factory to handle the loading from a url
            ContentURI uri = new ContentURI(this.url.toExternalForm());

            // Next, we ask the asset manager for the asset and wait for it to
            // be loaded
            Asset asset = AssetManager.getAssetManager().getAsset(uri);
            if (asset == null || AssetManager.getAssetManager().waitForAsset(asset) == false) {
                throw new IOException("No such asset "+url);
            }
            return new AssetInputStream(asset);
        } catch (URISyntaxException excp) {
            IOException ioe = new IOException("Error in URI syntax");
            ioe.initCause(excp);
            throw ioe;
        }        
    }
}
