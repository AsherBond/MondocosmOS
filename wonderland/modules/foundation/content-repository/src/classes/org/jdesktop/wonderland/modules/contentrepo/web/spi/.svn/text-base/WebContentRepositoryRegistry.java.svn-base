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
package org.jdesktop.wonderland.modules.contentrepo.web.spi;

import java.util.logging.Logger;
import javax.servlet.ServletContext;
import org.jdesktop.wonderland.utils.ServletPropertyUtil;

/**
 * Common place to register repository instances
 * @author jkaplan
 */
public class WebContentRepositoryRegistry {
    private static final Logger logger =
            Logger.getLogger(WebContentRepositoryRegistry.class.getName());

    public static final String KEY = WebContentRepositoryRegistry.class.getName();

    private static final String CONTEXT_PROP =
            WebContentRepositoryRegistry.class.getPackage().getName() +
            ".contextPath";
    private static final String CONTEXT_DEFAULT =
            "/content-repository/wonderland-content-repository";

    public static WebContentRepositoryRegistry getInstance() {
        return SingletonHolder.REGISTRY;
    }

    private WebContentRepositoryRegistry() {
    }

    /**
     * Register a connection to a repository
     * @param context the servlet context to register in
     * @param repo the repository associated with that session
     */
    public void registerRepository(ServletContext context,
                                   WebContentRepository repo)
    {
        ServletContext remote = getRemoteContext(context);
        if (remote != null) {
            remote.setAttribute(KEY, repo);
        }
    }

    /**
     * Unregister a repository
     * @param context the servlet context to unregister from
     */
    public void unregisterRepository(ServletContext context) {
        ServletContext remote = getRemoteContext(context);
        if (remote != null) {
            remote.removeAttribute(KEY);
        }
    }

    /**
     * Find a repository for the given context
     * @param context the ServletContext to find a repository for
     * @return the repository associated with the given session,
     * or null if it can't be found
     */
    public WebContentRepository getRepository(ServletContext context) {
        ServletContext remote = getRemoteContext(context);
        if (remote != null) {
            return (WebContentRepository) remote.getAttribute(KEY);
        }

        return null;
    }

    /**
     * Get the name of the remote context
     * @param context the local context
     */
    public ServletContext getRemoteContext(ServletContext context) {
        String name = ServletPropertyUtil.getProperty(CONTEXT_PROP, context,
                                                      CONTEXT_DEFAULT);
        ServletContext remote = context.getContext(name);
        if (remote == null) {
            logger.warning("Servlet context " + name + " not found.");
        }

        return remote;
    }


    static class SingletonHolder {
        private static final WebContentRepositoryRegistry REGISTRY =
                new WebContentRepositoryRegistry();
    }
}
