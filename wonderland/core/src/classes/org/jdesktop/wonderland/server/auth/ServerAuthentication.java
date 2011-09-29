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
package org.jdesktop.wonderland.server.auth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.login.AuthenticationException;
import org.jdesktop.wonderland.common.login.AuthenticationInfo;
import org.jdesktop.wonderland.common.login.AuthenticationManager;
import org.jdesktop.wonderland.common.login.AuthenticationService;

/**
 * Singelton to handle authenticating the Darkstar server to the Wonderland
 * web server.  It is assumed that the Darkstar server has the admin role
 * in the groups defined by the web server.
 * @author jkaplan
 */
public class ServerAuthentication {
    private static final Logger logger =
            Logger.getLogger(ServerAuthentication.class.getName());
    
    // properties we read
    private static final String AUTH_URL_PROP = "wonderland.authentication.url";
    private static final String SERVER_URL_PROP = "wonderland.web.server.url";
    private static final String NOAUTH_SERVICE_PATH =
            "security-session-noauth/security-session-noauth/identity";
    private static final String AUTH_SERVICE_PATH =
            "security-session-auth/security-session-auth/identity";


    private static final String DARKSTAR_USERNAME = "sgs.auth.username";
    private static final String DARKSTAR_USERNAME_DEFAULT = "darkstar";
    private static final String DARKSTAR_PASSWORD_FILE = "sgs.password.file";

    // the authentication server
    private AuthenticationService auth;

    /**
     * Get an instance of the authentication service
     */
    public static AuthenticationService getAuthenticationService() {
        return SingletonHolder.INSTANCE.getAuth();
    }

    // singleton -- use getInstance instead of this constructor
    protected ServerAuthentication() {
        // get the authentication URL
        String authUrl = System.getProperty(AUTH_URL_PROP);
        if (authUrl == null) {
            // fall back to the default, based on the web server URL
            authUrl = System.getProperty(SERVER_URL_PROP);
        }

        // the login username
        String username = System.getProperty(DARKSTAR_USERNAME,
                                             DARKSTAR_USERNAME_DEFAULT);

        // get the login password file.  If this is null, we will do
        // a login with no authentication
        String passwordFile = System.getProperty(DARKSTAR_PASSWORD_FILE);

        try {
            if (passwordFile == null) {
                auth = noAuthLogin(authUrl, username);
            } else {
                auth = authLogin(authUrl, username, passwordFile);
            }
        } catch (AuthenticationException ae) {
            throw new IllegalStateException("Error authenticating Darkstar " +
                                            "server", ae);
        }
    }

    /**
     * Get the authentication service.
     * @return the authentication service
     */
    protected AuthenticationService getAuth() {
        return auth;
    }

    /**
     * Log in with no credentials
     * @param username the username
     */
    protected AuthenticationService noAuthLogin(String authUrl, String username)
        throws AuthenticationException
    {
        if (!authUrl.endsWith("/")) {
            authUrl += "/";
        }
        authUrl += NOAUTH_SERVICE_PATH;

        AuthenticationInfo info = new AuthenticationInfo(
                AuthenticationInfo.Type.NONE, authUrl);
        return AuthenticationManager.login(info, username, "Darkstar server");
    }

    /**
     * Log in with the given username and password
     * @param username the username to log in with
     * @param password the password to log in with
     */
    protected AuthenticationService authLogin(String authUrl, String username,
                                              String passwordFile)
            throws AuthenticationException
    {
        if (!authUrl.endsWith("/")) {
            authUrl += "/";
        }
        authUrl += AUTH_SERVICE_PATH;

        String password;

        // read the password from the password file
        try {
            File f = new File(passwordFile);
            BufferedReader br = new BufferedReader(new FileReader(f));
            password = br.readLine();
        } catch (IOException ioe) {
            throw new AuthenticationException("Error reading password file " +
                                              passwordFile, ioe);
        }

        AuthenticationInfo info = new AuthenticationInfo(
                AuthenticationInfo.Type.WEB_SERVICE, authUrl);
        return AuthenticationManager.login(info, username, password);
    }

    private static final class SingletonHolder {
        private static final ServerAuthentication INSTANCE =
                new ServerAuthentication();
    }

}
