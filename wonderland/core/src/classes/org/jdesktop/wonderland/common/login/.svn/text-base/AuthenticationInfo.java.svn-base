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
package org.jdesktop.wonderland.common.login;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Details on how to authenticate to a server
 * @author jkaplan
 */
@XmlRootElement
public class AuthenticationInfo implements Cloneable {
    public enum Type { NONE, WEB_SERVICE, EITHER };

    private Type type;
    private String authURL;

    /**
     * Default constructor
     */
    public AuthenticationInfo() {
    }

    /**
     * Create a new Authentication info with the given type and
     * URL
     * @param type the type of authentication
     * @param authURL the URL to authenticate with
     */
    public AuthenticationInfo(Type type, String authURL) {
        this.type = type;
        this.authURL = authURL;
    }

    /**
     * Get the authentication URL.  The interpretation of this URL
     * depends on the login type, but typically it will be the base
     * URL to authenticate with
     * @return the authentication URL
     */
    @XmlElement
    public String getAuthURL() {
        return authURL;
    }

    /**
     * Set the authentication URL
     * @param authURL the new authentication URL
     */
    public void setAuthURL(String authURL) {
        this.authURL = authURL;
    }

    /**
     * Get the type of login.
     * @return the login type
     */
    @XmlElement
    public Type getType() {
        return type;
    }

    /**
     * Set the type of login
     * @param type the login type
     */
    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public AuthenticationInfo clone() {
        return new AuthenticationInfo(getType(), getAuthURL());
    }
}
