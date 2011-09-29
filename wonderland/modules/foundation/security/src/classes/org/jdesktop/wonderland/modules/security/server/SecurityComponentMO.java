/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

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

package org.jdesktop.wonderland.modules.security.server;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.kernel.ComponentRegistry;
import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.auth.WonderlandIdentity;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.cell.ClientCapabilities;
import org.jdesktop.wonderland.common.cell.messages.CellMessage;
import org.jdesktop.wonderland.common.cell.state.CellComponentClientState;
import org.jdesktop.wonderland.common.cell.state.CellComponentServerState;
import org.jdesktop.wonderland.common.messages.MessageID;
import org.jdesktop.wonderland.common.security.Action;
import org.jdesktop.wonderland.modules.security.common.ActionDTO;
import org.jdesktop.wonderland.modules.security.common.CellPermissions;
import org.jdesktop.wonderland.modules.security.common.Permission;
import org.jdesktop.wonderland.modules.security.common.Principal;
import org.jdesktop.wonderland.modules.security.common.SecurityComponentServerState;
import org.jdesktop.wonderland.modules.security.common.messages.PermissionsChangedMessage;
import org.jdesktop.wonderland.modules.security.common.messages.PermissionsRequestMessage;
import org.jdesktop.wonderland.modules.security.common.messages.PermissionsResponseMessage;
import org.jdesktop.wonderland.modules.security.server.service.CellResourceManagerInternal;
import org.jdesktop.wonderland.modules.security.server.service.UserPrincipals;
import org.jdesktop.wonderland.server.auth.ClientIdentityManager;
import org.jdesktop.wonderland.server.cell.AbstractComponentMessageReceiver;
import org.jdesktop.wonderland.server.cell.CellComponentMO;
import org.jdesktop.wonderland.server.cell.CellMO;
import org.jdesktop.wonderland.server.cell.ChannelComponentMO;
import org.jdesktop.wonderland.server.cell.annotation.UsesCellComponentMO;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;
import org.jdesktop.wonderland.server.security.ActionMap;
import org.jdesktop.wonderland.server.security.Resource;
import org.jdesktop.wonderland.server.security.ResourceMap;
import org.jdesktop.wonderland.server.security.SecureTask;
import org.jdesktop.wonderland.server.security.SecurityManager;
import org.jdesktop.wonderland.server.spatial.UniverseManager;

/**
 * A component that stores security settings for a cell
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class SecurityComponentMO extends CellComponentMO {
    private static final Logger logger =
            Logger.getLogger(SecurityComponentMO.class.getName());

    /**
     * The permissions, stored as a sorted set for quick lookups by
     * principal.
     */
    private SortedSet<Permission> permissions;

    /** the set of all owners for this cell */
    private Set<Principal> owners;

    /** the channel component */
    @UsesCellComponentMO(ChannelComponentMO.class)
    private ManagedReference<ChannelComponentMO> channelRef;

    public SecurityComponentMO(CellMO cell) {
        super(cell);
    }

    @Override
    protected String getClientClass() {
        return "org.jdesktop.wonderland.modules.security.client.SecurityQueryComponent";
    }

    /**
     * Get the permissions this component represents
     * @return a sorted set of permissions
     */
    public SortedSet<Permission> getPermissions() {
        return permissions;
    }

    /**
     * Return true if this cell has any owners, or false if not
     * @return whether this cell is owned
     */
    public boolean isOwned() {
        return ((owners != null) && !owners.isEmpty());
    }

    /**
     * Get the set of owners of this cell
     * @return the set of owners
     */
    public Set<Principal> getOwners() {
        return owners;
    }

    @Override
    public CellComponentClientState getClientState(CellComponentClientState state,
            WonderlandClientID clientID, ClientCapabilities capabilities)
    {
        // schedule an update to send the updated permissions for this user
        getPermissions();

        return super.getClientState(state, clientID, capabilities);
    }

    @Override
    public CellComponentServerState getServerState(CellComponentServerState state) {
        if (state == null) {
            state = new SecurityComponentServerState();
        }

        SecurityComponentServerState scss = (SecurityComponentServerState) state;
        scss.setPermissions(getCellPermissions());
        return super.getServerState(state);
    }

    @Override
    public void setServerState(CellComponentServerState state) {
        super.setServerState(state);

        SecurityComponentServerState scss = (SecurityComponentServerState) state;

        // make sure this user is an owner, and therefore has permission
        // to set the server state of this component.  Note we don't
        // check the user if their ID is null (that means it is the system
        // doing the setting) or if there are no owners.
        ClientIdentityManager cim = AppContext.getManager(ClientIdentityManager.class);
        WonderlandIdentity id = cim.getClientID();
        if (id != null && owners != null && !owners.isEmpty()) {
            // make a request to set the permissions if this is an owner
            Resource ownerRsrc = new OwnerResource(cellRef.get().getCellID().toString(),
                                                   owners);
            ActionMap am = new ActionMap(ownerRsrc, new OwnerAction());
            ResourceMap rm = new ResourceMap();
            rm.put(ownerRsrc.getId(), am);

            SecurityManager sec = AppContext.getManager(SecurityManager.class);
            SecureTask sst = new SetStateTask(id.getUsername(), ownerRsrc.getId(),
                                              this, scss.getPermissions());
            sec.doSecure(rm, sst);
        } else {
            // no security check, just set the values
            setCellPermissions(scss.getPermissions());
        }
    }

    @Override
    protected void setLive(boolean live) {
        super.setLive(live);

        CellMO cell = cellRef.getForUpdate();
        ChannelComponentMO channel = channelRef.getForUpdate();

        if (live) {
            channel.addMessageReceiver(PermissionsRequestMessage.class,
                                       new MessageReceiver(cell, this));
        } else {
            channel.removeMessageReceiver(PermissionsRequestMessage.class);
        }
    }

    /**
     * Get all permissions for all users.
     * @return the permissions for all users
     */
    protected CellPermissions getCellPermissions() {
        CellPermissions out = new CellPermissions();

        // add the owners
        if (isOwned()) {
            out.getOwners().addAll(owners);
        }

        // combine all permissions for all users
        if (permissions != null) {
            out.getPermissions().addAll(permissions);
        }

        // add all the actions
        CellResourceManagerInternal crmi =
                AppContext.getManager(CellResourceManagerInternal.class);
        for (Action a : crmi.getActions(cellID)) {
            out.getAllActions().add(new ActionDTO(a));
        }

        logger.fine("[SecurityComponentMO] writing " +
                    out.getPermissions().size() + " permissions.");

        return out;
    }

    /**
     * Set the permissions from a CellPermissions object
     * @param perms the permissions to set
     */
    protected void setCellPermissions(CellPermissions perms) {
        // read the owners
        owners = perms.getOwners();

        // get all the permissions, and make sure they are sorted properly
        // by principal
        permissions = new TreeSet<Permission>();
        permissions.addAll(perms.getPermissions());

        // update the service cache
        CellResourceManagerInternal crmi =
                AppContext.getManager(CellResourceManagerInternal.class);
        crmi.updateCellResource(cellID, owners, permissions);

        // update the cell cache as well
        UniverseManager um = AppContext.getManager(UniverseManager.class);
        um.revalidateCell(cellRef.get());

        // send a message to clients notifying them of the change
        if (channelRef != null) {
            channelRef.get().sendAll(null, new PermissionsChangedMessage());
        }
    }

    /**
     * Get the permissions for the current user.  This is submitted as a request
     * to the security service that evaluates the security rules for the
     * current user and sends the result over a channel.
     *
     * @param sender the sender to send the response to
     * @param clientID the id of the client to send the response to
     * @param messageID the id of the message to respond to
     * @param cellID the id of the cell to request permissions for, or null
     * to request permissions for this cell
     *
     * @return the set of permissions for the given user id, or null if
     * no permissions can be determined for the given user.
     */
    protected void sendUserPermissions(WonderlandClientSender sender,
                                       WonderlandClientID clientID,
                                       MessageID messageID,
                                       CellID requestCellID)
    {
        if (requestCellID == null) {
            requestCellID = cellID;
        }

        logger.warning("[SecurityComponentMO] send user permissions for " +
                       requestCellID + " to " + clientID);

        // get the resource for this cell
        CellResourceManagerInternal crmi =
                AppContext.getManager(CellResourceManagerInternal.class);
        Resource rsrc = crmi.getCellResource(requestCellID);
        if (rsrc == null) {
            // no resource -- send permission for everything
            Set<ActionDTO> send = new LinkedHashSet<ActionDTO>();
            for (Action action : crmi.getActions(requestCellID)) {
                send.add(new ActionDTO(action));
            }
            sender.send(clientID, new PermissionsResponseMessage(messageID, send));
            return;
        }

        // construct a request for this user's permissions
        ResourceMap rm = new ResourceMap();
        Action[] actions = crmi.getActions(requestCellID).toArray(new Action[0]);
        ActionMap am = new ActionMap(rsrc, actions);
        rm.put(rsrc.getId(), am);

        // construct a new task to send the message
        SecurityManager sm = AppContext.getManager(SecurityManager.class);
        SecureTask sendTask = new SendPermissionsTask(rsrc.getId(), sender,
                                                      clientID, messageID);
        sm.doSecure(rm, sendTask);
    }

    /**
     * Resource to check if the current user is the cell owner
     */
    private static class OwnerResource implements Resource, Serializable {
        private String cellID;
        private Set<Principal> owners;

        public OwnerResource(String cellID, Set<Principal> owners) {
            this.cellID = cellID;
            this.owners = owners;
        }

        public String getId() {
            return OwnerResource.class.getName() + "-" + cellID;
        }

        public Result request(WonderlandIdentity identity, Action action) {
            Set<Principal> userPrincipals = UserPrincipals.getUserPrincipals(
                    identity.getUsername(), false);
            if (userPrincipals == null) {
                return Result.SCHEDULE;
            } else if (isOwner(userPrincipals)) {
                return Result.GRANT;
            } else {
                return Result.DENY;
            }
        }

        public boolean request(WonderlandIdentity identity, Action action, ComponentRegistry registry) {
            return isOwner(UserPrincipals.getUserPrincipals(
                                                 identity.getUsername(), true));
        }

        boolean isOwner(Set<Principal> userPrincipals) {
            for (Principal p : userPrincipals) {
                if (owners.contains(p)) {
                    return true;
                }

                // admins can also change security
                if (p.getType() == Principal.Type.GROUP &&
                        p.getId().equals("admin"))
                {
                    return true;
                }
            }

            return false;
        }
    }

    /**
     * Task to set the state of the component if the user is the owner
     */
    private static final class SetStateTask implements SecureTask, Serializable {
        private String username;
        private String resourceID;
        private ManagedReference<SecurityComponentMO> componentRef;
        private CellPermissions permissions;

        public SetStateTask(String username, String resourceID,
                            SecurityComponentMO sc, CellPermissions perms)
        {
            this.username = username;
            this.resourceID = resourceID;
            this.componentRef = AppContext.getDataManager().createReference(sc);
            this.permissions = perms;
        }

        public void run(ResourceMap granted) {
            ActionMap grantedActions = granted.get(resourceID);
            if (grantedActions.size() == 1) {
                componentRef.get().setCellPermissions(permissions);
            } else {
                logger.log(Level.WARNING, "Attempt to set state by non-owner " +
                           username);
            }
        }
    }

    /**
     * Task to send the permissions for a cell to a user
     */
    private static class SendPermissionsTask implements SecureTask, Serializable {
        private String resourceID;
        private WonderlandClientSender sender;
        private ManagedReference<ClientSession> sessionRef;
        private MessageID requestID;

        public SendPermissionsTask(String resourceID, WonderlandClientSender sender,
                                   WonderlandClientID clientID, MessageID requestID)
        {
            this.resourceID = resourceID;
            this.sender = sender;
            this.requestID = requestID;

            sessionRef = AppContext.getDataManager().createReference(clientID.getSession());
        }

        public void run(ResourceMap granted) {
            // find the permissions for this user
            ActionMap am = granted.get(resourceID);
            Set<ActionDTO> actions = new LinkedHashSet<ActionDTO>();
            for (Action a : am.values()) {
                actions.add(new ActionDTO(a));
            }

            // create the message
            PermissionsResponseMessage prm =
                    new PermissionsResponseMessage(requestID, actions);

            // get the client ID to send to
            WonderlandClientID clientID = new WonderlandClientID(sessionRef);
            sender.send(clientID, prm);
        }
    }

    /**
     * Message receiver to handle permission change requests
     */
    public static final class MessageReceiver extends AbstractComponentMessageReceiver {
        private ManagedReference<SecurityComponentMO> componentRef;

        public MessageReceiver(CellMO cellMO, SecurityComponentMO component) {
            super (cellMO);

            componentRef = AppContext.getDataManager().createReference(component);
        }

        @Override
        public void messageReceived(WonderlandClientSender sender,
                                    WonderlandClientID clientID,
                                    CellMessage message)
        {
            CellID requestCellID = null;
            if (message instanceof PermissionsRequestMessage) {
                requestCellID = ((PermissionsRequestMessage) message).getRequestCellID();
            }

            componentRef.get().sendUserPermissions(sender, clientID,
                                                   message.getMessageID(),
                                                   requestCellID);
        }
    }

    /**
     * Dummy action to test for owners
     */
    private static final class OwnerAction extends Action {
        public OwnerAction() {
            super ("isOwner");
        }
    }
}
