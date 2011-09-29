/**
 * Open Wonderland
 *
 * Copyright (c) 2010 - 2011, Open Wonderland Foundation, All Rights Reserved
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

/**
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.ProximityComponent;
import org.jdesktop.wonderland.client.cell.Cell.RendererType;
import org.jdesktop.wonderland.client.cell.view.AvatarCell;
import org.jdesktop.wonderland.client.cell.view.LocalAvatar;
import org.jdesktop.wonderland.client.cell.view.LocalAvatar.ViewCellConfiguredListener;
import org.jdesktop.wonderland.client.comms.BaseConnection;
import org.jdesktop.wonderland.client.comms.CellClientSession;
import org.jdesktop.wonderland.client.comms.ConnectionFailureException;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuActionListener;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuEvent;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuInvocationSettings;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuItem;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuItemEvent;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuListener;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuManager;
import org.jdesktop.wonderland.client.contextmenu.SimpleContextMenuItem;
import org.jdesktop.wonderland.client.contextmenu.spi.ContextMenuFactorySPI;
import org.jdesktop.wonderland.client.hud.CompassLayout.Layout;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDEvent;
import org.jdesktop.wonderland.client.hud.HUDEvent.HUDEventType;
import org.jdesktop.wonderland.client.hud.HUDEventListener;
import org.jdesktop.wonderland.client.hud.HUDManagerFactory;
import org.jdesktop.wonderland.client.input.InputManager;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.client.jme.MainFrame;
import org.jdesktop.wonderland.client.jme.ViewManager;
import org.jdesktop.wonderland.client.scenemanager.event.ContextEvent;
import org.jdesktop.wonderland.client.softphone.AudioQuality;
import org.jdesktop.wonderland.client.softphone.SoftphoneControlImpl;
import org.jdesktop.wonderland.client.softphone.SoftphoneListener;
import org.jdesktop.wonderland.common.cell.CellID;
import org.jdesktop.wonderland.common.comms.ConnectionType;
import org.jdesktop.wonderland.common.messages.Message;
import org.jdesktop.wonderland.common.NetworkAddress;
import org.jdesktop.wonderland.modules.audiomanager.client.voicechat.AddHUDPanel;
import org.jdesktop.wonderland.modules.audiomanager.client.voicechat.AddHUDPanel.Mode;
import org.jdesktop.wonderland.modules.audiomanager.client.voicechat.IncomingCallHUDPanel;
import org.jdesktop.wonderland.modules.audiomanager.common.AudioManagerConnectionType;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.audio.CallEndedMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.audio.CallEstablishedMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.audio.CallMigrateMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.audio.CallMutedMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.audio.CallSpeakingMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.ChangeUsernameAliasMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.ConeOfSilenceEnterExitMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.GetPlayersInRangeResponseMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.GetVoiceBridgeRequestMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.GetVoiceBridgeResponseMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.MuteCallRequestMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.PlaceCallRequestMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.PlayerInRangeMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.TransferCallRequestMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.UDPPortTestMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatBusyMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatCallEndedMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatHoldMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatInfoResponseMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatJoinAcceptedMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatJoinRequestMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatLeaveMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatTransientMemberMessage;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.voicechat.VoiceChatMessage.ChatType;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarImiJME;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarNameEvent;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.AvatarNameEvent.EventType;
import org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer.WlAvatarCharacter;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManager;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManagerFactory;
import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;

/**
 *
 * @author jprovino
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class AudioManagerClient extends BaseConnection implements
        AudioMenuListener, SoftphoneListener, ViewCellConfiguredListener
{
    public static final String TABBED_PANEL_PROP =
            "AudioManagerClient.Tabbed.Panel";

    // initial state -- one of "mute" or "unmute", anything else
    // will result in the default behavior, unmuted.
    public static final String AUDIO_STATE_PROP =
            "AudioManagerClient.InitialState";

    // whether or not to show the audio status HUD by default
    public static final String AUDIO_HUD_PROP =
            "AudioManagerClient.ShowStatusHUD";

    private static final Logger logger =
            Logger.getLogger(AudioManagerClient.class.getName());
    private final static ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/audiomanager/client/resources/Bundle");

    // delay (in milliseconds) before releasing push-to-talk. Used by VUMeter
    // panels as well
    static final int PTT_DELAY = 500;

    private WonderlandSession session;
    private boolean connected = true;
    private PresenceManager pm;
    private PresenceInfo presenceInfo;
    private Cell cell;
    
    private PresenceControls controls;
    private ContextMenuListener ctxListener;

    private JMenuItem userListJMenuItem;
    private ArrayList<DisconnectListener> disconnectListeners = new ArrayList();
    private HashMap<String, ArrayList<MemberChangeListener>> memberChangeListeners =
            new HashMap();
    private List<UserInRangeListener> userInRangeListeners =
            Collections.synchronizedList(new ArrayList());
    private HUDComponent userListHUDComponent;
    private UserListPanel userListPanel;
    
    private boolean miniVUMeter = true;
    private HUDComponent vuMeterComponent;
    private HUDComponent vuMeterMiniComponent;
    private final HUDEventListener audioMeterListener;

    private ImageIcon voiceChatIcon;
    private ImageIcon userListIcon;

    private String localAddress;

    private boolean inPTT;
    private PTTReleaseTimer pttReleaseTimer;
    private boolean pttEnabled = true;

    /**
     * Create a new AudioManagerClient
     * @param session the session to connect to, guaranteed to be in
     * the CONNECTED state
     * @throws org.jdesktop.wonderland.client.comms.ConnectionFailureException
     */
    public AudioManagerClient() {
        AudioMenu.getAudioMenu(this).setEnabled(false);

        voiceChatIcon = new ImageIcon(getClass().getResource(
                "/org/jdesktop/wonderland/modules/audiomanager/client/" +
                "resources/UserListChatVoice32x32.png"));
        userListIcon = new ImageIcon(getClass().getResource(
                "/org/jdesktop/wonderland/modules/audiomanager/client/" +
                "resources/GenericUsers32x32.png"));

        userListJMenuItem = new javax.swing.JCheckBoxMenuItem();
        userListJMenuItem.setText(BUNDLE.getString("Users"));
        userListJMenuItem.setSelected(false);
        userListJMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
           		// issue #174 hud visibility management
           		showUsers(evt);
            }
        });
        userListJMenuItem.setEnabled(false);

        ctxListener = new ContextMenuListener() {
            public void contextMenuDisplayed(ContextMenuEvent event) {
                // only deal with invocations on AvatarCell
                if (!(event.getPrimaryCell() instanceof AvatarCell)) {
                    return;
                }

                ContextMenuInvocationSettings settings = event.getSettings();
                AvatarCell cell = (AvatarCell) event.getPrimaryCell();

                // if this is our avatar, add the configuration menu
                if (cell != ViewManager.getViewManager().getPrimaryViewCell()) {
                    settings.addTempFactory(new AudioContextMenuFactory(cell));
                }
            }
        };

        audioMeterListener = new HUDEventListener() {
            public void HUDObjectChanged(HUDEvent event) {
                if (event.getEventType() == HUDEvent.HUDEventType.APPEARED ||
                    event.getEventType() == HUDEvent.HUDEventType.DISAPPEARED)
                {
                    boolean visible = isAudioVolumeVisible();
                    AudioMenu.getAudioMenu(AudioManagerClient.this).audioVolumeVisible(visible);
                }
            }
        };

        logger.fine("Starting AudioManagerCLient");
    }

    public WlAvatarCharacter getWlAvatarCharacter() {
        AvatarImiJME rend =
                (AvatarImiJME) cell.getCellRenderer(RendererType.RENDERER_JME);
        return rend.getAvatarCharacter();
    }

    public void addDisconnectListener(DisconnectListener listener) {
        disconnectListeners.add(listener);
    }

    public void removeDisconnectListener(DisconnectListener listener) {
        disconnectListeners.add(listener);
    }

    private void notifyDisconnectListeners() {
        DisconnectListener[] listeners = disconnectListeners.toArray(new DisconnectListener[0]);
        for (DisconnectListener listener : listeners) {
            listener.disconnected();
        }
    }

    public void addMemberChangeListener(
            String group, MemberChangeListener listener) {
        ArrayList<MemberChangeListener> listeners =
                memberChangeListeners.get(group);

        if (listeners == null) {
            listeners = new ArrayList();
            memberChangeListeners.put(group, listeners);
        }

        listeners.add(listener);
    }

    public void removeMemberChangeListener(
            String group, MemberChangeListener listener) {
        ArrayList<MemberChangeListener> listeners =
                memberChangeListeners.get(group);
        listeners.remove(listener);
    }

    private void notifyMemberChangeListeners(
            String group, PresenceInfo member, boolean added) {

        notifyMemberChangeListeners(group, member, added, false);
    }

    private void notifyMemberChangeListeners(
            String group, PresenceInfo member, boolean added, boolean isTransient) {
        logger.fine("Member change for group " + group +
                " member " + member + " added " + added);
        ArrayList<MemberChangeListener> listeners =
                memberChangeListeners.get(group);

        if (listeners == null) {
            logger.fine("NO LISTENERS!");
            return;
        }

        for (MemberChangeListener listener : listeners) {
            listener.memberChange(member, added, isTransient);
        }
    }

    private void notifyMemberChangeListeners(
            String group, PresenceInfo[] members) {
        ArrayList<MemberChangeListener> listeners =
                memberChangeListeners.get(group);

        if (listeners == null) {
            logger.fine("NO LISTENERS!");
            return;
        }

        for (MemberChangeListener listener : listeners) {
            listener.setMemberList(members);
        }
    }

    public void addUserInRangeListener(UserInRangeListener listener) {
        if (userInRangeListeners.contains(listener)) {
            return;
        }

        userInRangeListeners.add(listener);
    }

    public void removeUserInRangeListener(UserInRangeListener listener) {
        userInRangeListeners.remove(listener);
    }

    public void notifyUserInRangeListeners(PresenceInfo info,
            PresenceInfo userInRange, boolean isInRange) {

        for (UserInRangeListener listener : userInRangeListeners) {
            listener.userInRange(info, userInRange, isInRange);
        }
    }

    public void showUsers(java.awt.event.ActionEvent evt) {
        if (presenceInfo == null) {
            return;
        }

        if (userListHUDComponent == null) {
            HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");
            
            if (Boolean.parseBoolean(System.getProperty(TABBED_PANEL_PROP))) {
                HUDTabbedPanel tabbedPanel = HUDTabbedPanel.getInstance();
                tabbedPanel.configInstance(controls, cell);
                userListHUDComponent = mainHUD.createComponent(tabbedPanel);
                tabbedPanel.setHUDComponent(userListHUDComponent);
                userListPanel = tabbedPanel;
            } else {
                UserListHUDPanel userListHUDPanel = new UserListHUDPanel(controls, cell);
                userListHUDComponent = mainHUD.createComponent(userListHUDPanel);
                userListHUDPanel.setHUDComponent(userListHUDComponent);
                userListPanel = userListHUDPanel;
            }

            userListHUDComponent.setPreferredLocation(Layout.NORTHWEST);
            userListHUDComponent.setName(BUNDLE.getString("Users") + " (0)");
            userListHUDComponent.setIcon(userListIcon);
            
            mainHUD.addComponent(userListHUDComponent);
            userListHUDComponent.addEventListener(new HUDEventListener() {
                // modified for fixing issue #174 hud visibility management
                public void HUDObjectChanged(HUDEvent event) {
                	HUDEventType hudEventType = event.getEventType();
                    if (hudEventType == HUDEventType.CLOSED
                    		|| hudEventType == HUDEventType.MINIMIZED
                    		|| hudEventType == HUDEventType.DISAPPEARED) {
                    	userListJMenuItem.setSelected(false);
                    } else 
                    if (hudEventType == HUDEventType.MAXIMIZED) {
                    	userListJMenuItem.setSelected(true);
                    } 
                }                
            });
        }

        userListPanel.setUserList();
        
        //userListHUDComponent.setVisible(usersMenuSelected);
        if (userListJMenuItem.isSelected()) {
    		userListHUDComponent.setMaximized();
    		userListHUDComponent.setVisible(true);
        } else {
        	userListHUDComponent.setVisible(false);
        }
        
    }

    public void removeDialogs() {
        HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");

        if (userListHUDComponent != null) {
            userListHUDComponent.setVisible(false);
            mainHUD.removeComponent(userListHUDComponent);
            userListHUDComponent = null;
        }

        if (vuMeterComponent != null) {
            vuMeterComponent.setVisible(false);
            mainHUD.removeComponent(vuMeterComponent);
            vuMeterComponent = null;
        }

        if (vuMeterMiniComponent != null) {
            vuMeterMiniComponent.setVisible(false);
            mainHUD.removeComponent(vuMeterMiniComponent);
            vuMeterComponent = null;
        }
    }

    public synchronized void execute(final Runnable r) {
    }

    @Override
    public void connect(WonderlandSession session)
            throws ConnectionFailureException {
        super.connect(session);

        this.session = session;

        pm = PresenceManagerFactory.getPresenceManager(session);

        LocalAvatar avatar = ((CellClientSession) session).getLocalAvatar();
        avatar.addViewCellConfiguredListener(this);
        if (avatar.getViewCell() != null) {
            // if the view is already configured, fake an event
            viewConfigured(avatar);
        }

        SoftphoneControlImpl.getInstance().addSoftphoneListener(this);

        // enable the menus
        AudioMenu.getAudioMenu(this).setEnabled(true);
        userListJMenuItem.setEnabled(true);

	audioProblemJFrame = new AudioProblemJFrame(this);

	connected = true;

        String audioMode = System.getProperty(AUDIO_STATE_PROP, "unmuted");
        if (audioMode.equalsIgnoreCase("mute") ||
            audioMode.equalsIgnoreCase("muted"))
        {
            setMute(true);
        } else {
            setMute(false);
        }

        // add push to talk listeners
        addPTTListeners();

        // show the mini vu meter by default
        boolean showHUD = Boolean.parseBoolean(
                System.getProperty(AUDIO_HUD_PROP, "true"));
        if (showHUD) {
            miniVUMeter();
        }
    }

    @Override
    public void disconnected() {
        super.disconnected();
        HUDTabbedPanel.getInstance().uninitialize();
	connected = false;

        // remove open dialogs
        removeDialogs();

        // remove push-to-talk listeners
        removePTTListeners();

        // TODO: add methods to remove listeners!
        LocalAvatar avatar = ((CellClientSession) session).getLocalAvatar();
        avatar.removeViewCellConfiguredListener(this);

        SoftphoneControlImpl.getInstance().removeSoftphoneListener(this);

	try {
            SoftphoneControlImpl.getInstance().sendCommandToSoftphone("Shutdown");
	} catch (IOException e) {
            logger.warning("Unable to shutdown softphone:  " + e.getMessage());
        }

        //JmeClientMain.getFrame().removeAudioMenuListener(this);
        notifyDisconnectListeners();
    }

    public Cell getCell() {
        return cell;
    }

    public void addMenus() {
        MainFrame mainFrame = JmeClientMain.getFrame();
        mainFrame.addToToolsMenu(AudioMenu.getAudioMenuItem(this), 1);
        mainFrame.addToWindowMenu(userListJMenuItem, 5);

        AudioMenu.getAudioMenu(this).addMenus();

        // make sure menus are up-to-date
        AudioMenu.getAudioMenu(this).mute(isMute());

        ContextMenuManager.getContextMenuManager().addContextMenuListener(ctxListener);
    }

    public void removeMenus() {
        MainFrame mainFrame = JmeClientMain.getFrame();
        mainFrame.removeFromToolsMenu(AudioMenu.getAudioMenuItem(this));
        mainFrame.removeFromWindowMenu(userListJMenuItem);

        AudioMenu.getAudioMenu(this).removeMenus();

        ContextMenuManager.getContextMenuManager().removeContextMenuListener(ctxListener);
    }

    // Context menu factory for avatar context menus
    private class AudioContextMenuFactory
            implements ContextMenuFactorySPI
    {
        private final AvatarCell remote;
        private final PresenceInfo remotePI;

        public AudioContextMenuFactory(AvatarCell remote) {
            this.remote = remote;

            remotePI = pm.getPresenceInfo(remote.getCellID());
        }

        public ContextMenuItem[] getContextMenuItems(ContextEvent event) {
            return new ContextMenuItem[] {
                new SimpleContextMenuItem(BUNDLE.getString("Volume..."),
                        new ContextMenuActionListener()
                {
                    public void actionPerformed(ContextMenuItemEvent event) {
                        VolumeControlJFrame vcjf = 
                                new VolumeControlJFrame(new VolumeChangeListener() 
                        {
                            public void volumeChanged(float volume) {
                                controls.setVolume(remotePI, volume);
                            }
                        }, remote.getIdentity().getUsername() + " " +
                           BUNDLE.getString("Volume"));
                        
                        vcjf.pack();
                        vcjf.setVisible(true);
                    }
                }),

                new SimpleContextMenuItem(BUNDLE.getString("Text_Chat..."),
                        new ContextMenuActionListener()
                {
                    public void actionPerformed(ContextMenuItemEvent event) {
                        controls.startTextChat(remote.getIdentity());
                    }
                }),

                new SimpleContextMenuItem(BUNDLE.getString("Voice_Chat..."),
                        new ContextMenuActionListener()
                {
                    public void actionPerformed(ContextMenuItemEvent event) {
                        controls.startVoiceChat(Collections.singletonList(remotePI),
                                                null);
                    }
                }),
            };
        }
    }

    public void viewConfigured(LocalAvatar localAvatar) {
        cell = localAvatar.getViewCell();
        if (cell == null) {
            logger.severe("TODO - Implement AudioManager.viewConfigured for the case when the primary view cell disconnects");
        } else {
            //System.out.println("LOCAL AVATAR BOUNDS:  " + cell.getLocalBounds());
            CellID cellID = cell.getCellID();

            /*
             * We require the PresenceManager so by the time we get here,
             * our presenceInfo has to be available.
             */
            presenceInfo = pm.getPresenceInfo(cellID);

            /*
             * Now we have everything we need to create the presence
             * controls.
             */
            controls = new PresenceControls(this, session, pm, presenceInfo);

            logger.fine("[AudioManagerClient] view configured for cell " +
                    cellID + " presence: " + presenceInfo + " from " + pm);

            connectSoftphone();

            if (cell.getComponent(ProximityComponent.class) == null) {
                cell.addComponent(new ProximityComponent(cell));
            }

            // OWL issue #140: make sure to show the window on the
            // AWT event thread
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    userListJMenuItem.setSelected(true);
                    showUsers(null);
                }
            });
        }
    }

    public void connectSoftphone() {
	if (presenceInfo == null) {
	    System.out.println("No presence info, can't connect softphone yet...");
	    return;
	}

        logger.fine("[AudioManagerClient] " +
                "Sending message to server to get voice bridge...");

        logger.warning("Sending message to server to get voice bridge... ");

        SoftphoneControlImpl sc = SoftphoneControlImpl.getInstance();
        sendMessage(new GetVoiceBridgeRequestMessage(sc.getCallID()));
    }

    public void showSoftphone() {
        SoftphoneControlImpl sc = SoftphoneControlImpl.getInstance();
        sc.setVisible(!sc.isVisible());
    }

    public void setAudioQuality(AudioQuality audioQuality) {
        SoftphoneControlImpl.getInstance().setAudioQuality(audioQuality);

        logger.info("Set audio quality to " + audioQuality +
                ", now reconnect softphone");
        reconnectSoftphone();
    }

    public void testAudio() {
	try {
            SoftphoneControlImpl.getInstance().runLineTest();
	}  catch (IOException e) {
            logger.warning("Unable to run line test:  " + e.getMessage());
        }
    }

    public void testUDPPort() {
	try {
            SoftphoneControlImpl.getInstance().sendCommandToSoftphone("TestUDPPort");
	}  catch (IOException e) {
            logger.warning("Unable to run UDP port test:  " + e.getMessage());
	    return;
        }
    }
	
    public void reconnectSoftphone() {
        connectSoftphone();
    }
    private CallMigrationForm callMigrationForm;

    public void transferCall() {
        AudioParticipantComponent component =
                cell.getComponent(AudioParticipantComponent.class);

        if (component == null) {
            logger.warning("Can't transfer call:  " +
                    "No AudioParticipantComponent for " + cell.getCellID());
            return;
        }

        if (callMigrationForm == null) {
            callMigrationForm = new CallMigrationForm(this);
        }

        callMigrationForm.setVisible(true);
    }

    public void logAudioProblem() {
	try {
            SoftphoneControlImpl.getInstance().logAudioProblem();
	} catch (IOException e) {
            logger.warning("Unable to log audio problem:  " + e.getMessage());
        }
    }

    public boolean isMute() {
        return isMuted;
    }

    public void toggleMute() {
	setMute(!isMuted);
    }

    public void setMute(boolean isMuted) {
        if (this.isMuted == isMuted) {
	    return;
	}

        SoftphoneControlImpl sc = SoftphoneControlImpl.getInstance();

        String callID = sc.getCallID();

        if (callID == null) {
            // if there is no call id, we still want the UI to be correct,
            // so we fake a call to softphoneMuted() as if the softphone
            // was reporting a mute
            softphoneMuted(isMuted);
            return;
        }

        sc.mute(isMuted);

        sendMessage(new MuteCallRequestMessage(callID, isMuted));
    }

    /**
     * Called when someone has forced us onto mute, and we need to represent
     * that in the UI
     */
    public void forceMute() {
        // mute the softphone
        SoftphoneControlImpl.getInstance().mute(true);

        // update the menu
        AudioMenu.getAudioMenu(this).mute(true);
    }

    private void addPTTListeners() {
        final JComponent canvas = JmeClientMain.getFrame().getCanvas3DPanel();

        InputMap im = canvas.getInputMap(JComponent.WHEN_FOCUSED);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "pttPush");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true), "pttRelease");

        ActionMap am = canvas.getActionMap();
        am.put("pttPush", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                // make sure push-to-talk is enabled
                if (!isPTTEnabled()) {
                    return;
                }

                // if the global entity does not have focus, ignore this event
                // because it is going to a shared app
                KeyEvent ke = new KeyEvent(canvas, KeyEvent.KEY_PRESSED,
                        e.getWhen(), 0, KeyEvent.VK_SPACE, ' ');
                Entity global = InputManager.inputManager().getGlobalFocusEntity();
                if (!InputManager.entityHasFocus(ke, global)) {
                    return;
                }

                // if we are on mute, pressing space triggers a push to talk.
                if (isMute()) {
                    inPTT = true;
                    setMute(false);
                }

                // if there is a pending release, cancel it
                if (pttReleaseTimer != null) {
                    pttReleaseTimer.cancel(true);
                    pttReleaseTimer = null;
                }
            }
        });
        am.put("pttRelease", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                // if we were in push-to-talk mode, start a release timer
                // when space is released. This allows any queued audio
                // to be sent before we go on mute. This also works around
                // key repeat issues on Linux
                if (inPTT && pttReleaseTimer == null) {
                    pttReleaseTimer = new PTTReleaseTimer();
                    pttReleaseTimer.execute();
                }
            }
        });
    }

    private void removePTTListeners() {
        JMenuBar menuBar = JmeClientMain.getFrame().getFrame().getJMenuBar();

        InputMap im = menuBar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        im.remove(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true));
        im.remove(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false));

        ActionMap am = menuBar.getActionMap();
        am.remove("pttPush");
        am.remove("pttRelease");
    }

    private class PTTReleaseTimer extends SwingWorker {
        @Override
        protected Object doInBackground() throws Exception {
            try {
                Thread.sleep(PTT_DELAY);
            } catch (InterruptedException ie) {
                // ignore
            }

            return null;
        }

        @Override
        protected void done() {
            if (inPTT && !isCancelled()) {
                inPTT = false;
                setMute(true);
            }

            // make sure to set the timer to null so it will
            // be reused during the next push-to-talk
            if (pttReleaseTimer == this) {
                pttReleaseTimer = null;
            }
        }
    }

    public boolean isPTTEnabled() {
        return pttEnabled;
    }

    public void setPTTEnabled(boolean pttEnabled) {
        this.pttEnabled = pttEnabled;
    }

    public void personalPhone() {
        voiceChat();
    }

    public void voiceChat() {
        if (presenceInfo == null) {
            return;
        }

        AddHUDPanel addPanel = new AddHUDPanel(
                this, session, presenceInfo, presenceInfo, Mode.INITIATE);

        HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");

        final HUDComponent addComponent = mainHUD.createComponent(addPanel);
        addPanel.setHUDComponent(addComponent);
        addComponent.setPreferredLocation(Layout.CENTER);
        addComponent.setName(BUNDLE.getString("Voice_Chat"));
        addComponent.setIcon(voiceChatIcon);
        mainHUD.addComponent(addComponent);
        addComponent.addEventListener(new HUDEventListener() {

            public void HUDObjectChanged(HUDEvent e) {
                if (e.getEventType().equals(HUDEventType.DISAPPEARED)) {
                }
            }
        });

        PropertyChangeListener plistener = new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent pe) {
                String propName = pe.getPropertyName();
                if (propName.equals("ok") || propName.equals("cancel")) {
                    addComponent.setVisible(false);
                }
            }
        };
        addPanel.addPropertyChangeListener(plistener);
        addComponent.setVisible(true);
    }

    public void softphoneVisible(boolean isVisible) {
    }

    private boolean isMuted = true;

    public void softphoneMuted(boolean isMuted) {
        if (this.isMuted == isMuted) {
            return;
        }

	this.isMuted = isMuted;

        AudioMenu.getAudioMenu(this).mute(isMuted);
    }

    private AudioProblemJFrame audioProblemJFrame;

    public void softphoneConnected(boolean connected) {
        if (connected == false) {
	    softphoneProblem("Softphone Disconnected");
	} else if (isMute()) {
            // sync up mute state
            SoftphoneControlImpl sc = SoftphoneControlImpl.getInstance();

            String callID = sc.getCallID();
            if (callID != null) {
                sc.mute(true);
                sendMessage(new MuteCallRequestMessage(callID, true));
            }
        }
    }

    public void softphoneExited() {
        logger.warning("Softphone exited, reconnect");

        /*
         * If presenceInfo is null, connectSoftphone will be called when
         * the presenceInfo is set.
         */
	showSoftphoneProblem("Softphone Exited, attempting to restart...");
        connectSoftphone();
    }

    public void softphoneProblem(String problem) {
	if (connected == false) {
	    return;
	}

	showSoftphoneProblem(problem);
    }

    private void showSoftphoneProblem(final String problem) {
	Timer timer = new Timer();

	timer.schedule(new TimerTask() {
	    public void run() {
		if (connected == false || getStatus().equals(Status.DISCONNECTED)) {
		    return;
		}

		if (getSession() == null ||
		        getSession().getStatus().equals(Status.DISCONNECTED) == true) {

		    return;
		}

		try {
		    if (SoftphoneControlImpl.getInstance().isConnected()) {
			return;
		    }
		} catch (IOException e) {
		}
			
		audioProblemJFrame.setText(problem);
	    }
	}, 3000);
    }

    public void softphoneTestUDPPort(int port, int duration) {
	sendMessage(new UDPPortTestMessage(localAddress, port, duration));
    }

    public void microphoneGainTooHigh() {
    }

    public void audioVolume() {
        boolean visible = isAudioVolumeVisible();
        if (visible && miniVUMeter) {
            vuMeterMiniComponent.setVisible(false);
        } else if (visible) {
            vuMeterComponent.setVisible(false);
        } else if (miniVUMeter) {
            miniVUMeter();
        } else {
            fullVUMeter();
        }
    }

    public boolean isAudioVolumeVisible() {
        return (vuMeterMiniComponent != null &&
                vuMeterMiniComponent.isVisible()) ||
               (vuMeterComponent != null &&
                vuMeterComponent.isVisible());
    }
    
    public void miniVUMeter() {
        miniVUMeter = true;

        if (vuMeterMiniComponent == null) {
            final VuMeterMiniPanel vuMeterPanel = new VuMeterMiniPanel(this);

            HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");

            vuMeterMiniComponent = mainHUD.createComponent(vuMeterPanel);
            vuMeterMiniComponent.setPreferredLocation(Layout.SOUTHEAST);
            vuMeterMiniComponent.setName(BUNDLE.getString("Microphone_Level"));
            vuMeterMiniComponent.setIcon(voiceChatIcon);
            vuMeterMiniComponent.setDecoratable(false);
            vuMeterMiniComponent.addEventListener(audioMeterListener);
            mainHUD.addComponent(vuMeterMiniComponent);
        }

        if (vuMeterComponent != null) {
            vuMeterComponent.setVisible(false);
        }

        vuMeterMiniComponent.setVisible(true);
    }

    public void fullVUMeter() {
        miniVUMeter = false;

        if (vuMeterComponent == null) {
            final VuMeterPanel vuMeterPanel = new VuMeterPanel(this);

            HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");

            vuMeterComponent = mainHUD.createComponent(vuMeterPanel);
            vuMeterComponent.setPreferredLocation(Layout.SOUTHEAST);
            vuMeterComponent.setName(BUNDLE.getString("Microphone_Level"));
            vuMeterComponent.setIcon(voiceChatIcon);
            vuMeterComponent.addEventListener(new HUDEventListener() {

                public void HUDObjectChanged(HUDEvent event) {
                    switch (event.getEventType()) {
                        case APPEARED:
                            vuMeterPanel.startVuMeter(true);
                            break;
                        case DISAPPEARED:
                            vuMeterPanel.startVuMeter(false);
                            break;
                        default:
                            break;
                    }
                }
            });
            vuMeterComponent.addEventListener(audioMeterListener);
            mainHUD.addComponent(vuMeterComponent);
            vuMeterPanel.startVuMeter(true);
        }

        if (vuMeterMiniComponent != null) {
            vuMeterMiniComponent.setVisible(false);
        }

		// issue #174 hud visibility management
        vuMeterComponent.setMaximized();
              
        vuMeterComponent.setVisible(true);
    }

    public void transferCall(String phoneNumber) {
        sendMessage(new TransferCallRequestMessage(presenceInfo, phoneNumber, false));
    }

    public void cancelCallTransfer() {
        sendMessage(new TransferCallRequestMessage(presenceInfo, "", true));
    }

    @Override
    public void handleMessage(Message message) {
        logger.fine("got a message...");

        if (message instanceof GetPlayersInRangeResponseMessage) {
            GetPlayersInRangeResponseMessage msg =
                    (GetPlayersInRangeResponseMessage) message;

            String[] playersInRange = msg.getPlayersInRange();

            for (int i = 0; i < playersInRange.length; i++) {
                playerInRange(new PlayerInRangeMessage(
                        msg.getPlayerID(), playersInRange[i], true));
            }

            return;
        }

        if (message instanceof GetVoiceBridgeResponseMessage) {
            startSoftphone((GetVoiceBridgeResponseMessage) message);
            return;
        }

        if (message instanceof ChangeUsernameAliasMessage) {
            changeUsernameAlias((ChangeUsernameAliasMessage) message);
            return;
        }

        if (message instanceof VoiceChatJoinRequestMessage) {
            logger.warning("Got VoiceChatJoinRequestMessage");

            final IncomingCallHUDPanel incomingCallHUDPanel =
                    new IncomingCallHUDPanel(this, session, cell.getCellID(),
                    (VoiceChatJoinRequestMessage) message);

            HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");
            final HUDComponent incomingCallHUDComponent =
                    mainHUD.createComponent(incomingCallHUDPanel);
            incomingCallHUDPanel.setHUDComponent(incomingCallHUDComponent);
            incomingCallHUDComponent.setPreferredLocation(Layout.CENTER);
            incomingCallHUDComponent.setIcon(voiceChatIcon);

            mainHUD.addComponent(incomingCallHUDComponent);
            incomingCallHUDComponent.addEventListener(new HUDEventListener() {

                public void HUDObjectChanged(HUDEvent e) {
                    if (e.getEventType().equals(HUDEventType.DISAPPEARED)) {
                        incomingCallHUDPanel.busy();
                    }
                }
            });

            incomingCallHUDComponent.setVisible(true);
            return;
        }

        if (message instanceof VoiceChatBusyMessage) {
            VoiceChatBusyMessage msg = (VoiceChatBusyMessage) message;

            VoiceChatBusyHUDPanel voiceChatBusyHUDPanel =
                    new VoiceChatBusyHUDPanel(msg.getCallee());
            HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");
            HUDComponent voiceChatBusyHUDComponent =
                    mainHUD.createComponent(voiceChatBusyHUDPanel);
            voiceChatBusyHUDPanel.setHUDComponent(voiceChatBusyHUDComponent);
            voiceChatBusyHUDComponent.setPreferredLocation(Layout.CENTER);
            voiceChatBusyHUDComponent.setIcon(voiceChatIcon);

            mainHUD.addComponent(voiceChatBusyHUDComponent);
            voiceChatBusyHUDComponent.addEventListener(new HUDEventListener() {

                public void HUDObjectChanged(HUDEvent e) {
                    if (e.getEventType().equals(HUDEventType.DISAPPEARED)) {
                    }
                }
            });

            voiceChatBusyHUDComponent.setVisible(true);

            notifyMemberChangeListeners(msg.getGroup(), msg.getCallee(), false);
            return;
        }

        if (message instanceof VoiceChatInfoResponseMessage) {
            VoiceChatInfoResponseMessage msg =
                    (VoiceChatInfoResponseMessage) message;
            notifyMemberChangeListeners(msg.getGroup(), msg.getChatters());
            return;
        }

        if (message instanceof VoiceChatJoinAcceptedMessage) {
            joinVoiceChat((VoiceChatJoinAcceptedMessage) message);
            return;
        }

        if (message instanceof VoiceChatHoldMessage) {
            VoiceChatHoldMessage msg = (VoiceChatHoldMessage) message;
            return;
        }

        if (message instanceof VoiceChatLeaveMessage) {
            leaveVoiceChat((VoiceChatLeaveMessage) message);
            return;
        }

        if (message instanceof VoiceChatCallEndedMessage) {
            VoiceChatCallEndedMessage msg = (VoiceChatCallEndedMessage) message;
            voiceChatCallEnded(msg);
            sendMessage(new VoiceChatLeaveMessage(msg.getGroup(), msg.getCallee(),
                    COSName));
            return;
        }

        if (message instanceof VoiceChatTransientMemberMessage) {
            transientMemberAdded((VoiceChatTransientMemberMessage) message);
            return;
        }

        if (message instanceof ConeOfSilenceEnterExitMessage) {
            coneOfSilenceEnterExit((ConeOfSilenceEnterExitMessage) message);
            return;
        }

        if (message instanceof PlayerInRangeMessage) {
            playerInRange((PlayerInRangeMessage) message);
            return;
        }

        if (message instanceof CallEstablishedMessage) {
            if (callMigrationForm != null) {
                callMigrationForm.setStatus("Migrated");
            }

            return;
        }

        if (message instanceof CallMigrateMessage) {
            callMigrate((CallMigrateMessage) message);
            return;
        }

        if (message instanceof CallMutedMessage) {
            callMuted((CallMutedMessage) message);
            return;
        }

        if (message instanceof CallSpeakingMessage) {
            callSpeaking((CallSpeakingMessage) message);
            return;
        }

        if (message instanceof CallEndedMessage) {
            callEnded((CallEndedMessage) message);
            return;
        }

        logger.warning("Unknown message " + message);

        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void startSoftphone(GetVoiceBridgeResponseMessage msg) {
        logger.warning("Got voice bridge " + msg.getBridgeInfo());

        String phoneNumber = System.getProperty(
                "org.jdesktop.wonderland.modules.audiomanager.client.PHONE_NUMBER");

        System.setProperty(
                "org.jdesktop.wonderland.modules.audiomanager.client.PHONE_NUMBER", "");

        if (phoneNumber != null && phoneNumber.length() > 0) {
            sendMessage(new PlaceCallRequestMessage(presenceInfo, phoneNumber,
                    0., 0., 0., 90., false));
            return;
        }

        SoftphoneControlImpl sc = SoftphoneControlImpl.getInstance();

        /*
         * The voice bridge info is a String of values separated by ":".
         * The numbers indicate the index in tokens[].
         *
         *     0      1      2                   3                 4
         * <bridgeId>::<privateHostName>:<privateControlPort>:<privateSipPort>
         *			 5                   6                 7
         *   	  :<publicHostName>:<publicControlPort>:<publicSipPort>
         */
        String tokens[] = msg.getBridgeInfo().split(":");

        String registrarAddress = tokens[5] + ";sip-stun:";

        registrarAddress += tokens[7];

        localAddress = null;

        try {
            InetAddress ia = NetworkAddress.getPrivateLocalAddress(
                    "server:" + tokens[5] + ":" + tokens[7] + ":10000");

            localAddress = ia.getHostAddress();
        } catch (UnknownHostException e) {
            logger.warning(e.getMessage());

            logger.warning("The client is unable to connect to the bridge " +
                    "public address. Trying InetAddress.getLocalHost().");

            try {
                //InetAddress ia = NetworkAddress.getPrivateLocalAddress(
                //        "server:" + tokens[2] + ":" + tokens[4] + ":10000");

                //localAddress = ia.getHostAddress();
		localAddress = InetAddress.getLocalHost().getHostAddress();

		if (localAddress.startsWith("127")) {
		    logger.warning("local address is " + localAddress
			+ ".  This can only work if the server is running locally."); 
		}
            } catch (UnknownHostException ee) {
                logger.warning(ee.getMessage());
            }
        }

        if (localAddress != null) {
            try {
                String sipURL = sc.startSoftphone(presenceInfo.getUserID().getUsername(), 
		    registrarAddress, 10, localAddress);

                logger.fine("Starting softphone:  " + presenceInfo);

                if (sipURL != null) {
                    // XXX need location and direction
                    sendMessage(new PlaceCallRequestMessage(
                            presenceInfo, sipURL, 0., 0., 0., 90., false));
                } else {
                    logger.warning("Failed to start softphone, retrying.");

                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                    }

                    connectSoftphone();
                }
            } catch (IOException e) {
                logger.warning(e.getMessage());
            }
        } else {
            // XXX Put up a dialog box here
            logger.warning("LOCAL ADDRESS IS NULL.  " +
                "AUDIO WILL NOT WORK!!!!!!!!!!!!");
            /*
             * Try again.
             */
            connectSoftphone();
        }
    }

    private void changeUsernameAlias(ChangeUsernameAliasMessage msg) {
        PresenceInfo info = msg.getPresenceInfo();

        pm.changeUsernameAlias(info, info.getUsernameAlias());

        AvatarNameEvent avatarNameEvent = new AvatarNameEvent(
                EventType.CHANGE_NAME, info.getUserID().getUsername(),
                info.getUsernameAlias());

        InputManager.inputManager().postEvent(avatarNameEvent);
    }

    private void joinVoiceChat(VoiceChatJoinAcceptedMessage msg) {
        logger.fine("GOT JOIN ACCEPTED MESSAGE FOR " + msg.getCallee());

        PresenceInfo info = pm.getPresenceInfo(msg.getCallee().getCallID());

        logger.fine(
                "GOT JOIN ACCEPTED FOR " + msg.getCallee() + " info " + info);

        if (info == null) {
            info = msg.getCallee();

            logger.warning("adding pm for " + info);
            pm.addLocalPresenceInfo(info);
        }

        if (msg.getChatType() == ChatType.SECRET) {
            info.setInSecretChat(true);
        } else {
            info.setInSecretChat(false);
        }

        notifyMemberChangeListeners(msg.getGroup(), info, true);
    }

    private void leaveVoiceChat(VoiceChatLeaveMessage msg) {
        PresenceInfo callee = msg.getCallee();

        logger.info("GOT LEAVE MESSAGE FOR " + callee);

        notifyMemberChangeListeners(msg.getGroup(), callee, false);

        if (callee.getClientID() == null) {
            pm.removeLocalPresenceInfo(callee);	// it's an outworlder
        }
    }

    private void voiceChatCallEnded(VoiceChatCallEndedMessage msg) {
        PresenceInfo callee = msg.getCallee();

        String reason = getUserFriendlyReason(msg.getReasonCallEnded());

        logger.warning("Call ended for " + callee + " Reason:  " + reason);

        if (!reason.equalsIgnoreCase("Hung up") &&
                !reason.equalsIgnoreCase("User requested call termination")) {
            callEnded(callee, reason);
        }

        if (callee.getClientID() == null) {
            pm.removeLocalPresenceInfo(callee);	// it's an outworlder
        }

        notifyMemberChangeListeners(msg.getGroup(), callee, false);
    }
    private String COSName;

    public String getCOSName() {
        return COSName;
    }

    private void coneOfSilenceEnterExit(ConeOfSilenceEnterExitMessage msg) {
        PresenceInfo info = pm.getPresenceInfo(msg.getCallID());

        if (info == null) {
            logger.warning("No presence info for " + msg.getCallID());
            return;
        }

        pm.setEnteredConeOfSilence(info, msg.entered());

        AvatarNameEvent avatarNameEvent;

        if (msg.entered()) {
            COSName = msg.getCOSName();

            avatarNameEvent = new AvatarNameEvent(
                    EventType.ENTERED_CONE_OF_SILENCE,
                    info.getUserID().getUsername(), info.getUsernameAlias());
        } else {
            COSName = null;

            avatarNameEvent = new AvatarNameEvent(
                    EventType.EXITED_CONE_OF_SILENCE,
                    info.getUserID().getUsername(), info.getUsernameAlias());
        }

        InputManager.inputManager().postEvent(avatarNameEvent);
    }

    private void callMigrate(CallMigrateMessage msg) {
        if (callMigrationForm == null) {
            return;
        }

        if (msg.isSuccessful()) {
            callMigrationForm.setStatus("Migrated");
        } else {
            callMigrationForm.setStatus("Migration failed");
        }
    }

    private void callMuted(CallMutedMessage msg) {
        PresenceInfo info = pm.getPresenceInfo(msg.getCallID());

        if (info == null) {
            logger.fine("No presence info for " + msg.getCallID());
            return;
        }

        pm.setMute(info, msg.isMuted());

        AvatarNameEvent avatarNameEvent;

        if (msg.isMuted()) {
            avatarNameEvent = new AvatarNameEvent(EventType.MUTE,
                    info.getUserID().getUsername(), info.getUsernameAlias());
        } else {
            avatarNameEvent = new AvatarNameEvent(EventType.UNMUTE,
                    info.getUserID().getUsername(), info.getUsernameAlias());
        }

        InputManager.inputManager().postEvent(avatarNameEvent);
    }

    private void callSpeaking(CallSpeakingMessage msg) {
        PresenceInfo info = pm.getPresenceInfo(msg.getCallID());

        if (info == null) {
            // Issue #1113: ignore this error
            logger.fine("No presence info for " + msg.getCallID());
            return;
        }

        logger.fine("Speaking " + msg.isSpeaking() + " " + info);

        pm.setSpeaking(info, msg.isSpeaking());

        AvatarNameEvent avatarNameEvent;

        if (msg.isSpeaking()) {
            avatarNameEvent = new AvatarNameEvent(EventType.STARTED_SPEAKING,
                    info.getUserID().getUsername(), info.getUsernameAlias());
        } else {
            avatarNameEvent = new AvatarNameEvent(EventType.STOPPED_SPEAKING,
                    info.getUserID().getUsername(), info.getUsernameAlias());
        }

        InputManager.inputManager().postEvent(avatarNameEvent);
    }

    private String getUserFriendlyReason(String reason) {
        if (reason.indexOf("Not Found") >= 0) {
            return "Invalid phone number";
        }

        if (reason.indexOf("No voip Gateway!") >= 0) {
            return "No connection to phone system";
        }

        return reason;
    }

    private void callEnded(CallEndedMessage msg) {
        PresenceInfo info = pm.getPresenceInfo(msg.getCallID());

        if (info != null && info.getClientID() == null) {
            pm.removeLocalPresenceInfo(info);	// it's an outworlder
        }

        String callID = msg.getCallID();

        if (!callID.equals(SoftphoneControlImpl.getInstance().getCallID())) {
            return;
        }

        if (callMigrationForm == null) {
            return;
        }

        String reason = getUserFriendlyReason(msg.getReason());

        if (!reason.equals("User requested call termination") &&
                reason.indexOf("migrated") < 0) {

            callMigrationForm.setStatus("Call ended:  " + reason);
        }
    }

    private void callEnded(PresenceInfo callee, String reason) {
        CallEndedHUDPanel callEndedHUDPanel =
                new CallEndedHUDPanel(callee, reason);
        HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");
        HUDComponent callEndedHUDComponent =
                mainHUD.createComponent(callEndedHUDPanel);
        callEndedHUDPanel.setHUDComponent(callEndedHUDComponent);
        callEndedHUDComponent.setPreferredLocation(Layout.CENTER);
        callEndedHUDComponent.setIcon(voiceChatIcon);

        mainHUD.addComponent(callEndedHUDComponent);
        callEndedHUDComponent.addEventListener(new HUDEventListener() {

            public void HUDObjectChanged(HUDEvent e) {
                if (e.getEventType().equals(HUDEventType.DISAPPEARED)) {
                }
            }
        });

        callEndedHUDComponent.setVisible(true);
    }

    private void playerInRange(PlayerInRangeMessage message) {
        String playerID = message.getPlayerID();
        String playerInRangeID = message.getPlayerInRangeID();
        boolean inRange = message.isInRange();
        logger.info("Player in range " + inRange + " " +
                playerID + " player in range " + playerInRangeID);

        PresenceInfo info = pm.getPresenceInfo(playerID);

        if (info == null) {
            logger.info("No PresenceInfo for " + playerID);
            return;
        }

        PresenceInfo userInRangeInfo = pm.getPresenceInfo(playerInRangeID);

        if (userInRangeInfo == null) {
            logger.info("No PresenceInfo for user in range " + playerInRangeID);
            return;
        }

        notifyUserInRangeListeners(info, userInRangeInfo, inRange);
        return;
    }

    private void transientMemberAdded(VoiceChatTransientMemberMessage message) {
        PresenceInfo info = pm.getPresenceInfo(message.getCallID());

        if (info == null) {
            logger.warning("No presence info for callID " + message.getCallID());
            return;
        }

        notifyMemberChangeListeners(message.getGroup(), info, message.getIsAdded(), true);
    }

    private void sendMessage(Message message) {
        if (session.getStatus() != WonderlandSession.Status.CONNECTED) {
            logger.warning("Not connected, can't send " + message);
            return;
        }

        session.send(this, message);
    }

    public ConnectionType getConnectionType() {
        return AudioManagerConnectionType.CONNECTION_TYPE;
    }
}
