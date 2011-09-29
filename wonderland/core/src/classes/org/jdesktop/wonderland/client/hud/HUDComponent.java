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

/**
 * A HUDComponent is an abstraction for an underlying visual element (2D or 3D)
 * that can be displayed on the HUD. 
 * 
 * A HUDComponent has a 2D position, and width and height. It can be visible
 * or invisible. It can also be enabled, in which case it responds to mouse
 * and keyboard events, or disabled.
 *
 * @author nsimpson
 */
public interface HUDComponent extends HUDObject {
}
