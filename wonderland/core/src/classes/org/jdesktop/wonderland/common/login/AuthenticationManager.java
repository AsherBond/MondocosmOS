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
package org.jdesktop.wonderland.common.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * The authentication manager manages multiple authentication sources.  Each
 * source is identified by URL.
 * @author jkaplan
 */
public class AuthenticationManager {
    private static final Logger logger =
            Logger.getLogger(AuthenticationManager.class.getName());

    private static final Map<String, AuthenticationServiceImpl> services =
            new HashMap<String, AuthenticationServiceImpl>();

    /**
     * Get an existing authentication service for the given URL
     * @param URL the authentication URL for the server
     * @return an existing authentication service for the given URL, or null
     * if no service exists
     */
    public synchronized static AuthenticationService get(String url) {
        return getImpl(url);
    }

    /**
     * Log in to the server identified by the given authentication information
     * with the given credentials.  The mechanism for login will depend on the
     * type of authentication.
     * <p>
     * If an existing authentication service exists for the authentication url
     * contained in the info object, the service will first be test for
     * validity using isTokenValid().  If the token is valid, the existing
     * service will be returned.  If the token is not valid, the service will
     * be updated to use the new credentials.  Existing instances of the
     * service should still work, although information like username and token
     * may have changed.  This implies that there can only be a single login
     * to a particular URL active at any time.
     *
     * @param info the information about the server to authenticate to
     * @param credentials the credentials to use for login
     * @return the AuthenticationService for the given credentials.
     * @throws InvalidLoginException if the authentication credentials are
     * invalid
     * @throws AuthenticationException if there is an error authenticating
     */
    public synchronized static AuthenticationService login(
            AuthenticationInfo info, String username, Object... credentials)
        throws AuthenticationException
    {
        AuthenticationServiceImpl out = getImpl(info.getAuthURL());
        if (out == null) {
            out = new AuthenticationServiceImpl(info.getAuthURL(), username);
            services.put(info.getAuthURL(), out);
        }

        // see if out has a valid token
        if (out.getAuthenticationToken() != null && out.isTokenValid()) {
            return out;
        }

        // if we got here, it means we need to actually log in.  How we do
        // this depends on the login type
        String token;
        switch (info.getType()) {
            case NONE:
                token = noAuthLogin(info.getAuthURL(), username, credentials);
                break;
            case WEB_SERVICE:
                token = webServiceLogin(info.getAuthURL(), username, credentials);
                break;
            default:
                throw new AuthenticationException("Login type " +
                        info.getType() + " not supported");
        }

        // we have successfully logged in -- set the user name and
        // authentication token
        out.setUsername(username);
        out.setAuthenticationToken(token);

        return out;
    }

    /**
     * Log in to the no auth login service.
     * @param url the url to log in to
     * @param username the username to login with
     * @param credentials an array of credentials to send.  In this case,
     * the first credential will be used for full name and the second (if it
     * exists) will be used for email address.
     * @return the token returned by the login attempt
     */
    private static String noAuthLogin(String url, String username,
                                      Object... credentials)
        throws AuthenticationException
    {
        String fullname = null;
        String email = null;

        if (credentials.length == 1) {
            // only full name specified
            fullname = (String) credentials[0];
        } else if (credentials.length == 2) {
            // full name and email specified
            fullname = (String) credentials[0];
            email = (String) credentials[1];
        } else {
            throw new AuthenticationException("Expected 1 or 2 arguments " +
                    " to noAuthLogin(fullname, <email>), got " +
                    credentials.length + ".");
        }

        try {
            URL u = new URL(url + "/noauth");
            HttpURLConnection uc = (HttpURLConnection) u.openConnection();
            uc.setRequestMethod("POST");
            uc.setRequestProperty("Content-Type",
                                  "application/x-www-form-urlencoded");

            uc.setDoOutput(true);
            uc.setDoInput(true);

            PrintWriter pr = new PrintWriter(
                                  new OutputStreamWriter(uc.getOutputStream()));
            pr.append("username=" + URLEncoder.encode(username, "UTF-8"));
            pr.append("&fullname=" + URLEncoder.encode(fullname, "UTF-8"));
            if (email != null) {
                pr.append("&email=" + URLEncoder.encode(email, "UTF-8"));
            }
            pr.close();

            logger.fine("NoAuthLogin Response: " + uc.getResponseCode() +
                        " : " + uc.getResponseMessage());

            if (uc.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN) {
                throw new AuthenticationException("Invalid username or password");
            }

            BufferedReader br = new BufferedReader(
                                    new InputStreamReader(uc.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                logger.finest("NoAuthLogin response line: " + line);

                if (line.startsWith("string=")) {
                    return line.substring("string=".length());
                }
            }
        } catch (IOException ioe) {
            throw new AuthenticationException(ioe);
        }

        throw new AuthenticationException("No token returned");
    }

    /**
     * Log in to the no an authentication web service.
     * @param url the url to log in to
     * @param username the username to login with
     * @param credentials an array of credentials to send.  In this case,
     * the first credential will be used for the password.
     * @return the token returned by the login attempt
     */
    private static String webServiceLogin(String url, String username,
                                          Object... credentials)
        throws AuthenticationException
    {
        String password = null;

        if (credentials.length == 1) {
            // only password specified
            password = (String) credentials[0];
        } else {
            throw new AuthenticationException("Expected 1 argument " +
                    " to webServiceLogin(password), got " +
                    credentials.length + ".");
        }

        try {
            URL u = new URL(url + "/authenticate");
            HttpURLConnection uc = (HttpURLConnection) u.openConnection();
            uc.setRequestMethod("POST");
            uc.setRequestProperty("Content-Type",
                                  "application/x-www-form-urlencoded");

            uc.setDoOutput(true);
            uc.setDoInput(true);

            PrintWriter pr = new PrintWriter(
                                  new OutputStreamWriter(uc.getOutputStream()));
            pr.append("username=" + URLEncoder.encode(username, "UTF-8"));
            pr.append("&password=" + URLEncoder.encode(password, "UTF-8"));
            pr.close();

            logger.fine("WebServiceLogin Response: " + uc.getResponseCode() +
                        " : " + uc.getResponseMessage());

            if (uc.getResponseCode() == HttpURLConnection.HTTP_FORBIDDEN) {
                throw new AuthenticationException("Invalid username or password");
            }

            BufferedReader br = new BufferedReader(
                                    new InputStreamReader(uc.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                logger.finest("WebService response line: " + line);

                if (line.startsWith("string=")) {
                    return line.substring("string=".length());
                }
            }
        } catch (IOException ioe) {
            throw new AuthenticationException(ioe);
        }

        throw new AuthenticationException("No token returned");
    }

    /**
     * Get an implementation of the authentication service
     * @return an implementation of the authentication service
     */
    private synchronized static AuthenticationServiceImpl getImpl(String url) {
        return services.get(url);
    }
}
