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
package org.jdesktop.wonderland.modules.colladaloader.server.cell;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import org.jdesktop.wonderland.common.AssetURI;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.state.CellClientState;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.modules.colladaloader.common.cell.state.ColladaCellClientState;
import org.jdesktop.wonderland.modules.colladaloader.common.cell.state.ColladaCellServerState;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;


/**
 * A cell for collada models
 * @author paulby
 */
@ExperimentalAPI
public class ColladaCellMO extends CellMO { 
    
    /* The unique model URI */
    private AssetURI modelURI = null;
    private Vector3f geometryTranslation;
    private Quaternion geometryRotation;
    	
    /** Default constructor, used when cell is created via WFS */
    public ColladaCellMO() {
    }
    
    public ColladaCellMO(Vector3f center, float size, AssetURI modelURI, Vector3f geometryTranslation, Quaternion geometryRotation) {
        super(new BoundingBox(new Vector3f(), size, size, size), new CellTransform(null, center));
        this.modelURI = modelURI;
        this.geometryRotation = geometryRotation;
        this.geometryTranslation = geometryTranslation;
    }

    public ColladaCellMO(Vector3f center, float size, AssetURI modelURI) {
        this(center, size,  modelURI, null, null);
    }
    
    public ColladaCellMO(BoundingVolume bounds, CellTransform transform) {
        super(bounds, transform);
    }
    
    @Override protected String getClientCellClassName(WonderlandClientID clientSession, ClientCapabilities capabilities) {
        return "org.jdesktop.wonderland.modules.colladaloader.client.cell.ColladaCell";
    }

    @Override
    public CellClientState getClientState(CellClientState state, WonderlandClientID clientID, ClientCapabilities capabilities) {
        if (state != null) {
            logger.severe("ColladaCellMO does not support being overloaded in this version");
        }
        CellClientState ret = new ColladaCellClientState(this.modelURI.toExternalForm(), geometryTranslation, geometryRotation);
        super.getClientState(ret, clientID, capabilities);
        return ret;
    }

    @Override
    public void setServerState(CellServerState setup) {
        super.setServerState(setup);
        this.modelURI = ((ColladaCellServerState)setup).getModel();

        System.err.println("******** GOT STATE "+this.modelURI);
    }


}
