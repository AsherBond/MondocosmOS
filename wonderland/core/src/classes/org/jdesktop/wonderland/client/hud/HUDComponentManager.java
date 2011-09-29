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

import java.util.Iterator;

/**
 * A manager for a set of HUDComponents.
 *
 * @author nsimpson
 */
public interface HUDComponentManager extends HUDEventListener {

    /**
     * Adds a HUDComponent to the set of components to be managed
     * @param component a HUDComponent to manage
     */
    public void addComponent(HUDComponent component);

    /**
     * Removes a HUDComponent from the set of managed components
     * @param component the HUDComponent to remove
     */
    public void removeComponent(HUDComponent component);

    /**
     * Gets an interator for the set of managed components
     * @return an iterator for managed HUDComponents
     */
    public Iterator<HUDComponent> getComponents();

    /**
     * Sets the layout manager responsible for positioning HUDComponents
     * in a HUD
     * @param layout a HUD layout manager
     */
    public void setLayoutManager(HUDLayoutManager layout);

    /**
     * Gets the current layout manager
     * @return the current layout manager
     */
    public HUDLayoutManager getLayoutManager();

    /**
     * Force the layout manager to re-layout all the HUDComponents on the HUD
     */
    public void relayout();

    /**
     * Force the layout manager to re-layout the specified HUDComponent
     * @param component the component to re-layout
     */
    public void relayout(HUDComponent component);

    /**
     * Change the visibility of a HUDComponent
     * @param component the component to change
     * @param visible true to make the component visible, false to make invisible
     */
    public void setVisible(HUDComponent component, boolean visible);

    /**
     * Gets whether a HUDComponent is visible
     * @param component the component to check for visibility
     * @return the visibility of the component
     */
    public boolean isVisible(HUDComponent component);

    /**
     * Minimize a HUDComponent
     * @param component the component to minimize
     */
    public void minimizeComponent(HUDComponent component);

    /**
     * Maximize a HUDComponent
     * @param component the component to maximize
     */
    public void maximizeComponent(HUDComponent component);

    /**
     * Raise a HUDComponent one level in the stacking order
     * @param component the component to raise
     */
    public void raiseComponent(HUDComponent component);

    /**
     * Lower a HUDComponent one level in the stacking order
     * @param component the component to lower
     */
    public void lowerComponent(HUDComponent component);

    /**
     * Get's the stacking order of the specified HUDComponent
     * @param component the component to query for its z-order
     * @return the stacking order of the component
     */
    public int getComponentZOrder(HUDComponent component);
}
