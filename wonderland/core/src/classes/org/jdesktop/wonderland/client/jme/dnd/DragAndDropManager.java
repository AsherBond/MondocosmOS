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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.input.EventClassListener;
import org.jdesktop.wonderland.client.input.InputManager;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.client.jme.dnd.spi.DataFlavorHandlerSPI;
import org.jdesktop.wonderland.client.jme.dnd.spi.DataFlavorHandlerSPI.ImportResultListener;
import org.jdesktop.wonderland.client.jme.input.DropTargetDropEvent3D;
import org.jdesktop.wonderland.client.jme.input.DropTargetEvent3D;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * Manages the drag-and-drop for the world. There is a single drop source for
 * the world, which is typically the main rendering panel. Other parts of the
 * system (e.g. modules) can register (via annotations) to handle various
 * data flavors of drag sources (e.g. if you are dragging from the Desktop
 * versus dragging a cell from the Cell Palette.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
public class DragAndDropManager {

    private static Logger logger = Logger.getLogger(DragAndDropManager.class.getName());

    // The I18N resource bundle
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/client/jme/dnd/Bundle");

    /* A map of supported data flavor and the handler */
    private final Map<DataFlavor, DataFlavorHandlerSPI> dataFlavorHandlerMap;

    /** the listener for drop events */
    private final GlobalDropListener dropEventListener;

    /** Default constructor */
    private DragAndDropManager() {
        // Create the hash map to hold all of the data flavor handlers and add
        // in the default one to handle drag-and-drop of files from the desktop
        dataFlavorHandlerMap = new HashMap();
        registerDataFlavorHandler(new FileListDataFlavorHandler());
        registerDataFlavorHandler(new URLDataFlavorHandler());
        registerDataFlavorHandler(new URIListDataFlavorHandler());

        dropEventListener = new GlobalDropListener();
        InputManager.inputManager().addGlobalEventListener(dropEventListener);
    }

    /**
     * Singleton to hold instance of DragAndDropMananger. This holder class is
     * loader on the first execution of DragAndDropManager.getDragAndDropManager().
     */
    private static class DragAndDropManagerHolder {
        private final static DragAndDropManager dndManager = new DragAndDropManager();
    }

    /**
     * Returns a single instance of this class
     * <p>
     * @return Single instance of this class.
     */
    public static final DragAndDropManager getDragAndDropManager() {
        return DragAndDropManagerHolder.dndManager;
    }

    /**
     * Registers a DataFlavorHandlerSPI. A data flavor handler handles when an
     * item has been dropped into the world for a specific type. Only one
     * handler is permitted per data flavor type.
     *
     * @param handler The data flavor handler
     */
    public void registerDataFlavorHandler(DataFlavorHandlerSPI handler) {
        // For each of the data flavors that are supported by the handler, add
        // then to the map. If the data flavor is already registered, then
        // overwrite.
        DataFlavor flavors[] = handler.getDataFlavors();
        if (flavors != null) {
            for (DataFlavor flavor : flavors) {
                dataFlavorHandlerMap.put(flavor, handler);
            }
        }
    }

    /**
     * Unregisters a DataFlavorHandlerSPI.
     *
     * @param handler The data flavor handler
     */
    public void unregisterDataFlavorHandler(DataFlavorHandlerSPI handler) {
        // For each of the data flavors that are supported by the handler,
        // remove then from the map. If the data flavor has been overwritten,
        // make sure not to remove it
        DataFlavor flavors[] = handler.getDataFlavors();
        if (flavors != null) {
            for (DataFlavor flavor : flavors) {
                DataFlavorHandlerSPI cur = dataFlavorHandlerMap.get(flavor);
                if (handler == cur) {
                    dataFlavorHandlerMap.remove(flavor);
                }
            }
        }
    }

    /**
     * Returns a set of supported data flavors.
     *
     * @return A Set of DataFlavor objects
     */
    public Set<DataFlavor> getDataFlavors() {
        return dataFlavorHandlerMap.keySet();
    }

    /**
     * Returns the data flavor handler for the given data flavor, null if one
     * does not exist for the data flavor.
     *
     * @param dataFlavor The DataFlavor object
     * @return A DataFlavorHandlerSPI object
     */
    public DataFlavorHandlerSPI getDataFlavorHandler(DataFlavor dataFlavor) {
        return dataFlavorHandlerMap.get(dataFlavor);
    }

    /**
     * Return true if a handler exists for the given data flavor
     *
     * @param dataFlavor The DataFlavor object
     * @return true if there is a handler for this flavor
     */
    public boolean hasDataFlavorHandler(DataFlavor dataFlavor) {
        return dataFlavorHandlerMap.containsKey(dataFlavor);
    }

    /**
     * Get the file extension for the given event, or return null
     * if no extension can be determined.
     *
     * @param event the event to determine the file name of
     */
    public String getFileExtension(DropTargetEvent3D event) {
        for (DataFlavor flavor : getSupportedFlavors(event.getDataFlavors())) {
            DataFlavorHandlerSPI handler = getDataFlavorHandler(flavor);
            if (handler.accept(event, flavor)) {
                String extension = handler.getFileExtension(event, flavor);
                if (extension != null) {
                    return extension;
                }
            }
        }
        
        return null;
    }

    /**
     * Import a file and return the corresponding URI to the listner
     *
     * @param event the event to import a file from
     * @param listener the optional listener to report results to
     */
    public void importContent(DropTargetDropEvent3D event,
                              ImportResultListener listener)
    {
        // Get the flavors we support
        List<DataFlavor> supportedFlavors = getSupportedFlavors(event.getDataFlavors());

        // Find a handler that will accept the file
        for (DataFlavor dataFlavor : supportedFlavors) {
            // Check to see whether the handler will accept the drop,
            // if not just go into the next in the list
            DataFlavorHandlerSPI handler = getDataFlavorHandler(dataFlavor);
            if (handler.accept(event, dataFlavor) == true) {
                handler.handleImport(event, dataFlavor, listener);
                return;
            }
        }

        // if we got here, the data flavor was not supported
        listener.importFailure(null, "No supported data flavors");
    }

    /**
     * Returns the string extension name of the given file name. If none, return
     * null. This simply looks for the final period (.) in the name.
     *
     * @param fileName The name of the file
     * @return The file extension
     */
    public static String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index == -1) {
            return null;
        }
        return fileName.substring(index + 1);
    }
    
    /**
     * Get the supported data flavors from the given array.
     *
     * @param dataFlavors an array of data flavors to consider
     * @return a list of supported data flavors
     */
    private List<DataFlavor> getSupportedFlavors(DataFlavor[] dataFlavors) {
        List<DataFlavor> supported = new ArrayList(Arrays.asList(dataFlavors));

        // remove all flavors we do not have a handler for
        for (Iterator<DataFlavor> i = supported.iterator(); i.hasNext();) {
            if (!hasDataFlavorHandler(i.next())) {
                i.remove();
            }
        }

        return supported;
    }
    /**
     * Adapter for the drop target event. Dispatches to the various handlers
     * registerd on this class for the matching data flavor
     */
    private class GlobalDropListener extends EventClassListener {

        @Override
        public Class[] eventClassesToConsume() {
            return new Class[] { DropTargetDropEvent3D.class };
        }

        @Override
        public void commitEvent(Event event) {
            DropTargetDropEvent3D dtde =
                    (DropTargetDropEvent3D) event;
            drop(dtde);
        }

        public void drop(DropTargetDropEvent3D dtde) {
            logger.warning("In global listener: " + dtde.getDataFlavors().length);

            // Check to see if the list of data flavors is empty. This happens
            // on MAX OSX, which is likely a Java bug. If so, display a dialog
            // to tell the user to retry the DnD.
            if (dtde.getDataFlavors().length == 0) {
                JFrame frame = JmeClientMain.getFrame().getFrame();
                String title = BUNDLE.getString("DND_Error_Title");
                String message = BUNDLE.getString("DND_Error_message");
                int type = JOptionPane.ERROR_MESSAGE;
                JOptionPane.showMessageDialog(frame, message, title, type);
                return;
            }

            // Get the flavors we support
            List<DataFlavor> supportedFlavors = getSupportedFlavors(dtde.getDataFlavors());

            // At this point, if there is at least one supported flavor, we
            // accept the drop on the presumption that at least one can really
            // handle it. It is possible that each decides to reject the drop,
            // but that is the rare case. In that event, we would acceptDrop(),
            // but later dropComplete(false), which isn't too bad I think.
            for (DataFlavor dataFlavor : supportedFlavors) {
                // Check to see whether the handler will accept the drop,
                // if not just go into the next in the list
                DataFlavorHandlerSPI handler = getDataFlavorHandler(dataFlavor);
                if (handler.accept(dtde, dataFlavor) == true) {
                    handler.handleDrop(dtde, dataFlavor);
                    return;
                }
            }
        }
    }
}
