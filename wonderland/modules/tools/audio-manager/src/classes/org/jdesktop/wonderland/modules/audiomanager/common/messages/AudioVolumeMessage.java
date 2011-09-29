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
package org.jdesktop.wonderland.modules.audiomanager.common.messages;

import org.jdesktop.wonderland.common.cell.messages.CellMessage;

import org.jdesktop.wonderland.common.cell.CellID;

/**
 *
 * @author jprovino
 */
public class AudioVolumeMessage extends CellMessage {
    
    private String softphoneCallID;
    private String otherCallID;
    private double volume;
    private boolean isSetVolume;

    public AudioVolumeMessage(CellID cellID, String softphoneCallID, String otherCallID, double volume, 
	    boolean isSetVolume) {

	super(cellID);

	this.softphoneCallID = softphoneCallID;
	this.otherCallID = otherCallID;
	this.volume = volume;
	this.isSetVolume = isSetVolume;
    }
    
    public String getSoftphoneCallID() {
	return softphoneCallID;
    }

    public String getOtherCallID() {
	return otherCallID;
    }

    public void setVolume(double volume) {
	this.volume = volume;
    }

    public double getVolume() {
	return volume;
    }

    public boolean isSetVolume() {
	return isSetVolume;
    }

}
