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
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ChannelManager;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.Delivery;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.cell.CellChannelConnectionType;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.server.WonderlandContext;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

/**
 * Channel component is the basic component cells use to manage communication.
 * The channel contained in the channel component contains subscriptions for
 * all views that have this cell in their cache.  It can be used to send
 * messages to all clients with a given cell in view.
 * <p>
 * The channel component appears as a single Channel for each cell in the
 * system, but in reality, this may be optimized so that multiple cells
 * with the same set of viewers share a channel.  This channel sharing is
 * good for cells without a lot of communication, but cells that send lots
 * of messages should force a local channel.  This can be done by calling
 * the <code>addLocalChannelRequest()</code> method. As long as there are any
 * outstanding local channel requests, the cell will force the use of a local
 * channel.
 *
 * @author Paul Byrne <paulby@dev.java.net>
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class ChannelComponentMO extends CellComponentMO {    
    /** the connection to send messages over */
    private WonderlandClientSender cellSender;

    /**
     * A reference to the channel to send on.  This may be a local channel
     * or may be shared with the cell's parent.
     */
    private ManagedReference<Channel> cellChannelRef;

    /**
     * A set of message receivers, as a map from the class of message it
     * handles to a message receiver. Only a single receiver may be registed
     * for a given type of message.
     */
    private final Map<Class, ComponentMessageReceiver> messageReceivers =
            new HashMap<Class, ComponentMessageReceiver>();

    /**
     * The Set of requests to use a local channel.  When this set is empty,
     * the channel may be coalesced with the parent channel.
     */
    private final Set<String> localRequests = new HashSet<String>();

    /**
     * Create a new ChanneComponentMO.
     * @param cell the cell this component is attached to
     */
    public ChannelComponentMO(CellMO cell) {
        super(cell);
    }

    /**
     * Open the channel for this component.  
     */
    protected void openChannel() {
        CellMO cell = cellRef.get();

        ChannelManager cm = AppContext.getChannelManager();
        Channel cellChannel = cm.createChannel("Cell "+cell.getCellID().toString(),
                                               null,
                                               Delivery.RELIABLE);

        DataManager dm = AppContext.getDataManager();
        cellChannelRef = dm.createReference(cellChannel);

        // cache the sender for sending to cell clients.  This saves a
        // Darkstar lookup for every cell we want to send to.
        cellSender = WonderlandContext.getCommsManager().getSender(CellChannelConnectionType.CLIENT_TYPE);
    }

    /**
     * Close the channel for this component.
     */
    protected void closeChannel() {
        DataManager dm = AppContext.getDataManager();
        Channel channel = cellChannelRef.get();
        dm.removeObject(channel);

        cellSender=null;
        cellChannelRef = null;
    }

    @Override
    protected void setLive(boolean live) {
        AppContext.getDataManager().markForUpdate(this);
        if (live)
            openChannel();
        else
            closeChannel();
    }

    /**
     * Send message to all clients on this channel
     * @param senderID the id of the sender session, or null if this
     * message being sent by the server
     * @param message
     *
     */
    public void sendAll(WonderlandClientID senderID, CellMessage message) {
        if (cellChannelRef==null) {
            return;
        }

        if (message.getCellID() == null) {
            message.setCellID(cellID);
        }

        if (senderID != null) {
            message.setSenderID(senderID.getID());
        }

        cellSender.send(cellChannelRef.get(), message);
    }

    /**
     * Add a request for a local channel.  Request names are, by convention,
     * the class name of the component making the request.  If a cell wants
     * to always force a local channel, it can use its own classname in the
     * request.  As long as there are any requests outstanding, a local
     * channel will be created.
     * XXX NOT IMPLEMENTED XXX
     * @param request the request to add, by convention the classname of the
     * requesting class
     */
    public void addLocalChannelRequest(String request) {
        localRequests.add(request);
    }

    /**
     * Remove a request for a local channel.
     * @param request the request to remove
     */
    public void removeLocalChannelRequest(String request) {
        localRequests.remove(request);
    }

    /**
     * Add user to the cells channel, if there is no channel simply return
     * @param userID
     */
    public void addUserToCellChannel(WonderlandClientID clientID) {
        if (cellChannelRef == null)
            return;

        // issue 963: the session may be null if the user is logging out
        ClientSession session = clientID.getSession();
        if (session != null) {
            cellChannelRef.getForUpdate().join(session);
        }
    }

    /**
     * Remove user from the cells channel
     * @param userID
     */
    public void removeUserFromCellChannel(WonderlandClientID clientID) {
        if (cellChannelRef == null)
            return;

        // issue 963: the session may be null if the user is logging out
        ClientSession session = clientID.getSession();
        if (session != null) {
            cellChannelRef.getForUpdate().leave(session);
        }
    }

    /**
     * Get a list of all members of the given channel
     * @return an iterator of channel members
     */
    public Iterator<WonderlandClientID> getChannelMembers() {
        final Iterator<ClientSession> sessions =
                cellChannelRef.get().getSessions();

        // wrap the iterator that returns ClientSessions into one
        // that returns WonderlandClientID.  Note that the
        return new Iterator<WonderlandClientID>() {
            public boolean hasNext() {
                return sessions.hasNext();
            }

            public WonderlandClientID next() {
                return new WonderlandClientID(sessions.next());
            }

            public void remove() {
                sessions.remove();
            }
        };
    }

    /**
     * Register a receiver for a specific message class. Only a single receiver
     * is allowed for each message class, calling this method to add a duplicate
     * receiver will cause an IllegalStateException to be thrown.
     *
     * @param msgClass
     * @param receiver
     */
    public void addMessageReceiver(Class<? extends CellMessage> msgClass, 
                                   ComponentMessageReceiver receiver)
    {
        if (receiver instanceof ManagedObject) {
            receiver = new ManagedComponentMessageReceiver(receiver);
        }

        Object old = messageReceivers.put(msgClass, receiver);
        if (old != null) {
            throw new IllegalStateException("Duplicate Message class added "+msgClass);
        }
    }

    public void removeMessageReceiver(Class<? extends CellMessage> msgClass) {
        messageReceivers.remove(msgClass);
    }

    @Override
    protected String getClientClass() {
        return "org.jdesktop.wonderland.client.cell.ChannelComponent";
    }

    /**
     * Dispatch messages to any receivers registered for the particular message class
     * @param sender
     * @param session
     * @param message
     */
    public void messageReceived(WonderlandClientSender sender,
                                WonderlandClientID clientID,
                                CellMessage message ) {

        CellID msgCellID = message.getCellID();
        if (!msgCellID.equals(cellID)) {
//            ManagedReference<ChannelComponentRefMO> refMO = refChannelComponents.get(msgCellID);
//            if (refMO==null) {
//                Logger.getAnonymousLogger().severe("Unable to find ChannelComponentRef for cell "+msgCellID);
//                return;
//            }
//            refMO.get().messageReceived(sender, clientID, message);
            Logger.getAnonymousLogger().severe("Message delivered to wrong ChannelComponent");
            return;
        }

        ComponentMessageReceiver recv = messageReceivers.get(message.getClass());
        if (recv == null) {
            Logger.getAnonymousLogger().warning("No listener for message " +
                                                message.getClass());
            return;
        }

        recv.messageReceived(sender, clientID, message);
        recv.recordMessage(sender, clientID, message);
    }

    /**
     * A listener to notify when messages of a certain class are recevied
     * by this cell.  Each message class can only be handled by a single
     * receiver type, but a single receiver can handle lots of different
     * types of message.
     * <p>
     * By default, all receivers are Serializable.  These receivers do not
     * keep state -- it is reset to the original state for every message
     * that is recevied.  If a receiver needs to store state, it should
     * implement the ManagedObject interface.  Be aware that this may have
     * performance impacts for messages that are received frequently.
     */
    public static interface ComponentMessageReceiver extends Serializable {
        public void messageReceived(WonderlandClientSender sender, 
                                    WonderlandClientID clientID,
                                    CellMessage message );
        
        /**
         * Record the message--as part of the event recording mechanism.
         * @param sender the sender of the message
         * @param clientID the id of the client sending the message
         * @param message
         */
        public void recordMessage(WonderlandClientSender sender,
                                    WonderlandClientID clientID,
                                    CellMessage message );
    }

    /**
     * A ManagedObject wrapper for a ComponentMessageReceiver
     */
    private static class ManagedComponentMessageReceiver
            implements ComponentMessageReceiver
    {
        private ManagedReference<ComponentMessageReceiver> receiverRef;

        // a transient copy of the receiver. If the receiver is removed in
        // the call to messageReceived (for example when removing a cell from
        // the world), the subsequent call to recordMessage will fail since
        // the ManagedReference is no longer valid.  This transient object
        // is therefore used to store the value of the receiver within a
        // single transaction
        private transient ComponentMessageReceiver receiver;

        public ManagedComponentMessageReceiver(ComponentMessageReceiver receiver) {
            receiverRef = AppContext.getDataManager().createReference(receiver);
        }


        public void messageReceived(WonderlandClientSender sender,
                                    WonderlandClientID clientID,
                                    CellMessage message)
        {
            if (receiver == null) {
                receiver = receiverRef.get();
            }

            receiver.messageReceived(sender, clientID, message);
        }

        public void recordMessage(WonderlandClientSender sender,
                                  WonderlandClientID clientID,
                                  CellMessage message)
        {
            if (receiver == null) {
                receiver = receiverRef.get();
            }

            receiver.recordMessage(sender, clientID, message);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof ComponentMessageReceiver)) {
                return false;
            }

            if (o instanceof ManagedComponentMessageReceiver) {
                return receiverRef.equals(((ManagedComponentMessageReceiver) o).receiverRef);
            }

            return receiverRef.get().equals(o);
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 97 * hash + (this.receiverRef != null ? this.receiverRef.hashCode() : 0);
            return hash;
        }
    }
}
