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

import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTargetEvent;
import org.jdesktop.wonderland.client.input.Event;

/**
 * 3D representation of a DropTarget drag exit event
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
public class DropTargetDragExitEvent3D extends DropTargetEvent3D {
    static {
        /** Allocate this event type's class ID. */
        EVENT_CLASS_ID = Event.allocateEventClassID();
    }

    /**
     * Default constructor for cloning
     */
    protected DropTargetDragExitEvent3D() {
    }

    /**
     * Constructor
     */
    public DropTargetDragExitEvent3D(DropTargetEvent dropEvent)
    {
        super (dropEvent);
    }

    @Override
    public DataFlavor[] getDataFlavors() {
        // no data
        return null;
    }

    @Override
    public Object getTransferData(DataFlavor dataFlavor) {
        // no data
        return null;
    }


    @Override
    public Event clone(Event event) {
        if (event == null) {
            event = new DropTargetDragExitEvent3D();
        }

        return super.clone(event);
    }
}
