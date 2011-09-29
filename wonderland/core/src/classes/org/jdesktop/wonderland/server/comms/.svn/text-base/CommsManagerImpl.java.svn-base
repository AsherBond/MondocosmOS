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
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.util.ScalableHashMap;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.utils.ScannedClassLoader;
import org.jdesktop.wonderland.server.comms.annotation.ClientHandler;
import org.jdesktop.wonderland.server.comms.annotation.Protocol;

/**
 * Implementation of CommsManager
 * @author jkaplan
 */
class CommsManagerImpl implements CommsManager, Serializable 
{
    /** the name of the binding for the implementation in the data store */
    private static final String BINDING_NAME =
            CommsManagerImpl.class.getName() + ".binding";

    /**
     * Create a new instance of CommsManagerImpl.
     */
    public CommsManagerImpl() {
    }
    
    /**
     * Static initializer sets up the ProtocolSessionListener and
     * WonderlandSessionListener
     */
    public static void initialize() {
         // initialize the default session listener
        ProtocolSessionListener.initialize();
        
        // initialize the Wonderland session listener
        WonderlandSessionListener.initialize();

        // create the protocol map and register the binding name
        Map<String, CommunicationsProtocol> protocols =
                new ScalableHashMap<String, CommunicationsProtocol>();
        AppContext.getDataManager().setBinding(BINDING_NAME, protocols);

        // find all annotated protocols and install them
        ScannedClassLoader scl = ScannedClassLoader.getSystemScannedClassLoader();
        Iterator<CommunicationsProtocol> pi = scl.getAll(Protocol.class,
                                                         CommunicationsProtocol.class);
        while (pi.hasNext()) {
            CommunicationsProtocol cp = pi.next();
            protocols.put(cp.getName(), cp);
        }

        // find all annotated client handlers and install them
        Iterator<ClientConnectionHandler> ci = scl.getAll(ClientHandler.class,
                                                          ClientConnectionHandler.class);
        while (ci.hasNext()) {
            WonderlandSessionListener.registerClientHandler(ci.next());
        }
    }
    
    public void registerProtocol(CommunicationsProtocol protocol) {
        getProtocolMap().put(protocol.getName(), protocol);
    }

    public void unregisterProtocol(CommunicationsProtocol protocol) {
        getProtocolMap().remove(protocol.getName());
    }

    public CommunicationsProtocol getProtocol(String name) {
        return getProtocolMap().get(name);
    }

    public Set<CommunicationsProtocol> getProtocols() {
        return Collections.unmodifiableSet(new HashSet(getProtocolMap().values()));
    }
    
    protected Map<String, CommunicationsProtocol> getProtocolMap() {
        return (Map<String, CommunicationsProtocol>)
                AppContext.getDataManager().getBinding(BINDING_NAME);
    }

    public CommunicationsProtocol getProtocol(ClientSession session) {
        return ProtocolSessionListener.getProtocol(session);
    }
    
    public Set<ClientSession> getClients(CommunicationsProtocol protocol) {
        return ProtocolSessionListener.getClients(protocol);
    }

    public void registerClientHandler(ClientConnectionHandler handler) {
        WonderlandSessionListener.registerClientHandler(handler);
    }
  
    public void unregisterClientHandler(ClientConnectionHandler handler) {
        WonderlandSessionListener.unregisterClientHandler(handler);
    }

    public ClientConnectionHandler getClientHandler(ConnectionType clientType) {
        return WonderlandSessionListener.getClientHandler(clientType);
    }

    public Set<ClientConnectionHandler> getClientHandlers() {
        return WonderlandSessionListener.getClientHandlers();
    }
    
    public WonderlandClientSender getSender(ConnectionType clientType) {
        return WonderlandSessionListener.getSender(clientType);
    }

    public WonderlandClientID getWonderlandClientID(BigInteger sessionID) {
	return SessionMapManagerFactory.getSessionMapManager().getClientID(sessionID);
    }

}
