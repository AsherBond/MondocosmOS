/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

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

import com.sun.sgs.kernel.ComponentRegistry;
import java.io.Serializable;
import java.util.Set;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.auth.WonderlandIdentity;
import org.jdesktop.wonderland.common.security.Action;
import org.jdesktop.wonderland.modules.security.common.Principal;
import org.jdesktop.wonderland.modules.security.common.Principal.Type;
import org.jdesktop.wonderland.server.security.Resource;

/**
 * A resource that will grant any request to a member of a particular group
 * or the admin group, but not to others.
 * @author jkaplan
 */
public class GroupMemberResource implements Resource, Serializable {
    public static final Logger logger =
            Logger.getLogger(GroupMemberResource.class.getName());

    private final String group;

    public GroupMemberResource(String group) {
        this.group = group;
    }

    public String getId() {
        return GroupMemberResource.class.getName() + "-" + group;
    }

    public String getGroupName() {
        return group;
    }

    public Result request(WonderlandIdentity identity, Action action) {
        Set<Principal> principals =
                UserPrincipals.getUserPrincipals(identity.getUsername(), false);
        
        // if there was no result from the resolver, force the task to be
        // rescheduled to a time when we can block
        if (principals == null) {
            logger.fine("Principals for " + identity.getUsername() +
                        " not in cache.");
            return Result.SCHEDULE;
        }
        
        if (isInGroup(principals)) {
            return Result.GRANT;
        } else {
            return Result.DENY;
        }
    }

    public boolean request(WonderlandIdentity identity, Action action, 
                           ComponentRegistry registry)
    {
        Set<Principal> principals =
                UserPrincipals.getUserPrincipals(identity.getUsername(), true);
        return isInGroup(principals);
    }

    private boolean isInGroup(Set<Principal> principals) {
        logger.fine("Testing " + principals.size() + " principals for " +
                    group);

        for (Principal p : principals) {
            logger.fine("Testing principal " + p  + " for group " + group);

            // OWL issue #78: be sure to include EVERYBODY groups as well
            // as regular groups in this test
            if (p.getType() == Type.GROUP || p.getType() == Type.EVERYBODY) {
                String name = p.getId();

                if (name.equals(group) || name.equals("admin")) {
                    logger.fine("Principal " + p + " is a member of " +
                                group);
                    return true;
                }
            }
        }

        return false;
    }
}
