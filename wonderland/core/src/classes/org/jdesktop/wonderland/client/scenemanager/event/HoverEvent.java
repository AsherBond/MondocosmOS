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

import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.LinkedList;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.client.input.Event;

/**
 * Event when the user input (e.g. mouse pointer) starts and stops hovering
 * above an Entity. The Entity in question is given as the first element of the
 * list returned by the getEntityList() method.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class HoverEvent extends SceneEvent {
    /* True if the hovering has started, false if the hovering has stopped */
    private boolean isStart = false;

    /* The MouseEvent that resulted in this context event */
    private MouseEvent mouseEvent = null;

    /** Default constructor */
    public HoverEvent() {
    }
    
    /**
     * Constructor, takes the Enitity over which the hovering takes place and
     * whether hovering is starting or stopping and the mouse event that caused
     * the hover.
     */
    public HoverEvent(Entity entity, boolean isStart, MouseEvent mouseEvent) {
        super(new LinkedList(Arrays.asList(entity)));
        this.isStart = isStart;
        this.mouseEvent = mouseEvent;
    }
    
    /**
     * Returns true if this is a hover start event, false if it is a hover stop
     * event.
     * 
     * @return True for hover start, false for hover stop
     */
    public boolean isStart() {
        return isStart;
    }

    /**
     * Returns the mouse event that resulted in this hover event.
     *
     * @return The MouseEvent object
     */
    public MouseEvent getMouseEvent() {
        return mouseEvent;
    }

    /** 
     * {@inheritDoc}
     * <br>
     * If event is null, a new event of this class is created and returned.
     */
    @Override
    public Event clone (Event event) {
        if (event == null) {
            event = new HoverEvent();
        }
        ((HoverEvent) event).isStart = isStart;
        ((HoverEvent) event).mouseEvent = mouseEvent;
        return super.clone(event);
    }
}
