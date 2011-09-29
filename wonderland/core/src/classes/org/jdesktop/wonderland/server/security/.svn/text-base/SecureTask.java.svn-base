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
 * A task to execute after security checks are completed.  SecureTask is
 * similar to the Darkstar built-in Task type, but with the extra security
 * constraints.  Like Task, SecureTask instances must either be Serializable
 * or implement ManagedObject.  For tasks that are Serializable, the
 * SecurityManager will automatically persist the task.  For tasks that
 * implement ManagedObject, the caller is assumed to manage adding and
 * removing the object from persistence.
 *
 * @author jkaplan
 */
public interface SecureTask {
    /**
     * An action to perform.  This method is passed in the set
     * of resources and actions that were granted for use by this task.
     * If an action was not granted for this task, it is up to the task to
     * take that into account when making any changes.
     *
     * @param granted the resources and actions that were granted.  This
     * may include resources with empty action sets if no actions were
     * granted on the given resource.
     */
    public void run(ResourceMap granted);
}
