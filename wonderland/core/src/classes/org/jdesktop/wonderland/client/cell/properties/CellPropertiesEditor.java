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
package org.jdesktop.wonderland.client.cell.properties;

import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.common.cell.state.CellServerState;

/**
 * An interface to represent a graphical editor of the cell properties. This
 * interface is implemented by the actual GUI frame and typically consists of
 * a series of panels to edit some part of the cell (and cell component)
 * properties.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@ExperimentalAPI
public interface CellPropertiesEditor {

    /**
     * Returns the Cell being edited.
     *
     * @return The Cell object
     */
    public Cell getCell();

    /**
     * Returns an instance of the Cell's server state object representing the
     * state of the Cell when the properties editor was first displayed.
     *
     * @return The CellServerState object
     */
    public CellServerState getCellServerState();

    /**
     * Tells the properties editor that the panel with the given class name
     * is either "dirty" (true, information has changed) or "clean" (false,
     * information has not changed) since the last "save".
     *
     * @param clazz The Class of the panel object
     * @param isDirty True to tell the properties editor the panel is dirty,
     * false if clean.
     */
    public void setPanelDirty(Class clazz, boolean isDirty);

    /**
     * Adds a CellServerState object to the "update" list to be sent to the
     * server during the next "apply" operation.
     *
     * @param cellServerState The Cell server state to add to the update list
     */
    public void addToUpdateList(CellServerState cellServerState);

    /**
     * Adds a CellComponentServerState object to the "update" list to be sent
     * to the server during the next "apply" operation.
     *
     * @param cellComponentServerState The CellComponent server state to add
     * to the update list
     */
    public void addToUpdateList(CellComponentServerState cellComponentServerState);
}
