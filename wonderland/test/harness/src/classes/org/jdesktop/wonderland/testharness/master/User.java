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
package org.jdesktop.wonderland.testharness.master;

import com.jme.math.Vector3f;
import java.util.logging.Logger;
import org.jdesktop.wonderland.testharness.common.ClientLogoutRequest;
import org.jdesktop.wonderland.testharness.common.ClientReply;
import org.jdesktop.wonderland.testharness.common.TestReply;
import org.jdesktop.wonderland.testharness.common.UserSimRequest;
import org.jdesktop.wonderland.testharness.manager.common.SimpleTestDirectorMessage;
import org.jdesktop.wonderland.testharness.manager.common.SimpleTestDirectorMessage.UserActionType;

/**
 *
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class User {
    private static final Logger logger = Logger.getLogger(User.class.getName());

    private String username;
    private UserContext context;
    private UserActionType currentAction = UserActionType.WALK;
    private float speed = 1f;

    public User(String username, UserContext context) {
        this.username = username;
        this.context = context;
    }

    public String getUsername() {
        return username;
    }

    public UserContext getContext() {
        return context;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    /**
     * If locations==null, stop walking
     * @param locations
     */
    public void doWalk(Vector3f[] locations) {
        context.getConnection().send(UserSimRequest.newWalkLoopRequest(username, locations, getSpeed(), -1));
        if (locations == null) {
            currentAction = UserActionType.IDLE;
        } else {
            currentAction = UserActionType.WALK;
        }
    }

    public void disconnect() {
        context.getConnection().send(new ClientLogoutRequest(username));
        context.cleanup();
    }

    public void processReply(TestReply reply) {
        System.err.println("User got reply " + reply);
        if (reply instanceof ClientReply) {
            ClientReply wtr = (ClientReply) reply;
            switch (wtr.getReplyType()) {
                case QUIT:
                    context.sendUIMessage(SimpleTestDirectorMessage.newClientQuitMessage(username));
                    currentAction = UserActionType.H_QUIT;
                    break;
                case ERROR:
                    currentAction = UserActionType.H_ERROR;
                    break;
            }
        } else {
            logger.warning("Unknown reply message " + reply.getClass().getName());
        }
    }

    public UserActionType getCurrentAction() {
        return currentAction;
    }
}
