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

import java.util.Iterator;
import java.util.LinkedList;
import org.jdesktop.mtgame.EntityComponent;
import org.jdesktop.wonderland.common.InternalAPI;

/**
 * The attach point for event listeners on an entity.
 *
 * @author deronj
 */

@InternalAPI
class EventListenerCollection extends EntityComponent {

    private final LinkedList<EventListener> listeners = new LinkedList<EventListener>();

    /**
     * Returns true if the given event listener if it is in the collection. Otherwise return false.
     * @param listener The event listener.
     */
    boolean contains (EventListener listener) {
	synchronized (listeners) {
	    return listeners.contains(listener);
	}
    }

    /**
     * Add an event listener, if the listener isn't already added.
     * @param listener The listener to add.
     */
    void add (EventListener listener) {
	synchronized (listeners) {
	    if (listeners.contains(listener)) return;
	    listeners.add(listener);
	}
    }

    /**
     * Remove an event listener.
     * @param listener The listener to remove.
     * @return Returns true if the listener was in the list.
     */
    boolean remove (EventListener listener) {
	synchronized (listeners) {
	    return listeners.remove(listener);
	}
    }

    /**
     * Return the number of listeners in the collection.
     */
    int size () {
	return listeners.size();
    }

    /**
     * Return an iterator over the collection.
     */
    Iterator<EventListener> iterator () {
	return listeners.iterator();
    }

    /**
     * FOR DEBUG
     */
    public void print () {
	System.err.println("Event listener collection size = " + listeners.size());
	for (EventListener listener : listeners) {
	    System.err.println("listener = " + listener);
	}
    }
}
