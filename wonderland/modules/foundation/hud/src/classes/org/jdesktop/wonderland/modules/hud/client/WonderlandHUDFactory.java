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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDFactorySPI;

/**
 * A HUD factory which creates new HUD object instances.
 *
 * A Wonderland client will typically have one HUDFactory which is the source
 * of all HUD instances.
 *
 * @author nsimpson
 */
public class WonderlandHUDFactory implements HUDFactorySPI {

    private static final Logger logger = Logger.getLogger(WonderlandHUDFactory.class.getName());

    /**
     * {@inheritDoc}
     */
    public HUD createHUD(Dimension displayBounds) {
        return new WonderlandHUD(displayBounds, 0, 0,
                (int) displayBounds.getWidth(), (int) displayBounds.getHeight());
    }

    /**
     * {@inheritDoc}
     */
    public HUD createHUD(Dimension displayBounds, int x, int y, int width, int height) {
        return new WonderlandHUD(displayBounds, x, y, width, height);

    }

    /**
     * {@inheritDoc}
     */
    public HUD createHUD(Dimension displayBounds, Rectangle hudBounds) {
        return new WonderlandHUD(displayBounds, hudBounds);


    }

    /**
     * {@inheritDoc}
     */
    public HUD createHUD(Dimension displayBounds, Rectangle2D.Float scalableBounds) {
        return new WonderlandHUD(displayBounds, scalableBounds);
    }

    /**
     * {@inheritDoc}
     */
    public HUD createHUD(Dimension displayBounds, float xPercent, float yPercent,
            float widthPercent, float heightPercent) {
        return new WonderlandHUD(displayBounds, xPercent, yPercent, widthPercent, heightPercent);

    }
}
