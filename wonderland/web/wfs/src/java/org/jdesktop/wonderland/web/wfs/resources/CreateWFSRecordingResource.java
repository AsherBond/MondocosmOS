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
package org.jdesktop.wonderland.web.wfs.resources;

import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.jdesktop.wonderland.common.wfs.WorldRoot;
import org.jdesktop.wonderland.web.wfs.WFSManager;
import org.jdesktop.wonderland.web.wfs.WFSRecording;

/**
 * Handles Jersey RESTful requests to create a wfs "recording"
 * Creates the recording in the pre-determined directory according to the
 * name provided. Returns an XML representation of the WorldRoot class
 * given the unique path of the wfs for later reference.
 * <p>
 * URI: http://<machine>:<port>/wonderland-web-wfs/wfs/create/recording
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Bernard Horan
 */
@Path(value="/create/recording")
public class CreateWFSRecordingResource {

    /**
     * Creates a new recording on the server. Adds a
     * new WFS object and creates the entry on disk. Returns a WorldRoot object
     * that represents the new recording
     * 
     * @param name the name of the recording
     * @return A WorldRoot object
     */
    @GET
    @Produces({"application/xml", "application/json"})
    public Response createWFSRecording(@QueryParam("name") String name) {
        // Do some basic stuff, get the WFS manager class, etc
        Logger logger = Logger.getLogger(CreateWFSRecordingResource.class.getName());
        WFSManager manager = WFSManager.getWFSManager();
        

        // Create the WFS check return value is not null (error if so)
        WFSRecording recording = manager.createWFSRecording(name);
        if (recording == null) {
            logger.warning("[WFS] Unable to create recording " + name);
            ResponseBuilder rb = Response.status(Response.Status.BAD_REQUEST);
            return rb.build();
        }
        
        // Form the root path of the wfs: "recordings/<name>/world-wfs"
        WorldRoot worldRoot = new WorldRoot(recording.getRootPath());
        
        // Formulate the response and return the world root object
        ResponseBuilder rb = Response.ok(worldRoot);
        return rb.build();
    }
}
