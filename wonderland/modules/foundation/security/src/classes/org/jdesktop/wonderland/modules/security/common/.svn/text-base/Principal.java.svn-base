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
import java.util.ResourceBundle;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A principal that can be added to the security component.
 * @author jkaplan
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
@XmlRootElement(name = "principal")
public class Principal implements Serializable, Comparable {

    public enum Type {

        USER, GROUP, EVERYBODY;

        @Override
        public String toString() {
            switch (this) {
                case GROUP:
                    return BUNDLE.getString("Group");
                case USER:
                    return BUNDLE.getString("User");
                case EVERYBODY:
                    return BUNDLE.getString("Everybody");
                default:
                    return "unknown";
            }
        }
    };
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/security/common/Bundle");
    private String id;
    private Type type;

    public Principal() {
        this(null, null);
    }

    public Principal(String id, Type type) {
        this.id = id;
        this.type = type;
    }

    @XmlElement
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlElement
    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Principal other = (Principal) obj;
        if ((id == null) ? (other.id != null) : !id.equals(other.id)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + (id != null ? id.hashCode() : 0);
        hash = 73 * hash + (type != null ? type.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "Principal{" + getType() + ": " + getId() + "}";
    }

    public int compareTo(Object o) {
        if (!(o instanceof Principal)) {
            return 0;
        }

        Principal op = (Principal) o;

        // first compare types
        if (getType() != op.getType()) {
            return getType().compareTo(op.getType());
        } else {
            // if the types are the same, compare ids
            return getId().compareTo(op.getId());
        }
    }
}
