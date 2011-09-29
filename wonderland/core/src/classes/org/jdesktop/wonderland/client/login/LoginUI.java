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
package org.jdesktop.wonderland.client.login;

import org.jdesktop.wonderland.client.login.ServerSessionManager.NoAuthLoginControl;
import org.jdesktop.wonderland.client.login.ServerSessionManager.UserPasswordLoginControl;
import org.jdesktop.wonderland.client.login.ServerSessionManager.EitherLoginControl;

/**
 * An interface that the login system will call back to to request
 * login details from the user.
 * @author jkaplan
 */
public interface LoginUI {
    /**
     * Request that the user interface prompt the user for login credentials.
     * This version corresponds to no authorization, so prompts for
     * username and full name, but no password.  Login is granted if the
     * username is unique.
     * @param control the login control
     */
    public void requestLogin(NoAuthLoginControl control);

    /**
     * Request that the user interface prompt the user for login credentials.
     * This version corresponds to web service authorization, so prompts for
     * username and password.  Login is granted if the username and password
     * are validated by the web service.
     * @param control the login control
     */
    public void requestLogin(UserPasswordLoginControl control);

    /**
     * Request that the user interface prompt the user for login credentials.
     * This version corresponds to authentication either by no authorization
     * (e.g. a guest) or by authentication, at the choice of the client.
     * @param control the login control
     */
    public void requestLogin(EitherLoginControl control);
}
