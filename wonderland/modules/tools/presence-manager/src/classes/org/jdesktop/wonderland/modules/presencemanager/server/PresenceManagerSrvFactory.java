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
package org.jdesktop.wonderland.modules.presencemanager.server;

/**
 * Server-side factory for presence manager implementation
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class PresenceManagerSrvFactory {
    public static PresenceManagerSrv getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Singleton for holding
     */
    private static class SingletonHolder {
        private static final PresenceManagerSrv INSTANCE = new PresenceManagerSrvImpl();
    }
}
