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

import java.util.Iterator;
import java.util.HashMap;
import org.jdesktop.wonderland.client.hud.CompassLayout.Layout;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDEvent;
import org.jdesktop.wonderland.client.hud.HUDEventListener;
import org.jdesktop.wonderland.client.hud.HUDManagerFactory;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.view.View2D;
import org.jdesktop.wonderland.modules.appbase.client.view.View2DDisplayer;
import org.jdesktop.wonderland.modules.appbase.client.view.WindowSwingHeader;

/**
 * Provides a <code>View2DDisplay</code> type which is used for displaying app windows in 
 * views in main Wonderland HUD. When an instance of this class is added to the view set of an 
 * app (see <code>View2DSet.add(View2DDisplayer)</code>), all of the app's windows are made 
 * visible in the HUD. They are usually removed from the HUD when the HUD sends a CLOSED 
 * HUDEvent to them.
 *
 * @author deronj
 */

@ExperimentalAPI
public class HUDDisplayer implements View2DDisplayer {

    /** The app displayed by this displayer. */
    private App2D app;
    /** The HUD. */
    private HUD mainHUD;
    /** HUD components for windows shown in the HUD. */
    private HashMap<HUDComponent,Window2D> hudComponents;

    public HUDDisplayer (App2D app) {
        this.app = app;
        mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");
        hudComponents = new HashMap<HUDComponent,Window2D>();
    }

    public void cleanup () {

        // See if cleanup has already happened
        if (mainHUD == null) return;
        
        destroyAllViews();

        mainHUD = null;
        app = null;
    }

    public View2D createView (Window2D window) {

        // Don't ever show frame headers in the HUD
        if (window instanceof WindowSwingHeader) return null;

        HUDComponent component = mainHUD.createComponent(window);
        component.setName(app.getName());
        component.setPreferredLocation(Layout.CENTER);

        // Maintain an association between components and their windows.
        // Note: it would be nice of the HUD could do this but its tricky.
        hudComponents.put(component, window);

        component.addEventListener(new HUDEventListener() {
            public void HUDObjectChanged(HUDEvent e) {
                if (e.getEventType().equals(HUDEvent.HUDEventType.CLOSED)) {
                    HUDComponent comp = (HUDComponent)e.getObject();
                    if (mainHUD != null) {
                        mainHUD.removeComponent(comp);
                    }
                    Window2D compWindow = hudComponents.get(comp);
                    hudComponents.remove(comp);

                    if (compWindow != null) {
                        if (compWindow.getType() == Window2D.Type.PRIMARY ||
                            compWindow.getType() == Window2D.Type.UNKNOWN) {
                            if (app != null) {
                                app.setShowInHUD(false);
                            }
                        }
                    }
                }
            }
        });

        mainHUD.addComponent(component);
        component.setVisible(true);

        // Note: it is okay that null is returned here. The caller never uses it.
        return null;
    }

    public void destroyView (View2D view) {
        // Intentionally a no-op
    }

    public void destroyAllViews () {
        for (HUDComponent component : hudComponents.keySet()) {
            component.setVisible(false);
            mainHUD.removeComponent(component);
        }
        hudComponents.clear();
    }

    public Iterator<? extends View2D> getViews () {
        return null;
    }

}
