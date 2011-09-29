/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., All Rights Reserved
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
package org.jdesktop.wonderland.client.jme.artimport;

import com.jme.scene.Node;
import java.net.URL;
import java.util.Map;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderComponent;

/**
 *
 * @author paulby
 */
public class ImportedModel extends Model {

    private Map<URL, String> textureFiles; // Mapping between url in loaded file and actual location of file
    private ImportSettings importSettings = null;
    private ModelLoader modelLoader;  // Loader used
    private Node modelBG;  // Root of scene created by loader
    private Entity entity;    // Entity for the model
    private String deploymentBaseURL;
    private String deployedModelURL;

    public ImportedModel(URL originalFile, Map<URL, String> textureFileMapping) {
        super(originalFile);
        this.textureFiles = textureFileMapping;
    }

    /**
     * @return the textureFiles
     */
    public Map<URL, String> getTextureFiles() {
        return textureFiles;
    }


    /**
     * @return the importSettings
     */
    public ImportSettings getImportSettings() {
        return importSettings;
    }

    /**
     * @param importSettings the importSettings to set
     */
    public void setImportSettings(ImportSettings importSettings) {
        this.importSettings = importSettings;
    }
    /**
     * @return the modelLoader
     */
    public ModelLoader getModelLoader() {
        return modelLoader;
    }

    /**
     * @param modelLoader the modelLoader to set
     */
    public void setModelLoader(ModelLoader modelLoader) {
        this.modelLoader = modelLoader;
    }

    /**
     * @return the loadedRoot
     */
    public Node getModelBG() {
        return modelBG;
    }

    /**
     * Get the root node for this scene, this will be a parent (or grand parent) of modelBG
     * @return
     */
    public Node getRootBG() {
        return entity.getComponent(RenderComponent.class).getSceneRoot();
    }

    /**
     * @param loadedRoot the loadedRoot to set
     */
    public void setModelBG(Node loadedRoot) {
        this.modelBG = loadedRoot;
    }

    /**
     * @return the entity
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * @param entity the entity to set
     */
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    /**
     * @return the deploymentBaseURL
     */
    public String getDeploymentBaseURL() {
        return deploymentBaseURL;
    }

    /**
     * @param deploymentBaseURL the deploymentBaseURL to set
     */
    public void setDeploymentBaseURL(String deploymentBaseURL) {
        this.deploymentBaseURL = deploymentBaseURL;
    }

    /**
     * The URL of the dep file
     * 
     * @param string
     */
    public void setDeployedModelURL(String string) {
        this.deployedModelURL = string;
    }

    /**
     * The URL of the dep file
     */
    public String getDeployedModelURL() {
        return deployedModelURL;
    }

}
