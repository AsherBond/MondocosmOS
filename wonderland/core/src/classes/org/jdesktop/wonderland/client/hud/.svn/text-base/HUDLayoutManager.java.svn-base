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
package org.jdesktop.wonderland.client.hud;

import com.jme.math.Vector2f;

/**
 * A HUDLayoutManager lays out HUD components in a 2D rectangular space.
 *
 * A HUDLayoutManager could be used to layout HUDs on the screen or HUD
 * components within a HUD.
 * 
 * @author nsimpson
 */
public interface HUDLayoutManager {

    /**
     * Add a component to the list of component this layout manager manages.
     * @param component the component to manage
     */
    public void manageComponent(HUDComponent component);

    /**
     * Remove a component from the list of component this layout manager manages.
     * @param component the component to stop managing
     */
    public void unmanageComponent(HUDComponent component);

    /**
     * Associates a view with a component
     * @param component the component
     * @param view the view associated with this component
     */
    public void addView(HUDComponent component, HUDView view);

    /**
     * Removes a view from a component
     * @param component the component
     * @param view the view to remove from the component
     */
    public void removeView(HUDComponent component, HUDView view);

    /**
     * Gets the view associated with a component
     * @param component the component
     * @return the specified component's view
     */
    public HUDView getView(HUDComponent component);

    /**
     * Get the position of the given component according to the specified
     * layout.
     * @param component the component for which the position is needed
     * @return returns the location of the component on its containing HUD
     */
    public Vector2f getLocation(HUDComponent component);

    /**
     * Trigger a re-layout of all the managed HUD components
     */
    public void relayout();

    /**
     * Trigger a re-layout of the specified HUD component
     * @param component the component to re-layout
     */
    public void relayout(HUDComponent component);
}
