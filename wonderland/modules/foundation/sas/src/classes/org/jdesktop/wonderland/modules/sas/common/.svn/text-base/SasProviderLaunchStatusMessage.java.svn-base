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
package org.jdesktop.wonderland.modules.sas.common;

import org.jdesktop.wonderland.common.InternalAPI;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.common.messages.MessageID;

/**
 * A message which the SAS provider uses to respond to server launch requests.
 * 
 * @author deronj
 */

@InternalAPI
public class SasProviderLaunchStatusMessage extends Message {
    
    /** The launch status type. */
    public enum LaunchStatus { SUCCESS, FAIL };

    /** The status code of the result of launch. */
    private LaunchStatus status;

    /** The message ID of the launch request messages. */
    private MessageID launchMsgID;

    /** The connection information. */
    private String connInfo;

    /** The default constructor. */
    public SasProviderLaunchStatusMessage () {
        super();
    }

    /**
     * Create a new instance of SasProviderLaunchSuccessMessage.
     * @param status The status code of the result of launch. 
     * @param msgID The message ID of the launch request messages. 
     * @param connInfo The connection information.
     */
    public SasProviderLaunchStatusMessage (LaunchStatus status, MessageID launchMsgID, String connInfo) {
        this.status = status;
        this.launchMsgID = launchMsgID;
        this.connInfo = connInfo;
    }

    public void setStatus (LaunchStatus status) {
        this.status = status;
    }

    public LaunchStatus getStatus () {
        return status;
    }

    public void setLaunchMessageID (MessageID launchMsgID) {
        this.launchMsgID = launchMsgID;
    }

    public MessageID getLaunchMessageID () {
        return launchMsgID;
    }

    public void setConnectionInfo (String connInfo) {
        this.connInfo = connInfo;
    }

    public String getConnectionInfo () {
        return connInfo;
    }
}
