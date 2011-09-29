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
 * A manager for a set of HUDs.
 *
 * @author nsimpson
 */
public abstract class HUDManager {

    /**
     * Adds a HUD to the set of managed HUDs
     * @param hud the HUD to add
     */
    public abstract void addHUD(HUD hud);

    /**
     * Removes a HUD from the set of managed HUDs
     * @param hud the HUD to remove
     */
    public abstract void removeHUD(HUD hud);

    /**
     * Gets a HUD by name
     * @param name the name of the HUD to get
     * @return a HUD if there's a HUD with the specified name
     */
    public abstract HUD getHUD(String name);

    /**
     * Gets an iterator that will iterate over the current set of HUDs managed
     * by the HUDManager
     * @return an iterator for HUDs
     */
    public abstract Iterator<HUD> getHUDs();

    /**
     * Assigns a layout manager which determines how HUDs are laid out by the
     * HUDManager
     * @param layout
     */
    public abstract void setLayoutManager(HUDLayoutManager layout);

    /**
     * Gets the layout manager
     * @return the HUDLayoutManager, if set
     */
    public abstract HUDLayoutManager getLayoutManager();

    /**
     * Force the layout manager to re-layout all the HUDs
     */
    public abstract void relayout();

    /**
     * Force the layout manager to re-layout the specified HUD
     * @param hud the HUD to re-layout
     */
    public abstract void relayout(HUD hud);

    /**
     * Change the visibility of a HUD
     * @param hud the HUD to change
     * @param visible true to make the HUD visible, false to make invisible
     */
    public abstract void setVisible(HUD hud, boolean visible);

    /**
     * Gets whether a HUD is visible
     * @param hud the hud to check for visibility
     * @return the visibility of the specified HUD
     */
    public abstract boolean isVisible(HUD hud);

    /**
     * Minimize a HUD
     * @param hud the HUD to minimize
     */
    public abstract void minimizeHUD(HUD hud);

    /**
     * Maximize a HUD
     * @param hud the HUD to maximize
     */
    public abstract void maximizeHUD(HUD hud);

    /**
     * Raise a HUD in the stacking order
     * @param hud the HUD to raise
     */
    public abstract void raiseHUD(HUD hud);

    /**
     * Lower a HUD in the stacking order
     * @param hud the HUD to lower
     */
    public abstract void lowerHUD(HUD hud);
}
