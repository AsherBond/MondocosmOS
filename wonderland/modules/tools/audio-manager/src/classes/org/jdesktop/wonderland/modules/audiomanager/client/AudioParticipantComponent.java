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
package org.jdesktop.wonderland.modules.audiomanager.client;


import java.util.logging.Logger;


import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellComponent;
import org.jdesktop.wonderland.client.cell.ChannelComponent;
import org.jdesktop.wonderland.client.cell.ChannelComponent.ComponentMessageReceiver;

import org.jdesktop.wonderland.client.cell.annotation.UsesCellComponent;

import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellStatus;

import org.jdesktop.wonderland.common.cell.CellID;

import org.jdesktop.wonderland.common.cell.messages.CellMessage;

import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;

import org.jdesktop.wonderland.modules.audiomanager.common.AudioParticipantComponentClientState;

import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManager;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManagerFactory;

import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;

/**
 * A component that provides audio participant control
 * 
 * @author jprovino
 */
@ExperimentalAPI
public class AudioParticipantComponent extends CellComponent implements ComponentMessageReceiver {
    
    private static Logger logger = Logger.getLogger(AudioParticipantComponent.class.getName());

    private ChannelComponent channelComp;

    PresenceManager pm;

    public AudioParticipantComponent(Cell cell) {
        super(cell);

	pm = PresenceManagerFactory.getPresenceManager(cell.getCellCache().getSession());
    }
    
   @Override
    public void setClientState(CellComponentClientState clientState) {
        super.setClientState(clientState);

	AudioParticipantComponentClientState state = (AudioParticipantComponentClientState) 
	    clientState;

	logger.fine("setClientState for " + cell.getCellID() 
	    + " " + state.isSpeaking() + " " + state.isMuted());

	//setSpeakingIndicator(cell.getCellID(), state.isSpeaking());
	//setMuteIndicator(cell.getCellID(), state.isMuted());
    }

    @Override
    protected void setStatus(CellStatus status, boolean increasing) {
	super.setStatus(status, increasing);

	switch(status) {
        case DISK:
            break;

	case ACTIVE:
            if (increasing) {
                channelComp = cell.getComponent(ChannelComponent.class);
	        break;
            }
	}
    }
    
    public void messageReceived(CellMessage message) {
    }

}
