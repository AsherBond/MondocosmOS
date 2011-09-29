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
package org.jdesktop.wonderland.client.protocols.wlj;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import org.jdesktop.wonderland.client.assetmgr.Asset;
import org.jdesktop.wonderland.client.assetmgr.AssetInputStream;
import org.jdesktop.wonderland.client.assetmgr.AssetManager;
import org.jdesktop.wonderland.client.assetmgr.modules.ModuleAssetRepositoryFactory;
import org.jdesktop.wonderland.common.JarURI;

/**
 * The WljURLConnection class is the URL connection to URLs that have the 'wlj'
 * protocol. This is used for Wonderland plugin JARs and interfaces with the asset
 * manager to properly load resources.
 * <p>
 * The format of the URL is:
 * <p>
 * wlj://<module name>/<jar path>
 * <p>
 * where <module name> is the name of the module that contains the asset, and
 * <jar path> is the relative path of the jar within the module and has the
 * form: <plugin name>/<jar type>/<jar name>, where <plugin name> is the unique
 * name of the plugin, <jar type> is either "client", "server", or "common",
 * and <jar name> is the name of the jar file (with the .jar extension)
 * 
 * @author paulby
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class WljURLConnection extends URLConnection {

    /**
     * Constructor, takes the 'wlj' protocol URL as an argument
     * 
     * @param url A URL with a 'wlj' protocol
     */
    public WljURLConnection(URL url) {
        super(url);

        this.setUseCaches(false);
    }
    
    @Override
    public void connect() throws IOException {
    }
    
    @Override
    public InputStream getInputStream() throws IOException {
        try {
            // Since we know this asset belongs to a module, first create a
            // factory to handle its loading.
            JarURI uri = new JarURI(this.url.toExternalForm());
            ModuleAssetRepositoryFactory factory = new ModuleAssetRepositoryFactory(uri);

            // Next, we ask the asset manager for the asset and wait for it to
            // be loaded
            Asset asset = AssetManager.getAssetManager().getAsset(uri, factory);
            if (asset == null || AssetManager.getAssetManager().waitForAsset(asset) == false) {
                throw new IOException("No such asset " + url);
            }

            return new AssetInputStream(asset);
        } catch (URISyntaxException excp) {
            IOException ioe = new IOException("Error in URI syntax");
            ioe.initCause(excp);
            throw ioe;
        }
    }
}
