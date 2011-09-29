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
package org.jdesktop.wonderland.modules.appbase.client.cell;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.common.cell.state.CellClientState;
import org.jdesktop.wonderland.client.cell.CellCache;
import org.jdesktop.wonderland.client.cell.ChannelComponent;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.common.cell.AppConventionalCellClientState;
import org.jdesktop.wonderland.modules.appbase.common.cell.AppConventionalCellSetConnectionInfoMessage;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.modules.appbase.common.cell.AppConventionalCellAppExittedMessage;
import org.jdesktop.wonderland.modules.appbase.client.App2D;
import org.jdesktop.wonderland.modules.appbase.client.FirstVisibleInitializer;

/**
 * The client-side cell for an 2D conventional application.
 *
 * @author deronj
 */
@ExperimentalAPI
public abstract class AppConventionalCell extends App2DCell {

    /** The session used by the cell cache of this cell to connect to the server */
    private WonderlandSession cellCacheSession;
    /** The user-visible app name. */
    protected String appName;
    /** Is this a SAS- or user-launched app? */
    protected String launchLocation;
    /** If a user-launched app, who is the launching user?  */
    protected String launchUser;
    /** The execution command. */
    protected String command;
    /** The connection info. */
    protected String connectionInfo;
    /** Indicates that this cell is a slave and has connectedToTheApp. */
    private boolean slaveStarted;
    /** Indicates that the cell renderer for this cell has been created. */
    private boolean cellRendererExists;

    /** 
     * Creates a new instance of AppConventionalCell.
     *
     * @param cellID The ID of the cell.
     * @param cellCache the cell cache which instantiated, and owns, this cell.
     */
    public AppConventionalCell(CellID cellID, CellCache cellCache) {
        super(cellID, cellCache);
        cellCacheSession = cellCache.getSession();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setClientState(CellClientState clientState) {
        super.setClientState(clientState);

        AppConventionalCellClientState state = (AppConventionalCellClientState) clientState;
        appName = state.getAppName();
        launchLocation = state.getLaunchLocation();
        launchUser = state.getLaunchUser();
        command = state.getCommand();

        if (launchLocation.equalsIgnoreCase("user") &&
            launchUser.equals(cellCacheSession.getUserID().getUsername())) {

            // Master case: nothing to do

        } else {

            // Slave case
            connectionInfo = state.getConnectionInfo();
            logger.info("Initial connection info value for slave = " + connectionInfo);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void setStatus(CellStatus status, boolean increasing) {
        super.setStatus(status, increasing);

        if (status == CellStatus.INACTIVE && increasing) {
            AppConventionalMessageReceiver mr = new AppConventionalMessageReceiver();
            channel.addMessageReceiver(AppConventionalCellSetConnectionInfoMessage.class, mr);
            channel.addMessageReceiver(AppConventionalCellAppExittedMessage.class, mr);
        } else if (status == CellStatus.INACTIVE && !increasing) {
            channel.removeMessageReceiver(AppConventionalCellSetConnectionInfoMessage.class);
            channel.removeMessageReceiver(AppConventionalCellAppExittedMessage.class);
        }

        // Keep track of whether the cell renderer has been created
        if (status == CellStatus.ACTIVE) {
            if (increasing) {
                cellRendererExists = true;
            } else {
                cellRendererExists = false;
            }

            startSlaveIfReady();
        }

        // Launch the app when it is visible for the first time
        if (status == CellStatus.VISIBLE && increasing) {

            // Slave case
            //
            // Slaves must wait to connect until valid connection info is known. This can happen 
            // in one of two ways. If the slave cell was loaded into this client AFTER the master
            // app started the connection info will already be known (i.e. non-null). Otherwise, 
            // if the slave cell was loaded into this client BEFORE the master app started this 
            // client will eventually receive a SetConnectionInfo message whic contains the 
            // connection info.

            startSlaveIfReady();
        } 

        // TODO: it would be a good idea to disconnect the slave at some point 
        // (INACTIVE && !increasing?) to save on resources.
    }

    /**
     * Handle a setConnectionInfo message
     * @param message the message
     */
    void handleConnectionInfo(AppConventionalCellSetConnectionInfoMessage message) {
        setConnectionInfo(message.getConnectionInfo());
    }

    /**
     * Handle an app exitted message
     * @param message the exited message
     */
    void handleAppExitted(final AppConventionalCellAppExittedMessage message) {
        try {
            JOptionPane.showMessageDialog(JmeClientMain.getFrame().getFrame(),
                                          "App " + message.getAppName() +
                                          " exitted with exit value = " +
                                          message.getExitValue());
        } catch (Exception ex) {}
    }

    /**
     * This is called when the server sends the connection info.
     */
    synchronized void setConnectionInfo (String connInfo) {
        
        // Has the connection info changed? If not, just return
        if (connectionInfo == null) {
            if (connInfo == null) {
                return;
            } 
        } else {
            if (connectionInfo.equals(connInfo)) {
                return;
            }
        }
        
        connectionInfo = connInfo;

        // The connection info has changed. Start the app.
        startSlaveIfReady();
    }

    /**
     * Starts the slave if all preconditions are satisified. Before we can start the slave
     * we must have a cell renderer and we must have a connection info.
     */
    private void startSlaveIfReady() {
        if (slaveStarted) return;

        if (cellRendererExists && connectionInfo != null) {
            startTheSlave(connectionInfo);
        }
    }

    private void startTheSlave (String connectionInfo) {
        logger.info("Starting app slave.");
        app = startSlave(connectionInfo, fvi);
        if (app != null) {
            slaveStarted = true;
            logger.info("Connected slave to app at " + connectionInfo);
        } else {
            slaveStarted = false;
            logger.warning("Could not create slave app, connectionInfo = " + connectionInfo);
        }
    }

    /** Information returned from startMaster. */
    public static class StartMasterReturnInfo {
        /** The app created. */
        public App2D app;
        /** Subclass-specific data for making a peer-to-peer connection between master and slave. */
        public String connInfo;
        public StartMasterReturnInfo (App2D app, String connInfo) {
            this.app = app;
            this.connInfo = connInfo;
        }                                                                        
    }


    /** 
     * Launch a master client.
     * @param appName The name of the app.
     * @param command The command string which launches the master app program (used only by master).
     * @return The app created and the subclass-specific connect info. 
     */
    protected abstract StartMasterReturnInfo startMaster(String appName, String command,
                                                         FirstVisibleInitializer fvi);
  
    /** 
     * Launch a slave client.
     * @param connectionInfo Subclass-specific data for making a peer-to-peer connection between 
     * master and slave.
     * @return the app, if successfully started. Otherwise returns null.
     */
    protected abstract App2D startSlave(String connectionInfo, FirstVisibleInitializer fvi);

    /**
     * Message receiver
     */
    private class AppConventionalMessageReceiver
            implements ChannelComponent.ComponentMessageReceiver
    {
        public void messageReceived(CellMessage message) {
            if (message instanceof AppConventionalCellSetConnectionInfoMessage) {
                handleConnectionInfo((AppConventionalCellSetConnectionInfoMessage) message);
            } else if (message instanceof AppConventionalCellAppExittedMessage) {
                handleAppExitted((AppConventionalCellAppExittedMessage) message);
            } else {
                logger.warning("Unexpected message type: " + message.getClass());
            }

        }
    }

    /** {@inheritDoc} */
    @Override
    public void setApp(App2D app) throws IllegalArgumentException, IllegalStateException {
        // Same as super, but doesn't move fvi into the app. This is done earlier in
        // a conventional app
        if (app == null) {
            throw new IllegalArgumentException("Argument app is null");
        }
        if (this.app != null) {
            throw new IllegalStateException("Cell already has an app");
        }

        this.app = app;
        setName(app.getName());
    }
}
