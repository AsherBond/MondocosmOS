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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.comms.LoginFailureException;
import org.jdesktop.wonderland.client.jme.login.WonderlandLoginDialog.LoginPanel;
import org.jdesktop.wonderland.client.jme.login.WonderlandLoginDialog.ValidityListener;
import org.jdesktop.wonderland.client.login.ServerSessionManager.LoginControl;
import org.jdesktop.wonderland.client.login.ServerSessionManager.NoAuthLoginControl;

/**
 *
 * @author jkaplan
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class NoAuthLoginPanel extends JPanel implements LoginPanel {

    private static final Logger LOGGER =
            Logger.getLogger(NoAuthLoginPanel.class.getName());
    private NoAuthLoginControl control;
    private List<ValidityListener> listeners;

    /** Creates new form NoAuthLoginPanel */
    public NoAuthLoginPanel(String serverURL, NoAuthLoginControl control) {
        this.control = control;

        initComponents();
        /* DISABLE for cutoff popup fix
        try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
         */
        // populate with defaults
        String userName = System.getProperty("user.name");
        userName = userName.replaceAll("\\s", "_");
        setUsername(userName);

        // override with any saved credentials
        loadCredentials();

        // set the server location
        setServer(serverURL);

        // notify validity listeners of validity of initial state
        notifyValidityListeners();
    }

    public JPanel getPanel() {
        return this;
    }

    public LoginControl getLoginControl() {
        return control;
    }

    public void setUsername(String username) {
        naUsernameField.setText(username);
    }

    public void setFullname(String fullname) {
        naFullNameField.setText(fullname);
    }

    public void setServer(String server) {
        naServerField.setText(server);
    }

    public void addValidityListener(ValidityListener listener) {
        if (listeners == null) {
            listeners = Collections.synchronizedList(new LinkedList());
        }
        listeners.add(listener);
    }

    public void removeValidityListener(ValidityListener listener) {
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    public void notifyValidityListeners() {
        if (listeners != null) {
            boolean isValid = naUsernameField.getDocument().getLength() > 0;

            Iterator<ValidityListener> iter = listeners.iterator();
            while (iter.hasNext()) {
                iter.next().setValidity(isValid);
            }
        }
    }

    public String doLogin() {
        String username = naUsernameField.getText();
        String fullname = naFullNameField.getText();
        try {
            control.authenticate(username, fullname);

            // If we got here, it means login succeeded.  Save the successful
            // login information.
            storeCredentials(username, fullname);
            return null;
        } catch (LoginFailureException lfe) {
            LOGGER.log(Level.WARNING, "Login failed", lfe);
            return lfe.getMessage();
        }
    }

    public void cancel() {
        control.cancel();
    }

    protected void storeCredentials(String username, String fullname) {
        Properties props = new Properties();
        props.put("username", username);
        props.put("fullname", fullname);

        File configDir = ClientContext.getUserDirectory("config");

        try {
            FileWriter outWriter =
                    new FileWriter(new File(configDir, "login.properties"));
            props.list(new PrintWriter(outWriter));
            outWriter.close();
        } catch (IOException ioe) {
            LOGGER.log(Level.WARNING, "Error writing login data", ioe);
        }
    }

    protected void loadCredentials() {
        File configDir = ClientContext.getUserDirectory("config");
        File propsFile = new File(configDir, "login.properties");
        if (!propsFile.exists()) {
            return;
        }

        try {
            FileInputStream inReader = new FileInputStream(propsFile);

            Properties props = new Properties();
            props.load(inReader);
            setUsername(props.getProperty("username"));
            setFullname(props.getProperty("fullname"));
        } catch (IOException ioe) {
            LOGGER.log(Level.WARNING, "Error reading login data", ioe);
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

        naServerLabel = new javax.swing.JLabel();
        naFullNameLabel = new javax.swing.JLabel();
        naUsernameLabel = new javax.swing.JLabel();
        naServerField = new javax.swing.JTextField();
        naUsernameField = new javax.swing.JTextField();
        naFullNameField = new javax.swing.JTextField();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 10, 1, 1));
        setMinimumSize(new java.awt.Dimension(360, 114));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(360, 114));

        naServerLabel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        naServerLabel.setForeground(new java.awt.Color(87, 101, 115));
        naServerLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/client/jme/login/Bundle"); // NOI18N
        naServerLabel.setText(bundle.getString("NoAuthLoginPanel.naServerLabel.text")); // NOI18N

        naFullNameLabel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        naFullNameLabel.setForeground(new java.awt.Color(87, 101, 115));
        naFullNameLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        naFullNameLabel.setText(bundle.getString("NoAuthLoginPanel.naFullNameLabel.text")); // NOI18N

        naUsernameLabel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        naUsernameLabel.setForeground(new java.awt.Color(87, 101, 115));
        naUsernameLabel.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        naUsernameLabel.setText(bundle.getString("NoAuthLoginPanel.naUsernameLabel.text")); // NOI18N

        naServerField.setEditable(false);
        naServerField.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        naUsernameField.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        naUsernameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                naUsernameFieldKeyReleased(evt);
            }
        });

        naFullNameField.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, naUsernameLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, naFullNameLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, naServerLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(naServerField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                    .add(naFullNameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, naUsernameField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {naFullNameLabel, naServerLabel, naUsernameLabel}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(naUsernameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(naUsernameLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(naFullNameField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(naFullNameLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(naServerField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(naServerLabel))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void naUsernameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_naUsernameFieldKeyReleased
        notifyValidityListeners();
    }//GEN-LAST:event_naUsernameFieldKeyReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField naFullNameField;
    private javax.swing.JLabel naFullNameLabel;
    private javax.swing.JTextField naServerField;
    private javax.swing.JLabel naServerLabel;
    private javax.swing.JTextField naUsernameField;
    private javax.swing.JLabel naUsernameLabel;
    // End of variables declaration//GEN-END:variables
}
