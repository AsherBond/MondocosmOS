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

import com.sun.stun.NetworkAddressManager.NetworkAddress;
import com.sun.stun.NetworkAddressManager;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;
import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.assetmgr.AssetManager;
import org.jdesktop.wonderland.client.jme.utils.GUIUtils;
import org.jdesktop.wonderland.client.softphone.AudioQuality;
import org.jdesktop.wonderland.client.softphone.SoftphoneControlImpl;

/**
 *
 * @author jkaplan
 * @author nsimpson
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class LoginOptionsFrame extends JDialog {

    private static final Logger LOGGER =
            Logger.getLogger(LoginOptionsFrame.class.getName());
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/client/jme/login/Bundle");
    private static final ResourceBundle bundle = ResourceBundle.getBundle("org/jdesktop/wonderland/client/jme/login/Bundle");

    /**
     * creates a new LoginOptionsFrame
     */
    public LoginOptionsFrame() {
        super((JDialog) null, true);

        GUIUtils.initLookAndFeel();
        initComponents();

        // set the OK button as the default
        getRootPane().setDefaultButton(okButton);

        // setup the list of IP addresses
        ipAddressComboBox.setModel(new DefaultComboBoxModel(
                NetworkAddressManager.getNetworkAddresses()));

        ipAddressComboBox.setSelectedItem(
                NetworkAddressManager.getDefaultNetworkAddress());

        // get the default client configuration
        //WonderlandClientConfig wcc = WonderlandClientConfig.getDefault();

        // setup the list of audio qualities
        audioQualityComboBox.setModel(
                new DefaultComboBoxModel(AudioQuality.values()));
        //audioQualityComboBox.setSelectedItem(wcc.getAudioQuality());

        AudioQuality audioQuality = AudioQuality.VPN;

        Preferences prefs =
                Preferences.userNodeForPackage(LoginOptionsFrame.class);

        String s = prefs.get("org.jdesktop.wonderland.modules.audiomanager." +
                "client.AUDIO_QUALITY", null);

        if (s != null) {
            AudioQuality[] audioQualityValues = AudioQuality.values();

            for (int i = 0; i < audioQualityValues.length; i++) {
                if (audioQualityValues[i].toString().equals(s)) {
                    audioQuality = audioQualityValues[i];
                    break;
                }
            }
        }

        audioQualityComboBox.setSelectedItem(audioQuality);

        //wcc.setPhoneNumber("");

        // read in proxy information
        //switch (wcc.getProxyType()) {
        //    case NONE:
        noProxyRB.setSelected(true);
        //        break;
        //    case SYSTEM:
        //        systemProxyRB.setSelected(true);
        //        break;
        //    case USER:
        //        wlProxyRB.setSelected(true);
        //        break;
        //}
        //httpProxyTF.setText(wcc.getHttpProxyHost());
        //httpProxyPortTF.setText(String.valueOf(wcc.getHttpProxyPort()));
        //httpsProxyTF.setText(wcc.getHttpsProxyHost());
        //httpsProxyPortTF.setText(String.valueOf(wcc.getHttpsProxyPort()));
        //noProxyTF.setText(wcc.getNoProxyHosts());

        // get the right initial value for the http proxy
        updateHttpProxy();

        systemProxyRB.setEnabled(false);
        wlProxyRB.setEnabled(false);

        // set the cache directory
        cacheLocation.setText(ClientContext.getUserDirectory().toString());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox2 = new javax.swing.JComboBox();
        proxyBG = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        networkPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        noProxyRB = new javax.swing.JRadioButton();
        systemProxyRB = new javax.swing.JRadioButton();
        wlProxyRB = new javax.swing.JRadioButton();
        httpProxyTFLabel = new javax.swing.JLabel();
        httpProxyTF = new javax.swing.JTextField();
        httpProxyPortTFLabel = new javax.swing.JLabel();
        httpProxyPortTF = new javax.swing.JTextField();
        httpsProxyTFLabel = new javax.swing.JLabel();
        httpsProxyTF = new javax.swing.JTextField();
        httpsProxyPortTFLabel = new javax.swing.JLabel();
        httpsProxyPortTF = new javax.swing.JTextField();
        noProxyTFLabel = new javax.swing.JLabel();
        noProxyTF = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        ipAddressComboBox = new javax.swing.JComboBox();
        audioPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        audioQualityComboBox = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        phoneNumber = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cachePanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        cacheLocation = new javax.swing.JTextField();
        clearCacheButton = new javax.swing.JButton();
        browseButton = new javax.swing.JButton();
        saveCB = new javax.swing.JCheckBox();
        resetButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/client/jme/login/Bundle"); // NOI18N
        setTitle(bundle.getString("LoginOptionsFrame.title")); // NOI18N
        setAlwaysOnTop(true);
        setBounds(new java.awt.Rectangle(0, 0, 0, 0));
        setResizable(false);

        jTabbedPane1.setFont(new java.awt.Font("Dialog", 0, 13));
        jTabbedPane1.setMaximumSize(new java.awt.Dimension(426, 287));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("LoginOptionsFrame.jPanel2.border.title"))); // NOI18N
        jPanel2.setFont(jPanel2.getFont());

        proxyBG.add(noProxyRB);
        noProxyRB.setFont(noProxyRB.getFont());
        noProxyRB.setSelected(true);
        noProxyRB.setText(bundle.getString("LoginOptionsFrame.noProxyRB.text")); // NOI18N
        noProxyRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noProxyRBActionPerformed(evt);
            }
        });

        proxyBG.add(systemProxyRB);
        systemProxyRB.setFont(systemProxyRB.getFont());
        systemProxyRB.setText(bundle.getString("LoginOptionsFrame.systemProxyRB.text")); // NOI18N
        systemProxyRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                systemProxyRBActionPerformed(evt);
            }
        });

        proxyBG.add(wlProxyRB);
        wlProxyRB.setFont(wlProxyRB.getFont());
        wlProxyRB.setText(bundle.getString("LoginOptionsFrame.wlProxyRB.text")); // NOI18N
        wlProxyRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                wlProxyRBActionPerformed(evt);
            }
        });

        httpProxyTFLabel.setFont(httpProxyTFLabel.getFont());
        httpProxyTFLabel.setText(bundle.getString("LoginOptionsFrame.httpProxyTFLabel.text")); // NOI18N

        httpProxyTF.setFont(new java.awt.Font("Dialog", 0, 13));

        httpProxyPortTFLabel.setFont(httpProxyPortTFLabel.getFont());
        httpProxyPortTFLabel.setText(bundle.getString("LoginOptionsFrame.httpProxyPortTFLabel.text")); // NOI18N

        httpProxyPortTF.setFont(new java.awt.Font("Dialog", 0, 13));
        httpProxyPortTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                httpProxyPortTFActionPerformed(evt);
            }
        });

        httpsProxyTFLabel.setFont(httpsProxyTFLabel.getFont());
        httpsProxyTFLabel.setText(bundle.getString("LoginOptionsFrame.httpsProxyTFLabel.text")); // NOI18N

        httpsProxyTF.setFont(new java.awt.Font("Dialog", 0, 13));

        httpsProxyPortTFLabel.setFont(httpsProxyPortTFLabel.getFont());
        httpsProxyPortTFLabel.setText(bundle.getString("LoginOptionsFrame.httpsProxyPortTFLabel.text")); // NOI18N

        httpsProxyPortTF.setFont(new java.awt.Font("Dialog", 0, 13));

        noProxyTFLabel.setFont(noProxyTFLabel.getFont());
        noProxyTFLabel.setText(bundle.getString("LoginOptionsFrame.noProxyTFLabel.text")); // NOI18N

        noProxyTF.setFont(new java.awt.Font("Dialog", 0, 13));

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel2Layout.createSequentialGroup()
                        .add(29, 29, 29)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(httpProxyTFLabel)
                            .add(httpsProxyTFLabel)
                            .add(noProxyTFLabel))
                        .add(6, 6, 6)
                        .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(jPanel2Layout.createSequentialGroup()
                                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, httpsProxyTF)
                                    .add(org.jdesktop.layout.GroupLayout.LEADING, httpProxyTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 168, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(jPanel2Layout.createSequentialGroup()
                                        .add(httpProxyPortTFLabel)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(httpProxyPortTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 67, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                    .add(jPanel2Layout.createSequentialGroup()
                                        .add(httpsProxyPortTFLabel)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(httpsProxyPortTF))))
                            .add(noProxyTF)))
                    .add(noProxyRB)
                    .add(systemProxyRB)
                    .add(wlProxyRB))
                .addContainerGap(103, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .add(noProxyRB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(systemProxyRB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(wlProxyRB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(httpProxyPortTFLabel)
                    .add(httpProxyPortTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(httpProxyTFLabel)
                    .add(httpProxyTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(httpsProxyTFLabel)
                    .add(httpsProxyTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(httpsProxyPortTFLabel)
                    .add(httpsProxyPortTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(noProxyTFLabel)
                    .add(noProxyTF, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("LoginOptionsFrame.jPanel1.border.title"))); // NOI18N
        jPanel1.setFont(jPanel1.getFont());

        jLabel1.setFont(jLabel1.getFont());
        jLabel1.setText(bundle.getString("LoginOptionsFrame.jLabel1.text")); // NOI18N

        ipAddressComboBox.setEditable(true);
        ipAddressComboBox.setFont(new java.awt.Font("Dialog", 0, 13));

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(ipAddressComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 167, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(214, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(ipAddressComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 24, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout networkPanelLayout = new org.jdesktop.layout.GroupLayout(networkPanel);
        networkPanel.setLayout(networkPanelLayout);
        networkPanelLayout.setHorizontalGroup(
            networkPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        networkPanelLayout.setVerticalGroup(
            networkPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(networkPanelLayout.createSequentialGroup()
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab(bundle.getString("LoginOptionsFrame.networkPanel.TabConstraints.tabTitle"), networkPanel); // NOI18N

        audioPanel.setFont(audioPanel.getFont());
        audioPanel.setMaximumSize(new java.awt.Dimension(426, 287));
        audioPanel.setMinimumSize(new java.awt.Dimension(426, 287));
        audioPanel.setPreferredSize(new java.awt.Dimension(426, 287));

        jLabel4.setFont(jLabel4.getFont());
        jLabel4.setText(bundle.getString("LoginOptionsFrame.jLabel4.text")); // NOI18N

        audioQualityComboBox.setFont(new java.awt.Font("Dialog", 0, 13));

        jLabel3.setFont(jLabel3.getFont());
        jLabel3.setText(bundle.getString("LoginOptionsFrame.jLabel3.text")); // NOI18N

        phoneNumber.setFont(new java.awt.Font("Dialog", 0, 13));
        phoneNumber.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                phoneNumberKeyTyped(evt);
            }
        });

        jLabel5.setFont(jLabel5.getFont());
        jLabel5.setText(bundle.getString("LoginOptionsFrame.jLabel5.text")); // NOI18N

        org.jdesktop.layout.GroupLayout audioPanelLayout = new org.jdesktop.layout.GroupLayout(audioPanel);
        audioPanel.setLayout(audioPanelLayout);
        audioPanelLayout.setHorizontalGroup(
            audioPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, audioPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(audioPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel4)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(audioPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel5)
                    .add(audioPanelLayout.createSequentialGroup()
                        .add(phoneNumber, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                        .add(119, 119, 119))
                    .add(audioPanelLayout.createSequentialGroup()
                        .add(audioQualityComboBox, 0, 113, Short.MAX_VALUE)
                        .add(119, 119, 119)))
                .add(200, 200, 200))
        );
        audioPanelLayout.setVerticalGroup(
            audioPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(audioPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(audioPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(audioQualityComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(audioPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(phoneNumber, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel5)
                .addContainerGap(216, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(bundle.getString("LoginOptionsFrame.audioPanel.TabConstraints.tabTitle"), audioPanel); // NOI18N

        jLabel2.setFont(jLabel2.getFont());
        jLabel2.setText(bundle.getString("LoginOptionsFrame.jLabel2.text")); // NOI18N

        cacheLocation.setEditable(false);
        cacheLocation.setFont(new java.awt.Font("Dialog", 0, 13));

        clearCacheButton.setFont(clearCacheButton.getFont());
        clearCacheButton.setText(bundle.getString("LoginOptionsFrame.clearCacheButton.text")); // NOI18N
        clearCacheButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearCacheButtonActionPerformed(evt);
            }
        });

        browseButton.setText(bundle.getString("LoginOptionsFrame.browseButton.text")); // NOI18N
        browseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseButtonActionPerformed(evt);
            }
        });

        saveCB.setSelected(true);
        saveCB.setText(bundle.getString("LoginOptionsFrame.saveCB.text")); // NOI18N

        resetButton.setText(bundle.getString("LoginOptionsFrame.resetButton.text")); // NOI18N
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout cachePanelLayout = new org.jdesktop.layout.GroupLayout(cachePanel);
        cachePanel.setLayout(cachePanelLayout);
        cachePanelLayout.setHorizontalGroup(
            cachePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cachePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cachePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(clearCacheButton)
                    .add(cachePanelLayout.createSequentialGroup()
                        .add(cachePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, cachePanelLayout.createSequentialGroup()
                                .add(saveCB)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .add(resetButton))
                            .add(org.jdesktop.layout.GroupLayout.LEADING, cacheLocation, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(browseButton)))
                .addContainerGap())
        );
        cachePanelLayout.setVerticalGroup(
            cachePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(cachePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(cachePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(cacheLocation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(browseButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(cachePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(cachePanelLayout.createSequentialGroup()
                        .add(saveCB)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(clearCacheButton))
                    .add(resetButton))
                .addContainerGap(206, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(bundle.getString("LoginOptionsFrame.cachePanel.TabConstraints.tabTitle"), cachePanel); // NOI18N

        okButton.setText(bundle.getString("LoginOptionsFrame.okButton.text")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(bundle.getString("LoginOptionsFrame.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap(349, Short.MAX_VALUE)
                .add(cancelButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(okButton)
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {cancelButton, okButton}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(jTabbedPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(okButton)
                    .add(cancelButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void wlProxyRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_wlProxyRBActionPerformed
    updateHttpProxy();
}//GEN-LAST:event_wlProxyRBActionPerformed

private void systemProxyRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_systemProxyRBActionPerformed
    updateHttpProxy();
}//GEN-LAST:event_systemProxyRBActionPerformed

private void noProxyRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noProxyRBActionPerformed
    updateHttpProxy();
}//GEN-LAST:event_noProxyRBActionPerformed

private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
    setVisible(false);
}//GEN-LAST:event_cancelButtonActionPerformed

private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
    //WonderlandClientConfig wcc = WonderlandClientConfig.getDefault();

    // store the audio quality
    //wcc.setAudioQuality((AudioQuality) audioQualityComboBox.getSelectedItem());
    AudioQuality audioQuality =
            (AudioQuality) audioQualityComboBox.getSelectedItem();
    Preferences prefs = Preferences.userNodeForPackage(LoginOptionsFrame.class);
    prefs.put(
            "org.jdesktop.wonderland.modules.audiomanager.client.AUDIO_QUALITY",
            audioQuality.toString());

    SoftphoneControlImpl.getInstance().setAudioQuality(audioQuality);

    // store the phone number
    //wcc.setPhoneNumber(phoneNumber.getText());

    System.setProperty(
            "org.jdesktop.wonderland.modules.audiomanager.client.PHONE_NUMBER",
            phoneNumber.getText());

    // store proxy properties
    //WonderlandClientConfig.ProxyType proxyType = WonderlandClientConfig.ProxyType.NONE;
    //if (systemProxyRB.isSelected()) {
    //    proxyType = WonderlandClientConfig.ProxyType.SYSTEM;
    //} else if (wlProxyRB.isSelected()) {
    //    proxyType = WonderlandClientConfig.ProxyType.USER;
    //
    //    if (httpProxyTF.getText().trim().length() == 0 ||
    //        httpProxyPortTF.getText().trim().length() == 0)
    //    {
    //         JOptionPane.showMessageDialog(this, "Invalid proxy settings",
    //                                       "Error", JOptionPane.ERROR_MESSAGE);
    //         return;
    //    }
    //
    //    wcc.setHttpProxyHost(httpProxyTF.getText());
    //    wcc.setHttpProxyPort(Integer.parseInt(httpProxyPortTF.getText()));
    //    wcc.setHttpsProxyHost(httpsProxyTF.getText());
    //    wcc.setHttpsProxyPort(Integer.parseInt(httpsProxyPortTF.getText()));
    //    wcc.setNoProxyHosts(noProxyTF.getText());
    //
    //}
    //wcc.setProxyType(proxyType);

    // write out the user configuration
    //WonderlandConfigUtil.writeUserConfig(wcc);

    NetworkAddress na = null;

    // store the network preferences
    Object selectedItem = ipAddressComboBox.getSelectedItem();
    if (selectedItem instanceof String) {
        String ipAddress = (String) selectedItem;

        try {
            InetAddress ia = InetAddress.getByName(ipAddress);
            na = new NetworkAddress("", ia);
        } catch (UnknownHostException e) {
            System.out.println("Unknown host:  " + ipAddress);
        }
    } else {
        na = (NetworkAddress) selectedItem;
    }

    if (na != null) {
        NetworkAddressManager.setDefaultNetworkAddress(na);
    }

    // if the cache has changed, update the settings for the directory
    File cacheDir = new File(cacheLocation.getText());
    if (!cacheDir.equals(ClientContext.getUserDirectory())) {
        // set the client directory to the desired value
        ClientContext.setUserDirectory(cacheDir, saveCB.isSelected());

        // close the asset cache to ensure the new value is used
        AssetManager.getAssetManager().closeAssetCache();
    }

    // close the dialog
    setVisible(false);
}//GEN-LAST:event_okButtonActionPerformed

private void clearCacheButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearCacheButtonActionPerformed
    String cachePath = cacheLocation.getText() + File.separator;
    String cacheDir = cachePath + "cache";
    String assetDBDir = cachePath + "AssetDB";
    String avatarCacheDir = cachePath + "AvatarCache";

    String message = BUNDLE.getString("Clear_Cache_Warning_Message");
    message = MessageFormat.format(message,
            cacheDir, assetDBDir, avatarCacheDir);
    int result = JOptionPane.showConfirmDialog(this,
            message,
            BUNDLE.getString("Confirm_Delete_Directories"),
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
    if (result == JOptionPane.YES_OPTION) {
        // OWL issue #120: shut down the asset database before changing any
        // files to make sure the directories can be deleted
        AssetManager.getAssetManager().closeAssetCache();

        deleteTree(new File(cacheDir));
        deleteTree(new File(assetDBDir));
        deleteTree(new File(avatarCacheDir));
    }
}//GEN-LAST:event_clearCacheButtonActionPerformed

private void httpProxyPortTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_httpProxyPortTFActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_httpProxyPortTFActionPerformed

private void phoneNumberKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_phoneNumberKeyTyped
    String text = phoneNumber.getText();
    audioQualityComboBox.setEnabled((text == null) || (text.length() == 0));
}//GEN-LAST:event_phoneNumberKeyTyped

private void browseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseButtonActionPerformed
    JFileChooser jfc = new JFileChooser(cacheLocation.getText());
    jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    int returnVal = jfc.showDialog(this, bundle.getString("SELECT CACHE DIRECTORY"));
    if (returnVal == JFileChooser.APPROVE_OPTION) {
        // set the cache directory in the field. The actual directory will
        // be set when the user clicks OK
        cacheLocation.setText(jfc.getSelectedFile().getPath());
    }
}//GEN-LAST:event_browseButtonActionPerformed

private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
    ClientContext.resetUserDirectory();
    cacheLocation.setText(ClientContext.getUserDirectory().toString());
}//GEN-LAST:event_resetButtonActionPerformed

    private void deleteTree(File file) {
        if (!file.exists() || !file.isDirectory()) {
            return;
        }

        for (File child : file.listFiles()) {
            if (child.isDirectory()) {
                deleteTree(child);
            } else {
                child.delete();
            }
        }

        file.delete();
    }

    private void updateHttpProxy() {
        boolean selected = wlProxyRB.isSelected();

        httpProxyTF.setEnabled(selected);
        httpProxyTFLabel.setEnabled(selected);
        httpProxyPortTF.setEnabled(selected);
        httpProxyPortTFLabel.setEnabled(selected);
        httpsProxyTF.setEnabled(selected);
        httpsProxyTFLabel.setEnabled(selected);
        httpsProxyPortTF.setEnabled(selected);
        httpsProxyPortTFLabel.setEnabled(selected);
        noProxyTF.setEnabled(selected);
        noProxyTFLabel.setEnabled(selected);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                LoginOptionsFrame lfe = new LoginOptionsFrame();
                lfe.addWindowStateListener(new WindowStateListener() {

                    public void windowStateChanged(WindowEvent evt) {
                        if (evt.getNewState() == WindowEvent.WINDOW_CLOSED) {
                            System.exit(0);
                        }
                    }
                });

                lfe.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel audioPanel;
    private javax.swing.JComboBox audioQualityComboBox;
    private javax.swing.JButton browseButton;
    private javax.swing.JTextField cacheLocation;
    private javax.swing.JPanel cachePanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton clearCacheButton;
    private javax.swing.JTextField httpProxyPortTF;
    private javax.swing.JLabel httpProxyPortTFLabel;
    private javax.swing.JTextField httpProxyTF;
    private javax.swing.JLabel httpProxyTFLabel;
    private javax.swing.JTextField httpsProxyPortTF;
    private javax.swing.JLabel httpsProxyPortTFLabel;
    private javax.swing.JTextField httpsProxyTF;
    private javax.swing.JLabel httpsProxyTFLabel;
    private javax.swing.JComboBox ipAddressComboBox;
    private javax.swing.JComboBox jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel networkPanel;
    private javax.swing.JRadioButton noProxyRB;
    private javax.swing.JTextField noProxyTF;
    private javax.swing.JLabel noProxyTFLabel;
    private javax.swing.JButton okButton;
    private javax.swing.JTextField phoneNumber;
    private javax.swing.ButtonGroup proxyBG;
    private javax.swing.JButton resetButton;
    private javax.swing.JCheckBox saveCB;
    private javax.swing.JRadioButton systemProxyRB;
    private javax.swing.JRadioButton wlProxyRB;
    // End of variables declaration//GEN-END:variables
}
