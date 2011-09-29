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
 * The app base conventional cell set connection info message.
 * 
 * @author deronj
 */
@InternalAPI
public class AppConventionalCellSetConnectionInfoMessage extends CellMessage {

    /** Subclass-specific data for making a peer-to-peer connection between master and slave. */
    private String connectionInfo;

    /** The default constructor */
    public AppConventionalCellSetConnectionInfoMessage() {
    }

    /**
     * Creates a new instance of AppConventionalSetConnectionInfoMessage.
     * 
     * @param cellID The ID of the cell whose connection info is to be changed.
     * @param connectionInfo Subclass-specific data for making a peer-to-peer connection between 
     * master and slave.
     */
    public AppConventionalCellSetConnectionInfoMessage(CellID cellID, String connectionInfo) {
        super (cellID);

        this.connectionInfo = connectionInfo;
    }
    
    /**
     * Sets the subclass data of the message.
     */
    public void setConnectionInfo(String connInfo) {
        connectionInfo = connInfo;
    }

    /**
     * Returns the subclass data.
     */
    public String getConnectionInfo() {
        return connectionInfo;
    }
}

