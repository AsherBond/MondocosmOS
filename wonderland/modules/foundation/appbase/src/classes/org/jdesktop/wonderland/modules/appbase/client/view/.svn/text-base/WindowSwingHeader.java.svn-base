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
package org.jdesktop.wonderland.modules.appbase.client.view;

import com.jme.math.Vector2f;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.App2D;
import org.jdesktop.wonderland.modules.appbase.client.Window2D;
import org.jdesktop.wonderland.modules.appbase.client.swing.WindowSwing;

/**
 * An undecorated WindowSwing used for the header of a window frame.
 *
 * @author deronj
 */
@ExperimentalAPI
public class WindowSwingHeader extends WindowSwing {
    
    private View2D view;

    public WindowSwingHeader (App2D app, Window2D parent, int width, int height, 
                              Vector2f pixelScale, String name, View2D view) {
        super(app, Window2D.Type.POPUP, parent, width, height, false, pixelScale, name);
        this.view = view;
    }

    /** The view which owns the frame to which this header belongs. */
    public View2D getView () {
        return view;
    }

    @Override
    protected void performFirstVisibleInitialization () {
        // No special first visible init for frame headers
    }
}
