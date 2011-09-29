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
package org.jdesktop.wonderland.server.wfs.exporter;

import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.jdesktop.wonderland.common.messages.MessageID;
import org.jdesktop.wonderland.common.wfs.WorldRoot;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.util.ManagedSerializable;
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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.wfs.CellDescriptor;
import org.jdesktop.wonderland.common.wfs.CellPath;
import org.jdesktop.wonderland.common.wfs.WFSRecordingList;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.CellManagerMO;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;
import org.jdesktop.wonderland.server.wfs.exporter.CellExportManager.CellExportListener;
import org.jdesktop.wonderland.server.wfs.exporter.CellExportManager.CellExportResult;
import org.jdesktop.wonderland.server.wfs.exporter.CellExportManager.ListRecordingsListener;
import org.jdesktop.wonderland.server.wfs.exporter.CellExportManager.RecordingCreationListener;
import org.jdesktop.wonderland.server.wfs.exporter.CellExportManager.SnapshotCreationListener;

/**
 *
 * @author kaplanj
 * @author Bernard Horan
 */
public class CellExportService extends AbstractService implements CellExportManager {

    /** The name of this class. */
    private static final String NAME = CellExportService.class.getName();

    /** The package name. */
    private static final String PKG_NAME = "org.jdesktop.wonderland.server.wfs.exporter";

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
    private static final String EXPORT_ON_STARTUP_PROP = NAME + ".export.on.startup";

    private final String exportOnStartup;

    /** manages the context of the current transaction */
    private TransactionContextFactory<CellExportTransactionContext> ctxFactory;

    /** executes the actual remote calls */
    private ExecutorService executor;


    public CellExportService(Properties props,
                           ComponentRegistry registry,
                           TransactionProxy proxy)
    {
        super(props, registry, proxy, logger);


        logger.log(Level.CONFIG, "Creating CellExportService properties:{0}",
                   props);
        PropertiesWrapper wrappedProps = new PropertiesWrapper(props);

        // read property values
        exportOnStartup = wrappedProps.getProperty(EXPORT_ON_STARTUP_PROP);

        // create the transaction context factory
        ctxFactory = new TransactionContextFactoryImpl(proxy);

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
        // create the executor thread
        executor = Executors.newSingleThreadExecutor();

        // if the export on startup property is set, export now
        if (exportOnStartup != null) {
            logger.log(Level.INFO, "CellExportService starting export");

            try {
                transactionScheduler.runTask(new KernelRunnable() {
                    public String getBaseTaskType() {
                        return NAME + ".CELL_EXPORTER";
                    }

                    public void run() throws Exception {
                        CellExporter ce = new CellExporter();
                        ce.export(exportOnStartup);
                    }
                }, taskOwner);
            } catch (Exception ex) {
                logger.logThrow(Level.WARNING, ex, "Error exporting");
            }
        }
        
        logger.log(Level.CONFIG, "CellExportService is ready");
    }

    @Override
    protected void doShutdown() {
        // stop the executor, attempting an orderly shutdown
        boolean shutdown = false;
        executor.shutdown();
        
        try {
           shutdown = executor.awaitTermination(1, TimeUnit.SECONDS); 
        } catch (InterruptedException ie) {
            // ignore
        }
        
        if (!shutdown) {
            List<Runnable> leftover = executor.shutdownNow();
            logger.log(Level.WARNING, "Terminating executor with tasks in" +
                       "  progress: " + leftover);
        }
    }

    @Override
    protected void handleServiceVersionMismatch(Version oldVersion,
                                                Version currentVersion) {
        throw new IllegalStateException(
 	            "unable to convert version:" + oldVersion +
	            " to current version:" + currentVersion);
    }

    public void createSnapshot(String name, SnapshotCreationListener listener) {
        if (!(listener instanceof ManagedObject)) {
            listener = new ManagedSnapshotCreationWrapper(listener);
        }

        // create a reference to the listener
        ManagedReference<SnapshotCreationListener> scl =
                dataService.createReference(listener);

        // now add the snapshot request to the transaction.  On commit
        // this request will be passed on to the executor for long-running
        // tasks
        CreateSnapshot cs = new CreateSnapshot(name, scl.getId());
        ctxFactory.joinTransaction().add(cs);
    }

    public void createRecording(String name, Set<CellID> cells, RecordingCreationListener listener) {
        if (!(listener instanceof ManagedObject)) {
            listener = new ManagedRecordingCreationWrapper(listener);
        }

        // create a reference to the listener
        ManagedReference<RecordingCreationListener> scl =
                dataService.createReference(listener);

        // now add the recording request to the transaction.  On commit
        // this request will be passed on to the executor for long-running
        // tasks
        CreateRecording cr = new CreateRecording(name, cells, scl.getId());
        ctxFactory.joinTransaction().add(cr);
    }


    public void exportCells(WorldRoot worldRoot,
                            Set<CellID> cellIDs,
                            CellExportListener listener,
                            boolean recordCellIDs)
    {
        if (!(listener instanceof ManagedObject)) {
            listener = new ManagedCellExportWrapper(listener);
        }

        // create a reference to the listener
        ManagedReference<CellExportListener> scl =
                dataService.createReference(listener);

        // create a queue of cells to process, and populate it with the
        // ids passed in in the original set
        Queue<CellExportEntry> ids = new LinkedList<CellExportEntry>();
        for (CellID id : cellIDs) {
            ids.add(new CellExportEntry(id, null));
        }

        // now add the snapshot request to the transaction.  On commit
        // this request will be passed on to the executor for long-running
        // tasks
        ExportCells ec = new ExportCells(worldRoot, ids, scl.getId(), recordCellIDs);
        ctxFactory.joinTransaction().add(ec);
    }

    public void listRecordings(MessageID messageID, WonderlandClientSender sender, WonderlandClientID clientID, ListRecordingsListener listener) {
        if (!(listener instanceof ManagedObject)) {
            listener = new ManagedRecordingsListenerWrapper(listener);
        }

        // create a reference to the listener
        ManagedReference<ListRecordingsListener> scl =
                dataService.createReference(listener);

        //We have to hang on to the wonderlandClientID to call the listener when we're done.
        //We can't pass it as an argument because of darkstar transactions
        //So create put it in the dataservice and keep a reference to it
        //Create a wrapper for the clientID, as it's not a managed object
        ManagedSerializable<WonderlandClientID> managedClientID = new ManagedSerializable(clientID);

        //create a refereence to the wrapped clientID
        ManagedReference<ManagedSerializable<WonderlandClientID>> wci = dataService.createReference(managedClientID);


        // now add the list recordings request to the transaction.  On commit
        // this request will be passed on to the executor for long-running
        // tasks
        ListRecordings lr = new ListRecordings(messageID, sender, wci.getId(), scl.getId());
        ctxFactory.joinTransaction().add(lr);
    }

    


    /**
     * A task that creates a new snapshot, and then notifies the snapshot
     * creation listener identified by managed reference id.
     */
    private class CreateSnapshot implements Runnable {
        private String name;
        private BigInteger listenerID;

        public CreateSnapshot(String name, BigInteger listenerID) {
            this.name = name;
            this.listenerID = listenerID;
        }

        public void run() {
            WorldRoot root = null;
            Exception ex = null;

            try {
                root = CellExporterUtils.createSnapshot(name);
            } catch (Exception ex2) {
                ex = ex2;
            }

            // notify the listener
            NotifySnapshotListener notify =
                    new NotifySnapshotListener(listenerID, root, ex);
            try {
                transactionScheduler.runTask(notify, taskOwner);
            } catch (Exception ex2) {
                logger.logThrow(Level.WARNING, ex2, "Error calling listener");
            }
        }
    }

    /**
     * A task that creates a new recording, and then notifies the recording
     * creation listener identified by managed reference id.
     */
    private class CreateRecording implements Runnable {
        private String name;
        private Set<CellID> cells;
        private BigInteger listenerID;

        public CreateRecording(String name, Set<CellID> cells, BigInteger listenerID) {
            this.name = name;
            this.cells = cells;
            this.listenerID = listenerID;
        }

        public void run() {
            WorldRoot root = null;
            Exception ex = null;

            try {
                root = CellExporterUtils.createRecording(name);
            } catch (Exception ex2) {
                ex = ex2;
            }

            // notify the listener
            NotifyRecordingListener notify =
                    new NotifyRecordingListener(listenerID, root, cells, ex);
            try {
                transactionScheduler.runTask(notify, taskOwner);
            } catch (Exception ex2) {
                logger.logThrow(Level.WARNING, ex2, "Error calling listener");
            }
        }
    }

    /**
     * A task that coordinates uploading a set of cells and all their children
     * to a snapshot on the server.  The results are then passed to a
     * CellExportListener identified by managed reference.
     */
    private class ExportCells implements Runnable {
        private WorldRoot root;
        private Queue<CellExportEntry> cells;
        private BigInteger listenerID;
        private boolean recordCellIDs;

        public ExportCells(WorldRoot root, Queue<CellExportEntry> cells,
                           BigInteger listenerID, boolean recordCellIDs)
        {
            this.root = root;
            this.cells = cells;
            this.listenerID = listenerID;
            this.recordCellIDs = recordCellIDs;
        }

        public void run() {
            // the result, as a map by cellID of the cells we've processed
            Map<CellID, CellExportResult> out =
                    new LinkedHashMap<CellID, CellExportResult>();

            // go through each cell on the list in turn. As we iterate through,
            // child cells will be added to the end of the cellIds list.
            // Keep iterating until this list is empty
            CellExportEntry entry;
            while ((entry = cells.poll()) != null) {
                if (out.containsKey(entry.cellID)) {
                    // yikes!  the cell has already been processed. This is
                    // probably really bad, but just print out a warning and
                    // go on
                    out.put(entry.cellID, new CellExportResultImpl(
                            "Duplicate cell ID: " + entry.cellID, null));
                    continue;
                }

                // first, resolve the cell ID into a CellDescriptor in a task.
                GetCellDescriptor get = new GetCellDescriptor(entry.cellID,
                        root, entry.parentPath, recordCellIDs);
                try {
                    transactionScheduler.runTask(get, taskOwner);
                } catch (Exception ex) {
                    out.put(entry.cellID, new CellExportResultImpl(
                            "Error in export: " + entry.cellID, ex));
                    continue;
                }

                // if the cell descriptor is null, it means this cell can't
                // be persisted.  That's fine, just ignore the cell
                if (get.getCellDescriptor() == null) {
                    out.put(entry.cellID, new CellExportResultImpl(
                            "Cell not exportable", null));
                    continue;
                }

                // now export the descriptor to the web service
                try {
                    CellExporterUtils.createCell(get.getCellDescriptor());
                } catch (Exception ex) {
                    out.put(entry.cellID, new CellExportResultImpl(
                            "Error writing " + entry.cellID, ex));
                    continue;
                }

                // Form the new parent path for this cell to pass down. If this
                // parent is at the root, then parentPath is null, so we need to
                // check for this
                String cellName = get.getCellDescriptor().getCellUniqueName();
                CellPath thisPath = (entry.parentPath != null) ?
                    entry.parentPath.getChildPath(cellName) : new CellPath(cellName);

                // finally, add entries for any children of this cell
                for (CellID childID : get.getChildren()) {
                    cells.add(new CellExportEntry(childID, thisPath));
                }

                // success -- add to the output
                out.put(entry.cellID, new CellExportResultImpl());
            }

            // notify the listener
            NotifyCellExportListener notify =
                    new NotifyCellExportListener(listenerID, out);
            try {
                transactionScheduler.runTask(notify, taskOwner);
            } catch (Exception ex) {
                logger.logThrow(Level.WARNING, ex, "Error calling listener");
            }
        }
    }

    /**
     * A task that requests the list of recordings, and then notifies the
     * notify list recordings listener identified by managed reference id.
     */
    private class ListRecordings implements Runnable {

        MessageID messageID;
        WonderlandClientSender sender;
        private BigInteger clientIDWrapperID;
        private BigInteger listenerID;

        private ListRecordings(MessageID messageID, WonderlandClientSender sender, BigInteger clientIDWrapperID, BigInteger id) {
            this.messageID = messageID;
            this.sender = sender;
            this.clientIDWrapperID = clientIDWrapperID;
            this.listenerID = id;
        }

        public void run() {
            WFSRecordingList recordings = null;
            Exception ex = null;

            try {
                recordings = CellExporterUtils.getWFSRecordings();
            } catch (Exception ex2) {
                ex = ex2;
            }

            // notify the listener
            NotifyListRecordingsListener notify =
                    new NotifyListRecordingsListener(listenerID, messageID, sender, clientIDWrapperID, recordings, ex);
            try {
                transactionScheduler.runTask(notify, taskOwner);
            } catch (Exception ex2) {
                logger.logThrow(Level.WARNING, ex2, "Error calling listener");
            }
        }
    }

    /**
     * The result of exporting a cell
     */
    class CellExportResultImpl implements CellExportResult {
        private boolean success;
        private String failureReason;
        private Throwable failureCause;

        public CellExportResultImpl() {
            this.success = true;
        }

        public CellExportResultImpl(String failureReason,
                Throwable failureCause)
        {
            this.success = false;
            this.failureReason = failureReason;
            this.failureCause = failureCause;
        }


        public boolean isSuccess() {
            return success;
        }

        public String getFailureReason() {
            return failureReason;
        }

        public Throwable getFailureCause() {
            return failureCause;
        }
    }

    /**
     * A task to resolve a CellID into a CellDescriptor.  This task also
     * gets the list of children for the given cell.
     */
    private class GetCellDescriptor implements KernelRunnable {
        private CellID cellID;
        private WorldRoot root;
        private CellPath parentPath;
        private boolean recordCellIDs;

        private CellDescriptor out;
        private Collection<CellID> children = new LinkedList<CellID>();

        public GetCellDescriptor(CellID cellID, WorldRoot root,
                                 CellPath parentPath, boolean recordCellIDs)
        {
            this.cellID = cellID;
            this.root = root;
            this.parentPath = parentPath;
            this.recordCellIDs = recordCellIDs;
        }

        public String getBaseTaskType() {
            return NAME + ".GET_CELL_DESCRIPTOR";
        }

        public CellDescriptor getCellDescriptor() {
            return out;
        }

        public Collection<CellID> getChildren() {
            return children;
        }

        public void run() throws Exception {
            // resolve the cell ID into a cell
            CellMO cell = CellManagerMO.getCell(cellID);
            if (cell == null) {
                throw new IllegalArgumentException("No such cell " + cellID);
            }
            
            // now create a cell descriptor for the cell
            out = CellExporterUtils.getCellDescriptor(root, parentPath, cell, recordCellIDs);

            // if the output is null, it means the cell doesn't implement
            // the getCellServerState() method.  That's fine, just ignore any
            // children
            if (out != null) {
                // finally, get the list of all the cell's children
                // XXX TODO: can we do this without dereferencing each child? XXX
                for (ManagedReference<CellMO> childRef : cell.getAllChildrenRefs()) {
                    children.add(childRef.get().getCellID());
                }
            }
        }
    }

    /** An entry holding details about a cell to export */
    class CellExportEntry {
        CellID cellID;
        CellPath parentPath;

        public CellExportEntry(CellID cellID, CellPath parentPath) {
            this.cellID = cellID;
            this.parentPath = parentPath;
        }
    }

    /**
     * A task to notify a SnapshotCreationListener
     */
    private class NotifySnapshotListener implements KernelRunnable {
        private BigInteger listenerID;
        private WorldRoot root;
        private Exception ex;

        public NotifySnapshotListener(BigInteger listenerID, WorldRoot root,
                                      Exception ex)
        {
            this.listenerID = listenerID;
            this.root = root;
            this.ex = ex;
        }

        public String getBaseTaskType() {
            return NAME + ".SNAPSHOT_LISTENER";
        }

        public void run() throws Exception {
            ManagedReference<?> lr =
                    dataService.createReferenceForId(listenerID);
            SnapshotCreationListener l =
                    (SnapshotCreationListener) lr.get();

            try {
                if (ex == null) {
                    l.snapshotCreated(root);
                } else {
                    l.snapshotFailed(ex.getMessage(), ex);
                }
            } finally {
                // clean up
                if (l instanceof ManagedSnapshotCreationWrapper) {
                    dataService.removeObject(l);
                }
            }
        }
    }

    /**
     * A task to notify a RecordingCreationListener
     */
    private class NotifyRecordingListener implements KernelRunnable {
        private BigInteger listenerID;
        private WorldRoot root;
        private Set<CellID> cells;
        private Exception ex;

        public NotifyRecordingListener(BigInteger listenerID, WorldRoot root, Set<CellID> cells,
                                      Exception ex)
        {
            this.listenerID = listenerID;
            this.root = root;
            this.cells = cells;
            this.ex = ex;
        }

        public String getBaseTaskType() {
            return NAME + ".RECORDING_LISTENER";
        }

        public void run() throws Exception {
            ManagedReference<?> lr =
                    dataService.createReferenceForId(listenerID);
            RecordingCreationListener l =
                    (RecordingCreationListener) lr.get();

            try {
                if (ex == null) {
                    l.recordingCreated(root, cells);
                } else {
                    l.recordingFailed(ex.getMessage(), ex);
                }
            } finally {
                // clean up
                if (l instanceof ManagedRecordingCreationWrapper) {
                    dataService.removeObject(l);
                }
            }
        }
    }

    /**
     * A task to notify a CellExportListener
     */
    private class NotifyCellExportListener implements KernelRunnable {
        private BigInteger listenerID;
        private Map<CellID, CellExportResult> results;

        public NotifyCellExportListener(BigInteger listenerID,
                Map<CellID, CellExportResult> results)
        {
            this.listenerID = listenerID;
            this.results = results;
        }

        public String getBaseTaskType() {
            return NAME + ".CELL_EXPORT_LISTENER";
        }

        public void run() throws Exception {
            ManagedReference<?> lr =
                    dataService.createReferenceForId(listenerID);
            CellExportListener l =
                    (CellExportListener) lr.get();

            try {
                l.exportResult(results);
            } finally {
                // clean up
                if (l instanceof ManagedCellExportWrapper) {
                    dataService.removeObject(l);
                }
            }
        }
    }

    /**
     * A task to notify a ListRecordingsListener
     */
    private class NotifyListRecordingsListener implements KernelRunnable {
        private BigInteger listenerID;
        private WFSRecordingList recordings;
        private MessageID messageID;
        private WonderlandClientSender sender;
        private BigInteger clientIDWrapperID;
        private Exception ex;

        public NotifyListRecordingsListener(BigInteger listenerID, MessageID messageID, WonderlandClientSender sender, BigInteger clientIDWrapperID, WFSRecordingList recordings, Exception ex) {
            this.listenerID = listenerID;
            this.recordings = recordings;
            this.messageID = messageID;
            this.sender = sender;
            this.clientIDWrapperID = clientIDWrapperID;
            this.ex = ex;
        }



        public String getBaseTaskType() {
            return NAME + ".LIST_RECORDINGS_LISTENER";
        }

        public void run() throws Exception {
            //Get the listener
            ManagedReference<?> lr =
                    dataService.createReferenceForId(listenerID);
            ListRecordingsListener l =
                    (ListRecordingsListener) lr.get();

            //Get the wonderlandClientID
            ManagedReference<?> cr =
                    dataService.createReferenceForId(clientIDWrapperID);
            ManagedSerializable<WonderlandClientID> managedClientID =
                    (ManagedSerializable<WonderlandClientID>) cr.get();
            WonderlandClientID clientID = managedClientID.get();

            try {
                if (ex == null) {
                    l.listRecordingsResult(messageID, sender, clientID, recordings);
                } else {
                    l.listRecordingsFailed(messageID, sender, clientID, ex.getMessage(), ex);
                }
            } finally {
                // clean up
                if (l instanceof ManagedRecordingsListenerWrapper) {
                    dataService.removeObject(l);
                }
            }
        }
    }

    /**
     * A wrapper around the SnapshotCreationListener as a managed object.
     * This assumes a serializable SnapshotCreationListener
     */
    private static class ManagedSnapshotCreationWrapper
            implements SnapshotCreationListener, ManagedObject, Serializable
    {
        private SnapshotCreationListener wrapped;

        public ManagedSnapshotCreationWrapper(SnapshotCreationListener listener)
        {
            wrapped = listener;
        }

        public void snapshotCreated(WorldRoot worldRoot) {
            wrapped.snapshotCreated(worldRoot);
        }

        public void snapshotFailed(String reason, Throwable cause) {
            wrapped.snapshotFailed(reason, cause);
        }
    }

    /**
     * A wrapper around the RecordingCreationListener as a managed object.
     * This assumes a serializable RecordingCreationListener
     */
    private static class ManagedRecordingCreationWrapper
            implements RecordingCreationListener, ManagedObject, Serializable
    {
        private RecordingCreationListener wrapped;

        public ManagedRecordingCreationWrapper(RecordingCreationListener listener)
        {
            wrapped = listener;
        }

        public void recordingFailed(String reason, Throwable cause) {
            wrapped.recordingFailed(reason, cause);
        }

        public void recordingCreated(WorldRoot worldRoot, Set<CellID> cells) {
            wrapped.recordingCreated(worldRoot, cells);
        }
    }

    /**
     * A wrapper around the SnapshotCreationListener as a managed object.
     * This assumes a serializable SnapshotCreationListener
     */
    private static class ManagedCellExportWrapper
            implements CellExportListener, ManagedObject, Serializable
    {
        private CellExportListener wrapped;

        public ManagedCellExportWrapper(CellExportListener listener)
        {
            wrapped = listener;
        }

        public void exportResult(Map<CellID, CellExportResult> results) {
            wrapped.exportResult(results);
        }
    }

    /**
     * A wrapper around the ListRecordingsListener as a managed object.
     * This assumes a serializable ListRecordingsListener
     */
    private static class ManagedRecordingsListenerWrapper
            implements ListRecordingsListener, ManagedObject, Serializable
    {
        private ListRecordingsListener wrapped;

        public ManagedRecordingsListenerWrapper(ListRecordingsListener listener)
        {
            wrapped = listener;
        }

        public void listRecordingsResult(MessageID messageID, WonderlandClientSender sender, WonderlandClientID clientID, WFSRecordingList recordings) {
            wrapped.listRecordingsResult(messageID, sender, clientID, recordings);
        }

        public void listRecordingsFailed(MessageID messageID, WonderlandClientSender sender, WonderlandClientID clientID, String message, Exception ex) {
            wrapped.listRecordingsFailed(messageID, sender, clientID, message, ex);
        }


    }


    /**
     * Transaction state
     */
    private class CellExportTransactionContext extends TransactionContext {
        List<Runnable> changes;

        public CellExportTransactionContext(Transaction txn) {
            super (txn);

            changes = new LinkedList<Runnable>();
        }

        public void add(Runnable change) {
            changes.add(change);
        }

        @Override
        public void abort(boolean retryable) {
            changes.clear();
        }

        @Override
        public void commit() {
            for (Runnable r : changes) {
                executor.submit(r);
            }

            changes.clear();
            isCommitted = true;
        }
    }

    /** Private implementation of {@code TransactionContextFactory}. */
    private class TransactionContextFactoryImpl
            extends TransactionContextFactory<CellExportTransactionContext> {

        /** Creates an instance with the given proxy. */
        TransactionContextFactoryImpl(TransactionProxy proxy) {
            super(proxy, NAME);

        }

        /** {@inheritDoc} */
        protected CellExportTransactionContext createContext(Transaction txn) {
            return new CellExportTransactionContext(txn);
        }
    }
}
