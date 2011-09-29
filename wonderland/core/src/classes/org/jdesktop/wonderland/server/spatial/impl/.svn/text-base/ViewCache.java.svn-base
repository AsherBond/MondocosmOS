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

import com.jme.bounding.BoundingSphere;
import com.jme.math.Vector3f;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.auth.Identity;
import com.sun.sgs.kernel.KernelRunnable;
import com.sun.sgs.service.DataService;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.ThreadManager;
import org.jdesktop.wonderland.common.cell.AvatarBoundsHelper;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.server.cell.CellDescription;
import org.jdesktop.wonderland.server.cell.ViewCellCacheMO;
import org.jdesktop.wonderland.server.spatial.ViewUpdateListener;

/**
 * The server side view cache for a specific view. 
 *
 * @author paulby
 */
class ViewCache {

    private static final Logger logger = Logger.getLogger(ViewCache.class.getName());
    private SpaceManager spaceManager;
    private SpatialCellImpl viewCell;
    private final HashMap<SpatialCell, Integer> rootCells = new HashMap(); // The rootCells visible in this cache

    private final HashSet<Space> spaces = new HashSet();              // Spaces that intersect this caches world bounds

    private Vector3f lastSpaceValidationPoint = null;   // The view cells location on the last space revalidation

    private static final float REVAL_DISTANCE_SQUARED = (float) Math.pow(SpaceManagerGridImpl.SPACE_SIZE/4, 2);
//    private static final float REVAL_DISTANCE_SQUARED = 1f;

    private final LinkedList<CacheUpdate> pendingCacheUpdates = new LinkedList();

//    private ScheduledExecutorService spaceLeftProcessor = Executors.newSingleThreadScheduledExecutor();

    private Identity identity;
    private CacheProcessor cacheProcessor;
    private BigInteger cellCacheId;
    private DataService dataService;

    private BoundingSphere proximityBounds = new BoundingSphere(AvatarBoundsHelper.PROXIMITY_SIZE, new Vector3f());

    private final LinkedHashMap<CellID, ViewUpdateListenerContainer> viewUpdateListeners = new LinkedHashMap();

    private Vector3f v3f = new Vector3f();      // Temporary vector

    private enum CacheUpdateOp { VIEW_MOVED, CELL_MOVED, SPACES_CHANGED,
                                 CELL_ADDED, CELL_REMOVED, CELL_REVALIDATED,
                                 CACHE_REVALIDATED };

    public ViewCache(SpatialCellImpl cell, SpaceManager spaceManager, Identity identity, BigInteger cellCacheId) {
        this.viewCell = cell;
        this.spaceManager = spaceManager;
        this.identity = identity;
        this.cellCacheId = cellCacheId;
        cacheProcessor = new CacheProcessor();
        cacheProcessor.start();
        dataService = UniverseImpl.getUniverse().getDataService();
    }

    public SpatialCellImpl getViewCell() {
        return viewCell;
    }

    private void viewCellMoved(final CellTransform worldTransform) {
        worldTransform.getTranslation(v3f);

        if (lastSpaceValidationPoint==null ||
            lastSpaceValidationPoint.distanceSquared(v3f)>REVAL_DISTANCE_SQUARED) {

            revalidateSpaces();
            
            if (lastSpaceValidationPoint==null)
                lastSpaceValidationPoint = new Vector3f(v3f);
            else
                lastSpaceValidationPoint.set(v3f);
        }

        UniverseImpl.getUniverse().scheduleTransaction(
                new KernelRunnable() {

            public String getBaseTaskType() {
                return ViewCache.class.getName() + ".MoveNotifier";
            }

            public void run() throws Exception {
                synchronized(viewUpdateListeners) {
                    for(ViewUpdateListenerContainer cont : viewUpdateListeners.values())
                        cont.notifyListeners(worldTransform);
                }
            }
        }, identity);

    }

    void login() {
        // Send an initial load of the environment cell
        sendEnvironmentCell();

        // Trigger a revalidation
        cellMoved(viewCell, viewCell.getWorldTransform());

        // notify listener of login
        UniverseImpl.getUniverse().scheduleTransaction(
                new KernelRunnable() {

            public String getBaseTaskType() {
                return ViewCache.class.getName() + ".LoginNotifier";
            }

            public void run() throws Exception {
                synchronized(viewUpdateListeners) {
                    for(ViewUpdateListenerContainer cont : viewUpdateListeners.values())
                        cont.notifyListenersLogin();
                }
            }
        }, identity);
    }

    void logout() {

        cacheProcessor.quit();

        synchronized(spaces) {
            for(Space sp : spaces)
                sp.removeViewCache(this);
            spaces.clear();
        }
        rootCells.clear();
        synchronized(pendingCacheUpdates) {
            pendingCacheUpdates.clear();
        }

        // notify listener of logout
        UniverseImpl.getUniverse().scheduleTransaction(
                new KernelRunnable() {

            public String getBaseTaskType() {
                return ViewCache.class.getName() + ".LogoutNotifier";
            }

            public void run() throws Exception {
                synchronized(viewUpdateListeners) {
                    for(ViewUpdateListenerContainer cont : viewUpdateListeners.values())
                        cont.notifyListenersLogout();
                }
            }
        }, identity);
        
//        System.err.println("-----------------> LOGOUT");
    }

    void addViewUpdateListener(CellID cellID, ViewUpdateListener listener) {
        synchronized(viewUpdateListeners) {
            viewUpdateListeners.put(cellID, new ViewUpdateListenerContainer(cellID, listener));
        }
    }

    void removeViewUpdateListener(CellID cellID, ViewUpdateListener listener) {
        ViewUpdateListenerContainer container;

        synchronized(viewUpdateListeners) {
            container = viewUpdateListeners.remove(cellID);
        }

        // OWL issue #86: send a last notification of the position (now out of
        // range) so that proximity listeners will update properly
        if (container != null) {
            sendLastTransformEvent(container);
        }
    }

    /**
     * Notification that a cell has moved, call by SpatialCell
     * @param cell
     * @param worldTransform
     */
    void cellMoved(SpatialCellImpl cell, CellTransform worldTransform) {
        if (cell==viewCell) {
            // Process view movement immediately
            viewCellMoved(worldTransform);
        } else {
            synchronized(pendingCacheUpdates) {
                pendingCacheUpdates.add(new CacheUpdate(cell, worldTransform));
            }
        }
    }

    /**
     * Notification that a cell's properties have changed, and that clients
     * may want to reevaluate it
     * @param cell
     */
    void cellRevalidated(SpatialCellImpl cell) {
        logger.fine(getViewCell().getCellID() + " Schedule revalidate cell " +
                   cell.getCellID());

        synchronized(pendingCacheUpdates) {
            pendingCacheUpdates.add(new CacheUpdate(cell));
        }
    }

    void cellDestroyed(SpatialCell cell) {
        CellID cellID = ((SpatialCellImpl)cell).getCellID();
        removeViewUpdateListener(cellID, null);
    }

    /**
     * Revalidate the entire cache, because the user who owns the cache
     * has changed.
     */
    void revalidate() {
        logger.fine(getViewCell().getCellID() + " Schedule revalidate");

        synchronized(pendingCacheUpdates) {
            pendingCacheUpdates.add(new CacheUpdate(spaces));
        }
    }

    void sendEnvironmentCell() {
        Collection<CellDescription> envCell =
                Collections.singleton((CellDescription)
                                      new CellDesc(CellID.getEnvironmentCellID()));

        UniverseImpl.getUniverse().scheduleQueuedTransaction(
                        new ViewCacheUpdateTask(envCell, ViewCacheUpdateType.LOAD),
                        identity, ViewCache.this);
    }

    void sendLastTransformEvent(final ViewUpdateListenerContainer cont)
    {
        UniverseImpl.getUniverse().scheduleTransaction(new KernelRunnable() {
            public String getBaseTaskType() {
                return ViewCache.class.getName() + ".MoveNotifier";
            }

            public void run() throws Exception {
                cont.notifyListeners(getViewCell().getWorldTransform());
            }
        }, identity);
    }

    void childCellAdded(SpatialCellImpl child) {
        logger.fine(getViewCell().getCellID() + " child cell added " +
                       child.getCellID());

        viewCell.acquireRootReadLock();

        try {
            synchronized(pendingCacheUpdates) {
                logger.fine("ViewCache childCellAdded");
                pendingCacheUpdates.add(new CacheUpdate(child, (SpatialCellImpl) child.getParent(), true));
            }
        } finally {
            viewCell.releaseRootReadLock();
        }

    }

    void childCellRemoved(SpatialCellImpl parent, SpatialCellImpl child) {
        logger.fine(getViewCell().getCellID() + " child cell removed " +
                       child.getCellID());

        viewCell.acquireRootReadLock();

        try {
            synchronized(pendingCacheUpdates) {
                logger.fine("ViewCache childCellRemoved "+child.getCellID());
                pendingCacheUpdates.add(new CacheUpdate(child, parent, false));
            }
        } finally {
            viewCell.releaseRootReadLock();
        }

    }

    /**
     * Called to add a root cell when the root cell is added to a space with which
     * this cache is already registered
     * @param rootCell
     */
    void rootCellAdded(SpatialCellImpl rootCell) {
        logger.fine(getViewCell().getCellID() + " root cell added " +
                       rootCell.getCellID());

        viewCell.acquireRootReadLock();

        try {
            synchronized(pendingCacheUpdates) {
                logger.fine("ViewCache rootCellAdded");
                pendingCacheUpdates.add(new CacheUpdate(rootCell, null, true));
            }
        } finally {
            viewCell.releaseRootReadLock();
        }
    }

    void rootCellRemoved(SpatialCellImpl rootCell) {
        logger.fine(getViewCell().getCellID() + " root cell removed " +
                    rootCell.getCellID());

        // Don't remove the caches view cell
        if (rootCell==viewCell) {
            // If we are called with our ViewCell then we have warped and
            // spaces need to be revalidated. Fix for issue 717
            revalidateSpaces();
            return;
        }

        viewCell.acquireRootReadLock();

        try {
            synchronized(pendingCacheUpdates) {
                logger.fine("ViewCache rootCellRemoved");
                pendingCacheUpdates.add(new CacheUpdate(rootCell, null, false));
            }
        } finally {
            viewCell.releaseRootReadLock();
        }
    }
    /**
     * Update the set of spaces which intersect with this caches world bounds
     */
    private void revalidateSpaces() {
        viewCell.acquireRootReadLock();

        try {
            Set<Space> oldSpaces = (Set<Space>) spaces.clone();
            Set<Space> newSpaces = new HashSet<Space>();

            proximityBounds.setCenter(viewCell.getWorldTransform().getTranslation(null));

            Iterable<Space> allSpaces = spaceManager.getEnclosingSpace(proximityBounds);


//            System.err.println("ViewCell Bounds "+proximityBounds);
//            StringBuffer buf = new StringBuffer("View in spaces ");

            StringBuffer logBuf = null;
            if (logger.isLoggable(Level.FINE))
                logBuf = new StringBuffer();

            for(Space sp : allSpaces) {
//                buf.append(sp.getName()+", ");
                if (spaces.add(sp)) {
                    // the space is new
                    newSpaces.add(sp);

                    if (logBuf!=null)
                        logBuf.append(sp.getName()+":"+sp.getRootCells().size()+" ");
                    
                    // Entered a new space
                    // OWL issue #96: we need to do this in a single atomic
                    // action on the cache update thread to ensure consistency
                    // synchronized(pendingCacheUpdates) {
                    //    pendingCacheUpdates.add(new CacheUpdate(sp, true));
                    //}
                    //sp.addViewCache(this);
                }
                oldSpaces.remove(sp);
            }

            if (logBuf!=null && logBuf.length()>0) {
                logBuf.insert(0, getViewCell().getCellID() + " view Entering spaces ");
                logger.fine(logBuf.toString());
                logBuf.setLength(0);
            }
//
//            System.err.println(buf.toString());
//
//            System.out.println("Old spaces cut "+oldSpaces.size());
//            buf = new StringBuffer("View leavoing spaces ");
            for(Space sp : oldSpaces) {
//                buf.append(sp.getName()+", ");
                //sp.removeViewCache(this);
                spaces.remove(sp);

                if (logBuf!=null) {
                    logBuf.append(sp.getName()+" ");
                }

                // We don't remove the space cells immediately in case the user
                // is moving along the border of the space

                // TODO this is flawed, we need to track pending removes and make changes
                // if the user moves so that the cell is visible again.

    //            spaceLeftProcessor.schedule(new CacheUpdate(sp, false), 30, TimeUnit.SECONDS);

                // In the meantime update the cache immediately
                // OWL issue #96: we need to do this in a single atomic
                // action on the cache update thread to ensure consistency
                //synchronized(pendingCacheUpdates) {
                //    pendingCacheUpdates.add(new CacheUpdate(sp,false));
                //}
            }

            if (logBuf!=null && logBuf.length()>0) {
                logBuf.insert(0, getViewCell().getCellID() + " view Leaving spaces ");
                logger.fine(logBuf.toString());
            }

            if (!newSpaces.isEmpty() || !oldSpaces.isEmpty()) {
                logger.fine(getViewCell().getCellID() + " schedule space update: "
                        + newSpaces.size() + " new spaces, " + oldSpaces.size() +
                        " old spaces.");

                // OWL issue #96: schedule a single transaction to add and remove
                // spaces, with spaces locked. This guarantees that if two cells
                // are moving around the same time, they will have a consistent
                // view of the set of cells
                pendingCacheUpdates.add(new CacheUpdate(newSpaces, oldSpaces));
            }

        } finally {
            viewCell.releaseRootReadLock();
        }
    }

    class CacheProcessor extends Thread {
        boolean quit = false;

        public CacheProcessor() {
            super(ThreadManager.getThreadGroup(),"CacheProcessor");
        }

        @Override
        public void run() {
            // TODO add update notifcation instead of just of the short sleep
            Collection<CacheUpdate> updateList;

            while(!quit) {
                synchronized(pendingCacheUpdates) {
                    updateList = (Collection<CacheUpdate>) pendingCacheUpdates.clone();
                    pendingCacheUpdates.clear();
                }

                for(CacheUpdate update : updateList) {
                    update.run();
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                }
            }
        }

        public void quit() {
            quit = true;
            cacheProcessor.interrupt();
        }
    }


    class CacheUpdate implements Runnable {

        private SpatialCellImpl cell;
        private SpatialCellImpl parentCell;
        private CellTransform worldTransform;
        private Iterable<Space> spaces;
        private Iterable<Space> oldSpaces;
        
        private final CacheUpdateOp op;

        /**
         * A cacheUpdate caused by a cell updating it's worldTransform
         * @param cell
         * @param worldTransform
         */
        public CacheUpdate(SpatialCellImpl cell, CellTransform worldTransform) {
            this.cell = cell;
            this.worldTransform = worldTransform;
            if (cell==viewCell)
                op = CacheUpdateOp.VIEW_MOVED;
            else
                op = CacheUpdateOp.CELL_MOVED;
        }

        /**
         * Enter or exit a space
         * @param space
         * @param enter
         */
        public CacheUpdate(Iterable<Space> newSpaces, Iterable<Space> oldSpaces) {
            op = CacheUpdateOp.SPACES_CHANGED;

            this.spaces = newSpaces;
            this.oldSpaces = oldSpaces;
        }

        /**
         * RootCell added/removed
         * @param rootCell
         * @param add
         */
        public CacheUpdate(SpatialCellImpl rootCell, SpatialCellImpl parent, boolean add) {
            op = (add) ? CacheUpdateOp.CELL_ADDED : CacheUpdateOp.CELL_REMOVED;
            this.cell = rootCell;
            this.parentCell = parent;
        }

        /**
         * Cell revalidated
         */
        public CacheUpdate(SpatialCellImpl cell) {
            op = CacheUpdateOp.CELL_REVALIDATED;
            this.cell = cell;
        }

        /**
         * Cache revalidated
         */
        public CacheUpdate(Iterable<Space> spaces) {
            op = CacheUpdateOp.CACHE_REVALIDATED;
            this.spaces = spaces;
        }

        public void run() {
            List<CellDescription> loadCells = new ArrayList<CellDescription>();
            List<CellDescription> unloadCells = new ArrayList<CellDescription>();
            List<CellDescription> revalidateCells = new ArrayList<CellDescription>();

            switch(op) {
                case VIEW_MOVED:
                    viewCellMoved(worldTransform);
                    break;
                case CELL_MOVED:
                    // Check for cache enter/exit
                    break;
                case SPACES_CHANGED:
                    processSpacesChanged(spaces, oldSpaces, loadCells, unloadCells);
                    break;
                case CELL_ADDED:
                    logger.fine(getViewCell().getCellID() +
                                   " cell " + cell.getCellID() + " added");

                    if (parentCell==null) {
                        synchronized(rootCells) {
                            addRootCellImpl(cell, loadCells);
                        }
                    } else {
                        addOrRemoveSubgraphCellImpl(cell, loadCells, true);
                    }
                    break;
                case CELL_REMOVED:
                    logger.fine(getViewCell().getCellID() +
                                   " cell " + cell.getCellID() + " removed");

//                    System.err.println("REMOVED from parent "+parentCell);
                    if (parentCell==null) {
                        synchronized(rootCells) {
                            removeRootCellImpl(cell, unloadCells);
                        }
                    } else {
                        addOrRemoveSubgraphCellImpl(cell, unloadCells, false);
                    }
                    break;
                case CELL_REVALIDATED:
                    // OWL issue #97: don't revalidate child cells unless the
                    // root is in the cache. See addOrRemoveSubgraphImpl()
                    // below for details.
                    synchronized (rootCells) {
                        if (rootCells.containsKey(cell)) {
                            revalidateCells.add(new CellDesc(cell.getCellID()));
                        }
                    }
                    break;
                case CACHE_REVALIDATED:
                    // find *all* the cells in this cache.  Yikes!
                    synchronized(rootCells) {
                        // revalidate all root cells we know about.
                        for (SpatialCell sc : rootCells.keySet()) {
                            SpatialCellImpl root = (SpatialCellImpl) sc;
                            root.acquireRootReadLock();
                            try {
                                revalidateCells.add(new CellDesc(root.getCellID()));
                                processChildCells(revalidateCells, root, CellStatus.ACTIVE);
                            } finally {
                                root.releaseRootReadLock();
                            }
                        }
                    }
                    break;
            }

            // schedule unload operations
            if (!unloadCells.isEmpty()) {
                if (logger.isLoggable(Level.FINE)) {
                    StringBuffer logBuf = new StringBuffer(getViewCell().getCellID() +
                                                           " unload: ");
                    for (CellDescription desc : unloadCells) {
                        logBuf.append(desc.getCellID() + " ");
                    }
                    logger.fine(logBuf.toString());
                }

                UniverseImpl.getUniverse().scheduleQueuedTransaction(
                        new ViewCacheUpdateTask(unloadCells, ViewCacheUpdateType.UNLOAD),
                        identity, ViewCache.this);
            }

            if (!loadCells.isEmpty()) {
                if (logger.isLoggable(Level.FINE)) {
                    StringBuffer logBuf = new StringBuffer(getViewCell().getCellID() +
                                                           " load: ");
                    for (CellDescription desc : loadCells) {
                        logBuf.append(desc.getCellID() + " ");
                    }
                    logger.fine(logBuf.toString());
                }

                UniverseImpl.getUniverse().scheduleQueuedTransaction(
                        new ViewCacheUpdateTask(loadCells, ViewCacheUpdateType.LOAD),
                        identity, ViewCache.this);
            }

            if (!revalidateCells.isEmpty()) {
                if (logger.isLoggable(Level.FINE)) {
                    StringBuffer logBuf = new StringBuffer(getViewCell().getCellID() +
                                                           " revalidate: ");
                    for (CellDescription desc : revalidateCells) {
                        logBuf.append(desc.getCellID() + " ");
                    }
                    logger.fine(logBuf.toString());
                }

                UniverseImpl.getUniverse().scheduleQueuedTransaction(
                        new ViewCacheUpdateTask(revalidateCells, ViewCacheUpdateType.REVALIDATE),
                        identity, ViewCache.this);
            }
        }

        /**
         * Atomically handle a change in spaces from oldSpaces to newSpaces.
         * For each space, we collect the current set of root cells, and add
         * ourselves as a listener so we will be updated about any future
         * changes.
         * <p>
         * This method collects the total set of cells to notify the clients
         * about.
         *
         * @param newSpaces the list of spaces entered
         * @param oldSpaces the list of spaces exited
         * @param loadCells the list of new cells to load, which this method
         * will add to
         * @param unloadCells the list of old cells to unload, which this method
         * will remove from
         */
        private void processSpacesChanged(Iterable<Space> newCells,
                                          Iterable<Space> oldCells,
                                          List<CellDescription> loadCells,
                                          List<CellDescription> unloadCells)
        {
            // OWL issue #96: make sure to make these changes atomically
            // so we don't miss changes that happen while we are changing
            // spaces
            
            // first, we need to lock all spaces so there are no changes while
            // we are collecting roots.  To prevent deadlock, it is critical 
            // that we sort the list of spaces to lock to guarantee everybody 
            // tries to take locks in the same order.  We sort spaces by
            // name in this case.
            SortedMap<Space, Boolean> sortedSpaces = 
                    new TreeMap<Space, Boolean>(SPACE_COMPARATOR);

            // now add all spaces to the list, tracking whether they are added
            // or removed.
            for (Space space : newCells) {
                sortedSpaces.put(space, true);
            }
            for (Space space : oldCells) {
                sortedSpaces.put(space, false);
            }

            // next, we iterate through the sorted spaces, grabbing the
            // lock for each space and -- once we have the lock -- adding
            // ourselves as a listener
            List<Space> lockedSpaces = new LinkedList<Space>();
            try {
                for (Map.Entry<Space, Boolean> e : sortedSpaces.entrySet()) {
                    Space space = e.getKey();
                    space.acquireRootCellReadLock();
                    lockedSpaces.add(space);

                    // now that the space is locked, we can add or remove 
                    // ourself as a listener
                    if (e.getValue()) {
                        space.addViewCache(ViewCache.this);
                    } else {
                        space.removeViewCache(ViewCache.this);
                    }
                }

                // ok, we now hold the locks on all spaces we are going to
                // read, so it is safe to process the cells.
                synchronized (rootCells) {
                    for (Map.Entry<Space, Boolean> e : sortedSpaces.entrySet()) {
                        Space space = e.getKey();
                        boolean add = e.getValue();

                        if (space.getRootCells().size() > 0) {
                            logger.fine(getViewCell().getCellID() +
                                        (add?" Adding":" Removing") +
                                        " space " + space.getName() +
                                        " with " + space.getRootCells().size() +
                                        " cells");
                        }

                        // add or remove each root to our list of roots
                        for (SpatialCellImpl root : space.getRootCells()) {
                            logger.fine(getViewCell().getCellID() +
                                        (add?" Add":" Remove") +
                                        " root " + root.getCellID() +
                                        " from space " + space.getName());

                            if (add) {
                                addRootCellImpl(root, loadCells);
                            } else {
                                removeRootCellImpl(root, unloadCells);
                            }
                        }
                    }
                }
            } finally {
                // be sure to release all locks
                for (Space space : lockedSpaces) {
                    space.releaseRootCellReadLock();
                }
            }
        }

        /**
         * A non root cell has been added or removed, traverse the new subgraph
         * and add/remove all the cells
         * 
         * @param child the root of the subgraph
         * @param newCells the set of cells in the subgraph (including child)
         * @param add true if we are adding, or false if we are removing
         */
        private void addOrRemoveSubgraphCellImpl(SpatialCellImpl child,
                                                 List<CellDescription> newCells,
                                                 boolean add)
        {
            child.acquireRootReadLock();
            try {
                // OWL issue #97: special case -- if we get an add notification
                // interleaved with a change spaces notification, we may be
                // notified of the addition of a child for which we no longer
                // have the parent. In that case, simply ignore the addition.
                // If the child is removed, we do need to process the removal
                // here, because we would not have been notified when changing
                // spaces (since the child was no longer in the space at the
                // time we revalidated).
                if (add) {
                    synchronized (rootCells) {
                        if (!rootCells.containsKey(child.getRoot())) {
                            logger.warning(getViewCell().getCellID() +
                                          " Attempting to add child " + 
                                           child.getCellID().toString() +
                                           " to unloaded root " +
                                           ((SpatialCellImpl) child.getRoot()).getCellID());
                            return;
                        }
                    }
                }

                newCells.add(new CellDesc(child.getCellID()));
                processChildCells(newCells, child, null);       // The status field is not used, so set it to null
            } finally {
                child.releaseRootReadLock();
            }
        }

        /**
         * Callers must be synchronized on rootCells
         * @param root the root of the graph
         * @param newCells the cumulative set of new cells that have been added
         */
        private void addRootCellImpl(SpatialCellImpl root, List<CellDescription> newCells) {
            Integer refCountI = rootCells.get(root);
            
            logger.fine(getViewCell().getCellID() + " add root " +
                        root.getCellID() + " refcount = " + refCountI);

            int refCount;
            if (refCountI==null) {
                root.acquireRootReadLock();
                try {
                    newCells.add(new CellDesc(root.getCellID()));
                    processChildCells(newCells, root, CellStatus.ACTIVE);
                } finally {
                    root.releaseRootReadLock();
                }
                refCount=1;
            } else {
                refCount = refCountI.intValue();
                refCount++;
            }

//            System.err.println("***** Adding root "+root.getCellID()+" ref count "+refCount);

            rootCells.put(root, refCount);
        }

        /**
         * Callers must be synchronized on rootCells
         * @param root
         * @param oldCells the cummalative set of cells that are being removed
         */
        private void removeRootCellImpl(SpatialCellImpl root, List<CellDescription> oldCells) {
            Integer refCountI = rootCells.get(root);

            logger.fine(getViewCell().getCellID() + " remove root " +
                        root.getCellID() + " refcount = " + refCountI);

            if (refCountI==null)
                return;

            int refCount = refCountI.intValue();

            if (refCount==1) {
                root.acquireRootReadLock();
                try {
                    oldCells.add(new CellDesc(root.getCellID()));
                    processChildCells(oldCells, root, CellStatus.DISK);
                } finally {
                    root.releaseRootReadLock();
                }
                refCount = 0;
                rootCells.remove(root);
            } else {
                refCount--;
                rootCells.put(root, refCount);
            }

//            System.err.println("****** Removing root "+root.getCellID()+" ref count "+refCount);

        }

        private void processChildCells(List<CellDescription> cells, SpatialCellImpl parent, CellStatus status) {
//            System.err.println("Processing Child Cells "+parent.getCellID()+"  "+parent.getChildren()+"  "+status);
            if (parent.getChildren()==null)
                return;
            
            for(SpatialCellImpl child : parent.getChildren()) {
                cells.add(new CellDesc(child.getCellID()));
                processChildCells(cells, child, status);
            }
        }

    }

    private static final Comparator<Space> SPACE_COMPARATOR =
            new Comparator<Space>()
    {
        public int compare(Space o1, Space o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    private enum ViewCacheUpdateType { LOAD, REVALIDATE, UNLOAD };
    class ViewCacheUpdateTask implements KernelRunnable {

        private final Collection<CellDescription> cells;
        private final ViewCacheUpdateType type;

        public ViewCacheUpdateTask(Collection<CellDescription> newCells,
                                   ViewCacheUpdateType type)
        {
            this.cells = newCells;
            this.type = type;
        }

        public String getBaseTaskType() {
            return KernelRunnable.class.getName();
        }

        public void run() throws Exception {
            ViewCellCacheMO cacheMO = (ViewCellCacheMO) dataService.createReferenceForId(cellCacheId).get();
            switch (type) {
                case LOAD:
                    // Check security and generate appropriate load messages
                    cacheMO.generateLoadMessagesService(cells);
                    break;
                case REVALIDATE:
                    cacheMO.revalidateCellsService(cells);
                    break;
                case UNLOAD:
                    // No need to check security, just generate unload messages
                    cacheMO.generateUnloadMessagesService(cells);
                    break;
            }

//            StringBuffer buf = new StringBuffer();
//            for(CellDescription c : cells)
//                buf.append(c.getCellID()+", ");
//            logger.warning("--------> DS UpdateTask "+viewCell.getCellID()+"  loading "+loadCells+"  "+cells.size()+"  "+buf.toString());
            
        }

    }

    static class CellDesc implements CellDescription, Serializable {

        private CellID cellID;

        public CellDesc(CellID cellID) {
            this.cellID = cellID;
        }

        public CellID getCellID() {
            return cellID;
        }
    }

    class ViewUpdateListenerContainer {
        private CellID cellID;
        private ViewUpdateListener viewUpdateListener;

        public ViewUpdateListenerContainer(CellID cellID, ViewUpdateListener listener) {
            this.cellID = cellID;
            this.viewUpdateListener = listener;

            if (listener instanceof ManagedObject) {
                throw new RuntimeException("ManagedObject listeners support not implemented yet");
            }
        }

        public void notifyListenersLogin() {
            viewUpdateListener.viewLoggedIn(cellID, viewCell.getCellID());
        }

        public void notifyListeners(final CellTransform viewWorldTransform) {
            viewUpdateListener.viewTransformChanged(cellID, viewCell.getCellID(), viewWorldTransform);
        }

        public void notifyListenersLogout() {
            viewUpdateListener.viewLoggedOut(cellID, viewCell.getCellID());
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof ViewUpdateListenerContainer) {
                ViewUpdateListenerContainer c = (ViewUpdateListenerContainer) o;
                if (c.cellID.equals(cellID) && c.viewUpdateListener==viewUpdateListener)
                    return true;
            }

            return false;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 29 * hash + (this.cellID != null ? this.cellID.hashCode() : 0);
            hash = 29 * hash + (this.viewUpdateListener != null ? this.viewUpdateListener.hashCode() : 0);
            return hash;
        }
    }
}
