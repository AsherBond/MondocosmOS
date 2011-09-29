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
package org.jdesktop.wonderland.client.cell;

import org.jdesktop.wonderland.client.cell.ChannelComponent.ComponentMessageReceiver;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.common.cell.messages.CellClientStateMessage;

/**
 * A listener on a cell's channel to handle updates to its state sent from the
 * server.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class CellClientStateMessageReceiver implements ComponentMessageReceiver {

    private Cell cell = null;

    public CellClientStateMessageReceiver(Cell cell) {
        this.cell = cell;
    }

    public void messageReceived(CellMessage message) {
       cell.setClientState(((CellClientStateMessage)message).getClientState());
    }
}
