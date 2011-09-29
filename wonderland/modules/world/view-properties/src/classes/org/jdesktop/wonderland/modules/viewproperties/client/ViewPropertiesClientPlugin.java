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
package org.jdesktop.wonderland.modules.viewproperties.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import org.jdesktop.wonderland.client.BaseClientPlugin;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.client.jme.ViewManager;
import org.jdesktop.wonderland.client.jme.ViewProperties;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.annotation.Plugin;

/**
 * Client-side plugin for the view properties. Installs a "Properties..." menu
 * item under "View"
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
@Plugin
public class ViewPropertiesClientPlugin extends BaseClientPlugin {

    private static final Logger LOGGER =
            Logger.getLogger(ViewPropertiesClientPlugin.class.getName());
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/viewproperties/client/Bundle");
    private JMenuItem propertiesMI = null;
    private WeakReference<ViewPropertiesJDialog> viewPropertiesFrameRef = null;

    @Override
    public void initialize(ServerSessionManager loginInfo) {
        // Create the Properties menu item that lets users edit the properties
        // of the view (field-of-view, front/back clip). The menu will be added
        // when our server becomes primary.
        propertiesMI = new JMenuItem(BUNDLE.getString("Properties..."));
        propertiesMI.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                ViewPropertiesJDialog dialog = getViewPropertiesJDialog();
                if (!dialog.isVisible()) {
                    dialog.pack();
                    dialog.setSize(300, 200);
                    dialog.setModal(false);
                    dialog.setVisible(true);
                }
            }
        });
        super.initialize(loginInfo);
    }

    /**
     * Notification that our server is now the the primary server
     */
    @Override
    protected void activate() {
        // Add to the View menu
        JmeClientMain.getFrame().addToViewMenu(propertiesMI, -1);

        // Attempt to load the stored properties from the user's local
        // repository and set the initial values in the view manager's
        // properties
        try {
            ViewProperties properties =
                    ViewPropertiesUtils.loadViewProperties();
            ViewManager manager = ViewManager.getViewManager();
            ViewProperties viewProperties = manager.getViewProperties();
            viewProperties.setFieldOfView(properties.getFieldOfView());
            viewProperties.setFrontClip(properties.getFrontClip());
            viewProperties.setBackClip(properties.getBackClip());
        } catch (java.lang.Exception excp) {
            LOGGER.log(Level.WARNING,
                    "Unable to read user's view properties", excp);
        }
    }

    @Override
    protected void deactivate() {
        // Remove from the View menu
        JmeClientMain.getFrame().removeFromViewMenu(propertiesMI);
    }

    /**
     * Returns the single ViewPropertiesJDialog for the system, creating it if
     * necessary
     */
    private ViewPropertiesJDialog getViewPropertiesJDialog() {
        if (viewPropertiesFrameRef == null ||
                viewPropertiesFrameRef.get() == null) {
            ViewPropertiesJDialog viewPropertiesFrame =
                    new ViewPropertiesJDialog();
            viewPropertiesFrameRef = new WeakReference(viewPropertiesFrame);
            return viewPropertiesFrame;
        } else {
            return viewPropertiesFrameRef.get();
        }
    }
}
