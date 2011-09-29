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

import com.sun.sgs.client.simple.SimpleClient;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Properties;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.auth.WonderlandIdentity;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.Message;

/**
 * The WonderlandSession is the base class for communicating with a given
 * Wonderland server.  The session encapsulates the work of login and
 * maintaining the state of the connection with the given server.
 * <p>
 * 
 * @author jkaplan
 */
@ExperimentalAPI
public interface WonderlandSession {
    /** possible states of the client */
    @ExperimentalAPI
    public enum Status { DISCONNECTED, CONNECTING, CONNECTED };
    
    /**
     * Get the server this client is connected to
     * @return the server
     */
    public WonderlandServerInfo getServerInfo();

    /**
     * Get the session manager that handles login for this session.
     * @return the session manager
     */
    public ServerSessionManager getSessionManager();

    /**
     * Get the status of this client.
     * @return the current status of the client
     */
    public Status getStatus();
    
    /**
     * Get the simple client connected to the Darkstar server.  This will
     * only be valid after the login method succeeds.
     * @return the darkstar client
     */
    public SimpleClient getSimpleClient();
    
    /**
     * Log in to the server.  This will connect to the given server,
     * and wait for the login to succeed.  If the login succeeds, 
     * negotiate a protocol using the Wonderland protocol negotiation
     * mechanism.  This method blocks until both the login and protocol
     * selection have been verified by the server.
     * <p>
     * If this method returns normally, then the login and protocol selection
     * succeeded.  If it throws a LoginFailureException, the login has failed.
     * <p>
     * @param loginParams the parameters required for login
     * @throws LoginFailureException if the login fails
     */
    public void login(LoginParameters loginParams) 
        throws LoginFailureException;
    
    /**
     * Logout from the server.
     */
    public void logout();
    
    /**
     * Get the ID of this session.  The ID is a unique identifier for this
     * particular session that is globally unique across all sessions
     * attached to the same server.  
     * <p>
     * The ID is only valid when the client
     * status is CONNECTED.  Attempting to read the ID when the status is
     * anything else will cause an IllegalStateException.
     * <p>
     * The ID is generated on the server and communicated as part of the
     * login process.  It is available at the time that the client is
     * notified of the state change.
     */
    public BigInteger getID();

    /**
     * Get the user ID associated with this session.  This ID is maintained
     * byt the server based on the user's login credentials.
     * <p>
     * The ID is generated on the server and communicated as part of the
     * login process.  It is available at the time that the client is
     * notified of the state change.
     */
    public WonderlandIdentity getUserID();
    
    /**
     * Connect a new client to this session, with no properties. This is
     * identical to calling <code>connect(client, null)</code>.
     * 
     * @param client the client to connect
     * @throws ConnectionFailureException of the connection fails
     * 
     * @see connect(ClientConnect, Properties)
     */
    public void connect(ClientConnection client) 
            throws ConnectionFailureException;
    
    /**
     * Connect a new client to this session.  When a client is connected to
     * this session, it will interact with the server associated with
     * this session.  
     * <p>
     * Only one client of any given ConnectionType may be
     * connected to the server at any time.  Attempting to connect a second
     * client of the same type will result in an ConnectionFailureException.
     * <p>
     * Clients may only be connected when a session is in the DISCONNECTED state.
     * If the session is in any other state, an ConnectionFailureException will
     * be thrown.  When a session disconnects, all clients are disconnected,
     * and must be re-connected to start working again.
     * <p>
     * A client may optionally specify properties to use when making the
     * given connection.  If properties are specified, the client will send
     * the properties to the server, where they will be available in the
     * <code>ClientConnectionManager.clientConnected()</code>.  If null
     * is passed in for the properties argument, and empty properties will
     * be sent to the server.
     * 
     * @param client the client to connect
     * @param properties the properties object to send to the server
     * @throws ConnectionFailureException of the connection fails
     */
    public void connect(ClientConnection client, Properties properties) 
            throws ConnectionFailureException;
    
    /**
     * Disconnect a previously connected client from this session.
     * @param client the client to logout
     */
    public void disconnect(ClientConnection client);
    
    /**
     * Get the connected ClientConnection for the given ConnectionType.
     * @param type the client type to get
     * @return the connected client for the given type, or null if no
     * client of the given type is connected
     */
    public ClientConnection getConnection(ConnectionType type);
    
    /**
     * Get the attched ClientConnection for the given ConnectionType.
     * @param type the client type to get
     * @param clazz the class of client to return
     * @return the connected client for the given type, or null if no
     * client of the given type is connected
     * @throws ClassCastException if the client for the given client type
     * is not assignable to the given type
     */
    public <T extends ClientConnection> T getConnection(ConnectionType type,
                                                    Class<T> clazz);
    
    /**
     * Get all clients connected to this session
     * @return the clients connected to this session
     */
    public Collection<ClientConnection> getConnections();
    
    /**
     * Send a message to the server over the session channel on behalf of the 
     * given client. The client must be successfully connected to this session 
     * in order for the send to work.
     * @param client the client that is sending the message
     * @param message the message to send
     * @throws MessageException if there is an error getting the bytes
     * for the message
     * @throws IllegalStateException if the client is not connected to this
     * session or the session is not connected
     */
    public void send(ClientConnection client, Message message);
    
    /**
     * Add a listener that will be notified when the status of this
     * session changes.
     * @param listener the listener to add
     */
    public void addSessionStatusListener(SessionStatusListener listener);
    
    /**
     * Remove a listener that will be notified when the status of this
     * session changes.
     * @param listener the listener to remove
     */
    public void removeSessionStatusListener(SessionStatusListener listener);
}
