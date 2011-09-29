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
package org.jdesktop.wonderland.client.jme.cellrenderer;

import com.jme.bounding.BoundingSphere;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Teapot;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * Renderer for Avatar, looks strangely like a teapot at the moment...
 * 
 * @author paulby
 */
@ExperimentalAPI
public class AvatarJME extends BasicRenderer {

    public AvatarJME(Cell cell) {
        super(cell);
    }

    @Override
    protected Node createSceneGraph(Entity entity) {
        ColorRGBA color = new ColorRGBA();
        
        color.r = 0.0f; color.g = 0.0f; color.b = 1.0f; color.a = 1.0f;
        Node ret = createTeapotEntity(cell.getCellID().toString(), color);        

        return ret;
    }

    public Node createTeapotEntity(String name, 
            ColorRGBA color) {
        MaterialState matState = null;
        
        // The center teapot
        Node ret = new Node();
        Teapot teapot = new Teapot();
        teapot.resetData();
        teapot.setLocalScale(0.2f);
        ret.attachChild(teapot);

        matState = (MaterialState) ClientContextJME.getWorldManager().getRenderManager().createRendererState(RenderState.RS_MATERIAL);
        matState.setDiffuse(color);
        ret.setRenderState(matState);

        ret.setModelBound(new BoundingSphere());
        ret.updateModelBound();

        return ret;
    }

}
