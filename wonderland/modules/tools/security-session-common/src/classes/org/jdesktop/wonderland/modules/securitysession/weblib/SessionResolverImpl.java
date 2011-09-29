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

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jdesktop.wonderland.modules.security.weblib.serverauthmodule.SessionResolver;

/**
 * A wrapper implementation of SessionResolver that passes all calls through
 * to the underlying session resolver.
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class SessionResolverImpl implements SessionResolver {

    public void initialize(Map opts) {
        SessionManager sm = SessionManagerFactory.getSessionManager();
        sm.initialize(opts);
    }

    public String getUserId(String token) {        
        SessionManager sm = SessionManagerFactory.getSessionManager();
        return sm.getUserId(token);
    }

    public String handleUnauthenticated(HttpServletRequest request,
                                        boolean mandatory,
                                        HttpServletResponse response)
        throws IOException
    {
        SessionManager sm = SessionManagerFactory.getSessionManager();
        return sm.handleUnauthenticated(request, mandatory, response);
    }
}
