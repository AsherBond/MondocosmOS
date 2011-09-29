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
package org.jdesktop.wonderland.modules.contentrepo.client;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;

/**
 * Common place to register repository instances
 * @author jkaplan
 */
public class ContentRepositoryRegistry {
    private static final Logger logger =
            Logger.getLogger(ContentRepositoryRegistry.class.getName());

    /** a map of remote repositories, mapped by session */
    private Map<ServerSessionManager, ContentRepository> repos =
            new HashMap<ServerSessionManager, ContentRepository>();

    /** the local repository */
    private ContentCollection localRepo;

    public static ContentRepositoryRegistry getInstance() {
        return SingletonHolder.REGISTRY;
    }

    private ContentRepositoryRegistry() {        
    }

    /**
     * Register a connection to a repository
     * @param session the session to register
     * @param repo the repository associated with that session
     */
    public void registerRepository(ServerSessionManager session,
                                   ContentRepository repo)
    {
        logger.fine("[ContentRepositoryRegistry] Register repository " + repo +
                    " for session " + session + " on " + this);
        repos.put(session, repo);
    }

    /**
     * Unregister a session
     * @param session the session to unregister
     */
    public void unregisterRepository(ServerSessionManager session)
    {
        logger.fine("[ContentRepositoryRegistry] Unregister repository for " +
                    "session " + session + " on " + this);
        repos.remove(session);
    }

    /**
     * Find a repository for the given session
     * @param session the session to find a repository for
     * @return the repository associated with the given session,
     * or null if it can't be found
     */
    public ContentRepository getRepository(ServerSessionManager session) {
        ContentRepository out = repos.get(session);
        logger.fine("[ContentRepositoryRegistry] Get repository for " +
                    session + " returns " + out + " on " + this);
        return repos.get(session);
    }

    /**
     * Register a local repository.  Throws an illegal argument exception
     * if there is already a local repository registered.  Registering a null
     * repository will remove the currently set repository.
     * @param collection the local repository, or null to unregister.
     */
    public void registerLocalRepository(ContentCollection collection) {
        if (collection != null && localRepo != null) {
            throw new IllegalStateException("Local repository already registered");
        }

        this.localRepo = collection;
    }

    /**
     * Get the local repository
     * @return the local repository, or null if none is registered
     */
    public ContentCollection getLocalRepository() {
        return localRepo;
    }


    static class SingletonHolder {
        private static final ContentRepositoryRegistry REGISTRY =
                new ContentRepositoryRegistry();
    }
}
