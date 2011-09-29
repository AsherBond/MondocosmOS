/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

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
package org.jdesktop.wonderland.modules.securitysession.noauth.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.jdesktop.wonderland.common.login.AuthenticationInfo;
import org.jdesktop.wonderland.front.admin.ServerInfo;
import org.jdesktop.wonderland.utils.Constants;

/**
 *
 * @author jkaplan
 */
public class SecuritySetupContextListener implements ServletContextListener {
    private static final String SECURITY_PATH =
            "security-session-noauth/security-session-noauth/identity";

    public void contextInitialized(ServletContextEvent evt) {
        // get the URL for the web server
        String serverUrl = System.getProperty(Constants.WEBSERVER_URL_PROP);
        serverUrl += SECURITY_PATH;
        
        // set the login type and URL
        AuthenticationInfo authInfo = new AuthenticationInfo(
                                       AuthenticationInfo.Type.NONE, serverUrl);
        ServerInfo.getServerDetails().setAuthInfo(authInfo);

        // get the internal URL for the web server
        serverUrl = System.getProperty(Constants.WEBSERVER_URL_INTERNAL_PROP);
        serverUrl += SECURITY_PATH;
        authInfo = new AuthenticationInfo(AuthenticationInfo.Type.NONE, serverUrl);
        ServerInfo.getInternalServerDetails().setAuthInfo(authInfo);
    }

    public void contextDestroyed(ServletContextEvent evt) {
        // nothing to do here
    }
}
