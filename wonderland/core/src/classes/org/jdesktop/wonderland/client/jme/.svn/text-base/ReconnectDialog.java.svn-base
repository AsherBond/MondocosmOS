/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., All Rights Reserved
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
package org.jdesktop.wonderland.client.jme;

import java.io.IOException;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.jdesktop.wonderland.client.login.ServerSessionManager;

/**
 *
 * @author jkaplan
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 * @author nsimpson
 */
public class ReconnectDialog implements Runnable {

    private static final Logger LOGGER =
            Logger.getLogger(ReconnectDialog.class.getName());
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/client/jme/Bundle");
    private JmeClientMain main;
    private ServerSessionManager mgr;
    private Thread reconnectThread;
    private JOptionPane reconnectPane;
    private JDialog reconnectDialog;

    /**
     * creates a new ReconnectDialog
     * @param main the main jme client
     * @param mgr the server session manager
     */
    public ReconnectDialog(JmeClientMain main, ServerSessionManager mgr) {
        this.main = main;
        this.mgr = mgr;

        initComponents();

        reconnectThread = new Thread(this, "Reconnect to server");
        reconnectThread.start();
    }

    private void initComponents() {
        reconnectPane = new JOptionPane(
                BUNDLE.getString("ATTEMPTING_TO_RECONNECT..."),
                JOptionPane.WARNING_MESSAGE, JOptionPane.DEFAULT_OPTION);
        reconnectDialog = reconnectPane.createDialog(
                JmeClientMain.getFrame().getFrame(),
                BUNDLE.getString("SERVER_DISCONNECTED"));
    }

    /**
     * shows or hides this dialog
     * @param visible if <tt>true</tt>, makes the dialog visible, otherwise
     * hides the dialog
     */
    public void setVisible(final boolean visible) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                reconnectDialog.setVisible(true);

            }
        });
    }

    public void run() {
        try {
            do {
                LOGGER.info("[ReconnectPane] sleeping");
                Thread.sleep(5000);
            } while (mgr.getDetails().getDarkstarServers() == null ||
                    mgr.getDetails().getDarkstarServers().size() == 0);

            LOGGER.info("[ReconnectPane] loading server " +
                        mgr.getDetails().getDarkstarServers());

            main.loadServer(mgr.getServerURL());
        } catch (IOException ex) {
            LOGGER.warning("Error reconnecting to server " +
                    mgr.getServerURL() + ": " + ex);
            String msg = BUNDLE.getString("UNABLE_TO_RECONNECT_TO_SERVER:_") +
                    "\n" + ex.getMessage();

            JOptionPane.showMessageDialog(JmeClientMain.getFrame().getFrame(),
                    msg, BUNDLE.getString("ERROR_CONNECTING_TO_SERVER"),
                    JOptionPane.ERROR_MESSAGE);
        } catch (InterruptedException ie) {
            // all done
        } finally {
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    reconnectDialog.setVisible(false);
                }
            });
        }
    }
}
