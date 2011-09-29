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
package org.jdesktop.wonderland.testharness.slave.client3D;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.ClientPlugin;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.Cell.RendererType;
import org.jdesktop.wonderland.client.cell.CellCache;
import org.jdesktop.wonderland.client.cell.CellCacheBasicImpl;
import org.jdesktop.wonderland.client.cell.CellRenderer;
import org.jdesktop.wonderland.client.cell.MovableComponent.CellMoveListener;
import org.jdesktop.wonderland.client.cell.MovableComponent.CellMoveSource;
import org.jdesktop.wonderland.client.cell.view.LocalAvatar;
import org.jdesktop.wonderland.client.cell.view.LocalAvatar.ViewCellConfiguredListener;
import org.jdesktop.wonderland.client.comms.SessionStatusListener;
import org.jdesktop.wonderland.client.comms.WonderlandServerInfo;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.comms.WonderlandSession.Status;
import org.jdesktop.wonderland.client.comms.CellClientSession;
import org.jdesktop.wonderland.client.comms.LoginFailureException;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.client.jme.MainFrame;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.client.login.LoginUI;
import org.jdesktop.wonderland.client.login.PluginFilter;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.client.login.ServerSessionManager.EitherLoginControl;
import org.jdesktop.wonderland.client.login.ServerSessionManager.NoAuthLoginControl;
import org.jdesktop.wonderland.client.login.ServerSessionManager.UserPasswordLoginControl;
import org.jdesktop.wonderland.client.login.SessionCreator;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.testharness.common.Client3DRequest;
import org.jdesktop.wonderland.testharness.common.TestRequest;
import org.jdesktop.wonderland.testharness.slave.ProcessingException;
import org.jdesktop.wonderland.testharness.slave.RequestProcessor;
import org.jdesktop.wonderland.testharness.slave.SlaveMain.ReplySender;

/**
 * A test client that simulates a 3D client
 */
public class Client3DSim
        implements RequestProcessor, SessionStatusListener {

    /** a logger */
    private static final Logger logger =
            Logger.getLogger(Client3DSim.class.getName());
    private static final Logger messageTimerLogger =
            Logger.getLogger(MessageTimer.class.getName());
    /** the name of this client */
    private String username;
    /** the session we are attached to */
    private CellClientSession session;
    /** the mover thread */
    private UserSimulator userSim;
    private MessageTimer messageTimer = new MessageTimer();

    public Client3DSim() {
    }

    public String getName() {
        return "Client3DSim";
    }

    public void initialize(String username, Properties props, ReplySender replyHandler)
            throws ProcessingException {
        this.username = username;

        // set the user directory to one specific to this client
        File userDir = new File(ClientContext.getUserDirectory("test"),
                username);
        ClientContext.setUserDirectory(userDir);
        ClientContext.setRendererType(RendererType.NONE);

        // set up the login system to

        // read the server URL from a property
        String serverURL = props.getProperty("serverURL");
        if (serverURL == null) {
            throw new ProcessingException("No serverURL found");
        }

        // set the login callback to give the right user name
        LoginManager.setLoginUI(new ClientSimLoginUI(username, props));

        // for now, load all plugins.  We should modify this to only load
        // some plugins, depending on the test
        LoginManager.setPluginFilter(new BlacklistPluginFilter());

        // create a fake mainframe
        JmeClientMain.setFrame(new FakeMainFrame());

        try {        
            ServerSessionManager mgr = LoginManager.getSessionManager(serverURL);
            session = mgr.createSession(new SessionCreator<CellClientSession>() {

                public CellClientSession createSession(ServerSessionManager sessionMgr,
                        WonderlandServerInfo serverInfo, ClassLoader loader) {
                    CellClientSession ccs = new CellClientSession(sessionMgr, serverInfo, loader) {

                        @Override
                        protected CellCache createCellCache() {
                            CellCacheBasicImpl impl = new CellCacheBasicImpl(this,
                                    getClassLoader(), getCellCacheConnection(),
                                    getCellChannelConnection()) {

                                @Override
                                protected CellRenderer createCellRenderer(Cell cell) {
                                    return null;
                                }
                            };

                            getCellCacheConnection().addListener(impl);
                            return impl;
                        }
                    };
                    ccs.addSessionStatusListener(Client3DSim.this);

                    final LocalAvatar avatar = ccs.getLocalAvatar();
                    avatar.addViewCellConfiguredListener(new ViewCellConfiguredListener() {

                        public void viewConfigured(LocalAvatar localAvatar) {
//                            MovableComponent mc =
//                                    avatar.getViewCell().getComponent(MovableComponent.class);
//                            mc.addServerCellMoveListener(messageTimer);

                            // start the simulator
                            userSim.start();
                        }
                    });
                    userSim = new UserSimulator(avatar);

                    return ccs;
                }
            });
        } catch (IOException ioe) {
            throw new ProcessingException(ioe);
        } catch (LoginFailureException lfe) {
            lfe.printStackTrace();
            throw new ProcessingException(lfe);
        }
    }

    public void destroy() {
        if (session != null) {
            session.logout();
        }
    }

    public void processRequest(TestRequest request) {
        if (request instanceof Client3DRequest) {
            processClient3DRequest((Client3DRequest) request);
        } else {
            Logger.getAnonymousLogger().severe("Unsupported request " + request.getClass().getName());
        }
    }

    private void processClient3DRequest(Client3DRequest request) {
        switch (request.getAction()) {
            case WALK:
                userSim.walkLoop(request.getDesiredLocations(), new Vector3f(1f, 0f, 0f), request.getSpeed(), request.getLoopCount());
                break;
            default:
                Logger.getAnonymousLogger().severe("Unsupported Client3DRequest " + request.getAction());
        }
    }

    public String getUsername() {
        return username;
    }

    public void sessionStatusChanged(WonderlandSession session,
            Status status) {
        logger.info(getName() + " change session status: " + status);
        if (status == Status.DISCONNECTED && userSim != null) {
            userSim.quit();
        }
    }

    public void waitForFinish() throws InterruptedException {
        if (userSim == null) {
            return;
        }

        // wait for the thread to end
        userSim.join();
    }

    /**
     * A very basic UserSimulator, this really needs a lot of attention.....
     */
    class UserSimulator extends Thread {

        private Vector3f currentLocation = new Vector3f();
        private Vector3f[] desiredLocations;
        private int locationIndex;
        private Vector3f step = null;
        private float speed;
        private Quaternion orientation = null;
        private LocalAvatar avatar;
        private boolean quit = false;
        private boolean walking = false;
        private long sleepTime = 500; // Time between steps (in ms)
        private int currentLoopCount = 0;
        private int desiredLoopCount;
        private Semaphore semaphore;

        public UserSimulator(LocalAvatar avatar) {
            super("UserSimulator");
            this.avatar = avatar;
            semaphore = new Semaphore(0);
        }

        public synchronized boolean isQuit() {
            return quit;
        }

        public synchronized void quit() {
            this.quit = true;
        }

        @Override
        public void run() {
            // Set initial position
            avatar.localMoveRequest(currentLocation, orientation);

            while (!quit) {
                try {
                    semaphore.acquire();
                } catch (InterruptedException ex) {
                    Logger.getLogger(Client3DSim.class.getName()).log(Level.SEVERE, null, ex);
                }

                while (!quit && walking) {
                    if (currentLocation.subtract(desiredLocations[locationIndex]).lengthSquared() < 0.1) {   // Need epsilonEquals
                        if (locationIndex < desiredLocations.length - 1) {
                            locationIndex++;

                            step = desiredLocations[locationIndex].subtract(currentLocation);
                            step.multLocal(speed / (1000f / sleepTime));

                        } else if (locationIndex == desiredLocations.length - 1 && desiredLoopCount != currentLoopCount) {
                            currentLoopCount++;
                            locationIndex = 0;

                            step = desiredLocations[locationIndex].subtract(currentLocation);
                            step.multLocal(speed / (1000f / sleepTime));
                        } else {
                            walking = false;
                        }
                    }

                    if (walking) {
                        currentLocation.addLocal(step);
                        avatar.localMoveRequest(currentLocation, orientation);
//                        messageTimer.messageSent(new CellTransform(orientation, currentLocation));
                    }

                    try {
                        sleep(sleepTime);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Client3DSim.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }

        /**
         * Walk  from the current location to the new location specified, and
         * orient to the give look direction
         * @param location
         * @param lookDirection
         * @param speed in meters/second
         */
        void walkLoop(Vector3f[] locations, Vector3f lookDirection, float speed, int loopCount) {
            this.speed = speed;
            locationIndex = 0;
            desiredLocations = locations;
            desiredLoopCount = loopCount;
            currentLoopCount = 0;

            step = new Vector3f(desiredLocations[0]);
            step.subtractLocal(currentLocation);
            step.multLocal(speed / (1000f / sleepTime));

            walking = true;
            semaphore.release();
        }

        /**
         * Send audio data to the server
         * 
         * TODO implement
         */
        public void talk() {
        }
    }

    /**
     * Measure the time between us sending a move request to the server and the server 
     * sending the message back to us.
     */
    class MessageTimer implements CellMoveListener {

        private long timeSum = 0;
        private long lastReport;
        private int count = 0;
        private long min = Long.MAX_VALUE;
        private long max = 0;
        private static final long REPORT_INTERVAL = 5000; // Report time in ms
        private LinkedList<TimeRecord> messageTimes = new LinkedList();

        public MessageTimer() {
            lastReport = System.nanoTime();
        }

        /**
         * Callback for messages from server
         * @param arg0
         * @param arg1
         */
        public void cellMoved(CellTransform transform, CellMoveSource moveSource) {
            if (messageTimes.size() != 0 && messageTimes.getFirst().transform.equals(transform)) {
                TimeRecord rec = messageTimes.removeFirst();

                long time = ((System.nanoTime()) - rec.sendTime) / 1000000;

                min = Math.min(min, time);
                max = Math.max(max, time);
                timeSum += time;
                count++;

                if (System.nanoTime() - lastReport > REPORT_INTERVAL * 1000000) {
                    long avg = timeSum / count;
                    messageTimerLogger.info("Roundtrip time avg " + avg + "ms " + username + " min " + min + " max " + max);
                    timeSum = 0;
                    lastReport = System.nanoTime();
                    count = 0;
                    min = Long.MAX_VALUE;
                    max = 0;
                }
            } else {
                logger.warning("No Time record for " + transform.getTranslation(null) + " queue size " + messageTimes.size());
//                if (messageTimes.size()!=0)
//                    logger.warning("HEAD "+messageTimes.getFirst().transform.getTranslation(null));
            }
        }

        public void messageSent(CellTransform transform) {
            messageTimes.add(new TimeRecord(transform, System.nanoTime()));
        }
    }

    class TimeRecord {

        private CellTransform transform;
        private long sendTime;

        public TimeRecord(CellTransform transform, long sendTime) {
            this.transform = transform;
            this.sendTime = sendTime;
        }
    }

    class ClientSimLoginUI implements LoginUI {

        private String username;
        private Properties props;

        public ClientSimLoginUI(String username, Properties props) {
            this.username = username;
            this.props = props;
        }

        public void requestLogin(NoAuthLoginControl control) {
            String fullname = props.getProperty("fullname");
            if (fullname == null) {
                fullname = username;
            }

            try {
                control.authenticate(username, fullname);
            } catch (LoginFailureException lfe) {
                logger.log(Level.WARNING, "Login failed", lfe);
                control.cancel();
            }
        }

        public void requestLogin(UserPasswordLoginControl control) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void requestLogin(EitherLoginControl control) {
            requestLogin(control.getNoAuthLogin());
        }
    }

    class FakeMainFrame implements MainFrame {

        private JFrame frame;
        private JPanel canvasPanel;
        private Canvas canvas;

        public FakeMainFrame() {
            try {
                frame = new JFrame();
            } catch (HeadlessException he) {
                // ignore
                logger.log(Level.INFO, "Running in headless mode");
            }
            canvasPanel = new JPanel(new BorderLayout());
            canvas = new Canvas();

            canvasPanel.add(canvas, BorderLayout.CENTER);

            if (frame != null) {
                frame.setContentPane(canvasPanel);
            }
        }

        public JFrame getFrame() {
            return frame;
        }

        public Canvas getCanvas() {
            return canvas;
        }

        public JPanel getCanvas3DPanel() {
            return canvasPanel;
        }

        public void setMessageLabel(String msg) {
            //ignore
        }

        /**
         * {@inheritDoc}
         */
        public void addToMenu(JMenu menu, JMenuItem menuItem, int index) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        public void addToFileMenu(JMenuItem menuItem) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        public void addToFileMenu(JMenuItem menuItem, int index) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        public void addToEditMenu(JMenuItem menuItem) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        public void addToEditMenu(JMenuItem menuItem, int index) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        public void addToViewMenu(JMenuItem menuItem) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        public void addToViewMenu(JMenuItem menuItem, int index) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        public void addToToolsMenu(JMenuItem menuItem) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        public void addToToolsMenu(JMenuItem menuItem, int index) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        public void addToPlacemarksMenu(JMenuItem menuItem) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        public void addToPlacemarksMenu(JMenuItem menuItem, int index) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        public void addToWindowMenu(JMenuItem menuItem) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        public void addToWindowMenu(JMenuItem menuItem, int index) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        public void addToHelpMenu(JMenuItem menuItem) {
            // ignore
        }

        /**
         * {@inheritDoc}
         */
        public void addToHelpMenu(JMenuItem menuItem, int index) {
            // ignore
        }

        public void setServerURL(String serverURL) {
            // ignore
        }

        public void addServerURLListener(ServerURLListener listener) {
            // ignore
        }

        public void removeFromFileMenu(JMenuItem menuItem) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void removeFromEditMenu(JMenuItem menuItem) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void removeFromViewMenu(JMenuItem menuItem) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void removeFromToolsMenu(JMenuItem menuItem) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void removeFromPlacemarksMenu(JMenuItem menuItem) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void removeFromWindowMenu(JMenuItem menuItem) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void addToInsertMenu(JMenuItem menuItem) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void addToInsertMenu(JMenuItem menuItem, int index) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void removeFromInsertMenu(JMenuItem menuItem) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void connected(boolean connected) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void setDesiredFrameRate(int desiredFrameRate) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void addToCameraChoices(JRadioButtonMenuItem cameraMenuItem, int index) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void removeFromCameraChoices(JRadioButtonMenuItem menuItem) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        public void removeFromHelpMenu(JMenuItem menuItem) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    class BlacklistPluginFilter implements PluginFilter {

        private final String[] JAR_BLACKLIST = {
            "ant",
            "ant-launcher",
            "artimport-client",
            "audiomanager-client",
            "avatarbase-client",
            "defaultenvironment-client",
            "kmzloader-client",
            "contextmenu"
        };
        private final String[] CLASS_BLACKLIST = {};

        public boolean shouldDownload(ServerSessionManager sessionManager, URL jarURL) {
            String urlPath = jarURL.getPath();
            int idx = urlPath.lastIndexOf("/");
            if (idx != -1) {
                urlPath = urlPath.substring(idx);
            }

            for (String check : JAR_BLACKLIST) {
                if (urlPath.contains(check)) {
                    return false;
                }
            }

            return true;
        }

        public boolean shouldInitialize(ServerSessionManager sessionManager,
                ClientPlugin plugin) {
            for (String check : CLASS_BLACKLIST) {
                if (plugin.getClass().getName().equals(check)) {
                    return false;
                }
            }

            return true;
        }
    }
}
