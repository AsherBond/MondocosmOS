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
package org.jdesktop.wonderland.testharness.manager.common;

import java.util.Properties;

/**
 *
 * @author paulby
 */
public class SimpleTestDirectorMessage extends ManagerMessage {

    public enum MessageType { USER_COUNT, UI_UPDATE, REQUEST_STATUS, CHANGE_ALLOCATOR, USER_ADDED, USER_REMOVED, USER_ACTION_CHANGE_REQUEST, CLIENT_QUIT, USER_LIST };

    public enum UserActionType { WALK, IDLE, H_QUIT, H_ERROR };   // THings starting with H_ won't appear in selection box in jpanel

    private MessageType messageType;
    private int userCount;
    private int desiredUserCount;
    private String allocatorName;
    private Properties props;
    private String username;
    private UserActionType userAction;
    private String[] allUsernames;
    private UserActionType[] allUserActions;

    private SimpleTestDirectorMessage(MessageType messageType) {
        this.messageType = messageType;
    }

    private SimpleTestDirectorMessage(MessageType messageType, int userCount, int desiredUserCount) {
        this(messageType);
        this.userCount = userCount;
        this.desiredUserCount = desiredUserCount;
    }
    
    public static SimpleTestDirectorMessage newUIUpdate(int userCount, int desiredUserCount) {
        return new SimpleTestDirectorMessage(MessageType.UI_UPDATE, userCount, desiredUserCount);
    }
    
    public static SimpleTestDirectorMessage newDesiredUserCountMessage(int desiredUserCount) {
        return new SimpleTestDirectorMessage(MessageType.USER_COUNT, 0, desiredUserCount);
    }
    
    public static SimpleTestDirectorMessage newRequestStatusMessage() {
        return new SimpleTestDirectorMessage(MessageType.REQUEST_STATUS);
    }

    public static SimpleTestDirectorMessage newUserAddedMessage(String username, boolean added) {
        SimpleTestDirectorMessage msg;
        if (added)
            msg = new SimpleTestDirectorMessage(MessageType.USER_ADDED);
        else
            msg = new SimpleTestDirectorMessage(MessageType.USER_REMOVED);
        msg.username = username;
        return msg;
    }

    public static SimpleTestDirectorMessage newUserActionChangeRequestMessage(String username, UserActionType userAction) {
        SimpleTestDirectorMessage msg = new SimpleTestDirectorMessage(MessageType.USER_ACTION_CHANGE_REQUEST);
        msg.username = username;
        msg.userAction = userAction;
        return msg;
    }

    public static SimpleTestDirectorMessage newChangeAllocatorMessage(
            String allocatorName, Properties props) {
        SimpleTestDirectorMessage out =
                new SimpleTestDirectorMessage(MessageType.CHANGE_ALLOCATOR);
        out.allocatorName = allocatorName;
        out.props = props;

        return out;
    }

    public static SimpleTestDirectorMessage newClientQuitMessage(String username) {
        SimpleTestDirectorMessage msg = new SimpleTestDirectorMessage(MessageType.CLIENT_QUIT);
        msg.username  = username;
        return msg;
    }

    public static SimpleTestDirectorMessage newUserListMessage(String[] usernames, UserActionType[] currentActions) {
        SimpleTestDirectorMessage msg = new SimpleTestDirectorMessage(MessageType.USER_LIST);
        msg.allUsernames = usernames;
        msg.allUserActions = currentActions;
        return msg;
    }


    public MessageType getMessageType() {
        return messageType;
    }
    
    public int getUserCount() {
        assert(messageType!=MessageType.REQUEST_STATUS);
        
        return userCount;
    }

    public int getDesiredUserCount() {
        return desiredUserCount;
    }

    public String getAllocatorName() {
        return allocatorName;
    }

    public Properties getProperties() {
        return props;
    }

    @Override
    public String toString() {
        return "SimpleTestDirectorMessage:"+messageType;
    }

    public String getUsername() {
        return username;
    }

    public UserActionType getUserAction() {
        return userAction;
    }

    public String[] getAllUsernames() {
        assert(messageType==MessageType.USER_LIST);
        return allUsernames;
    }

    public UserActionType[] getAllUserActions() {
        assert(messageType==MessageType.USER_LIST);
        return allUserActions;
    }
}
