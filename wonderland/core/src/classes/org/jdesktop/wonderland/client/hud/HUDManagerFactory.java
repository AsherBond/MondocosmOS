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

import java.awt.Canvas;

/**
 * A HUD Manager factory which creates new HUD Manager instances by
 * delegating to a HUD Manager Factory instance.
 *
 * The HUD system allows multiple HUD instances to share a client window.
 * Each HUD has a 2D position and a width and height.
 *
 * A HUDManager manages the placement and visual attributes of all the
 * HUD instances in a given client window.
 *
 * @author nsimpson
 */
public class HUDManagerFactory {

    private static HUDManagerFactorySPI spi;

    /**
     * Binds a specific HUD Manager Factory service provider to this factory
     * @param spii an instance of a HUD Manager Factory service provider
     */
    public static void setHUDManagerFactorySPI(final HUDManagerFactorySPI spii) {
        spi = spii;
    }

    /**
     * Creates a new instance of a HUD Manager
     * @return a new HUD Manager instance if a HUD Manager Factory exists,
     * null otherwise
     */
    public static HUDManager createHUDManager(Canvas canvas) {
        if (spi != null) {
            return spi.createHUDManager(canvas);
        } else {
            return null;
        }
    }

    /**
     * Gets the HUD Manager instance singleton
     * @return the HUD Manager instance if it has been created, null otherwise
     */
    public static HUDManager getHUDManager() {
        if (spi != null) {
            return spi.getHUDManager();
        } else {
            return null;
        }
    }
}
