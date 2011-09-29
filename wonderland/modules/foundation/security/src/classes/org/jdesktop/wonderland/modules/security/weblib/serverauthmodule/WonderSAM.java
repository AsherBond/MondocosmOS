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
package org.jdesktop.wonderland.modules.security.weblib.serverauthmodule;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.Principal;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import javax.security.auth.message.config.ServerAuthContext;
import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.jdesktop.wonderland.modules.security.weblib.UserGroupPrincipal;

/**
 *
 * @author jkaplan
 */
public class WonderSAM implements ServerAuthModule, ServerAuthContext {
    private static final Logger logger =
            Logger.getLogger(WonderSAM.class.getName());

    public static final String USERID_SESSION_ATTR = "__userId";

    private static final String SESSION_RESOLVER_OPT = "session.resolver.class";
    private static final String GROUP_RESOLVER_OPT = "group.resolver.class";
    private static final String LOGIN_PAGE_OPT = "login.page";

    private MessagePolicy requestPolicy;
    private MessagePolicy resPolicy;
    private CallbackHandler handler;
    private Map opts;
    
    private SessionResolver sessionManager;
    private GroupResolver groupManager;
    
    public void initialize(MessagePolicy reqPolicy, MessagePolicy resPolicy,
            CallbackHandler cBH, Map opts)
            throws AuthException
    {
        logger.fine("Configure WonderSAM");

        this.requestPolicy = reqPolicy;
        this.resPolicy = resPolicy;
        this.handler = cBH;
        this.opts = opts;

        try {
            // create the session resolver based on the specified class
            String smClass = (String) opts.get(SESSION_RESOLVER_OPT);
            sessionManager = createSessionManager(smClass);
            sessionManager.initialize(opts);

            // create the group resolver based on the specified class
            String grClass = (String) opts.get(GROUP_RESOLVER_OPT);
            groupManager = createGroupManager(grClass);
            groupManager.initialize(opts);
        } catch (IllegalStateException ise) {
            // make sure to log any errors
            logger.log(Level.WARNING, "Error instantiating helper", ise);
            throw ise;
        }
    }

    public Class[] getSupportedMessageTypes() {
        return new Class[] { HttpServletRequest.class,
                             HttpServletResponse.class };
    }

    public AuthStatus validateRequest(MessageInfo msgInfo, Subject client,
                                      Subject server)
            throws AuthException
    {
        HttpServletRequest req = (HttpServletRequest) msgInfo.getRequestMessage();
        HttpServletResponse res = (HttpServletResponse) msgInfo.getResponseMessage();
        boolean mandatory = requestPolicy.isMandatory();

        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Processing request for " + req.getRequestURI() +
                        " security required: " + mandatory +
                        " secure: " + req.isSecure());
        }

        try {
            // process authentication cookie, if any
            String userId = processAuthCookie(req.getCookies());
            if (userId == null) {
                // if we didn't find an authentication cookie in the request
                // see if we have a session with a user id stored in it
                userId = processSessionAuth(req.getSession(false));
            }

            // if we still haven't found a user id, ask the session manager
            // to find one for us.  If this returns null, don't do any more
            // work, assume the session manager took care of it
            if (userId == null) {
                userId = sessionManager.handleUnauthenticated(req, mandatory, res);
            }

            // if we found a valid user, initialize user id and groups
            if (userId != null) {
                try {
                    setupUser(client, userId);
                } catch (UnsupportedCallbackException uce) {
                    AuthException ae = new AuthException();
                    ae.initCause(uce);
                    throw ae;
                }
            } else if (requestPolicy.isMandatory()) {
                // otherwise, if the login is mandatory, it's a failure, since
                // there was no user id
                return AuthStatus.SEND_FAILURE;
            }
        } catch (IOException ioe) {
            AuthException ae = new AuthException();
            ae.initCause(ioe);
            throw ae;
        }

        // if we made it this far, we authenticated properly or authentication
        // wasn't required.  Good news!
        return AuthStatus.SUCCESS;
    }

    /**
     * Find the cookie specifying the user's SSO token. Validate the token
     * with the session manager, returning the userId this token maps to.
     * @param cookies a list of cookies
     * @return the userId associated with the user's set of authentication
     * cookies, or null if no user id is associated or the token is invalid.
     */
    protected String processAuthCookie(Cookie[] cookies) {
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase(SessionResolver.COOKIE_NAME)) {
                try {
                    String value = URLDecoder.decode(cookie.getValue(), "UTF-8");
                    String userId = sessionManager.getUserId(value);

                    logger.fine("User id for token " + value + " is " + userId);

                    if (userId != null) {
                        return userId;
                    }
                } catch (UnsupportedEncodingException nsee) {
                    throw new IllegalStateException(nsee);
                }
            }
        }

        return null;
    }

    /**
     * Search the session for a stored userId
     * @param session the session to search, or null if there is no session
     * @return the userId, or null if it can't be found
     */
    protected String processSessionAuth(HttpSession session) {
        if (session == null) {
            return null;
        }

        return (String) session.getAttribute(USERID_SESSION_ATTR);
    }

    /**
     * Setup the information associated with a user
     * @param subject the subject to set up
     * @param userId the user's id
     */
    protected void setupUser(Subject s, String userId)
            throws IOException, UnsupportedCallbackException
    {
        // get the set of groups from the resolver
        String[] groupNames = groupManager.getGroupsForUser(userId);

        // create a principal with the user and the groups they belong to
        Principal p = new UserGroupPrincipal(userId, groupNames);

        // use a callback to set the principal and groups for this user
        handler.handle(new Callback[] {
                    new CallerPrincipalCallback(s, p),
                    new GroupPrincipalCallback(s, groupNames) });
    }

    public AuthStatus secureResponse(MessageInfo info, Subject s)
            throws AuthException
    {
        return AuthStatus.SEND_SUCCESS;
    }

    public void cleanSubject(MessageInfo info, Subject s) throws AuthException {
        s.getPrincipals().clear();
    }

    /**
     * Create a session manager for use by this authentication manager.
     * @param className the fully-qualified class name of the session manager
     * to create, or null to create the default session manager.
     * @return the newly created session manager
     */
    private SessionResolver createSessionManager(String className) {
        if (className == null) {
            throw new IllegalStateException("No session resolver specified");
        }

        try {
            Class<SessionResolver> clazz =
                    (Class<SessionResolver>) Class.forName(className);
            return clazz.newInstance();
        } catch (InstantiationException ie) {
            throw new IllegalStateException("Unable to create class " +
                                            className, ie);
        } catch (IllegalAccessException iae) {
            throw new IllegalStateException("Unable to create class " +
                                            className, iae);
        } catch (ClassNotFoundException cnfe) {
            throw new IllegalStateException("Unable to find class " +
                                            className, cnfe);
        }
    }

    /**
     * Create a group manager for use by this authentication manager.
     * @param className the fully-qualified class name of the group manager
     * to create, or null to create the default group manager.
     * @return the newly created group manager
     */
    private GroupResolver createGroupManager(String className) {
        if (className == null) {
            throw new IllegalStateException("No group resolver specified");
        }

        try {
            Class<GroupResolver> clazz =
                    (Class<GroupResolver>) Class.forName(className);
            return clazz.newInstance();
        } catch (InstantiationException ie) {
            throw new IllegalStateException("Unable to create class " +
                                            className, ie);
        } catch (IllegalAccessException iae) {
            throw new IllegalStateException("Unable to create class " +
                                            className, iae);
        } catch (ClassNotFoundException cnfe) {
            throw new IllegalStateException("Unable to find class " +
                                            className, cnfe);
        }
    }
}
