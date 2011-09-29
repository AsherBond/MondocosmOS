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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.content.ContentImportManager;
import org.jdesktop.wonderland.client.content.spi.ContentImporterSPI;
import org.jdesktop.wonderland.client.jme.dnd.spi.DataFlavorHandlerSPI;
import org.jdesktop.wonderland.client.jme.input.DropTargetDropEvent3D;
import org.jdesktop.wonderland.client.jme.input.DropTargetEvent3D;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * A handler to support drag-and-drop from the desktop. The data flavor supported
 * is the "java file list" type. This interacts with the content manager to
 * find the importer for the content and dispatch there.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
public class FileListDataFlavorHandler implements DataFlavorHandlerSPI {

    private static Logger logger = Logger.getLogger(FileListDataFlavorHandler.class.getName());

    /**
     * @inheritDoc()
     */
    public DataFlavor[] getDataFlavors() {
        return new DataFlavor[] { DataFlavor.javaFileListFlavor };
    }
    
    /**
     * @inheritDoc()
     */
    public String getFileExtension(DropTargetEvent3D event, DataFlavor flavor) {
        List<File> fileList = (List<File>) event.getTransferData(flavor);
        if (fileList == null || fileList.isEmpty()) {
            return null;
        }

        return DragAndDropManager.getFileExtension(fileList.get(0).getName());
    }

    /**
     * @inheritDoc()
     */
    public boolean accept(DropTargetEvent3D event, DataFlavor dataFlavor) {
        // Just accept everything sent our way with a valid file list
        List<File> fileList = (List<File>) event.getTransferData(dataFlavor);
        if (fileList == null || fileList.isEmpty()) {
            return false;
        }
        
        return true;
    }

    /**
     * @inheritDoc()
     */
    public void handleDrop(DropTargetDropEvent3D event, DataFlavor dataFlavor) {

        // Fetch the list of files from the transferable using the flavor
        // provided (assuming it is the java file list flavor).
        List<File> fileList = (List<File>) event.getTransferData(dataFlavor);
        if (fileList == null) {
            logger.log(Level.WARNING, "No data for " + dataFlavor);
            return;
        }
        FileListDataFlavorHandler.importFile(fileList, true, null);
    }

    /**
     * @inheritDoc()
     */
    public void handleImport(DropTargetDropEvent3D event, DataFlavor flavor,
                             ImportResultListener listener)
    {
        // Fetch the list of files from the transferable using the flavor
        // provided (assuming it is the java file list flavor).
        List<File> fileList = (List<File>) event.getTransferData(flavor);
        if (fileList == null) {
            logger.log(Level.WARNING, "No data for " + flavor);
            return;
        }
        FileListDataFlavorHandler.importFile(fileList, false, listener);
    }

    /**
     * Launches a cell based upon a list of files. This method uploads the first
     * file found to the content repository and launches a Cell with it.
     *
     * @param fileList
     */
    public static void importFile(List<File> fileList, final boolean createCell,
                                  final ImportResultListener listener)
    {
        if (fileList.size() < 1) {
            logger.warning("No file is given during drag-and-drop");
            return;
        }
        
        // Just take the first file and find out its extension. If there is
        // none, then signal an error and return since we do not know who
        // handles this file type.
        final File file = fileList.get(0);
        final String extension = DragAndDropManager.getFileExtension(file.getName());
        if (extension == null) {
            logger.warning("No file extension found for " + file.getAbsolutePath());
            return;
        }

        // Otherwise, ask the content manager for whom handles this kind of
        // file and dispatch there.
        ContentImportManager cim = ContentImportManager.getContentImportManager();
        final ContentImporterSPI importer = cim.getContentImporter(extension, true);
        if (importer == null) {
            logger.warning("No importer found for " + file.getAbsolutePath());
            return;
        }

        // Kick off a thread to upload the content. We put this in its own
        // thread to let this method complete (since we are running on the
        // AWT event queue.
        new Thread() {
            @Override
            public void run() {
                try {
                    String uri = importer.importFile(file, extension, createCell);
                    if (listener != null) {
                        listener.importSuccess(uri);
            }
                } catch (Throwable t) {
                    if (listener != null) {
                        listener.importFailure(t, null);
                    } else {
                        logger.log(Level.WARNING, "Error importing " + file, t);
                    }
                }
            }
        }.start();
    }
}
