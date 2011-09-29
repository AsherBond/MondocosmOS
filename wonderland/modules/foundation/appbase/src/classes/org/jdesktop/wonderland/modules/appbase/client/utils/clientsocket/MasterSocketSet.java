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
package org.jdesktop.wonderland.modules.appbase.client.utils.clientsocket;

import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * Manages communications between a single master and multiple slaves over socket connections.
 * Slaves connect to the master by establishing a socket connection to the input server socket.
 * The master can unicast send to a single slave or broadcast send to all slaves. Slaves can
 * unicast send to the master. NOTE: slaves cannot send to other slaves.
 *
 * The master is notified of slave activity via the listener given to the constructor.
 *
 * @author deronj, Krishna Gadepalli
 */
@ExperimentalAPI
public class MasterSocketSet implements Runnable {

    private static Logger logger = Logger.getLogger(MasterSocketSet.class.getName());

    private BigInteger masterClientID;
    private ServerSocket serverSocket;
    private ClientSocketListener listener;
    private Thread acceptThread;
    private boolean stop = false;
    private final HashMap<BigInteger, MasterClientSocket> clientSocketMap =
        new HashMap<BigInteger, MasterClientSocket>();

    public MasterSocketSet(BigInteger masterClientID, ServerSocket serverSocket, 
                           ClientSocketListener listener) {
        this.masterClientID = masterClientID;
        this.serverSocket = serverSocket;
        this.listener = listener;
        acceptThread = new Thread(this);
        acceptThread.setName("MasterSocketSet-AcceptThread");
    }

    public void start() {
        acceptThread.start();
    }

    public void stop() {
        stop = true;
    }

    public void setEnable (BigInteger slaveID, boolean enable) {
	MasterClientSocket mcs;
        synchronized (clientSocketMap) {
	    mcs = clientSocketMap.get(slaveID);
	}
        if (mcs == null) {
            logger.warning("Slave to be enabled isn't connected, slaveID = " + slaveID);
            return;
        }
	mcs.setEnable(enable);
    }

    /**
     * Close a particular child socket
     */
    public void close (BigInteger slaveID) {
        MasterClientSocket mcs;
        synchronized (clientSocketMap) {
	    mcs = clientSocketMap.get(slaveID);
	}
        if (mcs == null) {
            logger.warning("Slave to be closed isn't connected, slaveID = " + slaveID);
            return;
        }
	mcs.close();
    }

    /**
     * Closes this MasterSocketSet. Also closes the server socket which was passed into the constructor.
     */
    public void close() {
        stop();

        // Close all subordinate MasterClientSockets
        LinkedList<MasterClientSocket> toClose = new LinkedList<MasterClientSocket>();
        synchronized (clientSocketMap) {
            for (MasterClientSocket mcs : clientSocketMap.values()) {
                toClose.add(mcs);
            }
            clientSocketMap.clear();
        }
        for (MasterClientSocket mcs : toClose) {
            mcs.close();
        }
        toClose = null;

        try {
            serverSocket.close();
        } catch (Exception e) {
            logger.warning("Cannot close server socket");
        }

        logger.severe("MasterSocketSet closed");
    }

    /**
     * The run method of the thread accepting connections.
     */
    public void run() {
        while (!stop) {
            try {
                Socket s = serverSocket.accept();
                MasterClientSocket mcs = new MasterClientSocket(masterClientID, s, new MyListener());
                if (mcs.initialize()) {
                    addSlave(mcs);
                    mcs.start();
                } else {
		    mcs.close();
                    logger.warning("Failed to start client socket : " + s);
                }
            } catch (Exception e) {
                if (serverSocket.isClosed()) {
                    // Stop the thread if the server socket closes
                    break;
                }
                logger.warning("Exception in MasterSocketSet accept thread, exception = " + e);
            }
        }
    }

    private class MyListener implements ClientSocketListener {

	public void receivedMessage(BigInteger otherClientID, byte[] buf) {
            if (listener != null) {
                listener.receivedMessage(otherClientID, buf);
            }
        }

        public void otherClientHasLeft(BigInteger otherClientID) {
            removeSlave(otherClientID);
            if (listener != null) {
                listener.otherClientHasLeft(otherClientID);
            }
        }
    }

    private void addSlave(MasterClientSocket mcs) {
        synchronized (clientSocketMap) {
            clientSocketMap.put(mcs.getOtherClientID(), mcs);
        }
    }

    private void removeSlave(BigInteger slaveID) {
        synchronized (clientSocketMap) {
            clientSocketMap.remove(slaveID);
        }
    }

    /**
     * Send an entire byte array to the given slave. Don't block.
     */
    public void send(BigInteger slaveID, byte[] buf) throws IOException {
        send(slaveID, buf, buf.length);
    }

    /**
     * Send the given number of bytes of the given byte array to the given slave. Don't block.
     */
    public void send(BigInteger slaveID, byte[] buf, int len) throws IOException {
        send(slaveID, buf, len, false);
    }

    /**
     * Send the given number of bytes of the given byte array to the given slave. Don't block.
     * @param force Ignore the enable status and force the message to be sent.
     */
    public void send(BigInteger slaveID, byte[] buf, int len, boolean force) throws IOException {
        if (len <= 0) {
            return;
        }
	MasterClientSocket mcs;
        synchronized (clientSocketMap) {
	    mcs = clientSocketMap.get(slaveID);
	}
        if (mcs != null) {
            mcs.send(buf, len, force);
        }
    }

    /**
     * Send an entire byte array to all connected slaves. Don't block.
     */
    public void send(byte[] buf) throws IOException {
        send(buf, buf.length);
    }

    /**
     * Send the given number of bytes of the given byte array to all conencted slaves. Don't block.
     */
    public void send(byte[] buf, int len) throws IOException {
        if (len <= 0) {
            return;
        }
        synchronized (clientSocketMap) {
            for (BigInteger slave : clientSocketMap.keySet()) {
                MasterClientSocket mcs = clientSocketMap.get(slave);
                if (mcs != null) {
                    mcs.send(buf, len, false);
                }
            }
        }
    }
}
