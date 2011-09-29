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
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.common.messages.MessageID;
import org.jdesktop.wonderland.common.messages.ResponseMessage;

/**
 * A response message giving the server state of a single cell component. This
 * message is sent when the client requests addition of a new cell component
 * in the case that the addition is successful.
 * 
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
@InternalAPI
public class CellServerComponentResponseMessage extends ResponseMessage {

    private CellComponentServerState serverState;
    
    /**
     * Constructor, takes the ID of the message and the cell component
     * server state.
     *
     * @param messageID the id of the message to which we are responding
     * @param serverState the state of the cell component
     */
    public CellServerComponentResponseMessage(MessageID messageID, 
                                              CellComponentServerState serverState)
    {
        super (messageID);
        this.serverState = serverState;
    }

    public CellComponentServerState getCellComponentServerState() {
        return serverState;
    }
}
