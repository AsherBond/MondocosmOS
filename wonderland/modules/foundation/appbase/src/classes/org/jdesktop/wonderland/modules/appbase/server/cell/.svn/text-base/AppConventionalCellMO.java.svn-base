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
package org.jdesktop.wonderland.modules.appbase.server.cell;

import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.modules.appbase.common.cell.AppConventionalCellClientState;
import org.jdesktop.wonderland.common.cell.state.CellClientState;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.modules.appbase.common.cell.AppConventionalCellServerState;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.Task;
import java.io.Serializable;
import java.util.logging.Level;
import org.jdesktop.wonderland.modules.appbase.common.cell.AppConventionalCellSetConnectionInfoMessage;
import org.jdesktop.wonderland.modules.appbase.common.cell.AppConventionalCellAppExittedMessage;
import org.jdesktop.wonderland.server.cell.CellManagerMO;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.messages.ErrorMessage;
import org.jdesktop.wonderland.server.cell.AbstractComponentMessageReceiver;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.ChannelComponentMO;
import org.jdesktop.wonderland.server.cell.annotation.UsesCellComponentMO;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

/**
 * The server-side cell for an 2D conventional application.
 * When the cell system creates the cell it uses the default constructor and
 * calls <code>setServerState</code> to transfer the setup information into the cell.
 * <br><br>
 * In this case the <code>setServerState</code> must specify:
 * <ol>
 * + command: The command to execute. This must not be a non-empty string.         
 * </ol>
 * The wlc <code>setServerState</code> can optionally specify:
 * <ol>
 * + <code>appName</code>: The name of the application (Default: "NoName").
 * </ol>
 *
 * @author deronj
 */
@ExperimentalAPI
public abstract class AppConventionalCellMO extends App2DCellMO {

    private static Logger logger = Logger.getLogger(AppConventionalCellMO.class.getName());

    /** The name of the Darkstar binding we use to store the reference to the SAS. */
    private static String APP_SERVER_LAUNCHER_BINDING_NAME = 
        "org.jdesktop.wonderland.modules.appbase.server.cell.AppServerLauncher";

    /** The parameters from the WFS file. */
    AppConventionalCellServerState serverState;
    
    /** Subclass-specific data for making a peer-to-peer connection between master and slave. */
    protected String connectionInfo;

    /** Opaque info which identifies the launched app to the app server. */
    private Object appServerLaunchInfo;

    /**
     * The SAS server must implement this.
     */
    public interface AppServerLauncher {

        public enum LaunchStatus { SUCCESS, FAIL };

        /**
         * Launch a server shared application. Reports the result of the launch by calling 
         * back to AppConventionalCellMO.appLaunchResult.
         * @param cell The cell that is launching the app.
         * @param executionCapability The type of execution capability needed (xremwin, vnc, etc.)
         * @param appName The name of the app.
         * @param command The execution command.
         * @return Opaque information which identifies the launched to the app server. This must be 
         * provided to the appStop method.
         */
        public Object appLaunch (AppConventionalCellMO cell, String executionCapability, String appname,
                                String command) throws InstantiationException;
        
        /**
         * Stop a running server shared application.
         */
        public void appStop (Object appServerLaunchInfo);
    }

    /**
     * Register an app server launcher with app conventional.
     */
    public static void registerAppServerLauncher (AppServerLauncher appServerLauncher) {
        AppContext.getDataManager().setBinding(APP_SERVER_LAUNCHER_BINDING_NAME, appServerLauncher);
    }

    /**
     * Returns the app server launcher.
     */
    public static AppServerLauncher getAppServerLauncher () {
        return (AppServerLauncher) AppContext.getDataManager().getBinding(APP_SERVER_LAUNCHER_BINDING_NAME);
    }

    /** Create an instance of AppConventionalCellMO. */
    public AppConventionalCellMO() {
        super();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setServerState(CellServerState state) {
        super.setServerState(state);
        serverState = (AppConventionalCellServerState) state;

        // Validate WFS parameters
        // TODO: what is the proper way to signal this error which is non-fatal to the server?

        /*
        String appName = serverState.getAppName();
        if (appName == null || appName.length() <= 0) {
            String msg = "Invalid app name";
            logger.severe(msg);
            throw new RuntimeException(msg);
        }

        // NOTE: launchLocation user is obsolete.

        String launchLocation = serverState.getLaunchLocation();
        if (!"user".equalsIgnoreCase(launchLocation) &&
            !"server".equalsIgnoreCase(launchLocation)) {
            String msg = "Invalid launch location: " + launchLocation;
            logger.severe(msg);
            throw new RuntimeException(msg);
        }

        if ("user".equalsIgnoreCase(launchLocation)) {
            String launchUser = serverState.getLaunchUser();
            if (launchUser == null || launchUser.length() <= 0) {
                String msg = "Invalid app launch user";
                logger.severe(msg);
                throw new RuntimeException(msg);
            }
        }

        String command = serverState.getCommand();
        if (command == null || command.length() <= 0) {
            String msg = "Invalid app command";
            logger.severe(msg);
            throw new RuntimeException(msg);
        }
        */
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    protected CellClientState getClientState(CellClientState cellClientState,
                                             WonderlandClientID clientID, ClientCapabilities capabilities) {
        if (cellClientState == null) {
            cellClientState = new AppConventionalCellClientState();
        }
        populateClientState((AppConventionalCellClientState) cellClientState);
        return super.getClientState(cellClientState, clientID, capabilities);
    }

    /**
     * Fill in the given client state with this cell's state.
     * @param clientState The client state whose properties are to be set.
     */
    private void populateClientState(AppConventionalCellClientState clientState) {
        clientState.setAppName(serverState.getAppName());
        clientState.setLaunchLocation(serverState.getLaunchLocation());
        clientState.setLaunchUser(serverState.getLaunchUser());
        clientState.setBestView(serverState.isBestView());
        clientState.setCommand(serverState.getCommand());
        clientState.setConnectionInfo(connectionInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CellServerState getServerState(CellServerState stateToFill) {
        if (stateToFill == null) {
            return null;
        }

        super.getServerState(stateToFill);

        AppConventionalCellServerState state = (AppConventionalCellServerState) stateToFill;
        state.setAppName(serverState.getAppName());
        state.setLaunchLocation(serverState.getLaunchLocation());
        state.setLaunchUser(serverState.getLaunchUser());
        state.setBestView(serverState.isBestView());
        state.setCommand(serverState.getCommand());

        return stateToFill;
    }

    @Override
    protected void setLive(boolean live) {
        if (isLive()==live)
            return;

        super.setLive(live);

        if (!"server".equalsIgnoreCase(serverState.launchLocation)) {
            // for a user launched app, add listeners for remote notification
            // of app status.  Note this protocol is not fully implemented
            // on the client for user launch, so some of these messages are never
            // received.
            if (live) {
                AppConventionalCellMessageReceiver receiver = new AppConventionalCellMessageReceiver(this);
                channelRef.get().addMessageReceiver(AppConventionalCellSetConnectionInfoMessage.class,
                                                    receiver);
                channelRef.get().addMessageReceiver(AppConventionalCellAppExittedMessage.class,
                                                    receiver);
            } else {
                channelRef.get().removeMessageReceiver(AppConventionalCellSetConnectionInfoMessage.class);
                channelRef.get().removeMessageReceiver(AppConventionalCellAppExittedMessage.class);
            }

            return;
        }

        /*
        ** Server shared app case.
        */

        AppServerLauncher appServerLauncher = getAppServerLauncher();
        if (appServerLauncher == null) {
            logger.warning("No SAS registered. Cannot launch app " + serverState.getAppName());
            return;
        }

        if (live) {

            // TODO: someday: need to generalize beyond xremwin
            try {
                appServerLaunchInfo = appServerLauncher.appLaunch(this, "xremwin", 
                                              serverState.getAppName(), serverState.getCommand());
            } catch (InstantiationException ex) {
                // TODO: jon: what should this exception be?
                logger.warning("App launch for app " + serverState.getAppName() + " failed with an exception.");
                logger.warning("Exception = " + ex);
                RuntimeException re = new RuntimeException("App launch for app " + serverState.getAppName() + " failed");
                throw re;
            }

        } else {
            if (connectionInfo != null) {
                appServerLauncher.appStop(appServerLaunchInfo);
                destroy();
            }
        }
    }

    public void appLaunchResult (AppServerLauncher.LaunchStatus status, String connInfo) {

        logger.info("AppConventionalCellMO: Launch result received");
        logger.info("status = " + status);
        logger.info("connInfo = " + connInfo);

        if (status == AppServerLauncher.LaunchStatus.FAIL) {
            logger.warning("Could not launch app " + serverState.getAppName());
            destroy();
            return;
        }

        setConnectionInfo(connInfo);
    }

    /**
     * Handle a message to set the connection info.  Only use this if the
     * app is client-launched.  This method is untested since the client
     * implementation of user-launch is incomplete.
     * @param clientID the id of the client who sent the message
     * @param message the message to process
     */
    void handleSetConnectionInfo(WonderlandClientID clientID,
                                 AppConventionalCellSetConnectionInfoMessage message)
    {
        // make sure the app wasn't server launched
        if ("server".equalsIgnoreCase(serverState.launchLocation)) {
            logger.warning("Cannot set connection info for server launched app.");
            return;
        }

        // TODO: should we do more to make sure this actually came from the
        // correct client?

        // send a notification
        setConnectionInfo(message.getConnectionInfo());
    }

    public void setConnectionInfo (String connInfo) {
        logger.info("Connection info for cellID " + cellID + " = " + connInfo);
        connectionInfo = connInfo;

        // Notify all client cells that connection info for a newly launched SAS master app
        // is now available.
        AppConventionalCellSetConnectionInfoMessage msg =
            new AppConventionalCellSetConnectionInfoMessage(cellID, connInfo);
        channelRef.get().sendAll(null, msg);
    }

    /**
     * Destroy this cell.
     */
    public void destroy () {
        CellManagerMO.getCellManager().removeCellFromWorld(this);

        // OWL issue #63: preserve the server state so that reparent (which
        // causes a setLive(false) followed by setLive(true)) works properly
        //serverState = null;

        connectionInfo = null;
    }

    /**
     * Notification from a user that an app has exitted.  Don't trust this
     * message unless the app is user launched.  This method is untested since
     * the client implementation of user-launch is incomplete.
     * @param clientID the client that sent the message
     * @param message the message
     */
    void handleAppExitted(WonderlandClientID clientID,
                          AppConventionalCellAppExittedMessage message)
    {
        // make sure the app wasn't server launched
        if ("server".equalsIgnoreCase(serverState.launchLocation)) {
            logger.warning("Cannot notify of exit for server launched app.");
            return;
        }

        // TODO: should we do more to make sure this actually came from the
        // correct client?

        // send a notification
        appExitted(message.getExitValue());
    }

    /**
     * App has exitted (or SAS provider has terminated).
     * @param exitValue If >= 0, this is the exit value of the app.
     * If < 0, the SAS provider has terminated and the app exit value is unknown.
     */
    public void appExitted (int exitValue) {
        // reset the connection info, but don't send a message, since the cell
        // is about to be destroyed and an exitted message will be sent below.
        connectionInfo = null;

        if (exitValue >= 0) {
            logger.warning("App cell terminated because app exitted with exit value = " + exitValue);
        } else {
            logger.warning("App cell terminated because SAS provider exitted. Exit value is unknown.");
        }

        if (serverState == null) {
            // TODO: someday: cell was destroyed via close button. I'm not sure what to send 
            // in this case, if anything.
        } else {

            // Notify all client cells that app has exitted.

            logger.warning("App name of cell = " + serverState.getAppName());
            logger.warning("Launch command of app = " + serverState.getCommand());

            AppConventionalCellAppExittedMessage msg = 
                new AppConventionalCellAppExittedMessage(cellID, exitValue, 
                                                         serverState.getAppName(), 
                                                         serverState.getCommand());
            channelRef.get().sendAll(null, msg);
        }

        destroy();
    }

    private static class AppConventionalCellMessageReceiver extends AbstractComponentMessageReceiver {
        public AppConventionalCellMessageReceiver(CellMO cellMO) {
            super (cellMO);
        }

        @Override
        public void messageReceived(WonderlandClientSender sender,
                                    WonderlandClientID clientID,
                                    CellMessage message)
        {
            AppConventionalCellMO cellMO = (AppConventionalCellMO) getCell();

            if (message instanceof AppConventionalCellSetConnectionInfoMessage) {
                cellMO.handleSetConnectionInfo(clientID, (AppConventionalCellSetConnectionInfoMessage) message);
            } else if (message instanceof AppConventionalCellAppExittedMessage) {
                cellMO.handleAppExitted(clientID, (AppConventionalCellAppExittedMessage) message);
            } else {
                logger.log(Level.WARNING, "Unexpected message type " +
                           message.getClass());
                sender.send(clientID, new ErrorMessage(message.getMessageID(),
                            "Unexpected message type: " + message.getClass()));
            }
        }
    }
}
