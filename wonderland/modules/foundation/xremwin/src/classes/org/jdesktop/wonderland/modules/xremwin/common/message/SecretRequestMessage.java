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
package org.jdesktop.wonderland.modules.xremwin.common.message;

import java.math.BigInteger;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.security.ViewAction;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.common.security.annotation.Actions;

/**
 * A message sent to the XrwSecurityConnectionHandler to request a client's
 * secret for the given cell.
 * <p>
 * This message requires view permissions on the cell that is requested. The
 * resource to enforce this is set up in the connection handler.
 *
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
@Actions(ViewAction.class)
public class SecretRequestMessage extends Message implements XrwSecurityMessage {
    private CellID cellID;
    private BigInteger clientID;

    public SecretRequestMessage(BigInteger clientID, CellID cellID) {
        super();

        this.clientID = clientID;
        this.cellID = cellID;
    }

    public BigInteger getClientID() {
        return clientID;
    }

    public CellID getCellID() {
        return cellID;
    }
}
