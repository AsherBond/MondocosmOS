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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.registry.CellComponentRegistry;
import org.jdesktop.wonderland.client.cell.registry.spi.CellComponentFactorySPI;
import org.jdesktop.wonderland.common.cell.state.CellServerState;

/**
 * A dialog box so that users can dynamically add a component.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class AddComponentDialog extends javax.swing.JDialog {

    /** A return status code - returned if Cancel button has been pressed */
    public static final int RET_CANCEL = 0;

    /** A return status code - returned if OK button has been pressed */
    public static final int RET_OK = 1;

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/celleditor/client/resources/Bundle");

    /* The table holding the list of components */
    private JTable componentsTable = null;

    /* A map of display names in the table to the component factories */
    private Map<String, CellComponentFactorySPI> factoryMap = new HashMap();

    /* The component factory selected to be added */
    private CellComponentFactorySPI cellComponentFactorySPI = null;

    /* The edit property frame displaying this dialog */
    private CellPropertiesJFrame editframe = null;

    /** Creates new form AddComponentDialog */
    public AddComponentDialog(CellPropertiesJFrame editframe, boolean modal, Cell cell) {
        super(editframe, modal);
        this.editframe = editframe;
        initComponents();

        // Create the JTable with all of the components and add it to the
        // scroll pane
        componentsTable = createTable();
        capabilityScrollPane.setViewportView(componentsTable);

        // Listen for selections on the table, and when a row is selected then
        // enable the OK button (or not)
        ListSelectionModel selectionModel = componentsTable.getSelectionModel();
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                boolean isEnabled = componentsTable.getSelectedRow() != -1;
                okButton.setEnabled(isEnabled);
            }
        });

    }

    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    /**
     * Returns the cell component factory of the component to add
     * @return the cell component factory of the component to add
     */
    public CellComponentFactorySPI getCellComponentFactorySPI() {
        return cellComponentFactorySPI;
    }

    /**
     * Creates the table based upon the list of registered components
     */
    private JTable createTable() {
        // Fetch the set of components and form a 2D array of table entries
        CellComponentRegistry registry =
                CellComponentRegistry.getCellComponentRegistry();
        Set<CellComponentFactorySPI> factories = registry.getAllCellFactories();

        // Fetch the set of component property display names that are already
        // on the set and remove from the list of factories.
        CellServerState state = editframe.getCellServerState();
        Iterator<CellComponentFactorySPI> it = factories.iterator();
        while (it.hasNext() == true) {
            CellComponentFactorySPI spi = it.next();
            Class clazz = spi.getDefaultCellComponentServerState().getClass();
            if (state.getComponentServerState(clazz) != null) {
                it.remove();
            }
        }

        // Put all of the factories into a list and sort based upon the display
        // name
        List<CellComponentFactorySPI> factoryList = new LinkedList(factories);
        Comparator nameComparator = new Comparator<CellComponentFactorySPI>() {
            public int compare(CellComponentFactorySPI o1, CellComponentFactorySPI o2) {
                return o1.getDisplayName().compareTo(o2.getDisplayName());
            }
        };
        Collections.sort(factoryList, nameComparator);

        // Loop through the ordered list and place into the table.
        int size = factoryList.size();
        Object[][] entries = new Object[size][2];
        int i = 0;
        for (CellComponentFactorySPI factory : factoryList) {
            entries[i][0] = factory.getDisplayName();
            entries[i][1] = factory.getDescription();
            factoryMap.put(factory.getDisplayName(), factory);
            i++;
        }

        // Create a table with the entry and table names
        Object[] names = new Object[]{
            BUNDLE.getString("Capability"), BUNDLE.getString("Description")
        };
        JTable table = new JTable(entries, names);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return table;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        capabilityScrollPane = new javax.swing.JScrollPane();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/celleditor/client/resources/Bundle"); // NOI18N
        setTitle(bundle.getString("Add_Capability_Title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        okButton.setText(bundle.getString("AddComponentDialog.okButton.text")); // NOI18N
        okButton.setEnabled(false);
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(bundle.getString("AddComponentDialog.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jLabel1.setText(bundle.getString("AddComponentDialog.jLabel1.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(okButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 67, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(cancelButton))
                    .add(jLabel1)
                    .add(capabilityScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 530, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {cancelButton, okButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(capabilityScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cancelButton)
                    .add(okButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        // Fetch the component factory based upon the selected comopnent
        int row = componentsTable.getSelectedRow();
        String displayName = (String) componentsTable.getModel().getValueAt(row, 0);
        cellComponentFactorySPI = factoryMap.get(displayName);
        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        cellComponentFactorySPI = null;
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JScrollPane capabilityScrollPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables

    private int returnStatus = RET_CANCEL;
}
