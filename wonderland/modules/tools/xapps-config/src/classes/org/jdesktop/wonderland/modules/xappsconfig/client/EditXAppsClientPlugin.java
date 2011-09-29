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
package org.jdesktop.wonderland.modules.xappsconfig.client;

import java.lang.ref.WeakReference;
import javax.swing.JMenuItem;
import org.jdesktop.wonderland.client.BaseClientPlugin;
import org.jdesktop.wonderland.client.login.ServerSessionManager;

/**
 * Client-size plugin for editing the X Apps registered on the system
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
//@Plugin
public class EditXAppsClientPlugin extends BaseClientPlugin {

    private WeakReference<EditXAppsJFrame> editXAppsFrameRef = null;
    private JMenuItem editMI;

    @Override
    public void initialize(ServerSessionManager loginInfo) {
        // Create the Palette menu and the Cell submenu and dialog that lets
        // users create new cells.  The menu will be added when our server
        // becomes primary.
//        editMI = new JMenuItem("Edit X Apps...");
//        editMI.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                EditXAppsJFrame editXAppsFrame;
//                if (editXAppsFrameRef == null || editXAppsFrameRef.get() == null) {
//                    JFrame frame = JmeClientMain.getFrame().getFrame();
//                    editXAppsFrame = new EditXAppsJFrame();
//                    editXAppsFrame.setLocationRelativeTo(frame);
//                    editXAppsFrame.setSize(400, 300);
//                    editXAppsFrameRef = new WeakReference(editXAppsFrame);
//                }
//                else {
//                    editXAppsFrame = editXAppsFrameRef.get();
//                }
//
//                if (editXAppsFrame.isVisible() == false) {
//                    editXAppsFrame.setSize(400, 300);
//                    editXAppsFrame.setVisible(true);
//                }
//            }
//        });

        super.initialize(loginInfo);
    }

    /**
     * Notification that our server is now the the primary server
     */
    @Override
    protected void activate() {
        // activate
//        JmeClientMain.getFrame().addToToolsMenu(editMI, -1);
    }

    @Override
    protected void deactivate() {
        // deactivate
//        JmeClientMain.getFrame().removeFromToolsMenu(editMI);
    }
}
