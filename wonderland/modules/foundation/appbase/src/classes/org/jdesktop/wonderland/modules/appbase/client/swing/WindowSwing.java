/**
 * Open Wonderland
 *
 * Copyright (c) 2010 - 2011, Open Wonderland Foundation, All Rights Reserved
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import org.jdesktop.wonderland.client.jme.MainFrame;
import org.jdesktop.wonderland.modules.appbase.client.App2D;
import org.jdesktop.wonderland.modules.appbase.client.swing.WindowSwingEmbeddedToolkit.WindowSwingEmbeddedPeer;
import com.sun.embeddedswing.EmbeddedPeer;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import java.util.Iterator;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.jdesktop.mtgame.EntityComponent;
import org.jdesktop.wonderland.client.input.InputManager;
import org.jdesktop.wonderland.client.input.InputManager.WindowSwingViewMarker;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.DrawingSurfaceBufferedImage;
import org.jdesktop.wonderland.modules.appbase.client.Window2D;
import org.jdesktop.wonderland.modules.appbase.client.view.View2D;
import java.awt.event.MouseEvent;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D;
import org.jdesktop.wonderland.client.jme.input.InputManager3D;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.modules.appbase.client.view.Gui2D;
import org.jdesktop.wonderland.modules.appbase.client.ControlArb;
import javax.swing.SwingUtilities;

/**
 * A 2D window in which a Swing panel can be displayed. Use <code>setComponent</code> to specify the Swing panel.
 * Here is an example of how to use <code>WindowSwing</code>. (Note that it is extremely important that the 
 * Swing panel be parented as an descendant of the root frame).
 * <br><br>
 * <code>
 * JPanel testPanel = new TestPanel();
 * <br>
 * JmeClientMain.getFrame().getCanvas3DPanel().add(testPanel);
 * <br>
 * setComponent(testPanel);
 * </code>
 * <br><br>
 * WindowSwing Sizing:
 * <br><br>
 * WindowSwing now supports the two sizing modes that Swing supports. I'm
 * not sure what Swing calls these two modes but I call them "preferred
 * size" mode and "forced size" mode.
 * <br><br>
 * In preferred size mode, the WindowSwing's size is computed by Swing as
 * it lays out the contained component. This layout process is typically
 * influenced by the preferred sizes of the component's subcomponents.
 * <br><br>
 * In forced size mode, the WindowSwing's size is specified by the
 * Wonderland program calling WindowSwing.setSize with a specified
 * non-null size. Swing will do the best it can to lay out the contained
 * subcomponents.
 * <br><br>
 * Basically, you use preferred size mode to make the WindowSwing adapt
 * to the contained component and you use forced size mode to make the
 * contained component adapt to the size of the WindowSwing.
 * <br><br>
 * Initially WindowSwing defaults to preferred size mode. To force a
 * size, use either of these two methods:
 * <br><br>
 * WindowSwing.setSize(int width, int height)
 * <br><br>
 * WindowSwing.setSize(java.awt.Dimension dims), where dims is non-null.
 * <br><br>
 * To return to preferred size mode invoke the following method:
 * <br><br>
 * WindowSwing.setSize(null)
 * <br><br>
 * In addition, all setSize methods require WindowSwing.setComponent to
 * have been already called and will throw an exception if it has not.
 */

// TODO: currently this has JME dependencies. It would be nice to do this in a graphics library independent fashion.
@ExperimentalAPI
public class WindowSwing extends Window2D {


    private static final Logger logger = Logger.getLogger(WindowSwing.class.getName());
    /** The Swing component which is displayed in this window */
    private Component component;
    /** The Swing Embedder object */
    private WindowSwingEmbeddedPeer embeddedPeer;
    /** The size of the window */
    private Dimension size;

    /** An entity component which provides a back pointer from the entity of a WindowSwing to the 
        WindowSwing. */
    static class WindowSwingViewReference extends EntityComponent {
        private View2D view;
        WindowSwingViewReference (View2D view) {
            this.view = view;
        }
        View2D getView() {
            return view;
        }
    }

    /**
     * The structure passed to EventHook.specifyHookInfoForEvent.
     */
    @InternalAPI
    public static class EventHookInfo {
        public Vector3f pointWorld;
        public int eventX, eventY;
        public EventHookInfo (Vector3f pointWorld, int eventX, int eventY) {
            this.pointWorld = pointWorld;
            this.eventX = eventX;
            this.eventY = eventY;
        }
        public String toString () {
            return "pointWorld=" + pointWorld +
                ", eventXY = " + eventX + "," + eventY;
        }
    }

    /**
     * This is used to pass event world coordinates to the Swing mouse event listeners.
     * This is useful for correct dragging of WindowSwings in the world.
     */
    @InternalAPI
    public interface EventHook {
        /** Specify various info for the given AWT mouse event. */
        public void specifyHookInfoForEvent(MouseEvent e, EventHookInfo hookInfo);
    }

    /**
     * Create an instance of WindowSwing with a default name. The first such window created for an app 
     * becomes the primary window. Subsequent windows are secondary windows.
     * @param app The application to which this window belongs.
     * @param width The window width (in pixels).
     * @param height The window height (in pixels).
     * @param decorated Whether the window is decorated with a frame.
     * @param pixelScale The size of the window pixels.
     */
    public WindowSwing(App2D app, int width, int height, boolean decorated, Vector2f pixelScale) {
        this(app, width, height, decorated, pixelScale, null);
    }

    /**
     * Create an instance of WindowSwing with the given name. The first such window created for an app 
     * becomes the primary window. Subsequent windows are secondary windows.
     * @param app The application to which this window belongs.
     * @param width The window width (in pixels).
     * @param height The window height (in pixels).
     * @param decorated Whether the window is top-level (e.g. is decorated) with a frame.
     * @param pixelScale The size of the window pixels.
     * @param name The name of the window.
     */
    public WindowSwing(App2D app, int width, int height, boolean decorated, Vector2f pixelScale, 
                       String name) {
        super(app, width, height, decorated, pixelScale, name, new DrawingSurfaceBufferedImage()); 
        initializeViews();
    }

    /**
     * Create an instance of WindowSwing of the given type with a default name. 
     * @param app The application to which this window belongs.
     * @param type The type of the window. If this is non-primary, the parent is set to the primary
     * window of the app (if there is one).
     * @param width The window width (in pixels).
     * @param height The window height (in pixels).
     * @param decorated Whether the window is decorated with a frame.
     * @param pixelScale The size of the window pixels.
     */
    public WindowSwing(App2D app, Type type, int width, int height, boolean decorated, 
                          Vector2f pixelScale) {
        this(app, type, width, height, decorated, pixelScale, null);
    }

    /**
     * Create an instance of WindowSwing of the given type with the given name. 
     * @param app The application to which this window belongs.
     * @param type The type of the window. If this is non-primary, the parent is set to the primary
     * window of the app (if there is one).
     * @param width The window width (in pixels).
     * @param height The window height (in pixels).
     * @param decorated Whether the window is top-level (e.g. is decorated) with a frame.
     * @param pixelScale The size of the window pixels.
     * @param name The name of the window.
     */
    public WindowSwing(App2D app, Type type, int width, int height, boolean decorated, Vector2f pixelScale, 
                       String name) {
        this(app, type, app.getPrimaryWindow(), width, height, decorated, pixelScale, name);
    }

    /**
     * Create an instance of WindowSwing of the given type with the given parent with a default name. 
     * @param app The application to which this window belongs.
     * @param type The type of the window. 
     * @param parent The parent of the window. (Ignored for primary windows).
     * @param width The window width (in pixels).
     * @param height The window height (in pixels).
     * @param decorated Whether the window is decorated with a frame.
     * @param pixelScale The size of the window pixels.
     */
    public WindowSwing(App2D app, Type type, Window2D parent, int width, int height, boolean decorated, 
                       Vector2f pixelScale) {
        this(app, type, parent, width, height, decorated, pixelScale, null);
    }

    /**
     * Create an instance of WindowSwing of the given type with the given parent with the given name. 
     * @param app The application to which this window belongs.
     * @param type The type of the window. If this is non-primary, the parent is set to the primary
     * window of the app (if there is one).
     * @param parent The parent of the window. (Ignored for primary windows).
     * @param width The window width (in pixels).
     * @param height The window height (in pixels).
     * @param decorated Whether the window is top-level (e.g. is decorated) with a frame.
     * @param pixelScale The size of the window pixels.
     * @param name The name of the window.
     */
    public WindowSwing(App2D app, Type type, Window2D parent, int width, int height, boolean decorated, 
                    Vector2f pixelScale, String name) {
        super(app, type, parent, width, height, decorated, pixelScale, name,
              new DrawingSurfaceBufferedImage()); 
        initializeViews();
    }

    @Override
    public void cleanup () {
        cleanupViews();
        super.cleanup();
        if (embeddedPeer != null) {
            final EmbeddedPeer e = embeddedPeer;
            embeddedPeer = null;

            // OWL issue #74 - be sure to dispose on the AWT event thread
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    e.dispose();
                }
            });
        }
    }

    /** Initialize all existing views. */
    private void initializeViews () {
        Iterator<View2D> it = getViews();
        while (it.hasNext()) {
            View2D view = it.next();
            viewInit(view);
        }
    }

    /** Clean up all views. */
    private void cleanupViews () {
        Iterator<View2D> it = getViews();
        while (it.hasNext()) {
            View2D view = it.next();
            viewCleanup(view);
        }
    }
    
    /** 
     * Specify the Swing component displayed in this window. The component is validated (that is it
     * is layed out).
     *
     * Note: After you call <code>setComponent</code> the window will be in "preferred size" mode, 
     * that is, it the window will be sized according to the Swing component's preferred sizes and 
     * the component's layout manager. If you call <code>WindowSwing.setSize(int width, int height)</code> or 
     * <code>WindowSwing.setSize(Dimension dims)</code> with a non-null <code>dims</code> the window will be 
     * in "forced size" mode. This means that the window will always be the size you specify and this
     * will constrain the sizes of the contained component. To switch back into preferred size mode
     * call <code>WindowSwing.setSize(null)</code>.
     *
     * @param component The component to be displayed.
     */
    public void setComponent(Component component) {
        if (this.component == component) {
            return;
        }
        this.component = component;
        if (embeddedPeer != null) {
            embeddedPeer.dispose();
            embeddedPeer = null;
        }
        if (component != null) {
            checkContainer();

        }

	// TODO: Uncomment this to demonstrate the embedded component enter/exit bug
	//component.addMouseListener(new MyAwtEnterListener());

        // OWL issue #47: be sure to call validate from the AWT event thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (embeddedPeer != null) {
                    embeddedPeer.validate();
                }
            }
        });

        embeddedPeer.repaint();
    }


    /* TODO: I'm leaving this hear to illustrate a bug
    private class MyAwtEnterListener extends MouseAdapter {

        public void mouseEntered(MouseEvent e) {
            if (e.getID() == MouseEvent.MOUSE_ENTERED) {
                System.err.println("********* MOUSE Entered Window Swing embedded component");
            } else {
                System.err.println("********* MOUSE Exited Window Swing embedded component");
            }
        }
    }
    */

    /** Returned the Swing component displayed in this window */
    public final Component getComponent() {
        return component;
    }

    /** First time initialization */
    private void checkContainer() {
        if (component == null) {
            if (embeddedPeer != null) {
                embeddedPeer.dispose();
                embeddedPeer = null;
            }
            return;
        }

        MainFrame frame = JmeClientMain.getFrame();
        JPanel embeddedParent = frame.getCanvas3DPanel();
        if (embeddedParent == null) {
            logger.warning("Embedded parent is null");
            return;
        }

        if (embeddedParent != null) {
            if (embeddedPeer != null && embeddedPeer.getParentComponent() != embeddedParent) {
                embeddedPeer.dispose();
                embeddedPeer = null;
            }
            if (embeddedPeer == null) {
                WindowSwingEmbeddedToolkit embeddedToolkit =
                        WindowSwingEmbeddedToolkit.getWindowSwingEmbeddedToolkit();
                embeddedPeer = embeddedToolkit.embed(embeddedParent, component);
                embeddedPeer.setWindowSwing(this);
            }
        }
    }

    /**
     * Specify the size of the window only (not the embedded peer).
     */
    void setWindowSize (int width, int height) {
        super.setSize(width, height);
    }

    /**
     * Specify the size of this WindowSwing. This switches the window from "preferred size" mode
     * to "forced size" mode?
     */
    @Override
    public void setSize (int width, int height) {
        setSize(new Dimension(width, height));
    }

    /**
     * Specify the size of this WindowSwing. If dims is non-null, the window is switched
     * into "forced size" mode--the window will be always be the size you specify. If dims is null,
     * the window is switched into "preferred size" mode--the window will size will be determined
     * by the size and layout of the embedded Swing component.
     */
    public void setSize (final Dimension dims) {
        if (embeddedPeer == null) {
            throw new RuntimeException("You must first set a component for this WindowSwing.");
        }

        // OWL issue #74 - make sure to call validate and setSize on the AWT
        // event thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (embeddedPeer != null) {
                    embeddedPeer.setSize(dims);
                    embeddedPeer.validate();
                }
            }
        });

        embeddedPeer.repaint();
    }

    /**
     * Re-lay out the contents of this window. This should be called whenever you make changes which
     * affect the layout of the contained component.
     */
    public void validate () {
        if (embeddedPeer != null) {
            // OWL issue #74 - make sure to call validate on the AWT event
            // thread
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (embeddedPeer != null) {
                        embeddedPeer.validate();
                    }
                }
            });

            embeddedPeer.repaint();
        }
    }

    /**
     * Repaint out the contents of this window.
     */
    @Override
    public void repaint () {
        if (embeddedPeer == null) {
            throw new RuntimeException("You must first set a component for this WindowSwing.");
        }
        embeddedPeer.repaint();
    }
    public final EmbeddedPeer getEmbeddedPeer () {
	return embeddedPeer;
    }

    protected void paint(Graphics2D g) {}

    private static class MyWindowSwingEventConsumer extends InputManager.WindowSwingEventConsumer {

        private App2D app;

        private MyWindowSwingEventConsumer (App2D app) {
            this.app = app;
        }

        public EventAction consumesEvent (MouseEvent3D me3d) {
            if (app == null) return EventAction.DISCARD;

            MouseEvent awtEvent = (MouseEvent) me3d.getAwtEvent();
            logger.fine("WS.consumesEvent: " + awtEvent);
            if (Gui2D.isChangeControlEvent(awtEvent)) {
                logger.fine("Is Change Control Event");

                // Perform the control toggle immediately
                ControlArb controlArb = app.getControlArb();
                if (controlArb != null) {
                    if (controlArb.hasControl()) {
                        controlArb.releaseControl();
                    } else {
                        controlArb.takeControl();
                    }
                }
                return EventAction.DISCARD;
            }
            logger.fine("Isn't change control event " + awtEvent);

            // If app doesn't have control, ignore the event

            if (app.getControlArb() == null || !app.getControlArb().hasControl()) {
                logger.fine("Doesn't have control");
                return EventAction.DISCARD;
            }
            logger.fine("Has control");

            // If app has control and focus, send the event to Swing
            if (InputManager3D.entityHasFocus(me3d, app.getFocusEntity())) {
                logger.fine("App entity has focus");
                return EventAction.CONSUME_2D;
            }
            logger.fine("App entity doesn't have focus");

            return EventAction.DISCARD;
        }
    }

    /** {@inheritDoc} */
    @Override
    public void addView(View2D view) {
        super.addView(view);
        viewInit(view);
    }

    /** {@inheritDoc} */
    @Override
    public void removeView(View2D view) {
        viewCleanup(view);
        super.removeView(view);
    }

    /** Attach the things we need to the given given view. */
    protected void viewInit (View2D view) {
        view.addEntityComponent(InputManager.WindowSwingViewMarker.class, new WindowSwingViewMarker());
        view.addEntityComponent(WindowSwingViewReference.class, new WindowSwingViewReference(view));
        view.addEntityComponent(InputManager.WindowSwingEventConsumer.class, 
                                new MyWindowSwingEventConsumer(getApp()));
    }

    /** Attach the things we use from the given given view. */
    protected void viewCleanup (View2D view) {
        view.removeEntityComponent(InputManager.WindowSwingViewMarker.class);
        view.removeEntityComponent(WindowSwingViewReference.class);
        view.removeEntityComponent(InputManager.WindowSwingEventConsumer.class); 
    }

    /** Return the world coordinate event hook for this WindowSwing. */
    @InternalAPI
    public EventHook getEventHook() {
        return null;
    }
}
