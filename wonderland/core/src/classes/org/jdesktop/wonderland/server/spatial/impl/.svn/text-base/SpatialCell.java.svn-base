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
package org.jdesktop.wonderland.server.spatial.impl;

import com.jme.bounding.BoundingVolume;
import com.sun.sgs.auth.Identity;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.server.spatial.ViewUpdateListener;

/**
 *
 * @author paulby
 */
public interface SpatialCell {

    /**
     * Return the bounds of the object in the local coordinate system
     * @return the localBounds
     */
    public BoundingVolume getLocalBounds();

    /**
     * Set the bounds of this object in the local coordinate system.
     * Note the system guarantees that
     * the bounds of a parent fully enclose the bounds of a child, so this
     * call may cause the world bounds of parent cells to be updated.
     *
     * @param localBounds the localBounds to set
     */
    public void setLocalBounds(BoundingVolume localBounds);

    /**
     * Get the bounds of this object in world coordinates
     * @return
     */
    public BoundingVolume getWorldBounds();

    /**
     * Return the local transform of this object
     * @return the transform
     */
    public CellTransform getLocalTransform();

    /**
     * Set the local transform for this object. Note the system guarantees that
     * the bounds of a parent fully enclose the bounds of a child, so this
     * call may cause the world bounds of parent cells to be updated.
     *
     * @param transform the transform to set
     */
    public void setLocalTransform(CellTransform transform, Identity identity);

    /**
     * Add the supplied object as a child of this.
     * @param child
     */
    public void addChild(SpatialCell child, Identity identity);

    /**
     * Get the set of children for this SpatialCell
     * @return
     */
//    public Iterable getChildren();

    /**
     * Remove the specified child from this SpatialCell
     * 
     * @param child
     */
    public void removeChild(SpatialCell child);

    /**
     * Set the current state of an attibute
     * @param attr the attr state
     */
    public void setAttribute(Object attr);

    /**
     * Revalidate this cell, causing each cache to decide whether or not
     * to reload the cell.
     */
    public void revalidate();

    /**
     * Destroy this cell, removing it from all the view caches
     */
    public void destroy();

    /**
     * Add a ViewUpdateLIstener to this cell. This listener will be called
     * whenever the view of a ViewCache that contains this cell is updated
     * 
     * @param viewUpdateListener listener to add
     */
    public void addViewUpdateListener(ViewUpdateListener viewUpdateListener);

    /**
     * Remove the specified ViewUpdateListener
     * @param viewUpdateListener listener to remove
     */
    public void removeViewUpdateListener(ViewUpdateListener viewUpdateListener);

    /**
     * Re-notify each listener of the position of this cell
     */
    public void revalidateListeners(Identity identity);
}
