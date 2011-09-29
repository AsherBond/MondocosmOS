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
package org.jdesktop.wonderland.modules.viewproperties.client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ResourceBundle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jdesktop.wonderland.client.jme.ViewManager;
import org.jdesktop.wonderland.client.jme.ViewProperties;

/**
 * A dialog box to configure the view properties (e.g. field-of-view, front
 * and back clip).
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class ViewPropertiesJDialog extends javax.swing.JDialog {

    private static final Logger LOGGER =
            Logger.getLogger(ViewPropertiesJDialog.class.getName());
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/viewproperties/client/Bundle");
    // The original values for the field-of-view, front/back clip to use upon
    // revert.
    private int originalFieldOfView = 0;
    private int originalFrontClip = 0;
    private int originalBackClip = 0;
    // The view manager's properties
    private ViewProperties viewProperties = null;
    // This boolean indicates whether the values of the sliders and text fields
    // are being set programmatically. In such a case, we do not want to
    // generate an event.
    private boolean setTextFieldsLocal = false;
    private boolean setSlidersLocal = false;

    /** Creates new form ViewControls */
    public ViewPropertiesJDialog() {
        initComponents();
        setTitle(BUNDLE.getString("View_Properties"));

        // Fetch the view properties object
        ViewManager manager = ViewManager.getViewManager();
        viewProperties = manager.getViewProperties();

        // Listen for when the field-of-view slider value is changed and update
        // the text field.
        fovSlider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                // Update the value of the text field and also the view
                // properties. We only do this if the value of the slider has
                // changed via the GUI and not programmatically.
                LOGGER.fine("Fov Slider State Changed, setSlidersLocal=" +
                        setSlidersLocal);
                if (!setSlidersLocal) {
                    // Says that we are changing the vlaues of the text field
                    // programatically. This prevents extra events being
                    // generated when manually setting the text field values.
                    LOGGER.fine("Fov Slider State Changed, setting fov text " +
                            "field value");
                    setTextFieldsLocal = true;
                    try {
                        int fov = fovSlider.getValue();
                        fovField.setValue((long) fov);
                        viewProperties.setFieldOfView((float) fov);
                        setApplyEnabled();
                    } finally {
                        setTextFieldsLocal = false;
                    }
                }
            }
        });

        // Listen for when focus is lost of the field-of-view and update the
        // value. Note that if the Close/Apply button are clicked, a focus
        // lost event is first generated.
        fovField.addPropertyChangeListener("value", new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                // We only care about these events if we are not setting the
                // text field values programmatically.
                LOGGER.fine("Fov Textfield Property Changed, " +
                        "setTextFieldsLocal=" + setTextFieldsLocal);
                if (!setTextFieldsLocal) {
                    // Says that we are changing the values of the sliders
                    // programmatically. This prevents extra events being
                    // generated when manually setting the slider values
                    LOGGER.fine("Fov Textfield Property Changed, setting fov " +
                            "slider value");
                    setSlidersLocal = true;
                    try {
                        long fovValue = (Long) fovField.getValue();
                        fovSlider.setValue((int) fovValue);
                        viewProperties.setFieldOfView((float) fovValue);
                        setApplyEnabled();
                    } finally {
                        setSlidersLocal = false;
                    }
                }
            }
        });

        // Listen for when the front clip slider value is changed and update
        // the text field.
        frontClipSlider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                // Update the value of the text field and also the view
                // properties. We only do this if the value of the slider has
                // changed via the GUI and not programmatically.
                LOGGER.fine("Front Clip Slider State Changed, " +
                        "setSlidersLocal=" + setSlidersLocal);
                if (!setSlidersLocal) {
                    // Says that we are changing the vlaues of the text field
                    // programatically. This prevents extra events being
                    // generated when manually setting the text field values.
                    LOGGER.fine("Front Clip Slider State Changed, setting " +
                            "fov text field value");
                    setTextFieldsLocal = true;
                    try {
                        int clip = frontClipSlider.getValue();
                        frontClipField.setValue((long) clip);
                        viewProperties.setFrontClip((float) clip);
                        setApplyEnabled();
                    } finally {
                        setTextFieldsLocal = false;
                    }
                }
            }
        });

        // Listen for when focus is lost of the front clip field and update the
        // value. Note that if the Close/Apply button are clicked, a focus
        // lost event is first generated.
        frontClipField.addPropertyChangeListener("value", new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                // We only care about these events if we are not setting the
                // text field values programmatically.
                LOGGER.fine("Front Clip Textfield Property Changed, " +
                        "setTextFieldsLocal=" + setTextFieldsLocal);
                if (!setTextFieldsLocal) {
                    // Says that we are changing the values of the sliders
                    // programmatically. This prevents extra events being
                    // generated when manually setting the slider values
                    LOGGER.fine("Front Clip Textfield Property Changed, " +
                            "setting front clip slider value");
                    setSlidersLocal = true;
                    try {
                        long clipValue = (Long) frontClipField.getValue();
                        frontClipSlider.setValue((int) clipValue);
                        viewProperties.setFrontClip((float) clipValue);
                        setApplyEnabled();
                    } finally {
                        setSlidersLocal = false;
                    }
                }
            }
        });

        // Listen for when the rear clip slider value is changed and update
        // the text field.
        rearClipSlider.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                // Update the value of the text field and also the view
                // properties. We only do this if the value of the slider has
                // changed via the GUI and not programmatically.
                LOGGER.fine("Rear Clip Slider State Changed, " +
                        "setSlidersLocal=" + setSlidersLocal);
                if (!setSlidersLocal) {
                    // Says that we are changing the vlaues of the text field
                    // programatically. This prevents extra events being
                    // generated when manually setting the text field values.
                    LOGGER.fine("Rear Clip Slider State Changed, setting fov " +
                            "text field value");
                    setTextFieldsLocal = true;
                    try {
                        int clip = rearClipSlider.getValue();
                        rearClipField.setValue((long) clip);
                        viewProperties.setBackClip((float) clip);
                        setApplyEnabled();
                    } finally {
                        setTextFieldsLocal = false;
                    }
                }
            }
        });

        // Listen for when focus is lost of the read cip field and update the
        // value. Note that if the Close/Apply button are clicked, a focus
        // lost event is first generated.
        rearClipField.addPropertyChangeListener("value", new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                // We only care about these events if we are not setting the
                // text field values programmatically.
                LOGGER.fine("Rear Clip Textfield Property Changed, " +
                        "setTextFieldsLocal=" + setTextFieldsLocal);
                if (!setTextFieldsLocal) {
                    // Says that we are changing the values of the sliders
                    // programmatically. This prevents extra events being
                    // generated when manually setting the slider values
                    LOGGER.fine("Rear Clip Textfield Property Changed, " +
                            "setting rear clip slider value");
                    setSlidersLocal = true;
                    try {
                        long clipValue = (Long) rearClipField.getValue();
                        rearClipSlider.setValue((int) clipValue);
                        viewProperties.setBackClip((float) clipValue);
                        setApplyEnabled();
                    } finally {
                        setSlidersLocal = false;
                    }
                }
            }
        });

        // Listen for when the window is closing. We will pop up a confirm
        // dialog to see if the user wants to keep the current values.
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                if (isDirty()) {
                    int result = JOptionPane.showConfirmDialog(
                            ViewPropertiesJDialog.this,
                            BUNDLE.getString("Apply_Message"),
                            BUNDLE.getString("Apply_Title"),
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
                    if (result == JOptionPane.YES_OPTION) {
                        okButtonActionPerformed(null);
                    } else {
                        // Reset the view to the original values and close the
                        // window. We do not dispose since we may want to use
                        // the dialog again.
                        viewProperties.setFieldOfView(originalFieldOfView);
                        viewProperties.setFrontClip(originalFrontClip);
                        viewProperties.setBackClip(originalBackClip);
                        setVisible(false);
                    }
                    return;
                }
                setVisible(false);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setVisible(boolean isVisible) {

        // If we are making the dialog visible, then store the original values
        // from the view manager's properties
        if (isVisible) {
            originalFieldOfView = (int) viewProperties.getFieldOfView();
            originalFrontClip = (int) viewProperties.getFrontClip();
            originalBackClip = (int) viewProperties.getBackClip();
            updateGUI();
            setApplyEnabled();
        }
        super.setVisible(isVisible);
    }

    /**
     * Updates the GUI with the original values.
     *
     * NOTE: This method assumes it is being called within the AWT Event Thread.
     */
    private void updateGUI() {
        // Says that we are changing the values of the sliders programmatically.
        // This prevents extra events being generated when manually setting the
        // slider values
        setTextFieldsLocal = true;
        setSlidersLocal = true;
        try {
            fovSlider.setValue((int) originalFieldOfView);
            fovField.setValue((long) originalFieldOfView);
            frontClipSlider.setValue((int) originalFrontClip);
            frontClipField.setValue((long) originalFrontClip);
            rearClipSlider.setValue((int) originalBackClip);
            rearClipField.setValue((long) originalBackClip);
        } finally {
            setTextFieldsLocal = false;
            setSlidersLocal = false;
        }
    }

    /**
     * Sets whether the Apply button is enabled depending upon whether changes
     * have been made to the GUI
     */
    private void setApplyEnabled() {
        okButton.setEnabled(isDirty());
    }

    /**
     * Returns true if changes have been made to the GUI different from the
     * original values.
     */
    private boolean isDirty() {
        long fovValue = (Long) fovField.getValue();
        if ((int) fovValue != originalFieldOfView) {
            return true;
        }

        long frontClipValue = (Long) frontClipField.getValue();
        if ((int) frontClipValue != originalFrontClip) {
            return true;
        }

        long rearClipValue = (Long) rearClipField.getValue();
        if ((int) rearClipValue != originalBackClip) {
            return true;
        }
        return false;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        mainPanel = new javax.swing.JPanel();
        fovLabel = new javax.swing.JLabel();
        fovSlider = new javax.swing.JSlider();
        frontClipLabel = new javax.swing.JLabel();
        rearClipSlider = new javax.swing.JSlider();
        frontClipSlider = new javax.swing.JSlider();
        rearClipLabel = new javax.swing.JLabel();
        fovField = new javax.swing.JFormattedTextField();
        frontClipField = new javax.swing.JFormattedTextField();
        rearClipField = new javax.swing.JFormattedTextField();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/viewproperties/client/Bundle"); // NOI18N
        okButton.setText(bundle.getString("ViewPropertiesJDialog.okButton.text")); // NOI18N
        okButton.setEnabled(false);
        okButton.setSelected(true);
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(bundle.getString("ViewPropertiesJDialog.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.setLayout(new java.awt.GridBagLayout());

        fovLabel.setText(bundle.getString("ViewPropertiesJDialog.fovLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        mainPanel.add(fovLabel, gridBagConstraints);

        fovSlider.setMaximum(180);
        fovSlider.setMinimum(30);
        fovSlider.setValue(0);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        mainPanel.add(fovSlider, gridBagConstraints);

        frontClipLabel.setText(bundle.getString("ViewPropertiesJDialog.frontClipLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        mainPanel.add(frontClipLabel, gridBagConstraints);

        rearClipSlider.setMaximum(5000);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        mainPanel.add(rearClipSlider, gridBagConstraints);

        frontClipSlider.setMaximum(1000);
        frontClipSlider.setValue(0);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        mainPanel.add(frontClipSlider, gridBagConstraints);

        rearClipLabel.setText(bundle.getString("ViewPropertiesJDialog.rearClipLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        mainPanel.add(rearClipLabel, gridBagConstraints);

        fovField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        fovField.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.25;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        mainPanel.add(fovField, gridBagConstraints);

        frontClipField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        frontClipField.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.25;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        mainPanel.add(frontClipField, gridBagConstraints);

        rearClipField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        rearClipField.setText("0");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.25;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 3, 3);
        mainPanel.add(rearClipField, gridBagConstraints);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap(346, Short.MAX_VALUE)
                .add(cancelButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(okButton)
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, mainPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(mainPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 121, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(okButton)
                    .add(cancelButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        // Save the current properties to the user's local repository
        try {
            ViewPropertiesUtils.saveViewProperties(viewProperties);
        } catch (java.lang.Exception excp) {
            String msg = "Error writing properties to user's local repository.";
            String title = "Error Writing Properties";
            JOptionPane.showMessageDialog(
                    this, msg, title, JOptionPane.ERROR_MESSAGE);
            LOGGER.log(Level.WARNING, "Error writing properties file", excp);
        }

        // Close the window, we do not dispose since we may want to use the
        // dialog again.
        setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed

        // Check to see if the values have changed from the original values. If
        // so then ask whether we want to apply the changes or not.
        if (isDirty()) {
            int result = JOptionPane.showConfirmDialog(
                    ViewPropertiesJDialog.this,
                    BUNDLE.getString("Apply_Message"),
                    BUNDLE.getString("Apply_Title"),
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                okButtonActionPerformed(null);
            } else {
                // Reset the view to the original values and close the window.
                // We do not dispose since we may want to use the dialog again.
                viewProperties.setFieldOfView((float) originalFieldOfView);
                viewProperties.setFrontClip((float) originalFrontClip);
                viewProperties.setBackClip((float) originalBackClip);
                setVisible(false);
            }
            return;
        }
        setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JFormattedTextField fovField;
    private javax.swing.JLabel fovLabel;
    private javax.swing.JSlider fovSlider;
    private javax.swing.JFormattedTextField frontClipField;
    private javax.swing.JLabel frontClipLabel;
    private javax.swing.JSlider frontClipSlider;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton okButton;
    private javax.swing.JFormattedTextField rearClipField;
    private javax.swing.JLabel rearClipLabel;
    private javax.swing.JSlider rearClipSlider;
    // End of variables declaration//GEN-END:variables
}
