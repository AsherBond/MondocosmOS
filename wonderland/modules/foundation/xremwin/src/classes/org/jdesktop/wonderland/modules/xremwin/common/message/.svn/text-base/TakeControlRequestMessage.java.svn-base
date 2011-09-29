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
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.common.security.annotation.Actions;
import org.jdesktop.wonderland.modules.xremwin.common.TakeControlAction;

/**
 * A message sent to the XrwSecurityConnectionHandler to request a particular
 * client take control of the application.  If the user has permission to
 * take control of the application, the server will respond with an OK
 * message.  If not, the server will send an Error message.
 *
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
@Actions(TakeControlAction.class)
public class TakeControlRequestMessage extends Message implements XrwSecurityMessage {
    private CellID cellID;
    private BigInteger clientID;

    public TakeControlRequestMessage(BigInteger clientID, CellID cellID) {
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
