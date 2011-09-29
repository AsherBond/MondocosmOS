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

package org.jdesktop.wonderland.modules.portal.client;

import com.jme.bounding.BoundingVolume;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.Cell.RendererType;
import org.jdesktop.wonderland.client.cell.CellComponent;
import org.jdesktop.wonderland.client.cell.ChannelComponent;
import org.jdesktop.wonderland.client.cell.ProximityComponent;
import org.jdesktop.wonderland.client.cell.ProximityListener;
import org.jdesktop.wonderland.client.cell.annotation.UsesCellComponent;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.input.EventClassListener;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.cellrenderer.CellRendererJME;
import org.jdesktop.wonderland.client.jme.input.AvatarCollisionEvent;
import org.jdesktop.wonderland.client.utils.AudioResource;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.modules.portal.common.PortalComponentClientState;

import org.jdesktop.wonderland.client.softphone.SoftphoneControlImpl;

/**
 * Client-side portal component. Moves the client to the specified position
 * when they get within range of the portal.
 * 
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class PortalComponent extends CellComponent
        implements ProximityListener
{
    private static Logger logger =
            Logger.getLogger(PortalComponent.class.getName());

    private String serverURL;
    private Vector3f location;
    private Quaternion look;

    private String audioSource;
    private boolean uploadFile;
    private float volume;

    @UsesCellComponent
    private ProximityComponent prox;
    private CollisionListener collisionListener = null;
    //private AudioResource teleportAudio;

    private URL teleportAudioURL;

    private ChannelComponent channelComp;

    public PortalComponent(Cell cell) {
        super(cell);

	new PortalComponentProperties(true);
    }

    @Override
    public void setClientState(CellComponentClientState clientState) {
        super.setClientState(clientState);

	PortalComponentClientState state = (PortalComponentClientState) clientState;

        serverURL = state.getServerURL();
        
        location = state.getLocation();
        
        look = state.getLook();

	audioSource = state.getAudioSource();
	
	if (audioSource == null || audioSource.length() == 0) {
	    audioSource = PortalComponentProperties.getDefaultAudioSource();
	}

	uploadFile = state.getUploadFile();

	volume = state.getVolume();

	if (audioSource != null) {
	    try {
                teleportAudioURL = 
		    PortalComponent.class.getResource(audioSource);

                //URL url = PortalComponent.class.getResource(audioSource);
                //teleportAudio = new AudioResource(url);
	        //teleportAudio.setVolume(volume);
	    } catch (Exception e) {
		System.out.println("Invalid URL:  " + audioSource
		    + ": " + e.getMessage());
	    }
	}
    }

    @Override
    protected void setStatus(CellStatus status, boolean increasing) {
        super.setStatus(status, increasing);

        // get the activation bounds from the cell we are part of
        BoundingVolume[] bounds = new BoundingVolume[] {
            this.cell.getLocalBounds()
        };

        if (increasing && status == CellStatus.ACTIVE) {
	    channelComp = cell.getComponent(ChannelComponent.class);

            //System.out.println("[PortalComponent] add prox listener: " + bounds[0]);
            //prox.addProximityListener(this, bounds);
        } else if (!increasing && status == CellStatus.INACTIVE) {
            //System.out.println("[PortalComponent] remove prox listener");
            //prox.removeProximityListener(this);
        } else if (status==CellStatus.VISIBLE) {
            if (increasing) {
                collisionListener = new CollisionListener();
                Entity ent = ((CellRendererJME)cell.getCellRenderer(RendererType.RENDERER_JME)).getEntity();
                ClientContextJME.getInputManager().addEventListener(collisionListener, ent);
            } else {
                ClientContextJME.getInputManager().removeEventListener(collisionListener, ((CellRendererJME)cell.getCellRenderer(RendererType.RENDERER_JME)).getEntity());
            }
        }
    }

    public void viewEnterExit(boolean entered, Cell cell, CellID viewCellID,
                              BoundingVolume proximityVolume, int proximityIndex)
    {
        System.out.println("[PortalComponent] trigger!");
        teleport();
    }

    private void teleport() {
        // teleport in a separate thread, since we don't know which one we
        // are called on
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    // teleport!
                    //teleportAudio.play();

		    System.out.println("GOTO LOCATION " + serverURL + " " + location);

                    ClientContextJME.getClientMain().gotoLocation(serverURL, location, look);

		    //URL url = PortalComponent.class.getResource(
		    //	"resources/" + "Teleport.au")

                    logger.warning("[PortalComponent] going to " + serverURL +
                                       " at " + location + ", " + look);

		    SoftphoneControlImpl.getInstance().sendCommandToSoftphone(
		    	"playFile=" + audioSource + "=" + volume);
                } catch (IOException ex) {
                    logger.log(Level.WARNING, "Error teleporting", ex);
                }
            }
        }, "Teleporter");
        t.start();
    }

    class CollisionListener extends EventClassListener {
        @Override
        public Class[] eventClassesToConsume() {
            return new Class[]{AvatarCollisionEvent.class};
        }

        @Override
        public void commitEvent(Event event) {
            teleport();
        }
    }
}
