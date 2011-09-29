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
package org.jdesktop.wonderland.common.cell;

import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.Math3DUtils;

import com.jme.math.FastMath;

/**
 *
 * Utility class to help implement proximity listeners on both client and server
 *
 * @author paulby
 * @author Drew Harry <drew_harry@dev.java.net>
 */
public class ProximityListenerRecord implements Serializable {

    protected static final Logger logger = Logger.getLogger(ProximityListenerRecord.class.getName());

    protected ProximityListenerWrapper proximityListener;
    private BoundingVolume[] localProxBounds;
    private BoundingVolume[] worldProxBounds;
    // private BoundingVolume currentlyIn = null;
    // private int currentlyInIndex = -1;

    // These maps keep track of which bounding volume index each CellID
    // (ViewCellID, which maps to a single avatar) is contained by. Used for
    // deciding of an avatar that has moved is entering/existing a bound that
    // this listener is tracking.
    protected Map<CellID, Integer> lastContainerIndexMap = null;

    // a lock to use when changing bounds
    protected final Serializable lock = new Serializable() {};

    // For serialization support on server
    public ProximityListenerRecord() {

    }

    public ProximityListenerRecord(ProximityListenerWrapper proximityListener, BoundingVolume[] localBounds) {
        this.proximityListener = proximityListener;
        setProximityBounds(localBounds);
    }

     /**
     * Set a list of bounds for which the system will track view enter/exit for
     * this cell. When the view enters/exits one of these bounds the listener
     * will be called with the index of the bounds in the supplied array.
     *
     * The bounds must be ordered from largest to smallest, thus localBounds[i]
     * must enclose localBounds[i+1]. An IllegalArgumentException will be thrown
     * if this is not the case.
     *
     * @param bounds
     */
    public void setProximityBounds(BoundingVolume[] localBounds) {
        this.localProxBounds = new BoundingVolume[localBounds.length];
        this.worldProxBounds = new BoundingVolume[localBounds.length];
        int i=0;
        for (BoundingVolume b : localBounds) {
            this.localProxBounds[i] = b.clone(null);
            worldProxBounds[i] = b.clone(null);

            if (i > 0 && !Math3DUtils.encloses(localProxBounds[i-1], localProxBounds[i]))
                    throw new IllegalArgumentException("Proximity Bounds incorrectly ordered");
            i++;
        }
    }

    /**
     * Get the proximity bounds in world coordinates.
     * @return the proximit bounds translated to world coordinates
     */
    public BoundingVolume[] getWorldBounds() {
        BoundingVolume[] out = new BoundingVolume[worldProxBounds.length];
        for (int i = 0; i < worldProxBounds.length; i++) {
            worldProxBounds[i].clone(out[i]);
        }
        return out;
    }

    /**
     * The cell world bounds have been updated, so update our internal
     * structures
     */
    public void updateWorldBounds(CellTransform worldTransform) {
        if (localProxBounds == null)
            return;

        // Update the world proximity bounds
        int i = 0;
        synchronized(lock) {
            for(BoundingVolume lb : localProxBounds) {
                worldProxBounds[i] = lb.clone(worldProxBounds[i]);
                worldTransform.transform(worldProxBounds[i]);
                
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest("Update world bounds: " + worldProxBounds[i] +
                                  " on " + this);
                }

                i++;
            }
        
            // the bounds of the cell have change. Go through each listener
            // record and calculate if that user has changed relative to the
            // bounds
            //CellID[] ids = getRecords().keySet().toArray(new CellID[0]);
            //ListenerRecord[] records = getRecords().values().toArray(new ListenerRecord[0]);
            //for (int c = 0; c < ids.length; c++) {
            //  viewCellMoved(ids[c], records[c].viewCellTransform);
            //}
        }
    }

    /**
     * The view cells transform has changed so update our internal structures
     * @param cell
     */
    public void viewCellMoved(CellID viewCellID, CellTransform viewCellTransform) {
        Vector3f viewCellWorldTranslation = viewCellTransform.getTranslation(null);

        // View Cell has moved
        synchronized(lock) {
            int currentContainerIndex = -1;      // -1 = not in any bounding volume
            int i = 0;
            while(i < worldProxBounds.length) {
                if (logger.isLoggable(Level.FINEST)) {
                    logger.finest("Checking if " + worldProxBounds[i] + 
                                  " contains " + viewCellWorldTranslation + 
                                  " on " + this);
                }

		boolean contains;

		if (worldProxBounds[i] instanceof BoundingBox) {
                    contains = contains((BoundingBox) worldProxBounds[i], viewCellWorldTranslation);
		} else {
		    contains = worldProxBounds[i].contains(viewCellWorldTranslation);
		}

                if (contains) {
                    currentContainerIndex = i;
                } else {
                    i = worldProxBounds.length; // Exit the while
                }
                i++;
            }

            // At this point, we know which bounds (if any) the viewCell
            // is currently contained by. Now we need to check and see if
            // it used to be in different bounds, the same bounds
            // or no bounds at all to decide if this represents an enter or
            // exit event.

            // Check to see if we have a record of this viewCell's position.
            int lastContainerIndex = -1;
            if (getIndexMap().containsKey(viewCellID)) {
                lastContainerIndex = getIndexMap().get(viewCellID);
            }

            // If they've changed, we need to look closer.
            // if they haven't changed, then it means an avatar
            // moved but is still in their same bounds
            if (lastContainerIndex == currentContainerIndex) {
                return;
            }

            logger.warning("View cell " + viewCellID + " lastContainerIndex: " +
                           lastContainerIndex + " currentContainerIndex: " +
                           currentContainerIndex + " on " + this);


            // Loop through the bounds to make sure we properly give all
            // updates to the listener.  This ensures that enters and exits
            // will always match up for any view.
            if (currentContainerIndex < lastContainerIndex) {
                // EXITS
                for (int l = lastContainerIndex; l > currentContainerIndex; l--) {
                    proximityListener.viewEnterExit(false, worldProxBounds[l],
                                                    l, viewCellID);
                }

                // remove this user from the map if they're currently contained
                // by no bounds object.  Otherwise, just update the existing
                // record with the current container.
                if(currentContainerIndex == -1) {
                    getIndexMap().remove(viewCellID);
                } else {
                    getIndexMap().put(viewCellID, currentContainerIndex);
                }
            } else {
                // ENTERS
                for (int l = lastContainerIndex + 1; l <= currentContainerIndex; l++) {
                    proximityListener.viewEnterExit(true, worldProxBounds[l],
                                                    l, viewCellID);
                }

                // update the record with the current container
                getIndexMap().put(viewCellID, currentContainerIndex);
            }
        }
    }

    private boolean contains(BoundingBox bounds, Vector3f point) {
	Vector3f center = bounds.getCenter();
	Vector3f extent = bounds.getExtent(null);
	
        return FastMath.abs(center.x - point.x) - extent.x <= .01
                && FastMath.abs(center.y - point.y) - extent.y <= .01
                && FastMath.abs(center.z - point.z) - extent.z <= .01;
    }

    /**
     * The view cell has exited, so exit all bounds
     */
    public void viewCellExited(CellID viewCellID) {
        synchronized (lock) {
            if (getIndexMap().containsKey(viewCellID)) {
                int lastContainerIndex = getIndexMap().remove(viewCellID);

                // notify the listener of each bounds in turn
                for (int curIdx = lastContainerIndex; curIdx >= 0; curIdx--) {
                    proximityListener.viewEnterExit(false, worldProxBounds[curIdx],
                                                    curIdx, viewCellID);
                }
            }
        }
    }

    /**
     * Get the index map for storing view-to-index mappings.  Subclasses
     * may override to provide a map other than the default.
     * @return the map from view cell ID to bounds index for all listeners
     */
    protected Map<CellID, Integer> getIndexMap() {
        synchronized (lock) {
            if (lastContainerIndexMap == null) {
                lastContainerIndexMap = new HashMap<CellID, Integer>();
            }
        }

        return lastContainerIndexMap;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ProximityListenerRecord))
            return false;

        ProximityListenerRecord plr = (ProximityListenerRecord) o;
        if (plr.proximityListener == null) {
            return (proximityListener == null);
        } else {
            return plr.proximityListener.equals(proximityListener);
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + (this.proximityListener != null ? this.proximityListener.hashCode() : 0);
        return hash;
    }

    /**
     * Wrapper for the listener, client and server listeners have a slightly
     * different interface
     */
    public interface ProximityListenerWrapper extends Serializable {
        public void viewEnterExit(boolean enter, BoundingVolume proximityVolume, int proximityIndex, CellID viewCellID );
    }
}
