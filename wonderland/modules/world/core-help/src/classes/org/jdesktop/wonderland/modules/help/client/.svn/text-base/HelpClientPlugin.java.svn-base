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
package org.jdesktop.wonderland.modules.help.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import org.jdesktop.wonderland.client.BaseClientPlugin;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.annotation.Plugin;
import org.jdesktop.wonderland.client.help.WebBrowserLauncher;

/**
 * Client-side plugin for the core help menu items.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@Plugin
public class HelpClientPlugin extends BaseClientPlugin {

    // The error logger
    private static final Logger LOGGER =
            Logger.getLogger(HelpClientPlugin.class.getName());

    // The I18N resource bundle
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/help/client/resources/Bundle");

    // An ordered list of menu items to add
    private List<JMenuItem> menuItemList = null;

    // An array of Help menu item (String key names in resource Bundle files)
    // and the URLs to launch
    private static final String HELP_ITEMS[][] = {
        {"User_Guide", "http://openwonderland.org/index.php?option=com_content&view=article&id=95&Itemid=94"},
        {"Blog", "http://blogs.openwonderland.org/"},
        {"Forum", "http://groups.google.com/group/openwonderland"},
        {"Tutorials", "http://code.google.com/p/openwonderland/wiki/OpenWonderland"},
        {"Report_Bug", "http://code.google.com/p/openwonderland/issues/list"},
        {"About", "http://www.openwonderland.org"}
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(ServerSessionManager loginInfo) {
        // Create the Help menu items in order from the given array
        menuItemList = new LinkedList<JMenuItem>();
        for (String[] item : HELP_ITEMS) {
            JMenuItem mi = getURLMenuItem(BUNDLE.getString(item[0]), item[1]);
            if (mi != null) {
                menuItemList.add(mi);
            }
        }

        super.initialize(loginInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void activate() {
        // Adds all of the menu items in the list. We make sure they are added
        // at the top of the Help menu
        int index = 0;
        for (JMenuItem menuItem : menuItemList) {
            JmeClientMain.getFrame().addToHelpMenu(menuItem, index);
            index++;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void deactivate() {
        // Removes all of the menu items in the list
        for (JMenuItem menuItem : menuItemList) {
            JmeClientMain.getFrame().removeFromHelpMenu(menuItem);
        }
    }

    /**
     * Creates and returns a new menu item that launches a browser that shows
     * a web page. This method takes the name of the menu item and the String
     * URL of the page to launch.
     *
     * @param name The name of the menu item
     * @param url The String URL of the page to load
     * @return A JMenuItem
     */
    private JMenuItem getURLMenuItem(String name, final String url) {
        JMenuItem menuItem = new JMenuItem(name);
        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    WebBrowserLauncher.openURL(url);
                } catch (Exception excp) {
                    LOGGER.log(Level.WARNING, "Failed to open Help URL: " +
                            url, excp);
                }
            }
        });
        return menuItem;
    }
}
