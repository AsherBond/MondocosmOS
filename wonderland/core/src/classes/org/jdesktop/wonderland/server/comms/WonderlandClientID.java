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
package org.jdesktop.wonderland.server.comms;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.ObjectNotFoundException;
import java.io.Serializable;
import java.math.BigInteger;

/**
 * Identifies a WonderlandClient.  This is a wrapper around the
 * Darkstar clientSession for this client.
 * @author jkaplan
 */
public class WonderlandClientID implements Serializable {
    /** a reference to the ClientSession */
    private ManagedReference<ClientSession> sessionRef;

    /**
     * For subclass constructor
     */
    protected WonderlandClientID() {

    }

    /**
     * Create a new clientID from the given ClientSession
     * @param session the session to wrap
     */
    public WonderlandClientID(ClientSession session) {
        this (AppContext.getDataManager().createReference(session));
    }

    /**
     * Create a new clientID from the given reference to a ClientSession
     * @param sessionRef a reference from a clientSession
     */
    public WonderlandClientID(ManagedReference<ClientSession> sessionRef) {
        this.sessionRef = sessionRef;
    }

    /**
     * Get the unique ID of this client.  This ID is the same as the one
     * returned by the <code>WonderlandSession.getID()</code> method on
     * the client.
     * @return a unique ID for this client
     */
    public BigInteger getID() {
        return sessionRef.getId();
    }

    /**
     * Get the ClientSession this for this client.  Note that the
     * <code>ClientSession.send()</code> method should not be used directly.
     * Instead, use
     * <code>WonderlandClientSender.send(WonderlandClientID, message)</code>.
     * @return the session associated with this client, or null if the session
     * has expired because the user logged out.
     */
    public ClientSession getSession() {
        try {
            return sessionRef.get();
        } catch (ObjectNotFoundException onfe) {
            // issue 963: return null if the session no longer exists
            return null;
        }
    }

    /**
     * Compare session IDs based on the sessionRef object.
     * @param obj the other object
     * @return true if <code>obj</code> is a WonderlandClientID with the same
     * sessionRef
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WonderlandClientID other = (WonderlandClientID) obj;
        if (this.sessionRef != other.sessionRef &&
                (this.sessionRef == null ||
                !this.sessionRef.equals(other.sessionRef)))
        {
            return false;
        }
        return true;
    }

    /**
     * Generate a hash code based on the sessionRef
     * @return a hashcode based on the sessionRef
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash +
                (this.sessionRef != null ? this.sessionRef.hashCode() : 0);
        return hash;
    }
}
