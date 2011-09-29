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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The WFS server state class for AppConventionalCellMO.
 * 
 * @author deronj
 */
public abstract class AppConventionalCellServerState extends App2DCellServerState {

    /** The name of the app. */
    @XmlElement(name = "appName")
    public String appName;
    /** Where the app should be launched (user vs. server host). */
    @XmlElement(name = "launchLocation")
    public String launchLocation;
    /** 
     * User that should launch the app. 
     * (Only used when launchLocation == user).
     */
    @XmlElement(name = "launchUser")
    public String launchUser;
    /** Cell should be configured so that primary window is in best view. */
    @XmlElement(name = "bestView")
    public boolean isBestView;
    /** The command which launches the app. */
    @XmlElement(name = "command")
    public String command;

    /** Default constructor */
    public AppConventionalCellServerState() {
    }

    @XmlTransient
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @XmlTransient
    public String getLaunchLocation() {
        return launchLocation;
    }

    public void setLaunchLocation(String launchLocation) {
        this.launchLocation = launchLocation;
    }

    @XmlTransient
    public String getLaunchUser() {
        return launchUser;
    }

    public void setLaunchUser(String launchUser) {
        this.launchUser = launchUser;
    }

    @XmlTransient
    public boolean isBestView() {
        return isBestView;
    }

    public void setBestView(boolean bestView) {
        isBestView = bestView;
    }

    @XmlTransient
    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Returns a string representation of this class.
     *
     * @return The server state information as a string.
     */
    @Override
    public String toString() {
        return super.toString() + " [AppConventionalCellServerState]: " +
                "appName=" + appName + 
                ",launchLocation=" + launchLocation + 
                ",launchUser=" + launchUser + 
                ",isBestView=" + isBestView + 
                ",command=" + command;
    }
}
