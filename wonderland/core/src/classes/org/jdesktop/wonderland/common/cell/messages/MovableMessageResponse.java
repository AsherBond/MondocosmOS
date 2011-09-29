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
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.common.messages.MessageID;
import org.jdesktop.wonderland.common.messages.ResponseMessage;

/**
 * Message from the server indicating that a move request was modified. This
 * occurs if the server determines the cell is not permitted to move to the
 * desired position.
 * 
 * @author paulby
 */
@InternalAPI
public class MovableMessageResponse extends ResponseMessage {

    private Vector3f translation;
    private Quaternion rotation;
    private float scale=1f;

    /**
     * MOVE_MODIFIED - the final destination of the move requested by
     * the user has been modified by the server
     * 
     */
    public enum ActionType { MOVE_MODIFIED };
    
    private ActionType actionType;
    
    private MovableMessageResponse(MessageID messageID, ActionType actionType) {
        super(messageID);
        this.actionType = actionType;
    }
    
    public Vector3f getTranslation() {
        return translation;
    }

    private void setLocation(Vector3f locationVW) {
        this.translation = locationVW;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }

    /**
     * @return the scale
     */
    public float getScale() {
        return scale;
    }

    /**
     * @param scale the scale to set
     */
    public void setScale(float scale) {
        this.scale = scale;
    }

    public ActionType getActionType() {
        return actionType;
    }
    
    public static MovableMessageResponse newMoveModifiedMessage(MessageID msgID, Vector3f translation, Quaternion rotation, float scale) {
        MovableMessageResponse ret = new MovableMessageResponse(msgID, ActionType.MOVE_MODIFIED);
        ret.setLocation(translation);
        ret.setRotation(rotation);
        ret.setScale(scale);
        return ret;
    }
    

}
