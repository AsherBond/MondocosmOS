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
package org.jdesktop.wonderland.modules.appbase.client.view;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.scene.TexCoords;
import com.jme.scene.state.TextureState;
import com.jme.util.geom.BufferUtils;
import java.nio.FloatBuffer;
import org.jdesktop.wonderland.client.jme.utils.graphics.TexturedQuad;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * A quad shaped geometry node.
 *
 * @author deronj
 */
@ExperimentalAPI
class GeometryNodeQuad extends GeometryNode {

    private static final Logger logger = Logger.getLogger(GeometryNodeQuad.class.getName());

    /** The actual geometry */
    private TexturedQuad quad;

    /**
     * Create a new instance of textured geometry. The dimensions
     * are derived from the view dimensions.
     *
     * @param view The view object the geometry is to display.
     */
    GeometryNodeQuad (View2D view) {
        super(view);

        quad = new TexturedQuad(null, "TexturedQuad for Geometry Node of " + view.getName());
        quad.setModelBound(new BoundingBox());
        attachChild(quad);

        // Arbitrary; will be later changed
        setSize(1f, 1f);
        setTexCoords(1f, 1f);

        quad.updateModelBound();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        super.cleanup();
        if (quad != null) {
            detachChild(quad);
            quad = null;
        }
    }

    /** 
     * {@inheritDoc} 
     * <br><br>
     * Note: this method resets the texture attributes of the quad to display the entire
     * contents of the texture image on the quad. It undoes any previous calls to 
     * <code>setTextureCoords</code> for this quad. If you have previously called 
     * <code>setTextureCoords</code> to display a subset of the image, you should 
     * call <code>setTextureCoords</code> again to set the appropriate texture coordinates
     * for the new size specified via this <code>setSize</code> call.
     */
    public void setSize (float width, float height) {
        quad.initialize(width, height);
    }

    /** {@inheritDoc} */
    @Override
    public void setTexture(Texture2D texture) {
        super.setTexture(texture);
        quad.setTexture(texture);
        logger.fine("Last texture assigned to quad = " + texture);
    }

    /** {@inheritDoc} */
    public void setTexCoords(float widthRatio, float heightRatio) {
        FloatBuffer tbuf = BufferUtils.createVector2Buffer(4);
        tbuf.put(0f).put(0);
        tbuf.put(0f).put(heightRatio);
        tbuf.put(widthRatio).put(heightRatio);
        tbuf.put(widthRatio).put(0);
        quad.setTextureCoords(new TexCoords(tbuf));
    }

    /** {@inheritDoc} */
    public Texture getTexture() {
        return quad.getTexture();
    }

    /** {@inheritDoc} */
    public TextureState getTextureState() {
        return quad.getTextureState();
    }

    /** {@inheritDoc} */
    public void setOrthoZOrder (int zOrder) {
        quad.setZOrder(zOrder);
    }

    /** {@inheritDoc} */
    public int getOrthoZOrder() {
        return quad.getZOrder();
    }
}
