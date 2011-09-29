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

import java.util.List;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.client.input.Event;

/**
 * Event when a selection action has been taken for an Entity or a set of
 * Entities. The ordered list of Entities selected is obtained via the
 * getEntityList() method on the SceneEvent superclass.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class SelectionEvent extends SceneEvent {
    
    /** Default constructor */
    public SelectionEvent() {
    }
    
    /** Constructor, takes the list of Enitities of the context event. */
    public SelectionEvent(List<Entity> entities) {
        super(entities);
    }
    
    /** 
     * {@inheritDoc}
     * <br>
     * If event is null, a new event of this class is created and returned.
     */
    @Override
    public Event clone (Event event) {
        if (event == null) {
            event = new SelectionEvent();
        }
        return super.clone(event);
    }
}
