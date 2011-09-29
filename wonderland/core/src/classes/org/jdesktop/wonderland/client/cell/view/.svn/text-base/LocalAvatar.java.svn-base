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
import java.util.ArrayList;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.messages.ViewCreateResponseMessage;

/**
 * The Avatar that is local to this client. Local means it's controlled
 * by this client
 * 
 * @author paulby
 */
public class LocalAvatar implements ClientView {

    // The viewID will determine which view we get from the user on the server
    private String viewID = "DEFAULT";
    private ViewCell viewCell = null;
    private WonderlandSession session;
    private CellID viewCellID = null;
    
    private ArrayList<ViewCellConfiguredListener> configListeners = null;
    
    public LocalAvatar(WonderlandSession session) {
        this.session = session;
    }

    /**
     * A request from this client to move the avatar. This request is sent to 
     * the server, and if approved will be applied to the world
     * 
     * @param location
     * @param rotation
     */
    public void localMoveRequest(Vector3f location, Quaternion rotation) {
//        System.out.println("********************** LocalAvatar.localMoveRequest");
        if (viewCell!=null) {
            viewCell.localMoveRequest(new CellTransform(rotation, location));
        }
    }
    
    public String getViewID() {
        return viewID;
    }

    public void serverInitialized(ViewCreateResponseMessage msg) {
        // Nothing to do
    }

    public void viewCellConfigured(CellID cellID) {
        if (cellID==null)
            viewCell = null;
        else
            viewCell = (ViewCell) ClientContext.getCellCache(session).getCell(cellID);
        ClientContext.getCellCache(session).setViewCell(viewCell);
        notifyViewCellConfiguredListeners();
    }
    
    /**
     * Add a listener which will be notified when the cell is configured for this localAvatar
     * @param listener
     */
    public void addViewCellConfiguredListener(ViewCellConfiguredListener listener) {
        synchronized(this) {
            if (configListeners==null)
                configListeners = new ArrayList();
            configListeners.add(listener);
        }
    }

    public void removeViewCellConfiguredListener(ViewCellConfiguredListener listener) {
        synchronized (this) {
            if (configListeners != null) {
                configListeners.remove(listener);
            }
        }
    }
    
    private void notifyViewCellConfiguredListeners() {
        if (configListeners==null)
            return;
        
        synchronized(this) {
            for(ViewCellConfiguredListener l : configListeners)
                l.viewConfigured(this);
        }
    }
    
    /**
     * Return the ViewCell associated with this avatar, or null if the ViewCell
     * has not been configured yet.
     * 
     * @return
     */
    public ViewCell getViewCell() {
        return viewCell;
    }

    /**
     * A listener interface for notification when the ViewCell is attached
     * to this LocalAvatar
     */
    public interface ViewCellConfiguredListener {
        public void viewConfigured(LocalAvatar localAvatar);
    }
}
