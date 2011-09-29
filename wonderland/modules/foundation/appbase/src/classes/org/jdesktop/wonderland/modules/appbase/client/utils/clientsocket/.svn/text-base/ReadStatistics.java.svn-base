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
 * Maintains statistics on the number of socket reads performed by the Client Socket code.
 *
 * @author deronj
 */
class ReadStatistics extends StatisticsSet {

    /** The associated client socket. */
    final private ClientSocket clientSocket;

    /** The number of bytes read from the master. */
    public long numBytesRead;

    protected ReadStatistics (ClientSocket clientSocket) {
        super("Socket Read");
	this.clientSocket = clientSocket;
    }

    protected void probe() {
        synchronized (clientSocket) {
            numBytesRead = clientSocket.getNumBytesRead();
        }
    }

    public void reset() {
        synchronized (clientSocket) {
            clientSocket.setNumBytesRead(0L);
        }
    }

    protected void accumulate(StatisticsSet cumulativeStats) {
        ReadStatistics stats = (ReadStatistics) cumulativeStats;
        stats.numBytesRead += numBytesRead;
    }

    protected void max(StatisticsSet maxStats) {
        ReadStatistics stats = (ReadStatistics) maxStats;
        stats.numBytesRead = max(stats.numBytesRead, numBytesRead);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void appendStats (StringBuffer sb) {
        sb.append("numBytesRead = " + numBytesRead + "\n");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void appendStatsAndRates (StringBuffer sb, double timeSecs) {
        appendStats(sb);

        // Calculate and print rates
        double numBytesReadPerSec = numBytesRead / timeSecs;
        sb.append("numBytesReadPerSec = " + numBytesReadPerSec + "\n");
    }
}

