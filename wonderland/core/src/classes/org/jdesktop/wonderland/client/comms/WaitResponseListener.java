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
import org.jdesktop.wonderland.common.messages.ResponseMessage;

/**
 * A response listener super type that lets users register a listener for
 * a response message.
 * @author kaplanj
 */
@ExperimentalAPI
public class WaitResponseListener implements ResponseListener {
    /** The response messsage that we received.  Set to non-null when
        a response is received */
    private ResponseMessage response = null;
   
    public void responseReceived(ResponseMessage response) {
        notifyResponse(response);
    }
    
    /**
     * Wait for a response to the message.  This method will return once
     * a response to the given message is received.
     * @return the ResponseMessage that was received in response to this message
     * @throws InterruptedException if the response is delayed
     */
    public synchronized ResponseMessage waitForResponse() 
            throws InterruptedException 
    {
        while (response == null) {
            wait();
        }
        
        return response;
    }
    
    /**
     * Notify that a message is received.  Subclasses that override the
     * <code>responseReceived()</code> method must call this in order
     * to notify listeners.
     * @param response the response message
     */
    protected synchronized void notifyResponse(ResponseMessage response) {
        this.response = response;
        notify();
    }
}
