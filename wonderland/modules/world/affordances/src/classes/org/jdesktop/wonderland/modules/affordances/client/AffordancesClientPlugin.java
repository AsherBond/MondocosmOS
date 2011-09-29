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
package org.jdesktop.wonderland.modules.affordances.client;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.swing.SwingUtilities;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuItemEvent;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuItem;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuActionListener;
import org.jdesktop.wonderland.client.contextmenu.SimpleContextMenuItem;
import org.jdesktop.wonderland.client.contextmenu.annotation.ContextMenuFactory;
import org.jdesktop.wonderland.client.contextmenu.spi.ContextMenuFactorySPI;
import org.jdesktop.wonderland.client.hud.CompassLayout.Layout;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDEvent;
import org.jdesktop.wonderland.client.hud.HUDEvent.HUDEventType;
import org.jdesktop.wonderland.client.hud.HUDEventListener;
import org.jdesktop.wonderland.client.hud.HUDManagerFactory;
import org.jdesktop.wonderland.client.input.InputManager;
import org.jdesktop.wonderland.client.scenemanager.event.ContextEvent;
import org.jdesktop.wonderland.common.cell.security.MoveAction;
import org.jdesktop.wonderland.modules.affordances.client.event.AffordanceRemoveEvent;
import org.jdesktop.wonderland.modules.security.client.SecurityComponent;

/**
 * Client-size plugin for the cell manipulator affordances.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
@ContextMenuFactory
public class AffordancesClientPlugin implements ContextMenuFactorySPI {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/affordances/client/resources/Bundle");

    /* The single instance of the Affordance HUD Panel */
    private static AffordanceHUDPanel affordanceHUDPanel;
    private static HUDComponent affordanceHUD;

    /**
     * Creates the affordance HUD frame.
     *
     * NOTE: This method should NOT be called on the AWT Event Thread.
     */
    private void createHUD() {
        HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");

        // create affordances Swing control
        affordanceHUDPanel = new AffordanceHUDPanel();

        // create HUD control
        affordanceHUD = mainHUD.createComponent(affordanceHUDPanel);
        affordanceHUDPanel.setHUDComponent(affordanceHUD);
        affordanceHUD.setName(BUNDLE.getString("Edit_Object_None_Selected"));
        affordanceHUD.setPreferredLocation(Layout.SOUTH);
        affordanceHUD.addEventListener(new HUDEventListener() {
            public void HUDObjectChanged(HUDEvent event) {
                /**
                 * Handles when the affordance frame is closed
                 */
                if (event.getEventType() == HUDEventType.CLOSED) {
                    // Tell all of the affordances to remove themselves by
                    // posting an event to the input system as such. Also tell
                    // the affordance panel it has closed
                    affordanceHUDPanel.closed();
                    InputManager.inputManager().postEvent(new AffordanceRemoveEvent());
                }
            }
        });

        // add affordances HUD panel to main HUD
        mainHUD.addComponent(affordanceHUD);
    }

    /**
     * {@inheritDoc}
     */
    public ContextMenuItem[] getContextMenuItems(ContextEvent event) {
        final SimpleContextMenuItem editItem = new SimpleContextMenuItem(
                BUNDLE.getString("Edit..."), new EditContextListener());

        // if there is security on this cell, do some calculation to
        // figure out if the user has access to this cell
        Cell cell = event.getPrimaryCell();
        final SecurityComponent sc = cell.getComponent(SecurityComponent.class);
        if (sc != null) {
            // see if the permissions are available immediately
            if (sc.hasPermissions()) {
                editItem.setEnabled(canMove(sc));
            } else {
                Thread t = new Thread(new Runnable() {
                    public void run() {
                        editItem.setLabel(BUNDLE.getString("Edit..."));
                        editItem.setEnabled(canMove(sc));
                        editItem.fireMenuItemRepaintListeners();
                    }
                }, "Security check wait thread");
                t.start();

                // wait for a bit to see if the listener comes back
                // quickly
                try {
                    t.join(250);
                } catch (InterruptedException ie) {
                }

                if (t.isAlive()) {
                    // the thread isn't done -- add in a wait message
                    editItem.setLabel(BUNDLE.getString("Edit_Checking"));
                    editItem.setEnabled(false);
                }
            }
        }

        // return the edit item
        return new ContextMenuItem[]{editItem};
    }

    private boolean canMove(SecurityComponent sc) {
        try {
            MoveAction ma = new MoveAction();
            return sc.getPermission(ma);
        } catch (InterruptedException ie) {
            // shouldn't happen, since we check above
            return true;
        }
    }

    /**
     * Handles when the "Edit" context menu item has been selected
     */
    class EditContextListener implements ContextMenuActionListener {

        public void actionPerformed(ContextMenuItemEvent event) {

            // Display the affordance HUD Panel. We need to call HUD methods
            // on a thread OTHER than the AWT Event Thread.
            if (affordanceHUD == null) {
                createHUD();
            }
            String name = BUNDLE.getString("Edit_Object");
            name = MessageFormat.format(name, event.getCell().getName());
            affordanceHUD.setName(name);
            affordanceHUD.setVisible(true);

            // Update the states of the HUD Swing components; we must do this
            // on the AWT Event Thread.
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    affordanceHUDPanel.setTranslationVisible(true);
                    affordanceHUDPanel.updateGUI();
                }
            });
        }
    }
}
