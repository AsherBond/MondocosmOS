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
package org.jdesktop.wonderland.server.wfs.exporter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import javax.xml.bind.JAXBException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.common.wfs.CellDescriptor;
import org.jdesktop.wonderland.common.wfs.CellPath;
import org.jdesktop.wonderland.common.wfs.WFSRecordingList;
import org.jdesktop.wonderland.common.wfs.WorldRoot;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.annotation.NoSnapshot;


/**
 * The CellExporter contains a collection of static utility methods to export
 * WFS information from the WFS web service.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Bernard Horan
 */
public class CellExporterUtils {
    private static final Logger logger =
            Logger.getLogger(CellExporterUtils.class.getName());

    /* The prefix to add to URLs for the WFS web service */
    private static final String WFS_PREFIX = "wonderland-web-wfs/wfs/";

    /**
     * Creates a new snapshot, returns a WorldRoot object representing the
     * new WFS or null upon failure
     * @param name the name of the snapshot to create, or null to use the
     * default name
     */
    public static WorldRoot createSnapshot(String name)
            throws IOException, JAXBException
    {
        String query = (name == null) ? "" : "?name=" + name;
        URL url = new URL(getWebServerURL(), WFS_PREFIX + "create/snapshot" + query);
        return WorldRoot.decode(new InputStreamReader(url.openStream()));
    }

    /**
     * Creates a new recording, returns a WorldRoot object representing the
     * new WFS or null upon failure
     * @param name the name of the recording to create, or null to use the
     * default name
     */
    public static WorldRoot createRecording(String name)
            throws IOException, JAXBException
    {
        String encodedName = URLEncoder.encode(name, "UTF-8");
        String query = (name == null) ? "" : "?name=" + encodedName;
        URL url = new URL(getWebServerURL(), WFS_PREFIX + "create/recording" + query);

        return WorldRoot.decode(new InputStreamReader(url.openStream()));
    }

    /**
     * Get a cell descriptor for the given cell.
     * @param worldRoot the root this cell will be written to
     * @param parentPath the path of the parent cell
     * @param cellMO the cell to get a descriptor for
     * @param recordCellIDs if true, record the cellID of the cell
     */
    public static CellDescriptor getCellDescriptor(WorldRoot worldRoot,
            CellPath parentPath, CellMO cellMO, boolean recordCellIDs)
        throws IOException, JAXBException
    {
        // Create the cell on the server, fetch the setup information from the
        // cell. If the cell does not return a valid setup object, then simply
        // ignore the cell (and its children).
        // Test to see if this cell can be snapshotted
        
        if (hasNoSnapshotAnnotation(cellMO.getClass()) && !recordCellIDs) {
            // if there is a NoSnapshot annotation and we are not in the
            // event recorder, we must be snapshotting
            // Ignore this cell
            logger.info("Ignore cell of type " + cellMO.getClass() +
                        " due to @NoSnapshot annotation");
            return null; 
        }

        String cellID = cellMO.getCellID().toString();
        String cellName = cellMO.getName();
        CellServerState setup = cellMO.getServerState(null);
        if (setup == null) {
            return null;
        }

        // Now take out any component state that shouldn't be snapshotted
        if (!recordCellIDs) {
            // get the list of all server states
            CellComponentServerState[] ccsss =
                    setup.getComponentServerStates().values().toArray(new CellComponentServerState[0]);

            // check for snapshot annotations on the corresponding class
            for (CellComponentServerState ccss : ccsss) {
                if (ccss != null && hasNoSnapshotAnnotation(ccss)) {
                    logger.info("Remove component of type " +
                                ccss.getServerComponentClassName() +
                                " due to @NoSnapshot annotation");

                    setup.removeComponentServerState(ccss.getClass());
                }
            }
        }

        // If required, put the cellID of the cell in its metadata
        // Required by event recorder
        if (recordCellIDs) {
            setup.getMetaData().put("CellID", cellID.toString());
        }

        // Create the descriptor for the cell using the world root, path of the
        // parent, name of the cell and setup information we obtained from the
        // cell
        return new CellExportDescriptor(worldRoot, parentPath, cellID, cellName, setup);
    }

    /**
     * Creates a cell on disk given the description of the cell, which includes
     * the root of the wfs, the path of the parent, the child name, and the
     * cell's setup information 
     */
    public static void createCell(CellDescriptor descriptor)
            throws IOException, JAXBException
    {
        // Open an output connection to the URL, pass along any exceptions
        URL url = new URL(getWebServerURL(), WFS_PREFIX + "create/cell");

        String cellName = (descriptor.getParentPath() == null) ? "" :
                                descriptor.getParentPath().toString();
        cellName += "/" + descriptor.getCellUniqueName();
        logger.info("[WFS Exporter] Writing cell " + cellName + " to " +
                    url.toExternalForm());

        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setRequestProperty("Content-Type", "application/xml");
        OutputStreamWriter w = new OutputStreamWriter(connection.getOutputStream());
        
        // Write out the class as an XML stream to the output connection
        descriptor.encode(w);
        w.close();
        
        // For some reason, we need to read in the input for the HTTP POST to
        // work
        InputStreamReader r = new InputStreamReader(connection.getInputStream());
        while (r.read() != -1) {
            // Do nothing
        }
        r.close();
    }

    /**
     * Returns all of the WFS recording names or null upon error
     * @return a list of names of recordings
     */
    public static WFSRecordingList getWFSRecordings() throws IOException, JAXBException {
        /*
         * Try to open up a connection the Jersey RESTful resource and parse
         * the stream. Upon error return null.
         */
        URL url = new URL(getWebServerURL(), WFS_PREFIX + "listrecordings");
        //CellExporter.logger.info("WFS: Loading recordings at " + url.toExternalForm());
        return WFSRecordingList.decode(url.openStream());
        
    }
    
    /**
     * Returns the base URL of the web server.
     */
    public static URL getWebServerURL() throws MalformedURLException {
        return new URL(System.getProperty("wonderland.web.server.url"));
    }

    /**
     * Return if the class associated with the given
     * CellComponentServerState object contains the NoSnapshotAnnotation
     * @param the CellComponentServerState to check
     * @return true if the associated ComponentServerState class has the
     * NoSnapshot annotation, or false if it does not have the annotation
     */
    private static boolean hasNoSnapshotAnnotation(CellComponentServerState state)
        throws IOException
    {
        try {
            // first, find the component classname
            String className = state.getServerComponentClassName();
            if (className == null) {
                return false;
            }

            // now turn that into a class to check
            Class clazz = Class.forName(className);
            return hasNoSnapshotAnnotation(clazz);
        } catch (ClassNotFoundException cnfe) {
            throw new IOException(cnfe);
        }
    }

    /**
     * Return true if the given class has the NoSnapshot annotation associated
     * with it.
     * @param clazz the class to check
     * @return true if the class has the NoSnapshot annotation, or false
     * if not
     */
    private static boolean hasNoSnapshotAnnotation(Class clazz) {
        return clazz.isAnnotationPresent(NoSnapshot.class);
    }
}
