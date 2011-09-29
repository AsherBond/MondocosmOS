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
package org.jdesktop.wonderland.modules.orb.client.cell;

import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.RenderState.StateType;
import com.jme.scene.state.ShadeState;
import com.jme.scene.state.ShadeState.ShadeMode;
import com.sun.scenario.animation.Animation;
import com.sun.scenario.animation.Clip;
import com.sun.scenario.animation.Clip.RepeatBehavior;
import java.util.HashSet;
import java.util.Set;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.jme.SceneWorker;
import org.jdesktop.wonderland.client.jme.input.MouseButtonEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.input.EventClassListener;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.cellrenderer.BasicRenderer;

import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.mtgame.processor.WorkProcessor.WorkCommit;
import org.jdesktop.wonderland.client.jme.input.MouseEnterExitEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D.ButtonId;

/**
 * @author jprovino
 */
public class OrbCellRenderer extends BasicRenderer {
    private static final float INNER_RADIUS = 0.175f / 2f;
    private static final float OUTER_RADIUS = 0.25f / 2f;
    private static final ColorRGBA DEFAULT_COLOR = new ColorRGBA(0.7f, 0.3f, 0.3f, 1.0f);
    private static final float MINIMUM_HEIGHT = 0.0f;
    private static final float MAXIMUM_HEIGHT = 0.1f;
    private static MaterialState DEFAULT_MATERIALSTATE;
    private static ShadeState DEFAULT_SHADESTATE;
    private static MaterialState HOVER_MATERIALSTATE;
    private static ShadeState HOVER_SHADESTATE;
    

    static {
        DEFAULT_MATERIALSTATE = (MaterialState) ClientContextJME.getWorldManager().getRenderManager().createRendererState(StateType.Material);
        DEFAULT_MATERIALSTATE.setAmbient(DEFAULT_COLOR);
        DEFAULT_MATERIALSTATE.setDiffuse(DEFAULT_COLOR);
        DEFAULT_MATERIALSTATE.setSpecular(DEFAULT_COLOR);
        DEFAULT_MATERIALSTATE.setEmissive(new ColorRGBA(0.0f, 0.0f, 0.0f, 1f));
        DEFAULT_SHADESTATE = (ShadeState) ClientContextJME.getWorldManager().getRenderManager().createRendererState(StateType.Shade);
        DEFAULT_SHADESTATE.setEnabled(true);
        DEFAULT_SHADESTATE.setShadeMode(ShadeMode.Flat);

        /** Mouse-over appearance **/
        HOVER_MATERIALSTATE = (MaterialState) ClientContextJME.getWorldManager().getRenderManager().createRendererState(StateType.Material);
        HOVER_MATERIALSTATE.setAmbient(new ColorRGBA(0.0f, 0.0f, 0.3f, 1.0f));
        HOVER_MATERIALSTATE.setDiffuse(new ColorRGBA(0.3f, 0.3f, 0.7f, 1.0f));
        HOVER_MATERIALSTATE.setSpecular(new ColorRGBA(0.3f, 0.3f, 0.7f, 1.0f));
        HOVER_MATERIALSTATE.setEmissive(new ColorRGBA(0.8f, 0.6f, 0.6f, 1.0f));
        HOVER_SHADESTATE = (ShadeState) ClientContextJME.getWorldManager().getRenderManager().createRendererState(StateType.Shade);
        HOVER_SHADESTATE.setEnabled(true);
        HOVER_SHADESTATE.setShadeMode(ShadeMode.Smooth);
    }

    private MyMouseListener listener;
    //The root of the cell, contains the orbNode
    private Node root;
    //The node containing the innerOrbNode and the outer sphere
    private Node orbNode;
    private Set<Animation> animations = new HashSet<Animation>();
    //The node containing the inner sphere
    private Node innerOrbNode;
    //The inner sphere
    private Sphere innerOrb;

    public OrbCellRenderer(Cell cell) {
        super(cell);
        listener = new MyMouseListener();
    }

    protected Node createSceneGraph(Entity entity) {
        root = new Node("orb root");
        orbNode = new Node("orb node");
        attachOrb(entity);
        attachNameTag();
        createSpeakingAnimations(entity);
        RenderComponent rc = ClientContextJME.getWorldManager().getRenderManager().createRenderComponent(root);
        entity.addComponent(RenderComponent.class, rc);
        listener.addToEntity(entity);
        setVisible(true);
        return root;
    }

    private void attachNameTag() {
        final Node nameTag = ((OrbCell) cell).getNameTagNode();
        nameTag.setLocalTranslation(0, OUTER_RADIUS/2, 0);
       	orbNode.attachChild(nameTag);
    }

    private void attachOrb(Entity entity) {
        attachInnerOrb(entity);
        attachOuterOrb(entity);
        //Spin the inner orb
        RotationAnimationProcessor spinner = new RotationAnimationProcessor(entity, innerOrbNode, 0f, 360, new Vector3f(0f,0f,1f));
        Clip spinnerClip = Clip.create(1000, Clip.INDEFINITE, spinner);
        spinnerClip.setRepeatBehavior(RepeatBehavior.LOOP);
        spinnerClip.start();
        spinnerClip.resume();
        //Rotate the inner orb
        RotationAnimationProcessor rotator = new RotationAnimationProcessor(entity, innerOrbNode, 0f, 360, new Vector3f(0f,1f,0f));
        Clip rotatorClip = Clip.create(1000, Clip.INDEFINITE, rotator);
        rotatorClip.setRepeatBehavior(RepeatBehavior.LOOP);
        rotatorClip.start();
        rotatorClip.resume();
        //Traslate the whole orb
        TranslationAnimationProcessor translator = new TranslationAnimationProcessor(entity, orbNode, new Vector3f(0f,MINIMUM_HEIGHT,0f) , new Vector3f(0f,MAXIMUM_HEIGHT,0f));
        Clip translatorClip = Clip.create(1000, Clip.INDEFINITE, translator);
        translatorClip.setRepeatBehavior(RepeatBehavior.REVERSE);
        translatorClip.start();
        translatorClip.resume();
    }

    private void attachInnerOrb(Entity entity) {
        innerOrbNode = new Node("Inner orb node");
        innerOrb = new Sphere("Inner Orb", 8, 8, INNER_RADIUS);
        innerOrb.setModelBound(new BoundingSphere());
        innerOrb.updateModelBound();
        innerOrb.setRenderState(DEFAULT_MATERIALSTATE);
        innerOrb.setRenderState(DEFAULT_SHADESTATE);

        innerOrbNode.attachChild(innerOrb);
        orbNode.attachChild(innerOrbNode);
    }

    private void attachOuterOrb(Entity entity) {
        final Sphere outerOrb = new Sphere("Outer Orb", 16, 16, OUTER_RADIUS);
        outerOrb.setModelBound(new BoundingSphere());
        outerOrb.updateModelBound();
        ColorRGBA orbColour = new ColorRGBA(0f, 0f, 1f, 0.2f);
        MaterialState matState = (MaterialState) ClientContextJME.getWorldManager().getRenderManager().createRendererState(StateType.Material);
        matState.setDiffuse(orbColour);
        outerOrb.setRenderState(matState);

        BlendState bs = (BlendState) ClientContextJME.getWorldManager().getRenderManager().createRendererState(StateType.Blend);
        bs.setEnabled(true);
        bs.setBlendEnabled(true);
        bs.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
        bs.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
        outerOrb.setRenderState(bs);

        CullState cs = (CullState) ClientContextJME.getWorldManager().getRenderManager().createRendererState(StateType.Cull);
        cs.setEnabled(true);
        cs.setCullFace(CullState.Face.Back);
        outerOrb.setRenderState(cs);

       	orbNode.attachChild(outerOrb);
    }


    public void setVisible(final boolean isVisible) {
        SceneWorker.addWorker(new WorkCommit() {

            public void commit() {
                if (isVisible) {
                    root.attachChild(orbNode);
                } else {
                    root.detachChild(orbNode);
                }
            }
        });
    }

    public void removeMouseListener() {
        listener.removeFromEntity(entity);
    }

    private void createSpeakingAnimations(Entity entity) {
        ScaleAnimationProcessor scaler = new ScaleAnimationProcessor(entity, innerOrbNode, 1.0f, OUTER_RADIUS/INNER_RADIUS * 0.9f);
        Clip scalingClip = Clip.create(1000, Clip.INDEFINITE, scaler);
        scalingClip.setRepeatBehavior(RepeatBehavior.REVERSE);
        scalingClip.start();
        scalingClip.pause();
        animations.add(scalingClip);
        //Need to add a colour animator?
    }

    private void setRenderState(Sphere aSphere, RenderState aRenderState) {
            aSphere.setRenderState(aRenderState);
            ClientContextJME.getWorldManager().addToUpdateList(aSphere);
    }

    private void enableSpeakingAnimations(boolean b) {
        for (Animation anim : animations) {
            if (b) {
                anim.resume();
            } else {
                anim.pause();
            }
        }
    }

    class MyMouseListener extends EventClassListener {

        public MyMouseListener() {
        }

        @Override
        public Class[] eventClassesToConsume() {
            return new Class[]{MouseEvent3D.class};
        }

        @Override
        public void commitEvent(Event event) {
            if (event instanceof MouseEnterExitEvent3D) {
                MouseEnterExitEvent3D mevt = (MouseEnterExitEvent3D) event;
                if (mevt.isEnter()) {
                    setRenderState(innerOrb, HOVER_MATERIALSTATE);
                    setRenderState(innerOrb, HOVER_SHADESTATE);
                } else {
                    setRenderState(innerOrb, DEFAULT_MATERIALSTATE);
                    setRenderState(innerOrb, DEFAULT_SHADESTATE);
                }
            }
            if (event instanceof MouseButtonEvent3D) {
                MouseButtonEvent3D buttonEvent = (MouseButtonEvent3D) event;
                if (buttonEvent.isPressed() && (buttonEvent.getButton() == ButtonId.BUTTON1)) {
                    logger.info("Orb Selected");
                    ((OrbCell) cell).orbSelected();
                }
                //For testing only
                if (buttonEvent.isPressed() && (buttonEvent.getButton() == ButtonId.BUTTON3)) {
                    if (!buttonEvent.getAwtEvent().isControlDown()) {
                        enableSpeakingAnimations(true);
                    }
                    if (!buttonEvent.getAwtEvent().isShiftDown()){
                        enableSpeakingAnimations(false);
                    }
                }
            }
        }

        
    }
}
