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
package org.jdesktop.wonderland.client.jme.content;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.jdesktop.wonderland.client.cell.registry.spi.CellFactorySPI;
import org.jdesktop.wonderland.client.cell.utils.CellCreationException;
import org.jdesktop.wonderland.client.cell.utils.CellSelectionRegistry;
import org.jdesktop.wonderland.client.cell.utils.CellUtils;
import org.jdesktop.wonderland.client.cell.utils.spi.CellSelectionSPI;
import org.jdesktop.wonderland.client.content.spi.ContentImporterSPI;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.state.CellServerState;

/**
 * An abstract base class that content importers may use to help implement the
 * ContentImporterSPI. This class takes care of querying whether the asset
 * already exists and utility methods to create the cell based upon the asset.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
public abstract class AbstractContentImporter implements ContentImporterSPI {

    // The error logger
    private static final Logger LOGGER =
            Logger.getLogger(AbstractContentImporter.class.getName());
    // The I18N resource bundle
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/client/jme/content/Bundle");

    /**
     * @inheritDoc()
     */
    public String importFile(File file, String extension) {
        return importFile(file, extension, true);
    }


    /**
     * @inheritDoc()
     */
    public String importFile(File file, String extension, boolean createCell) {

        final JFrame frame = JmeClientMain.getFrame().getFrame();

        // Next check whether the content already exists and ask the user if
        // the upload should still proceed. By default (result == 0), the
        // system will upload (and overwrite) and files.
        int result = JOptionPane.YES_OPTION;
        String uri = isContentExists(file);
        if (uri != null) {

            Object[] options = {
                BUNDLE.getString("Replace"),
                BUNDLE.getString("Use_Existing"),
                BUNDLE.getString("Cancel")
            };
            String msg = MessageFormat.format(
                    BUNDLE.getString("Replace_Question"), file.getName());
            String title = BUNDLE.getString("Replace_Title");

            result = JOptionPane.showOptionDialog(frame, msg, title,
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

            // If the user hits Cancel or a "closed" action (e.g. Escape key)
            // then just return
            if ((result == JOptionPane.CANCEL_OPTION)
                    || (result == JOptionPane.CLOSED_OPTION)) {

                return null;
            }
        }

        // If the content exists and we do not want to upload a new version,
        // then simply create it and return.
        if (result == JOptionPane.NO_OPTION) {
            if (createCell) {
            createCell(uri);
            }
            
            return uri;
        }

        // Display a dialog showing a wait message while we import. We need
        // to do this in the SwingWorker thread so it gets drawn
        String msg = MessageFormat.format(
                BUNDLE.getString("Please_Wait_Message"), file.getName());
        String title = BUNDLE.getString("Please_Wait_Title");
        JOptionPane waitMsg = new JOptionPane(msg);
        final JDialog dialog = waitMsg.createDialog(frame, title);
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                dialog.setVisible(true);
            }
        });

        // Next, do the actual upload of the file. This should display a
        // progress dialog if the upload is going to take a long time.
        try {
            uri = uploadContent(file);
        } catch (java.io.IOException excp) {
            LOGGER.log(Level.WARNING, "Failed to upload content file "
                    + file.getAbsolutePath(), excp);

            final String fname = file.getName();
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    dialog.setVisible(false);

                    String msg = MessageFormat.format(
                            BUNDLE.getString("Upload_Failed_Message"), fname);
                    String title = BUNDLE.getString("Upload_Failed_Title");

                    JOptionPane.showMessageDialog(frame, msg, title,
                            JOptionPane.ERROR_MESSAGE);
                }
            });
            return null;
        } finally {
            // Close down the dialog indicating success
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    dialog.setVisible(false);
                }
            });
        }

        // Finally, go ahead and create the cell.
        if (createCell) {
        createCell(uri);
        }
        
        return uri;
    }

    /**
     * If the content already exists in the user's content repository, return
     * it's URI. If the content does not exist, return null.
     *
     * @param file The File of the imported content
     * @return The URL of the existing content, null if it does not exist
     */
    public abstract String isContentExists(File file);

    /**
     * Uploads the content file. Throws IOException upon error. Returns a URI
     * that describes the location of the content
     *
     * @param file The content file to upload
     * @return A URI that represents the uploaded content
     * @throws IOException Upon upload error
     */
    public abstract String uploadContent(File file) throws IOException;

    /**
     * Create a cell based upon the uri of the content and the file extension
     * of the uploaded file.
     *
     * @param uri The URI of the uploaded content
     */
    public static void createCell(String uri) {
        // Figure out what the file extension is from the uri, looking for
        // the final '.'.
        String extension = getFileExtension(uri);
        if (extension == null) {
            LOGGER.warning("Could not find extension for " + uri);
            return;
        }

        // First look for the SPI that tells us which Cell to use. If there
        // is none, then it is a fairly big error. (There should be at least
        // one registered in the system).
        CellSelectionSPI spi = CellSelectionRegistry.getCellSelectionSPI();
        if (spi == null) {
            final JFrame frame = JmeClientMain.getFrame().getFrame();
            LOGGER.warning("Could not find the CellSelectionSPI factory");
            String message = BUNDLE.getString("Launch_Failed_Message");
            message = MessageFormat.format(message, uri);
            JOptionPane.showMessageDialog(frame, message,
                    BUNDLE.getString("Launch_Failed"),
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Next look for a cell type that handles content with this file
        // extension and create a new cell with it.
        CellFactorySPI factory = null;
        try {
            factory = spi.getCellSelection(extension);
        } catch (CellCreationException excp) {
            final JFrame frame = JmeClientMain.getFrame().getFrame();
            LOGGER.log(Level.WARNING,
                    "Could not find cell factory for " + extension);
            String message = BUNDLE.getString("Launch_Failed_Message");
            message = MessageFormat.format(message, uri);
            JOptionPane.showMessageDialog(frame, message,
                    BUNDLE.getString("Launch_Failed"),
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // If the returned factory is null, it means that the user has cancelled
        // the action, so we just return
        if (factory == null) {
            return;
        }

        // Get the cell server state, injecting the content URI into it via
        // the properties
        Properties props = new Properties();
        props.put("content-uri", uri);
        CellServerState state = factory.getDefaultCellServerState(props);

        // Create the new cell at a distance away from the avatar
        try {
            CellUtils.createCell(state);
        } catch (CellCreationException excp) {
            LOGGER.log(Level.WARNING, "Unable to create cell for uri " + uri, excp);
        }
    }

    /**
     * Utility routine to fetch the file extension from the URI, or null if
     * none can be found.
     */
    private static String getFileExtension(String uri) {
        // Figure out what the file extension is from the uri, looking for
        // the final '.'.
        int index = uri.lastIndexOf(".");
        if (index == -1) {
            return null;
        }
        return uri.substring(index + 1);
    }
}
