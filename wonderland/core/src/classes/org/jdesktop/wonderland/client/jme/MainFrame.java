/**
 * Open Wonderland
 *
 * Copyright (c) 2011, Open Wonderland Foundation, All Rights Reserved
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
package org.jdesktop.wonderland.client.jme;

import java.awt.Canvas;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;

/**
 * An interface for interacting with the main JME frame
 * @author jkaplan
 */
public interface MainFrame {

    /**
     * Get the underlying Swing frame object
     * @return the Swing frame
     */
    public JFrame getFrame();

    /**
     * Returns the canvas of the frame.
     */
    public Canvas getCanvas();

    /**
     * Returns the panel of the frame in which the 3D canvas resides.
     */
    public JPanel getCanvas3DPanel();

    /**
     * Add a menu item to a menu at the specified index, where -1 adds the
     * menu item to the end of the menu
     *
     * @param menu the menu to add the item to
     * @param menuItem the item to add
     * @param index the position in the menu
     */
    public void addToMenu(JMenu menu, JMenuItem menuItem, int index);

    /**
     * Add the specified menu item to the end of the File menu
     *
     * @param menuItem
     */
    public void addToFileMenu(JMenuItem menuItem);

    /**
     * Add the specified menu item to the edit menu.
     * Add a menu item to the File menu at the specified index, where -1 adds
     * the menu item to the end of the menu
     *
     * @param menuItem
     * @param index the position in the menu
     */
    public void addToFileMenu(JMenuItem menuItem, int index);

    /**
     * Remove the specified menu item from the File menu.
     *
     * @param menuItem
     */
    public void removeFromFileMenu(JMenuItem menuItem);

    /**
     * Add the specified menu item to the end of the Edit menu
     *
     * @param menuItem
     */
    public void addToEditMenu(JMenuItem menuItem);

    /**
     * Add a menu item to the Edit menu at the specified index, where -1 adds
     * the menu item to the end of the menu
     *
     * @param menuItem
     * @param index the position in the menu
     */
    public void addToEditMenu(JMenuItem menuItem, int index);

    /**
     * Remove the specified menu item from the Edit menu.
     *
     * @param menuItem
     */
    public void removeFromEditMenu(JMenuItem menuItem);

    /**
     * Add the specified menu item to the end of the View menu
     *
     * @param menuItem
     */
    public void addToViewMenu(JMenuItem menuItem);

    /**
     * Add a menu item to the View menu at the specified index, where -1 adds
     * the menu item to the end of the menu
     *
     * @param menuItem
     */
    public void addToViewMenu(JMenuItem menuItem, int index);

    /**
     * Remove the specified menu item from the View menu.
     *
     * @param menuItem
     */
    public void removeFromViewMenu(JMenuItem menuItem);

    /**
     * Add the specified menu item to the end of the Insert menu
     *
     * @param menuItem
     */
    public void addToInsertMenu(JMenuItem menuItem);

    /**
     * Add a menu item to the Insert menu at the specified index, where -1 adds
     * the menu item to the end of the menu
     *
     * @param menuItem
     */
    public void addToInsertMenu(JMenuItem menuItem, int index);

    /**
     * Remove the specified menu item from the Insert menu.
     *
     * @param menuItem
     */
    public void removeFromInsertMenu(JMenuItem menuItem);

    /**
     * Add the specified menu item to the end of the Tools menu
     *
     * @param menuItem
     */
    public void addToToolsMenu(JMenuItem menuItem);

    /**
     * Add a menu item to the Tools menu at the specified index, where -1 adds
     * the menu item to the end of the menu
     *
     * @param menuItem
     */
    public void addToToolsMenu(JMenuItem menuItem, int index);

    /**
     * Remove the specified menu item from the Tools menu.
     *
     * @param menuItem
     */
    public void removeFromToolsMenu(JMenuItem menuItem);

    /**
     * Add the specified menu item to the end of the Placemarks menu
     *
     * @param menuItem
     */
    public void addToPlacemarksMenu(JMenuItem menuItem);

    /**
     * Three types of Placemarks are expected:
     *   - Managements placemarks affect management (add, manage) and also include the Starting Location
     *   - System placemarks are attached to objects through the placemark capability
     *   - User placemarks are personal and created by the user through the management tools
     * The reason to declare them here and not in the Placemark API project is that the differences
     * only matter at the time of displaying them in the Placemark menu. A possible refactoring
     * could expose the menu bar to modules and let them manage the population of items. In that case
     * all this code could be moved and contained within the placemark module.
     */
    public enum PlacemarkType { MANAGEMENT, SYSTEM, USER }

     /**
     * Add a menu item to the Placemarks menu at the specified index, where -1
     * adds the menu item to the end of the menu.
     * Weights are not being used (anymore) for alphabetising placemarks as it was
     * impossible to use JSeparators(JComponents) in the Menu.
     * @param menuItem
     * @param index the position in the menu
     * @param placemarkType The type of the placemark (Management, User or System)
     * used for displaying purposes when organising the menu
     */
    public void addToPlacemarksMenu(JMenuItem menuItem, int index, PlacemarkType placemarktype);

    /**
     * Remove the specified menu item from the Placemarks menu.
     *
     * @param menuItem
     */
    public void removeFromPlacemarksMenu(JMenuItem menuItem);

    /**
     * Add the specified menu item to the end of the Window menu
     *
     * @param menuItem
     */
    public void addToWindowMenu(JMenuItem menuItem);

    /**
     * Add a menu item to the Window menu at the specified index, where -1 adds
     * the menu item to the end of the menu
     *
     * @param menuItem
     * @param index the position in the menu
     */
    public void addToWindowMenu(JMenuItem menuItem, int index);

    /**
     * Remove the specified menu item from the Window menu.
     *
     * @param menuItem
     */
    public void removeFromWindowMenu(JMenuItem menuItem);

    /**
     * Add the specified menu item to the end of the Help menu
     *
     * @param menuItem
     */
    public void addToHelpMenu(JMenuItem menuItem);

    /**
     * Add a menu item to the Help menu at the specified index, where -1 adds
     * the menu item to the end of the menu
     *
     * @param menuItem
     * @param index the position in the menu
     */
    public void addToHelpMenu(JMenuItem menuItem, int index);

    /**
     * Remove the specified menu item from the Help menu.
     *
     * @param menuItem
     */
    public void removeFromHelpMenu(JMenuItem menuItem);

    /**
     * Set the server URL in the location field
     * @param serverURL the server URL to set
     */
    public void setServerURL(String serverURL);

    /**
     * Add a listener that will be notified when the server URL changes
     * (i.e. when the user types a new location in the location bar)
     * @param listener the listener to add
     */
    public void addServerURLListener(ServerURLListener listener);

    /**
     * Notify when a connection is established or disconnected
     * @param connected true if a connection has been established, false otherwise
     */
    public void connected(boolean connected);

    /**
     * Set the desired frame rate
     * @param desiredFrameRate the desired frame rate in frames per second
     */
    public void setDesiredFrameRate(int desiredFrameRate);

    /**
     * Add a camera menu item to the View menu at the specified index, where -1 adds
     * to the end of the menu
     * @param cameraMenuItem the menu item to activate this camera.  The menu item
     * should have a listener that activates the given camera on selection.
     */
    public void addToCameraChoices(JRadioButtonMenuItem cameraMenuItem, int index);

     /**
     * Removes the specified camera choice
     * @param menuItem the previously-added camera choice
     */
    public void removeFromCameraChoices(JRadioButtonMenuItem menuItem);

    /**
     * A listener that will be notified when the server URL changes
     */
    public interface ServerURLListener {

        /**
         * A request to change the server URL to the given URL
         * @param serverURL the new server URL to connect to
         */
        public void serverURLChanged(String serverURL);

        /**
         * A request to log out of the current server
         */
        public void logout();
    }
}
