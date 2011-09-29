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
package org.jdesktop.wonderland.modules.jmecolladaloader.common.cell.state;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.common.cell.state.ModelCellComponentClientState;

/**
 * A cell component that encapsulates all the information required to load a
 * collada model.
 */
public class JmeColladaCellComponentClientState extends ModelCellComponentClientState {
    /* The unique URL that describes the model data */
    private String modelURI = null;
    private String modelGroupURI = null;
    private Vector3f modelTranslation;
    private Quaternion modelRotation;
    private Vector3f modelScale=null;
    private String modelLoaderClassname = null;
    
    /** Default constructor */
    public JmeColladaCellComponentClientState() {
    }
    
    /**
     * Returns the unique model URI, null if none.
     * 
     * @return The unique model URI
     */
    public String getModelURI() {
        return this.modelURI;
    }
    
    /**
     * Sets the unique model URI, null for none.
     * 
     * @param modelURI The unique model URI
     */
    public void setModelURI(String modelURI) {
        this.modelURI = modelURI;
    }

    public Vector3f getModelTranslation() {
        return modelTranslation;
    }

    public void setModelRotation(Quaternion modelRotation) {
        this.modelRotation = modelRotation;
    }

    public void setModelTranslation(Vector3f modelTranslation) {
        this.modelTranslation = modelTranslation;
    }

    public Quaternion getModelRotation() {
        return modelRotation;
    }

    public Vector3f getModelScale() {
        return modelScale;
    }

    public void setModelScale(Vector3f modelScale) {
        this.modelScale = modelScale;
    }

    /**
     * @return the modelSetURI
     */
    public String getModelGroupURI() {
        return modelGroupURI;
    }

    /**
     * @param modelSetURI the modelSetURI to set
     */
    public void setModelGroupURI(String modelSetURI) {
        this.modelGroupURI = modelSetURI;
    }

    /**
     * @return the modelLoaderClassname
     */
    public String getModelLoaderClassname() {
        return modelLoaderClassname;
    }

    /**
     * @param modelLoaderClassname the modelLoaderClassname to set
     */
    public void setModelLoaderClassname(String modelLoaderClassname) {
        this.modelLoaderClassname = modelLoaderClassname;
    }

}
