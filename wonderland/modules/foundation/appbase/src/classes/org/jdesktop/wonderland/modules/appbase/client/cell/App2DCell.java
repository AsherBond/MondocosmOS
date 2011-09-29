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
package org.jdesktop.wonderland.modules.appbase.client.cell;

import com.jme.math.Vector2f;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ResourceBundle;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.Cell.RendererType;
import org.jdesktop.wonderland.client.cell.CellCache;
import org.jdesktop.wonderland.client.cell.CellRenderer;
import org.jdesktop.wonderland.client.cell.ChannelComponent;
import org.jdesktop.wonderland.client.cell.annotation.UsesCellComponent;
import org.jdesktop.wonderland.client.cell.utils.CellUtils;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuActionListener;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuEvent;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuItem;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuItemEvent;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuListener;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuManager;
import org.jdesktop.wonderland.client.contextmenu.SimpleContextMenuItem;
import org.jdesktop.wonderland.client.contextmenu.cell.ContextMenuComponent;
import org.jdesktop.wonderland.client.contextmenu.spi.ContextMenuFactorySPI;
import org.jdesktop.wonderland.client.scenemanager.event.ContextEvent;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.state.CellClientState;
import org.jdesktop.wonderland.modules.appbase.client.App2D;
import org.jdesktop.wonderland.modules.appbase.client.ControlArb;
import org.jdesktop.wonderland.modules.appbase.client.Window2D;
import org.jdesktop.wonderland.modules.appbase.client.cell.view.View2DCellFactory;
import org.jdesktop.wonderland.modules.appbase.client.cell.view.viewdefault.View2DCell;
import org.jdesktop.wonderland.modules.appbase.client.view.View2D;
import org.jdesktop.wonderland.modules.appbase.client.view.View2DDisplayer;
import org.jdesktop.wonderland.modules.appbase.common.cell.App2DCellClientState;
import org.jdesktop.wonderland.modules.appbase.common.cell.App2DCellPerformFirstMoveMessage;
import org.jdesktop.wonderland.client.cell.TransformChangeListener;
import java.util.logging.Logger;

/**
 * The generic 2D application superclass. Displays the windows of a single 2D
 * application. It's only extra attribute is the pixel scale for all app windows
 * created in the cell. The pixel scale is a Vector2f. The x component specifies
 * the size (in local cell coordinates) of the windows along the local cell X
 * axis. The y component specifies the same along the local cell Y axis. The
 * pixel scale is in the cell client data (which must be of type
 * <code>App2DCellClientState</code>) sent by the server when it instantiates
 * this cell.
 *
 * @author deronj
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 *
 */
@ExperimentalAPI
public abstract class App2DCell extends Cell implements View2DDisplayer {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/appbase/client/Bundle");
    /** A list of all App2DCells in this client */
    private static final ArrayList<App2DCell> appCells =
            new ArrayList<App2DCell>();
    /** The view factory to use to create views for this cell. */
    private View2DCellFactory view2DCellFactory;
    /**
     * The number of world units per pixel in the cell local X and Y directions
     */
    // TODO: eliminate
    protected Vector2f pixelScale = new Vector2f();
    /** All app views displayed by this cell. */
    private LinkedList<View2DCell> views = new LinkedList<View2DCell>();
    /** The app displayed in this cell. */
    protected App2D app;
    // For the Window Menu
    @UsesCellComponent
    private ContextMenuComponent contextMenuComp = null;
    private ContextMenuFactorySPI menuFactory = null;
    private ContextMenuListener menuListener = null;
    /** 
     * The cell's first visible initializer. This is non-null if this cell has 
     * volunteered to do the initialization.
     */
    protected FirstVisibleInitializerCell fvi;
    /** The overriding initial placement size. */
    private Vector2f initialPlacementSize;
    // the cell channel
    @UsesCellComponent
    protected ChannelComponent channel;

    /** 
     * Creates a new instance of App2DCell.
     *
     * @param cellID The ID of the cell.
     * @param cellCache the cell cache which instantiated, and owns, this cell.
     */
    public App2DCell(CellID cellID, CellCache cellCache) {
        super(cellID, cellCache);
        synchronized (appCells) {
            appCells.add(this);
        }

        // TODO: Instrumentation for debugging 670: Part 1 of 3: Register a transform change listener
        // Part 2 is at the end of this file.
        // Part 3 is in the client logging.properties.
        addTransformChangeListener(new MyTransformChangeListener());
    }

    /**
     * {@inheritDoc}
     */
    public void cleanup() {
        if (app == null) {
            return;
        }
        synchronized (app.getAppCleanupLock()) {
            synchronized (appCells) {
                appCells.remove(this);
            }
            view2DCellFactory = null;
            pixelScale = null;
            views.clear();
            app = null;
        }
    }

    /**
     * Destroy the visual representation of this application.  This will
     * cause the client to unload all local data associated with this
     * app.
     *
     * THREAD USAGE NOTE: This is sometimes called on the EDT (e.g.HeaderPanel close button)
     * and sometimes called off the EDT (e.g. App2DCell.setStatus). Do not call this while
     * holding any app base locks.
     */
    protected void destroyClientVisual() {
        if (app != null) {
            app.cleanup();
        }
        cleanup();
    }

    /**
     * Destroy the app cell.  This will remove all visuals associated with
     * the cell (by calling <code>destroyClientVisual()</code>, and also remove
     * the cell from the server.
     */
    public void destroy() {
        // remove the client visuals for the cell
        destroyClientVisual();

        // Tell the server to remove the cell from the world
        CellUtils.deleteCell(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CellRenderer createCellRenderer(RendererType rendererType) {
        CellRenderer ret = null;
        switch (rendererType) {
            case RENDERER_2D:
                // No 2D Renderer yet
                break;
            case RENDERER_JME:
                ret = getViewFactory().createCellRenderer(this);
                break;
        }

        return ret;
    }

    /**
     * Associate the app with this cell. May only be called one time.
     * Note: this is only called by Swing app cells.
     *
     * @param app The world cell containing the app.
     * @throws IllegalArgumentException If the app already is associated with a
     * cell.
     * @throws IllegalStateException If the cell is already associated with an
     * app.
     */
    public void setApp(App2D app)
            throws IllegalArgumentException, IllegalStateException {

        if (app == null) {
            throw new NullPointerException();
        }
        if (this.app != null) {
            throw new IllegalStateException("Cell already has an app");
        }

        this.app = app;
        setName(app.getName());

        if (App2D.doAppInitialPlacement) {
            logger.info("Cell transferring fvi to app, fvi = " + fvi);
            app.setFirstVisibleInitializer(fvi);
        }
    }

    /**
     * Get the app associated with this cell.
     */
    public App2D getApp() {
        return app;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setClientState(CellClientState clientState) {
        super.setClientState(clientState);
        App2DCellClientState state = (App2DCellClientState) clientState;
        pixelScale = state.getPixelScale();

        logger.info("initialPlacementDone = " + state.isInitialPlacementDone());

        if (App2D.doAppInitialPlacement && !state.isInitialPlacementDone()) {
            // Initial cell placement hasn't yet been done. Volunteer to do it.
            CellTransform creatorViewTransform = state.getCreatorViewTransform();
            logger.info("creatorViewTransform = " + creatorViewTransform);
            if (creatorViewTransform != null) {
                fvi = new FirstVisibleInitializerCell(this, creatorViewTransform, initialPlacementSize);
            }
            logger.info("fvi = " + fvi);
        }
    }

    /**
     * This is called when the status of the cell changes.
     */
    @Override
    protected void setStatus(CellStatus status, boolean increasing) {
        super.setStatus(status, increasing);

        switch (status) {

            // The cell is now visible
            case ACTIVE:
                if (increasing) {
                    if (menuFactory == null) {
                        menuFactory = new ContextMenuFactorySPI() {

                            public ContextMenuItem[] getContextMenuItems(
                                    ContextEvent event) {
                                return windowMenuItemsForEvent(
                                        event, contextMenuComp);
                            }
                        };
                        contextMenuComp.addContextMenuFactory(menuFactory);
                    }

                    // Also create and add a listener for context menu events
                    // to perform special processing right before the context
                    // menu is displayed
                    if (menuListener == null) {
                        menuListener = new ContextMenuListener() {

                            public void contextMenuDisplayed(
                                    ContextMenuEvent event) {
                                windowMenuDisplayed(event, contextMenuComp);
                            }
                        };
                        ContextMenuManager cmm =
                                ContextMenuManager.getContextMenuManager();
                        cmm.addContextMenuListener(menuListener);
                    }
                } else {
                    // If the cell has decreased to ACTIVE it is no longer
                    // visible and is no longer close enough to the viewer to be
                    // potentially viewable, so we release control of the app if
                    // it is controlled. We did a similar thing in 0.4
                    // Wonderland, where we released control on a teleport. But
                    // this is a more elegant solution.
                    if (app != null) {
                        ControlArb controlArb = app.getControlArb();
                        if (controlArb != null) {
                            if (controlArb.hasControl()) {
                                controlArb.releaseControl();
                            }
                        }
                    }
                }
                break;

            // The cell is no longer visible
            case DISK:
                if (!increasing) {
                    if (menuFactory != null) {
                        contextMenuComp.removeContextMenuFactory(menuFactory);
                        menuFactory = null;
                    }

                    if (menuListener != null) {
                        ContextMenuManager cmm =
                                ContextMenuManager.getContextMenuManager();
                        cmm.removeContextMenuListener(menuListener);
                        menuListener = null;
                    }

                    // issue #968: clean up the local visuals for this app cell,
                    // but don't remove it from the server
                    App2D.invokeLater(new Runnable() {
                        public void run () {
                            destroyClientVisual();
                        }
                    });
                }
                break;
        }
    }

    /**
     * Performs any special pre-processing when a context menu is about to
     * be displayed
     */
    private void windowMenuDisplayed(ContextMenuEvent event,
            ContextMenuComponent contextMenuComp) {
        if (event.getSource() instanceof Window2D.WindowContextMenuEvent) {
            Window2D.WindowContextMenuEvent windowMenuEvent =
                    (Window2D.WindowContextMenuEvent) event.getSource();
            windowMenuEvent.getWindow().contextMenuDisplayed(
                    event, contextMenuComp);
        }
    }

    /**
     * Returns the window menu items that are appropriate for the given context
     * event.
     */
    private ContextMenuItem[] windowMenuItemsForEvent(ContextEvent event,
            ContextMenuComponent contextMenuComp) {
        if (event instanceof Window2D.WindowContextMenuEvent) {
            Window2D.WindowContextMenuEvent windowMenuEvent =
                    (Window2D.WindowContextMenuEvent) event;
            return windowMenuEvent.getWindow().windowMenuItems(contextMenuComp);
        } else {
            return windowMenuItemsForNoControl(contextMenuComp);
        }
    }

    /**
     * Return the app-specific window menu items for the case where the app
     * doesn't have control.
     */
    private ContextMenuItem[] windowMenuItemsForNoControl(ContextMenuComponent contextMenuComp) {
        contextMenuComp.setShowStandardMenuItems(true);

        ContextMenuItem[] menuItems = new ContextMenuItem[2];

        menuItems[0] = new SimpleContextMenuItem(
                               BUNDLE.getString("Take_Control"),
                               new ContextMenuActionListener() {
                                   public void actionPerformed(ContextMenuItemEvent event) {
                                       app.getControlArb().takeControl();
                                   }
                               });

        if (app.isShownInHUD()) {
            menuItems[1] = new SimpleContextMenuItem(
                               BUNDLE.getString("Remove_from_HUD"),
                               new ContextMenuActionListener() {
                                   public void actionPerformed(ContextMenuItemEvent event) {
                                       app.setShowInHUD(false);
                                   }
                               });
        } else {
            menuItems[1] = new SimpleContextMenuItem(
                               BUNDLE.getString("Show_in_HUD"),
                               new ContextMenuActionListener() {
                                   public void actionPerformed(ContextMenuItemEvent event) {
                                       app.setShowInHUD(true);
                                   }
                               });
        }

        return menuItems;
    }

    /**
     * Returns the pixel scale.
     */
    public Vector2f getPixelScale() {
        return pixelScale;
    }

    /** Returns the view cell factory */
    private View2DCellFactory getViewFactory() {
        View2DCellFactory vFactory = view2DCellFactory;
        if (vFactory == null) {
            vFactory = App2D.getView2DCellFactory();
        }

        if (vFactory == null) {
            throw new RuntimeException(
                    "App2D View2DCellFactory is not defined.");
        }

        return vFactory;
    }

    /** {@inheritDoc} */
    public synchronized View2D createView(Window2D window) {
        View2DCell view =
                (View2DCell) getViewFactory().createView(this, window);

        // This type of cell allows the app to fully control the visibility
        view.setVisibleUser(true);

        if (view != null) {
            views.add(view);
            window.addView(view);
        }

        return view;
    }

    /** {@inheritDoc} */
    public void destroyView(View2D view) {
        if (app == null) {
            return;
        }
        synchronized (app.getAppCleanupLock()) {
            synchronized (this) {
                if (views.remove((View2DCell) view)) {
                    Window2D window = view.getWindow();
                    window.removeView(view);
                    view.cleanup();
                }
            }
        }
    }

    /** {@inheritDoc} */
    public void destroyAllViews() {
        if (app == null) {
            return;
        }
        synchronized (app.getAppCleanupLock()) {
            synchronized (this) {
                LinkedList<View2D> toRemoveList =
                        (LinkedList<View2D>) views.clone();
                for (View2D view : toRemoveList) {
                    Window2D window = view.getWindow();
                    if (window != null) {
                        window.removeView(view);
                    }
                    view.cleanup();
                }
                views.clear();
                toRemoveList.clear();
            }
        }
    }

    /** {@inheritDoc} */
    public synchronized Iterator<? extends View2D> getViews() {
        return views.iterator();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        String str = "App2DCell for cellID=" + getCellID();
        str += ",app=";
        if (app == null) {
            str += "UNKNOWN APP";
        } else {
            str += app.getName();
        }
        return str;
    }

    // TODO: getter
    public void setViewFactory(View2DCellFactory vFactory) {
        view2DCellFactory = vFactory;
    }

    /**
     * Performs the initial move for the cell using a special app-specific first
     * move protocol.
     */
    public void performFirstMove(CellTransform cellTransform) {
        App2DCellPerformFirstMoveMessage msg =
                new App2DCellPerformFirstMoveMessage(
                getCellID(), cellTransform);
        channel.send(msg);
    }

    /**
     * Specify an initial placement width and or height to use instead of the
     * one calculated based on the first visible window size. If size.x is
     * non-zero, it overrides the calculated width in the cell initial placement
     * calculation. If size.y is non-zero, it overrides the calculated height.
     */
    public void setInitialPlacementSize(Vector2f size) {
        if (size == null) {
            initialPlacementSize = size;
        } else {
            initialPlacementSize = new Vector2f(size);
        }
        if (fvi != null) {
            fvi.setInitialPlacementSize(initialPlacementSize);
        }
    }

    public Vector2f getInitialPlacementSize() {
        return initialPlacementSize;
    }

    /**
     * Log this cell's scene graph.
     * <br>
     * FOR DEBUG. INTERNAL ONLY.
     */
    @InternalAPI
    public void logSceneGraph(RendererType rendererType) {
        switch (rendererType) {
            case RENDERER_JME:
                App2DCellRenderer renderer =
                        (App2DCellRenderer) getCellRenderer(rendererType);
                renderer.logSceneGraph();
                break;
            default:
                throw new RuntimeException(
                        "Unsupported cell renderer type: " + rendererType);
        }
    }

    // TODO: Instrumentation for debugging 670: Part 2 of 3: Print out all app cell transform sets.
    // Part 1 is in the constructor.
    // Part 3 is in the client logging.properties.
    private static class MyTransformChangeListener implements TransformChangeListener {

        private static final Logger logger = 
            Logger.getLogger("org.jdesktop.wonderland.modules.appbase.client.cell.App2D.CellTransform");
        
        public void transformChanged(Cell cell, ChangeSource source) {
            logger.info("cell = " + cell.getName() + "(" + cell.getCellID() + ")");
            logger.info("source = " + source);
            CellTransform localTransform = cell.getLocalTransform();
            logger.info("localTransform = " + localTransform);
        }
    }
}
