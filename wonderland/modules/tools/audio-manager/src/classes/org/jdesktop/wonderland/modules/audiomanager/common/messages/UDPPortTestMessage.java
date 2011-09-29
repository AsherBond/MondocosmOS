/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., All Rights Reserved
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
package org.jdesktop.wonderland.modules.audiomanager.common.messages;

import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.comms.ProtocolVersion;

import org.jdesktop.wonderland.common.messages.Message;

/**
 * The initial message that a client must send to the Wonderland server
 * in order to specify a communications protocol to use.
 * @author jprovino
 */
@ExperimentalAPI
public class UDPPortTestMessage extends Message {

    private String host;
    private int port;
    private int duration;

    public UDPPortTestMessage(String host, int port, int duration) {
	this.host = host;
	this.port = port;
	this.duration = duration;
    }

    public String getHost() {
	return host;
    }

    public int getPort() {
	return port;
    }

    public int getDuration() {
	return duration;
    }

}
