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
 * A message which SAS server uses to tell a SAS provider to stop an app
 * The app is identified by a launch message.
 *
 * @author deronj
 */

@InternalAPI
public class SasProviderAppStopMessage extends Message {
    
    /** The launch message ID. */
    private MessageID launchMessageID;

    /** The default constructor */
    public SasProviderAppStopMessage () {}

    /**
     * Create a new instance of SasProviderAppStopMessage
     * @param launchMessageID The message ID which identifies the app to the provider.
     */
    public SasProviderAppStopMessage (MessageID launchMessageID) {
        this.launchMessageID = launchMessageID;
    }

    public void setLaunchMessageID (MessageID launchMessageID) {
        this.launchMessageID = launchMessageID;
    }

    public MessageID getLaunchMessageID () {
        return launchMessageID;
    }
}
