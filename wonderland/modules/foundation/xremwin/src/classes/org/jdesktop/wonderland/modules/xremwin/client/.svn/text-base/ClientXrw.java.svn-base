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

import java.awt.Toolkit;
import java.io.IOException;
import java.util.logging.Level;
import com.jme.math.Vector3f;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.CreateWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.DestroyWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.ShowWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.ConfigureWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.PositionWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.RestackWindowMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.WindowSetDecoratedMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.WindowSetBorderWidthMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.WindowSetUserDisplMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.WindowSetRotateYMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.DisplayPixelsMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.CopyAreaMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.ControllerStatusMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.MessageArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.ServerMessageType;
import org.jdesktop.wonderland.modules.appbase.client.utils.stats.StatisticsReporter;
import org.jdesktop.wonderland.modules.appbase.client.utils.stats.StatisticsSet;
import org.jdesktop.wonderland.modules.appbase.client.ProcessReporter;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.ControlArb;
import org.jdesktop.wonderland.common.cell.CellTransform;
import javax.swing.SwingUtilities;
import java.io.EOFException;

// TODO: 0.4 protocol: temporarily insert
import org.jdesktop.wonderland.modules.xremwin.client.Proto.DisplayCursorMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.MoveCursorMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.ShowCursorMsgArgs;

/*
 * The generic Xrw client superclass. This is a protocol interpreter
 * for the Xremwin protocol. It communicates with a Server which 
 * represents either an Xremwin server or Xremwin master.
 *
 * Known Subclasses: ClientXrwMaster, ClientXrwSlave
 *
 * @author deronj
 */
@ExperimentalAPI
public abstract class ClientXrw implements Runnable {

    // TODO: Bugs in parsing the incoming stream from the server
    // end up trying to create huge windows and blowing the java heap
    // Prevent this. Eventually need to do cleaner.
    // It would be cleaner to base these numbers on the max tex width/height of the 
    // graphics card of the user display.
    private static final int WINDOW_MAX_WIDTH  = 4000;
    private static final int WINDOW_MAX_HEIGHT = 4000;

    // The connection to the XRemwin server or master.
    protected ServerProxy serverProxy;

    // The unique ID of this client connecting to the app (assigned by the master).
    protected int clientId;
    private CreateWindowMsgArgs createWinMsgArgs = new CreateWindowMsgArgs();
    private DestroyWindowMsgArgs destroyWinMsgArgs = new DestroyWindowMsgArgs();
    private ShowWindowMsgArgs showWinMsgArgs = new ShowWindowMsgArgs();
    private ConfigureWindowMsgArgs configureWinMsgArgs = new ConfigureWindowMsgArgs();
    private PositionWindowMsgArgs positionWinMsgArgs = new PositionWindowMsgArgs();
    private RestackWindowMsgArgs restackWinMsgArgs = new RestackWindowMsgArgs();
    private WindowSetDecoratedMsgArgs winSetDecoratedMsgArgs = new WindowSetDecoratedMsgArgs();
    private WindowSetBorderWidthMsgArgs winSetBorderWidthMsgArgs = new WindowSetBorderWidthMsgArgs();
    private WindowSetUserDisplMsgArgs winSetUserDisplMsgArgs = new WindowSetUserDisplMsgArgs();
    private WindowSetRotateYMsgArgs winSetRotateYMsgArgs = new WindowSetRotateYMsgArgs();
    private DisplayPixelsMsgArgs displayPixelsMsgArgs = new DisplayPixelsMsgArgs();
    private CopyAreaMsgArgs copyAreaMsgArgs = new CopyAreaMsgArgs();
    private ControllerStatusMsgArgs controllerStatusMsgArgs = new ControllerStatusMsgArgs();

    // TODO: 0.4 protocol: temporarily insert
    private DisplayCursorMsgArgs displayCursorMsgArgs = new DisplayCursorMsgArgs();
    private MoveCursorMsgArgs moveCursorMsgArgs = new MoveCursorMsgArgs();
    private ShowCursorMsgArgs showCursorMsgArgs = new ShowCursorMsgArgs();

    // true indicates the client thread should stop running
    protected boolean stop;

    // For debug
    //private static final boolean ENABLE_XREMWIN_STATS = false;
    private static boolean ENABLE_XREMWIN_STATS = false;
    private StatisticsReporter statReporter;
    private long numRequests = 0;
    private long displayPixelsNumBytes = 0;
    private long numCopyAreas = 0;

    // For debug
    private static boolean verbose = false;
    private static Level loggerLevelOrig;


    static {
        /* For debug
        System.err.println("logger level obj = " + AppXrw.logger.getLevel());
        if (AppXrw.logger.getLevel() != null) {
        System.err.println("logger level int = " + AppXrw.logger.getLevel().intValue());
        } else {
        System.exit(1);
        }
         */
        loggerLevelOrig = AppXrw.logger.getLevel();
        if (verbose) {
            AppXrw.logger.setLevel(Level.FINER);
        }
    }

    // For debug
    public static void toggleXremwinStatsEnable() {
        ENABLE_XREMWIN_STATS = !ENABLE_XREMWIN_STATS;
        System.err.println("Xremwin statistics are " +
                (ENABLE_XREMWIN_STATS ? "enabled" : "disabled"));
    }

    // For debug
    public static void toggleVerbosity() {
        verbose = !verbose;
        if (verbose) {
            AppXrw.logger.setLevel(Level.FINER);
        } else {
            AppXrw.logger.setLevel(loggerLevelOrig);
        }
        System.err.println("Xremwin verbosity is " +
                (verbose ? "enabled" : "disabled"));
    }
    /** The associated application */
    protected AppXrw app;
    /**
     * The control arbitrator used by this app.
     */
    protected ControlArb controlArb;
    /**
     * The protocol interpreter thread (the main loop of the client)
     */
    protected Thread thread;
    /** The output reporter */
    protected ProcessReporter reporter;
    /** Is the server connected? */
    protected boolean serverConnected;
    /** Whether the client is enabled. */
    protected boolean enable;
    /** Lock object used for enable. */
    private final Object enableLock = new Object();
    /** Used by the logging messages in this class */
    private int messageCounter = 0;

    /**
     * Create a new instance of ClientXrw.
     *
     * @param app The application for whom the client is operating.
     * @param controlArb The control arbiter for the app.
     * @param reporter Report output and exit status to this.
     * @throws InstantiationException If it could not make contact with the server.
     */
    public ClientXrw(AppXrw app, ControlArb controlArb, ProcessReporter reporter)
            throws InstantiationException {
        this.app = app;
        this.controlArb = controlArb;
        this.reporter = reporter;

        // TODO: it would be nice to put the app instance name here
        thread = new Thread(this, "Remote Window Client");

    /* TODO
    if (ENABLE_XREMWIN_STATS) {
    statReporter = new StatisticsReporter(15, new Statistics(),
    new Statistics(),
    new Statistics());
    statReporter.start();
    }
     */
    }

    /**
     * Release resources held. 
     */
    public void cleanup() {
        if (!stop && thread != null) {
            stop = true;
            /* Note: can't join thread here. Conflicts with app base shutdown hook.
            try {
                thread.join();
            } catch (InterruptedException ex) {
            }
            */
            thread = null;
        }

        if (reporter != null) {
            reporter.cleanup();
            reporter = null;
        }

        app = null;

        if (serverProxy != null) {
            serverProxy.cleanup();
            serverProxy = null;
        }

        enable = false;
        AppXrw.logger.severe("ClientXrw cleaned up");
    }

    /** 
     * Start the interpreter thread.
     */
    protected void start() {
        thread.start();
    }

    /**
     * The app associated with this client.
     */
    public AppXrw getApp() {
        return app;
    }

    /**
     * After the client loop is first started it will wait to make the first window visible
     * until the client is enabled. 
     */
    public void enable () {
        synchronized (enableLock) {
            if (enable) return;
            enable = true;
            enableLock.notifyAll();
        }
    }

    /** 
     * The  main loop of the client.
     */
    public void run() {

        while (serverConnected && !stop && serverProxy != null) {

            try {

                // Read message type from the server.
                ServerMessageType msgType = serverProxy.getMessageType();
                //if (msgType != Proto.ServerMessageType.DISPLAY_PIXELS){
                AppXrw.logger.info("msgType " + (++messageCounter) + ": " + msgType);
                //}

                /* TODO
                   if (ENABLE_XREMWIN_STATS) {
                   synchronized (this) {
                   numRequests++;
                   }
                   }
                */

                // Get the full message
                MessageArgs msgArgs = readMessageArgs(msgType);
                if (msgArgs != null) {
                    //if (msgType != Proto.ServerMessageType.DISPLAY_PIXELS){
                    AppXrw.logger.info("msgArgs: " + msgArgs);
                    //}

                    /* For debug: an example of how to ignore the firefox heartbeat which occurs on igoogle
                       if (msgType == ServerMessageType.DISPLAY_PIXELS) {
                       if (displayPixelsMsgArgs.x == 1261 &&
                       displayPixelsMsgArgs.y == 3 &&
                       displayPixelsMsgArgs.w == 17 &&
                       displayPixelsMsgArgs.h == 17) {
                       } else {
                       AppXrw.logger.info("msgType " + (++messageCounter) + ": " + msgType + ", msgArgs: " + msgArgs);
                       }
                       } else {
                       AppXrw.logger.info("msgType " + (++messageCounter) + ": " + msgType + ", msgArgs: " + msgArgs);
                       }
                    */
                }

                // Process the message
                processMessage(msgType);

            } catch (Throwable throwable) {
                if (serverProxy != null) {
                    throwable.printStackTrace();
                    stop = true;
                    cleanup();

                    if (app.isInSas()) {
                        System.err.println("SAS provider aborted.");
                        System.exit(1);
                    }
                }
            }
        }
    }

    /**
     * Read the specific message arguments for the given message type.
     *
     * @param msgType The message type.
     */
    protected MessageArgs readMessageArgs(ServerMessageType msgType) throws EOFException {
        if (serverProxy == null) return null;

        switch (msgType) {

            case SERVER_DISCONNECT:
                return null;

            case CREATE_WINDOW:
                serverProxy.getData(createWinMsgArgs);
                return createWinMsgArgs;

            case DESTROY_WINDOW:
                serverProxy.getData(destroyWinMsgArgs);
                return destroyWinMsgArgs;

            case SHOW_WINDOW:
                serverProxy.getData(showWinMsgArgs);
                return showWinMsgArgs;

            case CONFIGURE_WINDOW:
                serverProxy.getData(configureWinMsgArgs);
                return configureWinMsgArgs;

            case POSITION_WINDOW:
                serverProxy.getData(positionWinMsgArgs);
                return positionWinMsgArgs;

            case RESTACK_WINDOW:
                serverProxy.getData(restackWinMsgArgs);
                return restackWinMsgArgs;

            case WINDOW_SET_DECORATED:
                serverProxy.getData(winSetDecoratedMsgArgs);
                return winSetDecoratedMsgArgs;

            case WINDOW_SET_BORDER_WIDTH:
                serverProxy.getData(winSetBorderWidthMsgArgs);
                return winSetBorderWidthMsgArgs;

            case WINDOW_SET_USER_DISPLACEMENT:
                serverProxy.getData(winSetUserDisplMsgArgs);
                return winSetUserDisplMsgArgs;

            case WINDOW_SET_ROTATE_Y:
                serverProxy.getData(winSetRotateYMsgArgs);
                return winSetRotateYMsgArgs;

            case BEEP:
                serverProxy.getData();
                return null;

            case DISPLAY_PIXELS:
                serverProxy.getData(displayPixelsMsgArgs);
                return displayPixelsMsgArgs;

            case COPY_AREA:
                serverProxy.getData(copyAreaMsgArgs);
                return copyAreaMsgArgs;

            case CONTROLLER_STATUS:
                serverProxy.getData(controllerStatusMsgArgs);
                return controllerStatusMsgArgs;

            // TODO: 0.4 protocol: temporarily insert
            case DISPLAY_CURSOR:
                serverProxy.getData(displayCursorMsgArgs);
                return displayCursorMsgArgs;

            // TODO: 0.4 protocol: temporarily insert
            case MOVE_CURSOR:
                serverProxy.getData(moveCursorMsgArgs);
                return moveCursorMsgArgs;

            // TODO: 0.4 protocol: temporarily insert
            case SHOW_CURSOR:
                serverProxy.getData(showCursorMsgArgs);
                return moveCursorMsgArgs;

            default:
                throw new RuntimeException("Unknown server message: " + msgType);
        }
    }

    /**
     * Process the message that has been read for the given message type.
     *
     * @param msgType The message type.
     */
    protected void processMessage(ServerMessageType msgType) throws EOFException {
        WindowXrw win;

        switch (msgType) {

            case SERVER_DISCONNECT:
                serverConnected = false;
                break;

            case CREATE_WINDOW:

                // We can't make windows visible until we are enable.
                synchronized (enableLock) {
                    while (!enable) {
                        try { enableLock.wait(); } catch (InterruptedException ex) {}
                    }
                }

                win = lookupWindow(createWinMsgArgs.wid);
                if (win != null) {
                    AppXrw.logger.warning("CreateWindow: redundant create: wid = " + createWinMsgArgs.wid);
                } else {
                    createWindow(createWinMsgArgs);
                }
                break;

            case DESTROY_WINDOW:
                win = lookupWindow(destroyWinMsgArgs.wid);
                if (win == null) {
                    AppXrw.logger.warning("DestroyWindow: window doesn't exist: wid = " + destroyWinMsgArgs.wid);
                } else {
                    destroyWindow(win);
                }
                break;

            case SHOW_WINDOW:
                win = lookupWindow(showWinMsgArgs.wid);
                if (win == null) {
                    AppXrw.logger.warning("ShowWindow: window doesn't exist: wid = " + showWinMsgArgs.wid);
                } else {
                    /* TODO: 0.4 protocol:
                    WindowXrw transientFor = lookupWindow(showWinMsgArgs.transientFor);
                    win.setVisibleApp(showWinMsgArgs.show, transientFor);
                     */
                    win.setVisibleApp(showWinMsgArgs.show, showWinMsgArgs.isTransient);
                }
                break;

            case CONFIGURE_WINDOW:
                win = lookupWindow(configureWinMsgArgs.wid);
                if (win == null) {
                    AppXrw.logger.warning("ConfigureWindow: window doesn't exist: wid = " + configureWinMsgArgs.wid);
                } else {
                    configureWindow(win, configureWinMsgArgs);
                }
                break;

            case POSITION_WINDOW:
                // If the move was made interactively by this client, ignore it */
                if (positionWinMsgArgs.clientId != clientId) {
                    win = lookupWindow(positionWinMsgArgs.wid);
                    if (win == null) {
                        AppXrw.logger.warning("PositionWindow: window doesn't exist: wid = " + positionWinMsgArgs.wid);
                    } else {
                        win.setScreenPosition/*TODO:Local*/(positionWinMsgArgs.x, positionWinMsgArgs.y);
                    }
                }
                break;

            case RESTACK_WINDOW:
                // If the move was made interactively by this client, ignore it */
                if (restackWinMsgArgs.clientId != clientId) {
                    win = lookupWindow(restackWinMsgArgs.wid);
                    if (win == null) {
                        AppXrw.logger.warning("RestackWindow: window doesn't exist: wid = " + restackWinMsgArgs.wid);
                    } else {
                        WindowXrw sibwin = lookupWindow(restackWinMsgArgs.sibid);
                        if (sibwin == null) {
                            AppXrw.logger.warning("RestackWindow: sibling window doesn't exist: sibid = " +
                                    restackWinMsgArgs.sibid);
                        } else {
                            win.restackAbove/*TODO:winconfig:Local*/(sibwin);
                        }
                    }
                }
                break;

            case WINDOW_SET_DECORATED:
                win = lookupWindow(winSetDecoratedMsgArgs.wid);
                if (win == null) {
                    AppXrw.logger.warning("WindowSetDecorated: window doesn't exist: wid = " + winSetDecoratedMsgArgs.wid);
                } else {
                    win.setDecorated(winSetDecoratedMsgArgs.decorated);
                }
                break;

            case WINDOW_SET_BORDER_WIDTH:
                win = lookupWindow(winSetDecoratedMsgArgs.wid);
                if (win == null) {
                    AppXrw.logger.warning("WindowSetBorderWidth: window doesn't exist: wid = " +
                            winSetBorderWidthMsgArgs.wid);
                } else {
                    win.setBorderWidth(winSetBorderWidthMsgArgs.borderWidth);
                }

                break;

            case WINDOW_SET_USER_DISPLACEMENT:
                // If this was performed interactively by this client, ignore it
                if (winSetUserDisplMsgArgs.clientId != clientId) {
                    win = lookupWindow(winSetUserDisplMsgArgs.wid);
                    if (win == null) {
                        AppXrw.logger.warning("WindowSetUserDispl: window doesn't exist: wid = " +
                                winSetUserDisplMsgArgs.wid);
                    } else {
                        CellTransform transform = new CellTransform(null, winSetUserDisplMsgArgs.userDispl);
                        win.setUserTransformCellLocal(transform);
                    }
                    break;
                }

            case WINDOW_SET_ROTATE_Y:
                /* TODO:someday: not yet supported for secondaries (Part 1)
                // If this was performed interactively by this client, ignore it
                if (winSetRotateYMsgArgs.clientId != clientId) {
                    win = lookupWindow(winSetRotateYMsgArgs.wid);
                    if (win == null) {
                        AppXrw.logger.warning("WindowSetRotateY: window doesn't exist: wid = " + winSetRotateYMsgArgs.wid);
                    } else {
                        win.setRotateY(win, winSetRotateYMsgArgs.roty);
                    }
                }
                */
                break;

            case BEEP:
                Toolkit.getDefaultToolkit().beep();
                break;

            case DISPLAY_PIXELS:
                win = lookupWindow(displayPixelsMsgArgs.wid);
                if (win == null) {
                    AppXrw.logger.warning("DisplayPixels: invalid window ID = " + displayPixelsMsgArgs.wid);
                    return;
                }
                processDisplayPixels(win, displayPixelsMsgArgs);
                break;

            case COPY_AREA:
                win = lookupWindow(copyAreaMsgArgs.wid);
                if (win == null) {
                    AppXrw.logger.warning("CopyArea: window doesn't exist: wid = " + copyAreaMsgArgs.wid);
                } else {
                    synchronized (this) {
                        numCopyAreas++;
                    }

                    win.copyArea(copyAreaMsgArgs.srcX, copyAreaMsgArgs.srcY,
                            copyAreaMsgArgs.width, copyAreaMsgArgs.height,
                            copyAreaMsgArgs.dstX, copyAreaMsgArgs.dstY);
                }
                break;

            case CONTROLLER_STATUS:
                processControllerStatus(controllerStatusMsgArgs);
                break;

            // TODO: 0.4 protocol: temporarily insert
            case DISPLAY_CURSOR:
            case MOVE_CURSOR:
            case SHOW_CURSOR:
                break;

            default:
                throw new RuntimeException("Internal error: no handler for message type : " + msgType);
        }
    }

    /**
     * Handle the ControllerStatus Message.
     *
     * @param msgArgs The arguments which have been read for the ControllerStatus message.
     */
    protected void processControllerStatus(ControllerStatusMsgArgs msgArgs) {
        if (!(controlArb instanceof ControlArbXrw)) {
            return;
        }

        switch (msgArgs.status) {

            case REFUSED:
                // We only care about our attempts that are refused
                if (msgArgs.clientId == clientId) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run () {
                            ((ControlArbXrw)controlArb).controlRefused();
                        }
                    });
                }
                break;

            case GAINED:
                // We only care about our attempts that succeed
                if (msgArgs.clientId == clientId) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run () {
                            ((ControlArbXrw)controlArb).controlGained();
                        }
                    });
                }
                break;

            case LOST:
                if (msgArgs.clientId == clientId) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run () {
                            ((ControlArbXrw)controlArb).controlLost();
                        }
                    });
                } else {
                    // Update control highlighting for other clients besides control loser
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run () {
                            ((ControlArbXrw)controlArb).setController(null);
                        }
                    });
                }
                break;
        }
    }

    /**
     * Given a window ID return the associated window.
     *
     * @param wid The X11 window ID.
     */
    protected WindowXrw lookupWindow(int wid) {
        if (app == null) return null;
        WindowXrw win = ((AppXrw)app).getWindowForWid(wid);
        if (win == null) return null;
        if (win.isZombie()) return null;
        return win;
    }

    /**
     * Associate this window ID with this window.
     *
     * @param wid The X11 window ID.
     * @param window The window to associate with the wid.
     */
    protected void addWindow(int wid, WindowXrw window) {
        ((AppXrw)app).addWindow(wid, window);
    }

    /**
     * Remove the association of fhis window ID with its window.
     *
     * @param window The window to disassociate from the wid.
     */
    protected void removeWindow(WindowXrw win) {
        ((AppXrw)app).removeWindow(win.getWid());
    }

    /**
     * Create a window. 
     *
     * @param msg The message arguments which have been read for the CreateWindow message.
     */
    protected WindowXrw createWindow(CreateWindowMsgArgs msg) {
        try {
            WindowXrw win = app.createWindow(msg.x, msg.y, msg.wAndBorder, msg.hAndBorder, msg.borderWidth,
                    msg.decorated, msg.wid);
            addWindow(msg.wid, win);
            return win;
        } catch (IllegalStateException ex) {
            AppXrw.logger.warning("CreateWindow: Cannot create window " + msg.wid);
            return null;
        }
    }

    /**
     * Destroy the given window.
     *
     * @param win The window to destroy.
     */
    private void destroyWindow(WindowXrw win) {
        removeWindow(win);
        win.cleanup();
    }

    /** 
     * Configure (that is, resize, move or restack) a window.
     *
     * @param win The window to configure.
     * @param msg The message arguments which have been read for the ConfigureWindow message.
     */
    private void configureWindow(WindowXrw win, ConfigureWindowMsgArgs msg) {

        // Is this a configure from ourselves or some other client?
        if (msg.clientId == clientId) {

            // Self configure: see if this is a size change
            if (msg.wAndBorder != win.getWidth() ||
                msg.hAndBorder != win.getHeight()) {

                // Accept this self resize. This is because the user resize operation
                // is not completely finished until setDimensions is called with
                // the new width and height
            } else {
                // Not a resize. It's a move-or-restack-only from ourselves. Ignore it.
            }
        }

        if (msg.wAndBorder > WINDOW_MAX_WIDTH) {
            msg.wAndBorder = WINDOW_MAX_WIDTH;
            AppXrw.logger.warning("createWindow: width " + msg.wAndBorder + " was truncated to maximum width");
        }
        if (msg.hAndBorder > WINDOW_MAX_HEIGHT) {
            msg.hAndBorder = WINDOW_MAX_HEIGHT;
            AppXrw.logger.warning("createWindow: height " + msg.hAndBorder + " was truncated to maximum height");
        }

        WindowXrw sibWin = lookupWindow(msg.sibid);
        win.setScreenPosition(msg.x, msg.y);
        win.setSize(msg.wAndBorder, msg.hAndBorder);
        win.restackAbove(sibWin);
    }

    /**
     * Process the display pixels message.
     *
     * @param win The window in which to display pixels.
     * @param displayPixelsMsgArgs The message arguments which have been read for the message.
     */
    private void processDisplayPixels(WindowXrw win, DisplayPixelsMsgArgs displayPixelsMsgArgs) 
        throws EOFException
    {
        switch (displayPixelsMsgArgs.encoding) {

            case UNCODED:
                //displayRect(win, displayPixelsMsgArgs);
                throw new RuntimeException("UNCODED pixels from the xremwin server is no longersupported.");

            case RLE24:
                displayRectRle24(win, displayPixelsMsgArgs);
                break;

            default:
                throw new RuntimeException("Unknown pixel encoding " + displayPixelsMsgArgs.encoding);
        }
    }
    /**
     * Uncoded DisplayPixels (OBSOLETE)
     * If win == null we read the pixels but discard them.
    private void displayRect (WindowXrw win, DisplayPixelsMsgArgs dpMsgArgs) {

    //AppXrw.logger.finer("displayRect");
    //AppXrw.logger.finer("x = " + dpMsgArgs.x);
    //AppXrw.logger.finer("y = " + dpMsgArgs.y);
    //AppXrw.logger.finer("w = " + dpMsgArgs.w);
    //AppXrw.logger.finer("h = " + dpMsgArgs.h);

    serverProxy.setScanLineWidth(dpMsgArgs.w);

    int[] winPixels = new int[dpMsgArgs.w * dpMsgArgs.h];

    int dstIdx = 0;
    int dstNextLineIdx = 0;

    for (int y = 0; y < dpMsgArgs.h; y++) {
    dstNextLineIdx += dpMsgArgs.w;

    // Reads into scanLineBuf
    byte[] scanLineBytes = serverProxy.readScanLine();

    int srcIdx = 0;

    for (int i = 0;
    i < dpMsgArgs.w && srcIdx < scanLineBytes.length - 2;
    i++, srcIdx += 4) {

    //AppXrw.logger.finer("dstIdx = " + dstIdx);
    //AppXrw.logger.finer("srcIdx = " + srcIdx);
    //AppXrw.logger.finer("winPixels.length = " + winPixels.length);
    //AppXrw.logger.finer("scanLineBytes.length = " + scanLineBytes.length);

    // Note: source format is BGRX and dest format is XBGR
    winPixels[dstIdx++] =
    ((scanLineBytes[srcIdx + 2] & 0xff) << 16) |
    ((scanLineBytes[srcIdx + 1] & 0xff) <<  8) |
    (scanLineBytes[srcIdx + 0] & 0xff);
    }

    dstIdx = dstNextLineIdx;
    }

    //Debug: print all scanlines collected
    //printRLBegin();
    //for (int y = 0; y < dpMsgArgs.h; y++) {
    //    int idx = y * dpMsgArgs.w ;
    //    for (int i = 0; i < dpMsgArgs.w; i++) {
    //	printRL(winPixels[idx + i]);
    //    }
    //}
    //printRLEnd();

    if (win != null) {
    win.displayPixels(dpMsgArgs.x, dpMsgArgs.y, dpMsgArgs.w, dpMsgArgs.h, winPixels);
    }
    }
     */
    private int maxVerboseRuns = 120;
    private int numRunsReceived = 0;
    private byte[] chunkBuf = null;
    private int chunkBufSize = 0;

    /**
     * Decode a run-length encoded DisplayPixels message. If win == null we read the pixels but discard them.
     *
     * @param win The window in which to display pixels.
     * @param dpMsgArgs The message arguments which have been read for the message.
     */
    private void displayRectRle24(WindowXrw win, DisplayPixelsMsgArgs dpMsgArgs) throws EOFException {

        synchronized (this) {
            displayPixelsNumBytes += dpMsgArgs.w * dpMsgArgs.h * 4;
        }

        int h = dpMsgArgs.h;
        int numChunks = serverProxy.readRleInt();

        /*
        AppXrw.logger.finer("displayRectRle24");
        AppXrw.logger.finer("x = " + dpMsgArgs.x);
        AppXrw.logger.finer("y = " + dpMsgArgs.y);
        AppXrw.logger.finer("w = " + dpMsgArgs.w);
        AppXrw.logger.finer("h = " + h);
        AppXrw.logger.finer("numChunks = " + numChunks);
         */

        int[] winPixels = new int[dpMsgArgs.w * dpMsgArgs.h];

        int dstIdx = 0;
        int x = 0;
        int chunkCount = 0;

        while (numChunks-- > 0) {

            int chunkHeight = serverProxy.readRleInt();
            int chunkBytes = serverProxy.readRleInt();
            if (chunkBytes > chunkBufSize) {
                chunkBuf = new byte[chunkBytes];
                chunkBufSize = chunkBytes;
            }
            //AppXrw.logger.finer("chunkCount = " + chunkCount);
            //AppXrw.logger.finer("chunkBytes = " + chunkBytes);

            int dstNextLineIdx = dstIdx + chunkHeight * dpMsgArgs.w;

            // Read first chunk of data from server
            serverProxy.readRleChunk(chunkBuf, chunkBytes);

            int chunkOffset = 0;

            while (chunkBytes > 0) {
                int count = chunkBuf[chunkOffset + 3] & 0xFF;
                int pixel = ((chunkBuf[chunkOffset + 2] + 256) & 0xFF) << 16 |
                        ((chunkBuf[chunkOffset + 1] + 256) & 0xFF) << 8 |
                        ((chunkBuf[chunkOffset + 0] + 256) & 0xFF);
                //System.err.println("pixel = " + Integer.toHexString(pixel));

                // Make the pixels opaque so that we can copy them with Graphics.drawImage
                pixel |= 0xff000000;

                /*
                if (numRunsReceived++ < maxVerboseRuns) {
                AppXrw.logger.finer("numRunsReceived = " + numRunsReceived);
                AppXrw.logger.finer("count = " + count);
                AppXrw.logger.finer("pixel = " + Integer.toHexString(pixel));
                }
                 */

                for (int i = 0; i < count; i++, x++) {
                    if (x >= dpMsgArgs.w) {
                        x = 0;
                        dstIdx += dpMsgArgs.w;
                    }
                    try {
                        // TODO: this works around an index-out-of-bounds problem. Why?
                        if ((dstIdx + x) < winPixels.length) {
                            winPixels[dstIdx + x] = pixel;
                        }
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        AppXrw.logger.finer("*********** Array out of bounds!!!!!!!!!!!!!!!!");
                        AppXrw.logger.finer("winPixels.length = " + winPixels.length);
                        AppXrw.logger.finer("dstIdx + x = " + (dstIdx + x));
                        AppXrw.logger.finer("dstIdx = " + dstIdx);
                        AppXrw.logger.finer("x = " + x);
                        AppXrw.logger.finer("chunkCount = " + chunkCount);
                        AppXrw.logger.finer("chunkBytes = " + chunkBytes);
                    }
                }

                chunkOffset += 4;
                chunkBytes -= 4;
            }

            dstIdx = dstNextLineIdx;
            x = 0;

            chunkCount++;
        }

        // Now transfer the decoded pixels into the texture
        if (win != null) {
            win.displayPixels(dpMsgArgs.x, dpMsgArgs.y, dpMsgArgs.w, h, winPixels);
        }
    }

    /**
     * Sends updates to the user displacement to the server.
     *
     * @param win The window being displaced.
     * @param userDispl The new displacement vector.
     */
    public void windowSetUserDisplacement(WindowXrw win, Vector3f userDispl) {
        int wid = ((WindowXrw) win).getWid();
        AppXrw.logger.finer("To server: SetUserDispl: wid = " + wid + ", userDispl = " + userDispl);

        try {
            serverProxy.windowSetUserDisplacement(clientId, wid, userDispl);
        } catch (IOException ex) {
            AppXrw.logger.warning("Client cannot send user displacement for window " + wid);
        }
    }

    /**
     * Sends updates to the window size to the server.
     *
     * @param win The window being displaced.
     * @param w The new width of the window.
     * @param h The new height of the window.
     */
    public void windowSetSize(WindowXrw win, int w, int h) {
        int wid = ((WindowXrw) win).getWid();
        AppXrw.logger.finer("To server: SetSize: wid = " + wid + ", wh = " + w + ", " + h);

        try {
            serverProxy.windowSetSize(clientId, wid, w, h);
        } catch (IOException ex) {
            AppXrw.logger.warning("Client cannot send size for window " + wid);
        }
    }

    /**
     * Sends updates to the window's Y rotation to the server.
     *
     * @param win The window being rotated.
     * @param angle The new Y rotation angle of the window.
     */
    public void windowSetRotateY(WindowXrw win, float angle) {
        /* TODO:someday. not yet supported for secondaries  (Part 2)
        int wid = ((WindowXrw) win).getWid();
        AppXrw.logger.finer("To server: SetRotateY: wid = " + wid + ", angle = " + angle);

        try {
            serverProxy.windowSetRotateY(clientId, wid, angle);
        } catch (IOException ex) {
            AppXrw.logger.warning("Client cannot send rotation Y for window " + wid);
        }
        */
    }

    /**
     * Sends a message to the server telling it that the window has been moved to the front
     * of all other windows on the stack.
     *
     * @param win The window whose stack position has changed.
     */
    public void windowToFront(WindowXrw win) {
        int wid = win.getWid();
        AppXrw.logger.finer("To server: ToFront: wid = " + wid);

        try {
            serverProxy.windowToFront(clientId, wid);
        } catch (IOException ex) {
            AppXrw.logger.warning("Client cannot send toFront for window " + wid);
        }
    }

    /**
     * Called when the user closes the given window.
     *
     * @param win The window to close.
     */
    public abstract void windowCloseUser(WindowXrw win);

    /**
     * Returns whether the client is connected to the server. 
     */
    public boolean isConnected () {
        return serverConnected;
    }

    /*
     ** For Debug: Print pixel run lengths
     */
    private boolean printRLLastValueValid;
    private int printRLLastValue;
    private int printRLLastValueCount;

    private void printScanLine(byte[] scanLineBuf, int width) {
        printRLBegin();
        for (int i = 0; i < width; i++) {
            int m = (scanLineBuf[i * 4 + 2] & 0xFF) << 16 |
                    (scanLineBuf[i * 4 + 1] & 0xFF) << 8 |
                    (scanLineBuf[i * 4] & 0xFF);
            printRL(m);
        }
        printRLEnd();
    }

    private void printRLRun() {
        if (printRLLastValueCount > 0) {
            System.err.print(printRLLastValue);
            if (printRLLastValueCount > 1) {
                System.err.println(" x " + printRLLastValueCount);
            } else {
                System.err.println();
            }
        }
    }

    private void printRLBegin() {
        printRLLastValueValid = false;
        printRLLastValueCount = 0;
    }

    private void printRL(int value) {
        if (printRLLastValueValid && value == printRLLastValue) {
            printRLLastValueCount++;
        } else {
            printRLRun();

            printRLLastValueCount = 1;
            printRLLastValue = value;
            printRLLastValueValid = true;
        }
    }

    private void printRLEnd() {
        printRLRun();
    }

    private class Statistics extends StatisticsSet {

        // The number of requests of any type received
        private long numRequests;

        // The number of Display Pixels message bytes received
        private long displayPixelsNumBytes;

        // The number of Copy Area messages received
        private long numCopyAreas;

        protected Statistics() {
            super("Xremwin");
        }

        @Override
        protected boolean hasTriggered() {
            // Don't print stats for silent windows
            return numRequests != 0;
        }

        protected void probe() {
            synchronized (ClientXrw.this) {
                numRequests += ClientXrw.this.numRequests;
                displayPixelsNumBytes = ClientXrw.this.displayPixelsNumBytes;
                numCopyAreas = ClientXrw.this.numCopyAreas;
            }
        }

        protected void reset() {
            synchronized (ClientXrw.this) {
                ClientXrw.this.numRequests = 0;
                ClientXrw.this.displayPixelsNumBytes = 0;
                ClientXrw.this.numCopyAreas = 0;
            }
        }

        protected void accumulate(StatisticsSet cumulativeStats) {
            Statistics stats = (Statistics) cumulativeStats;
            stats.numRequests += numRequests;
            stats.displayPixelsNumBytes += displayPixelsNumBytes;
            stats.numCopyAreas += numCopyAreas;
        }

        protected void max(StatisticsSet maxStats) {
            Statistics stats = (Statistics) maxStats;
            stats.numRequests += max(stats.numRequests, numRequests);
            stats.displayPixelsNumBytes = max(stats.displayPixelsNumBytes, displayPixelsNumBytes);
            stats.numCopyAreas = max(stats.numCopyAreas, numCopyAreas);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void appendStats(StringBuffer sb) {
            sb.append("numRequests = " + numRequests + "\n");
            sb.append("displayPixelsNumBytes = " + displayPixelsNumBytes + "\n");
            sb.append("numCopyAreas = " + numCopyAreas + "\n");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void appendStatsAndRates(StringBuffer sb, double timeSecs) {
            appendStats(sb);

            // Calculate and print rates
            double numRequestsPerSec = numRequests / timeSecs;
            sb.append("numRequestsPerSec = " + numRequestsPerSec + "\n");
            double displayPixelsNumBytesPerSec = displayPixelsNumBytes / timeSecs;
            sb.append("displayPixelsNumBytes = " + displayPixelsNumBytesPerSec + "\n");
            double numCopyAreasPerSec = numCopyAreas / timeSecs;
            sb.append("numCopyAreas = " + numCopyAreasPerSec + "\n");
        }
    }
}
