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
package org.jdesktop.wonderland.client.comms;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.common.messages.MessageID;
import org.jdesktop.wonderland.common.messages.ResponseMessage;

/**
 * A basic Wonderland Connection that can be extended to develop real connections.
 * @author jkaplan
 */
@ExperimentalAPI
public abstract class BaseConnection implements ClientConnection {
    /** the current status */
    private Status status = Status.DISCONNECTED;
    
    /** the session we are connected to, or null if we are not connected to
     * any sessions. */
    private WonderlandSession session;
    
    /** outstanding message listeners, mapped by id */
    private Map<MessageID, ResponseListener> responseListeners =
            Collections.synchronizedMap(
                    new HashMap<MessageID, ResponseListener>());
   
    public WonderlandSession getSession() {
        return session;
    }
    
    public synchronized Status getStatus() {
        return status;
    }
    
    /**
     * Set the status
     * @param status the new status
     */
    protected synchronized void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Connect this client to the given session.  Identical to calling
     * <code>session.connect(this)</code>
     * @param session the session to connect to
     * @throws ConnectionFailureException if there is a problem connecting
     */
    public void connect(WonderlandSession session) 
            throws ConnectionFailureException
    {
        session.connect(this);
    }
    
    /**
     * Connect this client to the given session with the given properties.  
     * Identical to calling <code>session.connect(this, properties)</code>
     * @param session the session to connect to
     * @param properties the properties to use while connecting
     * @throws ConnectionFailureException if there is a problem connecting
     */
    public void connect(WonderlandSession session, Properties properties) 
            throws ConnectionFailureException
    {
        session.connect(this, properties);
    }
    
    public synchronized void connected(WonderlandSession session) {
        this.session = session;
        
        setStatus(Status.CONNECTED);
    }

    /**
     * Disconnect from the current session
     */
    public void disconnect() {
        getSession().disconnect(this);
    }
    
    public void disconnected() {
        setStatus(status.DISCONNECTED);
    }
    
    /**
     * When a message is received, do the basic processing needed to 
     * determine if it is a response to a message.  If it is a response,
     * notify the relevant listeners.  If not, forward it on to the
     * <code>handleMessage()</code> method.
     * @param message the message to handle
     */
    public void messageReceived(Message message) {
        // see if we are waiting for a response
        if (message instanceof ResponseMessage &&
                notifyResponseListener((ResponseMessage) message))
        {
            // if notifyResponseListener returned true, it means there
            // was a response listener waiting for the response
            // to the given message, so we should stop processing
            // here
            return;
        }

        // forward to the client
        handleMessage(message);
    }
    
    /**
     * Handle a message sent from the server.  This method is called when
     * a message is received that is not a response message.
     * @param message the message to handle
     */
    public abstract void handleMessage(Message message);
    
    /**
     * Send a message to the server over the session channel on behalf of this 
     * client.  Identical to calling send(message, null).
     * @param message the message to send
     * @throws MessageException if there is an error getting the bytes
     * for the message
     * @throws IllegalStateException if this client is not in the 
     * CONNECTED state or the session this client is connected to is not
     * in the CONNECTED state
     */
    protected void send(Message message) {
        send(message, null);
    }
   
     /**
     * Send a message to the server over the session channel and provide a
     * listener.  The message is sent on behalf of this client.
     * The client must be successfully connected to this session in order
     * for the send to work.
     * <p>
     * The given listener will be notified when a response is received. If
     * the listener is not null, it is critical that the server guarantees 
     * that either a ResponseMessage or an ErrorMessage will be sent in response
     * to this message, otherwise this will cause a memory leak.
     * <p>
     * Note that listeners are cleared as soon as the given client disconnects
     * from the session, so responses are not guaranteed in this case.
     * 
     * @param message the message to send
     * @param listener the message response listener to notify when a 
     * response is received
     * @throws MessageException if there is an error getting the bytes
     * for the message
     * @throws IllegalStateException if this client is not in the 
     * CONNECTED state or the session this client is connected to is not
     * in the CONNECTED state 
     */
    protected void send(Message message, ResponseListener listener) 
    {
        if (getStatus() != Status.CONNECTED) {
            throw new IllegalStateException("Not connected");
        }
        
        // if a listener was specified, register it with the client
        if (listener != null) {
            addResponseListener(message.getMessageID(), listener);
        }
        
        // send the message to the server
        getSession().send(this, message);
    }
    
    /**
     * Send a message to the server and wait for the response. This
     * method blocks until the response is received.
     * <p>
     * If the given client disconnects before a response is received from the
     * server, this method should throw an InterruptedException.
     * 
     * @param message the message to send
     * @return the response to the given message
     * @throws MessageException if there is an error getting the bytes
     * for the message
     * @throws InterruptedException if the wait is interrupted
     * @throws MessageException if there is an error getting the bytes
     * for the message
     * @throws IllegalStateException if this client is not in the 
     * CONNECTED state or the session this client is connected to is not
     * in the CONNECTED state 
     */
    protected ResponseMessage sendAndWait(Message message)
        throws InterruptedException
    {
        WaitResponseListener listener = new WaitResponseListener();
        send(message, listener);
        return listener.waitForResponse();
    }
    
    /**
     * Add a response listener
     * @param messageID the id of the message to listen for
     * @param listener the response listener
     */
    protected void addResponseListener(MessageID messageID, 
                                       ResponseListener listener)
    {
        responseListeners.put(messageID, listener);
    }
    
    /**
     * Notify any registered response listeners that a response to the
     * given message was received.
     * @param response the response message
     * @return true if a listener was found to respond to this message,
     * or false if not
     */
    protected boolean notifyResponseListener(ResponseMessage response) {
        ResponseListener listener =
                responseListeners.remove(response.getMessageID());

        if (listener != null) {
            listener.responseReceived(response);
        }

        // return true if we found a listener or false if not
        return (listener != null);
    }
    
    @Override
    public String toString() {
        return getConnectionType().toString();
    }
}
