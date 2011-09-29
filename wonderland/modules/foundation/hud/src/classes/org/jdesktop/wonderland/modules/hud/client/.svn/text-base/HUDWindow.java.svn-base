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
package org.jdesktop.wonderland.modules.hud.client;

import com.jme.math.Vector2f;
import java.awt.Component;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JToolTip;
import javax.swing.Popup;
import javax.swing.SwingUtilities;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.modules.appbase.client.Window2D;
import org.jdesktop.wonderland.modules.appbase.client.swing.PopupProvider;
import org.jdesktop.wonderland.modules.appbase.client.swing.WindowSwing;

/**
 * A extension of WindowSwung that properly displays popups on the HUD
 * @author jkaplan
 */
public class HUDWindow extends WindowSwing implements PopupProvider {
    private static final Logger LOGGER =
            Logger.getLogger(HUDWindow.class.getName());

    private final HUD hud;
    
    public HUDWindow(HUD hud, HUDApp2D app, Type type, int width, int height,
                     boolean decorated, Vector2f pixelScale, String name)
    {
        super (app, type, width, height, decorated, pixelScale, name);
        this.hud = hud;
    }

    public HUDWindow(HUD hud, HUDApp2D app, Type type, Window2D parent,
                     int width, int height, boolean decorated,
                     Vector2f pixelScale, String name)
    {
        super (app, type, parent, width, height, decorated, pixelScale, name);
        this.hud = hud;
    }

    public Popup getPopup(final Component component, final int x, final int y) {
        return new Popup() {
            private HUDComponent hc;

            @Override
            public void show() {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (hc != null) {
                            hidePopup(hc);
                        }

                        hc = showPopup(component, x, y);
                    }
                });
            }

            @Override
            public void hide() {
                if (hc == null) {
                    return;
                }

                // if we clean up too quickly, we can run into problems with
                // dropdown menus. Schedule a task to remove the component after
                // a short wait
                final Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                hidePopup(hc);
                            }
                        });
                        timer.cancel();
                    }
                }, 200);
            }
        };
    }

    private HUDComponent showPopup(Component component, int x, int y) {
        // sometimes we are given a tooltip with no text that doesn't close.
        // just ignore it
        if (component instanceof JToolTip &&
                (((JToolTip) component).getTipText() == null ||
                ((JToolTip) component).getTipText().isEmpty()))
        {
            return null;
        }

        HUDComponent hc = new HUDPopup2D((JComponent) component, this, x, y);
        hc.setDecoratable(false);

        hud.addComponent(hc);
        hc.setVisible(true);

        return hc;
    }

    private void hidePopup(final HUDComponent component) {
        component.setVisible(false);
        hud.removeComponent(component);
    }
}
