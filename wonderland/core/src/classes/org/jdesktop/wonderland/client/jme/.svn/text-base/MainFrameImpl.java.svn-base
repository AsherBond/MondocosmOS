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
package org.jdesktop.wonderland.client.jme;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.jdesktop.mtgame.WorldManager;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.InputMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.FrameRateListener;
import org.jdesktop.wonderland.client.hud.CompassLayout.Layout;
import org.jdesktop.wonderland.client.hud.HUD;
import org.jdesktop.wonderland.client.hud.HUDComponent;
import org.jdesktop.wonderland.client.hud.HUDManagerFactory;
import org.jdesktop.wonderland.client.input.InputManager;
import org.jdesktop.wonderland.client.jme.utils.GUIUtils;

/**
 * The Main JFrame for the wonderland jme client
 * 
 * @author  paulby
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class MainFrameImpl extends JFrame implements MainFrame {

    private static final Logger LOGGER =
            Logger.getLogger(MainFrameImpl.class.getName());
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/client/jme/resources/Bundle");
    private JMenuItem logoutMI;
    private JMenuItem exitMI;
    private CameraButtonGroup cameraButtonGroup = new CameraButtonGroup();
    private JRadioButtonMenuItem firstPersonRB;
    private JRadioButtonMenuItem thirdPersonRB;
    private JRadioButtonMenuItem frontPersonRB;
    private final Map<JMenuItem, Integer> menuWeights =
            new HashMap<JMenuItem, Integer>();
    
    private final List<JMenuItem> systemPlacemarks = 
            Collections.synchronizedList(new ArrayList<JMenuItem>());
    private final List<JMenuItem> userPlacemarks = 
            Collections.synchronizedList(new ArrayList<JMenuItem>());
    private final List<JMenuItem> managementPlacemarks = 
            Collections.synchronizedList(new ArrayList<JMenuItem>());
    
    private JMenu frameRateMenu;
    private int desiredFrameRate = 30;
    private FrameRateListener frameRateListener = null;
    private JMenuItem fpsMI;
    private JMenuItem logViewerMI;
    private JCheckBoxMenuItem locationBarMI;
    private Chart chart;
    private HUDComponent fpsComponent;
    private WorldManager wm;

    // variables for the location field
    private String serverURL;
    private ServerURLListener serverListener;

    /** Creates new form MainFrame */
    public MainFrameImpl(WorldManager wm, int width, int height) {
        this.wm = wm;

        GUIUtils.initLookAndFeel();
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        ToolTipManager.sharedInstance().setLightWeightPopupEnabled(false);

        initComponents();
        initMenus();

        setTitle(BUNDLE.getString("Wonderland"));

        if (width == -1 || height == -1) {
            // OWL issue #146: for full screen, set the size of the frame to the
            // full size of the screen device, and let the rest of the panels
            // size themselves appropriately
            GraphicsEnvironment ge =
                        GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice[] gs = ge.getScreenDevices();
            if (gs.length > 1) {
                LOGGER.warning("Fullscreen using size of first screen");
            }

            GraphicsConfiguration gc = gs[0].getDefaultConfiguration();
            Rectangle size = gc.getBounds();

            setBounds(size);
        } else {
            // user-specified size. Try to set the size of the 3d canvas to
            // the size the user specified
            centerPanel.setMinimumSize(new Dimension(width, height));
            centerPanel.setPreferredSize(new Dimension(width, height));
            pack();
        }

        // Register the main panel with the drag-and-drop manager
        // This is now handled by the InputManager
        // DragAndDropManager.getDragAndDropManager().setDropTarget(centerPanel);

        serverField.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                updateGoButton();
            }

            public void removeUpdate(DocumentEvent e) {
                updateGoButton();
            }

            public void changedUpdate(DocumentEvent e) {
                updateGoButton();
            }
        });

        // show the log viewer if visible on startup is selected
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (LogViewer.INSTANCE.isVisibleOnStartup()) {
                    LogViewer.INSTANCE.setVisible(true);
                }
            }
        });
    }

    private void initMenus() {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                // File menu
                // Log out
                logoutMI = new JMenuItem(BUNDLE.getString("Log out"));
                logoutMI.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent evt) {
                        logoutMIActionPerformed(evt);
                    }
                });
                addToFileMenu(logoutMI, 2);

                // Exit
                exitMI = new JMenuItem(BUNDLE.getString("Exit"));
                exitMI.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent evt) {
                        exitMIActionPerformed(evt);
                    }
                });
                addToFileMenu(exitMI, 3);

                // Turn location bar on and off
                locationBarMI = new JCheckBoxMenuItem(BUNDLE.getString("Location_Bar"));
                locationBarMI.setSelected(true);
                locationBarMI.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        locationBarMIActionPerformed(evt);
                    }
                });
                addToViewMenu(locationBarMI, -1);

                // View menu
                firstPersonRB = new JRadioButtonMenuItem(
                        BUNDLE.getString("First Person Camera"));
                firstPersonRB.setAccelerator(KeyStroke.getKeyStroke('f'));
                firstPersonRB.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent evt) {
                        cameraChangedActionPerformed(evt);
                    }
                });
                addToViewMenu(firstPersonRB, 0);
                cameraButtonGroup.add(firstPersonRB);

                thirdPersonRB = new JRadioButtonMenuItem(
                        BUNDLE.getString("Third Person Camera"));

                thirdPersonRB.setAccelerator(KeyStroke.getKeyStroke('t'));
                
                thirdPersonRB.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent evt) {
                        cameraChangedActionPerformed(evt);
                    }
                });
                addToViewMenu(thirdPersonRB, 1);
                cameraButtonGroup.add(thirdPersonRB);

                frontPersonRB = new JRadioButtonMenuItem(
                        BUNDLE.getString("Front Camera"));
                frontPersonRB.setToolTipText(
                        BUNDLE.getString("Front_Camera_Tooltip"));
                frontPersonRB.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent evt) {
                        cameraChangedActionPerformed(evt);
                    }
                });
                addToViewMenu(frontPersonRB, 2);
                cameraButtonGroup.add(frontPersonRB);

                // add custom accelerators for cycling and resetting the
                // camera
                InputMap im = mainMenuBar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
                im.put(KeyStroke.getKeyStroke('c'), "cycleCamera");
                im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "resetCamera");

                ActionMap am = mainMenuBar.getActionMap();
                am.put("cycleCamera", new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        cameraButtonGroup.next();
                    }
                });
                am.put("resetCamera", new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        ViewManager.getViewManager().setCameraController(
                                ViewManager.getDefaultCamera());
                    }
                });

                // Frame Rate menu
                frameRateMenu = new JMenu(BUNDLE.getString("Max Frame Rate"));

                JMenuItem fps15 = new JCheckBoxMenuItem(
                        BUNDLE.getString("15 fps"));
                JMenuItem fps30 = new JCheckBoxMenuItem(
                        BUNDLE.getString("30 fps (default)"));
                JMenuItem fps60 = new JCheckBoxMenuItem(
                        BUNDLE.getString("60 fps"));
                JMenuItem fps120 = new JCheckBoxMenuItem(
                        BUNDLE.getString("120 fps"));
                JMenuItem fps200 = new JCheckBoxMenuItem(
                        BUNDLE.getString("200 fps"));

                frameRateMenu.add(fps15);
                frameRateMenu.add(fps30);
                frameRateMenu.add(fps60);
                frameRateMenu.add(fps120);
                frameRateMenu.add(fps200);

                addToViewMenu(frameRateMenu, 5);

                fps15.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent evt) {
                        frameRateActionPerformed(evt);
                    }
                });
                fps30.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent evt) {
                        frameRateActionPerformed(evt);
                    }
                });
                fps60.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent evt) {
                        frameRateActionPerformed(evt);
                    }
                });
                fps120.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent evt) {
                        frameRateActionPerformed(evt);
                    }
                });
                fps200.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent evt) {
                        frameRateActionPerformed(evt);
                    }
                });

                // frame rate meter
                fpsMI = new JCheckBoxMenuItem(BUNDLE.getString("FPS_Meter"));
                fpsMI.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent evt) {
                        if ((fpsComponent == null) ||
                                !fpsComponent.isVisible()) {
                            showFPSMeter(true);
                        } else {
                            showFPSMeter(false);
                        }
                    }
                });

                addToWindowMenu(fpsMI, -1);

                // log viwer
                logViewerMI = new JMenuItem(BUNDLE.getString("Log_Viewer"));
                logViewerMI.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent ae) {
                        LogViewer.INSTANCE.setVisible(true);
                    }
                });
                addToHelpMenu(logViewerMI, -1);
            }
        });
    }

    private void logoutMIActionPerformed(ActionEvent evt) {
        if (serverListener != null) {
            serverListener.logout();
        }

        serverURL = null;
        updateGoButton();
    }

    private void exitMIActionPerformed(ActionEvent evt) {
        ClientContextJME.getWorldManager().shutdown();
        System.exit(0);
    }

    private void locationBarMIActionPerformed(ActionEvent evt) {
        serverPanel.setVisible(!serverPanel.isVisible());
        locationBarMI.setSelected(serverPanel.isVisible());
    }

    private void cameraChangedActionPerformed(ActionEvent evt) {
        ViewManager viewManager = ClientContextJME.getViewManager();
        if (evt.getSource() == firstPersonRB) {
            viewManager.setCameraController(
                    new FirstPersonCameraProcessor());
        } else if (evt.getSource() == thirdPersonRB) {
            viewManager.setCameraController(
                    new ThirdPersonCameraProcessor());
        } else if (evt.getSource() == frontPersonRB) {
            viewManager.setCameraController(
                    new FrontHackPersonCameraProcessor());
        }

    }

    private void frameRateActionPerformed(ActionEvent evt) {
        JMenuItem mi = (JMenuItem) evt.getSource();
        String[] fpsString = mi.getText().split(" ");
        int fps = Integer.valueOf(fpsString[0]);
        LOGGER.info("maximum fps: " + fps);
        setDesiredFrameRate(fps);
    }

    public void setDesiredFrameRate(int desiredFrameRate) {
        this.desiredFrameRate = desiredFrameRate;

        for (int i = 0; i < frameRateMenu.getItemCount(); i++) {
            JMenuItem item = frameRateMenu.getItem(i);
            String[] fpsString = item.getText().split(" ");
            int fps = Integer.valueOf(fpsString[0]);
            if (fps == desiredFrameRate) {
                item.setSelected(true);
            } else {
                item.setSelected(false);
            }
        }
        wm.getRenderManager().setDesiredFrameRate(desiredFrameRate);

        removeFrameRateListener(frameRateListener);
        frameRateListener = addFrameRateListener(desiredFrameRate);

        // update user preference
        JmeClientMain.setDesiredFrameRate(desiredFrameRate);
        
        if (chart != null) {
            chart.setMaxValue(desiredFrameRate);
        }
    }

    /**
     * updates the Go! button
     */
    public void updateGoButton() {
        String cur = serverField.getText();
        goButton.setEnabled(
                (cur != null) && (cur.length() > 0) && !cur.equals(serverURL));
    }

    /**
     * Return the JME frame
     * @return the frame
     */
    public JFrame getFrame() {
        return this;
    }

    /**
     * Returns the canvas of the frame.
     * @return the canvas of the frame.
     */
    public Canvas getCanvas() {
        return ViewManager.getViewManager().getCanvas();
    }

    /**
     * Returns the panel of the frame in which the 3D canvas resides.
     * @return the panel of the frame in which the 3D canvas resides.
     */
    public JPanel getCanvas3DPanel() {
        return centerPanel;
    }

    /**
     * {@inheritDoc}
     */
    public void addToMenu(final JMenu menu, final JMenuItem menuItem, int weight) {
        if (weight < 0) {
            weight = Integer.MAX_VALUE;
        }

        final int weightFinal = weight;

        LOGGER.fine(menu.getText() + " menu: inserting [" +
                menuItem.getText() + "] with weight: " + weight);

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                // find the index of the first menu item with a higher weight or
                // the same weight and later in the alphabet
                int index = 0;
                for (index = 0; index < menu.getItemCount(); index++) {
                    JMenuItem curItem = menu.getItem(index);
                    if (curItem == null || !menuWeights.containsKey(curItem)) {
                        continue; //Skipping separators & other objects with
                                  //no weight
                    }
                    
                    int curWeight = menuWeights.get(curItem);
                    if (curWeight > weightFinal) {
                        break;
                    } else if (curWeight == weightFinal) {
                        String currentItemName = curItem.getText();
                        if (currentItemName == null) {
                            break;
                        }

                        String name = menuItem.getText();
                        if ((name != null) &&
                                name.compareTo(currentItemName) > 0) {
                            break;
                        }
                    }
                }

                // add the item at the right place
                menu.insert(menuItem, index);
                
                // remember the menu's weight
                menuWeights.put(menuItem, weightFinal);
            }
        });
    }
    
    /**
     * Remove the given menu item from a menu
     * @param menu the menu to remove from
     * @param item the item to remove
     */
    public void removeFromMenu(JMenu menu, JMenuItem item) {
        menu.remove(item);
        menuWeights.remove(item);
    }

    /**
     * {@inheritDoc}
     */
    public void addToFileMenu(JMenuItem menuItem) {
        addToMenu(fileMenu, menuItem, -1);
    }

    /**
     * {@inheritDoc}
     */
    public void addToFileMenu(JMenuItem menuItem, int index) {
        addToMenu(fileMenu, menuItem, index);
    }

    /**
     * {@inheritDoc}
     */
    public void removeFromFileMenu(JMenuItem menuItem) {
        removeFromMenu(fileMenu, menuItem);
    }

    /**
     * {@inheritDoc}
     */
    public void addToEditMenu(JMenuItem menuItem) {
        addToMenu(editMenu, menuItem, -1);
    }

    /**
     * {@inheritDoc}
     */
    public void addToEditMenu(JMenuItem menuItem, int index) {
        addToMenu(editMenu, menuItem, index);
    }

    /**
     * {@inheritDoc}
     */
    public void removeFromEditMenu(JMenuItem menuItem) {
        removeFromMenu(editMenu, menuItem);
    }

    /**
     * {@inheritDoc}
     */
    public void addToViewMenu(JMenuItem menuItem) {
        addToMenu(viewMenu, menuItem, -1);
    }

    /**
     * {@inheritDoc}
     */
    public void addToViewMenu(JMenuItem menuItem, int index) {
        addToMenu(viewMenu, menuItem, index);
    }

    /**
     *
     * @param menuItem
     */
    public void addToViewMenuCameraGroup(JRadioButtonMenuItem menuItem) {
    }

    /**
     * {@inheritDoc}
     */
    public void removeFromViewMenu(JMenuItem menuItem) {
        removeFromMenu(viewMenu, menuItem);
    }

    /**
     * {@inheritDoc}
     */
    public void addToInsertMenu(JMenuItem menuItem) {
        addToMenu(insertMenu, menuItem, -1);
    }

    /**
     * {@inheritDoc}
     */
    public void addToInsertMenu(JMenuItem menuItem, int index) {
        addToMenu(insertMenu, menuItem, index);
    }

    /**
     * {@inheritDoc}
     */
    public void removeFromInsertMenu(JMenuItem menuItem) {
        removeFromMenu(insertMenu, menuItem);
    }

    /**
     * {@inheritDoc}
     */
    public void addToToolsMenu(JMenuItem menuItem) {
        addToMenu(toolsMenu, menuItem, -1);
    }

    /**
     * {@inheritDoc}
     */
    public void addToToolsMenu(JMenuItem menuItem, int index) {
        addToMenu(toolsMenu, menuItem, index);
    }

    /**
     * {@inheritDoc}
     */
    public void removeFromToolsMenu(JMenuItem menuItem) {
        removeFromMenu(toolsMenu, menuItem);
    }

    /**
     * {@inheritDoc}
     */
    public void addToPlacemarksMenu(JMenuItem menuItem) {
        addToMenu(placemarksMenu, menuItem, -1);
    }

     /**
     * {@inheritDoc}
     */
    public void addToPlacemarksMenu(final JMenuItem menuItem, int index, final PlacemarkType placemarkType){
        
        if (placemarkType == PlacemarkType.SYSTEM){
            systemPlacemarks.add(menuItem);
            synchronized (systemPlacemarks) {
                Collections.sort(systemPlacemarks, new JMenuItemAlphabeticalComparator());
            }
        }
        else if (placemarkType == PlacemarkType.USER){
            userPlacemarks.add(menuItem);
            synchronized (userPlacemarks) {
                Collections.sort(userPlacemarks, new JMenuItemAlphabeticalComparator());
            }
        }
        else {
            managementPlacemarks.add(menuItem);
        }

        addToMenu(placemarksMenu, menuItem, index);
        rebuildPlacemarksMenu(placemarksMenu);
    }

    private static class JMenuItemAlphabeticalComparator 
        implements Serializable, Comparator<JMenuItem> 
    {

        public JMenuItemAlphabeticalComparator() {
        }

        public int compare(JMenuItem one, JMenuItem other) {

            String textOne = ((JMenuItem) one).getText();
            String textTwo = ((JMenuItem) other).getText();
            return textOne.compareTo(textTwo);
        }
    }

    private void rebuildPlacemarksMenu(final JMenu menu){

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                menu.removeAll();
                menu.add(managementPlacemarks.get(0)); //Starting Location

                if (!systemPlacemarks.isEmpty()){
                    menu.addSeparator();
                    
                    synchronized (systemPlacemarks) {
                        for (JMenuItem jMenuItem : systemPlacemarks) {
                            menu.add(jMenuItem);
                        }
                    }
                }
                
                if (!userPlacemarks.isEmpty()){
                    menu.addSeparator();
                    
                    synchronized (userPlacemarks) {
                        for (JMenuItem jMenuItem : userPlacemarks) {
                            menu.add(jMenuItem);
                        }   
                    }
                }
                
                menu.addSeparator();
                menu.add(managementPlacemarks.get(1)); //Add Placemarks
                menu.add(managementPlacemarks.get(2)); //Manage Placemarks
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    public void removeFromPlacemarksMenu(JMenuItem menuItem) {
        systemPlacemarks.remove(menuItem);
        userPlacemarks.remove(menuItem);
        managementPlacemarks.remove(menuItem);
        removeFromMenu(placemarksMenu, menuItem);
    }

    /**
     * {@inheritDoc}
     */
    public void addToWindowMenu(JMenuItem menuItem) {
        addToMenu(windowMenu, menuItem, -1);
    }

    /**
     * {@inheritDoc}
     */
    public void addToWindowMenu(JMenuItem menuItem, int index) {
        addToMenu(windowMenu, menuItem, index);
    }

    /**
     * {@inheritDoc}
     */
    public void removeFromWindowMenu(JMenuItem menuItem) {
        removeFromMenu(windowMenu, menuItem);
    }

    /**
     * {@inheritDoc}
     */
    public void addToHelpMenu(JMenuItem menuItem) {
        addToMenu(helpMenu, menuItem, -1);
    }

    /**
     * {@inheritDoc}
     */
    public void addToHelpMenu(JMenuItem menuItem, int index) {
        addToMenu(helpMenu, menuItem, index);
    }

    /**
     * {@inheritDoc}
     */
    public void removeFromHelpMenu(JMenuItem menuItem) {
        removeFromMenu(helpMenu, menuItem);
    }

    /**
     * Set the server URL in the location field
     * @param serverURL the server URL to set
     */
    public void setServerURL(final String serverURL) {
        this.serverURL = serverURL;

        // issue #719: make sure to do this in the AWT event thread
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                serverField.setText(serverURL);
            }
        });
    }

    public void connected(boolean connected) {
        //showFPSMeter(connected);
        //Select the appropriate camera in the menu
        CameraController cameraController = ClientContextJME.getViewManager().getCameraController();
        firstPersonRB.setSelected(cameraController.getClass() == FirstPersonCameraProcessor.class);
        thirdPersonRB.setSelected(cameraController.getClass() == ThirdPersonCameraProcessor.class);
        frontPersonRB.setSelected(cameraController.getClass() == FrontHackPersonCameraProcessor.class);
    }

    /**
     * Add a camera menu item to the end of the View menu.
     *
     * @param cameraMenuItem
     */
    public void addToCameraChoices(JRadioButtonMenuItem cameraMenuItem) {
        addToCameraChoices(cameraMenuItem, -1);
    }

    /**
     * Add a camera menu item to the View menu at the specified index, where -1
     * adds to the end of the menu
     *
     * @param cameraMenuItem
     */
    public void addToCameraChoices(
            JRadioButtonMenuItem cameraMenuItem, int index) {
        final int indexFinal = index;
        final JRadioButtonMenuItem itemFinal = cameraMenuItem;
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                addToViewMenu(itemFinal, indexFinal);
                cameraButtonGroup.add(itemFinal);
            }
        });
    }

    /**
     * Removes the specified camera choice
     * @param menuItem
     */
    public void removeFromCameraChoices(JRadioButtonMenuItem menuItem) {
        cameraButtonGroup.remove(menuItem);
        removeFromViewMenu(menuItem);
    }

    public void showFPSMeter(boolean visible) {
        if (visible) {
            if (chart == null) {
                // display FPS meter
                HUD mainHUD = HUDManagerFactory.getHUDManager().getHUD("main");

                // create fps Swing control
                chart = new Chart(BUNDLE.getString("fps:"));
                chart.setSampleSize(200);
                chart.setMaxValue(desiredFrameRate);
                chart.setPreferredSize(new Dimension(200, 34));

                // create HUD control panel
                fpsComponent = mainHUD.createComponent(chart);
                fpsComponent.setDecoratable(false);
                fpsComponent.setPreferredLocation(Layout.SOUTHEAST);

                // add HUD control panel to HUD
                mainHUD.addComponent(fpsComponent);

                removeFrameRateListener(frameRateListener);
                frameRateListener = addFrameRateListener(desiredFrameRate);
            }
        } else {
            removeFrameRateListener(frameRateListener);
        }
        fpsComponent.setVisible(visible);
        fpsMI.setSelected(visible);
    }

    public FrameRateListener addFrameRateListener(int frameRate) {
        FrameRateListener listener = new FrameRateListener() {

            public void currentFramerate(float fps) {
                if (chart != null) {
                    chart.setValue(fps);
                }
            }
        };
        ClientContextJME.getWorldManager().getRenderManager().
                setFrameRateListener(listener, frameRate);

        return listener;
    }

    public void removeFrameRateListener(FrameRateListener listener) {
        if (listener != null) {
            ClientContextJME.getWorldManager().getRenderManager().
                    setFrameRateListener(null, desiredFrameRate);
            frameRateListener = null;
        }
    }

    public void addServerURLListener(ServerURLListener listener) {
        serverListener = listener;
    }

    /** 
     * A custom menu bar that only passes on key accelerator events if the
     * global focus entity has focus (ie it ignore keys when a app has control)
     */
    static class MainMenuBar extends JMenuBar {
        @Override
        protected boolean processKeyBinding(KeyStroke ks, KeyEvent e,
                                            int condition, boolean pressed)
        {
            // OWL issue #124: ignore keyboard accelerators if anything but
            // the global entity has keyboard focus
            Entity global = InputManager.inputManager().getGlobalFocusEntity();

            if (!InputManager.entityHasFocus(e, global)) {
                // if the global entity does not have focus, ignore the event
                return false;
            }

            return super.processKeyBinding(ks, e, condition, pressed);
        }
    }

    /**
     * An extension of ButtonGroup that allows for cycling.
     */
    static class CameraButtonGroup extends ButtonGroup {
        public void next() {
            int curIdx = 0;

            // find the index of the current button
            ButtonModel cur = getSelection();
            for (int i = 0; i < buttons.size(); i++) {
                if (buttons.get(i).getModel().equals(cur)) {
                    curIdx = i;
                    break;
                }
            }

            // get the next index and select the corresponding button
            int idx = (curIdx + 1) % buttons.size();
            buttons.get(idx).doClick();
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

        serverPanel = new javax.swing.JPanel();
        serverLabel = new javax.swing.JLabel();
        serverField = new javax.swing.JTextField();
        goButton = new javax.swing.JButton();
        centerPanel = new javax.swing.JPanel();
        mainMenuBar = new MainMenuBar();
        fileMenu = new javax.swing.JMenu();
        editMenu = new javax.swing.JMenu();
        viewMenu = new javax.swing.JMenu();
        insertMenu = new javax.swing.JMenu();
        placemarksMenu = new javax.swing.JMenu();
        toolsMenu = new javax.swing.JMenu();
        windowMenu = new javax.swing.JMenu();
        helpMenu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        serverPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        serverPanel.setPreferredSize(new java.awt.Dimension(35, 35));
        serverPanel.setLayout(new java.awt.BorderLayout());

        serverLabel.setFont(serverLabel.getFont());
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/client/jme/resources/Bundle"); // NOI18N
        serverLabel.setText(bundle.getString("Location:")); // NOI18N
        serverPanel.add(serverLabel, java.awt.BorderLayout.WEST);

        serverField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                serverFieldActionPerformed(evt);
            }
        });
        serverPanel.add(serverField, java.awt.BorderLayout.CENTER);

        goButton.setText(bundle.getString("Go!")); // NOI18N
        goButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                goButtonActionPerformed(evt);
            }
        });
        serverPanel.add(goButton, java.awt.BorderLayout.EAST);

        getContentPane().add(serverPanel, java.awt.BorderLayout.NORTH);

        centerPanel.setLayout(new java.awt.BorderLayout());
        getContentPane().add(centerPanel, java.awt.BorderLayout.CENTER);

        fileMenu.setText(bundle.getString("File")); // NOI18N
        mainMenuBar.add(fileMenu);

        editMenu.setText(bundle.getString("Edit")); // NOI18N
        mainMenuBar.add(editMenu);

        java.util.ResourceBundle bundle1 = java.util.ResourceBundle.getBundle("org/jdesktop/wonderland/client/jme/Bundle"); // NOI18N
        viewMenu.setText(bundle1.getString("MainFrameImpl.viewMenu.text")); // NOI18N
        mainMenuBar.add(viewMenu);

        insertMenu.setText(bundle1.getString("MainFrameImpl.insertMenu.text")); // NOI18N
        mainMenuBar.add(insertMenu);

        placemarksMenu.setText(bundle1.getString("MainFrameImpl.placemarksMenu.text")); // NOI18N
        mainMenuBar.add(placemarksMenu);

        toolsMenu.setText(bundle.getString("Tools")); // NOI18N
        mainMenuBar.add(toolsMenu);

        windowMenu.setText(bundle1.getString("MainFrameImpl.windowMenu.text")); // NOI18N
        mainMenuBar.add(windowMenu);

        helpMenu.setText(bundle1.getString("MainFrameImpl.helpMenu.text")); // NOI18N
        mainMenuBar.add(helpMenu);

        setJMenuBar(mainMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void serverFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_serverFieldActionPerformed
    String serverText = serverField.getText();
    if (serverText != null && serverText.equals("") == false) {
        goButton.doClick();
    }
}//GEN-LAST:event_serverFieldActionPerformed

private void goButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_goButtonActionPerformed
    LOGGER.info("[MainFrameImp] GO! " + serverField.getText());

    if (serverListener != null) {
        serverListener.serverURLChanged(serverField.getText());
    }
}//GEN-LAST:event_goButtonActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    ClientContextJME.getWorldManager().shutdown();
}//GEN-LAST:event_formWindowClosing
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel centerPanel;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JButton goButton;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenu insertMenu;
    private javax.swing.JMenuBar mainMenuBar;
    private javax.swing.JMenu placemarksMenu;
    private javax.swing.JTextField serverField;
    private javax.swing.JLabel serverLabel;
    private javax.swing.JPanel serverPanel;
    private javax.swing.JMenu toolsMenu;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JMenu windowMenu;
    // End of variables declaration//GEN-END:variables
}
