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
package org.jdesktop.wonderland.modules.textchat.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.SwingUtilities;
import org.jdesktop.wonderland.client.comms.ConnectionFailureException;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.hud.CompassLayout.Layout;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDEvent;
import org.jdesktop.wonderland.client.hud.HUDEvent.HUDEventType;
import org.jdesktop.wonderland.client.hud.HUDEventListener;
import org.jdesktop.wonderland.client.hud.HUDManagerFactory;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.modules.textchat.client.TextChatConnection.TextChatListener;

/**
 * Manages all of the Text Chat windows for the client.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class ChatManager implements TextChatListener {

    private static final Logger LOGGER =
            Logger.getLogger(ChatManager.class.getName());
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/textchat/client/resources/Bundle");
    // A mapping from named text chats to HUD components, where the key is the
    // name of the remote person involved in the text chat and the value is a
    // weak reference to the HUD panel displaying the chat. NOTE: Accesses to
    // this Map must by synchronized, and only takes place on the AWT Event
    // Thread.
    private Map<String, WeakReference<HUDComponent>> textChatHUDRefMap;
    // A further mapping from a text chat HUD component to the underlying
    // TextChatPanel. NOTE: Accesses to this Map must by synchronized, and only
    // takes place on the AWT Event Thread.
    private Map<HUDComponent, WeakReference<TextChatPanel>> textChatPanelRefMap;
    private JCheckBoxMenuItem textChatMenuItem;
    private TextChatConnection textChatConnection;
    private String localUserName;

    /**
     * Singleton to hold instance of ChatManager. This holder class is loaded
     * on the first execution of ChatManager.getChatManager().
     */
    private static class ChatManagerHolder {

        private final static ChatManager manager = new ChatManager();
    }

    /**
     * Returns a single instance of this class
     * <p>
     * @return Single instance of this class.
     */
    public static final ChatManager getChatManager() {
        return ChatManagerHolder.manager;
    }

    /**
     * Private constructor, singelton pattern
     */
    private ChatManager() {
        textChatHUDRefMap = new HashMap<String, WeakReference<HUDComponent>>();
        textChatPanelRefMap =
                new HashMap<HUDComponent, WeakReference<TextChatPanel>>();

        // Create the global text chat menu item. Listen for when it is
        // selected or de-selected and show/hide the frame as appropriate. This
        // menu item will get added/removed for each primary session.
        textChatMenuItem = new JCheckBoxMenuItem(
                BUNDLE.getString("Text_Chat_All"));
        textChatMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                // Fetch the global text chat menu item and make it visible
                // if not already so.
                HUDComponent hud = textChatHUDRefMap.get("").get();
                boolean show = !hud.isVisible();
                textChatMenuItem.setState(show);
                //issue #174 hud visibility management
                if (show) {
                	hud.setMaximized();
                }
                hud.setVisible(show);
            }
        });
        textChatMenuItem.setEnabled(false);
    }

    /**
     * Registers the primary session
     * @param session the primary session
     */
    public void register(WonderlandSession session) {
        // Capture the local user name for later use
        localUserName = session.getUserID().getUsername();

        // Create a new custom connection to receive text chats. Register a
        // listener that handles new text messages. Will display them in the
        // window.
        textChatConnection = new TextChatConnection();
        textChatConnection.addTextChatListener(this);

        // Open the text chat connection. If unsuccessful, then log an error
        // and return.
        try {
            textChatConnection.connect(session);
        } catch (ConnectionFailureException excp) {
            LOGGER.log(Level.WARNING, "Unable to establish a connection to " +
                    "the chat connection.", excp);
            return;
        }

        // Create the HUD component for the "Text Chat All" window. Note that
        // this must happen on the AWT Event Thread.
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                // Create the new text chat "all" window and make it visible
                // and active.
                HUDComponent hud = createTextChatHUD("", false);
                hud.setName(BUNDLE.getString("Text_Chat_All"));
                TextChatPanel panel = textChatPanelRefMap.get(hud).get();

                panel.setActive(textChatConnection, localUserName, "");
                textChatMenuItem.setEnabled(true);
                textChatMenuItem.setSelected(true);
                hud.setVisible(true);

                // Listen for when the HUD component closes. When it does, we
                // need to set the state of the checkbox menu item.
                hud.addEventListener(new HUDEventListener() {

                    public void HUDObjectChanged(HUDEvent event) {
                        // If the window is being closed, we need to update the
                        // state of the checkbox menu item, but we must do this
                        // in the AWT Event Thread.
                    	
                    	// modified for fixing issue #174 hud visibility management
                    	HUDEventType hudEventType = event.getEventType();
                    	if (hudEventType == HUDEventType.MINIMIZED
                    			|| hudEventType == HUDEventType.MAXIMIZED
                    			|| hudEventType == HUDEventType.CLOSED) {
                    		final boolean isSelected = hudEventType == HUDEventType.MAXIMIZED;
                    		SwingUtilities.invokeLater(new Runnable() {
                    			public void run() {
                    				textChatMenuItem.setSelected(isSelected);
                    			}
                    		});
                    	}
                    }
                });

                // Add the global text chat menu item to the "Window" menu. Note
                // we need to do this after the global text chat window is
                // create, otherwise there is a (small) chance that a user can
                // select the menu item before the text chat panel has been
                // created.
                JmeClientMain.getFrame().addToWindowMenu(textChatMenuItem, 2);
            }
        });
    }

    /**
     * Unregister and menus we have created, etc.
     */
    public void unregister() {
        // First remove the listen for incoming text chat messages.
        textChatConnection.removeTextChatListener(this);

        // Next, remove the menu item. We need to do this before we shut down
        // all of the text chat windows, otherwise there is a (small) chance
        // that a user can select the menu item after the chat windows have
        // been destroyed.
        JmeClientMain.getFrame().removeFromWindowMenu(textChatMenuItem);

        // Finally, close down all of the individual text chat windows and clear
        // out the maps of text chat panels. We need to do this on the AWT
        // Even Thread.
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                // Loop through all of the entries in the map and tell the HUD
                // components to close themselves down.
                for (String name : textChatHUDRefMap.keySet()) {
                    HUDComponent component = textChatHUDRefMap.get(name).get();
                    component.setVisible(false);
                    // TODO: really dispose of HUD component
                }

                // Clear out the maps of all of the text chat panels.
                textChatHUDRefMap.clear();
                textChatPanelRefMap.clear();

                // Disconnect from the text chat connection. Note that we should
                // do this AFTER we close the text chat windows, otherwise there
                // is a (small) chance that someone will type in a window when
                // there is no active connection (which may result in a some-
                // what hamrless exception, but it's good to avoid if we can).
                textChatConnection.disconnect();
            }
        });
    }

    /**
     * Creates a new text chat window, given the remote participants user name
     * and displays it.
     *
     * @param remoteUser The remote participants user name
     */
    public void startChat(final String remoteUser) {

        // Do all of this synchronized. This makes sure that multiple text chat
        // window aren't created if a local user clicks to create a new text
        // chat and a message comes in for that remote user. We do this on the
        // AWT Event Thread to achieve proper synchronization.
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                // Check to see if the text chat window already exists. If so,
                // then we do nothing and return.
                WeakReference<HUDComponent> ref =
                        textChatHUDRefMap.get(remoteUser);
                if (ref != null) {
                    return;
                }

                // Otherwise, create the frame, add it to the map, and display
                HUDComponent hud = createTextChatHUD(remoteUser, true);
                String name = BUNDLE.getString("Text_Chat_With");
                name = MessageFormat.format(name, remoteUser);
                hud.setName(name);
                TextChatPanel panel = textChatPanelRefMap.get(hud).get();
                panel.setActive(textChatConnection, localUserName, remoteUser);
                hud.setVisible(true);
            }
        });
    }

    /**
     * Deactivates the text chat given the remote user's name, if such a frame
     * exists. Displays a message in the window and turns off its GUI.
     *
     * @param remoteUser The remote participants user name
     */
    public void deactivateChat(final String remoteUser) {

        // Do all of this synchronized, so that we do not interfere with the
        // code to create chats. We do this on the AWT Event Thread to achieve
        // proper synchronization.
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                // Check to see if the text chat window exists. If not, then do
                // nothing.
                WeakReference<HUDComponent> ref =
                        textChatHUDRefMap.get(remoteUser);
                if (ref == null) {
                    return;
                }
                HUDComponent textChatHUDComponent = ref.get();
                TextChatPanel textChatPanel =
                        textChatPanelRefMap.get(textChatHUDComponent).get();
                textChatPanel.deactivate();
            }
        });
    }

    /**
     * Re-activates the text chat given the remote user's name, if such a frame
     * exists. Displays a message in the window and turns on its GUI.
     * @param remoteUser the remote user's name
     */
    public void reactivateChat(final String remoteUser) {

        // Do all of this synchronized, so that we do not interfere with the
        // code to create chats. We do this on the AWT Event Thread to achieve
        // proper synchronization.
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                // Check to see if the text chat window exists. If not, then do
                // nothing.
                WeakReference<HUDComponent> ref =
                        textChatHUDRefMap.get(remoteUser);
                if (ref == null) {
                    return;
                }
                HUDComponent textChatHUDComponent = ref.get();
                TextChatPanel textChatPanel =
                        textChatPanelRefMap.get(textChatHUDComponent).get();
                textChatPanel.reactivate();
            }
        });
    }

    public void showTextChatAll() {
        SwingUtilities.invokeLater(new Runnable() {
           public void run() {
               WeakReference<HUDComponent> ref =
                       textChatHUDRefMap.get("");

               if(ref == null) {
                   System.out.println("Text Chat All doesn't exist.");
                   return;
               }
               HUDComponent textChatHUDComponent = ref.get();
               TextChatPanel chatPanel = textChatPanelRefMap.get(textChatHUDComponent).get();
               chatPanel.reactivate();
               textChatHUDComponent.setVisible(true);
               textChatMenuItem.setState(true);

           }
        });
    }

    /**
     * @inheritDoc()
     */
    public void textMessage(final String message, final String fromUser,
            final String toUser) {

        // We do all of this on the AWT Event Thread to achieve the proper
        // synchronization on textChatHUDRefMap while also doing the proper
        // Swing stuff.
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                // Fetch the frame associated with the user. If the "to" user is
                // an empty string, then this is a "global" message and we fetch
                // its frame. It should exist. We always add the message, no
                // matter whether the frame is visible or not.
                if (toUser == null || toUser.equals("") == true) {
                    WeakReference<HUDComponent> ref = textChatHUDRefMap.get("");
                    if (ref == null) {
                        return;
                    }
                    HUDComponent textChatHUDComponent = ref.get();
                    TextChatPanel textChatPanel =
                            textChatPanelRefMap.get(textChatHUDComponent).get();
                    textChatPanel.appendTextMessage(message, fromUser);
                    return;
                }

                // Otherwise, the "toUser" is for this specific user. We fetch
                // the frame associated with the "from" user. If it exists
                // (which also means it is visible), then add the message.

                // Find the HUD component in the map. If it does exist, then
                // simply add the message to the end of the list.
                WeakReference<HUDComponent> ref =
                        textChatHUDRefMap.get(fromUser);
                if (ref != null) {
                    HUDComponent textChatHUDComponent = ref.get();
                    TextChatPanel textChatPanel =
                            textChatPanelRefMap.get(textChatHUDComponent).get();
                    textChatPanel.appendTextMessage(message, fromUser);
                    return;
                }

                // Finally, we reached here when we have a message from a
                // specific user, but the frame does not exist, and is not
                // visible. So we create it and display it.
                HUDComponent hud = createTextChatHUD(fromUser, true);
                String name = BUNDLE.getString("Text_Chat_With");
                name = MessageFormat.format(name, fromUser);
                hud.setName(name);
                TextChatPanel panel = textChatPanelRefMap.get(hud).get();
                panel.setActive(textChatConnection, toUser, fromUser);
                panel.appendTextMessage(message, fromUser);
                hud.setVisible(true);
            }
        });
    }

    /**
     * Creates and returns a new text chat component, given the name of the
     * remote user for which the text chat is assigned. If the "handleClose"
     * argument is true, then the HUD Component is removed from the Maps when
     * destroyed.
     *
     * NOTE: This method assumes it is being called on the AWT Event Thread
     */
    private HUDComponent createTextChatHUD(final String userKey,
            boolean handleClose) {

        // Create a new text chat Swing Panel
        final TextChatPanel textChatPanel = new TextChatPanel();

        // Create a new HUD Panel with the Swing panel and add. It still isn't
        // visible.
        HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");
        HUDComponent hudComponent = mainHUD.createComponent(textChatPanel);
        hudComponent.setIcon(new ImageIcon(getClass().getResource(
                "/org/jdesktop/wonderland/modules/textchat/client/resources/" +
                "UserListChatText32x32.png")));
        hudComponent.setPreferredLocation(Layout.SOUTHWEST);
        mainHUD.addComponent(hudComponent);

        // Listen for when the text chat HUD panel is closed. When that happens
        // remove it from the HUD and the various maps.
        if (handleClose == true) {
            hudComponent.addEventListener(new HUDEventListener() {

                public void HUDObjectChanged(final HUDEvent e) {
                    // Remove from the map which will let it garbage collect. We
                    // need to do this on the AWT Event Thread to synchronize
                    // access  to the Map
                    if (e.getEventType() == HUDEventType.CLOSED) {
                        SwingUtilities.invokeLater(new Runnable() {

                            public void run() {
                                e.getObject().setVisible(false);
                                // TODO: really dispose of HUD component
                                WeakReference<HUDComponent> ref =
                                        textChatHUDRefMap.get(userKey);
                                if (ref != null) {
                                    HUDComponent hud = ref.get();
                                    textChatHUDRefMap.remove(userKey);
                                    textChatPanelRefMap.remove(hud);
                                }
                            }
                        });
                    }
                }
            });
        }

        // Put in the proper maps: a map of the HUD component to the Swing
        // component and a map of the remote user name ("" for global text chat)
        // to the HUD component
        textChatPanelRefMap.put(
                hudComponent, new WeakReference<TextChatPanel>(textChatPanel));
        textChatHUDRefMap.put(
                userKey, new WeakReference<HUDComponent>(hudComponent));
        return hudComponent;
    }
}
