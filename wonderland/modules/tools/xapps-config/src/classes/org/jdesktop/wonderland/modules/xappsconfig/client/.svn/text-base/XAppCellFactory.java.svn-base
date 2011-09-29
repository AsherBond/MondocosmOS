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
package org.jdesktop.wonderland.modules.xappsconfig.client;

import java.awt.Image;
import java.util.Properties;
import org.jdesktop.wonderland.client.cell.registry.spi.CellFactorySPI;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.modules.xremwin.common.cell.AppCellXrwServerState;
import org.jdesktop.wonderland.common.cell.state.BoundingVolumeHint;
import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;
import java.util.logging.Logger;

/**
 * A generic cell factory which launches a specific X11 App. Takes the name of
 * the X11 app and the command to launch the app.
 * <p>
 * Two XAppCellFactory objects are equal if their app name's are equal.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class XAppCellFactory implements CellFactorySPI {

    private static final Logger logger = Logger.getLogger(XAppCellFactory.class.getName());

    // TODO: Part 2: temporary. This gives the ability to disable app-specific placement.
    // The other part of the boolean is in App2D.
    public static final boolean doAppInitialPlacement = true;

    private String appName = null;
    private String command = null;

    /**
     * Constructor, takes the display name of the app and the command to launch
     * the app, neither of which should be null. The app name should be unique
     * among all entries in the Cell Palette.
     *
     * @param appName The app name
     * @param command The command to launch the app
     */
    public XAppCellFactory(String appName, String command) {
        this.appName = appName;
        this.command = command;
    }

    /**
     * @inheritDoc()
     */
    public String[] getExtensions() {
        return new String[] {};
    }

    /**
     * @inheritDoc()
     */
    public <T extends CellServerState> T getDefaultCellServerState(Properties props) {

        AppCellXrwServerState serverState = new AppCellXrwServerState();
        serverState.setAppName(appName);

        // don't set a command -- the server will derive the command from
        // the app name
        serverState.setCommand(null);
        
        serverState.setLaunchLocation("server");

        if (doAppInitialPlacement) {
            // Disable system initial placement for app cells. Because cell bounds are
            // fixed at cell creation time we need to give app cells a huge bounds
            // (see the comment in App2DCellMO()). We won't know the right location to 
            // place an app cell until it's first window is made visible. Therefore,
            // we disable system placement and will perform the initial placement ourselves
            // later.
            logger.info("doAppInitialPlacement: disable system placement");
            BoundingVolumeHint hint = new BoundingVolumeHint(false, null);
            serverState.setBoundingVolumeHint(hint);
        }

        return (T) serverState;
    }

    /**
     * @inheritDoc()
     */
    public String getDisplayName() {
        return appName;
    }

    /**
     * @inheritDoc()
     */
    public Image getPreviewImage() {
        // TODO
        return null;
    }

    /**
     * The equals() method only depends upon the app name, not the command.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final XAppCellFactory other = (XAppCellFactory) obj;
        if ((this.appName == null) ? (other.appName != null) : !this.appName.equals(other.appName)) {
            return false;
        }
        return true;
    }

    /**
     * The hashCode() method only depends upon the app name, not the command.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.appName != null ? this.appName.hashCode() : 0);
        return hash;
    }
}
