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
package org.jdesktop.wonderland.modules.affordances.client.event;

import org.jdesktop.wonderland.client.input.Event;

/**
 * Event that indicates that affordances should remove themselves from the
 * Cell.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class AffordanceRemoveEvent extends Event {

    /** Default constructor */
    public AffordanceRemoveEvent() {
    }

    /**
     * {@inheritDoc}
     * <br>
     * If event is null, a new event of this class is created and returned.
     */
    @Override
    public Event clone (Event event) {
        if (event == null) {
            event = new AffordanceRemoveEvent();
        }
        return super.clone(event);
    }
}
