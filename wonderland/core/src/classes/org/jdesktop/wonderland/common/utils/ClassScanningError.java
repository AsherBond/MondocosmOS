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
package org.jdesktop.wonderland.common.utils;

/**
 * Error thrown during instantiation of classes from a scanned class loader
 * @author jkaplan
 */
public class ClassScanningError extends Error {

    /**
     * Creates a new instance of <code>ClassScanningError</code> without detail
     * message.
     */
    public ClassScanningError() {
    }


    /**
     * Constructs an instance of <code>ClassScanningError</code> with the
     * specified detail message.
     * @param msg the detail message.
     */
    public ClassScanningError(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>ClassScanningError</code> with the
     * specified cause.
     * @param cause the cause.
     */
    public ClassScanningError(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs an instance of <code>ClassScanningError</code> with the
     * specified detail message and cause.
     * @param msg the detail message.
     * @param cause the cause.
     */
    public ClassScanningError(String msg, Throwable cause) {
        super(msg, cause);
    }
}
