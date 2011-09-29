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
package org.jdesktop.wonderland.modules.darkstar.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.comms.LoginFailureException;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.client.login.LoginUI;
import org.jdesktop.wonderland.client.login.PluginFilter;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.client.login.ServerSessionManager.NoAuthLoginControl;
import org.jdesktop.wonderland.client.login.ServerSessionManager.UserPasswordLoginControl;
import org.jdesktop.wonderland.client.login.ServerSessionManager.EitherLoginControl;
import org.jdesktop.wonderland.front.admin.ServerInfo;
import org.jdesktop.wonderland.modules.darkstar.api.weblib.DarkstarWebLogin;
import org.jdesktop.wonderland.runner.RunManager;
import org.jdesktop.wonderland.runner.RunManager.RunnerListener;
import org.jdesktop.wonderland.runner.Runner;
import org.jdesktop.wonderland.runner.Runner.RunnerStatusListener;
import org.jdesktop.wonderland.runner.Runner.Status;

/**
 * Singleton for managing login to the Darkstar server from the web server.
 * @author jkaplan
 */
public class DarkstarWebLoginImpl implements DarkstarWebLogin, RunnerListener, RunnerStatusListener {
    private static final Logger logger =
            Logger.getLogger(DarkstarWebLoginImpl.class.getName());

    private static final String USERNAME_PROP = "wonderland.webserver.user";
    private static final String USERNAME_DEFAULT = "webserver";
    private static final String PASSWORDFILE_PROP = "wonderland.webserver.password.file";

    private final Map<DarkstarRunnerImpl, ServerSessionManager> sessions =
            new LinkedHashMap<DarkstarRunnerImpl, ServerSessionManager>();

    private final Set<DarkstarServerListener> listeners =
            new CopyOnWriteArraySet<DarkstarServerListener>();

    private String username;
    private File passwordFile;

    /**
     * Protected singleton constructor -- use getInstance() instead.
     */
    public DarkstarWebLoginImpl() {
        LoginManager.setLoginUI(new DarkstarWebLoginUI());
        LoginManager.setPluginFilter(new PluginFilter.NoPluginFilter());

        // read username and password file from properties
        username = System.getProperty(USERNAME_PROP, USERNAME_DEFAULT);
        String passwordFileName = System.getProperty(PASSWORDFILE_PROP);
        if (passwordFileName != null) {
            passwordFile = new File(passwordFileName);
        }

        // listen for runners
        RunManager.getInstance().addRunnerListener(this);

        // if any runners already exist, add them
        Collection<DarkstarRunnerImpl> runners =
                RunManager.getInstance().getAll(DarkstarRunnerImpl.class);
        for (DarkstarRunnerImpl dr : runners) {
            runnerAdded(dr);
        }
    }

    /**
     * Add a listener that will be notified when Darkstar servers start and
     * stop. On addition, the listener will be immediately notified of all
     * existing servers.
     * @param listener the listener to add
     */
    public void addDarkstarServerListener(DarkstarServerListener listener) {
        listeners.add(listener);

        // create a copy while holding the lock
        Entry<DarkstarRunnerImpl, ServerSessionManager>[] entries;
        synchronized (sessions) {
            entries = sessions.entrySet().toArray(new Entry[0]);
        }
        
        // notify listeners after releasing the lock
        for (Entry<DarkstarRunnerImpl, ServerSessionManager> e : entries) {
            listener.serverStarted(e.getKey(), e.getValue());
        }
    }

    /**
     * Remove a listener that will be notified of server stops and starts.
     * @param listener the listener to remove
     */
    public void removeDarkstarServerListener(DarkstarServerListener listener) {
        listeners.remove(listener);
    }

    /**
     * Notification that a new runner has been added
     * @param runner the runner that was added
     */
    public void runnerAdded(Runner runner) {
        if (!(runner instanceof DarkstarRunnerImpl)) {
            return;
        }

        Status status = runner.addStatusListener(this);
        if (status == Status.RUNNING) {
            statusChanged(runner, status);
        }
    }

    /**
     * Notification that a runner has been removed
     * @param runner the runner that was removed
     */
    public void runnerRemoved(Runner runner) {
        if (!(runner instanceof DarkstarRunnerImpl)) {
            return;
        }

        runner.removeStatusListener(this);
        statusChanged(runner, Status.NOT_RUNNING);
    }

    /**
     * Handle when a runner starts up or shuts down.
     * @param runner the runner that changed status
     * @param status the new status
     */
    public void statusChanged(final Runner runner,
                              final Status status)
    {
        new Thread(new Runnable() {
            public void run() {
                switch (status) {
                    case RUNNING:
                        fireServerStarted((DarkstarRunnerImpl) runner);
                        break;
                    case NOT_RUNNING:
                        fireServerStopped((DarkstarRunnerImpl) runner);
                        break;
                }
            }
        }).start();
    }

    /**
     * Notify listeners of a server starting
     * @param runner the runner that started
     */
    protected void fireServerStarted(DarkstarRunnerImpl runner) {

        try {
            // XXX TODO: Make server-specific
            ServerSessionManager sessionManager =
                LoginManager.getSessionManager(ServerInfo.getInternalServerURL());

            // add to session map
            synchronized (sessions) {
                sessions.put(runner, sessionManager);
            }
         
            // notify listeners
            for (DarkstarServerListener l : listeners) {
                try {
                    l.serverStarted(runner, sessionManager);
                } catch (Throwable t) {
                    logger.log(Level.WARNING, "Error sending server started event", t);
                }
            }
        } catch (IOException ioe) {
            logger.log(Level.WARNING, "Error getting session manager", ioe);
        }
    }

    /**
     * Notify listeners of a server stopping
     * @param runner the runner that stopped
     */
    protected void fireServerStopped(DarkstarRunnerImpl runner) {
        // remove from session map
        synchronized (sessions) {
            sessions.remove(runner);
        }
        
        for (DarkstarServerListener l : listeners) {
            try {
                l.serverStopped(runner);
            } catch (Throwable t) {
                logger.log(Level.WARNING, "Error sending server stopped event", t);
            }
        }
    }

    /**
     * Internal class for handling login to the Darkstar server
     */
    private class DarkstarWebLoginUI implements LoginUI {
        public void requestLogin(NoAuthLoginControl control) {
            try {
                control.authenticate(username, "Wonderland web server");
            } catch (LoginFailureException lfe) {
                logger.log(Level.WARNING, "Error connecting to " +
                           control.getServerURL(), lfe);
                control.cancel();
            }
        }

        public void requestLogin(UserPasswordLoginControl control) {
            try {
                // read the password file
                BufferedReader br = new BufferedReader(new FileReader(passwordFile));
                String password = br.readLine();

                control.authenticate(username, password);
                return;
            } catch (LoginFailureException lfe) {
                logger.log(Level.WARNING, "Error connecting to " +
                           control.getServerURL(), lfe);
                control.cancel();
            } catch (IOException ioe) {
                logger.log(Level.WARNING, "Error connecting to " +
                           control.getServerURL(), ioe);
                control.cancel();
            }
        }

        public void requestLogin(EitherLoginControl control) {
            if (passwordFile != null) {
                // if we have a password, use it
                requestLogin(control.getUserPasswordLogin());
            } else {
                // no password, try as a guest
                requestLogin(control.getNoAuthLogin());
            }
        }
    }
}
