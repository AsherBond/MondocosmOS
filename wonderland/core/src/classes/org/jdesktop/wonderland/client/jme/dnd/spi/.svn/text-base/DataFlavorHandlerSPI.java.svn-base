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
package org.jdesktop.wonderland.client.jme.dnd.spi;

import java.awt.datatransfer.DataFlavor;
import org.jdesktop.wonderland.client.jme.input.DropTargetDropEvent3D;
import org.jdesktop.wonderland.client.jme.input.DropTargetEvent3D;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * Handler for different data flavors for drag-and-drop. Classes implement this
 * interface to handle different DataFlavor objects when dropped into the
 * world.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
public interface DataFlavorHandlerSPI {

    /**
     * Returns an array of DataFlavors that this class supports. If none, then
     * return an empty array.
     *
     * @param An array of DataFlavor objects
     */
    public DataFlavor[] getDataFlavors();

    /**
     * Returns the file extension of the given event, if the extension can
     * be determined. If the extension cannot be determined from the given
     * event, return null.
     */
    public String getFileExtension(DropTargetEvent3D dtde, DataFlavor flavor);

    /**
     * Returns true to accept the event. This method provides the handler
     * the additional ability to either accept or reject the event. If
     * rejected, the system tries to find another suitable data flavor handler
     *
     * @param event the event to handle
     * @param dataFlavor the flavor being queried for
     */
    public boolean accept(DropTargetEvent3D dtde, DataFlavor flavor);

    /**
     * Handles when an item has been dropped into the world with a data flavor
     * supported by this class.
     *
     * @param event the event to handle
     * @param flavor the flavor being queried for
     */
    public void handleDrop(DropTargetDropEvent3D dtde, DataFlavor flavor);

    /**
     * Handles importing a file with the given data flavor (using the content
     * import manager). When the import completes, the given listener
     * will be asynchronously notified with the status.
     *
     * @param event the event to handle
     * @param flavor the flavor to handle
     * @param listener the import listener to notify with the results of the
     * import, or null if no listener should be notified.
     */
    public void handleImport(DropTargetDropEvent3D dtde, DataFlavor flavor,
                             ImportResultListener listener);

    /**
     * An interface for the results of import
     */
    public interface ImportResultListener {
        /**
         * Notification that the import succeeded. The uri included provides
         * access to the relevant content.
         *
         *  @param uri the URI for the content that was imported
         */
        public void importSuccess(String uri);

        /**
         * Notification that import failed. The root cause and error message
         * are included.
         *
         * @param cause the exception (if any) that prevented import from
         * working, or null if not available.
         * @param message the message (if any) that prevented import from
         * working, or null if not available.
         */
        public void importFailure(Throwable cause, String message);
}
}
