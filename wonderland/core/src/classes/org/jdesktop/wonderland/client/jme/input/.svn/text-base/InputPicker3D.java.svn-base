/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

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

import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.FocusEvent;
import java.util.EventObject;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.input.InputPicker;
import org.jdesktop.wonderland.common.InternalAPI;

/**
 * A specific implementation of <code>InputPicker</code> which uses 3D events.
 *
 * @author deronj
 */

@InternalAPI
public class InputPicker3D extends InputPicker {

    /** The input picker singleton */
    private static InputPicker inputPicker;

    /**
     * Returns the entity resolver singleton.
     */
    static InputPicker getInputPicker() {
        if (inputPicker == null) {
            inputPicker = new InputPicker3D();
        }
        return inputPicker;
    }

    /**
     * {@inheritDoc}
     */
    @InternalAPI
    public Event createWonderlandEvent(EventObject eventObj) {
        Event event = null;

        if (eventObj instanceof KeyEvent) {
            event = new KeyEvent3D((KeyEvent) eventObj);
        } else if (eventObj instanceof FocusEvent) {
            event = new FocusEvent3D((FocusEvent) eventObj);
        } else if (eventObj instanceof MouseWheelEvent) {
            event = new MouseWheelEvent3D((MouseWheelEvent) eventObj);
        } else if (eventObj instanceof MouseEvent) {
            switch (((MouseEvent) eventObj).getID()) {
                case MouseEvent.MOUSE_CLICKED:
                case MouseEvent.MOUSE_RELEASED:
                case MouseEvent.MOUSE_PRESSED:
                    event = new MouseButtonEvent3D((MouseEvent) eventObj);
                    break;
                case MouseEvent.MOUSE_ENTERED:
                case MouseEvent.MOUSE_EXITED:
                    event = new MouseEnterExitEvent3D((MouseEvent) eventObj);
                    break;
                case MouseEvent.MOUSE_MOVED:
                    event = new MouseMovedEvent3D((MouseEvent) eventObj);
                    break;
                case MouseEvent.MOUSE_DRAGGED:
                    event = new MouseDraggedEvent3D((MouseEvent) eventObj);
                    break;
                default:
                    logger.warning("Invalid AWT event type");
            }
        } else if (eventObj instanceof DropTargetDragEvent) {
            event = new DropTargetDragEvent3D((DropTargetDragEvent) eventObj);
        } else if (eventObj instanceof DropTargetDropEvent) {
            event = new DropTargetDropEvent3D((DropTargetDropEvent) eventObj);
        }

        return event;
    }
}
