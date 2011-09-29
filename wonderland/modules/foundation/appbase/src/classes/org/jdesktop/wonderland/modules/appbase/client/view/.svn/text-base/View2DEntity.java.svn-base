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
package org.jdesktop.wonderland.modules.appbase.client.view;

import org.jdesktop.mtgame.Entity;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.image.Texture2D;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import java.awt.Point;
import com.jme.scene.state.TextureState;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.logging.Logger;
import org.jdesktop.mtgame.CollisionComponent;
import org.jdesktop.mtgame.EntityComponent;
import org.jdesktop.mtgame.JMECollisionSystem;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.mtgame.RenderUpdater;
import org.jdesktop.wonderland.client.input.EventListener;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.input.MouseDraggedEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseWheelEvent3D;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.modules.appbase.client.App2D;
import org.jdesktop.wonderland.modules.appbase.client.ControlArb;
import org.jdesktop.wonderland.modules.appbase.client.Window2D;
import org.jdesktop.wonderland.modules.appbase.client.view.View2D.Type;
import java.awt.Button;
import org.jdesktop.wonderland.modules.appbase.client.DrawingSurface;
import org.jdesktop.wonderland.modules.appbase.client.swing.WindowSwing;

/**
 * A view which is capable of displaying a window using an MTGame Entity and scene graph.
 * At any one time the window contents can be displayed in the 3D world (<code>setOrtho(false)</code)
 * or in the ortho plane (<code>setOrtho(true)</code). (The ortho plane usually corresponds to the 
 * Wonderland main HUD, or "on the glass").
 * <br><br>
 * By default, the view is a quad, but this can be changed via the method <code>setGeometryNode</code>.
 * <br><br>
 * If the user resizable attribute is set to true via <code>setUserResizable</code> this view's dimensions
 * can be dynamically controlled from elsewhere (often via a GUI element) by calling the methods 
 * <code>userResizeStart</code>, <code>userResizeUpdate</code> and <code>userResizeFinish</code>.
 * <br><br>
 * A view also supports a number of window manipulation methods which relay actions to the view's window.
 * Examples include windowCloseUser and windowRestackToTop.
 * <br><br>
 * Note: this class supports independent pixel scales for the 3D world and the ortho plane. The values
 * set via <code>setPixelScale</code> are used for the 3D world and the values set via <code>setPixelScaleOrtho</code>
 * are used for the ortho plane.
 * <br><br>
 * This view class also supports a set of positioning methods for when the view is in the ortho plane
 * (e.g. <code>setLocationOrtho</code>) than when the view is in the 3D world (see the <code>View2D</code) class).
 * <br><br>
 * You can move this type of view in the local XY plane when it is in the 3D world by calling 
 * <code>applyDeltaTranslationUser</code>. This post-multiplies a translation matrix into the view's 
 * current world-to-local transform matrix. Certain utility routines exit (e.g. <code>userMovePlanarStart</code>, 
 * <code>userMovePlanarUpdate</code>, and <code>userMovePlanarUpdate</code>) can be called instead in order to make this easier.
 * (Only view's of type SECONDARY can be moved planar).
 * <br><br>
 * TODO: SOMEDAY: Implement move planar for primary views. Must update the cell when doing this.
 * <br><br>
 * @author deronj
 */
@ExperimentalAPI
public abstract class View2DEntity implements View2D {

    // IMPLEMENTATION NOTE: The entity and scene graph of this type of view has the following structure
    // Entity -> viewNode -> geometryNode -> Geometry

    private static final Logger logger = Logger.getLogger(View2DEntity.class.getName());

    private static float PIXEL_SCALE_DEFAULT = 0.01f;

    private static enum AttachState { DETACHED, ATTACHED_TO_ENTITY, ATTACHED_TO_WORLD };

    private static enum FrameChange { 
        ATTACH_FRAME, DETACH_FRAME, REATTACH_FRAME, UPDATE_TITLE, UPDATE_USER_RESIZABLE
    };
            
    // Attribute changed flags 
    protected static final int CHANGED_TYPE             = 0x0001;
    protected static final int CHANGED_PARENT           = 0x0002;
    protected static final int CHANGED_VISIBLE          = 0x0004;
    protected static final int CHANGED_DECORATED        = 0x0008;
    protected static final int CHANGED_GEOMETRY         = 0x0010;
    protected static final int CHANGED_SIZE_APP         = 0x0020;
    protected static final int CHANGED_PIXEL_SCALE      = 0x0040;
    protected static final int CHANGED_OFFSET           = 0x0080;
    protected static final int CHANGED_USER_TRANSFORM   = 0x0100;
    protected static final int CHANGED_TITLE            = 0x0200;
    protected static final int CHANGED_STACK            = 0x0400;
    protected static final int CHANGED_ORTHO            = 0x0800;
    protected static final int CHANGED_LOCATION_ORTHO   = 0x1000;
    protected static final int CHANGED_TEX_COORDS       = 0x2000;
    protected static final int CHANGED_USER_RESIZABLE   = 0x4000;

    protected static final int CHANGED_ALL = -1;

    /** The app to which the view belongs. */
    private App2D app;

    /** The name of the view. */
    protected String name;

    /** The entity of this view. */
    protected Entity entity;

    /** The parent entity to which this entity is connected. */
    private Entity parentEntity;

    /** The base node of the view. */
    protected Node viewNode;

    /** The textured 3D object in which displays the view contents. */
    protected GeometryNode geometryNode;

    /** The new geometry object to be used. */
    protected GeometryNode newGeometryNode;

    /** Whether we have created the geometry node ourselves. */
    private boolean geometrySelfCreated;

    /** The control arbitrator of the window being displayed. */
    private ControlArb controlArb;

    /** The view's window. */
    protected Window2D window;

    /** The type of this view. */
    protected Type type = Type.UNKNOWN;

    /** The parent view of this view. */
    protected View2DEntity parent;

    /** Whether the app wants the view to be visible. */
    private boolean visibleApp;

    /** Whether the user wants the view to be visible. */
    private boolean visibleUser;

    /** Whether this view should be decorated by a frame. */
    private boolean decorated;

    /** Whether this view's frame resize corner is enabled. */
    protected boolean userResizable = false;

    /** The frame title. */
    private String title;

    /** The size of the view specified by the app. */
    private Dimension sizeApp = new Dimension(1, 1);

    /** The size of the displayed pixels (in local units) when view is in cell mode. */
    private Vector2f pixelScaleCell;

    /** The size of the displayed pixels (in local units) when view is in ortho mode. */
    private Vector2f pixelScaleOrtho;

    /** The location of the view when view is in ortho mode. */
    private Vector2f locationOrtho = new Vector2f(0, 0);

    /** The interactive GUI object for this view. */
    private Gui2DInterior gui;

    /** The local offset translation from the center of the parent to the center of this view. */
    private Vector2f offset = new Vector2f(0f, 0f);

    /** The pixel offset translation from the top left corner of the parent to the top left corner of this view. */
    private Point pixelOffset = new Point(0, 0);

    /** The previous drag vector during an interactive planar move. */
    private Vector2f userMovePlanarDragVectorPrev;
    
    /** The next delta translation to apply. */
    private Vector3f deltaTranslationToApply;

    /** A copy of the current view node's user transformation in world (aka non-ortho) mode. */
    protected CellTransform userTransformCell = new CellTransform(null, null);

    /** True if the user transform cell was changed by an entire matrix replacement */
    protected boolean userTransformCellReplaced = false;

    /** True if we shouldn't bother informing other clients of modifications to the user transform cell. */
    protected boolean userTransformCellChangedLocalOnly = false;

    /** A copy of the current view node's user transformation in ortho mode. */
    private CellTransform userTransformOrtho = new CellTransform(null, null);

    /** The event listeners which are attached to this view while the view is attached to its cell */
    private LinkedList<EventListener> eventListeners = new LinkedList<EventListener>();

    /** The view's which are children of this view. */
    private LinkedList<View2DEntity> children = new LinkedList<View2DEntity>();

    /* The set of changes which have occurred since the last update. */
    protected int changeMask;

    /** Whether the view entity is to be displayed in ortho mode ("on the glass"). */
    private boolean ortho;
    
    /** Whether the view entity is attached and to what. */
    private AttachState attachState = AttachState.DETACHED;

    /** A dummy AWT component (used by deliverEvent). */
    private static Button dummyButton = new Button();

    /*
     ** TODO: WORKAROUND FOR A WONDERLAND PICKER PROBLEM:
     ** TODO: >>>>>>>> Is this obsolete in 0.5?
     **
     ** We cannot rely on the x and y values in the intersection info coming from LG
     ** for mouse release events.
     ** The problem is both the same for the X11 and AWT pickers. The LG pickers are
     ** currently defined to set the node info of all events interior to and terminating
     ** a grab to the node info of the button press which started the grab. Not only is
     ** the destination node set (as is proper) but also the x and y intersection info
     ** (which is dubious and, I believe, improper). Note that there is a hack in both
     ** pickers to work around this problem for drag events, which was all LG cared about
     ** at the time. I don't want to perturb the semantics of the LG pickers at this time,
     ** but in the future the intersection info must be dealt with correctly for all
     ** events interior to and terminating a grab. Note that this problem doesn't happen
     ** with button presses, because these start grabs.
     **
     ** For now I'm simply going to treat the intersection info in button release events
     ** as garbage and supply the proper values by tracking the pointer position myself.
     */
    private boolean pointerMoveSeen = false;
    private int pointerLastX;
    private int pointerLastY;

    /** A list of frame changes to be performed outside the window lock. */
    private LinkedList<FrameChange> frameChanges = new LinkedList<FrameChange>();

    /** A flag which indicates that the cleanup method is being executed. */
    private boolean inCleanup = false;

    // TODO: HACK: Part 1 of 4: temporary workaround for 951
    // Parts 2 and 3 are later in this file. Part 4 is in HUDView3D.
    private float hackZEpsilon = 0f;

    /**
     * Create an instance of View2DEntity with default geometry node.
     * @param The entity in which the view is displayed.
     * @param window The window displayed in this view.
     */
    public View2DEntity (Window2D window) {
        this(window, null);
    }

    /**
     * Create an instance of View2DEntity with a specified geometry node.
     * @param window The window displayed in this view.
     * @param geometryNode The geometry node on which to display the view.
     *
     * NOTE: the subclass must force a complete update after calling this constructor, as follows:
     *
     * changeMask = CHANGED_ALL;
     * update(); 
     */
    public View2DEntity (Window2D window, GeometryNode geometryNode) {
        this.window = window;
        this.newGeometryNode = geometryNode;

        name = "View for " + window.getName();

        // Create entity and node
        entity = new Entity("Entity for " + name);
        viewNode = new Node("Node for " + name);
        RenderComponent rc =
            ClientContextJME.getWorldManager().getRenderManager().createRenderComponent(viewNode);
        entity.addComponent(RenderComponent.class, rc);
        entityMakePickable(entity);

        // Create input-related objects 
        gui = new Gui2DInterior(this);
        gui.attachEventListeners(entity);
        controlArb = getWindow().getApp().getControlArb();

        logger.info("View2DEntity created: " + this);
    }

    /** {@inheritDoc} */
    public synchronized void cleanup () {
        inCleanup = true;

        changeMask = 0;
        disableGUI();

        setParent(null);
        setVisibleUser(false, false);
        setOrtho(false, false);
        setGeometryNode(null, false);
        update();
        // Note: don't do an updateFrame here because a deadlock can result!
        children.clear();

        if (gui != null) {
            gui.detachEventListeners(entity);
            gui.cleanup();
            gui = null;
        }


        if (geometryNode != null) {
            viewNode.detachChild(geometryNode);
            if (geometrySelfCreated) {
                geometryNode.cleanup();
                geometrySelfCreated = false;
            }
            geometryNode = null;
            newGeometryNode = null;
        }

        if (entity != null) {

            // Make sure that entity listeners and components are really gone
            // There is a case with movement into or out of the HUD where these
            // might get stuck on the entity
            for (EventListener listener : eventListeners) {
                listener.removeFromEntity(entity);
            }
            entity = null;
        }

        viewNode = null;
        controlArb = null;
        window = null;
        app = null;

        inCleanup = false;
    }

    /** 
     * TODO: HACK: Part 2 of 4: temporary workaround for 951
     * @deprecated
     */
    @InternalAPI
    public void setHackZEpsilon (float epsilon) {
        hackZEpsilon = epsilon;
    }

    /** {@inheritDoc} */
    public abstract View2DDisplayer getDisplayer ();

    /** Return this view's entity. */
    public Entity getEntity () {
        return entity;
    }

    /** Return this view's root node. */
    public Node getNode () {
        return viewNode;
    }

    /** {@inheritDoc} */
    public String getName () {
        return name;
    }

    /** 
     * {@inheritDoc}
     */
    public Window2D getWindow () {
        return window;
    }

    /** 
     * INTERNAL API.
     * <br>
     * Returns the root scene graph node of this view.
     */
    public Node getViewNode () {
        return viewNode;
    }

    /** 
     * INTERNAL API
     * <br>
     * Disables the GUI2D of this view. Used only by FrameHeaderSwing.
     */
    @InternalAPI
    public void disableGUI () {
        if (gui != null) {
            gui.detachEventListeners(entity);
            gui.cleanup();
            gui = null;
        }
    }

    /** {@inheritDoc} */
    public synchronized void setType (Type type) {
        setType(type, true);
    }

    /** {@inheritDoc} */
    public synchronized void setType (Type type, boolean update) {
        if (this.type == type) return;

        // Validate type argument
        if (this.type == Type.UNKNOWN) {
            // All new types are permitted
        } else if (this.type == Type.SECONDARY && type == Type.PRIMARY) {
            // A promotion of a secondary to a primary is permitted.
        } else if (this.type == Type.PRIMARY && type == Type.SECONDARY) {
            // A demotion of a primary to a secondary is permitted.
            // AppXrw.selectPrimaryWindow sometimes does this.
        } else if (this.type == Type.SECONDARY && type == Type.POPUP) {
            // A change of a secondary to a popup is permitted.
        } else {
            // No other type changes are permitted.
            logger.severe("Old view type = " + this.type);
            logger.severe("New view type = " + type);
            throw new RuntimeException("Invalid type change.");
        }
            
        logger.info("view = " + this);
        logger.info("change type = " + type);

        this.type = type;
        changeMask |= CHANGED_TYPE;
        if (update) {
            update();
            if (!inCleanup) {
                updateFrame();
            }
        }
    }

    /** {@inheritDoc} */
    public Type getType () {
        return type;
    }

    /** 
     * {@inheritDoc}
     * <br><br>
     * This also has the side effect of setting the ortho attribute of this
     * view (and all of its children) to the ortho attribute of the parent.
     */
    public synchronized void setParent (View2D parent) {
        setParent(parent, true);
    }

    /** 
     * {@inheritDoc}
     * <br><br>
     * This also has the side effect of setting the ortho attribute of this
     * view (and all of its children) to the ortho attribute of the parent.
     */
    public synchronized void setParent (View2D parent, boolean update) {
        if (this.parent == parent) return;

        // Detach this view from previous parent
        if (this.parent != null) {
            this.parent.children.remove(this);
        }

        logger.info("view = " + this);
        logger.info("change parent of view " + this + " to new parent view = " + parent);

        this.parent = (View2DEntity) parent;

        // Attach view to new parent
        if (this.parent != null) {
            this.parent.children.add(this);
        }

        changeMask |= CHANGED_PARENT;
        if (update) {
            update();
            if (!inCleanup) {
                updateFrame();
            }
        }

        // Inherit ortho state of parent (must do this after self update)
        if (this.parent != null) {
            // Update immediately
            setOrtho(this.parent.isOrtho());
        }
    }

    /** {@inheritDoc} */
    public View2D getParent () {
        return parent;
    }

    /** {@inheritDoc} */
    public synchronized void setVisibleApp (boolean visibleApp) {
        setVisibleApp(visibleApp, true);
    }

    /** {@inheritDoc} */
    public synchronized void setVisibleApp (boolean visibleApp, boolean update) {
        if (this.visibleApp == visibleApp) return;
        logger.info("view = " + this);
        logger.info("change visibleApp = " + visibleApp);
        this.visibleApp = visibleApp;
        changeMask |= CHANGED_VISIBLE;
        if (update) {
            update();
            if (!inCleanup) {
                updateFrame();
            }
        }
    }

    /** {@inheritDoc} */
    public boolean isVisibleApp () {
        return visibleApp;
    }

    /** {@inheritDoc} */
    public synchronized void setVisibleUser (boolean visibleUser) {
        setVisibleUser(visibleUser, true);
    }

    /** {@inheritDoc} */
    public synchronized void setVisibleUser (boolean visibleUser, boolean update) {
        if (this.visibleUser == visibleUser) return;
        logger.info("view = " + this);
        logger.info("change visibleUser = " + visibleUser);
        this.visibleUser = visibleUser;
        changeMask |= CHANGED_VISIBLE;
        if (update) {
            update();
            if (!inCleanup) {
                updateFrame();
            }
        }
    }

    /** {@inheritDoc} */
    public boolean isVisibleUser () {
        return visibleUser;
    }

    /** Recalculates the visibility of this view. */
    private synchronized void updateVisibility (boolean inCleanupParent) {
        changeMask |= CHANGED_VISIBLE;
        update();
        if (!inCleanup && !inCleanupParent) {
            updateFrame();
        }
    }

    /** {@inheritDoc} */
    public boolean isActuallyVisible () {
        logger.info("Check actually visible for view " + this);
        logger.info("visibleApp = " + visibleApp);
        logger.info("visibleUser = " + visibleUser);
        if (!visibleApp || !visibleUser) return false;
        return true;
    }

    /** {@inheritDoc} */
    public synchronized void setDecorated (boolean decorated) {
        setDecorated(decorated, true);
    }

    /** {@inheritDoc} */
    public synchronized void setDecorated (boolean decorated, boolean update) {
        if (this.decorated == decorated) return;
        logger.info("view = " + this);
        logger.info("change decorated = " + decorated);
        this.decorated = decorated;
        changeMask |= CHANGED_DECORATED;
        if (update) {
            update();
            if (!inCleanup) {
                updateFrame();
            }
        }
    }

    /** {@inheritDoc} */
    public boolean isDecorated () {
        return decorated;
    }

    /** {@inheritDoc} */
    public synchronized void setTitle (String title) {
        setTitle(title, true);
    }

    /** {@inheritDoc} */
    public synchronized void setTitle (String title, boolean update) {
        this.title = title;
        changeMask |= CHANGED_TITLE;
        if (update) {
            update();
            if (!inCleanup) {
                updateFrame();
            }
        }
    }

    /** {@inheritDoc} */
    public String getTitle () {
        return title;
    }

    /** {@inheritDoc} */
    public synchronized void setUserResizable (boolean userResizable) {
        setUserResizable(userResizable, true);
    }

    /** {@inheritDoc} */
    public synchronized void setUserResizable (boolean userResizable, boolean update) {
        if (this.userResizable == userResizable) return;
        logger.info("view = " + this);
        logger.info("change userResizable = " + userResizable);
        this.userResizable = userResizable;
        changeMask |= CHANGED_USER_RESIZABLE;
        if (update) {
            update();
            if (!inCleanup) {
                updateFrame();
            }
        }
    }

    /** {@inheritDoc} */
    public boolean isUserResizable () {
        return userResizable;
    }

    /** 
     * {@inheritDoc}
     */
    public void stackChanged (boolean update) {
        changeMask |= CHANGED_STACK;
        if (update) {
            update();
            if (!inCleanup) {
                updateFrame();
            }
        }
    }

    /** 
     * Specify the portion of the window which is displayed by this view when it is in cell mode.
     * Update afterward.
       TODO: notyet: public void setWindowAperture (Rectangle aperture);
     */

    /** 
     * Specify the portion of the window which is displayed by this view when it is in cell mode. 
     * Update if specified.
       TODO: notyet: public void setWindowAperture (Rectangle aperture, boolean update);
     */

    /** 
     * Return the portion of the window which is displayed by this view when it is in cell mode. 
       TODO: notyet: public Rectangle getWindowAperture ();
     */

    /** 
     * Specify the portion of the window which is displayed by this view when it is in ortho mode. 
     * Update afterward.
       TODO: notyet: public void setWindowApertureOrtho (Rectangle aperture);
     */

    /** 
     * Specify the portion of the window which is displayed by this view when it is in ortho mode. 
     * Update if specified.
       TODO: notyet: public void setWindowApertureOrtho (Rectangle aperture, boolean update);
     */

    /** 
     * Return the portion of the window which is displayed by this view when it is in ortho mode. 
       TODO: notyet: public Rectangle getWindowApertureOrtho ();
     */

    /** {@inheritDoc} */
    public synchronized void setGeometryNode (GeometryNode geometryNode) {
        setGeometryNode(geometryNode, true);
    }

    /** {@inheritDoc} */
    public synchronized void setGeometryNode (GeometryNode geometryNode, boolean update) {
        logger.info("view = " + this);
        logger.info("change geometryNode = " + geometryNode);
        newGeometryNode = geometryNode;
        changeMask |= CHANGED_GEOMETRY;
        if (update) {
            update();
            if (!inCleanup) {
                updateFrame();
            }
        }
    }
    
    /** {@inheritDoc} */
    public GeometryNode getGeometryNode () {
        return geometryNode;
    }

    /** {@inheritDoc} */
    public synchronized void setSizeApp (Dimension size) {
        setSizeApp(size, true);
    }

    /** {@inheritDoc} */
    public synchronized void setSizeApp (Dimension size, boolean update) {
        if (size.width == sizeApp.width && size.height == sizeApp.height) return;

        logger.info("view = " + this);
        logger.info("change sizeApp = " + sizeApp);

        sizeApp = (Dimension) size.clone();

        // Note: AWT doesn't like zero image sizes
        sizeApp.width = (sizeApp.width <= 0) ? 1 : sizeApp.width;
        sizeApp.height = (sizeApp.height <= 0) ? 1 : sizeApp.height;

        changeMask |= CHANGED_SIZE_APP;
        if (update) {
            update();
            if (!inCleanup) {
                updateFrame();
            }
        }
    }

    /** {@inheritDoc} */
    public Dimension getSizeApp () {
        return (Dimension) sizeApp.clone();
    }

    /** {@inheritDoc} */
    public float getDisplayerLocalWidth () {
        // TODO: ignore size mode and user size for now - always track window size as specified by app
        return getPixelScaleCurrent().x * sizeApp.width;
    }

    /** {@inheritDoc} */
    public float getDisplayerLocalHeight () {
        // TODO: ignore size mode and user size for now - always track window size as specified by app
        return getPixelScaleCurrent().y * sizeApp.height;
    }

    /** 
     * A window close which comes from the user. Close the window of this view.
     */
    public void windowCloseUser () {
        window.closeUser();
    }

    /** {@inheritDoc} */
    public void windowRestackToTop () {
        window.restackToTop();
    }

    /** {@inheritDoc} */
    public void windowRestackToBottom () {
        window.restackToBottom();
    }

    /** {@inheritDoc} */
    public void windowRestackAbove (View2D sibling) {
        if (sibling == null) {
            window.restackToTop();
        } else {
            window.restackAbove(sibling.getWindow());
        }
    }

    /** {@inheritDoc} */
    public void windowRestackBelow (View2D sibling) {
        if (sibling == null) {
            window.restackToBottom();
        } else {
            window.restackBelow(sibling.getWindow());
        }
    }

    /** 
     * Specify the size of the displayed pixels used when the view is in cell mode. 
     * Update afterward. This defaults to the initial pixel scale of the window.
     */
    public synchronized void setPixelScale (Vector2f pixelScale) {
        setPixelScale(pixelScale, true);
    }

    /** 
     * Specify the size of the displayed pixels used when the view is in cell mode. 
     * Update if specified. Defaults to the pixel scale of the window.
     */
    public synchronized void setPixelScale (Vector2f pixelScale, boolean update) {
        logger.info("view = " + this);
        logger.info("change pixelScale cell = " + pixelScale);
        this.pixelScaleCell = pixelScale.clone();
        changeMask |= CHANGED_PIXEL_SCALE;
        if (update) {
            update();
            if (!inCleanup) {
                updateFrame();
            }
        }
    }

    /** Return the pixel scale used when the view is displayed in cell mode. */
    public Vector2f getPixelScale () {
        if (pixelScaleCell == null) {
            if (window != null) {
                return window.getPixelScale();
            } else {
                return new Vector2f(0.01f, 0.01f);
            }
        } else {
            return pixelScaleCell.clone();
        }
    }

    /** 
     * Specify the size of the displayed pixels used when the view is in ortho mode. 
     * Update afterward. This defaults to (1.0, 1.0).
     */
    public synchronized void setPixelScaleOrtho (Vector2f pixelScale) {
        setPixelScaleOrtho(pixelScale, true);
    }

    /** 
     * Specify the size of the displayed pixels used when the view is in ortho mode. 
     * Update if specified. This defaults to (1.0, 1.0).
     */
    public synchronized void setPixelScaleOrtho (Vector2f pixelScale, boolean update) {
        logger.info("view = " + this);
        logger.info("change pixelScale ortho = " + pixelScale);
        this.pixelScaleOrtho = pixelScale.clone();
        changeMask |= CHANGED_PIXEL_SCALE;
        if (update) {
            update();
            if (!inCleanup) {
                updateFrame();
            }
        }
    }

    /** Return the pixel scale used when the view is displayed in ortho mode. */
    public Vector2f getPixelScaleOrtho () {
        if (pixelScaleOrtho == null) {
            return new Vector2f(1f, 1f);
        } else {
            return pixelScaleOrtho.clone();
        }
    }

    /** Returns the pixel scale vector for the current mode. */
    public Vector2f getPixelScaleCurrent () {
        Vector2f pixelScale;
        if (ortho) {
            pixelScale = getPixelScaleOrtho();
        } else {
            pixelScale = getPixelScale();
        }
        return pixelScale;
    }

    /**
     * Specify the location of a primary view used when the view is in ortho mode.
     * The location is an offset relative to the origin of the displayer and is in
     * the coordinate system of the ortho plane. Update afterward.
     * This attribute is ignored for non-primary views.
     *
     * Note: setPixelOffset is the other part of the ortho offset translation. The two offsets are 
     * added to produce the effective offset.
     *
     * Note: there is no corresponding attribute for cell mode because the cell itself automatically
     * controls the location of a primary view within the cell (usually centered) and the cell location
     * within the world is derived from WFS and other input. 
     */
    public synchronized void setLocationOrtho (Vector2f location) {
        setLocationOrtho(location, true);
    }

    /**
     * Specify the location of a primary view used when the view is in ortho mode.
     * The location is an offset relative to the origin of the displayer and is in
     * the coordinate system of the ortho plane. Update if specified.
     * This attribute is ignored for non-primary views.
     *
     * Note: there is no corresponding attribute for cell mode because the cell itself automatically
     * controls the location of a primary view within the cell (usually centered) and the cell location
     * within the world is derived from WFS and other input. 
     */
    public synchronized void setLocationOrtho (Vector2f location, boolean update) {
        if (locationOrtho.x == location.x && locationOrtho.y == location.y) return;
        logger.info("view = " + this);
        logger.info("change location ortho = " + location);
        locationOrtho = location.clone();
        changeMask |= CHANGED_LOCATION_ORTHO;
        if (update) {
            update();
            if (!inCleanup) {
                updateFrame();
            }
        }
    }

    /**
     * Returns the location used when this view is in ortho mode.
     */
    public Vector2f getLocationOrtho () {
        return locationOrtho.clone();
    }

    /**
     * Issue 151: Returns the corresponding component location for locationOrtho.
     */
    public Point getLocation() {
    	return
    		new Point(
    			(int)(locationOrtho.x - getDisplayerLocalWidth() / 2),
    			(int)(locationOrtho.y - getDisplayerLocalHeight() / 2)
    		);
    }
    
    /** {@inheritDoc} */
    public synchronized void setOffset(Vector2f offset) {
        setOffset(offset, true);
    }

    /** {@inheritDoc} */
    public synchronized void setOffset(Vector2f offset, boolean update) {
        if (this.offset.x == offset.x && this.offset.y == offset.y) return;
        logger.info("view = " + this);
        logger.info("change offset = " + offset);
        this.offset = (Vector2f) offset.clone();
        changeMask |= CHANGED_OFFSET;
        if (update) {
            update();
            if (!inCleanup) {
                updateFrame();
            }
        }
    }

    /** {@inheritDoc} */
    public Vector2f getOffset () {
        return (Vector2f) offset.clone();
    }

    /** Called for children to mark them as changed when the parent view size changes. */
    private void changeOffsetSelfAndChildren () {
        changeMask |= CHANGED_OFFSET;
        for (View2DEntity childView : children) {
            childView.changeOffsetSelfAndChildren();
        }
    }

    /** {@inheritDoc} */
    public synchronized void setPixelOffset(Point pixelOffset) {
        setPixelOffset(pixelOffset, true);
    }

    /** {@inheritDoc} */
    public synchronized void setPixelOffset(Point pixelOffset, boolean update) {
        if (this.pixelOffset.x == pixelOffset.x && this.pixelOffset.y == pixelOffset.y) return;
        logger.info("view = " + this);
        logger.info("change pixelOffset = " + pixelOffset);
        this.pixelOffset = (Point) pixelOffset.clone();
        changeMask |= CHANGED_OFFSET;
        if (update) {
            update();
            if (!inCleanup) {
                updateFrame();
            }
        }
    }

    /** {@inheritDoc} */
    public Point getPixelOffset () {
        return (Point) pixelOffset.clone();
    }

    /** {@inheritDoc} */
    public void applyDeltaTranslationUser (Vector3f deltaTranslation) {
        applyDeltaTranslationUser(deltaTranslation, true);
    }

    /** {@inheritDoc} */
    public void applyDeltaTranslationUser (Vector3f deltaTranslation, boolean update) {
        deltaTranslationToApply = deltaTranslation.clone();
        changeMask |= CHANGED_USER_TRANSFORM;
        if (update) {
            update();
            if (!inCleanup) {
                updateFrame();
            }
        }
    }

    /**
     * Called by the UI to indicate the start of an interactive planar move. 
     */
    public synchronized void userMovePlanarStart () {
        userMovePlanarDragVectorPrev = new Vector2f();
    }

    /**
     * Called by the UI to indicate a drag vector update during interactive planar move. 
     */
    public synchronized void userMovePlanarUpdate (Vector2f dragVector) {
        
        // Calculate the delta of the movement since the last update
        Vector2f deltaDragVector = dragVector.clone();
        deltaDragVector.subtractLocal(userMovePlanarDragVectorPrev);
        userMovePlanarDragVectorPrev = dragVector;

        applyDeltaTranslationUser(new Vector3f(deltaDragVector.x, deltaDragVector.y, 0f), true);
    }

    public synchronized void userMovePlanarFinish () {
    }

    private Dimension userResizeCalcWindowNewSize (Vector2f dragVector) {
        if (dragVector.x == 0 && dragVector.y == 0) return null;

        // Convert local coords drag vector to a pixel vector
        Vector2f pixelScale = getPixelScaleCurrent();
        //System.err.println("pixelScale = " + pixelScale);
        int deltaWidth = (int)(dragVector.x / pixelScale.x);
        int deltaHeight = (int)(dragVector.y / pixelScale.y);

        //System.err.println("deltaWH = " + deltaWidth + ", " + deltaHeight);

        // TODO: for now, we only support the resize mode where resizing the view
        // directly updates the window size.
        int width = window.getWidth();
        int height = window.getHeight();
        //System.err.println("Old size, wh = " + width + ", " + height);
        width += deltaWidth;
        height -= deltaHeight;
        if (width < 1) width = 1;
        if (height < 1) height = 1;
        //System.err.println("New size, wh = " + width + ", " + height);

        return new Dimension(width, height);
    }

    /**
     * Called by the UI to indicate the start of an interactive resize.
     */
    public void userResizeStart () {
    }

    /**
     * Called by the UI to indicate a drag vector update during interactive resize.
     * This is called on the EDT.
     */
    public void userResizeUpdate (Vector2f dragVector) {
    }

    public void userResizeFinish () {
    }

    protected void userResizeFrameUpdate (float width3D, float height3D, Dimension newSize) {
    }

    public void updateViewSizeOnly (int x, int y, int width, int height, 
                                    float newViewWidth3D, float newViewHeight3D,
                                    Dimension parentViewNewSize) {
        final Vector3f trans = new Vector3f();

        trans.x = offset.x;
        trans.y = offset.y;

        if (type != Type.PRIMARY && type != Type.UNKNOWN && parent != null) {

            // Convert pixel offset to local coords and add it in
            // TODO: does the width/height need to include the scroll bars?
            Vector2f pixelScale = getPixelScaleCurrent();
            Dimension parentSize = parent.getSizeApp();  // TODO: is this now unused?
            trans.y += parentViewNewSize.height * pixelScale.y / 2f;
            trans.y -= y * pixelScale.y / 2f;
        }

        final float width3D = getPixelScaleCurrent().x * width;
        final float height3D = getPixelScaleCurrent().y * height;

        Image image = getWindow().getTexture().getImage();
        final float widthRatio = (float)width / image.getWidth();
        final float heightRatio = (float)height / image.getHeight();

        ClientContextJME.getWorldManager().addRenderUpdater(new RenderUpdater() {
            public void update(Object arg0) {
                geometryNode.setLocalTranslation(trans);
                geometryNode.setSize(width3D, height3D);
                geometryNode.setTexCoords(widthRatio, heightRatio);
                ClientContextJME.getWorldManager().addToUpdateList(viewNode);
            }
        }, null);
    }

    /** TODO: NOTYET
    public synchronized void userMoveZStart (float dy) {
    }

    public synchronized void userMoveZUpdate (float dy) {
    }

    public synchronized void userMoveZFinish () {
    }
    */

    /**
     * Specifies whether the view entity is to be displayed in ortho mode ("on the glass").
     * Update immediately. In addition, the ortho attribute of all descendents is set to the
     * same value. And, when ortho is true, the decorated attribute is ignored and is regarded
     * as false.
     */
    public synchronized void setOrtho (boolean ortho) {
        setOrtho(ortho, true);
    }

    /**
     * Specifies whether the view entity is to be displayed in ortho mode ("on the glass").
     * Update if specified. This also changes the ortho attribute of all descendent views.
     */
    public synchronized void setOrtho (boolean ortho, boolean update) {
        if (this.ortho == ortho) return;
        logger.info("view = " + this);
        logger.info("change ortho = " + ortho);
        this.ortho = ortho;
        changeMask |= CHANGED_ORTHO;

        logger.info("Make corresponding ortho changes to descendents");
        logger.info("Num children = " + children.size());
        for (View2DEntity child : children) {
            child.setOrtho(ortho, false);
        }

        if (update) {
            update();
            if (!inCleanup) {
                updateFrame();
            }
        }
    }

    /**
     * Returns whether the view entity is in ortho mode.
     */
    public boolean isOrtho () {
        return ortho;
    }

    private void logChangeMask (int mask) {
        logger.info("changeMask " + Integer.toHexString(mask));
        int bit = 0x1;
        for (int i = 0; i < 32; i++, bit <<= 1) {
            int thisBit = mask & bit;
            if (thisBit != 0) {
                String str;
                switch (thisBit) {
                case CHANGED_TYPE:
                    str = "CHANGED_TYPE";
                    break;
                case CHANGED_PARENT:
                    str = "CHANGED_PARENT";
                    break;
                case CHANGED_VISIBLE:
                    str = "CHANGED_VISIBLE";
                    break;
                case CHANGED_DECORATED:
                    str = "CHANGED_DECORATED";
                    break;
                case CHANGED_GEOMETRY:
                    str = "CHANGED_GEOMETRY";
                    break;
                case CHANGED_SIZE_APP:
                    str = "CHANGED_SIZE_APP";
                    break;
                case CHANGED_PIXEL_SCALE:
                    str = "CHANGED_PIXEL_SCALE";
                    break;
                case CHANGED_OFFSET:
                    str = "CHANGED_OFFSET";
                    break;
                case CHANGED_USER_TRANSFORM:
                    str = "CHANGED_USER_TRANSFORM";
                    break;
                case CHANGED_TITLE:
                    str = "CHANGED_TITLE";
                    break;
                case CHANGED_STACK:
                    str = "CHANGED_STACK";
                    break;
                case CHANGED_ORTHO:
                    str = "CHANGED_ORTHO";
                    break;
                case CHANGED_LOCATION_ORTHO:
                    str = "CHANGED_LOCATION_ORTHO";
                    break;
                case CHANGED_TEX_COORDS:
                    str = "CHANGED_TEX_COORDS";
                    break;
                case CHANGED_USER_RESIZABLE:
                    str = "CHANGED_USER_RESIZABLE";
                    break;
                default:
                    continue;
                }

                // Printed selected values
                String str2 = null;
                switch (thisBit) {
                case CHANGED_TYPE:
                    str2 = ": type = " + type;
                    break;
                case CHANGED_PARENT:
                    str2 = ": parent = " + parent;
                    break;
                case CHANGED_VISIBLE:
                    str2 = ": visibleApp = " + visibleApp + ", visibleUser = " + visibleUser;
                    break;
                case CHANGED_DECORATED:
                    str2 = ": decorated = " + decorated;
                    break;
                case CHANGED_GEOMETRY:
                    break;
                case CHANGED_SIZE_APP:
                    str2 = ": sizeApp = " + sizeApp;
                    break;
                case CHANGED_PIXEL_SCALE:
                    str2 = ": pixelScaleCell = " + pixelScaleCell + ", pixelScaleOrtho = " + pixelScaleOrtho;
                    break;
                case CHANGED_OFFSET:
                    str2 = ": offset = " + offset + ", pixelOffset = " + pixelOffset;
                    break;
                case CHANGED_USER_TRANSFORM:
                    str2 = ": deltaTranslationToApply = " + deltaTranslationToApply;
                    break;
                case CHANGED_TITLE:
                    str2 = ": title = " + title;
                    break;
                case CHANGED_STACK:
                    break;
                case CHANGED_ORTHO:
                    str2 = ": ortho = " + ortho;
                    break;
                case CHANGED_LOCATION_ORTHO:
                    str2 = ": locationOrtho = " + locationOrtho;
                    break;
                case CHANGED_TEX_COORDS:
                    break;
                case CHANGED_USER_RESIZABLE:
                    str2 = ": userResizable = " + userResizable;
                    break;
                }

                str += "(" + Integer.toHexString(thisBit) + ")";
                if (str2 != null) {
                    str += str2;
                }
                logger.info(str);
            }
        }
    }

    /** {@inheritDoc} */
    public synchronized void update () {
        // Only-Update-When-Visible Optimization
        // 1. Always perform any visibility or size changes immediately.
        if ((changeMask & (CHANGED_VISIBLE | CHANGED_SIZE_APP)) == 0) {
            // 2. Don't perform other changes unless the view if both the app and the user 
            // have made it visible.
            if (!visibleApp || !visibleUser) {
                return;
            }
        }

        // Note: all of the scene graph changes are queued up and executed at the end
        boolean windowNeedsValidate = false;

        // For Debug - Part 1: Uncomment this to print info for HUD views only
        //if (!("View2DCell".equals(this.getClass().getName()))) {

        logger.info("------------------ Processing changes for view " + this);
        logger.info("type " + type);
        logChangeMask(changeMask);

        // For Debug - Part 2: Uncomment this to print info for HUD views only
        //        }

        // React to topology related changes
        if ((changeMask & (CHANGED_GEOMETRY | CHANGED_SIZE_APP | CHANGED_TYPE | CHANGED_PARENT | 
                           CHANGED_VISIBLE | CHANGED_ORTHO)) != 0) {
            logger.fine("Update topology for view " + this);
            
            // First, detach entity (if necessary)
            switch (attachState) {
            case ATTACHED_TO_ENTITY:
                if (parentEntity != null) {
                    logger.fine("Remove entity " + entity + " from parent entity " + parentEntity);
                    RenderComponent rc = (RenderComponent) entity.getComponent(RenderComponent.class);
                    sgChangeAttachPointSetAddEntity(rc, null, null, null);
                    parentEntity.removeEntity(entity);
                    parentEntity = null;
                }
                break;
            case ATTACHED_TO_WORLD:
                logger.fine("Remove entity " + entity + " from world manager.");
                ClientContextJME.getWorldManager().removeEntity(entity);
                break;
            }
            attachState = AttachState.DETACHED;

            // Does the geometry node itself need to change?
            if ((changeMask & CHANGED_GEOMETRY) != 0) {
                if (geometryNode != null) {
                    // Note: don't need to do in RenderUpdater because we've detached our entity
                    sgChangeGeometryDetachFromView(viewNode, geometryNode);
                    if (geometrySelfCreated) {
                        sgChangeGeometryCleanup(geometryNode);
                        geometrySelfCreated = false;
                    }
                }
                if (newGeometryNode != null) {
                    geometryNode = newGeometryNode;
                    newGeometryNode = null;
                } else {
                    geometryNode = new GeometryNodeQuad(this);
                    geometrySelfCreated = true;
                }
                // Note: don't need to do in RenderUpdater because we've detached our entity
                sgChangeGeometryAttachToView(viewNode, geometryNode);
            }

            // Uses: window
            if ((changeMask & (CHANGED_GEOMETRY | CHANGED_SIZE_APP)) != 0) {
                logger.fine("Update texture for view " + this);
                if (geometryNode != null) {
                    DrawingSurface surface = getWindow().getSurface();
                    if (surface != null) {
                        sgChangeGeometryTextureSet(geometryNode, getWindow().getTexture(), surface);
                        windowNeedsValidate = true;
                    }
                }
            }

            // Now reattach geometry if view should be visible
            // Note: MTGame can currently only setOrtho on a visible rc
            // Uses: visible, ortho
            if (isActuallyVisible()) {
                if (ortho) {
                    logger.fine("View is ortho for view " + this);
                    entity.getComponent(RenderComponent.class).setOrtho(true);

                    if (type == Type.PRIMARY  || type == Type.UNKNOWN) {
                        // Attach top level ortho views directly to world
                        ClientContextJME.getWorldManager().addEntity(entity);
                        attachState = AttachState.ATTACHED_TO_WORLD;
                        logger.fine("Attached entity " + entity + " to world manager.");
                    } else {
                        parentEntity = getParentEntity();
                        if (parentEntity == null) {
                            // Has no Parent; attach directly to world
                            ClientContextJME.getWorldManager().addEntity(entity);
                            attachState = AttachState.ATTACHED_TO_WORLD;
                            logger.fine("Attached parentless entity " + entity + " to world manager.");
                        } else {
                            RenderComponent rc = (RenderComponent) entity.getComponent(RenderComponent.class);
                            // TODO: these two statements appear to be obsolete.
                            RenderComponent rcParent = 
                                (RenderComponent) parentEntity.getComponent(RenderComponent.class);
                            Node attachNode = rcParent.getSceneRoot();

                            // Note: we need to attach non-primaries to the parent geometry node in 
                            // ortho mode, rather than the view node. This way it picks up the parent's
                            // offset translation, which contains locationOrtho
                            // TODO: do this cleaner. Convert attach node to a view and get the
                            // geometry node for this view.
                            attachNode = (Node) attachNode.getChild(0);

                            sgChangeAttachPointSetAddEntity(rc, attachNode, parentEntity, entity);
                            attachState = AttachState.ATTACHED_TO_ENTITY;
                            logger.fine("Attach ortho entity " + entity + " to geometry node of parent entity " + parentEntity);
                        }
                    }
                } else {
                    logger.fine("View is not ortho for view " + this);
                    parentEntity = getParentEntity();
                    if (parentEntity == null) {
                        logger.warning("getParentEntity() returns null; must be non-null");
                    } else {
                        logger.fine("Attach entity " + entity + " to parent entity " + parentEntity);

                        RenderComponent rc = (RenderComponent) entity.getComponent(RenderComponent.class);
                        RenderComponent rcParent = 
                            (RenderComponent) parentEntity.getComponent(RenderComponent.class);
                        Node attachNode = rcParent.getSceneRoot();

                        // SPECIAL NOTE: Here is where special surgery is done on header windows so 
                        // that they are parented to the *geometry node* of their parent view instead of 
                        // the view node, as windows normally are. This way it picks up the offset
                        // translation in the geometry node and stays in sync with the rest of the frame.
                        // See also: SPECIAL NOTE in Frame2DCell.attachViewToEntity.
                        if (window instanceof WindowSwingHeader) {
                            WindowSwingHeader wsh = (WindowSwingHeader) window;
                            if (wsh.getView().getType() == View2D.Type.SECONDARY) { 
                                // TODO: do this cleaner. Convert attach node to a view and get the
                                // geometry node for this view.
                                attachNode = (Node) attachNode.getChild(0);
                            }
                        }

                        sgChangeAttachPointSetAddEntity(rc, attachNode, parentEntity, entity);
                        attachState = AttachState.ATTACHED_TO_ENTITY;
                        entity.getComponent(RenderComponent.class).setOrtho(false);
                    }
                }
            }

            if ((changeMask & CHANGED_VISIBLE) != 0) {
                // Update visibility of children
                logger.fine("Update children visibility for view " + this);
                for (View2DEntity child : children) {
                    child.updateVisibility(inCleanup);
                }
            }            
        } // End Topology Changes

        // Determine what frame changes need to be performed. But these aren't executed now;
        // they are executed later by view.updateFrame, which must be invoked outside the window lock.
        // issue 151: prepare for reattachment of frame on resize. 
        if ((changeMask & (CHANGED_DECORATED | CHANGED_TITLE | CHANGED_TYPE | CHANGED_SIZE_APP |
                           CHANGED_PIXEL_SCALE | CHANGED_USER_RESIZABLE | CHANGED_VISIBLE)) != 0) { 
            logger.fine("Update frame for view " + this);
            logger.fine("decorated " + decorated);

            if ((changeMask & (CHANGED_DECORATED | CHANGED_VISIBLE)) != 0) {
                // Some popups initiall are decorated and then are set to undecorated before
                // the popup becomes visible. So to avoid wasting time, wait until the window
                // becomes visible before attaching its frame.
                if (decorated && isActuallyVisible()) {
                    if (!hasFrame()) {
                        logger.fine("Attach frame");
                        frameChanges.add(FrameChange.ATTACH_FRAME);
                    }
                } else {
                    if (hasFrame()) {
                        logger.fine("Detach frame");
                        frameChanges.add(FrameChange.DETACH_FRAME);
                    }
                }
            }
            
            if ((changeMask & CHANGED_TITLE) != 0) {
                if (decorated && hasFrame()) {
                    frameChanges.add(FrameChange.UPDATE_TITLE);
                }
            }

            if ((changeMask & (CHANGED_TYPE | CHANGED_SIZE_APP)) != 0) {
                if (decorated) {
                    frameChanges.add(FrameChange.REATTACH_FRAME);
                }
            }
            if ((changeMask & (CHANGED_USER_RESIZABLE | CHANGED_VISIBLE)) != 0) {
                if (decorated) {
                    frameChanges.add(FrameChange.UPDATE_USER_RESIZABLE);
                }
            }
        }            

        if ((changeMask & (CHANGED_STACK | CHANGED_ORTHO)) != 0) {
            logger.fine("Update geometry ortho Z order for view " + this);
            if (ortho) {
                if (window != null) {
                    int zOrder = window.getZOrder();
                    logger.fine("Z order = " + zOrder);
                    if (zOrder >= 0) {
                        sgChangeGeometryOrthoZOrderSet(geometryNode, zOrder);
                    }
                }
            }
        }

        if ((changeMask & CHANGED_ORTHO) != 0) {
            // MTGame: can currently only setOrtho on a visible rc
            if (isActuallyVisible()) {
                entity.getComponent(RenderComponent.class).setOrtho(ortho);
            }
            sgChangeViewNodeOrthoSet(viewNode, ortho);
        }

        // React to size related changes (must be done before handling transform changes)
        if ((changeMask & (CHANGED_DECORATED | CHANGED_SIZE_APP | CHANGED_PIXEL_SCALE | 
                           CHANGED_ORTHO)) != 0) { 

            float width = getDisplayerLocalWidth();
            float height = getDisplayerLocalHeight();
            sgChangeGeometrySizeSet(geometryNode, width, height);

            /**
             * Subtle: Changing the size of the quad will stomp the texture coordinates.
             * We must force them to be restored.
             */
            changeMask |= CHANGED_TEX_COORDS;
        }

        // React to texture coordinate changes
        // Uses: window, texture
        if ((changeMask & (CHANGED_TEX_COORDS|CHANGED_GEOMETRY|CHANGED_SIZE_APP)) != 0) {
            // TODO: for now, texcoords only depend on app size. Eventually this should
            // be the effective aperture rectangle width and height
            float width = (float) sizeApp.width;    
            float height = (float) sizeApp.height;
            if (getWindow() != null && getWindow().getTexture() != null) {
                Image image = getWindow().getTexture().getImage();
                float widthRatio = width / image.getWidth();
                float heightRatio = height / image.getHeight();
                sgChangeGeometryTexCoordsSet(geometryNode, widthRatio, heightRatio);
                windowNeedsValidate = true;
            }
        }

        // React to transform related changes
        // Uses: type, parent, pixelscale, size, offset, ortho, locationOrtho, stack
        if ((changeMask & (CHANGED_TYPE | CHANGED_PARENT | CHANGED_PIXEL_SCALE | CHANGED_SIZE_APP | 
                           CHANGED_OFFSET | CHANGED_ORTHO | CHANGED_LOCATION_ORTHO | CHANGED_STACK)) != 0) {
            CellTransform transform = null;

            switch (type) {
            case UNKNOWN:
            case PRIMARY:
                transform = new CellTransform(null, null);
                if (ortho) { 
                    Vector3f orthoLocTranslation = new Vector3f();
                    orthoLocTranslation.x = locationOrtho.x;
                    orthoLocTranslation.y = locationOrtho.y;
                    transform.setTranslation(orthoLocTranslation);
                } else {
                    // Note: primaries now also honor the offset.
                    // Uses: type, parent, pixelScale, size, offset, ortho
                    transform = calcOffsetStackTransform();
                }
                break;
            case SECONDARY:
            case POPUP:
                // Uses: type, parent, pixelScale, size, offset, ortho
                transform = calcOffsetStackTransform();
            }
            sgChangeGeometryTransformOffsetStackSet(geometryNode, transform);
        }

        // Update the view node's user transform, if necessary
        // Uses: type, deltaTranslationToApply
        if ((changeMask & (CHANGED_TYPE | CHANGED_USER_TRANSFORM | CHANGED_ORTHO)) != 0) {

            // Select the current user transform based on the ortho mode
            CellTransform currentUserTransform;
            if (ortho) {
                currentUserTransform = userTransformOrtho;
            } else {
                currentUserTransform = userTransformCell;
            }

            if (!userTransformCellReplaced) {            
                // Apply any pending user transform deltas (by post-multiplying them
                // into the current user transform
                logger.fine("currentUserTransform (before) = " + currentUserTransform);
                userTransformApplyDeltas(currentUserTransform);
            }

            logger.fine("currentUserTransform (latest) = " + currentUserTransform);

            // Now put the update user transformation into effect
            switch (type) {
            case UNKNOWN:
            case PRIMARY:
                updatePrimaryTransform(currentUserTransform);
                break;
            case SECONDARY:
                sgChangeTransformUserSet(viewNode, currentUserTransform);
                // Note: moving a secondary in the cell doesn't change the position
                // of the secondary in ortho, and vice versa.
                if (!ortho && !userTransformCellChangedLocalOnly) {
                    window.changedUserTransformCell(userTransformCell, this);
                }
                break;
            case POPUP:
                // Always set to identity
                sgChangeTransformUserSet(viewNode, new CellTransform(null, null));
            }

            userTransformCellReplaced = false;
            userTransformCellChangedLocalOnly = false;
        }

        // Changing the 3D size of the app can change the offset of children, such as headers.
        if ((changeMask & (CHANGED_SIZE_APP | CHANGED_PIXEL_SCALE)) != 0) { 
            for (View2DEntity childView : children) {
                childView.changeOffsetSelfAndChildren();
            }
        }

        sgProcessChanges();

        /* For Debug 
        System.err.println("************* After View2DEntity.processChanges, viewNode = ");
        GraphicsUtils.printNode(viewNode);
        */

        /* For debug of ortho entities which should be visible 
        WorldManager wm = ClientContextJME.getWorldManager();
        for (int i=0; i < wm.numEntities(); i++) {
            Entity e = wm.getEntity(i);
            if (e.toString().equals("<Plug the name of the window in here")) {
                System.err.println("e = " + e);
                RenderComponent rc = (RenderComponent) e.getComponent(RenderComponent.class);
            }
        }
        */

        // In certain situations, especially after we change the texture, WindowSwings
        // need to be repainted into that texture.
        if (windowNeedsValidate) {
            if (window instanceof WindowSwing) {
                ((WindowSwing)window).validate();
            }
        }

        // Inform the window's surface of the view visibility.
        if (window != null) {
            DrawingSurface surface = window.getSurface();
            if (surface != null) {
                surface.setViewIsVisible(this, isActuallyVisible());
            }
        }

        // Make sure that all descendent views are up-to-date
        logger.fine("Update children for view " + this);
        for (View2DEntity child : children) {
            if (child.changeMask != 0) {
                child.update();
            }
        }

        changeMask = 0;
    }

    /**
     * This returns the entity in the world to which this view should be attached as a child.
     * It must return non-null.
     * Uses: type, parent.
     */
    protected abstract Entity getParentEntity ();

    // Uses: type, parent, pixelscale, size, offset
    // View2DCell subclass uses: type, ortho, parent, stack
    private CellTransform calcOffsetStackTransform () {
        CellTransform transform = new CellTransform(null, null);

        // Uses: parent, pixelScale, size, offset, ortho
        Vector3f offsetTranslation = calcOffsetTranslation();

        // Uses: type
        Vector3f stackTranslation = calcStackTranslation();

        offsetTranslation.addLocal(stackTranslation);

        // TODO: HACK: Part 3 of 4 temporary workaround for 951
        offsetTranslation.addLocal(new Vector3f(0f, 0f, hackZEpsilon));

        transform.setTranslation(offsetTranslation);

        return transform;
    }

    // Uses: parent, pixelscale, size, offset
    // Convert the pixel-offset-from-upper-left of parent to a distance vector from the center of parent
    private Vector3f calcOffsetTranslation () {
        Vector3f translation = new Vector3f();

        if (ortho) {
            if (type == Type.PRIMARY || type == Type.UNKNOWN) {
                translation.x = locationOrtho.x;
                translation.y = locationOrtho.y;
            } else {

                if (parent == null) return translation;

                // Initialize to the first part of the offset (the local coordinate translation)
                logger.fine("view = " + this);
                logger.fine("parent = " + parent);
                logger.fine("locationOrtho = " + locationOrtho);
                logger.fine("offset = " + offset);
                translation.x = locationOrtho.x + offset.x;
                translation.y = locationOrtho.y + offset.y;
                logger.fine("translation 1 = " + translation);
                
                // Convert pixel offset to local coords and add it in
                Dimension parentSize = parent.getSizeApp();
                Vector2f pixelScaleOrtho = parent.getPixelScaleOrtho();
                logger.fine("parentSize = " + parentSize);
                translation.x += -parentSize.width * pixelScaleOrtho.x / 2f;
                translation.y += parentSize.height * pixelScaleOrtho.y / 2f;
                logger.fine("translation 2 = " + translation);
                logger.fine("sizeApp = " + sizeApp);
                pixelScaleOrtho = getPixelScaleOrtho();
                translation.x += sizeApp.width * pixelScaleOrtho.x / 2f;
                translation.y -= sizeApp.height * pixelScaleOrtho.y / 2f;
                logger.fine("translation 3 = " + translation);
                logger.fine("pixelOffset = " + pixelOffset);
                translation.x += pixelOffset.x * pixelScaleOrtho.x;
                translation.y -= pixelOffset.y * pixelScaleOrtho.y;
                logger.fine("translation 4 = " + translation);
            }
        } else {    

            // Initialize to the first part of the offset (the local coordinate translation)
            logger.fine("view = " + this);
            logger.fine("parent = " + parent);
            logger.fine("offset = " + offset);
            translation.x = offset.x;
            translation.y = offset.y;
            logger.fine("translation 1 = " + translation);

            if (type != Type.PRIMARY && type != Type.UNKNOWN && parent != null) {

                // Convert pixel offset to local coords and add it in
                // TODO: does the width/height need to include the scroll bars?
                Vector2f pixelScale = parent.getPixelScaleCurrent();
                Dimension parentSize = parent.getSizeApp();
                logger.fine("parentSize = " + parentSize);
                translation.x += -parentSize.width * pixelScale.x / 2f;
                translation.y += parentSize.height * pixelScale.y / 2f;
                logger.fine("translation 2 = " + translation);
                logger.fine("sizeApp = " + sizeApp);
                pixelScale = getPixelScaleCurrent();
                translation.x += sizeApp.width * pixelScale.x / 2f;
                translation.y -= sizeApp.height * pixelScale.y / 2f;
                logger.fine("translation 3 = " + translation);
                logger.fine("pixelOffset = " + pixelOffset);
                translation.x += pixelOffset.x * pixelScale.x;
                translation.y -= pixelOffset.y * pixelScale.y;
                logger.fine("translation 4 = " + translation);
            }
        }

        logger.fine("view = " + this);
        logger.fine("offset translation = " + translation);

        return translation;
    }


    protected Vector3f calcStackTranslation () {
        return new Vector3f(0f, 0f, 0f);
    }

    // Apply the pending deltas to the given user transform
    protected void userTransformApplyDeltas (CellTransform userTransform) {
        userTransformApplyDeltaTranslation(userTransform);
    }

    // Apply any pending translation delta to the given user transform.
    protected void userTransformApplyDeltaTranslation (CellTransform userTransform) {
        if (deltaTranslationToApply != null) {
            CellTransform transform = new CellTransform(null, null);
            transform.setTranslation(deltaTranslationToApply);
            //System.err.println("******* delta translation transform = " + transform);
            userTransform.mul(transform);
            deltaTranslationToApply = null;
        }
    }

    protected void updatePrimaryTransform (CellTransform transform) {
    }

    private enum SGChangeOp { 
        GEOMETRY_ATTACH_TO_VIEW,
        GEOMETRY_DETACH_FROM_VIEW,
        GEOMETRY_SIZE_SET, 
        GEOMETRY_TEX_COORDS_SET,
        GEOMETRY_TEXTURE_SET,
        GEOMETRY_ORTHO_Z_ORDER_SET,
        GEOMETRY_TRANSFORM_OFFSET_STACK_SET,
        GEOMETRY_CLEANUP,
        VIEW_NODE_ORTHO_SET,
        TRANSFORM_USER_SET,
        ATTACH_POINT_SET_ADD_ENTITY
    };

    private static class SGChange {
        private SGChangeOp op;
        private SGChange (SGChangeOp op) { 
            this.op = op; 
        }
        private SGChangeOp getOp () {
            return op;
        }
    }

    private static class SGChangeGeometryAttachToView extends SGChange {
        private Node viewNode;
        private GeometryNode geometryNode;
        private SGChangeGeometryAttachToView (Node viewNode, GeometryNode geometryNode) {
            super(SGChangeOp.GEOMETRY_ATTACH_TO_VIEW);
            this.viewNode = viewNode;
            this.geometryNode = geometryNode;
        }
    }

    private static class SGChangeGeometryDetachFromView extends SGChange {
        private Node viewNode;
        private GeometryNode geometryNode;
        private SGChangeGeometryDetachFromView (Node viewNode, GeometryNode geometryNode) {
            super(SGChangeOp.GEOMETRY_DETACH_FROM_VIEW);
            this.viewNode = viewNode;
            this.geometryNode = geometryNode;
        }
    }

    private static class SGChangeGeometrySizeSet extends SGChange {
        private GeometryNode geometryNode;
        private float width;
        private float height;
        private SGChangeGeometrySizeSet (GeometryNode geometryNode, float width, float height) {
            super(SGChangeOp.GEOMETRY_SIZE_SET);
            this.geometryNode = geometryNode;
            this.width = width;
            this.height = height;
        }
    }

    private static class SGChangeGeometryTexCoordsSet extends SGChange {
        private GeometryNode geometryNode;
        private float widthRatio;
        private float heightRatio;
        private SGChangeGeometryTexCoordsSet (GeometryNode geometryNode, 
                                              float widthRatio, float heightRatio) {
            super(SGChangeOp.GEOMETRY_TEX_COORDS_SET);
            this.geometryNode = geometryNode;
            this.widthRatio = widthRatio;
            this.heightRatio = heightRatio;
        }
    }

    private static class SGChangeGeometryTextureSet extends SGChange {
        private GeometryNode geometryNode;
        private Texture2D texture;
        private DrawingSurface surface;
        private SGChangeGeometryTextureSet (GeometryNode geometryNode, Texture2D texture,
                                            DrawingSurface surface) {
            super(SGChangeOp.GEOMETRY_TEXTURE_SET);
            this.geometryNode = geometryNode;
            this.texture = texture;
            this.surface = surface;
        }
    }

    private static class SGChangeGeometryOrthoZOrderSet extends SGChange {
        private GeometryNode geometryNode;
        private int zOrder;
        private SGChangeGeometryOrthoZOrderSet (GeometryNode geometryNode, int zOrder) {
            super(SGChangeOp.GEOMETRY_ORTHO_Z_ORDER_SET);
            this.geometryNode = geometryNode;
            this.zOrder = zOrder;
        }
    }

    private static class SGChangeGeometryCleanup extends SGChange {
        private GeometryNode geometryNode;
        private SGChangeGeometryCleanup (GeometryNode geometryNode) {
            super(SGChangeOp.GEOMETRY_CLEANUP);
            this.geometryNode = geometryNode;
        }
    }

    private static class SGChangeViewNodeOrthoSet extends SGChange {
        private Node viewNode;
        private boolean ortho;
        private SGChangeViewNodeOrthoSet (Node viewNode, boolean ortho) {
            super(SGChangeOp.VIEW_NODE_ORTHO_SET);
            this.viewNode = viewNode;
            this.ortho = ortho;
        }
    }

    private static class SGChangeTransform extends SGChange {
        protected CellTransform transform;
        private SGChangeTransform (SGChangeOp op, CellTransform transform) {
            super(op);
            this.transform = transform;
        }
    }
    
    private static class SGChangeGeometryTransformOffsetStackSet extends SGChangeTransform {
        private GeometryNode geometryNode;
        private SGChangeGeometryTransformOffsetStackSet (GeometryNode geometryNode, CellTransform transform) {
            super(SGChangeOp.GEOMETRY_TRANSFORM_OFFSET_STACK_SET, transform);
            this.geometryNode = geometryNode;
        }
    }

    private static class SGChangeTransformUserSet extends SGChangeTransform {
        private Node viewNode;
        private SGChangeTransformUserSet (Node viewNode, CellTransform transform) {
            super(SGChangeOp.TRANSFORM_USER_SET, transform);
            this.viewNode = viewNode;
       }
    }

    private static class SGChangeAttachPointSetAddEntity extends SGChange {
        private RenderComponent rc;
        private Node node;
        private Entity parentEntity;
        private Entity entity;
        private SGChangeAttachPointSetAddEntity (RenderComponent rc, Node node, Entity parentEntity, 
                                                 Entity entity) {
            super(SGChangeOp.ATTACH_POINT_SET_ADD_ENTITY);
            this.rc = rc;
            this.node = node;
            this.parentEntity = parentEntity;
            this.entity = entity;
       }
    }

    // The list of scene graph changes (to be applied at the end of update).
    private LinkedList<SGChange> sgChanges = new LinkedList<SGChange>();

    private synchronized void sgChangeGeometryAttachToView(Node viewNode, GeometryNode geometryNode) {
        sgChanges.add(new SGChangeGeometryAttachToView(viewNode, geometryNode));
    }

    private synchronized void sgChangeGeometryDetachFromView(Node viewNode, GeometryNode geometryNode) {
        sgChanges.add(new SGChangeGeometryDetachFromView(viewNode, geometryNode));
    }

    private synchronized void sgChangeGeometrySizeSet(GeometryNode geometryNode, float width, float height) {
        sgChanges.add(new SGChangeGeometrySizeSet(geometryNode, width, height));
    }

    private synchronized void sgChangeGeometryTexCoordsSet(GeometryNode geometryNode, float widthRatio, 
                                                           float heightRatio) {
        sgChanges.add(new SGChangeGeometryTexCoordsSet(geometryNode, widthRatio, heightRatio));
    }

    private synchronized void sgChangeGeometryTextureSet(GeometryNode geometryNode, Texture2D texture,
                                                         DrawingSurface surface) {
        sgChanges.add(new SGChangeGeometryTextureSet(geometryNode, texture, surface));
    }

    private synchronized void sgChangeGeometryOrthoZOrderSet(GeometryNode geometryNode, int zOrder) {
        sgChanges.add(new SGChangeGeometryOrthoZOrderSet(geometryNode, zOrder));
    }

    private synchronized void sgChangeGeometryCleanup(GeometryNode geometryNode) {
        sgChanges.add(new SGChangeGeometryCleanup(geometryNode));
    }

    private synchronized void sgChangeViewNodeOrthoSet(Node viewNode, boolean ortho) {
        sgChanges.add(new SGChangeViewNodeOrthoSet(viewNode, ortho));
    }

    private synchronized void sgChangeGeometryTransformOffsetStackSet (GeometryNode geometryNode,
                                                                       CellTransform transform) {
        sgChanges.add(new SGChangeGeometryTransformOffsetStackSet(geometryNode,transform));
    }

    protected synchronized void sgChangeTransformUserSet (Node viewNode, CellTransform transform) {
        sgChanges.add(new SGChangeTransformUserSet(viewNode, transform));
    }

    protected synchronized void sgChangeAttachPointSetAddEntity (RenderComponent rc, Node node, 
                                                                 Entity parentEntity, Entity entity) {
        sgChanges.add(new SGChangeAttachPointSetAddEntity(rc, node, parentEntity, entity));
    }

    // Note: this method doesn't need to be synchronized because it does everything via a 
    // synchronous render updater
    private void sgProcessChanges () {
        if (sgChanges.size() <= 0) return;

         ClientContextJME.getWorldManager().addRenderUpdater(new RenderUpdater() {
             public void update(Object arg0) {

                 for (SGChange sgChange : sgChanges) {
                     switch (sgChange.getOp()) {

                     case GEOMETRY_ATTACH_TO_VIEW: {
                         SGChangeGeometryAttachToView chg = (SGChangeGeometryAttachToView) sgChange;
                         chg.viewNode.attachChild(chg.geometryNode);
                         logger.fine("Attach geometryNode " + chg.geometryNode + " to viewNode " + 
                                     chg.viewNode);
                         break;
                     }

                     case GEOMETRY_DETACH_FROM_VIEW: {
                         SGChangeGeometryDetachFromView chg = (SGChangeGeometryDetachFromView) sgChange;
                         chg.viewNode.detachChild(chg.geometryNode);
                         logger.fine("Detach geometryNode " + chg.geometryNode + " from viewNode " + 
                                     chg.viewNode);
                         break;
                     }

                     case GEOMETRY_SIZE_SET: {
                         SGChangeGeometrySizeSet chg = (SGChangeGeometrySizeSet) sgChange;
                         chg.geometryNode.setSize(chg.width, chg.height);
                         forceTextureIdAssignment(true);
                         logger.fine("******** Geometry node = " + chg.geometryNode);
                         logger.fine("******** Geometry node setSize, wh = " + chg.width + ", " + chg.height);
                         break;
                     }

                     case GEOMETRY_TEX_COORDS_SET: {
                         SGChangeGeometryTexCoordsSet chg = (SGChangeGeometryTexCoordsSet) sgChange;
                         chg.geometryNode.setTexCoords(chg.widthRatio, chg.heightRatio);
                         logger.fine("******** viewNode = " + viewNode);
                         logger.fine("******** Geometry node setTexCoords, whRatio = " + chg.widthRatio + ", " + 
                                     chg.heightRatio);
                         break;
                     }

                     case GEOMETRY_TEXTURE_SET: {
                         SGChangeGeometryTextureSet chg = (SGChangeGeometryTextureSet) sgChange;

                         DrawingSurface surface = chg.surface;
                         boolean restoreUpdating = false;
                         if (surface.getUpdateEnable()) {
                             surface.setUpdateEnable(false);
                             restoreUpdating = true;
                         }
                         chg.geometryNode.setTexture(chg.texture);
                         
                         if (restoreUpdating) {
                             surface.setUpdateEnable(true);
                         }

                         logger.fine("Geometry node setTexture, texture = " + chg.texture);
                         break;
                     }

                     case GEOMETRY_ORTHO_Z_ORDER_SET: {
                         SGChangeGeometryOrthoZOrderSet chg = (SGChangeGeometryOrthoZOrderSet) sgChange;
                         if (chg.geometryNode != null) {
                             chg.geometryNode.setOrthoZOrder(chg.zOrder);
                         }
                         logger.fine("Geometry set ortho z order = " + chg.zOrder);
                         break;
                     }

                     case GEOMETRY_CLEANUP: {
                         SGChangeGeometryCleanup chg = (SGChangeGeometryCleanup) sgChange;
                         chg.geometryNode.cleanup();
                         logger.fine("Geometry node cleanup");
                         break;
                     }

                     case VIEW_NODE_ORTHO_SET: {
                         SGChangeViewNodeOrthoSet chg = (SGChangeViewNodeOrthoSet) sgChange;
                         if (chg.ortho) {
                             chg.viewNode.setCullHint(Spatial.CullHint.Never);
                         } else {
                             chg.viewNode.setCullHint(Spatial.CullHint.Inherit);
                         }
                         logger.fine("View node ortho cull hint set = " + chg.ortho);
                         break;
                     }

                     case GEOMETRY_TRANSFORM_OFFSET_STACK_SET: {
                         // The offset/stack transform resides in the geometry
                         SGChangeGeometryTransformOffsetStackSet chg =
                             (SGChangeGeometryTransformOffsetStackSet) sgChange;
                         chg.geometryNode.setTransform(chg.transform);
                         logger.fine("Geometry node set transform, transform = " + chg.transform);
                         break;
                     }

                     case TRANSFORM_USER_SET: {
                         SGChangeTransformUserSet chg = (SGChangeTransformUserSet) sgChange;
                         CellTransform userTransform = chg.transform.clone(null);
                         Quaternion r = userTransform.getRotation(null);
                         chg.viewNode.setLocalRotation(r);
                         logger.fine("View node set rotation = " + r);
                         Vector3f t = userTransform.getTranslation(null);
                         chg.viewNode.setLocalTranslation(t);
                         logger.fine("View node set translation = " + t);
                         break;
                     }

                     case ATTACH_POINT_SET_ADD_ENTITY: {
                         SGChangeAttachPointSetAddEntity chg = (SGChangeAttachPointSetAddEntity) sgChange;
                         chg.rc.setAttachPoint(chg.node);
                         
                         // Note: in the latest MTGame, addEntity makes the entity immediately visible.
                         // So, to avoid having the scene graph come up at the wrong place, we need
                         // to perform the addEntity after setting the attach point.
                         if (chg.parentEntity != null && chg.entity != null) {
                             chg.parentEntity.addEntity(chg.entity);
                         }
                         break;
                     }

                     }
                 }


                 // Propagate changes to JME
                 if (viewNode != null) {
                     ClientContextJME.getWorldManager().addToUpdateList(viewNode);
                 }

                 sgChanges.clear();
             }
         }, null, true);
         // NOTE: it is critical that this render updater runs to completion before anything else happens
    }

    /** {@inheritDoc} */
    public void deliverEvent(Window2D window, MouseEvent3D me3d) {

        // NOTE: This is called on the EDT.

        /*
        System.err.println("********** me3d = " + me3d);
        System.err.println("********** awt event = " + me3d.getAwtEvent());
        PickDetails pickDetails = me3d.getPickDetails();
        System.err.println("********** pt = " + pickDetails.getPosition());
        */

        // No special processing is needed for wheel events. Just
        // send the 2D wheel event which is contained in the 3D event.
        if (me3d instanceof MouseWheelEvent3D) {
            controlArb.deliverEvent(window, (MouseEvent) me3d.getAwtEvent());
            return;
        }

        // Can't convert if there is no geometry
        if (geometryNode == null) {
            return;
        }

        // Convert mouse event intersection point to 2D. For most events this is the intersection
        // point based on the destination pick details calculated by the input system, but for drag
        // events this needs to be derived from the actual hit pick details (because for drag events
        // the destination pick details might be overridden by a grab).
        Point point;
        if (me3d.getID() == MouseEvent.MOUSE_DRAGGED) {
            MouseDraggedEvent3D de3d = (MouseDraggedEvent3D) me3d;
            point = geometryNode.calcPositionInPixelCoordinates(de3d.getHitIntersectionPointWorld(), true);
        } else {
            point = geometryNode.calcPositionInPixelCoordinates(me3d.getIntersectionPointWorld(), false);
        }
        if (point == null) {
            // Event was outside our panel so do nothing
            // This can happen for drag events
            return;
        }

        // Construct a corresponding 2D event
        MouseEvent me = (MouseEvent) me3d.getAwtEvent();
        int id = me.getID();
        long when = me.getWhen();
        int modifiers = me.getModifiers();
        int button = me.getButton();

        // TODO: WORKAROUND FOR A WONDERLAND PICKER PROBLEM:
        // See comment for pointerMoveSeen above
        if (id == MouseEvent.MOUSE_RELEASED && pointerMoveSeen) {
            point.x = pointerLastX;
            point.y = pointerLastY;
        }

        me = new MouseEvent(dummyButton, id, when, modifiers, point.x, point.y,
                0, false, button);

        // Send event to the window's control arbiter
        controlArb.deliverEvent(window, me);

        // TODO: WORKAROUND FOR A WONDERLAND PICKER PROBLEM:
        // See comment for pointerMoveSeen above
        if (id == MouseEvent.MOUSE_MOVED || id == MouseEvent.MOUSE_DRAGGED) {
            pointerMoveSeen = true;
            pointerLastX = point.x;
            pointerLastY = point.y;
        }
    }

    /** {@inheritDoc} */
    public Point calcPositionInPixelCoordinates(Vector3f point, boolean clamp) {
        if (geometryNode == null) {
            return null;
        }
        return geometryNode.calcPositionInPixelCoordinates(point, clamp);
    }

    /** {@inheritDoc} */
    public Point calcIntersectionPixelOfEyeRay(int x, int y) {
        if (geometryNode == null) {
            return null;
        }
        if (isOrtho()) {
            return geometryNode.calcIntersectionPixelOfEyeRayOrtho(x, y);
        } else {
            return geometryNode.calcIntersectionPixelOfEyeRay(x, y);
        }
    }

    /** 
     * Force the texture ID of the texture to be allocated.
     * @param inRenderLoop true if the call is already being made from inside the render loop.
     */
    private void forceTextureIdAssignment(boolean inRenderLoop) {
        if (geometryNode == null) {
            setGeometryNode(null);
            if (geometryNode == null) {
                logger.severe("Cannot allocate geometry node for view!!");
                return;
            }
        }
        final TextureState ts = geometryNode.getTextureState();
        if (ts == null) {
            logger.warning("Trying to force texture id assignment while view texture state is null");
            return;
        }

        logger.info("texid alloc: ts.getTexture() = " + ts.getTexture());

        Texture tex = ts.getTexture();
        if (tex != null) {
            if (tex.getTextureId() != 0) {
                // Don't allocate a texture ID if one is already allocated. This can happen,
                // for example, when a new view is created which shows the same texture as
                // an existing view. One such case is Apps-in-HUD.
                return;
            }
        }

        if (inRenderLoop) {
            // We're already in the render loop
            ts.load();
        } else {
            // Not in render loop. Must do this inside the render loop.
            ClientContextJME.getWorldManager().addRenderUpdater(new RenderUpdater() {
                public void update(Object arg0) {
                    // The JME magic - must be called from within the render loop
                    ts.load();
                }
           }, null, true);  // Note: a rare case in which we must wait for the render updater to complete
        }

        /* For debug: Verify that ID was actually allocated
        Texture tex = geometryNode.getTexture();
        if (tex != null) {
            int texid = tex.getTextureId();
            logger.severe("))))))))))) V2E: texid alloc: texid = " + texid);
            if (texid == 0) {
                logger.severe("Failed to allocated texture ID");
            }
        }
        */
    }

    /** {@inheritDoc} */
    public synchronized void addEventListener(EventListener listener) {
        listener.addToEntity(entity);
    }

    /** {@inheritDoc} */
    public synchronized void removeEventListener(EventListener listener) {
        if (listener != null) {
            listener.removeFromEntity(entity);
        }
    }

    /** {@inheritDoc} */
    public void addEntityComponent(Class clazz, EntityComponent comp) {
        entity.addComponent(clazz, comp);
    }

    /** {@inheritDoc} */
    public void removeEntityComponent(Class clazz) {
        if (entity != null) {
            entity.removeComponent(clazz);
        }
    }

    /**
     * Return whether this view has an attached frame.
     */
    protected boolean hasFrame () {
        return false;
    }

    /**
     * Attach a frame to this view. This can be overridden by the subclass so a 
     * subclass-specific frame can be attached.
     */
    protected void attachFrame () {
    }

    /**
     * Detach this view's frame from the view.
     */
    protected void detachFrame () {
    }

    /**
     * Detach frame from the view and reattach it. This takes into account any changes
     * in the type of the view. This can be overridden by the subclass so a 
     * subclass-specific frame can be reattached.
     */
    protected void reattachFrame () {
    }

    /**
     * Update the frame's title.
     */
    protected void frameUpdateTitle () {
    }

    /**
     * Update this frame's userResizable attribute
     */
    protected void frameUpdateUserResizable () {
    }


    /**
     * Update the frame.
     */
    protected void frameUpdate () {
    }

    /** 
     * Make an entity pickable by attaching a collision component. Entity must already have
     * a render component and a scene root node.
     */
    public static void entityMakePickable (Entity entity) {
        JMECollisionSystem collisionSystem = (JMECollisionSystem) ClientContextJME.getWorldManager().
            getCollisionManager().loadCollisionSystem(JMECollisionSystem.class);
        RenderComponent rc = (RenderComponent) entity.getComponent(RenderComponent.class);
        CollisionComponent cc = collisionSystem.createCollisionComponent(rc.getSceneRoot());
        entity.addComponent(CollisionComponent.class, cc);
        cc.setCollidable(true);
        cc.setInheritCollidable(true);
    }


    /** {@inheritDoc} */
    public void updateFrame () {
    	
    	// issue 151
    	if (inCleanup) {
    		return;
    	}
        
    	for (FrameChange frameChg : frameChanges) {
            switch (frameChg) {
            case ATTACH_FRAME:
                attachFrame();
                break;
            case DETACH_FRAME:
                detachFrame();
                break;
            case REATTACH_FRAME:
                reattachFrame();
                break;
            case UPDATE_TITLE:
                frameUpdateTitle();
                break;
            case UPDATE_USER_RESIZABLE:
                frameUpdateUserResizable();
                break;
            }
        }
        frameChanges.clear();

        frameUpdate();
    }

    @Override
    public String toString () {
        return name;
    }
}
