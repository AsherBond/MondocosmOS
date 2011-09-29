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

import java.awt.event.FocusEvent;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.mtgame.Entity;

/**
 * An event which indicates that the key focus has been gained or lost.
 *
 * @author deronj
 */
@ExperimentalAPI
public class FocusEvent3D extends Event {

    static {
        /** Allocate this event type's class ID. */
        EVENT_CLASS_ID = Event.allocateEventClassID();
    }

    /** The originating AWT input event. */
    protected FocusEvent awtEvent;

    /** Default constructor (for cloning) */
    protected FocusEvent3D() {
    }

    /** 
     * Create a new instance of <code>FocusEvent3D</code>.
     * @param awtEvent The originating AWT key event.
     */
    FocusEvent3D(FocusEvent awtEvent) {
        this.awtEvent = awtEvent;
    }

    /**
     * Returns the ID of the associated AWT input event.
     */
    public int getID() {
        return awtEvent.getID();
    }

    /**
     * Returns the associated AWT input event
     */
    public FocusEvent getAwtEvent() {
        return awtEvent;
    }

    /**
     * Returns true if this event is a key focus gain.
     */
    public boolean isGained() {
        return (awtEvent.getID() == FocusEvent.FOCUS_GAINED);
    }

    /** Always returns null. This event doesn't have an associated entity. */
    @Override
    public Entity getEntity () {
	return null;
    }

    /** Always returns false. This event doesn't have an associated entity. */
    @Override
    public boolean isFocussed () {
        return false;
    }

    @Override
    public void setEntity (Entity entity) {
    }

    @Override
    public void setFocussed (boolean focussed) {
    }

    /** {@inheritDoc} */
    @Override
    public Event clone(Event event) {
        if (event == null) {
            event = new FocusEvent3D();
        }
        ((FocusEvent3D) event).awtEvent = awtEvent;
        return event;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Focus " + focusAction();
    }

    private String focusAction() {
        if (isGained()) {
            return "GAINED";
        } else {
            return "LOST";
        }
    }
}
