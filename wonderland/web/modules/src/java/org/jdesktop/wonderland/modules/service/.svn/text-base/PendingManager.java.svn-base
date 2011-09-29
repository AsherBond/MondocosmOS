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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.jdesktop.wonderland.modules.Module;
import org.jdesktop.wonderland.modules.ModuleFactory;

/**
 * The PendingManager class manages all modules that are pending and waiting
 * to be installed.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class PendingManager {
    /* The directory in which pending modules are kept */
    private static final String PENDING_DIR = "pending";
    private File pendingFile = null;
    
    /* The Map of modules pending to be installed and their Module objects */
    private Map<String, Module> pendingModules = null;
    
    /* The chunk size to write out while expanding archive files to disk */
    private static final int CHUNK_SIZE = (8 * 1024);

    /**
     * Constructor, takes the root directory of the module manager
     */
    public PendingManager(File root) {
        /* Create the pending directory if it does not exist */
        this.pendingFile = new File(root, PENDING_DIR);
        try {
            ModuleManagerUtils.makeDirectory(this.pendingFile);
        } catch (java.io.IOException excp) {
            ModuleManager.getLogger().log(Level.SEVERE,
                    "[MODULES] Failed to Create Pending Directory ",
                    excp);
        }
        
        /* Read the map of pending modules */
        this.pendingModules = Collections.synchronizedMap(this.fetchModules());
    }
    
    /**
     * Returns the hashmap of pending modules
     */
    public Map<String, Module> getModules() {
        return this.pendingModules;
    }
    
    /**
     * Adds a new module to be pending. Returns the new module object, or null
     * upon error.
     */
    public Module add(File jarFile) {
        /* Get the error logger */
        Logger logger = ModuleManager.getLogger();
        
        /* First attempt to open the URL as a module */
        Module module = null;
        try {
            module = ModuleFactory.open(jarFile);
        } catch (java.lang.Exception excp) {
            /* Log the error and return false */
            logger.log(Level.WARNING, "[MODULES] PENDING Failed to Open Module "
                    + jarFile, excp);
            return null;
        }
        
        /* Next, see the module already exists, log warning and continue */
        if (this.pendingModules.containsKey(module.getName()) == true) {
            logger.log(Level.INFO, "[MODULES] PENDING Module already exists "
                    + module.getName());
        }
        
        /* Add to the pending/ directory */
        File file = this.addToPending(module.getName(), jarFile);
        if (file == null) {
            logger.log(Level.WARNING, "[MODULES] PENDING Failed to add " +
                    module.getName());
            return null;
        }
        
        /* Re-open the module in the new directory */
        try {
            module = ModuleFactory.open(file);

            if (logger.isLoggable(Level.FINE)) {
                logger.fine("Add pending module " + module);
            }
        } catch (java.lang.Exception excp) {
            /* Log the error and return false */
            logger.log(Level.WARNING, "[MODULES] PENDING Failed to Open Module "
                    + file, excp);
            return null;
        }       
        /* If successful, add to the list of pending modules */
        this.pendingModules.put(module.getName(), module);
        return module;
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
        File file = new File(this.pendingFile, moduleName);
        try {
            FileUtils.deleteDirectory(file);
        } catch (IOException excp) {
            /* Log an error and continue */
            logger.log(Level.WARNING, "[MODULES] PENDING Failed to remove " +
                    file.getAbsolutePath(), excp);
        }
        this.pendingModules.remove(moduleName);
    }
    
    /**
     * Adds a new module to pending. This method expands the module (as
     * represented by a jar) to the pending directory. This simply copies files,
     * it assumes all preparations or checks have already been performed.
     */
    private File addToPending(String moduleName, File jarFile) {
        /* The error logger */
        Logger logger = ModuleManager.getLogger();
        
        /*
         * Expand the contents of the module to the pending/ directory. First
         * create a directory holding the module (but check first if it already
         * exists and log a warning message).
         */
        File file = new File(this.pendingFile, moduleName);
        if (ModuleManagerUtils.makeCleanDirectory(file) == false) {
            logger.log(Level.WARNING, "[MODULES] PENDING Failed to Create " +
                    file.getAbsolutePath());
            return null;
        }

        /* Next, expand the contents of the module into this directory */
        try {
            this.expand(file, jarFile);
        } catch (java.io.IOException excp) {
            logger.log(Level.WARNING, "[MODULES] PENDING Failed to Expand " +
                    jarFile, excp);
            return null;
        }
        return file;
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
        File[] files = this.pendingFile.listFiles();
        for (File file : files) {
            /* Attempt to create the module */
            try {
                Module module = ModuleFactory.open(file);
                map.put(module.getName(), module);

                if (logger.isLoggable(Level.FINE)) {
                    logger.fine("Load pending module " + module);
                }
            } catch (java.lang.Exception excp) {
                ModuleManager.getLogger().log(Level.WARNING,
                        "[MODULES] Invalid module " + file, excp);
            }
        }
        return map;
    }
    
    /**
     * Takes a base directory (which must exist and be readable) and expands
     * the contents of the archive module into that directory given the
     * URL of the module encoded as a jar file
     * 
     * @param root The base directory in which the module is expanded
     * @throw IOException Upon error
     */
    private void expand(File root, File jar) throws IOException {
        /*
         * Loop through each entry, fetch its input stream, and write to an
         * output stream for the file.
         */
        JarFile jarFile = new JarFile(jar);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements() == true) {
            /* Fetch the next entry, its name is the relative file name */
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            long size = entry.getSize();
            
            /* Don't expand anything that beings with META-INF */
            if (entryName.startsWith("META-INF") == true) {
                continue;
            }
            
            /* Ignore if it is a directory, then create it */
            if (entryName.endsWith("/") == true) {
                File file = new File(root, entryName);
                file.mkdirs();
                continue;
            }
            
            /* Write out to a file in 'root' */
            File file = new File(root, entryName);
            InputStream jis = jarFile.getInputStream(entry);
            FileOutputStream os = new FileOutputStream(file);
            byte[] b = new byte[PendingManager.CHUNK_SIZE];
            long read = 0;
            while (read < size) {
                int len = jis.read(b);
                if (len == -1) {
                    break;
                }
                read += len;
                os.write(b, 0, len);
            }
            jis.close();
            os.close();
        }
    }
}
