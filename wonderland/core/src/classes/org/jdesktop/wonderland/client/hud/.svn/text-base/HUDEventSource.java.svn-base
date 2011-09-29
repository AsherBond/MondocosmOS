/*
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
package org.jdesktop.wonderland.client.hud;

import org.jdesktop.wonderland.client.hud.HUDEvent.HUDEventType;

/**
 * A source of HUD events.
 *
 * @author nsimpson
 */
public interface HUDEventSource {

    /**
     * Adds a listener for HUD object events
     * @param listener a new listener
     */
    public void addEventListener(HUDEventListener listener);

    /**
     * Removes a listener for HUD object events
     * @param listener the listener to remove
     */
    public void removeEventListener(HUDEventListener listener);

    /**
     * Gets the listeners for this HUD object
     * @return a list of event listeners
     */
    public HUDEventListener[] getEventListeners();

    /**
     * Notifies this HUD object's event listeners of an event
     * @param event a HUD event
     */
    public void notifyEventListeners(HUDEvent event);

    /**
     * Convenience method for notifying event listeners
     * @param eventType the type of the notification event
     */
    public void notifyEventListeners(HUDEventType eventType);
}
