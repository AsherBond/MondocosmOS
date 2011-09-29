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
package org.jdesktop.wonderland.modules.appbase.client.cell.view.viewdefault;

import com.jme.bounding.BoundingBox;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.system.DisplaySystem;
import java.util.logging.Logger;
import org.jdesktop.mtgame.RenderUpdater;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.view.Gui2D;

/**
 * A non-textured rectangle component. Used in several places in the frame.
 * The the origin (0, 0) of the the local coordinate space is the center of the rectangle.
 * The width and height of the rectangle are as specified.
 *
 * @author deronj
 */
@ExperimentalAPI
public class FrameRect extends FrameComponent {

    private static final Logger logger = Logger.getLogger(FrameRect.class.getName());
    /** The width of the side in local coordinates. */
    protected float width;
    /** The height of the side in local coordinates. */
    protected float height;
    /** The quad geometry for the rect. */
    protected Quad quad;
    /** The color of this rectangle. */
    protected ColorRGBA color;

    /** 
     * Create a new instance of <code>FrameRect</code> with a default name and with the specified size.
     *
     * @param view The view the frame encloses.
     * @param gui The event handler.
     * @param width The width of rectangle in local coordinates.
     * @param height The height of rectangle in local coordinates.
     */
    public FrameRect(View2DCell view, Gui2D gui, float width, float height) {
        this("FrameRect", view, gui, width, height);
    }

    /** 
     * Create a new instance of <code>FrameRect</code> with the specified size.
     *
     * @param name The name of the node.
     * @param view The view the frame encloses.
     * @param gui The event handler.
     * @param width The width of rectangle in local coordinates.
     * @param height The height of rectangle in local coordinates.
     */
    public FrameRect(String name, View2DCell view, Gui2D gui, float width, float height) {
        super(name, view, gui);
        try {
            resize(width, height);
        } catch (InstantiationException ex) {
            logger.warning("Cannot update FrameRect component");
        }

        // TODO: someday: eventually remove when world lights are working
        initLightState();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void cleanup() {
        super.cleanup();
        quad = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void update() throws InstantiationException {
        updateLayout();

        ClientContextJME.getWorldManager().addRenderUpdater(new RenderUpdater() {
            public void update(Object arg0) {
                if (quad != null) {
                    quad.resize(width, height);
                    quad.updateModelBound();
                    ClientContextJME.getWorldManager().addToUpdateList(localToCellNode);
                }
            }
        }, null);

        super.update();
    }

    /**
     * Returns the width of this component. 
     */
    public float getWidth() {
        return width;
    }

    /**
     * Returns the height of this component. 
     */
    public float getHeight() {
        return height;
    }

    // TODO: someday: temporary: until sync up with global light change
    /**
     * Initialize the light state.
     */
    protected void initLightState() {
        PointLight light = new PointLight();
        light.setDiffuse(new ColorRGBA(0.75f, 0.75f, 0.75f, 0.75f));
        light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        light.setLocation(new Vector3f(100, 100, 100));
        light.setEnabled(true);
        LightState lightState = (LightState) ClientContextJME.getWorldManager().getRenderManager().createRendererState(RenderState.RS_LIGHT);
        lightState.setEnabled(true);
        lightState.attach(light);
        quad.setRenderState(lightState);
    /*
    System.err.println(">>>>>>>>>>>>>> Initializing light state for component" + name);
    System.err.println("--------------------------------------------");
    GraphicsUtils.printCommonRenderStates(quad);
    System.err.println("--------------------------------------------");
     */
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void setColor(final ColorRGBA color) {
        if (quad != null) {
            ClientContextJME.getWorldManager().addRenderUpdater(new RenderUpdater() {
                public void update(Object arg0) {
                    if (quad != null) {
                        MaterialState ms = (MaterialState) quad.getRenderState(RenderState.RS_MATERIAL);
                        if (ms == null) {
                            ms = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
                            quad.setRenderState(ms);
                        }
                        ms.setAmbient(new ColorRGBA(color));
                        ms.setDiffuse(new ColorRGBA(color));
                        ClientContextJME.getWorldManager().addToUpdateList(localToCellNode);
                    }
                }
            }, null); 
        }
    }

    /**
     * {@inheritDoc}
     */
    public ColorRGBA getColor() {
        MaterialState ms = null;
        if (quad != null) {
            ms = (MaterialState) quad.getRenderState(RenderState.RS_MATERIAL);
        }
        if (ms == null) {
            return new ColorRGBA(1f, 1f, 1f, 1f);
        } else {
            return ms.getDiffuse();
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void setForegroundColor(final ColorRGBA color) {
    }

    /**
     * {@inheritDoc}
     */
    public ColorRGBA getForegroundColor() {
        return null;
    }

    /**
     * Change the size of this component.
     *
     * @param width The new width.
     * @param height The new height.
     */
    public synchronized void resize(float width, float height) throws InstantiationException {
        this.width = width;
        this.height = height;
        update();
    }

    /**
     * Calculate the geometry layout.
     */
    protected void updateLayout() {
        // Nothing to do for this class
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Spatial[] getSpatials() {
        if (quad == null) {
            quad = new Quad("FrameRect-Quad", width, height);
            quad.setModelBound(new BoundingBox());
            quad.updateModelBound();
        }
        return new Spatial[]{quad};
    }
}

