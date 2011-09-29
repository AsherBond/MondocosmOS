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

import java.util.LinkedList;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.jdesktop.wonderland.web.wfs.WFSManager;
import org.jdesktop.wonderland.tools.wfs.WFS;
import org.jdesktop.wonderland.tools.wfs.WFSCell;
import org.jdesktop.wonderland.tools.wfs.WFSCellDirectory;
import org.jdesktop.wonderland.common.wfs.CellList;
import org.jdesktop.wonderland.common.wfs.CellList.Cell;

/**
 * The WFSDirectoryResource class is a Jersey RESTful resource that allows clients
 * to query for the children of a cell by using a URI that describes the WFS
 * root and the path within the WFS to the cell. Within the URL, the standard
 * WFS naming conventions are not employed (e.g. -wld). 
 * <p>
 * The format of the URI is: /wfs/{wfsname}/{path}/directory, where {wfsname} is
 * the name of the WFS root (as returned by the WFSRootsResource), and {path}
 * is the relative path of the file within the WFS (without any -wld or -wlc.xml
 * suffixes).
 * <p>
 * The cell information returned is the JAXB serialization of the cell directory
 * information (the WFSCellChildren class).
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@Path(value="/{wfsname:.*}/directory/{path:.*}")
public class WFSDirectoryResource {

    // The error logger
    private static final Logger LOGGER =
            Logger.getLogger(WFSDirectoryResource.class.getName());

    /**
     * Returns the JAXB XML serialization of the cell directory given the
     * name of the root WFS (without the -wfs extension) and the path of the
     * cell within the WFS (without any -wld or -wlc.xml extensions). Returns
     * the XML via an HTTP GET request.
     * 
     * @param wfsName The name of the WFS root (no -wfs extension)
     * @param path The relative path of the file (no -wld, -wlc.xml extensions)
     * @return The XML serialization of the cell setup information via HTTP GET.
     */
    @GET
    @Produces({"application/xml", "application/json"})
    public Response getCellResource(@PathParam("wfsname") String wfsName,
            @PathParam("path") String path) {

        LOGGER.fine("Looking in WFS " + wfsName + " for path " + path);

        // Fetch the wfs manager and the WFS. If invalid, then return a bad
        // response.
        WFS wfs = WFSManager.getWFSManager().getWFS(wfsName);
        if (wfs == null) {
            LOGGER.warning("Unable to find WFS with name " + wfsName);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        // Fetch the root directory, check if null, but should never be
        WFSCellDirectory dir = wfs.getRootDirectory();
        if (dir == null) {
            LOGGER.warning("Unable to find WFS root with name " + wfsName);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        // Split the path up into individual components. We then fetch the
        // objects down the chain. As a special case if path is an empty string,
        // then set paths[] to an empty array.
        String paths[] = new String[0];
        if (path.compareTo("") != 0) {
            paths = path.split("/");
        }
        
        // Loop through each component and find the subdirectory in turn.
        for (int i = 0; i < paths.length; i++) {

            // First fetch the cell. If it does not exist, then return a bad
            // response. This is an error since the path should never contain
            // a Cell that does not exist.
            WFSCell cell = dir.getCellByName(paths[i]);
            if (cell == null) {
                LOGGER.warning("Unable to find WFS Cell with path: " + path);
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            
            // Next, get the directory associated with the cell. All elements
            // the path should have a Cell directory, except perhaps for the
            // last element.
            dir = cell.getCellDirectory();
            if (dir == null && i < paths.length - 1) {
                // Some interior path does not have a Cell directory. This is
                // an error.
                LOGGER.warning("Unable to find WFS Cell directory with path: " +
                        path);
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            else if (dir == null) {
                // Otherwise, if the final path does not have a Cell directory,
                // this is ok. It just means that Cell has no children, so we
                // return an empty list.
                LOGGER.fine("Did not find a WFS Cell directory for path " +
                        path + ". This is ok, returning empty list.");
                CellList children = new CellList(path, new Cell[] {});
                return Response.ok(children).build();
            }
        }
        
        // If we have reached here, we have 'dir' with the directory in which
        // the cells should be. Create a WFSCellChildren object which is used
        // to serialize the result.
        String names[] = dir.getCellNames();
        if (names == null) {
            // If the directory exists, yet there are no children, this is ok.
            // Just return an empty list.
            LOGGER.fine("Did not find WFS Cell children for path " + path +
                    ". This is ok, returning empty list.");
            CellList children = new CellList(path, new Cell[]{});
            return Response.ok(children).build();
        }

        LOGGER.fine("For WFS Cell " + path + " # Children " + names.length);
        
        // Loop through and create the WFSCellChildren object, we need to
        // include the last modified time so that the client can check whether
        // the cell has been modified or not.
        LinkedList<Cell> list = new LinkedList<Cell>();
        for (String name : names) {
            WFSCell cell = dir.getCellByName(name);
            if (cell == null) {
                LOGGER.warning("No WFS cell exists with name " + name);
                continue;
            }
            LOGGER.fine("Found WFS child " + name + " in path " + path);
            list.add(new Cell(name, cell.getLastModified()));
        }
        
        // Convert the list of CellChilds to an array, form into a CellList and
        // send the CellList directly to the client.
        Cell[] childs = list.toArray(new Cell[] {});
        LOGGER.fine("For WFS Cell " + path + " setting children array " +
                childs);
        CellList children = new CellList(path, childs);
        return Response.ok(children).build();
    }
}
