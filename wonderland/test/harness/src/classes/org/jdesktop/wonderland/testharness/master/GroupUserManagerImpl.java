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
import java.util.ArrayList;
import java.util.Properties;
import org.jdesktop.wonderland.testharness.manager.common.SimpleTestDirectorMessage.UserActionType;

/**
 *
 * @author Jonathan Kaplan <kaplanj@dev.java.net>
 */
public class GroupUserManagerImpl implements UserManager {
    private static int USERS_PER_GROUP = 3;
    private static int GROUP_SPACING = 8;

    private final ArrayList<UserGroup> userGroups = new ArrayList();


    public GroupUserManagerImpl() {
    }

    public void initialize(Properties props) {
        userGroups.add(new UserGroup(new Vector3f(0, 0, 0)));
    }

    public User createUser(String username, UserContext context) {
        GroupUser out = new GroupUser(username, context);
        out.setSpeed(1f);

        // find a group for this user
        UserGroup group = findGroup();
        group.add(out);

        return out;
    }

    public void destroyUser(User user) {
        UserGroup group = findGroup((GroupUser) user);
        if (group != null) {
            group.remove((GroupUser) user);
        }
    }

    public void changeUserAction(User user, UserActionType userActionType) {
        GroupUser gu = (GroupUser) user;

        switch (userActionType) {
            case WALK:
                user.doWalk(gu.getUserGroup().getWalkPattern());
                break;
            case IDLE:
                user.doWalk(null);
        }
    }

    /**
     * Find the first group with space for a new user, creating a group
     * if necessary
     * @return the first group with space, or a new group if no groups
     * have space
     */
    private UserGroup findGroup() {
        for (UserGroup group : userGroups) {
            if (group.getUserCount() < USERS_PER_GROUP) {
                return group;
            }
        }

        UserGroup lastGroup = userGroups.get(userGroups.size() - 1);
        UserGroup out = new UserGroup(lastGroup.getCenter().add(new Vector3f(GROUP_SPACING, 0, 0)));
        userGroups.add(out);

        return out;
    }

    /**
     * Find the group containing the given user, or return null if no
     * group contains the current user.
     * @param user the user to look for
     * @return the group containing the given user
     */
    private UserGroup findGroup(GroupUser user) {
        for (UserGroup group : userGroups) {
            if (group.contains(user)) {
                return group;
            }
        }

        return null;
    }

    class UserGroup extends ArrayList<GroupUser> {

        private Vector3f center;
        private Vector3f[] walkPattern;

        public UserGroup(Vector3f center) {
            this.center = center;
            walkPattern = new Vector3f[]{
                        new Vector3f(0, 0, 0).add(center),
                        new Vector3f(4, 0, 0).add(center),
                        new Vector3f(2, 0, 4).add(center)
                    };
        }

        @Override
        public boolean add(GroupUser user) {
            if (super.add(user)) {
                user.setUserGroup(this);
                user.doWalk(walkPattern);
                return true;
            }

            return false;
        }

        public int getUserCount() {
            return size();
        }

        public Vector3f getCenter() {
            return center;
        }

        public Vector3f[] getWalkPattern() {
            return walkPattern;
        }
    }

    class GroupUser extends User {
        private UserGroup group;

        public GroupUser(String username, UserContext context) {
            super (username, context);
        }

        public UserGroup getUserGroup() {
            return group;
        }

        public void setUserGroup(UserGroup group) {
            this.group = group;
        }
    }
}
