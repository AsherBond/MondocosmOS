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
package org.jdesktop.wonderland.server.comms;

import org.jdesktop.wonderland.common.comms.ProtocolVersion;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import org.jdesktop.wonderland.common.comms.WonderlandProtocolVersion;

/**
 * The default communications protocol used by the Wonderland client.
 * @author jkaplan
 */
public class WonderlandClientCommsProtocol implements CommunicationsProtocol {
    /**
     * Get the name of this protocol
     * @return "wonderland_client"
     */
    public String getName() {
        return WonderlandProtocolVersion.PROTOCOL_NAME;
    }

    /**
     * Get the version of this protocol
     * @return the protocol version
     */
    public ProtocolVersion getVersion() {
        return WonderlandProtocolVersion.VERSION;
    }

    public ClientSessionListener createSessionListener(ClientSession session, 
                                                       ProtocolVersion version) 
    {
        return new WonderlandSessionListener(session);        
    }
}
