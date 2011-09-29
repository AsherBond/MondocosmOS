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

/**
 * Report an error to a client.
 * @author jkaplan
 */
@ExperimentalAPI
public class ErrorMessage extends ResponseMessage {
    /** the error message associated with the error */
    private String errorMessage;
    
    /** the exception associated with this error */
    private Throwable errorCause;
    
    /**
     * Create a new error message in response to the a request
     * message with the given id.
     * @param messageID the ID of the request message
     */
    public ErrorMessage(MessageID messageID) {
        this (messageID, null, null);
    }
    
    /**
     * Create a new error message in response to the a request
     * message with the given id.
     * @param messageID the ID of the request message
     * @param errorMessage the error message
     */
    public ErrorMessage(MessageID messageID, String errorMessage) {
        this (messageID, errorMessage, null);
    }
    
    /**
     * Create a new error message in response to the a request
     * message with the given id.
     * @param messageID the ID of the request message
     * @param errorCause the cause of the error
     */
    public ErrorMessage(MessageID messageID, Throwable errorCause) {
        this (messageID, null, errorCause);
    }
    
    /**
     * Create a new error message in response to a request message with the
     * given id.
     * @param messageID the ID of the request message
     * @param errorMessage the error messsage
     * @param errorCause the cause of the error
     */
    public ErrorMessage(MessageID messageID, String errorMessage, 
                        Throwable errorCause)
    {
        super (messageID);
        
        this.errorMessage = errorMessage;
        this.errorCause = errorCause;
    }

    /**
     * Get the error message
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Set the error message
     * @param errorMessage the error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * Get the error cause
     * @return the error cause
     */
    public Throwable getErrorCause() {
        return errorCause;
    }

    /**
     * Set the error cause
     * @param errorCause the error cause
     */
    public void setErrorCause(Throwable errorCause) {
        this.errorCause = errorCause;
    }
}
