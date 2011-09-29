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
package org.jdesktop.wonderland.server.spatial;

import java.util.logging.Level;
import org.jdesktop.wonderland.server.cell.TransformChangeListenerSrv;
import org.jdesktop.wonderland.server.spatial.impl.Universe;
import com.jme.bounding.BoundingVolume;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.NameNotBoundException;
import com.sun.sgs.app.util.ScalableHashMap;
import com.sun.sgs.app.util.ScalableHashSet;
import com.sun.sgs.app.util.ScalableList;
import com.sun.sgs.auth.Identity;
import com.sun.sgs.impl.sharedutil.LoggerWrapper;
import com.sun.sgs.impl.sharedutil.PropertiesWrapper;
import com.sun.sgs.impl.util.AbstractService;
import com.sun.sgs.impl.util.TransactionContext;
import com.sun.sgs.impl.util.TransactionContextFactory;
import com.sun.sgs.kernel.ComponentRegistry;
import com.sun.sgs.kernel.KernelRunnable;
import com.sun.sgs.service.Transaction;
import com.sun.sgs.service.TransactionProxy;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.ThreadManager;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.CellPersistence;
import org.jdesktop.wonderland.server.cell.view.ViewCellMO;
import org.jdesktop.wonderland.server.spatial.impl.SpatialCell;
import org.jdesktop.wonderland.server.spatial.impl.SpatialCellImpl;
import org.jdesktop.wonderland.server.spatial.impl.UniverseImpl;

/**
 *
 * @author paulby
 */
public class UniverseService extends AbstractService implements UniverseManager {

    /** The name of this class. */
    private static final String NAME = UniverseService.class.getName();

    /** The package name. */
    private static final String PKG_NAME = "org.jdesktop.wonderland.server.spatial";

    /** The logger for this class. */
    private static final LoggerWrapper logger =
        new LoggerWrapper(Logger.getLogger(PKG_NAME));

    /** The name of the version key. */
    private static final String VERSION_KEY = PKG_NAME + ".service.version";

    /** The major version. */
    private static final int MAJOR_VERSION = 1;

    /** The minor version. */
    private static final int MINOR_VERSION = 0;

    // default property values
    private static final String CELL_LOAD_PROP = NAME + ".cell.load.count";
    private static final int CELL_LOAD_DEFAULT = 5;

    /** Key for the list of transform change listener */
    private static final String LISTENERS_KEY = NAME + ".listeners";

    /** Key for the list of cell listeners */
    private static final String CELL_LISTENERS_KEY = NAME + ".cell.listeners";

    private Universe universe;
    private ChangeApplication changeApplication;

    // manages the context of the current transaction
    private TransactionContextFactory<BoundsTransactionContext> ctxFactory;

    // the number of cells to load per transaction
    private final int cellLoadCount;

    public UniverseService(Properties props,
                           ComponentRegistry registry,
                           TransactionProxy proxy)
    {
        super(props, registry, proxy, logger);


        logger.log(Level.CONFIG, "Creating UniverseService properties:{0}",
                   props);
        PropertiesWrapper wrappedProps = new PropertiesWrapper(props);

        // read properties
        cellLoadCount = wrappedProps.getIntProperty(CELL_LOAD_PROP,
                                                    CELL_LOAD_DEFAULT);

        // create the transaction context factory
        ctxFactory = new TransactionContextFactoryImpl(proxy);

        // create the cache objects we need
        changeApplication = new ChangeApplication();
        universe = new UniverseImpl(registry, proxy, taskOwner);

        try {
            /*
	         * Check service version.
 	         */
            transactionScheduler.runTask(new KernelRunnable() {
                public String getBaseTaskType() {
                    return NAME + ".VersionCheckRunner";
                }

                public void run() {
                    checkServiceVersion(
                            VERSION_KEY, MAJOR_VERSION, MINOR_VERSION);
                }
            }, taskOwner);
        } catch (Exception ex) {
            logger.logThrow(Level.SEVERE, ex, "Error reloading cells");
        }
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected void doReady() {
        // now that everything is set up, reload our cache of cells
        logger.log(Level.CONFIG, "Readying UniverseService");

        final CellPersistence cells = new CellPersistence();

        // the key in the datastore for our iterator binding
        final String key = NAME + ".ROOTS_ITERATOR";
        
        try {

            // get the set of all CellIDs to reload.  This is a ScalableHashSet,
            // so the iterator is guaranteed to be serializable, and work
            // properly across multiple transactions.
            GetRootCells getRoots = new GetRootCells(cells, key);
            transactionScheduler.runTask(getRoots, taskOwner);

            int addedCount = 0;
            int errorCount = 0;

            // now iterate through the set of roots, reloading up to
            // cellLoadCount cells at each iteration.  Note that all access
            // to the iterator must be done in a transaction, since the
            // iterator uses the data service
            boolean finished = false;
            while (!finished) {
                ReloadCells reload = new ReloadCells(cells, key, cellLoadCount);
                transactionScheduler.runTask(reload, taskOwner);

                // process the results
                for (Map.Entry<CellID, Boolean> result :
                        reload.getResults().entrySet())
                {
                    if (result.getValue().booleanValue()) {
                        // successful addition
                        addedCount++;
                    } else {
                        // error
                        logger.log(Level.WARNING, "Error loading " +
                                   result.getKey());
                        errorCount++;
                    }
                }

                finished = reload.isFinished();
            }

            logger.log(Level.INFO, "Added " + addedCount + " cells. " +
                       errorCount + " errors.");
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to reload cells", ex);
        }

        // restore listeners after cells are reloaded, since the listeners are
        // added to the SpatialCellImpl objects, which aren't created until
        // the cells are restored
        try {
            transactionScheduler.runTask(new ReloadListeners(), taskOwner);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to reload listeners", ex);
        }

        logger.log(Level.CONFIG, "UniverseService is ready");
    }

    @Override
    protected void doShutdown() {
        // nothing to do
    }

    @Override
    protected void handleServiceVersionMismatch(Version oldVersion,
                                                Version currentVersion) {
        throw new IllegalStateException(
 	            "unable to convert version:" + oldVersion +
	            " to current version:" + currentVersion);
    }

//    private SpatialCell cloneGraph(CellMO cellMO) {
//        SpatialCell ret = universe.createSpatialCell(cellMO.getCellID(), false);
//
//        for(ManagedReference<CellMO> childRef : cellMO.getAllChildrenRefs()) {
//            ret.addChild(cloneGraph(childRef.get()));
//        }
//
//        return ret;
//    }

    private void scheduleChange(Runnable change) {
        // get the current transaction state, and add the change to it
        ctxFactory.joinTransaction().addChange(change);
    }

    public void addRootToUniverse(CellMO rootCellMO) {
        final Identity identity = txnProxy.getCurrentOwner();
        scheduleChange(new Change(rootCellMO.getCellID(), null, null) {
            public void run() {
                if (logger.isLoggable(Level.FINE))
                    logger.log(Level.FINE, "RUN addRootToUniverse");
                universe.addRootSpatialCell(cellID, identity);
            }
        });
    }

    public void removeRootFromUniverse(CellMO rootCellMO) {
//        final Identity identity = txnProxy.getCurrentOwner();
        scheduleChange(new Change(rootCellMO.getCellID(), null, null) {
            public void run() {
                if (logger.isLoggable(Level.FINE))
                    logger.log(Level.FINE, "RUN removeRootFromUniverse");
                universe.removeCell(cellID);
            }
        });
    }

    public void createCell(CellMO cellMO, boolean notify) {
        final Class cellClazz = cellMO.getClass();
        final Identity identity = txnProxy.getCurrentOwner();
        final BigInteger dsID = AppContext.getDataManager().createReference(cellMO).getId();

        scheduleChange(new Change(cellMO.getCellID(), cellMO.getLocalBounds(), cellMO.getLocalTransform(null)) {
            public void run() {
                SpatialCell sc = universe.createSpatialCell(cellID, dsID, cellClazz);
                sc.setLocalBounds(localBounds);
                sc.setLocalTransform(localTransform, identity);
            }
        });

        if (notify) {
            // notify listeners
            for (CellMOListener listener : getCellListeners()) {
                listener.cellAdded(cellMO);
            }
        }
    }

    public void revalidateCell(CellMO cellMO) {
        scheduleChange(new Change(cellMO.getCellID(), null, null) {
            public void run() {
                universe.revalidateCell(cellID);
            }
        });
    }

    public void removeCell(CellMO cell) {
        scheduleChange(new Change(cell.getCellID(), null, null) {

            public void run() {
                if (logger.isLoggable(Level.FINE))
                    logger.log(Level.FINE, "RUN removeChild");
                universe.removeCell(cellID);
            }
        });

        // notify listeners
        for (CellMOListener listener : getCellListeners()) {
            listener.cellRemoved(cell);
        }
    }

    public void addChild(CellMO parent, CellMO child) {
        final Identity identity = txnProxy.getCurrentOwner();
        scheduleChange(new Change(parent.getCellID(), child.getCellID()) {
            public void run() {
                if (logger.isLoggable(Level.FINE))
                    logger.log(Level.FINE, "RUN addChild");
                SpatialCell parent = universe.getSpatialCell(cellID);
                parent.addChild(universe.getSpatialCell(childCellID), identity);
            }
        });

    }

    public void removeChild(CellMO parent, CellMO child) {
        scheduleChange(new Change(parent.getCellID(), child.getCellID()) {
            public void run() {
                if (logger.isLoggable(Level.FINE))
                    logger.log(Level.FINE, "RUN removeChild "+cellID+"  "+childCellID);
                SpatialCell parent = universe.getSpatialCell(cellID);
                parent.removeChild(universe.getSpatialCell(childCellID));
            }
        });
    }

    public void setLocalTransform(CellMO cellMO, CellTransform localTransform) {
        final Identity identity = txnProxy.getCurrentOwner();

        /*
        try {
            throw new Exception("Trace");
        } catch (Exception ex) {
            logger.log(Level.INFO, "set local transform for cell " +
                       cellMO.getCellID(), ex);
        }
         */

        scheduleChange(new Change(cellMO.getCellID(), null, localTransform) {
            public void run() {
                SpatialCell sc = universe.getSpatialCell(cellID);
                if (sc == null) {
                    logger.log(Level.WARNING, "Cell " + cellID + " not found!");
                } else {
                    sc.setLocalTransform(localTransform, identity);
                }
            }
        });
    }

    public void viewLogin(ViewCellMO viewCell) {
        final BigInteger cellCacheId = AppContext.getDataManager().createReference( viewCell.getCellCache()).getId();
        final Identity identity = txnProxy.getCurrentOwner();

        scheduleChange(new Change(viewCell.getCellID(), null, null) {
            public void run() {
                universe.viewLogin(cellID, cellCacheId, identity);
            }
        });
    }

    public void viewRevalidate(ViewCellMO viewCell) {
        scheduleChange(new Change(viewCell.getCellID(), null, null) {
            public void run() {
                universe.viewRevalidate(cellID);
            }
        });
    }

    public void viewLogout(ViewCellMO viewCell) {
        final Identity identity = txnProxy.getCurrentOwner();

        scheduleChange(new Change(viewCell.getCellID(), null, null) {
            public void run() {
                universe.viewLogout(cellID, identity);
            }
        });
    }

    public CellTransform getWorldTransform(CellMO cell, CellTransform result) {
        SpatialCellImpl spatial = (SpatialCellImpl ) universe.getSpatialCell(cell.getCellID());

        // issue #727: if the cell has not yet been added (because the job to
        // add it is scheduled but hasn't run yet), we should gracefully return
        // null here
        if (spatial == null) {
            return null;
        }

        CellTransform ret;
        spatial.acquireRootReadLock();
        if (spatial.getWorldTransform()==null)
            ret = null;
        else
            ret = spatial.getWorldTransform().clone(result);
        spatial.releaseRootReadLock();

        return ret;
    }

    public BoundingVolume getWorldBounds(CellMO cell, BoundingVolume result) {
        SpatialCellImpl spatial = (SpatialCellImpl) universe.getSpatialCell(cell.getCellID());
        BoundingVolume ret;

        // issue #727: if the cell has not yet been added (because the job to
        // add it is scheduled but hasn't run yet), we should gracefully return
        // null here
        if (spatial == null) {
            return null;
        }

        spatial.acquireRootReadLock();
        ret = spatial.getWorldBounds().clone(result);
        spatial.releaseRootReadLock();

        return ret;
    }

    public void addCellListener(CellMOListener listener) {
        getCellListeners().add(listener);
    }

    public void removeCellListener(CellMOListener listener) {
        getCellListeners().remove(listener);
    }

    public void addTransformChangeListener(CellMO cell, final TransformChangeListenerSrv listener) {
        // add a record of this listener so if can be reinstantiated
        addListenerRecord(cell.getCellID(), listener, ListenerRecord.Type.TRANSFORM);

        scheduleChange(new Change(cell.getCellID(), null, null) {
            public void run() {
                universe.addTransformChangeListener(cellID, listener);
            }
        });
    }

    public void removeTransformChangeListener(CellMO cell, final TransformChangeListenerSrv listener) {
        // remove the record of this listener
        removeListenerRecord(cell.getCellID(), listener, ListenerRecord.Type.TRANSFORM);

        scheduleChange(new Change(cell.getCellID(), null, null) {
            public void run() {
                universe.removeTransformChangeListener(cellID, listener);
            }
        });
    }

    public void addViewUpdateListener(CellMO cell, final ViewUpdateListener viewUpdateListener) {
        // add a record of this listener so if can be reinstantiated
        addListenerRecord(cell.getCellID(), viewUpdateListener, ListenerRecord.Type.VIEW);

        scheduleChange(new Change(cell.getCellID(), null, null) {
            public void run() {
                universe.addViewUpdateListener(cellID, viewUpdateListener);
            }
        });
    }

    public void removeViewUpdateListener(CellMO cell, final ViewUpdateListener viewUpdateListener) {
        // remove the record of this listener
        removeListenerRecord(cell.getCellID(), viewUpdateListener, ListenerRecord.Type.VIEW);

        scheduleChange(new Change(cell.getCellID(), null, null) {
            public void run() {
                universe.removeViewUpdateListener(cellID, viewUpdateListener);
            }
        });
    }

    private void addListenerRecord(CellID cellID, Object listener,
                                   ListenerRecord.Type type)
    {
        Map<Object, ListenerRecord> listeners = getListenerMap();

        ListenerRecord record = listeners.get(listener);
        if (record == null) {
            record = new ListenerRecord(listener);
            listeners.put(listener, record);
        }

        record.addCellRecord(cellID, type);
    }

    private void removeListenerRecord(CellID cellID, Object listener,
                                      ListenerRecord.Type type)
    {
        Map<Object, ListenerRecord> listeners = getListenerMap();

        ListenerRecord record = listeners.get(listener);
        if (record == null) {
            return;
        }

        record.removeCellRecord(cellID, type);
        if (record.getCells().isEmpty()) {
            listeners.remove(listener);
        }
    }

    private Map<Object, ListenerRecord> getListenerMap() {
        Map<Object, ListenerRecord> out;

        try {
            out = (Map<Object, ListenerRecord>)
                    dataService.getServiceBindingForUpdate(LISTENERS_KEY);
        } catch (NameNotBoundException nnbe) {
            out = new ScalableHashMap<Object, ListenerRecord>();
            dataService.setServiceBinding(LISTENERS_KEY, out);
        }

        return out;
    }

    private Set<CellMOListener> getCellListeners() {
        Set<CellMOListener> out;

        try {
            out = (Set<CellMOListener>) dataService.getServiceBindingForUpdate(CELL_LISTENERS_KEY);
        } catch (NameNotBoundException nnbe) {
            out = new ScalableHashSet<CellMOListener>();
            dataService.setServiceBinding(CELL_LISTENERS_KEY, out);
        }

        return out;
    }

    public void scheduleOnTransaction(Runnable runnable) {
        scheduleChange(runnable);
    }

    public void scheduleTask(UniverseKernelRunnable task) {
        try {
            task.setDataService(dataService);
            transactionScheduler.runTask(task, taskOwner);
        } catch (Exception ex) {
            // rethrow runtime exceptions to avoid Darkstar issues
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            }

            Logger.getLogger(UniverseService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void runTxnRunnable(UniverseTxnRunnable runnable) {
        runnable.run(txnProxy);
    }

    /**
     * A change to apply to the cell.  This change will be applied when
     * the current transaction commits.  The run() method of subclasses
     * should perform any actual changes.
     */
    private static abstract class Change implements Runnable {
        protected CellID cellID;
        protected BoundingVolume localBounds;
        protected CellTransform localTransform;
        protected CellID childCellID;

        public Change(CellID cellID, BoundingVolume localBounds, CellTransform localTransform) {
            this.cellID = cellID;
            this.localBounds = localBounds;
            this.localTransform = localTransform;
        }

        public Change(CellID cellID, CellID childCellID) {
            this.cellID = cellID;
            this.childCellID = childCellID;
        }

    }

    /**
     * Transaction state
     */
    private class BoundsTransactionContext extends TransactionContext {
        private List<Runnable> changes;

        public BoundsTransactionContext(Transaction txn) {
            super (txn);

            changes = new ArrayList();
        }

        public void addChange(Runnable change) {
            changes.add(change);
        }

        @Override
        public void abort(boolean retryable) {
            changes.clear();
        }

        @Override
        public void commit() {
            try {
                changeApplication.addChanges(changes);
                // done with all changes
                changes.clear();
            } finally {
//                boundsLock.writeLock().unlock();
            }

            isCommitted = true;
        }
    }

    /** Private implementation of {@code TransactionContextFactory}. */
    private class TransactionContextFactoryImpl
            extends TransactionContextFactory<BoundsTransactionContext> {

        /** Creates an instance with the given proxy. */
        TransactionContextFactoryImpl(TransactionProxy proxy) {
            super(proxy, NAME);

        }

        /** {@inheritDoc} */
        protected BoundsTransactionContext createContext(Transaction txn) {
            return new BoundsTransactionContext(txn);
        }
    }

    private static class ReloadState implements Serializable, ManagedObject {
        private Iterator<CellID> rootCells;

        public ReloadState(Iterator<CellID> rootCells) {
            this.rootCells = rootCells;
        }

        public Iterator<CellID> getRootCells() {
            return rootCells;
        }
    }

    private class GetRootCells implements KernelRunnable {
        private CellPersistence cells;
        private String key;

        GetRootCells(CellPersistence cells, String key) {
            this.cells = cells;
            this.key = key;
        }

        public String getBaseTaskType() {
            return NAME + ".GetRootCells";
        }

        public void run() throws Exception {
            // get the set of cells to reload, and store the value in a 
            // binding in the datastore.  Subsequent ReloadCells tasks
            // will retrieve this value to iterate through a portion of the
            // cells
            ReloadState state = new ReloadState(
                    cells.getRootCellIDs().iterator());
            dataService.setServiceBinding(key, state);
        }
    }

    private class ReloadCells implements KernelRunnable {
        private CellPersistence cells;
        private String key;
        private int max;

        private Map<CellID, Boolean> results = new LinkedHashMap<CellID, Boolean>();
        private boolean finished;

        ReloadCells(CellPersistence cells, String key, int max) {
            this.cells = cells;
            this.key = key;
            this.max = max;
        }

        boolean isFinished() {
            return finished;
        }

        Map<CellID, Boolean> getResults() {
            return results;
        }

        public String getBaseTaskType() {
            return NAME + ".ReloadCell";
        }

        public void run() throws Exception {
            // load the state of iteration from the data store
            ReloadState state = (ReloadState) dataService.getServiceBinding(key);
            if (state == null) {
                // make sure we haven't finished already
                finished = true;
                return;
            }

            // get the iterator of cells to reload
            Iterator<CellID> cellIDs = state.getRootCells();
            int count = 0;

            // load a cell
            while (cellIDs.hasNext() && (count < max)) {
                CellID cellID = cellIDs.next();

                // try to reload the cell
                boolean result = cells.reloadCell(cellID, UniverseService.this);

                // record the result
                results.put(cellID, result);

                count++;
            }

            // check if there are more cells left to load
            finished = !cellIDs.hasNext();
        
            // remove the binding if we are finished
            if (finished) {
                dataService.removeServiceBinding(key);
            }
        }
    }
    
    private class ReloadListeners implements KernelRunnable {
        public String getBaseTaskType() {
            return NAME + ".ReloadListeners";
        }

        public void run() throws Exception {
            final List<CellID> revalidateList = new ArrayList<CellID>();

            for (final ListenerRecord record : getListenerMap().values()) {
                for (final Map.Entry<CellID, ListenerRecord.Type> e :
                     (Set<Entry<CellID, ListenerRecord.Type>>) record.getCells().entrySet())
                {

                    // add the cell id to the revalidate list
                    revalidateList.add(e.getKey());

                    // schedule adding the listener
                    scheduleChange(new Change(e.getKey(), null, null) {
                        public void run() {
                            SpatialCell cell = universe.getSpatialCell(cellID);
                            if (cell == null) {
                                // the cell no longer exists.
                                return;
                            }

                            logger.log(Level.INFO, "Restoring listener " +
                                       record.get() + " type " + e.getValue());

                            if (e.getValue() == ListenerRecord.Type.TRANSFORM ||
                                e.getValue() == ListenerRecord.Type.BOTH)
                            {
                                universe.addTransformChangeListener(cellID, (TransformChangeListenerSrv) record.get());
                            }

                            if (e.getValue() == ListenerRecord.Type.VIEW ||
                                e.getValue() == ListenerRecord.Type.BOTH)
                            {
                                universe.addViewUpdateListener(cellID, (ViewUpdateListener) record.get());
                            }
                        }
                    });
                }
            }

            // now schedule a change to revalidate each cell ID we added, so
            // the transform is up to date
            for (CellID cellID : revalidateList) {
                scheduleChange(new Change(cellID, null, null) {
                    public void run() {
                        SpatialCell cell = universe.getSpatialCell(cellID);
                        if (cell != null) {
                            cell.revalidateListeners(taskOwner);
                        }
                    }
                });
            }
        }
    }

    /**
     * A data structure that tracks listeners that are installed by this
     * service.  This structure specifically is designed to coalesce all
     * data about a particular listener into one record.  This ensures that
     * when listeners are restored, only a single serialized copy of each
     * listener exists, and that same listener will be registered with
     * all the methods on this service.  This prevents issues like the listener
     * being split into two different objects, one of which receives view
     * events and the other of which receives transform events.
     */
    
    private static class ListenerRecord<T> implements Serializable {
        private static final long serialVersionUID = 1l;

        private final T listener;
        private final ManagedReference<T> listenerRef;

        enum Type {
            NONE, TRANSFORM, VIEW, BOTH;

            public Type add(Type type) {
                return valueOf(this.ordinal() | type.ordinal());
            }

            public Type remove(Type type) {
                return valueOf(this.ordinal() ^ type.ordinal());
            }

            public Type valueOf(int bits) {
                return Type.values()[bits];
            }
        };
        private final Map<CellID, Type> cells =
                new LinkedHashMap<CellID, Type>();

        public ListenerRecord(T listener) {
            if (listener instanceof ManagedObject) {
                this.listener = null;
                this.listenerRef = AppContext.getDataManager().createReference(listener);
            } else {
                this.listener = listener;
                this.listenerRef = null;
            }
        }

        public T get() {
            if (listener != null) {
                return listener;
            } else {
                return listenerRef.get();
            }
        }

        public void addCellRecord(CellID cellID, Type type) {
            Type curType = cells.get(cellID);
            if (curType == null) {
                cells.put(cellID, type);
                return;
            }

            cells.put(cellID, curType.add(type));
        }

        public void removeCellRecord(CellID cellID, Type type) {
            Type curType = cells.get(cellID);
            if (curType == null) {
                return;
            }

            Type newType = curType.remove(type);
            if (newType == Type.NONE) {
                cells.remove(cellID);
            } else {
                cells.put(cellID, newType);
            }
        }

        public Map<CellID, Type> getCells() {
            return cells;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ListenerRecord)) {
                return false;
            }

            return get().equals(((ListenerRecord) o).get());
        }

        @Override
        public int hashCode() {
            if (listener != null) {
                return listener.hashCode();
            } else {
                return listenerRef.hashCode();
            }
        }
    }

    private class ChangeApplication extends Thread {

        private LinkedBlockingQueue<Runnable> changeList = new LinkedBlockingQueue();

        public ChangeApplication() {
            super(ThreadManager.getThreadGroup(), "ChangeApplication");
            start();
        }

        public void addChanges(Collection<Runnable> changes) {
            changeList.addAll(changes);
        }

        @Override
        public void run() {
            Runnable change;

            while(true) {
                try {
                    change = changeList.take();
                    change.run();
                } catch (InterruptedException ex) {
                    // if the thread is interrupted, exit
                    break;
                } catch (Throwable t) {
                    // for any other exception, print a warning and continue.
                    logger.logThrow(Level.WARNING, t, "[UniverseService] " +
                                    "Unexpected error in change thread", t);
                }
            }
        }
    }

}
