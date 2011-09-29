/*
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
package org.jdesktop.wonderland.common.login;

import javax.naming.directory.Attributes;

/**
 * An interface for working with an authentication service.  Typically, the
 * authentication service will be a restful web service that performs
 * authentication, lookup and token validation.
 * <p>
 * An authentication service manages credentials for connecting to a particular
 * authentication source. The authentication manager is used to manage
 * AuthenticationServices for different sources.
 * <p>
 * AuthenticationService provides an implementation of CredentialManager, a
 * more general API for different authentication sources.
 * @author jkaplan
 */
public interface AuthenticationService extends CredentialManager{
    /**
     * Determine if the token is valid.  Identical to calling
     * <code>isTokenValid(getAuthenticationToken())</code>
     * @return true if the token for this service is valid, or false if not
     */
    public boolean isTokenValid() throws AuthenticationException;
    
    /**
     * Determine if the given token is valid
     * @param token the token to check
     * @return true if the token is valid, or false if not
     */
    public boolean isTokenValid(String token) throws AuthenticationException;
    
    /**
     * Get the name of the authentication cookie
     * @return the name of the authentication cookie
     */
    public String getCookieName() throws AuthenticationException;

    /**
     * Get all attributes for the given token.
     * @param token the token to get attributes for
     * @return the attributes for this user
     */
    public Attributes getAttributes(String token, String... attributeNames)
            throws AuthenticationException;

    /**
     * Get all attributes for the user with the given user id.  The current user
     * must be an administrator for this function to work
     * @param userId the userId to get attributes for
     * @return the attributes for the given user
     */
    public Attributes read(String userId, String... attributeNames)
            throws AuthenticationException;

    /**
     * Log the current user out of the system
     */
    public void logout() throws AuthenticationException;
}
