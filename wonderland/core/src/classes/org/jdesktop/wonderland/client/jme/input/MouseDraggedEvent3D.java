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

import java.awt.Point;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.math.Matrix4f;
import com.jme.scene.Node;
import java.awt.event.MouseEvent;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.mtgame.PickDetails;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import java.util.logging.Logger;
import org.jdesktop.mtgame.CollisionComponent;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.common.InternalAPI;

/**
 * An event which indicates that a mouse drag action occurred. 
 *
 * @author deronj
 */

@ExperimentalAPI
public class MouseDraggedEvent3D extends MouseMovedEvent3D {
    
    private static final Logger logger = Logger.getLogger(MouseDraggedEvent3D.class.getName());

    static {
	/** Allocate this event type's class ID. */
	EVENT_CLASS_ID = Event.allocateEventClassID();
    }

    /** The raw pick details of the actual pick hit. */
    protected PickDetails hitPickDetails;

    /** Default constructor (for cloning) */
    protected MouseDraggedEvent3D () {}

    /**
     * Create a new MouseDraggedEvent3D with a null pickDetails from an AWT mouse event.
     * @param awtEvent The AWT event
     */
    MouseDraggedEvent3D (MouseEvent awtEvent) {
        this(awtEvent, null);
    }

    /**
     * Create a new MouseDraggedEvent3D from an AWT mouse event.
     * @param awtEvent The AWT event
     * @param pickDetails The pick data for the event.
     */
    MouseDraggedEvent3D (MouseEvent awtEvent, PickDetails pickDetails) {
        super(awtEvent, pickDetails);
    }

    /**
     * Used by InputPicker to specify the raw hit pick details of this drag event.
     * <br>
     * INTERNAL ONLY
     */
    @InternalAPI
    public void setHitPickDetails (PickDetails hitPickDetails) {
	this.hitPickDetails = hitPickDetails;
    }

    /**
     * Returns the raw hit pick details of this drag event.
     */
    public PickDetails getHitPickDetails () {
	return hitPickDetails;
    }

    /**
     * Returns the actually entity hit by the event.
     */
    public Entity getHitEntity () {
	if (hitPickDetails == null) return null;
	return hitPickDetails.getEntity();
    }

    /**
     * Returns the distance from the eye to the intersection point, based on the actual hit pick details.
     * which were calculated by the input system. (This distance is in world coordinates). If the event has 
     * no hit pick details, 0 is returned. 
     */
   public float getHitDistance () {
	if (hitPickDetails == null) {
	    return 0f;
	} else {
	    return hitPickDetails.getDistance();
	}
    }

    /**
     * Returns the intersection point in world coordinates, based on the actual hit pick details.
     */
    public Vector3f getHitIntersectionPointWorld () {
	if (hitPickDetails == null) {
	    return null;
	} else {
	    return new Vector3f(hitPickDetails.getPosition());
	}
    }

    /**
     * Returns the intersection point in object (node) local coordinates, based on the actual hit 
     * pick details.
     */
    public Vector3f getHitIntersectionPointLocal () {
	if (hitPickDetails == null) {
	    return null;
	} else {
	    Vector3f posWorld = hitPickDetails.getPosition();
	    if (posWorld == null) return null;
	    CollisionComponent cc = hitPickDetails.getCollisionComponent();
	    Node node = cc.getNode();
	    node.getLocalToWorldMatrix(world2Local);
	    world2Local.invert();
	    return world2Local.mult(hitPickDetails.getPosition(), new Vector3f());
	}
    }

    /**
     * Returns the drag vector in world coordinates relative to the last mouse button press point.
     * While dragging, the returned value is the pointer movement vector projected into the plane 
     * of the drag start (mouse button press) point.
     * @param ret An Vector3f in which to store the drag vector. If null a new vector is created.
     * @return The argument ret is returned. If it was null a new vector is returned.
     */
    public Vector3f getDragVectorWorld (Vector3f dragStartWorld, Point dragStartScreen, Vector3f ret) {
        MouseEvent me = (MouseEvent) awtEvent;
	return getDragVectorWorld(me.getX(), me.getY(), dragStartWorld, dragStartScreen, ret);
    }

    public static Vector3f getDragVectorWorld (int eventX, int eventY, Vector3f dragStartWorld, 
                                               Point dragStartScreen, Vector3f ret) {
	
	logger.fine("dragStartWorld rel = " + dragStartWorld);
	if (ret == null) {
	    ret = new Vector3f();
	}

	// The current world position of the eye
	Vector3f eyeWorld = InputPicker3D.getInputPicker().getCameraPosition(null);
	logger.fine("eyeWorld = " + eyeWorld);

	// The float movement vector in screen space
	Vector2f scrPos = new Vector2f((float)(eventX - dragStartScreen.x),
                                       (float)(eventY - dragStartScreen.y));
	logger.fine("scrPos = " + scrPos);

	Vector2f pressXY = new Vector2f((float)dragStartScreen.x, (float)dragStartScreen.y);
	Vector3f pressWorld = ((InputManager3D)InputManager3D.getInputManager()).
	    getCamera().getWorldCoordinates(pressXY, 0f);

	Vector2f dragXY = new Vector2f((float)eventX, (float)eventY);
	Vector3f dragWorld = ((InputManager3D)InputManager3D.getInputManager()).
	    getCamera().getWorldCoordinates(dragXY, 0f);

	// The world position of this event (in the view plane)
	Vector3f thisWorld = ((InputManager3D)InputManager3D.getInputManager()).
	    getCamera().getWorldCoordinates(scrPos, 0f);
	logger.fine("thisWorld = " + thisWorld);

	// The calculations need to take place in eye space. Get the necessary matrices.
	Matrix4f camMatrix = InputPicker3D.getInputPicker().getCameraModelViewMatrix(null);
	Matrix4f camInverse = InputPicker3D.getInputPicker().getCameraModelViewMatrixInverse(null);
	logger.finest("camInverse = " + camInverse);

	// Transform vectors from world space into eye space
	Vector3f dragEye = new Vector3f();
	Vector3f dragStartEye = new Vector3f();
	Vector3f pressEye = new Vector3f();
	Vector3f eyeEye = new Vector3f();
	Vector3f thisEye = new Vector3f();
	camInverse.mult(dragWorld, dragEye);
	camInverse.mult(dragStartWorld, dragStartEye);
	camInverse.mult(pressWorld, pressEye);
	// TODO: perf: only really need to recalc eyeEye on camera change
	camInverse.mult(eyeWorld, eyeEye);
	camInverse.mult(thisWorld, thisEye);

	// The displacement vector of this event from the center of the drag plane
	Vector3f dragVectorEye = new Vector3f(
            (dragEye.x - pressEye.x) * (dragStartEye.z - eyeEye.z) / (thisEye.z - eyeEye.z),
	    (pressEye.y - dragEye.y) * (dragStartEye.z - eyeEye.z) / (thisEye.z - eyeEye.z),
	    0f);
	logger.fine("dragVectorEye = " + dragVectorEye);

	logger.finest("camInverse = " + camInverse);

	// Convert drag vector from eye space to world space
	camMatrix.mult(dragVectorEye, ret);
	logger.fine("dragVectorWorld = " + ret);

	return ret;
    }

    /** {@inheritDoc} */
    @Override
    public String toString () {
	// TODO: add internal state when drag methods are added
	return "Mouse Drag";
    }

    /** 
     * {@inheritDoc}
     * <br>
     * If event is null, a new event of this class is created and returned.
     * <br>
     * NOTE: any state set by <code>setPressPointScreen</code> and <code>getDragVectorWorld</copy>
     * is not copied into the newly cloned object.
     */
    @Override
    public Event clone (Event event) {
	if (event == null) {
	    event = new MouseDraggedEvent3D();
	}
        ((MouseDraggedEvent3D)event).hitPickDetails = hitPickDetails;
	return super.clone(event);
    }
}
