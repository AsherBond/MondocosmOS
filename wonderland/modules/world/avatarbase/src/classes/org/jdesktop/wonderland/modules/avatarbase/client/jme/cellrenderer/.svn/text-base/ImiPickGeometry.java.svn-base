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
 * Avatar pick geometry for IMI skeleton-based avatars
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
public class ImiPickGeometry extends Node implements PickGeometry {
    private static final PickBox[] MALE_PICK = {
        new PickBox("Head", 0f, -.02f, -.02f, .1f, .15f, .12f),
        new PickBox("Neck", 0f, -.36f, -.11f, .33f, .35f, .15f),
        new PickBox("rightUpLeg", 0f, .25f, 0f, .1f, .28f, .1f),
        new PickBox("rightLeg", 0f, .25f, 0f, .1f, .25f, .1f),
        new PickBox("leftUpLeg", 0f, .25f, 0f, .1f, .28f, .1f),
        new PickBox("leftLeg", 0f, .25f, 0f, .1f, .25f, .1f)
    };

    private static final PickBox[] FEMALE_PICK = {
        new PickBox("Head", 0f, -.02f, -.05f, .1f, .15f, .1f),
        new PickBox("Neck", 0f, -.39f, -.08f, .21f, .35f, .12f),
        new PickBox("rightUpLeg", 0f, .24f, 0f, .09f, .25f, .08f),
        new PickBox("rightLeg", 0f, .24f, 0f, .09f, .28f, .08f),
        new PickBox("leftUpLeg", 0f, .24f, 0f, .09f, .25f, .08f),
        new PickBox("leftLeg", 0f, .24f, 0f, .09f, .28f, .08f)
    };

    private final Cell cell;
    private final AvatarImiJME renderer;
    private final Entity entity;

    public ImiPickGeometry(String name, Cell cell, AvatarImiJME renderer,
                        PickBox[] boxes)
    {
        super (name + " PickGeometry");

        this.cell = cell;
        this.renderer = renderer;

        entity = new Entity(name + " PickGeometry");
        setupEntity(entity);

        attach(boxes);
    }

    public static PickBox[] getDefaultGeometry(boolean isMale) {
        PickBox[] src = isMale ? MALE_PICK : FEMALE_PICK;

        // be sure to return a copy, since pickboxes are mutable!
        PickBox[] out = new PickBox[src.length];
        for (int i = 0; i < src.length; i++) {
            out[i] = src[i].clone();
        }

        return out;
    }

    public void detach() {
        if (getEntity().getParent() != null) {
            getEntity().getParent().removeEntity(getEntity());
        }
    }

    @Override
    public int attachChild(Spatial child) {
        if (!(child instanceof PickBox)) {
            throw new IllegalArgumentException("Only pick boxes allowed");
        }

        return super.attachChild(child);
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

    private void attach(PickBox[] boxes) {
        CellRefComponent crc = new CellRefComponent(getCell());
        getEntity().addComponent(CellRefComponent.class, crc);

        getRenderer().getEntity().addEntity(getEntity());

        for (PickBox box : boxes) {
            box.attach(this);
        }
    }

    private void setupEntity(Entity e) {
        WlAvatarCharacter character = getRenderer().getAvatarCharacter();
        JScene js = character.getJScene();

        Node attachNode =
                js.getExternalKidsRoot(ExternalKidsType.UNTRANSFORMED);

        RenderManager rm = ClientContextJME.getWorldManager().getRenderManager();

        RenderComponent rc = rm.createRenderComponent(this);
        rc.setAttachPoint(attachNode);
        e.addComponent(RenderComponent.class, rc);

        JMECollisionSystem collisionSystem = (JMECollisionSystem) ClientContextJME.getWorldManager().
                getCollisionManager().loadCollisionSystem(JMECollisionSystem.class);
        CollisionComponent cc = collisionSystem.createCollisionComponent(rc.getSceneRoot());
        cc.setCollidable(false);
        cc.setPickable(true);
        e.addComponent(CollisionComponent.class, cc);
    }

    public static class PickBox extends Box implements Cloneable {
        private final String jointName;
        private PJoint attachJoint;

        public PickBox(String jointName, float x, float y, float z,
                       float xSize, float ySize, float zSize)
        {
            super ("Pick Box", new Vector3f(x, y, z), xSize, ySize, zSize);

            this.jointName = jointName;

            setModelBound(new BoundingBox(new Vector3f(x, y, z),
                                          xSize, ySize, zSize));
            setCullHint(CullHint.Always);
        }

        public String getJointName() {
            return jointName;
        }

        public void attach(ImiPickGeometry geometry) {
            AvatarImiJME renderer = geometry.getRenderer();
            SkeletonNode skel = renderer.getAvatarCharacter().getSkeleton();

            PJoint parentJoint = skel.getJoint(getJointName());
            attachJoint = new JMEUpdateJoint(this);
            parentJoint.addChild(attachJoint);

            geometry.attachChild(this);
            geometry.updateGeometricState(0, true);

            ClientContextJME.getWorldManager().addToUpdateList(geometry);
            ClientContextJME.getWorldManager().addToUpdateList(this);
        }

        public void detach() {
            if (attachJoint != null) {
                attachJoint.getParent().removeChild(attachJoint);
            }

            if (getParent() != null) {
                getParent().detachChild(this);
            }
        }

        @Override
        public PickBox clone() {
            return new PickBox(getJointName(),
                               getCenter().x, getCenter().y, getCenter().z,
                               getXExtent(), getYExtent(), getZExtent());
        }
    }
}
