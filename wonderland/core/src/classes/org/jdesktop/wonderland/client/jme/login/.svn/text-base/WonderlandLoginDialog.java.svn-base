/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.client.jme.login;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jdesktop.wonderland.client.assetmgr.Asset;
import org.jdesktop.wonderland.client.assetmgr.AssetManager;
import org.jdesktop.wonderland.client.assetmgr.AssetManager.AssetProgressListener;
import org.jdesktop.wonderland.client.jme.utils.GUIUtils;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.client.login.ServerSessionManager.LoginControl;
import org.jdesktop.wonderland.client.login.ServerStatusListener;

/**
 *
 * @author jkaplan
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class WonderlandLoginDialog extends javax.swing.JDialog
        implements AssetProgressListener, ServerStatusListener {

    private static final Logger LOGGER =
            Logger.getLogger(WonderlandLoginDialog.class.getName());
    private final static ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/client/jme/login/Bundle");
    private Frame parent;
    private LoginPanel login;
    /** the set of modules we are downloading */
    private Map<Integer, String> statusMessages =
            new LinkedHashMap<Integer, String>();
    private int nextDownloadID;
    /** a map from asset ids to status message ids for that asset */
    private Map<Asset, Integer> assetIDs =
            new HashMap<Asset, Integer>();
    /** the status message id of the server session manager status message */
    private final int sessionStatusID;

    /** Creates new form NoAuthLoginDialog */
    public WonderlandLoginDialog(Frame parent, boolean modal,
            LoginPanel login) {
        super(parent, modal);

        this.parent = parent;

        // remember the child panel
        this.login = login;

        login.addValidityListener(new ValidityListener());
        sessionStatusID = nextMessageID();

        // create our graphics
        GUIUtils.initLookAndFeel();
        initComponents();

        // set the status text to empty
        statusLabel.setText("  ");

        // add a listener that will be notified of any downloads in progress
        // assuming those are relevant to the current login
        AssetManager.getAssetManager().addProgressListener(this);

        // add a listener that will be notified of server status messages
        login.getLoginControl().getSessionManager().addServerStatusListener(
                this);

        // add the child panel
        loginSpecificPanel.add(login.getPanel(), BorderLayout.CENTER);
    }

    @Override
    public void dispose() {
        // unregister  listeners
        AssetManager.getAssetManager().removeProgressListener(this);
        login.getLoginControl().getSessionManager().removeServerStatusListener(
                this);
        super.dispose();
    }

    public void downloadProgress(final Asset asset,
            final int readBytes,
            final int percentage) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                // remove any session status messages, since they will block
                // the download message from being displayed
                statusMessages.remove(sessionStatusID);

                // find the message id for this asset
                Integer messageID = assetIDs.get(asset);
                if (messageID == null) {
                    messageID = nextMessageID();
                    assetIDs.put(asset, messageID);
                }

                // format the asset loading message
                String name = asset.getAssetURI().getURI();
                if (name.lastIndexOf('/') != -1) {
                    name = name.substring(name.lastIndexOf('/') + 1);
                }

                // set the status text
                String message = BUNDLE.getString("Downloading module");
                message = MessageFormat.format(message, name, percentage);
                statusMessages.put(messageID, message);
                updateStatus();
            }
        });
    }

    public void downloadFailed(final Asset asset) {
        downloadCompleted(asset);
    }

    public void downloadCompleted(final Asset asset) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                Integer messageID = assetIDs.remove(asset);
                if (messageID != null) {
                    statusMessages.remove(messageID);
                }
                updateStatus();
            }
        });
    }

    public void connecting(ServerSessionManager manager, final String message) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                statusMessages.put(sessionStatusID, message);
                updateStatus();
            }
        });
    }

    public void connected(ServerSessionManager sessionManager) {
    }

    public void disconnected(ServerSessionManager sessionManager) {
    }

    private void updateStatus() {
        if (!statusMessages.isEmpty()) {
            // get the first message in status message list
            String message = statusMessages.values().iterator().next();
            statusLabel.setText(message);
        }
    }

    private synchronized int nextMessageID() {
        return nextDownloadID++;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        userPasswordPanel = new javax.swing.JPanel();
        upServerLabel = new javax.swing.JLabel();
        upPasswordLabel = new javax.swing.JLabel();
        upUsernameLabel = new javax.swing.JLabel();
        upServerField = new javax.swing.JTextField();
        upPasswordField = new javax.swing.JPasswordField();
        upUsernameField = new javax.swing.JTextField();
        webPanel = new javax.swing.JPanel();
        webServerLabel = new javax.swing.JLabel();
        webServerField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        gradientPanel1 = new org.jdesktop.wonderland.client.jme.login.GradientPanel();
        jLabel2 = new javax.swing.JLabel();
        tagLineLabel = new javax.swing.JLabel();
        gradientPanel2 = new org.jdesktop.wonderland.client.jme.login.GradientPanel();
        loginSpecificPanel = new javax.swing.JPanel();
        buttonPanel = new javax.swing.JPanel();
        cancelButton = new javax.swing.JButton();
        loginButton = new javax.swing.JButton();
        getRootPane().setDefaultButton(loginButton);
        statusLabel = new javax.swing.JLabel();
        advancedButton = new javax.swing.JButton();

        userPasswordPanel.setOpaque(false);

        upServerLabel.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        upServerLabel.setForeground(new java.awt.Color(87, 101, 115));
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/client/jme/login/Bundle"); // NOI18N
        upServerLabel.setText(bundle.getString("WonderlandLoginDialog.upServerLabel.text")); // NOI18N

        upPasswordLabel.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        upPasswordLabel.setForeground(new java.awt.Color(87, 101, 115));
        upPasswordLabel.setText(bundle.getString("WonderlandLoginDialog.upPasswordLabel.text")); // NOI18N

        upUsernameLabel.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        upUsernameLabel.setForeground(new java.awt.Color(87, 101, 115));
        upUsernameLabel.setText(bundle.getString("WonderlandLoginDialog.upUsernameLabel.text")); // NOI18N

        upServerField.setEditable(false);

        upPasswordField.setFont(new java.awt.Font("Dialog", 0, 13));
        upPasswordField.setMinimumSize(new java.awt.Dimension(98, 22));

        upUsernameField.setFont(new java.awt.Font("Dialog", 0, 13)); // NOI18N
        upUsernameField.setMinimumSize(new java.awt.Dimension(98, 22));

        org.jdesktop.layout.GroupLayout userPasswordPanelLayout = new org.jdesktop.layout.GroupLayout(userPasswordPanel);
        userPasswordPanel.setLayout(userPasswordPanelLayout);
        userPasswordPanelLayout.setHorizontalGroup(
            userPasswordPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(userPasswordPanelLayout.createSequentialGroup()
                .add(26, 26, 26)
                .add(userPasswordPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(upPasswordLabel)
                    .add(upUsernameLabel)
                    .add(upServerLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(userPasswordPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(upUsernameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(upPasswordField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(upServerField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE))
                .addContainerGap())
        );
        userPasswordPanelLayout.setVerticalGroup(
            userPasswordPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, userPasswordPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(userPasswordPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(upUsernameLabel)
                    .add(upUsernameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(userPasswordPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(upPasswordLabel)
                    .add(upPasswordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(userPasswordPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(upServerField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(upServerLabel))
                .add(20, 20, 20))
        );

        webPanel.setOpaque(false);
        webPanel.setPreferredSize(new java.awt.Dimension(0, 0));

        webServerLabel.setFont(new java.awt.Font("Dialog", 1, 13));
        webServerLabel.setText(bundle.getString("WonderlandLoginDialog.webServerLabel.text")); // NOI18N

        webServerField.setEditable(false);
        webServerField.setFont(new java.awt.Font("Dialog", 0, 13));
        webServerField.setMinimumSize(new java.awt.Dimension(98, 22));

        jLabel1.setText(bundle.getString("WonderlandLoginDialog.jLabel1.text")); // NOI18N

        org.jdesktop.layout.GroupLayout webPanelLayout = new org.jdesktop.layout.GroupLayout(webPanel);
        webPanel.setLayout(webPanelLayout);
        webPanelLayout.setHorizontalGroup(
            webPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(webPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(webPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(webPanelLayout.createSequentialGroup()
                        .add(webServerLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(webServerField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE))
                    .add(jLabel1))
                .addContainerGap())
        );
        webPanelLayout.setVerticalGroup(
            webPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, webPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 75, Short.MAX_VALUE)
                .add(webPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(webServerLabel)
                    .add(webServerField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(java.awt.Color.white);

        gradientPanel1.setBackground(java.awt.Color.white);
        gradientPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 5, 10, 10));
        gradientPanel1.setAlignmentX(0.0F);
        gradientPanel1.setAlignmentY(0.0F);
        gradientPanel1.setGradientEndColor(new java.awt.Color(217, 225, 255));
        gradientPanel1.setGradientStartColor(new java.awt.Color(255, 255, 255));
        gradientPanel1.setMinimumSize(new java.awt.Dimension(370, 80));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/client/jme/login/pwl_idy_logomark-type_64.png"))); // NOI18N
        jLabel2.setAlignmentY(0.0F);
        jLabel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));

        tagLineLabel.setFont(new java.awt.Font("SansSerif", 0, 22)); // NOI18N
        tagLineLabel.setForeground(new java.awt.Color(87, 101, 115));
        tagLineLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        tagLineLabel.setText(bundle.getString("WonderlandLoginDialog.tagLineLabel.text")); // NOI18N
        tagLineLabel.setAlignmentY(0.0F);
        tagLineLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 2, 0));
        tagLineLabel.setFocusable(false);

        org.jdesktop.layout.GroupLayout gradientPanel1Layout = new org.jdesktop.layout.GroupLayout(gradientPanel1);
        gradientPanel1.setLayout(gradientPanel1Layout);
        gradientPanel1Layout.setHorizontalGroup(
            gradientPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gradientPanel1Layout.createSequentialGroup()
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tagLineLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                .add(0, 0, 0))
        );
        gradientPanel1Layout.setVerticalGroup(
            gradientPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gradientPanel1Layout.createSequentialGroup()
                .add(gradientPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(tagLineLabel)
                    .add(jLabel2))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        gradientPanel2.setBackground(java.awt.Color.white);
        gradientPanel2.setAlignmentX(0.0F);
        gradientPanel2.setAlignmentY(0.0F);
        gradientPanel2.setGradientEndColor(new java.awt.Color(163, 183, 255));
        gradientPanel2.setGradientStartColor(new java.awt.Color(217, 225, 255));
        gradientPanel2.setMinimumSize(new java.awt.Dimension(360, 174));
        gradientPanel2.setPreferredSize(new java.awt.Dimension(360, 174));
        gradientPanel2.setLayout(new java.awt.BorderLayout());

        loginSpecificPanel.setMinimumSize(new java.awt.Dimension(370, 114));
        loginSpecificPanel.setOpaque(false);
        loginSpecificPanel.setPreferredSize(new java.awt.Dimension(370, 114));
        loginSpecificPanel.setLayout(new java.awt.BorderLayout());
        gradientPanel2.add(loginSpecificPanel, java.awt.BorderLayout.CENTER);

        buttonPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        buttonPanel.setMinimumSize(new java.awt.Dimension(370, 60));
        buttonPanel.setOpaque(false);
        buttonPanel.setPreferredSize(new java.awt.Dimension(370, 60));

        cancelButton.setBackground(new java.awt.Color(255, 255, 255));
        cancelButton.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        cancelButton.setText(bundle.getString("WonderlandLoginDialog.cancelButton.text")); // NOI18N
        cancelButton.setAlignmentX(0.5F);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        loginButton.setBackground(new java.awt.Color(255, 255, 255));
        loginButton.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        loginButton.setText(bundle.getString("WonderlandLoginDialog.loginButton.text")); // NOI18N
        loginButton.setAlignmentX(0.5F);
        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginButtonActionPerformed(evt);
            }
        });

        statusLabel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        statusLabel.setForeground(new java.awt.Color(87, 101, 115));
        statusLabel.setText(bundle.getString("WonderlandLoginDialog.statusLabel.text")); // NOI18N
        statusLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        advancedButton.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        advancedButton.setText(bundle.getString("WonderlandLoginDialog.advancedButton.text")); // NOI18N
        advancedButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        advancedButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                advancedButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout buttonPanelLayout = new org.jdesktop.layout.GroupLayout(buttonPanel);
        buttonPanel.setLayout(buttonPanelLayout);
        buttonPanelLayout.setHorizontalGroup(
            buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(buttonPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(statusLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
                    .add(buttonPanelLayout.createSequentialGroup()
                        .add(advancedButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 43, Short.MAX_VALUE)
                        .add(cancelButton)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(loginButton)))
                .addContainerGap())
        );

        buttonPanelLayout.linkSize(new java.awt.Component[] {cancelButton, loginButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        buttonPanelLayout.setVerticalGroup(
            buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, buttonPanelLayout.createSequentialGroup()
                .add(statusLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(buttonPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(advancedButton)
                    .add(loginButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(cancelButton))
                .addContainerGap())
        );

        gradientPanel2.add(buttonPanel, java.awt.BorderLayout.PAGE_END);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(gradientPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
            .add(gradientPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(gradientPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 78, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(gradientPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        login.cancel();
        dispose();
}//GEN-LAST:event_cancelButtonActionPerformed

    private void loginButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginButtonActionPerformed
        loginButton.setEnabled(false);

        statusLabel.setText(BUNDLE.getString("Connecting..."));

        // perform the actual login in a separate thread, so we don't
        // block the AWT event thread
        new Thread(new Runnable() {

            public void run() {
                String errorMessage = login.doLogin();
                if (errorMessage == null) {
                    // success
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            dispose();
                        }
                    });
                } else {
                    String text = BUNDLE.getString("Error_Connecting");
                    text = MessageFormat.format(text, errorMessage);
                    statusLabel.setText(text);
                    loginButton.setEnabled(true);
                }
            }
        }, "Login thread").start();
}//GEN-LAST:event_loginButtonActionPerformed
    private LoginOptionsFrame loginOptionsFrame;

    private void advancedButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_advancedButtonActionPerformed
        if (loginOptionsFrame == null) {
            loginOptionsFrame = new LoginOptionsFrame();
        }

        loginOptionsFrame.setVisible(true);
    }//GEN-LAST:event_advancedButtonActionPerformed

    public class ValidityListener {

        public void setValidity(boolean isValid) {
            loginButton.setEnabled(isValid);
        }
    }

    public interface LoginPanel {

        public JPanel getPanel();

        public LoginControl getLoginControl();

        public void setServer(String server);

        public void addValidityListener(ValidityListener listener);

        public void removeValidityListener(ValidityListener listener);

        public void notifyValidityListeners();

        public String doLogin(); // return null on success or an error string
        // on failure

        public void cancel();
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton advancedButton;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private org.jdesktop.wonderland.client.jme.login.GradientPanel gradientPanel1;
    private org.jdesktop.wonderland.client.jme.login.GradientPanel gradientPanel2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton loginButton;
    private javax.swing.JPanel loginSpecificPanel;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JLabel tagLineLabel;
    private javax.swing.JPasswordField upPasswordField;
    private javax.swing.JLabel upPasswordLabel;
    private javax.swing.JTextField upServerField;
    private javax.swing.JLabel upServerLabel;
    private javax.swing.JTextField upUsernameField;
    private javax.swing.JLabel upUsernameLabel;
    private javax.swing.JPanel userPasswordPanel;
    private javax.swing.JPanel webPanel;
    private javax.swing.JTextField webServerField;
    private javax.swing.JLabel webServerLabel;
    // End of variables declaration//GEN-END:variables
}
