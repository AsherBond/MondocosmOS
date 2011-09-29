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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;
import org.jdesktop.wonderland.client.content.spi.ContentBrowserSPI;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepository;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepositoryRegistry;
import org.jdesktop.wonderland.modules.contentrepo.client.ui.AsynchronousJTable.AsyncTableSelectionListener;
import org.jdesktop.wonderland.modules.contentrepo.client.ui.AsynchronousJTree.AsyncTreeSelectionListener;
import org.jdesktop.wonderland.modules.contentrepo.client.ui.modules.ModuleRootContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;

/**
 * A generic browser for webdav content repositories. Supports the
 * ContentBrowserSPI interface so that it can be plugged into the browser
 * registry mechanism.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ContentBrowserJDialog
        extends JDialog implements ContentBrowserSPI {

    private static final Logger logger =
            Logger.getLogger(ContentBrowserJDialog.class.getName());

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/contentrepo/client/ui/resources/Bundle");

    private AsynchronousJTree jtree = null;
    private AsynchronousJTable jtable = null;
    private ContentNode treeSelectedNode = null;
    private ContentNode tableSelectedNode = null;
    private String homePath = null;
    private Map<ContentCollection, NodeURIFactory> factoryMap = null;
    private Set<ContentBrowserListener> listenerSet = null;

    /** Creates new form BrowserFrame */
    public ContentBrowserJDialog(ServerSessionManager session) {
        factoryMap = new HashMap();
        listenerSet = Collections.synchronizedSet(new HashSet());
        initComponents();

        // Create a new tree to display the hierarchy of repositories in a
        // scroll pane. We use AsynchronousJTree so that tree nodes are loaded
        // asynchronously in case a network call is required to load the node's
        // children.
        jtree = new AsynchronousJTree();
        JScrollPane treeScrollPane = new JScrollPane();
        treeScrollPane.setViewportView(jtree);
        treePanel.add(treeScrollPane);

        // Add roots in the JTree for the System area and Users area in the
        // default content repository.
        ContentRepositoryRegistry registry =
                ContentRepositoryRegistry.getInstance();
        ContentRepository repo = registry.getRepository(session);
        try {
            ContentCollection sysCollection = repo.getSystemRoot();
            ContentCollection userCollection =
                    (ContentCollection) repo.getRoot().getChild("users");
            jtree.addTreeRoot(BUNDLE.getString("System"), sysCollection);
            jtree.addTreeRoot(BUNDLE.getString("Users"), userCollection);
            factoryMap.put(sysCollection, new ContentRepoNodeURIFactory());
            factoryMap.put(userCollection, new ContentRepoNodeURIFactory());
        } catch (ContentRepositoryException excp) {
            logger.log(Level.WARNING, "Unable to create roots", excp);
        }

        // Formulate the "home" path as /Users/<login name>
        try {
            homePath = "/Users/" + repo.getUserRoot().getName();
        } catch (ContentRepositoryException excp) {
            logger.log(Level.WARNING, "Unable to find user's home", excp);
        }

        // Add the Module tree root, using a wrapper for the content repo to
        // present the modules properly
        try {
            ContentCollection moduleCollection =
                    new ModuleRootContentCollection(repo);
            jtree.addTreeRoot(BUNDLE.getString("Modules"), moduleCollection);
            factoryMap.put(moduleCollection, new ModuleNodeURIFactory());
        } catch (ContentRepositoryException excp) {
            logger.log(Level.WARNING, "Unable to create module root", excp);
        }

        // Create a new table to display a particular directory. We use an
        // AsynchronousJTable so that the entries are loaded asychronously in
        // case a network call is required to load the children.
        jtable = new AsynchronousJTable();
        JScrollPane tableScrollPane = new JScrollPane();
        tableScrollPane.setViewportView(jtable);
        tablePanel.add(tableScrollPane);

        // Listen for selections on the tree and update the right-hand table
        // with the children for the currently selected node.
        jtree.addAsyncTreeSelectionListener(new AsyncTreeSelectionListener() {
            public void treeSelectionChanged(ContentNode node) {
                treeSelectedNode = node;
                if (node == null) {
                    jtable.setContentCollection(null);
                } else if (node instanceof ContentCollection) {
                    jtable.setContentCollection((ContentCollection) node);
                }
            }
        });

        // Listen for when a new directory/file is selected in the list of
        // files in a content repository. Update the state of the buttons
        jtable.addAsyncTableSelectionListener(new AsyncTableSelectionListener() {

            public void tableSelectionChanged(
                    ContentNode node, boolean changeTo) {
                // If we select a node that isn't been selected then update
                // the table with the new selection.
                if (tableSelectedNode != node) {
                    tableSelectedNode = node;
                    changeTableSelection(node);
                }

                // If this selection was really a double-click to open a
                // directory, then update the tree selection too. We know that
                // the parent of the selected node in the table is the currently
                // selected node in the tree. We use this to form the path to
                // the newly selected node to expand
                if (changeTo == true) {
                    jtree.expandAndSelectChild(node.getName());
                }
            }
        });

        // When the Cancel button is pressed, fire off events to the listeners
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
                for (ContentBrowserListener l : listenerSet) {
                    l.cancelAction();
                }
            }
        });

        // When the Ok button is pressed, fire off events to the listeners,
        // passing them the URI of the selected item
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();

                for (ContentBrowserListener l : listenerSet) {
                    // We need to get the URI of the selection. We first need a
                    // factory to generate the URI for us, based upon the root
                    // of the selected node. We ask the JTree for this.
                    ContentCollection c = jtree.getSelectedRootCollection();
                    if (c == null) {
                        logger.warning("Unable to find selected root");
                        l.cancelAction();
                    }

                    NodeURIFactory factory = factoryMap.get(c);
                    if (factory == null) {
                        logger.warning("Unable to find factory for root");
                        l.cancelAction();
                    }

                    String uri = factory.getURI(tableSelectedNode);
                    l.okAction(uri);
                }
            }
        });

        // When the window becomes visible, then display the Home directory
        // by default
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                homeButton.doClick();
            }
        });
    }

    /**
     * @inheritDoc()
     */
    @Override
    public void setVisible(boolean visible) {
        // Set the dialog visible, but also set the current directory to the
        // user root
        setSize(600, 500);
        super.setVisible(visible);
    }

    /**
     * @inheritDoc()
     */
    public void setActionName(BrowserAction action, String name) {
        switch (action) {
            case OK_ACTION:
                okButton.setText(name);
                break;

            case CANCEL_ACTION:
                cancelButton.setText(name);
                break;
        }
    }

    /**
     * @inheritDoc()
     */
    public void addContentBrowserListener(ContentBrowserListener listener) {
        listenerSet.add(listener);
    }

    /**
     * @inheritDoc()
     */
    public void removeContentBrowserListener(ContentBrowserListener listener) {
        listenerSet.remove(listener);
    }

    /**
     * Handles when a new selection is made in the table, update the state
     * of buttons and labels, given the currently selected item.
     */
    private void changeTableSelection(ContentNode node) {
        // If the selection is cleared ('entry' is null) then disable the
        // proper buttons and return.
        if (node == null) {
            downloadButton.setEnabled(false);
            deleteCollectionButton.setEnabled(false);
            okButton.setEnabled(false);
            return;
        }

        // If the selection is a directory then enable the delete, but not the
        // download.
        if (node instanceof ContentCollection) {
            downloadButton.setEnabled(false);
            deleteCollectionButton.setEnabled(true);
            okButton.setEnabled(false);
        } else {
            downloadButton.setEnabled(true);
            deleteCollectionButton.setEnabled(true);
            okButton.setEnabled(true);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        topPanel = new javax.swing.JPanel();
        topButtonPanel = new javax.swing.JPanel();
        homeButton = new javax.swing.JButton();
        newCollectionButton = new javax.swing.JButton();
        deleteCollectionButton = new javax.swing.JButton();
        uploadButton = new javax.swing.JButton();
        downloadButton = new javax.swing.JButton();
        bottomPanel = new javax.swing.JPanel();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        centerPanel = new javax.swing.JPanel();
        mainSplitPane = new javax.swing.JSplitPane();
        treePanel = new javax.swing.JPanel();
        tablePanel = new javax.swing.JPanel();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/contentrepo/client/ui/resources/Bundle"); // NOI18N
        setTitle(bundle.getString("ContentBrowserJDialog.title")); // NOI18N

        topPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        homeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/contentrepo/client/ui/resources/ContentBrowserHome32x32.png"))); // NOI18N
        homeButton.setToolTipText(bundle.getString("ContentBrowserJDialog.homeButton.toolTipText")); // NOI18N
        homeButton.setMaximumSize(new java.awt.Dimension(32, 32));
        homeButton.setMinimumSize(new java.awt.Dimension(32, 32));
        homeButton.setPreferredSize(new java.awt.Dimension(32, 32));
        homeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homeButtonActionPerformed(evt);
            }
        });
        topButtonPanel.add(homeButton);

        newCollectionButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/contentrepo/client/ui/resources/ContentBrowserNewDirectory32x32.png"))); // NOI18N
        newCollectionButton.setToolTipText(bundle.getString("ContentBrowserJDialog.newCollectionButton.toolTipText")); // NOI18N
        newCollectionButton.setMaximumSize(new java.awt.Dimension(32, 32));
        newCollectionButton.setMinimumSize(new java.awt.Dimension(32, 32));
        newCollectionButton.setPreferredSize(new java.awt.Dimension(32, 32));
        newCollectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newCollectionButtonActionPerformed(evt);
            }
        });
        topButtonPanel.add(newCollectionButton);

        deleteCollectionButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/contentrepo/client/ui/resources/ContentBrowserDeleteFile32x32.png"))); // NOI18N
        deleteCollectionButton.setToolTipText(bundle.getString("ContentBrowserJDialog.deleteCollectionButton.toolTipText")); // NOI18N
        deleteCollectionButton.setEnabled(false);
        deleteCollectionButton.setMaximumSize(new java.awt.Dimension(32, 32));
        deleteCollectionButton.setMinimumSize(new java.awt.Dimension(32, 32));
        deleteCollectionButton.setPreferredSize(new java.awt.Dimension(32, 32));
        deleteCollectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteCollectionButtonActionPerformed(evt);
            }
        });
        topButtonPanel.add(deleteCollectionButton);

        uploadButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/contentrepo/client/ui/resources/ContentBrowserUploadFile32x32.png"))); // NOI18N
        uploadButton.setToolTipText(bundle.getString("ContentBrowserJDialog.uploadButton.toolTipText")); // NOI18N
        uploadButton.setMaximumSize(new java.awt.Dimension(32, 32));
        uploadButton.setMinimumSize(new java.awt.Dimension(32, 32));
        uploadButton.setPreferredSize(new java.awt.Dimension(32, 32));
        uploadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uploadButtonActionPerformed(evt);
            }
        });
        topButtonPanel.add(uploadButton);

        downloadButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/contentrepo/client/ui/resources/ContentBrowserDownloadFile32x32.png"))); // NOI18N
        downloadButton.setToolTipText(bundle.getString("ContentBrowserJDialog.downloadButton.toolTipText")); // NOI18N
        downloadButton.setEnabled(false);
        downloadButton.setMaximumSize(new java.awt.Dimension(32, 32));
        downloadButton.setMinimumSize(new java.awt.Dimension(32, 32));
        downloadButton.setPreferredSize(new java.awt.Dimension(32, 32));
        downloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadButtonActionPerformed(evt);
            }
        });
        topButtonPanel.add(downloadButton);

        topPanel.add(topButtonPanel);

        getContentPane().add(topPanel, java.awt.BorderLayout.NORTH);

        bottomPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 3, 5));

        cancelButton.setText(bundle.getString("ContentBrowserJDialog.cancelButton.text")); // NOI18N
        bottomPanel.add(cancelButton);

        okButton.setText(bundle.getString("ContentBrowserJDialog.okButton.text")); // NOI18N
        okButton.setEnabled(false);
        bottomPanel.add(okButton);

        getContentPane().add(bottomPanel, java.awt.BorderLayout.SOUTH);

        centerPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 10, 0, 10));
        centerPanel.setLayout(new java.awt.GridBagLayout());

        mainSplitPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        mainSplitPane.setDividerLocation(200);
        mainSplitPane.setDividerSize(7);

        treePanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        treePanel.setLayout(new java.awt.GridLayout(1, 0));
        mainSplitPane.setLeftComponent(treePanel);

        tablePanel.setLayout(new java.awt.GridLayout(1, 0));
        mainSplitPane.setRightComponent(tablePanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        centerPanel.add(mainSplitPane, gridBagConstraints);

        getContentPane().add(centerPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void downloadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadButtonActionPerformed

        // Display a file choose and select a directory in which to save the
        // content
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }

            @Override
            public String getDescription() {
                return BUNDLE.getString("Directories");
            }
        });
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);

        // Download the actual file
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String fileName = tableSelectedNode.getName();
            File out = new File(chooser.getSelectedFile(), fileName);
            try {
                ContentResource r = (ContentResource) tableSelectedNode;
                r.get(out);
            } catch (java.lang.Exception cre) {
                logger.log(Level.WARNING,
                        "Unable to download " + fileName, cre);

                // Display a dialog indicating that the delete failed.
                String msg = "Failed to download " + fileName + ". Please " +
                        "check your client logs for further details.";
                String title = "Download Failed";
                JOptionPane.showMessageDialog(
                        this, msg, title, JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_downloadButtonActionPerformed

    private void deleteCollectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteCollectionButtonActionPerformed

        try {
            ContentCollection parent = tableSelectedNode.getParent();
            parent.removeChild(tableSelectedNode.getName());
            jtable.setContentCollection(parent);
            jtree.refresh();
        } catch (java.lang.Exception excp) {
            String nodeName = tableSelectedNode.getName();
            logger.log(Level.WARNING, "Unable to delete " + nodeName, excp);

            // Display a dialog indicating that the delete failed.
            String msg = "Failed to delete " + nodeName + ". Please check " +
                    "your client logs for further details.";
            String title = "Deletion Failed";
            JOptionPane.showMessageDialog(
                    this, msg, title, JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_deleteCollectionButtonActionPerformed

    private void uploadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uploadButtonActionPerformed
        // Show a file chooser that queries for the file to upload.
        JFileChooser chooser = new JFileChooser();
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        // Fetch the file that was selected, make sure it exists, create an
        // entry in the content repository and upload the file.
        File file = chooser.getSelectedFile();
        String name = file.getName();
        if (file.exists() == true) {
            try {
                ContentCollection c = (ContentCollection) treeSelectedNode;
                ContentResource r = (ContentResource) c.createChild(
                        name, ContentNode.Type.RESOURCE);
                r.put(file);
                jtable.setContentCollection(c);
            } catch (java.lang.Exception excp) {
                logger.log(Level.WARNING, "Unable to upload " + file, excp);

                // Display a dialog indicating that the delete failed.
                String msg = "Failed to upload " + file + ". Please check " +
                        "your client logs for further details.";
                String title = "Upload Failed";
                JOptionPane.showMessageDialog(
                        this, msg, title, JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_uploadButtonActionPerformed

    private void newCollectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newCollectionButtonActionPerformed

        // Display a dialog that queries for the next directory name.
        String s = (String) JOptionPane.showInputDialog(this,
                BUNDLE.getString("New_Directory_Message"),
                BUNDLE.getString("Create_New_Directory"),
                JOptionPane.QUESTION_MESSAGE);

        // XXX Probably should check if it already exists.
        if (s == null) {
            return;
        }

        // Go ahead and create the new directory in the content repository
        String name = s.trim();
        try {
            ContentCollection collection = (ContentCollection) treeSelectedNode;
            collection.createChild(name, ContentNode.Type.COLLECTION);
            jtree.refresh();
            jtable.setContentCollection(collection);
        } catch (ContentRepositoryException ex) {
            logger.log(Level.WARNING, "Unable to create directory " + name, ex);

            // Display a dialog indicating that the delete failed.
            String msg = "Failed to create " + name + ". Please check your " +
                    "client logs for further details.";
            String title = "Creation Failed";
            JOptionPane.showMessageDialog(
                    this, msg, title, JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_newCollectionButtonActionPerformed

    private void homeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homeButtonActionPerformed
        jtree.expandAndSelectPath(homePath);
}//GEN-LAST:event_homeButtonActionPerformed

    /**
     * An interface to represent a factory that generates URIs given the
     * ContentNode
     */
    public interface NodeURIFactory {

        /**
         * Returns a String URI given a content node
         */
        public String getURI(ContentNode node);
    }

    /**
     * An implementation of the NodeURIFactory for the system and user's
     * content repository
     */
    private class ContentRepoNodeURIFactory implements NodeURIFactory {

        public String getURI(ContentNode node) {
            String assetPath = node.getPath();

            // The value returned from getPath() starts with a beginning
            // slash, so strip it if so
            if (assetPath.startsWith("/") == true) {
                assetPath = assetPath.substring(1);
            }
            return "wlcontent://" + assetPath;
        }
    }

    /**
     * An implementation of the NodeURIFactory for the module content
     * repository
     */
    private class ModuleNodeURIFactory implements NodeURIFactory {

        public String getURI(ContentNode node) {
            String assetPath = node.getPath();

            // The value returned from getPath() starts with a beginning
            // slash, so strip it if so
            if (assetPath.startsWith("/") == true) {
                assetPath = assetPath.substring(1);
            }

            // If the value returned from getPath() also has a "modules" then
            // strip it out too.
            if (assetPath.startsWith("modules/") == true) {
                assetPath = assetPath.substring("modules/".length());
            }
            return "wla://" + assetPath;
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JButton deleteCollectionButton;
    private javax.swing.JButton downloadButton;
    private javax.swing.JButton homeButton;
    private javax.swing.JSplitPane mainSplitPane;
    private javax.swing.JButton newCollectionButton;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel tablePanel;
    private javax.swing.JPanel topButtonPanel;
    private javax.swing.JPanel topPanel;
    private javax.swing.JPanel treePanel;
    private javax.swing.JButton uploadButton;
    // End of variables declaration//GEN-END:variables
}
