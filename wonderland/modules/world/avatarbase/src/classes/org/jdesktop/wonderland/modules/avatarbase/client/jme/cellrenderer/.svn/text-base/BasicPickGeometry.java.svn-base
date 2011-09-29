/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */
package org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Spatial.CullHint;
import com.jme.scene.shape.Box;
import imi.scene.JScene;
import imi.scene.JScene.ExternalKidsType;
import imi.scene.PJoint;
import imi.scene.SkeletonNode;
import org.jdesktop.mtgame.CollisionComponent;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.JMECollisionSystem;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.mtgame.RenderManager;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.jme.CellRefComponent;
import org.jdesktop.wonderland.client.jme.ClientContextJME;

/**
 * Avatar pick geometry for simple static avatars
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
public class BasicPickGeometry implements PickGeometry {
    private final Cell cell;
    private final AvatarImiJME renderer;
    private final Entity entity;

    public BasicPickGeometry(String name, Cell cell, AvatarImiJME renderer,
                             Node geometry)
    {
        this.cell = cell;
        this.renderer = renderer;

        entity = new Entity(name + " PickGeometry");
        setupEntity(entity, geometry);
    }

    public void detach() {
        if (getEntity().getParent() != null) {
            getEntity().getParent().removeEntity(getEntity());
        }
    }

    protected Entity getEntity() {
        return entity;
    }

    protected Cell getCell() {
        return cell;
    }

    protected AvatarImiJME getRenderer() {
        return renderer;
    }

    private void setupEntity(Entity e, Node geometry) {
        JMECollisionSystem collisionSystem = (JMECollisionSystem) ClientContextJME.getWorldManager().
                getCollisionManager().loadCollisionSystem(JMECollisionSystem.class);
        CollisionComponent cc = collisionSystem.createCollisionComponent(geometry);
        cc.setCollidable(false);
        cc.setPickable(true);
        e.addComponent(CollisionComponent.class, cc);

        getRenderer().getEntity().addEntity(e);
    }
}
