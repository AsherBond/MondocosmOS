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
package org.jdesktop.wonderland.server;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.NameNotBoundException;
import com.sun.sgs.app.Task;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Queue;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.auth.WonderlandIdentity;
import org.jdesktop.wonderland.server.auth.ClientIdentityManager;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;

/**
 * Manages the entire set of users logged into the system.
 *
 * @author paulby
 */
@ExperimentalAPI
public class UserManager implements ManagedObject, Serializable {
    
    private final Map<WonderlandClientID, ManagedReference<UserMO>> clientToUser =
	    new HashMap<WonderlandClientID, ManagedReference<UserMO>>();

    private final Set<ManagedReference<UserListener>> userListeners =
            new LinkedHashSet();

    /**
     * Name used in binding this object in DataManager
     **/
    private static final String BINDING_NAME="USER_MANAGER";

    private int userLimit = Integer.MAX_VALUE;

    enum NotificationType {
        LOGIN, LOGOUT
    }

    /**
     * Creates a new instance of UserManager
     */
    private UserManager() {
    }
    
    static void initialize() {
        UserManager mgr = new UserManager();
        AppContext.getDataManager().setBinding(BINDING_NAME, mgr);
    }
    
    /**
     * Return singleton user manager
     * @return the user manager
     */
    public static UserManager getUserManager() {
        return (UserManager) AppContext.getDataManager().getBinding(UserManager.BINDING_NAME);                
    }

    /**
     * Return the user with the given userID
     *
     * @return reference to the UserGLO
     */
    public UserMO getUser(WonderlandClientID clientID) {
        ManagedReference<UserMO> userRef = 
                clientToUser.get(clientID);
        if (userRef == null) {
            return null;
        }
        
        return userRef.get();
    }
    
    /**
     * Return the UserMO object associated with the unique userName
     *
     * @return UserMO object for username, or null if no such user
     */
    public static UserMO getUserMO(String username) {
        String userObjName = "user_"+username;
        UserMO user=null;
        
        DataManager dataMgr = AppContext.getDataManager();
        try {
            user = (UserMO) dataMgr.getBinding(userObjName);
        } catch(NameNotBoundException ex) {
            user = null;
        }
        
        return user;
    }
    
    private UserMO createUserMO(String username) {
        WonderlandIdentity identity = AppContext.getManager(ClientIdentityManager.class).getClientID();
        UserMO ret = new UserMO(identity);
        AppContext.getDataManager().setBinding("user_" + identity.getUsername(), ret);
        return ret;
    }

    /**
     * Returns true if the user with the specified userName is currently logged in, false otherwise.
     */
    public boolean isLoggedIn(String userName) {
        UserMO user = getUserMO(userName);
        if (user==null) {
            return false;
        }
        
        return user.isLoggedIn();
    }
    
    /**
      * Return a Collection of all users currently logged in
     *
     * @return collection of ManagedReferences to UserGLO's
     */
    public Collection<ManagedReference<UserMO>> getAllUsers() {
        return clientToUser.values();
    }
    
    /**
     * Log the user in from the specificed session. 
     * @param session 
     */
    public void login(WonderlandClientID clientID) {
        DataManager dm = AppContext.getDataManager();
        dm.markForUpdate(this);

        // issue 963: session could be null if client is in the process of
        // logging out
        if (clientID.getSession() == null) {
            return;
        }
        String name = clientID.getSession().getName();

        // find the user object from the database, create it if necessary
        UserMO user = getUserMO(name);
        if (user==null) {
            user = createUserMO(name);
        }
        
        // user is now logged in
        user.login(clientID);
        
        // add this session to our map
        clientToUser.put(clientID, user.getReference());

        // notify listeners for just this user
        for (ManagedReference<UserListener> listenerRef : user.getUserListeners()) {
            AppContext.getTaskManager().scheduleTask(
                    new UserListenerNotifier(listenerRef,
                                             dm.createReference(user),
                                             null,
                                             clientID,
                                             NotificationType.LOGIN));
        }

        // notify listeners for all users
        for (ManagedReference<UserListener> listenerRef : userListeners) {
            AppContext.getTaskManager().scheduleTask(
                    new UserListenerNotifier(listenerRef,
                                             dm.createReference(user),
                                             null,
                                             clientID,
                                             NotificationType.LOGIN));
        }
    }

    /**
     * Start the logout process for the given session.  This will create
     * a queue of tasks associated with the given session, that are
     * executed sequentially.  Once this queue is empty, all mappings from
     * the given id to a UserMO will be removed.
     * @param clientID the id of the client to begin the logout process for
     * @return the queue of tasks to perform before logout.
     * @throws IllegalArgumentException if the given clientID is not
     * currently logged in.
     */
    public Queue<Task> startLogout(WonderlandClientID clientID) {
        UserMO user = getUser(clientID);
        if (user == null) {
            throw new IllegalArgumentException("Client " + clientID +
                                               " not logged in.");
        }

        return user.startLogout(clientID);
    }

    /**
     * Finish the logout for the current session. This method will first notify
     * any user listeners of the fact that the user is logging out.  It will
     * then run all logout tasks for the given user.  When the logout tasks have
     * all completed, all mappings between the client id and the UserMO will
     * be removed.
     * @param clientID the id of the client to end the logout process for
     * @throws IllegalArgumentException if no user is associated with the given
     * client id.
     */
    public void finishLogout(WonderlandClientID clientID) {
        DataManager dm = AppContext.getDataManager();

        // make sure there is a user
        UserMO user = getUser(clientID);
        if (user == null) {
            throw new IllegalArgumentException("Client " + clientID +
                                               " not logged in.");
        }

        // get the task queue to use
        Queue<Task> tasks = user.getLogoutTasks(clientID);
        
        // add listener notification tasks to the queue, starting with
        // per-user listeners
        for (ManagedReference<UserListener> listener : user.getUserListeners()) {
            tasks.add(new UserListenerNotifier(listener, 
                                               dm.createReference(user), 
                                               dm.createReference(tasks), 
                                               clientID, 
                                               NotificationType.LOGOUT));
        }
        
        // next add global listeners
        for (ManagedReference<UserListener> listener : userListeners) {
            tasks.add(new UserListenerNotifier(listener, 
                                               dm.createReference(user), 
                                               dm.createReference(tasks), 
                                               clientID, 
                                               NotificationType.LOGOUT));
        }

        // now that we have collected all logout tasks, start running them
        AppContext.getTaskManager().scheduleTask(
                new LogoutTask(clientID, dm.createReference(tasks)));
    }

    /**
     * Cleanup when all logouts for a given id are finished
     * @param id the id to cleanup
     */
    private void cleanupClient(WonderlandClientID clientID) {
        AppContext.getDataManager().markForUpdate(this);

        // remove the mapping for this user
        ManagedReference<UserMO> userRef = clientToUser.remove(clientID);
        userRef.get().finishLogout(clientID);
    }

    /**
     * Notify listeners that of a change to a user's status.  Notifications will
     * each be done in separate tasks.
     * @param listeners the set of listeners to update
     * @param clientID the ID of the client that changed
     * @param userRef a reference to the user object that changed
     * @param type the type of change
     */
    static void notifyUserListeners(Set<ManagedReference<UserListener>> listeners,
                                    WonderlandClientID clientID,
                                    ManagedReference<UserMO> userRef,
                                    ManagedReference<Queue<Task>> tasksRef,
                                    NotificationType type)
    {
        for (ManagedReference<UserListener> listener : listeners) {
            switch (type) {
                case LOGIN:
                    listener.get().userLoggedIn(clientID, userRef);
                    break;
                case LOGOUT:
                    listener.get().userLoggedOut(clientID, userRef, tasksRef);
                    break;
            }
        }
    }

    /**
     * Return a Collection of all avatars for currently logged in users
     *
     * @return Collection of ManagedReferences to AvatarCellGLO's
     */
//    public Collection<ManagedReference> getAllAvatars() {
//        return uidToAvatarRef.values();
//    }
    
    /**
     *  Return total number of users currently logged in
     * 
     *  @return total number of users currently logged in
     **/
    public int getUserCount() {
        return clientToUser.size();
    }
    
    /**
     *  Get the maximum number of users allowed on the server
     * 
     *  @return the maximum number of concurrent users this world will
     * allow to log in.
     */
    public int getUserLimit() {
        return userLimit;
    }

    /**
     *  Set the maximum number of users allowed on the server
     * 
     * @param userLimit the maximum number of concurrent users this world will
     * allow to log in.
     */
    public void setUserLimit(int userLimit) {
        this.userLimit = userLimit;
    }

    /**
     * Add a listener to the set of listeners which are notified when any user
     * logs out.  In most cases, you should use add a listener to a specific
     * UserMO instead.
     * @param listener the listener to remove
     */
    public void addUserListener(UserListener listener) {
        AppContext.getDataManager().markForUpdate(this);
        userListeners.add(AppContext.getDataManager().createReference(listener));
    }

    /**
     * Removed a user listener that is listening to all users.
     * @param listener the listener to remove
     */
    public void removeUserListener(UserListener listener) {
        AppContext.getDataManager().markForUpdate(this);
        userListeners.remove(AppContext.getDataManager().createReference(listener));
    }

    /**
     * A task to notify listeners when a user logs in or out
     */
    private static class UserListenerNotifier implements Task, Serializable {
        private ManagedReference<UserListener> listenerRef;
        private ManagedReference<UserMO> userRef;
        private ManagedReference<Queue<Task>> tasksRef;
        private WonderlandClientID clientID;
        private NotificationType notificationType;


        public UserListenerNotifier(ManagedReference<UserListener> listenerRef,
                                    ManagedReference<UserMO> userRef,
                                    ManagedReference<Queue<Task>> tasksRef,
                                    WonderlandClientID clientID,
                                    NotificationType notificationType)
        {
            this.listenerRef = listenerRef;
            this.userRef = userRef;
            this.tasksRef = tasksRef;
            this.clientID = clientID;
            this.notificationType = notificationType;
        }

        public void run() throws Exception {
            switch (notificationType) {
                case LOGIN:
                    listenerRef.get().userLoggedIn(clientID, userRef);
                    break;
                case LOGOUT:
                    listenerRef.get().userLoggedOut(clientID, userRef, tasksRef);
                    break;
            }
        }
    }

    /**
     * A task to run the next logout task in the queue, or call
     * cleanupClient when all tasks in the queue are finished.
     */
    private static class LogoutTask implements Task, Serializable {
        private WonderlandClientID clientID;
        private ManagedReference<Queue<Task>> tasksRef;

        public LogoutTask(WonderlandClientID clientID,
                          ManagedReference<Queue<Task>> tasksRef)
        {
            this.clientID = clientID;
            this.tasksRef = tasksRef;
        }

        public void run() throws Exception {
            // get the first task
            Task task = tasksRef.get().remove();

            // run it
            task.run();

            // schedule the next task
            if (tasksRef.get().isEmpty()) {
                // we are all done -- call cleanup logout
                UserManager.getUserManager().cleanupClient(clientID);
            } else {
                // there are more tasks -- schedule the next one
                AppContext.getTaskManager().scheduleTask(
                        new LogoutTask(clientID, tasksRef));
            }

        }
    }
}
