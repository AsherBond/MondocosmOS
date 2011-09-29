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
package org.jdesktop.wonderland.common.comms.messages;

import java.util.Properties;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.Message;

/**
 * Message used to request attaching a new client
 * @author jkaplan
 */
public class AttachClientMessage extends Message {
    /** the client type to attach */
    private ConnectionType type;
    
    /** intialization information for the connection */
    private Properties properties;
    
    /**
     * Create a new AttachClientMessage
     * @param type the type of client to attach
     * @param properties the properties to send to the server
     * about this message
     */
    public AttachClientMessage(ConnectionType type, Properties properties) {
        this.type = type;
        this.properties = properties;
    }
    
    /**
     * Get the client type
     * @return the client type
     */
    public ConnectionType getClientType() {
        return type;
    }
    
    /**
     * Get the attach properties
     * @return the attach properties
     */
    public Properties getProperties() {
        return properties;
    }
}
