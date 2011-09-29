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
package org.jdesktop.wonderland.modules.security.weblib;

import java.security.Principal;

/**
 *
 * @author jkaplan
 */
public class UserGroupPrincipal implements Principal {
    private String name;
    private String[] groups;

    public UserGroupPrincipal(String name, String[] groups) {
        this.name = name;
        this.groups = groups;
    }

    public String getName() {
        return name;
    }

    public String[] getGroups() {
        return groups;
    }

    public boolean isMemberOfGroup(String groupName) {
        for (String g : groups) {
            if (g.equals(groupName)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserGroupPrincipal other = (UserGroupPrincipal) obj;
        if ((this.name == null) ? (other.name != null) :
            !this.name.equals(other.name))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
}
