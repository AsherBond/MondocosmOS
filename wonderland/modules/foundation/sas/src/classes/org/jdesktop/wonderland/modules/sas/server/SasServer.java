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
package org.jdesktop.wonderland.modules.sas.server;

import java.io.Serializable;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.modules.appbase.server.cell.AppConventionalCellMO;
import org.jdesktop.wonderland.modules.appbase.server.cell.AppConventionalCellMO.AppServerLauncher;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.AppContext;
import java.util.HashMap;
import java.util.LinkedList;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.CellManagerMO;
import java.util.logging.Level;

/**
 * Provides the main server-side logic for SAS. This singleton contains the Registry,
 * the Distributor and all of the server-side communications components for SAS.
 *
 * @author deronj
 */

@ExperimentalAPI
public class SasServer implements ManagedObject, Serializable, AppServerLauncher {

    private static final Logger logger = Logger.getLogger(SasServer.class.getName());

    /** Helps clean up app info when the app is stopped by the cell. */
    private static class SasLaunchInfo implements Serializable{
        private CellID cellID;
        private String executionCapability;
        private ManagedReference providerRef;
        private SasLaunchInfo (CellID cellID, String executionCapability, ManagedReference providerRef) {
            this.cellID = cellID;
            this.executionCapability = executionCapability;
            this.providerRef = providerRef;
        }
    }

    static class LaunchRequest implements Serializable {
        CellID cellID;
        String executionCapability;
        String appName;
        String command;
        ManagedReference providerRef;

        LaunchRequest (CellID cellID, String executionCapability, String appName, String command) {
            this.cellID = cellID;
            this.executionCapability = executionCapability;
            this.appName = appName;
            this.command = command;
        }

        void setProvider (ManagedReference providerRef) {
            this.providerRef = providerRef;
        }

        @Override
        public String toString () {
            return "cell = " + cellID + ", executionCapability = " + executionCapability +
                "appName = " + appName + ", command = " + command;
        }
    }

    /**
     * A map of the app launch requests in flight for various cells.
     * "In flight" means that the request has been sent to a provider.
     * Note: We manage things so that only one launch request can be in flight at a time 
     * for a particular app cell.
     */
    private HashMap<CellID,LaunchRequest> launchesInFlight = new HashMap<CellID,LaunchRequest>();

    /** 
     * A map of the SAS providers which have connected, indexed by their execution capabilities
     * Note: because a provider may support multiple capabilities it may appear on more than one list.
     */
    private HashMap<String,LinkedList<ManagedReference>> execCapToProviderList = 
        new HashMap<String,LinkedList<ManagedReference>>();

    /**
     * A list of the app launch requests which still must be honored.
     */
    private LaunchList pendingLaunches = new LaunchList();

    /**
     * A list of the app launch requests that have succeeded. This is the list
     * of currently running apps.
     */
     private LaunchList runningLaunches = new LaunchList();

    /**
     * Called when a new provider client connects to the SAS server.
     */
    public void providerConnected(WonderlandClientSender sender, WonderlandClientID clientID) {
        logger.info("Sas provider connected, clientID = " + clientID);
        
        // TODO: for now everything uses xremwin
        String execCap = "xremwin";

        ProviderProxy provider = new ProviderProxy(clientID, sender);
        ManagedReference providerRef = AppContext.getDataManager().createReference(provider);
        provider.addExecutionCapability(execCap);

        // Add to execution capability list
        LinkedList<ManagedReference> providers = execCapToProviderList.get(execCap);
        if (providers == null) {
            providers = new LinkedList<ManagedReference>();
            execCapToProviderList.put(execCap, providers);
        }
        providers.add(providerRef);

        logger.info("Provider added to xremwin list, clientID = " + clientID);

        // See if there are any pending launches
        try {
            tryPendingLaunches(execCap);
        } catch (InstantiationException ie) {
            logger.warning("Exception during new provider connection execution of pending launches.");
            logger.warning("Exception = " + ie);
        }

        // Mark server modified
        AppContext.getDataManager().markForUpdate(this);
    }

    /**
     * Called when provider client disconnects from the SAS server.
     * A reference to the provider proxy for that client is returned. It is the callers
     * responsibility to properly clean up the provider proxy.
     */
    ManagedReference providerDisconnected(WonderlandClientSender sender, WonderlandClientID clientID) {
        logger.info("Sas provider disconnnected, clientID = " + clientID);
        ManagedReference providerRefToRemove = null;

        // TODO: for now everything uses xremwin
        String execCap = "xremwin";

        // Remove provider from execution capability list 
        LinkedList<ManagedReference> providers = execCapToProviderList.get(execCap);
        if (providers != null) {
            for (ManagedReference providerRef : providers) {
                ProviderProxy provider = (ProviderProxy) providerRef.get();
                if (provider.getClientID().equals(clientID)) {
                    providerRefToRemove = providerRef;
                    break;
                }
            }
            if (providerRefToRemove != null) {
                providers.remove(providerRefToRemove);
                persistProviderApps(providerRefToRemove, execCap);
            }

            if (providers.size() <= 0) {
                execCapToProviderList.remove(execCap);
            }
        }

        // Mark server modified
        AppContext.getDataManager().markForUpdate(this);
        return providerRefToRemove;

    }

    /**
     * {@inheritDoc}
     */
    public Object appLaunch (AppConventionalCellMO cell, String executionCapability, String appName, 
                             String command) 
        throws InstantiationException 
    {
        logger.info("***** appLaunch, command = " + command);

        CellID cellID = cell.getCellID();

        // Construct the launch request
        // TODO: someday: allow multiple apps to be launched per cell.
        LaunchRequest launchReq = new LaunchRequest(cellID, executionCapability, appName, command);

        ManagedReference providerRef = requestLaunch(launchReq);
        return new SasLaunchInfo(launchReq.cellID, launchReq.executionCapability, providerRef);
    }

    private ManagedReference requestLaunch (LaunchRequest launchReq) 
        throws InstantiationException 
    {
        // Note: it is guaranteed that, during a warm start, the old SAS has already been removed
        // from this map.
        LinkedList<ManagedReference> providers = execCapToProviderList.get(launchReq.executionCapability);
        if (providers == null || providers.size() <= 0) {
            // No provider. Launch must pend
            logger.info("No SAS provider for " + launchReq.executionCapability + " is available.");
            logger.info("Launch attempt will pend.");
            pendingLaunches.add(launchReq);
            AppContext.getDataManager().markForUpdate(this);
            return null;
        }

        // TODO: someday: Right now we just try only the first provider. Eventually try multiple providers.
        ManagedReference providerRef = providers.getFirst();
        if (providerRef == null) {
            throw new InstantiationException("Cannot find a provider for " + 
                                             launchReq.executionCapability);
        }

        // Now request the provider to launch the app
        launchReq.setProvider(providerRef);
        launchesInFlight.put(launchReq.cellID, launchReq);
        ProviderProxy provider = (ProviderProxy) providerRef.get();
        provider.tryLaunch(launchReq.cellID, launchReq.executionCapability, launchReq.appName, 
                           launchReq.command);

        return providerRef;
    }
        
    /**
     * Called by the provider proxy to report the result of a launch
     */
    public void appLaunchResult (AppServerLauncher.LaunchStatus status, CellID cellID, String connInfo) {
        logger.info("############### SasServer: Launch result received");
        logger.info("status = " + status);
        logger.info("cellID = " + cellID);
        logger.info("connInfo = " + connInfo);

        // Get the request that we used to launch the app
        LaunchRequest launchReq = launchesInFlight.get(cellID);
        if (launchReq == null) {
            logger.warning("Cannot get app launch request for cell " + cellID);
            return;
        }
        launchesInFlight.remove(cellID);
        AppContext.getDataManager().markForUpdate(this);

        CellMO cell = CellManagerMO.getCell(cellID);
        if (cell == null) {
            logger.warning("Cannot find cell to which to report app launch result, launch request = " + 
                           launchReq);
            return;
        }
        if (!(cell instanceof AppConventionalCellMO)) {
            logger.warning("Cell reported in app launch result is not an AppConventionalMO, launch request = " + 
                           launchReq);
            return;
        }

        // TODO: someday: probably shouldn't do this if the provider tried to launch and it failed.
        // Probably should only do this if the provider wouldn't or couldn't launch for some reason.
        if (status != AppServerLauncher.LaunchStatus.SUCCESS || connInfo == null) {
            // The provider we tried cannot launch. Launch must pend.
            logger.warning("SAS provider launch failed with status " + status + 
                           " and connection info " + connInfo +
                           " for launch request = " + launchReq);
            logger.warning("Launch attempt will pend until a provider is found.");
            pendingLaunches.add(launchReq);
            // Note: server has already been marked for update above

            // TODO: someday: at some point we need to give up and call cell.appLaunchResult with a failure 
            // status. Need to implement a timeout.
            return;
        }

        // The app is now running
        runningLaunches.add(launchReq);

        ((AppConventionalCellMO)cell).appLaunchResult(status, connInfo);
    }
    
    /**
     * {@inheritDoc}
     */
    public void appStop (Object launchInfo) {
        SasLaunchInfo sasLaunchInfo = (SasLaunchInfo) launchInfo;

        // First, remove cell from the launches in flight map.
        launchesInFlight.remove(sasLaunchInfo.cellID);

        // Next, remove cell from the list of running apps
        runningLaunches.remove(sasLaunchInfo.cellID, sasLaunchInfo.executionCapability);

        // Next, remove cell from pending launch list. 
        // TODO: someday: For now, this code assumes only one app launch per cell.
        pendingLaunches.remove(sasLaunchInfo.cellID, sasLaunchInfo.executionCapability);

        AppContext.getDataManager().markForUpdate(this);

        // Finally tell the provider to stop the app.
        // 
        // If the provider was determined when appLaunch() was called, tell it to stop the app. */
        // But if the app had to pend waiting for a provider, we must notify all providers to 
        // see which one launched the app.
        if (sasLaunchInfo.providerRef != null) {
            ProviderProxy provider = (ProviderProxy) sasLaunchInfo.providerRef.get();
            if (provider != null) {
                provider.appStop(sasLaunchInfo.cellID);
            }
        } else {
            LinkedList<ManagedReference> providers =
                execCapToProviderList.get(sasLaunchInfo.executionCapability);
            if (providers != null) {
                for (ManagedReference providerRef : providers) {
                    ProviderProxy provider = (ProviderProxy) providerRef.get();
                    if (provider != null) {
                        provider.appStop(sasLaunchInfo.cellID);
                    }
                }
            }
        }
    }

    /* TODO:someday:currently assumes a single provider per execution capability */
    private void tryPendingLaunches (String executionCapability) throws InstantiationException {
        LinkedList<LaunchRequest> reqs = pendingLaunches.getLaunches(executionCapability);
        if (reqs == null) {
            return;
        }
        LinkedList<LaunchRequest> reqsForTraversal = (LinkedList<LaunchRequest>) reqs.clone();
        
        for (LaunchRequest req : reqsForTraversal) {

            // TODO: Some of this code is dup from above in tryLaunch; share it

            // See if there are any more providers to try
            LinkedList<ManagedReference> providers = execCapToProviderList.get(executionCapability);
            if (providers == null || providers.size() <= 0) {
                continue;
            }
            // TODO: someday: weed out providers already tried
            ManagedReference providerRef = providers.getFirst();
            ProviderProxy provider = (ProviderProxy) providerRef.get();

            // Remove request from pending list while it is in flight */
            reqs.remove(req);

            // Now request the newly selected provider to launch the app
            launchesInFlight.put(req.cellID, req);
            provider.tryLaunch(req.cellID, req.executionCapability, req.appName, req.command);
        }
    }

    /**
     * Make the currently running apps persist by transferring them to the pending
     * launches list. They will be rerun the next time a suitable provider connects.
     * @param providerRef The provider whose running apps should be persisted.
     * @param execCap The execution capability of the running apps that should be persisted.
     * TODO:someday:currently assumes a single provider per execution capability
     */
    private void persistProviderApps (ManagedReference providerRef, String execCap) {
        LinkedList<LaunchRequest> reqsToRelaunch = new LinkedList<LaunchRequest>();

        logger.info("Persist provider apps");

        // First, persist the in-flight launches (i.e. the launches that have been requested
        // but have not yet occurred.
        LinkedList<CellID> cellsToRemove = new LinkedList<CellID>();
        for (CellID cellID : launchesInFlight.keySet()) {
            LaunchRequest launchReq = launchesInFlight.get(cellID);
            if (launchReq != null) {
                ProviderProxy reqProvider = (ProviderProxy) launchReq.providerRef.get();
                if (reqProvider.provides(execCap)) {
                    reqsToRelaunch.add(launchReq);
                    cellsToRemove.add(cellID);
                }
            }
        }
        for (CellID cellID : cellsToRemove) {
            launchesInFlight.remove(cellID);
        }
        cellsToRemove.clear();

        // Next, persist the running apps.
        LinkedList<LaunchRequest> launches = runningLaunches.getLaunches(execCap);
        if (launches != null) {
            for (LaunchRequest launchReq : launches) {
                reqsToRelaunch.add(launchReq);
                cellsToRemove.add(launchReq.cellID);
            }
        }
        for (CellID cellID : cellsToRemove) {
            runningLaunches.remove(cellID, execCap);
        }
        cellsToRemove.clear();

        // Note: previously we were just adding the launch request directly to the pendingLaunches
        // list. However, that wasn't handling the case where the new SAS provider connects before 
        // the old SAS provider fully disconnects (and this method was fully done and its data committed). 
        // Since the only way anything is launched off of the pending launch list is for a new SAS
        // provider to connect, we were, in the previous case, adding requests to the pending launch
        // list but nobody was ever actually reading this list to relaunch the apps. 
        // 
        // Instead, we call requestLaunch. If the new SAS provider has already connected it will 
        // launch the app immediately. Otherwise, it will pend the launch and when the new SAS
        // provider later connects the request will be launched.

        for (LaunchRequest launchReq : reqsToRelaunch) {
            try {
                requestLaunch(launchReq);
            } catch (InstantiationException ex) {
                logger.log(Level.SEVERE, "App Instantiation error while trying to persist it", ex);
                continue;
            }
        }

        AppContext.getDataManager().markForUpdate(this);
    }
}

