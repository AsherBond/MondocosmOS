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

import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * Listen for changes to the session status.  Status is one of
 * CONNECTED, CONNECTING or DISCONNECTED
 * @author jkaplan
 */
@ExperimentalAPI
public interface SessionStatusListener {
    /**
     * Called when a session changes state, to one of the
     * CONNECTED, CONNECTING, or DISCONNECTED states.
     */
    public void sessionStatusChanged(WonderlandSession session, 
                                     WonderlandSession.Status status);
}
