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

import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;

import org.jdesktop.wonderland.common.ExperimentalAPI;

import org.jdesktop.wonderland.common.messages.Message;

/**
 * The initial message that a client must send to the Wonderland server
 * in order to specify a communications protocol to use.
 * @author jprovino
 */
@ExperimentalAPI
public class PlaceCallRequestMessage extends Message {

    private PresenceInfo presenceInfo;
    private String sipURL;	      // URL of softphone to call
    private double x;	      	      // location of the call
    private double y;
    private double z;
    private double direction;	      // direction of avatar
    private boolean confirmAnswered;  // user has to press 1

    public PlaceCallRequestMessage(PresenceInfo presenceInfo, 
	    String sipURL, double x, double y,
	    double z, double direction, boolean confirmAnswered) {

	this.presenceInfo = presenceInfo;
	this.sipURL = sipURL;
	this.x = x;
	this.y = y;
	this.z = z;
	this.direction = direction;
	this.confirmAnswered = confirmAnswered;
    }

    public PresenceInfo getPresenceInfo() {
	return presenceInfo;
    }

    public String getSipURL() {
	return sipURL;
    }

    public double getX() {
	return x;
    }

    public double getY() {
	return y;
    }

    public double getZ() {
	return z;
    }

    public double getDirection() {
	return direction;
    }

    public boolean getConfirmAnswered() {
	return confirmAnswered;
    }

}
