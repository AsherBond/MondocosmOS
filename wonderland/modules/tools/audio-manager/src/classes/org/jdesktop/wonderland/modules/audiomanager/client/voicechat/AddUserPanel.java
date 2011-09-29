/*
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
package org.jdesktop.wonderland.modules.audiomanager.client.voicechat;

import org.jdesktop.wonderland.modules.audiomanager.client.AudioManagerClient;
import org.jdesktop.wonderland.modules.audiomanager.client.MemberChangeListener;
import org.jdesktop.wonderland.modules.audiomanager.client.UserInRangeListener;

import org.jdesktop.wonderland.modules.audiomanager.client.voicechat.AddHUDPanel.Mode;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManager;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManagerListener.ChangeType;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManagerListener;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManagerFactory;
import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;

import org.jdesktop.wonderland.modules.audiomanager.common.messages.GetPlayersInRangeRequestMessage;

import org.jdesktop.wonderland.modules.audiomanager.common.messages.audio.EndCallMessage;

import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatInfoRequestMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatDialOutMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatJoinMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatLeaveMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatMessage.ChatType;

import org.jdesktop.wonderland.client.cell.ProximityComponent;
import org.jdesktop.wonderland.client.cell.ProximityListener;

import org.jdesktop.wonderland.client.comms.WonderlandSession;

import org.jdesktop.wonderland.client.softphone.SoftphoneControl;
import org.jdesktop.wonderland.client.softphone.SoftphoneControlImpl;

import org.jdesktop.wonderland.common.auth.WonderlandIdentity;

import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.NameTagNode;

import org.jdesktop.wonderland.common.cell.CellID;

import org.jdesktop.wonderland.client.cell.Cell;

import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDEvent;
import org.jdesktop.wonderland.client.hud.HUDEvent.HUDEventType;
import org.jdesktop.wonderland.client.hud.HUDEventListener;
import org.jdesktop.wonderland.client.hud.HUDManagerFactory;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarCharacter;

import com.jme.bounding.BoundingSphere;
import com.jme.bounding.BoundingVolume;

import com.jme.math.Vector3f;

/**
 *
 * @author nsimpson
 */
public class AddUserPanel extends javax.swing.JPanel implements 
	PresenceManagerListener, MemberChangeListener, UserInRangeListener,
	ProximityListener {

    private static final Logger logger = Logger.getLogger(AddUserPanel.class.getName());

    private static final String BYSTANDER_SYMBOL = "\u25B8 ";

    private AddHUDPanel addHUDPanel;
    private AudioManagerClient client;
    private WonderlandSession session;
    private PresenceManager pm;
    private PresenceInfo myPresenceInfo;
    private PresenceInfo caller;
    private String group;

    private ChatType chatType = ChatType.PRIVATE;

    private DefaultListModel userListModel;

    private PrivacyPanel privacyPanel;

    private boolean personalPhone;

    public AddUserPanel(AddHUDPanel addHUDPanel, AudioManagerClient client, WonderlandSession session,
            PresenceInfo myPresenceInfo, PresenceInfo caller, String group) {

	this.addHUDPanel = addHUDPanel;
	this.client = client;
	this.session = session;
	this.myPresenceInfo = myPresenceInfo;
	this.caller = caller;
	this.group = group;

        initComponents();

	userListModel = new DefaultListModel();
        addUserList.setModel(userListModel);
	addUserList.setCellRenderer(new UserListCellRenderer());
	
        pm = PresenceManagerFactory.getPresenceManager(session);

        pm.addPresenceManagerListener(this);

	client.addMemberChangeListener(group, this);

	client.addUserInRangeListener(this);

	privacyPanel = new PrivacyPanel();

	privacyPanel.addSecretRadioButtonActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		secretButtonActionPerformed(e);
	    }
	});

	privacyPanel.addPrivateRadioButtonActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		privateButtonActionPerformed(e);
	    }
	});

	privacyPanel.addPublicRadioButtonActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
		publicButtonActionPerformed(e);
	    }
	});

        addUserDetailsPanel.add(privacyPanel, BorderLayout.CENTER);

        validate();

	/*
	 * Ask for group members
	 */
	session.send(client, new VoiceChatInfoRequestMessage(group));

	/*
	 * Ask for users in range
	 */
	session.send(client, new GetPlayersInRangeRequestMessage(
	    myPresenceInfo.getCallID()));
    }

    public void addUserListSelectionListener(javax.swing.event.ListSelectionListener listener) {
        addUserList.addListSelectionListener(listener);
    }

    public void removeUserListSelectionListener(javax.swing.event.ListSelectionListener listener) {
        addUserList.removeListSelectionListener(listener);
    }

    public void setVisible(boolean isVisible) {
	super.setVisible(isVisible);

	if (isVisible == false) {
	    return;
	}

	updateUserList();
    }

    private void updateUserList() {
	Mode mode = addHUDPanel.getMode();

	if (mode.equals(Mode.ADD) || mode.equals(Mode.INITIATE)) {
	    addNonMembers();
	} else if (mode.equals(Mode.IN_PROGRESS)) {
	    addMembers();
	}
    }

    private void addNonMembers() {
	clearUserList();

        PresenceInfo[] presenceInfoList = pm.getAllUsers();

	for (int i = 0; i < presenceInfoList.length; i++) {
	    PresenceInfo info = presenceInfoList[i];

	    if (info.getCallID() == null) {
                // It's a virtual player, skip it.
		continue;
            }

	    synchronized (members) {
	        if (members.contains(info) || invitedMembers.contains(info)) {
                    removeFromUserList(info);
                } else {
                    addToUserList(info);
                }
	    }
	}
    }

    private void addMembers() {
	clearUserList();

	addToUserList(myPresenceInfo);

        PresenceInfo[] presenceInfoList = pm.getAllUsers();

	for (int i = 0; i < presenceInfoList.length; i++) {
	    PresenceInfo info = presenceInfoList[i];

	    if (info.getCallID() == null) {
                // It's a virtual player, skip it.
                continue;
            }

	    synchronized (members) {
	        synchronized (invitedMembers) {
	            if (members.contains(info)) {
	    	        if (info.equals(myPresenceInfo) == false) {
                            addToUserList(info);
		        }
		    } else if (invitedMembers.contains(info)) {
	    	        if (info.equals(myPresenceInfo) == false) {
                            addToUserList(info);
		        }
		    }
		}
	    }
	}
    }

    public void secretButtonActionPerformed(ActionEvent e) {
	chatType = chatType.SECRET;

	if (addHUDPanel.getMode().equals(Mode.IN_PROGRESS) == false) {
	    return;
	}

	changePrivacy();
    }

    public void privateButtonActionPerformed(ActionEvent e) {
	chatType = chatType.PRIVATE;

	if (addHUDPanel.getMode().equals(Mode.IN_PROGRESS) == false) {
	    return;
	}

	changePrivacy();
    }

    public void publicButtonActionPerformed(ActionEvent e) {
	chatType = chatType.PUBLIC;

	if (addHUDPanel.getMode().equals(Mode.IN_PROGRESS) == false) {
	    return;
	}

	changePrivacy();
    }

    private void changePrivacy() {
	ArrayList<PresenceInfo> users = getSelectedValues();

	animateCallAnswer();

        if (users.contains(myPresenceInfo) == false) {
            session.send(client, new VoiceChatJoinMessage(group, myPresenceInfo, 
		new PresenceInfo[0], chatType));
        }

        for (PresenceInfo info : users) {
            /*
             * You can only select yourself or outworlders
             */
            if (info.getClientID() != null) {
                continue;
            }

            session.send(client, new VoiceChatJoinMessage(group, info, 
		new PresenceInfo[0], chatType));
        }

	Cell cell = client.getCell();
	ProximityComponent prox = cell.getComponent(ProximityComponent.class);

	if (prox == null) {
	    logger.warning("NO PROX FOR CELL!");
	    return;
	}

	if (chatType.equals(ChatType.PUBLIC)) {
	    BoundingVolume[] bounds = new BoundingVolume[] {
                new BoundingSphere((float) 1, new Vector3f())
	    };

	    logger.info("Adding proximity listener... " + prox 
		+ " bounds " + bounds[0]);

	    prox.addProximityListener(this, bounds);
	} else {
	    prox.removeProximityListener(this);
	}
    }

    public void setPrivacy(ChatType chatType) {
	this.chatType = chatType;
    }

    public ChatType getPrivacy() {
	return chatType;
    }

    public void viewEnterExit(boolean entered, Cell cell, CellID viewCellID,
	    BoundingVolume proximityVolume, int proximityIndex) {

	PresenceInfo cellInfo = pm.getPresenceInfo(cell.getCellID());
	PresenceInfo viewCellInfo = pm.getPresenceInfo(viewCellID);

	logger.info("Entered " + entered + " cellInfo " + cellInfo
	    + " viewCellInfo " + viewCellInfo);
    }

    public void showPrivacyPanel(boolean showPrivacy) {
        addUserDetailsPanel.setVisible(showPrivacy);
    }

    public void callUser(String name, String number) {
        personalPhone = true;

	animateCallAnswer();

        session.send(client, new VoiceChatJoinMessage(group, myPresenceInfo,
            new PresenceInfo[0], chatType));

        SoftphoneControl sc = SoftphoneControlImpl.getInstance();

        String callID = sc.getCallID();

        PresenceInfo presenceInfo = new PresenceInfo(null, null, 
	    new WonderlandIdentity(name, name, null), callID);

        pm.addLocalPresenceInfo(presenceInfo);

	updateUserList();
        session.send(client, new VoiceChatDialOutMessage(group, callID, chatType, presenceInfo, number));
    }

    private void animateCallAnswer() {
	if (chatType.equals(ChatType.PRIVATE)) {
	    CallAnimator.animateCallAnswer(client);
	    logger.fine("Playing animation...");
	} else {
	    CallAnimator.stopCallAnswerAnimation(client);
	    logger.fine("Stopping animation...");
	}
    }

    public void inviteUsers() {
	ArrayList<PresenceInfo> usersToInvite = getSelectedValues();
	usersToInvite.remove(myPresenceInfo);
	inviteUsers(usersToInvite);
    }

    public void inviteUsers(ArrayList<PresenceInfo> usersToInvite) {
	clearUserList();

	animateCallAnswer();

        for (PresenceInfo info : usersToInvite) {
	    synchronized (invitedMembers) {
                invitedMembers.remove(info);
                invitedMembers.add(info);
		logger.warning("Sending invite to " + info + " chatType " + chatType);
	    }
	}

	updateUserList();

        session.send(client, new VoiceChatJoinMessage(group, myPresenceInfo,
            usersToInvite.toArray(new PresenceInfo[0]), chatType));
    }


    public ArrayList<PresenceInfo> getSelectedValues() {
	Object[] selectedValues = addUserList.getSelectedValues();

	ArrayList<PresenceInfo> usersToInvite = new ArrayList();

	if (selectedValues.length == 0) {
	    return new ArrayList<PresenceInfo>();
        }

	for (int i = 0; i < selectedValues.length; i++) {
            String username = NameTagNode.getUsername((String) selectedValues[i]);

            PresenceInfo info = pm.getAliasPresenceInfo(username);

            if (info == null) {
                logger.warning("no PresenceInfo for " + username);
                continue;
            }

            usersToInvite.add(info);
        }

	return usersToInvite;
    }

    private ConcurrentHashMap<String, String> usernameMap = new ConcurrentHashMap();

    private void clearUserList() {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
		clearUserListLater();
            }
        });
    }

    private void clearUserListLater() {
	userListModel.clear();
	usernameMap.clear();
    }

    private void addElement(final PresenceInfo info, final String usernameAlias) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
		addElementLater(info, usernameAlias);
            }
        });
    }

    private void addElementLater(PresenceInfo info, String usernameAlias) {
	//userListModel.removeElement(usernameAlias);
        userListModel.addElement(usernameAlias);
	usernameMap.put(info.getUserID().getUsername(), usernameAlias);
	//dump("addElement later size " + userListModel.size() + " " 
	//    + usernameAlias);
    }

    private void removeElement(final PresenceInfo info, final String usernameAlias) {
	//new Exception("removed " + info.userID.getUsername() + " mode " + addHUDPanel.getMode()).printStackTrace();

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
		removeElementLater(info, usernameAlias);
            }
        });
    }

    private void removeElementLater(PresenceInfo info, String usernameAlias) {
	userListModel.removeElement(usernameAlias);
	usernameMap.remove(info.getUserID().getUsername());
    }

    private void addToUserList(final PresenceInfo info) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
		addToUserListLater(info);
            }
        });
    }

    private void addToUserListLater(PresenceInfo info) {
        removeFromUserListLater(info);

        String displayName = NameTagNode.getDisplayName(info.getUsernameAlias(),
                info.isSpeaking(), info.isMuted());

        addElementLater(info, displayName);
    }

    private void removeFromUserList(final PresenceInfo info) {
	//new Exception("removed " + info.userID.getUsername() + " mode " + addHUDPanel.getMode()).printStackTrace();

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
		removeFromUserListLater(info);
            }
        });
    }

    private void removeFromUserListLater(PresenceInfo info) {
        String name = NameTagNode.getDisplayName(info.getUsernameAlias(), false, false);
        removeElementLater(info, name);

	name = BYSTANDER_SYMBOL + name;
        removeElementLater(info, name);

        name = NameTagNode.getDisplayName(info.getUsernameAlias(), false, true);
        removeElementLater(info, name);

	name = BYSTANDER_SYMBOL + name;
        removeElementLater(info, name);

        name = NameTagNode.getDisplayName(info.getUsernameAlias(), true, false);
        removeElementLater(info, name);

	name = BYSTANDER_SYMBOL + name;
        removeElementLater(info, name);
    }

    private void setElementAt(PresenceInfo info, String displayName, int ix) {
	setElementAt(displayName, ix);
	usernameMap.put(info.getUserID().getUsername(), displayName);
    }

    private void dump(String s) {
	System.out.println("======");
	System.out.println(s);
	
	for (int i = 0; i < userListModel.size(); i++) {
	    System.out.println((String) userListModel.getElementAt(i));
	}

	System.out.println("======");
    }

    private void setElementAt(String displayName, int ix) {
	if (ix < userListModel.size()) {
	    userListModel.setElementAt(displayName, ix);
	    //dump("Set at " + ix + " " + displayName);
	} else {
	    userListModel.addElement(displayName);
	    //dump("Added at " + ix + " " + displayName);
	}
    }

    private void removeElementAt(PresenceInfo info, int ix) {
	usernameMap.remove(info.getUserID().getUsername());
	userListModel.removeElementAt(ix);
	logger.fine("Removed element at " + ix + " " + info.getUserID().getUsername());
    }

    public void presenceInfoChanged(PresenceInfo presenceInfo, ChangeType type) {
	switch (addHUDPanel.getMode()) {
	case ADD:
	    switch (type) {
	    case USER_ADDED:
		synchronized (members) {
		    if (members.contains(presenceInfo)) {
		        removeFromUserList(presenceInfo);
		        break;
		    }
		}
		addToUserList(presenceInfo);
		break;

	    case USER_REMOVED:
		removeFromUserList(presenceInfo);
	        break;
	    }

	    break;

	case INITIATE:
	    switch (type) {
            case USER_ADDED:
		if (presenceInfo.equals(myPresenceInfo)) {
		    removeFromUserList(presenceInfo);
		    break;
		}

		addToUserList(presenceInfo);
		break;

	    case USER_REMOVED:
		removeFromUserList(presenceInfo);
		if (personalPhone) {
		    synchronized (members) {
		        if (presenceInfo.getClientID() == null && members.size() == 1) {
			    leave();
		        }
		    }
		}
	        break;
	    }

	    break;

	case IN_PROGRESS:
	    switch (type) {
	    case USER_ADDED:
		break;

	    case USER_REMOVED:
		removeFromInRangeMaps(presenceInfo);
		removeFromUserList(presenceInfo);
		break;

	    case UPDATED:
		updatePresenceInfo(presenceInfo);
	    }

	    break;
	}

	updateUserList();
    }

    private void updatePresenceInfo(PresenceInfo info) {
	int ix;

	if ((ix = members.indexOf(info)) >= 0) {
	    updatePresenceInfo(info, members.get(ix));
	}

	if ((ix = invitedMembers.indexOf(info)) >= 0) {
	    updatePresenceInfo(info, invitedMembers.get(ix));
	}

	//dumpu();

	Collection<CopyOnWriteArrayList<PresenceInfo>> c = usersInRangeMap.values();

	Iterator<CopyOnWriteArrayList<PresenceInfo>> it = c.iterator();

	while (it.hasNext()) {
	    CopyOnWriteArrayList<PresenceInfo> usersInRange = it.next();

	    if ((ix = usersInRange.indexOf(info)) >= 0) {
	        updatePresenceInfo(info, usersInRange.get(ix));
	    }
	}
    }

    private void updatePresenceInfo(PresenceInfo source, PresenceInfo dest) {
	dest.setSpeaking(source.isSpeaking());
	dest.setMuted(source.isMuted());
	dest.setInConeOfSilence(source.isInConeOfSilence());
	dest.setInSecretChat(source.isInSecretChat());
	//System.out.println("UPDATE:  " + source + " DEST " + dest);
    }

    private void removeFromInRangeMaps(PresenceInfo presenceInfo) {
	Enumeration<String> e = usersInRangeMap.keys();

	while (e.hasMoreElements()) {
	    String username = e.nextElement();

	    CopyOnWriteArrayList<PresenceInfo> usersInRange = usersInRangeMap.get(username);

	    usersInRange.remove(presenceInfo);
	}
    }
		
    private void dumpu() {
	System.out.println("+++++++++");

	Enumeration<String> e = usersInRangeMap.keys();

	while (e.hasMoreElements()) {
	    String username = e.nextElement();

	    System.out.println("In range of " + username);

	    CopyOnWriteArrayList<PresenceInfo> usersInRange = usersInRangeMap.get(username);
	    
	    for (PresenceInfo info : usersInRange) {
		System.out.println("  " + info.getUserID().getUsername());
	    }
	}
	
	System.out.println("+++++++++");
    }

    private CopyOnWriteArrayList<PresenceInfo> members = new CopyOnWriteArrayList();
    private CopyOnWriteArrayList<PresenceInfo> invitedMembers = new CopyOnWriteArrayList();
    private CopyOnWriteArrayList<PresenceInfo> transientMembers = new CopyOnWriteArrayList();

    public void memberChange(PresenceInfo presenceInfo, boolean added, boolean isTransientMember) {
	synchronized (invitedMembers) {
	    invitedMembers.remove(presenceInfo);
	}

	logger.fine("member change:  " + presenceInfo + " added " + added + " mode " + addHUDPanel.getMode()
	    + " isTransient " + isTransientMember);

	if (added) {
	    synchronized (members) {
	        if (members.contains(presenceInfo) == false) {
		    members.add(presenceInfo);
	        }
	    }

	    synchronized (transientMembers) {
		transientMembers.remove(presenceInfo);

	        if (isTransientMember) {
		    transientMembers.add(presenceInfo);
		}
	    }
	} else {
	    synchronized (members) {
	        members.remove(presenceInfo);
	    }

	    synchronized (transientMembers) {
		transientMembers.remove(presenceInfo);
	    }

	    synchronized (members) {
	        if (personalPhone && members.size() == 1) {
                    leave();
                }
	    }
	}

	updateUserList();
    }

    public void setMemberList(PresenceInfo[] memberList) {
	logger.fine("Set member list...");

	synchronized (invitedMembers) {
	    synchronized (members) {
	        for (int i = 0; i < memberList.length; i++) {
		    PresenceInfo info = memberList[i];

		    invitedMembers.remove(info);

		    logger.fine("Member " + info);

	            if (members.contains(info) == false) {
		        members.add(info);
			logger.fine("added " + members.size());
	            }
	        }
	    }
	}

	updateUserList();
    }

    private void leave() {
        session.send(client, new VoiceChatLeaveMessage(group, myPresenceInfo,
	    client.getCOSName()));

	CallAnimator.stopCallAnswerAnimation(client);
    }

    public void hangup() {
       ArrayList<PresenceInfo> membersInfo = getSelectedValues();

        for (PresenceInfo info : membersInfo) {
            if (info.getClientID() != null) {
                continue;
            }

            session.send(client, new EndCallMessage(info.getCallID(), "Terminated with malice"));
        }
    }

    private boolean isMe(PresenceInfo info) {
	return myPresenceInfo.equals(info);
    }

    private ConcurrentHashMap<String, CopyOnWriteArrayList<PresenceInfo>> usersInRangeMap = 
	new ConcurrentHashMap();

    private boolean isInRange(PresenceInfo info) {
	CopyOnWriteArrayList<PresenceInfo> usersInRange = usersInRangeMap.get(myPresenceInfo.getUserID().getUsername());

        return isMe(info) || usersInRange.contains(info);
    }

    private boolean isInRangeOfSomebody(PresenceInfo info) {
	Collection<CopyOnWriteArrayList<PresenceInfo>> c = usersInRangeMap.values();

	Iterator<CopyOnWriteArrayList<PresenceInfo>> it = c.iterator();

	while (it.hasNext()) {
	    CopyOnWriteArrayList<PresenceInfo> usersInRange = it.next();
	    if (usersInRange.contains(info)) {
		return true;
	    }
	}

	return false;
    }

    public void userInRange(PresenceInfo info, PresenceInfo userInRange, boolean isInRange) {
	CopyOnWriteArrayList<PresenceInfo> usersInRange = usersInRangeMap.get(info.getUserID().getUsername());

	logger.fine("userInRange:  " + info + " userInRange " + userInRange + " inRange "
	    + isInRange);
 
	if (usersInRange == null) {
	    if (isInRange == false) {
		return;
	    }

	    usersInRange = new CopyOnWriteArrayList();
	    usersInRangeMap.put(info.getUserID().getUsername(), usersInRange);
	    logger.fine("ADDING NEW MAP FOR " + info);
	}

	if (isInRange) {
	    if (usersInRange.contains(userInRange)) {
		updateUserList();
		return;
	    }

	    logger.fine("Adding in RANGE:  " + userInRange + " FOR " + info);
	    usersInRange.add(userInRange);
	} else {
	    usersInRange.remove(userInRange);
	    logger.fine("Removing user out of range " + userInRange);
	}

	//dumpu();

	updateUserList();
    }

    private void remove(PresenceInfo info) {
        String username = info.getUserID().getUsername();

	String mapEntry = usernameMap.get(username);

	if (mapEntry == null) {
	    removeFromUserListLater(info);
	    return;
	}

	// TODO Need to remove from userInRangeMap
	int position = userListModel.indexOf(mapEntry);
        removeFromUserListLater(info);
	removeBystanders(position);
    }

    private void removeBystanders(int position) {
    }

    private class UserListCellRenderer implements ListCellRenderer {

        protected DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();
        private Font font = new Font("SansSerif", Font.PLAIN, 13);

        public Component getListCellRendererComponent(JList list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index,
                    isSelected, cellHasFocus);

            String usernameAlias = NameTagNode.getUsername(((String) value).replace(BYSTANDER_SYMBOL, ""));

            PresenceInfo info = pm.getAliasPresenceInfo(usernameAlias);

            if (info == null) {
                logger.warning("No presence info for " + usernameAlias + " value " + value);
                return renderer;
            }

	    boolean isMember = members.contains(info);

	    // TODO if it's a member or a bystander, make it black.

	    if (isMember || addHUDPanel.getMode().equals(Mode.INITIATE) || addHUDPanel.getMode().equals(Mode.ADD)) {
                renderer.setFont(font);

		if (transientMembers.contains(info)) {
                    renderer.setForeground(Color.RED);
		} else {
                    renderer.setForeground(Color.BLACK);
		}
            } else {
                renderer.setFont(font);
                renderer.setForeground(Color.LIGHT_GRAY);
            }
            return renderer;
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        addUserScrollPane = new javax.swing.JScrollPane();
        addUserList = new javax.swing.JList();
        addUserDetailsPanel = new javax.swing.JPanel();

        setMinimumSize(new java.awt.Dimension(0, 95));
        setName("Form"); // NOI18N
        setPreferredSize(new java.awt.Dimension(295, 95));

        addUserScrollPane.setMinimumSize(new java.awt.Dimension(23, 89));
        addUserScrollPane.setName("addUserScrollPane"); // NOI18N

        addUserList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        addUserList.setName("addUserList"); // NOI18N
        addUserList.setVisibleRowCount(5);
        addUserList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                addUserListValueChanged(evt);
            }
        });
        addUserScrollPane.setViewportView(addUserList);

        addUserDetailsPanel.setBackground(new java.awt.Color(0, 0, 0));
        addUserDetailsPanel.setName("addUserDetailsPanel"); // NOI18N
        addUserDetailsPanel.setLayout(new java.awt.BorderLayout());

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, addUserDetailsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, addUserScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(addUserScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 93, Short.MAX_VALUE)
                .add(0, 0, 0)
                .add(addUserDetailsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 2, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

private void addUserListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_addUserListValueChanged
// TODO add your handling code here:
}//GEN-LAST:event_addUserListValueChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel addUserDetailsPanel;
    private javax.swing.JList addUserList;
    private javax.swing.JScrollPane addUserScrollPane;
    private javax.swing.ButtonGroup buttonGroup1;
    // End of variables declaration//GEN-END:variables
}
