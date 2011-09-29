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
package org.jdesktop.wonderland.modules.textchat.server;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.modules.textchat.common.TextChatMessage;
import org.jdesktop.wonderland.modules.textchat.common.TextChatConnectionType;
import org.jdesktop.wonderland.server.WonderlandContext;
import org.jdesktop.wonderland.server.comms.ClientConnectionHandler;
import org.jdesktop.wonderland.server.comms.CommsManager;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;
import org.jdesktop.wonderland.server.comms.WonderlandClientSender;

/**
 * Handles text chat messages from the client.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class TextChatConnectionHandler implements ClientConnectionHandler, Serializable {

    private static Logger logger = Logger.getLogger(TextChatConnectionHandler.class.getName());

    /**
     * Stores the classes that have registered as listening for new chat messages.
     */
    private Set<ManagedReference> listeners = new HashSet<ManagedReference>();

//    public TextChatConnectionHandler() {
//        logger.info("DEFAULT CONSTRUCTOR called!");
//
//        listeners = new HashSet<ManagedReference>();
//    }

    public ConnectionType getConnectionType() {
        return TextChatConnectionType.CLIENT_TYPE;
    }

    public void registered(WonderlandClientSender sender) {
        // ignore
    }

    public void clientConnected(WonderlandClientSender sender,
            WonderlandClientID clientID, Properties properties) {
        // ignore
    }

    public void clientDisconnected(WonderlandClientSender sender,
            WonderlandClientID clientID) {
        // ignore
    }

    public void messageReceived(WonderlandClientSender sender,
            WonderlandClientID clientID, Message message) {

        TextChatMessage tcm = (TextChatMessage)message;

        // First, notify listeners of a new message. On the server side,
        // all listeners get all messages, even if they're sent to
        // specific people. It's up to listeners to decide what to do
        // with them.
        for(ManagedReference listenerRef : this.listeners) {
            TextChatMessageListener listener = (TextChatMessageListener)listenerRef.get();
            logger.info("Sending to listener: " + listener);
            listener.handleMessage(tcm);
        }

        // Check to see if the message is a meant for everyone by looking at
        // the "to" field. If so, then echo the message back to all clients
        // except the one that sent the message.
        String toUser = tcm.getToUserName();
        Set<WonderlandClientID> clientIDs = sender.getClients();

        if (toUser == null || toUser.equals("") == true) {
            clientIDs.remove(clientID);
            sender.send(clientIDs, message);
            return;
        }

        // Otherwise, we need to send the message to a specific client, based
        // upon the "to" field. Loop through the list of clients and find the
        // one with the matching user name
        for (WonderlandClientID id : clientIDs) {
            String name = id.getSession().getName();
            logger.warning("Looking at " + name + " for " + toUser);
            if (name.equals(toUser) == true) {
                sender.send(id, message);
                return;
            }
        }
    }

    /**
     * Convenience method for the two paramter version that sends the message from
     * a fake "Server" user.
     *
     * @param msg The body of the text chat message.
     */
    public void sendGlobalMessage(String msg) {
        this.sendGlobalMessage("Server", msg);
    }

    /**
     * Sends a global text message to all users. You can decide who the message should
     * appear to be from; it doesn't need to map to a known user.
     *
     * @param from The name the message should be displayed as being from.
     * @param msg The body of the text chat message.
     */
    public void sendGlobalMessage(String from, String msg) {
        logger.finer("Sending global message from " + from + ": " + msg);
        // Originally included for the XMPP plugin, so people chatting with the XMPP bot
        // can have their messages replicated in-world with appropriate names.
        //
        // Of course, there are some obvious dangerous with this: it's not that hard
        // to fake an xmpp name to look like someone it's not. In an otherwise
        // authenticated world, this might be a way to make it look like
        // people are saying things they're not.

        CommsManager cm = WonderlandContext.getCommsManager();
        WonderlandClientSender sender = cm.getSender(TextChatConnectionType.CLIENT_TYPE);

        // Send to all clients, because the message is originating from a non-client source.
        Set<WonderlandClientID> clientIDs = sender.getClients();

        // Construct a new message with appropriate fields.
        TextChatMessage textMessage = new TextChatMessage(msg, from, "");
        sender.send(clientIDs, textMessage);
    }

    /**
     * Adds a listener object that will be called whenever a text chat message is sent.
     * Global messages sent from sendGlobalMessage are not included in these notifications.
     *
     * @param listener The listener object.
     */
    public void addTextChatMessageListener(TextChatMessageListener listener) {
        
        this.listeners.add(AppContext.getDataManager().createReference(listener));
    }

    /**
     * Removes the listener object from the list of listeners.
     * 
     * @param listener The listener object.
     */
    public void removeTextChatMessageListener(TextChatMessageListener listener) {
        this.listeners.remove(AppContext.getDataManager().createReference(listener));
    }
}
