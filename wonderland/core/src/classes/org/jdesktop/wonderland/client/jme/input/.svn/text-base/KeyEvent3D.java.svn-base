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

import java.awt.event.KeyEvent;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * An event which indicates that a keystroke occurred in a component.
 *
 * @author deronj
 */
@ExperimentalAPI
public class KeyEvent3D extends InputEvent3D {

    static {
        /** Allocate this event type's class ID. */
        EVENT_CLASS_ID = Event.allocateEventClassID();
    }

    /** Default constructor (for cloning) */
    protected KeyEvent3D() {
    }

    /** 
     * Create a new instance of <code>KeyEvent3D</code>.
     * @param awtEvent The originating AWT key event.
     */
    KeyEvent3D(KeyEvent awtEvent) {
        super(awtEvent);
    }

    /**
     * Returns true if this event is a key type.
     */
    public boolean isTyped() {
        return (awtEvent.getID() == KeyEvent.KEY_TYPED);
    }

    /**
     * Returns true if this event is a key press.
     */
    public boolean isPressed() {
        return (awtEvent.getID() == KeyEvent.KEY_PRESSED);
    }

    /**
     * Returns true if this event is a key release.
     */
    public boolean isReleased() {
        return (awtEvent.getID() == KeyEvent.KEY_RELEASED);
    }

    /**
     * Returns the character associated with the key in this event.
     * For example, the <code>KEY_TYPED</code> event for shift + "a" returns
     * the value for "A".
     *
     * <code>KEY_PRESSED</code> and <code>KEY_RELEASED</code> events are not intended for 
     * reporting of character input. Therefore, the values returned
     * by this method are guaranteed to be meaningful only for
     * <code>KEY_TYPED</code> events.
     *
     * @return the Unicode character defined for this key event.
     * If no valid Unicode character exists for this key event,
     * <code>CHAR_UNDEFINED</code> is returned.
     */
    public char getKeyChar() {
        return ((KeyEvent) awtEvent).getKeyChar();
    }

    /**
     * Returns the integer key code associated with the key in this event.
     * The key code is the same as that of the originating AWT event.
     *
     * @return the integer code for an actual key on the keyboard.
     * (For <code>KEY_TYPED events</code>, the key code is <code>VK_UNDEFINED</code>.)
     */
    public int getKeyCode() {
        return ((KeyEvent) awtEvent).getKeyCode();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "Key " + keyAction() + ": keyCode=" + getKeyCode() + ", keyChar=" + getKeyChar();
    }

    private String keyAction() {
        if (isPressed()) {
            return "PRESS";
        } else if (isReleased()) {
            return "RELEASE";
        } else {
            return "CLICK";
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
            event = new KeyEvent3D();
        }
        return super.clone(event);
    }
}
