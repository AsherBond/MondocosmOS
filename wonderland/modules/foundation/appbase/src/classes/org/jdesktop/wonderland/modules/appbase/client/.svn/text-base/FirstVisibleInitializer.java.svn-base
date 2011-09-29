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
package org.jdesktop.wonderland.modules.appbase.client;

import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * Used to initialize a cell before the first time something something is made visible 
 * within the cell. (This is usually done by the first slave that gets around to it. Or, 
 * in the case, of a user-launched app cell, by the master).
 *
 * @author deronj
 */
@ExperimentalAPI
public interface FirstVisibleInitializer {

    /** 
     * A window has been made visible. Perform the initialization. 
     * @param width3D The width of the window in world units.
     * @param height3D The height of the window in world units.
     */
    public void initialize (float width3D, float height3D);
}