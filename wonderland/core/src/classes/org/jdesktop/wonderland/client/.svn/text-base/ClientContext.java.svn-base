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
package org.jdesktop.wonderland.client;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.CellCache;
import org.jdesktop.wonderland.client.cell.CellManager;
import org.jdesktop.wonderland.client.cell.CellRenderer;
import org.jdesktop.wonderland.client.comms.WonderlandSession;
import org.jdesktop.wonderland.client.input.InputManager;
import org.jdesktop.wonderland.client.login.ServerSessionManager;
import org.jdesktop.wonderland.client.login.LoginManager;
import org.jdesktop.wonderland.common.ExperimentalAPI;
import org.jdesktop.wonderland.common.InternalAPI;

/**
 * Provides global static access to the various client subsystems.
 * 
 * @author paulby
 */
@ExperimentalAPI
public class ClientContext {

    private static final Logger logger =
            Logger.getLogger(ClientContext.class.getName());
    private static final String USERDIR_PROP = "wonderland.user.dir";

    private static HashMap<WonderlandSession, CellCache> cellCaches=null;
    private static InputManager inputManager=null;
    private static File userDir;

    private static Cell.RendererType currentRendererType = Cell.RendererType.RENDERER_JME;
    
    /**
     * Return the CellCache if the session has one, otherwise
     * return null
     * @return
     */
    public static CellCache getCellCache(WonderlandSession session) {
        if (cellCaches==null)
            return null;
        return cellCaches.get(session);
    }
    
    /**
     * Register the implementation of CellCache for the session. This
     * call can only be made once. If you attempt to call this method more
     * than once a RuntimeException will be thrown;
     * @param clientCellCache
     */
    @InternalAPI
    public static void registerCellCache(CellCache clientCellCache, WonderlandSession session) {
        if (cellCaches==null) {
            cellCaches = new HashMap();
        }
        
        CellCache previous = cellCaches.put(session, clientCellCache);
        
        if (previous!=null)
            throw new RuntimeException("registerCellCache can only be called once");
    }
    
    /**
     * Return the CellManager for this client
     * @return
     */
    public static CellManager getCellManager() {
        return CellManager.getCellManager();
    }
    
    /**
     * 
     * @param regInputManager
     */
    @InternalAPI
    public static void registerInputManager(InputManager regInputManager) {
        if (inputManager!=null)
            throw new RuntimeException("registerInputManager can only be called once");
        
        inputManager = regInputManager;
    }

    public static InputManager getInputManager() {
        return inputManager;
    }

    public static ServerSessionManager getSessionManager(String serverURL)
        throws IOException
    {
        return LoginManager.getSessionManager(serverURL);
    }

    /**
     * Set the default render type for a client. This needs to be set
     * very early during startup and should not be changed once the system
     * is running.
     * @param rendererType
     */
    public static void setRendererType(Cell.RendererType rendererType) {
        currentRendererType = rendererType;
    }

    /**
     * Return the default render type for a client
     * @return
     */
    public static Cell.RendererType getRendererType() {
        return currentRendererType;
    }

    /**
     * Return the wonderland user directory for this user
     * @return the user directory
     */
    public synchronized static File getUserDirectory() {
        if (userDir != null) {
            return userDir;
        }

        // first try the preference set up in the login dialog
        Preferences prefs = Preferences.userNodeForPackage(ClientContext.class);
        String userDirName = prefs.get(USERDIR_PROP, null);
        if (userDirName != null) {
            // if the user has set a preference for a directory, but that
            // directory doesn't exist, don't create it, just fall back to
            // what we would have used otherwise. This covers the case of
            // removable drives, etc
            File f = new File(userDirName);
            if (!f.exists() || !f.isDirectory()) {
                logger.warning("User directory " + userDirName + " does not " +
                        "exist. Falling back to default.");
                userDirName = null;
            }
        }
        
        // next try the system property
        if (userDirName == null) {
            userDirName = System.getProperty(USERDIR_PROP);
        }

        // if neither the preference or the system property are set, use
        // a default value based on the Wonderland version
        if (userDirName == null) {
            String version = System.getProperty("wonderland.version");
            userDirName = System.getProperty("user.home") + File.separator + 
                          ".wonderland" + File.separator + version;
        }
        
        File out = new File(userDirName);
        if (!out.exists()) {
            if (!out.mkdirs()) {
                logger.log(Level.WARNING, "Error trying to create user " +
                           "directory: " + out);
                
                out = new File(System.getProperty("java.io.tmpdir"));
            }
        }
        
        userDir = out;
        return out;
    }

    /**
     * Return a subdirectory of the wonderland user directory for this user
     * @param dirName the name of the subdirectory
     * @return the user directory subdirectory, created if it doesn't exist
     */
    public synchronized static File getUserDirectory(String dirName) {
        File dir = new File(getUserDirectory(), dirName);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dir;
    }

    /**
     * @InternalAPI
     * Set the default user directory.  This is useful in cases where
     * multiple clients are running in the same VM, so changing the
     * System property is not viable.  In other cases, the system
     * property should be used.
     * @param userDirectory the user directory to use
     * @param save if true, save this value in a preference for future use
     */
    public static void setUserDirectory(File userDir, boolean save) {
        if (save) {
            Preferences prefs = Preferences.userNodeForPackage(ClientContext.class);
            prefs.put(USERDIR_PROP, userDir.getPath());
        }

        ClientContext.userDir = userDir;
    }

    /**
     * @InternalAPI
     * Reset the user directory by removing the preference.
     */
    public static void resetUserDirectory() {
        Preferences prefs = Preferences.userNodeForPackage(ClientContext.class);
        prefs.remove(USERDIR_PROP);

        // reset the user directory to the default value
        ClientContext.userDir = null;
        ClientContext.userDir = getUserDirectory();
    }
}
