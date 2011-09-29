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
package org.jdesktop.wonderland.modules.xremwin.client;

import org.jdesktop.wonderland.client.utils.SmallIntegerAllocator;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.MonitoredProcess;
import org.jdesktop.wonderland.modules.appbase.client.ProcessReporter;
import org.jdesktop.wonderland.modules.appbase.client.ProcessReporterFactory;
import org.jdesktop.wonderland.modules.xremwin.client.wm.X11WindowManager;
import org.jdesktop.wonderland.modules.xremwin.client.wm.X11IntegrationModule;

/**
 * The Window System for an X11 app. This consists of two processes:
 * 
 * 1. The Xremwin server (an external process).
 * 
 * 2. The Wonderland X11 window manager (a Java thread).
 *
 * @author deronj
 */
@ExperimentalAPI
public class WindowSystemXrw
        implements X11WindowManager.ExitListener {

    /**
     * Provides a way for the window system to notify other
     * Wonderland software components that it has exitted.
     */
    public interface ExitListener {

        /** The window system has exitted */
        public void windowSystemExitted();
    }
    /**
     * Preallocate the first two display numbers. The first for the user display and
     * the second for the LG3D display (no longer necessary but used for safety).
     */
    private static SmallIntegerAllocator displayNumAllocator = new SmallIntegerAllocator(2);

    /**
     * The minimum display number, for guaranteeing that Wonderland X displays
     * don't interfere with existing X displays.
     */
    private static final int minDisplayNum = Integer.parseInt(
            System.getProperty("wonderland.appshare.minXDisplay", "0"));

    /** The name of the app instance */
    private String appInstanceName;
    /** The X display number of the X server started. This number is valid when it is non-zero */
    private int displayNum;
    /** The name of the X display. This is the display number prefixed with ":". */
    private String displayName;
    /** The reporter to use for the server */
    private ProcessReporter xServerReporter;

    /* The Xremwin server process */
    private MonitoredProcess xServerProcess;

    /* The X11 window manager */
    private X11WindowManager wm;

    /* A component who wants to listen for window title changes from the window manager */
    private X11WindowManager.WindowTitleListener wtl;
    /** An exit listener */
    private ExitListener exitListener;

    /**
     * Create an instance of WindowSystemXrw. This launches the
     * exernal Xremwin server process and then starts the 
     * window manager thread. This method blocks until the 
     * window manager connects to the server.
     *
     * @param appInstanceName The unique name of the app instance
     * @param wtl A listener whose setWindowTitle method is called whenever the Xremwin server notifies us
     * that the title of the window has changed.
     */
    public static WindowSystemXrw create(String appInstanceName, X11WindowManager.WindowTitleListener wtl) {
        WindowSystemXrw winSys = null;
        try {
            winSys = new WindowSystemXrw(appInstanceName, wtl);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return winSys;
    }

    /**
     * Create an instance of WindowSystemXrw. This launches the
     * exernal Xremwin server process and then starts the 
     * window manager thread. This method blocks until the 
     * window manager connects to the server.
     *
     * @param appInstanceName The unique name of the app instance
     * @param wtl A listener whose setWindowTitle method is called whenever the Xremwin server notifies us
     * that the title of the window has changed.
     */
    private WindowSystemXrw(String appInstanceName, X11WindowManager.WindowTitleListener wtl) {
        this.appInstanceName = appInstanceName;
        startXServer();
        startWindowManager(wtl);
    }

    /** Attach an exit listener */
    void setExitListener(ExitListener listener) {
        exitListener = listener;
    }

    /** Start the Xremwin server */
    private void startXServer() {
        displayNum = allocDisplayNum();
        displayName = ":" + displayNum;

        String topDir = System.getProperty("user.dir");

        String[] cmdAndArgs = new String[2];
        cmdAndArgs[0] = topDir + "/bin/runxremwin";
        cmdAndArgs[1] = displayName;

        AppXrw.logger.info("cmdAndArgs for " + appInstanceName);
        AppXrw.logger.info("cmdAndArgs[0] = " + cmdAndArgs[0]);
        AppXrw.logger.info("cmdAndArgs[1] = " + cmdAndArgs[1]);

        String processName = "Xremwin server for " + appInstanceName;
        xServerReporter = ProcessReporterFactory.getFactory().create(processName);
        if (xServerReporter == null) {
            cleanup();
            throw new RuntimeException("Cannot create error reporter for " +
                    processName);
        }

        xServerProcess = new MonitoredProcess(appInstanceName, cmdAndArgs, xServerReporter);
        if (!xServerProcess.start()) {
            xServerReporter.output("Cannot start Xremwin server");
            xServerReporter.exitValue(-1);
            cleanup();
            throw new RuntimeException("Cannot start Xremwin server");
        }
    }

    /** 
     * Start the window manager
     *
     * @param wtl A listener whose setWindowTitle method is called whenever the Xremwin server notifies us
     * that the title of the window has changed.
     */
    private void startWindowManager(X11WindowManager.WindowTitleListener wtl) {
        X11IntegrationModule nativeWinIntegration =
                new X11IntegrationModule(displayName);
        nativeWinIntegration.initialize(wtl);
        wm = nativeWinIntegration.getWindowManager();
        wm.addExitListener(this);
    }

    /** For internal use only */
    public void windowManagerExitted() {
        AppXrw.logger.severe("Window manager exitted for " + appInstanceName);
        wm.removeExitListener(this);
        cleanup();
    }

    /** 
     * Clean up resources. This kills the server process and stops the window manager thread.
     */
    public void cleanup() {
        AppXrw.logger.warning("Shutting down X11 window system for display " + displayNum);

        if (displayNum != 0) {
            deallocDisplayNum(displayNum);
        }

        if (xServerReporter != null) {
            xServerReporter.cleanup();
            xServerReporter = null;
        }

        if (xServerProcess != null) {
            xServerProcess.cleanup();
            xServerProcess = null;
        }


        // In the case of an error, the WindowManager may still be running.
        // Calling disconnect multiple times won't break anything, so double
        // check that it is disconnected.
        if (wm != null) {
            wm.disconnect();
            wm = null;
        }

        wtl = null;

        if (exitListener != null) {
            exitListener.windowSystemExitted();
        }
        exitListener = null;
    }

    /**
     * The display name used to start the X server.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * The display number used to start the X server.
     */
    public int getDisplayNum() {
        return displayNum;
    }

    /** 
     * Allocate a new unique X11 display number.
     *
     * @return The display number allocated.
     */
    private static int allocDisplayNum() {
        return displayNumAllocator.allocate() + minDisplayNum;
    }

    /**
     * Return the given X11 display number to the pool.
     *
     * @param displayNum The display number to deallocated.
     */
    private static void deallocDisplayNum(int displayNum) {
        displayNumAllocator.free(displayNum - minDisplayNum);
    }

    /**
     * Tell the window system that this window has been closed.
     * If the window is primary it may make the app quit.
     */
    public void deleteWindow(int wid) {
        if (wm != null) {
            wm.deleteWindow(wid);
        }
    }
}
