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
package org.jdesktop.wonderland.server.cell;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.server.cell.ChannelComponentMO.ComponentMessageReceiver;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;
import org.jdesktop.wonderland.server.eventrecorder.RecorderManager;

/**
 * An abstract base class that implements the channel component message
 * receiver interface and hides Darkstar implementation details from the
 * developer.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public abstract class AbstractComponentMessageReceiver implements ComponentMessageReceiver {

    private ManagedReference<CellMO> cellRef = null;
    private ManagedReference<ChannelComponentMO> channelRef = null;
    /**
     * Constructor, takes the cell associated with the channel component
     */
    public AbstractComponentMessageReceiver(CellMO cellMO) {
        cellRef = AppContext.getDataManager().createReference(cellMO);
        ChannelComponentMO channelComponent = (ChannelComponentMO) cellMO.getComponent(ChannelComponentMO.class);
        if (channelComponent == null) {
            throw new IllegalStateException("Cell does not have a ChannelComponent");
        }
        channelRef = AppContext.getDataManager().createReference(channelComponent); 
    }
    
    /**
     * Returns the cell associated with this message receiver.
     * 
     * @return The CellMO
     */
    public CellMO getCell() {
        return cellRef.getForUpdate();
    }
    
    /**
     * Returns the channel component associated with this message receiver.
     * 
     * @return The ChannelComponentMO
     */
    public ChannelComponentMO getChannelComponent() {
        return channelRef.getForUpdate();
    }
    
    public abstract void messageReceived(WonderlandClientSender sender,
            WonderlandClientID clientID, CellMessage message);

    public final void recordMessage(WonderlandClientSender sender,
                                    WonderlandClientID clientID,
                                    CellMessage message ) {

        RecorderManager mgr = RecorderManager.getDefaultManager();
        if (mgr.isRecording()) {
            mgr.recordMessage(sender, clientID, message);
            postRecordMessage(sender, clientID, message);
        }
    }

    /**
     * Subclasses may override this method to perform specialised recordings for their
     * component or cell.
     * @param sender the sender of the message
     * @param clientID the id of the client sending the message
     * @param message
     */
    protected void postRecordMessage(WonderlandClientSender sender, WonderlandClientID clientID, CellMessage message) {
        //No op
        //There is no requirement for subclasses to call this method using super.
    }
}
