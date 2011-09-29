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
package org.jdesktop.wonderland.modules.xremwin.client;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.registry.spi.CellFactorySPI;
import org.jdesktop.wonderland.common.cell.state.CellServerState;

/**
 * A cell factory which launches arbitrary X11 apps.
 * 
 * @author deronj
 */
//@CellFactory
public class RunXAppCellFactory implements CellFactorySPI {

    private Logger logger = Logger.getLogger(RunXAppCellFactory.class.getName());

    public String[] getExtensions() {
        return new String[] {};
    }

    public <T extends CellServerState> T getDefaultCellServerState(Properties props) {
//
//        // Show dialog to query values
//        RunXAppDialog dialog = new RunXAppDialog(JmeClientMain.getFrame().getFrame(), true);
//        dialog.setLocationRelativeTo(JmeClientMain.getFrame().getFrame());
//        dialog.setVisible(true);
//
//        if (!dialog.succeeded()) {
//            return null;
//        }
//        String appName = dialog.getAppName();
//        String command = dialog.getCommand();
//
//        // If we want to add to the Cell Palette, then do so.
//        if (dialog.isAddToCellPalette() == true) {
//            // First add the item to the user's local repository for later
//            // use on future invocations of the client.
//            XAppRegistryItem item = new XAppRegistryItem(appName, command);
//            try {
//                XAppRegistryItemUtils.addUserXAppRegistryItem(item);
//            } catch (Exception ex) {
//                logger.log(Level.WARNING, "Unable to add " + appName + " to " +
//                        "user's local x-apps store", ex);
//            }
//
//            // Add the item immediately to this session's cell palette. Make
//            // sure we add the " (User)" to the app name for the palette
//            String tmpAppName = appName + " (User)";
//            XAppCellFactory factory = new XAppCellFactory(tmpAppName, command);
//            CellRegistry.getCellRegistry().registerCellFactory(factory);
//        }
//
//        // Actually run the command.
//        AppCellXrwServerState serverState = new AppCellXrwServerState();
//        serverState.setAppName(appName);
//        serverState.setCommand(command);
//        serverState.setLaunchLocation("server");
//
//        return (T) serverState;
        return null;
    }

    public String getDisplayName() {
        return "Run X11 App";
    }

    public Image getPreviewImage() {
        URL url = RunXAppCellFactory.class.getResource("resources/RunXApp2.png");
        return Toolkit.getDefaultToolkit().createImage(url);
    }
}
