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
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.wonderland.modules.appbase.client.ControlArb;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.view.Gui2D;
import org.jdesktop.mtgame.RenderUpdater;
import org.jdesktop.wonderland.client.jme.ClientContextJME;

/**
 * The generic superclass of window frame components.
 *
 * @author deronj
 */
@ExperimentalAPI
public abstract class FrameComponent {

    /** The component name */
    protected String name;
    /** The color to display when the app has control. */
    protected static final ColorRGBA HAS_CONTROL_COLOR = new ColorRGBA(0.0f, 0.9f, 0.0f, 1f);
    /** The color to display when the app has control. */
    protected static final ColorRGBA NO_CONTROL_COLOR = new ColorRGBA(0.9f, 0.0f, 0.0f, 1f);
    /** The text color to display when the app has control. */
    protected static final ColorRGBA HAS_CONTROL_FOREGROUND_COLOR = new ColorRGBA(0.0f, 0.0f, 0.0f, 1f);
    /** The text color to display when the app has control. */
    protected static final ColorRGBA NO_CONTROL_FOREGROUND_COLOR = new ColorRGBA(1.0f, 1.0f, 1.0f, 1f);
    /** The view of the window the frame encloses. */
    protected View2DCell view;
    /** The control arb of the app. */
    protected ControlArb controlArb;
    /** The event handler of this component. */
    protected Gui2D gui;
    /** 
     * The entity of this component's parent component. This components entity is attached to this as
     * a child when ever the parentEntity is non-null.
     */
    protected Entity parentEntity;
    /** This component's entity. The scene graph and event listeners component are attached to this. */
    protected Entity entity;
    /** 
     * The local-to-cell transform node. Moves the rect local coords into cell coords. This is parented
     * to attach point of the parent entity when the cell goes live.
     */
    protected Node localToCellNode;
    /**
     * Whether this component's event listeners are attached to its entity.
     */
    protected boolean eventListenersAttached;

    /** 
     * Create a new instance of <code>FrameComponent</code>.
     * @param name The component name.
     * @param view The view the frame encloses.
     * @param gui The event handler.
     */
    public FrameComponent(String name, View2DCell view, Gui2D gui) {
        this.name = name;
        this.view = view;
        this.gui = gui;
        controlArb = view.getWindow().getApp().getControlArb();
        initEntity();
    }

    /**
     * Clean up resources.
     */
    public void cleanup() {
        view = null;
        cleanupEntity();
        if (gui != null) {
            gui.cleanup();
            gui = null;
        }
    }

    /**
     * Return this component's entity.
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Return's this component's scene graph node.
     */
    public Node getNode () {
        return localToCellNode;
    }

    /**
     * Initialize this component's entity.
     */
    protected void initEntity() {

        // Create this component's entity and parent it
        entity = new Entity("Entity for frame component " + name);

        // Create this component scene graph (l2c -> geometry)
        initSceneGraph();

        // Make the entity pickable
        View2DCell.entityMakePickable(entity);

        // Attach gui event listeners for this component
        if (gui != null) {
            gui.attachEventListeners(entity);
        }

        attachToParentEntity();
    }

    /**
     * Clean up resources for this component's entity.
     */
    protected void cleanupEntity() {
        detachFromParentEntity();
        if (gui != null) {
            gui.detachEventListeners(entity);
        }
        cleanupSceneGraph();
        entity = null;
    }

    /**
     * Construct this component's scene graph. This consists of the following nodes.
     *
     * parentEntity attachPoint -> localToCellNode -> Spatial, Spatial, etc. (subclass provided)
     */
    protected void initSceneGraph() {

        // Attach the localToCell node to the entity
        localToCellNode = new Node("Local-to-cell node for frame component " + name);
        RenderComponent rc = ClientContextJME.getWorldManager().getRenderManager().
                createRenderComponent(localToCellNode);
        entity.addComponent(RenderComponent.class, rc);
        rc.setEntity(entity);

        // Attach the subclass spatials to the localToCell node
        final Spatial[] spatials = getSpatials();
        if (spatials != null) {
            ClientContextJME.getWorldManager().addRenderUpdater(new RenderUpdater() {
                public void update(Object arg0) {
                    if (localToCellNode != null) {
                        for (Spatial spatial : spatials) {
                            localToCellNode.attachChild(spatial);
                        }
                        ClientContextJME.getWorldManager().addToUpdateList(localToCellNode);
                    }
                }
            }, null, true); // Topology change. Must wait for it to complete.
        }
    }

    /**
     * Detach and deallocate this component's scene graph nodes.
     */
    protected void cleanupSceneGraph() {
        if (entity != null) {
            entity.removeComponent(RenderComponent.class);
        }
        localToCellNode = null;
    // Note: the subclasses cleanup routine is responsible for cleaning up the spatials.
    }

    /**
     * Returns a list of this component's spatials. Non-container subclasses should
     * override this to return actual spatials
     */
    protected Spatial[] getSpatials() {
        return null;
    }

    /**
     * Attach this component's entity to its parent entity.
     */
    protected void attachToParentEntity() {
        if (parentEntity != null) {
            parentEntity.addEntity(entity);
            RenderComponent rcParentEntity =
                    (RenderComponent) parentEntity.getComponent(RenderComponent.class);
            RenderComponent rcEntity = (RenderComponent) entity.getComponent(RenderComponent.class);
            if (rcParentEntity != null && rcParentEntity.getSceneRoot() != null && rcEntity != null) {
                rcEntity.setAttachPoint(rcParentEntity.getSceneRoot());
            }
        }
    }

    /**
     * Detach this component's entity from its parent entity.
     */
    protected void detachFromParentEntity() {
        if (parentEntity != null) {
            parentEntity.removeEntity(entity);
            RenderComponent rcEntity = (RenderComponent) entity.getComponent(RenderComponent.class);
            if (rcEntity != null) {
                rcEntity.setAttachPoint(null);
            }
        }
        parentEntity = null;
    }

    /**
     * Specify the parent entity of this component.
     */
    public void setParentEntity(Entity parentEntity) {
        if (parentEntity != null) {
            detachFromParentEntity();
        }
        this.parentEntity = parentEntity;
        if (this.parentEntity != null) {
            attachToParentEntity();
        }
    }

    /**
     * The size of the view has changed. Make the corresponding
     * position and/or size updates for this frame component.
     *
     * @throw InstantiationException if couldn't allocate resources for the visual representation.
     */
    public void update() throws InstantiationException {
        updateColor();
    }

    /**
     * The control state of the app has changed. Make the corresponding change in the frame.
     *
     * @param controlArb The app's control arb.
     */
    public void updateControl(ControlArb controlArb) {
        updateColor();
    }

    /**
     * Update the component color based on whether the user has control of the app.
     */
    protected void updateColor() {
        if (controlArb == null || controlArb.hasControl()) {
            setColor(HAS_CONTROL_COLOR);
            setForegroundColor(HAS_CONTROL_FOREGROUND_COLOR);
        } else {
            setColor(NO_CONTROL_COLOR);
            setForegroundColor(NO_CONTROL_FOREGROUND_COLOR);
        }
    }

    /**
     * Set the background color of the component.
     *
     * @param color The new background color.
     */
    public abstract void setColor(ColorRGBA color);

    /**
     * Get the background color of the component.
     */
    public abstract ColorRGBA getColor();

    /**
     * Set the foreground color of the component.
     *
     * @param color The new foreground color.
     */
    public abstract void setForegroundColor(ColorRGBA color);

    /**
     * Get the foreground color of the component.
     */
    public abstract ColorRGBA getForegroundColor();

    /**
     * Sets the localToCell translation of this component.
     *
     * @param trans The translation vector.
     */
    public void setLocalTranslation(final Vector3f trans) {
        ClientContextJME.getWorldManager().addRenderUpdater(new RenderUpdater() {
            public void update(Object arg0) {
                if (localToCellNode != null) {
                    setLocalTranslationNoUpdater(trans);
                    ClientContextJME.getWorldManager().addToUpdateList(localToCellNode);
                }
            }
        }, null);
    }

    /**
     * Sets the localToCell translation of this component.
     *
     * @param trans The translation vector.
     */
    public void setLocalTranslationNoUpdater(final Vector3f trans) {
        localToCellNode.setLocalTranslation(trans);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Frame component " + name;
    }
}
