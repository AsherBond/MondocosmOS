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
package org.jdesktop.wonderland.modules.securitysession.weblib;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

/**
 *
 * @author jkaplan
 */
public class UserRecord {
    private String userId;
    private String token;
    private Attributes attribs;

    public UserRecord(String userId, String token) {
        this.userId = userId;
        this.token = token;

        attribs = new BasicAttributes();
    }

    public String getUserId() {
        return userId;
    }

    public String getToken() {
        return token;
    }

    public Attributes getAttributes() {
        return attribs;
    }

    public void setAttributes(Attributes attribs) {
        this.attribs = attribs;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final UserRecord other = (UserRecord) obj;
        if ((this.userId == null) ? (other.userId != null) :
            !this.userId.equals(other.userId))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.userId != null ? this.userId.hashCode() : 0);
        return hash;
    }
}
