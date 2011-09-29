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

import java.net.HttpURLConnection;

/**
 * Manages credentials for connecting to a particular authentication source.
 * The CredentialManager typically maps from the URL of an authentication
 * server to the authentication tokens identifying the user on that server.
 * <p>
 * In addition to returning the actual authentication token, the credential
 * manager can be used to update an HttpURLConnection with the necessary
 * cookies to identify this user to the given server.
 *
 * @author jkaplan
 */
public interface CredentialManager {
    /**
     * Get the URL of the credential server.  Note this may be different than
     * the URL of the Wonderland web server, for example if the Authentication
     * server is an external instance of OpenSSO.
     * @return the authentication server URL.  This is the baseURL where all
     * authentication actions can be taken.
     */
    public String getAuthenticationURL();

    /**
     * Get the username this user is connected as
     * @return the user name
     */
    public String getUsername();

    /**
     * Get the authentication token for this server
     * @return the authentication token
     */
    public String getAuthenticationToken();

    /**
     * Given an HttpURLConnection to a source using this credential manager,
     * add in the necessary cookies for recognizing this request.
     * @param conn the connection to add cookies to
     */
    public void secureURLConnection(HttpURLConnection conn);
}
