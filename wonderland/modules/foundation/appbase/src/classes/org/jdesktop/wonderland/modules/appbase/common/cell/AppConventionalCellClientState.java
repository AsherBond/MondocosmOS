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
package org.jdesktop.wonderland.modules.appbase.common.cell;

import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * Container for 2D conventional app client state. The server gives this to the client
 * in order to initialize the client cell.
 *
 * @author deronj
 */
@ExperimentalAPI
public class AppConventionalCellClientState extends App2DCellClientState {

    /** The name of the app. */
    protected String appName;
    /** Where the app should be launched (user vs. server host). */
    protected String launchLocation;
    /** 
     * User that should launch the app. 
     * (Only used when launchLocation == user).
     */
    protected String launchUser;
    /** Cell should be configured so that primary window is in best view. */
    protected boolean isBestView;
    /** The command which launches the app. */
    protected String command;
    /** Subclass-specific data for making a peer-to-peer connection between master and slave. */
    protected String connectionInfo;

    /** Create an instance of AppConventionalCellClientState. */
    public AppConventionalCellClientState() {
        super();
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getLaunchLocation() {
        return launchLocation;
    }

    public void setLaunchLocation(String launchLocation) {
        this.launchLocation = launchLocation;
    }

    public String getLaunchUser() {
        return launchUser;
    }

    public void setLaunchUser(String launchUser) {
        this.launchUser = launchUser;
    }

    public boolean isBestView() {
        return isBestView;
    }

    public void setBestView(boolean bestView) {
        isBestView = bestView;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setConnectionInfo(String connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    public String getConnectionInfo() {
        return connectionInfo;
    }
}
