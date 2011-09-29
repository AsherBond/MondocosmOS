/**
 * Open Wonderland
 *
 * Copyright (c) 2010 - 2011, Open Wonderland Foundation, All Rights Reserved
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

import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.auth.Identity;
import com.sun.sgs.kernel.KernelRunnable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.CellManagerMO;
import org.jdesktop.wonderland.server.cell.TransformChangeListenerSrv;
import org.jdesktop.wonderland.server.spatial.ViewUpdateListener;

/**
 *
 * @author paulby
 */
public class SpatialCellImpl implements SpatialCell, ViewUpdateListener {

    private BoundingVolume worldBounds = new BoundingSphere();
    private BoundingVolume localBounds = null;
    private CellTransform localTransform = null;
    private CellTransform worldTransform = null;
    private SpatialCellImpl rootNode = null;
    private SpatialCellImpl parent = null;
    private CellID cellID;
    private BigInteger dsID;        // FOR DEBUG, the DS ID of the cell

    private ArrayList<SpatialCellImpl> children = null;

    private ReentrantReadWriteLock readWriteLock = null;

    private TransformChangeListener worldTransformChangeListener=null;

    private HashSet<Space> spaces = null;

    private ViewCacheSet viewCacheSet = null;

    private ArrayList<TransformChangeListenerSrv> transformChangeListeners = null;
    private final Object transformChangeListenersSync = new Object();

    private CopyOnWriteArraySet<ViewUpdateListener> viewUpdateListeners = null;
    private final Object viewUpdateListenersSync = new Object();

    private boolean isRoot = false;

    public SpatialCellImpl(CellID id, BigInteger dsID) {
//        System.out.println("Creating SpatialCell "+id);
        this.cellID = id;
        this.dsID = dsID;
    }

    /**
     * Users should call acquireRootReadLock while they are using the results of this call
     * @return
     */
    public BoundingVolume getLocalBounds() {
        return localBounds;
    }

    public void setLocalBounds(BoundingVolume localBounds) {
        acquireRootWriteLock();
        try {
            this.localBounds = localBounds;

            if (rootNode!=null) {
                throw new RuntimeException("Updating bounds of live cell not supported yet");
            }
        } finally {
            releaseRootWriteLock();
        }
    }

    /**
     * Users should call acquireRootReadLock while they are using the results of this call
     * @return
     */
    public BoundingVolume getWorldBounds() {
        return worldBounds;
    }

    /**
     * Users should call acquireRootReadLock while they are using the results of this call
     * @return
     */
    public CellTransform getLocalTransform() {
        return localTransform;
    }

    public void setLocalTransform(CellTransform transform, Identity identity) {
        acquireRootWriteLock();

        BoundingVolume newWorldBounds = null;

        try {
            this.localTransform = transform;

            if (rootNode!=null) {
                newWorldBounds = updateWorldTransform(identity);
            }
        } finally {
            releaseRootWriteLock();
        }

        // OWL issue #96: to avoid deadlock, call after releasing the root
        // lock.
        if (isRoot && newWorldBounds != null) {
            computeSpaces(newWorldBounds);
        }
    }

    public CellTransform getWorldTransform() {
        return worldTransform;
    }

    public void addChild(SpatialCell child, Identity identity) {
        if (((SpatialCellImpl)child).parent!=null) {
            throw new RuntimeException("Multiple parent exception, current parent "+((SpatialCellImpl)child).parent.cellID);
        }

        acquireRootWriteLock();
        try {
            if (children==null) {
                children = new ArrayList();
            }

            children.add((SpatialCellImpl) child);
            ((SpatialCellImpl)child).setParent(this);

            if (rootNode!=null) {
                worldBounds.mergeLocal(((SpatialCellImpl)child).updateWorldTransform(identity));
            }

            notifyCacheChildAddedOrRemoved(this, (SpatialCellImpl)child, true);

            // No need to revalidate the parent here -- adding a child can't
            // change security parameters
            // revalidate(); // Security revalidation, optimize this
        } finally {
            releaseRootWriteLock();
        }
    }

    void addTransformChangeListener(TransformChangeListenerSrv listener) {

        synchronized(transformChangeListenersSync) {
            if (transformChangeListeners==null)
                transformChangeListeners = new ArrayList();
            transformChangeListeners.add(listener);

            // OWL issue #61: notify the listener immediately on
            // addition, to make sure that no updates are missed.
            // Only do this if the world root has already been set
            if (worldTransform != null) {
                Collection<TransformChangeListenerSrv> listeners =
                        Collections.singleton(listener);
                UniverseImpl.getUniverse().scheduleTransaction(new TransformChangeNotificationTask(listeners, cellID, localTransform, worldTransform), null);
            }
        }
    }

    void removeTransformChangeListener(TransformChangeListenerSrv listener) {
        if (transformChangeListeners==null)
            return;

        synchronized(transformChangeListenersSync) {
            transformChangeListeners.remove(listener);
        }
    }

    private void notifyTransformChangeListeners(Identity identity) {
        if (transformChangeListeners==null)
            return;

        synchronized(transformChangeListenersSync) {
            UniverseImpl.getUniverse().scheduleTransaction(new TransformChangeNotificationTask(transformChangeListeners, cellID, localTransform, worldTransform), identity);
        }
    }

    /**
     * Listener for changes to any view to which this cell is close
     * @param viewUpdateListener
     */
    public void addViewUpdateListener(ViewUpdateListener viewUpdateListener) {

        synchronized(viewUpdateListenersSync) {
            if (viewUpdateListeners==null)
                viewUpdateListeners = new CopyOnWriteArraySet();
            viewUpdateListeners.add(viewUpdateListener);
        }
    }

    /**
     * @param listener
     */
    public void removeViewUpdateListener(ViewUpdateListener viewUpdateListener) {
        if (viewUpdateListeners==null)
            return;

        synchronized(viewUpdateListenersSync) {
            viewUpdateListeners.remove(viewUpdateListener);
        }
    }

    Iterator<ViewUpdateListener> getViewUpdateListeners() {
        if (viewUpdateListeners==null)
            return null;

        return viewUpdateListeners.iterator();
    }

    /**
     * Re-notify each listener of the position of this cell
     */
    public void revalidateListeners(Identity identity) {
        notifyTransformChangeListeners(identity);
    }

    /**
     * Update the world transform of this node and all it's children iterating
     * down the graph, then coming back up set the world bounds correctly
     * Return the world bounds
     * @return
     */
    private BoundingVolume updateWorldTransform(Identity identity) {
        CellTransform oldWorld;
        boolean transformChanged = false;
        if (worldTransform==null)
            oldWorld=null;
        else
            oldWorld = worldTransform.clone(null);

        if (parent!=null) {
            CellTransform parentWorld = parent.worldTransform.clone(null);
            worldTransform = parentWorld.mul(localTransform);
        } else {
            worldTransform = localTransform.clone(null);
        }

        if (!worldTransform.equals(oldWorld)) {         // TODO should be epsilonEquals
            if (worldTransformChangeListener!=null)     // For view cells
                worldTransformChangeListener.transformChanged(this);
            transformChanged = true;
        }

        computeWorldBounds();

        if (children!=null) {
            for(SpatialCellImpl s : children) {
                worldBounds.mergeLocal(s.updateWorldTransform(identity));
            }
        }

        if (transformChanged) {
            notifyViewCaches(worldTransform);
            notifyTransformChangeListeners(identity);
        }
        
        return worldBounds;
    }
    
    /**
     * Compute the world bounds for this node from the local bounds and world transform
     */
    private void computeWorldBounds() {
        worldBounds = localBounds.clone(worldBounds);
        worldTransform.transform(worldBounds);
    }

    /**
     * Update the set of spaces this cell is a part of. Only called on root
     * cells.
     */
     private void computeSpaces(BoundingVolume worldBounds) {
        if (!isRoot) {
            return;
        }

        // OWL issue #96: to avoid deadlock, make sure the root lock is
        // not held while calling this method.
        synchronized (spaces) {
            HashSet<Space> oldSpaces = (HashSet<Space>) spaces.clone();

            // Check which spaces the bounds intersect with
            Iterable<Space> it = UniverseImpl.getUniverse().getSpaceManager().getEnclosingSpace(worldBounds);
            for(Space s : it) {
                if (!spaces.contains(s)) {
                    s.addRootSpatialCell(this);
                    spaces.add(s);
                } else {
                    oldSpaces.remove(s);
                }
            }

            // Remove this cell from spaces it no longer intersects with
            for(Space s : oldSpaces) {
                s.removeRootSpatialCell(this);
                spaces.remove(s);
            }
        }
    }

    /** Return the children, or null
     * 
     * @return
     */
    Iterable<SpatialCellImpl> getChildren() {
        return children;
    }

    public void removeChild(SpatialCell child) {
        if (children==null)
            return;

        acquireRootWriteLock();
        try {
            children.remove(child);
            notifyCacheChildAddedOrRemoved(this, (SpatialCellImpl)child, false);
            ((SpatialCellImpl)child).setParent(null); // Must be called after notifyCacheChildAddedOrRemoved so rootNode is still valid
        } finally {
            releaseRootWriteLock();
        }
    }

    public void setAttribute(Object attr) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public CellID getCellID() {
        return cellID;
    }

    /**
     * Return the root node of the graph that contains this node
     * @return
     */
    SpatialCell getRoot() {
        return rootNode;
    }

    /**
     * Set the root for this node and all it's children
     * @param root
     */
    void setRoot(SpatialCell root, ViewCacheSet viewCacheSet, Identity identity) {
        this.rootNode = (SpatialCellImpl) root;

        if (isRoot && root == null) {
            // We are a root node and we are being removed
            synchronized (spaces) {
                for(Space s : spaces) {
                    s.removeRootSpatialCell(this);
                }
                spaces.clear();
                spaces = null;
            }
                    
            isRoot = false;
            readWriteLock = null;
            this.viewCacheSet = null;
            return;
        }

        if (root == this) {
            // Adding a new root cell to the universe
            if (!isRoot) {
                // Fisrt time, so setup internal root cell structures
                readWriteLock = new ReentrantReadWriteLock(true);
                this.viewCacheSet = new ViewCacheSet();
                isRoot = true;
                spaces = new HashSet();
            }
                
            BoundingVolume newWorldBounds;
            try {
                acquireRootWriteLock();

                // This node is the root of a graph, so set the world transform & bounds
                newWorldBounds = updateWorldTransform(identity);
            } finally {
                releaseRootWriteLock();
            }

            // OWL issue #96: make sure to release the root lock before
            // updating the spaces
            computeSpaces(newWorldBounds);
        } else {
            this.viewCacheSet = viewCacheSet;
            
            // OWL issue #167: add listeners to all views. This will be cleaned
            // up properly when the cell is removed and ViewCacheSet.destroy()
            // is called
            viewCachesAddedOrRemoved(viewCacheSet.getCaches(), true, false, this);
        }

        if (children != null) {
            for(SpatialCellImpl s : children) {
                s.setRoot(root, this.viewCacheSet, identity);
            }
        }
    }

    void setParent(SpatialCellImpl parent) {
        this.parent = parent;
        if (parent != null) {
            setRoot(parent.getRoot(), parent.viewCacheSet, null);
        }
    }

    SpatialCell getParent() {
        return parent;
    }

    private void acquireRootWriteLock() {
        if (rootNode!=null)
            rootNode.readWriteLock.writeLock().lock();
    }
    
    private void releaseRootWriteLock() {
        if (rootNode!=null)
            rootNode.readWriteLock.writeLock().unlock();
    }

    public void acquireRootReadLock() {
        if (rootNode!=null)
            rootNode.readWriteLock.readLock().lock();
    }
    
    public void releaseRootReadLock() {
        if (rootNode!=null)
            rootNode.readWriteLock.readLock().unlock();
    }

    /**
     * Only called for root cells
     * The cell has entered a space, record all the view caches that
     * are now interested in this cell.
     *
     * A view cache can span a number of spaces, so the viewCache structure
     * maintains the set of spaces per ViewCache (effectively a reference count)
     *
     * @param caches
     * @param space
     */
    void addViewCache(Collection<ViewCache> caches, Space space) {
        viewCacheSet.addViewCache(caches, space);
        
        // Notify all cells in the graph that caches have
        // been added. TODO this could be optimized to only notify
        // children that have expressed an interest
        try {
            acquireRootReadLock();
            viewCachesAddedOrRemoved(caches, true, true, this);
        } finally {
            releaseRootReadLock();
        }
    }

    void removeViewCache(Collection<ViewCache> caches, Space space) {
        if (viewCacheSet!=null)
            viewCacheSet.removeViewCache(caches, space);

        // Notify all cells in the graph that caches have
        // been removed. TODO this could be optimized to only notify
        // children that have expressed an interest
        try {
            acquireRootReadLock();
            viewCachesAddedOrRemoved(caches, false, true, this);
        } finally {
            releaseRootReadLock();
        }
    }

    static void viewCachesAddedOrRemoved(Collection<ViewCache> caches,
            boolean added, boolean notifyChildren, SpatialCellImpl cell)
    {
        // Issue #860: add a single listener to each cache to notify this
        // cell of any view updates. This cell will then forward that
        // updates to all the registered listeners.
        for (ViewCache c : caches) {
            if (added) {
                c.addViewUpdateListener(cell.getCellID(), cell);
            } else {
                c.removeViewUpdateListener(cell.getCellID(), cell);
            }
        }

        if (notifyChildren && cell.getChildren() != null) {
            for(SpatialCellImpl child : cell.getChildren()) {
                viewCachesAddedOrRemoved(caches, added, true, child);
            }
        }
    }


    /**
     * Notify the ViewCaches that the worldTransform of this root cell
     * has changed
     * @param worldTransform
     */
    private void notifyViewCaches(CellTransform worldTransform) {
        // Called from updateWorldBounds so we have a write lock on the graph
        SpatialCellImpl root = (SpatialCellImpl) getRoot();
        if (root==null)
            return;

        viewCacheSet.notifyViewCaches(this, worldTransform);
    }

    /**
     * Notify the view caches that this cell needs to be revalidated
     */
    public void revalidate() {
        // Called from updateWorldBounds so we have a write lock on the graph
        SpatialCellImpl root = (SpatialCellImpl) getRoot();
        if (root==null)
            return;

        viewCacheSet.revalidate(this);
    }

    private void notifyCacheChildAddedOrRemoved(SpatialCellImpl parent, SpatialCellImpl child, boolean added) {
        SpatialCellImpl root = (SpatialCellImpl) parent.getRoot();
        if (root==null) {
            Logger.getLogger(SpatialCellImpl.class.getName()).severe("notifyCacheChildAddedOrRemoved has NULL root "+parent);
            return;
        }

        viewCacheSet.notifyCacheChildAddedOrRemoved(parent, child, added);
    }

    public void destroy() {
        acquireRootWriteLock();
        
        boolean wasRoot = isRoot;

        try {
            SpatialCellImpl root = (SpatialCellImpl) getRoot();
            if (root == null) {
                return;
            }

            viewCacheSet.destroy(this);
            viewCacheSet = null;

            if (isRoot) {
                isRoot = false;
                this.viewCacheSet = null;
            }
        } finally {
            releaseRootWriteLock();
        }

        // OWL issue #96: be sure to all after we release the root lock
        // to avoid deadlock
        if (wasRoot) {
            synchronized (spaces) {
                // This is a root node
                for(Space space : spaces) {
                    space.removeRootSpatialCell(this);
                }
                spaces.clear();
                spaces = null;
            }
        }
    }

    public void viewLoggedIn(CellID cell, CellID viewCellID) {
        // issue #860: forward on any view updates to our listeners
        if (viewUpdateListeners != null) {
            for(ViewUpdateListener viewUpdateListener : viewUpdateListeners) {
                viewUpdateListener.viewLoggedIn(cell, viewCellID);
            }
        }
    }

    public void viewTransformChanged(CellID cell, CellID viewCellID, CellTransform viewWorldTransform) {
        // issue #860: forward on any view updates to our listeners
        if (viewUpdateListeners != null) {
            for(ViewUpdateListener viewUpdateListener : viewUpdateListeners) {
                viewUpdateListener.viewTransformChanged(cell, viewCellID, viewWorldTransform);
            }
        }
    }

    public void viewLoggedOut(CellID cell, CellID viewCellID) {
        // issue #860: forward on any view updates to our listeners
        if (viewUpdateListeners != null) {
            for(ViewUpdateListener viewUpdateListener : viewUpdateListeners) {
                viewUpdateListener.viewLoggedOut(cell, viewCellID);
            }
        }
    }

    public interface WorldBoundsChangeListener {
        public void worldBoundsChanged(SpatialCell cell);
    }

    public interface TransformChangeListener {
        public void transformChanged(SpatialCell cell);
    }

    class TransformChangeNotificationTask implements KernelRunnable {

        private TransformChangeListenerSrv[] listeners;
        private CellID cellID;
        private CellTransform localTransform;
        private CellTransform worldTransform;

        public TransformChangeNotificationTask(Collection<TransformChangeListenerSrv> transformListeners, CellID cellID, CellTransform localTransform, CellTransform worldTransform) {
            listeners = transformListeners.toArray(new TransformChangeListenerSrv[transformListeners.size()]);
            this.cellID = cellID;
            this.localTransform = localTransform.clone(null);
            this.worldTransform = worldTransform.clone(null);
        }

        public String getBaseTaskType() {
            return KernelRunnable.class.getName();
        }

        public void run() throws Exception {
            ManagedReference<CellMO> cellRef = AppContext.getDataManager().createReference(CellManagerMO.getCell(cellID));
            for(int i=0; i<listeners.length; i++) {
                TransformChangeListenerSrv listener = listeners[i];
                if (listener instanceof ManagedReference)
                    ((ManagedReference<TransformChangeListenerSrv>)listener).get().transformChanged(cellRef, localTransform, worldTransform);
                else
                    listener.transformChanged(cellRef, localTransform, worldTransform);
            }
        }

    }

    /**
     * The set of ViewCaches in which the cell is visible. Currently all the SpatialCell below a
     * root node have the same ViewCacheSet object
     */
    class ViewCacheSet {
        private final Map<ViewCache, HashSet<Space>> viewCache = new HashMap();

        public ViewCacheSet() {
        }

        public Collection<ViewCache> getCaches() {
            // return a copy
            synchronized (viewCache) {
                return new ArrayList<ViewCache>(viewCache.keySet());
            }
        }

        /**
         * Only called for root cells
         * The cell has entered a space, record all the view caches that
         * are now interested in this cell.
         *
         * A view cache can span a number of spaces, so the viewCache structure
         * maintains the set of spaces per ViewCache (effectively a reference count)
         *
         * @param caches
         * @param space
         */
        void addViewCache(Collection<ViewCache> caches, Space space) {
            synchronized(viewCache) {
                for(ViewCache c : caches) {
                    HashSet<Space> s = viewCache.get(c);
                    if (s==null) {
                        s = new HashSet();
                        s.add(space);
                        viewCache.put(c, s);
                    } else {
                        s.add(space);
                    }

                }
            }
        }

        void removeViewCache(Collection<ViewCache> caches, Space space) {
            synchronized(viewCache) {
                for(ViewCache c : caches) {
                    HashSet<Space> s = viewCache.get(c);
                    if (s==null) {
                        throw new RuntimeException("ERROR, cache not in set");
                    } else {
                        s.remove(space);
                    }

                    if (s.size()==0) {
                        viewCache.remove(c);
                    }
                }
            }

        }

        /**
         * Notify the ViewCaches that the worldTransform of this root cell
         * has changed
         * @param worldTransform
         */
        void notifyViewCaches(SpatialCellImpl cell, CellTransform worldTransform) {
            // make a copy to avoid concurrent modification
            ViewCache[] caches;
            synchronized (viewCache) {
                caches = viewCache.keySet().toArray(new ViewCache[viewCache.size()]);
            }

            for(ViewCache cache : caches) {
                cache.cellMoved(cell, worldTransform);
            }
        }

        /**
         * Notify the view caches that this cell needs to be revalidated
         */
        void revalidate(SpatialCellImpl cell) {
            // make a copy to avoid concurrent modification
            ViewCache[] caches;
            synchronized (viewCache) {
                caches = viewCache.keySet().toArray(new ViewCache[viewCache.size()]);
            }

            for(ViewCache cache : caches) {
                cache.cellRevalidated(cell);
            }
        }

        void notifyCacheChildAddedOrRemoved(SpatialCellImpl parent, SpatialCellImpl child, boolean added) {
            // make a copy to avoid concurrent modification
            ViewCache[] caches;
            synchronized (viewCache) {
                caches = viewCache.keySet().toArray(new ViewCache[viewCache.size()]);
            }

            for(ViewCache cache : caches) {
                if (added)
                    cache.childCellAdded(child);
                else
                    cache.childCellRemoved(parent, child);
            }

        }

        void destroy(SpatialCellImpl cell) {
            // make a copy to avoid concurrent modification
            ViewCache[] caches;
            synchronized (viewCache) {
                caches = viewCache.keySet().toArray(new ViewCache[viewCache.size()]);
            }

            for(ViewCache cache : caches) {
                cache.cellDestroyed(cell);
            }
        }
    }
}
