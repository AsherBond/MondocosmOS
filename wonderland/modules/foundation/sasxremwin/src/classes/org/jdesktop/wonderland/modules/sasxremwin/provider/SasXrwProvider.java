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
package org.jdesktop.wonderland.modules.sasxremwin.provider;

import java.io.File;
import org.jdesktop.wonderland.client.comms.ConnectionFailureException;
import org.jdesktop.wonderland.client.comms.LoginFailureException;
import org.jdesktop.wonderland.client.comms.LoginParameters;
import org.jdesktop.wonderland.client.comms.WonderlandServerInfo;
import org.jdesktop.wonderland.client.login.ProgrammaticLogin;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.sas.provider.SasProvider;
import org.jdesktop.wonderland.modules.sas.provider.SasProviderConnectionListener;
import org.jdesktop.wonderland.modules.sas.provider.SasProviderSession;
import org.jdesktop.wonderland.modules.xremwin.client.XrwSecurityConnection;
import java.util.logging.Logger;

/**
 * The Xremwin-specific provider.
 *
 * @author deronj
 */
@ExperimentalAPI
public class SasXrwProvider extends SasProvider {

    static final Logger logger = Logger.getLogger(SasXrwProvider.class.getName());

    private SasXrwProviderMain main;

    public SasXrwProvider (String userName, File passwordFile, String serverUrl,
                           SasProviderConnectionListener listener, SasXrwProviderMain main) {
        super(userName, passwordFile, serverUrl, listener);
        this.main = main;
    }

    @Override
    protected ProgrammaticLogin<SasProviderSession> createSasLogin(
            String serverURL, SasProviderConnectionListener listener)
    {
        return new SasXrwLogin(serverURL, listener);
    }

    /** {@inheritDoc} */
    @Override
    protected void cleanup () {
        super.cleanup();

        // TODO: low: workaround for bug 205. This is draconian. Is there something else better?
        // TODO: is there any reason this doesn't just call AppXrwMaster.shutdownAllApps()?
        try {
            Runtime.getRuntime().exec("pkill -9 Xvfb-xremwin");
        } catch (Exception e) {}

        logger.warning("SasXrwProvider: cleaned up app processes.");
    }

    private class SasXrwLogin extends ProgrammaticLogin<SasProviderSession> {
        private SasProviderConnectionListener listener;

        public SasXrwLogin(String serverURL,
                           SasProviderConnectionListener listener)
        {
            super (serverURL);
            this.listener = listener;
        }

        @Override
        protected SasProviderSession createSession(ServerSessionManager sessionManager,
                                                   WonderlandServerInfo server,
                                                   ClassLoader loader)
        {
            return new SasXrwProviderSession(sessionManager, server, loader, listener);
        }
    }

    /**
     * An extension of sas provider session that also connects the
     * XrwSecurityConnection
     */
    private class SasXrwProviderSession extends SasProviderSession {
        private XrwSecurityConnection connection;

        public SasXrwProviderSession(ServerSessionManager sessionManager,
                                     WonderlandServerInfo server, 
                                     ClassLoader loader,
                                     SasProviderConnectionListener listener)
        {
            super (sessionManager, server, loader, listener);

            connection = new XrwSecurityConnection();
        }

        @Override
        public void login(LoginParameters loginParams) throws LoginFailureException {
            super.login(loginParams);

            // if login succeeds, connect the various clients
            try {
                logger.warning("Connecting XrwSecurityConnection");
                connection.connect(this);
            } catch (ConnectionFailureException afe) {
                // a client failed to connect -- logout
                logout();

                // throw a login exception
                throw new LoginFailureException("Failed to attach client" , afe);
            }
        }
    }
}

