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
package org.jdesktop.wonderland.client.jme.artimport;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.ModelCellComponent;
import org.jdesktop.wonderland.common.cell.state.ModelCellServerState;

/**
 * Interface for Model loader code. Provides support for inital import of the 
 * model from its original art files, deployment of the assets into a module 
 * and runtime load of the assets from the module
 * 
 * @author paulby
 */
public interface ModelLoader {

    /**
     * Import the model from it's original source
     * 
     * @param file
     * @return
     */
    public ImportedModel importModel(ImportSettings settings) throws IOException;
    
    /**
     * Given a previously deploy model, load the model.
     *
     * @param model the model to load
     * @param rootEntity the entity to which the returned node will be attached.
     */
    public Node loadDeployedModel(DeployedModel model, Entity rootEntity);

    /**
     * Deploy the art content to the module.
     * @param rootDir the art root directory of the module (usually <module>/art)
     */
    public DeployedModel deployToModule(File moduleRootDir, ImportedModel importedModel) throws IOException;

    /**
     * Return a cell server state object that will cause the appropriate cell
     * for the model to be create on the server.
     *
     * @param deployedURL the url of the deployed art file
     * @param modelTranslation the translation of the model, relative to the cell
     * @param modelRotation the rotation of the model, relative to the cell
     * @param modelScale the scale of the model, relative to the cell
     * @param properties set of loader specific properties, may be null
     *
     * @return a cell server state object
     */
    public ModelCellServerState getCellServerState(String deployedURL, 
            Vector3f modelTranslation,
            Quaternion modelRotation,
            Vector3f modelScale,
            Map<String, Object> properties);
    
    /**
     * Add a model loader component for the specified deployed model to the cell. Use this method if
     * you have your own cell, but you would like the loader system to manage
     * the loading of model. You will also need to add a ModelCellRenderer to your cell.
     * 
     * @param deployedURL the url of the deployed art file
     * @param modelTranslation the translation of the model, relative to the cell
     * @param modelRotation the rotation of the model, relative to the cell
     * @param modelScale the scale of the model, relative to the cell
     * @param properties set of loader specific properties, may be null
     * @return
     */
//    public ModelCellComponentServerState getModeCellComponentServerState(String deployedURL,
//            Vector3f modelTranslation,
//            Quaternion modelRotation,
//            Vector3f modelScale,
//            Map<String, Object> properties);
}
