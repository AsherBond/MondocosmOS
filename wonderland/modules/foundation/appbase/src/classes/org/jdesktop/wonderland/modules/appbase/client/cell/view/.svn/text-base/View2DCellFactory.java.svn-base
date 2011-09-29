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
package org.jdesktop.wonderland.modules.appbase.client.cell.view;

import org.jdesktop.wonderland.modules.appbase.client.Window2D;
import org.jdesktop.wonderland.modules.appbase.client.cell.App2DCell;
import org.jdesktop.wonderland.modules.appbase.client.cell.App2DCellRenderer;
import org.jdesktop.wonderland.modules.appbase.client.cell.view.viewdefault.View2DCell;
import org.jdesktop.wonderland.common.InternalAPI;

/**
 * All factory object which create views which live in a cell in the 3D world
 * must implement this interface. 
 *
 * @author deronj
 */

@InternalAPI
public interface View2DCellFactory {
    public void initialize ();
    public App2DCellRenderer createCellRenderer (App2DCell cell);
    public View2DCell createView (App2DCell cell, Window2D window);
}
