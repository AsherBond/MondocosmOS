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
package org.jdesktop.wonderland.modules.celleditor.client;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.properties.CellPropertiesEditor;
import org.jdesktop.wonderland.client.cell.properties.spi.PropertiesFactorySPI;
import org.jdesktop.wonderland.common.cell.messages.CellServerComponentMessage;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.cell.state.InteractionComponentServerState;
import org.jdesktop.wonderland.common.messages.ErrorMessage;
import org.jdesktop.wonderland.common.messages.ResponseMessage;

/**
 * A property sheet to edit the basic attributes of a cell
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
public class BasicJPanel extends JPanel implements PropertiesFactorySPI {
    private static final Logger LOGGER =
            Logger.getLogger(BasicJPanel.class.getName());
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/celleditor/client/resources/Bundle");

    private CellPropertiesEditor editor = null;
    private String originalCellName = null;
    private boolean origCollidable;
    private boolean origSelectable;

    /** Creates new form BasicJPanel */
    public BasicJPanel() {
        initComponents();

        // Listen for changes in the entry for the text field
        cellNameTextField.getDocument().addDocumentListener(new NameTextFieldListener());
    }

    /**
     * @inheritDoc()
     */
    public String getDisplayName() {
        return BUNDLE.getString("Basic");
    }

    /**
     * @inheritDoc()
     */
    public JPanel getPropertiesJPanel() {
        return this;
    }

    /**
     * @inheritDoc()
     */
    public void setCellPropertiesEditor(CellPropertiesEditor editor) {
        this.editor = editor;
    }

    /**
     * @inheritDoc()
     */
    public void open() {
        // Fetch the name and CellID from the Cell and Cell server state and
        // update the GUI
        Cell cell = editor.getCell();
        CellServerState cellServerState = editor.getCellServerState();

        if (cellServerState != null) {
            originalCellName = cellServerState.getName();
            cellNameTextField.setText(originalCellName);
            cellIDLabel.setText(cell.getCellID().toString());
            cellClassLabel.setText(cell.getClass().getName());

            InteractionComponentServerState icss = (InteractionComponentServerState)
                    cellServerState.getComponentServerState(InteractionComponentServerState.class);
            if (icss == null) {
                origCollidable = true;
                origSelectable = true;
            } else {
                origCollidable = icss.isCollidable();
                origSelectable = icss.isSelectable();
            }
            
            collidableCB.setSelected(origCollidable);
            selectableCB.setSelected(origSelectable);
        }

        checkDirty();
    }

    /**
     * @inheritDoc()
     */
    public void restore() {
        // Reset the GUI to the original name of the text field
        cellNameTextField.setText(originalCellName);
        collidableCB.setSelected(origCollidable);
        selectableCB.setSelected(origSelectable);

        checkDirty();
    }

    /**
     * @inheritDoc()
     */
    public void close() {
        // We do nothing here, since any changes in the GUI property sheet do
        // not take effect until apply(), so there is no state to revert here
        // and nothing to really clean up.
    }

    /**
     * @inheritDoc()
     */
    public void apply() {
        // Update the server-side state for the Cell.
        String name = cellNameTextField.getText();
        CellServerState cellServerState = editor.getCellServerState();
        ((CellServerState) cellServerState).setName(name);
        editor.addToUpdateList(cellServerState);
        
        boolean collidable = collidableCB.isSelected();
        boolean selectable = selectableCB.isSelected();
        
        InteractionComponentServerState icss = (InteractionComponentServerState)
                cellServerState.getComponentServerState(InteractionComponentServerState.class);
                
        if (icss == null && collidable && selectable) {
            // if both collidable and selectable are the default, we don't
            // need to add the component
        } else if (icss == null) {
            // we need to add the component
            icss = new InteractionComponentServerState();
            icss.setCollidable(collidable);
            icss.setSelectable(selectable);
            addInteractionComponent(icss);
        } else {
            // update the interaction component
            icss.setCollidable(collidable);
            icss.setSelectable(selectable);
            editor.addToUpdateList(icss);
        }

        checkDirty();
    }

    /**
     * Add the interaction component by sending an add component message
     * to the cell.
     */
    private void addInteractionComponent(InteractionComponentServerState icss) {
        CellServerComponentMessage cscm =
                CellServerComponentMessage.newAddMessage(
                editor.getCell().getCellID(), icss);
        ResponseMessage response = editor.getCell().sendCellMessageAndWait(cscm);
        if (response instanceof ErrorMessage) {
            LOGGER.log(Level.WARNING, "Unable to add interaction component "
                    + "for Cell " + editor.getCell().getName() + " with ID "
                    + editor.getCell().getCellID(),
                    ((ErrorMessage) response).getErrorCause());
        }
    }

    /**
     * Inner class to listen for changes to the text field and fire off dirty
     * or clean indications to the cell properties editor.
     */
    class NameTextFieldListener implements DocumentListener {
        public void insertUpdate(DocumentEvent e) {
            checkDirty();
        }

        public void removeUpdate(DocumentEvent e) {
            checkDirty();
        }

        public void changedUpdate(DocumentEvent e) {
            checkDirty();
        }
    }

    private void checkDirty() {
        // see if the name has changed
        boolean dirty = !cellNameTextField.getText().equals(originalCellName);

        // see if the checkboxes match the server state
        dirty |= collidableCB.isSelected() != origCollidable;
        dirty |= selectableCB.isSelected() != origSelectable;

        if (editor != null) {
            editor.setPanelDirty(BasicJPanel.class, dirty);
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

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cellIDLabel = new javax.swing.JLabel();
        cellNameTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cellClassLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        collidableCB = new javax.swing.JCheckBox();
        selectableCB = new javax.swing.JCheckBox();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/celleditor/client/resources/Bundle"); // NOI18N
        jLabel1.setText(bundle.getString("BasicJPanel.jLabel1.text")); // NOI18N

        jLabel2.setText(bundle.getString("BasicJPanel.jLabel2.text")); // NOI18N

        cellIDLabel.setText(bundle.getString("BasicJPanel.cellIDLabel.text")); // NOI18N

        jLabel3.setText(bundle.getString("BasicJPanel.jLabel3.text")); // NOI18N

        cellClassLabel.setText(bundle.getString("BasicJPanel.cellClassLabel.text")); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("BasicJPanel.jPanel1.border.title"))); // NOI18N

        collidableCB.setSelected(true);
        collidableCB.setText(bundle.getString("BasicJPanel.collidableCB.text")); // NOI18N
        collidableCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                collidableCBActionPerformed(evt);
            }
        });

        selectableCB.setSelected(true);
        selectableCB.setText(bundle.getString("BasicJPanel.selectableCB.text")); // NOI18N
        selectableCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectableCBActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(collidableCB)
                    .add(selectableCB))
                .addContainerGap(126, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(collidableCB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(selectableCB)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .add(35, 35, 35)
                        .add(cellIDLabel))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel3)
                            .add(jLabel2))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(cellClassLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)
                            .add(cellNameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE)))
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(cellIDLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(cellNameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(cellClassLabel))
                .add(18, 18, 18)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(266, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void collidableCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_collidableCBActionPerformed
        checkDirty();
    }//GEN-LAST:event_collidableCBActionPerformed

    private void selectableCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectableCBActionPerformed
        checkDirty();
    }//GEN-LAST:event_selectableCBActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel cellClassLabel;
    private javax.swing.JLabel cellIDLabel;
    private javax.swing.JTextField cellNameTextField;
    private javax.swing.JCheckBox collidableCB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JCheckBox selectableCB;
    // End of variables declaration//GEN-END:variables
}
