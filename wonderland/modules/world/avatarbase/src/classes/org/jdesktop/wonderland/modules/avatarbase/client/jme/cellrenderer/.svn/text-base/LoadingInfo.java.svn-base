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
package org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer;

import java.awt.EventQueue;
import java.util.HashMap;
import org.jdesktop.wonderland.client.hud.CompassLayout.Layout;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDManagerFactory;
import org.jdesktop.wonderland.client.hud.HUDMessage;
import org.jdesktop.wonderland.common.cell.CellID;

/**
 *
 * @author paulby
 */
public class LoadingInfo {

    private static LoadingInfo loadingInfo = new LoadingInfo();
    private final HashMap<CellID, String> currentlyLoading = new HashMap();
    private HUDMessage loadingMessage;

    /** Creates new form LoadingInfo */
    private LoadingInfo() {
        initHUD();
    }

    private void initHUD() {
        HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");

        if (loadingMessage == null) {
            loadingMessage = mainHUD.createMessage(java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/avatarbase/client/resources/Bundle").getString("Loading_Avatar..."));
            loadingMessage.setPreferredLocation(Layout.CENTER);
            // add HUD control panel to HUD
            mainHUD.addComponent(loadingMessage);
        }
    }

    private void startedLoadingImpl(CellID cellID, String avatarname) {
        synchronized (currentlyLoading) {
            currentlyLoading.put(cellID, avatarname);
            if (!loadingMessage.isVisible()) {
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        loadingMessage.setVisible(true);
                    }
                });
            }
        }
    }

    private void finishedLoadingImpl(CellID cellID, String avatarname) {
        synchronized (currentlyLoading) {
            currentlyLoading.remove(cellID);
            if (currentlyLoading.size() == 0) {
                EventQueue.invokeLater(new Runnable() {

                    public void run() {
                        loadingMessage.setVisible(false);
                    }
                });
            }
        }

    }

    public static void startedLoading(CellID cellID, String avatarname) {
        loadingInfo.startedLoadingImpl(cellID, avatarname);
    }

    public static void finishedLoading(CellID cellID, String avatarname) {
        loadingInfo.finishedLoadingImpl(cellID, avatarname);
    }
}
