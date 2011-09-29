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
package org.jdesktop.wonderland.modules.securitygroups.client;

import java.awt.event.ActionEvent;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import org.jdesktop.wonderland.client.BaseClientPlugin;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.client.jme.MainFrame;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.annotation.Plugin;

/**
 *
 * @author jkaplan
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
@Plugin
public class GroupEditorPlugin extends BaseClientPlugin {
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/securitygroups/client/Bundle");
    private JMenuItem menuItem;
    private GroupManagerFrame gmf;

    @Override
    protected void activate() {
        MainFrame mf = JmeClientMain.getFrame();
        if (mf == null) {
            return;
        }

        if (menuItem == null) {
            menuItem = new JMenuItem(
                    new AbstractAction(BUNDLE.getString("Groups...")) {
                        public void actionPerformed(ActionEvent e) {
                            if (gmf == null) {
                                ServerSessionManager manager =
                                        getSessionManager();
                                gmf = new GroupManagerFrame(
                                        manager.getServerURL(),
                                        manager.getCredentialManager());
                            }

                            gmf.setVisible(true);
                        }
                    });
        }

        mf.addToEditMenu(menuItem, 2);
    }

    @Override
    protected void deactivate() {
        MainFrame mf = JmeClientMain.getFrame();
        if (mf == null || menuItem == null) {
            return;
        }
        mf.removeFromEditMenu(menuItem);
    }
}
