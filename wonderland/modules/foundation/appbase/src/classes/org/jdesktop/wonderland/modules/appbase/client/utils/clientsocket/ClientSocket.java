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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.logging.Logger;
import java.util.logging.Level;
import org.jdesktop.wonderland.modules.appbase.client.utils.stats.StatisticsReporter;

/**
 * Code which is shared between the master and slaves.
 *
 * @author deronj, Krishna Gadepalli
 */
public class ClientSocket {

    private static Logger logger = Logger.getLogger(ClientSocket.class.getName());
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_IO = false;
    protected static boolean ENABLE_STATS = false;

    // For backport to Java 5
    private static byte[] arrayCopyOf(byte[] buf, int len) {
        byte[] copyBuf = new byte[len];
        System.arraycopy(buf, 0, copyBuf, 0, len);
        return copyBuf;
    }

    private static class Message {

        public byte[] buf;
        public int len;

        public Message(byte[] buf) {
            this(buf, buf.length);
        }

        public Message(byte[] buf, int len) {
            // Backport to Java 5
            //this.buf = Arrays.copyOf(buf, len);
            this.buf = arrayCopyOf(buf, len);
            this.len = len;
        }
    };
    private static int msgCountSent;
    private static int msgCountReceived;

    // 0 means unrestricted.
    private static long maxWriteQueueSize = 1024 * 1024 *
            Integer.getInteger(ClientSocket.class.getName() + ".maxWriteQueueSizeMB", 0);
    private ClientSocketListener listener;
    private Socket socket;
    protected boolean master;
    private BigInteger myClientID = null;
    private BigInteger otherClientID = null;
    private Thread readSocketThread = null;
    private Thread writeSocketThread = null;
    private boolean stopReading = false;
    private boolean stopWriting = false;
    private DataOutputStream dos = null;
    private DataInputStream dis = null;
    private final LinkedList<Message> writeQueue = new LinkedList<Message>();
    private long writeQueueSize = 0;
    private long numBytesRead;
    protected StatisticsReporter statReporter;
    private long numBytesWritten;

    private boolean closed = false;

    // Master Only: Don't write anything until this socket is enabled
    // The master will enable this when it has finished enqueueing the
    // welcome messages.
    protected boolean enable = false;

    // For debug
    // TODO: low why do the printed packet numbers for the sends start at 1 and not 0?
    //private int packetNumber = 0;

    protected ClientSocket(BigInteger clientID, Socket socket, ClientSocketListener listener) {
        myClientID = clientID;
        this.socket = socket;
        this.listener = listener;
    }

    /**
     * Returns the Wonderland client ID of the client on the other end.
     */
    public BigInteger getOtherClientID() {
        return otherClientID;
    }

    synchronized long getNumBytesRead() {
        return numBytesRead;
    }

    synchronized void setNumBytesRead(long n) {
        numBytesRead = n;
    }

    synchronized long getNumBytesWritten() {
        return numBytesWritten;
    }

    synchronized void setNumBytesWritten(long n) {
        numBytesWritten = n;
    }

    long getWriteQueueNumBytesInQueue() {
        return writeQueueSize;
    }

    int getWriteQueueNumMsgsInQueue() {
        return writeQueue.size();
    }

    int getNumBytesInSocketBuf () {
        try {
            return socket.getSendBufferSize();
        } catch (SocketException ex) {
            return 0;
        }
    }

    /* For debug */
    public static void toggleStatsEnable() {
        ENABLE_STATS = !ENABLE_STATS;
        logger.severe("Client socket statistics are " +
                (ENABLE_STATS ? "enabled" : "disabled"));
    }
    /**/

    /**
     * Perform initial handshake. We write our client ID and expect to read the 
     * other client's ID. Then start the read and write threads. Note: this handshake
     * is slightly different than 0.4.
     */
    public boolean initialize() {
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
        } catch (Exception ex) {
            logger.warning("Failed to get Data Input/Output streams!");
            return false;
        }

        // Write our client ID (blocking)
        if (!writeClientID(myClientID)) {
            logger.warning("Failed to write my session id");
            return false;
        }

        // Read the others ID (blocking)
        if ((otherClientID = readClientID()) == null) {
            logger.warning("Failed to read remote client ID");
            return false;
        }

        // Start the read thread
        readSocketThread = new Thread(new Runnable() {

            public void run() {
                readLoop();
            }
        });
        readSocketThread.setName("-readThread");

        // Start the write thread
        writeSocketThread = new Thread(new Runnable() {

            public void run() {
                writeLoop();
            }
        });
        writeSocketThread.setName("-writeThread");

        logger.info("STARTED client socket\n" +
                "  remote ClientID = " + otherClientID +
                ", maxWriteQueueSize = " + maxWriteQueueSize);

        return true;
    }

    public void start() {
        readSocketThread.start();
        writeSocketThread.start();
    }

    public void close () {
        close(true);
    }

    public void close (boolean callListener) {
        if (closed) return;

        enable = false;

        if (!socket.isClosed()) {
            logger.info("CLOSING socket");
            try {
                socket.close();
            } catch (Exception e) {
                logger.warning("Error CLOSING socket!!");
            }
        }

        // Wake up any waiters on the write queue
        synchronized (writeQueue) {
            writeQueue.notifyAll();
        }

        if (callListener) {
            listener.otherClientHasLeft(otherClientID);
        }

        if (ENABLE_STATS) {
            statReporter.stop();
        }

        stopReading = true;
        stopWriting = true;
        closed = true;

        logger.info("ClientSocket closed");
    }

    protected byte[] readMessage() throws IOException, EOFException {
        if (DEBUG) {
            msgCountReceived = dis.readInt();
            logger.fine("Received message " + msgCountReceived);
        }

        //For Debug: int pktNum = dis.readInt();
        int len = dis.readInt();
        byte[] buf = new byte[len];
        
        dis.readFully(buf, 0, len);

        if (DEBUG_IO) {
            logger.severe("clientSocket = " + this);
            //logger.severe("RECVD (" + pktNum + "): " + len + " bytes");
            logger.severe("RECVD: " + len + " bytes");
            print10bytes(buf);
        }

        return buf;
    }

    /**
     * Wait until the size of the write queue changes. (Note that this
     * might be either because something was added to it or something was removed).
     * Must be called inside a synchronized (writeQueue) block.
     * Note: this method must be called in a loop.
     * @return true if the socket is closed.
     */
    private boolean writeQueueWait() {
        if (socket.isClosed()) {
            return true;
        }

        try {
            writeQueue.wait();
        } catch (InterruptedException e) {
        }

        return socket.isClosed();
    }

    /**
     * Send the entire contents of the given byte array to the client on the other side. 
     * Do not block; just enqueue buffer to be written out later by write loop.
     * @param buf The byte array to send.
     */
    public void send(byte[] buf) throws IOException {
        send(buf, buf.length, false);
    }

    /**
     * Send the entire contents of the given byte array to the client on the other side. 
     * Do not block; just enqueue buffer to be written out later by write loop.
     * @param buf The byte array to send.
     * @param force If true, ignore the enable condition and send the message anyway.
     * (Used by the master to send welcome messages). Only used by master.
     */
    public void send(byte[] buf, boolean force) throws IOException {
        send(buf, buf.length, force);
    }

    /**
     * Send the entire contents of the given byte array to the client on the other side. 
     * Do not block; just enqueue buffer to be written out later by write loop.
     * @param buf The byte array to send.
     * @param len The number of bytes to send.
     * @param force If true, ignore the enable condition and send the message anyway.
     * (Used by the master to send welcome messages). Only used by master.
     */
    public void send(byte[] buf, int len, boolean force) throws IOException {

        // On the master only, don't write anything until enabled
        // NOTE: it is okay to drop these messages on the floor because the state they
        // contain will be sent to the slave during the upcoming sync message.
        if (master && !enable && !force) {
            return;
        }

        synchronized (writeQueue) {
            // TODO: krishna note: This might blow up the memory if too many messages
            // get backed up. Use ArrayBlockingQueue<E> of fixed capacity
            // or better still, keep track of total bytes backed up and limit it.
            if (maxWriteQueueSize > 0) {
                while (writeQueueSize >= maxWriteQueueSize) {
                    logger.finer("Waiting for socket to drain:\n" +
                            "     writeQueueSize = " + writeQueueSize + "\n" +
                            "     maxWriteQueueSize = " + maxWriteQueueSize);

                    boolean socketIsClosed = writeQueueWait();
                    if (socketIsClosed) {
                        return;
                    }
                }
            }

            writeQueue.add(new Message(buf, len));
            writeQueueSize += len;
            // Notify waiters that the size of the write queue has changed
            writeQueue.notifyAll();
        }
    }

    /**
     * Performs a blocking write of the given client ID.
     * @param clientID The client ID to write.
     */
    private boolean writeClientID(BigInteger clientID) {
        try {
            byte[] buf = clientID.toByteArray();
            writeMessageBuf(buf, buf.length);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * Performs a blocking read of the client ID sent by the initial handshake of the 
     * client on the other end.
     * @return The client ID read.
     */
    private BigInteger readClientID() {
        try {
            byte[] buf = readMessage();
            if (buf != null) {
                return new BigInteger(buf);
            }
        } catch (Exception e) {
        }

        return null;
    }

    public void readLoop() {
        while (!stopReading && !socket.isClosed()) {
            try {

                byte buf[] = readMessage();

                if ((buf != null) && (listener != null)) {
                    listener.receivedMessage(otherClientID, buf);
                }

                if (ENABLE_STATS && !master) {
                    synchronized (ClientSocket.this) {
                        numBytesRead += buf.length;
                    }
                }

            } catch (EOFException e) {
                close();
                break;

            } catch (SocketException e) {
                if (!socket.isClosed()) {
                    logger.info("SocketException during reading occurred but socket is still open. Exception = " + e);
                } else {
                    logger.info("Socket closed on the other side during reading. Closing and cleaning up...");
                }
                close();
                break;

            } catch (Exception e) {
                logger.log(Level.WARNING, "Exception occurred during reading. Exception = " + e, e);
                close();
                break;
            }
        }
        logger.info("EXIT client socket read thread\n" + " otherClientID = " + otherClientID);
    }

    public void writeLoop() {
        while (!stopWriting && !socket.isClosed()) {
            Message msg = null;

            synchronized (writeQueue) {
                closed = false;

                while (!closed && (writeQueue.size() <= 0)) {
                    closed = writeQueueWait();
                }

                if (!closed && (writeQueue.size() > 0)) {
                    msg = writeQueue.remove(0);
                    writeQueueSize -= msg.len;
                    // Notify waiters that the size of the write queue has changed
                    writeQueue.notifyAll();
                }
            }

            if (msg == null || msg.buf == null) {
                continue;
            }

            if (ENABLE_STATS) {
                synchronized (this) {
                    numBytesWritten += msg.len;
                }
            }

            if (DEBUG_IO) {
                logger.severe("clientSocket = " + this);
                //logger.severe("SENT (" + packetNumber + "): " + msg.len + " bytes");
                logger.severe("SENT: " + msg.len + " bytes");
                print10bytes(msg.buf);
            }

            if (!writeMessageBuf(msg.buf, msg.len)) {
                return;
            }
        }

        logger.info("EXIT client socket write thread\n" +
                " remote ClientID = " + otherClientID);
    }

    private final boolean writeMessageBuf(byte[] buf, int len) {
        try {
            // For debug: dos.writeInt(packetNumber++);
            dos.writeInt(len);
            dos.write(buf, 0, len);
        } catch (EOFException e) {
            close();
            return false;
        } catch (Exception e) {
            logger.info("Exception occurred during writing. Exception = " + e);
            close();
            return false;
        }

        return true;
    }

    // For Debug
    private static void print10bytes(byte[] bytes) {
        int n = (bytes.length > 10) ? 10 : bytes.length;
        for (int i = 0; i < n; i++) {
            System.err.print(Integer.toHexString(bytes[i] & 0xff) + " ");
        }
        System.err.println();
    }
}
