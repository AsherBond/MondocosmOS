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

import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * A simplified event listener which provides the input system with an array of event classes.
 * By doing this the listener is telling the input system that it wishes to receive only events of these classes.
 * Furthermore, the events are to be received only when the corresponding entity has focus.
 * If you wish to not honor focus, use <code>EventClassListener</code> instead.
 * This class uses <code>isAssignableFrom</code> to compare the event class with the desired event classes, so if
 * the return array of <code>eventClassesToConsume</code> contains a superclass, events of that class and 
 * all subclasses will be consumed.
 *
 * @author deronj
 */

@ExperimentalAPI
public class EventClassFocusListener extends EventClassListener {

    /**
     * Note on subclassing: the subclass should override this method.
     * @return An array of the event classes the listener wishes to consume.
     */
    @Override
    public Class[] eventClassesToConsume () {
	return null;
    }

    /**
     * INTERNAL ONLY.
     */
    @InternalAPI
    @Override
    public boolean consumesEvent (Event event) {
	if (!event.isFocussed()) return false;
	return super.consumesEvent(event);
    }
}
