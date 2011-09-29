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
package org.jdesktop.wonderland.modules.webdav.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Logger;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.webdav.lib.WebdavResource;
import org.apache.webdav.lib.WebdavResources;

/**
 * An extension of the standard WebDav resource that adds a particular
 * authentication cookie to its request headers.  In addition, it
 * passes this header on to any child resource.
 * @author jkaplan
 */
public class AuthenticatedWebdavResource extends WebdavResource {
    private static final Logger logger =
            Logger.getLogger(AuthenticatedWebdavResource.class.getName());

    private TokenCredentials tokenCredentials;

    public AuthenticatedWebdavResource(AuthenticatedWebdavResource parent,
                                       HttpURL url)
            throws HttpException, IOException
    {
        super (parent.connectionManager);
        setupResource(this, url, parent.getAuthCookieName(),
                      parent.getAuthCookieValue());
    }

    protected AuthenticatedWebdavResource(HttpURL url, String authCookieName,
                                          String authCookieValue)
            throws HttpException, IOException
    {
        super (URIUtil.decode(url.getEscapedURI()),
               new TokenCredentials(authCookieName, authCookieValue),
               true);
    }

    protected AuthenticatedWebdavResource(HttpConnectionManager connectionManager)
    {
        super (connectionManager);
    }

    public String getAuthCookieName() {
        return tokenCredentials.getAuthCookieName();
    }

    public String getAuthCookieValue() {
        return tokenCredentials.getAuthCookieValue();
    }

    @Override
    public void setCredentials(Credentials credentials) {
        if (credentials instanceof TokenCredentials) {
            this.tokenCredentials = (TokenCredentials) credentials;

            try {
                // add a cookie to the request headers with the authentication
                // token
                this.addRequestHeader("Cookie",
                        tokenCredentials.getAuthCookieName() + "=" +
                        URLEncoder.encode(tokenCredentials.getAuthCookieValue(),
                                          "UTF-8"));
            } catch (UnsupportedEncodingException uee) {
                throw new IllegalStateException(uee);
            }
        } else {
            super.setCredentials(credentials);
        }
    }

    @Override
    public WebdavResources getChildResources() 
            throws HttpException, IOException
    {
        WebdavResources resources = super.getChildResources();
        WebdavResources out = new WebdavResources();
        for (WebdavResource resource : resources.listResources()) {
            out.addResource(createResource(resource));
        }

        return out;
    }

    @Override
    public AuthenticatedWebdavResource[] listWebdavResources()
            throws HttpException, IOException
    {
        WebdavResource[] resources = super.listWebdavResources();
        AuthenticatedWebdavResource[] out =
                new AuthenticatedWebdavResource[resources.length];
        for (int i = 0; i < resources.length; i++) {
            out[i] = createResource(resources[i]);
        }

        return out;
    }

    protected AuthenticatedWebdavResource createResource(WebdavResource resource)
            throws HttpException, IOException
    {
        AuthenticatedWebdavResource out = new AuthenticatedWebdavResource(connectionManager);
        setupResource(out, resource.getHttpURL(), getAuthCookieName(),
                      getAuthCookieValue());
        return out;
    }

    protected static void setupResource(AuthenticatedWebdavResource resource,
                                        HttpURL url,
                                        String authCookieName,
                                        String authCookieValue)
            throws HttpException, IOException
    {
        resource.setFollowRedirects(true);
        resource.setCredentials(new TokenCredentials(authCookieName,
                                                     authCookieValue));
        resource.setHttpURL(url);
    }

    static class TokenCredentials implements Credentials {
        private String authCookieName;
        private String authCookieValue;

        public TokenCredentials(String authCookieName,
                                String authCookieValue)
        {
            this.authCookieName = authCookieName;
            this.authCookieValue = authCookieValue;
        }

        public String getAuthCookieName() {
            return authCookieName;
        }

        public String getAuthCookieValue() {
            return authCookieValue;
        }
    }
}
