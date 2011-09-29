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
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.messages.MessageID;
import org.jdesktop.wonderland.common.wfs.WFSRecordingList;
import org.jdesktop.wonderland.common.wfs.WorldRoot;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

/**
 * A service for exporting cells.  This service provides a set of
 * asynchronous mechanisms for creating cell snapshots and recordings, and writing a set
 * of cells to those snapshots and recordings.  Callers will be notified if the
 * export succeeds or fails.
 *
 * @author jkaplan
 * @author Bernard Horan
 */
public interface CellExportManager {
    /**
     * Create a new snapshot for writing cells to.  This method will contact
     * the remote web service to create a new snapshot, and then call the
     * given listener with the result of that call.
     * @param name the name of the snapshot to create, or null to use the
     * default name
     * @param listener a snapshot creation listener that will be notified of
     * the result of this call
     */
    public void createSnapshot(String name, SnapshotCreationListener listener);

    /**
     * Create a new recording for writing cells to.  This method will contact
     * the remote web service to create a new recording, and then call the
     * given listener with the result of that call.
     * @param name the name of the recording to create
     * @param cells the set of cells to record
     * @param listener a recording creation listener that will be notified of
     * the result of this call
     */
    public void createRecording(String name, Set<CellID> cells, RecordingCreationListener listener);



    /**
     * Write a set of cells to the given snapshot/recording.  This method will fetch the
     * given set of cells, and write the contents of the cells and all their
     * children to the remote web service.  Finally, the listener will be
     * notified with the results of the call.
     * @param worldRoot the snapshot/recording to write to
     * @param cellIDs a set of cell IDs to write.  Each cellID will be used
     * as a root for writing, so the entire graph under the given set of
     * cell IDs will be written.  The cellIDs set will be accessed across
     * multiple Darkstar transactions, so it is essential that the iterator
     * for the set be serializable and work correctly in the face of concurrent
     * access.  Typically, a ScalableHashSet is the best choice for the set.
     * @param listener a listener that will be notified of the results
     * @param recordCellIDs if true, record the cellID of each cell
     */
    public void exportCells(WorldRoot worldRoot, Set<CellID> cellIDs,
                            CellExportListener listener, boolean recordCellIDs);

    /**
     * List the recordings that are currently accessible via the web service.
     * This is used to respond to a client's request to open a form for a user to select
     * a "tape".<p>
     * The listener will be notifed with the results of the call. The inclusion of
     * these parameters is because the originating message requires a ResponseMessage return, which in
     * turn requires these parameters.
     * @param messageID the id of the message that originated the request
     * @param sender the sender of the message request
     * @param clientID the wonderlandClientID of the request
     * @param listener a list recordings listener whose method listRecordingsResult is called on success, or listRecordingsFailed on failure.
     */
    public void listRecordings(MessageID messageID, WonderlandClientSender sender, WonderlandClientID clientID, ListRecordingsListener listener);

    /**
     * A listener that will be notified of the success or failure of
     * creating a snapshot.  Implementations of SnapshotCreationListener
     * must be either a ManagedObject or Serializable.
     */
    public interface SnapshotCreationListener {
        /**
         * Notification that a snapshot has been created successfully
         * @param worldRoot the world root that was created
         */
        public void snapshotCreated(WorldRoot worldRoot);

        /**
         * Notification that snapshot creation has failed.
         * @param reason a String describing the reason for failure
         * @param cause an exception that caused the failure.
         */
        public void snapshotFailed(String reason, Throwable cause);

    }

    /**
     * A listener that will be notified of the success or failure of
     * creating a recording.  Implementations of RecordingCreationListener
     * must be either a ManagedObject or Serializable.
     */
    public interface RecordingCreationListener {
        /**
         * Notification that a recording has been created successfully
         * @param worldRoot the world root that was created
         * @param cells the cells to be recorded
         */
        public void recordingCreated(WorldRoot worldRoot, Set<CellID> cells);

        /**
         * Notification that recording creation has failed.
         * @param reason a String describing the reason for failure
         * @param cause an exception that caused the failure.
         */
        public void recordingFailed(String reason, Throwable cause);

    }



    /**
     * A listener that will be notified of the result of exporting a set
     * of cells to a snapshot or a recording.  Implementations of CellExportListener must
     * be either a ManagedObject or Serializable
     */
    public interface CellExportListener {
        /**
         * Notification of the result of cell export
         * @param results a Map from CellIDs in the request to results
         * for the export of that cell.
         */
        public void exportResult(Map<CellID, CellExportResult> results);
    }

    /**
     * The result of exporting a cell
     */
    public interface CellExportResult {
        /**
         * Whether or not the export was successful
         * @return true if the export was successful, or false if not
         */
        public boolean isSuccess();

        /**
         * If the export failed, return the reason
         * @return the reason for failure, or null
         */
        public String getFailureReason();

        /**
         * If the export failed, return the root cause exception
         * @return the root cause of the failure, or null
         */
        public Throwable getFailureCause();
    }

    /**
     * A listener that will be notified of result of requesting a list of
     * the currrent recordings accessible via the web service.
     * Implementations of ListRecordingsListener
     * must be either a ManagedObject or Serializable.
     */
    public interface ListRecordingsListener {

        /**
         * The result of listing the recordings.
         * @param messageID the message id of the originating message
         * @param sender the sender of the originating message
         * @param clientID the wonderland client id of the originating client
         * @param recordings the list of recordings
         */
        public void listRecordingsResult(MessageID messageID, WonderlandClientSender sender, WonderlandClientID clientID, WFSRecordingList recordings);
        
        /**
         * The listings of recordings failed.
         * @param messageID the message id of the originating message
         * @param sender the sender of the originating message
         * @param clientID the wonderland client id of the originating client
         * @param message the message accompanying the exception
         * @param ex the exception that was caught describing the failure
         */
        public void listRecordingsFailed(MessageID messageID, WonderlandClientSender sender, WonderlandClientID clientID, String message, Exception ex);
        
    }



}
