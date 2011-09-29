/**
 * Open Wonderland
 *
 * Copyright (c) 2010 - 2011, Open Wonderland Foundation, All Rights Reserved
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
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., All Rights Reserved
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
package org.jdesktop.wonderland.client.jme.cellrenderer;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.mtgame.RenderComponent.AttachPointNode;
import org.jdesktop.mtgame.RenderUpdater;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellStatistics.TimeCellStat;
import org.jdesktop.wonderland.client.cell.ModelCellComponent;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.artimport.DeployedModel;
import org.jdesktop.wonderland.client.jme.artimport.LoaderManager;
import org.jdesktop.wonderland.client.jme.artimport.ModelLoader;
import org.jdesktop.wonderland.client.jme.utils.traverser.ProcessNodeInterface;
import org.jdesktop.wonderland.client.jme.utils.traverser.TreeScan;
import org.jdesktop.wonderland.common.cell.state.ModelCellComponentServerState.TransparencyMode;

/**
 *
 * @author paulby
 */
public class ModelRenderer extends BasicRenderer {
    private URL deployedModelURL;
    private Vector3f modelTranslation = null;
    private Quaternion modelRotation = null;
    private Vector3f modelScale = null;

    private ModelCellComponent modelComponent = null;
    private DeployedModel deployedModel = null;

    private TransparencyMode transparency = null;

    public ModelRenderer(Cell cell, URL deployedModelURL) {
        this(cell, deployedModelURL, null, null, null, null);
    }

    public ModelRenderer(Cell cell, ModelCellComponent modelComponent) {
        this(cell, null, modelComponent);
    }

    public ModelRenderer(Cell cell, DeployedModel deployedModel) {
        this(cell, deployedModel, null);
    }

    public ModelRenderer(Cell cell, DeployedModel deployedModel, ModelCellComponent modelComponent) {
        super(cell);
        this.deployedModel = deployedModel;
        this.modelComponent = modelComponent;
    }

    public ModelRenderer(Cell cell,
            URL deployedModelURL,
            Vector3f modelTranslation,
            Quaternion modelRotation,
            Vector3f modelScale,
            Map<String, Object> properties) {
        super(cell);
        this.deployedModelURL = deployedModelURL;
        this.modelTranslation = modelTranslation;
        this.modelRotation = modelRotation;
        this.modelScale = modelScale;
    }

    @Override
    protected Node createSceneGraph(Entity entity) {
        long startTime = System.currentTimeMillis();
        TimeCellStat loadTime = new TimeCellStat("loadtime", "Model Load Time");
        
        try {
            if (modelComponent!=null) {
                return modelComponent.loadModel(entity);
            }

            if (deployedModel!=null) {
                ModelLoader loader = deployedModel.getModelLoader();
                if (loader==null) {
                    logger.warning("No loader for model "+deployedModel.getModelURL());
                    return new Node("No Loader");
                }
                Node ret = loader.loadDeployedModel(deployedModel, entity);
                return ret;
            }

            ModelLoader loader = LoaderManager.getLoaderManager().getLoader(deployedModelURL);
            if (loader==null) {
                logger.warning("No loader for model "+deployedModel.getModelURL());
                return new Node("No Loader");
            }
            deployedModel = new DeployedModel(deployedModelURL, loader);
            deployedModel.setModelTranslation(modelTranslation);
            deployedModel.setModelRotation(modelRotation);
            deployedModel.setModelScale(modelScale);

            return loader.loadDeployedModel(deployedModel, entity);
        } finally {
            // record statistics
            loadTime.setValue(System.currentTimeMillis() - startTime);
            getCell().getCellCache().getStatistics().add(getCell(), loadTime);
        }
    }

    @Override
    protected void addDefaultComponents(Entity entity, Node rootNode) {
        super.addDefaultComponents(entity, rootNode);

        // update transparency now that the scene root is set
        if (transparency != null) {
            applyTransparency(TransparencyMode.DEFAULT, transparency);
        }
    }

    /**
     * For the renderer to reload the scene graph. This is required if the user
     * changes properties of the graph, such as optimization levels. The detach of
     * the current graph and loading of the new graph are done asynchronously, this
     * method returns immediately.
     */
    public void reload() {
        if (sceneRoot!=null) {
            ClientContextJME.getWorldManager().addRenderUpdater(new RenderUpdater() {
                public void update(Object arg0) {
                    ((Node)arg0).removeFromParent();

                    // TODO DONT do the load (createSceneGraph) on the render thread
                    sceneRoot = createSceneGraph(entity);
                    rootNode.attachChild(sceneRoot);
                    ClientContextJME.getWorldManager().addToUpdateList(rootNode);
                }
            }, sceneRoot);
        }
    }

    /**
     * Change the transparency properties of the model
     * @param transparency the new transparency
     */
    public void setTransparency(TransparencyMode transparency) {
        if (this.transparency == transparency) {
            // ignore
            return;
        }

        TransparencyMode old = this.transparency;
        this.transparency = transparency;

        if (sceneRoot != null) {
            applyTransparency(old, transparency);
        }
    }

    protected void applyTransparency(TransparencyMode oldTM,
                                     TransparencyMode newTM)
    {
        final Transparency oldTrans = Transparency.get(oldTM);
        final Transparency newTrans = Transparency.get(newTM);

        // make the change on the render thread
        ClientContextJME.getWorldManager().addRenderUpdater(new RenderUpdater() {
            public void update(Object arg0) {
                TreeScan.findNode((Spatial) arg0, new ProcessNodeInterface() {
                    public boolean processNode(Spatial node) {
                        // stop if we cross an entity barier
                        if (node instanceof AttachPointNode) {
                            return false;
                        }

                        // load the blend state (if any)
                        BlendState as = (BlendState) node.getRenderState(RenderState.StateType.Blend);
                        if (as != null) {
                            // unapply the old state (getting us back to base state)
                            oldTrans.unapply(node, as);

                            // apply the new state
                            newTrans.apply(node, as);
                        }

                        return true;
                    }
                });
            }
        }, sceneRoot);
    }

    @Override
    protected void cleanupSceneGraph(Entity entity) {
        RenderComponent rc = entity.getComponent(RenderComponent.class);
        if (rc!=null) {
            ClientContextJME.getWorldManager().addRenderUpdater(new RenderUpdater() {

                public void update(Object arg0) {
                    TreeScan.findNode((Spatial) arg0, new ProcessNodeInterface() {
                        public boolean processNode(Spatial node) {
                            if (node instanceof Geometry) {
                                ((Geometry)node).clearBuffers();
                                TextureState ts = (TextureState) node.getRenderState(RenderState.RS_TEXTURE);
                                // deleteAll is too aggressive, it deletes other copies of the same texture
//                                if (ts!=null)
//                                    ts.deleteAll(false);
                            }
                            return true;
                        }
                    });
                }
            }, rc.getSceneRoot());
        }

        deployedModel = null;
    }


    private enum Transparency {
        DEFAULT,
        INVERSE {
            @Override
            public void apply(Spatial target, BlendState as) {
                ColorRGBA c = as.getConstantColor();
                if (c != null) {
                    c.set(1.0f - c.r, 1.0f - c.g, 1.0f - c.b, 1.0f - c.a);
                    as.setConstantColor(c);
                }
            }

            @Override
            public void unapply(Spatial target, BlendState as) {
                // apply will reverse the transparency, bringing it back
                // to the original level
                apply(target, as);
            }
        },
        NONE {
            @Override
            public void apply(Spatial target, BlendState as) {
                // disable blending & transparency
                as.setEnabled(false);
                target.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
            }

            @Override
            public void unapply(Spatial target, BlendState as) {
                // enable blending & transparency
                as.setEnabled(true);
                target.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
            }
        };

        // default does nothing -- we are already in the start state
        void apply(Spatial target, BlendState as) {}
        void unapply(Spatial target, BlendState as) {}

        static Transparency get(TransparencyMode mode) {
            switch (mode) {
                case INVERSE:
                    return Transparency.INVERSE;
                case NONE:
                    return Transparency.NONE;
                default:
                    return Transparency.DEFAULT;
            }
        }
    };
}
