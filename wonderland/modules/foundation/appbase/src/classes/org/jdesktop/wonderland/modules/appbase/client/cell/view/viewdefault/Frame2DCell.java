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

import com.jme.scene.Node;
import java.util.logging.Logger;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.ControlArb;
import org.jdesktop.wonderland.modules.appbase.client.view.Frame2D;
import org.jdesktop.wonderland.modules.appbase.client.view.View2D;
import java.awt.Dimension;

/**
 * Renders an optional rectangular border around a View2DCell. A frame is comprised of
 * several frame components: a header, two sides (left and right), a bottom, and a 
 * resize corner.
 * <br><br>
 * @author deronj
 */
@ExperimentalAPI
public class Frame2DCell implements Frame2D, ControlArb.ControlChangeListener {

    // IMPLEMENTATION NOTE:  The frame handles MTGame and JME update issues internally.

    private static final Logger logger = Logger.getLogger(Frame2DCell.class.getName());

    /** The control arb of the app to which this frame's view belongs. */
    private ControlArb controlArb;

    /** Whether the frame is currently attached to a view. */
    private boolean attached;

    /** Whether the resize corner is active. */
    private boolean userResizable;

    /**
     * Components who wish to be notified when the user has pressed the
     * close button should implement this interface and register themselves
     * with addCloseListener.
     */
    public interface CloseListener {
        /** Called when the user clicks on the frame's close button. */
        public void close();
    }

    /** The height of the header */
    public static final float HEADER_HEIGHT = /* 0.2f */ /*6.3f*/ 1.25f/3f;
    /** The thickness (in the plane of the frame) of the other parts of the border */
    public static final float SIDE_THICKNESS = /*0.07f*/ /* 3.0f */ 0.75f/6f;
    /** The width of the resize corner - currently the same as a header height */
    public static final float RESIZE_CORNER_WIDTH = HEADER_HEIGHT;
    /** The height of the resize corner - currently the same as a header height */
    public static final float RESIZE_CORNER_HEIGHT = HEADER_HEIGHT;
    /** The frame's header (top side) */
    private FrameHeaderSwing header;
    /** The frame's left side */
    private FrameSide leftSide;
    /** The frame's right side */
    private FrameSide rightSide;
    /** The frame's bottom side */
    private FrameSide bottomSide;
    /** The resize corner */
    private FrameResizeCorner resizeCorner;
    /** 
     * The root of the frame subgraph. This contains all geometry and is 
     * connected to the view node via an attach point.
     */
    private Node frameNode;
    /**  The root entity for this frame. */
    private Entity frameEntity;

    /** The name of this frame. */
    private String name;

    /** The view to which this cell belongs. */
    private View2DCell view;

    /**
     * Create a new instance of FrameWorldDefault.
     *
     * @param view The view the frame encloses.
     */
    public Frame2DCell (View2DCell view) {
        this.view = view;
        name = "Frame for " + view.getName();

        frameEntity = new Entity("Entity for " + name);
        frameNode = new Node("Node for " + name);
        RenderComponent rc =
                ClientContextJME.getWorldManager().getRenderManager().createRenderComponent(frameNode);
        frameEntity.addComponent(RenderComponent.class, rc);

        header = new FrameHeaderSwing(view);

        leftSide = new FrameSide(view, FrameSide.Side.LEFT, new Gui2DSide(view));
        leftSide.setParentEntity(frameEntity);

        rightSide = new FrameSide(view, FrameSide.Side.RIGHT, new Gui2DSide(view));
        rightSide.setParentEntity(frameEntity);

        bottomSide = new FrameSide(view, FrameSide.Side.BOTTOM, new Gui2DSide(view));
        bottomSide.setParentEntity(frameEntity);

        resizeCorner = new FrameResizeCorner(view, rightSide, bottomSide);
        resizeCorner.setParentEntity(frameEntity);

        controlArb = view.getWindow().getApp().getControlArb();
        if (controlArb != null) {
            controlArb.addListener(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized void cleanup() {
        if (attached) {
            detachFromViewEntity();
        }
        if (header != null) {
            header.cleanup();
            header = null;
        }
        if (leftSide != null) {
            leftSide.cleanup();
            leftSide = null;
        }
        if (rightSide != null) {
            rightSide.cleanup();
            rightSide = null;
        }
        if (bottomSide != null) {
            bottomSide.cleanup();
            bottomSide = null;
        }
        if (resizeCorner != null) {
            resizeCorner.cleanup();
            resizeCorner = null;
        }
        if (frameEntity != null) {
            frameEntity.removeComponent(RenderComponent.class);
            frameNode = null;
            frameEntity = null;
        }
        if (controlArb != null) {
            controlArb.removeListener(this);
            controlArb = null;
        }
        view = null;
    }

    public void attachToViewEntity () {
        if (attached) return;
        Entity viewEntity = view.getEntity();
        if (viewEntity == null) return;

        viewEntity.addEntity(frameEntity);

        RenderComponent rcFrame = (RenderComponent) frameEntity.getComponent(RenderComponent.class);

        if (rcFrame != null) {
            // We need to attach secondary view frames to the GEOMETRY NODE of its views
            // so that they move with the offset of the view
            //
            // SPECIAL NOTE: 
            // This provides special treatment for the left/right/bottom of a secondary frame
            // but it doesn't handle the header. See also: SPECIAL NOTE in View2DEntity.processChanges.
            if (view.getType() == View2D.Type.SECONDARY) {
                rcFrame.setAttachPoint(view.getGeometryNode());
            } else {
                rcFrame.setAttachPoint(view.getViewNode());
            }
        }

        if (header != null) {
            header.setVisible(true);
        }

        attached = true;
    }

    public void detachFromViewEntity () {
        if (!attached) return;

        Entity viewEntity = view.getEntity();
        if (viewEntity == null) return;
        viewEntity.removeEntity(frameEntity);
        RenderComponent rcFrame = (RenderComponent) frameEntity.getComponent(RenderComponent.class);
        if (rcFrame != null) {
            rcFrame.setAttachPoint(null);
        }
        if (header != null) {
            header.setVisible(false);
        }
        attached = false;
    }

    /** {@inheritDoc} */
    public View2D getView () {
        return view;
    }

    /** 
     * The size of the view has changed. Make the corresponding
     * position and/or size updates for the frame components.
     *
     * @throw InstantiationException if couldn't allocate resources for the visual representation.
     */
    public synchronized void update() throws InstantiationException {
        if (header != null) {
            header.update();
        }
        if (leftSide != null) {
            leftSide.update();
        }
        if (rightSide != null) {
            rightSide.update();
        }
        if (bottomSide != null) {
            bottomSide.update();
        }
        if (resizeCorner != null) {
            resizeCorner.update();
        }

        updateControl(controlArb);
    }

    /**
     * The form of update used during user resize. 
     */
    public synchronized void update (float newWidth3D, float newHeight3D, Dimension newSize) 
        throws InstantiationException 
    {
        if (header != null) {
            header.update(newWidth3D, newHeight3D, newSize);
        }
        if (leftSide != null) {
            leftSide.update(newWidth3D, newHeight3D);
        }
        if (rightSide != null) {
            rightSide.update(newWidth3D, newHeight3D);
        }
        if (bottomSide != null) {
            bottomSide.update(newWidth3D, newHeight3D);
        }
        if (resizeCorner != null) {
            resizeCorner.update(newWidth3D, newHeight3D);
        }
    }

    /** {@inheritDoc} */
    public synchronized void setTitle(String title) {
        if (header != null) {
            header.setTitle(title);
        }
    }

    /** {@inheritDoc} */
    public synchronized void setUserResizable(boolean userResizable) {
        if (resizeCorner != null) {
            resizeCorner.setEnabled(userResizable);
        }
    }

    /**
     * {@inheritDoc}
     * THREAD USAGE NOTE: Called on the EDT.
     */
    public void updateControl(ControlArb controlArb) {
        // Sometimes some of these are null during debugging
        if (header != null) {
            header.updateControl(controlArb);
        }
        if (leftSide != null) {
            leftSide.updateControl(controlArb);
        }
        if (rightSide != null) {
            rightSide.updateControl(controlArb);
        }
        if (bottomSide != null) {
            bottomSide.updateControl(controlArb);
        }
        if (resizeCorner != null) {
            resizeCorner.updateControl(controlArb);
        }
    }

    @Override
    public String toString () {
        return name;
    }

    // Returns the width of the header in local coordinates
    float getHeaderWidth () {
        if (header != null) {
            return header.getWidth();
        } else {
            return 0f;
        }
    }

    // Returns the height of the header in local coordinates
    float getHeaderHeight () {
        if (header != null) {
            return header.getHeight();
        } else {
            return 0f;
        }
    }

    // Returns the width of the frame left side in local coordinates
    float getLeftSideWidth () {
        if (leftSide != null) {
            return leftSide.getWidth();
        } else {
            return 0f;
        }
    }

    // Returns the height of the frame left side in local coordinates
    float getLeftSideHeight () {
        if (leftSide != null) {
            return leftSide.getHeight();
        } else {
            return 0f;
        }
    }

    // Returns the width of the frame right side in local coordinates
    float getRightSideWidth () {
        if (rightSide != null) {
            return rightSide.getWidth();
        } else {
            return 0f;
        }
    }

    // Returns the height of the frame right side in local coordinates
    float getRightSideHeight () {
        if (rightSide != null) {
            return rightSide.getHeight();
        } else {
            return 0f;
        }
    }

    // Returns the width of the frame bottom side in local coordinates
    float getBottomSideWidth () {
        if (bottomSide != null) {
            return bottomSide.getWidth();
        } else {
            return 0f;
        }
    }

    // Returns the height of the frame bottom side in local coordinates
    float getBottomSideHeight () {
        if (bottomSide != null) {
            return bottomSide.getHeight();
        } else {
            return 0f;
        }
    }
}

