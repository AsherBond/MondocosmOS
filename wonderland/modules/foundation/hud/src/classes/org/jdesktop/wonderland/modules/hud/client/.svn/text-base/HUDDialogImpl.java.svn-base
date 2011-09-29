/*
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
package org.jdesktop.wonderland.modules.hud.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.ImageIcon;
import org.jdesktop.wonderland.client.hud.HUDDialog.BUTTONS;
import org.jdesktop.wonderland.client.hud.HUDDialog.DIALOG_MODE;
import org.jdesktop.wonderland.client.hud.HUDDialog.MESSAGE_TYPE;

/**
 * A versatile input/message dialog.
 *
 * @author nsimpson
 */
public class HUDDialogImpl extends javax.swing.JPanel {

    private PropertyChangeSupport listeners;
    private static final Color MESSAGE_COLOR = Color.white;
    private static final Color DIALOG_COLOR = Color.black;
    private Font defaultFont;
    private Font messageFont;
    private Font dialogFont;
    private FontMetrics fontMetrics;
    private DIALOG_MODE mode = DIALOG_MODE.MESSAGE;
    private MESSAGE_TYPE type = MESSAGE_TYPE.INFO;
    private BUTTONS buttons = BUTTONS.NONE;

    public HUDDialogImpl() {
        initComponents();

        defaultFont = messageLabel.getFont();
        messageFont = messageLabel.getFont().deriveFont(
                messageLabel.getFont().getStyle() | Font.BOLD,
                messageLabel.getFont().getSize() + 5);
        dialogFont = messageLabel.getFont().deriveFont(
                messageLabel.getFont().getStyle() | Font.BOLD,
                messageLabel.getFont().getSize());
    }

    public void setMode(DIALOG_MODE mode) {
        this.mode = mode;
        switch (mode) {
            case MESSAGE:
                valueTextField.setVisible(false);
                messageLabel.setFont(messageFont);
                messageLabel.setForeground(MESSAGE_COLOR);
                break;
            case INPUT:
                valueTextField.setVisible(true);
                messageLabel.setFont(dialogFont);
                messageLabel.setForeground(DIALOG_COLOR);
                break;
            default:
                break;
        }
        validate();
    }

    public DIALOG_MODE getMode() {
        return mode;
    }

    public void setType(MESSAGE_TYPE type) {
        this.type = type;
        switch (type) {
            case INFO:
                iconLabel.setIcon(new ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/hud/client/resources/info24x24.png"))); // NOI18N
                break;
            case WARNING:
                iconLabel.setIcon(new ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/hud/client/resources/warning24x24.png"))); // NOI18N
                break;
            case ERROR:
                iconLabel.setIcon(new ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/hud/client/resources/error24x24.png"))); // NOI18N
                break;
            case QUERY:
                iconLabel.setIcon(new ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/hud/client/resources/query24x24.png"))); // NOI18N
                break;
            default:
                break;
        }
        validate();
    }

    public MESSAGE_TYPE getType() {
        return type;
    }

    public void setButtons(BUTTONS buttons) {
        this.buttons = buttons;
        switch (buttons) {
            case NONE:
                okButton.setVisible(false);
                cancelButton.setVisible(false);
                break;
            case OK:
                okButton.setVisible(true);
                cancelButton.setVisible(false);
                break;
            case OK_CANCEL:
                okButton.setVisible(true);
                cancelButton.setVisible(true);
                break;
            default:
                break;
        }
        validate();
    }

    public BUTTONS getButtons() {
        return buttons;
    }

    private void resizeToFit(String text) {
        fontMetrics = messageLabel.getFontMetrics(messageLabel.getFont());
        int width = (int) (4 + iconLabel.getPreferredSize().getWidth() + 4 + fontMetrics.stringWidth(text) + 15);
        setPreferredSize(new Dimension(width, (int) getPreferredSize().getHeight()));
        validate();
    }

    private void setMessageFont(Font font) {
        messageLabel.setFont(font);
    }

    /**
     * Sets the string to be displayed on the text field label
     * @param text the string to display
     */
    public void setMessage(String text) {
        messageLabel.setText(text);
        if (mode == DIALOG_MODE.MESSAGE) {
            resizeToFit(text);
        }
    }

    /**
     * Gets the string displayed on the text field label
     * @return the text field label
     */
    public String getMessage() {
        return messageLabel.getText();
    }

    /**
     * Sets the string displayed in the text field
     * @param text the string to display
     */
    public void setValue(String text) {
        valueTextField.setText(text);
    }

    /**
     * Gets the string entered by the user in the text field
     * @return the text field string
     */
    public String getValue() {
        return valueTextField.getText();
    }

    /**
     * Displays or hides the dialog and resets the value field
     * @param visible if true displays the dialog, if false, hides the dialog
     */
    @Override
    public void setVisible(boolean visible) {
        if (visible == true) {
            valueTextField.setText(null);
        }
        super.setVisible(visible);
    }

    /**
     * Sets the label on the confirm (OK) button
     * @param label the label to display
     */
    public void setConfirmButtonLabel(String label) {
        okButton.setText(label);
    }

    /**
     * Gets the label on the confirm (OK) button
     * @return the label displayed on the button (defaults to "OK")
     */
    public String getConfirmButtonLabel() {
        return okButton.getText();
    }

    /**
     * Sets the label on the cancel button
     * @param label the label to display
     */
    public void setCancelButtonLabel(String label) {
        cancelButton.setText(label);
    }

    /**
     * Gets the label on the cancel button
     * @return the label displayed on the button (defaults to "Cancel")
     */
    public String getCancelButtonLabel() {
        return cancelButton.getText();
    }

    /**
     * Adds a bound property listener to the dialog
     * @param listener a listener for dialog events
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (listeners == null) {
            listeners = new PropertyChangeSupport(this);
        }
        listeners.addPropertyChangeListener(listener);
    }

    /**
     * Removes a bound property listener from the dialog
     * @param listener the listener to remove
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        if (listeners != null) {
            listeners.removePropertyChangeListener(listener);
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

        iconLabel = new javax.swing.JLabel();
        valueTextField = new javax.swing.JTextField();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        messageLabel = new javax.swing.JLabel();

        setBackground(new java.awt.Color(208, 208, 208));

        iconLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/hud/client/resources/info24x24.png"))); // NOI18N

        valueTextField.setFont(valueTextField.getFont());
        valueTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                valueTextFieldActionPerformed(evt);
            }
        });

        cancelButton.setFont(cancelButton.getFont());
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/hud/client/resources/Bundle"); // NOI18N
        cancelButton.setText(bundle.getString("CANCEL")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setFont(okButton.getFont());
        okButton.setText(bundle.getString("OK")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        messageLabel.setBackground(new java.awt.Color(208, 208, 208));
        messageLabel.setText(bundle.getString("YOUR_MESSAGE_HERE")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(iconLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(messageLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                            .add(valueTextField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)))
                    .add(layout.createSequentialGroup()
                        .add(cancelButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(okButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(messageLabel)
                    .add(iconLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(valueTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(cancelButton)
                    .add(okButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void valueTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_valueTextFieldActionPerformed
        okButton.doClick();
}//GEN-LAST:event_valueTextFieldActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        listeners.firePropertyChange("cancel", "", null);
}//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        listeners.firePropertyChange("ok", "", valueTextField.getText());
}//GEN-LAST:event_okButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel iconLabel;
    private javax.swing.JLabel messageLabel;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField valueTextField;
    // End of variables declaration//GEN-END:variables
}
