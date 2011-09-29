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
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jdesktop.wonderland.client.cell.properties.CellPropertiesEditor;
import org.jdesktop.wonderland.client.cell.properties.annotation.PropertiesFactory;
import org.jdesktop.wonderland.client.cell.properties.spi.PropertiesFactorySPI;
import org.jdesktop.wonderland.common.cell.component.state.CellPhysicsPropertiesComponentServerState;
import org.jdesktop.wonderland.common.cell.component.state.PhysicsProperties;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.common.cell.state.CellServerState;

/**
 *
 * @author Paul Byrne
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
//@PropertiesFactory(CellPhysicsPropertiesComponentServerState.class)
public class CellPhysicsPropertiesComponentProperties
        extends JPanel implements PropertiesFactorySPI {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/celleditor/client/resources/Bundle");
    private CellPropertiesEditor editor;
    private float originalMass;

    /** Creates new form SampleComponentProperties */
    public CellPhysicsPropertiesComponentProperties() {
        // Initialize the GUI
        initComponents();

        // Listen for changes to the Spinner
        massSpinner.getModel().addChangeListener(new SpinnerChangeListener());
    }

    /**
     * @inheritDoc()
     */
    public Class getServerCellComponentClass() {
        return CellPhysicsPropertiesComponentServerState.class;
    }

    /**
     * @inheritDoc()
     */
    public String getDisplayName() {
        return BUNDLE.getString("Physics_Properties");
    }

    /**
     * @inheritDoc()
     */
    public JPanel getPropertiesJPanel(CellPropertiesEditor editor) {
        this.editor = editor;
        return this;
    }

    /**
     * @inheritDoc()
     */
    public <T extends CellServerState> void updateGUI(T cellServerState) {
        CellComponentServerState state =
                cellServerState.getComponentServerState(
                CellPhysicsPropertiesComponentServerState.class);
        if (state != null) {
            originalMass =
                    ((CellPhysicsPropertiesComponentServerState) state).getPhyiscsProperties(
                    CellPhysicsPropertiesComponentServerState.DEFAULT_NAME).
                    getMass();
            massSpinner.getModel().setValue(originalMass);
            return;
        }
    }

    /**
     * @inheritDoc()
     */
    public <T extends CellServerState> void getCellServerState(
            T cellServerState) {
        // Figure out whether there already exists a server state for the
        // component.
        CellPhysicsPropertiesComponentServerState state =
                (CellPhysicsPropertiesComponentServerState) cellServerState.getComponentServerState(
                CellPhysicsPropertiesComponentServerState.class);
        if (state == null) {
            state = new CellPhysicsPropertiesComponentServerState();
        }

        PhysicsProperties p = state.getPhyiscsProperties(
                CellPhysicsPropertiesComponentServerState.DEFAULT_NAME);
        if (p == null) {
            p = new PhysicsProperties();
            state.addPhysicsProperties(
                    CellPhysicsPropertiesComponentServerState.DEFAULT_NAME, p);
        }

        p.setMass(((Float) massSpinner.getModel().getValue()).floatValue());
        cellServerState.addComponentServerState(state);
    }

    public void setCellPropertiesEditor(CellPropertiesEditor editor) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public JPanel getPropertiesJPanel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void open() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void close() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void restore() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void apply() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Inner class to listen for changes to the text field and fire off dirty
     * or clean indications to the cell properties editor.
     */
    class SpinnerChangeListener implements ChangeListener {

        private void checkDirty() {
            if (editor != null) {
                Object value = massSpinner.getModel().getValue();
                float mass = ((Float) value).floatValue();
                editor.setPanelDirty(
                        CellPhysicsPropertiesComponentProperties.class,
                        mass != originalMass);
            }
        }

        public void stateChanged(ChangeEvent e) {
            checkDirty();
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
        massSpinner = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/celleditor/client/resources/Bundle"); // NOI18N
        jLabel1.setText(bundle.getString("CellPhysicsPropertiesComponentProperties.jLabel1.text")); // NOI18N

        massSpinner.setModel(new javax.swing.SpinnerNumberModel(Float.valueOf(1.0f), null, null, Float.valueOf(0.5f)));

        jLabel2.setText(bundle.getString("CellPhysicsPropertiesComponentProperties.jLabel2.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(massSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 66, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel2)
                .addContainerGap(215, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(massSpinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addContainerGap(247, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JSpinner massSpinner;
    // End of variables declaration//GEN-END:variables
}
