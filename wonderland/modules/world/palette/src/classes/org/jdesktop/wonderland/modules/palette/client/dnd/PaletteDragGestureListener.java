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
package org.jdesktop.wonderland.modules.palette.client.dnd;

import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import org.jdesktop.wonderland.client.cell.registry.spi.CellFactorySPI;

/**
 * Listener for the drag-start gesture for the cell palette preview image
 * and initiates the drag.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class PaletteDragGestureListener implements DragGestureListener {
    public CellFactorySPI cellFactory = null;
    public Image previewImage = null;

    /** Default Constructor */
    public PaletteDragGestureListener() {
    }

    public void dragGestureRecognized(DragGestureEvent dge) {

        // Initialize the transferable with the default cell server state
        // factory
        Transferable t = new CellServerStateTransferable(cellFactory);

        // Begin with the drag setting up the origin so that it aligns with
        // the image.
        Point dragOrigin = dge.getDragOrigin();
        dragOrigin.setLocation(-dragOrigin.x, -dragOrigin.y);
        dge.startDrag(DragSource.DefaultCopyNoDrop, previewImage, dragOrigin, t, null);
    }
}
