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

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.ConfigInstance;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.mtgame.WorldManager.ConfigLoadListener;
import org.jdesktop.wonderland.client.cell.asset.AssetUtils;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.common.cell.state.ModelCellServerState;
import org.jdesktop.wonderland.modules.jmecolladaloader.client.JmeColladaLoader;
import org.jdesktop.wonderland.client.jme.artimport.DeployedModel;
import org.jdesktop.wonderland.client.jme.artimport.ImportSettings;
import org.jdesktop.wonderland.client.jme.artimport.ImportedModel;
import org.jdesktop.wonderland.client.jme.artimport.ModelLoader;

/**
 *
 * Loader for SketchUp .kmz files
 * 
 * @author paulby
 */
class MtgLoader implements ModelLoader {

    private static final Logger logger = Logger.getLogger(MtgLoader.class.getName());
        
    /**
     * Load a SketchUP KMZ file and return the ImportedModel object
     * @param file
     * @return
     */
    @Override
    public ImportedModel importModel(ImportSettings settings) throws IOException {
        throw new RuntimeException("Not Implemented");
//        URL modelURL = settings.getModelURL();
//        ImportedModel importedModel=new ImportedModel(modelURL, null);
//        load(modelURL);
//
//        importedModel.setModelLoader(this);
//        importedModel.setImportSettings(settings);
//        importedModel.setModelBG(new Node("Fake"));
//
//        return importedModel;
    }
    
    public Node loadDeployedModel(DeployedModel model, Entity rootEntity) {
        String baseURL=null;
        Node ret = null;
        try {
            baseURL = AssetUtils.getAssetURL(model.getModelURL().substring(0, model.getModelURL().lastIndexOf('/')+1)).toExternalForm();
        } catch (MalformedURLException ex) {
            Logger.getLogger(MtgLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        ClientContextJME.getWorldManager().setConfigBaseURL(baseURL.substring(0,baseURL.length()-1));
        try {
            ret = load(AssetUtils.getAssetURL(model.getModelURL()), rootEntity);

        } catch (IOException ex) {
            Logger.getLogger(JmeColladaLoader.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    private Node load(URL url, Entity rootEntity) {
        Node rootNode = new Node("mtg-root");

        if (ClientContextJME.getWorldManager().getRenderManager().supportsOpenGL20() == false) {
            logger.warning("MTG files require OpenGL 2.0");
            return (rootNode);
        }
        
        rootEntity.addComponent(RenderComponent.class, ClientContextJME.getWorldManager().getRenderManager().createRenderComponent(rootNode));
        ClientContextJME.getWorldManager().loadConfiguration(url, new ConfigLoadListener() {

            public void configLoaded(ConfigInstance ci) {
//                System.out.println("Loaded: " + ci.getEntity());
            }
        });

        ConfigInstance ci[] = ClientContextJME.getWorldManager().getAllConfigInstances();
        for (int i=0; i<ci.length; i++) {
            RenderComponent rc = ci[i].getEntity().getComponent(RenderComponent.class);
            if (rc!=null)
                rc.setAttachPoint(rootNode);
            rootEntity.addEntity(ci[i].getEntity());
        }
        return rootNode;
    }

    public DeployedModel deployToModule(File moduleRootDir, ImportedModel importedModel) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ModelCellServerState getCellServerState(String deployedURL, Vector3f modelTranslation, Quaternion modelRotation, Vector3f modelScale, Map<String, Object> properties) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
