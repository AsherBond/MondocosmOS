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
package org.jdesktop.wonderland.client.login;

import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.jdesktop.wonderland.client.comms.SessionStatusListener;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.comms.WonderlandSession.Status;

/**
 * Handle logins for the Wonderland system. Keeps track of the relationship
 * between WonderlandSessions and the ServerSessionManager which owns the
 * server connection.
 *
 * @author jkaplan
 */
public class LoginManager {

    /** the UI to prompt the user for login information */
    private static LoginUI ui;

    /** the managers we have created, mapped by server URL */
    private static final ManagerCache managers = new ManagerCache();

    /** listeners to notify when the primary login manager changes */
    private static final Set<PrimaryServerListener> listeners =
            new CopyOnWriteArraySet<PrimaryServerListener>();

    /** the primary manager */
    private static ServerSessionManager primaryLoginManager;

    /** the default plugin filter to use */
    private static PluginFilter defaultPluginFilter = 
            new PluginFilter.DefaultPluginFilter();

    /**
     * Set the LoginUI to call back to during login attempts.
     * @param ui the user interface to login with
     */
    public synchronized static void setLoginUI(LoginUI ui) {
        LoginManager.ui = ui;
    }

    /**
     * Get the LoginUI to call back during login attempts
     * @return the login UI
     */
    synchronized static LoginUI getLoginUI() {
        return ui;
    }

    /**
     * Set the default plugin filter for all session managers created.
     * @param filter the plugin filter to use
     */
    public synchronized static void setPluginFilter(PluginFilter filter) {
        LoginManager.defaultPluginFilter = filter;
    }

    /**
     * Get the default plugin filter for all session managers created.
     * @return the plugin filter to use
     */
    synchronized static PluginFilter getPluginFilter() {
        return defaultPluginFilter;
    }

    /**
     * Get the session manager for a particular server URL
     * @param serverURL the serverURL to get a session manager for
     * @return the session manager
     * @throws IOException if there is an error connecting to the given
     * server URL
     */
    public static ServerSessionManager getSessionManager(String serverURL)
        throws IOException
    {
        synchronized (managers) {
            ServerSessionManager manager = findSessionManager(serverURL);
            if (manager == null) {
                manager = new ServerSessionManager(serverURL);
                managers.put(serverURL, manager);
            }

            return manager;
        }
    }

    /**
     * Finds an existing session manager that matches the given URL. This
     * search is based on the URL of existing session managers.  It is not
     * a direct comparison of strings, it tries to at least deal with
     * differences in slashes and so on between the URLs.
     * @param serverURL the url to search for
     * @return the matching session manager, or null if no session managers
     * match
     */
    public static ServerSessionManager findSessionManager(String serverURL) {
        ServerSessionManager out = null;

        synchronized (managers) {
            for (ServerSessionManager m : managers.getAll()) {
                String mURL = m.getServerURL();
                if (urlsMatch(serverURL, mURL)) {
                    out = m;
                    break;
                }
            }
        }

        return out;
    }

    /**
     * Get all session managers
     * @return a list of all known session managers
     */
    public static Collection<ServerSessionManager> getAll() {
        return managers.getAll();
    }

    /**
     * Get the primary session manager
     * @return the primary session manager, if one has been set
     */
    public synchronized static ServerSessionManager getPrimary() {
        return primaryLoginManager;
    }

    /**
     * Set the primary session manager
     * @param primary the primary session manager, or null if the client no
     * longer has a primary server
     */
    public synchronized static void setPrimary(ServerSessionManager primary) {
        LoginManager.primaryLoginManager = primary;

        // notify listeners
        for (PrimaryServerListener l : listeners) {
            l.primaryServer(primary);
        }
    }

    /**
     * Add a primary server listener.  This listener will be notified
     * immediately with the current value of the primary server.  It will
     * also be notified in the future of any changes to the primary server
     * value.
     * @param listener the listener to add
     */
    public synchronized static void addPrimaryServerListener(PrimaryServerListener listener) {
        listeners.add(listener);
        
        // notify the new listener of the current value immediately
        if (LoginManager.primaryLoginManager != null) {
            listener.primaryServer(LoginManager.primaryLoginManager);
        }
    }

    /**
     * Remove a primary server listener
     * @param listener the listener to remove
     */
    public static void removePrimaryServerListener(PrimaryServerListener listener) {
        listeners.remove(listener);
    }

    /**
     * Compare two strings as URLs
     * @param u1 the first URL
     * @param u2 the second URL
     * @return true if they match, or false if not
     */
    private static boolean urlsMatch(String u1, String u2) {
        // take off trailing slashes
        if (u1.endsWith("/")) {
            u1 = u1.substring(0, u1.length() - 1);
        }

        if (u2.endsWith("/")) {
            u2 = u2.substring(0, u2.length() - 1);
        }

        return u1.equals(u2);
    }

    /**
     * A cache of session manager objects.  This cache maintains real
     * references to any session with active sessions, but only weak references
     * to a session with no active sessions.  When a session is removed from
     * the cache, all associated plugins are cleaned up.
     */
    static class ManagerCache {
        private final Map<String, Reference<ServerSessionManager>> refMap =
                new HashMap<String, Reference<ServerSessionManager>>();

        public synchronized ServerSessionManager get(String key) {
            Reference<ServerSessionManager> ref = refMap.get(key);
            if (ref == null) {
                return null;
            }

            return ref.get();
        }

        public synchronized Collection<ServerSessionManager> getAll() {
            List<ServerSessionManager> out = new ArrayList<ServerSessionManager>();
            for (Reference<ServerSessionManager> ref : refMap.values()) {
                ServerSessionManager mgr = ref.get();
                if (mgr != null) {
                    out.add(mgr);
                }
            }

            return out;
        }

        public synchronized ServerSessionManager put(String key,
                                                     ServerSessionManager value)
        {
            // put the reference
            CacheReference ref = new CacheReference(value);
            Reference<ServerSessionManager> old = refMap.put(key, ref);
            if (old == null) {
                return null;
            }

            return old.get();
        }

        public synchronized ServerSessionManager remove(String key) {
            Reference<ServerSessionManager> ref = refMap.remove(key);
            if (ref == null) {
                return null;
            }

            return ref.get();
        }

        class CacheReference extends SoftReference<ServerSessionManager>
            implements SessionLifecycleListener, SessionStatusListener
        {
            // a strong reference that we hold as long as there are sessions
            // associated with the given session manager
            private ServerSessionManager strongRef;

            public CacheReference(ServerSessionManager manager)
            {
                super (manager);

                manager.addLifecycleListener(this);
                if (manager.getAllSessions().size() > 0) {
                    strongRef = manager;
                }
            }

            public void sessionCreated(WonderlandSession session) {
                session.addSessionStatusListener(this);
            }

            public void primarySession(WonderlandSession session) {
                // ignore
            }

            public void sessionStatusChanged(WonderlandSession session,
                                             Status status)
            {
                // update our reference if the session status changes
                if (status == Status.CONNECTED ||
                    status == Status.DISCONNECTED)
                {
                    if (session.getSessionManager().getAllSessions().size() > 0) {
                        strongRef = session.getSessionManager();
                    } else {
                        strongRef = null;
                    }
                }
            }
        }
    }
}
