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
package org.jdesktop.wonderland.common.auth;

import java.io.Serializable;

/**
 * An identity for the Wonderland system
 * @author jkaplan
 */
public class WonderlandIdentity implements Serializable {

    private String username;
    private String fullname;
    private String email;
    
    public WonderlandIdentity(String username, String fullname, String email) {
        this.username = username;
        this.fullname = fullname;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullname;
    }
    
    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "WonderlandIdentity: username=" + getUsername() +
               " fullname=" + getFullName() + " email=" + getEmail();
    }

    /**
     * WonderlandIdentities are equal if they have the same username
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WonderlandIdentity other = (WonderlandIdentity) obj;
        if ((this.username == null) ? (other.username != null) : !this.username.equals(other.username)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + (this.username != null ? this.username.hashCode() : 0);
        return hash;
    }
}
