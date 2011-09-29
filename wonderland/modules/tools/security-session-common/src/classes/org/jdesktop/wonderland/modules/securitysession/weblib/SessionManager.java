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
package org.jdesktop.wonderland.modules.securitysession.weblib;

import org.jdesktop.wonderland.modules.security.weblib.serverauthmodule.SessionResolver;

/**
 *
 * @author jkaplan
 */
public interface SessionManager extends SessionResolver {
    /**
     * Login to the session with the given credentials.  This will return
     * a UserRecord for the given user on success, or null if the
     * credentials are incorrect.  Calling login guarantees that a new, unique
     * token will be included in the returned UserRecord.  If login is called
     * more than once for the given user, this will result in multiple different
     * tokens mapping to the same user.
     * <p>
     * The credentials that need to be passed in are specific to each
     * particular session manager.
     *
     * @param userId the id to login as
     * @param credentials the login credentials to use
     * @return a UserRecord with a unique token for the given user
     * @throws SessionLoginException if there is an error logging in.  This is
     * distinct from the login failing due to bad credentials, in which
     * case the method returns null but does not throw an exception.  An
     * exception is thrown in the case of an exceptional error, such as the
     * wrong type of credentials being passed in.
     */
    public UserRecord login(String userId, Object... credentials)
            throws SessionLoginException;

    /**
     * Get a user record by userId.  If there are multiple sessions
     * associated with the given user, an aribtrary session will be returned.
     * @param userId the name of the user to get
     * @return the user with the given name, or null if no users exist with
     * the given name.
     */
    public UserRecord get(String userId);

    /**
     * Get the user corresponding to the given token
     * @param token the user's id
     * @return the user with the given token, or null if no user maps to the
     * given token
     */
    public UserRecord getByToken(String token);

    /**
     * Remove the session associated with the given token.
     * @param token the session token to invalidate
     * @return the user associated with the given token, or null if no
     * user is associated with the token
     */
    public UserRecord logout(String token);
}
