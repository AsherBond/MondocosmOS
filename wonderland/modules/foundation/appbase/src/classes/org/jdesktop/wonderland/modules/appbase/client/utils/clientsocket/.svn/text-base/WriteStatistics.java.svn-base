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

import org.jdesktop.wonderland.modules.appbase.client.utils.stats.StatisticsSet;

/**
 * Maintains statistics on the number of socket writes performed by the Client Socket code.
 *
 * @author deronj
 */

class WriteStatistics extends StatisticsSet {

    /** The associated client socket. */
    final private ClientSocket clientSocket;

    // The number of bytes written to the slave
    private long numBytesWritten;

    // The number of bytes in the queue's buffers
    // (Same as writeQueueSize)
    private long numBytesInQueue;

    // The number of messages in the queue
    private long numMsgsInQueue;

    // The number of bytes in the socket write buffer
    private long numBytesInSocketBuf;

    protected WriteStatistics(ClientSocket clientSocket) {
        super("Socket Write");
        this.clientSocket = clientSocket;
    }

    // Collect the latest stats
    protected void probe() {

        synchronized (clientSocket) {
            numBytesWritten = clientSocket.getNumBytesWritten();
        }

        // Don't need to lock because we never reset these
        numBytesInQueue = clientSocket.getWriteQueueNumBytesInQueue();
        numMsgsInQueue = clientSocket.getWriteQueueNumMsgsInQueue();

        numBytesInSocketBuf = clientSocket.getNumBytesInSocketBuf();
    }

    protected void reset() {
        synchronized (clientSocket) {
            clientSocket.setNumBytesWritten(0L);
        }
    }

    protected void accumulate(StatisticsSet cumulativeStats) {
        WriteStatistics stats = (WriteStatistics) cumulativeStats;
        stats.numBytesWritten += numBytesWritten;
        stats.numBytesInQueue += numBytesInQueue;
        stats.numMsgsInQueue += numMsgsInQueue;
        stats.numBytesInSocketBuf += numBytesInSocketBuf;
    }

    protected void max(StatisticsSet maxStats) {
        WriteStatistics stats = (WriteStatistics) maxStats;
        stats.numBytesWritten = max(stats.numBytesWritten, numBytesWritten);
        stats.numBytesInQueue = max(stats.numBytesInQueue, numBytesInQueue);
        stats.numMsgsInQueue = max(stats.numMsgsInQueue, numMsgsInQueue);
        stats.numBytesInSocketBuf = max(stats.numBytesInSocketBuf, numBytesInSocketBuf);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void appendStats(StringBuffer sb) {
        sb.append("numBytesWritten = " + numBytesWritten + "\n");
        sb.append("numBytesInQueue = " + numBytesInQueue + "\n");
        sb.append("numMsgsInQueue = " + numMsgsInQueue + "\n");
        sb.append("numBytesInSocketBuf = " + numBytesInSocketBuf + "\n");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void appendStatsAndRates(StringBuffer sb, double timeSecs) {
        appendStats(sb);

        // Calculate and print rates
        double numBytesWrittenPerSec = numBytesWritten / timeSecs;
        sb.append("numBytesWrittenPerSec = " + numBytesWrittenPerSec + "\n");
    }
}

