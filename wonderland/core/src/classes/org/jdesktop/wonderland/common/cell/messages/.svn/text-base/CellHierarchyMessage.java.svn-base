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

import com.jme.bounding.BoundingVolume;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.state.CellClientState;
import org.jdesktop.wonderland.common.messages.Message;

/**
 * Superclass for messages that change the cell hierarchy in the cell cache
 * 
 * @author paulby
 */
@InternalAPI
public class CellHierarchyMessage extends Message {
    
    protected ActionType msgType;
    protected CellID cellID;
    private CellID parentID;
    private BoundingVolume localBounds;
    private BoundingVolume computedBounds;
    private String cellClassName;
    private CellTransform cellTransform;
    private CellClientState setupData;
    private String avatarID;
    private String cellName;
    
    private CellTransform cellLocal2VW;      // FOR TESTING - TODO REMOVE

    /**
     * SET_VIEW - client informs server which avatar to use for a cell cache
     * LOAD_CLIENT_AVATAR - server informs client to load its avatar
     */
    public enum ActionType { LOAD_CELL, UNLOAD_CELL, CHANGE_PARENT,
        DELETE_CELL, CONFIGURE_CELL, SET_VIEW, LOAD_CLIENT_AVATAR};
    
    /**
     * Creates a new instance of CellHierarchyMessage 
     *
     * 
     * @param cellClassName Fully qualified classname for cell
     */
    public CellHierarchyMessage(ActionType msgType, 
                                String cellClassName, 
                                BoundingVolume localBounds, 
                                CellID cellID, 
                                CellID parentID,
                                CellTransform cellTransform,
                                CellClientState setupData,
                                String cellName) {
        this.msgType = msgType;
        this.cellClassName = cellClassName;
        this.cellID = cellID;
        this.parentID = parentID;
        this.localBounds = localBounds;
        this.cellTransform = cellTransform;
        this.setupData = setupData;
        this.cellName = cellName;
    }
    
    private CellHierarchyMessage(ActionType msgType) {
        this.msgType = msgType;
    }
    
    public CellHierarchyMessage() {
    }
    
    /**
     * Return the action type of this message
     * @return
     */
    public ActionType getActionType() {
        return msgType;
    }

    public CellID getCellID() {
        return cellID;
    }

    public CellID getParentID() {
        return parentID;
    }

    public BoundingVolume getLocalBounds() {
        return localBounds;
    }

    public BoundingVolume getComputedBounds() {
        return computedBounds;
    }
    
    public String getCellClassName() {
        return cellClassName;
    }

    public CellTransform getCellTransform() {
        return cellTransform;
    }

    public CellClientState getSetupData() {
        return setupData;
    }

    private void setViewID(String avatarID) {
        this.avatarID = avatarID;
    }
    
    /**
     * The View to which this cache is tied.
     * @return
     */
    public String getViewID() {
        return avatarID;
    }
    
    public String getCellName() {
        return cellName;
    }

    /**
     * FOR TESTING
     * TODO REMOVE
     * @return local2VW
     */
//    public CellTransform getCellLocal2VW() {
//        return cellLocal2VW;
//    }

    /**
     * FOR TESTING
     * TODO REMOVE
     * @param cellLocal2VW local2VW to set
     */
//    public void setCellLocal2VW(CellTransform cellLocal2VW) {
//        this.cellLocal2VW = cellLocal2VW;
//    }


    public static CellHierarchyMessage newSetAvatarMessage(String avatarID) {
        CellHierarchyMessage ret = new CellHierarchyMessage(ActionType.SET_VIEW);
        ret.setViewID(avatarID);
        return ret;
    }
    
    public static CellHierarchyMessage newLoadClientAvatar(CellID cellID) {
        CellHierarchyMessage ret = new CellHierarchyMessage(ActionType.LOAD_CLIENT_AVATAR);
        return ret;
       
    }
}
