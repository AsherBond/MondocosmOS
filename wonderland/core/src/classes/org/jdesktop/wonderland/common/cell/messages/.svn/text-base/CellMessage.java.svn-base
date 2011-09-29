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

import java.math.BigInteger;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.messages.Message;

/**
 * Message sent to a particular cell.
 * @author jkaplan
 */
@ExperimentalAPI
public abstract class CellMessage extends Message {
    /** the ID of the cell this message is for */
    private CellID cellID;

    /** the unique identifier of this message's sender */
    private BigInteger senderID;

    /**
     * Create a new cell message.  The cellID will be set automatically
     * when the message is sent.
     */
    public CellMessage() {
        this (null);
    }

    /**
     * Create a new cell message to the given cellID on the server
     * @param cellID the id of the cell to send to
     */
    public CellMessage(CellID cellID) {
        this.cellID = cellID;
    }
    
    /**
     * Get the ID of the cell this message is being sent to
     * @return the cellID
     */
    public CellID getCellID() {
        return cellID;
    }

    /**
     * Set the ID of the cell this message is being sent to.  If the message
     * is created without a CellID, this value will be filled in automatically
     * by the sender.
     * @param cellID the cellID to set
     */
    public void setCellID(CellID cellID) {
        this.cellID = cellID;
    }

    /**
     * Get the ID of this message's sender.  This is only valid on messages
     * sent from the server to the client.
     * @return the sender's ID, identical to calling
     * WonderlandSession.getSessionID() on the client session.  If the
     * sender's ID is not set, this method will return null. A
     * null ID typically means a message that originated at the server
     * and not with a particular client.
     */
    public BigInteger getSenderID() {
        return senderID;
    }

    /**
     * Set the ID of this message's sender.  This value will be filled in
     * automatically by the server when sending a message that originated
     * with a client.
     * @param senderID the sender's id.
     */
    public void setSenderID(BigInteger senderID) {
        this.senderID = senderID;
    }
}
