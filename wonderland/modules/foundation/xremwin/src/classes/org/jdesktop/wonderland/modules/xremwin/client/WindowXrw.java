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
package org.jdesktop.wonderland.modules.xremwin.client;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.math.BigInteger;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.App2D;
import org.jdesktop.wonderland.modules.appbase.client.Window2D;
import org.jdesktop.wonderland.modules.appbase.client.WindowConventional;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.modules.appbase.client.view.View2D;
import org.jdesktop.wonderland.modules.appbase.client.ControlArb;
import org.jdesktop.wonderland.common.InternalAPI;
import javax.swing.SwingUtilities;

/**
 * The Xremwin window class. 
 *
 * @author deronj
 */
@ExperimentalAPI
public class WindowXrw extends WindowConventional {

    /** The WID value for an invalid WID. */
    public static final int INVALID_WID = 0;

    /** The X11 window ID */
    private int wid;
    /** 
     * Non-zero indicates that the window is a transient window and the value is the window it is 
     * transient for. TODO: write code that uses this.
     */
    private WindowXrw winTransientFor;
    /** The screen position of this window */
    private Point scrPos = new Point(0, 0);

    /**
     * Create a new WindowXrw instance and its "World" view.
     *
     * @param app The application to which this window belongs.
     * @param x The X11 x coordinate of the top-left corner window.
     * @param y The X11 y coordinate of the top-left corner window.
     * @param borderWidth The X11 border width.
     * @param decorated Whether the window is decorated with a frame.
     * @param pixelScale The size of the window pixels.
     * @param wid The X11 window ID.
     * @throws Instantiation if the window cannot be created.
     */
    WindowXrw(App2D app, int x, int y, int width, int height, int borderWidth,
            boolean decorated, Vector2f pixelScale, int wid)
            throws InstantiationException {

        super(app, width, height, decorated, borderWidth, pixelScale,
                "WindowXrw " + wid + " for app " + app.getName());

        this.wid = wid;

        // Determine whether this window is transient for another
        // TODO: not yet implemented
        int transientForWid = ((AppXrw)app).getTransientForWid(wid);
        if (transientForWid != 0) {
            winTransientFor = ((AppXrw)app).getWindowForWid(transientForWid);
        }

        setScreenPosition(x, y);

        // TODO: disable resize corner until bugs are fixed
        setUserResizable(true);
    }

    /**
     * Clean up resources.
     */
    @Override
    public void cleanup() {
        super.cleanup();
        winTransientFor = null;
    }

    /**
     * Returns the window's wid.
     */
    public int getWid() {
        return wid;
    }

    /**
     * Specify the absolute screen position of this window for this client only.
     */
    public void setScreenPosition (int x, int y) {
        scrPos = new Point(x, y);
        updateOffset();
    }

    /**
     * Returns the X screen position of this window.
     */
    public int getScreenPositionX () {
        return scrPos.x;
    }

    /**
     * Returns the Y screen position of this window.
     */
    public int getScreenPositionY () {
        return scrPos.y;
    }

    /** {@inheritDoc} */
    @Override
    public void setType (Type type) throws IllegalStateException {
        super.setType(type);
        updateOffset();
    }

    /** {@inheritDoc} */
    @Override
    public void setType (Type type, boolean okayToDemotePrimary) throws IllegalStateException {
        super.setType(type, okayToDemotePrimary);
        updateOffset();
    }

    /** {@inheritDoc} */
    @Override
    public void setParent(Window2D parent) {
        super.setParent(parent);
        updateOffset();
    }

    /** 
     * Convert the screen position of this window into a parent-relative offset.
     */
    private void updateOffset() {
        
        // Make sure that this window is fully initialized before updating the offset
        if (scrPos == null) return;

        WindowXrw parent = (WindowXrw) getParent();
        if (getType() == Type.PRIMARY || parent == null) {
            setPixelOffset(0, 0);
        } else {
            if (parent.scrPos != null) {
                setPixelOffset(scrPos.x - parent.scrPos.x, scrPos.y - parent.scrPos.y);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void closeUser () {
        closeUser(false);
    }

    /**
     * {@inheritDoc}
     */
    @InternalAPI
    @Override
    public void closeUser (boolean forceClose) {
        if (!forceClose) {
            if (app == null || app.getControlArb() == null) return;

            // User must have control in order to close the window
            if (!app.getControlArb().hasControl()) {
                // TODO: bring up swing option window: "You cannot close this window
                // because you do not have control"
                // Danger: can't do this in SAS!
                AppXrw.logger.warning("You cannot close this window because you do not " +
                               "have control");
                return;
            }
        }

        // Notify the Xremwin server and other clients
        if (app == null || ((AppXrw) app).getClient() == null) return;
        ((AppXrw) app).getClient().windowCloseUser(this);

        // now clean up the window.
        super.closeUser(forceClose);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void restackToTop () {
        super.restackToTop();

        // Notify the Xremwin server and other clients
        ((AppXrw) app).getClient().windowToFront(this);
    }

    /**
     * Change the visibility of the window 
     *
     * @param visible Whether the window should be visible.
     * @param winTransientFor If non-null, the window whose visibility is being changed
     * is a transient window for winTransientFor.
     */
    public void setVisibleApp(boolean visible, boolean isPopup) {
        synchronized (this) {
            if (isVisibleApp() == visible) {
                return;
            }

            if (isPopup || !isDecorated()) {
                setType(Type.POPUP);

                // We assign the popup parent when the popup is first made visible
                // TODO: This is a kludge. Eventually replace with winTransientFor
                if (getParent() == null) {
                    setParent(determineParentForPopup());
            }
            } else {
                // If type hasn't been determined at this point, make window a secondary
                if (visible && getType() == Type.UNKNOWN) {
                    setType(Type.SECONDARY);
                }
            }

            setVisibleAppPart1(visible);

            performFirstVisibleInitialization();

            ((AppXrw)app).trackWindowVisibility(this);

            // If this is still a secondary assign a parent if necessary
            if (getType() == Type.SECONDARY && getParent() == null) {
                setParent(app.getPrimaryWindow());
            }

            setVisibleAppPart2();
        }

        updateFrames();
    }

    // TODO: This is a kludge. Eventually replace with winTransientFor
    private WindowXrw determineParentForPopup() {
        WindowXrw parent = null;
        AppXrw appx = (AppXrw) app;

        if (appx.isMaster()) {
            WindowXrw currentPointerWin = appx.getCurrentPointerWindow();
            if (currentPointerWin != this) {
                parent = currentPointerWin;
            }
            if (parent == null) {
                parent = (WindowXrw) app.getPrimaryWindow();
            }
            ((AppXrwMaster) appx).setPopupParentForSlaves(this, parent);
        } else {
            // On the slave the current pointer window may be
            // completely different than the master. Therefore
            // we cannot rely on it. Instead, just stick the popup
            // somewhere reasonable for now. First try to attach
            // it to the primary window and then just leave the parent
            // null (this will attach it directly to the cell). It doesn't
            // matter exactly where we put it for now because an
            // SetPopupParent request will be forthcoming from the
            // master, which will put the popup in the correct place.
            parent = (WindowXrw) app.getPrimaryWindow();
        }

        return parent;
    }

    /** 
     * Returns the window for which this window is a transient.
     */
    public WindowXrw getTransientFor() {
        return winTransientFor;
    }

    /**
     * Specify the user who is now controlling the application to which this window belongs.
     *
     * @param userName The controlling user.
     */
    public void setControllingUser(final String userName) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run () {
                ((ControlArbXrw) app.getControlArb()).setController(userName);
            } 
        });
    }

    /**
     * Returns the name of the controlling user.
     */
    public String getControllingUser() {
        ControlArb controlArb = app.getControlArb();
        if (controlArb instanceof ControlArbNull) return null;
        return ((ControlArbXrw) controlArb).getController();
    }

    /**
     * Used only by App Base Master during a new slave connection.
     * Sends the pixels of the entire window to the given slave.
     *
     * @param slaveID The slave to which to send the window pixels.
     */
    public void syncSlavePixels(BigInteger slaveID) {
        byte[] tmpPixelBytes = new byte[getWidth() * getHeight() * 4];

        // Get pixels in a byte array
        getPixelBytes(tmpPixelBytes, 0, 0, getWidth(), getHeight());

        ClientXrw client = ((AppXrw) app).getClient();
        ((ClientXrwMaster) client).writeSyncSlavePixels(slaveID, tmpPixelBytes);
    }

    /** {@inheritDoc} */
    @Override
    public void deliverEvent(MouseEvent event) {

        // TODO: temporary: until winTransientFor: used to determine parents of popups
        // Always record this regardless of the specific mouse event type
        if (!isDecorated()) {
            ((AppXrw) app).setCurrentPointerWindow(this);
        }

        super.deliverEvent(event);
    }

    /**
     * Notifies other clients that the user has changed the user cell transform in a view
     * of this window.
     * @param transform The new transform.
     * @param changingView The view the user manipulated to change the transform.
     */
    @Override
    public void notifyUserTransformCell (CellTransform transform, View2D changingView) {
        synchronized (this) {
            // TODO: someday: this is currently only used for planar moves of secondary windows.
            // Since we cannot currently rotate secondaries, we can just extract the translation and
            // use the client method windowSetUserDisplacement.
            Vector3f userTranslation = transform.getTranslation(null);
            ClientXrw client = ((AppXrw) app).getClient();
            client.windowSetUserDisplacement(this, userTranslation);
        }

        super.notifyUserTransformCell(transform, changingView);
    }

    /** {@inheritDoc} */
    @Override
    public String toString () {
        return getName();
    }

    public synchronized void userSetSize(int width, int height) {
        ClientXrw client = ((AppXrw) app).getClient();
        client.windowSetSize(this, width, height);
        setSize(width, height);
    }
}
