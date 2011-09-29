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
package org.jdesktop.wonderland.modules.textchat.client;

import org.jdesktop.wonderland.client.BaseClientPlugin;
import org.jdesktop.wonderland.client.comms.SessionStatusListener;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.comms.WonderlandSession.Status;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.client.login.SessionLifecycleListener;
import org.jdesktop.wonderland.common.annotation.Plugin;

/**
 * Client-side plugin for the text chat feature. Listens for the primary session
 * to connect/disconnect and instructs the ChatManager as such.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@Plugin
public class TextChatClientPlugin extends BaseClientPlugin
        implements SessionLifecycleListener, SessionStatusListener {

    /**
     * @inheritDoc()
     */
    @Override
    public void initialize(ServerSessionManager sessionManager) {
        // Listen for new primary sessions on this session manager
        sessionManager.addLifecycleListener(this);
        super.initialize(sessionManager);
    }

    @Override
    public void cleanup() {
        // Stop listening for the lifecycle changes
        getSessionManager().removeLifecycleListener(this);
        super.cleanup();
    }

    /**
     * @inheritDoc()
     */
    public void sessionCreated(WonderlandSession session) {
        // Do nothing.
    }

    /**
     * @inheritDoc()
     */
    public void primarySession(WonderlandSession session) {
        // Handle when a new primary session happens. Note that when there is
        // no primary session, the 'session' argument is null. In such a
        // case, we do nothing -- the case where the primary session becomes
        // disconnected is handled by the SessionStatusListener.
        if (session != null) {
            session.addSessionStatusListener(this);
            if (session.getStatus() == WonderlandSession.Status.CONNECTED) {
                connectClient(session);
            }
        }
    }

    /**
     * @inheritDoc()
     */
    public void sessionStatusChanged(WonderlandSession session, Status status) {
        switch (status) {
            case CONNECTED:
                connectClient(session);
                return;

            case DISCONNECTED:
                disconnectClient();
                return;
        }
    }

    /**
     * Connect the client.
     */
    private void connectClient(WonderlandSession session) {
        // Tell the Chat manager that there is a new primary session connected.
        ChatManager chatManager = ChatManager.getChatManager();
        chatManager.register(session);
    }

    /**
     * Disconnect the client
     */
    private void disconnectClient() {
        // Tell the Chat manager that a primary session has disconnected
        ChatManager chatManager = ChatManager.getChatManager();
        chatManager.unregister();
    }
}
