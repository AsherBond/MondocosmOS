/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.server.comms;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ExceptionRetryStatus;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.comms.SessionInternalConnectionType;
import org.jdesktop.wonderland.common.messages.ErrorMessage;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.common.messages.MessageID;
import org.jdesktop.wonderland.common.messages.MessagePacker;
import org.jdesktop.wonderland.common.messages.MessagePacker.PackerException;
import org.jdesktop.wonderland.common.messages.MessagePacker.ReceivedMessage;
import org.jdesktop.wonderland.common.messages.OKMessage;
import org.jdesktop.wonderland.common.messages.ProtocolSelectionMessage;
import org.jdesktop.wonderland.server.WonderlandContext;

/**
 * This core session listener implements the basic Wonderland protocol
 * selection mechanism.  When a new client connects, they request a protcol
 * using a ProtocolSelectionMessage.  This listener handles the protcol
 * selection message, either by sending an error or instantiating the listener
 * associated with the given protocol type.
 * <p>
 * Once the session type has been successfully selected, this listener
 * simply acts as a wrapper, passing all request on to the delegated
 * listener.
 * <p>
 * TODO: these messages should be defined in binary and not as Java objects
 * to allow connections from non-Java clients.
 *
 * @author jkaplan
 */
public class ProtocolSessionListener
        implements ClientSessionListener, Serializable {
    
    /** a logger */
    private static final Logger logger =
            Logger.getLogger(ProtocolSessionListener.class.getName());
    
    /** the session associated with this listener */
    private ManagedReference<ClientSession> sessionRef;
    
    /** the protocol in use by this client */
    private CommunicationsProtocol protocol;
    
    /** the wrapped session, or null if no wrapped session exists yet */
    private ClientSessionListener wrapped;

    /**
     * Create a new instance of WonderlandSessionListener for the given
     * session
     * @param session the session connected to this listener
     */
    public ProtocolSessionListener(ClientSession session) {
        DataManager dm = AppContext.getDataManager();
        sessionRef = dm.createReference(session);
    }
    
    /**
     * Initialize the session listener
     */
    public static void initialize() {
        DataManager dm = AppContext.getDataManager();
        
        // create map from protocols to clients
        dm.setBinding(ProtocolClientMap.DS_KEY, new ProtocolClientMap());
    }

    /**
     * Called when the listener receives a message.  If the wrapped session
     * has not yet been defined, look for ProtocolSelectionMessages, otherwise
     * simply forward the data to the delegate session
     * @param data the message data
     */
    public void receivedMessage(ByteBuffer data) {
       
        // if there is a wrapped session, simply forward the data to it
        if (wrapped != null) {
            wrapped.receivedMessage(data);
            return;
        }
        
        // no wrapped session -- look for a ProtocolSelectionMessage
        try {
            // the message contains a client identifier in the first
            // 2 bytes, so ignore those
            ReceivedMessage recv = MessagePacker.unpack(data);
            Message m = recv.getMessage();
            
            // check the message type
            if (!(m instanceof ProtocolSelectionMessage)) {
                sendError(m, "Only ProtcolSelectionMessage allowed");
                return;
            }
            
            ProtocolSelectionMessage psm = (ProtocolSelectionMessage) m;
            CommsManager cm = WonderlandContext.getCommsManager();

            // see if we have a protocol to match the request
            CommunicationsProtocol cp = cm.getProtocol(psm.getProtocolName());
            if (cp == null) {
                sendError(m, "Protocol " + psm.getProtocolName() + " not found");
                return;
            }
            
            // see if the versions match
            if (!cp.getVersion().isCompatible(psm.getProtocolVersion())) {
                sendError(m, "Client version incompatible with server " + 
                             "version " + cp.getVersion());
            }
            
            
            ClientSession session = getSession();
            logger.info("Session " + session.getName() + " connected with " +
                        "protocol " + cp.getName());
            
            // all set -- set the wrapped session
            wrapped = cp.createSessionListener(session, psm.getProtocolVersion());
            if (wrapped instanceof ManagedObject) {
                wrapped = new ManagedClientSessionWrapper(wrapped);
            }

            // TODO: is this the right thing to do, or should we only
            // do this automatically for the Wonderland protocol?
            WonderlandClientID clientID = new WonderlandClientID(session);
            WonderlandContext.getUserManager().login(clientID);
            
            // record the client connection
            this.protocol = cp;
            recordConnect(cp, session);
            
            // send an OK message
            sendToSession(new OKMessage(psm.getMessageID()));
        } catch (PackerException eme) {
            sendError(eme.getMessageID(), null, eme);
        } 
    }

    /**
     * Called when the delegate session is disconnected
     * @param forced true if the disconnect was forced
     */
    public void disconnected(boolean forced) {
        try {
            // notify the user that logout has started.
            // TODO: is this the right thing to do, or should we only
            // do this automatically from the Wonderland protocol?
            WonderlandClientID clientID = new WonderlandClientID(sessionRef);
            WonderlandContext.getUserManager().startLogout(clientID);

            // notify the wrapped session that we were disconnected
            if (wrapped != null) {
                wrapped.disconnected(forced);
            }
            
            // notify the user that logout is now complete.  This will start
            // running the logout tasks
            WonderlandContext.getUserManager().finishLogout(clientID);
        
            // record client disconnect
            if (protocol != null) {
                recordDisconnect(protocol, sessionRef);
            }
        } catch (RuntimeException re) {
            // OWL issue #77: Darkstar silently swallows exceptions in this
            // code. Make sure to log any errors that aren't retryable.
            if (!(re instanceof ExceptionRetryStatus) ||
                    !((ExceptionRetryStatus) re).shouldRetry())
            {
                logger.log(Level.WARNING, "Disconnected error", re);
            }

            throw re;
        }

        // XXX acording to the Darkstar docs, this is our responsibility,
        // but it throws an exception if we remove the session here.  Hopefully
        // Darkstar cleans this up for us
        // DataManager dm = AppContext.getDataManager();
        // dm.removeObject(getSession());
    }
    
    /**
     * Get all clients using the given protocol
     * @param protocol the protocol to get clients for
     * @return a set of all clients connected with that protocol, or null
     * if no clients are connected via the protocol
     */
    public static Set<ClientSession> getClients(CommunicationsProtocol protocol)
    {
        return getProtocolClientMap().get(protocol);
    }
    
    /**
     * Get the protocol in use by the given client
     * @param session the session to get protocol information for
     * @return the protocol used by that client, or null if the client
     * is not registered
     */
    public static CommunicationsProtocol getProtocol(ClientSession session)
    {
        return getProtocolClientMap().get(session);
    }
    
    /**
     * Get the session this listener represents.
     * @return the session connected to this listener
     */
    protected ClientSession getSession() {
        return sessionRef.get();
    }
    
    /**
     * Send an error to the session
     * @param message the source message
     * @param error the error to send
     */
    protected void sendError(Message source, String error) {
        sendError(source.getMessageID(), error, null);
    }
    
    /**
     * Send an error to the session
     * @param messageID the messageID of the original error
     * @param error the error message
     * @param cause the underlying exception
     */
    protected void sendError(MessageID messageID, String error, 
                             Throwable cause)
    {
        sendToSession(new ErrorMessage(messageID, error, cause));
    }
    
    /**
     * Send a message to the session
     * @param message the message to send
     */
    protected void sendToSession(Message message) {
        try {            
            ByteBuffer buf = MessagePacker.pack(message, SessionInternalConnectionType.SESSION_INTERNAL_CLIENT_ID);
        
            getSession().send(buf);
        } catch (PackerException ioe) {
            logger.log(Level.WARNING, "Unable to send message " + message, ioe);
        }
    }
    
    /**
     * Record a client of the given type connecting
     * @param protocol the protocol the session connected with
     * @param session the session that connected
     */
    protected void recordConnect(CommunicationsProtocol protocol,
                                 ClientSession session)
    {
        ProtocolClientMap pcm = getProtocolClientMap();
        
        DataManager dm = AppContext.getDataManager();
        dm.markForUpdate(pcm);
        
        pcm.add(protocol, session);
    }
    
    /**
     * Record a client of the given type disconnecting
     * @param protocol the protocol the session connected with
     * @param sessionRef a reference to the session that connected
     */
    protected void recordDisconnect(CommunicationsProtocol protocol,
                                    ManagedReference<ClientSession> sessionRef)
    {
        ProtocolClientMap pcm = getProtocolClientMap();
        
        DataManager dm = AppContext.getDataManager();
        dm.markForUpdate(pcm);
        
        pcm.remove(protocol, sessionRef);
    }
      
    /**
     * Get the protocol client map, which maps from protocols to clients
     * using that protocol
     * @return the ProtocolClientMap
     */
    protected static ProtocolClientMap getProtocolClientMap() {
        return (ProtocolClientMap) AppContext.getDataManager().getBinding(ProtocolClientMap.DS_KEY);
    }
    
    /**
     * A record of clients connected with the given protocol
     */
    protected static class ProtocolClientMap
            implements ManagedObject, Serializable
    {
        /** the key in the datastore */
        private static final String DS_KEY = ProtocolClientMap.class.getName();
        
        /** mapping from protocol to clients */
        private Map<CommunicationsProtocol, ManagedReference<ProtocolClientSet>> clientMap = 
                new HashMap<CommunicationsProtocol, ManagedReference<ProtocolClientSet>>();
        
        /** mapping from clients to protocols */
        private Map<ManagedReference<ClientSession>, CommunicationsProtocol> protocolMap =
                new HashMap<ManagedReference<ClientSession>, CommunicationsProtocol>();
        
        /**
         * Add a session to a communications protocol
         * @param protocol the communications protocol
         * @param session the client session associated with the given protocol
         */
        public void add(CommunicationsProtocol protocol, ClientSession session) {
            DataManager dm = AppContext.getDataManager();
                                
            // Add a reference to this client in the set of clients for
            // the given protocol.  If the set does not exist, then
            // create it.
            ManagedReference<ProtocolClientSet> ref = clientMap.get(protocol);
            if (ref == null) {
                ProtocolClientSet sessions = new ProtocolClientSet();
                ref = dm.createReference(sessions);
                clientMap.put(protocol, ref);
            }
            
            ManagedReference<ClientSession> sessionRef = dm.createReference(session);
            
            ProtocolClientSet sessions = ref.getForUpdate();
            sessions.add(sessionRef);
        
            // add a reference to the protocol from this client's session
            protocolMap.put(sessionRef, protocol);
        }
        
        /**
         * Remove a session from a communications protocol
         * @param protocol the communications protocol
         * @param sessionRef a reference to the client session associated with
         * the given protocol
         */
        public void remove(CommunicationsProtocol protocol, 
                           ManagedReference<ClientSession> sessionRef)
        {
            // Remove the reference to thei client in the set of client for
            // the given protocol.  If the set is empty, remove the set
            // altogether.
            ManagedReference<ProtocolClientSet> ref = clientMap.get(protocol);
            if (ref != null) {
                ProtocolClientSet sessions = ref.getForUpdate();
                sessions.remove(sessionRef);
                
                if (sessions.isEmpty()) {
                    clientMap.remove(protocol);
                }
            }
            
            // remove the reference to the protcol from this client's session
            protocolMap.remove(sessionRef);
        }
        
        /**
         * Get all sessions associated with the given protocol
         * @param protocol the protocol
         * @return the set of client sessions associated with the given
         * protocol, or an empty set if no sessions are associated with
         * the protocol
         */
        public Set<ClientSession> get(CommunicationsProtocol protocol) {
            ManagedReference<ProtocolClientSet> ref = clientMap.get(protocol);
            if (ref == null) {
                return Collections.emptySet();
            }
            ProtocolClientSet clients = ref.get();
            
            // return the sessions
            Set<ClientSession> out = new HashSet<ClientSession>();
            for (ManagedReference<ClientSession> clientRef : clients) {
                out.add(clientRef.get());
            }
            
            return out;
        }
        
        /**
         * Get the protocol associated with the given session
         * @param session the session to get
         * @return the protocol in use by that session, or null if the
         * sessionId does not exist
         */
        public CommunicationsProtocol get(ClientSession session) {
            DataManager dm = AppContext.getDataManager();
            ManagedReference sessionRef = dm.createReference(session);
            
            return protocolMap.get(sessionRef);
        }
    }
    
    static class ProtocolClientSet extends HashSet<ManagedReference<ClientSession>>
            implements ManagedObject, Serializable
    {
    }

    static class ManagedClientSessionWrapper 
            implements ClientSessionListener, Serializable
    {
        private ManagedReference<ClientSessionListener> listenerRef;

        public ManagedClientSessionWrapper(ClientSessionListener listener) {
            listenerRef = AppContext.getDataManager().createReference(listener);
        }

        public void receivedMessage(ByteBuffer message) {
            listenerRef.get().receivedMessage(message);
        }

        public void disconnected(boolean graceful) {
            listenerRef.get().disconnected(graceful);
        }
    }
}
