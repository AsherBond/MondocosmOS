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
package org.jdesktop.wonderland.modules.sas.server;

import com.sun.sgs.app.ManagedObject;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.CellID;
import com.sun.sgs.app.AppContext;

/**
 * Contains the cells launched by a provider.
 *
 * @author deronj
 */
@ExperimentalAPI
public class ProviderCellsLaunched implements ManagedObject, Serializable {

    private HashSet<CellID> cellsLaunched = new HashSet<CellID>();

    public void add (CellID cellID) {
        cellsLaunched.add(cellID);
        AppContext.getDataManager().markForUpdate(this);
    }

    public void remove (CellID cellID) {
        cellsLaunched.remove(cellID);
        AppContext.getDataManager().markForUpdate(this);
    }

    public Iterator<CellID> getIterator() {
        return cellsLaunched.iterator();
    }

    public void clear() {
        cellsLaunched.clear();
        AppContext.getDataManager().markForUpdate(this);
    }

    public int size() {
        return cellsLaunched.size();
    }
}
