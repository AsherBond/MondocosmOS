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
package org.jdesktop.wonderland.common.messages;

import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.comms.ProtocolVersion;

/**
 * The initial message that a client must send to the Wonderland server
 * in order to specify a communications protocol to use.
 * @author jkaplan
 */
@ExperimentalAPI
public class ProtocolSelectionMessage extends Message {
    /** the name of the protocol */
    private String protocolName;
    
    /** the version of the protocol */
    private ProtocolVersion protocolVersion;

    /**
     * Create a new ProtocolSelectionMessage
     * @param protocolName the name of the protocol
     * @param protocolVersion the version of the protocol
     */
    public ProtocolSelectionMessage(String protocolName,
                                    ProtocolVersion protocolVersion)
    {
        this.protocolName = protocolName;
        this.protocolVersion = protocolVersion;
    }

    /**
     * Get the name of the protocol
     * @return the protocol name
     */
    public String getProtocolName() {
        return protocolName;
    }

    /**
     * Set the name of the protocol
     * @param protocolName the name of the protocol
     */
    public void setProtocolName(String protocolName) {
        this.protocolName = protocolName;
    }

    /**
     * Get the protocol version
     * @return the protocol version
     */
    public ProtocolVersion getProtocolVersion() {
        return protocolVersion;
    }

    /**
     * Set the protocol version
     * @param protocolVersion the version of the protocol
     */
    public void setProtocolVersion(ProtocolVersion protocolVersion) {
        this.protocolVersion = protocolVersion;
    }
}
