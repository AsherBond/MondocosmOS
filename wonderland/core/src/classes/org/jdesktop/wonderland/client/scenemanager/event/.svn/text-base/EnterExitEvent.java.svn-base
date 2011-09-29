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
package org.jdesktop.wonderland.client.scenemanager.event;

import java.util.Arrays;
import java.util.LinkedList;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.client.input.Event;

/**
 * Event when the user has entered/exited an Entity. Note that this event
 * represents when the user input has entered/exited an Entity (e.g. via a
 * mouse event), and has nothing to do with a user's avatar. The Entity in
 * question is given as the first element of the list returned by the
 * getEntityList() method.
 *  
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class EnterExitEvent extends SceneEvent {
    /* True if the enter, false if exit */
    private boolean isEnter = false;

    /** Default constructor */
    public EnterExitEvent() {
    }
    
    /** Constructor, takes the Enitity that has been entered/exited. */
    public EnterExitEvent(Entity entity, boolean isEnter) {
        super(new LinkedList(Arrays.asList(entity)));
        this.isEnter = isEnter;
    }

    /**
     * Returns true if this is an enter event, false if it is an exit event.
     * 
     * @return True for enter, false for exit
     */
    public boolean isEnter() {
        return isEnter;
    }
    
    /** 
     * {@inheritDoc}
     * <br>
     * If event is null, a new event of this class is created and returned.
     */
    @Override
    public Event clone (Event event) {
        if (event == null) {
            event = new EnterExitEvent();
        }
        ((EnterExitEvent) event).isEnter = isEnter;
        return super.clone(event);
    }
}
