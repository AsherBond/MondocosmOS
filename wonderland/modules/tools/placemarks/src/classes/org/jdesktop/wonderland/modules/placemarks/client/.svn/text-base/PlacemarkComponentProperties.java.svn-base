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

import java.util.ResourceBundle;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jdesktop.wonderland.client.cell.properties.CellPropertiesEditor;
import org.jdesktop.wonderland.client.cell.properties.annotation.PropertiesFactory;
import org.jdesktop.wonderland.client.cell.properties.spi.PropertiesFactorySPI;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.modules.placemarks.common.PlacemarkComponentServerState;

/**
 * Edit the placemark component
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
@PropertiesFactory(PlacemarkComponentServerState.class)
public class PlacemarkComponentProperties extends JPanel
        implements PropertiesFactorySPI {
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/placemarks/client/resources/Bundle");

    private CellPropertiesEditor editor;
    private String origName;
    private String origRotation;

    /** Creates new form PortalComponentProperties */
    public PlacemarkComponentProperties() {
        // Initialize the GUI
        initComponents();

        // Listen for changes to the text fields
        TextFieldListener listener = new TextFieldListener();
        nameTF.getDocument().addDocumentListener(listener);
        rotationTF.getDocument().addDocumentListener(listener);
    }

    /**
     * @inheritDoc()
     */
    public String getDisplayName() {
        return BUNDLE.getString("Placemark");
    }

    /**
     * @inheritDoc()
     */
    public JPanel getPropertiesJPanel() {
        return this;
    }

    public void setCellPropertiesEditor(CellPropertiesEditor editor) {
        this.editor = editor;
    }

    /**
     * @inheritDoc()
     */
    public void open() {
        CellServerState cellServerState = editor.getCellServerState();
        PlacemarkComponentServerState state =
                (PlacemarkComponentServerState) cellServerState.getComponentServerState(
                PlacemarkComponentServerState.class);
        if (state != null) {
            origName = state.getPlacemarkName();
            origRotation = state.getPlacemarkRotation();
        }

        if (origName == null) {
            origName = "";
        }

        nameTF.setText(origName);

        if (origRotation == null) {
            origRotation = "0";
        }

        rotationTF.setText(origRotation);
    }

    /**
     * @inheritDoc()
     */
    public void close() {
        // Do nothing
    }

    /**
     * @inheritDoc()
     */
    public void apply() {
        // Figure out whether there already exists a server state for the
        // component.
        CellServerState cellServerState = editor.getCellServerState();
        PlacemarkComponentServerState state =
                (PlacemarkComponentServerState) cellServerState.getComponentServerState(
                PlacemarkComponentServerState.class);
        if (state == null) {
            state = new PlacemarkComponentServerState();
        }

        String name = nameTF.getText().trim();
        String rotation = rotationTF.getText().trim();

        if (name.length() == 0) {
            name = null;
        }

        if (rotation.length() == 0) {
            rotation = null;
        }

        state.setPlacemarkName(name);
        state.setPlacemarkRotation(rotation);

        editor.addToUpdateList(state);
    }

    /**
     * @inheritDoc()
     */
    public void restore() {
        // Restore from the originally stored values.
        nameTF.setText(origName);
        rotationTF.setText(origRotation);
    }

    private void checkDirty() {
        if (editor == null) {
            return;
        }

        boolean clean = nameTF.getText().equals(origName);
        clean = clean & rotationTF.getText().equals(origRotation);//////////////////////////////???????????????????????????
        editor.setPanelDirty(PlacemarkComponentProperties.class, !clean);
    }

    /**
     * Inner class to listen for changes to the text field and fire off dirty
     * or clean indications to the cell properties editor.
     */
    class TextFieldListener implements DocumentListener {

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

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nameTF = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        rotationTF = new javax.swing.JTextField();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/placemarks/client/resources/Bundle"); // NOI18N
        jLabel1.setText(bundle.getString("PlacemarkComponentProperties.jLabel1.text_1")); // NOI18N

        jLabel2.setText(bundle.getString("PlacemarkComponentProperties.jLabel2.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(rotationTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 52, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(nameTF, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(nameTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(rotationTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField nameTF;
    private javax.swing.JTextField rotationTF;
    // End of variables declaration//GEN-END:variables
}
