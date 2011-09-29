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
package org.jdesktop.wonderland.modules.appbase.client.swing;

import com.jme.math.Vector3f;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import javax.swing.JComponent;
import javax.swing.Popup;
import com.sun.embeddedswing.EmbeddedToolkit;
import com.sun.embeddedswing.EmbeddedPeer;
import java.awt.Canvas;
import org.jdesktop.wonderland.modules.appbase.client.DrawingSurface;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Point;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.jdesktop.mtgame.EntityComponent;
import org.jdesktop.wonderland.client.input.InputManager;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.modules.appbase.client.DrawingSurfaceBufferedImage;
import org.jdesktop.wonderland.modules.appbase.client.Window2D;
import org.jdesktop.wonderland.modules.appbase.client.view.View2D;
import org.jdesktop.wonderland.modules.appbase.client.App2D;

/**
 * The main interface to Embedded Swing. This singleton provides access to the three basic capabilities
 * of Embedded Swing.
 * <br><br>
 * 1. Component embedding for the purpose of drawing.
 * <br><br>
 * 2. Mouse event handling.
 * <br><br>
 * 3. Popup window creation.
 */
class WindowSwingEmbeddedToolkit
        extends EmbeddedToolkit<WindowSwingEmbeddedToolkit.WindowSwingEmbeddedPeer> {

    private static final Logger logger = Logger.getLogger(WindowSwingEmbeddedToolkit.class.getName());
    private static final WindowSwingEmbeddedToolkit embeddedToolkit = new WindowSwingEmbeddedToolkit();
    private Point lastPressPointScreen;
    private WindowSwing.EventHook lastPressEventHook;
    private WindowSwing lastWindow;

    public static WindowSwingEmbeddedToolkit getWindowSwingEmbeddedToolkit() {
        return embeddedToolkit;
    }

    @Override
    protected WindowSwingEmbeddedPeer createEmbeddedPeer(JComponent parent, Component embedded, Object... args) {
        return new WindowSwingEmbeddedPeer(parent, embedded, this);
    }

    @Override
    protected CoordinateHandler createCoordinateHandler(JComponent parent, Point2D point, MouseEvent e) {
        logger.fine("Enter WSET.createCoordinateHandler, frame coords: mouseEvent = " + e);

        // Temporarily convert event from frame coords into canvas coords
        Canvas canvas = JmeClientMain.getFrame().getCanvas();
        JFrame frame = (JFrame) e.getSource();
        Point framePoint = e.getPoint();
        Point canvasPoint = SwingUtilities.convertPoint(frame, framePoint, canvas);
        e.translatePoint(canvasPoint.x - framePoint.x, canvasPoint.y - framePoint.y);

        logger.fine("Canvas coords, mouseEvent = " + e);

        InputManager.PickEventReturn ret = InputManager.inputManager().pickMouseEventSwing(e);
        if (ret == null || ret.entity == null || ret.destPickDetails == null) {
            logger.fine("WindowSwing miss");
            e.translatePoint(-(canvasPoint.x - framePoint.x), -(canvasPoint.y - framePoint.y));

            // OWL issue #71: if the previous event was on a swing window and
            // this one is not, generate a fake mouse event outside of the
            // last swing window to ensure that a MouseExit event is generated.
            if (lastWindow != null) {
                logger.fine("Exit swing");

                // request focus in main window
                InputManager.ensureKeyFocusInMainWindow();

                // send a fake mouse event to generate an exit
                final EmbeddedPeer targetEmbeddedPeer = lastWindow.getEmbeddedPeer();
                lastWindow = null;
                return new CoordinateHandler() {
                    public EmbeddedPeer getEmbeddedPeer() {
                        return targetEmbeddedPeer;
                    }

                    public Point2D transform(Point2D src, Point2D dst, 
                                             MouseEvent event) 
                    {
                        if (dst == null) {
                            dst = new Point2D.Float();
                        }
                        
                        dst.setLocation(-1, -1);
                        return dst;
                    }
                };
            } else {
                return null;
            }
        }
        logger.fine("WindowSwing hit");
        logger.fine("Pick hit entity = " + ret.entity);

        EntityComponent comp = ret.entity.getComponent(WindowSwing.WindowSwingViewReference.class);
        assert comp != null;
        final View2D view = ((WindowSwing.WindowSwingViewReference) comp).getView();
        WindowSwing windowSwing = (WindowSwing) view.getWindow();

        // Keep track of the last window we hit
        if (lastWindow == null) {
            logger.fine("Enter swing");
        }
        lastWindow = windowSwing;

        // TODO: someday: I don't think we need to set this anymore for drag events. But it doesn't hurt.
        final Vector3f intersectionPointWorld = ret.destPickDetails.getPosition();
        logger.fine("intersectionPointWorld = " + intersectionPointWorld);

        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            lastPressPointScreen = new Point(e.getX(), e.getY());

            lastPressEventHook = windowSwing.getEventHook();
            if (lastPressEventHook != null) {
                WindowSwing.EventHookInfo hookInfo = new WindowSwing.EventHookInfo(intersectionPointWorld, 
                                                                                   e.getX(), e.getY());
                lastPressEventHook.specifyHookInfoForEvent(e, hookInfo);
            }
        }

        final EmbeddedPeer targetEmbeddedPeer = windowSwing.getEmbeddedPeer();
        CoordinateHandler coordinateHandler = new CoordinateHandler() {

            public EmbeddedPeer getEmbeddedPeer() {
                return targetEmbeddedPeer;
            }

            // Note: event is in frame coordinates
            public Point2D transform(Point2D src, Point2D dst, MouseEvent event) {

                logger.fine("event = " + event);
                logger.fine("src = " + src);

                Point pt;
                if (event.getID() == MouseEvent.MOUSE_DRAGGED) {

                    // We will need the event in canvas coordinates
                    Canvas canvas = JmeClientMain.getFrame().getCanvas();
                    JFrame frame = (JFrame) event.getSource();
                    Point framePoint = event.getPoint();
                    Point canvasPoint = SwingUtilities.convertPoint(frame, framePoint, canvas);
                    int canvasX = event.getX() + canvasPoint.x - framePoint.x;
                    int canvasY = event.getY() + canvasPoint.y - framePoint.y;
                    pt = view.calcIntersectionPixelOfEyeRay(canvasX, canvasY);

                    if (lastPressEventHook != null) {
                        WindowSwing.EventHookInfo hookInfo = 
                            new WindowSwing.EventHookInfo(intersectionPointWorld, canvasX, canvasY);
                        lastPressEventHook.specifyHookInfoForEvent(event, hookInfo);
                    }

                } else {
                    pt = view.calcPositionInPixelCoordinates(intersectionPointWorld, true);
                }
                
                // TODO: temp
                if (pt == null) {
                    logger.severe("pt is null");
                    return src;
                }

                if (dst == null) {
                    dst = new Point2D.Double();
                }

                // TODO: for now
                dst.setLocation(new Point2D.Double((double) pt.x, (double) pt.y));
                logger.fine("dst = " + dst);

                return dst;
            }
        };

        // Restore the event to frame coordinates
        e.translatePoint(-(canvasPoint.x - framePoint.x), -(canvasPoint.y - framePoint.y));

        return coordinateHandler;
    }

    @Override
    // Note: peer should be the owning WindowSwing.embeddedPeer	
    public Popup getPopup(EmbeddedPeer peer, Component contents, int x, int y) {

        int width = (int) contents.getPreferredSize().getWidth();
        int height = (int) contents.getPreferredSize().getHeight();

        if (!(peer instanceof WindowSwingEmbeddedPeer)) {
            throw new RuntimeException("Invalid embedded peer type");
        }
        WindowSwing winOwner = ((WindowSwingEmbeddedPeer) peer).getWindowSwing();
        if (winOwner instanceof PopupProvider) {
            return ((PopupProvider) winOwner).getPopup(contents, x, y);
        }

        WindowSwing winPopup = null;
        winPopup = new WindowSwing(winOwner.getApp(), Window2D.Type.POPUP, winOwner, width, height,
                                   false, winOwner.getPixelScale(), "Popup for " + winOwner.getName());
        winPopup.setComponent(contents);

        final WindowSwing popup = winPopup;

        winPopup.setPixelOffset(x, y);

        return new Popup() {

            @Override
            public void show() {
                App2D.invokeLater(new Runnable() {
                    public void run () {
                        popup.setVisibleApp(true);
                    }
                });
            }

            @Override
            public void hide() {
                App2D.invokeLater(new Runnable() {
                    public void run () {
                        popup.setVisibleApp(false);
                    }
                });
            }
        };
    }

    static class WindowSwingEmbeddedPeer extends EmbeddedPeer {

        WindowSwingEmbeddedToolkit toolkit;
        private WindowSwing windowSwing = null;

        protected WindowSwingEmbeddedPeer(JComponent parent, Component embedded, WindowSwingEmbeddedToolkit toolkit) {
            super(parent, embedded);
            this.toolkit = toolkit;
        }

        void repaint() {
            Component embedded = getEmbeddedComponent();
            repaint(embedded.getX(), embedded.getY(), embedded.getWidth(), embedded.getHeight());
        }

        @Override
        public void repaint(int x, int y, int width, int height) {
            if (windowSwing == null) {
                return;
            }

            //System.err.println("repaint xywh = " + x + ", " + y + ", " + width + ", " + height);

            // Clip the dirty region to the component
            Component embedded = getEmbeddedComponent();
            int compX0 = embedded.getX();
            int compY0 = embedded.getY();
            int compX1 = compX0 + embedded.getWidth();
            int compY1 = compY0 + embedded.getHeight();
            int x0 = Math.max(x, compX0);
            int y0 = Math.max(y, compY0);
            int x1 = Math.min(x + width, compX1);
            int y1 = Math.min(y + height, compY1);
            x = x0;
            y = y0;
            width = x1 - x0;
            height = y1 - y0;

            paintOnWindow(windowSwing, x, y, width, height);
        }

        void setWindowSwing(WindowSwing windowSwing) {
            this.windowSwing = windowSwing;
            synchronized (this) {
                notifyAll();
            }
        }

        WindowSwing getWindowSwing() {
            return windowSwing;
        }

        protected EmbeddedToolkit<?> getEmbeddedToolkit() {
            return toolkit;
        }

        // Note: called on EDT
        @Override
        protected void sizeChanged(Dimension oldSize, Dimension newSize) {
            synchronized (this) {
                while (windowSwing == null) {
                    try {
                        wait();
                    } catch (InterruptedException ex) {
                    }
                }
            }

            windowSwing.setWindowSize(newSize.width, newSize.height);
        }

        // Note: called on EDT
        private void paintOnWindow(final WindowSwing window,
                final int x, final int y, final int width, final int height) {

            final EmbeddedPeer embeddedPeer = this;

            EventQueue.invokeLater(new Runnable() {

                public void run() {
                    DrawingSurface drawingSurface = window.getSurface();
                    if (drawingSurface == null) return;
                    final DrawingSurfaceBufferedImage.DirtyTrackingGraphics gDst =
                            (DrawingSurfaceBufferedImage.DirtyTrackingGraphics) drawingSurface.getGraphics();
                    gDst.setClip(x, y, width, height);
                    gDst.executeAtomic(new Runnable() {

                        public void run() {
                            embeddedPeer.paint(gDst);
                            gDst.addDirtyRectangle(x, y, width, height);
                        }
                    });
                }
            });
        }
    }
}
