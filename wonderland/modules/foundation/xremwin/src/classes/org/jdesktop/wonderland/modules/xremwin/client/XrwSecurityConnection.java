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
package org.jdesktop.wonderland.modules.xremwin.client;

import java.math.BigInteger;
import java.util.logging.Level;
import javax.crypto.SecretKey;
import org.jdesktop.wonderland.client.comms.BaseConnection;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.ErrorMessage;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.common.messages.OKMessage;
import org.jdesktop.wonderland.common.messages.ResponseMessage;
import org.jdesktop.wonderland.modules.xremwin.common.XrwSecurityConnectionType;
import org.jdesktop.wonderland.modules.xremwin.common.message.SecretRequestMessage;
import org.jdesktop.wonderland.modules.xremwin.common.message.SecretResponseMessage;
import org.jdesktop.wonderland.modules.xremwin.common.message.TakeControlRequestMessage;

/**
 * A custom connection for querying Xrw security information.
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class XrwSecurityConnection extends BaseConnection {
    public ConnectionType getConnectionType() {
        return XrwSecurityConnectionType.TYPE;
    }

    /**
     * Get a secret key for the given clientID to access the given cell.
     * @param clientID the id of the client to get a secret key for
     * @param cellID the id of the client cell to request the secret for
     * @return the secret key for the given client, or null if the client
     * is not authorized to access the given cell
     */
    public SecretKey getSecret(BigInteger clientID, CellID cellID) {
        ResponseMessage rm;
        try {
            rm = sendAndWait(new SecretRequestMessage(clientID, cellID));
        } catch (InterruptedException ie) {
            AppXrw.logger.log(Level.WARNING, "Get key interruped", ie);
            return null;
        }

        if (rm instanceof ErrorMessage) {
            ErrorMessage em = (ErrorMessage) rm;
            AppXrw.logger.log(Level.WARNING, "Error getting secret for " +
                    clientID + " on cell " + cellID + ": " +
                    em.getErrorMessage(), em.getErrorCause());
            return null;
        }

        return ((SecretResponseMessage) rm).getSecret();
    }

    /**
     * Check if a client has permission to take control of the given cell.
     * @param clientID the id of the client to check control permissions for.
     * @param cellID the id of the cell to check permissions for
     * @return true if the client has permission, or false if not
     */
    public boolean checkTakeControl(BigInteger clientID, CellID cellID) {
        ResponseMessage rm;
        try {
            rm = sendAndWait(new TakeControlRequestMessage(clientID, cellID));
        } catch (InterruptedException ie) {
            AppXrw.logger.log(Level.WARNING, "Check take control interruped", ie);
            return false;
        }

        return (rm instanceof OKMessage);
    }

    @Override
    public void handleMessage(Message message) {
        // no messages to handle.  If the server sent any messages
        // we would handle them here (other than responses to our requests,
        // which are handled automatically).
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
