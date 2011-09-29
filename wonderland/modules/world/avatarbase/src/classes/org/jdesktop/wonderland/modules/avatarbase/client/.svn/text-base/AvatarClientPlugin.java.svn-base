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
package org.jdesktop.wonderland.modules.avatarbase.client;

import com.jme.math.Vector3f;
import imi.camera.CameraModels;
import imi.camera.ChaseCamModel;
import imi.camera.ChaseCamState;
import imi.character.AvatarSystem;
import imi.character.avatar.Avatar;
import imi.repository.Repository;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.instrument.Instrumentation;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.BaseClientPlugin;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.Cell.RendererType;
import org.jdesktop.wonderland.client.cell.CellComponent;
import org.jdesktop.wonderland.client.cell.CellRenderer;
import org.jdesktop.wonderland.client.cell.ComponentChangeListener;
import org.jdesktop.wonderland.client.cell.asset.AssetUtils;
import org.jdesktop.wonderland.client.cell.view.AvatarCell;
import org.jdesktop.wonderland.client.cell.view.ViewCell;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuActionListener;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuEvent;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuInvocationSettings;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuItem;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuItemEvent;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuListener;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuManager;
import org.jdesktop.wonderland.client.contextmenu.SimpleContextMenuItem;
import org.jdesktop.wonderland.client.contextmenu.spi.ContextMenuFactorySPI;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.client.jme.MainFrame;
import org.jdesktop.wonderland.client.jme.MainFrameImpl;
import org.jdesktop.wonderland.client.jme.ViewManager;
import org.jdesktop.wonderland.client.jme.ViewManager.ViewManagerListener;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.client.scenemanager.event.ContextEvent;
import org.jdesktop.wonderland.common.annotation.Plugin;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.modules.avatarbase.client.AvatarSessionLoader.AvatarLoaderStateListener;
import org.jdesktop.wonderland.modules.avatarbase.client.AvatarSessionLoader.State;
import org.jdesktop.wonderland.modules.avatarbase.client.cell.AvatarConfigComponent;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.FlexibleCameraAdapter;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarCollisionChangeRequestEvent;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarControls;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarImiJME;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarImiJME.AvatarChangedListener;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarTestPanel;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.GestureHUD;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarCharacter;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WonderlandAvatarCache;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.AvatarRegistry;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.AvatarRegistry.AvatarInUseListener;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.spi.AvatarSPI;
import org.jdesktop.wonderland.modules.avatarbase.client.ui.AvatarConfigFrame;
import org.jdesktop.wonderland.modules.avatarbase.common.cell.AvatarConfigInfo;

/**
 * A client-side plugin to initialize the avatar system
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@Plugin
public class AvatarClientPlugin extends BaseClientPlugin 
        implements AvatarLoaderStateListener, ViewManagerListener {

    private static Logger logger = Logger.getLogger(AvatarClientPlugin.class.getName());

    private static final ResourceBundle bundle =
            ResourceBundle.getBundle("org/jdesktop/wonderland/modules/" +
            "avatarbase/client/resources/Bundle");

    // A map of a session and the loader for that session
    private Map<ServerSessionManager, AvatarSessionLoader> loaderMap = null;

    // Listener for when a new avatar becomes in-use
    private AvatarInUseListener inUseListener = null;

    // Listen for when the avatar character changes to update the state of the
    // chase camera.
    private AvatarChangedListener avatarChangedListener = null;

    // The current avatar cell renderer
    private AvatarImiJME avatarCellRenderer = null;

    // Chase camera state and model and menu item
    private ChaseCamState camState = null;
    private ChaseCamModel camModel = null;
    private JRadioButtonMenuItem chaseCameraMI = null;


    // Some test control panels for the avatar
    private WeakReference<AvatarTestPanel> testPanelRef = null;
    private JMenuItem avatarControlsMI = null;
    private JMenuItem avatarSettingsMI = null;
    private Instrumentation instrumentation = null;

    // The gesture HUD panel and menu item
    private WeakReference<GestureHUD> gestureHUDRef = null;
    private JCheckBoxMenuItem gestureMI = null;

    // True if the menus have been added to the main menu, false if not
    private boolean menusAdded = false;

    // Menu items for the collision & gravity check boxes
    private JCheckBoxMenuItem collisionResponseEnabledMI = null;
    private JCheckBoxMenuItem gravityEnabledMI = null;

    // The avatar configuration menu item
    private JMenuItem avatarConfigMI = null;

    // Indicates that the avatar has already been set once the primary view
    // has been set, so that is does not happen more than once. We synchronized
    // on the mutex
    private Lock isAvatarSetMutex = new ReentrantLock();
    private Boolean isAvatarSet = false;

    // Context menu listener
    private ContextMenuListener ctxListener;

    /**
     * {@inheritDoc]
     */
    @Override
    public void initialize(ServerSessionManager manager) {
        loaderMap = new HashMap();

        // A listener for changes to the primary view cell renderer. (This
        // rarely happens in practice). When the avatar cell renderer changes,
        // reset the chase camera state.
        avatarChangedListener = new AvatarChangedListener() {
            public void avatarChanged(Avatar newAvatar) {
                if (camState != null) {
                    // stop listener for changes from the old avatar cell
                    // renderer.
                    avatarCellRenderer.removeAvatarChangedListener(avatarChangedListener);

                    if (newAvatar.getContext() != null) {
                        camState.setTargetCharacter(newAvatar);
                    }
                    else {
                        camState.setTargetCharacter(null);
                    }

                    // Fetch the initial position of the camera. This is based
                    // upon the current avatar position. We can assume that the
                    // primary View Cell exists at this point, since the menu
                    // item is not added until a primary View Cell exists.
                    ViewManager viewManager = ViewManager.getViewManager();
                    ViewCell viewCell = viewManager.getPrimaryViewCell();
                    CellTransform transform = viewCell.getWorldTransform();
                    Vector3f translation = transform.getTranslation(null);

                    // This is the offset from the avatar view Cell to place the
                    // camera
                    Vector3f offset = new Vector3f(0.0f, 4.0f, -10.0f);

                    // force an update
                    camState.setCameraPosition(translation.add(offset));
                }

                // OWL issue #125: Reinitialize the gesture HUD panel with the
                // current avatar character.
                if (gestureHUDRef != null && gestureHUDRef.get() != null) {
                    if (newAvatar instanceof WlAvatarCharacter) {
                        gestureHUDRef.get().setAvatarCharacter((WlAvatarCharacter) newAvatar,
                                                               gestureMI.getState());
                    } else {
                        gestureHUDRef.get().setVisible(false);
                    }
                }
            }
        };

        // A menu item for the chase camera
        chaseCameraMI = new JRadioButtonMenuItem(bundle.getString("Chase_Camera"));
        chaseCameraMI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                // Fetch the initial position of the camera. This is based upon
                // the current avatar position. We can assume that the primary
                // View Cell exists at this point, since the menu item is not
                // added until a primary View Cell exists.
                ViewManager viewManager = ViewManager.getViewManager();
                ViewCell viewCell = viewManager.getPrimaryViewCell();
                CellTransform transform = viewCell.getWorldTransform();
                Vector3f translation = transform.getTranslation(null);

                // This is the offset from the avatar view Cell to place the
                // camera
                Vector3f offset = new Vector3f(0.0f, 4.0f, -10.0f);

                // Create the camera state if it does not yet exist. Initialize
                // the initial position to that of the view Cell.
                if (camState == null) {
                    camModel = (ChaseCamModel) CameraModels.getCameraModel(ChaseCamModel.class);
                    camState = new ChaseCamState(offset, new Vector3f(0.0f, 1.8f, 0.0f));
                    camState.setDamping(1.7f);
                    camState.setLookAtDamping(1.7f);
                }
                camState.setCameraPosition(translation.add(offset));
                camState.setTargetCharacter(avatarCellRenderer.getAvatarCharacter());

                // Create the Chase Camera with the model and state and add to
                // the View Manager
                FlexibleCameraAdapter chaseCamera =
                        new FlexibleCameraAdapter(camModel, camState);
                viewManager.setCameraController(chaseCamera);
            }
        });

        // A menu item for a test control panel for the avatar.
//        avatarControlsMI = new JMenuItem(bundle.getString("Avatar_Controls"));
//        avatarControlsMI.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                if (testPanelRef == null || testPanelRef.get() == null) {
//                    AvatarTestPanel test = new AvatarTestPanel();
//                    JFrame f = new JFrame(bundle.getString("Avatar_Controls"));
//                    f.getContentPane().add(test);
//                    f.pack();
//                    f.setVisible(true);
//                    f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
//                    test.setAvatarCharacter(avatarCellRenderer.getAvatarCharacter());
//                    testPanelRef = new WeakReference(test);
//                } else {
//                    SwingUtilities.getRoot(testPanelRef.get().getParent()).setVisible(true);
//                }
//            }
//        });

        // Avatar Instrumentation is a dev tool
//        avatarSettingsMI = new JMenuItem(bundle.getString("Avatar_Settings..."));
//        avatarSettingsMI.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                AvatarInstrumentation in = new AvatarInstrumentation(instrumentation);
//                in.setVisible(true);
//            }
//        });
//        instrumentation = new DefaultInstrumentation(ClientContextJME.getWorldManager());

        // The menu item for the Gesture (HUD)
        gestureMI = new JCheckBoxMenuItem(bundle.getString("Gesture_UI"));
        gestureMI.setSelected(false);
        gestureMI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean visible = gestureMI.isSelected();
                if (gestureHUDRef == null || gestureHUDRef.get() == null) {
                    GestureHUD hud = new GestureHUD(gestureMI);
                    hud.setAvatarCharacter(avatarCellRenderer.getAvatarCharacter(), visible);
                    gestureHUDRef = new WeakReference(hud);
                }
                //issue #174 hud visibility management
                if (visible) {
                	gestureHUDRef.get().setMaximized();
                } 
                gestureHUDRef.get().setVisible(visible);
            }        
        });

        // The menu item for the avatar configuration
        avatarConfigMI = new JMenuItem(bundle.getString("Avatar_Appearance..."));
        avatarConfigMI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AvatarConfigFrame f = new AvatarConfigFrame();
                f.setVisible(true);
            }
        });

        // Check box to set collision enabled
        collisionResponseEnabledMI = new JCheckBoxMenuItem(bundle.getString("Avatar_Collision_Response_Enabled"));
        collisionResponseEnabledMI.setSelected(true); // TODO should be set by server
        collisionResponseEnabledMI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean isCollisionResponse = collisionResponseEnabledMI.isSelected();
                boolean isGravity = gravityEnabledMI.isSelected();
                ClientContext.getInputManager().postEvent(
                        new AvatarCollisionChangeRequestEvent(isCollisionResponse, isGravity));
            }
        });

        // Check box to set gravity (floor following) enabled
        gravityEnabledMI = new JCheckBoxMenuItem(bundle.getString("Avatar_Gravity_Enabled"));
        gravityEnabledMI.setSelected(true); // TODO should be set by server
        gravityEnabledMI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean isCollisionResponse = collisionResponseEnabledMI.isSelected();
                boolean isGravity = gravityEnabledMI.isSelected();
                ClientContext.getInputManager().postEvent(
                        new AvatarCollisionChangeRequestEvent(isCollisionResponse, isGravity));
            }
        });

        ctxListener = new ContextMenuListener() {
            public void contextMenuDisplayed(ContextMenuEvent event) {
                // only deal with invocations on AvatarCell
                if (!(event.getPrimaryCell() instanceof AvatarCell)) {
                    return;
                }

                ContextMenuInvocationSettings settings = event.getSettings();
                settings.setDisplayStandard(false);
                settings.setDisplayCellStandard(false);

                AvatarCell cell = (AvatarCell) event.getPrimaryCell();
                settings.setMenuName(cell.getIdentity().getUsername());

                // if this is our avatar, add the configuration menu
                if (cell == ViewManager.getViewManager().getPrimaryViewCell()) {
                    settings.addTempFactory(new ConfigureContextMenuFactory());
                }
            }
        };

        // register the renderer for this session
        ClientContextJME.getAvatarRenderManager().registerRenderer(manager,
                AvatarImiJME.class, AvatarControls.class);

        // XXX TODO: this shouldn't be done here -- it should be done in
        // activate or should be registered per session not globally
        // XXX
        try {
            String serverHostAndPort = manager.getServerNameAndPort();
            String baseURL = "wla://avatarbaseart/";
            URL url = AssetUtils.getAssetURL(baseURL, serverHostAndPort);
            WorldManager worldManager = ClientContextJME.getWorldManager();
            worldManager.addUserData(Repository.class, new Repository(worldManager,
                    new WonderlandAvatarCache(url.toExternalForm(),
                    ClientContext.getUserDirectory("AvatarCache"))));
        } catch (MalformedURLException excp) {
            logger.log(Level.WARNING, "Unable to form avatar base URL", excp);
        } catch(Exception e) {
            logger.log(Level.SEVERE,"Exception, are you using JDK 5 ?", e);
        }

        // Initialize the AvatarSystem after we set up caching
        AvatarSystem.initialize(ClientContextJME.getWorldManager());
        
        super.initialize(manager);
    }

    /**
     * {@inheritDoc]
     */
    @Override
    public void cleanup() {
        // XXX should be done in deactivate XXX
        WorldManager worldManager = ClientContextJME.getWorldManager();
        worldManager.removeUserData(Repository.class);

        ServerSessionManager manager = getSessionManager();
        ClientContextJME.getAvatarRenderManager().unregisterRenderer(manager);
        
        super.cleanup();
    }

    /**
     * {@inheritDoc]
     */
    @Override
    protected void activate() {
        // Upon a new session, load the session and put it in the map. Wait
        // for it to finish loading. When done then set up the view Cell or
        // wait for it to finish.
        ServerSessionManager manager = getSessionManager();
        AvatarSessionLoader loader = new AvatarSessionLoader(manager);
        loaderMap.put(manager, loader);
        loader.addAvatarLoaderStateListener(this);
        loader.load();

        // set up our custom context menu listener to disable the standard
        // menus on avatar cells
        ContextMenuManager.getContextMenuManager().addContextMenuListener(ctxListener);
    }

    /**
     * {@inheritDoc]
     */
    @Override
    protected void deactivate() {
        ContextMenuManager.getContextMenuManager().removeContextMenuListener(ctxListener);

        // First remove the menus. This will prevent users from taking action
        // upon the avatar in the (small) chance they do while the session is
        // being deactivated.
        if (menusAdded == true) {
            MainFrame frame = JmeClientMain.getFrame();
            frame.removeFromWindowMenu(gestureMI);
            frame.removeFromToolsMenu(collisionResponseEnabledMI);
            frame.removeFromToolsMenu(gravityEnabledMI);
            frame.removeFromEditMenu(avatarConfigMI);
            
            if (frame instanceof MainFrameImpl) { // Until MainFrame gets this method added
                ((MainFrameImpl) frame).removeFromCameraChoices(chaseCameraMI);
            }
            else {
                frame.removeFromViewMenu(chaseCameraMI);
            }

            // Remove the avatar controls (test) if it exists
            if (avatarControlsMI != null) {
                frame.removeFromWindowMenu(avatarControlsMI);
            }

            // Add the avatar instrumentions settings if it exists
            if (avatarSettingsMI != null) {
                frame.removeFromEditMenu(avatarSettingsMI);
            }
            menusAdded = false;
        }

        // Next, remove the listener for changes in avatar in use. This will
        // prevent the avatar from being updated while the avatars are removed
        // from the system.
        if (inUseListener != null) {
            AvatarRegistry.getAvatarRegistry().removeAvatarInUseListener(inUseListener);
            inUseListener = null;
        }

        // Stop listening for primary view changes.
        ViewManager.getViewManager().removeViewManagerListener(this);

        // Finally, fetch the avatar session loader for the session just ended
        // and unload all of the avatars from the system. This will remove the
        // avatars one-by-one.
        AvatarSessionLoader loader = loaderMap.get(getSessionManager());
        if (loader != null) {
            loader.removeAvatarLoaderStateListener(this);
            loader.unload();
            loaderMap.remove(getSessionManager());
        }
    }

    /**
     * {@inheritDoc]
     */
    public void stateChanged(State state) {
        if (state == State.READY) {
            // If the state is ready, then set-up the primary view Cell if it is
            // ready or wait for it to become ready.
            ViewManager manager = ViewManager.getViewManager();
            manager.addViewManagerListener(this);
            if (manager.getPrimaryViewCell() != null) {
                // fake a view cell changed event
                primaryViewCellChanged(null, manager.getPrimaryViewCell());
            }
        }
    }

    /**
     * {@inheritDoc]
     */
    public void primaryViewCellChanged(ViewCell oldViewCell, final ViewCell newViewCell) {
        // TODO tidy up oldViewCell
        
        // If there is an old avatar, then remove the listener (although in
        // practice primary view cells do not change.
        if (avatarCellRenderer != null) {
            avatarCellRenderer.removeAvatarChangedListener(avatarChangedListener);
        }

        // If the new primary view cell is null, then just return here
        if (newViewCell == null) {
            return;
        }

        logger.info("Primary view Cell Changes from " + oldViewCell +
                " to " + newViewCell + " " + newViewCell.getName());
        
        // Fetch the cell renderer for the new primary view Cell. It should
        // be of type AvatarImiJME. If not, log a warning and return
        CellRenderer rend = newViewCell.getCellRenderer(RendererType.RENDERER_JME);
        if (!(rend instanceof AvatarImiJME)) {
            logger.warning("Cell renderer for view " + newViewCell.getName() +
                    " is not of type AvatarImiJME.");
            return;
        }

        // We also want to listen (if we aren't doing so already) for when the
        // avatar in-use has changed.
        if (inUseListener == null) {
            inUseListener = new AvatarInUseListener() {
                public void avatarInUse(AvatarSPI avatar, boolean isLocal) {
                    refreshAvatarInUse(newViewCell, isLocal);
                }
            };
            AvatarRegistry.getAvatarRegistry().addAvatarInUseListener(inUseListener);
        }

        // set the current avatar
        avatarCellRenderer = (AvatarImiJME) rend;

        // start listener for new changes. This is used for the chase camera.
        avatarCellRenderer.addAvatarChangedListener(avatarChangedListener);

        // Set the state of the chase camera from the current avatar in the
        // cell renderer.
        if (camState != null) {
            camState.setTargetCharacter(avatarCellRenderer.getAvatarCharacter());
            camModel.reset(camState);
        }

        // Initialize the avatar control panel (test) with the current avatar
        // character.
        //        if (testPanelRef != null && testPanelRef.get() != null) {
        //            testPanelRef.get().setAvatarCharacter(avatarCellRenderer.getAvatarCharacter());
        //        }

        // Initialize the gesture HUD panel with the current avatar character.
        if (gestureHUDRef != null && gestureHUDRef.get() != null) {
            gestureHUDRef.get().setAvatarCharacter(avatarCellRenderer.getAvatarCharacter(),
                                                   gestureMI.isSelected());
        }

        // We also want to listen (if we aren't doing so already) for when the
        // avatar in-use has changed.
        if (inUseListener == null) {
            inUseListener = new AvatarInUseListener() {
                public void avatarInUse(AvatarSPI avatar, boolean isLocal) {
                    refreshAvatarInUse(newViewCell, isLocal);
                }
            };
            AvatarRegistry.getAvatarRegistry().addAvatarInUseListener(inUseListener);
        }

        // Once the avatar loader is ready and the primary view has been set,
        // we tell the avatar cell component to set it's avatar in use. We can
        // only do this after we know the AvatarConfigComponent is on the View
        // Cell. We therefore add a listener, but also check immediately whether
        // the component exists. The handleSetAvatar() method makes sure that
        // the call to refresh() only happens once.
        isAvatarSet = false;
        newViewCell.addComponentChangeListener(new ComponentChangeListener() {
            public void componentChanged(Cell cell, ChangeType type,
                    CellComponent component) {
                AvatarConfigComponent c =
                        cell.getComponent(AvatarConfigComponent.class);
                if (type == ChangeType.ADDED && c != null) {
                    handleSetAvatar((ViewCell)cell);
                }
            }
        });
        if (newViewCell.getComponent(AvatarConfigComponent.class) != null) {
            handleSetAvatar(newViewCell);
        }

        // Finally, enable the menu items to allow avatar configuration. We
        // do this after the view cell is set, so we know we have an avatar
        // in the world.
        if (menusAdded == false) {
            MainFrame frame = JmeClientMain.getFrame();
            frame.addToWindowMenu(gestureMI, 0);
            frame.addToToolsMenu(gravityEnabledMI, 3);
            frame.addToToolsMenu(collisionResponseEnabledMI, 2);
            frame.addToEditMenu(avatarConfigMI, 0);

            if (frame instanceof MainFrameImpl) { // Only until the MainFrame interface gets this method
                ((MainFrameImpl) frame).addToCameraChoices(chaseCameraMI, 3);
            }
            else {
                frame.addToViewMenu(chaseCameraMI, 3);
            }

            // Add the avatar control (test) if it exists
            if (avatarControlsMI != null) {
                frame.addToWindowMenu(avatarControlsMI, 0);
            }

            // Add the avatar instrumentation settings if it exists
            if (avatarSettingsMI != null) {
                frame.addToEditMenu(avatarSettingsMI, 1);
            }
            menusAdded = true;
        }
    }

    /**
     * Handles when the primary view has been set and the view cell contains an
     * AvatarConfigComponent. This insures that the refresh() avatar is only
     * called once.
     */
    private void handleSetAvatar(ViewCell viewCell) {
        // We synchronize on 'isAvatarSet' and only call refresh() if the value
        // is false.
        isAvatarSetMutex.lock();
        try {
            if (isAvatarSet == false) {
                isAvatarSet = true;
                refreshAvatarInUse(viewCell, false);
            }
        } finally {
            isAvatarSetMutex.unlock();
        }
    }

    /**
     * Refreshes the primary view cell with the current avatar in use given the
     * current primary view cell.
     */
    public synchronized void refreshAvatarInUse(ViewCell viewCell, boolean isLocal) {

        // Once the avatar loader is ready and the primary view has been set,
        // we tell the avatar cell component to set it's avatar in use.
        AvatarConfigComponent configComponent = viewCell.getComponent(AvatarConfigComponent.class);
        AvatarRegistry registry = AvatarRegistry.getAvatarRegistry();
        AvatarSPI avatar = registry.getAvatarInUse();
        if (avatar != null) {
            ServerSessionManager session = viewCell.getCellCache().getSession().getSessionManager();
            AvatarConfigInfo configInfo = avatar.getAvatarConfigInfo(session);
            configComponent.requestAvatarConfigInfo(configInfo, isLocal);
        }
    }

    /**
     * Context menu factory for configuring your avatar
     */
    private static class ConfigureContextMenuFactory
            implements ContextMenuFactorySPI
    {
        public ContextMenuItem[] getContextMenuItems(ContextEvent event) {
            return new ContextMenuItem[] {
                new SimpleContextMenuItem(bundle.getString("Configure..."),
                        new ContextMenuActionListener()
                {
                    public void actionPerformed(ContextMenuItemEvent event) {
                        AvatarConfigFrame f = new AvatarConfigFrame();
                        f.setVisible(true);
                    }
                })
            };
        }
    }
}
