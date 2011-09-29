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
package org.jdesktop.wonderland.client.jme.utils.graphics;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.TexCoords;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.export.InputCapsule;
import com.jme.util.export.JMEExporter;
import com.jme.util.export.JMEImporter;
import com.jme.util.export.OutputCapsule;
import java.io.IOException;
import java.nio.FloatBuffer;
import com.jme.util.geom.BufferUtils;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * A quad with a texture displayed on it.
 * 
 * @author deronj
 */

@ExperimentalAPI
public class TexturedQuad extends Quad {
    
    /** Version number for serializing */
    private static final long serialVersionUID = 1L;

    /** The texture which is displayed on the quad. */
    protected Texture texture;

    /** The width in of the quad, in local coordinates. */
    private float width;

    /** The height in of the quad, in local coordinates. */
    private float height;

    
    /**
     * Internal Only: Create a new instance of <code>TexturedQuad</code>. 
     * The width and height attributes must be supplied later by calling <code>initialize</code>. 
     *
     * @param texture The texture to display on the quad.
     */
    public TexturedQuad (Texture texture) {
	this(texture, "TempTexturedBox");
    }

    /**
     * Create a new instance of <code>TexturedQuad</code> object. The texture is displayed on the front face only.
     * The center and size vertice information must be supplied later. 
     * 
     * @param texture The texture to display on the quad.
     * @param name The name of the scene element. 
     */
    public TexturedQuad (Texture texture, String name) {
	super(name);
	this.texture = texture;
	initializeTexture();
    }

    /**
     * Create a new instance of <code>TexturedQuad</code> object given a width and height. The quad is centered
     * around its local origin.
     * 
     * @param texture The texture to display on the quad.
     * @param name The name of the scene element. 
     * @param width The width of the quad in local coordinates.
     * @param height The height of the quad in local coordinates.
     */
    public TexturedQuad (Texture texture, String name, float width, float height) {
	super(name, width, height);
	this.texture = texture;
	initializeTexture();
    }

    /**
     * Specify a new texture.
     */
    public void setTexture (Texture texture) {
	this.texture = texture;
	TextureState ts = (TextureState) getRenderState(RenderState.RS_TEXTURE);
	ts.setTexture(texture);
    }

    /**
     * Returns this quad's texture.
     */
    public Texture getTexture () {
	return texture;
    }

    /**
     * Returns this quad's TextureState.
     */
    public TextureState getTextureState () {
	return (TextureState) getRenderState(RenderState.RS_TEXTURE);
    }

    /**
     * Specify a new ambient-and-diffuse color of this quad.
     */
    public void setColor (ColorRGBA color) {
	MaterialState ms = (MaterialState) getRenderState(RenderState.RS_MATERIAL);
	if (ms == null) {
	    ms = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
	    setRenderState(ms);
	}
	ms.setAmbient(new ColorRGBA(color));
	ms.setDiffuse(new ColorRGBA(color));
    }

    /**
     * Returns the ambient-and-diffuse color of this quad. 
     */
    public ColorRGBA getColor () {
	MaterialState ms = (MaterialState) getRenderState(RenderState.RS_MATERIAL);
	if (ms == null) {
	    return new ColorRGBA(1f, 1f, 1f, 1f);
	} else {
	    return ms.getDiffuse();
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
     * for the new size specified via this <code>initialize</code> call.
     */
    @Override
    public void initialize (float width, float height) {
	super.initialize(width, height);
	initializeTexture();
	this.width = width;
	this.height = height;
        updateModelBound();
    }

    /**
     * Initialize texture attributes.
     */
    private void initializeTexture () {

	if (getRenderState(RenderState.RS_TEXTURE) == null) {
	    TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
	    ts.setTexture(texture);
	    setRenderState(ts);
	    setModelBound(new BoundingBox());
	    updateModelBound();
	}

        FloatBuffer tbuf = BufferUtils.createVector2Buffer(4);
        setTextureCoords(new TexCoords(tbuf));
	tbuf.put(0).put(0);
	tbuf.put(0).put(1);
	tbuf.put(1).put(1);
	tbuf.put(1).put(0);
    }

    /**
     * Creates a new TexturedQuad object containing the same data as this one.
     * 
     * @return The new TexturedQuad.
     */
    public Object clone() {
	TexturedQuad rVal = new TexturedQuad(texture, getName() + "_clone", width, height);
	return rVal;
    }

    public void write(JMEExporter e) throws IOException {
        super.write(e);
        OutputCapsule capsule = e.getCapsule(this);
	// TODO: texture attrs
    }

    public void read(JMEImporter e) throws IOException {
        super.read(e);
        InputCapsule capsule = e.getCapsule(this);
	// TODO: texture attrs
    }

    /**
     * For debug: Print the contents of this object's render state.
     */
    public void printRenderState () {
	MaterialState ms = (MaterialState) getRenderState(RenderState.RS_MATERIAL);
	GraphicsUtils.printRenderState(ms);
	TextureState ts = (TextureState) getRenderState(RenderState.RS_TEXTURE);
	GraphicsUtils.printRenderState(ts);
    }    

    /**
     * For debug: Print the contents of this object's geometry
     */
    public void printGeometry () {
	GraphicsUtils.printGeometry(this);
    }    
}
