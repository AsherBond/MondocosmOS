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
package org.jdesktop.wonderland.modules.affordances.client.jme;

import com.jme.scene.Node;
import com.jme.scene.state.RenderState.StateType;
import com.jme.scene.state.ZBufferState;
import java.util.logging.Logger;
import org.jdesktop.mtgame.CollisionComponent;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.JMECollisionSystem;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.mtgame.RenderManager;
import org.jdesktop.mtgame.RenderUpdater;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.jme.ClientContextJME;

/**
 * The base class of all Affordances (manipulators), e.g. for translation,
 * rotation, and resizing. This base class is an MTGame Entity and can be
 * added directly to the world scene graph. This class cannot be created directly
 * since it is abstract: rather one of the subclasses must be created.
 * <p>
 * The setVisible() method makes the affordance either visible or not in the
 * world scene graph.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public abstract class Affordance extends Entity {

    protected static Logger logger = Logger.getLogger(Affordance.class.getName());
    protected Node rootNode;
    private boolean isVisible = false;

    protected static ZBufferState zbuf = null;
    static {
        RenderManager rm = ClientContextJME.getWorldManager().getRenderManager();
        zbuf = (ZBufferState)rm.createRendererState(StateType.ZBuffer);
        zbuf.setEnabled(true);
        zbuf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
    }

    /** Constructor, takes the cell */
    protected Affordance(String name) {
        super(name);

        // Create the root node of the cell and the render component to attach
        // to the Entity with the node
        rootNode = new Node();
        RenderManager rm = ClientContextJME.getWorldManager().getRenderManager();
        RenderComponent rc = rm.createRenderComponent(rootNode);
        this.addComponent(RenderComponent.class, rc);
    }

    /**
     * Returns the root Node for the affordance Entity.
     *
     * @param The Entity root Node
     */
    public Node getRootNode() {
        return rootNode;
    }

    /**
     * Sets whether the affordance is visible (true) or invisible (false).
     *
     * @param visible True to make the affordance visible, false to not
     */
    public synchronized void setVisible(boolean visible) {
        // If we want to make the affordance visible and it already is not
        // visible, then make it visible.
        if (visible == true && isVisible == false) {
            RenderUpdater updater = new RenderUpdater() {
                public void update(Object arg0) {
                    Affordance affordance = (Affordance)arg0;
                    Node rootNode = affordance.getRootNode();
                    ClientContextJME.getWorldManager().addEntity(affordance);
                    ClientContextJME.getWorldManager().addToUpdateList(rootNode);
                }
            };
            WorldManager wm = ClientContextJME.getWorldManager();
            wm.addRenderUpdater(updater, this);
            isVisible = true;
            return;
        }

        // If we want to make the affordance invisible and it already is
        // visible, then make it invisible
        if (visible == false && isVisible == true) {
            RenderUpdater updater = new RenderUpdater() {
                public void update(Object arg0) {
                    Affordance affordance = (Affordance)arg0;
                    ClientContextJME.getWorldManager().removeEntity(affordance);
                }
            };
            WorldManager wm = ClientContextJME.getWorldManager();
            wm.addRenderUpdater(updater, this);
            isVisible = false;
            return;
        }
    }

    /**
     * Sets the size of the affordance as a floating point value. Generally,
     * a size of 1.0 means the same size as the Cell geometry.
     *
     * @param size The size of the affordances.
     */
    public abstract void setSize(float size);

    /**
     * Removes the affordance from the cell and cleans it up so that it may
     * be garbage collected. Once this method is invoked, the affordance can
     * no longer be used and must be recreated. If the affordance is currently
     * visible, this method makes it invisible first.
     */
    public void dispose() {
        setVisible(false);
    }

    /**
     * Make this entity pickable by adding a collision component to it.
     */
    protected void makeEntityPickable(Entity entity, Node node) {
        JMECollisionSystem collisionSystem = (JMECollisionSystem)
                ClientContextJME.getWorldManager().getCollisionManager().
                loadCollisionSystem(JMECollisionSystem.class);

        CollisionComponent cc = collisionSystem.createCollisionComponent(node);
        cc.setCollidable(false);  // Not collidable
        entity.addComponent(CollisionComponent.class, cc);
    }

    /**
     * Adds an Entity with its root node to the scene graph, using the super-
     * class Entity as the parent
     */
    protected void addSubEntity(Entity subEntity, Node subNode) {
        // Create the render component that associates the node with the Entity
        RenderManager rm = ClientContextJME.getWorldManager().getRenderManager();
        RenderComponent thisRC = rm.createRenderComponent(subNode);
        subEntity.addComponent(RenderComponent.class, thisRC);

        // Add this Entity to the parent Entity
        RenderComponent parentRC = this.getComponent(RenderComponent.class);
        thisRC.setAttachPoint(parentRC.getSceneRoot());
        this.addEntity(subEntity);
    }
}
