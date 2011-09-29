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

import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Spatial;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.mtgame.RenderUpdater;
import org.jdesktop.wonderland.modules.appbase.client.view.Gui2D;

/**
 * One of the sides of the frame.
 *
 * @author deronj
 */
@ExperimentalAPI
public class FrameSide extends FrameComponent {

    private static final Logger logger = Logger.getLogger(FrameSide.class.getName());

    /** The supported sides */
    public enum Side { TOP, LEFT, RIGHT, BOTTOM };
    /** Which side of the frame is this? */
    protected Side whichSide;
    /** The geometry of the side. */
    protected FrameRect rect;
    /** The x coordinate (relative to the view center) of the  upper left corner of the side */
    protected float x;
    /** The y coordinate (relative to the view center) of the upper left corner of the side */
    protected float y;
    /** The width of the side in world coordinates */
    protected float width;
    /** The height of the side in world coordinates */
    protected float height;

    /** 
     * Create a new instance of FrameSide with a default name.
     *
     * @param view The view the frame encloses.
     * @param whichSide The side of the frame.
     * @param gui The event controller of this component.
     */
    public FrameSide(View2DCell view, Side whichSide, Gui2D gui) {
        this("FrameSide " + whichSide, view, whichSide, gui);
        width = 1;
        height = 1;
    }

    /** 
     * Create a new instance of FrameSide.
     *
     * @param name The name of the node.
     * @param view The view the frame encloses.
     * @param whichSide The side of the frame.
     */
    public FrameSide(String name, View2DCell view, Side whichSide, Gui2D gui) {
        super(name, view, gui);
        this.whichSide = whichSide;
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        super.cleanup();
        if (rect != null) {
            rect.cleanup();
            rect = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() throws InstantiationException {
        updateLayout();
        updateCommon();
    }

    public void update(float newWidth3D, float newHeight3D) throws InstantiationException {
        updateLayout(newWidth3D, newHeight3D);
        updateCommon();
    }

    private void updateCommon () throws InstantiationException {
        rect.resize(width, height);

        // For some reason, this needs to be a synchronous (waiting) update
        ClientContextJME.getWorldManager().addRenderUpdater(new RenderUpdater() {
            public void update(Object arg0) {
                if (localToCellNode != null) {
                    localToCellNode.setLocalTranslation(new Vector3f(x, y, 0f));
                    ClientContextJME.getWorldManager().addToUpdateList(localToCellNode);
                }
            }
        }, null, true); 

        super.update();
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
     * Calculate the desired layout, based on the view size.
     */
    protected void updateLayout() {
        updateLayout(view.getDisplayerLocalWidth(), view.getDisplayerLocalHeight());
    }

    protected void updateLayout(float newWidth3D, float newHeight3D) {

        // Note: we are moving the side CENTERS in this routine. This is slightly
        // different from the old code.

        float innerWidth = newWidth3D;
        float innerHeight = newHeight3D;
        float sideThickness = Frame2DCell.SIDE_THICKNESS;

        switch (whichSide) {

            case TOP:
                x = 0;
                y = innerHeight / 2f + Frame2DCell.HEADER_HEIGHT / 2f;
                width = innerWidth + 2f * sideThickness;
                height = Frame2DCell.HEADER_HEIGHT;
                break;

            case LEFT:
                logger.fine("innerWidth = " + innerWidth);
                logger.fine("innerHeight = " + innerHeight);
                logger.fine("sideThickness = " + sideThickness);
                x = -innerWidth / 2f - sideThickness / 2f;
                y = 0;
                width = sideThickness;
                height = innerHeight;
                logger.fine("x = " + x);
                logger.fine("y = " + y);
                logger.fine("w = " + width);
                logger.fine("h = " + height);
                break;

            case RIGHT:
                x = innerWidth / 2f + sideThickness / 2f;
                y = Frame2DCell.RESIZE_CORNER_HEIGHT / 2f;
                width = sideThickness;
                height = innerHeight - Frame2DCell.RESIZE_CORNER_HEIGHT;
                break;

            case BOTTOM:
                logger.fine("Frame2DCell.RESIZE_CORNER_WIDTH = " + Frame2DCell.RESIZE_CORNER_WIDTH);
                logger.fine("sideThickness = " + sideThickness);
                logger.fine("innerHeight = " + innerHeight);
                logger.fine("innerWidth = " + innerWidth);
                x = -Frame2DCell.RESIZE_CORNER_WIDTH / 2f - sideThickness / 2f;
                y = -innerHeight / 2f - sideThickness / 2f;
                width = innerWidth + sideThickness - Frame2DCell.RESIZE_CORNER_WIDTH;
                height = sideThickness;
                logger.fine("x = " + x);
                logger.fine("y = " + y);
                logger.fine("width = " + width);
                logger.fine("height = " + height);
                break;
        }
    }

    /**
     * Returns the x location of this component.
     */
    public float getX() {
        return x;
    }

    /**
     * Returns the y location of this component.
     */
    public float getY() {
        return y;
    }

    /**
     * Returns the width of this component in local coordinates. 
     */
    public float getWidth() {
        return rect.width;
    }

    /**
     * Returns the height of this component in local coordinates. 
     */
    public float getHeight() {
        return rect.height;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Spatial[] getSpatials() {
        if (rect == null) {
            rect = new FrameRect("FrameRect for " + name, view, gui, width, height);
        }
        return rect.getSpatials();
    }
}
