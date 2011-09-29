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
package org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer;

import org.jdesktop.wonderland.client.input.Event;

/**
 * Set name tag
 *
 * @author jprovino
 */
public class AvatarNameEvent extends Event {

    public enum EventType {

        STARTED_SPEAKING,
        STOPPED_SPEAKING,
        MUTE,
        UNMUTE,
        CHANGE_NAME,
        ENTERED_CONE_OF_SILENCE,
        EXITED_CONE_OF_SILENCE,
        HIDE,
        SMALL_FONT,
        REGULAR_FONT,
        LARGE_FONT
    }

    private EventType eventType;
    private String username;
    private String usernameAlias;

    public AvatarNameEvent(EventType eventType, String username, String usernameAlias) {
	this.eventType = eventType;
	this.username = username;
	this.usernameAlias = usernameAlias;
    }

    public void setEventType(EventType eventType) {
	this.eventType = eventType;
    }

    public EventType getEventType() {
	return eventType;
    }

    public void setUsername(String username) {
	this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsernameAlias(String usernameAlias) {
	this.usernameAlias = usernameAlias;
    }

    public String getUsernameAlias() {
	return usernameAlias;
    }

    @Override
    public Event clone(Event evt) {
        if (evt == null) {
            evt = new AvatarNameEvent(eventType, username, usernameAlias);
        } else {
            AvatarNameEvent e = (AvatarNameEvent) evt;

	    e.setEventType(eventType);
	    e.setUsername(username);
	    e.setUsernameAlias(usernameAlias);
        }

        super.clone(evt);
        return evt;
    }

    public String toString() {
	return "AvatarNameEvent:  " + eventType + " " + username + " usernameAlias " 
	    + usernameAlias;
    }

}
