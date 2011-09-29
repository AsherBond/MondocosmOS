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
package org.jdesktop.wonderland.modules.appbase.server.cell;

import org.jdesktop.wonderland.modules.appbase.server.*;
import java.util.logging.Logger;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import com.jme.bounding.BoundingVolume;
import org.jdesktop.wonderland.common.cell.CellTransform;

/**
 * A server-side <code>app.base</code> app cell.
 *
 * @author deronj
 */
@ExperimentalAPI
public abstract class AppCellMO extends CellMO {

    protected static final Logger logger = Logger.getLogger(AppCellMO.class.getName());

    /** Create an instance of AppCellMO. */
    public AppCellMO() {
        super();
    }

    /** Create an instance of AppCellMO. */
    public AppCellMO(BoundingVolume localBounds, CellTransform transform) {
        super(localBounds, transform);
    }
}
