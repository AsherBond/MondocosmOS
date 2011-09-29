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
package org.jdesktop.wonderland.modules.security.common;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Stores a set of permissions
 * @author jkaplan
 */
@XmlRootElement(name="permissions-set")
public class CellPermissions implements Serializable {
    private SortedSet<Permission> permissions = new TreeSet<Permission>();
    private Set<Principal> owners             = new TreeSet<Principal>();
    private Set<ActionDTO> allPermissions     = new TreeSet<ActionDTO>();

    public CellPermissions() {
    }

    @XmlTransient
    public Set<Permission> getPermissions() {
        return permissions;
    }

    @XmlTransient
    public Set<Principal> getOwners() {
        return owners;
    }

    @XmlTransient
    public Set<ActionDTO> getAllActions() {
        return allPermissions;
    }

    @XmlElement
    public Permission[] getPermissionsInternal() {
        return permissions.toArray(new Permission[0]);
    }

    public void setPermissionsInternal(Permission[] permissions) {
        this.permissions = new TreeSet<Permission>();
        this.permissions.addAll(Arrays.asList(permissions));
    }

    @XmlElement
    public Principal[] getOwnersInternal() {
        return owners.toArray(new Principal[0]);
    }

    public void setOwnersInternal(Principal[] owners) {
        this.owners = new TreeSet<Principal>();
        this.owners.addAll(Arrays.asList(owners));
    }

    @XmlElement
    public ActionDTO[] getAllActionsInternal() {
        return allPermissions.toArray(new ActionDTO[0]);
    }

    public void setAllActionsInternal(ActionDTO[] defaultPermissions) {
        this.allPermissions = new TreeSet<ActionDTO>();
        this.allPermissions.addAll(Arrays.asList(defaultPermissions));
    }
}
