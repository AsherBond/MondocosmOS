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
package org.jdesktop.wonderland.common.cell.state;

import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;
import java.io.Serializable;

/**
 * A hint provided in a Cell's server state about the Cell's bounds used to
 * position a Cell when it is initially created.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class BoundingVolumeHint implements Serializable {

    // True if the system should just use a simple placement and let the Cell
    // do its own placement after creation, true by default.
    private boolean doSystemPlacement = true;

    // The bounding volume hint for the Cell, by default a Sphere or radius 1
    private BoundingVolume boundsHint = new BoundingSphere(1.0f, Vector3f.ZERO);

    /** Defualt constructor, needed for JAXB */
    public BoundingVolumeHint() {
    }

    /**
     * Constructor, takes the bounds hint and whether to tell the system to
     * do the placement.
     *
     * @param doSystemPlacement True if the system should attempt to place the
     * Cell intelligently
     * @param boundsHint The bounds hint given to the system
     */
    public BoundingVolumeHint(boolean doSystemPlacement, BoundingVolume boundsHint) {
        this.doSystemPlacement = doSystemPlacement;
        this.boundsHint = boundsHint;
    }

    /**
     * Returns true if the system should attempt to place the Cell intelligently,
     * false to let the Cell do it.
     *
     * @return True for intelligent system Cell placement
     */
    public boolean isDoSystemPlacement() {
        return doSystemPlacement;
    }

    /**
     * Returns the bounding volume hint.
     *
     * @return The bounding volume hint for initial Cell placement
     */
    public BoundingVolume getBoundsHint() {
        return boundsHint;
    }
}
