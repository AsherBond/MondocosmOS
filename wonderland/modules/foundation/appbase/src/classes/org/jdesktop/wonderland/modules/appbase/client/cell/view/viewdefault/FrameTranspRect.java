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

import com.jme.renderer.ColorRGBA;
import com.jme.scene.state.BlendState;
import com.jme.scene.state.RenderState;
import java.util.logging.Logger;
import org.jdesktop.mtgame.RenderUpdater;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.modules.appbase.client.view.Gui2D;

/**
 * A transparent non-textured rectangle frame component. 
 *
 * @author deronj
 */
@ExperimentalAPI
public class FrameTranspRect extends FrameRect {

    private static final Logger logger = Logger.getLogger(FrameTranspRect.class.getName());

    /** 
     * Create a new instance of <code>FrameTranspRect</code> with a default name and with the specified size.
     *
     * @param view The view the frame encloses.
     * @param gui The event handler.
     * @param width The width of rectangle in local coordinates.
     * @param height The height of rectangle in local coordinates.
     */
    public FrameTranspRect(View2DCell view, Gui2D gui, float width, float height) {
        super("FrameTranspRect", view, gui, width, height);
    }

    /** 
     * Create a new instance of <code>FrameTranspRect</code> with the specified size.
     *
     * @param name The name of the node.
     * @param view The view the frame encloses.
     * @param gui The event handler.
     * @param width The width of rectangle in local coordinates.
     * @param height The height of rectangle in local coordinates.
     */
    public FrameTranspRect(String name, View2DCell view, Gui2D gui, float width, float height) {
        super(name, view, gui, width, height);
    }

    /**
     * Specify the color and opacity of this rectangle. The percentage opacity is in the
     * a field of the argument color.
     */
    public void setColorAndOpacity (final ColorRGBA color) {
        setColor(color);

        final WorldManager wm = ClientContextJME.getWorldManager();
        wm.addRenderUpdater(new RenderUpdater() {
            public void update(Object arg0) {

                BlendState as = (BlendState) 
                    wm.getRenderManager().createRendererState(RenderState.StateType.Blend);

                // activate blending
                as.setBlendEnabled(true);
                // set the source function
                as.setSourceFunction(BlendState.SourceFunction.SourceAlpha);
                // set the destination function
                as.setDestinationFunction(BlendState.DestinationFunction.OneMinusSourceAlpha);
                // disable test
                as.setTestEnabled(false);
                // activate the blend state
                as.setEnabled(true);

                // assign the blender state to the node
                quad.setRenderState(as);
                quad.updateRenderState();

                ColorRGBA color = quad.getDefaultColor();
                quad.setDefaultColor(color);

                wm.addToUpdateList(quad);
            }
        }, null);
    }

    /** 
     * This is a no-op for this subclass type.
     */
    @Override
    protected void updateColor() {
        // No-op
    }
}

