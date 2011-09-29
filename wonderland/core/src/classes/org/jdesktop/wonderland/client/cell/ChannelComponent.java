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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.comms.ClientConnection.Status;
import org.jdesktop.wonderland.client.comms.ResponseListener;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.common.messages.ResponseMessage;

/**
 *
 * A Component that provides a cell specific communication channel with 
 * the server.
 * 
 * @author paulby
 */
@ExperimentalAPI
public class ChannelComponent extends CellComponent {
    private static final Logger logger =
            Logger.getLogger(ChannelComponent.class.getName());

    /** receivers for each message type */
    private final Map<Class, ComponentMessageReceiver> messageReceivers =
            new LinkedHashMap<Class, ComponentMessageReceiver>();

    /** the connection to send on */
    private CellChannelConnection connection;

    public ChannelComponent(Cell cell) {
        super(cell);

        setCellChannelConnection(cell.getCellCache().getCellChannelConnection());
    }

    /**
     * Notification of the CellChannelConnection to use when sending
     * data to the server for this cell.  This method will be called
     * automatically at cell creation time.
     */
    public void setCellChannelConnection(CellChannelConnection connection) {
        this.connection = connection;
    }

    /**
     * Register a receiver for a specific message class. Only a single receiver
     * is allowed for each message class, calling this method to add a duplicate
     * receiver will cause an IllegalStateException to be thrown.
     *
     * @param msgClass
     * @param receiver
     */
    public synchronized void addMessageReceiver(Class<? extends CellMessage> msgClass, ComponentMessageReceiver receiver) {
        Object old = messageReceivers.put(msgClass, receiver);

        // XXX hack to ignore duplicate registrations XXX
        if (old != null && old != receiver)
            throw new IllegalStateException("Duplicate Message class added "+msgClass);
    }

    /**
     * Remove the message receiver listening on the specifed message class
     * @param msgClass
     */
    public synchronized void removeMessageReceiver(Class<? extends CellMessage> msgClass) {
        messageReceivers.remove(msgClass);
    }

    /**
     * Get a message receiver for the given class
     * @param msgClass the class of message to get a receiver for
     */
    protected synchronized ComponentMessageReceiver getMessageReceiver(Class<? extends CellMessage> msgClass) {
        return messageReceivers.get(msgClass);
    }

    /**
     * Dispatch messages to any receivers registered for the particular message class
     * @param message
     */
    public void messageReceived(CellMessage message ) {
        if (logger.isLoggable(Level.FINEST)) {
            logger.finest("---> Impl received message for " + 
                          message.getCellID() + "   impl cell " + 
                          cell.getCellID() + "  recievers " + 
                          messageReceivers.size());
        }

        // make sure the message is being delivered to the right cell
        if (!message.getCellID().equals(cell.getCellID())) {
            logger.severe("Message for wrong cell " + message.getCellID());
            return;
        }

        // if we get here, we can actually deliver the message
        ComponentMessageReceiver recvRef = getMessageReceiver(message.getClass());
        if (recvRef == null) {
            logger.warning("No listener for message " + message.getClass() +
                           " from cell " + cell.getClass().getName() +
                           " status " + cell.getStatus());
            return;
        }

        recvRef.messageReceived(message);
    }

    public Status getStatus() {
        return connection.getStatus();
    }

    public void send(CellMessage message, ResponseListener listener) {
        if (message.getCellID() == null) {
            message.setCellID(cell.getCellID());
        }
        connection.send(message, listener);
    }

    public void send(CellMessage message) {
        if (message.getCellID() == null) {
            message.setCellID(cell.getCellID());
        }
        connection.send(message);
    }

    public ResponseMessage sendAndWait(CellMessage message)
        throws InterruptedException
    {
        if (message.getCellID() == null) {
            message.setCellID(cell.getCellID());
        }
        return connection.sendAndWait(message);
    }
    
    static public interface ComponentMessageReceiver {
        public void messageReceived(CellMessage message );        
    }
}
