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
package org.jdesktop.wonderland.modules.textchat.client;

import java.util.HashSet;
import java.util.Set;
import org.jdesktop.wonderland.client.comms.BaseConnection;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.modules.textchat.common.TextChatMessage;
import org.jdesktop.wonderland.modules.textchat.common.TextChatConnectionType;

/**
 * Client-side base connection for text chat.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class TextChatConnection extends BaseConnection {

    /**
     * @inheritDoc()
     */
    public ConnectionType getConnectionType() {
        return TextChatConnectionType.CLIENT_TYPE;
    }

    /**
     * Sends a text chat message from a user to a user. If the "to" user name
     * is null or an empty string, the message is sent to all users.
     *
     * @param message The String text message to send
     * @param from The user name the message is from
     * @param to The user name the message is to
     */
    public void sendTextMessage(String message, String from, String to) {
        super.send(new TextChatMessage(message, from, to));
    }

    /**
     * @inheritDoc()
     */
    public void handleMessage(Message message) {
        if (message instanceof TextChatMessage) {
            String text = ((TextChatMessage) message).getTextMessage();
            String from = ((TextChatMessage) message).getFromUserName();
            String to = ((TextChatMessage) message).getToUserName();
            synchronized (listeners) {
                for (TextChatListener listener : listeners) {
                    listener.textMessage(text, from, to);
                }
            }
        }
    }

    private Set<TextChatListener> listeners = new HashSet();

    /**
     * Adds a new listener for text chat messages. If the listener is already
     * present, this method does nothing.
     *
     * @param listener The listener to add
     */
    public void addTextChatListener(TextChatListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Removes a listener for text chat messages. If the listener is not
     * present, this method does nothing.
     *
     * @param listener The listener to remove
     */
    public void removeTextChatListener(TextChatListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Listener for text chat messages
     */
    public interface TextChatListener {
        /**
         * A text message has been received by the client, given the user name
         * the message is from and the user name the message is to (empty string
         * if for everyone.
         *
         * @param message The String text message
         * @param from The String user name from which the message came
         * @param to The String user name to which the message is intended
         */
        public void textMessage(String message, String from, String to);
    }
}
