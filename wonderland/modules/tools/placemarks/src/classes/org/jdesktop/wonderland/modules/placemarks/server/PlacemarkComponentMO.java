/**
 * Open Wonderland
 *
 * Copyright (c) 2011, Open Wonderland Foundation, All Rights Reserved
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

package org.jdesktop.wonderland.modules.placemarks.server;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedObjectRemoval;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.Task;
import java.io.Serializable;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.modules.placemarks.api.common.Placemark;
import org.jdesktop.wonderland.modules.placemarks.api.server.PlacemarkRegistrySrvFactory;
import org.jdesktop.wonderland.modules.placemarks.common.PlacemarkComponentServerState;
import org.jdesktop.wonderland.server.cell.CellComponentMO;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.TransformChangeListenerSrv;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;

/**
 * Server side component that creates a placemark at the location of the cell
 * the component is attached to.
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class PlacemarkComponentMO extends CellComponentMO 
        implements ManagedObjectRemoval
{
    private static Logger logger = Logger.getLogger(PlacemarkComponentMO.class.getName());

    private String placemarkName;
    private String placemarkRotation;
    private String moveTaskBindingName;

    public PlacemarkComponentMO(CellMO cell) {
        super(cell);

        moveTaskBindingName = PlacemarkComponentMO.class.getName() + 
                              ".moveTask." + cell.getCellID();
        MoveTask moveTask = new MoveTask(this);
        AppContext.getDataManager().setBinding(moveTaskBindingName, moveTask);
    }

    public void removingObject() {
        AppContext.getDataManager().removeBinding(moveTaskBindingName);
    }

    @Override
    protected String getClientClass() {
        return null;
    }

    @Override
    public CellComponentClientState getClientState(CellComponentClientState state,
                                                   WonderlandClientID clientID,
                                                   ClientCapabilities capabilities)
    {
        return null;
    }

    @Override
    public void setLive(boolean live) {
        super.setLive(live);

        if (live) {
            register();
            cellRef.get().addTransformChangeListener(new MoveListener(moveTaskBindingName));
        } else {
            unregister();
            cellRef.get().removeTransformChangeListener(new MoveListener(moveTaskBindingName));
        }
    }

    @Override
    public CellComponentServerState getServerState(CellComponentServerState state) {
        if (state == null) {
            state = new PlacemarkComponentServerState();
        }

        ((PlacemarkComponentServerState)state).setPlacemarkName(placemarkName);
        ((PlacemarkComponentServerState)state).setPlacemarkRotation(placemarkRotation);

        return super.getServerState(state);
    }

    @Override
    public void setServerState(CellComponentServerState state) {
        super.setServerState(state);

        // unregister the previous placemark
        if (isLive() && placemarkName != null) {
            unregister();
        }

        placemarkName = ((PlacemarkComponentServerState) state).getPlacemarkName();
        placemarkRotation= ((PlacemarkComponentServerState) state).getPlacemarkRotation();

        if (placemarkName == null) {
            // use the cell name if no placemark name is specified
            placemarkName = cellRef.get().getName();
        }

        if (placemarkRotation == null)
        {
            placemarkRotation = "";
        }
        
        // now register the new placemark
        if (isLive()) {
            register();
        }
    }

    protected void register() {
        Placemark pm = toPlacemark(placemarkName);
        if (pm != null) {
            PlacemarkRegistrySrvFactory.getInstance().registerPlacemark(pm);
        }
    }

    protected void unregister() {
        // construct a fake placemark with the given name
        Placemark pm = new Placemark(placemarkName, null, 0, 0, 0, 0);
        PlacemarkRegistrySrvFactory.getInstance().unregisterPlacemark(pm);
    }

    protected Placemark toPlacemark(String placemarkName) {
        CellTransform xform = cellRef.get().getWorldTransform(null);
        if (xform == null) {
            // the position of the cell has not been set.  We will be notified
            // later with a move message when the position is set, so we can
            // just return here.
            return null;
        }
   
        // get the cell's transform
        Vector3f trans = xform.getTranslation(null);
 
        // get the cell's rotation about each axis
        Quaternion rot = xform.getRotation(null);
        float[] angles = new float[3];
        rot.toAngles(angles);

        // get the relative offset (in Quaternions) 
        float angle = 0;
        if (placemarkRotation != null) {
            angle = (float) Math.toRadians(Float.valueOf(placemarkRotation)); 
        }

        // combine the relative offset with the y axis rotation (discard others) 
        angle  += angles[1];
 
        // convert angle to degrees
        angle = (float) Math.toDegrees(angle);

        // create placemark
        return new Placemark(placemarkName, null, trans.x, trans.y, trans.z, angle);    
    }

    /**
     * A listener that is notified whenever the component moves. If no
     * MoveTask is scheduled, this schedules one to update the placemark
     * associated with this object.
     */
    private static class MoveListener implements TransformChangeListenerSrv, Serializable {
        private String bindingName;

        public MoveListener(String bindingName) {
            this.bindingName = bindingName;
        }

        public void transformChanged(ManagedReference<CellMO> cellRef,
                                     CellTransform localTransform,
                                     CellTransform worldTransform)
        {
            // check if the task is already scheduled
            MoveTask mt = (MoveTask) AppContext.getDataManager().getBinding(bindingName);
            if (!mt.isScheduled()) {
                AppContext.getTaskManager().scheduleTask(mt, 10000);
                mt.setScheduled(true);
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final MoveListener other = (MoveListener) obj;
            if ((this.bindingName == null) ?
                (other.bindingName != null) :
                !this.bindingName.equals(other.bindingName))
            {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 83 * hash + (this.bindingName != null ? this.bindingName.hashCode() : 0);
            return hash;
        }

        
    }

    /**
     * A task to change the position of the placemark by registering then
     * unregistering. This is used when the cell moves to avoid sending to
     * many placemark changes to clients.
     */
    private static class MoveTask implements Task, ManagedObject, Serializable {
        private final ManagedReference<PlacemarkComponentMO> componentRef;
        private boolean scheduled = false;

        public MoveTask(PlacemarkComponentMO component) {
            componentRef = AppContext.getDataManager().createReference(component);
        }

        public boolean isScheduled() {
            return scheduled;
        }

        public void setScheduled(boolean scheduled) {
            this.scheduled = scheduled;
            AppContext.getDataManager().markForUpdate(this);
        }

        public void run() throws Exception {
            // unregister and re-register to update the placemark
            componentRef.get().unregister();
            componentRef.get().register();
            setScheduled(false);
        }
    }
}
