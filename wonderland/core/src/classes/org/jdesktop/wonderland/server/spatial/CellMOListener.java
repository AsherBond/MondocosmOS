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
package org.jdesktop.wonderland.server.spatial;

import org.jdesktop.wonderland.server.cell.CellMO;

/**
 * A listener used by server plugins to be notified whenever a cell is
 * added to or removed from the universe.
 *
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public interface CellMOListener {
    /**
     * Notification that a cell was added to the universe.
     * @param cell the cell that was added
     */
    public void cellAdded(CellMO cell);

    /**
     * Notification that a cell was removed from the universe.
     * @param cell the cell that was removed
     */
    public void cellRemoved(CellMO cell);
}
