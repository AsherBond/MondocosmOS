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

import java.util.Properties;
import java.util.Set;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.common.security.Action;
import org.jdesktop.wonderland.server.security.Resource;

/**
 * An extension of the ClientConnectionHandler that allows security checks
 * for who may or may not connect to the handler.
 * @author jkaplan
 */
@ExperimentalAPI
public interface SecureClientConnectionHandler extends ClientConnectionHandler {
    /**
     * Get the resource to use for security checks on a new incoming connection.
     * This resource will be queried with the connecting user's ID and and
     * instance of ConnectAction.  If the resource grants access to the
     * ConnectAction, the connection will be allowed to proceed.  If access is
     * denied, the client's connection will be aborted and the
     * <code>connectionRejected()</code> method will be called.
     *
     * @param clientID the ID of the session that connected
     * @param properties the properties the client is connecting with
     * @return a resource that can be used for security checks, or null
     * to skip security checks.
     */
    public Resource checkConnect(WonderlandClientID clientID,
                                 Properties properties);

    /**
     * Notification that a connection was rejected.  This will happen when the
     * provided resource denies access to the ConnectAction.  This method
     * is provided to give the handler the option of doing any necessary
     * bookkeeping when a connection is rejected.  In the case of a
     * rejection, the system automatically sends an error message back to
     * the requesting client.
     * @param clientID the ID of the client that was rejected
     */
    public void connectionRejected(WonderlandClientID clientID);


    /**
     * Get the resource to use for security checks on the given message.  This
     * resource will be queried with the sending user's ID and an instance of
     * each of the declared actions associated with the given message. Note that
     * if no actions are associated with the message, the resource will not
     * be queried.
     * 
     * If the resource grants access to all the requested actions, the message
     * will be delivered using the <code>messageReceived()</code> method.  If
     * access is denied, the <code>messageRejected()</code> method will be
     * called with the information about the message and the permissions that
     * were denied.  If the return value from <code>messageRejected</code> is
     * true, a standard error will be sent to the client.  If the return value
     * is false, the connection handler is responsible for sending an
     * appropriate error message to the client.
     *
     * @param clientID the ID of the session that sent the message
     * @param message the message that was sent
     * @return a resource that can be used for security checks, or null to
     * skip security checks.
     */
    public Resource checkMessage(WonderlandClientID clientID,
                                 Message message);

    /**
     * Notification that a message was rejected.  This method is provided so
     * that connection handlers can respond to the client, especially in the
     * case of partial rejections (a message in which only some of the
     * requested permissions were granted).
     * <p>
     * If the return value of this method is true, the system will send an
     * error response to the client.  If it is false, the system will not
     * send any response, and the handler is assumed to manage the
     * response.
     *
     * @param sender the sender that can be used to send responses to the
     * client
     * @param clientID the id of the client that sent the message
     * @param message the message that was rejected
     * @param requested the actions that were required to process the
     * message fully
     * @param granted the actions that were granted
     */
    public boolean messageRejected(WonderlandClientSender sender,
                                   WonderlandClientID clientID,
                                   Message message,
                                   Set<Action> requested,
                                   Set<Action> granted);
}
