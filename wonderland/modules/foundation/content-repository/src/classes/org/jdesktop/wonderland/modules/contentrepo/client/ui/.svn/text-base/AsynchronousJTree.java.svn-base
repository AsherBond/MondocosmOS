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

/*
 * This code is based upon DynamicTree.java by Joseph Bowbeer and placed in the
 * public domain. The original copyright message was as follows:
 *
 * Written by Joseph Bowbeer and released to the public domain, as explained at
 * http://creativecommons.org/licenses/publicdomain.
 *
 * See: http://java.sun.com/products/jfc/tsc/articles/threads/threads3.html
 */
package org.jdesktop.wonderland.modules.contentrepo.client.ui;

import java.awt.Cursor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import org.jdesktop.swingworker.SwingWorker;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;

/**
 * A JTree that implements support for dynamically and asynchronously loading
 * the tree hierarchy. Also supports pluggable "roots" of the JTree.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class AsynchronousJTree extends javax.swing.JTree {

    private Logger logger = Logger.getLogger(AsynchronousJTree.class.getName());
    private AsyncTreeNode rootNode = null;
    private Set<AsyncTreeSelectionListener> listenerSet = null;
    private Map<AsyncTreeNode, SwingWorker<List<ContentNode>, Void>> workerMap = null;

    /** Initializes the form and sets a default source. */
    public AsynchronousJTree() {
        super();
        listenerSet = new HashSet();
        workerMap = new HashMap();

        // Create the JTree model and the root node. We don't want to show
        // the root node
        rootNode = new AsyncTreeNode("Root");
        DefaultTreeModel model = (DefaultTreeModel)getModel();
        model.setRoot(rootNode);
        setRootVisible(false);

        // Listen for when the tree is expanded and collapsed and dispatch to
        // the proper handler.
        addTreeExpansionListener(new TreeExpansionListener() {
            public void treeExpanded(TreeExpansionEvent evt) {
                jTreeTreeExpanded(evt);
            }
            public void treeCollapsed(TreeExpansionEvent evt) {
                jTreeTreeCollapsed(evt);
            }
        });

        // Listen for when nodes in the tree are selected and dispatch to the
        // handler that notifies listeners registered on this class.
        addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent evt) {
                fireTreeSelectionChanged(evt);
            }
        });
        
        /*
         * Since nodes are added dynamically in this application, the only true
         * leaf nodes are nodes that don't allow children to be added. (By
         * default, askAllowsChildren is false and all nodes without children
         * are considered to be leaves.)
         *
         * But there's a complication: when the tree structure changes, JTree
         * pre-expands the root node unless it's a leaf. To avoid having the
         * root pre-expanded, we set askAllowsChildren *after* assigning the
         * new root.
         */
        model.setAsksAllowsChildren(true);
    }

    /**
     * Adds a new dynamic root to this tree. Multiple entries for the same
     * object are permitted.
     *
     * @param collection The ContentCollection representing the tree root
     */
    public void addTreeRoot(String displayName, ContentCollection collection) {
        // Create a new tree node, wrapping the given content collection in the
        // holder object
        TreeNodeHolder holder = new TreeNodeHolder(displayName, collection);
        final AsyncTreeNode node = new AsyncTreeNode(holder, true);

        // Add to the tree in a swing worker thread. We also update the map
        // which we always do in the AWT Event Thread.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                rootNode.insert(node, rootNode.getChildCount());
                ((DefaultTreeModel)getModel()).nodeStructureChanged(rootNode);
            }
        });
    }

    /**
     * Removes the given dynamic root from this tree. If multiple entries for
     * this tree root exist, this method removes the first found. If the tree
     * root does not exist, this method does nothing.
     *
     * @param displayName The display name to remove
     */
    public void removeTreeRoot(final String displayName) {
        // Search through the list of roots to find this one and remove it
        // from the tree and the map. We do this in the AWT Event Thread to
        // synchronize access
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // We do a brute-force search throug the entire list
                int size = rootNode.getChildCount();
                for (int i = 0; i < size; i++) {
                    AsyncTreeNode node = (AsyncTreeNode) rootNode.getChildAt(i);
                    TreeNodeHolder holder = (TreeNodeHolder)node.getUserObject();
                    if (holder.displayName.equals(displayName) == true) {
                        rootNode.remove(node);
                        ((DefaultTreeModel) getModel()).nodeStructureChanged(rootNode);
                    }
                }
            }
        });
    }

    /**
     * Adds a tree selection listener. If the listener is already present, this
     * method does nothing.
     *
     * @param listener The tree selection listener to add
     */
    public void addAsyncTreeSelectionListener(AsyncTreeSelectionListener listener) {
        synchronized (listenerSet) {
            listenerSet.add(listener);
        }
    }

    /**
     * Removes a tree selection listener. If the listener is not present, this
     * method does nothing.
     *
     * @param listener The tree selection listener to remove
     */
    public void removeAsyncTreeSelectionListener(AsyncTreeSelectionListener listener) {
        synchronized (listenerSet) {
            listenerSet.remove(listener);
        }
    }

    /**
     * Refresh the display of the currently selected tree node.
     */
    public void refresh() {
        // Stop any existing workers and start a new one for the selected node
        // to refresh the display
        TreePath path = getSelectionPath();
        AsyncTreeNode node = (AsyncTreeNode)path.getLastPathComponent();
        stopWorker(node);
        startWorker(node, null);
    }

    /**
     * Expands the currently selected node and select one of its children given
     * the display name of the child.
     *
     * @param childName The name of the child to select
     */
    public void expandAndSelectChild(String childName) {

        // Find the currently selected node, if there is none then just return
        AsyncTreeNode selectedNode = (AsyncTreeNode)getSelectedNode();
        if (selectedNode == null) {
            return;
        }

        // In this case, we always wish to start a new worker in case an old
        // worker exists. We need to kill the old worker to make sure the
        // child gets selected. This assumes we are running in the AWT Event
        // Thread so we do not need to synchronize around the worker map
        stopWorker(selectedNode);

        // Start a new worker to expand the node and select the child name.
        startWorker(selectedNode, childName);
    }

    /**
     * Given a path of the form /a/b/c of the display names of a hierarch of
     * tree nodes from the root, expands the tree to display the final path
     * component and selects that path component.
     *
     * @param path The path to expand and select
     */
    public void expandAndSelectPath(String path) {
        // Remove the leading forward-slash, if present. This prevents us from
        // receiving an empty token in the first element. Split the path into
        // path elements.
        if (path.startsWith("/") == true) {
            path = path.substring(1);
        }
        String pathElements[] = path.split("/");

        // Find the second to last note in the tree. We expand to that node,
        // causing it to load asynchronously and ask that one of its children
        // be selected (the last path element in the path).
        AsyncTreeNode parentNode = rootNode;
        for (int i = 0; i < pathElements.length - 1; i++) {
            // For each path element, look through each of the children and
            // find the name matching the path element.
            for (int j = 0; j < parentNode.getChildCount(); j++) {
                AsyncTreeNode node = (AsyncTreeNode)parentNode.getChildAt(j);
                if (pathElements[i].equals(node.toString()) == true) {
                    parentNode = node;
                    continue;
                }
            }
        }

        // In this case, we always wish to start a new worker in case an old
        // worker exists. We need to kill the old worker to make sure the
        // child gets selected. This assumes we are running in the AWT Event
        // Thread so we do not need to synchronize around the worker map
        stopWorker(parentNode);

        // Start a new worker to display the node and select the child name.
        startWorker(parentNode, pathElements[pathElements.length - 1]);
    }

    /**
     * Returns the content collection for the root of the currently selected
     * node. This walks up the tree to find the root node and takes its
     * content collection.
     */
    public ContentCollection getSelectedRootCollection() {
        // Find the currently selected path (if none, return null). The "root"
        // of the tree is the second path element. So if we have less than two
        // elements, we return null.
        TreePath path = getSelectionPath();
        if (path == null || path.getPath().length < 2) {
            return null;
        }

        // Otherwise, fetch the second element in the path, and find the content
        // collection based from the user data
        AsyncTreeNode treeNode = (AsyncTreeNode)path.getPath()[1];
        TreeNodeHolder holder = (TreeNodeHolder)treeNode.getUserObject();
        return (ContentCollection)holder.contentNode;
    }

    /**
     * Returns the currently-selected tree node, or null if nothing is selected.
     *
     * @return A AsyncTreeNode
     */
    private DefaultMutableTreeNode getSelectedNode() {
        TreePath path = getSelectionPath();
        if (path == null) {
            return null;
        }
        return (DefaultMutableTreeNode)path.getLastPathComponent();
    }

    /**
     * Informs all tree selections listeners that the selection tree node has
     * changed.
     */
    private void fireTreeSelectionChanged(TreeSelectionEvent selectionEvent) {
        // We ignore the selection given in the tree selection event (since it
        // seems to give events for both items now selected and items just
        // deselected. Just fetch the currently selected node from the tree
        // itself
        DefaultMutableTreeNode selectedNode = getSelectedNode();
        ContentNode contentNode = null;
        if (selectedNode != null) {
            TreeNodeHolder holder = (TreeNodeHolder) selectedNode.getUserObject();
            contentNode = holder.contentNode;
        }
        
        // Loop through all of the listener and notify them
        synchronized (listenerSet) {
            for (AsyncTreeSelectionListener listener : listenerSet) {
                listener.treeSelectionChanged(contentNode);
            }
        }
    }

    /**
     * Called when a node is expanded. Stops the active worker, if any, and
     * starts a new worker to create children for the expanded node.
     */
    private void jTreeTreeExpanded(TreeExpansionEvent evt) {

        // Using the last path element, find the tree node and the user object
        // from that. Try to start a worker if one does not already exist.
        TreePath path = evt.getPath();
        AsyncTreeNode node = (AsyncTreeNode)path.getLastPathComponent();
        startWorker(node, null);
    }

    /**
     * Called when a node is collapsed. Stops the active worker, if any, and
     * removes all the children.
     */
    private void jTreeTreeCollapsed(TreeExpansionEvent evt) {

        // Using the last path element, find the tree node and the user object
        // from that. Try to stop a worker if one exists.
        TreePath path = evt.getPath();
        AsyncTreeNode node = (AsyncTreeNode)path.getLastPathComponent();
        stopWorker(node);
    }

    /**
     * Given a tree node, starts a SwingWorker to create children for the
     * expanded node and insert them into the tree. This is called on the AWT
     * Event Thread.
     */
    private void startWorker(AsyncTreeNode node, String selectChild) {

        // Check to see if a worker already exists for the given node. Since
        // this method runs on the AWT Event Thread, we do not need to do a
        // synchronization around the worker Map.
        if (workerMap.containsKey(node) == true) {
            return;
        }

        // Otherwise, create a new swing worker, add it to the map, start it
        // off, and update the cursor
        TreeSwingWorker worker = new TreeSwingWorker(node, selectChild);
        workerMap.put(node, worker);
        worker.execute();
        updateCursor();
    }

    /**
     * Stops the active worker, if any. This is always called on the AWT Event
     * Thread.
     */
    private void stopWorker(AsyncTreeNode node) {
        
        // Check to see if there is an active worker for the give node. Since
        // this is always called on the AWT Event Thread, we do not need to
        // separately synchronize around the Map of workers.
        SwingWorker worker = workerMap.get(node);
        if (worker != null) {
            worker.cancel(true);
            workerMap.remove(node);
            updateCursor();
        }
    }

    /**
     * Updates the cursor depending upon whether the Map of SwingWorkers is
     * empty or not. This should be invokved on the AWT Event Thread
     */
    private void updateCursor() {
        if (workerMap.isEmpty() == true) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        else {
            setCursor(new Cursor(Cursor.WAIT_CURSOR));
        }
    }

    /**
     * A listener to indicate that a Node has been selected on the tree, giving
     * the root and the DirectoryEntry of the node selected.
     */
    public interface AsyncTreeSelectionListener {
        /**
         * Indicates that a Node has been selected on the tree.
         *
         * @param node The selected ContentNode
         */
        public void treeSelectionChanged(ContentNode node);
    }

    /**
     * A holder class used as the 'user object' in a AsyncTreeNode.
     * This holds the display name of the node and the ContentNode to which
     * it is associated.
     */
    private class TreeNodeHolder {

        public String displayName = null;
        public ContentNode contentNode = null;

        public TreeNodeHolder(String displayName, ContentNode contentNode) {
            this.displayName = displayName;
            this.contentNode = contentNode;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    /**
     * A class that wraps AsyncTreeNode to define isEquivalent() to
     * test whether a tree node refers to the same content node.
     */
    private class AsyncTreeNode extends DefaultMutableTreeNode {

        public AsyncTreeNode(Object userObject, boolean allowsChildren) {
            super(userObject, allowsChildren);
        }

        public AsyncTreeNode(Object userObject) {
            super(userObject);
        }

        /**
         * Returns true if this tree node represents the given content node.
         * This only compares the display names of the content node for this
         * node and the given content node.
         *
         * @param contentNode Compare to this content node
         * @return True If this node has the same content node
         */
        public boolean isEquivalent(ContentNode contentNode) {
            TreeNodeHolder holder = (TreeNodeHolder)getUserObject();
            String thisDisplayName = holder.contentNode.getName();
            String otherDisplayName = contentNode.getName();
            return thisDisplayName.equals(otherDisplayName);
        }
    }

    /**
     * A SwingWorker to update the nodes in tree depending upon what is loaded
     * from the content repository
     */
    private class TreeSwingWorker extends SwingWorker<List<ContentNode>, Void> {

        private AsyncTreeNode node = null;
        private String selectChild = null;

        /** Constructor, takes the node to load children for */
        public TreeSwingWorker(AsyncTreeNode node, String selectChild) {
            this.node = node;
            this.selectChild = selectChild;
        }

        /**
         * @inheritDoc()
         */
        @Override
        protected void done() {

            // Fetch the children from the asynchronous worker. Upon an
            // exception, log an error and return
            List<ContentNode> children = null;
            try {
                children = get();
            } catch (java.util.concurrent.CancellationException excp) {
                // Just ignore this exception. This happens when a node is
                // closed before it has time to finish loading. It is a normal
                // condition, not exceptional at all.
                return;
            } catch (java.lang.InterruptedException excp) {
                // event-dispatch thread won't be interrupted
                logger.log(Level.WARNING, "Failed to get children for " +
                        node.toString(), excp);
                throw new IllegalStateException(excp + "");
            } catch (java.lang.Exception excp) {
                logger.log(Level.WARNING, "Failed to get children for " +
                        node.toString(), excp);
                return;
            }

            // See if we find the child we wish to select. If one of the children
            // names matches the given child name, then select the node when
            // all done.
            AsyncTreeNode selectNode = null;

            // Keep a list of the children currently on the node. We will use
            // this list later to remove all the children that are not currently
            // present
            List<AsyncTreeNode> childList = new LinkedList();
            for (int i = 0; i < node.getChildCount(); i++) {
                childList.add((AsyncTreeNode)node.getChildAt(i));
            }
            
            // Loop through each of the (new) children found and add a tree node
            // for each if it already does not exist on the tree. We only add
            // directories in the tree and ignore the individual files.
            for (ContentNode childNode : children) {
                // If not a content collection (directory) then just continue
                if (!(childNode instanceof ContentCollection)) {
                    continue;
                }
                String name = childNode.getName();

                // If it is already present on the tree, then no need to re-add
                // it. We just take it off the existing child list so that it
                // does not get removed later.
                AsyncTreeNode present = isPresent(childList, childNode);
                if (present != null) {
                    // Check to see if the child matches what we wish to selected
                    if (selectChild != null && name.equals(selectChild) == true) {
                        selectNode = present;
                    }

                    // Remove it from the list of children to remove and continue
                    childList.remove(present);
                    continue;
                }

                // Otherwise, create a new node and add it to the tree.
                TreeNodeHolder holder = new TreeNodeHolder(name, childNode);
                AsyncTreeNode newNode = new AsyncTreeNode(holder, true);
                node.insert(newNode, node.getChildCount());

                // Check to see if the child matches what we wish to selected
                if (selectChild != null && name.equals(selectChild) == true) {
                    selectNode = newNode;
                }
            }

            // Remove any of the nodes remaining on the original child list
            for (AsyncTreeNode childNode : childList) {
                node.remove(childNode);
            }

            // Tell the tree model that its structure has changed. This will
            // update the appearance of the tree.
            DefaultTreeModel model = (DefaultTreeModel) getModel();
            model.nodeStructureChanged(node);

            // If we wish to select a node in the tree, then do so
            if (selectNode != null) {
                TreePath path = new TreePath(node.getPath());
                TreePath childPath = path.pathByAddingChild(selectNode);
                setSelectionPath(childPath);
            }

            // Now that we are done we can remove the work from the map and
            // update the cursor
            workerMap.remove(node);
            updateCursor();
        }

        /**
         * @inheritDoc()
         */
        @Override
        protected List<ContentNode> doInBackground() throws Exception {
            // From the node, find the "User Object" and then the content
            // node from the user object
            TreeNodeHolder holder = (TreeNodeHolder) node.getUserObject();
            ContentNode contentNode = (ContentNode) holder.contentNode;
            if (contentNode instanceof ContentCollection) {
                return ((ContentCollection) contentNode).getChildren();
            }
            return new LinkedList();
        }

        /**
         * Returns the AyncTreeNode if the given content node is represented by
         * some tree node on the given list, or null if not.
         */
        private AsyncTreeNode isPresent(List<AsyncTreeNode> childList, ContentNode contentNode) {
            for (AsyncTreeNode treeNode : childList) {
                if (treeNode.isEquivalent(contentNode) == true) {
                    return treeNode;
                }
            }
            return null;
        }
    }
}
