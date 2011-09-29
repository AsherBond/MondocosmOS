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

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jdesktop.wonderland.client.comms.BaseConnection;
import org.jdesktop.wonderland.client.comms.ClientConnection.Status;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.servermanager.common.PingRequestMessage;
import org.jdesktop.wonderland.servermanager.common.PingResponseMessage;
import org.jdesktop.wonderland.servermanager.common.ServerManagerConnectionType;


/**
 *
 * @author kaplanj
 */
public class ServerManagerConnection extends BaseConnection {
    /** default ping time */
    private static final long DEFAULT_PING_TIME = 5000;
    
    /** time between pings */
    private long pingTime = DEFAULT_PING_TIME;
    
    /** a timer to send the pings */
    private Timer pingTimer;
    
    /** listeners to notify when a ping is received */
    private List<PingListener> listeners = 
            new CopyOnWriteArrayList<PingListener>();
    
    public ServerManagerConnection() {
    }
    
    public ConnectionType getConnectionType() {
        return ServerManagerConnectionType.CONNECTION_TYPE;
    }
    
    @Override
    public void handleMessage(Message message) {
        if (message instanceof PingResponseMessage) {
            handlePingResponse((PingResponseMessage) message);
        }
    }

    protected void handlePingResponse(PingResponseMessage prm) {
        PingData data = new PingData();
        data.setPingTime(System.currentTimeMillis() - prm.getSentTime());
        
        for (PingListener pl : listeners) {
            pl.pingReceived(data);
        }
    }

    @Override
    protected synchronized void setStatus(Status status) {
        super.setStatus(status);

        if (status == Status.DISCONNECTED) {
            stopTimer();
        }
    }
    
    public synchronized long getPingTime() {
        return pingTime;
    }
    
    public synchronized void setPingTime(long pingTime) {
        this.pingTime = pingTime;
        
        if (pingTimer != null) {
            stopTimer();
            startTimer();
        }
    }
    
    public synchronized void addPingListener(PingListener l) {
        if (listeners.isEmpty()) {
            startTimer();
        }
        
        listeners.add(l);
    }
    
    public synchronized void removePingListener(PingListener l) {
        listeners.remove(l);
        
        if (listeners.isEmpty()) {
            stopTimer();
        }
    }

    protected synchronized void startTimer() {
        pingTimer = new Timer();
        pingTimer.scheduleAtFixedRate(new PingTask(), 0, getPingTime());
    }
    
    protected synchronized void stopTimer() {
        pingTimer.cancel();
        pingTimer = null;
    }
    
    class PingTask extends TimerTask {
        @Override
        public void run() {
            send(new PingRequestMessage(System.currentTimeMillis()));
        }
    }
}
