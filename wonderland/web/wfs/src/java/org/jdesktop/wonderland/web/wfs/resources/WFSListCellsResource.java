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
import java.util.LinkedList;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.web.wfs.WFSManager;
import org.jdesktop.wonderland.tools.wfs.WFS;
import org.jdesktop.wonderland.tools.wfs.WFSCell;
import org.jdesktop.wonderland.tools.wfs.WFSCellDirectory;
import org.jdesktop.wonderland.common.wfs.CellList;
import org.jdesktop.wonderland.common.wfs.CellList.Cell;


/**
 * The WFSListCellsResource class is a Jersey RESTful resource that allows
 * clients to obtain a list of all of the cells in a WFS.
 * <p>
 * The format of the URI is: /wfs/{wfsname}/cells/?reload={value}, where
 * {wfsname} is the name of the WFS root (as returned by the WFSRootsResource)
 * and {value} is either "true" or "false" and directs the WFS manager to
 * re-read the WFS from the underlying medium if true.
 * <p>
 * The cell information returned is the JAXB serialization of the WFSCellList
 * class.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@Path(value="/{wfsname:.*}/cells")
public class WFSListCellsResource {
    
    /**
     * Returns the JAXB XML serialization of the list of all cells in a WFS
     * name of the root WFS (without the -wfs extension). Returns the XML via
     * an HTTP GET request. The list of cell names returned is guaranteed to
     * be ordered such that parent cells appear in the list before child cells.
     * 
     * @param wfsName The name of the WFS root (no -wfs extension)
     * @param path The relative path of the file (no -wld, -wlc.xml extensions)
     * @return The XML serialization of the cell setup information via HTTP GET.
     */
    @GET
    @Produces({"application/xml", "application/json"})
    public Response getCellResource(@PathParam("wfsname") String wfsName, @QueryParam("reload") String reload) {
        /* Fetch thhe error logger for use in this method */
        Logger logger = WFSManager.getLogger();
        
        /* The list of resulting cells */
        LinkedList<CellList.Cell> cellList = new LinkedList();
        
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
        WFSCellDirectory rootDir = wfs.getRootDirectory();
        if (rootDir == null) {
            logger.warning("WFSManager: Unable to find WFS root with name " + wfsName);
            ResponseBuilder rb = Response.status(Response.Status.BAD_REQUEST);
            return rb.build();
        }
        
        /*
         * Find out whether we should reload the WFS as we read it. We acquire
         * ownership of the WFS momentarily to tell it to reload
         */
        if (reload != null && reload.compareTo("true") == 0) {
            try {
                wfs.acquireOwnership();
                rootDir.setReload();
                wfs.release();
            } catch (InterruptedException excp) {
                logger.warning("WFSManager: Unable to set WFS to reload " + excp.toString());
            }
        }
        
        /* A queue (last-in, first-out) containing a list of cell to search down */
        LinkedList<WFSCellDirectory> children = new LinkedList();
        children.addFirst(rootDir);
        
        /*
         * Perform a breadth-first search through the tree and add cells as we
         * discover them.
         */
        while (children.isEmpty() == false) {
            /* Fetch and remove the first on the list and load */
            WFSCellDirectory childdir = children.removeFirst();
            this.loadCells(childdir, children, cellList);
        }
        
        /* Convert the list of CellChilds to an array */
        Cell[] childs = cellList.toArray(new Cell[] {});
        CellList wfsCellList = new CellList("", childs);
        
        /* Send the serialized cell names to the client */
        ResponseBuilder rb = Response.ok(wfsCellList);
        return rb.build();
    }
    
    /**
     * Recurisvely loads cells from a given child directory (dir) in the WFS
     * and adds to the cell list given (cellList). If this child has any
     * children directories, then add to the children parameter.
     * 
     * @param dir The current directory of children to load
     * @param children A list of child directories remaining to be loaded
     * @param cellList An ordered list of cells in the WFS
     */
    private void loadCells(WFSCellDirectory dir,
            LinkedList<WFSCellDirectory> children,
            LinkedList<CellList.Cell> cellList) {
        
        /*
         * Fetch an array of the names of the child cells. Check this is not
         * null, although this getChildren() should return an empty array
         * instead.
         */
        WFSCell cells[] = dir.getCells();
        for (WFSCell cell : cells) {
            cellList.addLast(new Cell(cell.getCanonicalName(), cell.getLastModified()));
            if (cell.getCellDirectory() != null) {
                children.addLast(cell.getCellDirectory());
            }
        }
    }
}
