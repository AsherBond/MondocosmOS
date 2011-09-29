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
package org.jdesktop.wonderland.common.cell.messages;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellTransform;

/**
 *
 * @author paulby
 */
public class MovableAvatarMessage extends MovableMessage {

    private int trigger;
    private boolean pressed;
    private String animationName=null;
    private float height;
    private boolean collision;

    protected MovableAvatarMessage(CellID cellID, ActionType action) {
        super(cellID, action);
    }

    public static MovableAvatarMessage newMoveRequestMessage(CellID cellID, 
            Vector3f position, Quaternion rotation, int trigger, boolean pressed, 
            String animationName, float height, boolean collision) 
    {
        MovableAvatarMessage ret = new MovableAvatarMessage(cellID, ActionType.MOVE_REQUEST);
        ret.setTranslation(position);
        ret.setRotation(rotation);
        ret.setTrigger(trigger);
        ret.setPressed(pressed);
        ret.setAnimationName(animationName);
        ret.setHeight(height);
        ret.setCollision(collision);

        return ret;
    }

    public static CellMessage newMovedMessage(CellID cellID, 
            CellTransform transform, int trigger, boolean pressed, 
            String animationName, float height, boolean collision) 
    {
        MovableAvatarMessage ret = new MovableAvatarMessage(cellID, ActionType.MOVED);
        ret.setTranslation(transform.getTranslation(null));
        ret.setRotation(transform.getRotation(null));
        ret.setTrigger(trigger);
        ret.setPressed(pressed);
        ret.setAnimationName(animationName);
        ret.setHeight(height);
        ret.setCollision(collision);
        
        return ret;
    }

    /**
     * @return the trigger
     */
    public int getTrigger() {
        return trigger;
    }

    /**
     * @param trigger the trigger to set
     */
    protected void setTrigger(int trigger) {
        this.trigger = trigger;
    }

    /**
     * @return the pressed
     */
    public boolean isPressed() {
        return pressed;
    }

    /**
     * @param pressed the pressed to set
     */
    protected void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    protected void setAnimationName(String animationName) {
        this.animationName = animationName;
    }

    public String getAnimationName() {
        return animationName;
    }
    
    public float getHeight() {
        return height;
    }
    
    protected void setHeight(float height) {
        this.height = height;
    }
    
    public boolean isCollision() {
        return collision;
    }
    
    protected void setCollision(boolean collision) {
        this.collision = collision;
    }
}
