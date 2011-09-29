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
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.security.MoveAction;
import org.jdesktop.wonderland.common.security.annotation.Actions;

/**
 * Messages to/from MovableCells
 * 
 * @author paulby
 */
@InternalAPI
@Actions(MoveAction.class)
public class MovableMessage extends CellMessage {

    private Vector3f translation;
    private Quaternion rotation;
    private float scale=1f;

    /**
     * MOVE_REQUEST - client asking the server to move cell
     * MOVED - server informing clients cell has moved
     * 
     */
    public enum ActionType { MOVED, MOVE_REQUEST };
    
    private ActionType actionType;
    
    protected MovableMessage(CellID cellID, ActionType actionType) {
        super(cellID);
        this.actionType = actionType;
    }
    
    public Vector3f getTranslation() {
        return translation;
    }

    protected void setTranslation(Vector3f locationVW) {
        this.translation = locationVW;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    protected void setRotation(Quaternion orientation) {
        this.rotation = orientation;
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
    
    public static MovableMessage newMovedMessage(CellID cellID, CellTransform transform) {
        MovableMessage ret = new MovableMessage(cellID, ActionType.MOVE_REQUEST);
        ret.setTranslation(transform.getTranslation(null));
        ret.setRotation(transform.getRotation(null));
        ret.setScale(transform.getScaling());
        return ret;
    }
    
     public static MovableMessage newMovedMessage(CellID cellID, Vector3f translation, Quaternion rotation) {
         return newMovedMessage(cellID, translation, rotation, 1f);
     }

     public static MovableMessage newMovedMessage(CellID cellID, Vector3f translation, Quaternion rotation, float scale) {
        MovableMessage ret = new MovableMessage(cellID, ActionType.MOVED);
        ret.setTranslation(translation);
        ret.setRotation(rotation);
        ret.setScale(scale);
        return ret;
    }
     
    public static MovableMessage newMoveRequestMessage(CellID cellID, CellTransform transform) {
        return newMoveRequestMessage(cellID, transform.getTranslation(null), transform.getRotation(null), transform.getScaling());
    }

    public static MovableMessage newMoveRequestMessage(CellID cellID, Vector3f translation, Quaternion rotation) {
        return newMoveRequestMessage(cellID, translation, rotation, 1f);
    }

    public static MovableMessage newMoveRequestMessage(CellID cellID, Vector3f translation, Quaternion rotation, float scale) {
        MovableMessage ret = new MovableMessage(cellID, ActionType.MOVE_REQUEST);
        ret.setTranslation(translation);
        ret.setRotation(rotation);
        ret.setScale(scale);
        return ret;
    }

    /**
     * Return a new CellTransform with the values from this MovableMessage
     * @return
     */
    public CellTransform getCellTransform() {
        return new CellTransform(rotation, translation, scale);
    }
}
