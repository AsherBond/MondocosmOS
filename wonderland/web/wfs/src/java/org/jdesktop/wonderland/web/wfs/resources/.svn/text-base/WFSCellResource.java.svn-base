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
package org.jdesktop.wonderland.web.wfs.resources;

import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.jdesktop.wonderland.tools.wfs.WFS;
import org.jdesktop.wonderland.tools.wfs.WFSCell;
import org.jdesktop.wonderland.tools.wfs.WFSCellDirectory;
import org.jdesktop.wonderland.web.wfs.WFSManager;


/**
 * The WFSCellResource class is a Jersey RESTful resource that allows clients
 * to query for the contents of cell setup information by using a URI that
 * describes the WFS root and the path within the WFS to the cell. Within the
 * URL, the standard WFS naming conventions are not employed (e.g. -wld). 
 * <p>
 * The format of the URI is: /wfs/{wfsname}/{path}/cell, where {wfsname} is
 * the name of the WFS root (as returned by the WFSRootsResource), and {path}
 * is the relative path of the file within the WFS (without any -wld or -wlc.xml
 * suffixes).
 * <p>
 * The cell information returned is the JAXB serialization of the cell setup
 * class information.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@Path(value="/{wfsname:.*}/cell/{path:.*}")
public class WFSCellResource {
    
    /**
     * Returns the JAXB XML serialization of the cell setup class given the
     * name of the root WFS (without the -wfs extension) and the path of the
     * cell within the WFS (without any -wld or -wlc.xml extensions). Returns
     * the XML via an HTTP GET request.
     * 
     * @param wfsName The name of the WFS root (no -wfs extension)
     * @param path The relative path of the file (no -wld, -wlc.xml extensions)
     * @return The XML serialization of the cell setup information via HTTP GET.
     */
    @GET
    @Produces("text/plain")
    public Response getCellResource(@PathParam("wfsname") String wfsName, @PathParam("path") String path) {
        /* Fetch thhe error logger for use in this method */
        Logger logger = WFSManager.getLogger();
        
        /*
         * Fetch the wfs manager and the WFS. If invalid, then return a bad
         * response.
         */
        WFSManager wfsm = WFSManager.getWFSManager();
        WFS wfs = wfsm.getWFS(wfsName);
        if (wfs == null) {
            logger.warning("Unable to find WFS with name " + wfsName);
            ResponseBuilder rb = Response.status(Response.Status.BAD_REQUEST);
            return rb.build();
        }
        
        /* Fetch the root directory, check if null, but should never be */
        WFSCellDirectory dir = wfs.getRootDirectory();
        if (dir == null) {
            logger.warning("WFSManager: Unable to find WFS root with name " + wfsName);
            ResponseBuilder rb = Response.status(Response.Status.BAD_REQUEST);
            return rb.build();
        }
        
        /*
         * Split the path up into individual components. We then fetch the
         * object down the chain. We assume the last element is the file.
         */
        String paths[] = path.split("/");
        for (int i = 0; i < paths.length - 1; i++) {
            /*
             * First fetch the cell. If it does not exist, then return a bad
             * response.
             */
            WFSCell cell = dir.getCellByName(paths[i]);
            if (cell == null) {
                logger.warning("WFSManager: Unable to find cell with path: " + path);
                ResponseBuilder rb = Response.status(Response.Status.BAD_REQUEST);
                return rb.build();
            }
            
            /*
             * Next, get the directory associated with the cell. It also needs
             * to exist, otherwise, return a bad response.
             */
            if ((dir = cell.getCellDirectory()) == null) {
                logger.warning("WFSManager: Unable to find directory with path: " + path);
                ResponseBuilder rb = Response.status(Response.Status.BAD_REQUEST);
                return rb.build();
            }
        }
        
        /*
         * If we have reached here, we have one remaining element in the path
         * and 'dir' holds the directory in which the cell should be.
         */
        WFSCell cell = dir.getCellByName(paths[paths.length - 1]);
        if (cell == null) {
            logger.warning("WFSManager: Unable to find final cell with path: " + path);
            ResponseBuilder rb = Response.status(Response.Status.BAD_REQUEST);
            return rb.build();
        }
        
        /*
         * Load in the cell and stream its setup information to the client.
         */
        try {
            /* Fetch the essential configuration information, check for null */
            String setup = cell.getCellSetup();
            if (setup == null) {
                logger.warning("WFSManager: Unable to find cell setup: " + path);
                ResponseBuilder rb = Response.status(Response.Status.BAD_REQUEST);
                return rb.build();
            }
            else {
                /* Formulate the HTTP response and send the string */
                ResponseBuilder rb = Response.ok(setup);
                return rb.build();
            }
        } catch (java.lang.Exception excp) {
            logger.warning("WFSManager: Unable to read cell with path: " + path + ": " + excp.toString());
            ResponseBuilder rb = Response.status(Response.Status.BAD_REQUEST);
            return rb.build();
        }
    }
}
