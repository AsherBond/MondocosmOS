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
package org.jdesktop.wonderland.client.jme.input;

import java.awt.dnd.DropTargetDragEvent;
import org.jdesktop.wonderland.client.input.Event;

/**
 * 3D representation of a DropTarget drag enter event
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
public class DropTargetDragEnterEvent3D extends DropTargetDragEvent3D {
    static {
        /** Allocate this event type's class ID. */
        EVENT_CLASS_ID = Event.allocateEventClassID();
    }

    /**
     * Default constructor for cloning
     */
    protected DropTargetDragEnterEvent3D() {
    }

    /**
     * Constructor
     */
    public DropTargetDragEnterEvent3D(DropTargetDragEvent dropEvent)
    {
        super (dropEvent);
    }

    @Override
    public Event clone(Event event) {
        if (event == null) {
            event = new DropTargetDragEnterEvent3D();
        }

        return super.clone(event);
    }
}
