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
package org.jdesktop.wonderland.modules.security.server.service;

import java.util.Set;
import org.jdesktop.wonderland.modules.security.common.Principal;

/**
 * Resolve user principals
 * @author jkaplan
 */
public interface UserPrincipalResolver {
    /**
     * Get all principals (user and group) for the given user name. The blocking
     * boolean controls whether this method is allowed to block.  If blocking
     * is true, this method will block until principals are available for the
     * given user.  If blocking is set to false, the method must return
     * immediately, but may return null if no principals are available for
     * the given user.
     * @param username the username to resolve
     * @param blocking true if the method may block, or false if not
     * @return the principals associated with the given user.  The minimal
     * set is a single user principal with this user's username.  If blocking
     * is set to false, null indicates that the values can't be read.
     */
    public Set<Principal> getPrincipals(String username, boolean blocking);
}
