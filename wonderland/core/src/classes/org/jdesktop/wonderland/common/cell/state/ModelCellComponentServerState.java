/**
 * Open Wonderland
 *
 * Copyright (c) 2011, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.common.cell.state;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.jdesktop.wonderland.common.cell.state.annotation.ServerState;

/**
 * The ColladaCellSetup class is the cell that renders a collada model cell in
 * world.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 * @author paulby
 */
@XmlRootElement(name="model-cell-component")
@ServerState
public class ModelCellComponentServerState extends CellComponentServerState implements Serializable {
    public enum TransparencyMode { DEFAULT, INVERSE, NONE };

    /* The URI of the static deployedModelURL file */
    @XmlElement(name="deployedModelURL")
    public String deployedModelURL = null;

    @XmlElement(name="PickingEnabled")
    private boolean pickable = true;

    @XmlElement(name="CollisionEnabled")
    private boolean collidable = true;

    @XmlElement(name="LightingEnabled")
    private boolean lightingEnabled = true;

    @XmlElement(name="BackfaceCullingEnabled")
    private boolean backfaceCullingEnabled = true;

    @XmlElement(name="GraphOptimizationEnabled")
    private boolean graphOptimizationEnabled = true;

    @XmlElement(name="TransparencyMode")
    private TransparencyMode transparencyMode = TransparencyMode.DEFAULT;

    /** Default constructor */
    public ModelCellComponentServerState() {
    }



    @Override
    @XmlTransient public String getServerComponentClassName() {
        return "org.jdesktop.wonderland.server.cell.ModelCellComponentMO";
    }

    public CellComponentServerState clone(CellComponentServerState state) {
        ModelCellComponentServerState ret = (ModelCellComponentServerState) state;
        if (ret == null)
            ret = new ModelCellComponentServerState();

        ret.deployedModelURL = this.deployedModelURL;
        ret.pickable = this.pickable;
        ret.lightingEnabled = this.lightingEnabled;
        ret.backfaceCullingEnabled = this.backfaceCullingEnabled;
        ret.graphOptimizationEnabled = this.graphOptimizationEnabled;
        ret.transparencyMode = this.transparencyMode;

        return ret;
    }

    public CellComponentClientState setClientState(ModelCellComponentClientState state) {
        state.setDeployedModelURL(deployedModelURL);
        state.setPickingEnabled(pickable);
        state.setLightingEnabled(lightingEnabled);
        state.setBackfaceCullingEnabled(backfaceCullingEnabled);
        state.setGraphOptimizationEnabled(graphOptimizationEnabled);
        state.setTransparencyMode(transparencyMode);

        return state;
    }

    /**
     * Returns the deployedModelURL URI, this is the URI for the .dep file.
     *
     * @return The deployedModelURL URI specification
     */
    @XmlTransient public String getDeployedModelURL() {
        return this.deployedModelURL;
    }

    /**
     * Sets the deployedModelURL URI. If null, then this property will not be written
     * out to the file.
     *
     * @param deployedModelURL The deployedModelURL URI
     */
    public void setDeployedModelURL(String deployedModelURL) {
        this.deployedModelURL = deployedModelURL;
    }

    /**
     * @return the pickable
     */
    @XmlTransient public boolean isPickingEnabled() {
        return pickable;
    }

    /**
     * @param pickable the pickable to set
     */
    public void setPickingEnable(boolean pickable) {
        this.pickable = pickable;
    }

    /**
     * @deprecated Use InteractionComponent instead
     * @return the collidable
     */
    @XmlTransient public boolean isCollisionEnabled() {
        return collidable;
    }

    /**
     * @deprecated Use InteractionComponent instead
     * @param collidable the collidable to set
     */
    public void setCollisionEnabled(boolean collidable) {
        this.collidable = collidable;
    }

    /**
     * @return the lightingEnabled
     */
    @XmlTransient public boolean isLightingEnabled() {
        return lightingEnabled;
    }

    /**
     * @param lightingEnabled the lightingEnabled to set
     */
    public void setLightingEnabled(boolean lightingEnabled) {
        this.lightingEnabled = lightingEnabled;
    }

    @XmlTransient public boolean isBackfactCullingEnabled() {
        return backfaceCullingEnabled;
    }

    public void setBackfaceCullingEnabled(boolean backfaceCullingEnabled) {
        this.backfaceCullingEnabled = backfaceCullingEnabled;
    }

    @XmlTransient public boolean isGraphOptimizationEnabled() {
        return graphOptimizationEnabled;
    }

    public void setGraphOptimizationEnabled(boolean graphOptimizationEnabled) {
        this.graphOptimizationEnabled = graphOptimizationEnabled;
    }

    @XmlTransient public TransparencyMode getTransparencyMode() {
        return transparencyMode;
    }

    public void setTransparencyMode(TransparencyMode transparencyMode) {
        this.transparencyMode = transparencyMode;
    }
}
