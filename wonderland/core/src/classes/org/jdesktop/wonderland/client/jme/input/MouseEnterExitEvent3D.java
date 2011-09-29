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

import java.awt.event.MouseEvent;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.mtgame.PickDetails;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * An event which indicates that the mouse entered or exited a 3D object.
 *
 * @author deronj
 */
@ExperimentalAPI
public class MouseEnterExitEvent3D extends MouseEvent3D {

    static {
        /** Allocate this event type's class ID. */
        EVENT_CLASS_ID = Event.allocateEventClassID();
    }
    /** Whether this event was generated as a result of an interactive user event. */
    private boolean userGenerated;

    /** Default constructor (for cloning) */
    protected MouseEnterExitEvent3D() {
    }

    /**
     * Create a new instance of user generated MouseEnterExitEvent3D with a null pickDetails.
     * @param awtEvent The AWT event.
     */
    MouseEnterExitEvent3D(MouseEvent awtEvent) {
        this(awtEvent, true);
    }

    /**
     * Create a new instance of MouseEnterExitEvent3D with a null pickDetails.
     * @param awtEvent The AWT event.
     * @param userGenerated Whether this event was generated as a result of an interactive user event.
     */
    MouseEnterExitEvent3D(MouseEvent awtEvent, boolean userGenerated) {
        this(awtEvent, userGenerated, null);
    }

    /**
     * Create a new instance of MouseEnterExitEvent3D.
     * @param awtEvent The AWT event.
     * @param userGenerated Whether this event was generated as a result of an interactive user event.
     * @param pickDetails The pick data for the event.
     */
    MouseEnterExitEvent3D(MouseEvent awtEvent, boolean userGenerated, PickDetails pickDetails) {
        super(awtEvent, pickDetails);
        this.userGenerated = userGenerated;
    }

    /**
     * Returns true if this event indicates a mouse enter. Otherwise returns
     * false to indicate a mouse exit.
     */
    public boolean isEnter() {
        return awtEvent.getID() == MouseEvent.MOUSE_ENTERED;
    }

    /**
     * Returns whether this event was generated as a result of an interactive user event.
     */
    public boolean isUserGenerated() {
        return userGenerated;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Mouse " + enterAction() + ", userGen=" + isUserGenerated();
    }

    private String enterAction() {
        if (isEnter()) {
            return "ENTER";
        } else {
            return "EXIT";
        }
    }

    /** 
     * {@inheritDoc}
     * <br>
     * If event is null, a new event of this class is created and returned.
     */
    @Override
    public Event clone(Event event) {
        if (event == null) {
            event = new MouseEnterExitEvent3D();
        }
        ((MouseEnterExitEvent3D) event).userGenerated = userGenerated;
        return super.clone(event);
    }
}
