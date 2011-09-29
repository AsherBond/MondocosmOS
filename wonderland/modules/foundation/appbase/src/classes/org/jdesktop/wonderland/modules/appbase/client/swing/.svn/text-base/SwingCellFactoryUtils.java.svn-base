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
package org.jdesktop.wonderland.modules.appbase.client.swing;

import org.jdesktop.wonderland.common.cell.state.BoundingVolumeHint;
import org.jdesktop.wonderland.modules.appbase.client.App2D;
import org.jdesktop.wonderland.common.cell.state.CellServerState;
import java.util.logging.Logger;

/**
 * Utilities to be used by Swing module cell factory classes.
 * 
 * @author deronj
 */
public class SwingCellFactoryUtils {

    private static final Logger logger = Logger.getLogger(SwingCellFactoryUtils.class.getName());

    /**
     * Optimization: Skip the initial system placement for app cells. The app base
     * will do the initial placement itself.
     */
    public static void skipSystemInitialPlacement (CellServerState state) {

        // Minor Optimization: Skip the initial system placement for app cells. Because 
        // cell bounds are fixed at cell creation time we need to give app cells a huge 
        // bounds (see the comment in App2DCellMO()). We won't know the right location to 
        // place an app cell until it's first window is made visible. Therefore,
        // we disable system placement to save work. The app base will perform the
        // the initial placement later.
        if (App2D.doAppInitialPlacement) {
            logger.info("doAppInitialPlacement: disable system placement");
            BoundingVolumeHint hint = new BoundingVolumeHint(false, null);
            state.setBoundingVolumeHint(hint);
        }
    }
}
