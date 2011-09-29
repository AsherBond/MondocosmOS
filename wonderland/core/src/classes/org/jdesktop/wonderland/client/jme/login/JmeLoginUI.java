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
package org.jdesktop.wonderland.client.jme.login;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import javax.swing.SwingUtilities;
import org.jdesktop.wonderland.client.comms.LoginFailureException;
import org.jdesktop.wonderland.client.comms.WonderlandServerInfo;
import org.jdesktop.wonderland.client.jme.JmeClientSession;
import org.jdesktop.wonderland.client.jme.MainFrame;
import org.jdesktop.wonderland.client.jme.login.WonderlandLoginDialog.LoginPanel;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.client.login.ServerSessionManager.NoAuthLoginControl;
import org.jdesktop.wonderland.client.login.ServerSessionManager.UserPasswordLoginControl;
import org.jdesktop.wonderland.client.login.ServerSessionManager.EitherLoginControl;
import org.jdesktop.wonderland.client.login.LoginUI;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.client.login.SessionCreator;


/**
 * Manage the connection between this client and the wonderland server
 *
 * TODO RENAME, there must be a better name for this class !  LoginManager & JMELoginManager
 *
 * @author paulby
 */
public class JmeLoginUI implements LoginUI, SessionCreator<JmeClientSession> {
    private MainFrame parent;
    private Vector3f initialPosition;
    private Quaternion initialLook;
    private boolean primary = false;
    
    public JmeLoginUI(MainFrame parent) {
        this.parent = parent;
    }

    public void requestLogin(final NoAuthLoginControl control) {
        // see if we have properties for automatic login
        String username = System.getProperty("auth.username");
        String fullname = System.getProperty("auth.fullname");
        if (username != null && fullname != null) {
            try {
                control.authenticate(username, fullname);
                return;
            } catch (LoginFailureException lfe) {
                // error trying to login in.  Fall back to
                // showing a dialog
            }
        }

        // start the login panel in the AWT event thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LoginPanel lp = new NoAuthLoginPanel(control.getServerURL(),
                                                     control);
                WonderlandLoginDialog dialog = new WonderlandLoginDialog(
                                                   parent.getFrame(), true, lp);
                dialog.setLocationRelativeTo(parent.getFrame());
                dialog.setVisible(true);
            }
        });
    }

    public void requestLogin(final UserPasswordLoginControl control) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // see if we have properties for automatic login
                String username = System.getProperty("auth.username");
                String password = System.getProperty("auth.password");
                if (username != null && password != null) {
                    try {
                        control.authenticate(username, password);
                        return;
                    } catch (LoginFailureException lfe) {
                        // error trying to login in.  Fall back to
                        // showing a dialog
                    }
                }

                LoginPanel lp = new WebServiceAuthLoginPanel(control.getServerURL(),
                                                             control);
                WonderlandLoginDialog dialog = new WonderlandLoginDialog(
                                                   parent.getFrame(), true, lp);
                dialog.setLocationRelativeTo(parent.getFrame());
                dialog.setVisible(true);
            }
        });
    }

    public void requestLogin(final EitherLoginControl control) {
        // see if we have properties for automatic login
        String username = System.getProperty("auth.username");
        String fullname = System.getProperty("auth.fullname");
        String password = System.getProperty("auth.password");
        
        if (username != null && fullname != null) {
            try {
                control.getNoAuthLogin().authenticate(username, fullname);
                return;
            } catch (LoginFailureException lfe) {
                // error trying to login in.  Fall back to
                // showing a dialog
            }
        }
        
        if (username != null && password != null) {
            try {
                control.getUserPasswordLogin().authenticate(username, password);
                return;
            } catch (LoginFailureException lfe) {
                // error trying to login in.  Fall back to
                // showing a dialog
            }
        }

        // start the login panel in the AWT event thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LoginPanel lp = new EitherLoginPanel(control.getServerURL(),
                                                     control);
                WonderlandLoginDialog dialog = new WonderlandLoginDialog(
                                                   parent.getFrame(), true, lp);
                dialog.setLocationRelativeTo(parent.getFrame());
                dialog.setVisible(true);
            }
        });
    }

    public void setInitialPosition(Vector3f position, Quaternion look) {
        this.initialPosition = position;
        this.initialLook = look;
    }
    
    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public JmeClientSession createSession(ServerSessionManager manager,
                                          WonderlandServerInfo server,
                                          ClassLoader loader)
    {
        // OWL issue #185: if primary is set to true, make sure the
        // session manager is the current primary session *before* creating
        // any sessions. This guarantees that the correct primary session
        // manager will be available to all connections created when the
        // session logs in (in particular, the asset manager)
        if (primary && !manager.equals(LoginManager.getPrimary())) {
            LoginManager.setPrimary(manager);
        }
        
        JmeClientSession session = new JmeClientSession(manager, server, loader);
        session.setInitialPosition(initialPosition, initialLook);
        return session;
    }
}
