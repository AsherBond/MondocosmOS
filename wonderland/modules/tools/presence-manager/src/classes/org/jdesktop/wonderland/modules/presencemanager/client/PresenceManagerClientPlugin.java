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
package org.jdesktop.wonderland.modules.presencemanager.client;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdesktop.wonderland.client.BaseClientPlugin;
import org.jdesktop.wonderland.client.comms.ConnectionFailureException;
import org.jdesktop.wonderland.client.comms.SessionStatusListener;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.client.login.SessionLifecycleListener;
import org.jdesktop.wonderland.common.annotation.Plugin;

/**
 * Plugin to support the presence manager
 * @author jprovino
 */
@Plugin
public class PresenceManagerClientPlugin extends BaseClientPlugin
        implements SessionLifecycleListener, SessionStatusListener
{
    private static final Logger logger =
            Logger.getLogger(PresenceManagerClientPlugin.class.getName());
    
    private PresenceManagerClient client;
    
    @Override
    public void initialize(ServerSessionManager loginManager) {
        logger.info("Presence manager initialized");

        this.client = new PresenceManagerClient();
        loginManager.addLifecycleListener(this);
        super.initialize(loginManager);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        getSessionManager().removeLifecycleListener(this);
    }

    public void sessionCreated(WonderlandSession session) {
	logger.fine("Session created:  " + session.getUserID());
    }

    public void primarySession(WonderlandSession session) {
	if (session != null) {
            logger.fine("Primary session:  " + session.getUserID());
            session.addSessionStatusListener(this);
            if (session.getStatus() == WonderlandSession.Status.CONNECTED) {
                connectClient(session);
            }
        }
    }

    public void sessionStatusChanged(WonderlandSession session, 
                                     WonderlandSession.Status status)
    {
        logger.fine("session status changed " + session + " status " + status);
        if (status.equals(WonderlandSession.Status.CONNECTED)) {
            connectClient(session);
        } else if (status.equals(WonderlandSession.Status.DISCONNECTED)) {
            disconnectClient();
        }
    }
    
    /**
     * Connect the client.
     * @param session the WonderlandSession to connect to, guaranteed to
     * be in the CONNECTED state.
     */
    protected void connectClient(WonderlandSession session) {
        try {
            client.connect(session);
        } catch (ConnectionFailureException e) {
            logger.warning("Connect client error " + e.getMessage());
        }
    }

    /**
     * Disconnect the client
     */
    protected void disconnectClient() {
        client.disconnect();
    }
}
