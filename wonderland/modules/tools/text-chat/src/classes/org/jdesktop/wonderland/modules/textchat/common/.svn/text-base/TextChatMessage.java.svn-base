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
package org.jdesktop.wonderland.modules.textchat.common;

import org.jdesktop.wonderland.common.messages.Message;

/**
 * A chat message for a specific user. If the user is null or an empty string,
 * then it is meant for all users.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class TextChatMessage extends Message {

    private String textMessage = null;
    private String fromUserName = null;
    private String toUserName = null;

    /** Constructor */
    public TextChatMessage(String msg, String fromUserName, String toUserName) {
        this.textMessage = msg;
        this.fromUserName = fromUserName;
        this.toUserName = toUserName;
    }

    /**
     * Returns the name of the user from which the message came.
     *
     * @return A String user name
     */
    public String getFromUserName() {
        return fromUserName;
    }

    /**
     * Returns the name of the user to which the messages is sent. If meant
     * for everyone, this returns an empty string.
     *
     * @return A String user name
     */
    public String getToUserName() {
        return (toUserName != null) ? toUserName : "";
    }

    /**
     * Returns the text of the text chat message.
     *
     * @return A String text chat message
     */
    public String getTextMessage() {
        return textMessage;
    }
}