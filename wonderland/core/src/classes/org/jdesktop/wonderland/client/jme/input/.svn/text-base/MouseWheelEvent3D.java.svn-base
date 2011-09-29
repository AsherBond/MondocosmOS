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
package org.jdesktop.wonderland.client.jme.input;

import java.awt.event.MouseWheelEvent;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.mtgame.PickDetails;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * An event which indicates that a mouse wheel rotation occurred. 
 *
 * @author deronj
 */
@ExperimentalAPI
public class MouseWheelEvent3D extends MouseEvent3D {

    static {
        /** Allocate this event type's class ID. */
        EVENT_CLASS_ID = Event.allocateEventClassID();
    }

    /** Default constructor (for cloning) */
    protected MouseWheelEvent3D() {
    }

    /**
     * Create a new instance of MouseWheelEvent3D will a null pickDetails.
     * @param awtEvent The AWT event.
     */
    MouseWheelEvent3D(MouseWheelEvent awtEvent) {
        this(awtEvent, null);
    }

    /**
     * Create a new instance of MouseWheelEvent3D.
     * @param awtEvent The AWT event.
     * @param pickDetails The pick data for the event.
     */
    MouseWheelEvent3D(MouseWheelEvent awtEvent, PickDetails pickDetails) {
        super(awtEvent, pickDetails);
    }

    /**
     * Returns the number of "clicks" the mouse wheel was rotated.
     *
     * @return negative values if the mouse wheel was rotated up/away from the 
     * user, and positive values if the mouse wheel was rotated down/ 
     * towards the user
     */
    public int getWheelRotation() {
        return ((MouseWheelEvent) awtEvent).getWheelRotation();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Mouse Wheel, rot=" + getWheelRotation();
    }

    /** 
     * {@inheritDoc}
     * <br>
     * If event is null, a new event of this class is created and returned.
     */
    @Override
    public Event clone(Event event) {
        if (event == null) {
            event = new MouseWheelEvent3D();
        }
        return super.clone(event);
    }
}
