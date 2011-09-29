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

import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.view.Gui2D;

/**
 * A 2D label which displays the window title.
 *
 * @author deronj
 */
@ExperimentalAPI
public class FrameLabelTitle extends FrameLabel {

    /** 
     * Create a new instance of FrameComponent.
     *
     * @param view The view the frame encloses.
     * @param gui The event handler.
     */
    public FrameLabelTitle(View2DCell view, Gui2D gui) {
        super("FrameLabelTitle", view, gui);
    }

    /**
     * Calculate the geometry layout.
     */
    @Override
    protected void updateLayout() {

        // The width of title is half of view width
        float viewWidth = view.getDisplayerLocalWidth();
        width = viewWidth / 2f;
        height = LABEL_HEIGHT;

        //x = Frame2DCell.SIDE_THICKNESS;
        x = -(viewWidth - width) / 2f;
        y = 0f;
    }
}
