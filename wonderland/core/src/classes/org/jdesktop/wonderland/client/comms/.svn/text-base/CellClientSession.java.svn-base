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

import java.util.Properties;
import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.client.cell.view.LocalAvatar;
import org.jdesktop.wonderland.client.cell.CellCache;
import org.jdesktop.wonderland.client.cell.CellCacheBasicImpl;
import org.jdesktop.wonderland.client.cell.CellCacheConnection;
import org.jdesktop.wonderland.client.cell.CellChannelConnection;
import org.jdesktop.wonderland.client.cell.CellEditChannelConnection;
import org.jdesktop.wonderland.client.login.ServerSessionManager;

/**
 * An extension of WonderlandSession that attaches all the relevant
 * handlers needed for a client using the Cell system
 */
@InternalAPI
public class CellClientSession extends WonderlandSessionImpl {
    
    /** the cell client */
    private CellCacheConnection cellCacheConnection;
    private LocalAvatar localAvatar;
    private CellChannelConnection cellChannelConnection;
    private CellEditChannelConnection cellEditChannelConnection;
    private CellCache cellCache;

    /** properties for each connection */
    private final Properties cellCacheProps = new Properties();
    private final Properties cellChannelProps = new Properties();
    private final Properties cellEditProps = new Properties();

    public CellClientSession(ServerSessionManager manager, 
                             WonderlandServerInfo serverInfo)
    {
        this (manager, serverInfo, null);
    }
    
    public CellClientSession(ServerSessionManager manager,
                             WonderlandServerInfo serverInfo,
                             ClassLoader loader)
    {
        super (manager, serverInfo, loader);
        
        localAvatar = new LocalAvatar(this);
 
        cellCacheConnection = new CellCacheConnection(localAvatar);        
        cellChannelConnection = new CellChannelConnection();
        cellEditChannelConnection = new CellEditChannelConnection();
    
        // create the cell cache
        cellCache = createCellCache();
    }
    
    /**
     * Return the local avatar for this session
     * @return
     */
    public LocalAvatar getLocalAvatar() {
        return localAvatar;
    }
    
    /**
     * Return the cell cache for this session
     * @return the cell cache
     */
    public CellCache getCellCache() {
        return cellCache;
    }
    
    /**
     * Get the cell cache connection
     * @return the cell cache connection
     */
    public CellCacheConnection getCellCacheConnection() {
        return cellCacheConnection;
    }

    /**
     * Get the properties associated with the cell cache connection.
     * @return the properties for the cell cache connection
     */
    public Properties getCellCacheProperties() {
        return cellCacheProps;
    }

    /**
     * Get the cell channel connection
     * @return the cell channel connection
     */
    public CellChannelConnection getCellChannelConnection() {
        return cellChannelConnection;
    }

    /**
     * Get the properties associated with the cell channel connection.
     * @return the properties for the cell channel connection
     */
    public Properties getCellChannelProperties() {
        return cellChannelProps;
    }

    /**
     * Get the cell edit channel connection
     * @return the cell edit channel connection
     */
    public CellEditChannelConnection getCellEditChannelConnection() {
        return cellEditChannelConnection;
    }

    /**
     * Get the properties associated with the cell edit connection.
     * @return the properties for the cell edit connection
     */
    public Properties getCellEditProperties() {
        return cellEditProps;
    }

    /**
     * Override the login message to connect clients after the login
     * succeeds.  If a client fails to connect, the login will be aborted and
     * a LoginFailureException will be thrown
     * @param loginParameters the parameters to login with
     * @throws LoginFailureException if the login fails or any of the clients
     * fail to connect
     */
    @Override
    public void login(LoginParameters loginParams) 
            throws LoginFailureException 
    {
        // this will wait for login to succeed
        super.login(loginParams);
        
        // if login succeeds, connect the various clients
        try {
            // first connect the cell channel connection, so we can receive
            // cell messages.  We need to do this before attaching the
            // cell cache connection, since the cell cache connection
            // will create a view and immediately start joining us to cells
            cellChannelConnection.connect(this, cellChannelProps);

            // Now connect to the cellCache. The view will be determined via the
            // localAvatar object.
            cellCacheConnection.connect(this, cellCacheProps);
            
            // Finally, connect to the edit channel for cells. This will let
            // cells be added, removed, etc.
            cellEditChannelConnection.connect(this, cellEditProps);
        } catch (ConnectionFailureException afe) {
            // a client failed to connect -- logout
            logout();
            
            // throw a login exception
            throw new LoginFailureException("Failed to attach client" , afe);
        }
    }
    
    /**
     * Create the cell cache.  Called in the constructor after all connections
     * have already been created.
     * @return the newly created cell cache
     */
    protected CellCache createCellCache() {
        // create the cell cache and arrange for it to get messages
        // whenever a new cell is created
        CellCacheBasicImpl cacheImpl = 
                new CellCacheBasicImpl(this, getClassLoader(),
                                       cellCacheConnection, cellChannelConnection);
        cellCacheConnection.addListener(cacheImpl);
        return cacheImpl;
    }
}
