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
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.LinkedList;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.utils.SmallIntegerAllocator;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.modules.appbase.client.utils.net.NetworkAddress;
import org.jdesktop.wonderland.modules.appbase.client.MonitoredProcess;
import org.jdesktop.wonderland.modules.appbase.client.ProcessReporter;
import org.jdesktop.wonderland.modules.xremwin.client.wm.X11WindowManager;

/**
 * A Master Xremwin app. This is the AppXrw subclass used on the client machine
 * which is executing the app.
 *
 * @author deronj
 */
@InternalAPI
public class AppXrwMaster
        extends AppXrw
        implements X11WindowManager.WindowTitleListener, ClientXrwMaster.ExitListener {

    /* An allocator for instance numbers */
    private static HashMap<String, SmallIntegerAllocator> instanceAllocators =
            new HashMap<String, SmallIntegerAllocator>();
    /**  The app instance number (this distinguishes between multiple apps with the same name */
    private int appInstance;
    /**  The name of the app along with its instance number, in the format "<appName> <appInstance>" */
    private String appInstanceName;
    /** The window system (X11 server and window manager) for this app. */
    private WindowSystemXrw winSys;
    /** The main process of the application */
    private MonitoredProcess appProcess;
    /** The master/slave peer-to-peer socket */
    private ServerSocket serverSocket;
    /** The information that slaves use to connect to the server socket */
    private AppXrwConnectionInfo connectionInfo;
    /** List of master apps created by this client. */
    private static final LinkedList<AppXrwMaster> masterApps = new LinkedList<AppXrwMaster>();
    /** The process exit value. */
    private int exitValue = -2;

    /** Defines a listener which is called when the app exits. */
    public interface ExitListener {
        public void appExitted(AppXrwMaster app);
    }

    /** A listener which is called when the app exits. */
    private ExitListener exitListener;

    // Register the X11 appbase shutdown hook
    static {
        Runtime.getRuntime().addShutdownHook(new Thread("X11 App Base Master Shutdown Hook") {
            @Override
            public void run() { AppXrwMaster.shutdownAllApps(); }
        });
    }

    /**
     * Create a new instance of AppXrwMaster in a user client.
     *
     * @param appName The name of the application.
     * @param command The operating system command to execute to start app program.
     * @param cellID The id of the cell this app is associated with.
     * @param pixelScale The size of the window pixels.
     * @param processReporter Report output and exit status to this
     * @param session This app's Wonderland session.
     * @throws InstantiationException Could not launch app
     */
    public AppXrwMaster(String appName, String command, CellID cellID, Vector2f pixelScale,
                        ProcessReporter reporter, WonderlandSession session)
            throws InstantiationException {
        this(appName, command, cellID, pixelScale, reporter, session, false);
    }

    /**
     * Create a new instance of AppXrwMaster.
     *
     * @param appName The name of the application.
     * @param command The operating system command to execute to start app program.
     * @param cellID The id of the cell this app is associated with.
     * @param pixelScale The size of the window pixels.
     * @param processReporter Report output and exit status to this
     * @param session This app's Wonderland session.
     * @param sas Whether this app is being launched by a SAS provider
     * @throws InstantiationException Could not launch app
     */
    public AppXrwMaster(String appName, String command, CellID cellID, Vector2f pixelScale,
                        ProcessReporter reporter, WonderlandSession session, boolean sas)
            throws InstantiationException {

        super(appName, (sas ? new ControlArbNull() : new ControlArbXrw()), pixelScale);
        AppXrw.logger.info("appName = " + appName);
        controlArb.setApp(this);

        AppXrw.logger.info("Created AppXrwMaster with command = " + command);

        appInstance = allocAppInstance(appName);
        appInstanceName = appName + " " + appInstance;
        AppXrw.logger.info("appInstanceName = " + appInstanceName);

        // Note: we must be be careful to initialize components in the
        // following order

        // Start the window system for this app
        winSys = WindowSystemXrw.create(appInstanceName, this);
        if (winSys == null) {
            AppXrw.logger.warning("Cannot launch " + appInstanceName + ": Cannot create window system");
            cleanup();
            throw new InstantiationException();
        }

        // Create the peer-to-peer server socket
        // TODO: change name of this property?
        String masterHost = NetworkAddress.getDefaultHostAddress();
        String publicMasterHost = System.getProperty("wonderland.appshare.hostName", masterHost);
        InetAddress inetAddr = NetworkAddress.getDefaultInetAddress();
        try {
            serverSocket = createServerSocket(inetAddr);
        } catch (IOException ex) {
            AppXrw.logger.warning("Cannot create server socket for master for app " + appName);
            cleanup();
            throw new InstantiationException();
        }
        int portNum = serverSocket.getLocalPort();
        connectionInfo = new AppXrwConnectionInfo(publicMasterHost, portNum);

        // Create the Xremwin protocol client.
        client = null;
        try {
            client = new ClientXrwMaster(this, controlArb, session, cellID, masterHost, serverSocket,
                    winSys, reporter);
            ((ClientXrwMaster)client).setExitListener(this);
        } catch (InstantiationException ex) {
            AppXrw.logger.warning("Cannot launch " + appInstanceName + ": Cannot create Xremwin protocol client");
            cleanup();
            throw new InstantiationException();
        }

        // Launch the app (this must be done after the Xremwin protocol client
        // has been started).
        String displayName = winSys.getDisplayName();
        HashMap<String, String> env = new HashMap<String, String>();
        env.put("DISPLAY", displayName);
        appProcess = new MonitoredProcess(appInstanceName, command, env, reporter);
        if (!appProcess.start()) {
            AppXrw.logger.warning("Cannot launch " + appInstanceName + ": Cannot start application process: " +
                    command);
            cleanup();
            throw new InstantiationException();
        }
        synchronized (masterApps) {
            masterApps.add(this);
        }
    }

    /**
     * Specify a listener which is called when the app exits. 
     */
    public void setExitListener (ExitListener exitListener) {
        this.exitListener = exitListener;
    }

    /**
     * Find a valid server socket with the range specified by the
     * wl.appshare.minPort and wl.appshare.maxPort values.
     * @param inetAddr the address to open a socket on
     * @return a server socket bound to an open port with the given
     * port range
     * @throws IOException if no ports are available in the given range
     */
    protected ServerSocket createServerSocket(InetAddress addr)
            throws IOException {
        // read properties
        String minPortStr = System.getProperty("wonderland.appshare.minPort");
        String maxPortStr = System.getProperty("wonderland.appshare.maxPort");

        if (minPortStr == null || maxPortStr == null) {
            // no values specified
            return new ServerSocket(0, 0, addr);
        }

        logger.fine("Searching for port between " + minPortStr + " and " +
                maxPortStr);

        // parse ports into a range
        int minPort = Integer.parseInt(minPortStr);
        int maxPort = Integer.parseInt(maxPortStr);

        // Find a valid socket in the range.  We choose a random port in
        // the range, and record the ones we hit that are bad.  This is
        // better than just going in order for smaller numbers of connections,
        // but might take a long time for longer collections.
        int rangeSize = maxPort - minPort;
        Set<Integer> tried = new TreeSet<Integer>();
        while (tried.size() < rangeSize) {
            // generate a random number in the given range
            int port = minPort + (int) (Math.random() * rangeSize);

            // check if we have already tried it
            if (!tried.contains(new Integer(port))) {
                try {
                    return new ServerSocket(port, 0, addr);
                } catch (SocketException se) {
                    // port in use, just record it and go on
                    tried.add(new Integer(port));
                }
            }
        }

        // no ports available -- throw an exception
        throw new IOException("No ports available in range " + minPort +
                " - " + maxPort + ".");
    }

    /**
     * Clean up resources.
     */
    @Override
    public void cleanup() {
        super.cleanup();

        client = null;
        winSys = null;

        if (appProcess != null) {
            exitValue = appProcess.getExitValue();
            appProcess.cleanup();
            appProcess = null;
        }

        deallocAppInstance(getName(), appInstance);

        synchronized (masterApps) {
            masterApps.remove(this);
        }

        logger.info("AppXrwMaster stopped.");

        if (exitListener != null) {
            exitListener.appExitted(this);
        }
    }

    /**
     * Returns the connection info.
     */
    public AppXrwConnectionInfo getConnectionInfo() {
        return connectionInfo;
    }

    /** 
     * This method is called when the master client has exitted.
     */
    @Override
    public void clientExitted(ClientXrwMaster client) {
        logger.severe("Master client exitted");
        cleanup();
    }

    /**
     * Allocate the next available unique instance number for an app name.
     * (Instance numbers start at 1).
     *
     * @param appName The name of the app.
     * @return A number which is unique among apps with the same name.
     */
    private static int allocAppInstance(String appName) {
        SmallIntegerAllocator allocator = instanceAllocators.get(appName);
        if (allocator == null) {
            // First time allocation for app name
            allocator = new SmallIntegerAllocator(1);
            instanceAllocators.put(appName, allocator);
        }
        return allocator.allocate();
    }

    /**
     * Return the given instance number to the app name's pool of instances.
     *
     * @param appName The name of the app.
     * @param appInstance An app instance previous allocated by allocAppInstance.
     */
    private static void deallocAppInstance(String appName, int appInstance) {
        SmallIntegerAllocator allocator = instanceAllocators.get(appName);
        if (allocator == null) {
            throw new RuntimeException("Deallocation when app instance is not allocated = " +
                    appInstance);
        }

        allocator.free(appInstance);

        if (allocator.getNumAllocated() == 0) {
            instanceAllocators.remove(appName);
        }
    }

    /** 
     * The window title of Master window has changed.
     *
     * @param wid The X window ID of the window.
     * @param String The new window title.
     */
    @Override
    public void setWindowTitle(int wid, String windowTitle) {
        // Relay window title to master client
        ((ClientXrwMaster) client).setWindowTitle(wid, windowTitle);
    }

    /** {@inheritDoc} */
    public boolean isMaster () {
        return true;
    }

    /**
     * Inform all slaves that the given popup has the specified parent.
     */
    public void setPopupParentForSlaves (WindowXrw popup, WindowXrw parent) {
        ((ClientXrwMaster)client).setPopupParent(popup, parent);
    }

    /** Executed by the JVM shutdown process. */
    private static void shutdownAllApps () {
        if (masterApps.size() > 0) {
            logger.warning("Shutting down X11 app base master apps...");

            // TODO: low: workaround for bug 205. This is draconian. Is there something else better? 
            try {
                Runtime.getRuntime().exec("pkill -9 Xvfb-xremwin");
            } catch (Exception e) {}
        }
    }

    /**
     * Returns the exit value of the app process. A return value >= 0 indicates that the process
     * has exitted and returned an exit value. The return value is this exit value. 
     * <br><br>
     * A return value of -1 indicates that the process is still running, so no exit value is available.
     * <br>
     * A return value of -2 indicates that the process is no longer running, but for some reason
     * its exit value is not available.
     */
    public int getExitValue () {
        if (appProcess != null) {
            return appProcess.getExitValue();
        }
        return exitValue;
    }
}
