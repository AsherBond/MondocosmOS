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
package org.jdesktop.wonderland.server.cell;

import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.server.security.Resource;

/**
 * A service that supplies Resources associated with cells.  This
 * Darkstar service is implemented by the security module for this server.
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public interface CellResourceManager {
    /**
     * Get the resource associated with the given cell ID.  This method
     * will return a resource that can be used in security queries for
     * the given cell.  If this method returns null, it means there are
     * no security constraints associated with this cell.
     * @param cellID the cell to find a resource for
     * @return the resource representing the security constraints of the
     * given cell, or null if no resource represents this cell's
     * constraints.
     */
    public Resource getCellResource(CellID cellID);
}
