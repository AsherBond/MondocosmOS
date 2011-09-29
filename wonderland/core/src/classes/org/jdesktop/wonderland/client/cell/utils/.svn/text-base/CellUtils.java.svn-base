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
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., All Rights Reserved
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

import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.cell.CellEditChannelConnection;
import org.jdesktop.wonderland.client.cell.view.ViewCell;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.jme.ViewManager;
import org.jdesktop.wonderland.common.cell.CellEditConnectionType;
import org.jdesktop.wonderland.common.cell.messages.CellCreateMessage;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.cell.state.PositionComponentServerState;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellManager;
import org.jdesktop.wonderland.client.jme.utils.ScenegraphUtils;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.messages.CellDeleteMessage;
import org.jdesktop.wonderland.common.cell.state.BoundingVolumeHint;
import org.jdesktop.wonderland.common.cell.state.ViewComponentServerState;

/**
 * A collection of useful utility routines pertaining to Cells.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class CellUtils {

    private static Logger logger = Logger.getLogger(CellUtils.class.getName());

    /** The default bounds radius to use if no hint is given */
    private static final float DEFAULT_RADIUS = 1.0f;

    /**
     * Creates a cell in the world given the CellServerState of the cell. If the
     * given CellServerState is null, this method simply does not create a Cell.
     * This method attempts to position the Cell "optimally" so that the avatar
     * can see it, based upon "hints" about the Cell bounds given to it in the
     * CellServerState.
     *
     * @param state The cell server state for the new cell
     * @throw CellCreationException Upon error creating the cell
     */
    public static void createCell(CellServerState state)
            throws CellCreationException {

        // Find the parent cell for this creation (may be null)
        CellID parentID = null;
        Cell parent = CellCreationParentRegistry.getCellCreationParent();
        if (parent != null) {
            parentID = parent.getCellID();
            logger.info("Using parent with Cell ID " + parentID.toString());
        }

        // Go ahead and create the Cell with the parent ID we found
        createCell(state, parentID);
    }

    /**
     * Creates a cell in the world given the CellServerState of the cell. If the
     * given CellServerState is null, this method simply does not create a Cell.
     * This method attempts to position the Cell "optimally" so that the avatar
     * can see it, based upon "hints" about the Cell bounds given to it in the
     * CellServerState.
     * 
     * @param state The cell server state for the new cell
     * @param parentCellID The Cell ID of the parent, of null for world root
     * @throw CellCreationException Upon error creating the cell
     */
    public static void createCell(CellServerState state, CellID parentCellID)
            throws CellCreationException {

        // Check to see if the Cell server state is null, and fail quietly if
        // so
        if (state == null) {
            logger.fine("Creating cell with null server state. Returning.");
            return;
        }

        // Fetch the transform of the view (avatar) Cell and its "look at"
        // direction.
        ViewManager vm = ViewManager.getViewManager();
        ViewCell viewCell = vm.getPrimaryViewCell();
        CellTransform viewTransform = viewCell.getWorldTransform();
        ServerSessionManager manager =
                viewCell.getCellCache().getSession().getSessionManager();

        // The Cell Transform to apply to the Cell
        CellTransform transform = null;

        // Look for the "bounds hint" provided by the Cell. There are three
        // possible cases:
        //
        // (1) There is a hint and the Cell wants us to do the optimal layout
        // so go ahead and do it.
        //
        // (2) There is no hint, so use the default bounds radius and do the
        // optimal layout
        //
        // (3) There is a hint that says do not do the optimal layout, so we
        // will just put the Cell right on top of the avatar.
        BoundingVolumeHint hint = state.getBoundingVolumeHint();

        logger.info("Using bounding volume hint " + hint.getBoundsHint() +
                ", do placement=" + hint.isDoSystemPlacement());
        
        if (hint != null && hint.isDoSystemPlacement() == true) {
            // Case (1): We have a bounds hint and we want to do the layout,
            // so we find the distance away from the avatar and also the height
            // above the ground.
            BoundingVolume boundsHint = hint.getBoundsHint();
            transform = CellPlacementUtils.getCellTransform(manager, boundsHint,
                    viewTransform);
        }
        else if (hint == null) {
            // Case (2): Do the optimal placement using the default radius.
            BoundingVolume boundsHint = new BoundingSphere(DEFAULT_RADIUS, Vector3f.ZERO);
            transform = CellPlacementUtils.getCellTransform(manager, boundsHint,
                    viewTransform);
        }
        else if (hint != null && hint.isDoSystemPlacement() == false) {
            // Case (3): The Cell will take care of its own placement, use
            // the origin of the avatar as the initial placement.
            
            // Issue 998: make sure this is actually the current location of
            // the avatar, and not the origin.  This guarantees that the
            // cell will be in the viewcache of the creator at least, so
            // that the cell object can be (for example) positioned manually
            // by the client.
            transform = viewTransform;
        }
        
        // We also need to convert the initial origin of the Cell (in world
        // coordinates to the coordinates of the parent Cell (if non-null)
        if (parentCellID != null) {
            Cell parent = viewCell.getCellCache().getCell(parentCellID);
            CellTransform worldTransform = new CellTransform(null, null);
            CellTransform parentTransform = parent.getWorldTransform();

            logger.info("Transform of the parent cell: translation=" +
                    parentTransform.getTranslation(null).toString() + ", rotation=" +
                    parentTransform.getRotation(null).toString());

            transform = ScenegraphUtils.computeChildTransform(parentTransform,
                                                              transform);
        }
        
        logger.info("Final adjusted origin " + transform.getTranslation(null).toString());
        
        // Create a position component that will set the initial origin
        PositionComponentServerState position = (PositionComponentServerState)
                state.getComponentServerState(PositionComponentServerState.class);
        if (position == null) {
            position = new PositionComponentServerState();
            state.addComponentServerState(position);
        }
        position.setTranslation(transform.getTranslation(null));
        position.setRotation(transform.getRotation(null));
        position.setScaling(transform.getScaling(null));

        // Always pass in the view transform to the Cell's server state
        state.addComponentServerState(new ViewComponentServerState(viewTransform));

        // Send the message to the server
        WonderlandSession session = manager.getPrimarySession();
        CellEditChannelConnection connection = (CellEditChannelConnection)
                session.getConnection(CellEditConnectionType.CLIENT_TYPE);
        CellCreateMessage msg = new CellCreateMessage(parentCellID, state);
        connection.send(msg);
    }

    /**
     * Requests the server to delete the given cell from the world
     * of the primary session. Returns true if the deletion succeeds.
     * <br><br>
     * Note: currently always returns true because the server doesn't send 
     * any response to the cell delete message.
     */
    public static boolean deleteCell (Cell cell) {

        WonderlandSession session = cell.getCellCache().getSession();
        CellEditChannelConnection connection = (CellEditChannelConnection)
            session.getConnection(CellEditConnectionType.CLIENT_TYPE);
        CellDeleteMessage msg = new CellDeleteMessage(cell.getCellID());
        connection.send(msg);

        // TODO: there really really should be an OK/Error response from the server!

        return true;
    }
}
