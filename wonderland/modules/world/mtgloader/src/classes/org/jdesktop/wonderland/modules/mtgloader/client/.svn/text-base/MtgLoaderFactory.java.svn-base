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
package org.jdesktop.wonderland.modules.mtgloader.client;

import org.jdesktop.wonderland.client.BaseClientPlugin;
import org.jdesktop.wonderland.client.ClientPlugin;
import org.jdesktop.wonderland.client.content.ContentImportManager;
import org.jdesktop.wonderland.client.jme.artimport.LoaderManager;
import org.jdesktop.wonderland.client.jme.artimport.ModelLoader;
import org.jdesktop.wonderland.client.jme.artimport.ModelLoaderFactory;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.modules.jmecolladaloader.client.ModelDndContentImporter;

/**
 *
 * @author paulby
 */
public class MtgLoaderFactory extends ModelLoaderFactory
    implements ClientPlugin
{
    /** A BaseClientPlugin that delegate activate and deactivate to the parent */
    private BaseClientPlugin plugin;
    private ModelDndContentImporter importer;

    public void initialize(ServerSessionManager loginManager) {
        LoaderManager.getLoaderManager().registerLoader(this);
        this.importer = new ModelDndContentImporter(loginManager, new String[] {getFileExtension()});
        this.plugin = new BaseClientPlugin() {
            @Override
            protected void activate() {
                MtgLoaderFactory.this.register();
            }

            @Override
            protected void deactivate() {
                MtgLoaderFactory.this.unregister();
            }
        };

        plugin.initialize(loginManager);
    }

    public void cleanup() {
        LoaderManager.getLoaderManager().unregisterLoader(this);
        plugin.cleanup();
    }

    protected void register() {
        LoaderManager.getLoaderManager().activateLoader(this);
        ContentImportManager cim = ContentImportManager.getContentImportManager();
        cim.registerContentImporter(importer);
    }

    protected void unregister() {
        LoaderManager.getLoaderManager().deactivateLoader(this);
        ContentImportManager cim = ContentImportManager.getContentImportManager();
        cim.unregisterContentImporter(importer);
    }

    public String getFileExtension() {
        return null;
    }

    public ModelLoader getLoader() {
        return (ModelLoader) new MtgLoader();
    }

    @Override
    public String getLoaderClassname() {
        return MtgLoader.class.getName();
    }
}
