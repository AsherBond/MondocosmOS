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
 * A message which the SAS provider uses to tell the server that an app has exitted.
 * 
 * @author deronj
 */

@InternalAPI
public class SasProviderAppExittedMessage extends Message {
    
    /** The message ID of the message that launched the app. */
    private MessageID launchMsgID;

    /** The exit value. */
    private int exitValue;

    /** The default constructor. */
    public SasProviderAppExittedMessage () {
        super();
    }

    /**
     * Create a new instance of SasProviderLaunchSuccessMessage.
     * @param msgID The message ID of the message that launched the app. 
     * @param exitValue The app exit value.
     */
    public SasProviderAppExittedMessage (MessageID launchMsgID, int exitValue) {
        this.launchMsgID = launchMsgID;
        this.exitValue = exitValue;
    }

    public void setLaunchMessageID (MessageID launchMsgID) {
        this.launchMsgID = launchMsgID;
    }

    public MessageID getLaunchMessageID () {
        return launchMsgID;
    }

    public void setExitValue (int exitValue) {
        this.exitValue = exitValue;
    }

    public int getExitValue () {
        return exitValue;
    }
}
