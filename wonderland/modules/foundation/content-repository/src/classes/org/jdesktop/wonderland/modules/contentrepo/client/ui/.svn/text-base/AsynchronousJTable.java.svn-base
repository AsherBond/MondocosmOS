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

package org.jdesktop.wonderland.modules.contentrepo.client.ui;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;

/**
 * A JTable that implements support for dynamically and asynchronously loading
 * the table contents.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class AsynchronousJTable extends javax.swing.JTable {

    private Logger logger = Logger.getLogger(AsynchronousJTable.class.getName());
    private AsyncTableModel tableModel = null;
    private transient SwingWorker<List<ContentNode>, Void> worker = null;
    private Set<AsyncTableSelectionListener> listenerSet = null;

    /** Initializes the form and sets a default source. */
    public AsynchronousJTable() {
        super();
        listenerSet = new HashSet();

        // Set the model on the directory listing table and set up some of its
        // parameters and layout. Set the special renderer for the first column
        // to display a file/directory icon.
        tableModel = new AsyncTableModel();
        setModel(tableModel);
        setCellSelectionEnabled(false);
        setRowSelectionAllowed(true);
        setShowHorizontalLines(false);
        setShowVerticalLines(false);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        TableColumn column = getColumnModel().getColumn(0);
        column.setCellRenderer(new DirectoryTableCellRenderer());
        column.setMaxWidth(32);
        column.setMinWidth(32);

        // Listen for when a new directory/file is selected in the list. Notify
        // all of the listeners in this event.
        getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent e) {
                        if (e.getValueIsAdjusting() == false) {
                            fireTableSelectionChanged(e);
                        }
                    }
                }
        );
        
        // Listen for when there is a double-click on the file list to change
        // to that directory.
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    changeDirectory();
                }
            }
        });
    }

    /**
     * Updates the table with the children from the given content collection.
     *
     * @param collection Display the children of this ContentCollection
     */
    public void setContentCollection(ContentCollection collection) {
        stopWorker();
        startWorker(collection);
    }

    /**
     * Adds a table selection listener. If the listener is already present, this
     * method does nothing.
     *
     * @param listener The table selection listener to add
     */
    public void addAsyncTableSelectionListener(AsyncTableSelectionListener listener) {
        synchronized (listenerSet) {
            listenerSet.add(listener);
        }
    }

    /**
     * Removes a table selection listener. If the listener is not present, this
     * method does nothing.
     *
     * @param listener The table selection listener to remove
     */
    public void removeAsyncTableSelectionListener(AsyncTableSelectionListener listener) {
        synchronized (listenerSet) {
            listenerSet.remove(listener);
        }
    }

    /**
     * Informs all table selections listeners that the selection tree node has
     * changed.
     */
    private void fireTableSelectionChanged(ListSelectionEvent selectionEvent) {
        // Find the currently selected Content Node for the given selection.
        ContentNode node = null;
        int row = getSelectedRow();
        if (row != -1) {
            node = tableModel.getContentNode(row);
        }

        // Loop through all of the listener and notify them
        synchronized (listenerSet) {
            for (AsyncTableSelectionListener listener : listenerSet) {
                listener.tableSelectionChanged(node, false);
            }
        }
    }

    /**
     * Handles when a directory is changed. Opens the selected directory (if it
     * is a directory) and informs listeners
     */
    private void changeDirectory() {
        // Find the currently selected Content Node for the given selection.
        ContentNode node = null;
        int row = getSelectedRow();
        if (row != -1) {
            node = tableModel.getContentNode(row);
        }
        
        // Loop through all of the listener and notify them
        synchronized (listenerSet) {
            for (AsyncTableSelectionListener listener : listenerSet) {
                listener.tableSelectionChanged(node, true);
            }
        }
    }

    /**
     * Asynchronously loads the chidren of the given content collection and
     * updates the table model.
     */
    private void startWorker(final ContentCollection collection) {

        // Create a new SwingWorker to find the children for the given directory
        // and when done, update the list model.
        worker = new SwingWorker<List<ContentNode>, Void>() {
            @Override
            protected void done() {

                // Set the worker to null and stop the animation, but only if we
                // are the active worker.
                if (worker == this) {
                    worker = null;
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }

                // Fetch the children from the asynchronous worker. Upon an
                // exception, log an error and return
                List<ContentNode> children = null;
                try {
                    children = get();
                    tableModel.setNodeList(children);
                } catch (java.util.concurrent.CancellationException excp) {
                    // Just ignore this exception. This happens when a node is
                    // closed before it has time to finish loading. It is a normal
                    // condition, not exceptional at all.
                    return;
                } catch (java.lang.InterruptedException excp) {
                    // event-dispatch thread won't be interrupted
                    logger.log(Level.WARNING, "Failed to get children for " +
                            collection.toString(), excp);
                    throw new IllegalStateException(excp + "");
                } catch (java.lang.Exception excp) {
                    logger.log(Level.WARNING, "Failed to get children for " +
                            collection.toString(), excp);
                    return;
                }
            }

            @Override
            protected List<ContentNode> doInBackground() throws Exception {
                if (collection == null) {
                    return new LinkedList();
                }
                return collection.getChildren();
            }
        };

        // Start worker, update status line, and start animation.
        worker.execute();
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
    }

    /** Stops the active worker, if any. */
    private void stopWorker() {
        if (worker != null) {
            worker.cancel(true);
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            // worker set to null in finished
        }
    }

    /**
     * Listener for when items in the table are selected
     */
    public interface AsyncTableSelectionListener {
        /**
         * Indicates that an entry has been selected on the table. If the
         * selection is cleared, then 'entry' is null.
         *
         * @param node The ContentNode selection, or null if none
         * @param changeTo If true, this directory has been selected and changed
         * to.
         */
        public void tableSelectionChanged(ContentNode node, boolean changeTo);
    }
}
