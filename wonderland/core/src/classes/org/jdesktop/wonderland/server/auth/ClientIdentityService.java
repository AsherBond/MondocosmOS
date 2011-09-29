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

import com.sun.sgs.auth.Identity;
import com.sun.sgs.service.Service;
import com.sun.sgs.service.TransactionProxy;
import com.sun.sgs.kernel.ComponentRegistry;
import java.util.Properties;
import org.jdesktop.wonderland.common.auth.WonderlandIdentity;

/**
 * The ClientIdentityService class provides access to a persistent
 * ClientIdentity object, which, in turn, permits us to access the 
 * WonderlandIdentity object found in a given UserGLO object.  In 
 * particular, this is necessary for identifying the user group to 
 * which a given participant belongs.  With such information, we can 
 * effectively operate a discretionary access control system.
 * 
 * Note that this code is based upon sample code by Brendan Burns
 * (brendan.d.burns on gmail); modified by Tim Wright for the purpose
 * of accessing identity data. 
 * 
 * To use, add:
 * com.sun.sgs.app.services=<other services>:ClientIdentityService
 * com.sun.sgs.app.managers=<other managers>:ClientIdentityManager
 * 
 * to your DarkStar server's .properties file.  Then access with
 * AppContext.getManager(ClientIdentityManager.class); 
 *
 * @author twright
 * @version 0.1
 */
public class ClientIdentityService implements Service {
    private TransactionProxy transactionProxy;

    public ClientIdentityService(Properties prop,
                                 ComponentRegistry registry,
                                 TransactionProxy transactionProxy)
    {
        this.transactionProxy = transactionProxy;
    }

    public WonderlandIdentity getClientID() {
        // Use the TransactionProxy to access the client's identity.  We do
        // this by snagging a copy of the CurrentOwner object which will
        // permit us to access the client's WonderlandIdentity object.
        Identity id = transactionProxy.getCurrentOwner();
        if (id instanceof WonderlandServerIdentity) {
            return ((WonderlandServerIdentity) id).getIdentity();
        } else {
            return null;
        }
    }

    public String getName() {
        return ClientIdentityService.class.getName();
    }

    public void ready() throws Exception {
    }

    public void shutdown() {
    }
}
