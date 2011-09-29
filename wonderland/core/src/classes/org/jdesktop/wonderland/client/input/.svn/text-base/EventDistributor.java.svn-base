/**
 * Open Wonderland
 *
 * Copyright (c) 2010 - 2011, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.client.input;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.PickInfo;
import java.util.Iterator;
import java.util.logging.Level;
import org.jdesktop.wonderland.common.InternalAPI;
import java.util.logging.Logger;
import org.jdesktop.mtgame.EntityComponent;
import org.jdesktop.wonderland.common.ThreadManager;
import org.jdesktop.wonderland.client.input.InputManager.FocusChange;

/**
 * The abstract base class for an Event Distributor singleton. The Entity Distributor is part of the input 
 * subsystem which distributes events throughout the scene graph according to the information provided by 
 * the entity event listeners. It also supports a set of global event listeners. This are independent of 
 * any events. Events are always tried to be delivered to the global event listeners. In general, the user
 * cannot make any assumptions about the order in which individual event listeners methods are called.
 *
 * @author deronj
 */

@InternalAPI
public abstract class EventDistributor implements Runnable {

    private static final Logger logger = Logger.getLogger(EventDistributor.class.getName());

    /** The focus sets for the various event classes. */
    protected HashMap<Class,HashSet<Entity>> focusSets = new HashMap<Class,HashSet<Entity>>();

    /** The base input queue entry. */
    private static class Entry {
	/** The Wonderland event. */
	Event event;
	/** The destination pick info for the event */
	PickInfo destPickInfo;
	/** The actual hit pick info for the event (drag events only)*/
	PickInfo hitPickInfo;
        Entry (Event event, PickInfo destPickInfo) {
	    this(event, destPickInfo, null);
	}
        Entry (Event event, PickInfo destPickInfo, PickInfo hitPickInfo) {
	    this.event = event;
	    this.destPickInfo = destPickInfo;
	    this.hitPickInfo = hitPickInfo;
        }
    }

    /**
     * Used for events which are posted directly to a known entity. InputManager.postEvent(event,entity)
     * uses this as well as the internal SwingEnterExit3D events.
     * Note: this type of event has no pickInfo but has an entity./
     */
    private static class EntryPostEventToEntity extends Entry {
	/** The entity. */
	Entity entity;
        EntryPostEventToEntity (Event event, Entity entity) {
	    super(event, null);
	    this.entity = entity;
	}
    }

    private Thread thread;

    private LinkedBlockingQueue<Entry> inputQueue = new LinkedBlockingQueue<Entry>();

    private final EventListenerCollection globalEventListeners = new EventListenerCollection();

    protected void start () {
	thread = new Thread(ThreadManager.getThreadGroup(), this, "EventDistributor");
	thread.start();
    }

    /**
     * Add a Wonderland event to the event distribution queue, with null pick info.
     * @param event The event to enqueue.
     */
    void enqueueEvent (Event event) {
        inputQueue.add(new Entry(event, null));
    }

    /**
     * Add a Wonderland event to the event distribution queue.
     * @param event The event to enqueue.
     * @param pickInfo The pick info for the event.
     */
    void enqueueEvent (Event event, PickInfo pickInfo) {
        inputQueue.add(new Entry(event, pickInfo));
    }

    /**
     * Add a Wonderland drag event to the event distribution queue, with both the 
     * destination pick info and the actual hit pick info.
     * @param event The event to enqueue.
     * @param destPickInfo The destination pick info for the event.
     * @param hitPickInfo The hit pick info for the event.
     */
    void enqueueDragEvent (Event event, PickInfo destPickInfo, PickInfo hitPickInfo) {
	inputQueue.add(new Entry(event, destPickInfo, hitPickInfo));
    }

    void enqueueEvent(Event event, Entity entity) {
        inputQueue.add(new EntryPostEventToEntity(event, entity));
    }

    /**
     * The run loop of the thread.
     */
    public void run () {
	while (true) {
	    try {
		Entry entry = null;
                entry = inputQueue.take();
		if (entry instanceof EntryPostEventToEntity) {
		    EntryPostEventToEntity epost = (EntryPostEventToEntity) entry;
		    processPostEventToEntity(epost.event, epost.entity);
		} else {
                    processEvent(entry.event, entry.destPickInfo, entry.hitPickInfo);
		}
            } catch (Exception ex) {
		logger.log(Level.WARNING, "Exception caught in " +
                           "EventDistributor thread. Event is ignored.", ex);
	    }
	}
    }
    
    /** 
     * The responsibility for determining how to process individual event types is delegated to the subclass.
     * @param event The event to try to deliver to event listeners.
     * @param destPickInfo The destination pick info associated with the event.
     * @param hitPickInfo The hit pick info associated with the event.
     */
    protected abstract void processEvent (Event event, PickInfo destPickInfo, PickInfo hitPickInfo);

    /** 
     * The responsibility for determining how to process an event posted to a specific entity
     * is delegated to the subclass.
     * @param event The event to try to deliver to event listeners.
     * @param entity The entity associated with the event.
     */
    protected abstract void processPostEventToEntity (Event event, Entity entity);

    /**
     * Traverse the list of global listeners to see whether the event should be
     * delivered to any of them. Post the event to all willing consumers.
     */
    protected void tryGlobalListeners (Event event) {
        if (logger.isLoggable(Level.INFO)) {
            logger.info("tryGlobalListeners event = " + event);
        }
        synchronized (globalEventListeners) {
            Iterator<EventListener> it = globalEventListeners.iterator();
            while (it.hasNext()) {
		EventListener listener = it.next();
		if (listener.isEnabled()) {
                    if (logger.isLoggable(Level.INFO)) {
                        logger.info("Calling consume for listener " + listener);
                    }
		    Event distribEvent = createEventForGlobalListener(event);
		    if (listener.consumesEvent(distribEvent)) {
                        if (logger.isLoggable(Level.INFO)) {
                            logger.info("CONSUMED event: " + event);
                            logger.info("Consuming listener " + listener);
                        }
			listener.postEvent(distribEvent);
		    }
		}
            }
        }
    }
	
    /** 
     * See if any of the enabled event listeners for the given entity are 
     * willing to consume the given event. Post the event to the first willing
     * consumer. Returns true if the event was consumed, and false if not.
     *
     * @param param entity the entity to try listeners for
     * @param event the event to try
     * @returns true if the event should be propagated to parents,
     * or false if not
     */
    protected boolean tryListenersForEntity (Entity entity, Event event) {
        if (logger.isLoggable(Level.INFO)) {
            logger.info("tryListenersForEntity, entity = " + entity +
                        ", event = " + event);
        }

        // get the listeners for this entity
        EventListenerCollection listeners = (EventListenerCollection)
                entity.getComponent(EventListenerCollection.class);
        
        // if there are none, return that the event should go to parents
        if (listeners == null || listeners.size() <= 0) {
            logger.info("Entity has no listeners");
            return true;
        }
        
        boolean toParents = true;

        // try each listener in turn
        for (Iterator<EventListener> i = listeners.iterator(); i.hasNext();) {
            EventListener listener = i.next();
            if (listener.isEnabled()) {
                if (logger.isLoggable(Level.INFO)) {
                    logger.info("Calling consume for listener " + listener);
                }

                // create a copy of the event for this listener
                Event distribEvent = createEventForEntity(event, entity);

                // check if the listener will consume this event
                if (listener.consumesEvent(distribEvent)) {
                    if (logger.isLoggable(Level.INFO)) {
                        logger.info("CONSUMED event: " + event);
                        logger.info("Consuming entity " + entity);
                        logger.info("Consuming listener " + listener);
                    }
		
                    // the listener will consume the event
                    listener.postEvent(distribEvent);

                    // check if we should block the event from going to
                    // parents
                    if (!listener.propagatesToParent(event)) {
                        toParents = false;
                    }

                    // even if the event does not go to parents, try other
                    // listeners for this entity. Otherwise, if multiple
                    // listeners register for the same event type, it will be
                    // random which listener actually gets the events.
                }
            }
        }
        
        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Finished event dispatch. Send to parents: " + toParents);
        }

        return toParents;
    }

    /** 
     * Traverse the entity parent chain (starting with the given entity) to see
     * if the event should be delivered to any of their listeners. Continue the
     * search until the event is consumed or no more parents are found.
     * Post the event to willing consumers.
     *
     * @param entity the entity to start with
     * @param event the event to post
     * @return true if the event should go to global listeners, or false if not
     */
    protected boolean tryListenersForEntityAndParents (Entity entity,
                                                       Event event)
    {
        boolean toParents = true;

        while (entity != null && toParents) {
            toParents = tryListenersForEntity(entity, event);

            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Propagate to next parent");
            }

            entity = entity.getParent();
        }

        return toParents;
    }
	
    /**
     * Create an event for distribution to a global event listener, based on the given base event.
     */
    static Event createEventForGlobalListener (Event baseEvent) {
        Event event = baseEvent.clone(null);
        event.setFocussed(entityHasFocus(baseEvent, InputManager.inputManager().getGlobalFocusEntity()));
        return event;
    }


    /**
     * Create an event for distribution to the given entity, based on the given base event.
     */
    static Event createEventForEntity (Event baseEvent, Entity entity) {
        Event event = baseEvent.clone(null);
        event.setEntity(entity);
        event.setFocussed(entityHasFocus(baseEvent, entity));
            return event;
    }

    /**
     * Add an event listener to be tried once per event. This global listener can be added only once.
     * Subsequent attempts to add it will be ignored.
     * <br><br>
     * Note: It is not a good idea to call this from inside EventListener.computeEvent function.
     * However, it is okay to call this from inside EventListener.commitEvent function if necessary.
     *
     * @param listener The global event listener to be added.
     */
    public void addGlobalEventListener (EventListener listener) {
        synchronized (globalEventListeners) {
            if (globalEventListeners.contains(listener)) {
            return;
            } else {
            globalEventListeners.add(listener);
            listener.addToEntity(InputManager.inputManager().getGlobalFocusEntity());
            }
        }
    }

    /**
     * Remove this global event listener.
     * <br><br>
     * Note: It is not a good idea to call this from inside EventListener.computeEvent function.
     * However, it is okay to call this from inside EventListener.commitEvent function if necessary.
     *
     * @param listener The global event listener to be removed.
     */
    public void removeGlobalEventListener (EventListener listener) {
        synchronized (globalEventListeners) {
            globalEventListeners.remove(listener);
            listener.removeFromEntity(InputManager.inputManager().getGlobalFocusEntity());
        }
    }

    /**
     * Update the focus sets based on a change event which comes through the event queue.
     * Called at FocusChangeEvent time.
     * @param changes An array of the changes to apply to the focus sets.
     */
    protected void processFocusChangeEvent (FocusChange[] changes) {
        for (FocusChange change : changes) {
            Class[] classes = change.eventClasses;
            Entity[] entities = change.entities;

            HashSet<Entity> focusSet;

            switch (change.action) {

                // Add the entities to each event class focus set
            case ADD:
            for (Class clazz : classes) {
                focusSet = focusSets.get(clazz);
                if (focusSet == null) {
                // First time for this class
                focusSet = new HashSet<Entity>();
                focusSets.put(clazz, focusSet);
                }
                for (Entity entity : entities) {
                focusSet.add(entity);
                setEntityFocus(clazz, entity, true);
                }
            }
            break;

                // Remove the entities from each event class focus set
            case REMOVE:
            for (Class clazz : classes) {
                focusSet = focusSets.get(clazz);
                if (focusSet == null) continue;
                for (Entity entity : entities) {
                focusSet.remove(entity);
                setEntityFocus(clazz, entity, false);
                }
                if (focusSet.size() <= 0) {
                focusSets.remove(clazz);
                }
            }
            break;

                // Replace the existing entities from each event class focus set with the new entities
            case REPLACE:
            for (Class clazz : classes) {

                // First, unfocus previous entities
                focusSet = focusSets.get(clazz);
                for (Entity entity : focusSet) {
                setEntityFocus(clazz, entity, false);
                }

                // Now focus the new entiti
                if (entities == null || entities.length <= 0) {
                focusSets.remove(clazz);
                } else {
                focusSet = new HashSet<Entity>();
                for (Entity entity : entities) {
                    focusSet.add(entity);
                    setEntityFocus(clazz, entity, true);
                }
                focusSets.put(clazz, focusSet);
                }
            }
            break;
            }
        }

	// Debug
	//logger.warning("Updated focus sets");
	//logger.warning("------------------");
	//logFocusSets();
	//logger.warning("------------------");
    }

    /** For debug */
    private void logFocusSets () {
        for (Class clazz : focusSets.keySet()) {
            HashSet<Entity> focusSet = focusSets.get(clazz);
            StringBuffer sb = new StringBuffer();
            sb.append("Class = " + clazz + ": ");
            Iterator<Entity> it = focusSet.iterator();
            while (it.hasNext()) {
            Entity entity = it.next();
            sb.append(entity.toString() + ", ");
            }
            logger.warning(sb.toString());
        }
    }

    /** A marker component used to mark entities which have focus. */
    private static class EventFocusComponent extends EntityComponent {

	/** The set of event classes for which this entity has focus */
	private HashSet<Class> focussedClasses;

	/** Add the given event class to the focus set. */
	private void add (Class clazz) {
	    if (focussedClasses == null) {
            focussedClasses = new HashSet<Class>();
	    }
	    focussedClasses.add(clazz);
	}

	/** Remove the given event class from the focus set. */
	private void remove (Class clazz) {
	    if (focussedClasses == null) return;
            focussedClasses.remove(clazz);
	    if (focussedClasses.size() <= 0) {
            focussedClasses = null;
	    }
	}

	/** Does the focus set contain the given event class or one of its super classes? */
	private boolean containsClassOrSuperclass (Class clazz) {
	    if (focussedClasses == null) return false;
	    Iterator<Class> it = focussedClasses.iterator();
	    while (it.hasNext()) {
            Class cl = it.next();
            if (cl.isAssignableFrom(clazz)) {
                return true;
            }
	    }
	    return false;
	}

	/** Returns the number of event classes in the focus set. */
	private int size () {
	    if (focussedClasses == null) return 0;
	    return focussedClasses.size();
	}
    }

    /**
     * Specify whether the given entity is focussed. Called at FocusChangeEvent time. Marks
     * the entities has been focussed or not.
     */
    private static void setEntityFocus (Class clazz, Entity entity, boolean hasFocus) {
        EventFocusComponent focusComp = (EventFocusComponent) entity.getComponent(EventFocusComponent.class);
        if (hasFocus) {
            if (focusComp == null) {
            focusComp = new EventFocusComponent();
            entity.addComponent(EventFocusComponent.class, focusComp);
            }
            focusComp.add(clazz);
        } else {
            if (focusComp == null) return;
            focusComp.remove(clazz);
            if (focusComp.size() <= 0) {
            entity.removeComponent(EventFocusComponent.class);
            }
        }
    }

    /**
     * Returns true if the given entity is marked as having focus.
     * Called at Event Distribution time. Therefore this is based on the information on the entity
     * itself, not the focus sets, which are in a different time domain.
     * @param event The event to be delivered.
     * @param entity The entity to check if it is in the focus set.
     */
    static boolean entityHasFocus (Event event, Entity entity) {
        EventFocusComponent focusComp = (EventFocusComponent) entity.getComponent(EventFocusComponent.class);
        if (focusComp == null) return false;
        return focusComp.containsClassOrSuperclass(event.getClass());
    }
}
