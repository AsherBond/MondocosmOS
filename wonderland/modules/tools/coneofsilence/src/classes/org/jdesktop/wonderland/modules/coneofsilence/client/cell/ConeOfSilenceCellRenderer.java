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

package org.jdesktop.wonderland.modules.coneofsilence.client.cell;

import com.jme.scene.Node;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.client.cell.asset.AssetUtils;
import org.jdesktop.wonderland.client.jme.artimport.DeployedModel;
import org.jdesktop.wonderland.client.jme.artimport.LoaderManager;
import org.jdesktop.wonderland.client.jme.cellrenderer.BasicRenderer;

/**
 * @author jkaplan
 * @author Bernard Horan
 */
public class ConeOfSilenceCellRenderer extends BasicRenderer {
    
    public ConeOfSilenceCellRenderer(Cell cell) {
        super(cell);
    }
    
    protected Node createSceneGraph(Entity entity) {

        Node node = new Node();
        try {
            attachModel(node);
        } catch (IOException ex) {
            Logger.getLogger(ConeOfSilenceCellRenderer.class.getName()).log(Level.SEVERE, "Failed to load cone of silence", ex);
        }
        return node;
    }

    private void attachModel(Node aNode) throws IOException {
        LoaderManager manager = LoaderManager.getLoaderManager();
        URL url = AssetUtils.getAssetURL("wla://coneofsilence/pwl_3d_coneofsilence_016d.dae/pwl_3d_coneofsilence_016d.dae.gz.dep", this.getCell());
        DeployedModel dm = manager.getLoaderFromDeployment(url);
        Node cosModel = dm.getModelLoader().loadDeployedModel(dm, entity);
        aNode.attachChild(cosModel);
    }

}
