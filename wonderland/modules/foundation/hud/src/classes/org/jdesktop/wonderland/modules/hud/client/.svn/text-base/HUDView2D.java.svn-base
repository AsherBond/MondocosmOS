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

/*
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
package org.jdesktop.wonderland.modules.hud.client;

import com.jme.math.Vector2f;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderComponent;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDView;
import org.jdesktop.wonderland.modules.appbase.client.Window2D;
import org.jdesktop.wonderland.modules.appbase.client.swing.WindowSwing;
import org.jdesktop.wonderland.modules.appbase.client.view.GeometryNode;
import org.jdesktop.wonderland.modules.appbase.client.view.View2DDisplayer;
import org.jdesktop.wonderland.modules.appbase.client.view.View2DEntity;

/**
 * A 2D view for HUD component windows.
 *
 * @author nsimpson
 */
public class HUDView2D extends View2DEntity implements HUDView, MouseMotionListener,
        ActionListener {

    private static final Logger logger = Logger.getLogger(HUDView2D.class.getName());

    private final HUD hud;
    private final View2DDisplayer displayer;

    private HUDView2DDisplayer hudDisplayer;
    private HUDFrameHeader2D frame;
    private HUDView2D frameView;
    private List<ActionListener> actionListeners;
    private List<MouseMotionListener> mouseMotionListeners;
    private List<HUDViewListener> viewListeners;

    /**
     * Create an instance of HUDView2D with default geometry node.
     * @param displayer the entity in which the view is displayed.
     * @param window the window displayed in this view.
     */
    public HUDView2D(HUD hud, View2DDisplayer displayer, Window2D window) {
        this(hud, displayer, window, null);
    }

    /**
     * Create an instance of HUDView2D with a specified geometry node.
     * @param displayer the entity in which the view is displayed.
     * @param window The window displayed in this view.
     * @param geometryNode The geometry node on which to display the view.
     */
    public HUDView2D(HUD hud, View2DDisplayer displayer, Window2D window, GeometryNode geometryNode) {
        super(window, geometryNode);

        this.displayer = displayer;
        this.hud = hud;

        changeMask = CHANGED_ALL;
        name = "HUDView2D for " + window.getName();
        update();
        updateFrame();
    }

    private void createFrame() {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("creating frame header for: " + this);
        }

        HUDApp2D app = new HUDApp2D(hud, "HUD", new ControlArbHUD(), WonderlandHUD.HUD_SCALE);
        if (hudDisplayer == null) {
            hudDisplayer = new HUDView2DDisplayer(hud);
        }

        // create the Swing frame component and resize it to fit this view's
        // window
        HUDFrameHeader2DImpl frameImpl = new HUDFrameHeader2DImpl();
        Dimension frameSize = new Dimension(getWindow().getWidth(), frameImpl.getPreferredSize().height);
        frameImpl.setPreferredSize(frameSize);
        frame = new HUDFrameHeader2D(frameImpl);

        if (window.getApp() instanceof HUDApp2D) {
            // HACK! We only want to show the "remove from HUD" button for
            // views that are not managed by the HUD.
            frame.showHUDButton(false);
        }

        try {
            // create a window
            WindowSwing frameWindow = app.createWindow(frameSize.width, frameSize.height, Window2D.Type.PRIMARY, false, WonderlandHUD.HUD_SCALE, name);
            frameWindow.setComponent(frameImpl);
            frame.setWindow(frameWindow);
            frame.setTitle(getTitle());

            // create a view
            frameView = hudDisplayer.createView(frameWindow);
            frameWindow.addView(frameView);

            // set view properties
            frameView.setPixelScaleOrtho(WonderlandHUD.HUD_SCALE, false);
            frameView.setSizeApp(new Dimension((int) (frameWindow.getWidth()), frameWindow.getHeight()), false);
            frameView.setLocationOrtho(new Vector2f(0.0f, (float) (WonderlandHUD.HUD_SCALE.y * getWindow().getHeight() / 2 + WonderlandHUD.HUD_SCALE.y * frameImpl.getPreferredSize().height / 2)), false);
            frameView.setOrtho(true, false);

            // register listeners for events on the frame
            frameImpl.addMouseMotionListener(frame);
            frame.addMouseMotionListener(this);

            frameImpl.addActionListener(frame);
            frame.addActionListener(this);
        } catch (InstantiationException e) {
            logger.warning("failed to create window from HUD frame: " + e);
        }
    }

    /** 
     * {@inheritDoc}
     */
    public View2DDisplayer getDisplayer() {
        return displayer;
    }

    /**
     * {@inheritDoc}
     */
    protected Entity getParentEntity() {
        View2DEntity parentView = (View2DEntity) getParent();
        if (parentView == null) {
            return null;
        }
        return parentView.getEntity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setType(Type type, boolean update) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("view type changed from: " + getType() + " > " + type + " for " + this);
        }
        super.setType(type, update);

        if (viewListeners != null) {
            ListIterator<HUDViewListener> iter = viewListeners.listIterator();
            while (iter.hasNext()) {
                HUDViewListener listener = iter.next();
                listener.changedType(this, type);
            }
            iter = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean hasFrame() {
        return (frameView != null);
    }

    public void setFrameView(HUDView2D frameView) {
        this.frameView = frameView;
    }

    public HUDView2D getFrameView() {
        return frameView;
    }

    public void attachView(HUDView2D view) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("attach view: " + view);
            logger.finest("to: " + this);
        }

        Entity e = view.getEntity();
        RenderComponent rcFrame = (RenderComponent) e.getComponent(RenderComponent.class);
        rcFrame.setAttachPoint(this.getGeometryNode());
    }

    public void detachView(HUDView2D view) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("detach view: " + view);
            logger.finest("from: " + this);
        }

        Entity viewEntity = view.getEntity();
        if (viewEntity == null) {
            return;
        }
        entity.removeEntity(viewEntity);
        RenderComponent rcFrame = (RenderComponent) viewEntity.getComponent(RenderComponent.class);
        if (rcFrame != null) {
            rcFrame.setAttachPoint(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reattachFrame() {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("reattaching frame");
        }

        detachFrame();
        attachFrame();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void attachFrame() {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("attaching frame");
        }

        if (frameView == null) {
            createFrame();
        }
        if (frameView != null) {
            attachView(frameView);
            frameView.setVisibleApp(true, false);
            frameView.setVisibleUser(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void detachFrame() {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("detaching frame");
        }

        if (frameView != null) {
            frameView.setVisibleUser(false, false);
            frameView.setVisibleApp(false);
            detachView(frameView);
            frameView = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void frameUpdateTitle() {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("updating frame title");
        }

        if (frame != null) {
            frame.setTitle(getTitle());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void frameUpdate() {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("updating frame");
        }
        if (frameView != null) {
            Window2D frameWindow = frameView.getWindow();
            Window2D appWindow = getWindow();
            if (appWindow != null) {
                int appWidth = appWindow.getWidth();
                int frameWidth = frameWindow.getWidth();
                if (frameWidth != appWidth) {
                    // adjust frame to fit width of app window
                    frameView.getWindow().setSize(appWidth, frameWindow.getHeight());
                }
            }
        }
    }

    public void setControlled(boolean controlled) {
        if (frame != null) {
            frame.setControlled(controlled);
        }
    }

    public void actionPerformed(ActionEvent e) {
        e.setSource(this);
        notifyActionListeners(e);
    }

    public void mouseMoved(MouseEvent e) {
        e.setSource(this);
        notifyMouseMotionListeners(e);
    }

    public void mouseDragged(MouseEvent e) {
        e.setSource(this);
        notifyMouseMotionListeners(e);
    }

    public void addActionListener(ActionListener listener) {
        if (actionListeners == null) {
            actionListeners = Collections.synchronizedList(new LinkedList());
        }
        actionListeners.add(listener);
    }

    public void removeActionListener(ActionListener listener) {
        if (actionListeners != null) {
            actionListeners.remove(listener);
        }
    }

    public void notifyActionListeners(ActionEvent e) {
        if (actionListeners != null) {
            ListIterator<ActionListener> iter = actionListeners.listIterator();
            while (iter.hasNext()) {
                ActionListener listener = iter.next();
                listener.actionPerformed(e);
            }
            iter = null;
        }
    }

    public void addMouseMotionListener(MouseMotionListener listener) {
        if (mouseMotionListeners == null) {
            mouseMotionListeners = Collections.synchronizedList(new LinkedList());
        }
        mouseMotionListeners.add(listener);
    }

    public void removeMouseMotionListener(MouseMotionListener listener) {
        if (mouseMotionListeners != null) {
            mouseMotionListeners.remove(listener);
        }
    }

    public void notifyMouseMotionListeners(MouseEvent e) {
        if (mouseMotionListeners != null) {
            e.setSource(this);
            ListIterator<MouseMotionListener> iter = mouseMotionListeners.listIterator();
            while (iter.hasNext()) {
                MouseMotionListener listener = iter.next();

                switch (e.getID()) {
                    case MouseEvent.MOUSE_MOVED:
                        listener.mouseMoved(e);
                        break;
                    case MouseEvent.MOUSE_DRAGGED:
                        listener.mouseDragged(e);
                        break;
                    default:
                        break;
                }
            }
            iter = null;
        }
    }

    public void addHUDViewListener(HUDViewListener listener) {
        if (viewListeners == null) {
            viewListeners = Collections.synchronizedList(new LinkedList());
        }
        viewListeners.add(listener);
    }

    public void removeHUDViewListener(HUDViewListener listener) {
        if (viewListeners != null) {
            viewListeners.remove(listener);
        }
    }

    @Override
    public String toString() {
        String string = "view type: " + getClass().getSimpleName() +
                ", name: " + getName() +
                ", size: " + getSizeApp() +
                ", ortho: " + isOrtho();

        if (isOrtho()) {
            string += ", ortho location: " + this.getLocationOrtho();
        }
        return string;
    }
}
