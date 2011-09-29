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
package org.jdesktop.wonderland.common.login;

/**
 * An exception thrown during authentication
 * @author jkaplan
 */
public class AuthenticationException extends Exception {
    private String message;

    /**
     * Creates a new instance of <code>AuthenticationException</code>
     * without detail message.
     */
    public AuthenticationException() {
    }

    /**
     * Constructs an instance of <code>AuthenticationException</code>
     * with the specified detail message.
     * @param msg the detail message.
     */
    public AuthenticationException(String msg) {
        this.message = msg;
    }

    /**
     * Constructs an instance of <code>AuthenticationException</code>
     * with the specified root cause.
     * @param cause the exception cause
     */
    public AuthenticationException(Throwable cause) {
        super (cause);
    }

    /**
     * Constructs an instance of <code>AuthenticationException</code>
     * with the specified message and root cause.
     * @param msg the detail msg
     * @param cause the exception cause
     */
    public AuthenticationException(String msg, Throwable cause) {
        super (cause);

        this.message = msg;
    }

    /**
     * Override so we don't include the class name in the message
     * @return
     */
    @Override
    public String getMessage() {
        return message;
    }
}
