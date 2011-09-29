/**
 * Open Wonderland
 *
 * Copyright (c) 2011, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.client.login;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.client.ClientPlugin;
import org.jdesktop.wonderland.client.comms.LoginFailureException;
import org.jdesktop.wonderland.client.comms.LoginParameters;
import org.jdesktop.wonderland.client.comms.ServerUnavailableException;
import org.jdesktop.wonderland.client.comms.SessionStatusListener;
import org.jdesktop.wonderland.client.comms.WonderlandServerInfo;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.comms.WonderlandSession.Status;
import org.jdesktop.wonderland.client.comms.WonderlandSessionImpl;
import org.jdesktop.wonderland.common.modules.ModulePluginList;
import org.jdesktop.wonderland.client.modules.ModuleUtils;
import org.jdesktop.wonderland.common.JarURI;
import org.jdesktop.wonderland.common.annotation.Plugin;
import org.jdesktop.wonderland.common.login.AuthenticationException;
import org.jdesktop.wonderland.common.login.AuthenticationInfo;
import org.jdesktop.wonderland.common.login.AuthenticationManager;
import org.jdesktop.wonderland.common.login.AuthenticationService;
import org.jdesktop.wonderland.common.login.CredentialManager;
import org.jdesktop.wonderland.common.utils.ScannedClassLoader;

/**
 * Manager for all the sessions for a particular server
 *
 * @author jkaplan
 */
public class ServerSessionManager {
    private static final Logger logger =
            Logger.getLogger(ServerSessionManager.class.getName());

    private static final ResourceBundle BUNDLE =  ResourceBundle.getBundle(
            "org/jdesktop/wonderland/client/login/Bundle");

    /** where on the server to find the details object */
    private static final String DETAILS_URL =
            "wonderland-web-front/resources/ServerDetails";

    /** the default object to use when creating sessions */
    private static SessionCreator<?> defaultSessionCreator =
            new DefaultSessionCreator();

    /** the server this manager represents */
    private String serverURL;

    /** details about the server (read from server in constructor) */
    private ServerDetails details;

    /** whether or not we are authenticated to the server */
    private LoginControl loginControl;

    /** a lock to prevent connection and disconnection from happening at
     *  the same time
     */
    private final Object connectLock = new Object();

    /** the session for this login */
    private final Set<WonderlandSession> sessions = 
            Collections.synchronizedSet(new HashSet<WonderlandSession>());

    /** the primary session */
    private WonderlandSession primarySession;
    private final Object primarySessionLock = new Object();

    /** session lifecycle listeners */
    private final Set<SessionLifecycleListener> lifecycleListeners =
            new CopyOnWriteArraySet<SessionLifecycleListener>();

    /** server status listeners */
    private final Set<ServerStatusListener> statusListeners =
            new CopyOnWriteArraySet<ServerStatusListener>();

    /** the list of plugins we have initialized, to make sure we clean up */
    private final Set<ClientPlugin> plugins = new HashSet<ClientPlugin>();

    /**
     * Constructor is private, use getInstance() instead.
     * @param serverURL the url to connect to
     * @throws IOException if there is an error connecting to the server
     */
    ServerSessionManager(String serverURL) throws IOException {
        // load the server details
        this.details = loadDetails(serverURL);

        // reset the server URL to the canonical URL sent by the server
        this.serverURL = details.getServerURL();
    }

    /**
     * Get the server URL this server session manager represents.  This is the
     * canonical URL returned by the server that was originally requested,
     * not necessarily the original URL that was passed in
     * @return the canonical server URL
     */
    public String getServerURL() {
        return serverURL;
    }

    /**
     * Get the server URL as a string: &lt;server name&gt;:&lt;port&gt;
     * @return &lt;server name&gt;:&lt;port&gt;
     */
    public String getServerNameAndPort() {
        try {
            URL tmpURL = new URL(serverURL);
            String server = tmpURL.getHost();
            if (tmpURL.getPort() != -1) {
                server = server + ":" + tmpURL.getPort();
            }
            return server;
        } catch (MalformedURLException ex) {
            Logger.getLogger(ServerSessionManager.class.getName()).log(Level.WARNING, null, ex);
            return null;
        }
    }
    
    /**
     * Get the details for this server
     * @return the details for this server
     */
    public ServerDetails getDetails() {
        // if there are session connected, just return the details that
        // they connected with
        synchronized (this) {
            synchronized (sessions) {
                if (sessions.size() > 0) {
                    return details;
                }
            }
        }

        // otherwise, try to load the latest details from the server
        try {
            return loadDetails(getServerURL());
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Error loading details", ex);

            // return an empty details object
            return new ServerDetails();
        }
    }

    /**
     * Determine if this session manager is connected to the server.   This
     * method will return true after the first call to create session, once
     * login has completed and all plugins have been initialized.
     * @return true if this session manager is connected to the server, or
     * false if not
     */
    public synchronized boolean isConnected() {
        if (loginControl == null) {
            return false;
        }

        return loginControl.isAuthenticated();
    }

    /**
     * Disconnect this session.  All sessions will be disconnected, and
     * all plugins will be cleaned up.
     */
    public void disconnect() {
        logger.log(Level.WARNING, "[ServerSessionManager] Disconnect from " +
                   getServerURL());

        synchronized (connectLock) {
            synchronized (this) {
                // if we are already disconnected, just return
                if (loginControl == null) {
                    return;
                }

                // remove the login control immediately so that
                // isConnected() will return false during the
                // disconnect process
                loginControl = null;
            }

            // if we are the primary session, send a notification that there
            // is no longer a primary session
            if (this.equals(LoginManager.getPrimary())) {
                LoginManager.setPrimary(null);
            }

            // disconnect all sessions
            for (WonderlandSession session : getAllSessions()) {
                session.logout();
            }

            // clean up all plugins
            for (ClientPlugin plugin : plugins) {
                plugin.cleanup();
            }

            // all done, clean things up
            plugins.clear();

            // notify listeners
            fireServerStatus(false);
        }
    }

    /**
     * Get the username this session is connected as.  Only valid after
     * login has been requested.
     * @return the username this session is logged in as, or null if this
     * session is not connected
     */
    public String getUsername() {
        if (loginControl == null) {
            return null;
        }

        return loginControl.getUsername();
    }

    /**
     * Get the credential manager for making secure connections back to this
     * server.  Only valid after login has completed.
     * @return the credential manager, or null if this session manager is
     * not yet connected.
     */
    public CredentialManager getCredentialManager() {
        if (loginControl == null) {
            return null;
        }

        return loginControl.getCredentialManager();
    }

    /**
     * Create a new WonderlandSession using the default session creator
     * @return the newly created session
     * @throws LoginFailureException if there is a problem creating the
     * session with the login credentials from this manager
     */
    public WonderlandSession createSession()
        throws LoginFailureException
    {
        return createSession(defaultSessionCreator);
    }

    /**
     * Create a new WonderlandSession using a custom session creator
     * @param creator the SessionCreator to use when creating the session
     * @return the newly created session
     * @throws LoginFailureException if there is a problem creating the
     * session with the login credentials from this manager
     */
    public <T extends WonderlandSession> T createSession(SessionCreator<T> creator)
        throws LoginFailureException
    {
        synchronized (connectLock) {
            // check the server details to see if the timestamp has changed
            checkTimeStamp(getDetails());

            // determine what type of authentication to use
            AuthenticationInfo authInfo = getDetails().getAuthInfo();

            synchronized (this) {
                // create the login control if necessary
                if (loginControl == null) {
                    loginControl = createLoginControl(authInfo);
                }
            }

            // see if we are already logged in
            boolean isConnect = false;
            if (!loginControl.isAuthenticated()) {
                requestLogin(loginControl);

                // if we get here, it means that we just requested a new
                // login and it succeeded.  When this method finishes, we need
                // to notify server status listeners.
                isConnect = true;
            }

            // make sure there is a Darkstar server available
            if (getDetails().getDarkstarServers() == null ||
                    getDetails().getDarkstarServers().size() == 0)
            {
                throw new ServerUnavailableException("No running Darkstar " +
                        "server found for " + getDetails().getServerURL());
            }

            // choose a Darkstar server to connect to
            DarkstarServer ds = getDetails().getDarkstarServers().get(0);
            WonderlandServerInfo serverInfo =
                    new WonderlandServerInfo(ds.getHostname(), ds.getPort());

            // use the session creator to create a new session
            T session = creator.createSession(this, serverInfo,
                                              loginControl.getClassLoader());

            // log in to the session
            session.login(loginControl.getLoginParameters());

            // the session was created successfully.  Add it to our list of
            // sessions, and add a listener to remove it when it disconnects
            session.addSessionStatusListener(new SessionStatusListener() {
                public void sessionStatusChanged(WonderlandSession session,
                                                 Status status)
                {
                    synchronized (ServerSessionManager.this) {
                        if (status.equals(Status.DISCONNECTED)) {
                            sessions.remove(session);
                        }
                    }
                }

            });
            sessions.add(session);
            fireSessionCreated(session);

            // if this is a new connection, notify listeners
            if (isConnect) {
                fireServerStatus(true);
            }

            // return the session
            return session;
        }
    }

    /**
     * Get all sessions
     * @return a list of all sessions
     */
    public Collection<WonderlandSession> getAllSessions() {
        synchronized (sessions) {
            return new ArrayList(sessions);
        }
    }

    /**
     * Get all sessions that implement the given type
     * @param clazz the class of session to get
     */
    public <T extends WonderlandSession> Collection<T>
            getAllSession(Class<T> clazz)
    {
        Collection<T> out = new ArrayList<T>();
        synchronized (sessions) {
            for (WonderlandSession session : sessions) {
                if (clazz.isAssignableFrom(session.getClass())) {
                    out.add((T) session);
                }
            }
        }

        return out;
    }

    /**
     * Get the primary session
     * @return the primary session
     */
    public WonderlandSession getPrimarySession() {
        // use a separate lock for the primary session because other threads
        // may need access to the primary session during login, for example
        // during a call to initialize a client plugin
        synchronized (primarySessionLock) {
            return primarySession;
        }
    }

    /**
     * Set the primary session.  The primary session must be in the connected
     * state.  It will be removed automatically if it disconnects.
     * @param primary the primary session
     */
    public void setPrimarySession(final WonderlandSession primarySession) {
        if (primarySession != null && primarySession.getStatus() != Status.CONNECTED) {
            throw new IllegalStateException("Primary session must be connected");
        }

        synchronized (primarySessionLock) {
            this.primarySession = primarySession;
        }

        // add a listener that will remove the primary session if the session
        // disconnects
        if (primarySession != null) {
            primarySession.addSessionStatusListener(new SessionStatusListener() {
                public void sessionStatusChanged(WonderlandSession session,
                                                 Status status)
                {
                    if (status == Status.DISCONNECTED) {
                        synchronized (primarySessionLock) {
                            if (getPrimarySession() == primarySession) {
                                logger.fine("[ServerSessionManager] set primary session to null");
                                setPrimarySession(null);
                            }
                        }
                    }
                }
            });
        }

        // notify listeners
        firePrimarySession(primarySession);
    }

    /**
     * Add a lifecycle listener.  This will receive messages for all
     * clients that are created or change status
     * @param listener the listener to add
     */
    public void addLifecycleListener(SessionLifecycleListener listener) {
        lifecycleListeners.add(listener);
    }

    /**
     * Remove a lifecycle listener.
     * @param listener the listener to remove
     */
    public void removeLifecycleListener(SessionLifecycleListener listener) {
        lifecycleListeners.remove(listener);
    }

    /**
     * Add a session status listener.  This will be notified when this
     * session connects or disconnects.
     * @param listener the listener to add
     */
    public void addServerStatusListener(ServerStatusListener listener) {
        statusListeners.add(listener);
    }

    /**
     * Remove a session status listener.
     * @param listener the listener to remove
     */
    public void removeServerStatusListener(ServerStatusListener listener) {
        statusListeners.remove(listener);
    }

    /**
     * Get the classloader this session uses to load plugins.  Only valid after
     * login has been requested.
     * @return the classloader this session uses, or null if this
     * session is not connected
     */
    public ScannedClassLoader getClassloader() {
        if (loginControl == null) {
            return null;
        }

        return loginControl.getClassLoader();
    }

    /**
     * Load the server details for the server
     * @param serverURL the URL of the server to load details for
     * @throws IOException if there is an error loading or decoding the details
     */
    protected ServerDetails loadDetails(String serverURL) throws IOException {
        try {
            URL detailsURL = new URL(new URL(serverURL), DETAILS_URL);

            URLConnection detailsURLConn = detailsURL.openConnection();
            detailsURLConn.setRequestProperty("Accept", "application/xml");

            return ServerDetails.decode(new InputStreamReader(detailsURLConn.getInputStream()));
        } catch (JAXBException jbe) {
            IOException ioe = new IOException("Error reading server details " +
                                              "from: " + serverURL);
            ioe.initCause(jbe);
            throw ioe;
        }
    }

    /**
     * Check the server timestamp, and disconnect if it is newer than the
     * timestamp we have.
     * @throws LoginFailureException if there is an error checking the
     * timestamp
     */
    protected void checkTimeStamp(ServerDetails details)
            throws LoginFailureException
    {
        logger.fine("[ServerSessionManager] checkTimeStamp old: " +
                    getDetails().getTimeStamp() + " new: " +
                    details.getTimeStamp());

        if (this.details == null || 
                (details.getTimeStamp() > this.details.getTimeStamp()))
        {
            // the details are newer -- force a disconnect so we reconnect
            // completely on the next login attempt
            if (isConnected()) {
                disconnect();
            }
        }

        synchronized (this) {
            // update the details, since things like the Darkstar server
            // ordering may have changed
            this.details = details;
        }
    }

    /**
     * Create a new LoginControl of the appropriate type
     * @param authInfo the authentication info
     * @return a new LoginControl for the given type
     */
    protected LoginControl createLoginControl(AuthenticationInfo info) {
         switch (info.getType()) {
            case NONE:
                return new NoAuthLoginControl(info);
            case WEB_SERVICE:
                return new UserPasswordLoginControl(info);
            case EITHER:
                return new EitherLoginControl(info);
            default:
                throw new IllegalStateException("Unknown login type " +
                                                info.getType());
        }
    }

    /**
     * Request login from the given login control object
     * @param loginControl the login control object to get login info from
     * throws LoginFailureException if the login fails or is cancelled
     */
    protected void requestLogin(LoginControl control)
        throws LoginFailureException
    {
        // see if we already have a login in progress
        if (!control.isAuthenticating()) {
            control.requestLogin(LoginManager.getLoginUI());
        }

        // wait for the login to complete
        try {
            boolean result = control.waitForLogin();
            if (!result) {
                throw new LoginFailureException("Login cancelled");
            }
        } catch (InterruptedException ie) {
            throw new LoginFailureException(ie);
        }
    }

    /**
     * Notify any registered lifecycle listeners that a new session was created
     * @param session the client that was created
     */
    private void fireSessionCreated(WonderlandSession session) {
        for (SessionLifecycleListener listener : lifecycleListeners) {
            listener.sessionCreated(session);
        }
    }

    /**
     * Notify any registered lifecycle listeners that a session was declared
     * the primary session
     * @param session the client that was declared primary
     */
    private void firePrimarySession(WonderlandSession session) {
        for (SessionLifecycleListener listener : lifecycleListeners) {
            listener.primarySession(session);
        }
    }

    /**
     * Notify any registered status listeners that the server has connected
     * or disconnected
     * @param connected true if the status is now connected, or false if it
     * is now disconnected
     */
    private void fireServerStatus(boolean connected) {
        for (ServerStatusListener listener : statusListeners) {
            if (connected) {
                listener.connected(this);
            } else {
                listener.disconnected(this);
            }
        }
    }

    /**
     * Notify any registered status listeners that the server is in the process
     * of connecting
     * @param message the status message to send
     */
    private void fireConnecting(String message) {
        for (ServerStatusListener listener : statusListeners) {
            listener.connecting(this, message);
        }
    }

    /**
     * Set up the classloader with module jar URLs for this server
     * @param serverURL the URL of the server to connect to
     * @return the classloader setup with this server's URLs
     */
    private ScannedClassLoader setupClassLoader(String serverURL) {
        fireConnecting(BUNDLE.getString("Creating classloader"));

        // TODO: use the serverURL
        ModulePluginList list = ModuleUtils.fetchPluginJars(serverURL);
        List<URL> urls = new ArrayList<URL>();
        if (list == null) {
            logger.warning("Unable to configure classlaoder, falling back to " +
                           "system classloader");
            return new ScannedClassLoader(new URL[0], 
                                          getClass().getClassLoader());
        }

        for (JarURI uri : list.getJarURIs()) {
            try {
                // check the filter to see if we should add this URI
                if (LoginManager.getPluginFilter().shouldDownload(this, uri)) {
                    urls.add(uri.toURL());
                }
            } catch (Exception excp) {
                excp.printStackTrace();
           }
        }

        return new ScannedClassLoader(urls.toArray(new URL[0]),
                                      getClass().getClassLoader());
    }

    /**
     * Initialize plugins
     */
    private void initPlugins(ScannedClassLoader loader) {
        // At this point, we have successfully logged in to the server,
        // and the session should be connected.

        // Collect all plugins from service provides and from annotated
        // classes, then initialize each one
        Iterator<ClientPlugin> it = loader.getAll(Plugin.class,
                                                  ClientPlugin.class);

        while (it.hasNext()) {
            ClientPlugin plugin = it.next();

            String message = BUNDLE.getString("Initialize plugin");
            message = MessageFormat.format(
                    message, plugin.getClass().getSimpleName());
            fireConnecting(message);

            // check with the filter to see if we should load this plugin
            if (LoginManager.getPluginFilter().shouldInitialize(this, plugin)) {
                try {
                    plugin.initialize(this);
                    plugins.add(plugin);
                } catch(Exception e) {
                    logger.log(Level.WARNING, "Error initializing plugin " +
                               plugin.getClass().getName(), e);
                } catch(Error e) {
                    logger.log(Level.WARNING, "Error initializing plugin " +
                               plugin.getClass().getName(), e);
                }
            }
        }
    }

    public abstract class LoginControl {
        private AuthenticationInfo authInfo;

        private boolean started = false;
        private boolean finished = false;
        private boolean success = false;

        private LoginParameters params;
        private ScannedClassLoader classLoader;

        /**
         * Create a new login control for the given server
         * @param authInfo the authentication server
         */
        public LoginControl(AuthenticationInfo authInfo) {
            this.authInfo = authInfo;
        }

        /**
         * Get the authentication info for this login
         * @return the authentication info
         */
        protected AuthenticationInfo getAuthInfo() {
            return authInfo;
        }

        /**
         * Get the server URL for this login control object
         * @return the server URL to connect to
         */
        public String getServerURL() {
            return ServerSessionManager.this.getServerURL();
        }

        /**
         * Get the server session manager associated with this login control
         * object.
         * @return the server session manager
         */
        public ServerSessionManager getSessionManager() {
            return ServerSessionManager.this;
        }

        /**
         * Determine if login is complete and successful.
         * @return true of the login is complete and successful, false
         * if the login is in progress or failed.
         */
        public synchronized boolean isAuthenticated() {
            return finished && success;
        }

        /**
         * Determine if login is in progress.  This will return true
         * if a login has been requested from the client, but they
         * have not yet responded.
         * @return true if a login is in progress, or false if not
         */
        public synchronized boolean isAuthenticating() {
            return started && !finished;
        }

        /**
         * Request a login from the given login UI
         */
        public void requestLogin(LoginUI ui) {
            setStarted();
        }

        /**
         * Indicate that a login is in progress with this control object
         */
        protected synchronized void setStarted() {
            started = true;
        }

        /**
         * Get the classloader to use when connecting to the Darkstar server.
         * This method is only valid when isAuthenticated() returns true.
         * @return the classloader to use
         */
        public synchronized ScannedClassLoader getClassLoader() {
            if (!isAuthenticated()) {
                throw new IllegalStateException("Not authenticated");
            }

            return classLoader;
        }

        /**
         * Get the LoginParameters to use when connecting to the Darkstar
         * server. This method is valid starting after the server login
         * has happened, but before any plugins have been initialized.
         * @return the LoginParameters to use, or null if the parameters
         * have not been set yet
         */
        public synchronized LoginParameters getLoginParameters() {
            return params;
        }

        /**
         * Get the username that this user has connected as.  This should
         * be a unique identifier for the user based on the authentication
         * information they provided.  This method must return a value
         * any time after <code>loginComplete()</code> has been called.
         * @return the username the user has logged in as
         */
        public abstract String getUsername();

        /**
         * Get the credential manager associated with this login control.
         * @return the credential manager
         */
        public abstract CredentialManager getCredentialManager();

        /**
         * Indicate that the login attempt was successful, and pass in
         * the LoginParameters that should be sent to the Darkstar server
         * to create a session.
         * <p>
         * This method indicates that login has been successful, so
         * sets up the plugin classloader for use in session creation. Once
         * the classloader is setup, it notifies any listeners that login
         * is complete.
         *
         * @param loginParams the parameters to login with. A null
         * LoginParameters object indicates that the login attempt has failed.
         */
        protected synchronized void loginComplete(LoginParameters params) {
            this.params = params;
            if (params != null) {
                // setup the classloader
                this.classLoader = setupClassLoader(getServerURL());

                // initialize plugins
                initPlugins(classLoader);

                // if we get here, the login has succeeded
                this.success = true;
            }

            this.started = false;
            this.finished = true;
            notify();
        }

        /**
         * Cancel the login in progress
         */
        public synchronized void cancel() {
            loginComplete(null);
        }

        /**
         * Wait for the current login in progress to end
         * @return true if the login is successful, or false if not
         * @throws InterruptedException if the thread is interrupted before
         * the login parameters are determined
         */
        protected synchronized boolean waitForLogin()
            throws InterruptedException
        {
            while (isAuthenticating()) {
                wait();
            }

            return isAuthenticated();
        }
    }

    public abstract class WebServiceLoginControl extends LoginControl {
        private String username;
        private AuthenticationService authService;

        public WebServiceLoginControl(AuthenticationInfo authInfo) {
            super (authInfo);
        }

        public String getUsername() {
            return username;
        }

        protected void setUsername(String username) {
            this.username = username;
        }

        public CredentialManager getCredentialManager() {
            return authService;
        }

        protected void setAuthService(AuthenticationService authService) {
            this.authService = authService;
        }

        protected boolean needsLogin() {
            // check if we already have valid credentials
            synchronized (this) {
                if (authService == null) {
                    authService = AuthenticationManager.get(getAuthInfo().getAuthURL());
                }
            }

            try {
                if (authService != null && authService.isTokenValid()) {
                    // if this is the case, we already have a valid login
                    // for this server.  Set things up properly.
                    loginComplete(authService.getUsername(),
                                  authService.getAuthenticationToken());

                    // all set
                    return false;
                }
            } catch (AuthenticationException ee) {
                // ignore -- we'll just retry the login
                logger.log(Level.WARNING, "Error checking exiting service", ee);
            }

            // if we get here, there is no valid auth service for this server
            // url
            return true;
        }

        public void authenticate(String username, Object... credentials)
            throws LoginFailureException
        {
            fireConnecting(BUNDLE.getString(
                    "Sending authentication details..."));
            try {
                AuthenticationService as =
                        AuthenticationManager.login(getAuthInfo(), username,
                                                    credentials);
                setAuthService(as);
                loginComplete(username, as.getAuthenticationToken());
            } catch (AuthenticationException ae) {
                throw new LoginFailureException(ae);
            }
        }

        protected void loginComplete(String username, String token) {
            setUsername(username);

            LoginParameters lp = new LoginParameters(token, new char[0]);
            super.loginComplete(lp);
        }
    }

    public class NoAuthLoginControl extends WebServiceLoginControl {
        public NoAuthLoginControl(AuthenticationInfo info) {
            super (info);
        }

        @Override
        public void requestLogin(LoginUI ui) {
            super.requestLogin(ui);
            
            // only request credentials from the user if we don't have them
            // from an existing AuthenticationService
            if (needsLogin()) {
                ui.requestLogin(this);
            }
        }

        public void authenticate(String username, String fullname)
            throws LoginFailureException
        {
            super.authenticate(username, fullname);
        }
    }

    public class UserPasswordLoginControl extends WebServiceLoginControl {
        public UserPasswordLoginControl(AuthenticationInfo info) {
            super (info);
        }

        @Override
        public void requestLogin(LoginUI ui) {
            super.requestLogin(ui);

            // only request credentials from the user if we don't have them
            // from an existing AuthenticationService
            if (needsLogin()) {
                ui.requestLogin(this);
            }
        }

        public void authenticate(String username, String password)
            throws LoginFailureException
        {
            super.authenticate(username, password);
        }
    }

    /**
     * A wrapper that works with either a NoAuthLoginControl or a
     * WebServiceLoginControl, depending on which method the UI uses.
     */
    public class EitherLoginControl extends LoginControl {
        private LoginControl wrapped;
        private boolean cancelled = false;

        public EitherLoginControl(AuthenticationInfo info) {
            super (info);
        }

        @Override
        public void requestLogin(LoginUI ui) {
            super.requestLogin(ui);
            ui.requestLogin(this);
        }

        public NoAuthLoginControl getNoAuthLogin() {
            // create a web service object to log in with
            AuthenticationInfo info = super.getAuthInfo().clone();
            info.setType(AuthenticationInfo.Type.NONE);
            NoAuthLoginControl control = new NoAuthLoginControl(info);
            control.setStarted();
            setWrapped(control);
            return control;
        }

        public UserPasswordLoginControl getUserPasswordLogin() {
            // create a web service object to log in with
            AuthenticationInfo info = super.getAuthInfo().clone();
            info.setType(AuthenticationInfo.Type.WEB_SERVICE);
            UserPasswordLoginControl control = new UserPasswordLoginControl(info);
            control.setStarted();
            setWrapped(control);
            return control;
        }

        public String getUsername() {
            return getWrapped().getUsername();
        }

        @Override
        public CredentialManager getCredentialManager() {
            return getWrapped().getCredentialManager();
        }

        @Override
        public LoginParameters getLoginParameters() {
            return getWrapped().getLoginParameters();
        }

        @Override
        public synchronized ScannedClassLoader getClassLoader() {
            return getWrapped().getClassLoader();
        }
        
        @Override
        public boolean isAuthenticated() {
            if (getWrapped() == null) {
                return false;
            }

            return getWrapped().isAuthenticated();
        }

        @Override
        public boolean isAuthenticating() {
            if (getWrapped() == null) {
                return false;
            }

            return getWrapped().isAuthenticating();
        }

        @Override
        public synchronized void cancel() {
            cancelled = true;

            if (getWrapped() != null) {
                getWrapped().cancel();
            }
        }

        @Override
        protected boolean waitForLogin() throws InterruptedException {
            synchronized (this) {
                // wait for a wrapper to show up
                while (getWrapped() == null) {
                    wait();
                }
            }

            // when the wrapper changes, we will cancel the pending login
            // to make sure that the call to waitForLogin() returns.  Therefore
            // we ignore when the wrapper returns fals (cancellation), and
            // only pay attention to our own cancelled value.
            while (!isCancelled()) {
                if (getWrapped().waitForLogin()) {
                    return true;
                }
            }

            return false;
        }

        protected synchronized void setWrapped(LoginControl wrapped) {
            if (this.wrapped != null) {
                this.wrapped.cancel();
            }

            this.wrapped = wrapped;
            notify();
        }

        protected synchronized LoginControl getWrapped() {
            return wrapped;
        }

        protected synchronized boolean isCancelled() {
            return cancelled;
        }
    }

    public static class DefaultSessionCreator
            implements SessionCreator<WonderlandSession>
    {
        public WonderlandSession createSession(ServerSessionManager manager,
                                               WonderlandServerInfo serverInfo,
                                               ClassLoader loader)
        {
            return new WonderlandSessionImpl(manager, serverInfo, loader);
        }
    }
}
