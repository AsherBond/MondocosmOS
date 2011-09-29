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
package org.jdesktop.wonderland.modules.defaultenvironment.client;

import com.jme.image.Texture;
import com.jme.light.LightNode;
import com.jme.light.DirectionalLight;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Skybox;
import com.jme.scene.Spatial;
import com.jme.scene.Spatial.TextureCombineMode;
import com.jme.scene.state.CullState;
import com.jme.scene.state.FogState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.TextureManager;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.NewFrameCondition;
import org.jdesktop.mtgame.ProcessorArmingCollection;
import org.jdesktop.mtgame.ProcessorComponent;
import org.jdesktop.mtgame.RenderUpdater;
import org.jdesktop.mtgame.SkyboxComponent;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.EnvironmentCell;
import org.jdesktop.wonderland.client.cell.TransformChangeListener;
import org.jdesktop.wonderland.client.cell.asset.AssetUtils;
import org.jdesktop.wonderland.client.cell.view.ViewCell;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.ViewManager;
import org.jdesktop.wonderland.client.jme.ViewManager.ViewManagerListener;
import org.jdesktop.wonderland.client.jme.cellrenderer.CellRendererJME;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.CellTransform;

/**
 * Renders the default environment, including lights and skybox. Adapted from
 * DefaultEnvironment.
 * @author paulby
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
public class DefaultEnvironmentRenderer implements CellRendererJME,
        ViewManagerListener, TransformChangeListener
{
    private static final Logger logger =
            Logger.getLogger(DefaultEnvironmentRenderer.class.getName());

    private final EnvironmentCell cell;

    private CellStatus status = CellStatus.DISK;

    private Skybox skybox = null;
    private Entity skyboxEntity = null;
    private Set<LightNode> globalLights = new HashSet<LightNode>();
    private Vector3f translation = new Vector3f();

    private ViewCell curViewCell = null;
    private SkyboxProcessor skyboxProcessor;

    public DefaultEnvironmentRenderer(Cell cell) {
        this.cell = (EnvironmentCell) cell;
    }

    public synchronized Entity getEntity() {
        if (skyboxEntity == null) {
            addGlobalLights();
            createSkybox();
        }
        
        return skyboxEntity;
    }

    public void cellTransformUpdate(CellTransform localTransform) {
        // ignore -- shouldn't happen
    }

    public void setStatus(CellStatus status, boolean increasing) {
        this.status = status;

        if (status == CellStatus.INACTIVE && increasing) {
            ClientContextJME.getWorldManager().addEntity(getEntity());
        } else if (status == CellStatus.INACTIVE && !increasing) {
            removeGlobalLights();
            removeSkybox();
        }
    }

    public CellStatus getStatus() {
        return status;
    }

    /**
     * Add global lights
     */
    protected void addGlobalLights() {
        LightNode globalLight1 = null;
        LightNode globalLight2 = null;
        LightNode globalLight3 = null;

        float radius = 75.0f;
        float lheight = 30.0f;
        float x = (float)(radius*Math.cos(Math.PI/6));
        float z = (float)(radius*Math.sin(Math.PI/6));
        globalLight1 = createLight(x, lheight, z);
        x = (float)(radius*Math.cos(5*Math.PI/6));
        z = (float)(radius*Math.sin(5*Math.PI/6));
        globalLight2 = createLight(x, lheight, z);
        x = (float)(radius*Math.cos(3*Math.PI/2));
        z = (float)(radius*Math.sin(3*Math.PI/2));
        globalLight3 = createLight(x, lheight, z);

        globalLights.add(globalLight1);
        globalLights.add(globalLight2);
        globalLights.add(globalLight3);

        ClientContextJME.getWorldManager().getRenderManager().addLight(globalLight1);
        ClientContextJME.getWorldManager().getRenderManager().addLight(globalLight2);
        ClientContextJME.getWorldManager().getRenderManager().addLight(globalLight3);
    }

    private LightNode createLight(float x, float y, float z) {
        LightNode lightNode = new LightNode();
        DirectionalLight light = new DirectionalLight();
        light.setDiffuse(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
        light.setAmbient(new ColorRGBA(0.1f, 0.1f, 0.1f, 1.0f));
        light.setSpecular(new ColorRGBA(0.4f, 0.4f, 0.4f, 1.0f));
        light.setEnabled(true);
        lightNode.setLight(light);
        lightNode.setLocalTranslation(x, y, z);
        light.setDirection(new Vector3f(-x, -y, -z));
        return (lightNode);
    }

    /**
     * @{inheritDoc}
     */
    protected void removeGlobalLights() {
        for (LightNode node : globalLights) {
            ClientContextJME.getWorldManager().getRenderManager().removeLight(node);
        }

        globalLights.clear();
    }

    /**
     * @{@inheritDoc}
     */
    protected void createSkybox() {
        logger.fine("[DefaultEnvironment] add skybox to " + this);

        if (skyboxEntity == null) {
            skyboxEntity = createSkyboxEntity();
        }

        ViewManager.getViewManager().addViewManagerListener(this);
    }

    public void removeSkybox() {
        logger.fine("[DefaultEnvironment] remove skybox from " + this +
                    " curViewCell: " + curViewCell);

        ClientContextJME.getWorldManager().removeEntity(skyboxEntity);
        ViewManager.getViewManager().removeViewManagerListener(this);

        if (curViewCell != null) {
            curViewCell.removeTransformChangeListener(this);
            curViewCell = null;
        }

        skyboxEntity = null;
        skybox = null;
    }

    public void primaryViewCellChanged(ViewCell oldViewCell, ViewCell newViewCell) {
        logger.fine("[DefaultEnvironment] primary view changed for " + this +
                    ".  Old: " + oldViewCell + " new: " + newViewCell);

        if (curViewCell != null) {
            curViewCell.removeTransformChangeListener(this);
        }

        //Keep the skybox centered on the view
        curViewCell = newViewCell;
        if (curViewCell!=null) {
            curViewCell.addTransformChangeListener(this);
            transformChanged(curViewCell, ChangeSource.LOCAL);
        }
    }

    public void transformChanged(Cell cell, ChangeSource source) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("[DefaultEnvironment] transform changed for " + this);
        }

        skyboxProcessor.viewMoved(cell.getWorldTransform().getTranslation(translation));

        ClientContextJME.getWorldManager().addRenderUpdater(new RenderUpdater(){
            public void update(Object arg0) {

            }
        }, cell);
    }

    private Entity createSkyboxEntity() {
        try {
            /* Form the asset URIs */
            URL northURL = AssetUtils.getAssetURL("wla://defaultenvironment/skybox1/1.jpg", cell);
            URL southURL = AssetUtils.getAssetURL("wla://defaultenvironment/skybox1/3.jpg", cell);
            URL eastURL = AssetUtils.getAssetURL("wla://defaultenvironment/skybox1/2.jpg", cell);
            URL westURL = AssetUtils.getAssetURL("wla://defaultenvironment/skybox1/4.jpg", cell);
            URL downURL = AssetUtils.getAssetURL("wla://defaultenvironment/skybox1/5.jpg", cell);
            URL upURL = AssetUtils.getAssetURL("wla://defaultenvironment/skybox1/6.jpg", cell);

            WorldManager wm = ClientContextJME.getWorldManager();
            skybox = new Skybox("skybox", 1000, 1000, 1000);
            Texture north = TextureManager.loadTexture(northURL, Texture.MinificationFilter.BilinearNearestMipMap, Texture.MagnificationFilter.Bilinear);
            Texture south = TextureManager.loadTexture(southURL, Texture.MinificationFilter.BilinearNearestMipMap, Texture.MagnificationFilter.Bilinear);
            Texture east = TextureManager.loadTexture(eastURL, Texture.MinificationFilter.BilinearNearestMipMap, Texture.MagnificationFilter.Bilinear);
            Texture west = TextureManager.loadTexture(westURL, Texture.MinificationFilter.BilinearNearestMipMap, Texture.MagnificationFilter.Bilinear);
            Texture up = TextureManager.loadTexture(upURL, Texture.MinificationFilter.BilinearNearestMipMap, Texture.MagnificationFilter.Bilinear);
            Texture down = TextureManager.loadTexture(downURL, Texture.MinificationFilter.BilinearNearestMipMap, Texture.MagnificationFilter.Bilinear);
            skybox.setTexture(Skybox.Face.North, north);
            skybox.setTexture(Skybox.Face.West, west);
            skybox.setTexture(Skybox.Face.South, south);
            skybox.setTexture(Skybox.Face.East, east);
            skybox.setTexture(Skybox.Face.Up, up);
            skybox.setTexture(Skybox.Face.Down, down);
            //skybox.preloadTextures();
            CullState cullState = (CullState) wm.getRenderManager().createRendererState(RenderState.RS_CULL);
            cullState.setEnabled(true);
            skybox.setRenderState(cullState);
            ZBufferState zState = (ZBufferState) wm.getRenderManager().createRendererState(RenderState.RS_ZBUFFER);
            //zState.setEnabled(false);
            skybox.setRenderState(zState);
            FogState fs = (FogState) wm.getRenderManager().createRendererState(RenderState.RS_FOG);
            fs.setEnabled(false);
            skybox.setRenderState(fs);
            skybox.setLightCombineMode(Spatial.LightCombineMode.Off);
            skybox.setCullHint(Spatial.CullHint.Never);
            skybox.setTextureCombineMode(TextureCombineMode.Replace);
            skybox.updateRenderState();
            skybox.lockBounds();
            //skybox.lockMeshes();
            Entity e = new Entity("Skybox");
//            e.addComponent(ProcessorComponent.class, new TextureAnimationProcessor(up));
            SkyboxComponent sbc = wm.getRenderManager().createSkyboxComponent(skybox, true);
            e.addComponent(SkyboxComponent.class, sbc);

            skyboxProcessor = new SkyboxProcessor(wm);
            e.addComponent(SkyboxProcessor.class, skyboxProcessor);

            return e;

        } catch (MalformedURLException ex) {
            Logger.getLogger(DefaultEnvironmentRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    class SkyboxProcessor extends ProcessorComponent {

        private Vector3f translation=new Vector3f();
        private boolean translationDirty = false;
        private final WorldManager worldManager;

        public SkyboxProcessor(WorldManager worldManager) {
            this.worldManager = worldManager;
        }

        @Override
        public void compute(ProcessorArmingCollection arg0) {
        }

        @Override
        public void commit(ProcessorArmingCollection arg0) {
            synchronized(translation) {
                if (translationDirty) {
                    skybox.setLocalTranslation(translation);
                    worldManager.addToUpdateList(skybox);
                    translationDirty = false;
                }
            }
        }

        @Override
        public void initialize() {
            setArmingCondition(new NewFrameCondition(this));
        }

        private void viewMoved(Vector3f translation) {
            synchronized(translation) {
                this.translation.set(translation);
                translationDirty = true;
            }
        }

    }

}
