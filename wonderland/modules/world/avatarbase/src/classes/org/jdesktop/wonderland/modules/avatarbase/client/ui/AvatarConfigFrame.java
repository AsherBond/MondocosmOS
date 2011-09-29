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
package org.jdesktop.wonderland.modules.avatarbase.client.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.modules.avatarbase.client.imi.ImiAvatar;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarImiJME;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.AvatarRegistry;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.AvatarRegistry.AvatarInUseListener;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.AvatarRegistry.AvatarListener;
import org.jdesktop.wonderland.modules.avatarbase.client.registry.spi.AvatarSPI;

/**
 * A JFrame to manage the list of avatar configuration.
 *
 * @author paulby
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class AvatarConfigFrame extends javax.swing.JFrame {

    private static final Logger LOGGER =
            Logger.getLogger(AvatarConfigFrame.class.getName());

    private static final ResourceBundle bundle = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/avatarbase/client/resources/Bundle");

    // The image icons for the JList to display the avatar configurations
    private Icon checkBoxIcon = null;
    private Icon blankIcon = null;

    /** Creates new form AvatarConfigFrame */
    public AvatarConfigFrame() {

        // Initialize the GUI components
        initComponents();

        // Initialize the icons that are used for the JList
        URL url = AvatarListCellRenderer.class.getResource("resources/check_icon.png");
        checkBoxIcon = new ImageIcon(url);
        blankIcon = new Icon() {
            public void paintIcon(Component c, Graphics g, int x, int y) {
                // Do nothing
            }

            public int getIconWidth() {
                return 10;
            }

            public int getIconHeight() {
                return 10;
            }
        };

        // Populate the avatar list with the current list of avatars
        populateAvatarList();

        // Set the cell renderer for the JList so we add a checkbox next to
        // the currently selected avatar configuration
        avatarList.setCellRenderer(new AvatarListCellRenderer());

        // Listen for when an avatar has been added or remove from the list of
        // avatars and update the JList appropriately.
        AvatarRegistry registry = AvatarRegistry.getAvatarRegistry();
        registry.addAvatarListener(new AvatarListener() {
            public void avatarAdded(AvatarSPI avatar) {
                ((DefaultListModel) avatarList.getModel()).addElement(avatar);
            }

            public void avatarRemoved(AvatarSPI avatar) {
                ((DefaultListModel) avatarList.getModel()).removeElement(
                        avatar);
            }
        });

        // Listen for when a new avatar has been selected for use and repaint
        // the list. We do not need to do this in the AWT Event Thread because
        // repaint() just schedules something there
        registry.addAvatarInUseListener(new AvatarInUseListener() {
            public void avatarInUse(AvatarSPI avatar, boolean isLocal) {
                avatarList.repaint();
            }
        });

        // Listen for when the selection of the list of avatar changes, so we
        // update the state of buttons
        avatarList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    updateButtonEnabled();
                }
            }
        });
    }

    /**
     * Populates the list of configured avatar names.
     */
    private void populateAvatarList() {
        DefaultListModel listModel = (DefaultListModel) avatarList.getModel();
        AvatarRegistry registry = AvatarRegistry.getAvatarRegistry();
        
        // sort avatars by name
        Set<AvatarSPI> avatarSet = new TreeSet<AvatarSPI>(new Comparator<AvatarSPI>() {
            public int compare(AvatarSPI o1, AvatarSPI o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        avatarSet.addAll(registry.getAllAvatars());

        // We want to make sure the default avatar if the first, so add it. We
        // also need to remove it from the list so it does not get added twice.
        AvatarSPI defaultAvatar = registry.getDefaultAvatar();
        if (defaultAvatar != null) {
            listModel.addElement(defaultAvatar);
            avatarSet.remove(defaultAvatar);
        }
        
        // Next add all of the other avatars
        for (AvatarSPI avatar : avatarSet) {
            listModel.addElement(avatar);
        }
    }

    /**
     * Updates the state of the Customize, Delete, and Use buttons based upon
     * whether an item in the list is selected and whether it permits Customize
     * and Delete
     */
    private void updateButtonEnabled() {
        // First check to see if something is selected. Set the state of the
        // buttons based upon that
        boolean selected = avatarList.getSelectedIndex() != -1;
        customizeButton.setEnabled(selected);
        deleteButton.setEnabled(selected);
        useButton.setEnabled(selected);

        // Next, ask the selected avatar whether Customize and Delete is
        // supported
        if (selected == true) {
            AvatarSPI avatar = (AvatarSPI) avatarList.getSelectedValue();
            customizeButton.setEnabled(avatar.canConfigure());
            deleteButton.setEnabled(avatar.canDelete());
        }

        // Finally, we see if the select avatar is the one currently in use.
        // If so, do not enable the Use button
        if (selected == true) {
            AvatarSPI avatar = (AvatarSPI) avatarList.getSelectedValue();
            AvatarRegistry registry = AvatarRegistry.getAvatarRegistry();
            AvatarSPI avatarInUse = registry.getAvatarInUse();
            if ((avatarInUse != null) && avatarInUse.equals(avatar)) {
                useButton.setEnabled(false);
            }
        }
    }

    /**
     * A cell renderer for the list to put a checkbox next to the selected
     * avatar
     */
    class AvatarListCellRenderer extends JLabel implements ListCellRenderer {

        public Component getListCellRendererComponent(
                JList list, // the list
                Object value, // value to display
                int index, // cell index
                boolean isSelected, // is the cell selected
                boolean cellHasFocus) // does the cell have focus
        {
            // Fetch the avatar object from the list and fetch its name to use
            // as the label.
            AvatarSPI avatar = (AvatarSPI) value;
            String s = avatar.getName();
            setText(s);

            // From the avatar configuration manager, fetch the current avatar
            // in use.
            AvatarRegistry registry = AvatarRegistry.getAvatarRegistry();
            AvatarSPI avatarInUse = registry.getAvatarInUse();

            // If the avatar configuration is currently selected, then we
            // want to place a checkbox next to the name.
            if (avatar.equals(avatarInUse) == true) {
                setIcon(checkBoxIcon);
            } else {
                setIcon(blankIcon);
            }

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(Color.white);
                setForeground(Color.black);
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;
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

        genderGrou = new javax.swing.ButtonGroup();
        customiseFrame = new javax.swing.JFrame();
        jPanel2 = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        saveB = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        buttonPanel = new javax.swing.JPanel();
        newButton = new javax.swing.JButton();
        customizeButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        useButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        avatarListScrollPane = new javax.swing.JScrollPane();
        avatarList = new javax.swing.JList();

        jPanel2.setLayout(new java.awt.BorderLayout());
        jPanel2.add(scrollPane, java.awt.BorderLayout.CENTER);

        customiseFrame.getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        saveB.setText(bundle.getString("Save")); // NOI18N

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(177, Short.MAX_VALUE)
                .add(saveB)
                .add(148, 148, 148))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(65, Short.MAX_VALUE)
                .add(saveB)
                .addContainerGap())
        );

        customiseFrame.getContentPane().add(jPanel3, java.awt.BorderLayout.SOUTH);

        setTitle(bundle.getString("Edit_Avatar")); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/avatarbase/client/resources/Bundle"); // NOI18N
        jLabel1.setText(bundle.getString("AvatarConfigFrame.jLabel1.text")); // NOI18N

        newButton.setText(bundle.getString("AvatarConfigFrame.newButton.text")); // NOI18N
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });

        customizeButton.setText(bundle.getString("AvatarConfigFrame.customizeButton.text")); // NOI18N
        customizeButton.setEnabled(false);
        customizeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customizeButtonActionPerformed(evt);
            }
        });

        deleteButton.setText(bundle.getString("AvatarConfigFrame.deleteButton.text")); // NOI18N
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        useButton.setText(bundle.getString("AvatarConfigFrame.useButton.text")); // NOI18N
        useButton.setEnabled(false);
        useButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout buttonPanelLayout = new org.jdesktop.layout.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(newButton)
            .add(customizeButton)
            .add(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                .add(org.jdesktop.layout.GroupLayout.LEADING, useButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(org.jdesktop.layout.GroupLayout.LEADING, deleteButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(buttonPanelLayout.createSequentialGroup()
                .add(newButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(customizeButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(deleteButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(useButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        closeButton.setText(bundle.getString("AvatarConfigFrame.closeButton.text")); // NOI18N
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });

        avatarList.setModel(new DefaultListModel());
        avatarList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        avatarListScrollPane.setViewportView(avatarList);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel1)
                            .add(avatarListScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 159, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(buttonPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, closeButton))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(buttonPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 112, Short.MAX_VALUE)
                        .add(closeButton))
                    .add(layout.createSequentialGroup()
                        .add(avatarListScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 242, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void useButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useButtonActionPerformed

        // Find the selected avatar. If the avatar does not require high-res
        // graphics, then simply set the avatar in use and return.
        AvatarRegistry registry = AvatarRegistry.getAvatarRegistry();
        AvatarSPI avatar = (AvatarSPI) avatarList.getSelectedValue();
        if (avatar.isHighResolution() == false) {
            registry.setAvatarInUse(avatar, false);
            return;
        }

        if (!AvatarImiJME.supportsHighQualityAvatars()) {
            String msg = "Unfortunately your system graphics does not" +
                    " support the shaders which are required to use" +
                    " this avatar";
            String title = "Advanced Shaders Required";
            JFrame frame = JmeClientMain.getFrame().getFrame();
            JOptionPane.showMessageDialog(frame, msg, title, JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tell the server to use the selected avatar. Since the Use button is
        // only enabled when an item is selected, we assume something is
        // selected in the list. At this point, we also know the graphics system
        // supports the avatar, if high-res.
        registry.setAvatarInUse(avatar, false);
    }//GEN-LAST:event_useButtonActionPerformed

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        // Fetch the currently selected avatar and remove it from the list
        // of configurations. Since the Delete button is only enabled when an
        // item is selected, we assume something is selected in the list.
        AvatarSPI avatar = (AvatarSPI) avatarList.getSelectedValue();
        String avatarName = avatar.getName();
        avatar.delete();

        // If the deleted avatar is currently the avatar in use, then reset to
        // the default avatar.
        AvatarRegistry registry = AvatarRegistry.getAvatarRegistry();
        if (registry.isAvatarInUse(avatarName) == true) {
            AvatarSPI defaultAvatar = registry.getDefaultAvatar();
            if (defaultAvatar == null) {
                LOGGER.warning("Unable to reset avatar to default, none exists.");
                return;
            }
            registry.setAvatarInUse(defaultAvatar, false);
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void customizeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customizeButtonActionPerformed

        // Find the selected avatar. If the avatar does not require high-res
        // graphics, then go ahead and configure the avatar.
        AvatarSPI avatar = (AvatarSPI) avatarList.getSelectedValue();
        if (avatar.isHighResolution() == false) {
            avatar.configure();
            return;
        }

        if (!AvatarImiJME.supportsHighQualityAvatars()) {
            String msg = "Unfortunately your system graphics does not" +
                    " support the shaders which are required to configure" +
                    " this avatar";
            String title = "Advanced Shaders Required";
            JFrame frame = JmeClientMain.getFrame().getFrame();
            JOptionPane.showMessageDialog(frame, msg, title, JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Fetch the currently selected avatar and display a dialog box to
        // edit its configuration. Since the Customize button is only enabled
        // when an item is selected, we assume something is selected in the
        // list. At this point, we also know the graphics system supports the
        // avatar, if high-res.
        avatar.configure();
    }//GEN-LAST:event_customizeButtonActionPerformed

    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed

        if (!AvatarImiJME.supportsHighQualityAvatars()) {
            String msg = "Unfortunately your system graphics does not" +
                    " support the shaders which are required to create" +
                    " an avatar";
            String title = "Advanced Shaders Required";
            JFrame frame = JmeClientMain.getFrame().getFrame();
            JOptionPane.showMessageDialog(frame, msg, title, JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Generate a new avatar, using the IMI avatar as a factory, since we
        // can only create new IMI avatars. (In the future, perhaps there should
        // be some registry that generates a new avatar, rather than hard-coding
        // the IMI stuff. First generate a new unique name for the avatar.
        AvatarRegistry registry = AvatarRegistry.getAvatarRegistry();
        String avatarName = registry.getUniqueAvatarName();
        ImiAvatar newAvatar = ImiAvatar.createAvatar(avatarName);

        // Next, simply tell the new avatar to configure itself. It is then
        // responsible for changing the appearance of the avatar, etc. to do
        // whatever it needs to do for configuration.
        newAvatar.configure();
    }//GEN-LAST:event_newButtonActionPerformed

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
        // Close down the dialog. We do not dispose() since it can be used
        // again
        setVisible(false);
    }//GEN-LAST:event_closeButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList avatarList;
    private javax.swing.JScrollPane avatarListScrollPane;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton closeButton;
    private javax.swing.JFrame customiseFrame;
    private javax.swing.JButton customizeButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.ButtonGroup genderGrou;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JButton newButton;
    private javax.swing.JButton saveB;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JButton useButton;
    // End of variables declaration//GEN-END:variables
}
