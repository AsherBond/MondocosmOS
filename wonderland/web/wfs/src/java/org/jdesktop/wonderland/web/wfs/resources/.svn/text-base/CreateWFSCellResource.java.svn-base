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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.jdesktop.wonderland.common.wfs.CellDescriptor;
import org.jdesktop.wonderland.tools.wfs.WFS;
import org.jdesktop.wonderland.tools.wfs.WFSCell;
import org.jdesktop.wonderland.tools.wfs.WFSCellDirectory;
import org.jdesktop.wonderland.web.wfs.WFSManager;

/**
 * Handles Jersey RESTful requests to create a cell in a wfs given information
 * about the cell, including the root path of the wfs, its parent path, the
 * cell name, and the setup information.
 * <p>
 * URI: http://<machine>:<port>/wonderland-web-wfs/wfs/create/cell
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@Path("/create/cell/")
public class CreateWFSCellResource {
    
    /**
     * Creates a new wfs cell given the cell description (wfs root path, parent
     * path, cell name, cell setup information.
     * 
     * @param cellDescriptor The necessary information about the new cell
     * @return An OK response upon success, BAD_REQUEST upon error
     */
    @POST
    @Consumes({"application/xml"})
    public Response createWFSCell(CellDescriptor cellDescriptor) {
        // Do some basic stuff, get the WFS manager class, etc
        Logger logger = Logger.getLogger(CreateWFSCellResource.class.getName());
        WFSManager manager = WFSManager.getWFSManager();
                
        // Fetch the WFS for the world root path, flag an error if it does
        // not yet exist.
        String rootPath = cellDescriptor.getRootPath().getRootPath();
        WFS wfs = manager.getWFS(cellDescriptor.getRootPath());
        if (wfs == null) {
            logger.warning("[WFS] The WFS " + rootPath + " does not exist.");
            return Response.status(Status.BAD_REQUEST).build();
        }
        
        // Fetch the root directory, this should exist
        WFSCellDirectory wfsDirectory = wfs.getRootDirectory();
        if (wfsDirectory == null) {
            logger.warning("[WFS] Unable to find root directory for " + rootPath);
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        // Iterate through all of the parents of the cell and fetch the proper
        // directory to place the new cell in. We fetch the names of the parent
        // cell individually and jump down through the list of directories. Note
        // that all of the directories, except perhaps the last should exist,
        // so we flag an error if that is not the case. If the parent path is
        // null, then this is a cell at the root of the wfs
        if (cellDescriptor.getParentPath() != null) {
            String parentCells[] = cellDescriptor.getParentPath().getParentPaths();
            for (int i = 0; i < parentCells.length; i++) {
                // First fetch the cell. If it does not exist, then return a bad
                // response.
                WFSCell parentCell = wfsDirectory.getCellByName(parentCells[i]);
                if (parentCell == null) {
                    logger.warning("[WFS] Unable to find cell " + parentCells[i] +
                            " in WFS " + rootPath);
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }

                // Next, get the directory associated with the cell. It also needs
                // to exist, otherwise, return a bad response. The only exception
                // is the last parent in the cell, which may not have its child
                // directory yet created.
                wfsDirectory = parentCell.getCellDirectory();
                if (i < parentCells.length - 1 && wfsDirectory == null) {
                    // This means that a parent cell directory, other than the
                    // immediate parent does not exist (which means the immediate
                    // parent cell does not exist, which is very bad!
                    logger.warning("[WFS] Unable to find directory for cell " +
                            parentCells[i] + " in WFS " + rootPath);
                    return Response.status(Response.Status.BAD_REQUEST).build();
                }
                else if (wfsDirectory == null) {
                    // Unless we are talking about the cell directory associated
                    // with the parent. In which case we should create it.
                    try {
                        wfs.acquireOwnership();
                        wfsDirectory = parentCell.createCellDirectory();
                        parentCell.write();
                    } catch (java.lang.InterruptedException excp) {
                        logger.log(Level.WARNING, "[WFS] Unable to lock WFS " +
                                rootPath, excp);
                        return Response.status(Response.Status.BAD_REQUEST).build();
                    } catch (java.lang.Exception excp) {
                        logger.log(Level.WARNING, "[WFS] Failed to create WFS " +
                                " directory " + rootPath);
                        return Response.status(Response.Status.BAD_REQUEST).build();
                    } finally {
                        wfs.release();
                    }
                }
            }
        }
        
        // When we have reached here, the directory in which to place the new
        // cell is in 'wfsDirectory'. We create the cell and write the WFS
        // back out to its disk. In this case, the cell name is the name of
        // the file, which should be <Cell Name>-<Cell ID>.
        try {
            wfs.acquireOwnership();
            String cellName = cellDescriptor.getCellUniqueName();
            WFSCell cell = wfsDirectory.addCell(cellName);
            if (cell == null) {
                logger.warning("[WFS] Failed to create cell " + cellName +
                        " in WFS " + rootPath);
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            cell.setCellSetup(cellDescriptor.getSetupInfo());
            wfsDirectory.write();
        } catch (java.lang.Exception excp) {
            logger.log(Level.WARNING, "[WFS] Unable to lock WFS " + rootPath, excp);
            return Response.status(Response.Status.BAD_REQUEST).build();
        } finally {
            wfs.release();
        }
        return Response.ok().build();
    }
}
