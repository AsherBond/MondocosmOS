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
import java.io.Serializable;
import org.jdesktop.wonderland.common.auth.WonderlandIdentity;

/**
 * A Darkstar identity that wraps a WonderlandIdentity.  This Darkstar
 * server-specific identity can be used to get the general WonderlandIdentity
 * that can be shared with the Wonderland client.
 * @author jkaplan
 */
public class WonderlandServerIdentity implements Identity, Serializable {
    private WonderlandIdentity identity;
    
    public WonderlandServerIdentity(WonderlandIdentity identity) {
        this.identity = identity;
    }

    /**
     * Get the WonderlandIdentity associated with this user
     * @return the Wonderland identity
     */
    public WonderlandIdentity getIdentity() {
        return identity;
    }

    /**
     * The user's name is the same as the name of the underlying
     * WonderlandIdentity.
     * @return the unique name of this identity
     */
    public String getName() {
        return getIdentity().getUsername();
    }

    public void notifyLoggedIn() {
        // do nothing
    }

    public void notifyLoggedOut() {
        // do nothing
    }
}
