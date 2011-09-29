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
package org.jdesktop.wonderland.client.jme.dnd;

import java.awt.datatransfer.DataFlavor;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.jme.content.AbstractContentImporter;
import org.jdesktop.wonderland.client.jme.dnd.spi.DataFlavorHandlerSPI;
import org.jdesktop.wonderland.client.jme.input.DropTargetDropEvent3D;
import org.jdesktop.wonderland.client.jme.input.DropTargetEvent3D;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * A handler to support drag-and-drop from a URL (perhaps from a web browser).
 * The data flavor supported has the mime type "application/x-java-url". This
 * simply looks for a Cell that can handle the data type and launches it.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
public class URLDataFlavorHandler implements DataFlavorHandlerSPI {

    private static final Logger LOGGER = Logger.getLogger(
            URLDataFlavorHandler.class.getName());
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/client/jme/dnd/Bundle");

    /**
     * @inheritDoc()
     */
    public DataFlavor[] getDataFlavors() {
        try {
            return new DataFlavor[]{
                        new DataFlavor(
                                "application/x-java-url; class=java.net.URL")
                    };
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.WARNING, "Unable to find DataFlavor for URL", ex);
            return new DataFlavor[]{};
        }
    }

    /**
     * @inheritDoc()
     */
    public String getFileExtension(DropTargetEvent3D event, DataFlavor flavor) {
        URL url = (URL) event.getTransferData(flavor);
        if (url == null) {
            return null;
        }

        return DragAndDropManager.getFileExtension(url.getFile());
    }

    /**
     * @inheritDoc()
     */
    public boolean accept(DropTargetEvent3D event, DataFlavor dataFlavor) {
        // We will accept all transferables, except those with the "file"
        // protocol. We kick those out to the "flie list" data flavor handler
        URL url = (URL) event.getTransferData(dataFlavor);
        if (url == null) {
            LOGGER.warning("No data for " + dataFlavor);
            return false;
        }

        String protocol = url.getProtocol();
        if (protocol.equals("file") == true) {
            return false;
        }
        return true;
    }

    /**
     * @inheritDoc()
     */
    public void handleDrop(DropTargetDropEvent3D event, DataFlavor dataFlavor) {
        // Fetch the url from the transferable using the flavor it is provided
        // (assuming it is a URL data flavor)
        URL url = (URL) event.getTransferData(dataFlavor);
        if (url == null) {
            LOGGER.log(Level.WARNING, "Unable to read data for " + dataFlavor);
            return;
        }
        URLDataFlavorHandler.launchCellFromURL(url);
    }

    /**
     * @inheritDoc()
     */
    public void handleImport(DropTargetDropEvent3D event, DataFlavor flavor,
                             ImportResultListener listener)
    {
        // Fetch the url from the transferable using the flavor it is provided
        // (assuming it is a URL data flavor)
        URL url = (URL) event.getTransferData(flavor);
        if (url == null) {
            if (listener != null) {
                listener.importFailure(null, "Unable to read data for " + flavor);
            }

            return;
        }

        // Notify the listener
        importURL(url, listener);
        }

    /**
     * Import the file at the given URL.
     *
     * @param url the url to import
     * @param listener the listener to notify of the result
     */
    public static void importURL(URL url, ImportResultListener listener) {
        // Notify the listener of the new URL
        listener.importSuccess(url.toExternalForm());
        }

    /**
     * Launches a cell based upon a given URL. This method assumes the URL
     * refers to some generally-available web content that all clients can fetch
     *
     * @param url The URL to launch a Cell with
     */
    public static void launchCellFromURL(URL url) {
        // Delegate cell creation to the AbstractContentImporter, which
        // knows how to create cells by URI
        AbstractContentImporter.createCell(url.toExternalForm());
        }
    }
