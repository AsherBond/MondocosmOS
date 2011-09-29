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

import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.jdesktop.wonderland.common.wfs.WFSRecordingList;
import org.jdesktop.wonderland.web.wfs.WFSManager;
import org.jdesktop.wonderland.web.wfs.WFSRecording;

/**
 * The WFSRecordingsResource class is a Jersey RESTful resource that allows clients
 * to query for the WFS recording names by using a URI.
 * <p>
 * The format of the URI is: http://<machine>:<port>/wonderland-web-wfs/wfs/listrecordings.
 * <p>
 * The recordings information returned is the JAXB serialization of the recording name
 * information (the WFSRecordingList class). The getRecordings() method handles the
 * HTTP GET request
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Bernard Horan
 */
@Path(value = "/listrecordings")
public class WFSRecordingsResource {

    /**
     * Returns the JAXB XML serialization of the WFS recording names. Returns
     * the XML via an HTTP GET request. The format of the URI is:
     * <p>
     * /wfs/listrecordings
     * <p>
     * Returns BAD_REQUEST to the HTTP connection upon error
     *
     * @return The XML serialization of the wfs recordings via HTTP GET
     */
    @GET
    @Produces({"application/xml", "application/json"})
    public Response getRecordings() {
        /*
         * Fetch the wfs manager and the individual recording names. If the recordings
         * is null, then return a blank response.
         */
        WFSManager wfsm = WFSManager.getWFSManager();
        List<WFSRecording> recordingList = wfsm.getWFSRecordings();
        List<String> recordingNames = new ArrayList<String>(recordingList.size());
        for (WFSRecording recording : recordingList) {
            recordingNames.add(recording.getName());
        }
        WFSRecordingList wfsRecordings = new WFSRecordingList(recordingNames.toArray(new String[0]));

        /* Send the serialized recording names to the client */
        return Response.ok(wfsRecordings).build();

    }
}
