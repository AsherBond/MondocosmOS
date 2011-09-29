/**
 * Open Wonderland
 *
 * Copyright (c) 2011, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.client.cell.view;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.cell.CellCache;
import org.jdesktop.wonderland.client.cell.CellRenderer;
import org.jdesktop.wonderland.client.jme.AvatarRenderManager.RendererUnavailable;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.ViewManager;
import org.jdesktop.wonderland.client.jme.cellrenderer.AvatarJME;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.state.CellClientState;

/**
 * A cell representing the users avatar
 * 
 * @author paulby
 */
public class AvatarCell extends ViewCell {

    private boolean selectedForInput = false;

    public AvatarCell(CellID cellID, CellCache cellCache) {
        super(cellID, cellCache);
    }

    /**
     * TODO this is a temporary interface for handling avatar actions, need
     * to uplevel
     * @param trigger
     * @param pressed
     */
    public void triggerAction(int trigger, boolean pressed, String animationName) {
        if (ClientContext.getRendererType()==RendererType.RENDERER_JME) {
            CellRenderer rend = getCellRenderer(RendererType.RENDERER_JME);
            if (rend instanceof AvatarActionTrigger) {
                ((AvatarActionTrigger)rend).trigger(trigger, pressed, animationName);
            }
        }
    }

    /**
     * TODO is a temporary interface for handling avatar actions, need
     * to uplevel
     * @param trigger
     * @param pressed
     */
    public void triggerCollision(float height, boolean collision) {
        if (ClientContext.getRendererType()==RendererType.RENDERER_JME) {
            CellRenderer rend = getCellRenderer(RendererType.RENDERER_JME);
            if (rend instanceof AvatarActionTrigger) {
                ((AvatarActionTrigger)rend).triggerCollision(height, collision);
            }
        }
    }
    
    /**
     * TODO this is a temporary interface for handling avatar goto, need
     * to uplevel
     */
    public void triggerGoto(Vector3f worldPosition, Quaternion look) {
        if (ClientContext.getRendererType()==RendererType.RENDERER_JME) {
            CellRenderer rend = getCellRenderer(RendererType.RENDERER_JME);
            if (rend instanceof AvatarActionTrigger) {
                ((AvatarActionTrigger)rend).triggerGoto(worldPosition, look);
            }
        }
    }
    
    @Override
    public void setClientState(CellClientState cellClientState) {
        super.setClientState(cellClientState);
    }

    @Override
    protected CellRenderer createCellRenderer(RendererType rendererType) {
        CellRenderer ret = null;
        switch(rendererType) {
            case RENDERER_2D :
                // No 2D Renderer yet
                break;
            case RENDERER_JME :
                if (ViewManager.getViewManager().useAvatars) {
                    try {
                        ServerSessionManager session = getCellCache().getSession().getSessionManager();
                        ret = ClientContextJME.getAvatarRenderManager().createRenderer(session, this);
                    } catch (RendererUnavailable ex) {
                        Logger.getLogger(AvatarCell.class.getName()).log(Level.SEVERE, null, ex);
                        ret = new AvatarJME(this);
                    }
                } else {
                    ret = new AvatarJME(this);
                }
                break;                
        }
        
        return ret;
    }

    /**
     * @return the selectedForInput
     * @InternalAPI
     */
    public boolean isSelectedForInput() {
        return selectedForInput;
    }

    /**
     * @param selectedForInput the selectedForInput to set
     * @InternalAPI
     */
    public void setSelectedForInput(boolean selectedForInput) {
        this.selectedForInput = selectedForInput;
    }

    /**
     * Temporary interface, used to expose the IMI actions until we uplevel them
     */
    public interface AvatarActionTrigger {
        public void trigger(int trigger, boolean pressed, String animationName);
    
        // trigger for movement.  Also needs to be uplevelled.
        public void triggerGoto(Vector3f worldPosition, Quaternion look);
    
        // trigger for shared gravity and collision
        public void triggerCollision(float height, boolean collision);
    }
}
