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
package org.jdesktop.wonderland.client.jme.input;

import java.awt.event.MouseEvent;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.input.EventDistributor;
import org.jdesktop.mtgame.PickInfo;
import org.jdesktop.mtgame.PickDetails;
import java.util.logging.Logger;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.client.input.FocusChangeEvent;
import org.jdesktop.wonderland.client.input.InputManager;
import org.jdesktop.wonderland.common.InternalAPI;

/**
 * The part of the input subsystem which distributes events throughout the scene graph 
 * according to the information provided by the entity event listeners.
 *
 * @author deronj
 */
@InternalAPI
public class EventDistributor3D extends EventDistributor implements Runnable {

    private static final Logger logger = Logger.getLogger(EventDistributor3D.class.getName());

    /** The pick info for the last mouse event */
    private PickInfo mousePickInfoPrev;
    /** The singleton event distributor */
    private static EventDistributor eventDistributor;

    /** Return the event distributor singleton */
    static EventDistributor getEventDistributor() {
        if (eventDistributor == null) {
            eventDistributor = new EventDistributor3D();
            ((EventDistributor3D) eventDistributor).start();
        }
        return eventDistributor;
    }

    protected void processEvent(Event event, PickInfo destPickInfo, PickInfo hitPickInfo) {
        if (event instanceof MouseEvent3D) {
            processMouseKeyboardEvent(event, destPickInfo, hitPickInfo);
        } else if (event instanceof KeyEvent3D) {
            processMouseKeyboardEvent(event, mousePickInfoPrev, null);
        } else if (event instanceof FocusEvent3D) {
            processFocusEvent((FocusEvent3D)event);
        } else if (event instanceof FocusChangeEvent) {
            processFocusChangeEvent(((FocusChangeEvent) event).getChanges());
        } else if (event instanceof DropTargetEvent3D) {
            processDropEvent((DropTargetEvent3D) event, destPickInfo);
        } else if (event instanceof Event) {
            processGlobalEvent(event);
        } else {
            logger.warning("Invalid event type encountered, event = " + event);
        }
    }

    protected void processMouseKeyboardEvent(final Event event,
            final PickInfo destPickInfo, final PickInfo hitPickInfo)
    {
        logger.fine("Distributor: received event = " + event);
        logger.fine("Distributor: destPickInfo = " + destPickInfo);
       
        // Track the last mouse pick info for focus-follows-mouse keyboard focus policy
        if (event instanceof MouseEvent3D) {
            mousePickInfoPrev = destPickInfo;
            MouseEvent3D mouseEvent = (MouseEvent3D) event;
            if (mouseEvent.getAwtEvent() instanceof InputManager.NondeliverableMouseEvent) {
                return;
            }
        }

        if (event instanceof InputEvent3D) {
            ((InputEvent3D) event).setPickInfo(destPickInfo);
        }

        // propagate the event to the picked entity
        processEntityEvent(event, destPickInfo, new EventModifier<Event>() {
            public void modifyEvent(Event event, int index) {
                // if this is a mouse event, set the details
                if (event instanceof MouseEvent3D) {
                    // get the selected set of details
                    PickDetails details = null;
                    if (destPickInfo != null && destPickInfo.size() > index) {
                        details = destPickInfo.get(index);
                    }

                    ((MouseEvent3D) event).setPickDetails(details);

                    // for a drag event, use the hitPickInfo instead
                    if (((MouseEvent3D) event).getID() == MouseEvent.MOUSE_DRAGGED &&
                        hitPickInfo != null)
                    {
                        MouseDraggedEvent3D de3d = (MouseDraggedEvent3D) event;
                        if (index < hitPickInfo.size()) {
                            de3d.setHitPickDetails(hitPickInfo.get(index));
                        }
                    }
                }
            }
        });
    }

    protected void processDropEvent(final DropTargetEvent3D event,
                                    final PickInfo pickInfo)
    {
        logger.fine("Distributor: received event = " + event);
        logger.fine("Distributor: pickInfo = " + pickInfo);

        // set the pickinfo for the event
        event.setPickInfo(pickInfo);

        // propagate the event to the picked entity
        processEntityEvent(event, pickInfo, new EventModifier<DropTargetEvent3D>() {
            public void modifyEvent(DropTargetEvent3D event, int index) {
                // get the PickDetails for the event, if any
                PickDetails details = null;
                if (pickInfo != null && pickInfo.size() > index) {
                    details = pickInfo.get(index);
                }

                event.setPickDetails(details);
            }
        });
    }

    protected <T extends Event> void processEntityEvent(T event,
            PickInfo pickInfo, EventModifier<? super T> modifier)
    {
        // find the first pickdetails with an entity
        int index = 0;
        Entity entity = null;

        if (pickInfo != null) {
            for (index = 0; index < pickInfo.size(); index++) {
                PickDetails details = pickInfo.get(index);

                // see if we picked an entity
                entity = details.getEntity();
                if (entity != null) {
                    break;
                }
            }
        }

        boolean toGlobal = true;

        // if we found an entity, try it and (if necessary) its parents to see
        // if anyone consumes the event
        if (entity != null) {
            if (modifier != null) {
                modifier.modifyEvent(event, index);
            }

            toGlobal = tryListenersForEntityAndParents(entity, event);
        }

        // see if we should try the global listener
        if (toGlobal) {
            if (modifier != null) {
                modifier.modifyEvent(event, index);
            }

            tryGlobalListeners(event);
        }
    }

    protected void processFocusEvent(FocusEvent3D event) {
        logger.fine("Distributor: received focus event = " + event);

        // Focus enty has no associated entity, so only try global listeners
        tryGlobalListeners(event);
    }

    protected void processPostEventToEntity (Event event, Entity entity) {
        logger.fine("Distributor: received event = " + event + ", entity = " + entity);

        boolean toGlobal = true;

        // if we found an entity, try it and (if necessary) its parents to see
        // if anyone consumes the event
        if (entity != null) {
            toGlobal = tryListenersForEntityAndParents(entity, event);
        }

        // see if we should try the global listener
        if (toGlobal) {
            tryGlobalListeners(event);
        }
    }

    private void processGlobalEvent(Event event) {
        logger.fine("Distributor: received global event = " + event);
        tryGlobalListeners(event);
    }

    protected interface EventModifier<T extends Event> {
        public void modifyEvent(T event, int index);
    }
}

