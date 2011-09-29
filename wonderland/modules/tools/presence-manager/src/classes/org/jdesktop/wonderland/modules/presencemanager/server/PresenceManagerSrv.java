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
package org.jdesktop.wonderland.modules.presencemanager.server;

import java.math.BigInteger;
import java.util.Collection;
import org.jdesktop.wonderland.common.auth.WonderlandIdentity;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

/**
 * Server-side presence manager interface
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public interface PresenceManagerSrv {
    /**
     * Add a new presence info object for the given client and cell. This
     * is equivalent to calling
     * <code>addPresenceInfo(null, null, clientID, cellID)</code>.
     * @param clientID the client to add presence info for.
     * @param cellID the cell to associate the presence info with.
     * @return the newly created presence info object.
     */
    public PresenceInfo addPresenceInfo(BigInteger clientID, CellID cellID);

    /**
     * Create presence info for the given user.
     * @param sender the WonderlandClientSender to send messages on, or null
     * to use the default presence sender.
     * @param wlID the identity of the user to add presence info for, or null
     * to use the identity of the current user.
     * @param clientID the unique client to add presence info for.  This can be
     * the unique ID of any Darkstar managed object, although typically the ID
     * of the client session (WonderlandClientID.getID()) is used. If no
     * user session is appropriate, a cell id can be used instead.
     * @param cellID the cell to associate the presence info with. This is
     * typically the user's avatar cell.
     * @return the newly created presence info object.
     */
    public PresenceInfo addPresenceInfo(WonderlandClientSender sender,
            WonderlandIdentity wlID, BigInteger clientID,
            CellID cellID);

    /**
     * Enable player in range notification for the presence info with
     * the given id.
     * @param clientID the id of the player to add notifications for
     * @param wlClientID the wonderland client ID to send messages to
     */
    public void enableInRangeNotification(BigInteger clientID,
                                          WonderlandClientID wlClientID);

    /**
     * Set the alias associated with a particular client
     * @param clientID the clientID to change the alias for
     * @param alias the alias for the given user
     */
    public void setUsernameAlias(BigInteger clientID, String alias);

    /**
     * Set whether or not the given user is in the cone of silence.
     * @param clientID the clientID to change the COS status for
     * @param inCOS true if the given user is in the cone of silence, or
     * false otherwise
     */
    public void setInConeOfSilence(BigInteger clientID, boolean inCOS);

    /**
     * Set speaking for a user in a particular private audio group
     * @param targetID the client ID to send the message to
     * @param speakingID the client ID that is speaking
     * @param speaking true if this client is speaking, or false if not
     */
    public void setSpeakingInGroup(WonderlandClientID targetID,
                                   BigInteger speakingID,
                                   boolean speaking);

    /**
     * Get all registered PresenceInfo objects.
     * @return the collection of registered PresenceInfo objects.
     */
    public Collection<PresenceInfo> getAllPresenceInfo();

    /**
     * Get the presence info associated with the given client ID
     * @param clientID the clientID that the presence info object was added
     * with
     * @return the presenceInfo associated with the given ID, or null if
     * no presence info is associated with the ID.
     */
    public PresenceInfo getPresenceInfo(BigInteger clientID);

    /**
     * Remove presence info for the given ID.
     * @param clientID the client ID to remove information for
     * @return the presence info object that was removed, or null if no
     * presence info is removed.
     */
    public PresenceInfo removePresenceInfo(BigInteger clientID);
}
