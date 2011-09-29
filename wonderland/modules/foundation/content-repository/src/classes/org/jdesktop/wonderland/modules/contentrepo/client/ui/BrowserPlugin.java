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
package org.jdesktop.wonderland.modules.contentrepo.client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import org.jdesktop.wonderland.client.BaseClientPlugin;
import org.jdesktop.wonderland.client.cell.registry.CellRegistry;
import org.jdesktop.wonderland.client.cell.registry.spi.CellFactorySPI;
import org.jdesktop.wonderland.client.cell.utils.CellCreationException;
import org.jdesktop.wonderland.client.cell.utils.CellUtils;
import org.jdesktop.wonderland.client.content.ContentBrowserManager;
import org.jdesktop.wonderland.client.content.spi.ContentBrowserSPI.BrowserAction;
import org.jdesktop.wonderland.client.content.spi.ContentBrowserSPI.ContentBrowserListener;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.common.annotation.Plugin;
import org.jdesktop.wonderland.common.cell.state.CellServerState;

/**
 *
 * @author jkaplan
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
@Plugin
public class BrowserPlugin extends BaseClientPlugin {

    private static Logger logger =
            Logger.getLogger(BrowserPlugin.class.getName());
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/contentrepo/client/ui/resources/Bundle");
    private WeakReference<ContentBrowserJDialog> browserDialogRef = null;
    private JMenuItem newBrowserItem;
    private ContentBrowserJDialog defaultBrowser;

    @Override
    public void initialize(final ServerSessionManager loginInfo) {

        final ContentBrowserListener listener = new ContentBrowserListener() {

            public void okAction(String uri) {
                // Figure out what the file extension is from the uri, looking
                // for the final '.'.
                int index = uri.lastIndexOf(".");
                if (index == -1) {
                    logger.warning("Could not find extension for " + uri);
                    return;
                }
                String extension = uri.substring(index + 1);

                // Next look for a cell type that handles content with
                // this file extension and create a new cell with it.
                CellRegistry registry = CellRegistry.getCellRegistry();
                Set<CellFactorySPI> factories =
                        registry.getCellFactoriesByExtension(extension);
                if (factories == null) {
                    logger.warning(
                            "Could not find cell factory for " + extension);
                }
                CellFactorySPI factory = factories.iterator().next();

                // Create the cell, inject the content uri
                Properties props = new Properties();
                props.put("content-uri", uri);
                CellServerState state =
                        factory.getDefaultCellServerState(props);

                // Create the new cell at a distance away from the avatar
                try {
                    CellUtils.createCell(state);
                } catch (CellCreationException excp) {
                    logger.log(Level.WARNING,
                            "Unable to create cell for uri " + uri, excp);
                }
            }

            public void cancelAction() {
                // Do nothing
            }
        };

        // Add the Palette menu and the Cell submenu and dialog that lets users
        // create new cells.
        newBrowserItem = new JMenuItem(BUNDLE.getString("Content_Browser"));
        newBrowserItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                ContentBrowserJDialog contentBrowserFrame;
                if (browserDialogRef == null ||
                        browserDialogRef.get() == null) {
                    contentBrowserFrame = new ContentBrowserJDialog(loginInfo);
                    contentBrowserFrame.setModal(false);
                    contentBrowserFrame.setActionName(
                            BrowserAction.OK_ACTION,
                            BUNDLE.getString("Create"));
                    contentBrowserFrame.setActionName(
                            BrowserAction.CANCEL_ACTION,
                            BUNDLE.getString("Cancel"));

                    contentBrowserFrame.addContentBrowserListener(listener);
                    browserDialogRef = new WeakReference(contentBrowserFrame);
                } else {
                    contentBrowserFrame = browserDialogRef.get();
                }

                if (contentBrowserFrame.isVisible() == false) {
                    contentBrowserFrame.setVisible(true);
                }
            }
        });

        super.initialize(loginInfo);
    }

    @Override
    protected void activate() {
        // Register the content browser frame with the registry of such panels.
        // Do this within the AWT Event Thread to avoid some exceptions (Issue
        // #442).
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                ContentBrowserManager manager =
                        ContentBrowserManager.getContentBrowserManager();
                defaultBrowser = new ContentBrowserJDialog(getSessionManager());
                manager.setDefaultContentBrowser(defaultBrowser);
            }
        });

        // add menu items
        JmeClientMain.getFrame().addToToolsMenu(newBrowserItem, 4);
    }

    @Override
    protected void deactivate() {
        // Reset the default content browser back to null
        ContentBrowserManager manager =
                ContentBrowserManager.getContentBrowserManager();
        if (defaultBrowser == manager.getDefaultContentBrowser()) {
            manager.setDefaultContentBrowser(null);
            defaultBrowser = null;
        }

        // remove menu items
        JmeClientMain.getFrame().removeFromToolsMenu(newBrowserItem);
    }
}
