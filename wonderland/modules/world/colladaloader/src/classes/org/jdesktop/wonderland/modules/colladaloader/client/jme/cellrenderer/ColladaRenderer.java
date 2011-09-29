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
package org.jdesktop.wonderland.modules.colladaloader.client.jme.cellrenderer;

import com.jme.bounding.BoundingBox;
import org.jdesktop.wonderland.modules.colladaloader.client.cell.*;
import org.jdesktop.wonderland.client.jme.cellrenderer.*;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
//import imi.environments.ColladaEnvironment;
import java.net.URL;
import java.util.logging.Level;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.utils.traverser.ProcessNodeInterface;
import org.jdesktop.wonderland.client.jme.utils.traverser.TreeScan;
import org.jdesktop.wonderland.common.cell.CellTransform;

/**
 * A cell renderer that uses the IMI Collada loader
 * 
 * @author paulby
 */
public class ColladaRenderer extends BasicRenderer {

    public ColladaRenderer(Cell cell) {
        super(cell);
    }
    
    @Override
    protected Entity createEntity() {
        throw new RuntimeException("IMI Colalda loader not used at the moment");
//        ColladaEnvironment environment=null;
//
//        /* Fetch the basic info about the cell */
//        CellTransform transform = cell.getLocalTransform();
//        Vector3f translation = transform.getTranslation(null);
//        Vector3f scaling = transform.getScaling(null);
//        Quaternion rotation = transform.getRotation(null);
//
//        try {
//            URL modelLocation = getAssetURL(((ColladaCell)cell).getModelURI());
//            logger.warning("****** URL: " + modelLocation.toExternalForm());
//
//            WorldManager worldManager = ClientContextJME.getWorldManager();
//
//            // TODO this has the side effect of creating and adding entities to the WorldManager
//            environment = new ColladaEnvironment(worldManager, modelLocation, this.getClass().getName()+"_"+cell.getCellID());
//            worldManager.addUserData(ColladaEnvironment.class, environment);
//
//            rootNode = environment.getJMENode();
//
//            applyTransform(rootNode, cell.getWorldTransform());
//            addRenderState(rootNode);
//
//            addDefaultComponents(environment, rootNode);
//
//            // Make sure all the geometry has model bounds
//            TreeScan.findNode(rootNode, Geometry.class, new ProcessNodeInterface() {
//
//                public boolean processNode(Spatial node) {
//                    Geometry g = (Geometry)node;
//                    if (g.getModelBound()==null) {
//                        g.setModelBound(new BoundingBox());
//                        g.updateModelBound();
//                    }
//
//                    return true;
//                }
//
//            }, false, true);
//
//            logger.warning("ColladaREnderer not applying geometry offsets yet....");
//            // Adjust model origin wrt to cell
////            if (((ColladaCell)cell).getGeometryTranslation()!=null)
////                rootNode.setLocalTranslation(((ColladaCell)cell).getGeometryTranslation());
////            if (((ColladaCell)cell).getGeometryRotation()!=null)
////                rootNode.setLocalRotation(((ColladaCell)cell).getGeometryRotation());
//        } catch (Exception e) {
//            logger.log(Level.SEVERE, "Error loading Collada file "+((ColladaCell)cell).getModelURI(), e);
//        }
//
//        return environment;
    }

    @Override
    protected Node createSceneGraph(Entity entity) {
        // Never called as we overload createEntity
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
