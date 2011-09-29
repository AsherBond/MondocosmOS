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
package org.jdesktop.wonderland.modules.appbase.client.cell.view.viewdefault;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import java.awt.Dimension;
import org.jdesktop.mtgame.RenderUpdater;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.modules.appbase.client.view.GeometryNode;
import com.jme.renderer.ColorRGBA;

/**
 * A frame component which is a transparent rectangle. This component is made
 * visible during the interactive resizing of a view to demonstrate to the user
 * the currently selected size of the view. Only after the user commits to that
 * size by releasing the mouse button is the view actually resized. Use of this
 * "proxy rectangle" is much more efficient than trying to resize the view on
 * every change.
 *
 * @author deronj
 */

public class ResizeRectangle extends FrameTranspRect {

    private boolean visible;

    private Node nodeZTrans;

    /** 
     * Create a new instance of <code>ResizeRect</code>.
     *
     * @param view The view the rectangle helps resize
     */
    public ResizeRectangle (View2DCell view) {
        // Rectangle's size is initialized but set to something sensible later.
        // Rectangle doesn't receive events (no GUI object).
        super("ResizeRect", view, null, 1, 1);

        nodeZTrans = new Node("Node Z Trans for View " + view);

        final Spatial[] spatials = getSpatials();

        ClientContextJME.getWorldManager().addRenderUpdater(new RenderUpdater() {
            public void update(Object arg0) {
                for (int i = 0; i < spatials.length; i++) {
                    nodeZTrans.attachChild(spatials[i]);
                }
                nodeZTrans.setLocalTranslation(new Vector3f(0f, 0f, 0.001f));
                ClientContextJME.getWorldManager().addToUpdateList(nodeZTrans);
            }
        }, null, false);

        ColorRGBA transpYellow = new ColorRGBA(ColorRGBA.yellow);
        transpYellow.a = 0.5f;
        setColorAndOpacity(transpYellow);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void cleanup() {
        setVisible(false);
        super.cleanup();
        nodeZTrans = null;
    }

    /**
     * Resize this rect based on the size of its view.
     */
    public void updateSizeFromView () {
        float width, height;

        if (view.isDecorated()) {
            // Resize rectangle must cover the frame. So, if a frame exists, the width 
            // is the header width. Height is left side height;
            Frame2DCell frame = view.getFrame();
            width = frame.getHeaderWidth();
            height = frame.getLeftSideHeight() + frame.getHeaderHeight() + frame.getBottomSideHeight();
        } else {
            // No frame. Resize rect merely covers the view.
            width = view.getDisplayerLocalWidth();
            height = view.getDisplayerLocalHeight();
        }

        try {
            resize(width, height);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Add the given vector to the 3D size.
     */
    public void sizeAdd (Vector2f vec) {
        //System.err.println("***** vec = " + vec);

        //System.err.println("***** before ");
        //System.err.println("***** width = " + width);
        //System.err.println("***** height = " + height);

        if (vec.x == 0 && vec.y == 0) return;

        // Convert local coords drag vector to a pixel vector
        Vector2f pixelScale = view.getPixelScaleCurrent();
        int deltaWidth = (int)(vec.x / pixelScale.x);
        int deltaHeight = (int)(vec.y / pixelScale.y);

        //System.err.println("deltaWH = " + deltaWidth + ", " + deltaHeight);

        int widthPix = view.getWindow().getWidth();
        int heightPix = view.getWindow().getHeight();

        //System.err.println("Old size, wh = " + width + ", " + height);
        widthPix += deltaWidth;
        heightPix -= deltaHeight;
        if (widthPix < 1) widthPix = 1;
        if (heightPix < 1) heightPix = 1;

        float newWidth3D = widthPix * pixelScale.x;
        float newHeight3D = heightPix * pixelScale.y;

        try {
            resize(newWidth3D, newHeight3D);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        //System.err.println("***** after");
        //System.err.println("***** width = " + width);
        //System.err.println("***** height = " + height);
    }

    /**
     * Return the desired integral view size, based on the size of this resize rectangle.
     */
    public Dimension getViewSize () {
        float viewWidth3D = width;
        float viewHeight3D = height;

        if (view.isDecorated()) {
            Frame2DCell frame = view.getFrame();
            viewWidth3D -= frame.getLeftSideWidth() + frame.getRightSideWidth();
            viewHeight3D -= frame.getHeaderHeight() + frame.getBottomSideHeight();
        }

        // Convert to window coordinates
        Vector2f pixelScale = view.getPixelScaleCurrent();
        int widthPix = (int) (viewWidth3D / pixelScale.x);
        int heightPix = (int) (viewHeight3D / pixelScale.y);

        //System.err.println("***** widthPix = " + widthPix);
        //System.err.println("***** heightPix = " + heightPix);

        if (widthPix < 1) {
            widthPix = 1;
        }
        if (heightPix < 1) {
            heightPix = 1;
        }

        return new Dimension(widthPix, heightPix);
    }

    public void setVisible (final boolean visible) {
        if (visible == this.visible) return;

        final GeometryNode geomNode = view.getGeometryNode();
        
        ClientContextJME.getWorldManager().addRenderUpdater(new RenderUpdater() {
            public void update(Object arg0) {
                if (visible) {
                    geomNode.attachChild(nodeZTrans);
                } else {
                    geomNode.detachChild(nodeZTrans);
                }
                ClientContextJME.getWorldManager().addToUpdateList(geomNode);
            }
        }, null, false);

        this.visible = visible;
    }
}
