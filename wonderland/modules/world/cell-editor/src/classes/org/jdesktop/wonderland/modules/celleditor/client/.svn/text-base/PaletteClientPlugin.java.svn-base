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
package org.jdesktop.wonderland.modules.celleditor.client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JMenuItem;
import org.jdesktop.wonderland.client.BaseClientPlugin;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuActionListener;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuItemEvent;
import org.jdesktop.wonderland.client.contextmenu.ContextMenuItem;
import org.jdesktop.wonderland.client.contextmenu.SimpleContextMenuItem;
import org.jdesktop.wonderland.client.contextmenu.annotation.ContextMenuFactory;
import org.jdesktop.wonderland.client.contextmenu.spi.ContextMenuFactorySPI;
import org.jdesktop.wonderland.client.jme.JmeClientMain;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.client.scenemanager.event.ContextEvent;
import org.jdesktop.wonderland.common.annotation.Plugin;

/**
 * Client-size plugin for the cell editor. Installs a "Cell Editor" menu item
 * under "Tools"
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
@Plugin
@ContextMenuFactory
public class PaletteClientPlugin extends BaseClientPlugin
        implements ContextMenuFactorySPI {

    private static final Logger LOGGER =
            Logger.getLogger(PaletteClientPlugin.class.getName());

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "org/jdesktop/wonderland/modules/celleditor/client/resources/Bundle");

    private JMenuItem editorMI = null;
    private WeakReference<CellPropertiesJFrame> cellPropertiesFrameRef = null;

    @Override
    public void initialize(ServerSessionManager loginInfo) {
        // Create the Palette menu and the Cell submenu and dialog that lets
        // users create new cells.  The menu will be added when our server
        // becomes primary.
        editorMI = new JMenuItem(BUNDLE.getString("Cell_Editor"));
        editorMI.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                CellPropertiesJFrame frame = getCellPropertiesJFrame();
                if (frame.isVisible() == false) {
                    frame.setSelectedCell(null);
                    frame.setSize(800, 650);
                    frame.setVisible(true);
                }
            }
        });

        super.initialize(loginInfo);
    }

    /**
     * Notification that our server is now the the primary server
     */
    @Override
    protected void activate() {
        // activate
        JmeClientMain.getFrame().addToToolsMenu(editorMI, 5);
    }

    @Override
    protected void deactivate() {
        // deactivate
        JmeClientMain.getFrame().removeFromToolsMenu(editorMI);
    }

    /**
     * @inheritDoc()
     */
    public ContextMenuItem[] getContextMenuItems(ContextEvent event) {
        return new ContextMenuItem[] {
                    new SimpleContextMenuItem(BUNDLE.getString("Properties..."),
                    null, new PropertiesListener())
        };
    }

    /**
     * Returns the single CellPropertiesJFrame for the system, creating it if
     * necessary
     */
    private CellPropertiesJFrame getCellPropertiesJFrame() {
        if (cellPropertiesFrameRef == null || cellPropertiesFrameRef.get() == null) {
            CellPropertiesJFrame cellPropertiesFrame = new CellPropertiesJFrame();
            cellPropertiesFrameRef = new WeakReference(cellPropertiesFrame);
            return cellPropertiesFrame;
        }
        else {
            return cellPropertiesFrameRef.get();
        }
    }

    /**
     * Listener class for the "Properties..." context menu item
     */
    private class PropertiesListener implements ContextMenuActionListener {

        public void actionPerformed(ContextMenuItemEvent event) {
            // Create a new cell edit frame passing in the Cell and make
            // it visible
            Cell cell = event.getCell();
            try {
                CellPropertiesJFrame frame = getCellPropertiesJFrame();
                frame.setSelectedCell(cell);
                frame.setSize(800, 650);
                frame.setVisible(true);
            } catch (IllegalStateException excp) {
                LOGGER.log(Level.WARNING, null, excp);
            }
        }
    }
}
