/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

/**
 *
 * @author paulby
 */
class Space {

    private BoundingVolume worldBounds;

    private final HashSet<SpatialCellImpl> rootCells = new HashSet();
    private final HashSet<ViewCache> viewCaches = new HashSet();
    private String name;

    private final ReadWriteLock rootCellsLock = new ReentrantReadWriteLock();

    private static final Logger logger = Logger.getLogger(Space.class.getName());

    public Space(BoundingVolume worldBounds, String name) {
        this.worldBounds = worldBounds;
        this.name = name;
    }

    public BoundingVolume getWorldBounds() {
        return worldBounds;
    }

    public String getName() {
        return name;
    }

    public void addRootSpatialCell(SpatialCellImpl cell) {
        logger.fine("Adding cell "+cell.getCellID()+" to space "+getName());

        try {
            acquireRootCellWriteLock();
            rootCells.add(cell);
        } finally {
            releaseRootCellWriteLock();
        }
        
        synchronized(viewCaches) {
            cell.addViewCache(viewCaches, this);

            // issue 754: avoid concurrent modification
            ViewCache[] caches = viewCaches.toArray(new ViewCache[viewCaches.size()]);
            for(ViewCache cache : caches)
                cache.rootCellAdded(cell);
        }
    }

    public void removeRootSpatialCell(SpatialCellImpl cell) {
        logger.fine("Removing cell "+cell.getCellID()+" from space "+getName());

        try {
            acquireRootCellWriteLock();
            rootCells.remove(cell);
        } finally {
            releaseRootCellWriteLock();
        }
        
        synchronized(viewCaches) {
            cell.removeViewCache(viewCaches, this);

            // issue 754: avoid concurrent modification
            ViewCache[] caches = viewCaches.toArray(new ViewCache[viewCaches.size()]);
            for(ViewCache cache : caches)
                cache.rootCellRemoved(cell);
        }
    }

    public void addViewCache(ViewCache cache) {
        // make sure we have the roots lock before we go any further
        try {
            acquireRootCellReadLock();

            // now go ahead and add the cache
            synchronized(viewCaches) {
                viewCaches.add(cache);

                for (SpatialCellImpl rootCell : rootCells) {
                    rootCell.addViewCache(Collections.singletonList(cache), this);
                }
            }
        } finally {
            releaseRootCellReadLock();
        }
    }

    public void removeViewCache(ViewCache cache) {
         // make sure we have the roots lock before we go any further
        try {
            acquireRootCellReadLock();
            
            // now go ahead and remove the cache
            synchronized(viewCaches) {
                viewCaches.remove(cache);

                for(SpatialCellImpl rootCell : rootCells) {
                    rootCell.removeViewCache(Collections.singletonList(cache), this);
                }
            }
        } finally {
            releaseRootCellReadLock();
        }
    }

    /**
     * Returns a snapshot of the set of root cells (no need to hold a lock when
     * calling this method)
     * @return
     */
    public Collection<SpatialCellImpl> getRootCells() {
        try {
            acquireRootCellReadLock();
            return (Collection<SpatialCellImpl>) rootCells.clone();
        } finally {
            releaseRootCellReadLock();
        }
    }

    void acquireRootCellReadLock() {
        rootCellsLock.readLock().lock();
    }

    void releaseRootCellReadLock() {
        rootCellsLock.readLock().unlock();
    }

    void acquireRootCellWriteLock() {
        rootCellsLock.writeLock().lock();
    }

    void releaseRootCellWriteLock() {
        rootCellsLock.writeLock().unlock();
    }
}
