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
package org.jdesktop.wonderland.modules.phone.server.cell;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;

import java.util.logging.Logger;

import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.state.CellClientState;
import org.jdesktop.wonderland.common.cell.state.CellServerState;

import org.jdesktop.wonderland.common.security.annotation.Actions;

import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.MovableComponentMO;

import com.jme.bounding.BoundingBox;
import com.jme.math.Vector3f;

import org.jdesktop.wonderland.server.comms.WonderlandClientID;

import org.jdesktop.wonderland.modules.phone.common.PhoneCellClientState;
import org.jdesktop.wonderland.modules.phone.common.PhoneCellServerState;
import org.jdesktop.wonderland.modules.phone.common.PhoneInfo;

import org.jdesktop.wonderland.modules.phone.common.cell.security.UnlockPhoneAction;

/**
 * A server cell that provides conference phone functionality
 * @author jprovino
 */
@Actions(UnlockPhoneAction.class)
public class PhoneCellMO extends CellMO {

    private static final Logger logger =
            Logger.getLogger(PhoneCellMO.class.getName());
    private String modelFileName;
    private final static double PRIVATE_DAMPING_COEFFICIENT = 0.5;
    private PhoneInfo phoneInfo;
    private ManagedReference<PhoneMessageHandler> phoneMessageHandlerRef;

    private ManagedReference<IncomingCallHandler> incomingCallHandlerRef;

    public PhoneCellMO() {
    }

    public PhoneCellMO(Vector3f center, float size) {
        super(new BoundingBox(new Vector3f(), size, size, size),
                new CellTransform(null, center));
    }

    protected void setLive(boolean live) {
        super.setLive(live);

        if (live == false) {
            if (phoneMessageHandlerRef != null) {
                PhoneMessageHandler phoneMessageHandler = phoneMessageHandlerRef.get();
                phoneMessageHandler.done();
                AppContext.getDataManager().removeObject(phoneMessageHandler);
		IncomingCallHandler incomingCallHandler = incomingCallHandlerRef.get();
		incomingCallHandler.done();
                AppContext.getDataManager().removeObject(incomingCallHandler);
                phoneMessageHandlerRef = null;
            }
            return;
        }

	IncomingCallHandler incomingCallHandler = IncomingCallHandler.getInstance();

	incomingCallHandler.addPhone(getCellID(), phoneInfo);

	incomingCallHandlerRef = AppContext.getDataManager().createReference(
	    incomingCallHandler);

        phoneMessageHandlerRef = AppContext.getDataManager().createReference(
            new PhoneMessageHandler(this));
    }

    @Override
    protected String getClientCellClassName(WonderlandClientID clientID,
            ClientCapabilities capabilities) {

        return "org.jdesktop.wonderland.modules.phone.client.cell.PhoneCell";
    }

    @Override
    public CellClientState getClientState(CellClientState cellClientState, WonderlandClientID clientID,
            ClientCapabilities capabilities) {

        if (cellClientState == null) {
            cellClientState = new PhoneCellClientState();
        }

        ((PhoneCellClientState) cellClientState).setPhoneInfo(phoneInfo);

        return super.getClientState(cellClientState, clientID, capabilities);
    }

    @Override
    public void setServerState(CellServerState cellServerState) {
        super.setServerState(cellServerState);

        PhoneCellServerState phoneCellServerState = (PhoneCellServerState) cellServerState;

        phoneInfo = phoneCellServerState.getPhoneInfo();
    }

    /**
     * Return a new CellServerState object that represents the current
     * state of the cell.
     *
     * @return CellServerState representing the current state
     */
    @Override
    public CellServerState getServerState(CellServerState cellServerState) {
        /* Create a new BasicCellState and populate its members */
        if (cellServerState == null) {
            cellServerState = new PhoneCellServerState();

            ((PhoneCellServerState) cellServerState).setPhoneInfo(phoneInfo);
        }
        return super.getServerState(cellServerState);
    }

    public PhoneInfo getPhoneInfo() {
        return phoneInfo;
    }

    public void setPhoneInfo(PhoneInfo phoneInfo) {
        this.phoneInfo = phoneInfo;
    }

}
