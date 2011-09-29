/**
 * Open Wonderland
 *
 * Copyright (c) 2010 - 2011, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

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
package org.jdesktop.wonderland.server.wfs.importer;

import org.jdesktop.wonderland.common.wfs.CellList;
import org.jdesktop.wonderland.common.wfs.WorldRootList;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import org.jdesktop.wonderland.server.WonderlandContext;


/**
 * The CellImporterUtils contains a collection of static utility methods to load
 * WFS information from the WFS web service.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class CellImporterUtils {

    // The error logger
    private static Logger LOGGER =
            Logger.getLogger(CellImporterUtils.class.getName());

    /* The prefix to add to URLs for the WFS web service */
    private static final String WFS_PREFIX = "wonderland-web-wfs/wfs/";
    
    /**
     * Returns the list of cells in the WFS as a hashmap. The list of cells
     * is ordered so that parent cells appear before child cells. Takes the WFS
     * URI of the WFS root.
     */
    public static CellList getWFSCells(String root, boolean reload) {
        /*
         * Try to open up a connection the Jersey RESTful resource and parse
         * the stream. Upon error return null.
         */
        try {
            URL url = getURL(WFS_PREFIX + root + "/cells/?reload=" + Boolean.toString(reload));
            return CellList.decode("", url.openStream());
        } catch (java.lang.Exception excp) {
            LOGGER.log(Level.WARNING, "Error getting WFS cells, root=" + root +
                    ", reload=" + reload, excp);
            return null;
        }
    }
    
    /**
     * Returns the cell's setup information, null upon error. The relativePath
     * argument must never begin with a "/". For a cell in the root path, use
     * an empty string for the relative path argument
     */
    public static CellServerState getWFSCell(String root, String relativePath, String name) {
        /*
         * Try to open up a connection the Jersey RESTful resource and parse
         * the stream. Upon error return null.
         */
        try {
            URL url = null;
            if (relativePath.compareTo("") == 0) {
                url = getURL(WFS_PREFIX + root + "/cell/" + name);
            }
            else {
                url = getURL(WFS_PREFIX + root + "/cell/" + relativePath + "/" + name);
            }
            
            /* Read in and parse the cell setup information */
            InputStreamReader isr = new InputStreamReader(url.openStream());
            return CellServerState.decode(isr, null);
        } catch (java.lang.Exception excp) {
            LOGGER.log(Level.WARNING, "Unable to fetch WFS Cell with root=" +
                    root + ", relative path=" + relativePath + ", name=" +
                    name, excp);
            return null;
        }
    }
    
    /**
     * Returns the children of the root WFS path, given the name of the WFS
     * root.
     */
    public static CellList getWFSRootChildren(String root) {
        try {
            URL url = getURL(WFS_PREFIX + root + "/directory/");
            return CellList.decode("", url.openStream());
        } catch (java.lang.Exception excp) {
            LOGGER.log(Level.WARNING, "Unable to fetch children, root=" +
                    root, excp);
            return null;
        }            
    }
    
    /**
     * Returns the children of the WFS path. The relativePath argument must
     * never begin with a "/".
     */
    public static CellList getWFSChildren(String root, String canonicalName) {
        /*
         * Try to open up a connection the Jersey RESTful resource and parse
         * the stream. Upon error return null.
         */
        try {
            URL url = getURL(WFS_PREFIX + root + "/directory/" + canonicalName);
            return CellList.decode(canonicalName, url.openStream());
        } catch (java.lang.Exception excp) {
            LOGGER.log(Level.WARNING, "Error getting WFS Children, root=" +
                    root + "name=" + canonicalName, excp);
            return null;
        }        
    }
    
    /**
     * Returns all of the WFS root names or null upon error
     */
    public static WorldRootList getWFSRoots() {
        /*
         * Try to open up a connection the Jersey RESTful resource and parse
         * the stream. Upon error return null.
         */
        try {
            URL url = getURL(WFS_PREFIX + "roots");
            return WorldRootList.decode(url.openStream());
        } catch (java.lang.Exception excp) {
            LOGGER.log(Level.WARNING, "Error loading WFS Root", excp);
            return null;
        }
    }
    
    /**
     * Returns the base URL of the web server.
     */
    public static URL getWebServerURL() throws MalformedURLException {
        return WonderlandContext.getWebServerURL();
    }

    /**
     * Forms a URL using the web server as the base url, appending the given
     * URL suffix. Properly encodes space.
     *
     * @param urlPart A part of a URL
     * @return A URL consisting of the base URL + the given URL part
     * @throw MalformedURLException Upon an invalid URL
     */
    private static URL getURL(String urlPart) throws MalformedURLException, URISyntaxException {
        urlPart = new URI(null, null, urlPart, null).toASCIIString();
        return new URL(getWebServerURL(), urlPart);
    }
}
