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
package org.jdesktop.wonderland.server.security;

import java.util.LinkedHashMap;
import org.jdesktop.wonderland.common.security.Action;

/**
 * A set of actions associated with a given resource
 * @author jkaplan
 */
public class ActionMap extends LinkedHashMap<String, Action> {
    /** the resource this action set is associated with */
    private Resource resource;

    /**
     * Create a new ActionMap referencing the given resource and
     * actions
     * @param resourceID the resource to reference
     * @param actions the initial actions for the set
     */
    public ActionMap(Resource resource, Action... actions) {
        super();

        this.resource = resource;

        // add all actions to the set
        if (actions != null) {
            for (Action action : actions) {
                put(action.getName(), action);
            }
        }
    }

    /**
     * Get the resource associated with this set
     * @return the resource
     */
    public Resource getResource() {
        return resource;
    }
}
