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
package org.jdesktop.wonderland.modules.appbase.client.cell;

import org.jdesktop.wonderland.client.jme.cellrenderer.BasicRenderer;

/**
 * A cell renderer for app cells which allows views to be attached to it.
 *
 * @author dj
 */

public abstract class App2DCellRenderer extends BasicRenderer {

    /**
     * Create a new instance of App2DCellRenderer.
     * @param cell The cell to be rendered.
     */
    public App2DCellRenderer (App2DCell cell) {
        super(cell);
    }

    /**
     * Log this cell renderer's scene graph.
     */
    public abstract void logSceneGraph ();

}
