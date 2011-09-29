/**
 * Open Wonderland
 *
 * Copyright (c) 2011, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.modules.placemarks.client;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import org.jdesktop.wonderland.client.jme.MainFrame.PlacemarkType;
import org.jdesktop.wonderland.modules.placemarks.api.client.PlacemarkRegistry;
import org.jdesktop.wonderland.modules.placemarks.api.client.PlacemarkRegistry.PlacemarkListener;
import org.jdesktop.wonderland.modules.placemarks.api.client.PlacemarkRegistryFactory;
import org.jdesktop.wonderland.modules.placemarks.api.common.Placemark;
import org.jdesktop.wonderland.modules.placemarks.common.PlacemarkList;

/**
 * A JFrame to allow editing of the list of X Apps registered to appear in the
 * Cell Palettes.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class EditPlacemarksJFrame extends javax.swing.JFrame {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/placemarks/client/resources/Bundle");
    private static final Logger LOGGER =
            Logger.getLogger(EditPlacemarksJFrame.class.getName());
    private PlacemarkTableModel placemarksTableModel = null;
    private JTable placemarksTable = null;
    private DecimalFormat df;

    /** Creates new form EditPlacemarksJFrame */
    public EditPlacemarksJFrame() {
        initComponents();

        df = (DecimalFormat)DecimalFormat.getNumberInstance();
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);

        // Create the user table to display the user Placemarks
        PlacemarkList placemarkList = PlacemarkUtils.getUserPlacemarkList();
        placemarksTableModel =
                new PlacemarkTableModel(placemarkList.getPlacemarksAsList());
        placemarksTable = new JTable(placemarksTableModel);
        placemarksTable.setColumnSelectionAllowed(false);
        placemarksTable.setRowSelectionAllowed(true);
        placemarksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userScrollPane.setViewportView(placemarksTable);

        // Listen for changes to the select on the user table and enable/
        // disable the Edit/Remove buttons as a result.
        ListSelectionListener userListener = new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                boolean isRowSelected = placemarksTable.getSelectedRow() != -1;
                editButton.setEnabled(isRowSelected);
                removeButton.setEnabled(isRowSelected);
            }
        };
        placemarksTable.getSelectionModel().addListSelectionListener(
                userListener);

        // Upon a double-click, activated the Edit... button
        placemarksTable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editButton.doClick();
                }
            }
        });

        // Listen for changes in the list of registered Placemarks in the
        // system and update the table model accordingly
        PlacemarkRegistry registry = PlacemarkRegistryFactory.getInstance();
        registry.addPlacemarkRegistryListener(new PlacemarkListener() {

            public void placemarkAdded(
                    Placemark placemark, PlacemarkType type) {
                if (type == PlacemarkType.USER) {
                    placemarksTableModel.addToPlacemarkList(placemark);
                }
            }

            public void placemarkRemoved(
                    Placemark placemark, PlacemarkType type) {
                if (type == PlacemarkType.USER) {
                    placemarksTableModel.removeFromPlacemarkList(placemark);
                }
            }
        });
    }

    /**
     * A table model that displays a list of user-specific placemarks.
     */
    private class PlacemarkTableModel extends AbstractTableModel {

        private List<Placemark> placemarkList = null;

        /** Constructor, takes the list of registry items to display */
        public PlacemarkTableModel(List<Placemark> items) {
            placemarkList = items;
        }

        /**
         * @inheritDoc()
         */
        public int getRowCount() {
            return placemarkList.size();
        }

        /**
         * @inheritDoc()
         */
        public int getColumnCount() {
            return 4;
        }

        /**
         * @inheritDoc()
         */
        @Override
        public String getColumnName(int column) {
            switch (column) {
                case 0:
                    return BUNDLE.getString("Name");
                case 1:
                    return BUNDLE.getString("Server_URL");
                case 2:
                    return BUNDLE.getString("Location");
                case 3:
                    return BUNDLE.getString("Look_Angle");
                default:
                    return "";
            }
        }

        /**
         * @inheritDoc()
         */
        public Object getValueAt(int rowIndex, int columnIndex) {
            Placemark item = placemarkList.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return item.getName();
                case 1:
                    return item.getUrl();
                case 2:
                    return "(" + df.format(item.getX()) + ", " + df.format(item.getY()) + ", " +
                            df.format(item.getZ()) + ")";
                case 3:
                    String value = BUNDLE.getString("Look_Angle_Value");
                    return MessageFormat.format(value, df.format(item.getAngle()));
                default:
                    return "";
            }
        }

        /**
         * Returns the Nth placemark in the list.
         *
         * @param n The index into the list
         * @return Returns the nth placemark
         */
        public Placemark getPlacemark(int n) {
            return placemarkList.get(n);
        }

        /**
         * Resets the list of Placemarks and tells the table to update itself
         */
        public void setPlacemarkList(List<Placemark> list) {
            placemarkList = list;
            fireTableDataChanged();
        }

        /**
         * Add a placemark to the list
         */
        public void addToPlacemarkList(Placemark placemark) {
            placemarkList.add(placemark);
            fireTableDataChanged();
        }

        /**
         * Remove a placemark from the list
         */
        public void removeFromPlacemarkList(Placemark placemark) {
            placemarkList.remove(placemark);
            fireTableDataChanged();
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

        addButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        userScrollPane = new javax.swing.JScrollPane();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/placemarks/client/resources/Bundle"); // NOI18N
        setTitle(bundle.getString("EditPlacemarksJFrame.title")); // NOI18N

        addButton.setText(bundle.getString("EditPlacemarksJFrame.addButton.text")); // NOI18N
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        editButton.setText(bundle.getString("EditPlacemarksJFrame.editButton.text")); // NOI18N
        editButton.setEnabled(false);
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        removeButton.setText(bundle.getString("EditPlacemarksJFrame.removeButton.text")); // NOI18N
        removeButton.setEnabled(false);
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        closeButton.setText(bundle.getString("EditPlacemarksJFrame.closeButton.text")); // NOI18N
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(addButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(editButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(removeButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(closeButton))
                    .add(userScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {addButton, editButton, removeButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(userScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(closeButton)
                    .add(removeButton)
                    .add(editButton)
                    .add(addButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        // Fetch the list of known USER Placemark names
        PlacemarkRegistry registry = PlacemarkRegistryFactory.getInstance();
        Set<Placemark> placemarkSet =
                registry.getAllPlacemarks(PlacemarkType.USER);

        // When the Add... button is pressed popup a dialog asking for all of
        // the information. Add it to the repository and registry upon OK.
        AddEditPlacemarkJDialog dialog =
                new AddEditPlacemarkJDialog(this, true, placemarkSet);
        dialog.setTitle(BUNDLE.getString("Add_Placemark"));
        dialog.setLocationRelativeTo(this);
        dialog.pack();
        dialog.setVisible(true);

        if (dialog.getReturnStatus() == AddEditPlacemarkJDialog.RET_OK) {
            String name = dialog.getPlacemarkName();
            String url = dialog.getServerURL();
            float x = dialog.getLocationX();
            float y = dialog.getLocationY();
            float z = dialog.getLocationZ();
            float angle = dialog.getLookAtAngle();

            Placemark placemark = new Placemark(name, url, x, y, z, angle);
            try {
                PlacemarkUtils.addUserPlacemark(placemark);
            } catch (Exception excp) {
                LOGGER.log(Level.WARNING, "Unable to add " + name + " to " +
                        " user's placemarks", excp);
                return;
            }

            // Tell the client-side registry of placemarks that a new one has
            // been added
            registry.registerPlacemark(placemark, PlacemarkType.USER);
        }
}//GEN-LAST:event_addButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        // When the Remove..... button is pressed find out the name of the
        // Placemark being removed and remove it from the placemark list and
        // refresh the table.
        int row = placemarksTable.getSelectedRow();
        if (row == -1) {
            return;
        }
        String name = (String) placemarksTableModel.getValueAt(row, 0);
        Placemark placemark = placemarksTableModel.getPlacemark(row);

        try {
            PlacemarkUtils.removeUserPlacemark(name);
        } catch (Exception excp) {
            LOGGER.log(Level.WARNING, "Unable to remove " + name + " from " +
                    " user's placemarks", excp);
            return;
        }

        // Tell the client-side registry of placemarks that a new one has
        // been added
        PlacemarkRegistry registry = PlacemarkRegistryFactory.getInstance();
        registry.unregisterPlacemark(placemark, PlacemarkType.USER);
}//GEN-LAST:event_removeButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        // Fetch the list of known USER Placemark names
        PlacemarkRegistry registry = PlacemarkRegistryFactory.getInstance();
        Set<Placemark> placemarkSet =
                registry.getAllPlacemarks(PlacemarkType.USER);

        // When the Edit..... button is pressed find the Placemark selected
        // and display a dialog with the values filled in.
        int row = placemarksTable.getSelectedRow();
        if (row == -1) {
            return;
        }
        Placemark placemark = placemarksTableModel.getPlacemark(row);

        // Display a dialog with the values in the Placemark. And if we wish
        // to update the values, then re-add the placemark. (Re-adding the
        // placemark should have the effect of updating its values.
        AddEditPlacemarkJDialog dialog = new AddEditPlacemarkJDialog(
                this, true, placemark, placemarkSet);
        dialog.setTitle(BUNDLE.getString("Edit_Placemark"));
        dialog.setLocationRelativeTo(this);
        dialog.pack();
        dialog.setVisible(true);

        if (dialog.getReturnStatus() == AddEditPlacemarkJDialog.RET_OK) {
            // First remove the old placemark.
            String oldName = placemark.getName();
            try {
                PlacemarkUtils.removeUserPlacemark(oldName);
            } catch (Exception excp) {
                LOGGER.log(Level.WARNING, "Unable to remove " + oldName +
                        " from " + " user's placemarks", excp);
                return;
            }

            // Tell the client-side registry of placemarks that a new one has
            // been added
            registry.unregisterPlacemark(placemark, PlacemarkType.USER);

            // Create a new placemark with the new information.
            String name = dialog.getPlacemarkName();
            String url = dialog.getServerURL();
            float x = dialog.getLocationX();
            float y = dialog.getLocationY();
            float z = dialog.getLocationZ();
            float angle = dialog.getLookAtAngle();
            Placemark newPlacemark = new Placemark(name, url, x, y, z, angle);

            try {
                PlacemarkUtils.addUserPlacemark(newPlacemark);
            } catch (Exception excp) {
                LOGGER.log(Level.WARNING, "Unable to add " + name + " to " +
                        " user's placemarks", excp);
                return;
            }

            // Tell the client-side registry of placemarks that a new one has
            // been added
            registry.registerPlacemark(newPlacemark, PlacemarkType.USER);
        }
    }//GEN-LAST:event_editButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_closeButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton editButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JScrollPane userScrollPane;
    // End of variables declaration//GEN-END:variables
}
