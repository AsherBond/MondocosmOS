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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.hud.CompassLayout.Layout;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDManagerFactory;
import org.jdesktop.wonderland.client.softphone.SoftphoneControlImpl;
import org.jdesktop.wonderland.common.auth.WonderlandIdentity;
import org.jdesktop.wonderland.modules.audiomanager.client.voicechat.AddHUDPanel;
import org.jdesktop.wonderland.modules.audiomanager.client.voicechat.AddHUDPanel.Mode;
import org.jdesktop.wonderland.modules.audiomanager.common.messages.AudioVolumeMessage;
import org.jdesktop.wonderland.modules.presencemanager.client.PresenceManager;
import org.jdesktop.wonderland.modules.presencemanager.common.PresenceInfo;
import org.jdesktop.wonderland.modules.textchat.client.ChatManager;

/**
 * Controls that can be used to initiate common operations on remote avatars,
 * such as starting a text chat, voice chat, or adjusting volume.
 *
 * @author Jonathan Kaplan <jonathankap@gmail.com>
 */
public class PresenceControls {
    private static final Logger LOGGER =
            Logger.getLogger(PresenceControls.class.getName());

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/audiomanager/client/resources/Bundle");

    private final PresenceManager pm;
    private final AudioManagerClient client;
    private final WonderlandSession session;
    private final PresenceInfo me;

    private final Map<PresenceInfo, Float> volumeChangeMap;

    public PresenceControls(AudioManagerClient client,
                            WonderlandSession session,
                            PresenceManager pm,
                            PresenceInfo me)
    {
        this.client = client;
        this.session = session;
        this.pm = pm;
        this.me = me;

        volumeChangeMap = new HashMap<PresenceInfo, Float>();
    }

    public AudioManagerClient getClient() {
        return client;
    }

    public WonderlandSession getSession() {
        return session;
    }

    public PresenceManager getPresenceManager() {
        return pm;
    }

    public PresenceInfo getMe() {
        return me;
    }

    public void startTextChat(WonderlandIdentity remote) {
        ChatManager chatManager = ChatManager.getChatManager();
        String remoteUser = remote.getUsername();
        chatManager.startChat(remoteUser);
    }

    public void startVoiceChat(List<PresenceInfo> usersToInvite,
                               HUDComponent parent)
    {
        AddHUDPanel addHUDPanel = new AddHUDPanel(client, session, me,
                                                  me, Mode.IN_PROGRESS);

        addHUDPanel.inviteUsers(new ArrayList<PresenceInfo>(usersToInvite));

        HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");
        final HUDComponent hudComponent = mainHUD.createComponent(addHUDPanel);
        addHUDPanel.setHUDComponent(hudComponent);
        hudComponent.setName(BUNDLE.getString("Voice_Chat"));
        hudComponent.setIcon(new ImageIcon(getClass().getResource(
                "/org/jdesktop/wonderland/modules/audiomanager/client/"
                + "resources/UserListChatVoice32x32.png")));
        mainHUD.addComponent(hudComponent);
       
        PropertyChangeListener plistener = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent pe) {
                String name = pe.getPropertyName();
                if (name.equals("ok") || name.equals("cancel")) {
                    hudComponent.setVisible(false);
                }
            }
        };

        addHUDPanel.addPropertyChangeListener(plistener);
        hudComponent.setPreferredLocation(Layout.CENTER);
        hudComponent.setVisible(true);

        if (parent != null) {
           addHUDPanel.setLocation(parent.getX() + parent.getWidth(),
                                   parent.getY() + parent.getHeight()
                                   - hudComponent.getHeight());
        }
    }

    public void setVolume(PresenceInfo info, float volume) {
        LOGGER.info("changing volume for " + info.getUserID().getUsername() +
                    " to: " + volume);

        synchronized (volumeChangeMap) {
            volumeChangeMap.put(info, volume);
        }

        SoftphoneControlImpl sc = SoftphoneControlImpl.getInstance();

        session.send(client, new AudioVolumeMessage(info.getCellID(), sc.getCallID(),
                info.getCallID(), volume, true));
    }

    public float getVolume(PresenceInfo info) {
        synchronized (volumeChangeMap) {
            return volumeChangeMap.containsKey(info) ? volumeChangeMap.get(info) : 1.0f;
        }
    }
}
