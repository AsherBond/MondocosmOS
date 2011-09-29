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

import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import java.awt.event.KeyEvent;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.input.EventClassListener;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.input.KeyEvent3D;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.modules.appbase.client.cell.App2DCell;
import org.jdesktop.wonderland.modules.appbase.client.cell.App2DCellRenderer;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.jme.utils.graphics.GraphicsUtils;

/**
 * A cell renderer which uses JME to render app cell contents. It creates
 * a root node which it hands off to mtgame. It allows window views to be
 * attached. When a window view is attached the base node of the window is 
 * added as a child of this rootNode.
 *
 * @author dj
 */
@InternalAPI
public class App2DCellRendererJME extends App2DCellRenderer {

    /** The root node of the cell renderer. */
    protected Node acrjRootNode;

    /** The light state. */
    protected LightState lightState;

    /** 
     * Create a new instance of App2DCellRendererJME.
     * @param cell The cell to be rendered.
     */
    public App2DCellRendererJME(App2DCell cell) {
        super(cell);

        acrjRootNode = new Node("Root node for cell " + cell.getCellID().toString());

        initLightState();
        acrjRootNode.setRenderState(lightState);

        // For debug
        //ClientContextJME.getInputManager().addGlobalEventListener(new SceneGraphPrinter());
        //ClientContextJME.getInputManager().addGlobalEventListener(new OrthoPrinter());
    }

    // For debug
    private class SceneGraphPrinter extends EventClassListener {

        @Override
        public Class[] eventClassesToConsume() {
            return new Class[]{KeyEvent3D.class};
        }

        @Override
        public void commitEvent(Event event) {
            KeyEvent3D ke3d = (KeyEvent3D) event;
            if (ke3d.isPressed()) {
                KeyEvent ke = (KeyEvent) ke3d.getAwtEvent();
                if (ke.getKeyCode() == KeyEvent.VK_P) {
                    printEntitySceneGraphs(App2DCellRendererJME.this.getEntity(), 0);
                }
            }
        }
    }

    // For debug
    public static class OrthoPrinter extends EventClassListener {

        @Override
        public Class[] eventClassesToConsume() {
            return new Class[]{KeyEvent3D.class};
        }

        @Override
        public void commitEvent(Event event) {
            KeyEvent3D ke3d = (KeyEvent3D) event;
            if (ke3d.isPressed()) {
                KeyEvent ke = (KeyEvent) ke3d.getAwtEvent();
                if (ke.getKeyCode() == KeyEvent.VK_O) {
                    printOrthoNodes();
                }
            }
        }
    }

    // For debug
    public static void printOrthoNodes () {
        // Print ortho nodes attached to the world manager
        WorldManager wm = ClientContextJME.getWorldManager();
        for (int i=0; i < wm.numEntities(); i++) {
            Entity e = wm.getEntity(i);
            RenderComponent rc = (RenderComponent) e.getComponent(RenderComponent.class);
            if (rc == null || !rc.getOrtho()) continue;
            System.err.println("Ortho node = " + rc.getSceneRoot());
            GraphicsUtils.printNode(rc.getSceneRoot());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Node createSceneGraph(Entity entity) {

        RenderComponent rc = ClientContextJME.getWorldManager().getRenderManager().
                createRenderComponent(acrjRootNode);
        entity.addComponent(RenderComponent.class, rc);
        rc.setEntity(entity);

        return acrjRootNode;
    }

    /**
     * Initialize the light state.
     */
    protected void initLightState() {
        PointLight light = new PointLight();
        light.setDiffuse(new ColorRGBA(0.75f, 0.75f, 0.75f, 0.75f));
        light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        light.setLocation(new Vector3f(100, 100, 100));
        light.setEnabled(true);
        lightState = (LightState) ClientContextJME.getWorldManager().getRenderManager().createRendererState(RenderState.RS_LIGHT);
        lightState.setEnabled(true);
        lightState.attach(light);
    }

    /**
     * {@inheritDoc}
     */
    public void logSceneGraph() {
        EntityLogger.logEntity(getEntity());
    //printEntitySceneGraphs(getEntity(), 0);
    }
    private static String INDENT = "    ";

    private static void printIndentLevel(int indentLevel) {
        for (int i = 0; i < indentLevel; i++) {
            System.err.print(INDENT);
        }
    }

    static void printEntitySceneGraphs(Entity entity, int indentLevel) {
        printIndentLevel(indentLevel);
        System.err.println("Entity = " + entity);

        printIndentLevel(indentLevel);
        System.err.print("sceneRoot = ");
        RenderComponent rc = (RenderComponent) entity.getComponent(RenderComponent.class);
        if (rc == null) {
            System.err.println("null");
        } else {
            Node sceneRoot = rc.getSceneRoot();
            System.err.println(sceneRoot);
            if (sceneRoot != null) {
                GraphicsUtils.printNode(sceneRoot);
            }

        }

        /** 
         * For Debug: only enable when we need to see the entity tree. 
         * Otherwise this prints out too much info. (It prints out the scene graph multiple times!)
        int numChildren = entity.numEntities();
        for (int i = 0; i < numChildren; i++) {
            Entity child = entity.getEntity(i);
            printIndentLevel(indentLevel);
            System.err.println("==================");
            printIndentLevel(indentLevel);
            System.err.println("Child Entity " + i + ": " + child);
            printEntitySceneGraphs(child, indentLevel + 1);
            printIndentLevel(indentLevel);
            System.err.println("==================");
        }
        */
    }
}
