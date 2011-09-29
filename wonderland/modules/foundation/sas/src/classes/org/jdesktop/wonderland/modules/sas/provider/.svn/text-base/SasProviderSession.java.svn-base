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
package org.jdesktop.wonderland.modules.sas.provider;

import org.jdesktop.wonderland.client.comms.ConnectionFailureException;
import org.jdesktop.wonderland.client.comms.LoginFailureException;
import org.jdesktop.wonderland.client.comms.LoginParameters;
import org.jdesktop.wonderland.client.comms.WonderlandServerInfo;
import org.jdesktop.wonderland.client.comms.WonderlandSessionImpl;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * The SAS provider client session. Uses the SAS provider connection.
 *
 * @author deronj
 */

@ExperimentalAPI
public class SasProviderSession extends WonderlandSessionImpl {

    private SasProviderConnection connection;
    private SasProviderConnectionListener listener;

    public SasProviderSession(ServerSessionManager sessionManager, WonderlandServerInfo serverInfo, SasProviderConnectionListener listener) {
        this (sessionManager, serverInfo, null, listener);
    }
    
    public SasProviderSession(ServerSessionManager sessionManager, WonderlandServerInfo serverInfo, ClassLoader loader,
                              SasProviderConnectionListener listener) {
        super (sessionManager, serverInfo, loader);
        connection = new SasProviderConnection(listener);        
        listener.setSession(this);
    }

    public SasProviderConnection getConnection () {
        return connection;
    }

    /**
     * Override the login message to connect clients after the login
     * succeeds.  If a client fails to connect, the login will be aborted and
     * a LoginFailureException will be thrown
     * @param loginParameters the parameters to login with
     * @throws LoginFailureException if the login fails or any of the clients
     * fail to connect
     */
    @Override
    public void login(LoginParameters loginParams) 
            throws LoginFailureException 
    {
        // this will wait for login to succeed
        super.login(loginParams);
        
        // if login succeeds, connect the various clients
        try {
            connection.connect(this);
        } catch (ConnectionFailureException afe) {
            // a client failed to connect -- logout
            logout();
            
            // throw a login exception
            throw new LoginFailureException("Failed to attach client" , afe);
        }
    }
}
