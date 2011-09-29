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
package org.jdesktop.wonderland.client.hud;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

/**
 * A HUD factory which creates new HUD object instances.
 *
 * A Wonderland client will typically have one HUDFactory which is the source
 * of all HUD instances.
 *
 * @author nsimpson
 */
public class HUDFactory {

    private static HUDFactorySPI spi;

    /**
     * Binds a specific HUD factory service provider to this factory.
     * @param spii an instance of a HUD factory service provider
     */
    public static void setHUDFactorySPI(final HUDFactorySPI spii) {
        spi = spii;
    }

    /**
     * Creates a new Wonderland HUD instance the same size as the display.
     * @param displayBounds the size of the display
     * @return a new HUD instance, sized to the display
     */
    public static HUD createHUD(Dimension displayBounds) {
        if (spi != null) {
            return spi.createHUD(displayBounds);
        } else {
            return null;
        }
    }

    /**
     * Creates a new Wonderland HUD instance with a fixed size.
     * @param displayBounds the size of the display in pixels
     * @param x the x position of the HUD relative to the x origin of the view
     * @param y the y position of the HUD relative to the y origin of the view
     * @param width the width of the HUD relative to the width of the view
     * @param height the height of the HUD relative to the height of the view
     * @return a new HUD instance with the specified fixed size
     */
    public static HUD createHUD(Dimension displayBounds, int x, int y, int width, int height) {
        if (spi != null) {
            return spi.createHUD(displayBounds, x, y, width, height);
        } else {
            return null;
        }
    }

    /**
     * Creates a new Wonderland HUD instance using percentages of the display
     * size for the bounds of the HUD.
     * @param displayBounds the size of the display in pixels
     * @param hudBounds the size and position of the HUD expressed in pixels
     * @return a new HUD instance with the specified fixed size
     */
    public static HUD createHUD(Dimension displayBounds, Rectangle hudBounds) {
        if (spi != null) {
            return spi.createHUD(displayBounds, hudBounds);
        } else {
            return null;
        }
    }

    /**
     * Creates a new Wonderland HUD instance using percentages of the display
     * size for the bounds of the HUD.
     * @param displayBounds the size of the display in pixels
     * @param scalableBounds the size and position of the HUD expressed in
     * percentages
     */
    public static HUD createHUD(Dimension displayBounds, Rectangle2D.Float scalableBounds) {
        if (spi != null) {
            return spi.createHUD(displayBounds, scalableBounds);
        } else {
            return null;
        }
    }

    /**
     * Creates a new HUD instance with scalable bounds.
     * @param displayBounds the size of the display in pixels
     * @param xPercent the x-coordinate of the HUD as a percentage of the width
     * of the display
     * @param yPercent the y-coordinate of the HUD as a percentage of the height
     * of the display
     * @param widthPercent the width of the HUD as a percentage of the width of
     * the display
     * @param heightPercent the height of the HUD as a percentage of the height
     * of the display
     * @return a new HUD instance with scalable bounds.
     */
    public static HUD createHUD(Dimension displayBounds, float xPercent, float yPercent,
            float widthPercent, float heightPercent) {
        if (spi != null) {
            return spi.createHUD(displayBounds, xPercent, yPercent,
                    widthPercent, heightPercent);
        } else {
            return null;
        }
    }
}
