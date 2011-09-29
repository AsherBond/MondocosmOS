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
package org.jdesktop.wonderland.client.input;

import org.jdesktop.wonderland.client.input.InputManager.FocusChange;
import org.jdesktop.wonderland.common.InternalAPI;

/**
 * An internal event which is used to change focus sets in a way which is 
 * atomic with repect to the <code>EventDistributor</code>'sevent queue.
 *
 * @author deronj
 */

@InternalAPI
public class FocusChangeEvent extends Event {

    static {
	/** Allocate this event type's class ID. */
	EVENT_CLASS_ID = Event.allocateEventClassID();
    }

    /** The ways in which this event will change the focus sets. */
    private FocusChange[] changes;

    /** Create a new instance of FocusChangeEvent. */
    public FocusChangeEvent (FocusChange[] changes) {
	this.changes = changes;
    }

    /** Return the changes of this event */
    public FocusChange[] getChanges () {
	return changes;
    }
}
