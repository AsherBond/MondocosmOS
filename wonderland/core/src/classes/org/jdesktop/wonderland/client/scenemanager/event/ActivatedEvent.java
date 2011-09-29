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
 * Event when an Entity is activated.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ActivatedEvent extends SceneEvent {
    /** Default constructor */
    public ActivatedEvent() {
    }
    
    /** Constructor, takes the Enitity that has been activated. */
    public ActivatedEvent(Entity entity) {
        super(new LinkedList(Arrays.asList(entity)));
    }
    
    /** 
     * {@inheritDoc}
     * <br>
     * If event is null, a new event of this class is created and returned.
     */
    @Override
    public Event clone (Event event) {
        if (event == null) {
            event = new ActivatedEvent();
        }
        return super.clone(event);
    }
}
