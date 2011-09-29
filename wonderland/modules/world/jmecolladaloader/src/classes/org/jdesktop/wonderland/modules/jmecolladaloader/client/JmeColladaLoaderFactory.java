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
package org.jdesktop.wonderland.modules.jmecolladaloader.client;

import org.jdesktop.wonderland.client.BaseClientPlugin;
import org.jdesktop.wonderland.client.ClientPlugin;
import org.jdesktop.wonderland.client.content.ContentImportManager;
import org.jdesktop.wonderland.client.jme.artimport.LoaderManager;
import org.jdesktop.wonderland.client.jme.artimport.ModelLoader;
import org.jdesktop.wonderland.client.jme.artimport.ModelLoaderFactory;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.annotation.Plugin;

/**
 * LoaderFactory for the JmeColladaLoader.
 * 
 * @author paulby
 */
@Plugin
public class JmeColladaLoaderFactory extends ModelLoaderFactory
    implements ClientPlugin
{
    /** A BaseClientPlugin that delegates activate and deactivate to
     * the superclass
     */
    private BaseClientPlugin plugin;
    private ModelDndContentImporter importer;

    public void initialize(ServerSessionManager manager) {
        LoaderManager.getLoaderManager().registerLoader(this);
        this.importer = new ModelDndContentImporter(manager, new String[] {getFileExtension()});
        this.plugin = new BaseClientPlugin() {
            @Override
            protected void activate() {
                JmeColladaLoaderFactory.this.register();
            }

            @Override
            protected void deactivate() {
                JmeColladaLoaderFactory.this.unregister();
            }
        };

        plugin.initialize(manager);
    }

    public void cleanup() {
        LoaderManager.getLoaderManager().unregisterLoader(this);
        plugin.cleanup();
    }

    public void register() {
        LoaderManager.getLoaderManager().activateLoader(this);
        ContentImportManager cim = ContentImportManager.getContentImportManager();
        cim.registerContentImporter(importer);
    }

    public void unregister() {
        LoaderManager.getLoaderManager().deactivateLoader(this);
        ContentImportManager cim = ContentImportManager.getContentImportManager();
        cim.unregisterContentImporter(importer);
    }

    public String getFileExtension() {
        return "dae";
    }

    public ModelLoader getLoader() {
        return (ModelLoader) new JmeColladaLoader();
    }

    public String getLoaderClassname() {
        return JmeColladaLoader.class.getName();
    }
}
