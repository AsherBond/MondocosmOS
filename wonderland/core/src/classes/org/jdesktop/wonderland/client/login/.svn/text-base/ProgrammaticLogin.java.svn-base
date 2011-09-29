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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.comms.LoginFailureException;
import org.jdesktop.wonderland.client.comms.WonderlandServerInfo;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.comms.WonderlandSessionImpl;
import org.jdesktop.wonderland.client.jme.WonderlandURLStreamHandlerFactory;
import org.jdesktop.wonderland.client.login.ServerSessionManager.NoAuthLoginControl;
import org.jdesktop.wonderland.client.login.ServerSessionManager.UserPasswordLoginControl;
import org.jdesktop.wonderland.client.login.ServerSessionManager.EitherLoginControl;

/**
 * Utility class to handle login to a Wonderland server.  Given a serverURL,
 * username, and optionally the path to a password file, this class will create
 * a WonderlandSession connected to the given server.
 *
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class ProgrammaticLogin<T extends WonderlandSession> {
    private static final Logger logger =
            Logger.getLogger(ProgrammaticLogin.class.getName());

    /** The server URL to connect to */
    private String serverURL;

    /**
     * Initialize programmatic login.  Required before ProgrammaticLogin
     * can be used.
     */
    static {
        // set up URL handlers for Wonderland types
        URL.setURLStreamHandlerFactory(new WonderlandURLStreamHandlerFactory());

        // Prevent the login manager from loading usual Wonderland user client jars
        LoginManager.setPluginFilter(new PluginFilter.NoPluginFilter());
    }

    /**
     * Create a new ProgrammaticLogin that connects to the given server.
     * @param serverURL the server URL to connect to.
     */
    public ProgrammaticLogin(String serverURL) {
        this.serverURL = serverURL;
    }

    /**
     * Get the URL of the server this login object is connected to.
     * @return the server URL.
     */
    public String getServerURL() {
        return serverURL;
    }

    /**
     * Login to the server with the given credentials.  This returns a
     * WonderlandSession which is connected to the server.
     * @param username the username to connect with.  Required.
     * @param passwordFile a file containing the password to connect with.
     * Optional.  Only used when security is set up to require a username and
     * password for login.
     * @return the WonderlandSession created after connecting.
     */
    public T login(String username, File passwordFile) {
        // create a login UI object that uses the values passed in to this
        // method to respond to login queries.  Note that this means
        // ProgrammaticLogin objects are not thread-safe -- only a single
        // login can be in progress at a time!
        ProgrammaticLoginUI loginUI = new ProgrammaticLoginUI(username,
                                                              passwordFile);
        LoginManager.setLoginUI(loginUI);

        // get a session manager for connecting to the given URL
        ServerSessionManager lm;
        try {
            lm = LoginManager.getSessionManager(getServerURL());
        } catch (IOException ioe) {
            RuntimeException re = new RuntimeException("Cannot get login manager instance");
            re.initCause(ioe);
            throw re;
        }

        // create a new session
        WonderlandSession curSession = null;
        try {

            // keep trying to log in until we succeed.  Pause 5 seconds between
            // login attempts
            boolean loggedIn = false;
            boolean notified = false;
            do {
                try {
                    curSession = lm.createSession(loginUI);
                    loggedIn = true;
                } catch (LoginFailureException le) {
                    if (!notified) {
                        logger.log(Level.WARNING, "Darkstar " +
                                   "server not available.  Retrying every 5 " +
                                   "seconds.");
                        notified = true;
                    }
                    Thread.sleep(5000);
                }
            } while (!loggedIn);

            if (notified) {
                logger.log(Level.WARNING, "Connected to " +
                           "Darkstar server.");
            }
       } catch (InterruptedException ie) {
            // thread interrupted
            RuntimeException re = new RuntimeException("Error connecting to server.");
            re.initCause(ie);
            throw re;
        }

        // make sure we logged in successfully
        if (curSession == null) {
            throw new RuntimeException("Unable to create session.");
        }

        LoginManager.setPrimary(lm);
        lm.setPrimarySession(curSession);

        return (T) curSession;
    }

    /**
     * Create a new WonderlandSession.  This method is provided so
     * subclasses can override it to return different session types.
     * @param sessionManager the session manager that created this session
     * @param server the server the session is connected to
     * @param loader the classloader for use in the session
     * @return a newly created WonderlandSession  object
     */
    protected T createSession(ServerSessionManager sessionManager,
                              WonderlandServerInfo server,
                              ClassLoader loader)
    {
        return (T) new WonderlandSessionImpl(sessionManager, server, loader);
    }

    /**
     * Provides the login information from the constructor to the login manager.
     */
    private class ProgrammaticLoginUI implements LoginUI, SessionCreator<WonderlandSession> {
        private String username;
        private File passwordFile;

        public ProgrammaticLoginUI(String username, File passwordFile) {
            this.username = username;
            this.passwordFile = passwordFile;
        }

        // The LoginManager calls this during the login process to handle
        // login to servers where authentication is disabled.
        public void requestLogin(final NoAuthLoginControl control) {
            try {
                control.authenticate(username, username);
                return;
            } catch (LoginFailureException lfe) {
                RuntimeException re = 
                        new RuntimeException("Cannot authenticate user " + username);
                re.initCause(lfe);
                throw re;
            }
        }

        // The LoginManager calls this during the login process to handle
        // login to servers with a username and password.
        public void requestLogin(UserPasswordLoginControl control) {
            try {
                // read the password file
                BufferedReader br = new BufferedReader(new FileReader(passwordFile));
                String password = br.readLine();

                control.authenticate(username, password);
                return;
            } catch (LoginFailureException lfe) {
                RuntimeException re = new RuntimeException("Cannot authenticate user " + username);
                re.initCause(lfe);
                throw re;
            } catch (IOException ioe) {
                RuntimeException re = new RuntimeException("Error reading password file");
                re.initCause(ioe);
                throw re;
            }
        }

        // Use a password if we have one, or a guest login if not
        public void requestLogin(EitherLoginControl control) {
            if (passwordFile != null) {
                // use a password
                requestLogin(control.getUserPasswordLogin());
            } else {
                // use a guest login
                requestLogin(control.getNoAuthLogin());
            }
        }

        // The LoginManager calls this to create the WonderlandSession
        public WonderlandSession createSession(ServerSessionManager sessionManager,
                                               WonderlandServerInfo server,
                                               ClassLoader loader)
        {
            return ProgrammaticLogin.this.createSession(sessionManager, server, loader);
        }
    }
}
