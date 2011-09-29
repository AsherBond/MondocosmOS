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
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jkaplan
 */
public interface SessionResolver {
    public static final String COOKIE_NAME = "WonderlandAuthCookie";

    /**
     * Set up this session resolver with the options from the given map.
     * The map is the one passed in to the SAM during intialization
     * @param opts the map passed in to the initialization of the SAM
     */
    public void initialize(Map opts);

    /**
     * Get the userId associated with the given token
     * @param token the token to use
     * @return the user id for the given token or null if the token is
     * invalid or null
     */
    public String getUserId(String token);

    /**
     * Handle a user who is attempting to log in without credentials. The
     * resolver can return one of two things in this case:
     * returning a user id will treat the unauthenticated user as having
     * the given user id, identical to if getUserId() returned a valid
     * token. Returning null indicates that the resolver will send a
     * response directly, and there is no need for the SAM to do any
     * more work in response to this request.  A typical response from
     * a module would be a further challenge or a redirect to a login page.
     * @param request the request for a page
     * @param mandatory whether authentication is mandatory for the given
     * page
     * @param response the response to send to the user
     * @return a user id or null, as described above
     * @throws IOException if there is an error sending the response
     */
    public String handleUnauthenticated(HttpServletRequest request,
                                        boolean mandatory,
                                        HttpServletResponse response)
        throws IOException;
}
