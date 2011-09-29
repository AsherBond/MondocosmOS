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
package org.jdesktop.wonderland.client.modules;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.client.login.ServerStatusListener;
import org.jdesktop.wonderland.common.modules.ModuleInfo;
import org.jdesktop.wonderland.common.modules.ModuleList;

/**
 * A cache of all modules on a particular server
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ServerCache {

    /* The base url of the server: http://<server name>:<port> */
    private String serverURL = null;
    
    /* The session listener to remove this cache when all sessions disconnect
     * from this server
     */
    private SessionListener listener;

    /* A hashmap of module names and caches of their information */
    private Map<String, CachedModule> cachedModules = new HashMap();
    
    /* The error logger */
    private Logger logger = Logger.getLogger(ServerCache.class.getName());
    
    /* A hashmap of base server urls and their ServerCache objects */
    private static final Map<String, ServerCache> serverCaches = new HashMap();
    
    /** Constructor, takes base URL  of the server */
    public ServerCache(String serverURL) {

        this.serverURL = serverURL;
        this.reload();

        // create the associated with this cache
        ServerSessionManager ssm = LoginManager.findSessionManager(serverURL);
        if (ssm == null) {
            logger.log(Level.WARNING, "Cannot find session for " + serverURL);
        } else {
            this.listener = new SessionListener(ssm);
        }
    }
    
    /**
     * Returns an instance of the ServerCache object for the given server URL
     * (http://<server name>:<port>). If the cache for the server does not
     * exist yet, creates it.
     *
     * @param serverURL The base URL of the server
     * @return The cache of information pertaining to the server
     */
    public static ServerCache getServerCache(String serverURL) {
        synchronized (serverCaches) {
            ServerCache cache = serverCaches.get(serverURL);
            if (cache == null) {
                cache = new ServerCache(serverURL);
                serverCaches.put(serverURL, cache);
            }
            return cache;
        }
    }

    /**
     * Remove a server cache for the given URL
     * @param serverURL the base url of the server to remove
     */
    static void removeServerCache(String serverURL) {
        synchronized (serverCaches) {
            serverCaches.remove(serverURL);
        }
    }

    /**
     * Returns a collection of cached module names. If no module names exist,
     * returns an empty collection.
     * 
     * @return A collection of cached module names
     */
//    public Collection<String> getModuleNames() {
//        return this.cachedModules.keySet();
//    }
    
    /**
     * Returns a module given its unique name, or null if none exists.
     * 
     * @param moduleName The unique name of the module
     * @return The Module or null if none with the name exists
     */
    public CachedModule getModule(String moduleName) {
        // Look for the cached module. If we can't find it, see if it exists
        // on the server nevertheless.
        CachedModule cm = this.cachedModules.get(moduleName);
        if (cm == null) {
            ModuleInfo info = ModuleUtils.fetchModuleInfo(this.serverURL, moduleName);
            if (info == null) {
                logger.info("[MODULES] No module information found for " + moduleName);
                return null;
            }
            cm = new CachedModule(serverURL, info);
            this.cachedModules.put(moduleName, cm);
        }
        return cm;
    }
    
    /**
     * Reloads the list of modules from the server from scratch
     */
    private synchronized void reload() {
        /* Clear out the existing cache, load from the server */
        this.cachedModules.clear();
        ModuleList list = ModuleUtils.fetchModuleList(this.serverURL);
        if (list == null) {
            logger.info("[MODULES] No module information found");
            return;
        }
        
        /* Loop through each and create the module object, insert into map */
        for (ModuleInfo moduleInfo : list.getModuleInfos()) {
            CachedModule cachedModule = new CachedModule(serverURL, moduleInfo);
            this.cachedModules.put(moduleInfo.getName(), cachedModule);
        }
    }

    private class SessionListener implements ServerStatusListener {
        private ServerSessionManager manager;

        SessionListener(ServerSessionManager manager) {
            this.manager = manager;
            manager.addServerStatusListener(this);
        }

        public void connecting(ServerSessionManager manager, String message) {
            // ignore
        }

        public void connected(ServerSessionManager sessionManager) {
            // ignore
        }

        public void disconnected(ServerSessionManager sessionManager) {
            // flush the cache
            logger.fine("[ServerCache] removing cache for " + serverURL);
            ServerCache.removeServerCache(serverURL);
        }
    }
}
