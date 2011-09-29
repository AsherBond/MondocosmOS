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
package org.jdesktop.wonderland.modules.xremwin.client;

import java.io.IOException;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.ProcessReporter;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.MessageArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.ServerMessageType;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.SetPopupParentMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.SetWindowTitleMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.UserNameMsgArgs;
import org.jdesktop.wonderland.modules.xremwin.client.Proto.SlaveCloseWindowMsgArgs;
import javax.swing.SwingUtilities;
import java.io.EOFException;

/**
 * The slave version of the Xremwin protocol client. This communicates with an
 * Xremwin master using the Xremwin protocol. The Xremwin looks like a type
 * of Xremwin server to this client, although there are some messages (such as
 * the welcome message) which only a master sends.
 *
 * @author deronj
 */
@ExperimentalAPI
public class ClientXrwSlave extends ClientXrw implements ServerProxySlave.DisconnectListener {

    /** Argument buffers for slave-specific messages */
    private SetWindowTitleMsgArgs setWindowTitleMsgArgs = new SetWindowTitleMsgArgs();
    private SetPopupParentMsgArgs setPopupParentMsgArgs = new SetPopupParentMsgArgs();
    private UserNameMsgArgs userNameMsgArgs = new UserNameMsgArgs();
    private SlaveCloseWindowMsgArgs slaveCloseWindowMsgArgs = new SlaveCloseWindowMsgArgs();

    /**
     * Create a new instance of ClientXrwSlave.
     *
     * @param app The application for whom the client is operating.
     * @param controlArb The control arbiter for the app.
     * @param session This app's Wonderland session.
     * @param connectionInfo Subclass-specific data for making a peer-to-peer connection between master and slave.
     * @param reporter Report output and exit status to this.
     * @throws InstantiationException If it could not make contact with the server.
     */
    public ClientXrwSlave(AppXrw app, ControlArbXrw controlArb, WonderlandSession session,
            AppXrwConnectionInfo connectionInfo, ProcessReporter reporter)
            throws InstantiationException, BadConnectionInfoException {
        super(app, controlArb, reporter);

        // Connect to the Xremwin server
        serverProxy = new ServerProxySlave(this, session, connectionInfo, this);
        try {
            serverProxy.connect();
        } catch (IOException ex) {
            throw new BadConnectionInfoException(connectionInfo);
        }
        serverConnected = true;

        ((ControlArbXrw) controlArb).setServerProxy(serverProxy);

        // Start the protocol interpreter
        start();
    }

    /**
     * @{inheritDoc}
     */
    @Override
    protected MessageArgs readMessageArgs(ServerMessageType msgType)  throws EOFException {
        switch (msgType) {

            case SET_WINDOW_TITLE:
                serverProxy.getData(setWindowTitleMsgArgs);
                return setWindowTitleMsgArgs;

            case SET_POPUP_PARENT:
                ((ServerProxySlave) serverProxy).getData(setPopupParentMsgArgs);
                return setPopupParentMsgArgs;

            case CONTROLLING_USER_NAME:
                ((ServerProxySlave) serverProxy).getData(userNameMsgArgs);
                return userNameMsgArgs;

            case SLAVE_CLOSE_WINDOW:
                ((ServerProxySlave) serverProxy).getData(slaveCloseWindowMsgArgs);
                return slaveCloseWindowMsgArgs;

            default:
                return super.readMessageArgs(msgType);
        }
    }

    /**
     * @{inheritDoc}
     */
    @Override
    protected void processMessage(ServerMessageType msgType) throws EOFException {
        WindowXrw win;

        switch (msgType) {

            case SET_WINDOW_TITLE:
                win = lookupWindow(setWindowTitleMsgArgs.wid);
                if (win != null) {
                    win.setTitle(setWindowTitleMsgArgs.title);
                }
                break;

            case SET_POPUP_PARENT:
                win = lookupWindow(setPopupParentMsgArgs.wid);
                WindowXrw parentWin = lookupWindow(setPopupParentMsgArgs.parentWid);
                if (win != null && parentWin != null) {
                    win.setParent(parentWin);
                } else {
                    if (win == null) {
                        AppXrw.logger.warning("SetPopupParent: window doesn't exist: wid = " +
                                setPopupParentMsgArgs.wid);
                    }
                    if (parentWin == null) {
                        AppXrw.logger.warning("SetPopupParent: parent window doesn't exist: wid = " +
                                setPopupParentMsgArgs.parentWid);
                    }
                }
                break;

            case CONTROLLING_USER_NAME:
                SwingUtilities.invokeLater(new Runnable() {
                    public void run () {
                        ((ControlArbXrw)controlArb).setController(userNameMsgArgs.userName);
                        }
                    });
                break;

            case SLAVE_CLOSE_WINDOW:
                // All slaves receive this when the window closes. Don't need to do anything
                // because the cell is about to go away.
                break;


            default:
                super.processMessage(msgType);
        }
    }

    /** 
     * Set the client ID of this client. ServerProxySlave calls this with
     * the ID for this client which was assigned by the master.
     *
     * @param clientId The unique small integer ID of this slave client
     */
    void setClientId(int clientId) {
        this.clientId = clientId;
    }

    /** {@inheritDoc} */
    public void windowCloseUser(WindowXrw win) {
        if (controlArb.hasControl()) {
            try {
                // Notify the master that the window has been closed by the user
                ((ServerProxySlave) serverProxy).slaveCloseWindow(clientId, win.getWid());
            } catch (IOException ex) {
                AppXrw.logger.warning("Controlling slave cannot close X window " + win.getWid());
            }
        }
    }

    /**
     * Called when the slave disconnects from the master.
     */
    public void disconnected() {
        AppXrw.logger.info("ClientXrwSlave disconnected");
        serverConnected = false;
        // We no longer control the remote app group
        if (controlArb != null && controlArb.hasControl()) {
            ((ControlArbXrw)controlArb).controlLost();
        }
    }

    /**
     * Update the app's windows with information received from the slave sync
     * Used during slave synchronization of conventional apps.
     */
    public void updateSlaveWindows () {
        app.updateSlaveWindows();
    }
}