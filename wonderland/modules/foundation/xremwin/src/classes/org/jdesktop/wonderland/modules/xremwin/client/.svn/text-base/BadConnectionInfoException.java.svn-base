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
package org.jdesktop.wonderland.modules.xremwin.client;

import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * An exception which is thrown to indicate that a connection could
 * not be made using the given connection info. This can happen sometimes
 * during a warm start of the SAS.
 *
 * @author deronj
 */
@ExperimentalAPI
public class BadConnectionInfoException extends Exception {

    private AppXrwConnectionInfo connInfo;

    public BadConnectionInfoException (AppXrwConnectionInfo connInfo) {
        this.connInfo = connInfo;
    }

    public AppXrwConnectionInfo getConnInfo () {
        return connInfo;
    }
}
