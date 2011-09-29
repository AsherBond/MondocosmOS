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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A principal that can be added to the security component.
 * @author jkaplan
 */
@XmlRootElement(name="permission")
public class Permission implements Serializable, Comparable {
    public enum Access { GRANT, DENY };

    private Principal principal;
    private ActionDTO action;
    private Access access;

    public Permission() {
    }

    public Permission(Principal principal, ActionDTO action, Access access) {
        this.principal = principal;
        this.action = action;
        this.access = access;
    }

    @XmlElement
    public Principal getPrincipal() {
        return principal;
    }

    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    @XmlElement
    public ActionDTO getAction() {
        return action;
    }

    public void setAction(ActionDTO action) {
        this.action = action;
    }
    
    @XmlElement
    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Permission other = (Permission) obj;
        if (this.principal != other.principal &&
                (this.principal == null || !this.principal.equals(other.principal)))
        {
            return false;
        }

        if (this.action != other.action &&
                (this.action == null || !this.action.equals(other.action)))
        {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 29 * hash + (this.principal != null ? this.principal.hashCode() : 0);
        return hash;
    }

    public int compareTo(Object o) {
        if (!(o instanceof Permission)) {
            return 0;
        }

        Permission op = (Permission) o;

        // sort by principal first
        int pcomp = getPrincipal().compareTo(op.getPrincipal());
        if (pcomp != 0) {
            return pcomp;
        }

        // now sort by action
        return getAction().compareTo(op.getAction());
    }
}
