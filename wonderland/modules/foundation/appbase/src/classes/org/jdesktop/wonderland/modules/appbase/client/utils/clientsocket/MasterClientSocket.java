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

import java.math.BigInteger;
import java.net.Socket;
import org.jdesktop.wonderland.modules.appbase.client.utils.stats.StatisticsReporter;

/**
 * The master side of the socket code.
 *
 * @author deronj, Krishna Gadepalli
 */
class MasterClientSocket extends ClientSocket {

    public MasterClientSocket(BigInteger masterClientID, Socket s, ClientSocketListener listener) {
        super(masterClientID, s, listener);
        master = true;

        if (ENABLE_STATS) {
            statReporter = new StatisticsReporter(10, /* secs */
                    new WriteStatistics(this),
                    new WriteStatistics(this),
                    new WriteStatistics(this));
            statReporter.start();
        }
    }

    @Override
    public void close() {
        super.close();
        if (ENABLE_STATS) {
            statReporter.stop();
        }
    }

    @Override
    public void close(boolean callListener) {
        super.close(callListener);
        if (ENABLE_STATS) {
            statReporter.stop();
        }
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnabled() {
        return enable;
    }
}
