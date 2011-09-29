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

import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;

/**
 * The message sent by the server to clients when an app base conventional cell app exits.
 * 
 * @author deronj
 */
@InternalAPI
public class AppConventionalCellAppExittedMessage extends CellMessage {

    /** The exit value of the application. */
    private int exitValue;

    /** The name of the app. */
    private String appName;

    /** The command which was used to execute the app. */
    private String command;

    /**
     * Creates a new instance of AppConventionalCellAppExittedMessage.
     * 
     * @param cellID The ID of the cell whose connection info is to be changed.
     * @param connectionInfo Subclass-specific data for making a peer-to-peer connection between 
     * master and slave.
     */
    public AppConventionalCellAppExittedMessage(CellID cellID, int exitValue, String appName, 
                                                String command)
    {
        super (cellID);

        this.exitValue = exitValue;
        this.appName = appName;
        this.command = command;
    }
    
    /**
     * Sets the exit value of the message.
     */
    public void setExitValue(int exitValue) {
        this.exitValue = exitValue;
    }

    /**
     * Returns the exit value.
     */
    public int getExitValue() {
        return exitValue;
    }

    /**
     * Sets the app name of the message.
     */
    public void setAppName (String appName) {
        this.appName = appName;
    }

    /**
     * Returns the app name.
     */
    public String getAppName() {
        return appName;
    }

    /**
     * Sets the command of the message.
     */
    public void setCommand (String command) {
        this.command = command;
    }

    /**
     * Returns the command.
     */
    public String getCommand() {
        return command;
    }
}

