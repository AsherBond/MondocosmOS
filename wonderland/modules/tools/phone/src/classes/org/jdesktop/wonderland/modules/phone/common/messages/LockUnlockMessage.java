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
package org.jdesktop.wonderland.modules.phone.common.messages;

import org.jdesktop.wonderland.modules.phone.common.CallListing;

import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.messages.Message;

import org.jdesktop.wonderland.common.security.annotation.Actions;

import org.jdesktop.wonderland.modules.phone.common.cell.security.UnlockPhoneAction;

/**
 *
 * @author jprovino
 */
@Actions(UnlockPhoneAction.class)
public class LockUnlockMessage extends PhoneControlMessage {   
    
    private String password;
    private boolean lock;
    private boolean keepUnlocked;

    public LockUnlockMessage(CellID phoneCellID, String password, boolean lock, 
	    boolean keepUnlocked) {

	super(phoneCellID);

	this.password = password;
	this.lock = lock;
	this.keepUnlocked = keepUnlocked;
    }

    public String getPassword() {
	return password;
    }

    public boolean lock() {
	return lock;
    }

    public boolean keepUnlocked() {
	return keepUnlocked;
    }

}
