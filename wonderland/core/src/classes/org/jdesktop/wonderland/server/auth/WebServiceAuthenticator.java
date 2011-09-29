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

import com.sun.sgs.auth.Identity;
import com.sun.sgs.auth.IdentityAuthenticator;
import com.sun.sgs.auth.IdentityCredentials;
import com.sun.sgs.impl.auth.NamePasswordCredentials;
import java.util.Properties;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.security.auth.login.CredentialException;
import javax.security.auth.login.LoginException;
import org.jdesktop.wonderland.common.auth.WonderlandIdentity;
import org.jdesktop.wonderland.common.login.AuthenticationException;
import org.jdesktop.wonderland.common.login.AuthenticationService;

/**
 * An authenticator that reads all data from what the user passes in in
 * the username field.  No attempt is made to authenticate the user,
 * but the user's name is filled in based on the passed in data.
 * @author jkaplan
 */
public class WebServiceAuthenticator implements IdentityAuthenticator {
    // logger
    private static final Logger logger =
            Logger.getLogger(WebServiceAuthenticator.class.getName());

    // authentication service
    private AuthenticationService auth;
    
    public WebServiceAuthenticator(Properties prop) {
        logger.info("Loading WebServie authenticator");

        auth = ServerAuthentication.getAuthenticationService();
    }

    public String[] getSupportedCredentialTypes() {
        return new String[] { "NameAndPasswordCredentials" };
    }
    
    public Identity authenticateIdentity(IdentityCredentials credentials) 
        throws LoginException 
    {
        logger.warning("Auth: " + credentials);

        if (!(credentials instanceof NamePasswordCredentials)) {
            throw new CredentialException("Wrong credential class: " +
                    credentials.getClass().getName());
        }
        
        NamePasswordCredentials npc = (NamePasswordCredentials) credentials;
        
        // make sure the name is specified
        if (npc.getName() == null) {
            logger.warning("No name specified");
            throw new CredentialException("Invalid username");
        }

        logger.warning("Auth token: " + npc.getName());

        // unpack the components of the id
        try {
            String username = null;
            String fullname = null;
            String email = null;

            Attributes attrs = auth.getAttributes(npc.getName(), "uid", "cn", "mail");

            Attribute usernameAttr = attrs.get("uid");
            if (usernameAttr != null) {
                username = (String) usernameAttr.get();
            }

            Attribute fullnameAttr = attrs.get("cn");
            if (fullnameAttr != null) {
                fullname = (String) fullnameAttr.get();
            }

            Attribute emailAttr = attrs.get("mail");
            if (emailAttr != null) {
                email = (String) emailAttr.get();
            }
        
            logger.warning("un: " + username + " fn: " + fullname + " m: " + email);

            // make sure at least a username was specified
            if (username == null || username.trim().length() == 0) {
                logger.warning("Unable to find username");
                throw new CredentialException("No username specified");
            }

            // construct a new WonderlandServerIdentity to return
            return new WonderlandServerIdentity
                    (new WonderlandIdentity(username, fullname, email));
        } catch (AuthenticationException ae) {
            logger.warning("Authentication error: " + ae);
            
            CredentialException ce = new CredentialException("Error authenticating token");
            ce.initCause(ae);
            throw ce;
        } catch (NamingException ne) {
            logger.warning("Authentication error: " + ne);
            
            CredentialException ce = new CredentialException("Error reading attribute");
            ce.initCause(ne);
            throw ce;
        }
    }
}
