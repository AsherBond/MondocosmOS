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

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import java.util.logging.Logger;
import org.jdesktop.mtgame.NewFrameCondition;
import org.jdesktop.wonderland.common.InternalAPI;
import java.lang.reflect.Method;
import javax.media.opengl.GLContext;

/**
 * INTERNAL API 
 * <br><br>
 * A type of drawing surface is based on using an AWT Graphics2D to draw to an AWT BufferedImage
 * and then copies the rendering into an ImageGraphics and, ultimately, the Texture. This is not as
 * optimal as DrawingSurfaceImageGraphics, but because it uses a normal AWT Graphics2D its rendering
 * functionality is more complete.
 *
 * @author deronj
 */
@InternalAPI
public class DrawingSurfaceBufferedImage extends DrawingSurfaceImageGraphics {

    private static final Logger logger = Logger.getLogger(DrawingSurfaceBufferedImage.class.getName());
    /** The buffered image. */
    protected BufferedImage bufImage;
    /** The Graphics2D we return from getGraphics */
    protected DirtyTrackingGraphics g;

    // We need to call this method reflectively because it isn't available in Java 5
    // BTW: we don't support Java 5 on Linux, so this is okay.
    private static Method isAWTLockHeldByCurrentThreadMethod;

    static {
        String osName = System.getProperty("os.name");
        boolean isUnix = "Linux".equals(osName) || "SunOS".equals(osName);
        if (isUnix) {
            try {
                Class awtToolkitClass = Class.forName("sun.awt.SunToolkit");
                isAWTLockHeldByCurrentThreadMethod =
                        awtToolkitClass.getMethod("isAWTLockHeldByCurrentThread");
            } catch (ClassNotFoundException ex) {
            } catch (NoSuchMethodException ex) {
            }
        }
    }

    /** 
     * Create an instance of DrawingSurfaceBufferedImage.
     * Before it can be used you must call setWindow.
     */
    public DrawingSurfaceBufferedImage() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void cleanup() {
        super.cleanup();
        bufImage = null;
        g = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void setSize(int width, int height) {
        super.setSize(width, height);

        // Any change?
        if (bufImage != null && width == bufImage.getWidth() && height == bufImage.getHeight()) {
            return;
        }

        // Create new image of new size
        BufferedImage bufImageNew = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        // Create new graphics for the new image
        DirtyTrackingGraphics gNew = new DirtyTrackingGraphics((Graphics2D) bufImageNew.getGraphics());
        gNew.setClip(0, 0, width, height);

        // Erase the buffered image to 50% gray (less noticed by user)
        Color bkgdSave = gNew.getBackground();
        gNew.setBackground(Color.GRAY);
        gNew.clearRect(0, 0, width, height);
        gNew.setBackground(bkgdSave);

        // Copy the old image into the new
        gNew.drawImage(bufImage, 0, 0, null);

        // Dirty the entire new image
        gNew.addDirtyRectangle(0, 0, width, height);

        // Make the new image current
        bufImage = bufImageNew;
        g = gNew;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Graphics2D getGraphics() {
        return g;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeSurface() {
        initSurface(g);
    }

    /**
     * Extracts the pixels in a given subrectangle and places them as bytes in a byte array.
     * @param pixelBytes The byte array in which the pixels are returned.
     * @param x The x coordinate of the origin of the subrectangle.
     * @param y The y coordinate of the origin of the subrectangle.
     * @param width The width of the subrectangle.
     * @param height The height of the subrectangle.
     */
    public synchronized void getPixelBytes(final byte[] pixelBytes, final int x, final int y,
                                           final int width, final int height) {

        WritableRaster srcRas = bufImage.getRaster();
        DataBufferInt srcDataBuf = (DataBufferInt) srcRas.getDataBuffer();
        int[] srcPixels = srcDataBuf.getData();
        int srcLineWidth = bufImage.getWidth();

        int dstIdx = 0;
        int srcIdx = y * srcLineWidth + x;
        int srcNextLineIdx = srcIdx;

        for (int srcY = 0; srcY < height; srcY++) {
            srcNextLineIdx += srcLineWidth;
            for (int srcX = 0; srcX < width; srcX++) {
                int pixel = srcPixels[srcIdx++];
                pixelBytes[dstIdx++] = (byte) ((pixel >> 24) & 0xff);
                pixelBytes[dstIdx++] = (byte) ((pixel >> 16) & 0xff);
                pixelBytes[dstIdx++] = (byte) ((pixel >> 8) & 0xff);
                pixelBytes[dstIdx++] = (byte) (pixel & 0xff);
            }
            srcIdx = srcNextLineIdx;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected UpdateProcessor createUpdateProcessor() {
        return new BufferedImageUpdateProcessor();
    }

    /**
     * A subclass of DrawingSurfaceImageGraphicsBuffer which only performs an update if 
     * the buffered image has been dirtied since it was last cleaned.
     */
    protected class BufferedImageUpdateProcessor extends UpdateProcessor {

        private boolean checkForUpdateReturn;

        /**
         * {@inheritDoc}
         */
        @Override
        protected boolean checkForUpdate() {

            // Linux-specific workaround: On Linux JOGL holds the SunToolkit AWT lock in mtgame commit methods.
            // In order to avoid deadlock with any threads which are already holding the AWT lock and which
            // want to acquire the lock on the dirty rectangle so they can draw (e.g Embedded Swing threads)
            // we need to temporarily release the AWT lock before we lock the dirty rectangle and then reacquire
            // the AWT lock afterward.
            // NOTE: we need to do this manually, rather than using a swingsafe processor, because
            // we need to be holding the AWT lock when we return from this method. 
            // TODO: low: MTgame probably provides a cleaner way to reacquire the lock.
            GLContext glContext = null;
            if (isAWTLockHeldByCurrentThreadMethod != null) {
                try {
                    Boolean ret = (Boolean) isAWTLockHeldByCurrentThreadMethod.invoke(null);
                    if (ret.booleanValue()) {
                        glContext = GLContext.getCurrent();
                        glContext.release();
                    }
                } catch (Exception ex) {
                }
            }

            final int texid = getTexture().getTextureId();

            // In this implementation, we need to check our DirtyTrackingGraphics to see whether it is dirty.
            // If it is dirty, we copy the entire buffered image into the imageGraphics.
            g.executeAtomic(new Runnable() {

                public void run() {
                    Rectangle dirtyRect = g.getDirtyRectangle();
                    if (dirtyRect != null) {

                        //System.err.println("DBSI: Rendering into texid = " + texid);

			int x1 = dirtyRect.x;
			int y1 = dirtyRect.y;
			int x2 = dirtyRect.x + dirtyRect.width - 1;
			int y2 = dirtyRect.y + dirtyRect.height - 1;

                        if (!imageGraphics.drawImage(bufImage, x1, y1, x2, y2,
                                       x1, y1, x2, y2, null, null)) {
                            logger.warning("drawImage returned false. Skipping image rendering.");
                        }

                        g.clearDirty();
                        checkForUpdateReturn = true;
                    } else {
                        checkForUpdateReturn = false;
                    }
                }
            });

            // Linux-specific workaround: Reacquire the lock if necessary.
            if (glContext != null) {
                glContext.makeCurrent();
            }

            return checkForUpdateReturn;
        }

        private void start() {
            setArmingCondition(new NewFrameCondition(this));
        }

        private void stop() {
            setArmingCondition(null);
        }
    }

    private void debugPrintBufImage () {
        int x = 0;
        int y = 0;
        int width = bufImage.getWidth();
        int height = bufImage.getHeight();

        WritableRaster srcRas = bufImage.getRaster();
        DataBufferInt srcDataBuf = (DataBufferInt) srcRas.getDataBuffer();
        int[] srcPixels = srcDataBuf.getData();
        int srcLineWidth = width;

        int dstIdx = 0;
        int srcIdx = y * srcLineWidth + x;
        int srcNextLineIdx = srcIdx;

        width = (width > 20) ? 20 : width;
        height = (height > 20) ? 20 : height;

        for (int srcY = 0; srcY < height; srcY++) {
            srcNextLineIdx += srcLineWidth;
            for (int srcX = 0; srcX < width; srcX++) {
                int pixel = srcPixels[srcIdx++];
                System.err.print(Integer.toHexString(pixel) + " ");
            }
            srcIdx = srcNextLineIdx;
            System.err.println();
        }
    }

    /**
     * A Graphics2D which sets a dirty flag whenever it is used for rendering.
     */
    public class DirtyTrackingGraphics extends Graphics2D {

        /** The delegate Graphics2D */
        private Graphics2D delegate;
        /** The union of all dirty rectangles since the last clear */
        Rectangle dirtyRect;

        private DirtyTrackingGraphics(Graphics2D delegate) {
            this.delegate = delegate;
        }

        public synchronized void addDirtyRectangle(int x, int y, int width, int height) {
            Rectangle newRect = new Rectangle(x, y, width, height);
            if (dirtyRect == null) {
                dirtyRect = newRect;
            } else {
                dirtyRect = unionRects(dirtyRect, newRect);
            }
        }

        // The union of two rectangles returned in a third rectangle.
        private Rectangle unionRects(Rectangle r1, Rectangle r2) {
            int r1x0 = r1.x;
            int r1y0 = r1.y;
            int r1x1 = r1.x + r1.width;
            int r1y1 = r1.y + r1.height;

            int r2x0 = r2.x;
            int r2y0 = r2.y;
            int r2x1 = r2.x + r2.width;
            int r2y1 = r2.y + r2.height;

            int x0 = Math.min(r1x0, r2x0);
            int y0 = Math.min(r1y0, r2y0);
            int x1 = Math.max(r1x1, r2x1);
            int y1 = Math.max(r1y1, r2y1);

            return new Rectangle(x0, y0, x1 - x0, y1 - y0);
        }

        public synchronized Rectangle getDirtyRectangle() {
            return dirtyRect;
        }

        public synchronized void clearDirty() {
            dirtyRect = null;
        }

        public synchronized void executeAtomic(Runnable runnable) {
            runnable.run();
        }

        public Color getColor() {
            return delegate.getColor();
        }

        public void setColor(Color c) {
            delegate.setColor(c);
        }

        public void setPaintMode() {
            delegate.setPaintMode();
        }

        public void setXORMode(Color c1) {
            delegate.setXORMode(c1);
        }

        public Font getFont() {
            return delegate.getFont();
        }

        public void setFont(Font font) {
            delegate.setFont(font);
        }

        public FontMetrics getFontMetrics(Font f) {
            return delegate.getFontMetrics(f);
        }

        public Rectangle getClipBounds() {
            return delegate.getClipBounds();
        }

        public void clipRect(int x, int y, int width, int height) {
            delegate.clipRect(x, y, width, height);
        }

        public void setClip(int x, int y, int width, int height) {
            delegate.setClip(x, y, width, height);
        }

        public Shape getClip() {
            return delegate.getClip();
        }

        public void setClip(Shape clip) {
            delegate.setClip(clip);
        }

        public void dispose() {
            delegate.dispose();
        }

        public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
            return delegate.hit(rect, s, onStroke);
        }

        public GraphicsConfiguration getDeviceConfiguration() {
            return delegate.getDeviceConfiguration();
        }

        public void setComposite(Composite comp) {
            delegate.setComposite(comp);
        }

        public void setPaint(Paint paint) {
            delegate.setPaint(paint);
        }

        public void setStroke(Stroke s) {
            delegate.setStroke(s);
        }

        public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
            delegate.setRenderingHint(hintKey, hintValue);
        }

        public Object getRenderingHint(RenderingHints.Key hintKey) {
            return delegate.getRenderingHint(hintKey);
        }

        public void setRenderingHints(Map<?, ?> hints) {
            delegate.setRenderingHints(hints);
        }

        public void addRenderingHints(Map<?, ?> hints) {
            delegate.addRenderingHints(hints);
        }

        public RenderingHints getRenderingHints() {
            return delegate.getRenderingHints();
        }

        public void translate(int x, int y) {
            delegate.translate(x, y);
        }

        public void translate(double tx, double ty) {
            delegate.translate(tx, ty);
        }

        public void rotate(double theta) {
            delegate.rotate(theta);
        }

        public void rotate(double theta, double x, double y) {
            delegate.rotate(theta, x, y);
        }

        public void scale(double sx, double sy) {
            delegate.scale(sx, sy);
        }

        public void shear(double shx, double shy) {
            delegate.shear(shx, shy);
        }

        public void transform(AffineTransform Tx) {
            delegate.transform(Tx);
        }

        public void setTransform(AffineTransform Tx) {
            delegate.setTransform(Tx);
        }

        public AffineTransform getTransform() {
            return delegate.getTransform();
        }

        public Paint getPaint() {
            return delegate.getPaint();
        }

        public Composite getComposite() {
            return delegate.getComposite();
        }

        public void setBackground(Color color) {
            delegate.setBackground(color);
        }

        public Color getBackground() {
            return delegate.getBackground();
        }

        public Stroke getStroke() {
            return delegate.getStroke();
        }

        public void clip(Shape s) {
            delegate.clip(s);
        }

        public FontRenderContext getFontRenderContext() {
            return delegate.getFontRenderContext();
        }

        public Graphics create() {
            return new DirtyTrackingGraphics((Graphics2D) delegate.create());
        }

        @Override
        public Graphics create(int x, int y, int width, int height) {
            return new DirtyTrackingGraphics((Graphics2D) delegate.create(x, y, width, height));
        }

        public void copyArea(int x, int y, int width, int height, int dx, int dy) {
            delegate.copyArea(x, y, width, height, dx, dy);
        }

        public void drawLine(int x1, int y1, int x2, int y2) {
            delegate.drawLine(x1, y1, x2, y2);
        }

        public void fillRect(int x, int y, int width, int height) {
            delegate.fillRect(x, y, width, height);
        }

        public void clearRect(int x, int y, int width, int height) {
            delegate.clearRect(x, y, width, height);
        }

        public void drawRoundRect(int x, int y, int width, int height, int arcWidth,
                int arcHeight) {
            delegate.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
        }

        public void fillRoundRect(int x, int y, int width, int height, int arcWidth,
                int arcHeight) {
            delegate.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
        }

        public void drawOval(int x, int y, int width, int height) {
            delegate.drawOval(x, y, width, height);
        }

        public void fillOval(int x, int y, int width, int height) {
            delegate.fillOval(x, y, width, height);
        }

        public void drawArc(int x, int y, int width, int height, int startAngle,
                int arcAngle) {
            delegate.drawArc(x, y, width, height, startAngle, arcAngle);
        }

        public void fillArc(int x, int y, int width, int height, int startAngle,
                int arcAngle) {
            delegate.fillArc(x, y, width, height, startAngle, arcAngle);
        }

        public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
            delegate.drawPolyline(xPoints, yPoints, nPoints);
        }

        public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
            delegate.drawPolygon(xPoints, yPoints, nPoints);
        }

        public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
            delegate.fillPolygon(xPoints, yPoints, nPoints);
        }

        public boolean drawImage(java.awt.Image img, int x, int y, ImageObserver observer) {
            return delegate.drawImage(img, x, y, observer);
        }

        public boolean drawImage(java.awt.Image img, int x, int y, int width, int height,
                ImageObserver observer) {
            return delegate.drawImage(img, x, y, width, height, observer);
        }

        public boolean drawImage(java.awt.Image img, int x, int y, Color bgcolor,
                ImageObserver observer) {
            return delegate.drawImage(img, x, y, bgcolor, observer);
        }

        public boolean drawImage(java.awt.Image img, int x, int y, int width, int height,
                Color bgcolor, ImageObserver observer) {
            return delegate.drawImage(img, x, y, width, height, bgcolor, observer);
        }

        public boolean drawImage(java.awt.Image img, int dx1, int dy1, int dx2, int dy2,
                int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
            return delegate.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
        }

        public boolean drawImage(java.awt.Image img, int dx1, int dy1, int dx2, int dy2,
                int sx1, int sy1, int sx2, int sy2, Color bgcolor,
                ImageObserver observer) {
            return delegate.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
        }

        public void draw(Shape s) {
            delegate.draw(s);
        }

        public boolean drawImage(java.awt.Image img, AffineTransform xform,
                ImageObserver obs) {
            return delegate.drawImage(img, xform, obs);
        }

        public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
            delegate.drawImage(img, op, x, y);
        }

        public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
            delegate.drawRenderedImage(img, xform);
        }

        public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
            delegate.drawRenderableImage(img, xform);
        }

        public void drawString(String str, int x, int y) {
            delegate.drawString(str, x, y);
        }

        public void drawString(String s, float x, float y) {
            delegate.drawString(s, x, y);
        }

        public void drawString(AttributedCharacterIterator iterator, int x, int y) {
            delegate.drawString(iterator, x, y);
        }

        public void drawString(AttributedCharacterIterator iterator, float x, float y) {
            delegate.drawString(iterator, x, y);
        }

        public void drawGlyphVector(GlyphVector g, float x, float y) {
            delegate.drawGlyphVector(g, x, y);
        }

        public void fill(Shape s) {
            delegate.fill(s);
        }
    }
}
