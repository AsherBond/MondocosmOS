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
package org.jdesktop.wonderland.modules.security.common.messages;

import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;

/**
 * Request an update to this client's permissions
 * @author jkaplan
 */
public class PermissionsRequestMessage extends CellMessage {
    /** the cell we are requesting permissions for */
    private CellID requestCellID;

    public PermissionsRequestMessage() {
        this (null);
    }

    public PermissionsRequestMessage(CellID requestCellID) {
        this.requestCellID = requestCellID;
    }

    /**
     * Get the requested cell id. If the cell id is null, get the permissions
     * for the cell this component is attached to.
     */
    public CellID getRequestCellID() {
        return requestCellID;
    }
}
