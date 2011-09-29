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
package org.jdesktop.wonderland.common.cell.messages;

import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.messages.MessageID;
import org.jdesktop.wonderland.common.messages.ResponseMessage;

/**
 * A response message giving the cell server state. This message is always
 * sent when a query is made to get the cell server state with the "GET"
 * CellServerStateMessage.
 * 
 * @author Jordasn Slott <jslott@dev.java.net>
 */
@InternalAPI
public class CellServerStateResponseMessage extends ResponseMessage {

    private CellServerState serverState;
    
    /**
     * Constructor, takes the ID of the message and the cell server state.
     *
     * @param messageID the id of the message to which we are responding
     * @param viewCellID the id of the view cell
     */
    public CellServerStateResponseMessage(MessageID messageID, CellServerState serverState) {
        super (messageID);
        this.serverState = serverState;
    }

    public CellServerState getCellServerState() {
        return serverState;
    }
}
