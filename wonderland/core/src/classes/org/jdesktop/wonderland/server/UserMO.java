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
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedObjectRemoval;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.Task;
import com.sun.sgs.app.util.ScalableDeque;
import com.sun.sgs.app.util.ScalableHashMap;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.auth.WonderlandIdentity;
import org.jdesktop.wonderland.server.auth.ClientIdentityManager;
import org.jdesktop.wonderland.server.cell.view.AvatarCellMO;
import org.jdesktop.wonderland.server.comms.WonderlandClientID;

/**
 * This class represensents a real world user. A user can be logged into
 * the system from multiple concurrent clients with different protocols
 * 
 * For example a user may be logged in from a 3D client and a cell phone
 * 
 * @author paulby
 */
@ExperimentalAPI
public class UserMO implements ManagedObject, Serializable, ManagedObjectRemoval {

    private WonderlandIdentity identity;
    private ArrayList<String> groups = null;
    
    private Set<WonderlandClientID> activeClients = null;
    private Map<String, Serializable> extendedData = null;
    private Map<WonderlandClientID, Map<String, ManagedReference<AvatarCellMO>>> avatars = new HashMap();

    private final ManagedReference<Map<WonderlandClientID, Queue<Task>>> logoutTasksRef;


    private final Set<ManagedReference<UserListener>> userListeners =
            new LinkedHashSet<ManagedReference<UserListener>>();

    protected static Logger logger = Logger.getLogger(UserMO.class.getName());

    /**
     * Create a new User managed object with a unique username
     * 
     * @param identity
     */
    UserMO(WonderlandIdentity identity) {
        this.identity = identity;

        DataManager dm = AppContext.getDataManager();
        logoutTasksRef = dm.createReference((Map<WonderlandClientID, Queue<Task>>)
                new ScalableHashMap<WonderlandClientID, Queue<Task>>());
    }
    
    /**
     * Get unique identity
     * 
     * @return
     */
    public WonderlandIdentity getIdentity() {
        return identity;
    }

    public String getUsername() {
	return identity.getUsername();
    }

    /**
     * Put a named object in the extended data Map for this User
     * 
     * This name/object pairing allows developers to add data to the user class
     * without needing to modify the userMO class.
     * 
     * @param name
     * @param object
     */
    public void putExtendedData(String name, Serializable object) {
        if (extendedData==null) {
            extendedData = new HashMap();
        }

        AppContext.getDataManager().markForUpdate(this);
        extendedData.put(name, object);
    }
    
    /**
     * Return the object associated with the name.
     * @param name
     * @return Object, or null if their is no object associated with the given name
     */
    public Object getExtendedData(String name) {
        if (extendedData==null)
            return null;
        return extendedData.get(name);
    }
    
    /**
     * Return the specified avatar for this User and session, or null if that avatar
     * does not exist
     * 
     * @param avatarName
     * @return
     */
    public AvatarCellMO getAvatar(WonderlandClientID clientID, String avatarName) {
        Map<String, ManagedReference<AvatarCellMO>> sessionAvatars = avatars.get(clientID);
            
        if (sessionAvatars==null)
            return null;
        
        ManagedReference<AvatarCellMO> avatarRef = sessionAvatars.get(avatarName);
        if (avatarRef == null) {
            return null;
        }
        
        return avatarRef.get();
    }

    /**
     * Return all avatars for this User
     * @return all the user's avatars
     */
    public Collection<ManagedReference<AvatarCellMO>> getAllAvatars() {
        Collection<ManagedReference<AvatarCellMO>> out =
                new LinkedHashSet<ManagedReference<AvatarCellMO>>();

        for (Map<String, ManagedReference<AvatarCellMO>> sas : avatars.values()) {
            out.addAll(sas.values());
        }
        
        return out;
    }
    
    /**
     * Put the avatarRef and the name in the set of avatars for this user. Each
     * ClientSession can have a set of avatars.
     * 
     * @param avatarName
     * @param avatar
     */
    public void putAvatar(WonderlandClientID clientID, String avatarName, AvatarCellMO avatar) {
        DataManager dm = AppContext.getDataManager();
        Map<String, ManagedReference<AvatarCellMO>> clientAvatars = avatars.get(clientID);
        if (clientAvatars==null) {
            clientAvatars = new HashMap();

            dm.markForUpdate(this);
            avatars.put(clientID, clientAvatars);
        }
        clientAvatars.put(avatarName, dm.createReference(avatar));
    }
    
    /**
     * User has logged in from specified session with specificed protocol listener
     * @param session
     * @param protocol
     */
    void login(WonderlandClientID clientID) {
        DataManager dm = AppContext.getDataManager();
        dm.markForUpdate(this);

        if (activeClients==null) {
            activeClients = new HashSet<WonderlandClientID>();
        }
        
	String username = AppContext.getManager(ClientIdentityManager.class).getClientID().getUsername();
        logger.info("User Login " + username);
        activeClients.add(clientID);
    }

    /**
     * The logout process has started for the given client. Create a queue
     * of tasks to execute before logout is complete.  When the queue is
     * empty, the loggedOut() method will be called to clean up the mapping
     * for the given client id.
     * @param clientID the id to start the logout process for.
     * @return the queue of logout tasks for the given client.
     */
    Queue<Task> startLogout(WonderlandClientID clientID) {
        if (!activeClients.contains(clientID)) {
            throw new IllegalStateException("Client " + clientID +
                                            " is not active.");
        }

        if (logoutTasksRef.get().containsKey(clientID)) {
            throw new IllegalStateException("Client " + clientID +
                    " has already started logout.");
        }

        // create the task queue for this id
        Queue<Task> tasks = new ScalableDeque<Task>();
        logoutTasksRef.get().put(clientID, tasks);
        return tasks;
    }

    /**
     * Get the logout tasks for a given clientID.  The client must be
     * in the process of logging out (i.e. between when startLogout() is
     * called and loggedOut() is called).  If the client is not in the
     * process of logging out, this method will return null.
     * @param clientID the client ID to get logout tasks for
     * @return the tasks for the given user, or null if the user is not
     * in the process of logging out
     */
    public Queue<Task> getLogoutTasks(WonderlandClientID clientID) {
        return logoutTasksRef.get().get(clientID);
    }


    /**
     * User has logged out from specified session.
     * @param clientID the id of the client session that completed logout.
     */
    void finishLogout(WonderlandClientID clientID) {
        AppContext.getDataManager().markForUpdate(this);
        activeClients.remove(clientID);

        // clean up the tasks queue for this client
        Queue<Task> tasks = logoutTasksRef.get().remove(clientID);
        AppContext.getDataManager().removeObject(tasks);
    }

    /**
     * Return true if this user is logged in, false otherwise
     * @return
     */
    boolean isLoggedIn() {
        return activeClients.size() > 0;
    }
    
    /**
     * Add a listener to be notified when clients of this user log in or out.
     * @param listener the listener to notify on log in or log out.  Listener
     * must implement ManagedObject.
     */
    public void addUserListener(UserListener listener) {
        DataManager dm = AppContext.getDataManager();
        dm.markForUpdate(this);
        userListeners.add(dm.createReference(listener));
    }

    /**
     * Remove a user listener from this user
     * @param listener the listener to remove
     */
    public void removeUserListener(UserListener listener) {
        DataManager dm = AppContext.getDataManager();
        dm.markForUpdate(this);
        userListeners.remove(dm.createReference(listener));
    }

    /**
     * Get user listeners
     * @return the user listeners
     */
    Set<ManagedReference<UserListener>> getUserListeners() {
        return userListeners;
    }

    /**
     * Convenience method that returns the ManagedReference to this ManagedObject
     * @return
     */
    public ManagedReference getReference() {
        return AppContext.getDataManager().createReference(this);
    }

    /**
     * Clean up managed objects we create.
     */
    public void removingObject() {
        AppContext.getDataManager().removeObject(logoutTasksRef.get());
    }
}
