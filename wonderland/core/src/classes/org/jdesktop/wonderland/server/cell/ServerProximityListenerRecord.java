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
package org.jdesktop.wonderland.server.cell;

import com.jme.bounding.BoundingVolume;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.NameNotBoundException;
import com.sun.sgs.app.util.ScalableHashMap;
import com.sun.sgs.service.NonDurableTransactionParticipant;
import com.sun.sgs.service.Transaction;
import com.sun.sgs.service.TransactionProxy;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.ProximityListenerRecord;
import org.jdesktop.wonderland.server.spatial.UniverseManager;
import org.jdesktop.wonderland.server.spatial.UniverseTxnRunnable;
import org.jdesktop.wonderland.server.spatial.ViewUpdateListener;

/**
 * This listener record provides the server specific hooks for the generic 
 * proximity listener code, which is shared with the client code.
 *
 * @author paulby
 */
public class ServerProximityListenerRecord extends ProximityListenerRecord implements TransformChangeListenerSrv, ViewUpdateListener {
    private static final Logger logger =
            Logger.getLogger(ServerProximityListenerRecord.class.getName());

    private static final String BINDING_NAME = ServerProximityListenerRecord.class.getName();
    private ServerProximityListenerWrapper wrapper;
    private String id;

    private final Map<CellID, Integer> indexMap = 
            Collections.synchronizedMap(new HashMap<CellID, Integer>());
    private transient final ThreadLocal<MapUpdateTransactionParticipant> mutp =
            new ThreadLocal<MapUpdateTransactionParticipant>();

    ServerProximityListenerRecord(ServerProximityListenerWrapper proximityListener, BoundingVolume[] localBounds, String id) {
        super(proximityListener, localBounds);        

        this.wrapper = proximityListener;
        this.id = id;
    }

    public void viewTransformChanged(CellID cell, CellID viewCellID, CellTransform viewWorldTransform) {
        viewCellMoved(viewCellID, viewWorldTransform);
    }

    public void viewLoggedIn(CellID cell, CellID viewCellID) {
        // ignore
    }

    public void viewLoggedOut(CellID cell, CellID viewCellID) {
        logger.finest("View cell " + viewCellID + " exited on " + this);

        // remove view
        viewCellExited(viewCellID);

        // the wrapper may have more information in this case, for example
        // after a warm start, listeners persisted in the datastore
        wrapper.viewCellExited(viewCellID, getWorldBounds());
    }

    public void transformChanged(ManagedReference<CellMO> cellRef, CellTransform localTransform, CellTransform worldTransform) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Transform changed: " + localTransform + " " +
                          worldTransform + " on " + this);
        }

        updateWorldBounds(worldTransform);
    }

    void setLive(boolean isLive, final CellMO cell, UniverseManager mgr) {
        DataManager dm = AppContext.getDataManager();

        if (isLive) {
            Map<CellID, Integer> indexMap = new ScalableHashMap<CellID, Integer>();
            dm.setBinding(BINDING_NAME + id, indexMap);

            mgr.addTransformChangeListener(cell, this);
            mgr.addViewUpdateListener(cell, this);

            // issue #727: if the cell has not yet been added (because the job
            // to add it is scheduled but hasn't run), this may return null.
            // In that case, just return, since the listener will be notified
            // with the actual bounds once the cell is fully inserted into the
            // world
            CellTransform worldTransform = cell.getWorldTransform(null);
            if (worldTransform != null) {
                // Issue #721: we need to set the transform to the cell's transform
                // here, since the cell may already exist and we can't count on
                // getting a transform changed notification until the cell moves
                updateWorldBounds(worldTransform);
            }
        } else {
            mgr.removeTransformChangeListener(cell, this);
            mgr.removeViewUpdateListener(cell, this);

            try {
                dm.removeBinding(BINDING_NAME + id);
            } catch (NameNotBoundException nnbe) {
                // we can safely ignore this -- this just means the component
                // is being added before the cell is live, so the first time
                // setLive is called, the binding hasn't been created
                logger.log(Level.FINE, null, nnbe);
            }
        }
    }

    /**
     * Override to use a transactional version of this map.
     * @return the transactional map
     */
    @Override
    protected Map<CellID, Integer> getIndexMap() {
        // OWL issue #93: the map returned here must be transactional:
        // if a transaction updates an index and then aborts, that update
        // must get undone before the transaction retries. We use a
        // simple transaction participant stored in a thread-local
        // variable to do that.

        MapUpdateTransactionParticipant p = mutp.get();
        if (p == null) {
            // this is the first time we are accessing the thread-local
            // variable, so create it and join the transaction currently
            // in progress. After the join(), we are guaranteed to get
            // either a commit or an abort.
            final MapUpdateTransactionParticipant fp =
                    new MapUpdateTransactionParticipant(indexMap, mutp);

            AppContext.getManager(UniverseManager.class).runTxnRunnable(
                    new UniverseTxnRunnable()
            {
                public void run(TransactionProxy txnProxy) {
                    logger.fine("Join transaction " + txnProxy.getCurrentTransaction() +
                                " on " + ServerProximityListenerRecord.this +
                                " participant: " + fp);
                    txnProxy.getCurrentTransaction().join(fp);
                }
            });

            mutp.set(fp);
            p = fp;
        }

        return p;
    }

    /**
     * A simple implementation of a Map that stores puts and gets
     * conditionally until the transaction is committed. The participant
     * starts with a copy of the current global map, and updates that map
     * only if the transaction commits.
     * <p>
     * This is meant to be a fast operation, since it is done on every move.
     * Using a ManagedObject (with the potential for conflict) would likely
     * be too slow.
     * <p>
     * Note this only implements a subset of map: put(), get(), containsKey()
     * and remove().
     */
    private static class MapUpdateTransactionParticipant 
            extends HashMap<CellID, Integer> 
            implements NonDurableTransactionParticipant, Serializable
    {
        private final Map<CellID, Integer> globalMap;
        private final ThreadLocal<MapUpdateTransactionParticipant> tl;

        private final Map<CellID, MapChange> changes = 
                new HashMap<CellID, MapChange>();

        // generate a unique id, since HashMap does funny stuff with
        // equals() and hashcode()
        private final Object id = new Object();
        
        public MapUpdateTransactionParticipant(Map<CellID, Integer> globalMap,
            ThreadLocal<MapUpdateTransactionParticipant> tl)
        {
            this.globalMap = globalMap;
            this.tl = tl;
        }

        @Override
        public boolean containsKey(Object key) {
            MapChange change = changes.get(key);
            if (change == null) {
                logger.fine(this + " contains " + key + " from global map");
                return globalMap.containsKey(key);
            }
            
            return (change.type == MapChange.Type.ADD);
        }

        @Override
        public Integer get(Object key) {
            MapChange change = changes.get(key);
            if (change == null) {
                logger.fine(this + " get " + key + " from global map");
                return globalMap.get(key);
            }
            
            if (change.type == MapChange.Type.ADD) {
                return change.value;
            } else {
                return null;
            }
        }
        
        @Override
        public Integer put(CellID key, Integer value) {
            Integer cur = globalMap.get(key);
            logger.fine(this + " put " + key + " = " + value);
            changes.put(key, new MapChange(MapChange.Type.ADD, value));
            return cur;
        }

        @Override
        public Integer remove(Object key) {
            Integer cur = globalMap.get(key);
            logger.fine(this + " remove " + key);
            changes.put((CellID) key, new MapChange(MapChange.Type.REMOVE, cur));
            return cur;
        }
        
        public String getTypeName() {
            return MapUpdateTransactionParticipant.class.getName();
        }
        
        public boolean prepare(Transaction t) throws Exception {
            return false;
        }

        public void commit(Transaction t) {
            logger.fine(this + " commit " + t + " to " + globalMap + "; " +
                        changes.size() + " changes");

            synchronized (globalMap) {
                for (Map.Entry<CellID, MapChange> e : changes.entrySet()) {
                    e.getValue().apply(globalMap, e.getKey());
                }
            }

            tl.remove();
        }

        public void abort(Transaction t) {
            logger.fine(this + " abort " + t + " on " + globalMap);

            // make no changes to the global map
            tl.remove();
        }
        
        public void prepareAndCommit(Transaction t) throws Exception {
            logger.fine(this + " prepare and commit " + t);
            prepare(t);
            commit(t);
        }

        @Override
        public String toString() {
            return id.toString();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final MapUpdateTransactionParticipant other = (MapUpdateTransactionParticipant) obj;
            if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 17 * hash + (this.id != null ? this.id.hashCode() : 0);
            return hash;
        }

    }

    /**
     * Record a provisional change to a map
     */
    private static class MapChange {
        enum Type { ADD, REMOVE };
        
        final Type type;
        final Integer value;
        
        public MapChange(Type type, Integer value) {
            this.type = type;
            this.value = value;
        }
        
        public void apply(Map<CellID, Integer> map, CellID key) {
            switch (type) {
                case ADD:
                    logger.fine("Commit add " + key + ":" + value + " to " +
                                   map);
                    map.put(key, value);
                    break;
                case REMOVE:
                    logger.fine("Commit remove " + key + ":" + value + " from " +
                                   map);
                    map.remove(key);
                    break;
            }
        }

        @Override
        public String toString() {
            return "{Change: " + type + " value: " + value + "}";
        }
    }

    /**
     * Internal structure containing the array of bounds for a given listener.
     * This class keeps a map from cell id to bounds index for all listeners
     * in the bounds of this cell.  This map is kept as a Darkstar managed
     * object, and is used during warm restart to remember what listeners
     * were previously available and clean them up (during the call to
     * viewCellExited() when the view for that cell is cleaned up).  The map is
     * kept here because the listener is only notified when there is a change,
     * so this minimizes the number of calls to Darkstar managed objects.
     */
    static class ServerProximityListenerWrapper implements ProximityListenerRecord.ProximityListenerWrapper {

        private ProximityListenerSrv listener;
        private CellID cellID;
        private String id;

        public ServerProximityListenerWrapper(CellID cell, ProximityListenerSrv listener, String id) {
            this.listener = listener;
            this.cellID = cell;
            this.id = id;
        }

        CellID getCellID() {
            return cellID;
        }

        public void viewEnterExit(boolean enter,
                                  BoundingVolume proximityVolume,
                                  int proximityIndex,
                                  CellID viewCellID)
        {
            listener.viewEnterExit(enter, cellID, viewCellID, proximityVolume,
                                   proximityIndex);
        
            int curIndex = proximityIndex;
            if (!enter) {
                curIndex -= 1;
            }
            
            if (curIndex == -1) {
                getIndexMap().remove(viewCellID);
            } else {
                getIndexMap().put(viewCellID, curIndex);
            }
        }

        public void viewCellExited(CellID viewCellID, BoundingVolume[] bounds) {
            if (getIndexMap().containsKey(viewCellID)) {
                int lastIndex = getIndexMap().remove(viewCellID);

                // notify exit for each bounds
                for (int i = lastIndex; i >= 0; i--) {
                    listener.viewEnterExit(false, cellID, viewCellID,
                            bounds[i], i);
                }
            }
        }

        Map<CellID, Integer> getIndexMap() {
            DataManager dm = AppContext.getDataManager();
            try {
                return (Map<CellID, Integer>) dm.getBinding(BINDING_NAME + id);
            } catch (NameNotBoundException nnbe) {
                logger.warning("No binding for " + id);
                return Collections.emptyMap();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ServerProximityListenerWrapper))
                return false;

            return (((ServerProximityListenerWrapper)o).listener.equals(listener));
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 53 * hash + (this.listener != null ? this.listener.hashCode() : 0);
            return hash;
        }
    }
}

