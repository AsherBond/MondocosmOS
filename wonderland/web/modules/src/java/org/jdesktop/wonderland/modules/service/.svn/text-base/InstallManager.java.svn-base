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
package org.jdesktop.wonderland.modules.service;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.jdesktop.wonderland.modules.Module;
import org.jdesktop.wonderland.modules.ModuleFactory;

/**
 * The InstallManager class manages all modules that are installed.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class InstallManager {
    /* The directory in which installed modules are kept */
    private static final String installed_DIR = "installed";
    private File installedFile = null;
    
    /* The Map of modules installed to be installed and their Module objects */
    private Map<String, Module> installedModules = null;

    /**
     * Constructor, takes the root directory of the module manager
     */
    public InstallManager(File root) {
        /* Create the installed directory if it does not exist */
        this.installedFile = new File(root, installed_DIR);
        try {
            ModuleManagerUtils.makeDirectory(this.installedFile);
        } catch (java.io.IOException excp) {
            ModuleManager.getLogger().log(Level.SEVERE,
                    "[MODULES] Failed to Create installed Directory ",
                    excp);
        }
        
        /* Read the map of installed modules */
        this.installedModules = Collections.synchronizedMap(this.fetchModules());
    }

    /**
     * Returns a map of installed modules
     */
    public Map<String, Module> getModules() {
        return this.installedModules;
    }
    
    /**
     * Removes an existing module, given its name. 
     */
    public void remove(String moduleName) {
        Logger logger = ModuleManager.getLogger();
        
        /*
         * Simply delete the directory associated with the module (quietly) and
         * remove from the list
         */
        File file = new File(this.installedFile, moduleName);
        try {
            FileUtils.deleteDirectory(file);
        } catch (IOException excp) {
            /* Log an error and continue */
            logger.log(Level.WARNING, "[MODULES] installed Failed to remove " +
                    file.getAbsolutePath());
        }
        this.installedModules.remove(moduleName);
    }
    
    /**
     * Adds a new module to installed. This simply copies files, it assumes all
     * preparations or checks have already been performed. It is given the
     * module and the File root of where to copy and returns the Module object
     * representing the installed module
     */
    public Module add(String moduleName, File root) {
        /* The error logger */
        Logger logger = ModuleManager.getLogger();
        
        /*
         * Expand the contents of the module to the installed/ directory. First
         * create a directory holding the module (but check first if it already
         * exists and log a warning message).
         */
        File file = new File(this.installedFile, moduleName);
        if (ModuleManagerUtils.makeCleanDirectory(file) == false) {
            logger.log(Level.WARNING, "[MODULES] INSTALL Failed to Create " +
                    file.getAbsolutePath());
            return null;
        }

        /* Next, expand the contents of the module into this directory */
        try {
            FileUtils.copyDirectory(root, file);
        } catch (java.io.IOException excp) {
            logger.log(Level.WARNING, "[MODULES] INSTALL Failed to Copy " +
                    root.getAbsolutePath() + " To " + file.getAbsolutePath(),
                    excp);
            return null;
        }
        
        /* Re-open module in the installed directory, add to the list */
        Module module = null;
        try {
            module = ModuleFactory.open(file);
            this.installedModules.put(moduleName, module);

            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Add installed module " + module);
            }
        } catch (java.lang.Exception excp) {
            /* Log the error and return false */
            logger.log(Level.WARNING, "[MODULES] PENDING Failed to Open Module", excp);
            return null;
        }
        return module;
    }
    
    /**
     * Returns a map of module names and objects from a given directory. If no
     * modules are present, this method returns an empty map.
     * 
     * @return An map of unique module names and their Module objects
     */
    private Map<String, Module> fetchModules() {
        Logger logger = ModuleManager.getLogger();
        Map<String, Module> map = new HashMap<String, Module>();
        
        /*
         * Loop through each file and check that it is potentially valid.
         * If so, add its name to the map of module names
         */
        File[] files = this.installedFile.listFiles();
        for (File file : files) {
            /* Attempt to create the module */
            try {
                Module module = ModuleFactory.open(file);
                map.put(module.getName(), module);

                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Load installed module " + module);
                }
            } catch (java.lang.Exception excp) {
                ModuleManager.getLogger().log(Level.WARNING,
                        "[MODULES] Invalid module " + file, excp);
            }
        }
        return map;
    }
}
