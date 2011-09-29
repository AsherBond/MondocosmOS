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
package org.jdesktop.wonderland.modules.contextmenu.client.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuItemEvent;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuItem;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuActionListener;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuEvent;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuInvocationSettings;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuItem.MenuItemRepaintListener;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuManager;
import org.jdesktop.wonderland.client.contextmenu.SimpleContextMenuItem;
import org.jdesktop.wonderland.client.contextmenu.cell.ContextMenuComponent;
import org.jdesktop.wonderland.client.contextmenu.spi.ContextMenuFactorySPI;
import org.jdesktop.wonderland.client.input.Event;
import org.jdesktop.wonderland.client.input.EventClassListener;
import org.jdesktop.wonderland.client.scenemanager.SceneManager;
import org.jdesktop.wonderland.client.scenemanager.event.ActivatedEvent;
import org.jdesktop.wonderland.client.scenemanager.event.ContextEvent;
import org.jdesktop.wonderland.client.scenemanager.event.SelectionEvent;

/**
 * A Swing-based implementation of the system context menu.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class SwingContextMenu implements MenuItemRepaintListener {

    private static Logger logger = Logger.getLogger(SwingContextMenu.class.getName());
    private JFrame contextMenu = null;
    private JPanel contextPanel = null;
    private Cell popupCell = null;
    private Map<JMenuItem, ContextMenuItem> menuItemMap = new HashMap();
    private Map<ContextMenuItem, JMenuItem> reverseMenuMap = new HashMap();

    // Colors matching the Wonderland logo color scheme
    private Color WL_LIGHT_BLUE = new Color(12, 104, 234);
    private Color WL_BLUE = new Color(7, 73, 165);
    private Color WL_LIGHT_GREEN = new Color(61, 207, 60);
    private Color WL_GREEN = new Color(45, 164, 48);

    // the context selection listener we registered
    private final ContextSelectionListener listener = new ContextSelectionListener();

    /** Constructor */
    public SwingContextMenu() {
        // Initialize the GUI.
        contextMenu = new JFrame();
        contextMenu.setResizable(false);
        contextMenu.setUndecorated(true);
        contextMenu.getContentPane().setLayout(new GridLayout(1, 1));

        contextPanel = new JPanel();
        contextMenu.getContentPane().add(contextPanel);
        contextPanel.setBorder(BorderFactory.createLineBorder(WL_LIGHT_BLUE, 2));
        contextPanel.setLayout(new BoxLayout(contextPanel, BoxLayout.Y_AXIS));
    }

    /**
     * Add ourself as a global event listener
     */
    public void register() {
         // Register a global listener for context and selection events
        SceneManager.getSceneManager().addSceneListener(listener);
    }

    /**
     * Remove ourself from being a global event listener
     */
    public void unregister() {
        SceneManager.getSceneManager().removeSceneListener(listener);
    }

    /**
     * This method is synchronized so that the possible repaints don't happen
     * while we are still constructing the menu in initializeMenu().
     */
    public synchronized void repaintMenuItem(ContextMenuItem menuItem) {
        // Tell the menu item to repaint itself. Reset the values on the
        // context menu, tell it to repaint.
        JMenuItem item = reverseMenuMap.get(menuItem);
        if (menuItem instanceof SimpleContextMenuItem) {
            SimpleContextMenuItem scmi = (SimpleContextMenuItem)menuItem;
            item.setText(scmi.getLabel());
            item.setEnabled(scmi.isEnabled());
            if (scmi.getImage() != null) {
                item.setIcon(new ImageIcon(scmi.getImage()));
            }
            item.repaint();
            contextMenu.pack();
            contextMenu.repaint();
        }
    }

    /**
     * Initialize the context menu items. This is synchronized so nothing else
     * can do stuff while the context menu is being created. An example of
     * would be repaintMenuItem() which may try to access the context menu
     * before it has been created.
     */
    private synchronized void initializeMenu(ContextEvent event, Cell cell) {
        // Loop through any menu item and remove the listener
        for (Map.Entry<JMenuItem, ContextMenuItem> entry : menuItemMap.entrySet()) {
            entry.getValue().removeMenuItemRepaintListener(this);
        }

        // Clear out any existing entries in the context menu
        contextPanel.removeAll();
        menuItemMap.clear();
        reverseMenuMap.clear();

        
        // create new ContextMenuEvent
        ContextMenuEvent ctxEvent = new ContextMenuEvent(event, cell);

        // Tell the context menu listeners that we are about to display a
        // context menu, giving listeners an opportunity to make adjustments
        ContextMenuManager.getContextMenuManager().fireContextMenuEvent(ctxEvent);

        // get the settings from the event, possibly adjusted by listeners
        ContextMenuInvocationSettings settings = ctxEvent.getSettings();
        // Adjust name of menu (by default, this is the cell's name)
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        titlePanel.setBackground(WL_BLUE);
        JLabel title = new JLabel("<html><b>" + settings.getMenuName() + "</b></html>");
        title.setForeground(Color.WHITE);
        title.setBackground(Color.GRAY);
        titlePanel.add(title);
        contextPanel.add(titlePanel);
        contextPanel.invalidate();
        
        // Fetch the manager of the context menu
        ContextMenuManager cmm = ContextMenuManager.getContextMenuManager();
        
        // fetch context menu component
        ContextMenuComponent cmc = cell.getComponent(ContextMenuComponent.class);
        
        // show standard menu items?
        if (settings.isDisplayStandard() == true) {
          // fetch standard items from CMM
          List<ContextMenuFactorySPI> factoryList = cmm.getContextMenuFactoryList();
          // add each item to the menu
          for (ContextMenuFactorySPI factory : factoryList) {
              ContextMenuItem items[] = factory.getContextMenuItems(event);
              for (ContextMenuItem item : items) {
                  addContextMenuItem(item, cell);
              }
          }
        }

        // show cell-specific standard items?
        if (settings.isDisplayCellStandard() && cmc != null) {
          // fetch standard factories from CMC
          ContextMenuFactorySPI factories[] = cmc.getContextMenuFactories();
          // add each item to the menu
          for (ContextMenuFactorySPI factory : factories) {
              ContextMenuItem items[] = factory.getContextMenuItems(event);
              for (ContextMenuItem item : items) {
                  addContextMenuItem(item, cell);
              }
          }
        }

        // show temporary items?

        if (settings.isDisplayTemporaryFactories()) {
          // fetch standard factories from CMC
          List<ContextMenuFactorySPI> factoryList = settings.getFactoryList();
          // add each item to the menu
          for (ContextMenuFactorySPI factory : factoryList) {
              ContextMenuItem items[] = factory.getContextMenuItems(event);
              for (ContextMenuItem item : items) {
                  addContextMenuItem(item, cell);
              }
          }
        }
    }

    /**
     * Adds a context menu entry.
     *
     * @param menuItem The new context menu item
     */
    private void addContextMenuItem(ContextMenuItem menuItem, Cell cell) {

        // Only support SimpleContextMenuItems for now!
        if (!(menuItem instanceof SimpleContextMenuItem)) {
            logger.warning("Menu item type not supported: " + menuItem);
            return;
        }
        SimpleContextMenuItem simpleItem = (SimpleContextMenuItem)menuItem;

        // Creates the context menu item, using the image as an icon if it
        // exists.
        String name = simpleItem.getLabel();
        Image image = simpleItem.getImage();
        boolean isEnabled = simpleItem.isEnabled();

        JMenuItem item = null;
        if (image == null) {
            item = new JMenuItem(name);
        }
        else {
            item = new JMenuItem(name, new ImageIcon(image));
        }

        // Try to make the menu item look a bit nicer
        item.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 7));
        item.addMouseListener(new LabelListener(name));

        // If the state of the menu item is "DISABLED" then grey out the item
        item.setEnabled(isEnabled);

        // Add the item to the menu
        contextPanel.add(item);
        contextPanel.invalidate();
        contextMenu.pack();

        // Listen for changes to each menu item.
        menuItem.addMenuItemRepaintListener(this);

        // Add an entry to the map of each menu item
        menuItemMap.put(item, menuItem);
        reverseMenuMap.put(menuItem, item);
    }

    /**
     * Shows the Entity context menu given the AWT MouseEvent and the Entity
     * associated with the mouse click
     */
    private void showContextMenu(MouseEvent event, Cell cell) {
        // Check if there is an existing popup menu visible and make
        // it invisible
        if (contextMenu.isVisible() == true) {
            contextMenu.setVisible(false);
        }

        // Make the popup menu visible in the new locatio
        popupCell = cell;
        Component component = event.getComponent();
        Point parentPoint = new Point(component.getLocationOnScreen());
        parentPoint.translate(event.getX(), event.getY());
        contextMenu.setLocation(parentPoint);
        contextMenu.setVisible(true);
        contextMenu.toFront();
        contextMenu.pack();
        contextMenu.repaint();
    }

    /**
     * Hides the context menu.
     */
    private void hideContextMenu() {
        contextMenu.setVisible(false);
    }

    /**
     * Listeners for clicks on any contxt menu element
     */
    class LabelListener extends MouseAdapter {

        /* The original text of the string */
        public String text = null;

        /** Constructor, takes the index of the element */
        public LabelListener(String text) {
            this.text = text;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            // Fetch the source for this event. Check to see if it is enabled,
            // if not then just return
            JMenuItem item = (JMenuItem)e.getSource();
            if (item.isEnabled() == false) {
                return;
            }

            // Otherwise, highlight the menu item and hide the menu
            item.setBackground(WL_GREEN);
            item.repaint();
            hideContextMenu();

            // Find the listener to dispatch the action to
            ContextMenuItem menuItem = menuItemMap.get(item);
            if (menuItem != null && menuItem instanceof SimpleContextMenuItem) {
                // Only deal with SimpleMenuItems for now
                SimpleContextMenuItem scmi = (SimpleContextMenuItem)menuItem;
                ContextMenuActionListener listener = scmi.getActionListener();
                if (listener != null) {
                    listener.actionPerformed(
                            new ContextMenuItemEvent(menuItem, popupCell));
                }
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // Highlight the menu item with a color, but only if it is enabled
            JMenuItem item = (JMenuItem)e.getSource();
            if (item.isEnabled() == true) {
                item.setBackground(WL_LIGHT_GREEN);
                item.setOpaque(true);
                item.repaint();
                contextMenu.pack();
            }
        }

        @Override
        public void mouseExited(MouseEvent e) {
            // De-highlight the menu item with a color, but only if it is enabled
            JMenuItem item = (JMenuItem) e.getSource();
            if (item.isEnabled() == true) {
                item.setBackground(Color.white);
                contextMenu.pack();
            }
        }
    }

    /**
     * Inner class that listeners for context and selection events
     */
    class ContextSelectionListener extends EventClassListener {

        public ContextSelectionListener() {
            setSwingSafe(true);
        }

        @Override
        public Class[] eventClassesToConsume() {
            return new Class[] {
                ActivatedEvent.class, ContextEvent.class, SelectionEvent.class
            };
        }

        @Override
        public void commitEvent(Event event) {

            // Go ahead and either close the context menu or show the context
            // menu.
            if (event instanceof ActivatedEvent || event instanceof SelectionEvent) {
                // Hide the context menu in the AWT Event Thread so that we do
                // not interfere with the MT Game Render thread
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        hideContextMenu();
                    }
                });
            }
            else if (event instanceof ContextEvent) {
                // Show the context menu, initialize the menu if this is the
                // first time
                final Cell cell = ((ContextEvent)event).getPrimaryCell();
                if (cell == null) {
                    // Hide the context menu in the AWT Event Thread so that we do
                    // not interfere with the MT Game Render thread
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            hideContextMenu();
                        }
                    });
                    return;
                }

                // Initialize the context menu in the AWT Event Thread so that
                // we do not interfere with the MT Game Render thread
                final ContextEvent ce = (ContextEvent) event;
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        initializeMenu(ce, cell);
                        showContextMenu(ce.getMouseEvent(), cell);
                    }
                });
            }
        }
    }
}
