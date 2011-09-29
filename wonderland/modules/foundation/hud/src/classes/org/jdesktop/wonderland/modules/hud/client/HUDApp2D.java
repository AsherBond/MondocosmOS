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

/*
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
package org.jdesktop.wonderland.modules.hud.client;

import com.jme.math.Vector2f;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.modules.appbase.client.App2D;
import org.jdesktop.wonderland.modules.appbase.client.ControlArb;
import org.jdesktop.wonderland.modules.appbase.client.Window2D.Type;
import org.jdesktop.wonderland.modules.appbase.client.swing.WindowSwing;

/**
 * A class which provides an appbase implementation of the HUD.
 *
 * Manages a set of HUD windows. The windows are displayed by a displayer
 * which is responsible for creating HUD specific views.
 *
 * @author nsimpson
 */
public class HUDApp2D extends App2D {

    private static final Logger logger = Logger.getLogger(HUDApp2D.class.getName());

    /** the HUD this app is associated with */
    private final HUD hud;

    /**
     * Create a new instance of HUDApp2D with a default name.
     *
     * @param controlArb The control arbiter to use. null means that all users can control at the same time.
     * @param pixelScale The size of the window pixels in world coordinates.
     */
    public HUDApp2D(HUD hud, ControlArb controlArb, Vector2f pixelScale) {
        super(controlArb, pixelScale);

        this.hud = hud;

        controlArb.setApp(this);
        controlArb.takeControl();
    }

    /**
     * Create a new instance of HUDApp2D with the given name.
     *
     * @param name The name of the app.
     * @param controlArb The control arbiter to use. null means that all users can control at the same time.
     * @param pixelScale The size of the window pixels in world coordinates.
     */
    public HUDApp2D(HUD hud, String name, ControlArb controlArb, Vector2f pixelScale) {
        super(name, controlArb, pixelScale);

        this.hud = hud;

        controlArb.setApp(this);
        controlArb.takeControl();
    }

    /**
     * Create a new WindowSwing window as a container for a Swing Component to
     * be displayed on the HUD.
     *
     * @param width The width (in pixels) of the window.
     * @param height The height (in pixels of the window.
     * @param topLevel Whether the window is top-level (that is, whether the window is decorated with a frame).
     */
    public WindowSwing createWindow(int width, int height, Type type, boolean decorated, Vector2f pixelScale, String name)
            throws InstantiationException {
        logger.info("creating HUD window: " + type + ", " + width + "x" + height + ", " + decorated +
                ", " + pixelScale + ", " + name);

        return new HUDWindow(hud, this, type, width, height, decorated, pixelScale, name);
    }

    /**
     * Create a new WindowSwing window as a container for a Swing Component to
     * be displayed on the HUD.
     *
     * @param width The width (in pixels) of the window.
     * @param height The height (in pixels of the window.
     * @param topLevel Whether the window is top-level (that is, whether the window is decorated with a frame).
     */
    public WindowSwing createWindow(int width, int height, Type type, HUDWindow parent,
                                    boolean decorated, Vector2f pixelScale, String name)
            throws InstantiationException {
        logger.info("creating HUD window: " + type + ", " + width + "x" + height + ", " + decorated +
                ", " + pixelScale + ", " + name);

        return new HUDWindow(hud, this, type, parent, width, height, decorated, pixelScale, name);
    }
}
