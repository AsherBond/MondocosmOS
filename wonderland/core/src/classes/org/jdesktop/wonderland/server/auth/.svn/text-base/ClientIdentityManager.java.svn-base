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
package org.jdesktop.wonderland.server.auth;

import org.jdesktop.wonderland.common.auth.WonderlandIdentity;

/**
 * The ClientIdentityManager class facilitates access (by way of
 * the ClientIdentity class) to the WonderlandIdentity object found
 * in a given UserGLO object.  In particular, this is necessary
 * for identifying the user group to which a given participant belongs.
 * With such information, we can effectively operate a discretionary
 * access control system.
 * 
 * Note that this code is based upon sample code by Brendan Burns
 * (brendan.d.burns on gmail); modified by Tim Wright for the purpose
 * of accessing identity data. 
 * 
 * To use, add:
 * com.sun.sgs.app.services=<other services>:ClientIdentityService
 * com.sun.sgs.app.managers=<other managers>:ClientIdentityManager
 * 
 * to the DarkStar server's .properties file.  Then access with
 * AppContext.getManager(ClientIdentityManager.class);
 *
 * @author twright
 * @version 0.1
 */
public class ClientIdentityManager {

    ClientIdentityService service;

    /**
     * Constructor, required by a manager object.
     **/
    public ClientIdentityManager(ClientIdentityService service) {
        this.service = service;
    }

    /**
     * Get a client's identity.
     * @return the WonderlandIdentity object belonging to the client.
     **/
    public WonderlandIdentity getClientID() {
        return service.getClientID();
    }
}
