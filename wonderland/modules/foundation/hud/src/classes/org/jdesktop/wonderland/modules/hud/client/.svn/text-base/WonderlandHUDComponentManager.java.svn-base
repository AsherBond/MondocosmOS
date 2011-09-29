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

/*
 * Project Wonderland
 * 
 * Copyright (c) 2004-2009, Sun Microsystems, Inc., All Rights Reserved
 * 
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 * 
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License") { } you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 * 
 * Sun designates this particular file as subject to the "Classpath"
 * exception as provided by Sun in the License file that accompanied
 * this code.
 */
package org.jdesktop.wonderland.modules.hud.client;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.system.DisplaySystem;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import org.jdesktop.mtgame.RenderUpdater;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDComponentManager;
import org.jdesktop.wonderland.client.hud.HUDEvent;
import org.jdesktop.wonderland.client.hud.HUDLayoutManager;
import org.jdesktop.wonderland.client.hud.HUDObject.DisplayMode;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.input.MouseEnterExitEvent3D;
import org.jdesktop.wonderland.client.jme.input.test.EnterExitEvent3DLogger;
import org.jdesktop.wonderland.client.jme.utils.graphics.TexturedQuad;
import org.jdesktop.wonderland.modules.appbase.client.Window2D;
import org.jdesktop.wonderland.modules.appbase.client.Window2D.Type;
import org.jdesktop.wonderland.modules.appbase.client.swing.WindowSwing;
import org.jdesktop.wonderland.modules.appbase.client.view.GeometryNode;
import org.jdesktop.wonderland.modules.appbase.client.view.View2D;

/**
 * A WonderlandHUDComponentManager manages a set of HUDComponents.
 *
 * It lays out HUDComponents within a HUD with a HUDLayoutManager layout manager.
 * It also decorates HUDComponents with a frame border that allows the user
 * to move, resize, minimize and maximize and close a HUDComponent.
 *
 * @author nsimpson
 */
public class WonderlandHUDComponentManager implements HUDComponentManager,
        ActionListener, MouseMotionListener, HUDViewListener {

    private static final Logger logger = Logger.getLogger(WonderlandHUDComponentManager.class.getName());
    protected HUD hud;
    // a mapping between HUD components and their states
    protected Map<HUDComponent, HUDComponentState> hudStateMap;
    // a mapping between views and HUD components
    protected Map<HUDView2D, HUDComponent> hudViewMap;
    // the layout manager for the HUD
    protected HUDLayoutManager layout;
    // displays HUD components on the glass
    protected HUDView2DDisplayer hudDisplayer;
    //
    protected HUDApp2D hudApp;
    protected boolean dragging = false;
    protected int dragX = 0;
    protected int dragY = 0;
    protected static final float DEFAULT_FOCUSED_TRANSPARENCY = 0.0f;
    protected static final float DEFAULT_UNFOCUSED_TRANSPARENCY = 0.2f;
    protected static final long DEFAULT_FADE_IN_TIME = 250;
    protected static final long DEFAULT_FADE_OUT_TIME = 1500;
    protected static final String DEFAULT_HUD_ICON = "/org/jdesktop/wonderland/modules/hud/client/resources/GenericWindow32x32.png";
    // TODO: set these from user properties
    protected float focusedTransparency = DEFAULT_FOCUSED_TRANSPARENCY;
    protected float unfocusedTransparency = DEFAULT_UNFOCUSED_TRANSPARENCY;
    protected long fadeInTime = DEFAULT_FADE_IN_TIME;
    protected long fadeOutTime = DEFAULT_FADE_OUT_TIME;

    public WonderlandHUDComponentManager(HUD hud) {
        this.hud = hud;
        hudStateMap = Collections.synchronizedMap(new HashMap());
        hudViewMap = Collections.synchronizedMap(new HashMap());
    }

    public Window2D createWindow(HUDComponent component) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("creating window for HUD component: " + component);
        }

        Window2D window = null;

        //if (hudApp == null) {
        hudApp = new HUDApp2D(hud, "HUD", new ControlArbHUD(), WonderlandHUD.HUD_WORLD_SCALE);
        //}
        try {
            // TODO: pixel scale doesn't match
            if (component instanceof HUDPopup2D) {
                HUDPopup2D popup = (HUDPopup2D) component;
                HUDWindow parent = popup.getParent();
                HUDApp2D parentApp = (HUDApp2D) parent.getApp();
                window = parentApp.createWindow(component.getWidth(), component.getHeight(),
                                                Type.POPUP, parent, false, WonderlandHUD.HUD_SCALE,
                                                "HUD popup");
                window.setPixelOffset(popup.getXOffset(), popup.getYOffset());
            } else {
                window = hudApp.createWindow(component.getWidth(), component.getHeight(), Type.PRIMARY,
                        false, WonderlandHUD.HUD_SCALE, "HUD component");
            }

            JComponent comp = ((HUDComponent2D) component).getComponent();
            ((WindowSwing) window).setComponent(comp);
        } catch (InstantiationException e) {
            logger.warning("failed to create window for HUD component: " + e);
        }

        return window;
    }

    /**
     * {@inheritDoc}
     */
    public void addComponent(final HUDComponent component) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("adding HUD component to component manager: " + component);
        }

        HUDComponentState state = new HUDComponentState(component);
        HUDComponent2D component2D = (HUDComponent2D) component;
        Window2D window;

        if (component2D.getWindow() != null) {
            window = component2D.getWindow();
        } else {
            window = createWindow(component);
            component2D.setWindow(window);
        }

        window.addEventListener(new EnterExitEvent3DLogger() {

            @Override
            public boolean propagatesToParent(Event event) {
                return false;
            }

            @Override
            public void commitEvent(Event event) {
                MouseEnterExitEvent3D mouseEvent = (MouseEnterExitEvent3D) event;
                switch (mouseEvent.getID()) {
                    case MouseEvent.MOUSE_ENTERED:
                        if (logger.isLoggable(Level.FINEST)) {
                            logger.finest("mouse entered component: " + component);
                        }
                        setFocused(component, true);
                        break;
                    case MouseEvent.MOUSE_EXITED:
                        if (logger.isLoggable(Level.FINEST)) {
                            logger.finest("mouse exited component: " + component);
                        }
                        setFocused(component, false);
                        break;
                    default:
                        break;
                }
            }
        });

        state.setWindow(window);
        hudStateMap.put(component, state);
    }

    /**
     * {@inheritDoc}
     */
    public void removeComponent(HUDComponent component) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("removing HUD component from component manager: " + component);
        }

        HUDComponentState state = (HUDComponentState) hudStateMap.get(component);

        if (state != null) {
            // remove HUD view
            Window2D window = state.getWindow();
            HUDView2D view2D = state.getView();

            if (window != null) {
                if (view2D != null) {
                    hudViewMap.remove(view2D);
                    window.removeView(view2D);
                    view2D.cleanup();
                    view2D = null;
                }
                if (component instanceof HUDComponent2D) {
                    HUDComponent2D component2D = (HUDComponent2D) component;
                    if (component2D.isHUDManagedWindow()) {
                        window.cleanup();
                    }
                }
            }

            // remove in-world view
            HUDView3D view3D = state.getWorldView();

            if (view3D != null) {
                view3D.cleanup();
                view3D = null;
            }

            hudStateMap.remove(component);
            state = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public Iterator<HUDComponent> getComponents() {
        return hudStateMap.keySet().iterator();
    }

    public void actionPerformed(ActionEvent e) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("action performed: " + e);
        }

        if (e.getActionCommand().equals("close")) {
            logger.info("close action performed: " + e);
            close(hudViewMap.get((HUDView2D) e.getSource()));
        } else if (e.getActionCommand().equals("minimize")) {
            logger.info("minimize action performed: " + e);
            minimizeComponent(hudViewMap.get((HUDView2D) e.getSource()));
        } else if (e.getActionCommand().equals("hud")) {
            logger.info("remove from HUD action performed: " + e);
            close(hudViewMap.get((HUDView2D) e.getSource()));
        }
    }

    public void mouseMoved(MouseEvent e) {
        dragging = false;
    }

    public void mouseDragged(MouseEvent e) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("mouse dragged to: " + e.getPoint());
        }

        HUDView2D view = (HUDView2D) e.getSource();
        if (view instanceof HUDView2D) {
            HUDComponent hudComponent = hudViewMap.get(view);
            if (hudComponent != null) {
                if (!dragging) {
                    dragX = e.getX();
                    dragY = e.getY();
                    dragging = true;
                }

                // calculate new location of HUD component
                Point location = hudComponent.getLocation();
                float xDelta = WonderlandHUD.HUD_SCALE.x * (float) (e.getX() - dragX);
                float yDelta = WonderlandHUD.HUD_SCALE.y * (float) (e.getY() - dragY);
                location.setLocation(location.getX() + xDelta, location.getY() - yDelta);

                // move the HUD component
                hudComponent.setLocation(location);
                //System.err.println("--- setting location to: " + location);
                dragX = e.getX();
                dragY = e.getY();
            }
        }
    }

    protected void componentVisible(HUDComponent2D component) {
        if (logger.isLoggable(Level.INFO)) {
            logger.info("showing HUD component on HUD: " + component);
        }

        HUDComponentState state = (HUDComponentState) hudStateMap.get(component);

        if (state == null) {
            return;
        }

        HUDView2D view = state.getView();

        if (view == null) {
            if (hudDisplayer == null) {
                hudDisplayer = new HUDView2DDisplayer(hud);
            }

            view = hudDisplayer.createView(state.getWindow());
            view.addActionListener(this);
            view.addMouseMotionListener(this);
            view.addHUDViewListener(this);
            hudViewMap.put(view, component);
            state.setView(view);

            if (layout != null) {
                layout.addView(component, view);
            }
        }

        // move the component to the screen
        view.setOrtho(true, false);
        view.setPixelScaleOrtho(WonderlandHUD.HUD_SCALE, false);

        // position the component on the screen.
        if ((view.getType() == View2D.Type.PRIMARY) || (view.getType() == View2D.Type.UNKNOWN)) {
            Vector2f location = (layout != null) ? layout.getLocation(component) : new Vector2f(component.getX(), component.getY());
            component.setLocation((int) location.x, (int) location.y, false);
            view.setLocationOrtho(new Vector2f(location.x + view.getDisplayerLocalWidth() / 2, location.y + view.getDisplayerLocalHeight() / 2), false);
        }

        if (component.getPreferredTransparency() != 1.0f) {
            // component has a preferred transparency, so set it to that
            // transparency initially
            component.setTransparency(component.getPreferredTransparency());
        } else {
            // fade the component in from invisible to the unfocused
            // transparency
            component.changeTransparency(1.0f, unfocusedTransparency);
        }

        // set the initial control state
        view.setControlled(component.hasControl());

        // set the text to display on the frame header (if displayed)
        view.setTitle(component.getName());

        // display the component
        if (((HUDComponent2D) component).isHUDManagedWindow()) {
            view.setDecorated(component.getDecoratable());
            view.setVisibleApp(true);
        }

        view.setVisibleUser(true);
    }

    public void setFocused(HUDComponent component, boolean focused) {
        if (component != null) {
            component.changeTransparency(component.getTransparency(),
                    focused ? focusedTransparency : ((component.getPreferredTransparency() != 1.0f) ? component.getPreferredTransparency() : unfocusedTransparency),
                    focused ? fadeInTime : fadeOutTime);

        }
    }

    public void setTransparency(HUDView2D view, final float transparency) {
        if (view != null) {
            GeometryNode node = view.getGeometryNode();

            if ((node == null) || !(node.getChild(0) instanceof TexturedQuad)) {
                logger.warning("can't find quad for view, unable to set transparency");
                return;
            }
            final TexturedQuad quad = (TexturedQuad) node.getChild(0);

            RenderUpdater updater = new RenderUpdater() {

                public void update(Object arg0) {
                    WorldManager wm = (WorldManager) arg0;

                    BlendState as = (BlendState) wm.getRenderManager().createRendererState(RenderState.StateType.Blend);
                    // activate blending
                    as.setBlendEnabled(true);
                    // set the source function
                    as.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
                    // set the destination function
                    as.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
                    // disable test
                    as.setTestEnabled(false);
                    // activate the blend state
                    as.setEnabled(true);

                    // assign the blender state to the node
                    quad.setRenderState(as);
                    quad.updateRenderState();

                    MaterialState ms = (MaterialState) quad.getRenderState(RenderState.StateType.Material);
                    if (ms == null) {
                        ms = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
                        quad.setRenderState(ms);
                    }

                    if (ms != null) {
                        ColorRGBA diffuse = ms.getDiffuse();
                        diffuse.a = 1.0f - transparency;
                        ms.setDiffuse(diffuse);
                    } else {
                        logger.warning("quad has no material state, unable to set transparency");
                        return;
                    }

                    ColorRGBA color = quad.getDefaultColor();
                    color.a = transparency;
                    quad.setDefaultColor(color);

                    wm.addToUpdateList(quad);
                }
            };
            WorldManager wm = ClientContextJME.getWorldManager();
            wm.addRenderUpdater(updater, wm);
        }
    }

    public void setTransparency(HUDComponent2D component, final float transparency) {
        HUDComponentState state = (HUDComponentState) hudStateMap.get(component);
        if (state != null) {
            setTransparency(state.getView(), transparency);
        }
    }

    protected void componentInvisible(HUDComponent2D component) {
        if (logger.isLoggable(Level.INFO)) {
            logger.info("hiding HUD component on HUD: " + component);
        }

        HUDComponentState state = (HUDComponentState) hudStateMap.get(component);

        if ((state != null) && (state.isVisible())) {
            HUDView2D view = state.getView();

            if (view != null) {
                logger.fine("hiding HUD view");
                if (component.isHUDManagedWindow()) {
                    view.setVisibleApp(false, false);
                }
                view.setVisibleUser(false);
            } else {
                logger.warning("attempt to set HUD invisible with no HUD view");
            }
        }
    }

    protected void componentWorldVisible(HUDComponent2D component) {
        if (logger.isLoggable(Level.INFO)) {
            logger.info("showing HUD component in world: " + component);
        }

        HUDComponentState state = (HUDComponentState) hudStateMap.get(component);

        if ((state != null) && (!state.isWorldVisible())) {
            Cell cell = component.getCell();
            if (cell != null) {
                // can only create world views of HUD components that are
                // associated with a cell
                HUDView3D worldView = state.getWorldView();

                if (worldView == null) {
                    logger.fine("creating new world displayer");
                    HUDView3DDisplayer worldDisplayer = new HUDView3DDisplayer(cell);

                    logger.fine("creating new in-world view");
                    worldView = worldDisplayer.createView(state.getWindow());
                    worldView.setPixelScale(WonderlandHUD.HUD_WORLD_SCALE);
                    state.setWorldView(worldView);
                }

                logger.fine("displaying in-world view");
                worldView.setOrtho(false, false);
                worldView.setPixelScale(WonderlandHUD.HUD_WORLD_SCALE);
                worldView.setVisibleApp(true, false);
                worldView.setVisibleUser(true, false);
                componentMovedWorld(component);
                worldView.update();
                worldView.updateFrame();
            }
        }
    }

    protected void componentWorldInvisible(HUDComponent2D component) {
        if (logger.isLoggable(Level.INFO)) {
            logger.info("hiding HUD component in world: " + component);
        }

        HUDComponentState state = (HUDComponentState) hudStateMap.get(component);

        if ((state != null) && (state.isWorldVisible())) {
            HUDView3D worldView = state.getWorldView();

            if (worldView != null) {
                logger.fine("hiding in-world view");
                worldView.setVisibleApp(false, false);
                worldView.setVisibleUser(false, false);
                worldView.update();
                worldView.updateFrame();
            } else {
                logger.warning("attempt to set world invisible with no world view");
            }
        }
    }

    protected void componentMoved(HUDComponent2D component) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("moving component to: " + component.getX() + ", " + component.getY());
        }

        HUDComponentState state = (HUDComponentState) hudStateMap.get(component);
        if (state == null) {
            return;
        }

        HUDView2D view = state.getView();
        if (view != null) {
            // position the component on the screen
            Vector2f location = (layout != null) ? layout.getLocation(component) : new Vector2f(component.getX(), component.getY());
            view.setLocationOrtho(new Vector2f(location.x + view.getDisplayerLocalWidth() / 2, location.y + view.getDisplayerLocalHeight() / 2), true);
        }
    }

    protected void componentMovedWorld(HUDComponent2D component) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("moving HUD component in world: " + component);
        }

        HUDComponentState state = (HUDComponentState) hudStateMap.get(component);
        if (state == null) {
            return;
        }

        HUDView3D view = state.getWorldView();
        if (view != null) {
            Vector3f worldLocation = component.getWorldLocation();
            view.setOffset(new Vector2f(worldLocation.x, worldLocation.y));
            view.applyDeltaTranslationUser(worldLocation);
        }
    }

    protected void componentResized(final HUDComponent2D component) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("resizing HUD component: " + component); 
        }

        final HUDComponentState state = (HUDComponentState) hudStateMap.get(component);
        if (state == null) {
            return;
        }

        final HUDView2D view = state.getView();
        if (view != null) { 
            view.setSizeApp(component.getSize());
            
            // issue 151: update componentLocation by deriving it from location on glass
            component.setLocation(view.getLocation());
        }
    }

    protected void componentViewChanged(HUDComponent2D component) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.fine("changing HUD component view: " + component);
        }

        HUDComponentState state = (HUDComponentState) hudStateMap.get(component);
        if (state == null) {
            return;
        }

        HUDView2D view = state.getView();
        if (component.getDisplayMode().equals(DisplayMode.HUD)) {
            // moving to HUD
            view.setLocationOrtho(new Vector2f(component.getX(), component.getY()), false);
            view.setOrtho(true);
        } else {
            // moving to world
            // position HUD in x, y
            Vector3f worldLocation = component.getWorldLocation();
            view.setOffset(new Vector2f(worldLocation.x, worldLocation.y));
            view.applyDeltaTranslationUser(worldLocation);
            view.setOrtho(false);
        }
    }

    protected void componentMinimized(final HUDComponent2D component) {
        if (logger.isLoggable(Level.INFO)) {
            logger.info("minimizing HUD component: " + component);
        }

        HUDComponentState state = (HUDComponentState) hudStateMap.get(component);

        if (state != null) {
            component.setVisible(false);
        }
    }

    protected void componentMaximized(HUDComponent2D component) {
        if (logger.isLoggable(Level.INFO)) {
            logger.info("maximizing HUD component: " + component);
        }

        HUDComponentState state = (HUDComponentState) hudStateMap.get(component);
        if (state != null) {
            component.setVisible(true);
        }
    }

    protected void componentClosed(HUDComponent2D component) {
        if (logger.isLoggable(Level.INFO)) {
            logger.info("closing HUD component: " + component);
        }
    }

    protected void componentTransparencyChanged(HUDComponent2D component) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("changing transparency of: " + component);
        }

        float transparency = component.getTransparency();
        setTransparency(component, transparency);
    }

    protected void componentNameChanged(HUDComponent2D component) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("changing name of: " + component);
        }

        HUDComponentState state = (HUDComponentState) hudStateMap.get(component);
        if (state != null) {
            HUDView2D view = state.getView();
            if (view != null) {
                view.setTitle(component.getName());
            }
        }
    }

    protected void componentControlChanged(HUDComponent2D component) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("changing control of: " + component);
        }
        HUDComponentState state = (HUDComponentState) hudStateMap.get(component);
        if (state != null) {
            HUDView2D view = state.getView();
            if (view != null) {
                view.setControlled(component.hasControl());
            }
        }
    }

    public void changedType(HUDView2D view, View2D.Type type) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("view: " + view + " changed to type: " + type);
        }
        if ((type != View2D.Type.UNKNOWN) && (type != View2D.Type.PRIMARY)) {
            // Views of apps displayed on the the HUD can change type when they
            // are displayed. The view placement stategy depends on the type
            // of the view. So, when a view changes type, we may need to
            // re-position the view. In one scenario, a view changes from
            // type UNKNOWN to type POPUP. The HUD component manager explicitly
            // positions views of type UNKNOWN, but must not position views
            // of type POPUP. Fortunately, View2DEntity can be made to
            // re-evaluate its view placement by resetting it's ortho position
            // like this:
            view.setLocationOrtho(new Vector2f());
        }
    }

    private void handleHUDComponentChanged(HUDEvent event) {
        HUDComponent2D comp = (HUDComponent2D) event.getObject();

        switch (event.getEventType()) {
            case ADDED:
                // event generated by HUD
                addComponent(comp);
                break;
            case REMOVED:
                // event generated by HUD
                removeComponent(comp);
                break;
            case APPEARED:
                componentVisible(comp);
                break;
            case APPEARED_WORLD:
                componentWorldVisible(comp);
                break;
            case DISAPPEARED:
                componentInvisible(comp);
                break;
            case DISAPPEARED_WORLD:
                componentWorldInvisible(comp);
                break;
            case CHANGED_MODE:
                componentViewChanged(comp);
                break;
            case MOVED:
                componentMoved(comp);
                break;
            case MOVED_WORLD:
                componentMovedWorld(comp);
                break;
            case RESIZED:
                componentResized(comp);
                break;
            case MINIMIZED:
                componentMinimized(comp);
                break;
            case MAXIMIZED:
                componentMaximized(comp);
                break;
            case ENABLED:
                break;
            case DISABLED:
                break;
            case CHANGED_TRANSPARENCY:
                componentTransparencyChanged(comp);
                break;
            case CHANGED_NAME:
                componentNameChanged(comp);
                break;
            case CHANGED_CONTROL:
                componentControlChanged(comp);
                break;
            case CLOSED:
                componentClosed(comp);
                break;
            default:
                logger.info("TODO: handle HUD event type: " + event.getEventType());
                break;
        }
    }

    private void handleHUDChanged(HUDEvent event) {
        switch (event.getEventType()) {
            case RESIZED:
                relayout();
                break;
            default:
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void HUDObjectChanged(HUDEvent event) {
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("HUD object changed: " + event);
        }
        if (event.getObject() instanceof HUDComponent2D) {
            handleHUDComponentChanged(event);
        } else if (event.getObject() instanceof HUD) {
            handleHUDChanged(event);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setLayoutManager(HUDLayoutManager layout) {
        this.layout = layout;
    }

    /**
     * {@inheritDoc}
     */
    public HUDLayoutManager getLayoutManager() {
        return layout;
    }

    /**
     * {@inheritDoc}
     */
    public void relayout() {
        if (layout != null) {
            layout.relayout();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void relayout(HUDComponent component) {
        if (layout != null) {
            layout.relayout(component);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setVisible(HUDComponent component, boolean visible) {
        HUDComponentState state = (HUDComponentState) hudStateMap.get(component);
        if (state != null) {
            component.setVisible(visible);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isVisible(HUDComponent component) {
        boolean visible = false;
        HUDComponentState state = (HUDComponentState) hudStateMap.get(component);
        if (state != null) {
            visible = state.isVisible();
        }
        return visible;
    }

    /**
     * {@inheritDoc}
     */
    public void minimizeComponent(HUDComponent component) {
        HUDComponentState state = (HUDComponentState) hudStateMap.get(component);
        if (state != null) {
            component.setMinimized();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void maximizeComponent(HUDComponent component) {
        HUDComponentState state = (HUDComponentState) hudStateMap.get(component);
        if (state != null) {
            component.setMaximized();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void raiseComponent(HUDComponent component) {
        HUDComponentState state = (HUDComponentState) hudStateMap.get(component);
        if (state != null) {
            int zorder = state.getZOrder();
            state.setZOrder(zorder++);
            // TODO: update component
        }
    }

    /**
     * {@inheritDoc}
     */
    public void lowerComponent(HUDComponent component) {
        HUDComponentState state = (HUDComponentState) hudStateMap.get(component);
        if (state != null) {
            int zorder = state.getZOrder();
            state.setZOrder(zorder--);
            // TODO: update component
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getComponentZOrder(HUDComponent component) {
        int zorder = 0;
        HUDComponentState state = (HUDComponentState) hudStateMap.get(component);
        if (state != null) {
            zorder = state.getZOrder();

        }
        return zorder;
    }

    public void close(HUDComponent component) {
        component.setVisible(false);
        component.setClosed();
    }
}
