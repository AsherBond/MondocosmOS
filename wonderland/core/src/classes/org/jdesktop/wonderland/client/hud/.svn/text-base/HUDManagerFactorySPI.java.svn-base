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
 * A service provider interface for factories which create new HUD Manager
 * instances.
 *
 * @author nsimpson
 */
public interface HUDManagerFactorySPI {

    /**
     * Creates a new HUD Manager instance
     * @param display the display to manage
     * @return a new HUD Manager instance
     */
    public HUDManager createHUDManager(Canvas display);

    /**
     * Gets the HUD Manager singleton
     * @return the current HUD Manager
     */
    public HUDManager getHUDManager();
}
