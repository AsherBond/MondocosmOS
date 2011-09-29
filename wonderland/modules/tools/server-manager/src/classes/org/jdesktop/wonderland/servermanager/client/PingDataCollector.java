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
package org.jdesktop.wonderland.servermanager.client;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.comms.ConnectionFailureException;
import org.jdesktop.wonderland.client.comms.LoginFailureException;
import org.jdesktop.wonderland.client.comms.LoginParameters;
import org.jdesktop.wonderland.client.comms.SessionStatusListener;
import org.jdesktop.wonderland.client.comms.WonderlandServerInfo;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.comms.WonderlandSessionImpl;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.client.login.SessionCreator;
import org.jdesktop.wonderland.front.admin.ServerInfo;
import org.jdesktop.wonderland.modules.darkstar.api.weblib.DarkstarRunner;
import org.jdesktop.wonderland.modules.darkstar.api.weblib.DarkstarWebLogin.DarkstarServerListener;
import org.jdesktop.wonderland.modules.darkstar.api.weblib.DarkstarWebLoginFactory;

/**
 * Montitor the server, and periodically collect data
 * @author jkaplan
 */
public class PingDataCollector 
        implements PingListener, DarkstarServerListener, SessionStatusListener
{
    private Logger logger =
            Logger.getLogger(PingDataCollector.class.getName());
    
    public static final String KEY = "_ping_data_collector";
    
    // the list of data we have collected
    private List<PingData> data = Collections.synchronizedList(
                                        new LinkedList<PingData>());
    
    // a map from Darkstar runners to the session associated with that
    // runner
    private final Map<DarkstarRunner, ServerManagerSession> sessions =
            Collections.synchronizedMap(
                new HashMap<DarkstarRunner, ServerManagerSession>());
    
    public PingDataCollector() {
        DarkstarWebLoginFactory.getInstance().addDarkstarServerListener(this);
    }

    /**
     * Get the total amount of data we've collected
     * @return the number of pingdata objects we've stored
     */
    public int getDataSize() {
        return data.size();
    }
    
    /**
     * Get all the data we've collected
     * @returh the data we collected
     */
    public List<PingData> getData() {
        return getData(0);
    }
    
    /**
     * Get all data after a given timestamp
     * @param after the timestamp to get after
     * @return all data after the given timestamp
     */
    public List<PingData> getData(long after) {
        return getData(0, Integer.MAX_VALUE);
    }
    
    /**
     * Get all data after a given timestamp, up to a maximum of
     * the given number of data points.
     * @param after the timestamp to get data after
     * @param count the maximum number of data points to get
     */
    public List<PingData> getData(long after, int count) {
        int firstIdx = 0;
        if (after > 0) {
            firstIdx = findFirstAfter(data, after);
        }
        
        int remaining = data.size() - firstIdx;
        int lastIdx = firstIdx + Math.min(remaining, count);
    
        // make sure these are valid values
        if (firstIdx < 0 || lastIdx < 0) {
            return Collections.emptyList();
        }
        
        return data.subList(firstIdx, lastIdx);
    }
    
    
    /**
     * Find the first data element after the given value. This assumes the
     * list is in ascending order of timestamp
     * @param data the list of data to search
     * @param after the timestamp to search after
     * @return the first index bigger than the given time, or null if
     * no indices are bigger than the given
     */
    protected int findFirstAfter(List<PingData> data, long after) {
        int count = 0;
        synchronized (data) {
            for (PingData d : data) {
                if (d.getSampleDate() > after) {
                    break;
                }
                
                count++;
            }
        }
        
        // check for not found
        if (count >= data.size()) {
            count = -1;
        }
        
        return count;
    }
    
    /**
     * Notification that a session's status has changed
     * @param session the session with the changed status
     * @param status the session's status
     */
    public void sessionStatusChanged(WonderlandSession session, 
                                     WonderlandSession.Status status) 
    {
        if (status == WonderlandSession.Status.DISCONNECTED) {
            // find the runner associated with this session
            DarkstarRunner dr = null;
            synchronized (sessions) {
                for (Entry<DarkstarRunner, ServerManagerSession> e : 
                     sessions.entrySet()) 
                {
                    if (e.getValue().equals(session)) {
                        dr = e.getKey();
                        break;
                    }
                }
            }
            
            if (dr != null) {
                serverStopped(dr);
            }
        }
    }
    
    /**
     * Handle an incoing ping message
     * @param data the ping data
     */
    public void pingReceived(PingData cur) {
        data.add(cur);
    }
    
    /**
     * Shut down the collector and remove all registered listeners
     */
    public synchronized void shutdown() {
        // remove runner listener
        DarkstarWebLoginFactory.getInstance().removeDarkstarServerListener(this);

        // shutdown sessions
        for (ServerManagerSession session : sessions.values()) {
            session.logout();
        }
    }

    // connect to a server
    public void serverStarted(DarkstarRunner dr, ServerSessionManager ssm) {
        logger.warning("Connect to server " + dr.getHostname() + " " +
                       dr.getPort());

        // TODO: connect to a particular Darkstar server
        try {
            ServerManagerSession session = ssm.createSession(
                    new SessionCreator<ServerManagerSession>()
            {
                public ServerManagerSession createSession(
                        ServerSessionManager manager,
                        WonderlandServerInfo serverInfo,
                        ClassLoader loader)
                {
                    // use our classloader
                    return new ServerManagerSession(manager, serverInfo,
                                                   getClass().getClassLoader());
                }
            });

            session.addSessionStatusListener(this);

            // remember the session
            sessions.put(dr, session);

            // make a note of the new connection
            WonderlandServerInfo info = session.getServerInfo();
            PingData note = new PingData();
            note.setPingNoteTitle("Connected to server");
            note.setPingNoteText("Connected to server " + info.getHostname() +
                                 ":" + info.getSgsPort());
            data.add(note);
        } catch (LoginFailureException le) {
            logger.log(Level.WARNING, "Unable to log in to server " +
                       ServerInfo.getServerURL(), le);
        }
    }

    // disconnect from the server
    public void serverStopped(DarkstarRunner dr) {
        // TODO get from runner
        String serverHost = dr.getHostname();
        int serverPort = dr.getPort();
        
        logger.warning("Disconnect from " + serverHost + " " + serverPort);
        
        // disconnect from the server
        ServerManagerSession session = sessions.remove(dr);
        if (session != null) {
            session.logout();
        }
        
        // make a note of the disconnect
        PingData note = new PingData();
        note.setPingNoteTitle("Disconnected from server");
        note.setPingNoteText("Disconnected from server " + serverHost + 
                             ":" + serverPort);
        data.add(note);
    }
    
    class ServerManagerSession extends WonderlandSessionImpl {
        private ServerManagerConnection smc;
        
        public ServerManagerSession(ServerSessionManager manager,
                                    WonderlandServerInfo server,
                                    ClassLoader classLoader) 
        {
            super (manager, server, classLoader);
        }

        @Override
        public void login(LoginParameters loginParams) 
                throws LoginFailureException 
        {
            super.login(loginParams);
            
            try {
                smc = new ServerManagerConnection();
                smc.connect(this);
                smc.addPingListener(PingDataCollector.this);
            } catch (ConnectionFailureException cfe) {
                logout();
                
                throw new LoginFailureException("Error connecting server " +
                                                "manager connection", cfe);
            }
            
        }
    }
}
