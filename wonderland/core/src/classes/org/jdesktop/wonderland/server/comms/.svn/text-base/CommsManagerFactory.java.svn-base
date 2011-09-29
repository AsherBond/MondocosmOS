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

/**
 * Create the comms manager.
 * @author jkaplan
 */
public class CommsManagerFactory {
    /**
     * Setup the comms manager
     */
    public static void initialize() {
        // initialize the comms manager
        CommsManagerImpl.initialize();
    }

    /**
     * Get the comms manager
     * @return the comms manager
     */
    public static CommsManager getCommsManager() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Hold the singleton CommsManagerImpl instance. A singleton is OK here
     * because the CommsManagerImpl is stateless.  All changes are made by
     * first dereferencing a ManagedObject.
     */
    private static class SingletonHolder {
        private static final CommsManagerImpl INSTANCE = new CommsManagerImpl();
    }
}
