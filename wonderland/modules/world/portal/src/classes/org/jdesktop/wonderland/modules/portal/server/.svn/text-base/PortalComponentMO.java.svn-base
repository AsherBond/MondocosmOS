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

package org.jdesktop.wonderland.modules.portal.server;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.modules.portal.common.PortalComponentClientState;
import org.jdesktop.wonderland.modules.portal.common.PortalComponentServerState;
import org.jdesktop.wonderland.modules.portal.common.PortalComponentServerState.AudioSourceType;
import org.jdesktop.wonderland.server.cell.CellComponentMO;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;

/**
 * A sample cell component
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class PortalComponentMO extends CellComponentMO {

    private static Logger logger = Logger.getLogger(PortalComponentMO.class.getName());
    
    private String serverURL;
    private Vector3f location;
    private Quaternion look;

    private AudioSourceType audioSourceType;
    private String audioSource;
    private boolean uploadFile;
    private String cachedAudioSource;
    private float volume;

    public PortalComponentMO(CellMO cell) {
        super(cell);
    }

    @Override
    protected String getClientClass() {
        return "org.jdesktop.wonderland.modules.portal.client.PortalComponent";
    }

    @Override
    public CellComponentClientState getClientState(CellComponentClientState state, WonderlandClientID clientID, ClientCapabilities capabilities) {
        if (state == null) {
            state = new PortalComponentClientState();
        }

	PortalComponentClientState clientState = (PortalComponentClientState) state;

        clientState.setServerURL(serverURL);
        clientState.setLocation(location);
        clientState.setLook(look);
	clientState.setAudioSourceType(audioSourceType);
	clientState.setAudioSource(audioSource);
	clientState.setUploadFile(uploadFile);
	clientState.setCachedAudioSource(cachedAudioSource);
	clientState.setVolume(volume);

        return super.getClientState(state, clientID, capabilities);
    }

    @Override
    public CellComponentServerState getServerState(CellComponentServerState state) {
        if (state == null) {
            state = new PortalComponentServerState();
        }

	PortalComponentServerState serverState = (PortalComponentServerState) state;

        serverState.setServerURL(serverURL);
        serverState.setLocation(location);
        serverState.setLook(look);
	serverState.setAudioSourceType(audioSourceType);
	serverState.setAudioSource(audioSource);
	serverState.setUploadFile(uploadFile);
	serverState.setCachedAudioSource(cachedAudioSource);
	serverState.setVolume(volume);

        return super.getServerState(state);
    }

    @Override
    public void setServerState(CellComponentServerState state) {
        super.setServerState(state);

	PortalComponentServerState serverState = (PortalComponentServerState) state;

        serverURL = serverState.getServerURL();
        location = serverState.getLocation();
        look = serverState.getLook();
	audioSourceType = serverState.getAudioSourceType();
	audioSource = serverState.getAudioSource();
	uploadFile = serverState.getUploadFile();
	cachedAudioSource = serverState.getCachedAudioSource();
	volume = serverState.getVolume();
    }

}
