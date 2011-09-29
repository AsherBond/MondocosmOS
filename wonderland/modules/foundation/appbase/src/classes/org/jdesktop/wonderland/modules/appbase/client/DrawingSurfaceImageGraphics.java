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
import com.jmex.awt.swingui.ImageGraphics;
import java.awt.Point;
import java.util.HashSet;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.NewFrameCondition;
import org.jdesktop.mtgame.ProcessorArmingCollection;
import org.jdesktop.mtgame.ProcessorComponent;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.modules.appbase.client.view.View2D;

/**
 * INTERNAL API 
 * <br><br>
 * A rectangular, pixel-based drawing surface (image) onto which 2D graphics can be drawn.  You can draw
 * on the surface's image using the Graphics2D returned by <code>getGraphics</code>.
 * <br><br>
 * When this drawing surface is associated with a texture (via <code>setTexture</code>) and updating
 * is enabled (via <code>setUpdateEnable</code>) the contents of the surface are continually copied 
 * into the texture. To be specific, once per frame all of the newly drawn pixels in the surface's image
 * are copied into the texture. Initially updating is disabled. It must be explicitly enabled.
 * <br><br>
 * This class provides to subclasses the protected members (penX, penY) which the subclass can use to 
 * indicate the current position at which drawing operations take place (the "pen"). An XOR cursor is 
 * drawn on the image to visually indicate this position.
 * <br><br>
 * NOTE: this is an internal API. Instead of using this class use the public DrawingSurfaceBufferedImage
 * instead. The reason is not yet a part of the public App Base API is because the JME ImageGraphics 
 * returns from getGraphics() is flawed. It doesn't properly handle the screen space footprint of 
 * rendered primitives for many different types of graphics parameters and primitive types, such as 
 * wide lines, horizontal/vertical lines, and non-unity user-to-screen space transforms. 
 * DrawingSurface is slower because it copies the entire image but it is guaranteed to render 
 * correctly in all cases.
 *
 * @author paulby, deronj
 */
@InternalAPI
public abstract class DrawingSurfaceImageGraphics implements DrawingSurface {

    private static final Logger logger = Logger.getLogger(DrawingSurfaceImageGraphics.class.getName());

    /* The ImageGraphics onto which drawing for this surface is rendered. */
    protected ImageGraphics imageGraphics;

    /* The X location of the current cursor position (in image coordinates). */
    protected int penX;

    /* The Y location of the current cursor position (in image coordinates). */
    protected int penY;
    /** The width of the surface (in pixels) */
    protected int surfaceWidth;
    /** The height of the surface (in pixels) */
    protected int surfaceHeight;
    /** The desired destination texture. */
    private Texture texture;
    /** Whether texture updating is enabled */
    private boolean updateEnable;
    /** The processor performing the updates. */
    private UpdateProcessor updateProcessor;
    /** The entity to which the processor is attached. */
    private Entity updateEntity;
    /** The 2D window which is served by this drawing surface. */
    private Window2D window;
    /** Whether this drawing surface is for an app running in the sas. */
    private boolean isInSas;
    /** The set of views which are visible and which are displaying this surface. */
    private HashSet<View2D> visibleViews = new HashSet<View2D>();
    /**  
     * Create an instance of DrawingSurfaceImageGraphics with the given name.
     * Before it can be used you must call setWindow.
     */
    public DrawingSurfaceImageGraphics() {}

    /**
     * Clean up resources held.
     */
    public synchronized void cleanup() {
        setUpdateEnable(false);
        imageGraphics = null;
        texture = null;
    }

    /**
     * Returns this drawing surface's window.
     */
    public Window2D getWindow() {
        return window;
    }

    /**
     * Specify the window which uses this drawing surface.
     * @param window The 2D window which is served by this drawing surface.
     */
    public void setWindow(Window2D window) {
        this.window = window;
        isInSas = App2D.isInSas();
        setSize(window.getWidth(), window.getHeight());
    }

    /**
     * Resize the surface. 
     *
     * @param width The new width of the surface in pixels.
     * @param height The new height of the surface in pixels.
     */
    public synchronized void setSize(int width, int height) {
        surfaceWidth = width;
        surfaceHeight = height;
        
        if (!isInSas) {
            imageGraphics = ImageGraphics.createInstance(width, height, 0);
            imageGraphics.clipRect(0, 0, width, height);
        }

        updateUpdating();
    }

    /**
     * Initialize the contents of the surface.
     */
    public synchronized void initializeSurface() {
        initSurface(getGraphics());
    }

    /**
     * Given an (x,y) coordinate, where both x and y are in the range [-1,1]
     * relative to the center of the image, calculate the corresponding
     * pixel location in the image.
     *
     * @return The corresponding pixel location in the image.
     */
    protected Point computeImagePoint(float x, float y) {
        int pX = surfaceWidth / 2 + (int) ((x * surfaceWidth) / 2);
        int pY = surfaceHeight / 2 + (int) ((y * surfaceHeight) / 2);

        return new Point(pX, pY);
    }

    /**
     * Called on surface initialization after the surface has been
     * initially erased. The subclass can draw whatever it wants on 
     * the surface. However, care should be taken to draw in a place
     * that doesn't get overdrawn by future paints.
     *
     * @param g The Graphics2D that must be used during this call 
     * to draw to the surface. Use g instead of getGraphics().
     */
    protected void initSurface(Graphics2D g) {
    }

    /**
     * Returns a Graphics2D to draw on the surface.
     */
    public abstract Graphics2D getGraphics();

    /**
     * Returns the width of the surface.
     */
    public int getWidth() {
        return surfaceWidth;
    }

    /**
     * Returns the height of the surface.
     */
    public int getHeight() {
        return surfaceHeight;
    }

    /**
     * Specify the texture that this surface's contents should be copied into.
     */
    public synchronized void setTexture(Texture texture) {
        this.texture = texture;
        updateUpdating();
    }

    /**
     * Return this surface's associated texture.
     */
    public synchronized Texture getTexture() {
        return texture;
    }

    /**
     * Enable or disabling the updating of the texture.
     */
    public synchronized void setUpdateEnable(boolean enable) {
        if (enable == updateEnable) {
            return;
        }
        updateEnable = enable;
        updateUpdating();
    }

    /**
     * Return whether texture updating is enabled.
     */
    public synchronized boolean getUpdateEnable() {
        return updateEnable;
    }

    /** {@inheritDoc} */
    public void setViewIsVisible(View2D view, boolean isVisible) {
        if (isVisible) {
            if (!visibleViews.contains(view)) {
                visibleViews.add(view);
            }
        } else {
            if (visibleViews.contains(view)) {
                visibleViews.remove(view);
            }
        }

        // If there are any views which are visible, make sure drawing surface is being updated
        if (visibleViews.size() > 0) {
            if (!getUpdateEnable()) {
                logger.info("Enable updating for surface " + this);
                setUpdateEnable(true);
            }
        } else {
            if (getUpdateEnable()) {
                logger.info("Disable updating for surface " + this);
                setUpdateEnable(false);
            }
        }
    }

    /**
     * Check whether or not updating should be activated.
     */
    private synchronized void updateUpdating() {
        
        /** Don't enable texture updating if running in the SAS. */
        if (isInSas) return;

        if (updateEnable && imageGraphics != null && texture != null) {
            if (updateProcessor == null) {
                updateProcessor = createUpdateProcessor();
                updateEntity = new Entity("DrawingSurface updateEntity");
                updateEntity.addComponent(ProcessorComponent.class, updateProcessor);
                ClientContextJME.getWorldManager().addEntity(updateEntity);
                updateProcessor.start();
                logger.info("Updating enabled for " + this);
            }
        } else {
            if (updateProcessor != null) {
                updateProcessor.stop();
                ClientContextJME.getWorldManager().removeEntity(updateEntity);
                updateEntity.removeComponent(ProcessorComponent.class);
                updateEntity = null;
                updateProcessor = null;
                logger.info("Updating disabled for " + this);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public String toString () {
        if (window == null) {
            return "Anonymous DrawingSurface";
        } else {
            return "Surface for window " + window.getName();
        }
    }

    /**
     * Create a new instance of UpdateProcessor.
     */
    protected UpdateProcessor createUpdateProcessor() {
        return new UpdateProcessor();
    }

    protected class UpdateProcessor extends ProcessorComponent {

        public void initialize() {
        }

        /**
         * Called once per frame to perform the update.
         */
        public void compute(ProcessorArmingCollection collection) {
        }

        /**
         * Called once per frame to perform the update.
         */
        public void commit(ProcessorArmingCollection collection) {
            synchronized (DrawingSurfaceImageGraphics.this) {
                // TODO: doug: okay to be doing a lock and this much work in a commit?
                if (texture == null) {
                    return;
                }
                if (texture.getTextureId() == 0) {
                    logger.warning("Trying to draw to texture whose ID hasn't been allocated");
                    stop();
                    return;
                    /* TODO: just report, don't try to force allocate at this point. If it hasn't
                       been allocated up until now it probably won't be.
                       >>>> I think I can remove this now. But I'm leaving it in a bit longer to be sure.
                    if (window == null) {
                        return;
                    }
                    window.forceTextureIdAssignment();
                    if (texture.getTextureId() == 0) {
                        stop();
                        logger.warning("Destination texture = " + texture);
                        throw new RuntimeException("imageGraphics.update when texture id is still unassigned!!!!");
                    }
                    */
                }

                if (checkForUpdate()) {
                    if (imageGraphics != null && texture != null && texture.getTextureId() != 0) {
                        imageGraphics.update(texture, true);
                    }
                }
            }
        }

        /**
         * Return whether the processor should perform imageGraphics.update.
         */
        protected boolean checkForUpdate() {
            // In this implementation, the imageGraphics.update always checks itself to see whether
            // it is dirty and needs copying into the texture.
            return true;
        }

        private void start() {
            setArmingCondition(new NewFrameCondition(this));
        }

        private void stop() {
            setArmingCondition(null);
        }
    }
}
