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
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;

/**
 * The app base conventional cell set connection info message.
 * 
 * @author deronj
 */
@InternalAPI
public class App2DCellPerformFirstMoveMessage extends CellMessage {
    /** The transform of the cell. */
    private CellTransform cellTransform;

    /**
     * Creates a new instance of App2DPerformFirstMoveMessage.
     * 
     * @param cellID The ID of the cell whose connection info is to be changed.
     * @param connectionInfo Subclass-specific data for making a peer-to-peer connection between 
     * master and slave.
     */
    public App2DCellPerformFirstMoveMessage(CellID cellID, CellTransform cellTransform) {
        super (cellID);
        this.cellTransform = cellTransform;
    }
    
    /**
     * Sets the cell transform of the message.
     */
    public void setCellTransform(CellTransform cellTransform) {
        this.cellTransform = cellTransform;
    }

    /**
     * Returns the cell transform.
     */
    public CellTransform getCellTransform () {
        return cellTransform;
    }
}

