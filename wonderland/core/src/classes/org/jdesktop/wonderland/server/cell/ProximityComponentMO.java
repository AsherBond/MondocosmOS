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
package org.jdesktop.wonderland.server.cell;

import com.jme.bounding.BoundingVolume;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ObjectNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.server.cell.ServerProximityListenerRecord.ServerProximityListenerWrapper;
import org.jdesktop.wonderland.server.spatial.UniverseManager;

/**
 * Provides a mechanism for listener notification when the a view cell
 * enters/exits a set of bounds for a cell. 
 * 
 * The bounds must be ordered from largest to smallest, thus localBounds[i]
 * must enclose localBounds[i+1]. The listeners will be notified as the View
 * enters each subsequent bounding volume and then notified the view exits each
 * volume.
 * 
 * For example given a set of Bounding Spheres with the same center and radii of
 * 10, 5, 2. As the ViewCell moves from outside to the center of the spheres the
 * listeners will be called with
 * 
 * enter, 10
 * enter, 5 
 * enter, 2
 * 
 * then as the user moves away from the center the following sequence of exits
 * will be called
 * 
 * exit, 2
 * exit, 5
 * exit, 10
 * 
 * 
 * @author paulby
 */
@ExperimentalAPI
public class ProximityComponentMO extends CellComponentMO {
    private static final Logger LOGGER =
	Logger.getLogger(ProximityComponentMO.class.getName());

    protected final Map<ProximityListenerSrv, ServerProximityListenerRecord> proximityListeners =
        new LinkedHashMap<ProximityListenerSrv, ServerProximityListenerRecord>();
    private boolean isLive = false;
    private int nextID = 0;

//    private ManagedReference<UserLeftListener> userListenerRef;
    
    /**
     * Set a list of bounds for which the system will track view enter/exit for
     * this cell. When the view enters/exits one of these bounds the listener
     * will be called with the index of the bounds in the supplied array.
     * 
     * The bounds must be ordered from largest to smallest, thus localBounds[i]
     * must enclose localBounds[i+1]
     * 
     * @param cell the cell
     * @param localProximityBounds the proximity bounds in cell local coordinates
     */
    public ProximityComponentMO(CellMO cell) {
        super(cell);
    }
    
    /**
     * Add a ProximityListener for the cell to which this component is attached.
     * The listener will be called as View cells in the universe enter or exit
     * the bounds specified.
     *
     * The bounds must be ordered from largest to smallest, thus localBounds[i]
     * must enclose localBounds[i+1]. The listeners will be notified as the View
     * enters each subsequent bounding volume and then notified the view exits each
     * volume.
     *
     * For example given a set of Bounding Spheres with the same center and radii of
     * 10, 5, 2. As the ViewCell moves from outside to the center of the spheres the
     * listeners will be called with
     *
     * enter, 10
     * enter, 5
     * enter, 2
     *
     * then as the user moves away from the center the following sequence of exits
     * will be called
     *
     * exit, 2
     * exit, 5
     * exit, 10
     * 
     * @param listener
     * @param localBounds the set of bounds, in the local coordinate system of the cell
     */
    public void addProximityListener(ProximityListenerSrv listener, BoundingVolume[] localBounds) {
        String id = getIDFor(listener);

        if (listener instanceof ManagedObject) {
            listener = new ManagedProximityListenerWrapper(listener, id);
        }

        ServerProximityListenerRecord rec = new ServerProximityListenerRecord(
                        new ServerProximityListenerWrapper(cellID, listener, id),
                        localBounds, id);

        proximityListeners.put(listener, rec);
       
        UniverseManager mgr = AppContext.getManager(UniverseManager.class);
        CellMO cell = cellRef.get();
        rec.setLive(isLive, cell, mgr);
    }

    /**
     * Remove the specified ProximityListener
     * @param listener
     */
    public void removeProximityListener(ProximityListenerSrv listener) {
	if (listener instanceof ManagedObject) {
            listener = new ManagedProximityListenerWrapper(listener, getIDFor(listener));
        }

        ServerProximityListenerRecord rec = proximityListeners.remove(listener);

        if (rec != null) {
            UniverseManager mgr = AppContext.getManager(UniverseManager.class);
            CellMO cell = cellRef.get();
            rec.setLive(false, cell, mgr);
        }

	// clean up the binding the wrapper created
        if (listener instanceof ManagedProximityListenerWrapper) {
            ((ManagedProximityListenerWrapper) listener).cleanup();
        }
    }

    /**
     * Updates the bounds of the specified listener to be the specified local bounds.
     *
     * If the specified listener object is not a registered listener, this method
     * will have no effect.
     * 
     * @param listener The listener object who's bounds you want to change.
     * @param localBounds The new bounds list.
     */
    public void setProximityListenerBounds(ProximityListenerSrv listener, BoundingVolume[] localBounds) {
	if (listener instanceof ManagedObject) {
            listener = new ManagedProximityListenerWrapper(listener, getIDFor(listener));
        }

        ServerProximityListenerRecord rec = this.proximityListeners.get(listener);
	
        if (rec != null) {
            rec.setProximityBounds(localBounds);
        }
    }
    
    @Override
    public void setLive(boolean isLive) {
        super.setLive(isLive);
        this.isLive = isLive;

        if (isLive) {
            UniverseManager mgr = AppContext.getManager(UniverseManager.class);
            CellMO cell = cellRef.get();
            for (ServerProximityListenerRecord rec : proximityListeners.values()) {
                rec.setLive(isLive, cell, mgr);
            }
        } else {
            UniverseManager mgr = AppContext.getManager(UniverseManager.class);
            CellMO cell = cellRef.get();
             for (ServerProximityListenerRecord rec : proximityListeners.values()) {
                rec.setLive(isLive, cell, mgr);
             }
         }
    }

    @Override
    protected String getClientClass() {
        return null;
    }

    public String getIDFor(ProximityListenerSrv listener) {
        // issue #1101: if the listener is a managed object, generate
        // and ID based on the ID of the underlying managed object.
        if (listener instanceof ManagedObject) {
            DataManager dm = AppContext.getDataManager();
            return cellID + "." + dm.createReference(listener).getId();
        } else {
            // the object is not a managed object, so return a newly assigned
            // id
            return cellID + "." + nextID++;
        }
    }

    static class ManagedProximityListenerWrapper implements ProximityListenerSrv {
        private static final String BINDING_NAME =
                ManagedProximityListenerWrapper.class.getName();
        private String id;

        public ManagedProximityListenerWrapper(ProximityListenerSrv listener, String id) {
            this.id = id;

            DataManager dm = AppContext.getDataManager();
            dm.setBinding(BINDING_NAME + id, listener);
        }

        public void viewEnterExit(boolean entered, CellID cell, CellID viewCellID,
                                  BoundingVolume proximityVolume, int proximityIndex)
        {
            DataManager dm = AppContext.getDataManager();

            try {
                ProximityListenerSrv listener = (ProximityListenerSrv)
                        dm.getBinding(BINDING_NAME + id);
                listener.viewEnterExit(entered, cell, viewCellID,
                                                proximityVolume, proximityIndex);
            } catch (ObjectNotFoundException onfe) {
                LOGGER.warning("[ManagedProximityListenerWrapper] Object " +
                               id + " not found");
	    }
	}

	public void cleanup() {
	    AppContext.getDataManager().removeBinding(BINDING_NAME + id);
	}

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ManagedProximityListenerWrapper)) {
                return false;
            }

            return id.equals(((ManagedProximityListenerWrapper) o).id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }
}
