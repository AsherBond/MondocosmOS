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

import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatMessage.ChatType;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.hud.CompassLayout.Layout;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDEvent;
import org.jdesktop.wonderland.client.hud.HUDEvent.HUDEventType;
import org.jdesktop.wonderland.client.hud.HUDEventListener;
import org.jdesktop.wonderland.client.hud.HUDManagerFactory;
import org.jdesktop.wonderland.modules.audiomanager.client.AudioManagerClient;
import org.jdesktop.wonderland.modules.audiomanager.client.DisconnectListener;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatHoldMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatLeaveMessage;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManager;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManagerFactory;
import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;

/**
 *
 * @author nsimpson
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class AddHUDPanel
        extends javax.swing.JPanel implements DisconnectListener {

    public enum Mode {
        ADD, INITIATE, IN_PROGRESS, HOLD
    };

    public Mode mode;
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/audiomanager/client/resources/Bundle");
    private static final Logger LOGGER =
            Logger.getLogger(AddHUDPanel.class.getName());
    private AddTypePanel addTypePanel;
    private AddUserPanel addUserPanel;
    private AddPhoneUserPanel addPhoneUserPanel;
    private HoldPanel holdPanel;
    private AddInitiateButtonPanel addInitiateButtonPanel;
    private InProgressButtonPanel inProgressButtonPanel;
    private AudioManagerClient client;
    private WonderlandSession session;
    private PresenceManager pm;
    private PresenceInfo myPresenceInfo;
    private PresenceInfo caller;
    private String group;
    private static int groupNumber;
    private static ArrayList<AddHUDPanel> addHUDPanelList = new ArrayList();
    private PropertyChangeSupport listeners;
    private HUDComponent addHUDComponent;
    private int normalHeight = 0;

    public AddHUDPanel() {
        initComponents();
        setMode(Mode.INITIATE);
    }

    public AddHUDPanel(AudioManagerClient client, WonderlandSession session,
            PresenceInfo myPresenceInfo, PresenceInfo caller, Mode mode) {

        this(client, session, myPresenceInfo, caller, null, mode);
    }

    public AddHUDPanel(AudioManagerClient client, WonderlandSession session,
            PresenceInfo myPresenceInfo, PresenceInfo caller, String group,
            Mode mode) {

        this.client = client;
        this.session = session;
        this.myPresenceInfo = myPresenceInfo;
        this.caller = caller;

        if (group == null) {
            group = caller.getUserID().getUsername() + "-" + groupNumber++;
        }

        this.group = group;

        //System.out.println("NEW HUD For " + group);

        initComponents();

        setMode(mode);

        setEnabledInviteButton();
        setEnabledActionButton();

        pm = PresenceManagerFactory.getPresenceManager(session);

        addHUDPanelList.add(this);

        client.addDisconnectListener(this);
    }

    public void setHUDComponent(HUDComponent addHUDComponent) {
        this.addHUDComponent = addHUDComponent;

        addHUDComponent.addEventListener(new HUDEventListener() {

            public void HUDObjectChanged(HUDEvent e) {
		//System.out.println("GOT EVENT " + e);

                if (mode.equals(Mode.IN_PROGRESS) && e.getEventType().equals(HUDEventType.CLOSED)) {
                    leave();
                }
            }
        });
    }

    public HUDComponent getHUDComponent() {
        return addHUDComponent;
    }

    public void setPreferredLocation(Layout location) {
        if (addHUDPanelList.size() == 1) {
            addHUDComponent.setPreferredLocation(location);
            return;
        }

        setLocation(0, 0);
    }

    @Override
    public void setLocation(int x, int y) {
        for (AddHUDPanel addHUDPanel : addHUDPanelList) {
            if (addHUDPanel == this) {
                continue;
            }

            HUDComponent hudComponent = addHUDPanel.getHUDComponent();

            Point p = hudComponent.getLocation();

            //System.out.println("x " + x + " y " + y + " Location " + p + " width " + addHUDComponent.getWidth());

            if (p.getX() >= x) {
                x = (int) (p.getX() + hudComponent.getWidth());
                y = (int) p.getY();
            }
        }

        addHUDComponent.setLocation(x, y);
    }

    public void setPhoneType() {
        addTypePanel.setPhoneType();
        showAddPhonePanel(true, true);
        addInitiateButtonPanel.setEnabledActionButton(false);
        addInitiateButtonPanel.setActionButtonText(BUNDLE.getString("Call"));
        userMode = false;
    }

    public void inviteUsers(ArrayList<PresenceInfo> usersToInvite) {
        addUserPanel.inviteUsers(usersToInvite);
        setMode(Mode.IN_PROGRESS);
    }

    public void setClosed() {
        addHUDComponent.setClosed();
    }

    public void setPrivacy(ChatType chatType) {
	if (addUserPanel != null) {
	    addUserPanel.setPrivacy(chatType);
	}
    }

    public void disconnected() {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                addHUDComponent.setVisible(false);
            }
        });
    }

    /**
     * Adds a bound property listener to the dialog
     * @param listener a listener for dialog events
     */
    @Override
    public synchronized void addPropertyChangeListener(
            PropertyChangeListener listener) {
        if (listeners == null) {
            listeners = new PropertyChangeSupport(this);
        }
        listeners.addPropertyChangeListener(listener);
    }

    /**
     * Removes a bound property listener from the dialog
     * @param listener the listener to remove
     */
    @Override
    public synchronized void removePropertyChangeListener(
            PropertyChangeListener listener) {
        if (listeners != null) {
            listeners.removePropertyChangeListener(listener);
        }
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        switch (mode) {
            case INITIATE:
                setInitiateMode();
                break;
            case ADD:
                setAddUserMode();
                break;
            case IN_PROGRESS:
                setInProgressMode();
                break;
            case HOLD:
                setHoldMode();
                break;
        }
    }
    private boolean userMode = true;

    private void showAddType(boolean show) {
        if (addTypePanel == null) {
            addTypePanel = new AddTypePanel();
            addTypePanel.addUserModeListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    showAddUserPanel(true, (mode != Mode.ADD));
                    addInitiateButtonPanel.setActionButtonText(
                            BUNDLE.getString("Invite"));
                    setEnabledInviteButton();
                    userMode = true;
                }
            });
            addTypePanel.addPhoneModeListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    showAddPhonePanel(true, (mode != Mode.ADD));
                    addInitiateButtonPanel.setActionButtonText(
                            BUNDLE.getString("Call"));
                    setEnabledActionButton();
                    userMode = false;
                }
            });
        }
        addTypePanel.setVisible(show);
        if (show) {
            add(addTypePanel, BorderLayout.NORTH);
        }
    }

    private void showAddUserPanel(boolean showPanel, boolean showPrivacy) {
        if (addPhoneUserPanel != null) {
            addPhoneUserPanel.setVisible(false);
        }

        if (addUserPanel == null) {
            addUserPanel = new AddUserPanel(
                    this, client, session, myPresenceInfo, caller, group);

            addUserPanel.addUserListSelectionListener(
                    new javax.swing.event.ListSelectionListener() {

                        public void valueChanged(ListSelectionEvent e) {
                            addUserListValueChanged(e);
                        }
                    });
        }

        addUserPanel.setVisible(showPanel);

        if (showPanel) {
            add(addUserPanel, BorderLayout.CENTER);
        }

        addUserPanel.showPrivacyPanel(showPrivacy);
    }

    private void showAddPhonePanel(boolean showPanel, boolean showPrivacy) {
        if (addUserPanel != null) {
            addUserPanel.setVisible(false);
        }

        if (addPhoneUserPanel == null) {
            addPhoneUserPanel = new AddPhoneUserPanel();

            addPhoneUserPanel.addNameTextActionListener(new ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    phoneTextActionPerformed(evt);
                }
            });

            addPhoneUserPanel.addPhoneTextActionListener(new ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent e) {
                    phoneTextActionPerformed(e);
                }
            });

            addPhoneUserPanel.addNameTextKeyReleasedListener(new KeyAdapter() {

                @Override
                public void keyReleased(java.awt.event.KeyEvent e) {
                    setEnabledActionButton();
                }
            });
            addPhoneUserPanel.addPhoneTextKeyReleasedListener(new KeyAdapter() {

                @Override
                public void keyReleased(java.awt.event.KeyEvent e) {
                    setEnabledActionButton();
                }
            });
        }

        addPhoneUserPanel.setVisible(showPanel);

        if (showPanel) {
            add(addPhoneUserPanel, BorderLayout.CENTER);
        }
        addPhoneUserPanel.showPrivacyPanel(showPrivacy);
    }

    private void showHoldPanel(boolean showPanel) {
        if (holdPanel == null) {
            holdPanel = new HoldPanel();

            holdPanel.addHoldListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    takeOffHold();
                }
            });

            holdPanel.addVolumeChangeListener(new ChangeListener() {

                public void stateChanged(javax.swing.event.ChangeEvent evt) {
                    javax.swing.JSpinner holdVolumeSpinner = (javax.swing.JSpinner) evt.getSource();

                    setHoldVolume((Float) holdVolumeSpinner.getValue());
                }
            });
        }

        holdPanel.setVisible(showPanel);

        if (showPanel) {
            add(holdPanel, BorderLayout.NORTH);
            if (normalHeight == 0) {
                normalHeight = addHUDComponent.getHeight();
            }
	    /*
	     * FIX ME:  setting the height confuses the mouse listener
	     * and clicking on the Take Off Hold button doesn't work.
	     */
            //addHUDComponent.setHeight(holdPanel.getPreferredSize().height);
        }
    }

    private void showInitiateButtons(boolean show) {
        if (addInitiateButtonPanel == null) {
            addInitiateButtonPanel = new AddInitiateButtonPanel();

            addInitiateButtonPanel.addActionButtonListener(
                    new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            actionButtonActionPerformed();
                        }
                    });

            addInitiateButtonPanel.addCancelButtonListener(
                    new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            cancelButtonActionPerformed(e);
                        }
                    });
        }
        addInitiateButtonPanel.setVisible(show);
        if (show) {
            add(addInitiateButtonPanel, BorderLayout.SOUTH);
        }
    }

    private void actionButtonActionPerformed() {
        if (userMode) {
            addUserPanel.inviteUsers();
        } else {
            /*
             * Phone Mode
             */
            String name = addPhoneUserPanel.getPhoneName();

            PresenceInfo[] info = pm.getAllUsers();

            for (int i = 0; i < info.length; i++) {
                if (info[i].getUsernameAlias().equals(name) ||
                        info[i].getUserID().getUsername().equals(name)) {

                    addPhoneUserPanel.setStatusMessage(
                            BUNDLE.getString("Name_Used"));
                    return;
                }
            }

            addUserPanel.callUser(name, addPhoneUserPanel.getPhoneNumber());

            if (mode.equals(Mode.ADD)) {
                addHUDComponent.setVisible(false);
                addHUDComponent.setClosed();
            }
        }

        if (mode.equals(Mode.INITIATE)) {
            setMode(Mode.IN_PROGRESS);
        }
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        addHUDComponent.setVisible(false);
    }

    private void showInProgressButtons(boolean show) {
        if (inProgressButtonPanel == null) {
            inProgressButtonPanel = new InProgressButtonPanel();

            inProgressButtonPanel.addAddButtonListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    addButtonActionPerformed(e);
                }
            });

            inProgressButtonPanel.addHangUpButtonListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    hangup(e);
                }
            });

            inProgressButtonPanel.addHoldButtonListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    setMode(Mode.HOLD);
                }
            });

            inProgressButtonPanel.addLeaveButtonListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    leave();
                }
            });
        }
        inProgressButtonPanel.setVisible(show);
        if (show) {
            add(inProgressButtonPanel, BorderLayout.SOUTH);
        }
    }
    private HUDComponent addModeAddHUDComponent;

    private void addButtonActionPerformed(ActionEvent e) {
        AddHUDPanel addHUDPanel = new AddHUDPanel(client, session,
                myPresenceInfo, myPresenceInfo, group, Mode.ADD);

	addHUDPanel.setPrivacy(addUserPanel.getPrivacy());

        HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");
        addModeAddHUDComponent = mainHUD.createComponent(addHUDPanel);
        addHUDPanel.setHUDComponent(addModeAddHUDComponent);

        addModeAddHUDComponent.setName("Add to Voice Chat");

        addHUDPanel.setPreferredLocation(Layout.EAST);

        mainHUD.addComponent(addModeAddHUDComponent);

        inProgressButtonPanel.setEnabledAddButton(false);

        addModeAddHUDComponent.addEventListener(new HUDEventListener() {

            public void HUDObjectChanged(HUDEvent e) {
                if (e.getEventType().equals(HUDEventType.DISAPPEARED)) {
                    inProgressButtonPanel.setEnabledAddButton(true);
                }
            }
        });

        PropertyChangeListener plistener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent pe) {
                String name = pe.getPropertyName();
                if (name.equals("ok") || name.equals("cancel")) {
                    addModeAddHUDComponent.setVisible(false);
                }
            }
        };

        addHUDPanel.addPropertyChangeListener(plistener);
        addModeAddHUDComponent.setVisible(true);
    }

    private void leave() {
        session.send(client, new VoiceChatLeaveMessage(group, myPresenceInfo,
	    client.getCOSName()));
        addHUDComponent.setVisible(false);
        addHUDPanelList.remove(this);

        if (addModeAddHUDComponent != null) {
            addModeAddHUDComponent.setVisible(false);
        }

	CallAnimator.stopCallAnswerAnimation(client);
    }

    private void hangup(ActionEvent e) {
        addUserPanel.hangup();
    }

    private void setInitiateMode() {
        clearPanel();
        showAddType(true);
        showAddUserPanel(true, true);
        showInitiateButtons(true);
    }

    private void setAddUserMode() {
        clearPanel();
        showAddType(true);
        showAddUserPanel(true, false);
        showInitiateButtons(true);
    }

    private void setInProgressMode() {
        clearPanel();
        showAddUserPanel(true, true);
        showInProgressButtons(true);
        holdOtherCalls();

	if (addUserPanel.getPrivacy().equals(ChatType.PRIVATE)) {
	   CallAnimator.animateCallAnswer(client);
	   
	   String COSName = client.getCOSName();

	   if (COSName != null) {
                session.send(client,
                    new VoiceChatHoldMessage(group, myPresenceInfo, false, 1, COSName));
	   }
	} else {
	   CallAnimator.stopCallAnswerAnimation(client);
	}
    }

    private void setHoldMode() {
        clearPanel();
        showHoldPanel(true);

        float volume = holdPanel.getHoldVolume();

        try {
            session.send(client, new VoiceChatHoldMessage(group, myPresenceInfo,
                    true, volume, client.getCOSName()));
        } catch (IllegalStateException e) {
            leave();
        }

	CallAnimator.stopCallAnswerAnimation(client);
    }

    private void setHoldVolume(float volume) {
        try {
            session.send(client, new VoiceChatHoldMessage(group, myPresenceInfo,
		true, volume, client.getCOSName()));
        } catch (IllegalStateException e) {
            leave();
        }
    }

    private void holdOtherCalls() {
        for (AddHUDPanel addHUDPanel : addHUDPanelList) {
            if (addHUDPanel == this) {
                continue;
            }

            if (addHUDPanel.getMode().equals(Mode.IN_PROGRESS)) {
                addHUDPanel.setMode(Mode.HOLD);
            }
        }
    }

    private void takeOffHold() {
        try {
            session.send(client,
                    new VoiceChatHoldMessage(group, myPresenceInfo, false, 1, client.getCOSName()));
            setMode(Mode.IN_PROGRESS);
        } catch (IllegalStateException e) {
            leave();
        }
    }

    private void clearPanel() {
        Component[] components = getComponents();
        for (int c = 0; c < components.length; c++) {
            components[c].setVisible(false);
        }
        validate();
        if ((normalHeight > 0) && (addHUDComponent != null)) {
            // restore dialog to the normal height if was in HOLD mode
            addHUDComponent.setHeight(normalHeight);
        }
    }

    private void addUserListValueChanged(ListSelectionEvent e) {
        ArrayList<PresenceInfo> selectedValues =
                addUserPanel.getSelectedValues();

        setEnabledInviteButton();
        setEnabledActionButton();

        if (inProgressButtonPanel == null) {
            return;
        }

        for (PresenceInfo info : selectedValues) {
            if (info.getClientID() != null) {
                if (inProgressButtonPanel != null) {
                    inProgressButtonPanel.setEnabledHangUpButton(false);
                }
                return;
            }
        }

        inProgressButtonPanel.setEnabledHangUpButton(true);
    }

    private void phoneTextActionPerformed(java.awt.event.ActionEvent e) {
        if (addPhoneUserPanel.getPhoneName().length() > 0 &&
                addPhoneUserPanel.getPhoneNumber().length() > 0) {

            actionButtonActionPerformed();
        }
    }

    private void setEnabledInviteButton() {
        if (addInitiateButtonPanel == null) {
            return;
        }

        addInitiateButtonPanel.setEnabledActionButton(
                addUserPanel.getSelectedValues().size() > 0);
    }

    private void setEnabledActionButton() {
        if (addInitiateButtonPanel == null || addPhoneUserPanel == null) {
            return;
        }

        boolean isEnabled = addPhoneUserPanel.getPhoneName().length() > 0 &&
                addPhoneUserPanel.getPhoneNumber().length() > 0;

        addInitiateButtonPanel.setEnabledActionButton(isEnabled);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addTypeButtonGroup = new javax.swing.ButtonGroup();

        setPreferredSize(new java.awt.Dimension(322, 150));
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup addTypeButtonGroup;
    // End of variables declaration//GEN-END:variables
}
