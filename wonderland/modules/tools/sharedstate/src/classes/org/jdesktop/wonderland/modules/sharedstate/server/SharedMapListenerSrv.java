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
package org.jdesktop.wonderland.modules.sharedstate.server;

/**
 * A listener for server-side value changes.  Server side listeners can
 * veto change requests by returning false from the propertyChanged message.
 * Listeners will be processed in the order they were added, until either the
 * list ends or a listener returns false.
 * @author jkaplan
 */
public interface SharedMapListenerSrv {
    /**
     * Notification that a property changed
     * @param event the event that describes the change
     * @return true to accept the change, or false to reject it.
     */
    public boolean propertyChanged(SharedMapEventSrv event);
}
