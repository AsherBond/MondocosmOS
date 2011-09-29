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
package org.jdesktop.wonderland.modules.orb.server.cell;

import java.util.logging.Level;
import org.jdesktop.wonderland.modules.orb.common.messages.OrbAttachVirtualPlayerMessage;
import org.jdesktop.wonderland.modules.orb.common.messages.OrbChangeNameMessage;
import org.jdesktop.wonderland.modules.orb.common.messages.OrbBystandersMessage;

import java.util.Set;

import java.util.logging.Logger;

import org.jdesktop.wonderland.common.cell.CellChannelConnectionType;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.state.CellClientState;
import org.jdesktop.wonderland.common.cell.state.CellServerState;

import org.jdesktop.wonderland.server.WonderlandContext;

import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.CellComponentMO;
import org.jdesktop.wonderland.server.cell.MovableComponentMO;
import org.jdesktop.wonderland.server.cell.TransformChangeListenerSrv;

import org.jdesktop.wonderland.server.cell.ViewCellCacheMO;
import org.jdesktop.wonderland.server.cell.view.ViewCellMO;

//import org.jdesktop.wonderland.server.cell.annotation.UsesCellComponentMO;

import com.jme.bounding.BoundingSphere;

import com.jme.math.Vector3f;

import org.jdesktop.wonderland.server.comms.CommsManager;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;

import org.jdesktop.wonderland.modules.orb.common.OrbCellClientState;
import org.jdesktop.wonderland.modules.orb.common.OrbCellServerState;

//import org.jdesktop.wonderland.modules.audiomanager.server.AudioParticipantComponentMO;

import com.sun.mpk20.voicelib.app.Player;
import com.sun.mpk20.voicelib.app.VirtualPlayer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.List;
import org.jdesktop.wonderland.common.auth.WonderlandIdentity;
import org.jdesktop.wonderland.server.cell.annotation.NoSnapshot;

/**
 * A server cell that provides Orb functionality
 * @author jprovino
 */
@NoSnapshot
public class OrbCellMO extends ViewCellMO {

    private static final Logger logger =
            Logger.getLogger(OrbCellMO.class.getName());
    private ManagedReference<OrbMessageHandler> orbMessageHandlerRef;
    private String username;
    private String callID;
    private boolean simulateCalls;
    private VirtualPlayer vp;
    private String[] bystanders;

    private ManagedReference<OrbCellCacheMO> orbCellCacheRef;

//    @UsesCellComponentMO(AudioParticipantComponentMO.class)
//    private ManagedReference<AudioParticipantComponentMO> compRef;

    public OrbCellMO() {
	initialize();
    }

    public OrbCellMO(Vector3f center, float size, String username, 
	    String callID, boolean simulateCalls) {

	this(center, size, username, callID, simulateCalls, null, new String[0]);
    }

    public OrbCellMO(Vector3f center, float size, String username, 
	    String callID, boolean simulateCalls, VirtualPlayer vp,
	    String[] bystanders) {

	super(new BoundingSphere(size, center), new CellTransform(null, center));

	this.username = username;
        this.callID = callID;
        this.simulateCalls = simulateCalls;
	this.vp = vp;
	this.bystanders = bystanders;

        addComponent(new MovableComponentMO(this));
        addTransformChangeListener(new TransformChangeListener());
    }

    private void initialize() {
        addComponent(new MovableComponentMO(this));

	try {
	    Class audioParticipantClass = 
		Class.forName("org.jdesktop.wonderland.modules.audiomanager.server.AudioParticipantComponentMO");

	    Constructor[] cArray = audioParticipantClass.getConstructors();

	    addComponent((CellComponentMO) cArray[0].newInstance(this));
	} catch (InstantiationException ex) {
            logger.log(Level.WARNING, null, ex);
        } catch (IllegalAccessException ex) {
            logger.log(Level.WARNING, null, ex);
        } catch (IllegalArgumentException ex) {
            logger.log(Level.WARNING, null, ex);
        } catch (InvocationTargetException ex) {
            // issue #527: rethrow nested runtime exceptions
            if (ex.getCause() != null && ex.getCause() instanceof RuntimeException) {
                throw (RuntimeException) ex.getCause();
            }

            logger.log(Level.WARNING, null, ex);
        } catch (ClassNotFoundException ex) {
            logger.log(Level.WARNING, null, ex);
        }
    }

    @Override
    protected void setLive(boolean live) {
        super.setLive(live);

        if (live == false) {
            if (orbMessageHandlerRef != null) {
                OrbMessageHandler orbMessageHandler = orbMessageHandlerRef.get();

                AppContext.getDataManager().removeObject(orbMessageHandler);

                orbMessageHandlerRef = null;

                orbMessageHandler.done();
            }

	    if (orbCellCacheRef != null) {
	        orbCellCacheRef.get().logout();
	        orbCellCacheRef = null;
	    }
            return;
        }

        orbMessageHandlerRef = AppContext.getDataManager().createReference(
             new OrbMessageHandler(this, username, callID, simulateCalls));
    }

    @Override
    protected String getClientCellClassName(WonderlandClientID clientID,
            ClientCapabilities capabilities) {

        return "org.jdesktop.wonderland.modules.orb.client.cell.OrbCell";
    }

    @Override
    public CellClientState getClientState(CellClientState cellClientState, 
	    WonderlandClientID clientID, ClientCapabilities capabilities) {

        if (cellClientState == null) {
            WonderlandIdentity id = new WonderlandIdentity(username, null, null);
            cellClientState = new OrbCellClientState(id, username, callID,
		getPlayerWithVpCallID(), bystanders);
        }

        return super.getClientState(cellClientState, clientID, capabilities);
    }

    @Override
    public void setServerState(CellServerState cellServerState) {
        super.setServerState(cellServerState);

        OrbCellServerState orbCellServerState = (OrbCellServerState) cellServerState;
    }

    /**
     * Return a new CellServerState Java bean class that represents the current
     * state of the cell.
     *
     * @return a JavaBean representing the current state
     */
    @Override
    public CellServerState getServerState(CellServerState cellServerState) {
        /* Create a new BasicCellState and populate its members */
        if (cellServerState == null) {
            cellServerState = new OrbCellServerState(username, username, callID, 
		getPlayerWithVpCallID(), bystanders);
        }

        return super.getServerState(cellServerState);
    }

    public void setUsername(String username) {
	this.username = username;

	orbMessageHandlerRef.get().setUsername(username);

	CommsManager cm = WonderlandContext.getCommsManager();

        WonderlandClientSender sender = cm.getSender(CellChannelConnectionType.CLIENT_TYPE);

	sender.send(new OrbChangeNameMessage(cellID, username));
    }

    public String getUsername() {
    	return username;
    }
   
    public void setBystanders(String[] bystanders) {
	CommsManager cm = WonderlandContext.getCommsManager();

        WonderlandClientSender sender = cm.getSender(CellChannelConnectionType.CLIENT_TYPE);

	sender.send(new OrbBystandersMessage(cellID, bystanders));
    }

    public String getCallID() {
	return callID;
    }

    public String getPlayerWithVpCallID() {
	if (vp == null) {
	    return "";
	}

	return vp.playerWithVirtualPlayer.getId();
    }

    public void attach(String callID) {
	CommsManager cm = WonderlandContext.getCommsManager();

        WonderlandClientSender sender = cm.getSender(CellChannelConnectionType.CLIENT_TYPE);

        sender.send(new OrbAttachVirtualPlayerMessage(cellID, callID));
    }

    public void endCall() {
	if (orbMessageHandlerRef != null) {
	    orbMessageHandlerRef.get().done();
	}
    }

    @Override
    public ViewCellCacheMO getCellCache() {
        return orbCellCacheRef.get();
    }

    Set<CellID> getLoadedCells() {
        return orbCellCacheRef.get().getLoadedCells();
    }

    private ManagedReference<OrbCellCacheMO> getOrbCellCacheRef() {
        return orbCellCacheRef;
    }

    private void setCellCache(OrbCellCacheMO cache) {
	orbCellCacheRef = AppContext.getDataManager().createReference(cache);
    	cache.login();
    }

    private static class TransformChangeListener implements TransformChangeListenerSrv {

        public void transformChanged(ManagedReference<CellMO> cellRef, CellTransform localTransform, 
	    CellTransform worldTransform) {

            //It's at this stage that we know we're in the world and located at a certain position
            //logger.info("worldTransform: " + getWorldTransform(null));
            //Create the cell cache and set it up
            OrbCellMO cellMO = (OrbCellMO) cellRef.get();

            if (cellMO.getOrbCellCacheRef() == null) {
		cellMO.setCellCache(new OrbCellCacheMO(cellMO));
                //We don't need to listen any longer
                cellMO.removeTransformChangeListener(this);
            } else {
                //we shouldn't have reached here
                logger.severe("Failed to remove change listener");
            }
        }
   
    } 

}
