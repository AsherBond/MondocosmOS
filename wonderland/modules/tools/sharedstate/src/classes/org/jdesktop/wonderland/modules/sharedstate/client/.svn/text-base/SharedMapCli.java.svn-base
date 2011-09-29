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
package org.jdesktop.wonderland.modules.sharedstate.client;

import org.jdesktop.wonderland.modules.sharedstate.common.SharedMap;

/**
 * Extension of the SharedMap to work on the client.
 * @author jkaplan
 */
public interface SharedMapCli extends SharedMap {
    /**
     * Add a listener that will be notified when any property in the map
     * changes.
     * @param listener the listener to notify of changes
     */
    public void addSharedMapListener(SharedMapListenerCli listener);

    /**
     * Remove a listener that will be notified when any property in the map
     * changes.
     * @param listener the listener to remove
     */
    public void removeSharedMapListener(SharedMapListenerCli listener);

    /**
     * Add a listener that will be notified when properties matching the
     * given regular expression change
     * @param propRegex the regular expression to match (this can just be a
     * property name)
     * @param listener the listener to notify
     */
    public void addSharedMapListener(String propRegex,
                                     SharedMapListenerCli listener);

    /**
     * Remove a listener that will be notified when properties matching the
     * given regular expression change
     * @param propRegex the regular expression to match (this can just be a
     * property name)
     * @param listener the listener to remove
     */
    public void removeSharedMapListener(String propRegex,
                                        SharedMapListenerCli listener);
}
