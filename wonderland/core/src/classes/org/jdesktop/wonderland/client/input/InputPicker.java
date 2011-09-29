/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
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

import java.awt.Canvas;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.CollisionManager;
import org.jdesktop.mtgame.JMECollisionSystem;
import org.jdesktop.mtgame.PickInfo;
import org.jdesktop.mtgame.PickDetails;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.math.Matrix4f;
import com.jme.renderer.Camera;
import com.jme.renderer.AbstractCamera;
import java.awt.Button;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.util.EventObject;
import java.util.Iterator;
import java.util.LinkedList;
import org.jdesktop.mtgame.CameraComponent;
import org.jdesktop.mtgame.CollisionComponent;
import org.jdesktop.mtgame.EntityComponent;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.input.KeyEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEvent3D;
import org.jdesktop.wonderland.common.InternalAPI;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import org.jdesktop.wonderland.client.jme.ViewManager;
import org.jdesktop.wonderland.client.jme.input.MouseDraggedEvent3D;
import org.jdesktop.wonderland.client.jme.input.MouseEnterExitEvent3D;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.client.input.InputManager.WindowSwingEventConsumer;
import org.jdesktop.wonderland.client.input.InputManager.WindowSwingEventConsumer.EventAction;
import org.jdesktop.wonderland.client.jme.input.DropTargetDragEnterEvent3D;
import org.jdesktop.wonderland.client.jme.input.DropTargetDragExitEvent3D;

/**
 * The abstract base class for an InputPicker singleton. The InputPicker is the part of the 
 * input subsystem which determines the pick details and the entity which an input event "hits." There are two 
 * mouse event processing methods. One method is used from WindowSwing code when a WindowSwing exists.
 * This method performs a pick, determines whether it hits a WindowSwing and if so returns the pickInfo to the
 * WindowSwing code. If a WindowSwing is not hit the pickInfo is stored for later use.
 *
 * The other mouse event processing method is used for events which don't hit a WindowSwing. These events
 * come from the Canvas (via the InputManager). The processing for these events, called "3D events," attempts
 * to avoid performing a pick by using pickInfo provided by WindowSwing, if available. (See previous paragraph).
 *
 * Note: the pick operation happens on the AWT event dispatch thread in all cases.
 *
 * @author deronj
 */

// TODO: if possible separate out the JME-dependent code into jme.input.InputPicker3D

@InternalAPI
public abstract class InputPicker {

    protected static final Logger logger = Logger.getLogger(InputPicker.class.getName());

    // Bit masks used during the grab calculation
    private static final int PG_SHIFT_DOWN_MASK     = 1<<0;
    private static final int PG_CTRL_DOWN_MASK      = 1<<1;
    private static final int PG_META_DOWN_MASK      = 1<<2;
    private static final int PG_ALT_DOWN_MASK       = 1<<3;
    private static final int PG_ALT_GRAPH_DOWN_MASK = 1<<4;
    private static final int PG_ALL_BUTTONS_MASK =
        PG_SHIFT_DOWN_MASK |
        PG_CTRL_DOWN_MASK  |
        PG_META_DOWN_MASK  |
        PG_ALT_DOWN_MASK   |
        PG_ALT_GRAPH_DOWN_MASK;

    /** The type of grab transition */
    private enum GrabChangeType { GRAB_ACTIVATE, GRAB_DEACTIVATE, GRAB_NO_CHANGE };

    /** Whether a grab is currently active */
    private boolean grabIsActive = false;

    /** The JME collision system (used for picking) */
    // TODO: how do we also handle the other types of collision systems? 
    JMECollisionSystem collisionSys;

    /** The destination pick info that the picker computes */
    private PickInfo destPickInfo;

    /** The destination pick info for the previous picked event */
    private PickInfo destPickInfoPrev;

    /** The most-recently picked drop target */
    private PickInfo dropPickInfoPrev;

    // 3 pixels
    private static final int BUTTON_CLICK_POSITION_THRESHOLD = 3;

    // Coordinate of last button press event
    private int buttonLastX, buttonLastY;

    // The pick info of the last time a button was pressed.
    private PickInfo lastButtonPressedPickInfo;
    
    // The pick info of the mouse button press event which started a grab
    private PickInfo grabPickInfo;
    
    /** The camera component to use for picking. */
    private CameraComponent cameraComp;

    /** The canvas to use for picking. */
    private Canvas canvas;

    /** The event distributor associated with this picker */
    protected EventDistributor eventDistributor;

    /** A temporary used during picking */
    private Vector2f eventPointScreen = new Vector2f();

    /** Another temporary used during picking */
    private Vector3f eventPointWorld = new Vector3f();

    /** Another temporary used during picking */
    private Vector3f directionWorld = new Vector3f();

    /** Another temporary used during picking */
    private Ray pickRay = new Ray();

    /**
     * The format of information we pass from the Swing picker to the 3D picker in the case
     * that the event missed a swing window.
     *
     * NOTE: it is tempting to also pass the destination pick info (accounting for grabs) in 
     * this queue. But it significantly complicates the reader side of the queue. We can easily
     * recalculate the destination pick info on the reader side.
     */
    private static class PickInfoQueueEntry {
	private PickInfo hitPickInfo;
	private MouseEvent mouseEvent;
	private PickInfoQueueEntry (PickInfo hitPickInfo, MouseEvent mouseEvent) {
	    this.hitPickInfo = hitPickInfo;
	    // TODO: For verification only. Evetually remove.
	    this.mouseEvent = mouseEvent;
	}
    }

    /** 
     * When a WindowSwing exists events go to WindowSwingEmbeddedPeer.createCoordinateHandler before
     * for picking before coming to the InputPicker. createCoordinateHandler stores the pick infos
     * for non-WindowSwing events in this queue so we don't need to repick in the picker.
     */
    private LinkedBlockingQueue<PickInfoQueueEntry> swingPickInfos = 
	new LinkedBlockingQueue<PickInfoQueueEntry>();


    /**
     * As we examine the entities along the pick ray we need to keep
     * track of the entities which are visible (that is not obscured
     * by an entity whose listeners doesn't propagate to under). We also
     * need to keep track the pick details which will be ultimately sent
     * in the enter/exit events.
     */
    private static class EntityAndPickDetails {
	private Entity entity;
	private PickDetails pickDetails;
	private EntityAndPickDetails (Entity entity, PickDetails pickDetails) {
	    this.entity = entity;
	    this.pickDetails = pickDetails;
	}
    }

    /** The entities that the pointer is inside for the current event. */
    LinkedList<EntityAndPickDetails> insideEntities = new LinkedList<EntityAndPickDetails>();

    /** The entities that the pointer was inside for the last event. */
    LinkedList<EntityAndPickDetails> insideEntitiesPrev = new LinkedList<EntityAndPickDetails>();

    /** The entities that no longer have the pointer inside. */
    LinkedList<EntityAndPickDetails> noLongerInsideEntities = new LinkedList<EntityAndPickDetails>();

    /** The entities that newly have the pointer inside. */
    LinkedList<EntityAndPickDetails> newlyInsideEntities = new LinkedList<EntityAndPickDetails>();

    /** A dummy AWT component used in enter/exit events. */
    private static Button dummyButton = new Button();

    /** Whether the previous pointer position was inside the given window swing entity. */
    private Entity insideSwingEntityPrev;

    /** The camera movement listener (used by drag events). */
    private MyCameraListener cameraListener = new MyCameraListener();

    /** An object used to lock the following camera-related variables */
    private final Integer cameraLock = new Integer(0);

    /** The current camera position (in world coordinates). */
    private Vector3f cameraPositionWorld = new Vector3f();

    /** The current model view matrix of the camera. */
    private Matrix4f cameraModelViewMatrix;

    /** The current inverse of the model view matrix of the camera. */
    private Matrix4f cameraModelViewMatrixInverse;

    /**
     * Create a new instance of InputPicker.
     */
    protected InputPicker () {
	CollisionManager cm = ClientContextJME.getWorldManager().getCollisionManager();
	collisionSys = (JMECollisionSystem) cm.loadCollisionSystem(JMECollisionSystem.class);
    }

    /**
     * Specify the associated event distributor.
     * @param eventDistributor
     */
    public void setEventDistributor (EventDistributor eventDistributor) {
	this.eventDistributor = eventDistributor;
    }

    /**
     * Picker for mouse events for the Embedded Swing case.
     * To be called by Embedded Swing toolkit createCoordinateHandler.
     *
     * Returns non-null if window is a WindowSwing. If it is a WindowSwing then
     * return the appropriate hit entity and the corresponding pick info.
     * Otherwise save the pickinfos the event to the event distributor as a 3D event.
     *
     * @param awtEvent The event whose entity and pickInfo need to be picked.
     * @return An object of class PickEventReturn, which contains the return
     * values entity and pickDetails.
     */
    public InputManager.PickEventReturn pickMouseEventSwing (MouseEvent awtMouseEvent) {

	logger.info("Picker Swing: received awt event = " + awtMouseEvent);

	// Determine the destination pick info by performing a pick, considering grabs. etc.
	PickInfo hitPickInfo;
	DetermineDestPickInfoReturn ret = determineDestPickInfo(awtMouseEvent);
	if (ret == null) {
	    destPickInfo = null;
	    hitPickInfo = null;
	} else {
	    destPickInfo = ret.destPickInfo;
	    hitPickInfo = ret.hitPickInfo;
	}
	logger.fine("destPickInfo = " + destPickInfo);
 	logger.fine("hitPickInfo = " + hitPickInfo);

	// Generate 3D enter/exit events associated with this mouse event
	int eventID = awtMouseEvent.getID();
	if (eventID == MouseEvent.MOUSE_MOVED ||
	    eventID == MouseEvent.MOUSE_DRAGGED ||
	    eventID == MouseEvent.MOUSE_ENTERED ||
	    eventID == MouseEvent.MOUSE_EXITED) {
	    generateEnterExitEvents(awtMouseEvent, destPickInfo);
	}

	// Check for pick miss
	if (destPickInfo == null || destPickInfo.size() <= 0) {
	    // Pick miss. Send it to the event distributor without pick info.
	    logger.finest("Picker: pick miss");
	    logger.finest("Enqueue null pick info for 3D event");
	    logger.finest("awtMouseEvent = " + awtMouseEvent);
	    swingPickInfos.add(new PickInfoQueueEntry(null, awtMouseEvent));
	    return null;
	}

	// Create the Wonderland event which corresponds to this AWT event
	// (and, for drag events, attach the raw hit pick info).
	MouseEvent3D event = (MouseEvent3D) createWonderlandEvent(awtMouseEvent);

	// Get the destination entity and move pick details into the event
	PickDetails pickDetails = destPickInfo.get(0);
        Entity entity = pickDetails.getEntity();
	logger.fine("Picker: pickDetails = " + pickDetails);
        logger.fine("Picker: entity = " + entity);
        event.setPickDetails(pickDetails);
        if (eventID == MouseEvent.MOUSE_DRAGGED && hitPickInfo != null) {
            MouseDraggedEvent3D de3d = (MouseDraggedEvent3D) event;
            if (hitPickInfo.size() > 0) {
                de3d.setHitPickDetails(hitPickInfo.get(0));
            }
        }

        if (isWindowSwingEntity(entity)) {
            logger.info("Hit window swing entity = " + entity);

            // Get the WindowSwing of the entity
            WindowSwingEventConsumer eventConsumer = 
                (WindowSwingEventConsumer) entity.getComponent(WindowSwingEventConsumer.class);
            
            // Treat change control events as 3D events, regardless of control or focus
            EventAction eventAction =  eventConsumer.consumesEvent(event);
            logger.info("Event action = " + eventAction);

            switch (eventAction) {

            case CONSUME_3D:

                // We haven't hit an input sensitive WindowSwing so post the event to the 
                // event distributor as a 3D event
                swingPickInfos.add(new PickInfoQueueEntry(hitPickInfo, awtMouseEvent));
                return null;

            case CONSUME_2D:

                // HACK: see doc for this method
                cleanupGrab(awtMouseEvent);

                // Return the event to Swing
                if (eventID == MouseEvent.MOUSE_DRAGGED && hitPickInfo != null &&
                    hitPickInfo.size() > 0) {
                    return new InputManager.PickEventReturn(entity, pickDetails, hitPickInfo.get(0));
                } else {
                    return new InputManager.PickEventReturn(entity, pickDetails, null);
                }

            case DISCARD:
            default:
                return null;
            }
        } else {

            // This is a 3D event
            if (awtMouseEvent.getID() == MouseEvent.MOUSE_RELEASED) {
                // TODO: someday: HACK: workaround for 600
                swingPickInfos.add(new PickInfoQueueEntry(destPickInfo, awtMouseEvent));
            } else {
                swingPickInfos.add(new PickInfoQueueEntry(hitPickInfo, awtMouseEvent));
            }

            return null;
        }
    }

    /**
     * Returns true if the given entity belongs to a WindowSwing.
     */
    private static boolean isWindowSwingEntity (Entity entity) {
	// We know whether it is a WindowSwing entity by looking for the attached WindowSwingEntityComponent
	EntityComponent comp = entity.getComponent(InputManager.WindowSwingViewMarker.class);
	return comp != null;
    }

    /**
     * Mouse Event picker for non-Swing (3D) events.
     * Finds the first consuming entity and then turns the work over to the event deliverer.
     * This method does not return a result but instead enqueues an entry for the event in
     * the input queue of the event deliverer.
     */
    void pickMouseEvent3D (MouseEvent awtEvent) {
	logger.fine("pickMouseEvent3D: Received awt event = " + awtEvent);
	MouseEvent3D event;

	// Determine the destination pick info by reading from the pickInfo Queue, performing a pick, 
	// considering grabs. etc.
	DetermineDestPickInfoReturn ret = determineDestPickInfo(awtEvent);
	if (ret == null) {
	    destPickInfo = null;
	} else {
	    destPickInfo = ret.destPickInfo;
	}
	logger.fine("destPickInfo = " + destPickInfo);
	if (ret != null) {
	    logger.fine("hitPickInfo = " + ret.hitPickInfo);
	}

	// Generate enter/exit events associated with this mouse event
	int eventID = awtEvent.getID();
	if (eventID == MouseEvent.MOUSE_MOVED ||
	    eventID == MouseEvent.MOUSE_DRAGGED ||
	    eventID == MouseEvent.MOUSE_ENTERED ||
	    eventID == MouseEvent.MOUSE_EXITED) {
	    generateEnterExitEvents(awtEvent, destPickInfo);
	}

	// Check for pick miss
	if (destPickInfo == null || destPickInfo.size() <= 0) {
	    // Pick miss. Send it along without pick info.
	    logger.finest("Picker: pick miss");
	    event = (MouseEvent3D) createWonderlandEvent(awtEvent);
	    eventDistributor.enqueueEvent(event, (PickInfo)null);
	    return;
	}
	logger.fine("Picker: pick hit: destPickInfo = " + destPickInfo);

	// Create the Wonderland event which corresponds to this AWT event
	// (and, for drag events, attach the raw hit pick info).
	event = (MouseEvent3D) createWonderlandEvent(awtEvent);

	// Do the rest of the work in the EventDistributor
	if (eventID == MouseEvent.MOUSE_DRAGGED && ret != null) {
	    eventDistributor.enqueueDragEvent(event, destPickInfo, ret.hitPickInfo);
	} else {
	    eventDistributor.enqueueEvent(event, destPickInfo);
	}
    }

    /**
     * Drop Event picker for non-Swing (3D) events.
     * Finds the first consuming entity and then turns the work over to the event deliverer.
     * This method does not return a result but instead enqueues an entry for the event in
     * the input queue of the event deliverer.
     */
    void pickDropEvent (DropTargetEvent dropEvent) {
	logger.fine("pickDrop: Received awt event = " + dropEvent);

	// If the event has coordinates, determine what we are over by
        // performing a pick.  Otherwise send the event to the last
        // object we picked.
        Point location = null;
        if (dropEvent instanceof DropTargetDropEvent) {
            // we are going to use the result in Wonderland, so accept the
            // drop
            ((DropTargetDropEvent) dropEvent).acceptDrop(DnDConstants.ACTION_MOVE);
            location = ((DropTargetDropEvent) dropEvent).getLocation();
        } else if (dropEvent instanceof DropTargetDragEvent) {
            // we are going to use the result in Wonderland, so accept the
            // drag
            ((DropTargetDragEvent) dropEvent).acceptDrag(DnDConstants.ACTION_MOVE);
            location = ((DropTargetDragEvent) dropEvent).getLocation();
        }

        PickInfo pickInfo = null;
        if (location != null) {
            pickInfo = pickEventScreenPos(location.x, location.y);
        }

        // now that we have the current and previous pick infos, we can
        // generate any necessary enter and exit events
        if (dropPickInfoPrev != null && !pickInfosEqual(dropPickInfoPrev, pickInfo)) {
            // generate an exit event
            DropTargetDragExitEvent3D e =
                    new DropTargetDragExitEvent3D(dropEvent);
            eventDistributor.enqueueEvent(e, dropPickInfoPrev);
        }

        if (pickInfo != null && !pickInfosEqual(pickInfo, dropPickInfoPrev) &&
            dropEvent instanceof DropTargetDragEvent)
        {
            // generate an enter event
            DropTargetDragEnterEvent3D e =
                    new DropTargetDragEnterEvent3D((DropTargetDragEvent) dropEvent);
            eventDistributor.enqueueEvent(e, pickInfo);
        }

        // forward along the actual event
        Event event = createWonderlandEvent(dropEvent);
        if (event != null) {
            eventDistributor.enqueueEvent(event, pickInfo);
        }        

        // remember the last object that was notified
        dropPickInfoPrev = pickInfo;

        // if this was a drop, notify the system that it is complete
        if (dropEvent instanceof DropTargetDropEvent) {
            ((DropTargetDropEvent) dropEvent).dropComplete(true);

            // we don't get a drag exit event after a drop, so make sure
            // to reset the previous pickinfo so we don't generate an extra
            // exit
            dropPickInfoPrev = null;
        }
    }

    /**
     * Determine if two PickInfo objects are equivalent
     * @param p1 the first PickInfo
     * @param p2 the second PickInfo
     * @return true if the pickinfos represent the same set of objects, or
     * false if not
     */
    private boolean pickInfosEqual(PickInfo p1, PickInfo p2) {
        // check for null
        if (p1 == null) {
            return (p2 == null);
        } else if (p2 == null) {
            return (p1 == null);
        }

        // compare size of pick list
        if (p1.size() != p2.size()) {
            return false;
        }

        // next compare entities of PickDetail objects
        for (int i = 0; i < p1.size(); i++) {
            PickDetails pd1 = p1.get(i);
            PickDetails pd2 = p2.get(i);

            if (pd1.getEntity() == null) {
                if (pd2.getEntity() != null) {
                    return false;
                }
            } else if (!pd1.getEntity().equals(pd2.getEntity())) {
                return false;
            }
        }

        // if everything is the same, the objects are equal
        return true;
    }

    /**
     * Process key events. No picking is actually performed. Key events are delivered starting at the
     * entity that has the keyboard focus.
     */
    void pickKeyEvent (KeyEvent awtEvent) {
	logger.fine("Picker: received awt event = " + awtEvent);
	KeyEvent3D keyEvent = (KeyEvent3D) createWonderlandEvent(awtEvent);
	eventDistributor.enqueueEvent(keyEvent);
    }

    /** The type of return value returned by determineDestPickInfo. */
    private class DetermineDestPickInfoReturn {
	// The destination pick info, accounting for grabs.
	private PickInfo destPickInfo;
	// The actual, raw pick info from the pick hit, ignoring grabs.
	private PickInfo hitPickInfo;
	private DetermineDestPickInfoReturn (PickInfo destPickInfo, PickInfo hitPickInfo) {
	    this.destPickInfo = destPickInfo;
	    this.hitPickInfo = hitPickInfo;
	}
    }

    /**
     * Performs a pick on the scene graph and determine the actual destination pick info 
     * taking into account button click threshold and mouse button grabbing.
     * Returns the destination pick info in the global member destPickInfo.
     *
     * @param e The mouse event.
     * @return The destination pick info.
     */
    protected DetermineDestPickInfoReturn determineDestPickInfo (MouseEvent e) {
        boolean deactivateGrab = false;

	/* For debug: uncomment this to breakpoint on only drag events 
	if (e.getID() == MouseEvent.MOUSE_DRAGGED || e.getID() == MouseEvent.MOUSE_PRESSED) {
	    logger.severe("Event is drag or press");
	}
	*/

	// See if the WindowSwing has already determined a pickInfo for this event.
	// TODO: right now, button release events are never sent to createCoordinateHandler so they
	// never have pre-calculated pickInfos. I don't yet know if this is an Embedded Swing bug or
	// whether it is a feature.
	PickInfo swingHitPickInfo = null;
	if (swingPickInfos.peek() != null) {
	    try {
		PickInfoQueueEntry entry = swingPickInfos.take();

		// TODO: for now, verify that this is the right pickInfo for this event
		// Only check certain fields. Other fields (such as absolute X and Y) are 
		// expected to be different
		if (e.getID() == entry.mouseEvent.getID() &&
		    e.getX() == entry.mouseEvent.getX() &&
		    e.getY() == entry.mouseEvent.getY()) {
		    swingHitPickInfo = entry.hitPickInfo;
		} else {
		    logger.finest("Swing pickInfo event doesn't match 3D event. Repicking.");
		    logger.finest("3D event = " + e);
		    logger.finest("pickInfo event = " + entry.mouseEvent);
		}
	    } catch (InterruptedException ex) {}
	}

	// Implement the click threshold. Allow click event to be passed along only
	if (e.getID() == MouseEvent.MOUSE_PRESSED) {
	    buttonLastX = e.getX();
	    buttonLastY = e.getY();
	} else if (e.getID() == MouseEvent.MOUSE_CLICKED) {
	    if (!buttonWithinClickThreshold(e.getX(), e.getY())) {
		// Discard the event by returing a miss
		return null;
	    }
	}

	// Handle button clicked events specially. The mouse clicked event
	// comes after the grab has terminated. So we do this in order to 
	// force the clicked event to go to the same destination as the
	// pressed event
	if (e.getID() == MouseEvent.MOUSE_CLICKED) {
	    return new DetermineDestPickInfoReturn(lastButtonPressedPickInfo, lastButtonPressedPickInfo);
	}

	// First perform the pick (the pick details in the info are ordered
	// from least to greatest eye distance.
        PickInfo hitPickInfo;
	if (swingHitPickInfo == null) {
	    hitPickInfo = pickEventScreenPos(e.getX(), e.getY());
	    logger.finest("Result of pickEventScreenPos = " + hitPickInfo);
	    if (hitPickInfo != null) {
		logger.finest("hitPickInfo.size() = " + hitPickInfo.size());
	    }
	} else {
	    hitPickInfo = swingHitPickInfo;
	}

	/* For Debug
	int n = hitPickInfo.size();
	System.err.println("n = " + n);
	for (int i = 0; i < n; i++) {
	    PickDetails pd = hitPickInfo.get(i);
	    System.err.println("pd[" + i + "] = " + pd);
	    Entity pickEntity = pd.getEntity();
	    System.err.println("entity[" + i + "] = " + pickEntity);
	}
	*/

	// Calculate how the grab state should change. If the a grab should activate, activate it.
	GrabChangeType grabChange = GrabChangeType.GRAB_NO_CHANGE;
        int eventID = e.getID();
	if (eventID == MouseEvent.MOUSE_PRESSED ||
	    eventID == MouseEvent.MOUSE_RELEASED) {

	    grabChange = evaluateButtonGrabStateChange(eventID, e);
	    if (grabChange == GrabChangeType.GRAB_ACTIVATE) {
		grabIsActive = true;
		grabPickInfo = hitPickInfo;
		logger.finest("Grab activate, grabPickInfo = " + grabPickInfo);
		if (grabPickInfo != null) {
		    logger.finest("grabPickInfo.size() = " + grabPickInfo.size());
		    if (grabPickInfo.size() > 0) {
			PickDetails pd = grabPickInfo.get(0);
			logger.finest("Grab pickDetails[0] = " + pd);
			if (pd != null) {
			    logPickDetailsEntity(pd);
			    CollisionComponent cc = pd.getCollisionComponent();
			    logger.finest("cc = " + cc);
			    if (cc != null) {
				logger.finest("cc entity = " + cc.getEntity());
			    }
			}
		    }
		}
	    }
	}

	// If a grab is active, the event destination pick info will be the grabbed pick info
	PickInfo destPickInfo;
	logger.finest("grabIsActive = " + grabIsActive);
	if (grabIsActive) {
	    destPickInfo = grabPickInfo;
	    logger.finest("Grab is active, grabPickInfo = " + grabPickInfo);
	} else {
	    destPickInfo = hitPickInfo;
	}
	logger.finest("After grab calc, destPickInfo = " + destPickInfo);

	// It is now safe to disable the grab
	if (grabChange == GrabChangeType.GRAB_DEACTIVATE) {
	    grabIsActive = false;
	    grabPickInfo = null;
	}

	if (e.getID() == MouseEvent.MOUSE_PRESSED) {
	    lastButtonPressedPickInfo = destPickInfo;
	}

	logger.fine("Picked awt event = " + e);
	logPickInfo("destPickInfo = ", destPickInfo);
	logPickInfo("hitPickInfo = ", hitPickInfo);

	return new DetermineDestPickInfoReturn(destPickInfo, hitPickInfo);
    }

    public static void logPickInfo (String str, PickInfo pickInfo) {
	logger.fine(str + pickInfo);
	if (pickInfo == null) return;
	logger.fine("pickInfo size = " + pickInfo.size());
	for (int idx = 0; idx < pickInfo.size(); idx++) {
	    PickDetails pickDetails = pickInfo.get(idx);
	    if (pickDetails == null) continue;
	    logger.fine("pickDetails " + idx + ": ");
	    logPickDetails(pickDetails);
	}
    }

    private static void logPickDetails (PickDetails pickDetails) {
	logPickDetailsEntity(pickDetails);
    }

    private static void logPickDetailsEntity (PickDetails pickDetails) {
	Entity entity = pickDetails.getEntity();
	logger.fine("pickDetails Entity = " + entity);
    }

    /** 
     * Specify the canvas to be used for picking.
     *
     * @param canvas The AWT canvas to use for picking operations.
     */
    public void setCanvas (Canvas canvas) {
	this.canvas = canvas;
    }

    /** 
     * Returns the canvas that is used for picking.
     */
    public Canvas getCanvas () {
	return canvas;
    }

    /** 
     * Specify the camera component to be used for picking.
     *
     * @param cameraComp The mtgame camera component to use for picking operations.
     */
    void setCameraComponent (CameraComponent cameraComp) {

        // One time addition of camera movement listener
        if (this.cameraComp == null) {
	    ViewManager.getViewManager().addCameraListener(cameraListener);     
	}		

	this.cameraComp = cameraComp;
    }

    /** 
     * Returns the camera component that is used for picking.
     * <br>
     * INTERNAL ONLY.
     */
    @InternalAPI
    public CameraComponent getCameraComponent () {
	return cameraComp;
    }

    /**
     * The camera movement listener.
     */
    private class MyCameraListener implements ViewManager.CameraListener {
        public void cameraMoved(CellTransform cameraWorldTransform) {
	    synchronized (cameraLock) {
		cameraWorldTransform.getTranslation(cameraPositionWorld);
		if (cameraComp != null) {
		    Camera camera = cameraComp.getCamera();
		    cameraModelViewMatrix = ((AbstractCamera)camera).getModelViewMatrix();
		    cameraModelViewMatrixInverse = null;
		}
	    }
	}
    }

    /**
     * Returns the current camera position (in world coordinates).
     * <br>
     * INTERNAL ONLY.
     */
    @InternalAPI
    public Vector3f getCameraPosition (Vector3f ret) {
	synchronized (cameraLock) {
	    if (ret == null) {
		return new Vector3f(cameraPositionWorld);
	    }
	    ret.set(cameraPositionWorld);
	    return ret;
	}
    }

    /**
     * Returns the current model view matrix of the camera.
     * <br>
     * INTERNAL ONLY.
     */
    @InternalAPI
    public Matrix4f getCameraModelViewMatrix (Matrix4f ret) {
	synchronized (cameraLock) {
	    if (ret == null) {
		return new Matrix4f(cameraModelViewMatrix);
	    }
	    ret.set(cameraModelViewMatrix);
	    return ret;
	}
    }

    /**
     * Returns the current inverse model view matrix of the camera.
     * <br>
     * INTERNAL ONLY.
     */
    @InternalAPI
    public Matrix4f getCameraModelViewMatrixInverse (Matrix4f ret) {
	synchronized (cameraLock) {
	    if (cameraModelViewMatrixInverse == null) {
		cameraModelViewMatrixInverse = cameraModelViewMatrix.invert(new Matrix4f());
	    }
	    if (ret == null) {
		return new Matrix4f(cameraModelViewMatrixInverse);
	    }
	    ret.set(cameraModelViewMatrixInverse);
	    return ret;
	}
    }

    /** 
     * Calculates the ray to use for picking, based on the given screen coordinates.
     */
    Ray calcPickRayWorld (int x, int y) {
        Ray result = null;
	// Get the world space coordinates of the eye position
	Camera camera = cameraComp.getCamera();
	Vector3f eyePosWorld = camera.getLocation();

	// Convert the event from AWT coords to JME float screen space.
	// Need to invert y because (0f, 0f) is at the button left corner.
	eventPointScreen.setX((float)x);
	eventPointScreen.setY((float)(canvas.getHeight()-1-y));

	// Get the world space coordinates of the screen space point from the event
	// (The glass plate of the screen is considered to be at at z = 0 in world space
        try { // May fail if jME thinks the camera matrix is singular
            camera.getWorldCoordinates(eventPointScreen, 0f, eventPointWorld);
            // Compute the diff and create the ray
            eventPointWorld.subtract(eyePosWorld, directionWorld);
            result = new Ray(eyePosWorld, directionWorld.normalize());
        } catch (ArithmeticException ex) {
            logger.log(Level.SEVERE,"Problem getting world space coords for pick ray.", ex);
            result = new Ray();
        }
        return result;
    }

    /**
     * Actually perform the pick.
     */
    PickInfo pickEventScreenPos (int x, int y) {
	if (cameraComp == null) return null;

	logger.fine("pick at " + x + ", " + y);
	Ray pickRayWorld = calcPickRayWorld(x, y);

	// Note: pickAll is needed to in order to pick through transparent objects.
	return collisionSys.pickAllWorldRay(pickRayWorld, true, false/*TODO:interp*/,
                                            true, /* for now, always include ortho objects in picking */
                                            cameraComp);
    }

    private GrabChangeType evaluateButtonGrabStateChange (int eventID, MouseEvent e) {
	int modifiers = convertAwtEventModifiersToPassiveGrabModifiers(e.getModifiers());

	if (eventID == MouseEvent.MOUSE_PRESSED) {
	    // Button press
	    return GrabChangeType.GRAB_ACTIVATE;
	} else if (eventID == MouseEvent.MOUSE_RELEASED &&
	           (modifiers & PG_ALL_BUTTONS_MASK) == 0) {
	    // Button release: Similar to X11: terminate grab only when all buttons are released	
	    return GrabChangeType.GRAB_DEACTIVATE;
	}

	return GrabChangeType.GRAB_NO_CHANGE;
    }

    // Returns true if the button release is close enough to the button press
    // so as to consitute a click event.
    // Note: These are Java-on-Windows behavior. There is no click event position
    // threshold on Java-on-Linux. But Hideya wants the Windows behavior.

    private final boolean buttonWithinClickThreshold (int x, int y) {
	return Math.abs(x - buttonLastX) <= BUTTON_CLICK_POSITION_THRESHOLD &&
	       Math.abs(y - buttonLastY) <= BUTTON_CLICK_POSITION_THRESHOLD;
    }

    private int convertAwtEventModifiersToPassiveGrabModifiers (int modifiers) {
	int pgModifiers = 0;

        if ((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) {
	    pgModifiers |= PG_SHIFT_DOWN_MASK;
	}

        if ((modifiers & InputEvent.CTRL_DOWN_MASK) != 0) {
	    pgModifiers |= PG_CTRL_DOWN_MASK;
	}

	if ((modifiers & InputEvent.META_DOWN_MASK) != 0) {
	    pgModifiers |= PG_META_DOWN_MASK;
	}

	if ((modifiers & InputEvent.ALT_DOWN_MASK) != 0) {
	    pgModifiers |= PG_ALT_DOWN_MASK;
	}

	if ((modifiers & InputEvent.ALT_GRAPH_DOWN_MASK) != 0) {
	    pgModifiers |= PG_ALT_GRAPH_DOWN_MASK;
	}

	return pgModifiers;
    }

    /**
     * Converts a 2D AWT event into a Wonderland event.
     */
    protected abstract Event createWonderlandEvent (EventObject eventObj);

    /**
     * Generate the appropriate enter/exit events. 
     *
     * NOTE: the pointer can be inside an entity, but if this entity is obscured by
     * an entity with event listeners that don't propagate to under then the obscured
     * entity is effectively exitted. This is an example of an "event shadow."
     */
    private void generateEnterExitEvents (MouseEvent awtEvent, PickInfo pickInfo) {

        MouseEnterExitEvent3D enterEventProto = 
	    createEnterExitEventFromAwtEvent(awtEvent, MouseEvent.MOUSE_ENTERED);
	MouseEnterExitEvent3D exitEventProto = 
	    createEnterExitEventFromAwtEvent(awtEvent, MouseEvent.MOUSE_EXITED);

	// Calculate which entities have the pointer inside
	calcInsideEntities(awtEvent, enterEventProto, pickInfo);

	//System.err.println("awtEvent = " + awtEvent);
	//System.err.println("insideEntities = " + insideEntities);

	// Calculate entities which had the pointer inside on the last event
	// but which no longer have the pointer inside
	noLongerInsideEntities.clear();
	for (EntityAndPickDetails entry : insideEntitiesPrev) {
	    boolean found = false;
	    for (EntityAndPickDetails entryInside : insideEntities) {
		if (entry.entity.equals(entryInside.entity)) {
		    found = true;
		    break;
		}
	    }
	    if (!found) {
		noLongerInsideEntities.add(entry);
	    }
	}

	//System.err.println("noLongerInsideEntities = " + noLongerInsideEntities);

	// Calculate entities which did not have the pointer inside on the last
	// event but now have the pointer inside.
	newlyInsideEntities.clear();
	for (EntityAndPickDetails entry : insideEntities) {
	    boolean found = false;
	    for (EntityAndPickDetails entryInsidePrev : insideEntitiesPrev) {
		if (entry.entity.equals(entryInsidePrev.entity)) {
		    found = true;
		    break;
		}
	    }
	    if (!found) {
		newlyInsideEntities.add(entry);
	    }
	}

	//System.err.println("newlyInsideEntities = " + newlyInsideEntities);

	// Send the exit events to the no longer inside entities.
	sendExitEvents(exitEventProto, pickInfo);

	// Send the enter events to the newly inside entities.
	sendEnterEvents(enterEventProto, pickInfo);

	// Remember the inside entities for the event
	insideEntitiesPrev.clear();
	insideEntitiesPrev.addAll(insideEntities);
    }


    /**
     * Calculate the current set of entities the pointer is inside.
     */
    private void calcInsideEntities (MouseEvent awtEvent, MouseEnterExitEvent3D enterEventProto,
				     PickInfo pickInfo) {
	
	// Calculate the current set of entities the pointer is inside.
	insideEntities.clear();
	if (awtEvent.getID() == MouseEvent.MOUSE_EXITED || pickInfo == null ||
	    pickInfo.size() <= 0) {
	    // Note: Canvas exit event is treated just like a pick miss (pickInfo is null)
	    return;
	}

	// Gather up entities which intersect the pick ray until we encounter an
	// entity which doesn't propagate to under.
	boolean propagatesToUnder = true;
	PickDetails pickDetails = pickInfo.get(0);
	int idx = 0;
	while (pickDetails != null && idx < destPickInfo.size() && propagatesToUnder) {
	    Entity entity = pickDetails.getEntity();
	    /*
	    if (pickDetails != null) {
		CollisionComponent cc = pickDetails.getCollisionComponent();
		logger.finest("pd cc = " + cc);
		if (cc != null) {
		    logger.finest("cc entity = " + cc.getEntity());
		}
	    }
	    */

	    if (entity == null) {
		idx++;
		if (idx < destPickInfo.size()) {
		    pickDetails = destPickInfo.get(idx);
		} else {
		    pickDetails = null;
		}
		continue;
	    }

	    insideEntities.add(new EntityAndPickDetails(entity, pickDetails));
	    
	    propagatesToUnder = false;
	    EventListenerCollection listeners = (EventListenerCollection) 
		entity.getComponent(EventListenerCollection.class);
	    
	    if (listeners == null) { 
		propagatesToUnder = false;
	    } else {
		Iterator<EventListener> it = listeners.iterator();
		while (it.hasNext()) {
		    EventListener listener = it.next();
		    if (listener.isEnabled()) {
			MouseEnterExitEvent3D distribEvent = (MouseEnterExitEvent3D) 
			    EventDistributor.createEventForEntity(enterEventProto, entity);
			distribEvent.setPickDetails(pickDetails);
			distribEvent.setPickInfo(pickInfo);
			// TODO: someday: decommit for now
			//propagatesToUnder |= listener.propagatesToUnder(distribEvent);
		    }
		}
		if (propagatesToUnder) {
		    idx++;
		    if (idx < destPickInfo.size()) {
			pickDetails = destPickInfo.get(idx);
		    } else {
			pickDetails = null;
		    }
		}
	    }	    
	}
    }

    /**
     * Send the exit events to the no longer inside entities.
     */
    private void sendExitEvents (MouseEnterExitEvent3D exitEventProto, PickInfo pickInfo) {
	for (EntityAndPickDetails entry : noLongerInsideEntities) {
	    MouseEnterExitEvent3D exitEvent = (MouseEnterExitEvent3D) 
		EventDistributor.createEventForEntity(exitEventProto, entry.entity);
	    exitEvent.setPickDetails(entry.pickDetails);
	    exitEvent.setPickInfo(pickInfo);
	    //System.err.println("Try sending exitEvent = " + exitEvent);
	    eventDistributor.tryGlobalListeners(exitEvent);
	    //System.err.println("Try entity = " + entry.entity);
	    tryListenersForEntity(entry.entity, exitEvent);
	}
    }

    /**
     * Send the enter events to the newly inside entities.
     */
    private void sendEnterEvents (MouseEnterExitEvent3D enterEventProto, PickInfo pickInfo) {
	for (EntityAndPickDetails entry : newlyInsideEntities) {
	    MouseEnterExitEvent3D enterEvent = (MouseEnterExitEvent3D) 
		EventDistributor.createEventForEntity(enterEventProto, entry.entity);
	    enterEvent.setPickDetails(entry.pickDetails);
	    enterEvent.setPickInfo(pickInfo);
	    eventDistributor.tryGlobalListeners(enterEvent);
	    tryListenersForEntity(entry.entity, enterEvent);
	}
    }

    /**
     * Try to send the given event to the listeners for the given entity.
     */
    private void tryListenersForEntity (Entity entity, Event event) {
	EventListenerCollection listeners = 
	    (EventListenerCollection) entity.getComponent(EventListenerCollection.class);
	if (listeners != null && listeners.size() > 0) { 
	    Iterator<EventListener> it = listeners.iterator();
	    while (it.hasNext()) {
		EventListener listener = it.next();
		if (listener.isEnabled()) {
		    logger.fine("Calling consume for listener " + listener);
		    if (listener.consumesEvent(event)) {
			logger.fine("CONSUMED by entity " + entity);
			listener.postEvent(event);
		    }
		}
	    }
	}
    }

    private MouseEnterExitEvent3D createEnterExitEventFromAwtEvent (MouseEvent awtEvent, int id) {
	int x = awtEvent.getX();
	int y = awtEvent.getY();
	long when = awtEvent.getWhen();
	int modifiers = awtEvent.getModifiers();
	int button = awtEvent.getButton();
	MouseEvent me = new MouseEvent(dummyButton, id, when, modifiers, x, y, 0, false, button);
	return (MouseEnterExitEvent3D) createWonderlandEvent(me);
    }

    /**
     * HACK: This is called on a WindowSwing hit. If embedded swing is going to activate a grab
     * on this event then we must clean up any grabbing which the picker performed by deactivating
     * the grab. The proper way to handle this situation is to avoid activating the grab in first
     * place but this would greatly complicate the picker code. So we choose to take the more
     * expedient route and just "back out" the grab. The test case is as follows:
     *
     * 1. Perform a mouse press inside a WindowSwing.
     * 2. Move the cursor outside and then hit an avatar movement key.
     *
     * Unless we clean up the grab the key press will do nothing.
     */
    private void cleanupGrab (MouseEvent e) {
	if (grabIsActive && !isMouseGrab(e) && e.getID() != MouseEvent.MOUSE_CLICKED) {
	    grabIsActive = false;
	    grabPickInfo = null;
	}
    }

    //copied from Scenario EmbeddedSwing EmbeddedEventQueue.isMouseGrab
    /* This method effectively returns whether or not a mouse button was down
     * just BEFORE the event happened.  A better method name might be
     * wasAMouseButtonDownBeforeThisEvent().
     */
    private static boolean isMouseGrab(MouseEvent e) {
        int modifiers = e.getModifiersEx();
        
        if(e.getID() == MouseEvent.MOUSE_PRESSED 
                || e.getID() == MouseEvent.MOUSE_RELEASED) {
            switch (e.getButton()) {
            case MouseEvent.BUTTON1:
                modifiers ^= InputEvent.BUTTON1_DOWN_MASK;
                break;
            case MouseEvent.BUTTON2:
                modifiers ^= InputEvent.BUTTON2_DOWN_MASK;
                break;
            case MouseEvent.BUTTON3:
                modifiers ^= InputEvent.BUTTON3_DOWN_MASK;
                break;
            }
        }
        /* modifiers now as just before event */ 
        return ((modifiers & (InputEvent.BUTTON1_DOWN_MASK
                              | InputEvent.BUTTON2_DOWN_MASK
                              | InputEvent.BUTTON3_DOWN_MASK)) != 0);
    }
}



