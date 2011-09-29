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
package org.jdesktop.wonderland.modules.palette.client;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.wonderland.client.cell.registry.spi.CellFactorySPI;
import org.jdesktop.wonderland.client.cell.registry.CellRegistry;
import org.jdesktop.wonderland.client.cell.registry.CellRegistry.CellRegistryListener;
import org.jdesktop.wonderland.client.cell.utils.CellCreationException;
import org.jdesktop.wonderland.client.cell.utils.CellUtils;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.modules.palette.client.dnd.PaletteDragGestureListener;

/**
 * A palette of cell types available to create in the world.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class CellPalette extends javax.swing.JFrame implements ListSelectionListener {

    private final static Logger LOGGER =
            Logger.getLogger(CellPalette.class.getName());

    /* A map of cell display names and their cell factories */
    private Map<String, CellFactorySPI> cellFactoryMap = new HashMap();

    /* The "No Preview Available" image */
    private Image noPreviewAvailableImage = null;

    /* The handler for the drag source for the preview image */
    private PaletteDragGestureListener gestureListener = null;

    /* The listener for changes in the list of registered Cell factories */
    private CellRegistryListener cellListener = null;

    /* The drag support from the preview label */
    private DragSource dragSource = null;

    /* The drag gesture recognizers for the cell palette */
    private DragGestureRecognizer previewRecognizer = null;
    private DragGestureRecognizer listRecognizer = null;

    /** Creates new form CellPalette */
    public CellPalette() {
        // Initialize the GUI components
        initComponents();

        // Create the icon for the "No Preview Available" image
        URL url = 
                CellPalette.class.getResource("resources/nopreview128x128.png");
        noPreviewAvailableImage = Toolkit.getDefaultToolkit().createImage(url);

        // Listen for list selection events and update the preview panel with
        // the selected item's image
        cellList.addListSelectionListener(this);

        // Add support for drag from the preview image label
        dragSource = DragSource.getDefaultDragSource();
        gestureListener = new PaletteDragGestureListener();

        // Create a listener for changes to the list of registered Cell
        // factories, to be used in setVisible(). When the list changes we
        // simply do a fresh update of all values.
        cellListener = new CellRegistryListener() {
            public void cellRegistryChanged() {
                // Since this is not happening (necessarily) in the AWT Event
                // Thread, we should put it in one
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        updateListValues();
                    }
                });
            }
        };
    }

    @Override
    public void setVisible(boolean visible) {
        // Update the list values. We also want to (de)register a listener for
        // changes to the list of registered Cell factories any time after we
        // make it (in)visible.
        CellRegistry cellRegistry = CellRegistry.getCellRegistry();
        if (visible == true) {
            updateListValues();
            cellRegistry.addCellRegistryListener(cellListener);
        }
        else {
            cellRegistry.removeCellRegistryListener(cellListener);
        }

        // Finally, ask the superclass to make the dialog visible.
        super.setVisible(visible);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollBar1 = new javax.swing.JScrollBar();
        cellScrollPane = new javax.swing.JScrollPane();
        cellList = new javax.swing.JList();
        createButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        previewPanel = new javax.swing.JPanel();
        previewLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/palette/client/resources/Bundle"); // NOI18N
        setTitle(bundle.getString("CellPalette.title")); // NOI18N
        setName("cellFrame"); // NOI18N

        cellList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        cellScrollPane.setViewportView(cellList);

        createButton.setText(bundle.getString("CellPalette.createButton.text")); // NOI18N
        createButton.setEnabled(false);
        createButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createActionPerformed(evt);
            }
        });

        jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel1.setText(bundle.getString("CellPalette.jLabel1.text")); // NOI18N

        previewPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        previewPanel.setMinimumSize(new java.awt.Dimension(128, 128));
        previewPanel.setPreferredSize(new java.awt.Dimension(128, 128));
        previewPanel.setLayout(new java.awt.GridLayout(1, 0));

        previewLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        previewLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        previewLabel.setIconTextGap(0);
        previewPanel.add(previewLabel);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(cellScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(previewPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(createButton))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cellScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE)
                    .add(layout.createSequentialGroup()
                        .add(previewPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 75, Short.MAX_VALUE)
                        .add(createButton)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void createActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createActionPerformed

    // We synchronized around the cellFactoryMap so that this action does not
    // interfere with any changes in the map.
    synchronized (cellFactoryMap) {
        // From the selected value, find the proper means to create the object
        final String cellDisplayName = (String) cellList.getSelectedValue();
        final CellFactorySPI factory = cellFactoryMap.get(cellDisplayName);
        new Thread(new Runnable() {
            public void run() {
                CellServerState setup = factory.getDefaultCellServerState(null);

                // Create the new cell at a distance away from the avatar
                try {
                    CellUtils.createCell(setup);
                } catch (CellCreationException excp) {
                    LOGGER.log(Level.WARNING, "Unable to create cell " +
                            cellDisplayName + " using palette", excp);
                }
            }
        }).start();
    }
}//GEN-LAST:event_createActionPerformed

    /**
     * Updates the list of values displayed from the CellRegistry.
     */
    private void updateListValues() {
        // Let's synchronized around cellFactoryMap so that any selections do
        // not interfere with changes in this map
        synchronized (cellFactoryMap) {
            // Clear out any existing entries in the map of registered Cells
            cellFactoryMap.clear();

            // Fetch the registry of cells and for each, get the palette info
            // and populate the list.
            CellRegistry registry = CellRegistry.getCellRegistry();
            Set<CellFactorySPI> cellFactories = registry.getAllCellFactories();
            List<String> listNames = new LinkedList();

            // Loop through each cell factory we find. Insert the cell names
            // into a list. Ignore any factories without a cell name.
            for (CellFactorySPI cellFactory : cellFactories) {
                try {
                    String name = cellFactory.getDisplayName();
                    if (name != null) {
                        listNames.add(name);
                        cellFactoryMap.put(name, cellFactory);
                    }
                } catch (java.lang.Exception excp) {
                    // Just ignore, but log a message
                    LOGGER.log(Level.WARNING,
                            "No Display Name for Cell Factory " +
                            cellFactory, excp);
                }
            }

            // Set the names of the list, first sorting the list in alphabetical
            // order
            Collections.sort(listNames);
            cellList.setListData(listNames.toArray(new String[]{}));
            cellList.setDragEnabled(true);
        }
    }

    /**
     * Handles when a selection has been made of the list of cell type names.
     * @param e
     */
    public void valueChanged(ListSelectionEvent e) {

        // We synchronized around the cellFactoryMap so that this action does
        // not interfere with any changes in the map.
        synchronized (cellFactoryMap) {

            // Fetch the display name of the cell selected. If it happens to
            // be null (not sure why this would happen), then simply return.
            String selectedName = (String) cellList.getSelectedValue();
            if (selectedName == null) {
                // If nothing is selected, then disable the Insert button, the
                // preview image and disable drag-and-drop from the preview
                // label.
                createButton.setEnabled(false);
                previewLabel.setIcon(null);

                // Make sure the recognizers are not null, and set their
                // components to null;
                if (previewRecognizer != null) {
                    previewRecognizer.setComponent(null);
                }

                if (listRecognizer != null) {
                    listRecognizer.setComponent(null);
                }
                return;
            }

            // Next, fetch the Cell factory associated with the display name.
            // If it happens to be null (not sure why this would happen), then
            // simply return.
            CellFactorySPI cellFactory = cellFactoryMap.get(selectedName);
            if (cellFactory == null) {
                return;
            }

            // Enable the Insert button
            createButton.setEnabled(true);

            // Otherwise, update the preview image, if one exists, otherwise
            // use the default image.
            Image previewImage = cellFactory.getPreviewImage();
            if (previewImage != null) {
                ImageIcon icon = new ImageIcon(previewImage);
                previewLabel.setIcon(icon);

                // Pass the necessary information for drag and drop
                gestureListener.cellFactory = cellFactory;
                gestureListener.previewImage = previewImage;
            }
            else {
                ImageIcon icon = new ImageIcon(noPreviewAvailableImage);
                previewLabel.setIcon(icon);

                // Pass the necessary information for drag and drop
                gestureListener.cellFactory = cellFactory;
                gestureListener.previewImage = noPreviewAvailableImage;
            }

            // Enable drag-and-drop from the preview image, creating the
            // recognizer if necessary
            if (previewRecognizer == null) {
                previewRecognizer =
                        dragSource.createDefaultDragGestureRecognizer(
                        previewLabel, DnDConstants.ACTION_COPY_OR_MOVE,
                        gestureListener);
            }
            else {
                previewRecognizer.setComponent(previewLabel);
            }

            // Add support for drag from the text list of cells, creating the
            // recognizer if necessary
            if (listRecognizer == null) {
                listRecognizer =
                        dragSource.createDefaultDragGestureRecognizer(cellList,
                        DnDConstants.ACTION_COPY_OR_MOVE, gestureListener);
            }
            else {
                listRecognizer.setComponent(cellList);
            }
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList cellList;
    private javax.swing.JScrollPane cellScrollPane;
    private javax.swing.JButton createButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollBar jScrollBar1;
    private javax.swing.JLabel previewLabel;
    private javax.swing.JPanel previewPanel;
    // End of variables declaration//GEN-END:variables
}
