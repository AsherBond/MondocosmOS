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
package org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat;

import org.jdesktop.wonderland.common.messages.Message;

import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;

/**
 *
 * @author jprovino
 */
public class VoiceChatHoldMessage extends VoiceChatMessage {
    
    private PresenceInfo callee;
    private boolean onHold;
    private double volume;
    private String COSName;

    /*
     * Put call on hold
     */
    public VoiceChatHoldMessage(String group, PresenceInfo callee, boolean onHold, 
	    double volume, String COSName) {

	super(group);

	this.callee = callee;
	this.onHold = onHold;
	this.volume = volume;
	this.COSName = COSName;
    }
	 
    public PresenceInfo getCallee() {
	return callee;
    }

    public boolean isOnHold() {
	return onHold;
    }

    public double getVolume() {
	return volume;
    }

    public String getCOSName() {
	return COSName;
    }

}
