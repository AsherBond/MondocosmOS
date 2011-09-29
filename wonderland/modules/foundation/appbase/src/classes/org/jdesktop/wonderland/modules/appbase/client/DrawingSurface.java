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

import com.jme.image.Texture;
import java.awt.Graphics2D;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.modules.appbase.client.view.View2D;

/**
 * INTERNAL API.
 * <br>br>
 * A rectangular, pixel-based drawing surface (image) onto which 2D graphics can be drawn.  You can draw
 * on the surface's image using the Graphics2D returned by <code>getGraphics</code>.
 * <br>br>
 * When this drawing surface is associated with a texture (via <code>setTexture</code>) and updating
 * is enabled (via <code>setUpdateEnable</code>) the contents of the surface are continually copied 
 * into the texture. To be specific, once per frame all of the newly drawn pixels in the surface's image
 * are copied into the texture. Initially updating is disabled. It must be explicitly enabled.
 *
 * @author paulby, deronj
 */
@InternalAPI
public interface DrawingSurface {

    /**
     * Clean up resources held.
     */
    public void cleanup();

    /**
     * Returns this drawing surface's window.
     */
    public Window2D getWindow();

    /**
     * Specify the window which uses this drawing surface.
     * @param window The 2D window which is served by this drawing surface.
     */
    public void setWindow(Window2D window);

    /**
     * Resize the surface. 
     *
     * @param width The new width of the surface in pixels.
     * @param height The new height of the surface in pixels.
     */
    public void setSize(int width, int height);

    /**
     * Initialize the contents of the surface.
     */
    public void initializeSurface();

    /**
     * Returns a Graphics2D to draw on the surface.
     */
    public Graphics2D getGraphics();

    /**
     * Returns the width of the surface.
     */
    public int getWidth();

    /**
     * Returns the height of the surface.
     */
    public int getHeight();

    /**
     * Specify the texture that this surface's contents should be copied into.
     */
    public void setTexture(Texture texture);

    /**
     * Return this surface's associated texture.
     */
    public Texture getTexture();

    /**
     * Enable or disabling the updating of the texture.
     */
    public void setUpdateEnable(boolean enable);

    /**
     * Return whether texture updating is enabled.
     */
    public boolean getUpdateEnable();

    /**
     * Specifies whether a particular view on a surface is visible.
     */
    public void setViewIsVisible(View2D view, boolean visible);
}
