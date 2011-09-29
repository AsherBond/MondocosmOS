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
package org.jdesktop.wonderland.modules.appbase.server.cell;

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.modules.appbase.common.cell.App2DCellClientState;
import org.jdesktop.wonderland.modules.appbase.common.cell.App2DCellServerState;
import com.jme.bounding.BoundingBox;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.state.ViewComponentServerState;
import org.jdesktop.wonderland.modules.appbase.common.cell.App2DCellPerformFirstMoveMessage;
import org.jdesktop.wonderland.server.cell.MovableComponentMO;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.cell.AbstractComponentMessageReceiver;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import com.sun.sgs.app.ManagedReference;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.annotation.UsesCellComponentMO;
import org.jdesktop.wonderland.server.cell.ChannelComponentMO;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.jdesktop.wonderland.common.messages.ErrorMessage;
import org.jdesktop.wonderland.common.cell.state.CellClientState;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;

/**
 * An abstract server-side app.base cell for 2D apps. 
 * Intended to be subclassed by server-side 2D app cells.
 *
 * @author deronj
 */
@ExperimentalAPI
public abstract class App2DCellMO extends AppCellMO {

    private static Logger logger = Logger.getLogger(App2DCellMO.class.getName());

    /** The pixel scale. */
    protected Vector2f pixelScale;

    /** The view transform of the cell creator. */
    private CellTransform creatorViewTransform;

    /** Whether the initial placement of the cell has been completed. */
    private boolean initialPlacementDone;

    @UsesCellComponentMO(ChannelComponentMO.class)
    protected ManagedReference<ChannelComponentMO> channelRef;

    /** Create an instance of App2DCellMO. */
    public App2DCellMO() {
        // Unfortunately, the bounds cannot be modified later, so we need to leave
        // enough space for a fairly large window. A window can easily be 1K x 1K,
        // 4K x 4K is the max, so 2K x 2K seems like a reasonable number. Also 
        // unfortunately, we don't know the pixel scale at this point so, out of 
        // desparation, we choose the default value of 0.01 meters per pixel.
        // This gives values of approx. A radius of 10 x 10 for the local width and height
        // of the bounds. 5 meters (in front) should be reasonable for the depth because the 
        // step per window stack level is only 0.01 meter and the stack never gets too large.
        super(new BoundingBox(new Vector3f(), 10, 10, 5), new CellTransform(null, null));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setServerState(CellServerState state) {
        super.setServerState(state);
        App2DCellServerState serverState = (App2DCellServerState) state;
        pixelScale = new Vector2f(serverState.getPixelScaleX(), serverState.getPixelScaleY());

        ViewComponentServerState vcss =
                (ViewComponentServerState) serverState.getComponentServerState(ViewComponentServerState.class);
        logger.info("vcss = " + vcss);
        if (vcss != null) {
	    creatorViewTransform = vcss.getCellTransform();
        }
	logger.info("Cell creator view transform = " + creatorViewTransform);
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    protected CellClientState getClientState(CellClientState cellClientState,
                                             WonderlandClientID clientID, ClientCapabilities capabilities) {
        if (cellClientState == null) {
            cellClientState = new App2DCellClientState();
        }
        populateClientState((App2DCellClientState) cellClientState);
        return super.getClientState(cellClientState, clientID, capabilities);
    }

    /**
     * Fill in the given client state with the cell server state.
     */
    private void populateClientState(App2DCellClientState clientState) {
        clientState.setPixelScale(pixelScale);
        clientState.setCreatorViewTransform(creatorViewTransform);
        clientState.setInitialPlacementDone(initialPlacementDone);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CellServerState getServerState(CellServerState stateToFill) {
        if (stateToFill == null) {
            return null;
        }

        App2DCellServerState state = (App2DCellServerState) stateToFill;
        state.setPixelScaleX(pixelScale.getX());
        state.setPixelScaleY(pixelScale.getY());

        return super.getServerState(stateToFill);
    }

    void setInitialPlacementDone (boolean done) {
        initialPlacementDone = done;
    }

    boolean isInitialPlacementDone () {
        return initialPlacementDone;
    }

    @Override
    protected void setLive(boolean live) {
        if (isLive()==live)
            return;

        super.setLive(live);

        if (live) {
            App2DCellMessageReceiver receiver = new App2DCellMessageReceiver(this);
            channelRef.get().addMessageReceiver(App2DCellPerformFirstMoveMessage.class, receiver);
        } else {
            channelRef.get().removeMessageReceiver(App2DCellPerformFirstMoveMessage.class);
        }
    }

    private void handlePerformFirstMove (WonderlandClientID clientID, 
                                         App2DCellPerformFirstMoveMessage msg) {

        // Only the first message moves the cell; ignore all subsequent
        if (isInitialPlacementDone()) return;

        // Make sure the cell has a movable component
        MovableComponentMO mc = getComponent(MovableComponentMO.class);
        if (mc == null) {
            mc = new MovableComponentMO(this);
            addComponent(mc);
        }

        // Request the move
        logger.info("Perform first move for cell = " + cellID);
        logger.info("Perform first move to transform = " + msg.getCellTransform());
        mc.moveRequest(null, msg.getCellTransform());

        setInitialPlacementDone(true);
    }

    private static class App2DCellMessageReceiver extends AbstractComponentMessageReceiver {
        public App2DCellMessageReceiver(CellMO cellMO) {
            super (cellMO);
        }

        @Override
        public void messageReceived(WonderlandClientSender sender,
                                    WonderlandClientID clientID,
                                    CellMessage message)
        {
            App2DCellMO cellMO = (App2DCellMO) getCell();

            if (message instanceof App2DCellPerformFirstMoveMessage) {
                cellMO.handlePerformFirstMove(clientID, (App2DCellPerformFirstMoveMessage)message);
            } else {
                logger.log(Level.WARNING, "Unexpected message type " +
                           message.getClass());
                sender.send(clientID, new ErrorMessage(message.getMessageID(),
                            "Unexpected message type: " + message.getClass()));
            }
        }
    }
}
