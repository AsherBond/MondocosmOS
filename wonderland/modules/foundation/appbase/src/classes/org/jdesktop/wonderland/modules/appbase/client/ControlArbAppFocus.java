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
package org.jdesktop.wonderland.modules.appbase.client;

import java.awt.Canvas;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.client.hud.CompassLayout.Layout;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDButton;
import org.jdesktop.wonderland.client.hud.HUDManagerFactory;
import org.jdesktop.wonderland.client.input.InputManager;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.client.jme.input.InputManager3D;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * A control arb which maintains app input focus. When an control is taken for
 * an app, its focus entity is focussed for key and mouse events. When control
 * is released for an app, its focus entity is defocussed for key and mouse
 * events. Finally, whenever one or more app is controlled the global focus
 * entity is defocussed. 
 *
 * @author deronj
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
@ExperimentalAPI
public abstract class ControlArbAppFocus extends ControlArb {

    private static final Logger logger = Logger.getLogger(
            ControlArbAppFocus.class.getName());
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/appbase/client/Bundle");
    /** The input manager. */
    private InputManager inputManager;
    /** The number of apps which have control. */
    private static int numControlledApps;
    /** The HUD on which the release control all button is displayed. */
    private static HUD hud;
    /** The release control all button. */
    private static HUDButton releaseControlAllButton;
    /** Is the release control all button visible? */
    private static boolean releaseControlAllButtonVisible;

    /**
     * Create an instance of ControlArbAppFocus.
     */
    public ControlArbAppFocus() {
        inputManager = InputManager3D.getInputManager();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void takeControlPerform () {
        if (hasControl()) {
            return;
        }

        logger.info("Took control");
        appControl = true;
        updateControl();

        if (!hasControl()) {
            return;
        }

        // Assign focus to the app
        inputManager.addKeyMouseFocus(new Entity[]{app.getFocusEntity()});

        numControlledApps++;
        if (numControlledApps == 1) {

            // At least one app has keyboard/mouse control. Disable global
            // (world) listeners.
            inputManager.removeKeyMouseFocus(
                    inputManager.getGlobalFocusEntity());

            // Update key focus for root canvas
            updateKeyFocus(true);

            // Display a button to allow the user to release control
            App2D.invokeLater(new Runnable() {
                public void run () {
                    releaseControlAllButtonSetVisible(true);
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void releaseControlPerform() {
        if (!hasControl()) {
            return;
        }

        appControl = false;
        logger.info("Released control");
        updateControl();

        if (hasControl()) {
            return;
        }

        // Remove focus from the app
        inputManager.removeKeyMouseFocus(new Entity[]{app.getFocusEntity()});

        numControlledApps--;

        if (numControlledApps <= 0) {

            // Undisplay a button to allow the user to release control
            App2D.invokeLater(new Runnable() {
                public void run () {
                    releaseControlAllButtonSetVisible(false);
                }
            });

            // No more apps have control. Reenable global (world) listeners.
            inputManager.addKeyMouseFocus(inputManager.getGlobalFocusEntity());

            // Make sure the main canvas gets keyboard focus
            updateKeyFocus(false);
            Canvas canvas = JmeClientMain.getFrame().getCanvas();
            if (!canvas.requestFocusInWindow()) {
                logger.info("Focus request for main canvas rejected.");
            }
        }
    }

    /**
     * By default, the control arb makes it so the main canvas is not focusable
     * when an app has control. Subclasses may override this method to change
     * this behavior.
     * @param hasControl true if the app has just gained control, or false if
     * not
     */
    protected void updateKeyFocus(boolean hasControl) {
        Canvas canvas = JmeClientMain.getFrame().getCanvas();

        if (!hasControl) {
            // Make sure that the main canvas is focusable
            canvas.setFocusable(true);
        } else {
            // Prevent the main canvas for gaining focus while an app has focus
            canvas.setFocusable(false);
        }
    }

    /**
     * THREAD USAGE NOTE: This is called on the App Invoker Thread.
     */
    public static void releaseControlAllButtonSetVisible(boolean visible) {
        if (releaseControlAllButtonVisible == visible) {
            return;
        }
        releaseControlAllButtonVisible = visible;

        if (visible) {
            if (releaseControlAllButton == null) {
                hud = HUDManagerFactory.getHUDManager().getHUD("main");
                releaseControlAllButton = hud.createButton(
                        BUNDLE.getString("Release_App_Control"));
                releaseControlAllButton.setDecoratable(false);
                releaseControlAllButton.setPreferredLocation(Layout.NORTHEAST);
                releaseControlAllButton.addActionListener(new ActionListener() {

                    // This is called on the EDT
                    public void actionPerformed(ActionEvent event) {
                        // Need to get off EDT because this uses app base locks
                        App2D.invokeLater(new Runnable() {
                            public void run () {
                                ControlArb.releaseControlAll();
                            }
                        });
                    }
                });
                hud.addComponent(releaseControlAllButton);
            }
            releaseControlAllButton.setVisible(true);
        } else {
            releaseControlAllButton.setVisible(false);
        }
    }
}
