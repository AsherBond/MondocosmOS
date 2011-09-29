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
package org.jdesktop.wonderland.modules.celleditor.client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellCache;
import org.jdesktop.wonderland.client.cell.CellEditChannelConnection;
import org.jdesktop.wonderland.client.cell.CellManager;
import org.jdesktop.wonderland.client.cell.CellStatusChangeListener;
import org.jdesktop.wonderland.client.cell.EnvironmentCell;
import org.jdesktop.wonderland.client.cell.properties.CellPropertiesEditor;
import org.jdesktop.wonderland.client.cell.properties.PropertiesManager;
import org.jdesktop.wonderland.client.cell.properties.spi.PropertiesFactorySPI;
import org.jdesktop.wonderland.client.cell.registry.CellComponentRegistry;
import org.jdesktop.wonderland.client.cell.registry.spi.CellComponentFactorySPI;
import org.jdesktop.wonderland.client.cell.utils.CellUtils;
import org.jdesktop.wonderland.client.cell.view.AvatarCell;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.jme.utils.ScenegraphUtils;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.common.cell.CellEditConnectionType;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.messages.CellReparentMessage;
import org.jdesktop.wonderland.common.cell.messages.CellServerComponentMessage;
import org.jdesktop.wonderland.common.cell.messages.CellServerComponentResponseMessage;
import org.jdesktop.wonderland.common.cell.messages.CellServerStateRequestMessage;
import org.jdesktop.wonderland.common.cell.messages.CellServerStateResponseMessage;
import org.jdesktop.wonderland.common.cell.messages.CellServerStateUpdateMessage;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.cell.state.PositionComponentServerState;
import org.jdesktop.wonderland.common.messages.ErrorMessage;
import org.jdesktop.wonderland.common.messages.OKMessage;
import org.jdesktop.wonderland.common.messages.ResponseMessage;

/**
 * A frame to allow the editing of properties for the cell.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class CellPropertiesJFrame extends JFrame implements CellPropertiesEditor {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/celleditor/client/resources/Bundle");

    private static final Logger LOGGER =
            Logger.getLogger(CellPropertiesJFrame.class.getName());

    private List<PropertiesFactorySPI> factoryList = null;
    private Cell selectedCell = null;
    private CellServerState selectedCellServerState = null;
    private PropertiesFactorySPI cellProperties = null;
    private DefaultListModel listModel = null;
    private SortedTreeNode treeRoot = null;
    private CellStatusChangeListener cellListener = null;
    private TreeSelectionListener treeListener = null;
    private SortedTreeNode dragOverTreeNode = null;
    private Set<Class> dirtyPanelSet = new HashSet();
    private StateUpdates stateUpdates = null;

    // A Map from the Cell to its node in the tree. All access to this Map must
    // happen in the AWT Event Thread to insure synchronized access.
    private Map<Cell, SortedTreeNode> cellNodes = null;

    // The two standard panels for all Cells: Basic and Position
    private PropertiesFactorySPI basicPropertiesFactory = null;
    private PropertiesFactorySPI positionPropertiesFactory = null;

    /** Constructor */
    public CellPropertiesJFrame() {
        factoryList = new LinkedList();
        stateUpdates = new StateUpdates();

        // Initialize the GUI components
        initComponents();

        // Add a list model for the list of capabilities. Also, listen for
        // selections on the list to display the appropriate panel
        listModel = new DefaultListModel();
        capabilityList.setModel(listModel);
        capabilityList.addListSelectionListener(new CapabilityListSelectionListener());

        // Create and add a basic panel for all cells as a special case.
        basicPropertiesFactory = new BasicJPanel();
        basicPropertiesFactory.setCellPropertiesEditor(this);

        // Create the position panel for all cells as a special case.
        positionPropertiesFactory = new PositionJPanel();
        positionPropertiesFactory.setCellPropertiesEditor(this);

        // Set up all of the stuff we need to the tree to display Cells
        treeRoot = new SortedTreeNode(BUNDLE.getString("World_Root"));
        cellNodes = new HashMap();
        DefaultTreeModel treeModel = new DefaultTreeModel(treeRoot);
        cellHierarchyTree.setModel(treeModel);
        cellHierarchyTree.setCellRenderer(new CellTreeRenderer());

        // Create a listener that will listen to the status of Cells. This
        // listener gets added when the dialog is made visible. We need to do
        // all of this in the AWT even thread.
        cellListener = new CellStatusChangeListener() {
            public void cellStatusChanged(final Cell cell, final CellStatus status) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        DefaultMutableTreeNode node = cellNodes.get(cell);
                        if (status == CellStatus.DISK) {
                            // If there is a Node that corresponds to the Cell,
                            // then remove it from the tree.
                            if (node != null) {

                                // We need to handle a special case here: if the
                                // node is currently selected and we have made
                                // edits, the GUI will think the panel is in
                                // the "dirty" state so we need to pretend the
                                // edits have not happened and just delete the
                                // Cell.
                                if (selectedCell == cell) {
                                    dirtyPanelSet.clear();
                                }

                                // Now just go ahead and remove the Cell from
                                // the tree.
                                TreeModel m = cellHierarchyTree.getModel();
                                ((DefaultTreeModel) m).removeNodeFromParent(node);
                                cellNodes.remove(cell);
                            }
                        }
                        else if (status == CellStatus.RENDERING) {
                            // If the node does not exist, then create it and
                            // tell the tree that a node has been inserted. We
                            // fetch the parent node of the newly created node
                            // and tell its parent that its structure has
                            // changed.
                            if (node == null) {
                                createJTreeNode(cell);
                            }
                        }
                    }
                });
            }
        };

        // Listen to selections on the tree and change the selected Cell.
        treeListener = new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                SortedTreeNode selectedNode =
                        (SortedTreeNode) cellHierarchyTree.getLastSelectedPathComponent();
                if (selectedNode != null) {
                    Object userObject = selectedNode.getUserObject();
                    if (userObject instanceof Cell) {
                        setSelectedCell((Cell) userObject);
                    }
                    else {
                        setSelectedCell(null);
                    }
                }
                else {
                    setSelectedCell(null);
                }
            }
        };

        // Install drag and drop on the tree. This will handle when a tree node
        // is dropped on top of another node.
        cellHierarchyTree.setDragEnabled(true);
        DropTarget dt = new DropTarget();
        try {
            dt.addDropTargetListener(new CellDropTargetListener());
        } catch (TooManyListenersException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        cellHierarchyTree.setDropTarget(dt);

        // Listen for when the window is closing. If so, see if there are any
        // changes to the properties and ask if the user wants to apply
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (dirtyPanelSet.isEmpty() == false) {
                    int result = JOptionPane.showConfirmDialog(
                            CellPropertiesJFrame.this,
                            BUNDLE.getString("Apply_Close_Message"),
                            BUNDLE.getString("Apply_Title"),
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);

                    if (result == JOptionPane.YES_OPTION) {
                        applyValues();
                    }
                    else {
                        restoreValues();
                    }
                }

                // Regardless, tell the panels to close themselves
                for (PropertiesFactorySPI factory : factoryList) {
                    factory.close();
                }
            }
        });
    }

    /**
     * Overrides setVisible() to refect the GUI if being made visible.
     */
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible == true) {
            // Add the listener for the JTree for Cell status changes
            CellManager.getCellManager().addCellStatusChangeListener(cellListener);
            updateTreeGUI();
            updateGUI();

            // issue #687: force a repaint
            invalidate();
            repaint();
        }
        else {
            // Remove the listener for the JTree for Cell status changes
            CellManager.getCellManager().removeCellStatusChangeListener(cellListener);
        }
    }

    /**
     * Sets the currently selected Cell. Update the GUI of the Cell Properties
     * frame to reflect the newly-selected Cell's state.
     * @param cell the currently selected Cell
     */
    public void setSelectedCell(Cell cell) throws IllegalStateException {

        // Check to see if there have been changes to the values in the Cell
        // Properties. If so, then prompt the user whether these should be
        // applies first. If so, then apply, otherwise restore the values so
        // the GUI is in a clean state.
        if (dirtyPanelSet.isEmpty() == false) {
            int result = JOptionPane.showConfirmDialog(this,
                    BUNDLE.getString("Apply_Switch_Message"),
                    BUNDLE.getString("Apply_Title"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                applyValues();
            }
            else {
                restoreValues();
            }
        }


        // Remember the selected index before we clear the capabilities panel.
        // We do this to try to keep the same capability selected if at all
        // possible.
        int oldSelectedIndex = capabilityList.getSelectedIndex();

        // Remove any existing panels from the GUI. We first need to tell them
        // to close themselves. We do this before we set to the new selected
        // Cell.
        clearPanelSet();

        // Now, set the currnent Cell. If it is null, we simply return. We
        // also turn off the "add" capability button and "remove" Cell button.
        selectedCell = cell;
        if (selectedCell == null) {
            addCapabilityButton.setEnabled(false);
            removeCellButton.setEnabled(false);
            return;
        }

        // Next, fetch the server-state of the Cell. This will tell us which
        // panels we will need to add to the frame.
        selectedCellServerState = fetchCellServerState();
        if (selectedCellServerState == null) {
            String warning = "Unable to fetch cell server state for " + cell.getName();
            LOGGER.warning(warning);
            throw new IllegalStateException(warning);
        }

        // Turn on the "add" capability button and the "remove" Cell button
        addCapabilityButton.setEnabled(true);

        // Disable the remove button for the environment cell, enable it
        // otherwise
        removeCellButton.setEnabled(!cell.getCellID().equals(CellID.getEnvironmentCellID()));

        // Update the panel set based upon the elements in the server state
        updatePanelSet();
        if (isVisible() == true) {
            updateGUI();
        }

        // Try to set the selected index intelligently. A great example is if
        // you select "Position" in one Cell and want to compare with the
        // "Position" of another Cell. It's a pain to (1) select "Position",
        // (2) select another Cell, (3) select "Position" again. So we try to
        // keep the same tab selected.
        if (listModel.getSize() > oldSelectedIndex && oldSelectedIndex != -1) {
            capabilityList.setSelectedIndex(oldSelectedIndex);
        }
        else {
            // Set the initial selected capability to "Basic"
            capabilityList.setSelectedIndex(0);
        }

        // Make sure the GUI redraws itself
        invalidate();
        repaint();
    }

    /**
     * @inheritDoc()
     */
    public void addToUpdateList(CellServerState cellServerState) {
        stateUpdates.cellServerState = cellServerState;
    }

    /**
     * @inheritDoc()
     */
    public void addToUpdateList(CellComponentServerState cellComponentServerState) {
        stateUpdates.cellComponentServerStateSet.add(cellComponentServerState);
    }

    /**
     * @inheritDoc()
     */
    public Cell getCell() {
        return selectedCell;
    }

    /**
     * @inheritDoc()
     */
    public CellServerState getCellServerState() {
        return selectedCellServerState;
    }

    /**
     * @inheritDoc()
     */
    public void setPanelDirty(Class clazz, boolean isDirty) {
        
        // Either add or remove the Class depending upon whether it is dirty
        if (isDirty == true) {
            dirtyPanelSet.add(clazz);
        }
        else {
            dirtyPanelSet.remove(clazz);
        }

        // Enable/disable the Ok/Apply buttons depending upon whether the set
        // of dirty panels is empty or not
        applyButton.setEnabled(dirtyPanelSet.isEmpty() == false);
        restoreButton.setEnabled(dirtyPanelSet.isEmpty() == false);
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

        mainPanel = new javax.swing.JPanel();
        topLevelSplitPane = new javax.swing.JSplitPane();
        jPanel4 = new javax.swing.JPanel();
        propertyButtonPanel = new javax.swing.JPanel();
        refreshButton = new javax.swing.JButton();
        restoreButton = new javax.swing.JButton();
        applyButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        propertyPanel = new javax.swing.JPanel();
        leftSplitPanePanel = new javax.swing.JSplitPane();
        capabilityPanel = new javax.swing.JPanel();
        capabilityListPanel = new javax.swing.JPanel();
        capabilityListScrollPane = new javax.swing.JScrollPane();
        capabilityList = new javax.swing.JList();
        capabilityButtonPanel = new javax.swing.JPanel();
        addCapabilityButton = new javax.swing.JButton();
        removeCapabilityButton = new javax.swing.JButton();
        cellHierarchyPanel = new javax.swing.JPanel();
        treePanel = new javax.swing.JPanel();
        treeScrollPane = new javax.swing.JScrollPane();
        cellHierarchyTree = new javax.swing.JTree();
        removeCellButton = new javax.swing.JButton();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/celleditor/client/resources/Bundle"); // NOI18N
        setTitle(bundle.getString("CellPropertiesJFrame.title")); // NOI18N
        getContentPane().setLayout(new java.awt.GridLayout(1, 1));

        mainPanel.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        mainPanel.setLayout(new java.awt.GridLayout(1, 1));

        topLevelSplitPane.setOneTouchExpandable(true);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("CellPropertiesJFrame.jPanel4.border.title"))); // NOI18N
        jPanel4.setLayout(new java.awt.GridBagLayout());

        propertyButtonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 0, 5, 0));
        propertyButtonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 0));

        refreshButton.setText(bundle.getString("CellPropertiesJFrame.refreshButton.text")); // NOI18N
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });
        propertyButtonPanel.add(refreshButton);

        restoreButton.setText(bundle.getString("CellPropertiesJFrame.restoreButton.text")); // NOI18N
        restoreButton.setEnabled(false);
        restoreButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restoreButtonActionPerformed(evt);
            }
        });
        propertyButtonPanel.add(restoreButton);

        applyButton.setText(bundle.getString("CellPropertiesJFrame.applyButton.text")); // NOI18N
        applyButton.setEnabled(false);
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyButtonActionPerformed(evt);
            }
        });
        propertyButtonPanel.add(applyButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        jPanel4.add(propertyButtonPanel, gridBagConstraints);

        propertyPanel.setBackground(new java.awt.Color(255, 255, 255));
        propertyPanel.setLayout(new java.awt.GridLayout(1, 0));
        jScrollPane1.setViewportView(propertyPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jPanel4.add(jScrollPane1, gridBagConstraints);

        topLevelSplitPane.setRightComponent(jPanel4);

        leftSplitPanePanel.setDividerLocation(250);
        leftSplitPanePanel.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        leftSplitPanePanel.setOneTouchExpandable(true);

        capabilityPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("CellPropertiesJFrame.capabilityPanel.border.title"))); // NOI18N
        capabilityPanel.setLayout(new java.awt.GridBagLayout());

        capabilityListPanel.setLayout(new java.awt.GridLayout(1, 0));

        capabilityListScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        capabilityListScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        capabilityList.setBackground(new java.awt.Color(204, 204, 255));
        capabilityList.setBorder(javax.swing.BorderFactory.createEmptyBorder(4, 4, 4, 4));
        capabilityList.setFont(new java.awt.Font("Lucida Grande", 1, 12));
        capabilityList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        capabilityListScrollPane.setViewportView(capabilityList);

        capabilityListPanel.add(capabilityListScrollPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        capabilityPanel.add(capabilityListPanel, gridBagConstraints);

        capabilityButtonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 0, 5, 0));
        capabilityButtonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        addCapabilityButton.setFont(new java.awt.Font("Lucida Grande", 1, 14));
        addCapabilityButton.setText(bundle.getString("CellPropertiesJFrame.addCapabilityButton.text")); // NOI18N
        addCapabilityButton.setEnabled(false);
        addCapabilityButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        addCapabilityButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addCapabilityButtonActionPerformed(evt);
            }
        });
        capabilityButtonPanel.add(addCapabilityButton);

        removeCapabilityButton.setFont(new java.awt.Font("Lucida Grande", 1, 14));
        removeCapabilityButton.setText(bundle.getString("CellPropertiesJFrame.removeCapabilityButton.text")); // NOI18N
        removeCapabilityButton.setEnabled(false);
        removeCapabilityButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        removeCapabilityButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeCapabilityButtonActionPerformed(evt);
            }
        });
        capabilityButtonPanel.add(removeCapabilityButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        capabilityPanel.add(capabilityButtonPanel, gridBagConstraints);

        leftSplitPanePanel.setBottomComponent(capabilityPanel);

        cellHierarchyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("CellPropertiesJFrame.cellHierarchyPanel.border.title"))); // NOI18N
        cellHierarchyPanel.setLayout(new java.awt.GridBagLayout());

        treePanel.setMinimumSize(new java.awt.Dimension(250, 23));
        treePanel.setLayout(new java.awt.GridLayout(1, 0));

        treeScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        treeScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("root");
        cellHierarchyTree.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        cellHierarchyTree.setDragEnabled(true);
        treeScrollPane.setViewportView(cellHierarchyTree);

        treePanel.add(treeScrollPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 3, 3);
        cellHierarchyPanel.add(treePanel, gridBagConstraints);

        removeCellButton.setFont(new java.awt.Font("Lucida Grande", 1, 14));
        removeCellButton.setText(bundle.getString("CellPropertiesJFrame.removeCellButton.text")); // NOI18N
        removeCellButton.setEnabled(false);
        removeCellButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        removeCellButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeCellButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        cellHierarchyPanel.add(removeCellButton, gridBagConstraints);

        leftSplitPanePanel.setTopComponent(cellHierarchyPanel);

        topLevelSplitPane.setLeftComponent(leftSplitPanePanel);

        mainPanel.add(topLevelSplitPane);

        getContentPane().add(mainPanel);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addCapabilityButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addCapabilityButtonActionPerformed
        // Create a new AddComponentDialog and display. Wait for the dialog
        // to close
        AddComponentDialog dialog = new AddComponentDialog(this, true, selectedCell);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);

        // If the OK button was pressed on the dialog and we can fetch a valid
        // cell component factory, then try to add it on the server.
        CellComponentFactorySPI spi = dialog.getCellComponentFactorySPI();
        if (dialog.getReturnStatus() == AddComponentDialog.RET_OK && spi != null) {
            addComponent(spi);
        }
    }//GEN-LAST:event_addCapabilityButtonActionPerformed

    private void removeCapabilityButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeCapabilityButtonActionPerformed
        // Find out which component is selected and remove it
        int index = capabilityList.getSelectedIndex();
        PropertiesFactorySPI spi = factoryList.get(index);
        removeComponent(spi);
    }//GEN-LAST:event_removeCapabilityButtonActionPerformed

    private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyButtonActionPerformed
        // Simply apply all of the values in the GUI
        applyValues();
    }//GEN-LAST:event_applyButtonActionPerformed

    private void restoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restoreButtonActionPerformed
        // Show a confirmation dialog before the restore takes place.
        int result = JOptionPane.showConfirmDialog(this,
                BUNDLE.getString("Restore_Message"),
                BUNDLE.getString("Restore_Title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            restoreValues();
        }
    }//GEN-LAST:event_restoreButtonActionPerformed

    private void removeCellButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeCellButtonActionPerformed
        // Post a dialog asking if the user really wants to delete the Cell.
        // Since the button is only enabled when a Cell is selected, we are
        // guaranteed to have a Cell.
        String message = BUNDLE.getString("Confirm_Delete_Message");
        String title = BUNDLE.getString("Confirm_Delete");
        message = MessageFormat.format(message, selectedCell.getName());
        int result = JOptionPane.showConfirmDialog(this, message, title, JOptionPane.YES_NO_OPTION);

        // Note that pressing the Esc key results in -1, so we must check
        // for that too (CLOSED_OPTION)
        if (result == JOptionPane.NO_OPTION || result == JOptionPane.CLOSED_OPTION) {
            return;
        }

        CellUtils.deleteCell(selectedCell);
    }//GEN-LAST:event_removeCellButtonActionPerformed

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        // Show a confirmation dialog before the refresh takes place
         int result = JOptionPane.showConfirmDialog(this,
                BUNDLE.getString("Refresh_Message"),
                BUNDLE.getString("Refresh_Title"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            refreshValues();
        }
    }//GEN-LAST:event_refreshButtonActionPerformed

    /**
     * Inner class to deal with selection on the capability list.
     */
    class CapabilityListSelectionListener implements ListSelectionListener {

        public void valueChanged(ListSelectionEvent e) {
            // Check to see if the value is still changing and if so, ignore
            if (e.getValueIsAdjusting() == true) {
                return;
            }

            // Handles when an item has been selected in the list of
            // capabilities Display the proper panel in such an instance. We
            // also enable or disable the '-' sign to remove components
            // depending upon what is selected
            int index = capabilityList.getSelectedIndex();
            if (index == -1) {
                propertyPanel.removeAll();
                removeCapabilityButton.setEnabled(false);
                propertyPanel.revalidate();
                propertyPanel.repaint();
                return;
            }

            // For all items, look up the panel in the ordered list of panels
            propertyPanel.removeAll();
            PropertiesFactorySPI factory = factoryList.get(index);
            propertyPanel.add(factory.getPropertiesJPanel());

            // We want to enable/disable the "remove" button only for property
            // sheets other than the first two. (The first three if the Cell
            // property sheet is non-null.
            if (cellProperties == null) {
                removeCapabilityButton.setEnabled(index >= 2);
            }
            else {
                removeCapabilityButton.setEnabled(index >= 3);
            }

            // Invalidate the layout and repaint
            propertyPanel.revalidate();
            propertyPanel.repaint();
        }
    }

    /**
     * Applies the values stored in the GUI to the cell. Loops through each
     * of the panels and tells them to apply().
     */
    private void applyValues() {
        // Loop through all of the properties in the list and tell them to
        // apply if they have been marked as dirty.
        for (PropertiesFactorySPI factory : factoryList) {
            // Fetch the Class for the factory and see if it is either in the
            // cell or cell component dirty list
            Class clazz = factory.getClass();
            if (dirtyPanelSet.contains(clazz) == true) {
                factory.apply();
            }
        }

        // As a first step, remove all of the cell component server states from
        // the cell server state. These cell component server states to update
        // are kept in a separate list.
        CellServerState updateState = stateUpdates.cellServerState;
        if (updateState != null) {
            updateState.removeAllComponentServerStates();
        }

        // Form a new CellUpdateServerState message with the appropriate
        // information and send it
        CellServerStateUpdateMessage msg = new CellServerStateUpdateMessage(
                selectedCell.getCellID(),
                updateState,
                stateUpdates.cellComponentServerStateSet);
        ResponseMessage response = selectedCell.sendCellMessageAndWait(msg);
        if (response instanceof ErrorMessage) {
            // XXX Probably should get a success/failed here!
            ErrorMessage em = (ErrorMessage) response;
            LOGGER.log(Level.WARNING, "Error applugin values: " +
                    em.getErrorMessage(), em.getErrorCause());

            JOptionPane.showMessageDialog(this, em.getErrorMessage(),
                    "Error applying values", JOptionPane.ERROR_MESSAGE);
        }

        // Clear any existing updates store by this class
        stateUpdates.clear();

        // Re-fetch the server state of the Cell here. This is so we have a
        // fetch copy of the state.
        selectedCellServerState = fetchCellServerState();
        if (selectedCellServerState == null) {
            LOGGER.warning("Unable to fetch cell server state for " +
                    selectedCell.getName());
            return;
        }

        // Clear out the set of "dirty" panels and disable the apply/restore
        // buttons
        dirtyPanelSet.clear();
        applyButton.setEnabled(false);
        restoreButton.setEnabled(false);

        // Finally, with the new server state just fetched, re-open the panels
        // to cause their GUIs to be refreshed
        updateGUI();

        // Also tell the tree node of the select Cell to repaint itself. This
        // is necessary, for example, if the name of the Cell has changed and
        // the label on the tree node needs to resize itself.
        SortedTreeNode node = cellNodes.get(selectedCell);
        node.nameChanged();
    }

    /**
     * Restores all of the values in the GUI to the original values. Loops
     * through each of the panels and tells them to refresh()
     */
    private void restoreValues() {
        // First clear out any existing updates stored by this class
        stateUpdates.clear();

        // Next iteratate through all factories and tell them to refresh
        for (PropertiesFactorySPI factory : factoryList) {
            factory.restore();
        }

        // Clear out the set of "dirty" panels and disable the apply/restore
        // buttons
        dirtyPanelSet.clear();
        applyButton.setEnabled(false);
        restoreButton.setEnabled(false);
    }

    /**
     * Refreshes all of the values in the GUI to the values stored by the
     * server.
     */
    private void refreshValues() {
        // First clear out any existing updates stored by this class and the
        // "dirty" state for the panel, to ignore any changes made by the
        // user.
        stateUpdates.clear();
        dirtyPanelSet.clear();

        // Then tell the dialog to simply reset the selected Cell
        setSelectedCell(selectedCell);
    }

    /**
     * Iterates through all of the properties panels and tell them they are
     * about to be closed. They should (perhaps) reset some values. Then this
     * method removes the panels from the Cell Properties dialog.
     */
    private void clearPanelSet() {
        // First, loop through all of the factories and tell them they are about
        // to close. It is up to them to decide what to do.
        for (PropertiesFactorySPI factory : factoryList) {
            factory.close();
        }

        // We simply clear out the list model and the list of factories
        listModel.clear();
        factoryList.clear();
    }

    /**
     * Asks the server for the server state of the cell; returns null upon
     * error
     */
    private CellServerState fetchCellServerState() {
        // Fetch the setup object from the Cell object. We send a message on
        // the cell channel, so we must fetch that first.
        ResponseMessage response = selectedCell.sendCellMessageAndWait(
                new CellServerStateRequestMessage(selectedCell.getCellID()));
        if (response == null) {
            return null;
        }

        // We need to remove the position component first as a special case
        // since we do not want to update it after the cell is created.
        CellServerStateResponseMessage cssrm = (CellServerStateResponseMessage) response;
        CellServerState state = cssrm.getCellServerState();
        if (state != null) {
            state.removeComponentServerState(PositionComponentServerState.class);
        }
        return state;
    }

    /**
     * Update the node in the tree with the current Cell hierarchy.
     */
    private void updateTreeGUI() {
        // Stop listening to selections on the tree while we update the GUI
        cellHierarchyTree.removeTreeSelectionListener(treeListener);

        // Refresh the Cell hierarchy tree. Expand the tree path around the
        // selected cell and select it in the tree, if there is one.
        refreshCells(LoginManager.getPrimary().getPrimarySession());
        if (selectedCell != null) {
            SortedTreeNode node = cellNodes.get(selectedCell);
            if (node == null) {
                LOGGER.warning("Unable to find tree node for selected Cell " +
                        selectedCell);
                return;
            }
            TreePath path = new TreePath(node.getPath());
            cellHierarchyTree.expandPath(path);
            cellHierarchyTree.setSelectionPath(path);
        }

        // Start listening to the tree once again
        cellHierarchyTree.addTreeSelectionListener(treeListener);
    }

    /**
     * Updates the GUI with values currently set in the cell 
     */
    private void updateGUI() {
        // Loop through all of the panels and tell them to refresh their values
        for (PropertiesFactorySPI factory : factoryList) {
            factory.open();
        }
    }

    /**
     * For the current Cell object, fetch which CellComponents are currently
     * associated with the cell and creates/deletes any panels on the GUI
     * edit frame as necessary. 
     */
    private void updatePanelSet() {
        // Look through the registry of cell property objects and check to see
        // if a panel exists for the cell. Add it if so
        Class clazz = selectedCellServerState.getClass();
        PropertiesManager manager = PropertiesManager.getPropertiesManager();
        cellProperties = manager.getPropertiesByClass(clazz);

        // For all cells, add the "Basic" and "Position" panels
        listModel.addElement(basicPropertiesFactory.getDisplayName());
        factoryList.add(basicPropertiesFactory);
        listModel.addElement(positionPropertiesFactory.getDisplayName());
        factoryList.add(positionPropertiesFactory);

        // If the cell properties panel exists, add an entry for it
        if (cellProperties != null && cellProperties.getPropertiesJPanel() != null) {
            listModel.addElement(cellProperties.getDisplayName());
            factoryList.add(cellProperties);
            cellProperties.setCellPropertiesEditor(this);
        }

        // Loop through all of the cell components in the server state and for
        // each see if there is a properties sheet registered for it. If so,
        // then add it.
        for (Map.Entry<Class, CellComponentServerState> e :
                selectedCellServerState.getComponentServerStates().entrySet()) {

            CellComponentServerState state = e.getValue();
            PropertiesFactorySPI spi = manager.getPropertiesByClass(state.getClass());
            if (spi != null) {
                JPanel panel = spi.getPropertiesJPanel();
                if (panel != null) {
                    String displayName = spi.getDisplayName();
                    spi.setCellPropertiesEditor(this);
                    listModel.addElement(displayName);
                    factoryList.add(spi);
                }
            }
        }
    }

    /**
     * Given a component factory, adds the component to the server and upates
     * the GUI to indicate its presence
     */
    private void addComponent(CellComponentFactorySPI spi) {
        // Fetch the default server state for the factory, and cell id. Make
        // sure we make it dynamically added
        CellComponentServerState state = spi.getDefaultCellComponentServerState();
        CellID cellID = selectedCell.getCellID();

        // Send a ADD component message on the cell channel. Wait for a
        // response. If OK, then update the GUI with the new component.
        // Otherwise, display an error dialog box.
        CellServerComponentMessage message =
                CellServerComponentMessage.newAddMessage(cellID, state);
        ResponseMessage response = selectedCell.sendCellMessageAndWait(message);
        if (response == null) {
            // log and error and post a dialog box
            LOGGER.warning("Received a null reply from cell with id " +
                    selectedCell.getCellID() + " with name " +
                    selectedCell.getName() + " adding component.");
            return;
        }

        if (response instanceof CellServerComponentResponseMessage) {
            // If successful, add the component to the GUI by refreshing the
            // Cell that is selected.
            setSelectedCell(selectedCell);
        }
        else if (response instanceof ErrorMessage) {
            // Log an error. Eventually we should display a dialog
            LOGGER.log(Level.WARNING, "Unable to add component to the server: " +
                    ((ErrorMessage) response).getErrorMessage(),
                    ((ErrorMessage) response).getErrorCause());
        }
    }

    /**
     * Given the component properties SPI, removes the component from the server
     * and updates the GUI to indicate its absense
     */
    private void removeComponent(PropertiesFactorySPI factory) {
        // Using the given factory, find out the server-side class name that
        // corresponds to the component.
        Class clazz = PropertiesManager.getServerStateClass(factory);
        if (clazz == null) {
            LOGGER.warning("Unable to remove component for " + factory);
            return;
        }

        // Using the registry of Cell components, find a factory to generate
        // a default cell component server state class.
        CellComponentRegistry r = CellComponentRegistry.getCellComponentRegistry();
        CellComponentFactorySPI spi = r.getCellFactoryByStateClass(clazz);
        if (spi == null) {
            LOGGER.warning("Could not find cell component factory for " +
                    clazz.getName());
            return;
        }

        // Create a new (default) instance of the server-side cell component
        // state class. We use this to find out what the class name is for
        // the cell component on the server side.
        CellComponentServerState s = spi.getDefaultCellComponentServerState();
        String className = s.getServerComponentClassName();

        // Send a message to remove the component giving the class name. Wait
        // for a response.
        CellID cellID = selectedCell.getCellID();

        // Send a message to the server with the cell id and class name and
        // wait for a response
        CellServerComponentMessage cscm =
                CellServerComponentMessage.newRemoveMessage(cellID, className);
        ResponseMessage response = selectedCell.sendCellMessageAndWait(cscm);
        if (response == null) {
            LOGGER.warning("Received a null reply from cell with id " +
                    cellID + " with name " + selectedCell.getName() +
                    " removing component.");
            return;
        }

        // Send the message to the server. Wait for a response. If OK, then
        // update the GUI with the new component. Otherwise, display an error
        // dialog box.
        if (response instanceof OKMessage) {
            // If successful, then remove the component from the GUI by
            // refreshing the Cell that is selected.
            setSelectedCell(selectedCell);
        }
        else if (response instanceof ErrorMessage) {
            // Log an error. Eventually we should display a dialog
            LOGGER.log(Level.WARNING, "Unable to add component to the server: " +
                    ((ErrorMessage) response).getErrorMessage(),
                    ((ErrorMessage) response).getErrorCause());
        }
    }

    /**
     * Holds the collection of updates to the server state to be sent to the
     * server
     */
    private class StateUpdates {

        public CellServerState cellServerState = null;
        public Set<CellComponentServerState> cellComponentServerStateSet = null;

        /** Default constructor */
        public StateUpdates() {
            cellComponentServerStateSet = new HashSet();
        }

        /**
         * Clears out any existing updates of state
         */
        public void clear() {
            cellServerState = null;
            cellComponentServerStateSet.clear();
        }
    }

    /**
     * An inner class that extends DefaultMutableTreeNode to make sure all of
     * the nodes are sorted.
     */
    class SortedTreeNode extends DefaultMutableTreeNode implements Comparable {
        private String displayName;

        /**
         * Constructor, takes a display name, use for the root of the world.
         * @param name The visual name of the node
         */
        public SortedTreeNode(String name) {
            super(name);
        }


        /**
         * Constructor, takes a display name and a cell, use for cells with
         * a different display name than the cell name.
         * @param cell the cell to represent
         * @param name The visual name of the node
         */
        public SortedTreeNode(Cell cell, String name) {
            super((cell == null) ? name : cell);

            this.displayName = name;
        }
        
        /**
         * Constructor, takes the Cell to use as the 'user object'
         * @param cell The Cell as the 'user object'
         */
        public SortedTreeNode(Cell cell) {
            super(cell);
        }

        /**
         * Get a user-visible name to use. If null, use the cell's name
         * @return the user-visible name
         */
        public String getDisplayName() {
            return displayName;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void insert(MutableTreeNode child, int childIndex) {
            // Insert the child and then sort all of the nodes.
            super.insert(child, childIndex);
            Collections.sort(this.children);
        }

         /**
         * {@inheritDoc}
         */
        public int compareTo(Object obj) {
            // Fetch the Cell names from this node and from obj and compare.
            Cell thisCell = (Cell)getUserObject();
            String thisName = thisCell.getName() + " (" +
                    thisCell.getCellID().toString() + ")";

            DefaultMutableTreeNode objNode = (DefaultMutableTreeNode) obj;
            Cell objCell = (Cell) objNode.getUserObject();
            String objName = objCell.getName() + " (" +
                    objCell.getCellID().toString() + ")";

            return thisName.compareToIgnoreCase(objName);
        }

        /**
         * Notifies the node that its name has changed, causing a redraw of
         * the node and a re-sort of the parent's children
         */
        public void nameChanged() {
            // Tell the parent to restore its children
            SortedTreeNode parentNode = (SortedTreeNode)getParent();
            if (parentNode != null) {
                Collections.sort(parentNode.children);
            }

            // Redraw this node, so that the entire name appears, necessary if
            // the name is made longer and to avoid the "..." that would be
            // drawn as a result.
            DefaultTreeModel model = (DefaultTreeModel)cellHierarchyTree.getModel();
            if (parentNode != null) {
                model.nodeStructureChanged(parentNode);
            }
            model.nodeChanged(this);

            // Make sure selected node is still selected
            TreePath treePath = new TreePath(getPath());
            cellHierarchyTree.setSelectionPath(treePath);
        }
    }

    /**
     * Render for Cells in the JTree. This uses the "default" tree cell renderer
     * which is a subclass of JLabel. Each tree node has a "user object" which
     * is a Cell. Draw the Cell name, and a border around it if it is currently
     * dragged-over in a drag-and-drop operation. The "root" node is just a
     * default mutable tree node with a user object of a String name.
     */
    private class CellTreeRenderer extends DefaultTreeCellRenderer {

        /**
         * @inheritDoc()
         */
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean selected, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {

            // Call the super class method to render the tree node properly.
            super.getTreeCellRendererComponent(tree, value, selected, expanded,
                    leaf, row, hasFocus);

            // Assume the node is a default mutable tree node. If the node is
            // currently being dragged-over in a drag-and-drop operation, then
            // set a black line border around the tree node, otherwise clear
            // the border.
            SortedTreeNode treeNode = (SortedTreeNode) value;
            if (treeNode == dragOverTreeNode) {
                setBorder(BorderFactory.createLineBorder(Color.BLACK));
            }
            else {
                setBorder(null);
            }

            // Using the name of the Cell to set the name of the tree node.
            if (treeNode.getDisplayName() != null) {
                setText(treeNode.getDisplayName());
            } else if (treeNode.getUserObject() instanceof Cell) {
                Cell cell = (Cell) treeNode.getUserObject();
                setText(cell.getName() + " (" + cell.getCellID().toString() + ")");
            }
            return this;
        }
    }

    /**
     * Listener for drop target events. Makes sure the node in the tree for the
     * drop is properly highlighted and performs the drop by reparenting the
     * Cell.
     */
    private class CellDropTargetListener extends DropTargetAdapter {

        /**
         * @inheritDoc()
         */
        @Override
        public void dragOver(DropTargetDragEvent dtde) {
            // Fetch the location over which we are dragging and find
            // the tree node corresponding to that position.
            Point location = dtde.getLocation();
            TreePath path = cellHierarchyTree.getPathForLocation(location.x, location.y);
            if (path == null) {
                dragOverTreeNode = null;
            }
            else {
                dragOverTreeNode = (SortedTreeNode) path.getLastPathComponent();
            }
            cellHierarchyTree.repaint();
        }

        /**
         * @inheritDoc()
         */
        @Override
        public void dragExit(DropTargetEvent dte) {
            dragOverTreeNode = null;
            cellHierarchyTree.repaint();
        }

        /**
         * @inheritDoc()
         */
        @Override
        public void drop(DropTargetDropEvent dtde) {
            // Fetch the location over which we are dropping and find
            // the tree node corresponding to that position.
            Point location = dtde.getLocation();
            TreePath path = cellHierarchyTree.getPathForLocation(location.x, location.y);
            if (path == null) {
                dtde.rejectDrop();
                return;
            }

            // Accept the drop and fetch the transferable from the drop event.
            // We are given the value of toString() from the dropped object.
            // Since this is a default mutable tree node, we defined the
            // toString() method below to return CellID@<CellID>.
            dtde.acceptDrop(DnDConstants.ACTION_MOVE);
            Transferable transferable = dtde.getTransferable();
            String cellIDString = null;
            try {
                DataFlavor df = new DataFlavor(
                        "application/x-java-jvm-local-objectref; " +
                        "class=java.lang.String");
                cellIDString = (String) transferable.getTransferData(df);
            } catch (Exception excp) {
                LOGGER.log(Level.WARNING, "Unable to fetch Cell ID string " +
                        "from the drop target", excp);
                return;
            }

            // Check to make sure the cellID String is properly formed. If not,
            // then log an error and return
            if (cellIDString == null || cellIDString.startsWith("CellID@") == false) {
                LOGGER.warning("Invalid Cell ID from drag and drop " + cellIDString);
                return;
            }

            // Parse out the Cell ID from the String
            int cellIDInt = -1;
            try {
                cellIDInt = Integer.parseInt(cellIDString.substring(7));
            } catch (Exception excp) {
                LOGGER.log(Level.WARNING, "Unable to fetch Cell ID integer " +
                        "from the drop target", excp);
                return;
            }
            CellID cellID = new CellID(cellIDInt);

            // Fetch the client-side Cell cache and find the Cell with the
            // dropped CellID
            WonderlandSession session = LoginManager.getPrimary().getPrimarySession();
            CellCache cache = ClientContext.getCellCache(session);
            if (cache == null) {
                LOGGER.warning("Unable to find Cell cache for session " + session);
                return;
            }
            Cell draggedCell = cache.getCell(cellID);
            if (draggedCell == null) {
                LOGGER.warning("Unable to find dragged Cell with ID " + cellID);
                return;
            }

            // Find out what Cell ID this was dropped over. This will form the
            // new parent. If the Cell is dropped over the world root, then set
            // the CellID to InvalidCellID
            CellID parentCellID = CellID.getInvalidCellID();
            SortedTreeNode treeNode = (SortedTreeNode) path.getLastPathComponent();
            Object userObject = treeNode.getUserObject();
            Cell newParent = null;
            if (userObject instanceof Cell && !(userObject instanceof EnvironmentCell)) {
                parentCellID = ((Cell) userObject).getCellID();
                newParent = (Cell) userObject;
                if (draggedCell.equals(newParent) == true) {
                    // User dropped cell on itself, return !
                    return;
                }
            }

            // Find the world transform of the new parent. If there is no new
            // parent (e.g. if the Cell is to be placed at the world root), then
            // use a null transform.
            CellTransform newParentWorld = new CellTransform(null, null);
            if (newParent != null) {
                newParentWorld = newParent.getWorldTransform();
            }

            CellTransform newChildLocal = ScenegraphUtils.computeChildTransform(
                    newParentWorld, draggedCell.getWorldTransform());

            // Send a message to the server indicating the change in the
            // parent. We need to send this over the cell edit connection,
            // rather than the cell connection.
            CellEditChannelConnection connection =
                    (CellEditChannelConnection) session.getConnection(
                    CellEditConnectionType.CLIENT_TYPE);
            connection.send(new CellReparentMessage(cellID, parentCellID, newChildLocal));

            // Turn off the selected node border and repaint the tree.
            dragOverTreeNode = null;
            cellHierarchyTree.repaint();
        }
    }

    /**
     * Get the  cells from the cache and update the nodes in tree
     */
    private void refreshCells(WonderlandSession session) {
        // Fetch the client-side Cell cache, log an error if not found and
        // return
        CellCache cache = ClientContext.getCellCache(session);
        if (cache == null) {
            LOGGER.warning("Unable to find Cell cache for session " + session);
            return;
        }

        // Clear out any existing Cells from the tree. We do this by creating a
        // new tree model
        treeRoot = new SortedTreeNode(cache.getEnvironmentCell(),
                                      BUNDLE.getString("World_Root"));
        DefaultTreeModel treeModel = new DefaultTreeModel(treeRoot);
        cellHierarchyTree.setModel(treeModel);
        cellNodes.clear();
        cellNodes.put(cache.getEnvironmentCell(), treeRoot);

        // Loop through all of the root cells and add into the world
        Collection<Cell> rootCells = cache.getRootCells();
        for (Cell rootCell : rootCells) {
            createJTreeNode(rootCell);
        }
        cellHierarchyTree.repaint();
    }

    /**
     * Creates a new tree node for the given Cell and inserts it into the tree.
     */
    private void createJTreeNode(Cell cell) {
        // As a special case, if the Cell is an AvatarCell, then simply ignore
        // and return, since Avatar Cells are returned by the Cell cache.
        if (cell instanceof AvatarCell) {
            return;
        }

        // Create the tree node and put into the map of all nodes. We override
        // the toString() method to return a string containing the Cell ID.
        // This is used in the drag and drop mechanism to figure out which
        // Cell is being dragged.
        SortedTreeNode ret = new SortedTreeNode(cell) {
            @Override
            public String toString() {
                Cell cell = (Cell) getUserObject();
                return "CellID@" + cell.getCellID().toString();
            }
        };
        cellNodes.put(cell, ret);

        // Find the parent node of the new node, and insert it into the tree
        SortedTreeNode parentNode = cellNodes.get(cell.getParent());
        if (parentNode == null) {
            parentNode = treeRoot;
        }
        parentNode.add(ret);

        // Tell the model that a new node has been inserted
        DefaultTreeModel model = (DefaultTreeModel)cellHierarchyTree.getModel();
        int childIndex = parentNode.getIndex(ret);
        model.nodesWereInserted(parentNode, new int[] { childIndex });

        // Recursively iterate through all of the Cell's children and add to
        // the tree.
        List<Cell> children = cell.getChildren();
        for (Cell child : children) {
            createJTreeNode(child);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addCapabilityButton;
    private javax.swing.JButton applyButton;
    private javax.swing.JPanel capabilityButtonPanel;
    private javax.swing.JList capabilityList;
    private javax.swing.JPanel capabilityListPanel;
    private javax.swing.JScrollPane capabilityListScrollPane;
    private javax.swing.JPanel capabilityPanel;
    private javax.swing.JPanel cellHierarchyPanel;
    private javax.swing.JTree cellHierarchyTree;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane leftSplitPanePanel;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel propertyButtonPanel;
    private javax.swing.JPanel propertyPanel;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton removeCapabilityButton;
    private javax.swing.JButton removeCellButton;
    private javax.swing.JButton restoreButton;
    private javax.swing.JSplitPane topLevelSplitPane;
    private javax.swing.JPanel treePanel;
    private javax.swing.JScrollPane treeScrollPane;
    // End of variables declaration//GEN-END:variables
}
