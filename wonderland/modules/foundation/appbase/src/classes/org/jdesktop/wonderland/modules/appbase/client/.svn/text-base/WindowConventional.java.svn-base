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

import com.jme.math.Vector2f;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * A window object for a 2D conventional type of application. This kind of window provides main two ways
 * to update its image contents: <code>displayPixels</code> and <code>copyArea</code>. 
 * <br><br>
 * The window provides the concept of a "border width." Some conventional window systems (namely, X11)
 * support a non-zero gap of borderWidth number of pixels thickness ringing the window. No drawing is 
 * performed in this area. The input coordinates to <code>displayPixels</code> and <code>copyArea</code> 
 * are in border-less window coordinates (that is, they don't include the border). (This is opposed to 
 * window image coordinates which include the border. But these coordinates are only used internally). 
 * If the conventional window system used by a subclass doesn't support the concept of border width then 
 * the <code>borderWidth argument</code> passed to the constructor should always be 0.
 * <br><br>
 * WARNING: This interface is not thread safe. External synchronization must be performed if the 
 * <code>displayPixels</code> and <code>copyArea</code> methods of the same window are to be accessed 
 * simultaneously by multiple threads.
 *
 * @author deronj
 */
@ExperimentalAPI
public abstract class WindowConventional extends Window2D {

    private static final Logger logger = Logger.getLogger(WindowConventional.class.getName());
    /** The app to which the window belongs */
    protected AppConventional appConventional;
    /** The border width of the window */
    protected int borderWidth;
    /** An intermediate buffer used by displayPixels. */
    private BufferedImage tempImage;

    /** 
     * Create a new instance of WindowConventional with a default name.
     *
     * @param app The app to which the window belongs.
     * @param width The width of the window (in pixels). This does NOT include the borderWidth.
     * @param height The height of the window (in pixels). This does NOT include the borderWidth.
     * @param decorated Whether the window is decorated with a frame.
     * @param borderWidth The border width of the window.
     * @param pixelScale The size of the window pixels in world coordinates.
     */
    public WindowConventional(App2D app, int width, int height, boolean decorated, int borderWidth, 
                              Vector2f pixelScale) {
        super(app, width, height, decorated, pixelScale, new DrawingSurfaceBufferedImage());
        this.borderWidth = borderWidth;
        appConventional = (AppConventional) app;
    }

    /** 
     * Create a new instance of WindowConventional with the given  name.
     *
     * @param app The app to which the window belongs.
     * @param width The width of the window (in pixels). This does NOT include the borderWidth.
     * @param height The height of the window (in pixels). This does NOT include the borderWidth.
     * @param decorated Whether the window is decorated with a frame.
     * @param borderWidth The border width of the window.
     * @param pixelScale The size of the window pixels in world coordinates.
     * @param name The name of the window.
     */
    public WindowConventional(App2D app, int width, int height, boolean decorated, int borderWidth, 
                              Vector2f pixelScale, String name) {
        super(app, width, height, decorated, pixelScale, name, new DrawingSurfaceBufferedImage());
        this.borderWidth = borderWidth;
        appConventional = (AppConventional) app;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        super.cleanup();
    }

    /**
     * {@inheritDoc}
     * Note: the arguments do NOT include the borderWidth.
     */
    @Override
    public void setSize(int width, int height) {
        super.setSize(width + 2 * borderWidth, height + 2 * borderWidth);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(int width, int height, Window2D sibWin) {
        setSize(width, height);
        super.configure(width, height, sibWin);
    }

    /**
     * Specify a new border width.
     * The visual representations of the window are updated accordingly.
     *
     * @param borderWidth The new border width.
     */
    public void setBorderWidth(int borderWidth) {
        int width = getWidth();
        int height = getHeight();
        width -= 2 * borderWidth;
        height -= 2 * borderWidth;
        this.borderWidth = borderWidth;
        setSize(width, height);
    }

    /** 
     * Returns the border width of the window.
     */
    public int getBorderWidth() {
        return borderWidth;
    }

    /**
     * Insert the given pixels into the window's image into a subrectangle starting at (x, y) 
     * (in borderless coordinates) and having dimensions w x h.
     * 
     * @param x The X coordinate of the top-lel corner of the image subrectangle which is to be changed.
     * @param y The Y coordinate of the top left corner of the image subrectangle which is to be changed.
     * @param w The width of the image subrectangle which is to be changed.
     * @param h The height of the image subrectangle which is to be changed.
     * @param pixels An array which contains the pixels. It must be of length w x h.
     */
    public void displayPixels(final int x, final int y, final int w, final int h, int[] pixels) {
        if (pixels == null || surface == null) {
            return;
        }

        // Grow temp image if necessary
        if (tempImage == null ||
            tempImage.getWidth() != w || tempImage.getHeight() != h) {
            tempImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        }

        // Put pixel buffer into tempImage
        int dstWidth = tempImage.getWidth();
        WritableRaster ras = tempImage.getRaster();
        DataBufferInt dataBuf = (DataBufferInt) ras.getDataBuffer();
        int[] dstPixels = dataBuf.getData();
        if (dstPixels == null) {
            return;
        }
        int srcIdx = 0;
        int dstIdx = 0;
        int dstNextLineIdx = dstIdx;
        for (int srcY = 0; srcY < h; srcY++) {
            dstNextLineIdx += dstWidth;
            for (int srcX = 0; srcX < w; srcX++) {
                dstPixels[dstIdx++] = pixels[srcIdx++];
            }
            dstIdx = dstNextLineIdx;
        }

        final DrawingSurfaceBufferedImage.DirtyTrackingGraphics gDst =
                (DrawingSurfaceBufferedImage.DirtyTrackingGraphics) surface.getGraphics();
        gDst.setClip(x, y, w, h);
        gDst.executeAtomic(new Runnable() {

            public void run() {
                gDst.drawImage(tempImage, x, y, w, h, null);
                gDst.addDirtyRectangle(x, y, w, h);
            }
        });
    }

    /**
     * Copy the contents of a source subrectangle of the window's image to a destination subrectangle.
     * The source subrectangle starts at (srcX, srcY) (in borderless coordinates) and has dimensions
     * width x height. The destination subrectangle starts at (dstX, dstY) (in borderless coordinates) 
     * and has dimensions width x height. The source and destination rectangles can overlap.
     *
     * @param srcX The X coordinate of the top left corner of the source subrectangle.
     * @param srcY The Y coordinate of the top left corner of the source subrectangle.
     * @param width The width of both the source and destination subrectangles.
     * @param height The height of both the source and destination subrectangles.
     * @param dstX The X coordinate of the top left corner of the destination subrectangle.
     * @param dstY The Y coordinate of the top left corner of the destination subrectangle.
     */
    public void copyArea(final int srcX, final int srcY, final int width, final int height, 
                         final int dstX, final int dstY) {
        final DrawingSurfaceBufferedImage.DirtyTrackingGraphics gDst =
                (DrawingSurfaceBufferedImage.DirtyTrackingGraphics) surface.getGraphics();
        gDst.setClip(dstX, dstY, width, height);
        gDst.executeAtomic(new Runnable() {

            public void run() {
                gDst.copyArea(srcX, srcY, width, height, dstX-srcX, dstY-srcY);
                gDst.addDirtyRectangle(dstX, dstY, width, height);
            }
        });
    }

    /**
     * Extracts the pixels in a given subrectangle and places them as bytes in a byte array.
     * @param pixelBytes The byte array in which the pixels are returned.
     * @param x The x coordinate of the origin of the subrectangle.
     * @param y The y coordinate of the origin of the subrectangle.
     * @param width The width of the subrectangle.
     * @param height The height of the subrectangle.
     */
    public void getPixelBytes (final byte[] pixelBytes, final int x, final int y, 
                               final int width, final int height) {

        final DrawingSurfaceBufferedImage.DirtyTrackingGraphics gDst =
                (DrawingSurfaceBufferedImage.DirtyTrackingGraphics) surface.getGraphics();
        gDst.executeAtomic(new Runnable() {

            public void run() {
                ((DrawingSurfaceBufferedImage)surface).getPixelBytes(pixelBytes, x, y, width, height);
            }
        });
    }
}
