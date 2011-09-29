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

import com.sun.sgs.auth.Identity;
import com.sun.sgs.kernel.ComponentRegistry;
import com.sun.sgs.kernel.KernelRunnable;
import com.sun.sgs.kernel.TaskQueue;
import com.sun.sgs.kernel.TransactionScheduler;
import com.sun.sgs.service.DataService;
import com.sun.sgs.service.TransactionProxy;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.server.cell.TransformChangeListenerSrv;
import org.jdesktop.wonderland.server.spatial.ViewUpdateListener;
import org.jdesktop.wonderland.server.cell.view.ViewCellMO;

/**
 *
 * @author paulby
 */
public class UniverseImpl implements Universe {

    private SpaceManager spaceManager = new SpaceManagerGridImpl();
    private final HashMap<CellID, SpatialCell> cells = new HashMap();
    private static UniverseImpl universe;
    private TransactionProxy transactionProxy;
    private DataService dataService;
    private TransactionScheduler transactionScheduler;
    private Identity defaultTaskOwner;

    private static final Logger logger = Logger.getLogger(UniverseImpl.class.getName());
    private final HashMap<Object, TaskQueue> taskQueues = new HashMap();

    // keep track of all update listeners so we can send logout messages
    // properly even if the cell cache doesn't exist
    private final Map<ViewUpdateListener, Set<CellID>> updateListeners =
            new HashMap<ViewUpdateListener, Set<CellID>>();

    public UniverseImpl(ComponentRegistry componentRegistry,
                        TransactionProxy transactionProxy,
                        Identity defaultTaskOwner)
    {
        this.transactionProxy = transactionProxy;
        this.dataService = transactionProxy.getService(DataService.class);
        this.transactionScheduler = componentRegistry.getComponent(TransactionScheduler.class);
        this.defaultTaskOwner = defaultTaskOwner;

        universe = this;
        logger.setLevel(Level.ALL);
    }

    public static UniverseImpl getUniverse() {
        return universe;
    }

    SpaceManager getSpaceManager() {
        return spaceManager;
    }

    public TransactionProxy getTransactionProxy() {
        return transactionProxy;
    }

    public DataService getDataService() {
        return dataService;
    }

    public void scheduleTransaction(KernelRunnable transaction, Identity identity) {
        if (identity == null) {
            identity = defaultTaskOwner;
        }

        transactionScheduler.scheduleTask(transaction, identity);
    }

    public void scheduleQueuedTransaction(KernelRunnable task, Identity identity, Object queueOwner) {
        if (identity == null) {
            identity = defaultTaskOwner;
        }

        // TODO this implementation will not work in multinode
        synchronized(taskQueues) {
            TaskQueue queue = taskQueues.get(queueOwner);
            if (queue==null) {
                queue = transactionScheduler.createTaskQueue();
                taskQueues.put(queueOwner, queue);
            }
            queue.addTask(task, identity);
        }
    }

    public void deleteTransactionQueue(Object queueOwner) {
        synchronized(taskQueues) {
            taskQueues.remove(queueOwner);
        }
    }

    public void addRootSpatialCell(CellID cellID, Identity identity) {
        SpatialCellImpl cellImpl = (SpatialCellImpl)getSpatialCell(cellID);

        // Set the root, which causes the WorldBounds to be evaluated and
        // the graph added to spaces & view caches.
        // No need to lock for this call because until it returns there
        // is no root to lock
        cellImpl.setRoot(cellImpl, null, identity);
    }

    public void removeRootSpatialCell(CellID cellID, Identity identity) {
        // Handled in removeCell();

        
//        logger.fine("removeSpatialCell "+cellID);
//        SpatialCellImpl cellImpl = (SpatialCellImpl)getSpatialCell(cellID);
//
//        if (cellImpl==null) {
//            logger.warning("removeRootSpatialCell FAILED, unable to find cell "+cellID);
//            return;
//        }
//
//        // Set the root node to null. Internally this method will lock correctly
//        // and ensure the graph is removed from spaces etc
//        cellImpl.setRoot(null, null, identity);

    }

    public SpatialCellImpl createSpatialCell(CellID id, BigInteger dsID, Class cellClass) {
        logger.fine("createSpatialCell "+id+"   dsID "+dsID);
        SpatialCellImpl ret;
        if (ViewCellMO.class.isAssignableFrom(cellClass)) {
            ret = new ViewCellImpl(id, spaceManager, dsID);
        } else
            ret = new SpatialCellImpl(id, dsID);
        synchronized(cells) {
            cells.put(id, ret);
        }

        return ret;
    }

    public void revalidateCell(CellID id) {
        SpatialCell cell = getSpatialCell(id);
        if (cell == null) {
            logger.warning("Attempt to revalidate non-existant cell " + id);
        } else {
            cell.revalidate();
        }
    }

    public void removeCell(CellID id) {
        // TODO remove from caches
        logger.fine("removeCell "+id);
        
        SpatialCellImpl cell = (SpatialCellImpl) getSpatialCell(id);
        if (cell.getParent()!=null) {
            cell.getParent().removeChild(cell);
        }

        Iterable<SpatialCellImpl> children = cell.getChildren();
        if (children!=null) {
            for(SpatialCellImpl child : cell.getChildren())
                removeCell(child.getCellID());
        }

        cell.destroy();

        synchronized(cells) {
            cells.remove(id);
        }
    }

//    private void print(SpatialCellImpl cell, int depth) {
//        for(int i=0; i<depth; i++)
//            System.err.print("  ");
//
//        System.err.println(cell.getCellID()+"  "+cell.viewCacheSet);
//        if (cell.getChildren()!=null)
//            for(SpatialCellImpl c : cell.getChildren())
//                print(c, depth+1);
//    }

    public SpatialCell getSpatialCell(CellID cellID) {
        synchronized(cells) {
            return cells.get(cellID);
        }
    }

    public void viewLogin(CellID viewCellID, BigInteger cellCacheId, Identity identity) {
        ViewCellImpl viewCell;
        synchronized(cells) {
            viewCell = (ViewCellImpl) cells.get(viewCellID);
        }

        logger.fine("ViewLogin ViewCell="+viewCell+"  ds ID="+viewCellID);
        
        ViewCache viewCache = new ViewCache(viewCell, spaceManager, identity, cellCacheId);
        viewCell.setViewCache(viewCache);
        viewCache.login();
    }

    public void viewRevalidate(CellID viewCellID) {
        ViewCellImpl viewCell;
        synchronized(cells) {
            viewCell = (ViewCellImpl) cells.get(viewCellID);
        }

        // it's possible the viewcell won't be in our list of cells.  For
        // example when there is a warm restart, this node will see all the
        // clients log out, even though it never saw them log in.  This is
        // fine, just ignore the request.
        if (viewCell != null && viewCell.getViewCache() != null) {
            viewCell.getViewCache().revalidate();
        }
    }

    public void viewLogout(CellID viewCellID, Identity identity) {
        logger.fine("ViewLogout viewCell="+viewCellID);
        ViewCellImpl viewCell;
        synchronized(cells) {
            viewCell = (ViewCellImpl) cells.get(viewCellID);
        }
        
        // it's possible the viewcell won't be in our list of cells.  For
        // example when there is a warm restart, this node will see all the
        // clients log out, even though it never saw them log in.  This is
        // fine, just ignore the request.
        if (viewCell != null && viewCell.getViewCache() != null) {
            viewCell.getViewCache().logout();
        } else {
            // if the view or cache doesn't exist for any reason, we still
            // need to notify listeners that the view logged out (for
            // example in a warm start).  Notify all listeners since we
            // can't limit it to only listeners in a particular cell's
            // cache.
            notifyLogout(viewCellID, identity);
        }
    }

    public void addTransformChangeListener(CellID cellID, TransformChangeListenerSrv listener) {
        synchronized(cells) {
            SpatialCellImpl cell = (SpatialCellImpl) cells.get(cellID);
            if (cell!=null)
                cell.addTransformChangeListener(listener);
        }
    }

    public void removeTransformChangeListener(CellID cellID, TransformChangeListenerSrv listener) {
        synchronized(cells) {
            SpatialCellImpl cell = (SpatialCellImpl) cells.get(cellID);
            if (cell!=null)
                cell.removeTransformChangeListener(listener);
        }
    }

    public void addViewUpdateListener(CellID cellID, ViewUpdateListener viewUpdateListener) {
        // keep a list of cells for each update listener
        synchronized (updateListeners) {
            Set<CellID> cellIDs = updateListeners.get(viewUpdateListener);
            if (cellIDs == null) {
                cellIDs = new LinkedHashSet<CellID>();
                updateListeners.put(viewUpdateListener, cellIDs);
            }
            cellIDs.add(cellID);
        }

        synchronized(cells) {
            SpatialCell cell = cells.get(cellID);
            if (cell!=null)
                cell.addViewUpdateListener(viewUpdateListener);
        }
    }

    public void removeViewUpdateListener(CellID cellID, ViewUpdateListener viewUpdateListener) {
        // update list of cells for this listener
        synchronized (updateListeners) {
            Set<CellID> cellIDs = updateListeners.get(viewUpdateListener);
	    if (cellIDs != null) {
                cellIDs.remove(cellID);
            
                if (cellIDs.isEmpty()) {
                    updateListeners.remove(viewUpdateListener);
		}
            }
        }

        synchronized(cells) {
            SpatialCell cell = cells.get(cellID);
            if (cell!=null)
                cell.removeViewUpdateListener(viewUpdateListener);
        }
    }

    private void notifyLogout(final CellID viewCellID,
                              final Identity identity)
    {
        // collect all listeners and cells to notify
        List<ListenerNotification> notifications =
                new LinkedList<ListenerNotification>();
        synchronized (updateListeners) {
            for (Map.Entry<ViewUpdateListener, Set<CellID>> e :
                 updateListeners.entrySet())
            {
                for (CellID cellID : e.getValue()) {
                    ListenerNotification ln = new ListenerNotification();
                    ln.listener = e.getKey();
                    ln.cellID = cellID;

                    notifications.add(ln);
                }
            }
        }

        // notify each listener in a separate task
        for (final ListenerNotification ln : notifications) {
            scheduleTransaction(new KernelRunnable() {
                public String getBaseTaskType() {
                    return UniverseImpl.class.getName() + ".LogoutNotifier";
                }

                public void run() throws Exception {
                    ln.listener.viewLoggedOut(ln.cellID, viewCellID);
                }
            }, identity);
        }
    }

    private class ListenerNotification {
        ViewUpdateListener listener;
        CellID cellID;
    }
}
