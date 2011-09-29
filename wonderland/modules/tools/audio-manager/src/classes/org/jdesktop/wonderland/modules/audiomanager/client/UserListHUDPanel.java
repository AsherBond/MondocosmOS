/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

/*
 * Project Wonderland
 * 
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., All Rights Reserved
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
package org.jdesktop.wonderland.modules.audiomanager.client;

import com.jme.math.Vector3f;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.ChannelComponent;
import org.jdesktop.wonderland.client.cell.view.ViewCell;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.hud.CompassLayout.Layout;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDEvent;
import org.jdesktop.wonderland.client.hud.HUDEvent.HUDEventType;
import org.jdesktop.wonderland.client.hud.HUDEventListener;
import org.jdesktop.wonderland.client.hud.HUDManagerFactory;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.ViewManager;
import org.jdesktop.wonderland.client.softphone.SoftphoneControlImpl;
import org.jdesktop.wonderland.client.softphone.SoftphoneListener;
import org.jdesktop.wonderland.common.auth.WonderlandIdentity;
import org.jdesktop.wonderland.common.cell.CellTransform;
import org.jdesktop.wonderland.modules.audiomanager.client.voicechat.AddHUDPanel;
import org.jdesktop.wonderland.modules.audiomanager.client.voicechat.AddHUDPanel.Mode;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.AudioVolumeMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.VolumeConverter;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.ChangeUsernameAliasMessage;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.NameTagNode;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManager;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManagerListener;
import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;
import org.jdesktop.wonderland.modules.textchat.client.ChatManager;

/**
 *
 * @author nsimpson
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class UserListHUDPanel
        extends javax.swing.JPanel implements PresenceManagerListener,
        UsernameAliasChangeListener, UserListPanel
{

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/audiomanager/client/resources/Bundle");
    private static final Logger LOGGER =
            Logger.getLogger(UserListHUDPanel.class.getName());
    private Cell cell;
    private ChannelComponent channelComp;
    private PresenceManager pm;
    private PresenceControls pc;
    private PresenceInfo presenceInfo;
    private Map<PresenceInfo, HUDComponent> changeNameMap =
            Collections.synchronizedMap(
            new HashMap<PresenceInfo, HUDComponent>());
    private ConcurrentHashMap<String, String> usernameMap =
            new ConcurrentHashMap<String, String>();
    private HUDComponent namePropertiesHUDComponent;
    private String[] selection = null;
    private DefaultListModel userListModel;
    private ArrayList<PresenceInfo> usersInRange = new ArrayList();
    private int outOfRangeIndex = 0;
    private Font inRangeFont = new Font("SansSerif", Font.PLAIN, 14);
    private Font outOfRangeFont = new Font("SansSerif", Font.PLAIN, 14);
    private ImageIcon mutedIcon;
    private ImageIcon unmutedIcon;
    private ImageIcon upIcon;
    private ImageIcon downIcon;
    private AudioManagerClient client;
    private WonderlandSession session;
    private HUDComponent userListHUDComponent;
    private HUDComponent addHUDComponent;

    private VolumeConverter volumeConverter;

    public UserListHUDPanel(PresenceControls pc, Cell cell) {
        this.pc = pc;

        this.client = pc.getClient();
        this.session = pc.getSession();
        this.pm = pc.getPresenceManager();

        this.cell = cell;

        initComponents();

        mutedIcon = new ImageIcon(getClass().getResource(
                "/org/jdesktop/wonderland/modules/audiomanager/client/" +
                "resources/UserListMicMuteOn24x24.png"));
        unmutedIcon = new ImageIcon(getClass().getResource(
                "/org/jdesktop/wonderland/modules/audiomanager/client/" +
                "resources/UserListMicMuteOff24x24.png"));
        upIcon = new ImageIcon(getClass().getResource(
                "/org/jdesktop/wonderland/modules/audiomanager/client/" +
                "resources/upArrow23x10.png"));
        downIcon = new ImageIcon(getClass().getResource(
                "/org/jdesktop/wonderland/modules/audiomanager/client/" +
                "resources/downArrow23x10.png"));

        userListModel = new DefaultListModel();
        userList.setModel(userListModel);
        userList.setCellRenderer(new UserListCellRenderer());

        textChatButton.setEnabled(false);
        voiceChatButton.setEnabled(false);
        gotoUserButton.setEnabled(false);

        channelComp = cell.getComponent(ChannelComponent.class);

        pm.addPresenceManagerListener(this);
        presenceInfo = pm.getPresenceInfo(cell.getCellID());

        if (presenceInfo == null) {
            LOGGER.warning("no presence info for cell " + cell.getCellID());
            return;
        }
        controlPanel.setVisible(false);
        editButton.setEnabled(false);
        propertiesButton.setEnabled(true);

	volumeConverter = new VolumeConverter(volumeSlider.getMaximum());

        SoftphoneControlImpl sc = SoftphoneControlImpl.getInstance();
        sc.addSoftphoneListener(new SoftphoneListener() {

            public void softphoneVisible(boolean isVisible) {
            }

            public void softphoneMuted(boolean muted) {
                updateMuteButton();
            }

            public void softphoneConnected(boolean connected) {
            }

            public void softphoneExited() {
            }

            public void microphoneGainTooHigh() {
            }

	    public void softphoneTestUDPPort(int port, int duration) {
	    }

	    public void softphoneProblem(String problem) {
	    }
        });
    }

    public void setHUDComponent(HUDComponent userListHUDComponent) {
        this.userListHUDComponent = userListHUDComponent;
    }

    public void changeUsernameAlias(PresenceInfo info) {
        session.send(client, new ChangeUsernameAliasMessage(info.getCellID(), info));
    }

    public void updateMuteButton() {
        SoftphoneControlImpl sc = SoftphoneControlImpl.getInstance();

        if (sc.isMuted()) {
            muteButton.setIcon(mutedIcon);
        } else {
            muteButton.setIcon(unmutedIcon);
        }
    }

    public void done() {
        setVisible(false);
    }

    private boolean isMe(PresenceInfo info) {
        if ((presenceInfo == null) && (cell != null) && (pm != null)) {
            presenceInfo = pm.getPresenceInfo(cell.getCellID());
        }
        return ((presenceInfo != null) && presenceInfo.equals(info));
    }

    private boolean isInRange(PresenceInfo info) {
        return isMe(info) || (usersInRange.contains(info));
    }

    public synchronized void setUserList() {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                setUserListLater();
            }
        });
    }

    private void setUserListLater() {
        PresenceInfo[] presenceInfoList = pm.getAllUsers();


        for (int i = 0; i < presenceInfoList.length; i++) {
            PresenceInfo info = presenceInfoList[i];

            if (info.getCallID() == null) {
                // It's a virtual player, skip it.
                continue;
            }

            String username = info.getUserID().getUsername();
            String displayName = NameTagNode.getDisplayName(
                    info.getUsernameAlias(), info.isSpeaking(), info.isMuted());

            boolean inRange = isInRange(info);
            //displayName = (inRange ? "\u25B8 " : "") + displayName;

            int desiredPosition;
            boolean newUser = !usernameMap.containsKey(username);

            if (newUser) {
                if (isMe(info)) {
                    // this user always goes at the top of the list
                    desiredPosition = 0;
                } else {
                    // some other user
                    if (inRange) {
                        // new in range user
                        // put them at the bottom of the in range list
                        desiredPosition = outOfRangeIndex;
                    } else {
                        // new out of range user
                        // add them to the end of the out of range list
                        // (bottom of user list)
                        desiredPosition = userListModel.size();
                    }
                }
                LOGGER.finest("inserting new user: " + username +
                        " at position: " + desiredPosition);
                if (inRange) {
                    outOfRangeIndex++;
                }

                usernameMap.put(username, displayName);
                userListModel.insertElementAt(displayName, desiredPosition);
            } else {
                // existing user
                // update entry in list model
                String oldName = usernameMap.get(username);
                int currentIndex =
                        userListModel.indexOf(usernameMap.get(username));
                boolean wasInRange = currentIndex < outOfRangeIndex;

                if (inRange != wasInRange) {
                    boolean reselect = userList.isSelectedIndex(currentIndex);
                    LOGGER.finest("user in range: " + username + ": " +
                            wasInRange + " -> " + inRange);
                    LOGGER.finest("removing: " + username +
                            " at " + currentIndex);
                    userListModel.removeElementAt(currentIndex);

                    if (wasInRange) {
                        // user has gone out of range
                        // add them to the end of the out of range list
                        // (at the bottom of user list)
                        outOfRangeIndex--;
                        desiredPosition = outOfRangeIndex;
                        LOGGER.finest("inserting out of range: " + username +
                                " at " + desiredPosition);
                    } else {
                        // user has come in range
                        // put them at the bottom of the in range list
                        desiredPosition = outOfRangeIndex;
                        outOfRangeIndex++;
                        LOGGER.finest("inserting in range: " + username +
                                " at " + desiredPosition);
                    }
                    userListModel.insertElementAt(displayName, desiredPosition);
                    if (reselect) {
                        userList.setSelectedIndex(desiredPosition);
                    }
                } else {
                    // user range didn't change
                    desiredPosition = currentIndex;
                }
                // update name
                if (!displayName.equals(oldName)) {
                    LOGGER.finest("name change: " +
                            oldName + " -> " + displayName);
                    usernameMap.replace(username, displayName);
                    userListModel.setElementAt(displayName, desiredPosition);
                }
            }
        }

        // search for removed users
        Iterator<String> iter = usernameMap.keySet().iterator();
        while (iter.hasNext()) {
            // for each user previously displayed...
            String username = (String) iter.next();
            boolean found = false;

            // check if user is in current presence list
            for (int i = 0; i < presenceInfoList.length; i++) {
                PresenceInfo info = presenceInfoList[i];
                if (username.equals(info.getUserID().getUsername())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                // user is no longer present, remove them from the user list
                LOGGER.finest("removing user: " + username);
                int index = userListModel.indexOf(usernameMap.get(username));
                boolean wasInRange = index < outOfRangeIndex;

                userListModel.removeElement(usernameMap.get(username));
                usernameMap.remove(username);
                if (wasInRange) {
                    outOfRangeIndex--;
                }
            }
        }

        // Update the name of the Users List HUD to match the number of users
        if (userListHUDComponent != null) {
            userListHUDComponent.setName(BUNDLE.getString("Users") + " (" +
                    usernameMap.size() + ")");
        }

        //logger.finest("map: " + usernameMap);
        //logger.finest("out of range index: " + outOfRangeIndex);
    }

    private class UserListCellRenderer implements ListCellRenderer {

        protected DefaultListCellRenderer defaultRenderer =
                new DefaultListCellRenderer();

        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            JLabel renderer =
                    (JLabel) defaultRenderer.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
            if (index < outOfRangeIndex) {
                renderer.setFont(inRangeFont);
                renderer.setForeground(Color.BLUE);
            } else {
                renderer.setFont(outOfRangeFont);
                renderer.setForeground(Color.BLACK);
            }
            return renderer;
        }
    }

    public void presenceInfoChanged(PresenceInfo info, ChangeType type) {
        if (type.equals(ChangeType.USER_IN_RANGE)) {
            LOGGER.fine("user in range:  " + info);
            usersInRange.add(info);
        } else if (type.equals(ChangeType.USER_OUT_OF_RANGE)) {
            LOGGER.fine("user out of range:  " + info);
            usersInRange.remove(info);
        }

        setUserList();
    }

    public void usernameAliasChanged(PresenceInfo info) {
        setUserList();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        controlPanel = new javax.swing.JPanel();
        muteButton = new javax.swing.JButton();
        textChatButton = new javax.swing.JButton();
        voiceChatButton = new javax.swing.JButton();
        phoneButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        propertiesButton = new javax.swing.JButton();
        volumeLabel = new javax.swing.JLabel();
        gotoUserButton = new javax.swing.JButton();
        volumeSlider = new javax.swing.JSlider();
        userListScrollPane = new javax.swing.JScrollPane();
        userList = new javax.swing.JList();
        jPanel1 = new javax.swing.JPanel();
        panelToggleButton = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(194, 300));

        muteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/audiomanager/client/resources/UserListMicMuteOff24x24.png"))); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/modules/audiomanager/client/resources/Bundle"); // NOI18N
        muteButton.setToolTipText(bundle.getString("UserListHUDPanel.muteButton.toolTipText")); // NOI18N
        muteButton.setBorderPainted(false);
        muteButton.setMaximumSize(new java.awt.Dimension(24, 24));
        muteButton.setMinimumSize(new java.awt.Dimension(24, 24));
        muteButton.setPreferredSize(new java.awt.Dimension(24, 24));
        muteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                muteButtonActionPerformed(evt);
            }
        });

        textChatButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/audiomanager/client/resources/UserListChatText24x24.png"))); // NOI18N
        textChatButton.setToolTipText(bundle.getString("UserListHUDPanel.textChatButton.toolTipText")); // NOI18N
        textChatButton.setBorderPainted(false);
        textChatButton.setMaximumSize(new java.awt.Dimension(24, 24));
        textChatButton.setMinimumSize(new java.awt.Dimension(24, 24));
        textChatButton.setPreferredSize(new java.awt.Dimension(24, 24));
        textChatButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textChatButtonActionPerformed(evt);
            }
        });

        voiceChatButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/audiomanager/client/resources/UserListChatVoice24x24.png"))); // NOI18N
        voiceChatButton.setToolTipText(bundle.getString("UserListHUDPanel.voiceChatButton.toolTipText")); // NOI18N
        voiceChatButton.setMaximumSize(new java.awt.Dimension(24, 24));
        voiceChatButton.setMinimumSize(new java.awt.Dimension(24, 24));
        voiceChatButton.setPreferredSize(new java.awt.Dimension(24, 24));
        voiceChatButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                voiceChatButtonActionPerformed(evt);
            }
        });

        phoneButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/audiomanager/client/resources/UserListPhone24x24.png"))); // NOI18N
        phoneButton.setToolTipText(bundle.getString("UserListHUDPanel.phoneButton.toolTipText")); // NOI18N
        phoneButton.setMaximumSize(new java.awt.Dimension(24, 24));
        phoneButton.setMinimumSize(new java.awt.Dimension(24, 24));
        phoneButton.setPreferredSize(new java.awt.Dimension(24, 24));
        phoneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                phoneButtonActionPerformed(evt);
            }
        });

        editButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/audiomanager/client/resources/UserListEdit24x24.png"))); // NOI18N
        editButton.setToolTipText(bundle.getString("UserListHUDPanel.editButton.toolTipText")); // NOI18N
        editButton.setMaximumSize(new java.awt.Dimension(24, 24));
        editButton.setMinimumSize(new java.awt.Dimension(24, 24));
        editButton.setPreferredSize(new java.awt.Dimension(24, 24));
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        propertiesButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/audiomanager/client/resources/UserListProperties24x24.png"))); // NOI18N
        propertiesButton.setToolTipText(bundle.getString("UserListHUDPanel.propertiesButton.toolTipText")); // NOI18N
        propertiesButton.setMaximumSize(new java.awt.Dimension(24, 24));
        propertiesButton.setMinimumSize(new java.awt.Dimension(24, 24));
        propertiesButton.setPreferredSize(new java.awt.Dimension(24, 24));
        propertiesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                propertiesButtonActionPerformed(evt);
            }
        });

        volumeLabel.setText(bundle.getString("UserListHUDPanel.volumeLabel.text")); // NOI18N

        gotoUserButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/audiomanager/client/resources/UserListGoto24x24.png"))); // NOI18N
        gotoUserButton.setToolTipText(bundle.getString("UserListHUDPanel.gotoUserButton.toolTipText")); // NOI18N
        gotoUserButton.setMaximumSize(new java.awt.Dimension(24, 24));
        gotoUserButton.setMinimumSize(new java.awt.Dimension(24, 24));
        gotoUserButton.setPreferredSize(new java.awt.Dimension(24, 24));
        gotoUserButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gotoUserButtonActionPerformed(evt);
            }
        });

        volumeSlider.setMinorTickSpacing(10);
        volumeSlider.setPaintTicks(true);
        volumeSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                volumeSliderStateChanged(evt);
            }
        });

        org.jdesktop.layout.GroupLayout controlPanelLayout = new org.jdesktop.layout.GroupLayout(controlPanel);
        controlPanel.setLayout(controlPanelLayout);
        controlPanelLayout.setHorizontalGroup(
            controlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(controlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(controlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(controlPanelLayout.createSequentialGroup()
                        .add(muteButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(3, 3, 3)
                        .add(textChatButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(3, 3, 3)
                        .add(voiceChatButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(3, 3, 3)
                        .add(phoneButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(3, 3, 3)
                        .add(gotoUserButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(3, 3, 3)
                        .add(editButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(2, 2, 2)
                        .add(propertiesButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(volumeLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                    .add(volumeSlider, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE))
                .addContainerGap())
        );
        controlPanelLayout.setVerticalGroup(
            controlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(controlPanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(controlPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(propertiesButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(textChatButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(muteButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(voiceChatButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(phoneButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(gotoUserButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(editButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(volumeLabel)
                .add(volumeSlider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0))
        );

        userListScrollPane.setPreferredSize(new java.awt.Dimension(260, 300));

        userList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                userListValueChanged(evt);
            }
        });
        userListScrollPane.setViewportView(userList);

        jPanel1.setMaximumSize(new java.awt.Dimension(32767, 17));
        jPanel1.setMinimumSize(new java.awt.Dimension(100, 17));
        jPanel1.setPreferredSize(new java.awt.Dimension(164, 17));

        panelToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/jdesktop/wonderland/modules/audiomanager/client/resources/upArrow23x10.png"))); // NOI18N
        panelToggleButton.setBorder(null);
        panelToggleButton.setMaximumSize(new java.awt.Dimension(63, 14));
        panelToggleButton.setMinimumSize(new java.awt.Dimension(63, 14));
        panelToggleButton.setPreferredSize(new java.awt.Dimension(63, 14));
        panelToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                panelToggleButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap(181, Short.MAX_VALUE)
                .add(panelToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelToggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(userListScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
            .add(controlPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(userListScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                .add(0, 0, 0)
                .add(controlPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        HUDComponent changeNameHUDComponent = changeNameMap.get(presenceInfo);

        if (changeNameHUDComponent == null) {
            HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");
            ChangeNameHUDPanel changeNameHUDPanel =
                    new ChangeNameHUDPanel(this, pm, presenceInfo);
            final HUDComponent comp =
                    mainHUD.createComponent(changeNameHUDPanel);
            comp.setPreferredLocation(Layout.NORTH);
            comp.setName(BUNDLE.getString("Change_Alias"));
            comp.setIcon(new ImageIcon(getClass().getResource(
                    "/org/jdesktop/wonderland/modules/audiomanager/client/" +
                    "resources/UserListEdit32x32.png")));
            mainHUD.addComponent(comp);
            changeNameMap.put(presenceInfo, comp);

            PropertyChangeListener plistener = new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent pe) {
                    String name = pe.getPropertyName();
                    if (name.equals("ok") || name.equals("cancel")) {
                        comp.setVisible(false);
                    }
                }
            };
            changeNameHUDPanel.addPropertyChangeListener(plistener);
            changeNameHUDComponent = comp;
        }

        changeNameHUDComponent.setVisible(true);
}//GEN-LAST:event_editButtonActionPerformed

    private void propertiesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_propertiesButtonActionPerformed
        if (namePropertiesHUDComponent == null) {
            HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");
            NamePropertiesHUDPanel namePropertiesHUDPanel =
                    new NamePropertiesHUDPanel(pm, session, presenceInfo);
            namePropertiesHUDComponent =
                    mainHUD.createComponent(namePropertiesHUDPanel);
            namePropertiesHUDComponent.setPreferredLocation(Layout.NORTH);
            namePropertiesHUDComponent.setName(
                    BUNDLE.getString("User_Properties"));
            namePropertiesHUDComponent.setIcon(
                    new ImageIcon(getClass().getResource(
                    "/org/jdesktop/wonderland/modules/audiomanager/client/" +
                    "resources/UserListProperties32x32.png")));
            mainHUD.addComponent(namePropertiesHUDComponent);

            PropertyChangeListener plistener = new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent pe) {
                    String name = pe.getPropertyName();
                    if (name.equals("ok") || name.equals("cancel")) {
                        namePropertiesHUDComponent.setVisible(false);
                    }
                }
            };
            namePropertiesHUDPanel.addPropertyChangeListener(plistener);
        }

        namePropertiesHUDComponent.setVisible(true);
}//GEN-LAST:event_propertiesButtonActionPerformed

    private void userListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_userListValueChanged
        Object[] selectedValues = userList.getSelectedValues();

        if (selectedValues.length == 0) {
            editButton.setEnabled(false);
            volumeLabel.setText(BUNDLE.getString("Private_Volume"));
	    volumeSlider.setEnabled(false);
            controlPanel.setVisible(false);
            textChatButton.setEnabled(false);
            voiceChatButton.setEnabled(false);
            gotoUserButton.setEnabled(false);
            panelToggleButton.setIcon(upIcon);
        } else if (selectedValues.length == 1) {
            // one user (self or someone else)
            controlPanel.setVisible(true);
	    volumeSlider.setEnabled(true);
            textChatButton.setEnabled(true);
            panelToggleButton.setIcon(downIcon);

            String username =
                    NameTagNode.getUsername((String) selectedValues[0]);

            PresenceInfo info = pm.getAliasPresenceInfo(username);

            if (info == null) {
                LOGGER.warning("no PresenceInfo for " + username);
                editButton.setEnabled(false);
                return;
            }

            if (isMe(info)) {
                // this user
                volumeLabel.setText(BUNDLE.getString("Master_Volume"));
                editButton.setEnabled(true);
                textChatButton.setEnabled(false);
                voiceChatButton.setEnabled(false);
                gotoUserButton.setEnabled(false);
            } else {
                // another user
                String text = BUNDLE.getString("Private_Volume_For_Single");
                text = MessageFormat.format(text, username);
                volumeLabel.setText(text);
                editButton.setEnabled(false);
                textChatButton.setEnabled(true);
                voiceChatButton.setEnabled(true);
                gotoUserButton.setEnabled(true);
            }

            if (info != null) {
                Integer v = volumeConverter.getVolume(pc.getVolume(info));

		int volume;

		if (v == null) {
		    volume = (volumeSlider.getMaximum() - volumeSlider.getMinimum()) / 2;
		} else {
		    volume = v;
		}
	
		volumeSlider.setValue(volume);
            }
        } else {
            // multiple users
            String text = BUNDLE.getString("Private_Volume_For_Multiple");
            text = MessageFormat.format(text, selectedValues.length);
            volumeLabel.setText(text);
            textChatButton.setEnabled(false);
            voiceChatButton.setEnabled(true);
            panelToggleButton.setIcon(downIcon);
        }
}//GEN-LAST:event_userListValueChanged

private void textChatButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textChatButtonActionPerformed

    // Fetch the currently selected value in the user list. There should only
    // be one. Start a chat with that person, if one does not already exist
    String selectedUser = (String) userList.getSelectedValue();
    if (selectedUser == null) {
        LOGGER.warning("No user selected on chat window");
        return;
    }

    LOGGER.warning("Selected user is " + selectedUser);
    String userName = NameTagNode.getUsername(selectedUser);
    PresenceInfo info = pm.getAliasPresenceInfo(userName);
    WonderlandIdentity id = info.getUserID();
    if (id == null) {
        LOGGER.warning("No ID found for user " + selectedUser);
        return;
    }

    pc.startTextChat(id);
}//GEN-LAST:event_textChatButtonActionPerformed

private void voiceChatButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_voiceChatButtonActionPerformed
    ArrayList<PresenceInfo> usersToInvite = new ArrayList();

    for (Object selectedValue : userList.getSelectedValues()) {
        String username = NameTagNode.getUsername((String) selectedValue);

        PresenceInfo info = pm.getAliasPresenceInfo(username);

        if (info == null) {
            LOGGER.warning("no PresenceInfo for " + username);
            continue;
        }

        if (info.equals(presenceInfo)) {
            /*
             * I'm the caller and will be added automatically
             */
            continue;
        }

        usersToInvite.add(info);
    }

    pc.startVoiceChat(usersToInvite, userListHUDComponent);
}//GEN-LAST:event_voiceChatButtonActionPerformed

private void muteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_muteButtonActionPerformed
    client.toggleMute();
}//GEN-LAST:event_muteButtonActionPerformed

private void phoneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_phoneButtonActionPerformed
    AddHUDPanel addHUDPanel = new AddHUDPanel(client, session, presenceInfo,
            presenceInfo, Mode.INITIATE);

    addHUDPanel.setPhoneType();

    HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");
    addHUDComponent = mainHUD.createComponent(addHUDPanel);
    addHUDComponent.setName(BUNDLE.getString("Call"));
    addHUDComponent.setIcon(new ImageIcon(getClass().getResource(
            "/org/jdesktop/wonderland/modules/audiomanager/client/" +
            "resources/UserListChatVoice32x32.png")));
    addHUDComponent.setPreferredLocation(Layout.CENTER);

    addHUDPanel.setHUDComponent(addHUDComponent);

    mainHUD.addComponent(addHUDComponent);
    addHUDComponent.addEventListener(new HUDEventListener() {

        public void HUDObjectChanged(HUDEvent e) {
            if (e.getEventType().equals(HUDEventType.DISAPPEARED)) {
            }
        }
    });

    PropertyChangeListener plistener = new PropertyChangeListener() {

        public void propertyChange(PropertyChangeEvent pe) {
        }
    };

    addHUDPanel.addPropertyChangeListener(plistener);
    addHUDComponent.setVisible(true);
}//GEN-LAST:event_phoneButtonActionPerformed

private void gotoUserButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gotoUserButtonActionPerformed
    Object[] selectedValues = userList.getSelectedValues();

    if (selectedValues.length == 1) {
        String username =
                NameTagNode.getUsername((String) selectedValues[0]);

        // map the user to a presence info
        PresenceInfo info = pm.getAliasPresenceInfo(username);
        if (info == null) {
            LOGGER.warning("no PresenceInfo for " + username);
            return;
        }

        // get the position of the other user based on their cellID
        Vector3f position = pm.getCellPosition(info.getCellID());
        if (position == null) {
            LOGGER.warning("unable to find location of " + info.getCellID());
        }

        // get the current look direction of the avatar
        ViewCell viewCell = ViewManager.getViewManager().getPrimaryViewCell();
        CellTransform viewTransform = viewCell.getWorldTransform();

        // go to the new location
        try {
            ClientContextJME.getClientMain().gotoLocation(null, position,
                    viewTransform.getRotation(null));
        } catch (IOException ioe) {
            LOGGER.log(Level.WARNING, "Error going to location", ioe);
        }
    }
}//GEN-LAST:event_gotoUserButtonActionPerformed

private void panelToggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_panelToggleButtonActionPerformed
    controlPanel.setVisible(!controlPanel.isVisible());
    if (controlPanel.isVisible()) {
        panelToggleButton.setIcon(downIcon);
    } else {
        panelToggleButton.setIcon(upIcon);
    }
}//GEN-LAST:event_panelToggleButtonActionPerformed

private void volumeSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_volumeSliderStateChanged

	int v = volumeSlider.getValue();

	float volume = volumeConverter.getVolume(v);

        Object[] selectedValues = userList.getSelectedValues();

        if (selectedValues.length > 0) {
            for (int i = 0; i < selectedValues.length; i++) {
                String username =
                        NameTagNode.getUsername((String) selectedValues[i]);

                PresenceInfo info = pm.getAliasPresenceInfo(username);

                if (info == null) {
                    LOGGER.warning("no PresenceInfo for " + username);
                    continue;
                }

                pc.setVolume(info, volume);
            }
	}
}//GEN-LAST:event_volumeSliderStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel controlPanel;
    private javax.swing.JButton editButton;
    private javax.swing.JButton gotoUserButton;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton muteButton;
    private javax.swing.JButton panelToggleButton;
    private javax.swing.JButton phoneButton;
    private javax.swing.JButton propertiesButton;
    private javax.swing.JButton textChatButton;
    private javax.swing.JList userList;
    private javax.swing.JScrollPane userListScrollPane;
    private javax.swing.JButton voiceChatButton;
    private javax.swing.JLabel volumeLabel;
    private javax.swing.JSlider volumeSlider;
    // End of variables declaration//GEN-END:variables
}
