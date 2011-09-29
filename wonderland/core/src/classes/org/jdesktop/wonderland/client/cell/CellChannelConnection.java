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
package org.jdesktop.wonderland.client.cell;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.ClientContext;
import org.jdesktop.wonderland.client.cell.CellCache.CellCacheListener;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.client.comms.BaseConnection;
import org.jdesktop.wonderland.client.comms.ResponseListener;
import org.jdesktop.wonderland.common.cell.CellChannelConnectionType;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.CellStatus;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.common.messages.ResponseMessage;

/**
 * Handler for Cell Channels. All data to/from cells is handled via this
 * class.
 * <p>
 * There is a potential ordering issues with cell channel messages. The
 * CellHierarchyMessages to load a cell are sent to a single client, and
 * thus are not sent over this channel. Therefore load messages can be
 * out-of-order with respect to the cell messages.  This handler maintains
 * queues of delayed messages for each cell, to ensure these messages are
 * delivered in the correct order.
 *
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
@InternalAPI
public class CellChannelConnection extends BaseConnection 
        implements CellCacheListener, CellStatusChangeListener
{
    private static Logger logger = Logger.getLogger(CellChannelConnection.class.getName());

    /** Executor to schedule queue removals */
    private static final ScheduledExecutorService executor =
            Executors.newSingleThreadScheduledExecutor();

    /** The delay (ms) to wait for cell messages after a cell is unloaded */
    private static final long CLEANUP_DELAY = 1000;

    /** The cell cache this connection is associated with */
    private CellCache cache;

    /** The loaded cells, with a delivery object for each one */
    private final Map<CellID, CellMessageDelivery> delivery =
            new HashMap<CellID, CellMessageDelivery>();

    public CellChannelConnection() {
        super();
    }
    
    /**
     * Get the type of client
     * @return CellChannelConnectionType.CELL_CLIENT_TYPE
     */
    public ConnectionType getConnectionType() {
        return CellChannelConnectionType.CLIENT_TYPE;
    }

    /**
     * Send a cell message to a specific cell on the server
     * @see org.jdesktop.wonderland.client.comms.WonderlandSession#send(WonderlandClient, Message)
     * 
     * @param message the cell message to send
     */
    public void send(CellMessage message) {
        super.send(message);
    }
    
    /**
     * Send a cell message to a specific cell on the server with the given
     * listener.
     * @see org.jdesktop.wonderland.client.comms.WonderlandSession#send(WonderlandClient, Message, ResponseListener)
     * 
     * @param message the message to send
     * @param listener the response listener to notify when a response
     * is received.
     */
    public void send(CellMessage message, ResponseListener listener) {
        super.send(message, listener);
    }
    
    /**
     * Send a cell messag to a specific cell on the server and wait for a 
     * response.
     * @see org.jdesktop.wonderland.client.comms.WonderlandSession#sendAndWait(WonderlandClient, Message)
     * 
     * @param message the message to send
     * @throws InterruptedException if there is a problem sending a message
     * to the given cell
     */
    public ResponseMessage sendAndWait(CellMessage message)
        throws InterruptedException
    {
        return super.sendAndWait(message);
    }

    /**
     * Set the cell cache for this connection
     * @param cache the cache
     */
    void setCellCache(CellCache cache) {
        this.cache = cache;
        
        // add a listener for dealing with cell delay queues
        cache.addCellCacheListener(this);
    }

    /**
     * Handle a message from the server
     * @param message the message to handle
     */
    public void handleMessage(Message message) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("Handling Message "+message.getClass().getName());
        }
        
        CellMessage cellMessage = (CellMessage)message;
        CellCache cellCache = ClientContext.getCellCache(getSession());
        
        if (cellCache==null) {
            logger.severe("Unable to deliver CellMessage, CellCache is null");
            return;
        }

        // deliver the message to the cell message delivery object for this
        // cell
        getCellMessageDelivery(cellMessage.getCellID()).deliver(cellMessage);
    }

    /**
     * Get the cell message delivery for a particular cell.  This method
     * always returns an object.  If there is no delivery object for the
     * given cellID, a new one is created.
     * @param cellID the cellID to get a deliver object for
     * @return the message delivery object for the given cell
     */
    protected synchronized CellMessageDelivery getCellMessageDelivery(CellID cellID) {
        CellMessageDelivery out = delivery.get(cellID);
        if (out == null) {
            out = new CellMessageDelivery();
            delivery.put(cellID, out);
        }

        return out;
    }

    /**
     * When a new cell is loaded, add a status listener to set the channel
     * when the cell becomes INACTIVE.
     */
    public void cellLoaded(CellID cellID, Cell cell) {
        cell.addStatusChangeListener(this);
    }

    /**
     * When a cell's status becomes inactive, set the channel object
     * in the delivery correctly.  This method is guaranteed to be called
     * after all cell component's status has been set, so it is safe to
     * assume that any message listeners that are going to be set up
     * will be set up.
     */
    public void cellStatusChanged(Cell cell, CellStatus status) {
        // if the cell is newly loaded, set its channel
        // Issue #405: wait until the cell becomes ACTIVE to deliver delayed
        // messages. Many cells and components don't register listeners until
        // they are set to ACTIVE, so delivering delayed messages before that
        // will cause messages to get dropped.
        if (status == CellStatus.ACTIVE) {
            ChannelComponent channel = cell.getComponent(ChannelComponent.class);
            CellMessageDelivery cmd = getCellMessageDelivery(cell.getCellID());
            if (channel == null) {
                logger.warning("No channel for cell " + cell.getCellID() +
                               ": ignoring messages.");

                // there is no channel for this cell.  Add a fake receiver
                // that will print out errors
                cmd.setMessageReceiver(new NoopMessageReceiver(cell.getCellID()));
            } else {
                // all good -- notify the delivery of this channel
                cmd.setMessageReceiver(new ChannelMessageReceiver(channel));
            }
        }

        // if the cell has been unloaded, make sure to clean up
        if (status == CellStatus.DISK) {
            cellUnloaded(cell.getCellID(), cell);
        }
    }

    public void cellLoadFailed(CellID cellID, String className,
                               CellID parentCellID,
                               Throwable cause)
    {
        // If a cell fails to load, add a receiver that won't store the
        // messages -- just drop them until the cell is unloaded
        logger.warning("Failed to load cell " + cellID + " of type " +
                       className +".  Discarding messages.");
        CellMessageDelivery cmd = getCellMessageDelivery(cellID);
        cmd.setMessageReceiver(new NoopMessageReceiver(cellID));
    }

    public void cellUnloaded(final CellID cellID, Cell cell) {
        // When a cell is unloaded, set the queue to a noop queue so
        // we will be notified of any messages that come after the
        // unload.  After a little bit of time, remove the delay queue
        // altogether so that a new queue will be created if the cell is
        // reloaded.
        // XXX this could cause messages to be lost if a cell is
        // repeatedly unloaded and reloaded in a short timespan XXX
        final CellMessageDelivery cmd;
        synchronized (this) {
            // don't create a new message delivery if its null
            cmd = delivery.get(cellID);
        }
        
        // if there was no record of the cell, just ignore
        if (cmd == null) {
            return;
        }
        
        // set up a queue to drop messages
        final MessageReceiver noopRecv = new NoopMessageReceiver(cellID);
        cmd.setMessageReceiver(noopRecv);
        
        // schedule a task to clean up
        executor.schedule(new Runnable() {
            public void run() {
                synchronized (CellChannelConnection.this) {
                    // make sure the queue hasn't changed in the
                    // interim
                    if (cmd.getMessageReceiver() == noopRecv) {
                        delivery.remove(cellID);
                    }
                }
            }
        }, CLEANUP_DELAY, TimeUnit.MILLISECONDS);
    }

    /**
     * Deliver messages to a particular cell.  This class may delay the
     * messages or send them directly, depending on the cell state.
     */
    protected static class CellMessageDelivery {
        private Queue<CellMessage> delayQueue;
        private MessageReceiver receiver;

        /**
         * Deliver a message to this cell.  If the cell is not fully
         * set up yet, this object will delay the message and deliver it
         * when the cell is entirely set up.
         */
        protected void deliver(CellMessage message) {
            synchronized (this) {
                // first check if there is a non-null delay queue.  If there
                // is, add the message to the queue so it will be ordered
                // correctly
                if (delayQueue != null) {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("Delaying message (existing queue) of " +
                                    "type " +
                                    message.getClass().getSimpleName() +
                                    " for " + message.getCellID());
                    }
                    
                    delayQueue.add(message);
                    return;
                }

                // if this cell doesn't have a channel set yet, it means
                // the cell isn't fully configured.  In that case, create
                // a delay queue and delay the message
                if (receiver == null) {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine("Delaying message (no receiver) of type " +
                                    message.getClass().getSimpleName() +
                                    " for " + message.getCellID());
                    }

                    logger.warning("Delaying messages for cell " +
                                   message.getCellID());

                    delayQueue = new LinkedList<CellMessage>();
                    delayQueue.add(message);
                    return;
                }

                // if we get here, the cell is in good shape to deliver
                // the message. Deliver the message outside the synchronized
                // block to avoid deadlocks
            }
            receiver.messageReceived(message);
        }

        /**
         * Set the receiver for this cell. This is done when the cell's
         * status becomes INACTIVE, indicating it is ready to start
         * receiving messages.
         * @param receiver the message receiver to send messages to, or null
         * to start delaying messages
         */
        protected void setMessageReceiver(MessageReceiver receiver) {
            // set the channel to this object
            synchronized (this) {
                this.receiver = receiver;
            }

            // deliver any delayed messages we have queued up
            deliverDelayedMessages();
        }

        /**
         * Get the message receiver for this cell
         * @return the message receiver for this cell.
         */
        protected synchronized MessageReceiver getMessageReceiver() {
            return receiver;
        }

        /**
         * Deliver any delayed messages to this cell.
         */
        protected void deliverDelayedMessages() {
            if (delayQueue != null && logger.isLoggable(Level.FINE)) {
                logger.fine("Delivering " + delayQueue.size() +
                            " delayed messages");
            }

            // loop until the queue is empty
            while (true) {
                CellMessage message;

                // get the first message from the delayed message from the
                // queue while holding the lock on this object.  This prevents
                // new messages from being added while we are checking.
                synchronized (this) {
                    // make sure there are delayed messages
                    if (delayQueue == null) {
                        return;
                    }

                    message = delayQueue.poll();

                    // if there are no more messages, remove the delay
                    // queue so that future messages will be sent directly
                    // to the channel
                    if (message == null) {
                        delayQueue = null;

                        // all done
                        break;
                    }

                    // deliver the message without holding the lock
                    receiver.messageReceived(message);
                }
            }
        }
    }

    /**
     * A message receiver
     */
    protected interface MessageReceiver {
        public void messageReceived(CellMessage message);
    }

    /**
     * Message receiver for a channel
     */
    private static class ChannelMessageReceiver implements MessageReceiver {
        private ChannelComponent channel;

        public ChannelMessageReceiver(ChannelComponent channel) {
            this.channel = channel;
        }

        public void messageReceived(CellMessage message) {
            channel.messageReceived(message);
        }
    }

    /**
     * A channel component implementation that ignores messages
     */
    private class NoopMessageReceiver implements MessageReceiver {
        private CellID cellID;

        public NoopMessageReceiver(CellID cellID) {
            this.cellID = cellID;
        }

        @Override
        public void messageReceived(CellMessage message) {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Ignoring message " + message + " for failed " +
                            "cell " + cellID);
            }
        }
    }
}
