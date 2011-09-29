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

import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Spatial;
import com.jme.util.TextureManager;
import java.net.URL;
import java.util.LinkedList;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * The close button of the frame which is on the header.
 *
 * @author deronj
 */
@ExperimentalAPI
public class FrameCloseButton extends FrameComponent {

    private static final Logger logger = Logger.getLogger(FrameCloseButton.class.getName());
    /** The color the component has when the mouse is inside it */
    protected static final ColorRGBA MOUSE_INSIDE_COLOR = new ColorRGBA(1.0f, 1.0f, 0f, 1.0f);
    /** The distance between this component and the underlying frame header */
    private float Z_OFFSET = 0.001f;
    /** The height of the close button */
    private static final float CLOSE_BUTTON_HEIGHT = Frame2DCell.HEADER_HEIGHT * 0.9f;
    /** The width of the close button */
    private static final float CLOSE_BUTTON_WIDTH = CLOSE_BUTTON_HEIGHT;
    /** The name of the image resource which contains the "X" */
    private String IMAGE_RESOURCE_NAME = "resources/client/app/base/gui/guidefault/window-close.png";
    /** The listeners to be notified when the close button is pressed */
    protected LinkedList<Frame2DCell.CloseListener> closeListeners;
    /** The transparent textured rectangle containing the "X" image */
    protected FrameTexRect rect;
    /** The texture of the "X" */
    protected Texture texture;
    /** The x coordinate in view coordinates */
    protected float x;
    /** The y coordinate in view coordinates */
    protected float y;
    /** Whether the mouse pointer is inside this component */
    protected boolean mouseInside;

    /** 
     * Create a new instance of FrameCloseButton.
     *
     * @param view The view the frame encloses.
     * @param closeListeners The listeners to be notified when the header's close button is pressed.
     */
    public FrameCloseButton(View2DCell view, LinkedList<Frame2DCell.CloseListener> closeListeners) {
        super("FrameCloseButton for " + view.getName(), view, new Gui2DCloseButton(view));
        ((Gui2DCloseButton) gui).setComponent(this);
        this.closeListeners = closeListeners;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        if (closeListeners != null) {
            closeListeners.clear();
            closeListeners = null;
        }
        if (rect != null) {
            rect.cleanup();
            rect = null;
        }
        super.cleanup();
    }

    /**
     * Returns the listeners to be notified when the header's close button is pressed.
     */
    public LinkedList<Frame2DCell.CloseListener> getCloseListeners() {
        return closeListeners;
    }

    /**
     * Specify whether the mouse pointer is inside the close button.
     *
     * @param inside True if the mouse pointer is inside.
     */
    public void setMouseInside(boolean inside) {
        if (mouseInside == inside) {
            return;
        }
        mouseInside = inside;

        if (mouseInside) {
            setColor(MOUSE_INSIDE_COLOR);
        } else {
            // When mouse is outside make this component the same color as the underlying component
            if (controlArb.hasControl()) {
                setColor(HAS_CONTROL_COLOR);
            } else {
                setColor(NO_CONTROL_COLOR);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() throws InstantiationException {
        updateLayout();

        if (texture == null) {
            createTexture();
        }

        updateLayout();

        // Update position relative to view
        localToCellNode.setLocalTranslation(new Vector3f(x, y, Z_OFFSET));

        super.update();

    /* For debug
    printRenderState();
    printGeometry();
     */
    }

    /**
     * Layout the rectangle of the close button.
     * Note: this has package scope because it is invoked by FrameLabelControler.updateLayout()
     */
    protected void updateLayout() {
        x = (view.getDisplayerLocalWidth() - CLOSE_BUTTON_WIDTH) / 2f + Frame2DCell.SIDE_THICKNESS;
        y = 0;
    }

    /** Create the texture */
    protected void createTexture() throws InstantiationException {
        URL buttonImageUrl = this.getClass().getClassLoader().getResource(IMAGE_RESOURCE_NAME);
        if (buttonImageUrl == null) {
            throw new InstantiationException("Cannot find resource: " + IMAGE_RESOURCE_NAME);
        }

        texture = TextureManager.loadTexture(buttonImageUrl, Texture.MinificationFilter.Trilinear,
                Texture.MagnificationFilter.Bilinear);
        if (texture == null) {
            //throw new InstantiationException("Allocation of image resource failed: " + buttonImageUrl);
            throw new InstantiationException("Allocation of image resource failed");
        }

        texture.setApply(Texture.ApplyMode.Decal);
    }

    /**
     * Returns the X coordinate of the button.
     */
    public float getX() {
        return x;
    }

    /**
     * Returns the y coordinate of the button.
     */
    public float getY() {
        return y;
    }

    /**
     * {@inheritDoc}
     */
    public void setColor(ColorRGBA color) {
        if (rect != null) {
            rect.setColor(color);
        }
    }

    /**
     * {@inheritDoc}
     */
    public ColorRGBA getColor() {
        if (rect != null) {
            return rect.getColor();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setForegroundColor(ColorRGBA color) {
        if (rect != null) {
            rect.setForegroundColor(color);
        }
    }

    /**
     * {@inheritDoc}
     */
    public ColorRGBA getForegroundColor() {
        if (rect != null) {
            return rect.getForegroundColor();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Spatial[] getSpatials() {
        if (rect == null) {
            rect = new FrameTexRect(view, gui, texture, CLOSE_BUTTON_WIDTH, CLOSE_BUTTON_HEIGHT);
        }
        return rect.getSpatials();
    }
}

