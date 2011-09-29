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

import java.util.logging.Logger;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.modules.appbase.client.Window2D;

/**
 * Maintains the display state of a HUD component
 *
 * @author nsimpson
 */
public class HUDComponentState {

    private static final Logger logger = Logger.getLogger(HUDComponentState.class.getName());
    private HUDComponent component;
    private HUDComponentVisualState state;
    // HUD component visuals
    private Window2D window;
    private HUDView2D view;
    private HUDView3D worldView;
    // HUD frame visuals
    private HUDFrameHeader2D frame;
    private Window2D frameWindow;
    private HUDView2D frameView;
    private HUDImageComponent icon;
    private boolean decorated;
    private int zorder;

    public enum HUDComponentVisualState {

        MINIMIZED, NORMAL, MAXIMIZED
    };

    public HUDComponentState(HUDComponent component) {
        this(component, false, HUDComponentVisualState.NORMAL, 0);
    }

    public HUDComponentState(HUDComponent component,
            boolean decorated, HUDComponentVisualState state, int zorder) {
        this.component = component;
        this.decorated = decorated;
        this.state = state;
        this.zorder = zorder;
    }

    /**
     * Sets the managed HUD component
     * @param component the HUD component
     */
    public void setComponent(HUDComponent component) {
        this.component = component;
    }

    /**
     * Gets the managed HUD component
     * @return the managed HUD component
     */
    public HUDComponent getComponent() {
        return component;
    }

    /**
     * Sets the frame decoration for the HUD component
     * @param frame the frame
     */
    public void setFrame(HUDFrameHeader2D frame) {
        this.frame = frame;
    }

    /**
     * Gets the frame for the HUD component
     * @return the HUD component's frame
     */
    public HUDFrameHeader2D getFrame() {
        return frame;
    }

    /**
     * Sets the frame view of the HUD component
     * @param view the HUD component's frame view
     */
    public void setFrameView(HUDView2D frameView) {
        this.frameView = frameView;
    }

    /**
     * Gets the HUD component's frame view
     * @return the HUD component's frame view
     */
    public HUDView2D getFrameView() {
        return frameView;
    }

    /**
     * Sets the HUD component's frame window
     * @param window the HUD components frame window
     */
    public void setFrameWindow(Window2D frameWindow) {
        this.frameWindow = frameWindow;
    }

    /**
     * Gets the HUD component's frame window
     * @return the HUD component's window
     */
    public Window2D getFrameWindow() {
        return frameWindow;
    }

    /**
     * Sets the window of the HUD component
     * @param window the HUD component's window
     */
    public void setWindow(Window2D window) {
        this.window = window;
    }

    /**
     * Gets the HUD component's window
     * @return the HUD component's window
     */
    public Window2D getWindow() {
        return window;
    }

    /**
     * Sets the view of the HUD component
     * @param view the HUD component's view
     */
    public void setView(HUDView2D view) {
        this.view = view;
    }

    /**
     * Gets the HUD component's view
     * @return the HUD component's view
     */
    public HUDView2D getView() {
        return view;
    }

    /**
     * Sets the world view of the HUD component
     * @param view the HUD component's view
     */
    public void setWorldView(HUDView3D view) {
        this.worldView = view;
    }

    /**
     * Gets the HUD component's world view
     * @return the HUD component's world view
     */
    public HUDView3D getWorldView() {
        return worldView;
    }

    /**
     * Gets whether the HUD component is visible
     * @return true if the HUD component is visible, false if it's hidden
     */
    public boolean isVisible() {
        return ((view != null) && (view.isActuallyVisible()));
    }

    /**
     * Gets whether the HUD component is visible in world
     * @return true if the HUD component is visible in world, false if it's hidden
     */
    public boolean isWorldVisible() {
        return ((worldView != null) && (worldView.isActuallyVisible()));
    }

    /**
     * Sets whether the HUD component is decorated
     * @param decorated true to decorate the HUD component, false to remove
     * decorations
     */
    public void setDecorated(boolean decorated) {
        this.decorated = decorated;
    }

    /**
     * Gets whether the HUD component is decorated
     * @return true if the HUD component is decorated, false if it is not
     */
    public boolean isDecorated() {
        return decorated;
    }

    /** 
     * Sets the icon associated with the HUD component
     * @param icon the icon
     */
    public void setIcon(HUDImageComponent icon) {
        this.icon = icon;
    }

    /**
     * Gets the icon associated with the HUD component
     * @return the icon
     */
    public HUDImageComponent getIcon() {
        return icon;
    }

    /**
     * Sets the visual state of the HUD component, whether it's minimized,
     * maximized or in normal state
     * @param state the new visual state of the HUD component
     */
    public void setState(HUDComponentVisualState state) {
        this.state = state;
    }

    /**
     * Get's the visual state of the HUD component
     * @return the visual state
     */
    public HUDComponentVisualState getState() {
        return state;
    }

    /**
     * Sets the z-order of the HUD component
     * @param zorder the z-order of the HUD component
     */
    public void setZOrder(int zorder) {
        this.zorder = zorder;
    }

    /**
     * Gets the z-order of the HUD component
     * @return the HUD component's z-order
     */
    public int getZOrder() {
        return zorder;
    }
}
