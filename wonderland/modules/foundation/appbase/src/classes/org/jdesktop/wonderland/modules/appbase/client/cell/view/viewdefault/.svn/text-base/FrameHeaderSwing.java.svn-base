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
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., All Rights Reserved
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
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.Point;
import java.text.MessageFormat;
import java.util.logging.Logger;
import java.util.ResourceBundle;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.jdesktop.wonderland.client.jme.input.MouseDraggedEvent3D;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.App2D;
import org.jdesktop.wonderland.modules.appbase.client.cell.App2DCell;
import org.jdesktop.wonderland.modules.appbase.client.ControlArb;
import org.jdesktop.wonderland.modules.appbase.client.ControlArbSingle;
import org.jdesktop.wonderland.modules.appbase.client.swing.WindowSwing;
import org.jdesktop.wonderland.modules.appbase.client.view.View2D;
import org.jdesktop.wonderland.modules.appbase.client.view.View2DDisplayer;
import org.jdesktop.wonderland.modules.appbase.client.view.View2DEntity;
import org.jdesktop.wonderland.modules.appbase.client.Window2D;

/**
 * The frame header (top side) for Frame2DCellSwing. Uses a WindowSwing.
 *
 * @author deronj
 */
@ExperimentalAPI
public class FrameHeaderSwing
    extends FrameComponent
    implements HeaderPanel.Container, MouseListener, MouseMotionListener
{
    private static final Logger LOGGER = Logger.getLogger(
            FrameHeaderSwing.class.getName());
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/appbase/client/Bundle");

    // TODO: New UI: add zones: move planar, move z, rotate
    private FrameHeaderSwingWindow headerWindow;

    /** The AWT background color of the header window. */
    private Color bkgdColor;

    /** The AWT background color of the header window. */
    private Color fgrdColor;

    /** The panel displayed in the frame. */
    private HeaderPanel headerPanel;

    /** The app of the window of the view which this frame decorates. */
    private App2D app;

    /** The view of this header. */
    private View2DCell view;

    /** Whether the frame is visible. */
    private boolean visible;

    /* The view of this header in the cell displayer. */
    private View2DEntity frameView;

    /** True if a drag is active. */
    private boolean dragging;

    private int x, y, width, height;

    /** The intersection point on the entity over which the button was pressed, in world coordinates. */
    protected Vector3f dragStartWorld;
    /** The intersection point in parent local coordinates. */
    protected Vector3f dragStartLocal;
    /** The screen coordinates of the button press event. */
    protected Point dragStartScreen;
    /** The amount that the cursor has been dragged in local coordinates. */
    protected Vector3f dragVectorLocal;

    private static HeaderPanelAllocator headerPanelAllocator;

    /** 
     * This forces the FrameHeaderSwing class to be loaded and initialized well before
     * the first instance of FrameHeaderSwing is instantiated. This is essential for 
     * avoiding deadlocks with the EDT.
     */
    static void staticInitialize () {
        headerPanelAllocator = new HeaderPanelAllocator();
    }

    /**
     * Create a new instance of FrameHeaderSwing.
     *
     * @param view The view the frame encloses.
     * @param closeListeners The listeners to be notified when the header's close button is pressed.
     */
    public FrameHeaderSwing(View2DCell view) {
        super("FrameHeaderSwing for " + view, view, null);

        this.view = view;
        Window2D viewWindow = view.getWindow();
        app = viewWindow.getApp();
        headerWindow = new FrameHeaderSwingWindow(app, viewWindow, 1, 1, view.getPixelScale(), 
                                                  "Header Window for " + view.getName(), view);
        headerWindow.setCoplanar(true);

        // Unless we do this the interior of the frame will deliver events 
        // to the control arb of the application and they will look like they
        // are coming from the interior of the main window's view. We don't want this.
        View2DDisplayer displayer = view.getDisplayer();
        frameView = (View2DEntity) headerWindow.getView(displayer);
        frameView.disableGUI();

        headerPanel = headerPanelAllocator.allocate();

        JmeClientMain.getFrame().getCanvas3DPanel().add(headerPanel);
        headerPanel.setContainer(this);
        headerWindow.setComponent(headerPanel);

        // Turn preferred size into forced size. This will (hopefully) allow us
        // to access the headerPanel height in updateLayout off the EDT.
        headerPanel.setSize(headerPanel.getPreferredSize()); 

        headerPanel.addMouseListener(this);
        headerPanel.addMouseMotionListener(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {

        headerPanel.removeMouseListener(this);
        headerPanel.removeMouseMotionListener(this);

        // issue #74: make sure to remove panels on the AWT event thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JmeClientMain.getFrame().getCanvas3DPanel().remove(headerPanel);
                headerPanelAllocator.deallocate(headerPanel);
            }
        });

        setVisible(false);

        if (headerWindow != null) {
            headerWindow.cleanup();
            headerWindow = null;
        }


        frameView = null;

        super.cleanup();

        visible = false;
        view = null;
        app = null;
    }

    /**
     * Specify the visibility of this header.
     */
    public void setVisible (boolean visible) {
        if (this.visible == visible) return;
        this.visible = visible;

        Window2D viewWindow = view.getWindow();
        if (viewWindow != null) {
            boolean isPrimary = viewWindow.getType() == Window2D.Type.PRIMARY ||
                                viewWindow.getType() == Window2D.Type.UNKNOWN;
            if (!isPrimary) {
                headerPanel.showHUDButton(false);
            }
        }

        headerWindow.setVisibleApp(visible);
        headerWindow.setVisibleUser(view.getDisplayer(), visible);
    }

    /**
     * Update the size and position of the header and its subcomponents.
     *
     * @throw InstantiationException if couldn't allocate resources for the visual representation.
     */
    @Override
    public void update() throws InstantiationException {
        updateLayout();
        headerWindow.setPixelOffset(x, y);
        headerWindow.setSize(width, height);
        headerWindow.setDesiredZOrder(headerWindow.getZOrder());
    }

    public void update(float newWidth3D, float newHeight3D, Dimension newSize) throws InstantiationException {
        updateLayout(newWidth3D, newHeight3D);
        frameView.updateViewSizeOnly(x, y, width, height, newWidth3D, newHeight3D, newSize);
    }

    protected void updateLayout() {
        updateLayout(view.getDisplayerLocalWidth(), view.getDisplayerLocalHeight());         
    }

    /** 
     * {@inheritDoc} 
     * <br><br>
     * Note: sometimes this gets called on the EDT and sometimes it gets called off the EDT.
     */
    protected void updateLayout(final float newWidth3D, final float newHeight3D) {
        updateTheLayout(newWidth3D, newHeight3D);
    }

    private void updateTheLayout (float newWidth3D, float newHeight3D) {
        Vector2f pixelScale = view.getPixelScale();

        // Calculate size. This is essentially the same as for FrameSide TOP, but 
        // converted to pixels.
        float innerWidth = newWidth3D;
        float innerHeight = newHeight3D;
        float sideThickness = Frame2DCell.SIDE_THICKNESS;
        width = (int) ((innerWidth + 2f * sideThickness) / pixelScale.x);
        height = headerPanel.getHeight();

        // Calculate the pixel offset of the upper-left of the header relative to the 
        // upper-left of the view. Note that we need to calculate x so that the header
        // left side aligns with the left side of the frame.
        x = (int) (-sideThickness / pixelScale.x);
        y = -height;
    }

    /**
     * Set the title displayed in the header.
     *
     * @param text The new title.
     */
    public void setTitle(String title) {
        headerPanel.setTitle(title);
    }

    /**
     * Set the controller displayed in the header.
     *
     * @param text The new controller.
     */
    public void setController(String controller) {
        headerPanel.setController(controller);
    }

    /**
     * {@inheritDoc}
     */
    public void setColor(ColorRGBA color) {
        bkgdColor = new Color(color.r, color.g, color.b, color.a);
        if (headerPanel != null) {
            headerPanel.setBackground(bkgdColor);
        }
    }

    /**
     * {@inheritDoc}
     */
    public ColorRGBA getColor() {
        return new ColorRGBA(bkgdColor.getRed()/255.0f, 
                             bkgdColor.getGreen()/255.0f, 
                             bkgdColor.getBlue()/255.0f, 
                             bkgdColor.getAlpha()/255.0f);
    }

    /**
     * {@inheritDoc}
     */
    public void setForegroundColor(ColorRGBA color) {
        fgrdColor = new Color(color.r, color.g, color.b, color.a);
        if (headerPanel != null) {
            headerPanel.setForeground(fgrdColor);
        }
    }

    /**
     * {@inheritDoc}
     */
    public ColorRGBA getForegroundColor() {
        return new ColorRGBA(fgrdColor.getRed()/255.0f,
                             fgrdColor.getGreen()/255.0f,
                             fgrdColor.getBlue()/255.0f,
                             fgrdColor.getAlpha()/255.0f);
    }

    public void	mouseClicked(MouseEvent e) {
        if (view == null) return;

        if (e.getID() == MouseEvent.MOUSE_CLICKED &&
            e.getButton() == MouseEvent.BUTTON1 &&
            e.getModifiersEx() == 0) {
            App2D.invokeLater(new Runnable() {
                public void run () {
                    if (view != null) {
                        view.getWindow().restackToTop();
                    }
                }
            });
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void	mouseExited(MouseEvent e) {
    }

    public void	mousePressed(MouseEvent e) {
        if (view == null) return;

        // Is this a Window menu event? Display menu even when we don't have control.
        if (e.getID() == MouseEvent.MOUSE_PRESSED &&
            e.getButton() == MouseEvent.BUTTON3 &&
            e.getModifiersEx() == MouseEvent.BUTTON3_DOWN_MASK) {
            view.getWindow().displayWindowMenu(view.getEntity(), e);
            return;
        }

        // TODO: the following drag code only works for secondary windows. Eventually 
        // upgrade it to work with primary windows also.
        if (view.getType() != View2D.Type.SECONDARY) return;

        //System.err.println("******* Press event " + e);

        if (!dragging && 
            e.getButton() == MouseEvent.BUTTON1 &&
            e.getModifiersEx() == MouseEvent.BUTTON1_DOWN_MASK) {

            WindowSwing.EventHookInfo hookInfo = headerWindow.getHookInfoForEvent(e);
            if (hookInfo == null) {
                LOGGER.warning("Cannot drag window because can't get hook info for event " + e);
                return;
            }

            dragging = true;

            // Remember: the move occurs in parent coords
            View2DEntity parentView = (View2DEntity) view.getParent();
            if (parentView == null) {            
                // Note: we don't yet support dragging of primaries
                LOGGER.warning("Drag secondary operation can't get parent of secondary");
                return;
            }

            dragStartScreen = new Point(hookInfo.eventX, hookInfo.eventY);
            dragStartWorld = hookInfo.pointWorld;
            dragStartLocal = parentView.getNode().worldToLocal(dragStartWorld, new Vector3f());

            App2D.invokeLater(new Runnable() {
                public void run () {
                    if (view != null) {
                        view.userMovePlanarStart();
                    }
                }
            });
        }
    }

    public void mouseDragged(MouseEvent e) {
        if (view == null) return;

        // TODO: the following drag code only works for secondary windows. Eventually 
        // upgrade it to work with primary windows also.
        if (view.getType() != View2D.Type.SECONDARY) return;

        WindowSwing.EventHookInfo hookInfo = headerWindow.getHookInfoForEvent(e);
        if (hookInfo == null) {
            LOGGER.warning("Cannot drag window because can't get hook info for event " + e);
            return;
        }

        //System.err.println("******* Drag event " + e);
        if (dragging) {

            /*
            Vector2f pixelScale = view.getPixelScale();
            Vector2f dragCurrentLocal = new Vector2f();
            dragCurrentLocal.x = e.getX() * pixelScale.x;
            dragCurrentLocal.y = -e.getY() * pixelScale.y;

            Vector2f dragVectorLocal = dragCurrentLocal.subtractLocal(dragStartLocal);
            */

            Vector3f dragVectorWorld = 
                MouseDraggedEvent3D.getDragVectorWorld(hookInfo.eventX, hookInfo.eventY,
                                                       dragStartWorld, dragStartScreen,
                                                       new Vector3f());

            // Convert from world to parent coordinates.
            Node viewNode = ((View2DEntity)view.getParent()).getNode();
            Vector3f curWorld = dragStartWorld.add(dragVectorWorld, new Vector3f());
            Vector3f curLocal = viewNode.worldToLocal(curWorld, new Vector3f());
            dragVectorLocal = curLocal.subtract(dragStartLocal);

            //System.err.println("dragVectorLocal = " + dragVectorLocal);
            App2D.invokeLater(new Runnable() {
                public void run () {
                    if (view != null) {
                        view.userMovePlanarUpdate(new Vector2f(dragVectorLocal.x, dragVectorLocal.y));
                    }
                }
            });
        }
    }

    public void mouseReleased(MouseEvent e) {
        if (view == null) return;

        // TODO: the following drag code only works for secondary windows. Eventually 
        // upgrade it to work with primary windows also.
        if (view.getType() != View2D.Type.SECONDARY) return;

        if (e.getButton() == MouseEvent.BUTTON1) {
            if (dragging) {
                dragging = false;
                App2D.invokeLater(new Runnable() {
                    public void run () {
                        if (view != null) {
                            view.userMovePlanarFinish();
                        }
                    }
                });
            }
        }
    }

    public void mouseMoved(MouseEvent e) {
    }

    // For ortho subwindow debug: set to true to debug ortho subwindows with close button
    private static final boolean orthoSubwindowDebug = false;

    // This is called on the EDT.
    public void close () {
        Window2D viewWindow = view.getWindow();
        if (orthoSubwindowDebug) {
            toggleOrthoSafe(viewWindow);
        } else {
            boolean isPrimary = viewWindow.getType() == Window2D.Type.PRIMARY ||
                                viewWindow.getType() == Window2D.Type.UNKNOWN;
            
            // Clicking the close button on a primary window deletes the cell
            if (isPrimary) {

                // Display a confirmation dialog to make sure we really want to delete the cell.
                App2D app = viewWindow.getApp();
                String message = BUNDLE.getString("Quit_Message");
                message = MessageFormat.format(message, app.getName());
                int result = JOptionPane.showConfirmDialog(
                        JmeClientMain.getFrame().getFrame(),
                        message, BUNDLE.getString("Confirm_Quit"),
                        JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.NO_OPTION) {
                    return;
                }

                closeWindowAndCellSafe(viewWindow, view.getCell());

            } else {
                // Otherwise just close the window. 
                closeWindowSafe(viewWindow);
            }
        }
    }

    private void toggleOrthoSafe (final Window2D window) {
        App2D.invokeLater(new Runnable() {
            public void run () {
                window.toggleOrtho();
            }
        });
    }

    private void closeWindowSafe (final Window2D window) {
        App2D.invokeLater(new Runnable() {
            public void run () {
                window.closeUser();
            }
        });
    }

    private void closeWindowAndCellSafe (final Window2D window, final App2DCell cell) {
        App2D.invokeLater(new Runnable() {
            public void run () {
                window.closeUser();
                cell.destroy();
            }
        });
    }

    /** 
     * {@inheritDoc} 
     * THREAD USAGE NOTE: Called on the EDT.
     */
    public void toggleHUD() {
        app.setShowInHUD(!app.isShownInHUD());
    }

    /** {@inheritDoc} */
    @Override
    public void updateControl(ControlArb controlArb) {
        super.updateControl(controlArb);

        if (controlArb instanceof ControlArbSingle) {
            ControlArbSingle ca = (ControlArbSingle) controlArb;
            setController(ca.getController());
        } else {
            // TODO: someday: if it's Multi it would be nice to display the number of users controlling,
            // or actually a list of users controlling.
        }
    }

    /**
     * Returns the width of this component in local coordinates. 
     */
    float getWidth () {
        Vector2f pixelScale = view.getPixelScale();
        return width * pixelScale.x;
    }

    /**
     * Returns the height of this component in local coordinates. 
     */
    float getHeight () {
        Vector2f pixelScale = view.getPixelScale();
        return height * pixelScale.y;
    }
}

