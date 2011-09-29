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
package org.jdesktop.wonderland.common.cell;

import java.io.Serializable;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.InternalAPI;

/**
 * CellID provides a unique id for cells from a specific wonderland world 
 * instance.
 *  
 * @author paulby
 */
@ExperimentalAPI
public class CellID implements Serializable {
    
    private long id;
    private transient String str=null;
    
    private static final CellID invalidCellID = new CellID(Long.MIN_VALUE);
    private static final CellID environmentCellID = new CellID(0);
    private static final long firstCellID = 1;
    
    /**
     * Creates a new instance of CellID. Users should never call this, CellIDs
     * are only created by 
     */
    @InternalAPI
    public CellID(long id) {
        this.id = id;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CellID)
            if (((CellID) obj).id==id)
                return true;
        return false;
    }
    
    @Override
    public int hashCode() {
        return (int)id;
    }
    
    @Override
    public String toString() {
        if (str==null)
            str = Long.toString(id);

        return str;
    }

    /**
     * Get the first cell ID that should be assigned to cells
     * @return
     */
    public static long getFirstCellID() {
        return firstCellID;
    }
    
    /**
     * Returns a cellID that represents an invalid cell
     * @return
     */
    public static CellID getInvalidCellID() {
        return invalidCellID;
    }

    /**
     * Returns a cellID that represents the environment cell
     */
    public static CellID getEnvironmentCellID() {
        return environmentCellID;
    }
}
