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
package org.jdesktop.wonderland.server.cell;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.common.cell.messages.MovableMessage;
import org.jdesktop.wonderland.server.cell.ChannelComponentMO.ComponentMessageReceiver;
import org.jdesktop.wonderland.server.cell.annotation.UsesCellComponentMO;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;
import org.jdesktop.wonderland.server.eventrecorder.RecorderManager;

/**
 *
 * @author paulby
 */
public class MovableComponentMO extends CellComponentMO {

    @UsesCellComponentMO(ChannelComponentMO.class)
    protected ManagedReference<ChannelComponentMO> channelComponentRef = null;
    
    /**
     * Create a MovableComponent for the given cell. The cell must already
     * have a ChannelComponent otherwise this method will throw an IllegalStateException
     * @param cell
     */
    public MovableComponentMO(CellMO cell) {
        super(cell);        
    }
    
    @Override
    public void setLive(boolean live) {
        super.setLive(live);

        if (live) {
            channelComponentRef.getForUpdate().addMessageReceiver(getMessageClass(), new ComponentMessageReceiverImpl(this));
        } else {
            channelComponentRef.getForUpdate().removeMessageReceiver(getMessageClass());
        }
    }

    protected Class getMessageClass() {
        return MovableMessage.class;
    }
    
    void moveRequest(WonderlandClientID clientID, MovableMessage msg) {
        moveRequest(clientID, msg.getCellTransform());
    }

    @Override
    protected String getClientClass() {
        return "org.jdesktop.wonderland.client.cell.MovableComponent";
    }

    /**
     * Set the transform for the cell and notify all client cells of the move.
     * @param sessionID the id of the session that originated the move, or null
     * if the server originated it
     * @param transform
     */
    public void moveRequest(WonderlandClientID clientID, CellTransform transform) {

        CellMO cell = cellRef.getForUpdate();
        ChannelComponentMO channelComponent;
        cell.setLocalTransform(transform);
        

        if (cell.isLive()) {
            channelComponent = channelComponentRef.getForUpdate();
            channelComponent.sendAll(clientID, MovableMessage.newMovedMessage(cell.getCellID(), transform));
        }
    }
    
    /**
     * Listener inteface for cell movement
     */
    public interface CellTransformChangeListener extends ManagedObject {
        public void transformChanged(CellMO cell, CellTransform transform);
    }

    private static class ComponentMessageReceiverImpl implements ComponentMessageReceiver {

        private ManagedReference<MovableComponentMO> compRef;
        
        public ComponentMessageReceiverImpl(MovableComponentMO comp) {
            compRef = AppContext.getDataManager().createReference(comp);
        }

        public void messageReceived(WonderlandClientSender sender, WonderlandClientID clientID, CellMessage message) {
            MovableMessage ent = (MovableMessage) message;
            
//            System.out.println("MovableComponentMO.messageReceived "+ent.getActionType());
            switch (ent.getActionType()) {
                case MOVE_REQUEST:
                    // TODO check permisions
                    
                    compRef.getForUpdate().moveRequest(clientID, ent);

                    // Only need to send a response if the move can not be completed as requested
                    //sender.send(session, MovableMessageResponse.newMoveModifiedMessage(ent.getMessageID(), ent.getTranslation(), ent.getRotation()));
                    break;
                case MOVED:
                    Logger.getAnonymousLogger().severe("Server should never receive MOVED messages");
                    break;
            }
        }

         /**
         * Record the message -- part of the event recording mechanism.
         * Nothing more than the message is recorded in this implementation, delegate it to the recorder manager
         */
        public void recordMessage(WonderlandClientSender sender, WonderlandClientID clientID, CellMessage message) {
            RecorderManager.getDefaultManager().recordMessage(sender, clientID, message);
        }
    }
}
