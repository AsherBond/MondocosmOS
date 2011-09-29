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
package org.jdesktop.wonderland.modules.sharedstate.server;

import org.jdesktop.wonderland.modules.sharedstate.common.SharedMap;

/**
 * Extension to the shared map for server listeners.
 * @author jkaplan
 */
public interface SharedMapSrv extends SharedMap {
    /**
     * Add a server listener that will be notified and can veto property
     * changes.  A server listener must either be serializable or implement
     * managed object.
     * @param listener the listener to add
     */
    public void addSharedMapListener(SharedMapListenerSrv listener);

    /**
     * Remove a server listener.
     * @param listener the listener to remove
     */
    public void removeSharedMapListener(SharedMapListenerSrv listener);
}
