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
package org.jdesktop.wonderland.client.cell;

import java.io.Serializable;

/**
 * Listener for tracking changes to the children of a Cell
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public interface CellChildrenChangeListener extends Serializable {
    /**
     * Called when a child has been added to this cell
     * @param cell the cell whose component list has changed
     * @param child the cell that has been added as a child
     */
    public void childAdded(Cell cell, Cell child);

    /**
     * Called when a child has been removed from this cell
     * @param cell the cell whose component list has changed
     * @param child the cell that has been removed as a child
     */
    public void childRemoved(Cell cell, Cell child);
}
