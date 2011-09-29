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
package org.jdesktop.wonderland.modules.xremwin.client;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import java.io.EOFException;

/*
 ** This class allows us to read the incoming byte arrays from the
 ** RemoteWindowServer client as a sequential list of bytes. It provides methods
 ** similar for InputStream for decoding shorts and ints as well.
 *
 * @author deronj
 */
@ExperimentalAPI
class DataBufferQueue {

    private CurrentBuffer curBuf = new CurrentBuffer();
    private LinkedList<ClientData> bufQueue = new LinkedList<ClientData>();
    private boolean closed = false;

    class ClientData {

        byte[] buf;
    }

    static class CurrentBuffer {

        byte[] buf = null;
        int idx = 0;

        void setBuffer(byte[] buf) {
            this.buf = buf;
            idx = 0;
        }

        void setBuffer(byte[] buf, int startIdx) {
            this.buf = buf;
            idx = startIdx;
        }

        boolean hasData() {
            return buf != null && idx < buf.length;
        }

        int numBytesAvail() {
            if (buf == null) {
                return 0;
            }
            return buf.length - idx;
        }

        void skip(int n) {
            idx += n;
        }

        byte nextByte() {
            return buf[idx++];
        }

        short nextShort() {
            int value = ((buf[idx + 0] << 8) & 0xff00) |
                    ((buf[idx + 1]) & 0x00ff);
            idx += 2;
            return (short) value;
        }

        int nextInt() {
            int value = ((buf[idx + 0] << 24) & 0xff000000) |
                    ((buf[idx + 1] << 16) & 0x00ff0000) |
                    ((buf[idx + 2] << 8) & 0x0000ff00) |
                    ((buf[idx + 3]) & 0x000000ff);
            idx += 4;
            return value;
        }

        float nextFloat() {
            byte[] b = new byte[4];
            b[0] = buf[idx + 0];
            b[1] = buf[idx + 1];
            b[2] = buf[idx + 2];
            b[3] = buf[idx + 3];
            idx += 4;
            return bytesToFloat(b);
        }

        float bytesToFloat(byte[] bytes) {
            ByteBuffer byteBuf = ByteBuffer.allocate(4);
            byteBuf.put(bytes);
            byteBuf.rewind();
            return byteBuf.getFloat();
        }

        // For debug
        void printNumeric() {
            printByteArrayNumeric(buf);
        }

        private void printByteArrayNumeric(byte[] buf) {
            for (byte b : buf) {
                System.err.print(Integer.toHexString(((int) b & 0xff)) + " ");
            }
            System.err.println();
        }
    }

    // Called when the master or server connection is disconnected.
    public void close () {
        closed = true;
        synchronized (this) {
            notifyAll();
        }
    }

    public void printCurBufNumeric() {
        curBuf.printNumeric();
    }

    // Debug
    int numEnqueues = 0;

    // Called by the message receiver to add an incoming buffer to the queue
    synchronized void enqueue(byte[] buf) {
        /* Debug 
        numEnqueues++;
        System.err.println("Enqueue buf " + numEnqueues + ", buflen = " +
        buf.length);
        */

        ClientData cd = new ClientData();
        cd.buf = buf;
        bufQueue.addLast(cd);
        try {
            notifyAll();
        } catch (Exception e) {
        }
    }

    private synchronized ClientData dequeue() throws EOFException {
        while (bufQueue.size() == 0 && !closed) {
            try {
                wait();
            } catch (Exception e) {
            }
        }

        if (closed) {
            throw new EOFException();
        }

        ClientData cd = bufQueue.getFirst();
        bufQueue.remove(0);

        //System.err.println("Dequeued buf, buflen = " + cd.buf.length);
        return cd;
    }

    // Return the next current buffer for next*() to pull from.
    // This blocks if no buffer is available.
    private void getNextCurBufIfNoData() throws EOFException {
        //System.err.println("Enter getNextCurBufIfNoData");
        if (curBuf.hasData()) {
            return;
        }
        ClientData cd = dequeue();
        // Note: at least for now, cd.client is always the RemoteWindowServer client
        curBuf.setBuffer(cd.buf);
    }

    // Read a single byte from the RemoteWindowServer. Block if a byte isn't available.
    // Throw EOFException if this buffer queue is closed.
    byte nextByte() throws EOFException {
        //System.err.println("Enter DBQ.nextByte");
        getNextCurBufIfNoData();
        return curBuf.nextByte();
    }

    // Fills a byte array of a given length with data from the RemoteWindowServer. 
    // Block if all bytes aren't available.
    void nextBytes(byte[] buf) throws EOFException {
        nextBytes(buf, buf.length);
    }

    // Fills a byte array with the specified number of data bytes from the
    // RemoteWindowServer. Block if all bytes aren't available.
    void nextBytes(byte[] buf, int len) throws EOFException {
        getNextCurBufIfNoData();
        int numAvail = curBuf.numBytesAvail();
        if (numAvail < len) {
            int i;

            // Get the available bytes the fast way
            for (i = 0; i < numAvail; i++) {
                buf[i] = curBuf.buf[curBuf.idx + i];
            }
            curBuf.idx += numAvail;

            // Get the rest the slower way
            // Note: we could optimize this more, but nextBytes()
            // is used only during the initial handshake..
            while (i < len) {
                buf[i++] = nextByte();
            }
        } else {
            for (int i = 0; i < len; i++) {
                buf[i] = curBuf.buf[curBuf.idx + i];
            }
            curBuf.idx += len;
        }
    }

    // Skip n bytes without returning any
    // Note: this method isn't used for anything performance critical
    // so we'll skip the slower way
    void skipBytes(int n) throws EOFException {
        for (int i = 0; i < n; i++) {
            nextByte();
        }
    }

    // Read a short from the RemoteWindowServer. Block if a full short isn't available.
    short nextShort() throws EOFException {
        getNextCurBufIfNoData();
        if (curBuf.numBytesAvail() < 2) {
            // There must be at least one, so read it directly
            byte b0 = curBuf.nextByte();
            // For the next byte, get block for new buffers as necessary
            byte b1 = nextByte();
            return (short) ((b0 << 8) | b1);
        } else {
            return curBuf.nextShort();
        }
    }

    // Read an int from the RemoteWindowServer. Block if a full int isn't available.
    int nextInt() throws EOFException {
        getNextCurBufIfNoData();
        if (curBuf.numBytesAvail() < 4) {
            // There must be at least one, so read it directly
            byte b0 = curBuf.nextByte();
            // For the next 3 bytes, get block for new buffers as necessary
            byte b1 = nextByte();
            byte b2 = nextByte();
            byte b3 = nextByte();
            return (b0 << 24) | (b1 << 16) | (b2 << 8) | b3;
        } else {
            return curBuf.nextInt();
        }
    }

    // Read a float from the RemoteWindowServer. Block if a full float isn't available.
    float nextFloat() throws EOFException {
        getNextCurBufIfNoData();
        if (curBuf.numBytesAvail() < 4) {
            // There must be at least one, so read it directly
            byte[] b = new byte[4];
            b[0] = curBuf.nextByte();
            // For the next 3 bytes, get block for new buffers as necessary
            b[1] = nextByte();
            b[2] = nextByte();
            b[3] = nextByte();
            return curBuf.bytesToFloat(b);
        } else {
            return curBuf.nextFloat();
        }
    }

    // Return the next complete buffer. Throw an exception if there 
    // is partial data in the current buffer.
    byte[] nextBuffer() throws EOFException {
        if (curBuf.numBytesAvail() != 0) {
            throw new RuntimeException("Remote window protocol error");
        }

        ClientData cd = dequeue();
        curBuf.setBuffer(null);
        return cd.buf;
    }

    int numBytesAvail() {
        return curBuf.numBytesAvail();
    }
}

