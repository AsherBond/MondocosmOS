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
import java.awt.Container;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JPanel;
import org.jdesktop.wonderland.client.jme.login.WonderlandLoginDialog.LoginPanel;
import org.jdesktop.wonderland.client.jme.login.WonderlandLoginDialog.ValidityListener;
import org.jdesktop.wonderland.client.login.ServerSessionManager.EitherLoginControl;
import org.jdesktop.wonderland.client.login.ServerSessionManager.LoginControl;
import org.jdesktop.wonderland.client.login.ServerSessionManager.NoAuthLoginControl;
import org.jdesktop.wonderland.client.login.ServerSessionManager.UserPasswordLoginControl;

/**
 *
 * @author jkaplan
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class EitherLoginPanel extends JPanel implements LoginPanel {

    private static final Logger LOGGER =
            Logger.getLogger(EitherLoginPanel.class.getName());

    private String serverURL;
    private EitherLoginControl control;
    private final Set<ValidityListener> listeners =
            new CopyOnWriteArraySet<ValidityListener>();

    private LoginPanel curLogin;

    /** Creates new form NoAuthLoginPanel */
    public EitherLoginPanel(String serverURL, EitherLoginControl control) {
        this.serverURL = serverURL;
        this.control = control;

        initComponents();

        setLoginControl(control.getNoAuthLogin());
    }

    protected synchronized void setLoginControl(LoginControl control) {
        
        // create a panel of the correct type
        if (control instanceof NoAuthLoginControl) {
            curLogin = new NoAuthLoginPanel(serverURL,
                    (NoAuthLoginControl) control);
        } else if (control instanceof UserPasswordLoginControl) {
            curLogin = new WebServiceAuthLoginPanel(serverURL,
                    (UserPasswordLoginControl) control);
        }

        // copy validity listeners
        for (ValidityListener vl : listeners) {
            curLogin.addValidityListener(vl);
        }

        // add the panel to the layout
        loginPanel.removeAll();
        loginPanel.add(curLogin.getPanel(), BorderLayout.CENTER);
        loginPanel.invalidate();
        
        // try to repaint from the dialog level
        JDialog dialog = findDialog();
        if (dialog != null) {
            // try to repaint the parent
            dialog.pack();
        } else {
            // otherwise just repaint ourself
            repaint();
        }
    }

    protected JDialog findDialog() {
        Container parent = getParent();
        while (parent != null) {
            if (parent instanceof JDialog) {
                return (JDialog) parent;
            }

            parent = parent.getParent();
        }

        return null;
    }

    public void setServer(String server) {
        this.serverURL = server;
        curLogin.setServer(server);
    }

    public JPanel getPanel() {
        return this;
    }

    public LoginControl getLoginControl() {
        return control;
    }

    public synchronized void addValidityListener(ValidityListener listener) {
        listeners.add(listener);
        curLogin.addValidityListener(listener);
    }

    public synchronized void removeValidityListener(ValidityListener listener) {
        listeners.remove(listener);
        curLogin.removeValidityListener(listener);
    }

    public void notifyValidityListeners() {
        curLogin.notifyValidityListeners();
    }

    public String doLogin() {
        return curLogin.doLogin();
    }

    public void cancel() {
        curLogin.cancel();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        loginTypeBG = new javax.swing.ButtonGroup();
        guestLoginRB = new javax.swing.JRadioButton();
        authLoginRB = new javax.swing.JRadioButton();
        loginPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();

        setOpaque(false);

        loginTypeBG.add(guestLoginRB);
        guestLoginRB.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        guestLoginRB.setSelected(true);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/client/jme/login/Bundle"); // NOI18N
        guestLoginRB.setText(bundle.getString("EitherLoginPanel.guestLoginRB.text")); // NOI18N
        guestLoginRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guestLoginRBActionPerformed(evt);
            }
        });

        loginTypeBG.add(authLoginRB);
        authLoginRB.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        authLoginRB.setText(bundle.getString("EitherLoginPanel.authLoginRB.text")); // NOI18N
        authLoginRB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                authLoginRBActionPerformed(evt);
            }
        });

        loginPanel.setOpaque(false);
        loginPanel.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(87, 101, 115));
        jLabel1.setText(bundle.getString("EitherLoginPanel.jLabel1.text")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, loginPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(guestLoginRB)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(authLoginRB)
                .addContainerGap(82, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(guestLoginRB)
                    .add(authLoginRB)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(loginPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 114, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void guestLoginRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guestLoginRBActionPerformed
        updateLoginPanel();
    }//GEN-LAST:event_guestLoginRBActionPerformed

    private void authLoginRBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_authLoginRBActionPerformed
        updateLoginPanel();
    }//GEN-LAST:event_authLoginRBActionPerformed

    protected void updateLoginPanel() {
        if (guestLoginRB.isSelected()) {
            setLoginControl(control.getNoAuthLogin());
        } else {
            setLoginControl(control.getUserPasswordLogin());
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton authLoginRB;
    private javax.swing.JRadioButton guestLoginRB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel loginPanel;
    private javax.swing.ButtonGroup loginTypeBG;
    // End of variables declaration//GEN-END:variables

}
