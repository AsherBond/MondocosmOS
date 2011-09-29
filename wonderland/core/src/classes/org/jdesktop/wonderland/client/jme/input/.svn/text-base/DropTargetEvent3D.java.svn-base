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
package org.jdesktop.wonderland.client.jme.input;

import com.jme.scene.Node;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.JMEPickDetails;
import org.jdesktop.mtgame.PickDetails;
import org.jdesktop.mtgame.PickInfo;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.jme.dnd.DragAndDropManager;

/**
 * 3D representation of a DropTarget event
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
public abstract class DropTargetEvent3D extends Event {
    private static final Logger LOGGER =
            Logger.getLogger(DropTargetEvent3D.class.getName());

    private DropTargetEvent dropEvent;
    private PickDetails pickDetails;
    private PickInfo pickInfo;

    /**
     * Default constructor for cloning
     */
    protected DropTargetEvent3D() {
    }

    /**
     * Constructor
     */
    DropTargetEvent3D(DropTargetEvent event) {
        this.dropEvent = event;
    }

    /**
     * Get the drop target event associated with this 3D event
     */
    public DropTargetEvent getDropEvent() {
        return dropEvent;
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
     * Sets the original destination pick details of the event, as calculated by the input system,
     * accounting for grabs.
     */
    public void setPickDetails(PickDetails pickDetails) {
        this.pickDetails = pickDetails;
    }

    /**
     * Returns the node which the pick hit.
     */
    public Node getNode () {
        return ((JMEPickDetails)pickDetails).getReportedNode();
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
     * INTERNAL ONLY.
     * <br>
     * Used by the input system to specify the pickInfo for this input event.
     */
    public void setPickInfo(PickInfo pickInfo) {
        this.pickInfo = pickInfo;
    }

    /**
     * INTERNAL ONLY.
     * <br>
     * Returns the pickInfo for this event. This provides full event information to Wonderland components
     * which need it, such as the World Builder.
     */
    public PickInfo getPickInfo() {
        return pickInfo;
    }

    /**
     * Get the data flavors for this event. Returns the data flavors associated
     * with the event, or null if no data flavors are associated.
     */
    public abstract DataFlavor[] getDataFlavors();

    /**
     * Get data for a particular data flavor. Returns null if no data flavors
     * are associated with the event.
     */
    public abstract Object getTransferData(DataFlavor dataFlavor);

    /**
     * Given a Transferable and a list of data flavors, copy all data into
     * a map.
     */
    static Map<DataFlavor, Object> getData(Transferable transferable,
                                           DataFlavor[] dataFlavors)
    {
        Map<DataFlavor, Object> data = new HashMap<DataFlavor, Object>();
        for (DataFlavor flavor : dataFlavors) {
            // only handle registered flavors
            if (DragAndDropManager.getDragAndDropManager().hasDataFlavorHandler(flavor)) {
                try {
                    data.put(flavor, transferable.getTransferData(flavor));
                } catch (UnsupportedFlavorException ufe) {
                    // ignore
                    LOGGER.log(Level.FINE, "Unsupported flavor: " + flavor, ufe);
                } catch (IOException ioe) {
                    // ignore
                    LOGGER.log(Level.FINE, "Error reading flavor: " + flavor, ioe);
                } catch (InvalidDnDOperationException idoe) {
                    // ingore
                    LOGGER.log(Level.FINE, "Error reading flavor: " + flavor, idoe);
                }
            }
        }

        return data;
    }

    @Override
    public Event clone(Event event) {
        ((DropTargetEvent3D) event).dropEvent = dropEvent;
        ((DropTargetEvent3D) event).pickDetails = pickDetails;
        ((DropTargetEvent3D) event).pickInfo = pickInfo;

        return super.clone(event);
    }
}
