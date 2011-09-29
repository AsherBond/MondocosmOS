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
package org.jdesktop.wonderland.modules.appbase.client.cell.view.viewdefault;

import org.jdesktop.wonderland.modules.appbase.client.Window2D;
import org.jdesktop.wonderland.modules.appbase.client.cell.App2DCell;
import org.jdesktop.wonderland.modules.appbase.client.cell.App2DCellRenderer;
import org.jdesktop.wonderland.modules.appbase.client.cell.view.View2DCellFactory;

/**
 * Implements a factory which creates new instances of <code>View2DCell</code>.
 *
 * @author deronj
 */

public class View2DCellFactoryDefault implements View2DCellFactory {

    public void initialize () {
        FrameHeaderSwing.staticInitialize();
    }

    public App2DCellRenderer createCellRenderer (App2DCell cell) {
        return new App2DCellRendererJME(cell);
    }

    public View2DCell createView (App2DCell cell, Window2D window) {
        return new View2DCell(cell, window);
    }
}
