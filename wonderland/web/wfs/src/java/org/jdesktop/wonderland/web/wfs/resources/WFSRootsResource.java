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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.jdesktop.wonderland.web.wfs.WFSManager;
import org.jdesktop.wonderland.common.wfs.WorldRootList;
import org.jdesktop.wonderland.web.wfs.WFSRoot;


/**
 * The WFSRootsResource class is a Jersey RESTful resource that allows clients
 * to query for the WFS root names by using a URI. 
 * <p>
 * The format of the URI is: /wfs/roots/directory.
 * <p>
 * The root information returned is the JAXB serialization of the root name
 * information (the WFSRoots class). The getCellResource() method handles the
 * HTTP GET request
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@Path(value="/roots")
public class WFSRootsResource {
    
    /**
     * Returns the JAXB XML serialization of the WFS root names. Returns
     * the XML via an HTTP GET request. The format of the URI is:
     * <p>
     * /wfs/roots/directory
     * <p>
     * Returns BAD_REQUEST to the HTTP connection upon error
     *
     * @return The XML serialization of the cell setup information via HTTP GET
     */
    @GET
    @Produces("application/xml")
    public Response getCellResource() {
        /* Fetch thhe error logger for use in this method */
        Logger logger = WFSManager.getLogger();
        
        /*
         * Fetch the wfs manager and the individual root names. If the roots
         * is null, then return a blank response.
         */
        WFSManager wfsm = WFSManager.getWFSManager();
        List<WFSRoot> rootList = wfsm.getWFSRoots();
        List<String> rootNames = new ArrayList<String>(rootList.size());
        for (WFSRoot root : rootList) {
            rootNames.add(root.getRootPath());
        }
        WorldRootList wfsRoots = new WorldRootList(rootNames.toArray(new String[0]));
        
        /* Send the serialized cell names to the client */
        ResponseBuilder rb = Response.ok(wfsRoots);
        return rb.build();
    }
}
