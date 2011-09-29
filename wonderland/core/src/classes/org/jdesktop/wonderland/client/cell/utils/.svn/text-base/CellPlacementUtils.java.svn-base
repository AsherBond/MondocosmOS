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
package org.jdesktop.wonderland.client.cell.utils;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector3f;
import java.util.logging.Logger;
import org.jdesktop.mtgame.JMECollisionSystem;
import org.jdesktop.mtgame.PickDetails;
import org.jdesktop.mtgame.PickInfo;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.ViewManager;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.cell.CellTransform;

/**
 * A set of utility routines to aid the initial placement of Cells in the world
 * based upon the hints given to it.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class CellPlacementUtils {

    private static Logger logger = Logger.getLogger(CellPlacementUtils.class.getName());

    /** The minimum distance away to place a Cell */
    private static final float MIN_DISTANCE = 1.0f;

    /**
     * Returns a Cell transform so that it is optimally placed given the
     * bounding volume of the Cell and the transform of the viewer. Optionally
     * takes the current server session, if null, uses the primary one.
     *
     * The transform returned is in world coordinates.
     *
     * @param session The server session
     * @param bounds The bounding volume of the Cell
     * @param viewTransform The transform of the view Cell
     */
    public static CellTransform getCellTransform(ServerSessionManager session,
            BoundingVolume bounds, CellTransform viewTransform) {

        Vector3f origin = getCellOrigin(session, bounds, viewTransform);
        Quaternion rotation = getCellRotation(viewTransform);
        return new CellTransform(rotation, origin);
    }

    /**
     * Returns a vector that represents the origin of a Cell placed optimally,
     * given the bounding volume of the Cell and the "view" Cell Tranform. Also
     * takes the server session, if null, uses the primary session.
     *
     * The origin returned is in world coordinates.
     *
     * @param session The server session
     * @param bounds The bounding volume of the Cell
     * @param viewTransform The transform of the view Cell
     */
    public static Vector3f getCellOrigin(ServerSessionManager session,
            BoundingVolume bounds, CellTransform viewTransform) {

        // If the given session is null, then simply take the current primary
        // session.
        ViewManager vm = ViewManager.getViewManager();
        if (session == null) {
            session = LoginManager.getPrimary();
        }

        // Fetch a whole bunch of things about the view cell: its transform,
        // position, rotation, and "look at" vector.
        Vector3f viewPosition = viewTransform.getTranslation(null);
        Quaternion viewRotation = viewTransform.getRotation(null);
        Vector3f lookAt = CellPlacementUtils.getLookDirection(viewRotation, null);

        logger.info("Using position of view cell " + viewPosition);
        logger.info("Using look-at vector of view cell " + lookAt);

        // Use the "Default" collision system
        JMECollisionSystem system = (JMECollisionSystem)
                ClientContextJME.getCollisionSystem(session, "Default");

        // Project a vector away from the avatar and see the distance to the
        // closest collidable. We want to make sure that the Cell is in front
        // of this collidable by a small (0.1f) amount.
//        float minDistance = CellPlacementUtils.getVectorToCollidable(system,
//                viewPosition, lookAt);
//        logger.warning("Distance away from the closest collidable " + minDistance);
        float minDistance = -1.0f;

        // Find the distance away from the view Cell to place the Cell based
        // upon the field-of-view, subject to a minimum and maximum distance.
        float fov = vm.getViewProperties().getFieldOfView();
        float distance = CellPlacementUtils.getDistance(bounds, fov,
                MIN_DISTANCE, minDistance);

        logger.info("Using field-of-view " + fov + " degrees.");
        logger.info("Distance away to place the Cell " + distance);
        
        // Create a vector representing the origin of the Cell. This is with
        // respect to the local view cell coordinates which we assume is
        // oriented so that +x is to the right, +y is up and +z is towards the
        // view.
        Vector3f origin = lookAt.mult(distance);

        // Add to the position of the view cell to determine the (nearly) final
        // origin for the Cell's initial position.
        origin = origin.add(viewPosition);

        // Try to find the ground position based upon the cell's origin and
        // collision with the ground.
        // OWL issue #166: be sure to do this at the cell's projected origin
        // as opposed to the view location, otherwise you will pick the avatar
        // instead of the ground.
        float yDown = CellPlacementUtils.getVectorToGround(system, origin);
        if (yDown == -1) {
            logger.warning("Unable to find floor for initial Cell placement," +
                    " using avatar position as floor, y=" + viewPosition.y);
            yDown = 0;
        }

        // Use the distance to the ground from the view Cell to find where to
        // place the Cell so that is above ground, with respect to the view
        // Cell.
        float height = CellPlacementUtils.getBoundsHeight(bounds);
        origin.y += height - yDown;

        logger.info("Determined world origin of Cell is " + origin);
        return origin;
    }

    /**
     * Returns the rotation of a Cell so that is faces the viewer.
     *
     * @param viewTransform The transform of the viewer
     * @return A rotation in world coordinates
     */
    public static Quaternion getCellRotation(CellTransform viewTransform) {
        Quaternion viewRotation = viewTransform.getRotation(null);
        Vector3f lookAt = CellPlacementUtils.getLookDirection(viewRotation, null);
        Quaternion rotation = new Quaternion();
        rotation.lookAt(lookAt.negate(), new Vector3f(0, 1, 0));
        return rotation;
    }

    /**
     * Returns the distance away to play a Cell so that its horizontal bounds
     * are entirely in-view. Takes the bounding volume of the Cell and a field-
     * of-view of the viewer (in degrees). Assumes the bounding volume is either
     * a sphere or box, if not, assumes it has a radius of 1.0.
     * <p>
     * This method assumes the Cell is rotated so that, with respect to the
     * viewer, the +x axis is to the right, the +y axis is up, and the +z axis
     * is towards the viewer.
     * <p>
     * This distance returned is at least 'minDistance' away from the viewer
     * and at most 'maxDistance' away from the viewer. If the value for the
     * minimum distance is -1, there is no minimum distance. If the maximum
     * distance is -1, there is no maximum distance. Note that the maximum
     * distance takes precendence over the minimum distance: that is, the Cell
     * may be placed closer than the minimum distance if it exceeds the maximum
     * distance.
     * 
     * @param bounds The bounding volume of the Cell
     * @param fieldOfView The field-of-view of the viewer (in degrees)
     * @param minDistance The minimum distance away from the viewer that the
     * front face of the Cell may be, or -1 for no minimum distance
     * @param maxDistance The maximum distance away from the viewer that the
     * front face of the Cell may be, or -1 for no maximim distance
     * @return The distance away to place the Cell so that the horizontal bounds
     * are entirely within view.
     */
    public static float getDistance(BoundingVolume bounds, float fieldOfView,
            float minDistance, float maxDistance) {
        
        // Depending upon whether the bounds is a sphere, or a box, determine
        // how far to place the Cell so that it is entirely in-view horizontally.
        // We assume the Cell is rotated so that its local +x axis is to the
        // right, +y axis is up, and +z axis is towards the viewer. This means
        // that the horizontal axis is the x-axis and the front-to-back axis
        // is the z-axis.
        float alongRadius = 1.0f;
        float crossRadius = 1.0f;
        if (bounds instanceof BoundingSphere) {
            alongRadius = crossRadius = ((BoundingSphere)bounds).radius;
        }
        else if (bounds instanceof BoundingBox) {
            alongRadius = ((BoundingBox)bounds).xExtent;
            crossRadius = ((BoundingBox)bounds).zExtent;
        }
        double eyeDist = alongRadius / Math.tan(Math.toRadians(fieldOfView / 2.0));

        // To make sure the front face of the Cell is in-view we must add the
        // bounds of the Cell along the z-axis
        eyeDist += crossRadius;

        // Make sure the Cell is at least a minimum distance away from the
        // viewer, if it is not -1
        if (minDistance != -1) {
            eyeDist = Math.max(eyeDist, minDistance);
        }

        // Make sure the Cell is at most a maximum distance away from the
        // viewer, if it is not -1
        if (maxDistance != -1) {
            eyeDist = Math.min(eyeDist, maxDistance);
        }
        
        return (float)eyeDist;
    }

    /**
     * Returns the vertical component of the bounds, assuming the +y axis is up.
     *
     * @param bounds The bounding volume
     * @return The vertical component of the bounds
     */
    public static float getBoundsHeight(BoundingVolume bounds) {
        if (bounds instanceof BoundingSphere) {
            return ((BoundingSphere) bounds).radius;
        }
        else if (bounds instanceof BoundingBox) {
            return ((BoundingBox)bounds).yExtent;
        }
        return 0.0f;
    }

    /**
     * Returns the "look direction" given a rotation.
     *
     * @param rotation The Quaternion rotation
     * @param v If non-null, place the look-direction in this vector
     * @return The look direction
     */
    public static Vector3f getLookDirection(Quaternion rotation, Vector3f v) {
        if (v == null) {
            v = new Vector3f(0, 0, 1);
        } else {
            v.set(0, 0, 1);
        }
        rotation.multLocal(v);
        v.normalizeLocal();
        return v;
    }

    /**
     * Returns the distance to the ground from the given position vector. This
     * method assumes the ground is in the -y direction. Returns -1 if no ground
     * is found.
     *
     * @param collision The collision system to do picking
     * @param position The world translation of the position from which the
     * vector to the ground will be computed.
     * @return A floating point distance to the ground, as a positive number.
     */
    public static float getVectorToGround(JMECollisionSystem collision,
            Vector3f position) {

        // Construct a ray down to the ground from the position given. We add
        // a bit of "slop" to the y vector.
        float yDelta = 1.0f;
        Ray heightRay = new Ray();
        heightRay.origin.set(position);
        heightRay.origin.y += yDelta;
        heightRay.direction = new Vector3f(0.0f, -1.0f, 0.0f);

        // Use the given collision system to find where the ground is. If we
        // find one, then return the distance to the ground with respect to
        // the given view position.
        PickInfo pi = collision.pickAllWorldRay(heightRay, true, false);
        if (pi.size() != 0) {
            // Grab the first one
            PickDetails pd = pi.get(0);
            return pd.getDistance() - yDelta;
        }

        // Otherwise, if we did not find any ground, then return -1
        return -1;
    }

    /**
     * Returns the distance from the avatar to the closest collidable object,
     * given a "look at" vector of the avatar and its current position. Returns
     * -1 if no collidable is found.
     * <p>
     * This method is used, for example, to find the closest "wall" to the
     * avatar.
     *
     * @param collision The collision system to do picking
     * @param position The position of the avatar
     * @param lookAt The direction the avatar is looking at
     * @return A floating point distance to the closest collidable object
     */
    public static float getVectorToCollidable(JMECollisionSystem collision,
            Vector3f position, Vector3f lookAt) {
        
        // Construct a ray out from the avatar given it's "look at".
        Ray heightRay = new Ray();
        heightRay.origin.set(position);
        heightRay.direction = lookAt;

        logger.info("For collidable, origin=" + position + " look at " +
                lookAt);

        // Use the given collision system to find where the closest collidable
        // object is.
        PickInfo pi = collision.pickAllWorldRay(heightRay, true, false);
        if (pi.size() != 0) {
            // Grab the first one
            PickDetails pd = pi.get(0);
            return pd.getDistance();
        }

        // Otherwise, if we did not find any ground, then return -1
        return -1;
    }

    /**
     * A utility routine to convert the given transform from world coordinates
     * to another reference system. Typically, the given transform is the
     * initial transform of the Cell in world coordinates and we want to
     * transform with respect to some Cell in the world.
     *
     */
    public static CellTransform transform(CellTransform transform,
            CellTransform fromReferenceSystem, CellTransform toReferenceSystem) {

        CellTransform newTransform = toReferenceSystem.clone(null);
        newTransform.invert();
        newTransform.mul(fromReferenceSystem);
        newTransform.mul(transform);
        return newTransform;
    }
}
