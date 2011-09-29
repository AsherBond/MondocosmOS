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

import com.jme.math.Matrix4f;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import java.awt.event.MouseEvent;
import org.jdesktop.mtgame.CollisionComponent;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.PickDetails;
import org.jdesktop.mtgame.JMEPickDetails;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.InternalAPI;

/**
 * The abstract super class for all Wonderland mouse events.
 *
 * @author deronj
 */
@ExperimentalAPI
public abstract class MouseEvent3D extends InputEvent3D {

    /** The supported button codes. */
    public enum ButtonId {

        NOBUTTON, BUTTON1, BUTTON2, BUTTON3
    };
    /** The destination pick details for the event (accounts for grabs) */
    private PickDetails pickDetails;
    /** A temporary used for getIntersectionPointLocal */
    protected Matrix4f world2Local;

    /** Default constructor (for cloning) */
    protected MouseEvent3D() {
    }

    /**
     * Create an instance of MouseEvent3D.
     * @param awtEvent The originating AWT mouse event
     * @param pickDetails The pick details for the event.
     */
    public MouseEvent3D(MouseEvent awtEvent, PickDetails pickDetails) {
        super(awtEvent);
        this.pickDetails = pickDetails;
    }

    /**
     * Returns the node which the pick hit.
     */
    public Node getNode () {
        return ((JMEPickDetails)pickDetails).getReportedNode();
    }

    /**
     * Returns the original pick details of the event. 
     */
    public PickDetails getPickDetails() {
        return pickDetails;
    }

    /**
     * INTERNAL ONLY
     * <br>
     * Sets the original desination pick details of the event, as calculated by the input system,
     * accounting for grabs.
     */
    @InternalAPI
    public void setPickDetails(PickDetails pickDetails) {
        this.pickDetails = pickDetails;
    }

    /**
     * Returns the entity hit by the event, based on the destination pick details which were calculated
     * by the input system. Normally, this will be the pick hit entity unless previously overridden by 
     * the input system grab calculations. 
     */
    @Override
    public Entity getEntity() {
        if (entity == null) {
	    if (pickDetails != null) {
		entity = pickDetails.getEntity();
	    }
        }
        return entity;
    }

    /**
     * Returns which, if any, of the mouse buttons has changed state.
     * @return one of the following enums: NOBUTTON, BUTTON1, BUTTON2 or BUTTON3.
     */
    public ButtonId getButton() {
        ButtonId ret = ButtonId.NOBUTTON;

        int button = ((MouseEvent) awtEvent).getButton();
        switch (button) {
            case MouseEvent.BUTTON1:
                ret = ButtonId.BUTTON1;
                break;
            case MouseEvent.BUTTON2:
                ret = ButtonId.BUTTON2;
                break;
            case MouseEvent.BUTTON3:
                ret = ButtonId.BUTTON3;
                break;
            default:
                assert (button == MouseEvent.NOBUTTON);
        }

        return ret;
    }

    /**
     * Returns the distance from the eye to the intersection point, based on the destination pick details 
     * which were calculated by the input system. (This distance is in world coordinates). If the event has 
     * no pick details, 0 is returned. 
     */
    public float getDistance() {
        if (pickDetails == null) {
            return 0f;
        } else {
            return pickDetails.getDistance();
        }
    }

    /**
     * Returns the intersection point in world coordinates, based on the destination pick details 
     * which were calculated by the input system.
     */
    public Vector3f getIntersectionPointWorld() {
        if (pickDetails == null) {
            return null;
        } else {
	    return new Vector3f(pickDetails.getPosition());
        }
    }

    /**
     * Returns the intersection point in object (node) local coordinates, based on the destination 
     * pick details which were calculated by the input system.
     */
    public Vector3f getIntersectionPointLocal() {
        if (pickDetails == null) {
            return null;
        } else {
            Vector3f posWorld = pickDetails.getPosition();
            if (posWorld == null) {
                return null;
            }
            CollisionComponent cc = pickDetails.getCollisionComponent();
            Node node = cc.getNode();
            node.getLocalToWorldMatrix(world2Local);
            world2Local.invert();
            return world2Local.mult(pickDetails.getPosition(), new Vector3f());
        }
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public Event clone(Event event) {
        // Note: doesn't create an event because this class is abstract
        super.clone(event);
        ((MouseEvent3D) event).pickDetails = pickDetails;
        return event;
    }
}
