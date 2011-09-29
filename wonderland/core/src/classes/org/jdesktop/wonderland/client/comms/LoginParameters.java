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
package org.jdesktop.wonderland.client.comms;

import org.jdesktop.wonderland.common.ExperimentalAPI;

/**
 * Data required to log user into a server.
 * 
 * @author paulby
 */
@ExperimentalAPI
public class LoginParameters {

    private String userName;
    private char[] password;

    public LoginParameters(String userName, char[] password) {
        this.userName = userName;
        this.password = password;
    }
    
    /**
     * return the userName
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Return the users password
     * @return the password, as a character array
     */
    public char[] getPassword() {
        return password;
    }

    void setPassword(char[] password) {
        this.password = password;
    }
}
