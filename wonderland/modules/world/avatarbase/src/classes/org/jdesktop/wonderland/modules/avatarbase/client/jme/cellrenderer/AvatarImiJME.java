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
package org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer;

import org.jdesktop.wonderland.client.jme.input.AvatarCollisionEvent;
import com.jme.bounding.BoundingSphere;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import org.jdesktop.mtgame.JMECollisionSystem;
import org.jdesktop.wonderland.client.cell.MovableComponent.CellMoveSource;
import org.jdesktop.wonderland.client.jme.cellrenderer.*;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.Geometry;
import com.jme.scene.Spatial.CullHint;
import com.jme.scene.shape.Box;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.ZBufferState;
import imi.character.CharacterAnimationProcessor;
import imi.character.CharacterMotionListener;
import imi.character.CharacterProcessor;
import imi.character.avatar.Avatar;
import imi.character.avatar.AvatarController;
import imi.character.avatar.AvatarCollisionListener;
import imi.character.statemachine.GameContextListener;
import imi.character.statemachine.GameState;
import imi.character.statemachine.corestates.CycleActionState;
import imi.collision.CollisionController;
import imi.input.DefaultCharacterControls;
import imi.scene.PMatrix;
import imi.scene.PTransform;
import java.lang.ref.WeakReference;
import java.util.List;
import javolution.util.FastList;
import org.jdesktop.mtgame.CollisionComponent;
import org.jdesktop.mtgame.CollisionInfo;
import org.jdesktop.mtgame.CollisionSystem;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.JMECollisionDetails;
import org.jdesktop.mtgame.ProcessorCollectionComponent;
import org.jdesktop.mtgame.ProcessorComponent;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.mtgame.RenderManager;
import org.jdesktop.mtgame.RenderUpdater;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.mtgame.processor.WorkProcessor.WorkCommit;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.cell.MovableAvatarComponent;
import org.jdesktop.wonderland.client.cell.MovableComponent;
import org.jdesktop.wonderland.client.cell.MovableComponent.CellMoveListener;
import org.jdesktop.wonderland.client.cell.view.AvatarCell;
import org.jdesktop.wonderland.client.cell.view.AvatarCell.AvatarActionTrigger;
import org.jdesktop.wonderland.client.cell.view.ViewCell;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.input.EventClassListener;
import org.jdesktop.wonderland.client.jme.SceneWorker;
import org.jdesktop.wonderland.client.jme.ViewManager;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.Math3DUtils;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.modules.avatarbase.client.cell.AvatarConfigComponent;
import org.jdesktop.wonderland.modules.avatarbase.client.cell.AvatarConfigComponent.AvatarConfigChangeListener;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.ImiPickGeometry.PickBox;
import org.jdesktop.wonderland.modules.avatarbase.client.loader.AvatarLoaderRegistry;
import org.jdesktop.wonderland.modules.avatarbase.client.loader.spi.AvatarLoaderFactorySPI;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.AvatarConfigInfo;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.messages.AvatarConfigMessage;


/**
 * Cell renderer for Avatars, using the IMI avatar system.
 * 
 * @author paulby
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
public class AvatarImiJME extends BasicRenderer implements AvatarActionTrigger {

    private WlAvatarCharacter avatarCharacter = null;

    private boolean selectedForInput = false;

//    private AvatarRendererChangeRequestEvent.AvatarQuality quality = AvatarRendererChangeRequestEvent.AvatarQuality.High;
    private CharacterMotionListener characterMotionListener;
    private GameContextListener gameContextListener;
    private int currentTrigger = -1;
    private boolean currentPressed = false;
    private float positionMinDistanceForPull = 0.1f;
    private float positionMaxDistanceForPull = 3.0f;
    private String username;
    private DefaultCharacterControls controlScheme = null;

    private boolean pickable = true;
    private PickGeometry pickGeometry;

    private ProcessorComponent cameraChainedProcessor = null;  // The processor to which the camera is chained

    private CellMoveListener cellMoveListener = null;

    private Entity rootEntity = null;

    private CollisionChangeRequestListener collisionChangeRequestListener;
    private AvatarCollisionListener collisionListener = null;
    
    /** Collection of listeners **/
    private final List<WeakReference<AvatarChangedListener>> avatarChangedListeners = new FastList();

    private AvatarUIEventListener avatarUIEventListener;

    public AvatarImiJME(Cell cell) {
        super(cell);
        assert (cell != null);
        final Cell c = cell;

        // Listen for avatar configuration changes.
        AvatarConfigComponent comp = cell.getComponent(AvatarConfigComponent.class);
        comp.addAvatarConfigChangeListener(new AvatarChangeListener());

        // XXX NPC HACK XXX
        if (cell instanceof AvatarCell)
            username = ((AvatarCell) cell).getIdentity().getUsername();
        else
            username = "npc"; // HACK !

        characterMotionListener = new CharacterMotionListener() {
            Vector3f prevTrans;
            PMatrix prevRot;
            float prevHeight;
            boolean prevCollision;
            
            public void transformUpdate(Vector3f translation, PMatrix rotation) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest("Transform update: translation: prev: " +
                            prevTrans + " cur: " + translation +
                            " rotation: prev: " + prevRot + " cur: " +
                            rotation);
                }
                
                float height = avatarCharacter.getController().getHeight();
                boolean collision = avatarCharacter.getController().isColliding();
             
                if (prevTrans == null || !Math3DUtils.epsilonEquals(prevTrans, translation, 0.001f) ||
                    prevRot == null || !prevRot.epsilonEquals(rotation, 0.001f) ||
                    !Math3DUtils.epsilonEquals(prevHeight, height, 0.001f) ||
                    prevCollision != collision)
                {                    
                    MovableAvatarComponent mac = ((MovableAvatarComponent) c.getComponent(MovableComponent.class));
                    mac.localMoveRequest(new CellTransform(rotation.getRotation(), translation), height, collision);

                    prevTrans = translation.clone();
                    prevRot = new PMatrix(rotation);
                    prevHeight = height;
                    prevCollision = collision;
                }
            };   
        };

        // This info will be sent to the other clients to animate the avatar
        gameContextListener = new GameContextListener() {

            public void trigger(boolean pressed, int trigger, Vector3f translation, Quaternion rotation) {
                synchronized (this) {
                    currentTrigger = trigger;
                    currentPressed = pressed;
                }
                
                GameState state = avatarCharacter.getContext().getCurrentState();
                String animationName=null;
                if (state instanceof CycleActionState) {
                    animationName = avatarCharacter.getContext().getState(CycleActionState.class).getAnimationName();
                }
                
                float height = avatarCharacter.getController().getHeight();
                boolean collision = avatarCharacter.getController().isColliding();
                
                if (c.getComponent(MovableComponent.class)==null) {
                    logger.warning("!!!! NULL MovableComponent");
                } else {
                    MovableAvatarComponent mac = ((MovableAvatarComponent) c.getComponent(MovableComponent.class));
                    mac.localMoveRequest(new CellTransform(rotation, translation), 
                                         trigger, pressed, animationName, 
                                         height, collision, null);
                
                }
            }
        };



    }




    /**
     * Returns the avatar renderer for the primary view cell, or null if none
     * exists.
     *
     * @return An instance of this class that is the avatar cell renderer
     */
    public static AvatarImiJME getPrimaryAvatarRenderer() {
        // Fetch the primary view cell, make sure it is an avatar and then get
        // its cell renderer.
        ViewCell cell = ClientContextJME.getViewManager().getPrimaryViewCell();
        if (cell instanceof AvatarCell) {
            AvatarCell avatarCell = (AvatarCell) cell;
            return (AvatarImiJME) avatarCell.getCellRenderer(ClientContext.getRendererType());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStatus(CellStatus status, boolean increasing) {
        super.setStatus(status, increasing);
        WlAvatarCharacter pendingAvatar = null;

        logger.info("AVATAR RENDERER STATUS " + status + " DIR " + increasing);
        
        // If we are increasing to the ACTIVE state, then turn everything on.
        // Add the listeners to the avatar Cell and set the avatar character
        if (status == CellStatus.ACTIVE && increasing == true) {
            BoundsDebugger.getInstance().add(this);
            if (cellMoveListener != null) {
                // mc should not be null, but sometimes it seems to be
                MovableComponent mc = cell.getComponent(MovableComponent.class);
                if (mc==null) {
                    logger.severe("NULL MovableComponent in avatar "+((AvatarCell)cell).getName());
                } else {
                    mc.removeServerCellMoveListener(cellMoveListener);
                }
                cellMoveListener = null;
            }

            // If we have not creating the avatar yet, then look for the config
            // component on the cell. Fetch the avatar configuration.
            if (avatarCharacter == null) {
                AvatarConfigComponent configComp = cell.getComponent(AvatarConfigComponent.class);
                AvatarConfigInfo avatarConfigInfo = null;
                if (configComp != null) {
                    avatarConfigInfo = configComp.getAvatarConfigInfo();
                }
                logger.info("LOADING AVATAR FOR " + avatarConfigInfo);
                pendingAvatar = loadAvatar(avatarConfigInfo);
            }
            else {
                // Otherwise remove the existing avatar from the world
                ClientContextJME.getWorldManager().removeEntity(avatarCharacter);
                pendingAvatar = null;
            }

            // Go ahead and change the avatar
            logger.info("CHANGING AVATAR IN SET STATUS");
            changeAvatar(pendingAvatar);

            if (cellMoveListener == null) {
                cellMoveListener = new CellMoveListener() {
                    public void cellMoved(CellTransform transform, CellMoveSource source) {
                        if (source == CellMoveSource.REMOTE) {
                            //                            System.err.println("REMOTE MOVE "+transform.getTranslation(null));
                            if (avatarCharacter != null) {
                                if (avatarCharacter.getModelInst() == null) {  // Extra debug check
                                    logger.severe("MODEL INST IS NULL !");
                                    Thread.dumpStack();
                                    return;
                                }
                                avatarCharacter.getModelInst().setTransform(new PTransform(transform.getRotation(null), transform.getTranslation(null), new Vector3f(1, 1, 1)));
                            }
                        }
                    }
                };
            }
            cell.getComponent(MovableComponent.class).addServerCellMoveListener(cellMoveListener);

            avatarUIEventListener = new AvatarUIEventListener();
            ClientContext.getInputManager().addGlobalEventListener(avatarUIEventListener);

            collisionChangeRequestListener = new CollisionChangeRequestListener();
            ClientContext.getInputManager().addGlobalEventListener(collisionChangeRequestListener);
        } else if (status==CellStatus.DISK && !increasing) {
            BoundsDebugger.getInstance().remove(this);
            ClientContext.getInputManager().removeGlobalEventListener(avatarUIEventListener);
            ClientContext.getInputManager().removeGlobalEventListener(collisionChangeRequestListener);
            cell.getComponent(MovableComponent.class).removeServerCellMoveListener(cellMoveListener);
            avatarUIEventListener=null;
            cellMoveListener=null;
            collisionChangeRequestListener=null;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Entity createEntity() {
        assert (rootEntity == null);
        rootEntity = new Entity("AvatarRoot");
        return rootEntity;
    }

    private void handleAvatarRendererChangeRequest(AvatarRendererChangeRequestEvent event) {
        switch (event.getQuality()) {
            case High :
                // Fetch the avatar configuration information and change to the
                // current avatar
                AvatarConfigComponent comp = cell.getComponent(AvatarConfigComponent.class);
                AvatarConfigInfo avatarConfigInfo = comp.getAvatarConfigInfo();
                changeAvatar(loadAvatar(avatarConfigInfo));
                break;
            case Medium :
                changeAvatar(loadAvatar(null));
                break;
            case Low :
                changeAvatar(loadAvatar(null));
                break;
        }
    }

    /**
     * Changes the avatar to the given avatar on the MT Game Render Thread
     */
    public void changeAvatar(final WlAvatarCharacter avatar) {
        RenderUpdater updater = new RenderUpdater() {
            public void update(Object arg0) {
                changeAvatarInternal(avatar);
            }
        };
        WorldManager wm = ClientContextJME.getWorldManager();
        wm.addRenderUpdater(updater, null);
    }

    /**
     * Change the current avatar to the given avatar.
     *
     * NOTE: This method must be called in the MT Game Render Thread. As such,
     * we assume only one of these methods is called at a time.
     *
     * @param newAvatar The new avatar to change to.
     */
    private void changeAvatarInternal(WlAvatarCharacter newAvatar) {

        if (newAvatar==null)
            return;

        // Turn on an indication that the avatar is being loaded
        LoadingInfo.startedLoading(cell.getCellID(), newAvatar.getName());

        // Fetch the name tag node. There should be only one of these in the
        // system.
        Node nameTagNode = getNameTagNode();

        // If there is an existing avatar character, then remove it, but store
        // away its position. Remove the name tag, turn off input and destroy
        // the avatar character.
        PMatrix currentLocation = null;
        if (avatarCharacter != null) {
            currentLocation = avatarCharacter.getModelInst().getTransform().getWorldMatrix(true);
            rootEntity.removeEntity(avatarCharacter);
            avatarCharacter.getJScene().getExternalKidsRoot().detachChild(nameTagNode);
            selectForInput(false);
            avatarCharacter.destroy();
        }

        // Set the new avatar character. If there is none (when would that happen?)
        // then just return.
        avatarCharacter = newAvatar;
        if (newAvatar == null) {
            return;
        }

        // Add all of the default components to the renderer, but remove the
        // collision component, since we use our own collision graph
        RenderComponent rc = (RenderComponent) avatarCharacter.getComponent(RenderComponent.class);
        addDefaultComponents(avatarCharacter, rc.getSceneRoot());
        avatarCharacter.removeComponent(CollisionComponent.class);

        // Set the initial location of the avatar if there is one
        if (currentLocation != null && avatarCharacter.getModelInst() != null) {
            avatarCharacter.getModelInst().setTransform(new PTransform(currentLocation));
        }

        // Attach the name tag to the new avatar and add the avatar entity to
        // the cell renderer root entity and turn on input.
        Node externalRoot = avatarCharacter.getJScene().getExternalKidsRoot();
        if (nameTagNode!=null) {
            externalRoot.attachChild(nameTagNode);
            externalRoot.setModelBound(new BoundingSphere());
            externalRoot.updateModelBound();
            externalRoot.updateGeometricState(0, true);
        }
        rootEntity.addEntity(avatarCharacter);

        // Turn on input handle for the renderer, if we wish. Check for AvatarCell
        // to allow NPC's to work
        if (cell instanceof AvatarCell) {
            selectForInput(((AvatarCell) cell).isSelectedForInput());
        }

        // Notify listeners that the avatar has changed.
        for (WeakReference<AvatarChangedListener> listenerRef : avatarChangedListeners) {
            AvatarChangedListener listener = listenerRef.get();
            if (listener != null) {
                listener.avatarChanged(avatarCharacter);
            }
            else {
                avatarChangedListeners.remove(listenerRef);
            }
        }

        // update the bounds if necessary
        if (avatarCharacter.getJScene() != null) {
            avatarCharacter.getPScene().submitTransformsAndGeometry(true);
            avatarCharacter.getJScene().setModelBound(new BoundingSphere());
            avatarCharacter.getJScene().updateModelBound();
            avatarCharacter.getJScene().updateWorldBound();
        }

        // Update pick geometry
        updatePickGeometry();

        // Turn off the indication that we have finished loading
        LoadingInfo.finishedLoading(cell.getCellID(), newAvatar.getName());
    }

    public boolean isPickable() {
        return pickable;
    }

    public void setPickable(boolean pickable) {
        this.pickable = pickable;

        // schedule a task to update the pick geometry
        SceneWorker.addWorker(new WorkCommit() {
            public void commit() {
                updatePickGeometry();
            }
        });
    }

    public PickGeometry getPickGeometry() {
        return pickGeometry;
    }

    protected void updatePickGeometry() {
        // clean up any existing pick geometry
        if (pickGeometry != null) {
            pickGeometry.detach();
            pickGeometry = null;
        }

        // make sure we are pickable and ready to add geometry
        if (!isPickable() || avatarCharacter.getJScene() == null) {
            return;
        }

        if (avatarCharacter.getSkeleton() != null) {
            // use pick geometry for an IMI avatar
            boolean isMale = avatarCharacter.getCharacterParams().isMale();
            PickBox[] pickBoxes = getPickBoxes(isMale);

            pickGeometry = new ImiPickGeometry(cell.getName(), cell,
                                               AvatarImiJME.this, pickBoxes);
        } else if (avatarCharacter.getSimpleStaticGeometry() != null) {
            pickGeometry = new BasicPickGeometry(cell.getName(), cell, this,
                                                 avatarCharacter.getSimpleStaticGeometry());
        }
    }

    protected PickBox[] getPickBoxes(boolean isMale) {
        return ImiPickGeometry.getDefaultGeometry(isMale);
    }

    @Override
    protected void addRenderState(Node node) {
        // Nothing to do
    }

    @Override
    public void cellTransformUpdate(CellTransform transform) {
        // Don't call super, we don't use a MoveProcessor for avatars

        if (!selectedForInput && avatarCharacter != null && avatarCharacter.getContext().getController().getModelInstance()!=null ) {
            // If the user is being steered by AI, do not mess it up
            // (objects that the AI is dealing with gota be synced)
//            System.err.println("Steering "+avatarCharacter.getContext().getSteering().isEnabled()+"  "+avatarCharacter.getContext().getSteering().getCurrentTask());
            if (avatarCharacter.getContext().getBehaviorManager().isEnabled()
                    && avatarCharacter.getContext().getBehaviorManager().getCurrentTask() != null) {
            } else {
                Vector3f pos = transform.getTranslation(null);
                Vector3f dir = new Vector3f(0, 0, -1);
                transform.getRotation(null).multLocal(dir);
//                System.err.println("Setting pos "+pos);
                PMatrix local = avatarCharacter.getContext().getController().getModelInstance().getTransform().getLocalMatrix(true);
                final Vector3f currentPosition = local.getTranslation();
                float currentDistance = currentPosition.distance(pos);
                if (currentDistance < positionMaxDistanceForPull) {
                    pos.set(currentPosition);
                }

            }
        }
    }

    public void loadAndChangeAvatar(final AvatarConfigInfo avatarConfigInfo) {
        logger.info("Loading avatar info");
        WlAvatarCharacter avatar = loadAvatar(avatarConfigInfo);
        logger.info("Changing avatar character");
        changeAvatar(avatar);
        logger.info("Done changing avatar character");
    }

    /**
     * Load and return an avatar given its configuration information.
     *
     * @param avatarConfigInfo The avatar configuration info
     * @return The Avatar character
     */
    private WlAvatarCharacter loadAvatar(AvatarConfigInfo avatarConfigInfo) {
        // Load the avatar configuration information, placing a loading
        // message until it is finished
        LoadingInfo.startedLoading(cell.getCellID(), username);
        try {
            if (avatarConfigInfo != null) {
                logger.info("Loading avatar with config info url " +
                        avatarConfigInfo.getAvatarConfigURL() + " with loader " +
                        avatarConfigInfo.getLoaderFactoryClassName());
            }
            else {
                logger.info("Loading default avatar.");
            }

            return loadAvatarInternal(avatarConfigInfo);
        } catch (java.lang.Exception excp) {
            // Loger and error and return null
            String url = avatarConfigInfo == null ? "null" : avatarConfigInfo.getAvatarConfigURL();
            logger.log(Level.WARNING, "Failed to load avatar character for " +
                    "url " + url, excp);
            return null;
        } finally {
            LoadingInfo.finishedLoading(cell.getCellID(), username);
        }
    }

    /**
     * Load and return the avatar. To make this the current avatar changeAvatar()
     * must be called
     * 
     * @param avatarConfigURL
     * @return
     */
    private WlAvatarCharacter loadAvatarInternal(AvatarConfigInfo avatarConfigInfo)
            throws MalformedURLException, IOException {
        
        WlAvatarCharacter ret = null;
        PMatrix origin = new PMatrix();
        CellTransform transform = cell.getLocalTransform();
        origin.setTranslation(transform.getTranslation(null));
        origin.setRotation(transform.getRotation(null));

        // Create the character
        String avatarDetail = System.getProperty("avatar.detail", "high");
        
        // check if we support high-quality avatars
        if (!supportsHighQualityAvatars()) {
            logger.warning("Forcing low detail.");
            avatarDetail = "low";
        }

        // Check to see if there is no avatar configuration info and/or if we
        // have the avatar details set to "low". If so, then use the default
        AvatarLoaderRegistry registry = AvatarLoaderRegistry.getAvatarLoaderRegistry();
        if (avatarConfigInfo == null || avatarDetail.equalsIgnoreCase("low")) {

            // Find the "default" factory to generate an avatar. Ask it to
            // loader the avatar. If it does not exist (it should), simply
            // log an error andr return.
            AvatarLoaderFactorySPI factory = registry.getDefaultAvatarLoaderFactory();
            if (factory == null) {
                logger.warning("No default avatar factory is registered.");
                return null;
            }

            // We need to rewrite the AvatarConfigInfo object here a bit,
            // otherwise, the loader may still loader the wrong avatar. If
            // we set the URL in the AvatarConfigInfo to null, that should do
            // the trick. (Note that since we manually obtained the
            // AvatarLoaderFactorySPI, we don't need to update the factory
            // class name in the AvatarConfigInfo object, but we do anyway).
            String defaultClassName = factory.getClass().getName();
            AvatarConfigInfo defaultInfo =
                    new AvatarConfigInfo(null, defaultClassName);

            // Go ahead and load the default avatar
            ret = factory.getAvatarLoader().getAvatarCharacter(cell, username,
                    defaultInfo);
        }
        else {
            // If the avatar has a non-null configuration information, then
            // ask the loader factory to generate a new loader for this avatar
            String className = avatarConfigInfo.getLoaderFactoryClassName();
            if (className == null) {
                logger.warning("No class name given for avatar configuration" +
                        " with url " + avatarConfigInfo.getAvatarConfigURL());
                return null;
            }

            // Find the avatar factory, if it does not exist, return an error
            AvatarLoaderFactorySPI factory = registry.getAvatarLoaderFactory(className);
            if (factory == null) {
                logger.warning("No avatar loader factory for the class name " +
                        className + " with url " + avatarConfigInfo.getAvatarConfigURL());
                return null;
            }

            // Ask the avatar loader to create and return an avatar character
            ret = factory.getAvatarLoader().getAvatarCharacter(cell, username, avatarConfigInfo);
        }

        ret.getModelInst().getTransform().getLocalMatrix(true).set(origin);

        // XXX NPC HACK XXX
        // TODO - remove hardcoded npc support
//        if (username.equals("npc") && avatarConfigURL != null) {
//            String u = avatarConfigURL.getFile();
//            username = u.substring(u.lastIndexOf('/') + 1, u.lastIndexOf('.'));
//        }

        // Sets the Z-buffer state on the external kids root
        Node external = ret.getJScene().getExternalKidsRoot();
        setZBufferState(external);

        // JSCENE HAS NOT CHILDREN, so this does nothing
//        ret.getJScene().updateGeometricState(0, true);
//        GraphicsUtils.printGraphBounds(ret.getJScene());

        //        JScene jscene = avatar.getJScene();
        //        jscene.renderToggle();      // both renderers
        //        jscene.renderToggle();      // jme renderer only
        //        jscene.setRenderPRendererMesh(true);  // Force pRenderer to be instantiated
        //        jscene.toggleRenderPRendererMesh();   // turn off mesh
        //        jscene.toggleRenderBoundingVolume();  // turn off bounds


        return ret;
    }

    /**
     * Return true if we support high-quality avatars
     */
    public static boolean supportsHighQualityAvatars() {
        String shaderCheck = System.getProperty("avatar.shaderCheck");
        boolean shaderPass = true;

        // Check to see if the system supports OpenGL 2.0. If not, then
        // always use the low-detail avatar character
        RenderManager rm = ClientContextJME.getWorldManager().getRenderManager();
        if (shaderCheck != null && shaderCheck.equals("true")) {
            shaderPass = rm.getContextCaps().GL_MAX_VERTEX_UNIFORM_COMPONENTS_ARB >= 512;
        }

        // Issue 1114: make sure the system is telling the truth about what
        // it supports by trying a mock shader program
        boolean uniformsPass = shaderPass && ShaderTest.getInstance().testShaders();

        logger.warning("Checking avatar detail level.  OpenGL20: " +
                       rm.supportsOpenGL20() + " ShaderCheck: " + shaderPass +
                       " UniformsCheck: " + uniformsPass);

        // OWL issue #110 -- ignore the value of supportsOpenGL20() here. This
        // is known to report false negatives on at least one graphics card.
        // Our shader test should do an adequate job determining whether a
        // graphics card supports the OpenGL 2.0 features we use.
        
        // Update: fixed version of supportsOpenGL20 should properly detect
        // version.
        
        return rm.supportsOpenGL20() && shaderPass && uniformsPass;
    }

    /**
     * Returns the name tag node, creating it if it does not exist. There is a
     * single name tag node attached to the avatar Cell.
     *
     * NOTE: This method assumes it is being called in an MT-Safe manner.
     */
    private NameTagNode getNameTagNode() {
        NameTagComponent nameTagComp = cell.getComponent(NameTagComponent.class);
        if (nameTagComp == null) {
            return null;
        }
        return nameTagComp.getNameTagNode();
    }

    /**
     * Given the Avatar character, create and set it's collision controller.
     */
    private void setCollisionController(WlAvatarCharacter avatar) {
        // Create a spatial that represents the bounds of the avatar to use
        // for collision. These are hardcoded values for now.
        Vector3f origin = new Vector3f(0f, 0.92f, 0.15f);
        float xExtent = 0.3f;
        float yExtent = 0.6f;
        float zExtent = 0.15f;

        if (selectedForInput) {
            Spatial collisionGraph = new Box("AvatarCollision", origin, xExtent,
                    yExtent, zExtent);
            collisionGraph.setModelBound(new BoundingSphere());
            collisionGraph.updateModelBound();

            // Fetch the JME Collision system using the server manager of the Cell
            // to which this renderer is attached.
            ServerSessionManager manager =
                    cell.getCellCache().getSession().getSessionManager();
            CollisionSystem collisionSystem =
                    ClientContextJME.getCollisionSystem(manager, "Default");

            // Create a new collision controller, and set on the avatar
            CollisionController controller = new CollisionController(collisionGraph,
                    (JMECollisionSystem) collisionSystem);
            collisionChangeRequestListener.setCollisionController(controller);
            AvatarController ac = (AvatarController)avatar.getContext().getController();
            ac.setCollisionController(controller);
            if (collisionListener==null) {
                collisionListener = new WLCollisionListener();
            }
            ac.addCollisionListener(collisionListener);
        } else {
            AvatarController ac = (AvatarController)avatar.getContext().getController();
            ac.setCollisionController(null);
            ac.removeCollisionListener(collisionListener);
         }
    }

    /**
     * Sets the Z-buffer state on the given node.
     * 
     * NOTE: This method assumes it is being called in a MT-Safe manner.
     */
    private void setZBufferState(Node node) {
        RenderManager rm = ClientContextJME.getWorldManager().getRenderManager();
        ZBufferState zbuf = (ZBufferState)rm.createRendererState(RenderState.StateType.ZBuffer);
        zbuf.setEnabled(true);
        zbuf.setFunction(ZBufferState.TestFunction.LessThanOrEqualTo);
        node.setRenderState(zbuf);
    }

    void checkBounds(Spatial placeHolder) {
        traverseGraph(placeHolder, 0);
    }

    void traverseGraph(Spatial s, int level) {
        //for (int i=0; i<level; i++) {
        //    System.out.print("\t");
        //}

        if (s instanceof Geometry) {
            Geometry g = (Geometry)s;
            g.lockBounds();
            //System.out.println("Bounds for " + g + " is : " + g.getWorldBound());
        }
        if (s instanceof Node) {
            Node n = (Node)s;
            //n.setModelBound(new BoundingSphere());
            for (int i=0; i<n.getQuantity(); i++) {
                traverseGraph(n.getChild(i), level+1);
            }
            //n.updateWorldBound();
            //System.out.println("Bounds for " + n + " is : " + n.getWorldBound());
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Node createSceneGraph(Entity entity) {
        // Nothing to do here
        return null;
    }

    /**
     * Returns the WlAvatarCharacter object for this renderer. The WlAvatarCharacter
     * provides the control points in the avatar system.
     * @return
     */
    public WlAvatarCharacter getAvatarCharacter() {
        return avatarCharacter;
    }

    public void selectForInput(boolean selected) {
        if (selectedForInput==selected)
            return;

        if (avatarCharacter==null) {
            logger.warning("selectForInput called with null avatarCharacter");
            Thread.dumpStack();
            return;
        }

        logger.info("selectedForInput "+selected);
        selectedForInput = selected;

        if (avatarCharacter!=null) {
             WorldManager wm = ClientContextJME.getWorldManager();

            ((WlAvatarContext) avatarCharacter.getContext()).getBehaviorManager().setEnable(false);

            if (controlScheme == null && selectedForInput) {
                controlScheme = new DefaultCharacterControls(ClientContextJME.getWorldManager());
                ((AvatarControls)wm.getUserData(AvatarControls.class)).setDefault(controlScheme);
            }
            setCollisionController(avatarCharacter);
            if (selectedForInput) {

                // Listen for avatar movement and update the cell
                avatarCharacter.getContext().getController().addCharacterMotionListener(characterMotionListener);

                // Listen for game context changes
                avatarCharacter.getContext().addGameContextListener(gameContextListener);
                avatarCharacter.selectForInput();
                controlScheme.addCharacterToTeam(avatarCharacter);
                controlScheme.setCharacter(avatarCharacter);

                // Chain the camera processor to the avatar motion processor for
                // smooth animation. For animated avatars we use CharacterAnimationProcessor for the simple
                // avatar CharacterProcessor
                ProcessorCollectionComponent pcc = avatarCharacter.getComponent(ProcessorCollectionComponent.class);
                ProcessorComponent characterProcessor = null;
                ProcessorComponent characterAnimationProcessor = null;
                for(ProcessorComponent pc : pcc.getProcessors()) {
                    if (pc instanceof CharacterProcessor)
                        characterProcessor = pc;
                    else if (pc instanceof CharacterAnimationProcessor) {
                        characterAnimationProcessor = pc;
                        break;
                    }
                }

                cameraChainedProcessor=null;
                if (characterAnimationProcessor!=null) {
                    cameraChainedProcessor = characterAnimationProcessor;
                } else if (characterProcessor!=null)
                    cameraChainedProcessor = characterProcessor;

                if (cameraChainedProcessor!=null) {
                    cameraChainedProcessor.addToChain(ViewManager.getViewManager().getCameraProcessor());
                    cameraChainedProcessor.setRunInRenderer(true);
                }

                // Disable culling for local avatar, fix for issue 799
                avatarCharacter.getJScene().setCullHint(CullHint.Never);
            } else {
                avatarCharacter.getContext().getController().removeCharacterMotionListener(characterMotionListener);
                avatarCharacter.getContext().removeGameContextListener(gameContextListener);
                if (controlScheme!=null) {
                    controlScheme.clearCharacterTeam();
                }

                if (cameraChainedProcessor!=null) {
                    cameraChainedProcessor.removeFromChain(ViewManager.getViewManager().getCameraProcessor());
                    cameraChainedProcessor = null;
                }
                //Reenable culling for local avatar, fix for issue 799
                avatarCharacter.getJScene().setCullHint(CullHint.Dynamic);
            }
        } else {
            logger.severe("The avatar was null during enableInputListeners().");
        }
    }

    public void trigger(int trigger, boolean pressed, String animationName) {
        if (!selectedForInput && avatarCharacter != null) {
            // Sync to avoid concurrent updates of currentTrigger and currentPressed
            synchronized (this) {
                if (currentTrigger == trigger && currentPressed == pressed) {
                    return;
                }

                try {
                    if (pressed) {
                        if (animationName != null) {
                            ((WlAvatarContext) avatarCharacter.getContext()).setMiscAnimation(animationName);
                        }

                        avatarCharacter.getContext().triggerPressed(trigger);
                    } else {
                        avatarCharacter.getContext().triggerReleased(trigger);
                    }
                } catch (Exception e) {
                    // We can get this if a user is viewing a female avatar but
                    // has not yet set female as the default. 
                }

                currentTrigger = trigger;
                currentPressed = pressed;
            }
        }
    }
    
    public void triggerCollision(float height, boolean collision) {
        if (!selectedForInput && avatarCharacter != null) {
            ((WlAvatarController) avatarCharacter.getController()).setHeight(height);
            ((WlAvatarController) avatarCharacter.getController()).setColliding(collision);
        }
    }

    public void triggerGoto(final Vector3f position, final Quaternion look) {

        if (avatarCharacter != null) {
            SceneWorker.addWorker(new WorkCommit() {
                public void commit() {
                    PTransform xform = new PTransform(look, position,
                                                      new Vector3f(1, 1, 1));
                    avatarCharacter.getModelInst().setTransform(xform);
                }
            });
        } else {
            CellTransform transform = new CellTransform();
            transform.setRotation(look);
            transform.setTranslation(position);
        
            cell.getComponent(MovableComponent.class).localMoveRequest(transform);
        }
    }

    /**
     * Avatar model configuration listener, re-loads the avatar whenever a
     * reconfiguration happens
     */
    private class AvatarChangeListener implements AvatarConfigChangeListener {
        public void avatarConfigChanged(AvatarConfigMessage message) {
            // Fetch the new config info and try to load it. The null case is
            // handled by loadAvatar, and loads the system default avatar.
            loadAndChangeAvatar(message.getAvatarConfigInfo());
//            changeAvatar(loadAvatar(message.getAvatarConfigInfo()));
        }
    }

    /**
     * Add an avatar changed listener to the list. Duplicate checking is not 
     * performed. This method is thread-safe.
     * @param listener A non-null listener
     * @throws NullPointerException If listener == null
     */
    public synchronized void addAvatarChangedListener(AvatarChangedListener listener) {
        if (listener == null)
            throw new NullPointerException("Null listener provided!");
        avatarChangedListeners.add(new WeakReference<AvatarChangedListener>(listener));
    }
    
    /**
     * Remove an avatar changed listener from the list. This method will remove
     * erroneously added duplicates if any exist.
     * @param remove A non-null listener to remove
     * @throws NullPointerException If (remove == null)
     */
    public synchronized void removeAvatarChangedListener(AvatarChangedListener remove) {
        if (remove == null)
            throw new NullPointerException("Null listener provided!");
        for (WeakReference<AvatarChangedListener> listenerRef : avatarChangedListeners) {
            AvatarChangedListener listener = listenerRef.get();
            if (listener == null || listener == remove)
                avatarChangedListeners.remove(listenerRef);
        }
    }

    /**
     * This interface is used to receive call-backs whenever the underlying avatar
     * is changed
     */
    public static interface AvatarChangedListener {
        /**
         * The avatar has changed.
         * @param newAvatar The newly assigned avatar.
         */
        public void avatarChanged(Avatar newAvatar);
    }

    class DebugNode extends Node {
        @Override
        public void draw(Renderer r) {
            System.err.println("START**********************************");
            super.draw(r);
            System.err.println("END ***********************************");
        }
    }

    class CollisionChangeRequestListener extends EventClassListener {

        private CollisionController collisionController;
        private AvatarCollisionChangeRequestEvent evt=null;

        private Class[] consumeClasses = new Class[]{
            AvatarCollisionChangeRequestEvent.class,
        };

        public void setCollisionController(CollisionController collisionController) {
            synchronized(this) {
                this.collisionController = collisionController;
                if (collisionController!=null) {
                    if (evt!=null) {
                        collisionController.setCollisionResponseEnabled(evt.isCollisionResponseEnabled());
                        collisionController.setGravityEnabled(evt.isGravityEnabled());
                    } else {
                        collisionController.setCollisionResponseEnabled(true);
                        collisionController.setGravityEnabled(true);
                    }
                }
            }
        }

        @Override
        public Class[] eventClassesToConsume() {
            return consumeClasses;
        }

        @Override
        public void commitEvent(Event event) {
        }

        @Override
        public void computeEvent(Event evtIn) {
            synchronized(this) {
                evt = (AvatarCollisionChangeRequestEvent) evtIn;
                if (collisionController!=null) {
                    collisionController.setCollisionResponseEnabled(evt.isCollisionResponseEnabled());
                    collisionController.setGravityEnabled(evt.isGravityEnabled());
                }
            }
        }
    }

    class WLCollisionListener implements AvatarCollisionListener {

        public void processCollision(CollisionInfo collisionInfo) {
            JMECollisionDetails cd = (JMECollisionDetails)collisionInfo.get(0);
            if (logger.isLoggable(Level.INFO))
                logger.info("Collided with: " + cd.getReportedNode() + " on entity: " + cd.getEntity());
            ClientContextJME.getInputManager().postEvent(new AvatarCollisionEvent(collisionInfo), cd.getEntity());
        }

    }
    
    class AvatarUIEventListener extends EventClassListener {
            private Class[] consumeClasses = new Class[]{
                AvatarRendererChangeRequestEvent.class,
                AvatarNameEvent.class
            };

            @Override
            public Class[] eventClassesToConsume() {
                return consumeClasses;
            }

            @Override
            public void commitEvent(Event event) {
                if (event instanceof AvatarNameEvent) {
                    AvatarNameEvent e = (AvatarNameEvent) event;

                    // Fetch the name tag node, there should only be one of
                    // these in the system and set the name.
                    NameTagNode nameTagNode = getNameTagNode();
                    if (nameTagNode!=null && e.getUsername().equals(username) == true) {
                        nameTagNode.setNameTag(e.getEventType(), username,
                                           e.getUsernameAlias());
                    }
                } else if (event instanceof AvatarRendererChangeRequestEvent) {
                    handleAvatarRendererChangeRequest((AvatarRendererChangeRequestEvent)event);
                }
            }

            @Override
            public void computeEvent(Event evtIn) {
            }
    }
}
