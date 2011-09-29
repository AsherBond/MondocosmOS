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

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.ProcessorArmingCollection;
import org.jdesktop.mtgame.PostEventCondition;
import org.jdesktop.mtgame.ProcessorCollectionComponent;
import org.jdesktop.mtgame.ProcessorComponent;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.InternalAPI;

// For debug
import org.jdesktop.mtgame.ProcessorArmingCondition;

/**
 * The base implementation of a Wonderland event listener. Almost all custom event listeners should
 * extend this class.
 *
 * @author deronj
 */

@ExperimentalAPI
public class EventListenerBaseImpl extends ProcessorComponent implements EventListener  {

    private static final Logger logger = Logger.getLogger(EventListenerBaseImpl.class.getName());

    /** The input event queue for this collection. The listeners will be called for these. */
    private LinkedBlockingQueue<Event> inputQueue = new LinkedBlockingQueue<Event>();

    /** Whether the listener is enabled. */
    protected boolean enabled = true;
    
    /** The number of entities to which the listener is attached */
    private int numEntitiesAttached;

    /** The list of events which was encountered when computeEvent was last called. */
    private final LinkedList<Event> computedEvents = new LinkedList<Event>();

    /** The MTGame event used by the input system. */
    private static long mtgameEventID;

    /**
     * Called during input system initialization.
     */
    static void initializeEventListeners () {
        mtgameEventID = ClientContextJME.getWorldManager().allocateEvent();
    }

    /**
     * {@inheritDoc}
     * Note: A listener is enabled by default.
     */
    @Override
    public boolean isEnabled () {
	return enabled;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnabled (boolean enable) {
	if (this.enabled == enable) return;
        this.enabled = enable;

	if (enabled && numEntitiesAttached > 0) {
	    // Make sure listener is armed if it is enabled and attached.
	    arm();
	} else if (!enabled || numEntitiesAttached <= 0) {
	    // Make sure listener is disarmed if it is disabled or detached.
	    disarm();
	}

	// Discard pending input events if listener is being disabled.
	if (!enable) {
	    inputQueue.clear();
	}
    }

    /**
     * {@inheritDoc}
     * <br><br>
     * Note on subclassing: unless a subclass overrides this method, true is always returned.
     */
    public boolean consumesEvent (Event event) {
	return true;
    }

    /**
     * {@inheritDoc}
     * <br><br>
     * Note on subclassing: unless a subclass overrides this method, true is always returned.
     */
    public boolean propagatesToParent (Event event) {
	return true;
    }

    /**
     * {@inheritDoc}
     */
    public void computeEvent (Event event) {}

    /**
     * {@inheritDoc}
     */
    public void commitEvent (Event event) {}

    /**
     * {@inheritDoc}
     */
    public void addToEntity (Entity entity) {

	// Make sure that the entity has an event listener collection to use as an attach point.
	EventListenerCollection collection = (EventListenerCollection) 
	    entity.getComponent(EventListenerCollection.class);

	if (collection == null) {
	    collection = new EventListenerCollection();
	    entity.addComponent(EventListenerCollection.class, collection);
	} else {
	    // See if listener is already attached to this entity
	    if (collection.contains(this)) {
		return;
	    }
	}	
	collection.add(this);

	addProcessorCompToEntity(this, entity);

	numEntitiesAttached++;

	// Arm on first attach (if listener is enabled)
	if (numEntitiesAttached == 1 && enabled) {
	    arm();
	}
    }

    /**
     * {@inheritDoc}
     */
    public void removeFromEntity (Entity entity) {

	EventListenerCollection collection = (EventListenerCollection) 
	    entity.getComponent(EventListenerCollection.class);

	if (collection == null) {
	    return;
	}

	// Remove listener from collection, seeing if listener was attached to this entity
	if (!collection.remove(this)) {
	    // Listener was not in this collection
	}
	
	if (collection.size() <= 0) {
	    entity.removeComponent(EventListenerCollection.class);
	}

	removeProcessorCompFromEntity(this, entity);

	numEntitiesAttached--;

	// Disarm on last detach
	if (numEntitiesAttached <= 0) {
	    disarm();
	}
    }

    /**
     * {@inheritDoc}
     */
    public boolean isListeningForEntity (Entity entity) {
	EventListenerCollection collection = (EventListenerCollection) 
	    entity.getComponent(EventListenerCollection.class);

	if (collection == null) {
	    return false;
	}

	return collection.contains(this);
    }

    /**
     * INTERNAL ONLY.
     * <br>
     * Deliver the given event to this collection. This is only ever called by the EventDeliverer.
     */
    @InternalAPI
    public void postEvent (Event event) {
	if (!enabled) return;
	inputQueue.add(event);
	ClientContextJME.getWorldManager().postEvent(mtgameEventID);
    }

    /**
     * {@inheritDoc}
     */
    public void initialize() {}
    
    /**
     * INTERNAL ONLY.
     * <br>
     * Called when there is new event in the input queue.
     */
    @InternalAPI
    public void compute (ProcessorArmingCollection collection) {
	Event event = null;
	synchronized (computedEvents) {
	    computedEvents.clear();
	}
	try {
	    while (inputQueue.peek() != null) {

		// Get the next available event
		event = inputQueue.take();
		if (event == null) {
		    logger.warning("No event found during read of listener input queue.");
		    return;
		}

		// Compute the event
		computeEvent(event);
		synchronized (computedEvents) {
		    computedEvents.add(event);
		}
	    }
	} catch (Exception ex) {
	    ex.printStackTrace();
	    logger.warning("Exception during read of listener input queue.");
	}
    }

    /**
     * INTERNAL ONLY
     * <br>
     * Called in the render loop to allow this collection to commit the 
     * <code>compute()</code> calculations to alter the scene graph.
     */
    @InternalAPI
    public void commit (ProcessorArmingCollection collection) {
	// Commit all the events which were previously computd
	synchronized (computedEvents) {
	    for (Event event : computedEvents) {
		commitEvent(event);
	    }
	}
    }

    /** Arm the listener's processor. */
    void arm () {
	ProcessorArmingCondition condition = new PostEventCondition(this, new long[] { mtgameEventID});
	setArmingCondition(condition);
    }

    /** Disarm the listener's processor. */
    void disarm () {
	setArmingCondition(null);
    }

    /** Add the given processor component to the given entity. */
    private static void addProcessorCompToEntity (ProcessorComponent pc, Entity entity) {
	ProcessorCollectionComponent pcc = (ProcessorCollectionComponent) entity.getComponent(ProcessorCollectionComponent.class);
	if (pcc == null) {
	    pcc = new ProcessorCollectionComponent();
	    entity.addComponent(ProcessorCollectionComponent.class, pcc);
	}
	pcc.addProcessor(pc);
    }

    /** Remove the given processor component from the given entity. */
    private static void removeProcessorCompFromEntity (ProcessorComponent pc, Entity entity) {
	ProcessorCollectionComponent pcc = (ProcessorCollectionComponent) entity.getComponent(ProcessorCollectionComponent.class);
	if (pcc == null) return;
	pcc.removeProcessor(pc);
	ProcessorComponent[] pcAry = pcc.getProcessors();
	if (pcAry == null || pcAry.length <= 0) {
	    entity.removeComponent(ProcessorCollectionComponent.class);
	}
    }
}
