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
package org.jdesktop.wonderland.modules.xremwin.client.cell;

import javax.crypto.SecretKey;
import org.jdesktop.wonderland.client.cell.CellCache;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.state.CellClientState;
import org.jdesktop.wonderland.modules.appbase.client.AppConventional;
import org.jdesktop.wonderland.modules.appbase.client.ProcessReporterFactory;
import org.jdesktop.wonderland.modules.appbase.client.cell.AppConventionalCell;
import org.jdesktop.wonderland.modules.xremwin.client.AppXrw;
import org.jdesktop.wonderland.modules.xremwin.client.AppXrwMaster;
import org.jdesktop.wonderland.modules.xremwin.client.AppXrwSlave;
import org.jdesktop.wonderland.modules.xremwin.client.AppXrwConnectionInfo;
import org.jdesktop.wonderland.modules.xremwin.common.cell.AppCellXrwClientState;
import org.jdesktop.wonderland.modules.appbase.client.App2D;
import org.jdesktop.wonderland.modules.appbase.client.FirstVisibleInitializer;
import org.jdesktop.wonderland.modules.xremwin.client.BadConnectionInfoException;
import javax.swing.JOptionPane;
import java.util.logging.Level;

/**
 * An Xremwin client-side app cell.
 *
 * @author deronj
 */
@ExperimentalAPI
public class AppCellXrw extends AppConventionalCell {

    /** The session used by the cell cache of this cell to connect to the server */
    private WonderlandSession session;

    /** The shared secret */
    private SecretKey secret;

    /**
     * Create an instance of AppCellXrw.
     *
     * @param cellID The ID of the cell.
     * @param cellCache the cell cache which instantiated, and owns, this cell.
     */
    public AppCellXrw(CellID cellID, CellCache cellCache) {
        super(cellID, cellCache);
        session = cellCache.getSession();
    }

    /**
     * @{inheritDoc}
     */
    @Override
    public void setClientState(CellClientState clientState) {
        super.setClientState(clientState);

        secret = ((AppCellXrwClientState) clientState).getSecret();
    }

    /**
     * {@inheritDoc}
     */
    protected AppConventionalCell.StartMasterReturnInfo startMaster(String appName, String command,
                                                                    FirstVisibleInitializer fvi) {
        App2D theApp = null;
        try {
            app = new AppXrwMaster(appName, command, getCellID(), pixelScale,
                                   ProcessReporterFactory.getFactory().create(appName), session);
        } catch (InstantiationException ex) {
            return null;
        }

        ((AppConventional) theApp).addDisplayer(this);

        setApp(theApp);

        // Must be done before enabling client
        if (App2D.doAppInitialPlacement && fvi != null) {
            logger.info("Cell transferring fvi to app, fvi = " + fvi);
            app.setFirstVisibleInitializer(fvi);
        }

        // Now it is safe to enable the master client loop
        ((AppXrw)theApp).getClient().enable();

        return new AppConventionalCell.StartMasterReturnInfo(theApp,
                       ((AppXrwMaster)theApp).getConnectionInfo().toString());
    }

    /**
     * {@inheritDoc}
     */
    protected App2D startSlave(String connectionInfo, FirstVisibleInitializer fvi) {
        App2D theApp = null;
        try {
            theApp = new AppXrwSlave(appName, pixelScale,
                                     ProcessReporterFactory.getFactory().create(appName),
                                     new AppXrwConnectionInfo(connectionInfo, secret), session, 
                                     this, fvi);
        } catch (BadConnectionInfoException ex) {
            // This is not a hard fail. It can happen in the normal course of a SAS warm start.
            // Sometimes connection infos can be stale (i.e. can be from a previous run of the 
            // SAS. In this case we just return null. The caller will then expect to get a valid
            // connection info in the future.
            logger.info("Bad Connection Info. Waiting for good one: " + connectionInfo);
            this.connectionInfo = null;
            return null;
        } catch (InstantiationException ex) {
            JOptionPane.showMessageDialog(null, "Cannot create Xremwin protocol client for " + appName, 
                                          "Error", JOptionPane.ERROR_MESSAGE);
            cleanup();
            return null;
        }

        if (!((AppXrwSlave)theApp).isConnected()) {
            // There is a case when the connection info is stale that the socket gets created above
            // but the first read causes an EOF. This code infers from the EOF that the connection 
            // info is stale. Even if the connection info isn't stale, the app has died and the cell
            // is shortly about to go away anyway.
            logger.info("Bad connection info because of EOF: " + connectionInfo);
            this.connectionInfo = null;
            return null;
        }

        setApp(theApp);

        return theApp;
    }
}
