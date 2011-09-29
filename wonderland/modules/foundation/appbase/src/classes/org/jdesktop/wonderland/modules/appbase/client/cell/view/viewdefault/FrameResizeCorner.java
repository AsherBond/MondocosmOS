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
import com.jme.scene.Node;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.mtgame.RenderUpdater;
import org.jdesktop.wonderland.client.jme.ClientContextJME;

/**
 * The resize corner for Frame2DCell.
 *
 * @author deronj
 */
@ExperimentalAPI
public class FrameResizeCorner extends FrameComponent {

    private static final Logger logger = Logger.getLogger(FrameResizeCorner.class.getName());
    /** The width of this resize corner */
    protected float RESIZE_CORNER_WIDTH = Frame2DCell.RESIZE_CORNER_WIDTH;
    /** The height of this resize corner */
    protected float RESIZE_CORNER_HEIGHT = Frame2DCell.RESIZE_CORNER_HEIGHT;
    /** The distance of this component above its underlying component */
    protected float Z_OFFSET = 0;
    /** The color the component has when the mouse is inside it */
    protected static final ColorRGBA MOUSE_INSIDE_COLOR = new ColorRGBA(1.0f, 1.0f, 0f, 1.0f);
    /** The horizontal bar */
    protected FrameRect horizBar;
    /** The vertical bar */
    protected FrameRect vertBar;
    /** Whether the mouse pointer is inside this component */
    protected boolean mouseInside;
    /** The origin of the resize corner (in cell local coordinates) */
    protected Vector3f origin;
    /** The x position of the horizontal bar (relative to the origin) */
    protected float horizX;
    /** The y position of the horizontal bar (relative to the origin) */
    protected float horizY;
    /** The width of the horizontal bar */
    protected float horizWidth;
    /** The height of the horizontal bar */
    protected float horizHeight;
    /** The x position of the vertical bar (relative to the origin) */
    protected float vertX;
    /** The y position of the vertical bar (relative to the origin) */
    protected float vertY;
    /** The width of the vertical bar */
    protected float vertWidth;
    /** The height of the vertical bar */
    protected float vertHeight;
    /** The bordering right side frame component */
    private FrameSide rightSide;
    /** The bordering bottom side frame component */
    private FrameSide bottomSide;
    /** 
     * Whether the resize corner is enabled. When it is enabled, it responds to input events 
     * and highlights. 
     */
    private boolean enabled = false;

    /** 
     * Create a new instance of FrameResizeCorner.
     *
     * @param view The frame's view.
     */
    public FrameResizeCorner(View2DCell view, FrameSide rightSide, FrameSide bottomSide) {
        super("FrameResizeCorner", view, new Gui2DResizeCorner(view));
        this.rightSide = rightSide;
        this.bottomSide = bottomSide;
        ((Gui2DResizeCorner) gui).setComponent(this);
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        super.cleanup();
        if (horizBar != null) {
            horizBar.cleanup();
            horizBar = null;
        }
        if (vertBar != null) {
            vertBar.cleanup();
            vertBar = null;
        }
        rightSide = null;
        bottomSide = null;
        enabled = false;
    }

    /**
     * Specify whether this resize corner should be enabled (that is, react to input events).
     * Default: false.
     */
    public void setEnabled (boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Returns whether this resize corner is enabled (that is, does it react to input events).
     */
    public boolean isEnabled () {
        return enabled;
    }


    @Override
    public void update() throws InstantiationException {
        updateLayout();
        updateCommon();
    }

    /**
     * {@inheritDoc}
     */
    public void update(float newWidth3D, float newHeight3D) throws InstantiationException {
        updateLayout(newWidth3D, newHeight3D);
        updateCommon();
    }

    public void updateCommon() throws InstantiationException {
        
        ClientContextJME.getWorldManager().addRenderUpdater(new RenderUpdater() {
            public void update(Object arg0) {
                if (horizBar != null) {
                    horizBar.setLocalTranslationNoUpdater(new Vector3f(horizX, horizY, Z_OFFSET));
                }
                if (vertBar != null) {
                    vertBar.setLocalTranslationNoUpdater(new Vector3f(vertX, vertY, Z_OFFSET));
                }
                if (localToCellNode != null) {
                    localToCellNode.setLocalTranslation(origin);
                    ClientContextJME.getWorldManager().addToUpdateList(localToCellNode);
                }
            }
        }, null);

        // Update size
        horizBar.resize(horizWidth, horizHeight);
        vertBar.resize(vertWidth, vertHeight);

        super.update();

        if (!enabled) {
            setMouseOutsideColor();
        }
    }

        
    protected void updateLayout() {
        updateLayout(view.getDisplayerLocalWidth(), view.getDisplayerLocalHeight());
    }

    /**
     * Layout the two bars of the resize corner.
     */
    protected void updateLayout(float viewWidth, float viewHeight) {

        // First make sure that the geometry of the neighboring components is up to date
        rightSide.updateLayout();
        bottomSide.updateLayout();

        // Origin of the resize corner coordinate system is the lower right
        // corner of the view.
        origin = new Vector3f(0f, 0f, Z_OFFSET);

        horizX = (viewWidth - Frame2DCell.RESIZE_CORNER_WIDTH) / 2f;
        horizY = (-viewHeight - Frame2DCell.SIDE_THICKNESS) / 2f;
        horizWidth = RESIZE_CORNER_WIDTH;
        horizHeight = Frame2DCell.SIDE_THICKNESS;

        vertX = (viewWidth + Frame2DCell.SIDE_THICKNESS) / 2f;
        vertY = (-viewHeight - Frame2DCell.SIDE_THICKNESS + RESIZE_CORNER_HEIGHT) / 2f;
        vertWidth = Frame2DCell.SIDE_THICKNESS;
        vertHeight = RESIZE_CORNER_HEIGHT + Frame2DCell.SIDE_THICKNESS;

        logger.fine("vertX = " + vertX);
        logger.fine("vertY = " + vertY);
        logger.fine("vertWidth = " + vertWidth);
        logger.fine("vertHeight = " + vertHeight);
    }

    /**
     * {@inheritDoc}
     */
    public void setColor(ColorRGBA color) {
        if (horizBar != null) {
            logger.fine("horiz color = " + color);
            horizBar.setColor(color);
        }
        if (vertBar != null) {
            logger.fine("vert color = " + color);
            vertBar.setColor(color);
        }
    }

    /**
     * {@inheritDoc}
     */
    public ColorRGBA getColor() {
        if (horizBar != null) {
            return horizBar.getColor();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setForegroundColor(ColorRGBA color) {
        if (horizBar != null) {
            logger.fine("horiz color = " + color);
            horizBar.setForegroundColor(color);
        }
        if (vertBar != null) {
            logger.fine("vert color = " + color);
            vertBar.setForegroundColor(color);
        }
    }

    /**
     * {@inheritDoc}
     */
    public ColorRGBA getForegroundColor() {
        if (horizBar != null) {
            return horizBar.getForegroundColor();
        } else {
            return null;
        }
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

        if (enabled && mouseInside) {
            // Use the underlay to highlight button when mouse is inside
            setColor(MOUSE_INSIDE_COLOR);
        } else {
            setMouseOutsideColor();
        }
    }

    private void setMouseOutsideColor () {
        // When mouse is outside make underlay the same color as the
        // underlaying component
        if (controlArb.hasControl()) {
            setColor(HAS_CONTROL_COLOR);
        } else {
            setColor(NO_CONTROL_COLOR);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Spatial[] getSpatials() {

        if (horizBar == null) {
            horizBar = new FrameRect("HorizontalBar", view, gui, horizWidth, horizHeight);
        }
        Node horizNode = horizBar.getNode();

        if (vertBar == null) {
            vertBar = new FrameRect("Vertical Bar", view, gui, vertWidth, vertHeight);
        }
        Node vertNode = vertBar.getNode();

        return new Node[]{horizNode, vertNode};
    }
}

