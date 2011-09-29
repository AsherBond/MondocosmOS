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
package org.jdesktop.wonderland.modules.securitygroups.server;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.common.login.CredentialManager;
import org.jdesktop.wonderland.modules.security.common.Principal;
import org.jdesktop.wonderland.modules.security.common.Principal.Type;
import org.jdesktop.wonderland.modules.security.server.service.UserPrincipalResolver;
import org.jdesktop.wonderland.modules.security.server.service.util.AgedValue;
import org.jdesktop.wonderland.modules.security.server.service.util.TimeBasedCache;
import org.jdesktop.wonderland.modules.securitygroups.common.GroupDTO;
import org.jdesktop.wonderland.modules.securitygroups.common.GroupUtils;
import org.jdesktop.wonderland.server.auth.ServerAuthentication;

/**
 * Implementation of a user principal resolver using the group
 * web services API.
 * @author jkaplan
 */
public class WebServiceUserPrincipalResolver implements UserPrincipalResolver {
    private static final Logger logger =
            Logger.getLogger(WebServiceUserPrincipalResolver.class.getName());
    
    private static final String SERVER_URL_PROP = "wonderland.web.server.url";
    private static final String BASE_URL = getBaseURL();

    // property for cache timeout, in milliseconds -- default is 10 minutes
    private static final String CACHE_TIMEOUT_PROP =
            WebServiceUserPrincipalResolver.class.getName() + ".CacheTimeout";
    private static final String CACHE_TIMEOUT_DEFAULT =
            String.valueOf(10 * 60 * 1000);

    // property for default group name
    private static final String DEFAULT_GROUP_PROP =
            WebServiceUserPrincipalResolver.class.getName() + ".DefaultGroup";
    private static final String DEFAULT_GROUP_DEFAULT = "users";

    // the cache of principals by username.  This is a timed cache, so data
    // lasts for a maximum of 10 minutes before being queried again
    private final TimeBasedCache<String, Set<Principal>> cache;

    // the default group name
    private final String defaultGroup;

    /**
     * Get an instance of this singleton
     */
    public static WebServiceUserPrincipalResolver getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Singleton constructor -- use getInstance() instead
     */
    protected WebServiceUserPrincipalResolver() {
        String cacheTime = System.getProperty(CACHE_TIMEOUT_PROP, 
                                              CACHE_TIMEOUT_DEFAULT);
        cache = new TimeBasedCache<String, Set<Principal>>(Long.parseLong(cacheTime));
        defaultGroup = System.getProperty(DEFAULT_GROUP_PROP,
                                          DEFAULT_GROUP_DEFAULT);
    }

    public Set<Principal> getPrincipals(String username, boolean blocking) {
        // see if we have the value in the cache
        synchronized (cache) {
            AgedValue<Set<Principal>> cached = cache.get(username);
            if (cached != null) {
                // found a value in the cache -- return it
                return cached.getValue();
            }
        }

        // see if we are allowed to block to find a result
        if (blocking) {
            return getRemotePrincipals(username);
        }

        // not cached and can't block -- return null
        return null;
    }
    
    public Set<Principal> getRemotePrincipals(String username) {
        Set<Principal> out = new LinkedHashSet<Principal>();
        
        // add the user principal based on this user's username
        out.add(new Principal(username, Type.USER));

        // add the default group principal
        out.add(new Principal(defaultGroup, Type.EVERYBODY));

        // get the credential manager to use
        CredentialManager cm = ServerAuthentication.getAuthenticationService();
        try {
            // request this user's groups
            Set<GroupDTO> groups =
                    GroupUtils.getGroupsForUser(BASE_URL, username, false, cm);
            for (GroupDTO g : groups) {
                out.add(new Principal(g.getId(), Type.GROUP));
            }

            // add to the cache
            synchronized (cache) {
                cache.put(username, new AgedValue(out));
            }
        } catch (IOException ex) {
            logger.log(Level.WARNING, "Error reading groups for " + username +
                       " from " + BASE_URL, ex);
        } catch (JAXBException ex) {
            logger.log(Level.WARNING, "Error reading groups for " + username +
                       " from " + BASE_URL, ex);
        }

        return out;
    }

    public void invalidate(String username) {
        synchronized (cache) {
            cache.remove(username);
        }
    }

    private static String getBaseURL() {
        String baseURL = System.getProperty(SERVER_URL_PROP);
        if (baseURL.endsWith("/")) {
            baseURL = baseURL.substring(0, baseURL.length() - 1);
        }

        return baseURL;
    }

    private static final class SingletonHolder {
        private static final WebServiceUserPrincipalResolver INSTANCE =
                new WebServiceUserPrincipalResolver();
    }
}
