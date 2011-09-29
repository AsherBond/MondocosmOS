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
package org.jdesktop.wonderland.modules.xremwin.server;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.kernel.ComponentRegistry;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import javax.crypto.SecretKey;
import org.jdesktop.wonderland.common.auth.WonderlandIdentity;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.ErrorMessage;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.common.messages.OKMessage;
import org.jdesktop.wonderland.common.messages.ResponseMessage;
import org.jdesktop.wonderland.common.security.Action;
import org.jdesktop.wonderland.modules.security.server.service.GroupMemberResource;
import org.jdesktop.wonderland.modules.xremwin.common.XrwSecurityConnectionType;
import org.jdesktop.wonderland.modules.xremwin.common.message.SecretRequestMessage;
import org.jdesktop.wonderland.modules.xremwin.common.message.SecretResponseMessage;
import org.jdesktop.wonderland.modules.xremwin.common.message.TakeControlRequestMessage;
import org.jdesktop.wonderland.modules.xremwin.common.message.XrwSecurityMessage;
import org.jdesktop.wonderland.server.UserMO;
import org.jdesktop.wonderland.server.UserManager;
import org.jdesktop.wonderland.server.cell.CellResourceManager;
import org.jdesktop.wonderland.server.comms.SecureClientConnectionHandler;
import org.jdesktop.wonderland.server.comms.SessionMapManager;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;
import org.jdesktop.wonderland.server.comms.annotation.ClientHandler;
import org.jdesktop.wonderland.server.security.Resource;

/**
 * A connection handler that implements the server-side of the
 * XrwSecurityConnection.  This handler accepts requests to verify security
 * for Xapps on behalf of various clients.
 *
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
@ClientHandler
public class XrwSecurityConnectionHandler
        implements SecureClientConnectionHandler, Serializable
{
    /** A logger for output */
    private static final Logger logger =
            Logger.getLogger(XrwSecurityConnectionHandler.class.getName());

    /**
     * Return the connection type used by this connection (in this case, the
     * XrwSecurityConnectionType)
     * @return ColorChangeConnectionType.TYPE
     */
    public ConnectionType getConnectionType() {
        return XrwSecurityConnectionType.TYPE;
    }

    /**
     * @{inheritDoc}
     */
    public void registered(WonderlandClientSender sender) {
    }

    /**
     * @{inheritDoc}
     */
    public Resource checkConnect(WonderlandClientID clientID,
                                 Properties properties)
    {
        // only administrators can connect
        return new GroupMemberResource("admin");
    }

    /**
     * @{inheritDoc}
     */
    public void clientConnected(WonderlandClientSender sender,
                                WonderlandClientID clientID,
                                Properties properties)
    {
    }

    /**
     * @{inheritDoc}
     */
    public void connectionRejected(WonderlandClientID clientID) {
    }

    /**
     * @{inheritDoc}
     */
    public Resource checkMessage(WonderlandClientID clientID, Message message) {
        if (message instanceof XrwSecurityMessage) {
            return getXrwSecurityResource((XrwSecurityMessage) message);
        }
        
        return null;
    }
    
    /**
     * Get the resource for security checks for an Xrw security message.  The
     * resource is the normal cell resource used to verify secruity on this
     * cell.  That resource is wrapped in a resource that changes the ID
     * of the security request to the ID that is passed in in the message.
     * If the cell resource is null, this method will return null as well
     * (indicating no security check).  If the user id cannot be found,
     * the returned resource will always deny access.
     * @param message the message to check
     * @return the wrapped resource for checking cell
     */
    protected Resource getXrwSecurityResource(XrwSecurityMessage message) {

        // check that the client has view permission on the requested cell.
        // We do this by returning the cell resource for the target
        // cell with a wrapper that makes the request based on a new
        // identity.

        // first find the client's identity.  If this returns null, force an
        // error to the client.
        WonderlandIdentity id = getIdentity(message.getClientID());
        if (id == null) {
            logger.warning("ID " + message.getClientID() + " not found.");
            return new ErrorResource();
        }

        // now find the resource to check for permissions on this cell
        CellResourceManager crm = AppContext.getManager(CellResourceManager.class);
        Resource cellResource = crm.getCellResource(message.getCellID());
        if (cellResource == null) {
            logger.warning("No resource for cell " + message.getCellID());
            
            // there is no security on this cell, so allow anyone to
            // connect
            return null;
        }

        // now return a wrapper that changes the ID
        return new IdentityResourceWrapper(id, cellResource);
    }

    /**
     * Get the Wonderland identity based on the given client id.  This
     * requires mapping first to a WonderlandClientID and then to a
     * UserMO
     * @param clientID the client ID as a BigInteger
     * @return the identity associated with that clientID, or null if the
     * client can't be found
     */
    protected WonderlandIdentity getIdentity(BigInteger clientID) {
        // map the provided value to a client ID to check
        SessionMapManager smm = AppContext.getManager(SessionMapManager.class);
        WonderlandClientID checkID = smm.getClientID(clientID);
        if (checkID == null) {
            logger.warning("Unable to find client ID for " + clientID);
            return null;
        }

        // now get a user
        UserMO user = UserManager.getUserManager().getUser(checkID);
        if (user == null) {
            logger.warning("Unable to find user for " + checkID.getID());
            return null;
        }

        return user.getIdentity();
    }

    /**
     * Handle requests from the client of this connection.  Requests will
     * be differentiated by message type.
     */
    public void messageReceived(WonderlandClientSender sender,
                                WonderlandClientID clientID,
                                Message message)
    {
        ResponseMessage response;

        if (message instanceof SecretRequestMessage) {
            response = handleSecretRequest(sender, clientID,
                                           (SecretRequestMessage) message);
        } else if (message instanceof TakeControlRequestMessage) {
            response = handleTakeControl(sender, clientID, message);
        } else {
            logger.warning("Unrecognized message type: " + message.getClass() +
                           " from " + clientID);
            response = new ErrorMessage(message.getMessageID(),
                                        "Unrecognized message type: " +
                                        message.getClass());
        }

        // send a response back to the requester
        if (response != null) {
            sender.send(clientID, response);
        }
    }

    protected ResponseMessage handleSecretRequest(WonderlandClientSender sender,
                                                  WonderlandClientID clientID,
                                                  SecretRequestMessage message)
    {
        // if we got here, the client has permission to send the secret.
        SessionMapManager smm = AppContext.getManager(SessionMapManager.class);
        WonderlandClientID checkID = smm.getClientID(message.getClientID());
        
        // send the secret to the client
        SecretKey secret = XrwSecretManager.getInstance().getSecret(checkID);
        return new SecretResponseMessage(message.getMessageID(), secret);
    }

    public ResponseMessage handleTakeControl(WonderlandClientSender sender,
                                             WonderlandClientID clientID,
                                             Message message)
    {
        // if we got here, the client has permission to take control.  Just
        // send back an OK message
        return new OKMessage(message.getMessageID());
    }

    public boolean messageRejected(WonderlandClientSender sender,
                                   WonderlandClientID clientID,
                                   Message message, Set<Action> requested,
                                   Set<Action> granted)
    {
        // the system will send an error on our behalf
        return true;
    }

    /**
     * @{inheritDoc}
     */
    public void clientDisconnected(WonderlandClientSender sender,
                                   WonderlandClientID clientID)
    {
    }
    
    /**
     * A resource wrapper that changes the identity that is requested
     */
    private static class IdentityResourceWrapper implements Resource, Serializable {
        private WonderlandIdentity id;
        private Resource wrapped;

        public IdentityResourceWrapper(WonderlandIdentity id,
                                       Resource wrapped)
        {
            this.id = id;
            this.wrapped = wrapped;
        }

        public String getId() {
            return IdentityResourceWrapper.class.getSimpleName() + "-" +
                   wrapped.getId();
        }

        public Result request(WonderlandIdentity identity, Action action) {
            return wrapped.request(id, action);
        }

        public boolean request(WonderlandIdentity identity, Action action,
                               ComponentRegistry registry)
        {
            return wrapped.request(id, action, registry);
        }
    }

    /**
     * A resource that forces an error
     */
    private static class ErrorResource implements Resource, Serializable {

        public String getId() {
            return ErrorResource.class.getName();
        }

        public Result request(WonderlandIdentity identity, Action action) {
            return Result.DENY;
        }

        public boolean request(WonderlandIdentity identity, Action action,
                               ComponentRegistry registry)
        {
            return false;
        }
    }
}
