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
package org.jdesktop.wonderland.common.cell;

/**
 *
 * CellStatus is the current state of a cell. The class is used for both client
 * and servers cell represenations, although the server does not use the RENDERING
 * or VISIBLE states.
 *
 * A cells state progresses from the lowest state (DISK), to the highest, and
 * the system guarantees that all intermediate states are visited. Cell developers
 * should do all their setup and tear down of a cells state and resources by
 * overriding setStatus(CellStatus status, boolean increasing). The increasing
 * boolean is true when the state is increasing (from DISK upwards) and false when
 * it's decreasing (from VISIBLE downwards).
 *
 * @author paulby
 */
public enum CellStatus {
    
    DISK,       // Cell is on disk with no memory footprint
    INACTIVE,   // Cell object and bounds are in memory, but the current state has not been set, state changes are not propagated to/from the server
    ACTIVE,     // Cell state is synchronized with the server
    RENDERING,  // Cell is close to the avatar, audio etc should start rendering, visual components should be loaded, but need not be rendered until VISIBLE
    VISIBLE     // Cell is in view frustum
}
