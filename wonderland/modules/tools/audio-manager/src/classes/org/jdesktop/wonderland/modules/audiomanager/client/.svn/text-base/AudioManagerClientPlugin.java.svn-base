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
package org.jdesktop.wonderland.modules.audiomanager.client;

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
 * Plugin to support the audio manager
 * @author jprovino
 */
@Plugin
public class AudioManagerClientPlugin extends BaseClientPlugin
        implements SessionLifecycleListener, SessionStatusListener {

    private static final Logger logger =
            Logger.getLogger(AudioManagerClientPlugin.class.getName());

    @Override
    public void initialize(ServerSessionManager loginManager) {
        logger.info("Audio manager initialize");

        loginManager.addLifecycleListener(this);
        super.initialize(loginManager);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        getSessionManager().removeLifecycleListener(this);
    }

    @Override
    protected void activate() {
        getClient().addMenus();
    }

    @Override
    protected void deactivate() {
        getClient().removeMenus();
    }

    public void sessionCreated(WonderlandSession session) {
    }

    public void primarySession(WonderlandSession session) {
        if (session != null) {
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
            getClient().connect(session);
        } catch (ConnectionFailureException e) {
            logger.log(Level.WARNING, "Connect client error", e);
        }
    }

    /**
     * Disconnect the client
     */
    protected void disconnectClient() {
        getClient().disconnect();
    }

    public static AudioManagerClient getClient() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final AudioManagerClient INSTANCE =
                new AudioManagerClient();
    }
}
