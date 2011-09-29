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

import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import java.awt.event.MouseEvent;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.App2D;
import org.jdesktop.wonderland.modules.appbase.client.Window2D;
import org.jdesktop.wonderland.modules.appbase.client.swing.WindowSwing;
import org.jdesktop.wonderland.modules.appbase.client.view.View2D;
import org.jdesktop.wonderland.modules.appbase.client.view.WindowSwingHeader;

/**
 * The WindowSwing used by FrameHeaderSwing.
 *
 * @author deronj
 */
@ExperimentalAPI
class FrameHeaderSwingWindow extends WindowSwingHeader
{
    private FHSEventHook eventHook;

    FrameHeaderSwingWindow (App2D app, Window2D parent, int width, int height,
                            Vector2f pixelScale, String name, View2D view) {
        super(app, parent, width, height, pixelScale, name, view);
    }

    /** {@inheritDoc} */
    @Override
    public void cleanup () {
        super.cleanup();
        if (eventHook != null) {
            eventHook = null;
        }
    }
    
    @Override
    public WindowSwing.EventHook getEventHook () {
        // TODO: the following assumes that only secondary window can be dragged. Eventually 
        // we hope to support this feature for primary windows also.
        if (getView().getType() == View2D.Type.SECONDARY) {
            if (eventHook == null) {
                eventHook = new FHSEventHook(this);
            }
        }

        return eventHook;
    }

    WindowSwing.EventHookInfo getHookInfoForEvent (MouseEvent e) {
        if (eventHook != null) {
            return eventHook.getHookInfoForEvent(e);
        } else {
            return null;
        }
    }
}
