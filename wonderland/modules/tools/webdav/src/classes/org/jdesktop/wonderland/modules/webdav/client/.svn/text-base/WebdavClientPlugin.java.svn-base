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
package org.jdesktop.wonderland.modules.webdav.client;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.httpclient.HttpException;
import org.jdesktop.wonderland.common.login.AuthenticationException;
import org.jdesktop.wonderland.modules.webdav.common.WebdavContentCollection;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.jdesktop.wonderland.client.BaseClientPlugin;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.login.AuthenticationManager;
import org.jdesktop.wonderland.common.login.AuthenticationService;
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepositoryRegistry;
import org.jdesktop.wonderland.modules.webdav.common.FileContentCollection;
import org.jdesktop.wonderland.modules.webdav.common.AuthenticatedWebdavResource;


/**
 * Register the WebdavContentRepository as the content repository for this
 * session
 * @author jkaplan
 */
public class WebdavClientPlugin extends BaseClientPlugin {
    private static final Logger logger =
            Logger.getLogger(WebdavClientPlugin.class.getName());

    /** the local repository */
    private FileContentCollection localRepo;

    @Override
    public void initialize(ServerSessionManager loginInfo) {
        try {
            HttpURL baseURL = new HttpURL(loginInfo.getServerURL());
            baseURL = new HttpURL(baseURL, "webdav/content");

            //System.out.println("[WebdavClientPlugin] got base URL " + baseURL);

            // get the authentication service for this session
            AuthenticationService as =
                AuthenticationManager.get(loginInfo.getCredentialManager().getAuthenticationURL());

            // activate the webdav repository for this session
            String authCookieName = as.getCookieName();
            String authCookieValue = as.getAuthenticationToken();
            AuthenticatedWebdavResource wdr =
                    new RootWebdavResource(baseURL,
                                           authCookieName,
                                           authCookieValue);
            WebdavContentCollection root =
                    new WebdavContentCollection(wdr, null)
            {
                // don't include the root node in the path
                @Override
                public String getPath() {
                    return "";
                }
            };
            WebdavContentRepository repo =
                    new WebdavContentRepository(root, loginInfo.getUsername());

            logger.fine("[WebdavClientPlugin] Register repository with root " +
                        baseURL + " and resource " + wdr);

            ContentRepositoryRegistry.getInstance().registerRepository(loginInfo, repo);
        } catch (AuthenticationException ae) {
            logger.log(Level.WARNING, "Unable to get auth cookie name", ae);
        } catch (IOException ioe) {
            logger.log(Level.WARNING, "Unable to start content repository", ioe);
        }

        // activate the local repository -- it's OK to do this in intialize
        // since everything to do with the content repository is in the
        // ContentRepo module, and therefore in the same classloader as
        // this plugin. Since all the code is isolated to this classloader,
        // even static changes like registering the local repository will
        // ony be in effect for this server.
        String dirName = "localRepo/" + loginInfo.getUsername();
        final File userDir = ClientContext.getUserDirectory(dirName);
        localRepo = new FileContentCollection(userDir, null) {
            @Override
            protected URL getBaseURL() {
                try {
//                    System.err.println("BASE URL "+userDir.toURI().toURL().toExternalForm());
                    return userDir.toURI().toURL();
                } catch (MalformedURLException ex) {
                    logger.log(Level.WARNING, "Unable to create local repository", ex);
                    return null;
                }
            }

            @Override
            public String getPath() {
                return "";
            }

        };
        ContentRepositoryRegistry.getInstance().registerLocalRepository(localRepo);
        super.initialize(loginInfo);
    }

    @Override
    public void cleanup() {
        ContentRepositoryRegistry.getInstance().unregisterRepository(getSessionManager());
        ContentRepositoryRegistry reg = ContentRepositoryRegistry.getInstance();
        if (reg.getLocalRepository() == localRepo) {
            reg.registerLocalRepository(null);
        }

        super.cleanup();
    }

    private static class RootWebdavResource extends AuthenticatedWebdavResource {
        public RootWebdavResource(HttpURL url, String authCookieName,
                                  String authCookieValue)
                 throws HttpException, IOException
        {
            super (url, authCookieName, authCookieValue);
            connectionManager = new MultiThreadedHttpConnectionManager();
        }
    }
}
