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
package org.jdesktop.wonderland.modules.avatarbase.client.jme.cellrenderer;

import imi.character.CharacterEyes;
import imi.character.avatar.AvatarContext.TriggerNames;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDEvent;
import org.jdesktop.wonderland.client.hud.HUDEvent.HUDEventType;
import org.jdesktop.wonderland.client.hud.HUDEventListener;
import org.jdesktop.wonderland.client.hud.HUDManagerFactory;

/**
 * A HUD display for avatar gestures
 *
 * @author nsimpson
 * @author ronny.standtke@fhnw.ch
 */
public class GestureHUD {

    private static final Logger logger = Logger.getLogger(GestureHUD.class.getName());
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/avatarbase/client/resources/Bundle");

    private final JCheckBoxMenuItem gestureMI;

    private boolean visible = false;
    // maps GUI visible gesture names to non-visible action names
    private Map<String, String> gestureMap = new HashMap<String, String>();
    private HUDComponent gestureHud;
    private HUD mainHUD;
    private JPanel panelForHUD = new JPanel();
    //TODO we are not using positions anymore, can clean the bundle - same goes for show/hide gestures
    // map gestures to column, row locations on gesture HUD
    private String[] gestures = {
        BUNDLE.getString("AnswerCell"),
        BUNDLE.getString("Sit"),
        /*{"Take Damage", "0", "3"},*/
        BUNDLE.getString("PublicSpeaking"),
        BUNDLE.getString("Bow"),
        BUNDLE.getString("ShakeHands"),
        BUNDLE.getString("Cheer"),
        BUNDLE.getString("Clap"),
        BUNDLE.getString("Laugh"),
        BUNDLE.getString("Wave"),
        BUNDLE.getString("RaiseHand"),
        BUNDLE.getString("Follow"),
        /*{"Left Wink", "4", "0"},*/
        BUNDLE.getString("Wink"),
        BUNDLE.getString("No"),
        BUNDLE.getString("Yes")};
    private int leftMargin = 295;
    private int bottomMargin = 5;

    /**
     * creates a new GestureHUD
     */
    public GestureHUD(JCheckBoxMenuItem gestureMI) {
        this.gestureMI = gestureMI;

        setAvatarCharacter(null, false);
    }

    /**
     * maximizes the gesture HUD
     * making the HUD visible may require
     * to maximize it if it was minimized
     * introduced for fixing issue #174 hud visibility management
     */
    public void setMaximized() {
    	SwingUtilities.invokeLater(new Runnable() {
    		public void run() {
    			gestureHud.setMaximized();	
    		}
    	});
    }
    
    /**
     * shows or hides the gesture HUD
     * @param visible if <tt>true</tt>, the HUD is shown, otherwise hidden
     */
    public void setVisible(final boolean show) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                GestureHUD.this.visible = show;
                gestureHud.setVisible(show);
                    return;
                }
                    });
                }

    /**
     * returns <tt>true</tt>, when the HUD is visible, otherwise false
     * @return <tt>true</tt>, when the HUD is visible, otherwise false
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Invoke the Sit gesture.
     */
    private void doSitGesture(final WlAvatarCharacter avatar) {
        // Create a thread that sleeps and tells the sit action to stop.
        final Runnable stopSitRunnable = new Runnable() {

            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    logger.log(Level.WARNING, "Sleep failed.", ex);
                }
                avatar.triggerActionStop(TriggerNames.SitOnGround);
            }
        };

        // Spawn a thread to start the animation, which then spawns a thread
        // to stop the animation after a small sleep.
        new Thread() {

            @Override
            public void run() {
                avatar.triggerActionStart(TriggerNames.SitOnGround);
                new Thread(stopSitRunnable).start();
            }
        }.start();
    }

    /**
     * Creation of a JPanel with a message stating that that no gestures are supported
     * @return panelForHUD with the appropriate message
     */
    private JPanel createNoGesturesMessage() {
        panelForHUD = new JPanel();
        panelForHUD.setLayout(new BorderLayout());

        JTextArea jta = new JTextArea(3, 30);
        jta.setEditable(false);
        jta.setLineWrap(true);
        jta.setWrapStyleWord(true);
        jta.setFont(new Font(null, Font.PLAIN, 16));
        jta.setText(BUNDLE.getString("Gestures_Not_Supported"));

        JScrollPane jsp = new JScrollPane(jta);
        panelForHUD.add(jsp, BorderLayout.CENTER);
        
        return panelForHUD;
    }

    /**
     * sets the avatar and activates supported gestures
     * @param avatar the avatar to set
     * @param show stating if the HUD should be visible
     */
    public void setAvatarCharacter(final WlAvatarCharacter avatar, final boolean show) {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                if (mainHUD == null) {
                    mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");
                }

                gestureMap.clear();

                if (gestureHud != null) {
                    gestureHud.setVisible(false);
                    mainHUD.removeComponent(gestureHud);
                    gestureHud = null;
                    panelForHUD = null;
                }

                // If we don't have an avatar, then just return
                if (avatar == null) {
                    return;
                }

                // Otherwise, figure out which gestures are supported. We want
                // to remove the "Male_" or "Female_" for now.
                for (String action : avatar.getAnimationNames()) {
                    String name = action;
                    if (action.startsWith("Male_") == true) {
                        name = name.substring(5);
                    } else if (action.startsWith("Female_") == true) {
                        name = name.substring(7);
                    }
                    // add to a map of user-friendly names to avatar animations
                    // e.g., "Shake Hands" -> "Male_ShakeHands"
                    gestureMap.put(BUNDLE.getString(name), action);
                }

                // Add the left and right wink
                if (avatar.getCharacterParams().isAnimatingFace()) {
                    gestureMap.put(BUNDLE.getString("Wink"), "RightWink");
                    gestureMap.put(BUNDLE.getString("Sit"), "Sit");
                }

                if (!gestureMap.keySet().isEmpty()) {
                    panelForHUD = new JPanel();
                    panelForHUD.setLayout(new GridLayout(5, 3));

                    // Create HUD buttons for each of the actions
                    for (String name : gestureMap.keySet()) {

                    	// find the button row, column position for this gesture
                    	for (String gesture : gestures) {
                    		if (gesture.equals(name)) {
                                JButton button = new JButton(name);
                                button.addActionListener(
                                	new ActionListener() {
                                		public void actionPerformed(ActionEvent event) {
                                			String action = gestureMap.get(event.getActionCommand());
                                			logger.info("playing animation: " + event.getActionCommand());
                                			if (action.equals("Sit")) {
                                				doSitGesture(avatar);
                                			} else if (action.equals("RightWink")) {
                                				CharacterEyes eyes = avatar.getEyes();
                                				eyes.wink(false);
                                			} else {
                                				avatar.playAnimation(action);
                                			}
                                		}
                                	});
                                panelForHUD.add(button);
                                break;
                    		}
                    	}
                    }
                
                } else {
                    logger.warning("No animations found; creating No gestures Message.");
                    panelForHUD = createNoGesturesMessage();
                }

                if (gestureHud == null){
                    gestureHud = mainHUD.createComponent(panelForHUD);
                    //gestureHud.setDecoratable(false);
                    gestureHud.setName(BUNDLE.getString("Gesture_UI"));
                    gestureHud.setLocation(leftMargin, bottomMargin);
                    mainHUD.addComponent(gestureHud);

                    // issue #174 hud visibility management
                    gestureHud.addEventListener(new HUDEventListener() {
                    	public void HUDObjectChanged(HUDEvent event) {
                    		HUDEventType hudEventType = event.getEventType();
                    		if (event.getEventType() == HUDEventType.DISAPPEARED
                    				|| hudEventType == HUDEventType.CLOSED
                    				|| hudEventType == HUDEventType.MINIMIZED) {
                    			gestureMI.setSelected(false);
                    		} else 
                    		if (event.getEventType() == HUDEventType.APPEARED
                    				|| hudEventType == HUDEventType.MAXIMIZED) {
                    			gestureMI.setSelected(true);
                    		} 
                    	}
                    });
                }
                
                GestureHUD.this.visible = show;
                gestureHud.setVisible(show);
            }
        });
    }
}
