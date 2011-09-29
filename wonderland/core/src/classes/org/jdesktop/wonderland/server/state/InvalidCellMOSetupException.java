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
package org.jdesktop.wonderland.server.state;

/**
 * The InvalidWFSCellException is thrown if an invalid WFS cell file is loaded.
 * Processing continues after this exception, ignoring the error.
 * <p>
 * @author jslott
 * @deprecated
 * TODO delete me
 */
public class InvalidCellMOSetupException extends Exception {
    
    /**
     * Creates a new instance of <code>InvalidWFSCellException</code> without
     * detail message.
     */
    public InvalidCellMOSetupException() {
    }
    
    /**
     * Constructs an instance of <code>InvalidWFSCellException</code> with the 
     * specified detail message.
     * @param msg the detail message.
     */
    public InvalidCellMOSetupException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>InvalidWFSCellException</code> with the 
     * specified cause.
     * @param cause the cause.
     */
    public InvalidCellMOSetupException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs an instance of <code>InvalidWFSCellException</code> with the 
     * specified detail message and cause.
     & @param msg the detail message.
     * @param cause the cause.
     */
    public InvalidCellMOSetupException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
