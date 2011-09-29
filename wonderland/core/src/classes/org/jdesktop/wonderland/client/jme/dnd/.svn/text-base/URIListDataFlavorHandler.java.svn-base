/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.client.jme.dnd;

import java.awt.datatransfer.DataFlavor;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.jme.dnd.spi.DataFlavorHandlerSPI;
import org.jdesktop.wonderland.client.jme.input.DropTargetDropEvent3D;
import org.jdesktop.wonderland.client.jme.input.DropTargetEvent3D;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * A handler to support drag-and-drop from a URI (perhaps from a web browser).
 * The data flavor supported has the mime type "application/x-java-url". This
 * simply looks for a Cell that can handle the data type and launches it.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
public class URIListDataFlavorHandler implements DataFlavorHandlerSPI {

    private static Logger logger = Logger.getLogger(URIListDataFlavorHandler.class.getName());

    /**
     * @inheritDoc()
     */
    public DataFlavor[] getDataFlavors() {
        try {
            return new DataFlavor[] {
                new DataFlavor("text/uri-list;class=java.lang.String")
            };
        } catch (ClassNotFoundException ex) {
            logger.log(Level.WARNING, "Unable to find DataFlavor for URL", ex);
            return new DataFlavor[] {};
        }
    }

    /**
     * @inheritDoc()
     */
    public String getFileExtension(DropTargetEvent3D event, DataFlavor flavor) {
        // get the URI from the event
        URI uri = getURI(event, flavor);
        if (uri == null) {
            return null;
        }

        return DragAndDropManager.getFileExtension(uri.getPath());
    }

    /**
     * @inheritDoc()
     */
    public boolean accept(DropTargetEvent3D event, DataFlavor dataFlavor) {
        // Just accept everything sent our way with a valid URI list
        String data = (String) event.getTransferData(dataFlavor);
        if (data == null) {
            return false;
        }

        List<URI> uris = uriStringToList(data);
        if (uris.isEmpty()) {
            return false;
        }

        return true;
    }

    /**
     * @inheritDoc()
     */
    public void handleDrop(DropTargetDropEvent3D event, DataFlavor dataFlavor) {
        // Get the URI from the event
        URI uri = getURI(event, dataFlavor);

        // Translate into a file list
        List<File> fileList = getFileList(uri);
        if (fileList == null) {
            // The first element was not a file:// URI. Assume it was a URL.
        try {
                URLDataFlavorHandler.launchCellFromURL(uri.toURL());
            } catch (MalformedURLException excp) {
                logger.log(Level.WARNING, "Unable to form URL from URI " +
                        uri.toString(), excp);
        }

            return;
        }

        // Handle the file list via the FileListDataFlavorHandler
        FileListDataFlavorHandler.importFile(fileList, true, null);
    }

    /**
     * @inheritDoc
     */
    public void handleImport(DropTargetDropEvent3D dtde, DataFlavor flavor,
                             ImportResultListener listener)
    {
        // Get the URI from the event
        URI uri = getURI(dtde, flavor);

        // Translate into a file list
        List<File> fileList = getFileList(uri);
        if (fileList == null) {
            // The first element was not a file:// URI. Assume it was a URL.
            try {
                URLDataFlavorHandler.importURL(uri.toURL(), listener);
            } catch (MalformedURLException excp) {
                listener.importFailure(excp, "Unable to form URL from URI " +
                                       uri.toString());
            }

            return;
        }

        // Handle the file list via the FileListDataFlavorHandler
        FileListDataFlavorHandler.importFile(fileList, false, listener);
    }

    /**
     * Translate the given URI into a list of files. If the protocol of the
     * URI is not file://, return null.
     *
     * @param uri the uri to translate
     * @return a list of one file with the given URI, or null if the URI
     * is not a file:// uri.
     */
    private List<File> getFileList(URI uri) {
        // Find the scheme. We'll need this to dispatch to other places. Make
        // sure it is not null (can it ever be null?)
        String scheme = uri.getScheme();
        if (scheme == null) {
            logger.warning("Scheme is null for dropped URI " + uri.toString());
            return null;
        }

        // First check to see if the protocol is not "file". If so, then assume
        // the content is available over the network somewhere and launch a
        // Cell based upon it.
        if (!scheme.equals("file")) {
            return null;
            }

            // The URI has a scheme of "file". On certain systems (e.g. Mac OSX),
            // the URI has the form "file://localhost/<path>". This form cannot
            // be converted into a File, so we must just take the <path> part
            // and create a File with it. If the URi does not have an "authority"
            // then we can directly create a file object from it.
            File file = null;
            if (uri.getAuthority() != null) {
                logger.warning("FILE PATH " + uri.getPath());
                file = new File(uri.getPath());
        } else {
                logger.warning("FILE PATH " + uri.toString());
                file = new File(uri);
            }

            // Launch a file based upon the File object we just created
        return Arrays.asList(file);
        }

    /**
     * Get a URI from a DropTargetEvent3D that supports our data flavor
     *
     * @param event the event to query
     * @param flavor the data flavor associated with the event
     * @return the URI from the given event, or null if no URI could
     * be found
     */
    private URI getURI(DropTargetEvent3D event, DataFlavor flavor) {
        // Fetch the uri from the transferable using the flavor it is provided
        // (assuming it is a URI data flavor). Convert into a list of URIs,
        // just take the first one.
        String data = (String) event.getTransferData(flavor);
        if (data == null) {
            logger.warning("No data for " + flavor);
            return null;
    }

        List<URI> uriList = uriStringToList(data);

        // Check to see if we have at least one URI, if not, log an error and
        // return
        if (uriList.isEmpty() == true) {
            logger.warning("No URIs found in transferable, data " + data);
            return null;
        }
        return uriList.get(0);
    }


    /**
     * Takes a string list of URIs returned from the "text/uri-list" DataFlavor
     * type and returns a List of URIs of the data.
     * <p>
     * This method is taken from the Java SE bug database at:
     * http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4899516.
     */
    private List<URI> uriStringToList(String data) {
        java.util.List list = new ArrayList(1);
        for (StringTokenizer st = new StringTokenizer(data, "\r\n"); st.hasMoreTokens();) {
            String s = st.nextToken();
            if (s.startsWith("#") == true) {
                // the line is a comment (as per the RFC 2483)
                continue;
            }
            try {
                URI uri = new URI(s);
                list.add(uri);
            } catch (java.net.URISyntaxException e) {
                // malformed URI
                logger.log(Level.FINE, "Error for URI " + s, e);
            } catch (IllegalArgumentException e) {
                // the URI is not a valid 'file:' URI
                logger.log(Level.FINE, "Error for URI " + s, e);
            }
        }
        return list;
    }
}
