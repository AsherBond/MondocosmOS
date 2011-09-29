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

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Set;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import org.jdesktop.wonderland.modules.placemarks.api.common.Placemark;

/**
 * A dialog box so that users can add a Placemark.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class AddEditPlacemarkJDialog
        extends JDialog implements DocumentListener {

    public static final int RET_CANCEL = 0;
    public static final int RET_OK = 1;
    private int returnStatus = RET_CANCEL;
    private String name = null;
    private String serverURL = null;
    private float x = 0.0f;
    private float y = 0.0f;
    private float z = 0.0f;
    private float angle = 0.0f;
    private DecimalFormat df;
    private Set<Placemark> placemarkSet = null;
    // If we are editing a placemark, we pass in the current placemark to be
    // edited.
    private Placemark currentPlacemark = null;

    /** 
     * Creates a new dialog with empty initial values
     */
    public AddEditPlacemarkJDialog(JFrame frame, boolean modal,
            Set<Placemark> placemarks) {
        super(frame, modal);
        placemarkSet = placemarks;
        initComponents();

        df = (DecimalFormat)DecimalFormat.getNumberInstance();
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
        xTextField.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(df)));
        yTextField.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(df)));
        zTextField.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(df)));
        angleTextField.setFormatterFactory(new DefaultFormatterFactory(new NumberFormatter(df)));

        // Set the initial values for the fields
        nameTextField.setText("");
        urlTextField.setText("");
        xTextField.setText("0");
        yTextField.setText("0");
        zTextField.setText("0");
        angleTextField.setText("0");

        // Listen for changes in the text fields, to enable/disable buttons,
        // etc.
        nameTextField.getDocument().addDocumentListener(this);
        urlTextField.getDocument().addDocumentListener(this);
    }

    /**
     * Creates a new dialog with initial values found in the Placemark
     */
    public AddEditPlacemarkJDialog(JFrame frame, boolean model,
            Placemark placemark, Set<Placemark> placemarks) {
        this(frame, model, placemarks);
        currentPlacemark = placemark;

        nameTextField.setText(placemark.getName());
        urlTextField.setText(placemark.getUrl());
        xTextField.setValue(placemark.getX());
        yTextField.setValue(placemark.getY());
        zTextField.setValue(placemark.getZ());
        angleTextField.setValue(placemark.getAngle());
    }

    public String getPlacemarkName() {
        return name;
    }

    public String getServerURL() {
        return serverURL;
    }

    public float getLocationX() {
        return x;
    }

    public float getLocationY() {
        return y;
    }

    public float getLocationZ() {
        return z;
    }

    public float getLookAtAngle() {
        return angle;
    }

    /**
     * @return the return status of this dialog - one of RET_OK or RET_CANCEL
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    /**
     * @inheritDoc()
     */
    public void insertUpdate(DocumentEvent e) {
        textFieldsUpdated();
    }

    /**
     * @inheritDoc()
     */
    public void removeUpdate(DocumentEvent e) {
        textFieldsUpdated();
    }

    /**
     * @inheritDoc()
     */
    public void changedUpdate(DocumentEvent e) {
        textFieldsUpdated();
    }

    /**
     * Handles when changes are made to the text fields: enables buttons if
     * text fiels are not null, etc.
     */
    private void textFieldsUpdated() {
        String tmpName = nameTextField.getText();
        String tmpURL = urlTextField.getText();

        // The OK button can only be abled if both text fields are not null
        boolean enabled = !tmpName.equals("") && !tmpURL.equals("");
        okButton.setEnabled(enabled);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        urlTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        xTextField = new javax.swing.JFormattedTextField();
        yTextField = new javax.swing.JFormattedTextField();
        zTextField = new javax.swing.JFormattedTextField();
        angleTextField = new javax.swing.JFormattedTextField();

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/placemarks/client/resources/Bundle"); // NOI18N
        setTitle(bundle.getString("AddEditPlacemarkJDialog.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        okButton.setText(bundle.getString("AddEditPlacemarkJDialog.okButton.text")); // NOI18N
        okButton.setEnabled(false);
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(bundle.getString("AddEditPlacemarkJDialog.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jLabel1.setFont(jLabel1.getFont().deriveFont(jLabel1.getFont().getStyle() | java.awt.Font.BOLD));
        jLabel1.setText(bundle.getString("AddEditPlacemarkJDialog.jLabel1.text")); // NOI18N

        jLabel2.setText(bundle.getString("AddEditPlacemarkJDialog.jLabel2.text")); // NOI18N

        nameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nameTextFieldActionPerformed(evt);
            }
        });

        jLabel3.setText(bundle.getString("AddEditPlacemarkJDialog.jLabel3.text")); // NOI18N

        urlTextField.setEditable(false);

        jLabel4.setText(bundle.getString("AddEditPlacemarkJDialog.jLabel4.text")); // NOI18N

        jLabel5.setText(bundle.getString("AddEditPlacemarkJDialog.jLabel5.text")); // NOI18N

        jLabel6.setText(bundle.getString("AddEditPlacemarkJDialog.jLabel6.text")); // NOI18N

        jLabel7.setText(bundle.getString("AddEditPlacemarkJDialog.jLabel7.text")); // NOI18N

        jLabel8.setText(bundle.getString("AddEditPlacemarkJDialog.jLabel8.text")); // NOI18N

        jLabel9.setText(bundle.getString("AddEditPlacemarkJDialog.jLabel9.text")); // NOI18N

        xTextField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        xTextField.setText(bundle.getString("AddEditPlacemarkJDialog.xTextField.text")); // NOI18N

        yTextField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        yTextField.setText(bundle.getString("AddEditPlacemarkJDialog.yTextField.text")); // NOI18N

        zTextField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        zTextField.setText(bundle.getString("AddEditPlacemarkJDialog.zTextField.text")); // NOI18N

        angleTextField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                        .addContainerGap())
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 101, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel5))
                            .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 119, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel8, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 119, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 119, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(angleTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel9))
                            .add(layout.createSequentialGroup()
                                .add(xTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel6)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(yTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jLabel7)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(zTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(urlTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
                            .add(nameTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE))
                        .add(8, 8, 8))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(cancelButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(okButton)
                        .addContainerGap())))
        );

        layout.linkSize(new java.awt.Component[] {yTextField, zTextField}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(nameTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(urlTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel3))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(jLabel5)
                    .add(xTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel6)
                    .add(yTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel7)
                    .add(zTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel8)
                    .add(angleTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel9))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(okButton)
                    .add(cancelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        // Check to see that a name has been entered in the GUI, if not, flag
        // an error, but leave the dialog open
        name = nameTextField.getText();
        if (name == null || name.equals("")) {
            String msg = "Please enter a name for the Placemark.";
            JOptionPane.showMessageDialog(this, msg);
            return;
        }

        // Check to see that no other entry has the name. If there is
        // another entry, note an error and do not close the dialog box.
        if (isNameDuplicate(name)) {
            String msg = "Another Placemark already exists with the same " +
                    "name. Please choose another.";
            JOptionPane.showMessageDialog(this, msg);
            return;
        }

        // Update the member variables with the values in the text fields
        serverURL = urlTextField.getText();
        x = parseFloatString(xTextField.getText());
        y = parseFloatString(yTextField.getText());
        z = parseFloatString(zTextField.getText());
        angle = parseFloatString(angleTextField.getText());

        doClose(RET_OK);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        doClose(RET_CANCEL);
    }//GEN-LAST:event_closeDialog

    private void nameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nameTextFieldActionPerformed
        if (nameTextField.getText().length() > 0) {
            okButton.doClick();
        }
    }//GEN-LAST:event_nameTextFieldActionPerformed

    private void doClose(int retStatus) {
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    /**
     * Attempts to parse a String as a floating point value. If valid, returns
     * the floating point value, if not, returns 0.0. Properly handles null and
     * empty strings too.
     */
    private float parseFloatString(String floatStr) {
        // If the given String is null or an empty return, return 0.0 right away
        if (floatStr == null || floatStr.equals("")) {
            return 0.0f;
        }

        // Otherwise, try to parse it and return it
        try {
            return df.parse(floatStr).floatValue();
            //return Float.parseFloat(floatStr);
        } catch (ParseException excp) {
            return 0.0f;
        }
    }

    /**
     * Returns true if the given placemark name equals another name in the
     * current set of known placemarks and is not the same name as the currently
     * edited placemark.
     */
    private boolean isNameDuplicate(String name) {
        // First check to see if we are currently editing a placemark. And if
        // so and the name is the same name as it, then return false.
        if ((currentPlacemark != null) &&
                name.equals(currentPlacemark.getName())) {
            return false;
        }

        // Otherwise, loop through the list of all known placemark names and
        // check to see whether it equals one of those.
        for (Placemark placemark : placemarkSet) {
            String placemarkName = placemark.getName();
            if (placemarkName.equals(name)) {
                return true;
            }
        }
        return false;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField angleTextField;
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField urlTextField;
    private javax.swing.JFormattedTextField xTextField;
    private javax.swing.JFormattedTextField yTextField;
    private javax.swing.JFormattedTextField zTextField;
    // End of variables declaration//GEN-END:variables
}
