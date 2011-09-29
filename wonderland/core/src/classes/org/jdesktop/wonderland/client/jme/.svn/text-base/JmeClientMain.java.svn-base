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
package org.jdesktop.wonderland.client.jme;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.sun.scenario.animation.Clip;
import com.sun.scenario.animation.Interpolators;
import com.sun.scenario.animation.TimingTarget;
import org.jdesktop.wonderland.client.comms.WonderlandSession.Status;
import org.jdesktop.wonderland.client.jme.login.JmeLoginUI;
import java.io.File;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jdesktop.mtgame.CameraComponent;
import org.jdesktop.mtgame.CollisionManager;
import org.jdesktop.mtgame.CollisionSystem;
import org.jdesktop.mtgame.JBulletDynamicCollisionSystem;
import org.jdesktop.mtgame.JBulletPhysicsSystem;
import org.jdesktop.mtgame.JMECollisionSystem;
import org.jdesktop.mtgame.PhysicsManager;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.mtgame.processor.WorkProcessor.WorkCommit;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.assetmgr.AssetDB;
import org.jdesktop.wonderland.client.assetmgr.AssetDBException;
import org.jdesktop.wonderland.client.cell.view.AvatarCell;
import org.jdesktop.wonderland.client.cell.view.ViewCell;
import org.jdesktop.wonderland.common.ThreadManager;
import org.jdesktop.wonderland.client.comms.LoginFailureException;
import org.jdesktop.wonderland.client.comms.SessionStatusListener;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.input.InputManager;
import org.jdesktop.wonderland.client.jme.MainFrame.ServerURLListener;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.common.LogControl;
/* For Testing FocusEvent3D
import org.jdesktop.wonderland.client.jme.input.FocusEvent3D;
import org.jdesktop.wonderland.client.jme.input.InputManager3D;
 */

/**
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class JmeClientMain {

    public static final String SERVER_URL_PROP = "sgs.server";
    private static final Logger LOGGER =
            Logger.getLogger(JmeClientMain.class.getName());
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/client/jme/resources/Bundle");

    /** Default window size */
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;

    /** The frame of the Wonderland client window. */
    private static MainFrame frame;
    // standard properties
    private static final String PROPS_URL_PROP = "run.properties.file";
    private static final String CONFIG_DIR_PROP =
            "wonderland.client.config.dir";
    private static final String DESIRED_FPS_PROP = "wonderland.client.fps";
    private static final String WINDOW_SIZE_PROP =
            "wonderland.client.windowSize";
    // default values
    private static final String SERVER_URL_DEFAULT = "http://localhost:8080";
    private static final String DESIRED_FPS_DEFAULT = "30";
    private static final String WINDOW_SIZE_DEFAULT = "800x600";
  
    // the current Wonderland login and session
    private JmeLoginUI login;
    private JmeClientSession curSession;
    // keep tack of whether we are currently logging out
    private boolean loggingOut;
    // whether we should try to auto-reconnect
    private boolean autoReconnect = true;


    // log uncaught exceptions
    private static final UncaughtExceptionHandler ueh =
            new UncaughtExceptionHandler()
    {
        public void uncaughtException(Thread t, Throwable e) {
            LOGGER.log(Level.WARNING, "Uncaught exception", e);
        }
    };

    private enum OS {

        Linux, Windows, OSX, Other
    }
    private OS os;

    /**
     * creates a new JmeClientMain
     * @param args the command line arguments
     */
    public JmeClientMain(String[] args) {
        detectOS();

        checkVmVersion();

        // process command line arguments
        processArgs(args);

        // load properties in a properties file
        URL propsURL = getPropsURL();
        loadProperties(propsURL);

        // Check whether there is another JVM processing running that is
        // attached to the database. Note we need to do this check AFTER the
        // client properties are loaded, otherwise, the wrong user directory
        // is used.
        checkDBException();

        // set up the context
        ClientContextJME.setClientMain(this);

        String windowSize = System.getProperty(
                WINDOW_SIZE_PROP, WINDOW_SIZE_DEFAULT);
        
        int width = DEFAULT_WIDTH;
        int height = DEFAULT_HEIGHT;

        try {
            if (windowSize.equalsIgnoreCase("fullscreen")) {
                // OWL issue #146: for full screen, let the frame figure out
                // how to size itself
                width = -1;
                height = -1; // -50 hack for current swing decorations
            } else {
                String sizeWidth =
                        windowSize.substring(0, windowSize.indexOf('x'));
                String sizeHeight =
                        windowSize.substring(windowSize.indexOf('x') + 1);
                width = Integer.parseInt(sizeWidth);
                height = Integer.parseInt(sizeHeight);
            }
        } catch (Exception e) {
            LOGGER.warning(WINDOW_SIZE_PROP
                    + " error, should be of the form 640x480 (or fullscreen), "
                    + "instead of the current " + windowSize);
        }

        // make sure the server URL is set
        String serverURL = System.getProperty(SERVER_URL_PROP);
        if (serverURL == null) {
            serverURL = SERVER_URL_DEFAULT;
            System.setProperty(SERVER_URL_PROP, serverURL);
        }

        // HUGE HACK ! Force scenario to initialize so menus work correctly
        // (work around for scenario bug)
        Clip clip2 = Clip.create(1000, new TimingTarget() {

            public void timingEvent(float arg0, long arg1) {
            }

            public void begin() {
            }

            public void end() {
            }

            public void pause() {
            }

            public void resume() {
            }
        });
        clip2.setInterpolator(Interpolators.getEasingInstance(0.4f, 0.4f));
        clip2.start();
        // End HUGE HACK.

        WorldManager worldManager = ClientContextJME.getWorldManager();
        worldManager.getRenderManager().setDesiredFrameRate(getDesiredFrameRate());

        createUI(worldManager, width, height);

        // Register our loginUI for login requests
        login = new JmeLoginUI(frame);
        LoginManager.setLoginUI(login);

        // add a listener that will be notified when the user selects a new
        // server
        frame.addServerURLListener(new ServerURLListener() {

            public void serverURLChanged(final String serverURL) {
                // run in a new thread so we don't block the AWT thread
                new Thread(ThreadManager.getThreadGroup(), new Runnable() {

                    public void run() {
                        try {
                            loadServer(serverURL);
                        } catch (IOException ioe) {
                            LOGGER.log(Level.WARNING, "Error connecting to "
                                    + serverURL, ioe);
                        }
                    }
                }).start();
            }

            public void logout() {
                new Thread(ThreadManager.getThreadGroup(), new Runnable() {

                    public void run() {
                        JmeClientMain.this.logout();
                    }
                }).start();
            }
        });

//        JMenuItem physicsMI = new JCheckBoxMenuItem(
//                BUNDLE.getString("Physics Enabled"));
//        physicsMI.setEnabled(false);
//        physicsMI.setSelected(false);
//        physicsMI.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent e) {
//                PhysicsSystem phySystem = ClientContextJME.getPhysicsSystem(
//                        curSession.getSessionManager(), "Default");
//                if (phySystem instanceof JBulletPhysicsSystem) {
//                    ((JBulletPhysicsSystem) phySystem).setStarted(
//                            ((JCheckBoxMenuItem) e.getSource()).isSelected());
//                } else {
//                    LOGGER.severe("Unsupported physics system " + phySystem);
//                }
//            }
//        });
//        frame.addToEditMenu(physicsMI, 3);

        // load the starting coordinates and look direction
        float startX = Float.parseFloat(System.getProperty("x", "0"));
        float startY = Float.parseFloat(System.getProperty("y", "0"));
        float startZ = Float.parseFloat(System.getProperty("z", "0"));
        Vector3f startLoc = new Vector3f(startX, startY, startZ);

        float look = Float.parseFloat(System.getProperty("look",  "0"));
        Quaternion startLook = new Quaternion(
                new float[] { 0f, (float) Math.toRadians(look), 0f });

        // connect to the default server
        try {
            loadServer(serverURL, startLoc, startLook);
        } catch (IOException ioe) {
            LOGGER.log(Level.WARNING, "Error connecting to default server "
                    + serverURL, ioe);
        }
    }

    /**
     * Return whether or not we should auto-reconnect on disconnect
     * @return true if auto reconnect is enabled, or false if not
     */
    public synchronized boolean getAutoReconnect() {
        return autoReconnect;
    }

    /**
     * Set whether or not we should auto-reconnect on disconnect
     * @param autoReconnect true to reconnect automatically, or false not to
     */
    public synchronized void setAutoReconnect(boolean autoReconnect) {
        this.autoReconnect = autoReconnect;
    }

    /**
     * Move the client to the given location
     * @param serverURL the url of the server to go to, or null to stay
     * on the current server
     * @param translation the translation
     * @param look the direction to look in, or null to look in the default
     * direction
     * @throws IOException if there is an error going to the new location
     */
    public void gotoLocation(String serverURL, final Vector3f translation,
            final Quaternion look)
            throws IOException {
        if (serverURL == null) {
            // get the server from the current session
            if (curSession == null) {
                throw new IllegalStateException("No server");
            }

            serverURL = curSession.getSessionManager().getServerURL();
        } else {
            URL fullServerURL = new URL(serverURL);
            if (fullServerURL.getFile().isEmpty()) {
                //Missing trailing '/' from server URL
                serverURL = serverURL + '/';
            }
        }

        // see if we need to change servers
        // issue #859 - compare URLs as URLs
        if (curSession != null && urlEquals(serverURL,
                curSession.getSessionManager().getServerURL())) {
            // no need to change - make a local move request
            ViewCell vc = curSession.getLocalAvatar().getViewCell();
            if (vc instanceof AvatarCell) {
                ((AvatarCell) vc).triggerGoto(translation, look);
            }

        } else {
            // issue #859 - load the server in a separate thread to
            // guarantee it won't get loaded in the awt event thread
            ServerLoader sl = new ServerLoader(serverURL, translation, look);
            Thread t = new Thread(sl);
            t.start();

            // wait for the thread to finish
            try {
                t.join();
            } catch (InterruptedException ex) {
                LOGGER.log(Level.WARNING, "Join interrupted", ex);
            }

            // see if there was an exception
            if (sl.getException() != null) {
                throw sl.getException();
            }
        }
    }

    protected void loadServer(String serverURL) throws IOException {
        loadServer(serverURL, null, null);
    }

    protected void loadServer(String serverURL, Vector3f translation,
            Quaternion look)
            throws IOException {
        LOGGER.info("[JmeClientMain] loadServer " + serverURL);

        logout();

        // get the login manager for the given server
        ServerSessionManager lm = LoginManager.getSessionManager(serverURL);

        // Register physics and phyiscs collision systems for this session
        WorldManager worldManager = ClientContextJME.getWorldManager();
        CollisionManager collisionManager = worldManager.getCollisionManager();
        CollisionSystem collisionSystem = collisionManager.loadCollisionSystem(
                JBulletDynamicCollisionSystem.class);
        JBulletDynamicCollisionSystem jBulletCollisionSystem =
                (JBulletDynamicCollisionSystem) collisionSystem;
        PhysicsManager physicsManager = worldManager.getPhysicsManager();
        JBulletPhysicsSystem jBulletPhysicsSystem =
                (JBulletPhysicsSystem) physicsManager.loadPhysicsSystem(
                JBulletPhysicsSystem.class, jBulletCollisionSystem);
        ClientContextJME.addCollisionSystem(
                lm, "Physics", jBulletCollisionSystem);
        ClientContextJME.addPhysicsSystem(lm, "Physics", jBulletPhysicsSystem);

        // Register default collision system for this session
        JMECollisionSystem jmeCollisionSystem =
                (JMECollisionSystem) collisionManager.loadCollisionSystem(
                JMECollisionSystem.class);
        ClientContextJME.addCollisionSystem(lm, "Default", jmeCollisionSystem);

        // set the initial position, which will bne sent with the initial
        // connection properties of the cell cache connection
        login.setInitialPosition(translation, look);

        // OWL issue #185: set this manager as primary before creating any
        // connectons (but after it is properly initialized)
        login.setPrimary(true);
        
        // create a new session
        try {
            curSession = lm.createSession(login);
        } catch (LoginFailureException lfe) {
            IOException ioe = new IOException("Error connecting to "
                    + serverURL);
            ioe.initCause(lfe);
            throw ioe;
        }

        // make sure we logged in successfully
        if (curSession == null) {
            LOGGER.log(Level.WARNING, "Unable to connect to session");
            return;
        }

        frame.connected(true);

        // Listen for session disconnected and remove session physics and
        // collision systems
        curSession.addSessionStatusListener(new SessionStatusListener() {

            public void sessionStatusChanged(
                    WonderlandSession session, Status status) {
                if (status == Status.DISCONNECTED) {
                    ServerSessionManager serverSessionManager =
                            session.getSessionManager();
                    ClientContextJME.removeAllPhysicsSystems(
                            serverSessionManager);
                    ClientContextJME.removeAllCollisionSystems(
                            serverSessionManager);

                    // update the UI for logout
                    boolean inLogout;
                    synchronized (JmeClientMain.this) {
                        inLogout = loggingOut;
                    }

                    if (!inLogout) {
                        // if we didn't initiate the logout through the
                        // logout() method, then this is an unexpected
                        // logout.  Clean up by calling the logout() method,
                        // then attempt to reconnect
                        // reconnect dialog
                        final ServerSessionManager mgr =
                                curSession.getSessionManager();

                        LOGGER.warning("[JmeClientMain] unexpected logout!");

                        logout();

                        if (getAutoReconnect()) {
                            SwingUtilities.invokeLater(new Runnable() {

                                public void run() {
                                    ReconnectDialog rf = new ReconnectDialog(
                                            JmeClientMain.this, mgr);
                                    rf.setVisible(true);
                                }
                            });
                        } else {
                            // reset auto reconnect
                            setAutoReconnect(true);
                        }

                    } else {
                        synchronized (JmeClientMain.this) {
                            loggingOut = false;
                        }
                    }
                }
            }
        });

        // set the primary session
        lm.setPrimarySession(curSession);
        frame.setServerURL(serverURL);
  
        // OWL issue #92: request focus in main window
        frame.getCanvas().requestFocusInWindow();
    }

    /**
     * logs out
     */
    protected void logout() {
        LOGGER.info("[JMEClientMain] log out");

        // disconnect from the current session
        if (curSession != null) {
            if (curSession.getStatus() == Status.CONNECTED) {
                synchronized (this) {
                    loggingOut = true;
                }
            }

            curSession.getCellCache().unloadAll();

            curSession.logout();
            curSession = null;
            frame.connected(false);

            // notify listeners that there is no longer a primary server
            LoginManager.setPrimary(null);
        }
    }

    /**
     * Compare two Strings as URLs.
     * @param url1 the first URL to compare
     * @param url2 the second URL to compare
     * @return true if the URLs match
     */
    private boolean urlEquals(String url1, String url2) {
        try {
            URL u1 = new URL(url1);
            URL u2 = new URL(url2);

            System.out.println("Compare " + url1 + " to " + url2 + ": "
                    + u1.equals(u2));

            return u1.equals(u2);
        } catch (MalformedURLException mue) {
            LOGGER.log(Level.WARNING, "Comparing non URL: " + url1 + ", "
                    + url2, mue);
            return url1.equalsIgnoreCase(url2);
        }
    }

    /**
     * returns the properties URL
     * @return the properties URL
     */
    protected URL getPropsURL() {
        String propURLStr = System.getProperty(PROPS_URL_PROP);
        try {
            URL propsURL;

            if (propURLStr == null) {
                String configDir = System.getProperty(CONFIG_DIR_PROP);
                if (configDir == null) {
                    File userDir = new File(System.getProperty("user.dir"));
                    configDir = userDir.toURI().toURL().toString();
                }

                // use the default
                URL configDirURL = new URL(configDir);
                propsURL = new URL(configDirURL, "run-client.properties");
            } else {
                propsURL = new URL(propURLStr);
            }

            return propsURL;
        } catch (IOException ioe) {
            LOGGER.log(Level.WARNING, "Unable to load properties", ioe);
            return null;
        }
    }

    /**
     * Get the current desired framerate
     * @return the current framerate
     */
    static int getDesiredFrameRate() {
        Preferences prefs = Preferences.userNodeForPackage(JmeClientMain.class);
        
        // first try a preference
        String requestedFPS = null;
        try {
            if (prefs.node(prefs.absolutePath()).get("fps", null) != null) {
                requestedFPS = prefs.get("fps", DESIRED_FPS_DEFAULT);
            }
        } catch (Exception e) {
        }
        
        // if the preference was not set, use a system property
        if (requestedFPS == null) {
            requestedFPS = System.getProperty(DESIRED_FPS_PROP, DESIRED_FPS_DEFAULT);
        }

        try {
            return Integer.parseInt(requestedFPS);
        } catch (NumberFormatException e) {
            LOGGER.warning(DESIRED_FPS_PROP + " property format error for '" + 
                           requestedFPS + "', using default");
            return Integer.parseInt(DESIRED_FPS_DEFAULT);
        }
    }
    
    /**
     * Set the current desired framerate
     * @param frameRate the current desired framerate
     */
    static void setDesiredFrameRate(int frameRate) {
        Preferences prefs = Preferences.userNodeForPackage(JmeClientMain.class);
        prefs.put("fps", String.valueOf(frameRate));
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Check for the -b benchmark/test harness arg and process.
        // This need to happen very early because it sets the user dir
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("-b ")) {
                TestHarnessSupport.processCommandLineArgs(args[i]);
            }
        }

        // set up logging
        new LogControl(JmeClientMain.class,
                "/org/jdesktop/wonderland/client/jme/resources/"
                + "logging.properties");

        // set up our custom log handler to pipe messages to the LogViewerFrame.
        // We need to do this to work around the fact that Web Start won't
        // load loggers not on the system classpath
        Logger rootLogger = Logger.getLogger("");
        rootLogger.addHandler(new LogViewerHandler());

        // make sure Swing exceptions are captured in the log
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Thread.currentThread().setUncaughtExceptionHandler(ueh);
            }
        });
        
        // try our best to log any uncaught exceptions
        Thread.setDefaultUncaughtExceptionHandler(ueh);

        if (Webstart.isWebstart()) {
            Webstart.webstartSetup();
        }

        JmeClientMain worldTest = new JmeClientMain(args);

    }

    /**
     * Process any command line args
     */
    private void processArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-fps")) {
                setDesiredFrameRate(Integer.parseInt(args[i + 1]));
                i++;
            } else if (args[i].equals("-p")) {
                System.setProperty(PROPS_URL_PROP, "file:" + args[i + 1]);
                i++;
            }
        }
    }

    /**
     * Create all of the Swing windows - and the 3D window
     */
    private void createUI(WorldManager wm, int width, int height) {

        frame = new MainFrameImpl(wm, width, height);
        // center the frame
        frame.getFrame().setLocationRelativeTo(null);

        // show frame
        frame.getFrame().setVisible(true);
        
        JPanel canvas3D = frame.getCanvas3DPanel();
        // Initialize an onscreen view
        ViewManager.initialize(canvas3D.getWidth(), canvas3D.getHeight());

        // This call will block until the render buffer is ready, for it to
        // become ready the canvas3D must be visible
        // Note: this disables focus traversal keys for the canvas it creates.
        ViewManager viewManager = ViewManager.getViewManager();
        viewManager.attachViewCanvas(canvas3D);

        // Initialize the input manager.
        // Note: this also creates the view manager.
        // TODO: low bug: we would like to initialize the input manager BEFORE
        // frame.setVisible. But if we create the camera before frame.setVisible
        // the client window never appears.
        CameraComponent cameraComp = viewManager.getCameraComponent();
        InputManager inputManager = ClientContext.getInputManager();
        inputManager.initialize(frame.getCanvas(), cameraComp);

        // Default Policy: Enable global key and mouse focus everywhere
        // Note: the app base will impose its own (different) policy later
        inputManager.addKeyMouseFocus(inputManager.getGlobalFocusEntity());

        /* For Testing FocusEvent3D
        InputManager3D.getInputManager().addGlobalEventListener(
        new EventClassListener () {
        private final Logger logger = Logger.getLogger("My Logger");
        public Class[] eventClassesToConsume () {
        return new Class[] { FocusEvent3D.class };
        }
        public void commitEvent (Event event) {
        logger.severe("Global listener: received mouse event, event = " + event);
        }
        });
         */

        /* Note: Example of global key and mouse event listener
        InputManager3D.getInputManager().addGlobalEventListener(
        new EventClassFocusListener () {
        private final Logger logger = Logger.getLogger("My Logger");
        public Class[] eventClassesToConsume () {
        return new Class[] { KeyEvent3D.class, MouseEvent3D.class };
        }
        public void commitEvent (Event event) {
        // NOTE: to test, change the two logger.fine calls below to logger.warning
        if (event instanceof KeyEvent3D) {
        if (((KeyEvent3D)event).isPressed()) {
        logger.fine("Global listener: received key event, event = " + event );
        }
        } else {
        logger.fine("Global listener: received mouse event, event = " + event);
        MouseEvent3D mouseEvent = (MouseEvent3D) event;
        System.err.println("Event pickDetails = " + mouseEvent.getPickDetails());
        System.err.println("Event entity = " + mouseEvent.getEntity());
        }
        }
        });
         */

        // make sure we don't miss any MT-Game exceptions
        SceneWorker.addWorker(new WorkCommit() {
            public void commit() {
                Thread.currentThread().setUncaughtExceptionHandler(ueh);
            }
        });

        frame.setDesiredFrameRate(getDesiredFrameRate());

        // OWL issue #146: for full screen, make sure the canvas is actually
        // the right size by forcing a repaint
        canvas3D.invalidate();
        frame.getFrame().getContentPane().validate();
    }

    /**
     * Returns the frame of the Wonderland client window.
     * @return the frame of the Wonderland client window.
     */
    public static MainFrame getFrame() {
        return frame;
    }

    /**
     * Set the main frame
     * @param frame the new main frame
     */
    public static void setFrame(MainFrame frame) {
        JmeClientMain.frame = frame;
    }

    /**
     * Load system properties and properties from the named file
     * @param propsURL the URL of the properties file to load
     */
    protected void loadProperties(URL propsURL) {
        Properties mergeProps = new Properties();

        // load the given file
        if (propsURL != null) {
            try {
                mergeProps.load(propsURL.openStream());
            } catch (IOException ioe) {
                LOGGER.log(Level.WARNING, "Error reading properties from "
                        + propsURL, ioe);
            }
        }

        // only set a property if it has not been set by web start
        for (String propsName : mergeProps.stringPropertyNames()) {
            if (System.getProperties().getProperty(propsName) == null) {
                System.getProperties().setProperty(propsName,
                                                   mergeProps.getProperty(propsName));
            }
        }
    }

    private void detectOS() {
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Mac OS X")) {
            os = OS.OSX;
        } else if (osName.startsWith("Windows")) {
            os = OS.Windows;
        } else if (osName.startsWith("Linux")) {
            os = OS.Linux;
        } else {
            os = OS.Other;
        }
    }

    /**
     * Check we are running in a supported VM.
     */
    private void checkVmVersion() {
        String version = System.getProperty("java.version");
        String[] tokens = version.split("\\.");
        if (tokens.length > 2) {
            if (Integer.parseInt(tokens[1]) < 6) {
                LOGGER.severe("Java Version is older than 6");
                String errorMessage = BUNDLE.getString("JAVA_VERSION_ERROR")
                        + "\n\n" + BUNDLE.getString(os == OS.OSX
                        ? "JAVA_VERSION_ERROR_OSX"
                        : "JAVA_VERSION_ERROR_OTHER");
                JOptionPane.showMessageDialog(null, errorMessage,
                        BUNDLE.getString("JAVA_VERSION_ERROR_TITLE"),
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        } else {
            LOGGER.warning("could not parse java version \"" + version + '\"');
        }
    }

    /**
     * Check if another JVM process has the database opened. If so, then post
     * a message and exit.
     */
    private void checkDBException() {
        // Create an AssetDB object, which will attempt to open the DB. Upon
        // exception, launch a message dialog. Upon success, just close the
        // DB
        AssetDB assetDB = null;
        try {
            assetDB = new AssetDB();
        } catch (AssetDBException excp) {
            LOGGER.log(Level.SEVERE,
                    "Unable to connect DB, another JVM running", excp);
            String errorMessage = BUNDLE.getString("DB_ERROR");
            if (os == OS.Windows) {
                errorMessage += "\n" + BUNDLE.getString("DB_ERROR_WINDOWS");
            }
            JOptionPane.showMessageDialog(null,
                    errorMessage,
                    BUNDLE.getString("DB_ERROR_TITLE"),
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        assetDB.disconnect();
    }

    /** runnable for loading the server and remembering the exception */
    class ServerLoader implements Runnable {

        private String serverURL;
        private Vector3f translation;
        private Quaternion look;
        private IOException ioe;

        public ServerLoader(String serverURL, Vector3f translation,
                Quaternion look) {
            this.serverURL = serverURL;
            this.translation = translation;
            this.look = look;
        }

        public void run() {
            try {
                loadServer(serverURL, translation, look);
            } catch (IOException ioe) {
                // remember the exception
                this.ioe = ioe;
            }
        }

        public IOException getException() {
            return ioe;
        }
    }
}
