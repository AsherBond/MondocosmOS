/**
 * Open Wonderland
 *
 * Copyright (c) 2011, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.client.jme.cellrenderer;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.state.CullState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.ZBufferState;
import com.jme.util.resource.ResourceLocator;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.mtgame.CollisionComponent;
import org.jdesktop.mtgame.CollisionSystem;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.JBulletCollisionComponent;
import org.jdesktop.mtgame.JBulletDynamicCollisionSystem;
import org.jdesktop.mtgame.JBulletPhysicsComponent;
import org.jdesktop.mtgame.JBulletPhysicsSystem;
import org.jdesktop.mtgame.JMECollisionSystem;
import org.jdesktop.mtgame.PhysicsComponent;
import org.jdesktop.mtgame.PhysicsSystem;
import org.jdesktop.mtgame.PostEventCondition;
import org.jdesktop.mtgame.ProcessorArmingCollection;
import org.jdesktop.mtgame.ProcessorComponent;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.mtgame.RenderUpdater;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.cell.CellComponent;
import org.jdesktop.wonderland.client.cell.component.CellPhysicsPropertiesComponent;
import org.jdesktop.wonderland.client.cell.CellRenderer;
import org.jdesktop.wonderland.client.cell.ComponentChangeListener;
import org.jdesktop.wonderland.client.cell.InteractionComponent;
import org.jdesktop.wonderland.client.cell.InteractionComponent.InteractionComponentListener;
import org.jdesktop.wonderland.client.cell.MovableComponent;
import org.jdesktop.wonderland.client.cell.asset.AssetUtils;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.jme.CellRefComponent;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.utils.traverser.ProcessNodeInterface;
import org.jdesktop.wonderland.client.jme.utils.traverser.TreeScan;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.component.state.PhysicsProperties;

/**
 *
 * Abstract Renderer class that implements CellRendererJME
 * 
 * @author paulby
 */
@ExperimentalAPI
public abstract class BasicRenderer implements CellRendererJME {
    
    protected static Logger logger = Logger.getLogger(BasicRenderer.class.getName());
    protected Cell cell;
    protected Entity entity;
    private Object entityLock=new Object();
    protected Node rootNode;
    protected Node sceneRoot;
    protected MoveProcessor moveProcessor = null;
    
    private static ZBufferState zbuf = null;

    private CellStatus status = CellStatus.DISK;

    private boolean collisionEnabled = true;
    private boolean pickingEnabled = true;
    private boolean lightingEnabled = true;
    private boolean backfaceCullingEnabled = true;

    private final CollisionListener collisionListener;

    static {
        zbuf = (ZBufferState) ClientContextJME.getWorldManager().getRenderManager().createRendererState(RenderState.RS_ZBUFFER);
        zbuf.setEnabled(true);
        zbuf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
    }
    
    public BasicRenderer(Cell cell) {
        this.cell = cell;

        // update the collidable property of this renderer based on the
        // cell's interaction component
        this.collisionListener = new CollisionListener(cell);
    }

    /**
     * Return the cell that contains this component
     * @return
     */
    public Cell getCell() {
        return cell;
    }

    public CellStatus getStatus() {
        return status;
    }

    public void setStatus(CellStatus status,boolean increasing) {
        this.status = status;
        switch(status) {
            case ACTIVE :
                if (increasing && cell!=null) {
                    Entity parentEntity= findParentEntity(cell.getParent());
                    Entity thisEntity = getEntity();

                    if (thisEntity==null) {
                        logger.severe("Got null entity for "+this.getClass().getName());
                        return;
                    }

                    thisEntity.addComponent(CellRefComponent.class, new CellRefComponent(cell));

                    if (parentEntity!=null) {
                        parentEntity.addEntity(thisEntity);
                    } else {
                        ClientContextJME.getWorldManager().addEntity(thisEntity);
                    }

                    // Figure out the correct parent entity for this cells entity.
                    if (parentEntity!=null && thisEntity!=null) {
                        RenderComponent parentRendComp = (RenderComponent) parentEntity.getComponent(RenderComponent.class);
                        RenderComponent thisRendComp = (RenderComponent)thisEntity.getComponent(RenderComponent.class);
                        if (parentRendComp!=null && parentRendComp.getSceneRoot()!=null && thisRendComp!=null) {
                            thisRendComp.setAttachPoint(parentRendComp.getSceneRoot());
                        }
                    }

                    // enable the collision listener
                    collisionListener.enable();
                } else {
                    logger.info("No Entity for Cell "+cell.getClass().getName());
                }
            break;
            case INACTIVE :
                if (!increasing) {
                    collisionListener.disable();

                    try {
                        Entity child = getEntity();
                        Entity parent = child.getParent();
                        if (parent!=null)
                            parent.removeEntity(child);
                        else
                            ClientContextJME.getWorldManager().removeEntity(child);
                        cleanupSceneGraph(child);
                    } catch(Exception e) {
                        System.err.println("NPE in "+this);
                        e.printStackTrace();
                    }
                }
                break;
        }

    }

    /**
     * Convenience method which attaches the child entity to the specified
     * parent AND sets the attachpoint of the childs RenderComponent to the
     * scene root of the parents RenderComponent
     * 
     * @param parentEntity
     * @param child
     */
    public static void entityAddChild(Entity parentEntity, Entity child) {
        if (parentEntity!=null && child!=null) {
            RenderComponent parentRendComp = (RenderComponent) parentEntity.getComponent(RenderComponent.class);
            RenderComponent thisRendComp = (RenderComponent)child.getComponent(RenderComponent.class);
            if (parentRendComp!=null && parentRendComp.getSceneRoot()!=null && thisRendComp!=null) {
                thisRendComp.setAttachPoint(parentRendComp.getSceneRoot());
            }
            parentEntity.addEntity(child);
        }
    }

    /**
     * Traverse up the cell hierarchy and return the first Entity
     * @param cell
     * @return
     */
    private Entity findParentEntity(Cell cell) {
        if (cell==null)
            return null;

        CellRenderer rend = cell.getCellRenderer(ClientContext.getRendererType());
        if (cell!=null && rend!=null) {
            if (rend instanceof CellRendererJME) {
//                    System.out.println("FOUND PARENT ENTITY on CELL "+cell.getName());
                return ((CellRendererJME)rend).getEntity();
            }
        }

        return findParentEntity(cell.getParent());
    }

    protected Entity createEntity() {
        Entity ret = new Entity(this.getClass().getName()+"_"+cell.getCellID());

        rootNode = new Node();
        rootNode.setName("CellRoot_"+cell.getCellID());
        sceneRoot = createSceneGraph(ret);
        rootNode.attachChild(sceneRoot);
        applyTransform(rootNode, cell.getLocalTransform());
        addRenderState(rootNode);

        addDefaultComponents(ret, rootNode);

        return ret;        
    }

    /**
     * Return the scene root, this is the node created by createSceneGraph.
     * The BasicRenderer also has a rootNode which contains the cell transform,
     * the rootNode is the parent of the scene root.
     * @return
     */
    public Node getSceneRoot() {
        return sceneRoot;
    }
    
    /**
     * Add the default renderstate to the root node. Override this method
     * if you want to apply a different RenderState
     * @param node
     */
    protected void addRenderState(Node node) {
        node.setRenderState(zbuf);
    }
    
    protected void addDefaultComponents(Entity entity, Node rootNode) {
        if (cell.getComponent(MovableComponent.class)!=null) {
            if (rootNode==null) {
                logger.warning("Cell is movable, but has no root node !");
            } else {           
                // The cell is movable so create a move processor
                moveProcessor = new MoveProcessor(ClientContextJME.getWorldManager(), rootNode);
                entity.addComponent(MoveProcessor.class, moveProcessor);
            }
        }

        if (rootNode!=null) {
            rootNode.updateWorldBound();

            // Some subclasses (like the imi collada renderer) already add
            // a render component
            RenderComponent rc = entity.getComponent(RenderComponent.class);
            if (rc==null) {
                rc = ClientContextJME.getWorldManager().getRenderManager().createRenderComponent(rootNode);
                entity.addComponent(RenderComponent.class, rc);
            } else {
                rc.setSceneRoot(rootNode);
            }

            // TODO: shouldn't this just be a call to adjustCollisionSystem()?
            WonderlandSession session = cell.getCellCache().getSession();
            CollisionSystem collisionSystem = ClientContextJME.getCollisionSystem(session.getSessionManager(), "Default");


            CollisionComponent cc=null;

            cc = setupCollision(collisionSystem, rootNode);
            if (cc!=null) {
                entity.addComponent(CollisionComponent.class, cc);
            }

            // set initial lighting
            adjustLighting(entity);

//            PhysicsSystem jBulletPhysicsSystem = ClientContextJME.getPhysicsSystem(session.getSessionManager(), "Physics");
//            CollisionSystem jBulletCollisionSystem = ClientContextJME.getCollisionSystem(session.getSessionManager(), "Physics");
//            if (jBulletPhysicsSystem!=null) {
//                CollisionComponent jBulletCollisionComponent = setupPhysicsCollision(jBulletCollisionSystem, rootNode);
//                PhysicsComponent pc = setupPhysics(jBulletCollisionComponent, jBulletPhysicsSystem, rootNode);
//                entity.addComponent(JBulletCollisionComponent.class, jBulletCollisionComponent);
//                entity.addComponent(JBulletPhysicsComponent.class, pc);
//            }
        } else {
            logger.warning("**** BASIC RENDERER - ROOT NODE WAS NULL !");
        }

    }


    protected CollisionComponent setupCollision(CollisionSystem collisionSystem, Node rootNode) {
        CollisionComponent cc=null;
        if (collisionSystem instanceof JMECollisionSystem) {
            cc = ((JMECollisionSystem)collisionSystem).createCollisionComponent(rootNode);
            cc.setCollidable(collisionEnabled);
            cc.setPickable(pickingEnabled);
        } else {
            logger.warning("Unsupported CollisionSystem "+collisionSystem);
        }

        return cc;
    }

    protected PhysicsComponent setupPhysics(CollisionComponent physicsCC, PhysicsSystem physicsSystem, Node rootNode) {
        if (physicsCC!=null && physicsCC instanceof JBulletCollisionComponent && physicsSystem instanceof JBulletPhysicsSystem) {
            JBulletPhysicsComponent pc = ((JBulletPhysicsSystem)physicsSystem).createPhysicsComponent((JBulletCollisionComponent)physicsCC);
            CellPhysicsPropertiesComponent prop = cell.getComponent(CellPhysicsPropertiesComponent.class);
            if (prop!=null) {
                PhysicsProperties phy = prop.getPhysicsProperties(CellPhysicsPropertiesComponent.DEFAULT_NAME);
                if (phy!=null) {
                    System.err.println("---------------> setting mass on "+this);
                    pc.setMass(phy.getMass());
                }
            }
            return pc;
        } else {
            logger.warning("Unsupported PhysicsSystem "+physicsSystem);
        }

        return null;
    }

    protected CollisionComponent setupPhysicsCollision(CollisionSystem collisionSystem, Node rootNode) {
        CollisionComponent cc=null;
        if (collisionSystem instanceof JBulletDynamicCollisionSystem) {
            cc = ((JBulletDynamicCollisionSystem)collisionSystem).createCollisionComponent(rootNode);
        } else {
            logger.warning("Unsupported CollisionSystem "+collisionSystem);
        }

        return cc;
    }

    /**
     * Create the scene graph. The node returned will have  default
     * components set to handle collision and rendering. The returned graph will
     * also automatically be positioned correctly with the cells transform. This
     * is achieved by adding the returned Node to a rootNode for this renderer which
     * automatically tracks the cells transform.
     * @return
     */
    protected abstract Node createSceneGraph(Entity entity);

    /**
     * Cleanup the scene graph, allowing resources to be gc'ed
     * TODO - should be abstract, but don't want to break compatability in 0.5 API
     *
     * @param entity
     */
    protected void cleanupSceneGraph(Entity entity) {

    }

    /**
     * Apply the transform to the jme node
     * @param node
     * @param transform
     */
    public static void applyTransform(Spatial node, CellTransform transform) {
        node.setLocalRotation(transform.getRotation(null));
        node.setLocalScale(transform.getScaling(null));
        node.setLocalTranslation(transform.getTranslation(null));
    }

    /**
     * Return the entity for this basic renderer. The first time this
     * method is called the entity will be created using createEntity()
     * @return
     */
    public Entity getEntity() {
        synchronized(entityLock) {
            logger.fine("Get Entity "+this.getClass().getName());
            if (entity==null)
                entity = createEntity();
        }
        return entity;
    }

    /**
     * Callback notifying the renderer that the cell transform has changed.
     * @param localTransform the new local transform of the cell
     */
    public void cellTransformUpdate(CellTransform localTransform) {
        // The fast-path case is if the move processor already exists, in
        // which case, we move the cell
        if (moveProcessor != null) {
            moveProcessor.cellMoved(localTransform);
            return;
        }

        // Otherwise, the move processor is null so we will attempt to add it
        // but only if there is a movable component on the cell.
        if (cell.getComponent(MovableComponent.class) != null && rootNode != null) {
            moveProcessor = new MoveProcessor(ClientContextJME.getWorldManager(), rootNode);
            getEntity().addComponent(MoveProcessor.class, moveProcessor);
            moveProcessor.cellMoved(localTransform);
        }
    }

    /**
     * Given a url, determine and return the full asset URL. This is a
     * convenience method that invokes methods on AssetUtils using the session
     * associated with the cell for this cell renderer
     * 
     * @param uri The asset URI
     * @return A URL representing the uri
     * @throws MalformedURLException Upon error forming the URL
     */
    protected URL getAssetURL(String uri) throws MalformedURLException {
        return AssetUtils.getAssetURL(uri, cell);
    }

    /**
     * @return the collisionEnabled
     */
    public boolean isCollisionEnabled() {
        return collisionEnabled;
    }

    /**
     * @param collisionEnabled the collisionEnabled to set
     */
    public void setCollisionEnabled(boolean collisionEnabled) {
        if (this.collisionEnabled == collisionEnabled)
            return;

        synchronized(entityLock) {
            this.collisionEnabled = collisionEnabled;

            if (entity!=null) {
                adjustCollisionSystem();
            }
        }
    }

    public void setPickingEnabled(boolean pickingEnabled) {
        if (this.pickingEnabled == pickingEnabled)
            return;

        synchronized(entityLock) {
            this.pickingEnabled = pickingEnabled;

            if (entity!=null) {
                adjustCollisionSystem();
            }
        }
    }

    /**
     * Adjust the collision system after a change to picking or collision
     */
    private void adjustCollisionSystem() {
        CollisionComponent cc = entity.getComponent(CollisionComponent.class);
        if (collisionEnabled==false && pickingEnabled==false && cc!=null)
            entity.removeComponent(CollisionComponent.class);

        if (cc==null) {
            WonderlandSession session = cell.getCellCache().getSession();
            CollisionSystem collisionSystem = ClientContextJME.getCollisionSystem(session.getSessionManager(), "Default");
            cc = setupCollision(collisionSystem, rootNode);
            entity.addComponent(CollisionComponent.class, cc);
            return;
        }

        cc.setCollidable(collisionEnabled);
        cc.setPickable(pickingEnabled);
    }

    public void setLightingEnabled(final boolean lightingEnabled) {
        if (this.lightingEnabled == lightingEnabled) {
            return;
        }

        this.lightingEnabled = lightingEnabled;

        if (entity != null) {
            adjustLighting(entity);
        }
    }

    public void setBackfaceCullingEnabled(boolean backfaceCullingEnabled) {
        if (this.backfaceCullingEnabled==backfaceCullingEnabled)
            return;

        this.backfaceCullingEnabled = backfaceCullingEnabled;

        if (entity!=null) {
            final RenderComponent rc = entity.getComponent(RenderComponent.class);
            final CullState.Face face = backfaceCullingEnabled ? CullState.Face.Back : CullState.Face.None;
            if (rc!=null && rc.getSceneRoot()!=null) {
                ClientContextJME.getWorldManager().addRenderUpdater(new RenderUpdater() {

                    public void update(Object arg0) {
                        TreeScan.findNode(rc.getSceneRoot(), new ProcessNodeInterface() {
                            public boolean processNode(Spatial node) {
                                CullState cs = (CullState) node.getRenderState(RenderState.RS_CULL);
                                if (cs==null) {
                                    cs = (CullState) ClientContextJME.getWorldManager().getRenderManager().createRendererState(RenderState.RS_CULL);
                                    node.setRenderState(cs);
                                }
                                cs.setCullFace(face);

                                return true;
                            }
                        });
                        ClientContextJME.getWorldManager().addToUpdateList(rc.getSceneRoot());
                    }
                }, null);
            }
        }
    }

    private void adjustLighting(final Entity entity) {
        RenderComponent rc = entity.getComponent(RenderComponent.class);
        rc.setLightingEnabled(lightingEnabled);
    }

    /**
     * JME Asset locator using WL Asset manager
     */
    public class AssetResourceLocator implements ResourceLocator {

        private String modulename;
        private String path;
        private String protocol;

        /**
         * Locate resources for the given file
         * @param url
         */
        public AssetResourceLocator(URL url) {
            // The modulename can either be in the "user info" field or the
            // "host" field. If "user info" is null, then use the host name.
//            System.out.println("ASSET RESOURCE LOCATOR FOR URL " + url.toExternalForm());

            if (url.getUserInfo() == null) {
                modulename = url.getHost();
            }
            else {
                modulename = url.getUserInfo();
            }
            path = url.getPath();
            path = path.substring(0, path.lastIndexOf('/')+1);
            protocol = url.getProtocol();

//            System.out.println("MODULE NAME " + modulename + " PATH " + path);
        }

        public URL locateResource(String resource) {
//            System.err.println("Looking for resource "+resource);
//            System.err.println("Module "+modulename+"  path "+path);
            try {
                if (resource.startsWith("/")) {
                    URL url = getAssetURL(protocol + "://"+modulename+resource);
//                    System.err.println("Using alternate "+url.toExternalForm());
                    return url;
                } else {
                    String urlStr = trimUrlStr(protocol + "://"+modulename+path + resource);

                    URL url = getAssetURL(urlStr);
//                    System.err.println("Using " + url.toExternalForm());
                    return url;
                }
            } catch (MalformedURLException ex) {
                logger.log(Level.SEVERE, "Unable to locateResource "+resource, ex);
                return null;
            }
        }

        /**
         * Trim ../ from url
         * @param urlStr
         */
        private String trimUrlStr(String urlStr) {
            int pos = urlStr.indexOf("/../");
            if (pos==-1)
                return urlStr;

            StringBuilder buf = new StringBuilder(urlStr);
            int start = pos;
            while(buf.charAt(--start)!='/') {}
            buf.replace(start, pos+4, "/");
//            System.out.println("Trimmed "+buf.toString());

           return buf.toString();
        }

    }

    /**
     * An mtgame ProcessorCompoenent to process cell moves.
     */
    public class MoveProcessor extends ProcessorComponent {

        private CellTransform cellTransform;
        private boolean dirty = false;
        private Node node;
        private WorldManager worldManager;
        private Vector3f tmpV3f = new Vector3f();
        private Vector3f tmp2V3f = new Vector3f();
        private Quaternion tmpQuat = new Quaternion();

        private final long postId = ClientContextJME.getWorldManager().allocateEvent();

        private PostEventCondition postCondition = new PostEventCondition(this, new long[] {postId});
        
        public MoveProcessor(WorldManager worldManager, Node node) {
            this.node = node;
            this.worldManager = worldManager;
        }
        
        @Override
        public void compute(ProcessorArmingCollection arg0) {
        }

        @Override
        public void commit(ProcessorArmingCollection arg0) {
            // The dirty flag is important for Avatars, as we chain
            // the moveProcessor to the avatarcontrol which update per frame
            // This needs breaking out at some point in the future

            synchronized(this) {
                if (dirty) {
//                    System.err.println("BasicRenderer.cellMoved "+node.getLocalTranslation()+"  "+cellTransform.getTranslation(null));
                    node.setLocalTranslation(cellTransform.getTranslation(tmpV3f));
                    node.setLocalRotation(cellTransform.getRotation(tmpQuat));
                    node.setLocalScale(cellTransform.getScaling(tmp2V3f));
                    dirty = false;
                    worldManager.addToUpdateList(node);
//            System.err.println("--------------------------------");
                }
            }

            // Clear the triggering events
            if (arg0.size() != 0) {
               PostEventCondition pec = (PostEventCondition)arg0.get(0);
               pec.getTriggerEvents();
            }
        }

        @Override        
        public void initialize() {
            setArmingCondition(postCondition);
        }

        /**
         * Notify the MoveProcessor that the cell has moved
         * 
         * @param transform cell transform in world coordinates
         */
        public void cellMoved(CellTransform transform) {
            synchronized(this) {
                this.cellTransform = transform;
                dirty = true;
//                System.err.println("CellMoved "+postId);
                ClientContextJME.getWorldManager().postEvent(postId);
            }
        }

        @Override
        protected void finalize() {
            ClientContextJME.getWorldManager().freeEvent(postId);
        }
    }

    private class CollisionListener
            implements ComponentChangeListener, InteractionComponentListener
    {
        private final Cell cell;
        private InteractionComponent ic;

        public CollisionListener(Cell cell) {
            this.cell = cell;
        }

        public void enable() {
            cell.addComponentChangeListener(this);
            setInteractionComponent(cell.getComponent(InteractionComponent.class));
        }

        public void disable() {
            cell.removeComponentChangeListener(this);
            setInteractionComponent(null);
        }

        public void componentChanged(Cell cell, ChangeType type, CellComponent component) {
            if (component instanceof InteractionComponent) {
                switch (type) {
                    case ADDED:
                        setInteractionComponent((InteractionComponent) component);
                        break;
                    case REMOVED:
                        setInteractionComponent(null);
                        break;
                }
            }
        }

        public void collidableChanged(boolean collidable) {
            setCollisionEnabled(collidable);
        }

        public void selectableChanged(boolean selectable) {
            // ignore
        }

        private void setInteractionComponent(InteractionComponent ic) {
            if (this.ic != null) {
                this.ic.removeInteractionComponentListener(this);
            }

            this.ic = ic;

            if (ic != null) {
                ic.addInteractionComponentListener(this);
                setCollisionEnabled(ic.isCollidable());
            } else {
                // if there is no interaction component, set collision to
                // the default, true
                setCollisionEnabled(true);
            }
        }

    }
}
