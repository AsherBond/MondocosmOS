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
package org.jdesktop.wonderland.modules.presencemanager.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.math.BigInteger;
import java.util.logging.Logger;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.comms.WonderlandSession.Status;
import org.jdesktop.wonderland.common.auth.WonderlandIdentity;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManagerListener.ChangeType;
import com.jme.bounding.BoundingVolume;
import com.jme.math.Vector3f;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jdesktop.wonderland.client.input.InputManager;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarNameEvent;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarNameEvent.EventType;
import org.jdesktop.wonderland.modules.presencemanager.common.messages.PresenceInfoChangeAliasMessage;

public class PresenceManagerImpl implements PresenceManager {

    private static final Logger logger =
            Logger.getLogger(PresenceManagerImpl.class.getName());
    private final Map<CellID, PresenceInfo> cellIDMap = new HashMap();
    private final Map<BigInteger, PresenceInfo> sessionIDMap = new HashMap();
    private final Map<WonderlandIdentity, PresenceInfo> userIDMap = new HashMap();
    private final Map<String, PresenceInfo> callIDMap = new HashMap();
    private final List<PresenceManagerListener> listeners = new ArrayList();
    private final Set<PresenceInfo> localInfo = new LinkedHashSet<PresenceInfo>();

    private WonderlandSession session;

    public PresenceManagerImpl(WonderlandSession session) {
        this.session = session;
    }

    public void presenceInfoAdded(PresenceInfo presenceInfo) {
        synchronized (cellIDMap) {
            synchronized (sessionIDMap) {
                synchronized (userIDMap) {
                    synchronized (callIDMap) {
                        if (alreadyInMaps(presenceInfo) == false) {
                            addPresenceInfoInternal(presenceInfo);
                        }
                    }
                }
            }
        }

        notifyListeners(presenceInfo, ChangeType.USER_ADDED);
    }

    private void addPresenceInfoInternal(PresenceInfo presenceInfo) {
        logger.fine("Adding presenceInfo for " + presenceInfo);

        userIDMap.put(presenceInfo.getUserID(), presenceInfo);

        if (presenceInfo.getCellID() != null) {
            cellIDMap.put(presenceInfo.getCellID(), presenceInfo);
        }

        if (presenceInfo.getClientID() != null) {
            sessionIDMap.put(presenceInfo.getClientID(), presenceInfo);
        }

        if (presenceInfo.getCallID() != null) {
            callIDMap.put(presenceInfo.getCallID(), presenceInfo);
        }
    }

    private boolean alreadyInMaps(PresenceInfo presenceInfo) {
        PresenceInfo info;

        if (presenceInfo.getCellID() != null) {
            info = cellIDMap.get(presenceInfo.getCellID());

            if (info != null && info.equals(presenceInfo) == false) {
                logger.info("Already in cellIDMap:  Existing PI " + info + " new PI " + presenceInfo);
                return true;
            }
        }

        if (presenceInfo.getClientID() != null) {
            info = sessionIDMap.get(presenceInfo.getClientID());

            if (info != null && info.equals(presenceInfo) == false) {
                logger.info("Already in clientIDMap:  Existing PI " + info + " new PI " + presenceInfo);
                return true;
            }
        }

        if (presenceInfo.getUserID() != null) {
            info = userIDMap.get(presenceInfo.getUserID());

            if (info != null && info.equals(presenceInfo) == false) {
                logger.info("Already in userIDMap:  Existing PI " + info + " new PI " + presenceInfo);
                return true;
            }
        }

        if (presenceInfo.getCallID() != null) {
            info = callIDMap.get(presenceInfo.getCallID());

            if (info != null && info.equals(presenceInfo) == false) {
                logger.info("Already in callIDMap:  Existing PI " + info + " new PI " + presenceInfo);
                return true;
            }
        }

        return false;
    }

    private void notifyListeners(PresenceInfo presenceInfo, ChangeType type) {
        /*
         * Notify listeners
         */
        PresenceManagerListener[] listenerArray;

        synchronized (listeners) {
            listenerArray = this.listeners.toArray(new PresenceManagerListener[0]);
        }

        for (int i = 0; i < listenerArray.length; i++) {
            listenerArray[i].presenceInfoChanged(presenceInfo, type);
        }
    }

    public void presenceInfoRemoved(PresenceInfo presenceInfo) {
        synchronized (cellIDMap) {
            synchronized (sessionIDMap) {
                synchronized (userIDMap) {
                    synchronized (callIDMap) {
                        cellIDMap.remove(presenceInfo.getCellID());

                        if (presenceInfo.getClientID() != null) {
                            sessionIDMap.remove(presenceInfo.getClientID());
                        }

                        userIDMap.remove(presenceInfo.getUserID());

                        if (presenceInfo.getCallID() != null) {
                            callIDMap.remove(presenceInfo.getCallID());
                        }
                    }
                }
            }
        }

        notifyListeners(presenceInfo, ChangeType.USER_REMOVED);
    }

    /**
     * Get PresenceInfo from a cellID.  The cellID must be for a ViewCell.
     * @param CellID the CellID of the ViewCell
     * @return PresenceInfo the PresenceInfo assoicated with the CellID.
     */
    public PresenceInfo getPresenceInfo(CellID cellID) {
        synchronized (cellIDMap) {
            PresenceInfo info = cellIDMap.get(cellID);

            if (info == null) {
                logger.fine("No presence info for CellID " + cellID);
                return null;
            }

            return info;
        }
    }

    /**
     * Get PresenceInfo from a Wonderland sessionID.
     * @param BigInteger the Wonderland sessionID
     * @return PresenceInfo PresenceInfo associated with the sessionID.
     */
    public PresenceInfo getPresenceInfo(BigInteger sessionID) {
        if (sessionID == null) {
            return null;
        }

        synchronized (sessionIDMap) {
            PresenceInfo info = sessionIDMap.get(sessionID);

            if (info == null) {
                logger.fine("No presence info for sessionID " + sessionID);
                return null;
            }

            return info;
        }
    }

    /**
     * Get PresenceInfo from a WonderlandIdentity.
     * @param WonderlandIdentity userID
     * @return PresenceInfo PresenceInfo associated with the WonderlandIdentity.
    public PresenceInfo getPresenceInfo(WonderlandIdentity userID) {
        synchronized (userIDMap) {
            PresenceInfo info = userIDMap.get(userID);

            if (info == null) {
                logger.fine("No presence info for userID " + userID);
    	        return null;
            }

            return info;
        }
    }

    /**
     * Get PresenceInfo from a callID.
     * @param String callID
     * @return PresenceInfo the PresenceInfo associated with the callID.
     */
    public PresenceInfo getPresenceInfo(String callID) {
        synchronized (callIDMap) {
            PresenceInfo info = callIDMap.get(callID);

            if (info == null) {
                logger.fine("No presence info for callID " + callID);
                return null;
            }

            return info;
        }
    }

    /**
     * Get the location of a cell
     */
    public Vector3f getCellPosition(CellID cellID) {
        return PresenceManagerClient.getInstance().getCellPosition(cellID);
    }

    /**
     * Get the WonderlandIdentity list of cells in range of the specified cellID.
     * @param CellID the CellID of the requestor
     * @param BoundingVolume The BoundingBox or BoundingSphere specifying the range.
     * @return WonderlandIdentity[] the array of user ID's.
     */
    private ArrayList<PresenceInfo> usersInRange = new ArrayList();

    public void playerInRange(PresenceInfo info, boolean isInRange) {
	if (isInRange) {
	    usersInRange.add(info);
	    notifyListeners(info, ChangeType.USER_IN_RANGE);
	} else {
	    usersInRange.remove(info);
	    notifyListeners(info, ChangeType.USER_OUT_OF_RANGE);
	}
    }

    public PresenceInfo[] getUsersInRange(CellID cellID, BoundingVolume bounds) {
        return usersInRange.toArray(new PresenceInfo[0]);
    }

    /**
     * Get the ID's of all users.
     * @return WonderlandIdentity[] the array of user ID's.
     */
    public PresenceInfo[] getAllUsers() {
        synchronized (userIDMap) {
            return userIDMap.values().toArray(new PresenceInfo[0]);
        }
    }

    /**
     * Get PresenceInfo for a given username.  
     */
    public PresenceInfo getUserPresenceInfo(String username) {
        PresenceInfo[] users;

        synchronized (userIDMap) {
            users = userIDMap.values().toArray(new PresenceInfo[0]);
        }

        for (int i = 0; i < users.length; i++) {
            if (users[i].getUserID().getUsername().equals(username)) {
                return users[i];
	    }
        }

        logger.fine("No presence info for " + username);
        return null;
    }

    /**
     * Get PresenceInfo for a given username alias.  If there is more 
     * than one user with the username alias, all of them are returned;
     * @param String user name alias
     * @return PresenceInfo presence information for user.
     */
    public PresenceInfo getAliasPresenceInfo(String usernameAlias) {
        PresenceInfo[] users;

        synchronized (userIDMap) {
            users = userIDMap.values().toArray(new PresenceInfo[0]);
        }

        for (int i = 0; i < users.length; i++) {
            if (users[i].getUsernameAlias().equals(usernameAlias)) {
                return users[i];
	    }
        }

        logger.fine("No presence info for " + usernameAlias);
        return null;
    }

    /**
     * Request that the server change this user's alias.
     * @param alias the alias to change
     */
    public void requestChangeUsernameAlias(String alias) {
        send(new PresenceInfoChangeAliasMessage(alias));
    }

    /**
     * Change usernameAlias in PresenceInfo.
     * @param String user name
     */
    public void changeUsernameAlias(PresenceInfo info, String alias) {
        info.setUsernameAlias(alias);
        notifyListeners(info, ChangeType.UPDATED);
    }

    /**
     * Set speaking flag
     * @param PresenceInfo
     * @param boolean
     */
    public void setSpeaking(PresenceInfo info, boolean isSpeaking) {
        info.setSpeaking(isSpeaking);
        fireAvatarNameEvent(info, isSpeaking?EventType.STARTED_SPEAKING:EventType.STOPPED_SPEAKING);
        notifyListeners(info, ChangeType.UPDATED);
    }

    /**
     * Set mute flag
     * @param PresenceInfo
     * @param boolean
     */
    public void setMute(PresenceInfo info, boolean isMuted) {
        info.setMuted(isMuted);
        fireAvatarNameEvent(info, isMuted?EventType.MUTE:EventType.UNMUTE);
        notifyListeners(info, ChangeType.UPDATED);
    }

    /**
     * Set enteredConeOfSilence flag
     * @param PresenceInfo
     * @param boolean
     */
    public void setEnteredConeOfSilence(PresenceInfo info, boolean inConeOfSilence) {
        info.setInConeOfSilence(inConeOfSilence);
        fireAvatarNameEvent(info, inConeOfSilence?EventType.ENTERED_CONE_OF_SILENCE:EventType.EXITED_CONE_OF_SILENCE);
        notifyListeners(info, ChangeType.UPDATED);
    }

    /**
     * Set inSecretChat flag
     * @param PresenceInfo
     * @param boolean
     */
    public void setInSecretChat(PresenceInfo info, boolean inSecretChat) {
        info.setInSecretChat(inSecretChat);
        notifyListeners(info, ChangeType.UPDATED);
    }

    /**
     * Post an avatar name event, which will update the view of this avatar.
     * @param info the PresenceInfo that changed
     * @param type the type of change
     */
    private void fireAvatarNameEvent(PresenceInfo info, EventType type) {
        InputManager.inputManager().postEvent(new AvatarNameEvent(
                type, info.getUserID().getUsername(), info.getUsernameAlias()));
    }

    public void addLocalPresenceInfo(PresenceInfo info) {
        synchronized (localInfo) {
            localInfo.add(info);
            presenceInfoAdded(info);
        }
    }

    public void removeLocalPresenceInfo(PresenceInfo info) {
        if (!isLocal(info)) {
            logger.fine("Attempt to remove non-local PresenceInfo: " + info);
            return;
        }

        synchronized (localInfo) {
            localInfo.remove(info);
            presenceInfoRemoved(info);
        }
    }

    private boolean isLocal(PresenceInfo info) {
        synchronized (localInfo) {
            return localInfo.contains(info);
        }
    }

    /**
     * Listener for changes
     * @param PresenceManagerListener the listener to be notified of a change
     */
    public void addPresenceManagerListener(PresenceManagerListener listener) {
        PresenceInfo[] info;

        synchronized (listeners) {
            if (listeners.contains(listener)) {
                logger.info("Listener is already added:  " + listener);
                return;
            }

            listeners.add(listener);
            info = cellIDMap.values().toArray(new PresenceInfo[0]);
        }

        for (int i = 0; i < info.length; i++) {
            listener.presenceInfoChanged(info[i], ChangeType.USER_ADDED);
        }
    }

    /**
     * Remove Listener for changes
     * @param PresenceManagerListener the listener to be removed
     */
    public void removePresenceManagerListener(PresenceManagerListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    private void send(Message message) {
	if (session.getStatus().equals(Status.CONNECTED) == false) {
	    logger.warning("Not Connected.  Can't send " + message);
	    return;
	}

	session.send(PresenceManagerClient.getInstance(), message);
    }

    /**
     * Display all presenceInfo
     */
    public void dump() {
        dump("Cell ID MAP", cellIDMap.values().toArray(new PresenceInfo[0]));
        dump("Session ID Map", sessionIDMap.values().toArray(new PresenceInfo[0]));
        dump("User ID Map", userIDMap.values().toArray(new PresenceInfo[0]));
        dump("Call ID Map", callIDMap.values().toArray(new PresenceInfo[0]));
    }

    private void dump(String message, PresenceInfo[] info) {
        System.out.println(message);

        for (int i = 0; i < info.length; i++) {
            System.out.println("  " + info[i]);
        }
    }
}
