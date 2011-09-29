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
package org.jdesktop.wonderland.client.scenemanager;

import org.jdesktop.wonderland.client.input.Event;

/**
 * The SceneManagerPolicy interface defines the mapping between input events and
 * higher-level conception actions in Wonderland that is managed by the
 * Selection Manager.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public interface SceneManagerPolicy {
    
    /**
     * Returns true if the event corresponds to clearing the selection.
     * 
     * @param event The input event
     * @return True if the event clears the selection
     */
    public boolean isClearedSelection(Event event);
    
    /**
     * Returns true if the event corresponds to a single-select event.
     * 
     * @param event The input event
     * @return True if the event is a single-selection event
     */
    public boolean isSingleSelection(Event event);
    
    /**
     * Returns true if the event corresponds to a multi-selection event.
     * 
     * @param event The input event
     * @return True if the event is a multi-selection event
     */
    public boolean isMultiSelection(Event event);
    
    /**
     * Returns true if the event corresponds to an activation event.
     * 
     * @param event The input event
     * @return True if the event is an activation event
     */
    public boolean isActivation(Event event);
    
    /**
     * Returns true if the event corresponds to an Entity enter event.
     * 
     * @param event The input event
     * @return True if the event is an enter event
     */
    public boolean isEnter(Event event);
    
    /**
     * Returns true if the event corresponds to an Entity exit event.
     * 
     * @param event The input event
     * @return True if the event is an exit event
     */
    public boolean isExit(Event event);
    
    /**
     * Returns true if the event corresponds to any event that will interrupt
     * a mouse hover.
     * 
     * @param event The input event
     * @return True if the event is a hover stop event
     */
    public boolean isHoverInterrupt(Event event);
    
    /**
     * Returns the time (in milliseconds) between the current time and the last
     * event which interrupts the hover before a hover action starts.
     * 
     * @return A time before the hover action starts 
     */
    public long getHoverDelay();
    
    /**
     * Returns true if the event corresponds to a context event.
     * 
     * @param event The input event
     * @return True if the event is a context event
     */
    public boolean isContext(Event event);
}
