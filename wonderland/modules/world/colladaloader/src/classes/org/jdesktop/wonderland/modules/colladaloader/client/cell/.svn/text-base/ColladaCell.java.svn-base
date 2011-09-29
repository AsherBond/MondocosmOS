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
package org.jdesktop.wonderland.modules.colladaloader.client.cell;

import org.jdesktop.wonderland.modules.colladaloader.client.jme.cellrenderer.ColladaRenderer;
import org.jdesktop.wonderland.modules.colladaloader.common.cell.state.ColladaCellClientState;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import org.jdesktop.wonderland.client.cell.*;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.state.CellClientState;

/**
 * Client side cell for rendering JME content
 * 
 * @author paulby
 */
public class ColladaCell extends Cell {
    
    /* The URI of the model asset */
    private String modelURI = null;
    private Vector3f geometryTranslation;
    private Quaternion geometryRotation;
    
    public ColladaCell(CellID cellID, CellCache cellCache) {
        super(cellID, cellCache);
    }
    
    /**
     * Called when the cell is initially created and any time there is a 
     * major configuration change. The cell will already be attached to it's parent
     * before the initial call of this method
     * 
     * @param config the cell config object
     */
    @Override
    public void setClientState(CellClientState config) {
        System.err.println("***** CONFIGURE");
        super.setClientState(config);
        ColladaCellClientState colladaConfig = (ColladaCellClientState) config;
        this.modelURI = colladaConfig.getModelURI();
        this.geometryRotation = colladaConfig.getGeometryRotation();
        this.geometryTranslation = colladaConfig.getGeometryTranslation();
        logger.warning("[CELL] COLLADA CELL " + this.modelURI);
    }
    
    @Override
    protected CellRenderer createCellRenderer(RendererType rendererType) {
        CellRenderer ret = null;
        switch(rendererType) {
            case RENDERER_2D :
                // No 2D Renderer yet
                break;
            case RENDERER_JME :
                ret= new ColladaRenderer(this);
                break;                
        }
        
        return ret;
    }
    
    /**
     * Returns the URI of the model asset.
     * 
     * TODO shouldn't this be a URL instead of a String ?
     * 
     * @return The asset URI
     */
    public String getModelURI() {
        return this.modelURI;
    }

    public Vector3f getGeometryTranslation() {
        return geometryTranslation;
    }

    public Quaternion getGeometryRotation() {
        return geometryRotation;
    }
}
