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
package org.jdesktop.wonderland.modules.presencemanager.common;

import java.io.Serializable;

import java.math.BigInteger;

import org.jdesktop.wonderland.common.auth.WonderlandIdentity;

import org.jdesktop.wonderland.common.cell.CellID;

/**
 * Presence Information 
 * @author jprovino
 */
public class PresenceInfo implements Serializable {

    private final CellID cellID;
    private final BigInteger clientID;
    private final WonderlandIdentity userID;
    private final String callID;

    private boolean speaking;
    private boolean muted;
    private boolean inConeOfSilence;
    private boolean inSecretChat;

    private String usernameAlias;

    public PresenceInfo(CellID cellID, BigInteger clientID, 
	    WonderlandIdentity userID, String callID) {

        this.cellID = cellID;
	this.clientID = clientID;
        this.userID = userID;
	this.callID = callID;

	usernameAlias = userID.getUsername();
    }

    public String getCallID() {
        return callID;
    }

    public CellID getCellID() {
        return cellID;
    }

    public BigInteger getClientID() {
        return clientID;
    }

    public WonderlandIdentity getUserID() {
        return userID;
    }

    public boolean isInConeOfSilence() {
        return inConeOfSilence;
    }

    public void setInConeOfSilence(boolean inConeOfSilence) {
        this.inConeOfSilence = inConeOfSilence;
    }

    public boolean isInSecretChat() {
        return inSecretChat;
    }

    public void setInSecretChat(boolean inSecretChat) {
        this.inSecretChat = inSecretChat;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public boolean isSpeaking() {
        return speaking;
    }

    public void setSpeaking(boolean speaking) {
        this.speaking = speaking;
    }

    public String getUsernameAlias() {
        return usernameAlias;
    }

    public void setUsernameAlias(String usernameAlias) {
        this.usernameAlias = usernameAlias;
    }

    @Override
    public boolean equals(Object info) {
	if (!(info instanceof PresenceInfo)) {
            return false;
        }

        PresenceInfo pi = (PresenceInfo) info;

	if (cellID == null) {
	    return callID.equals(pi.callID);
	}

	return cellID.equals(pi.cellID);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.cellID != null ? this.cellID.hashCode() : 0);
        hash = 37 * hash + (this.callID != null ? this.callID.hashCode() : 0);
        return hash;
    }

    @Override
    public Object clone() {
	return new PresenceInfo(cellID, clientID, userID, callID);
    }

    @Override
    public String toString() {
	String s = "null";

	if (userID != null) {
	    s = userID.toString();
	}

        return "cellID=" + cellID + ", userID=" + s
            + ", clientID=" + clientID + ", callID=" + callID 
	    + ", alias=" + usernameAlias 
	    + ", isSpeaking " + speaking
	    + ", isMuted " + muted
	    + ", inConeOfSilence " + inConeOfSilence
	    + ", inSecretChat " + inSecretChat;
    }

}
