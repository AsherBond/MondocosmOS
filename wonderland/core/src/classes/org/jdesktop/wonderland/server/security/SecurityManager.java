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

/**
 * Interface for handling security checks.
 * @author jkaplan
 */
public interface SecurityManager {
    /**
     * Run the given task after performing a security check.  The
     * security check is done by evaluating the set of requested resources
     * and actions for the current identity.  The task is then called with
     * the result of that check in the form of a set of granted actions.
     * It is up to the task to properly handle the set of permissions that
     * are granted.
     * <p>
     * Note that no guarantees are made about when the secure task is executed.
     * A best effort is made to execute the task in the same transaction
     * as the request, but in some cases (for example when security policy
     * is being loaded from an external source) a separate transaction may
     * be necessary.  In this case, the SecureTask will be run inside a
     * separate Darkstar task that is executed after the check is made.
     * <p>
     * TODO: define ordering issues, if any, with this approach
     *
     * @param request the requested resources and actions on those
     * resources
     * @param task the task to perform after the security check has been
     * completed
     */
    public void doSecure(ResourceMap request, SecureTask task);
}
