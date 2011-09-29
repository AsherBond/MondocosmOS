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
package org.jdesktop.wonderland.modules.file;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.modules.Module;
import org.jdesktop.wonderland.common.modules.ModuleInfo;
import org.jdesktop.wonderland.modules.ModulePart;
import org.jdesktop.wonderland.common.modules.ModuleRepository;
import org.jdesktop.wonderland.common.modules.ModuleRequires;
import org.jdesktop.wonderland.utils.RunUtil;

/**
 * The FileModule class extends the Module abstract base class and represents
 * all modules that exist as a collection of directories and files on disk
 * representing the structure of the module.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class FileModule extends Module {

    private static Logger logger = Logger.getLogger(FileModule.class.getName());

    /** Default constructor, takes a reference to the module directory root */
    public FileModule(File root) {
        super();
        this.setFile(root);
        
        /*
         * Fetch the module info, this is pretty bad if module.xml doesn't exist
         */
        ModuleInfo info = this.fetchModuleInfo();
        if (info == null) {
            info = new ModuleInfo();
        }
        this.setInfo(info);
        
        /*
         * Fetch the module dependencies, this isn't terrible if it doesn't exist
         */
        ModuleRequires requires = this.fetchModuleRequires();
        if (requires == null) {
            requires = new ModuleRequires();
        }
        this.setRequires(requires);
        
        /*
         * Fetch the module asset servers, this isn't terrible if it doesn't exist
         */
        ModuleRepository repository = this.fetchModuleRepository();
        if (repository == null) {
            repository = new ModuleRepository();
        }
        this.setRepository(repository);
        
        /*
         * Fetch the module parts, at least this should return an empty map
         */
        this.setParts(this.fetchModuleParts());
    }

    /**
     * Reads the module info from the module.
     */
    private ModuleInfo fetchModuleInfo() {
        FileReader reader = null;
        try {
            /* Fetch the entry, return null if it does not exist */
            File entry = new File(this.getFile(), Module.MODULE_INFO);
            if (entry.exists() == false) {
                logger.info("[MODULE] No module.xml for Module " + this.getFile());
                return null;
            }

            /* Read in the file and parse it */
            reader = new FileReader(entry);
            return ModuleInfo.decode(reader);
        } catch (java.lang.Exception excp) {
            /* This is pretty bad -- if this doesn't exist, then the module is invalid */
            logger.log(Level.WARNING, "[MODULE] Invalid Module " + this.getFile(), excp);
            return null;
        } finally {
            RunUtil.close(reader);
        }
    }

    /**
     * Reads the dependency info from the module.
     */
    private ModuleRequires fetchModuleRequires() {
        FileReader reader = null;
        try {
            /* Fetch the entry, return null if it does not exist */
            File entry = new File(this.getFile(), Module.MODULE_REQUIRES);
            if (entry.exists() == false) {
                logger.info("[MODULE] No requires.xml for Module " + this.getFile());
                return null;
            }

            /* Read in the file and parse it */
            reader = new FileReader(entry);
            return ModuleRequires.decode(reader);
        } catch (java.lang.Exception excp) {
            /* This is not too bad if it does not exist */
            logger.log(Level.INFO, "[MODULE] Error reading requires.xml for Module " + this.getFile(), excp);
            return null;
        } finally {
            RunUtil.close(reader);
        }
    }

    /**
     * Reads the asset server info from the module.
     */
    private ModuleRepository fetchModuleRepository() {
        FileReader reader = null;
        try {
            /* Fetch the entry, return null if it does not exist */
            File entry = new File(this.getFile(), Module.MODULE_REPOSITORY);
            if (entry.exists() == false) {
                logger.info("[MODULE] No repository.xml for Module " + this.getFile());
                return null;
            }

            /* Read in the file and parse it */
            reader = new FileReader(entry);
            return ModuleRepository.decode(reader);
        } catch (java.lang.Exception excp) {
            /* This is not too bad if it does not exist */
            logger.log(Level.INFO, "[MODULE] Error reading repository.xml for Module " + this.getFile(), excp);
            return null;
        } finally {
            RunUtil.close(reader);
        }
    }
    
    /**
     * Reads the module parts.
     */
    private Map<String, ModulePart> fetchModuleParts() {
        /* Create a map to store the entries, get the entries */
        Map<String, ModulePart> map = new HashMap<String, ModulePart>();
        File[] files = this.getFile().listFiles();
        if (files == null) {
            return map;
        }

        /*
         * Loop through each entry and see if its a directory. If so, create
         * a new part.
         */
        for (File file : files) {
            if (file.isDirectory() == true && file.isHidden() == false) {
                String name = file.getName();
                ModulePart part = new ModulePart(name, file);
                map.put(name, part);
            }
        }
        return map;
    }
}
