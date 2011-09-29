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
package org.jdesktop.wonderland.server.comms;

import org.jdesktop.wonderland.common.security.Action;

/**
 * An action that requests permission to connect to a particular handler
 * @author jkaplan
 */
public class ConnectAction extends Action {
    /** the name of this action */
    private static final String NAME = "CONNECT";

    /** the display name for this action */
    private static final String DISPLAY_NAME = "Connect to a Connection";

    /**
     * Singleton -- use getInstance() instead.
     */
    public ConnectAction() {
        super (NAME, null, DISPLAY_NAME, null);
    }

    /**
     * Get an instance of ConnectAction
     * @return a ConnectAction instance
     */
    public static ConnectAction getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static final class SingletonHolder {
        private static final ConnectAction INSTANCE = new ConnectAction();
    }
}
