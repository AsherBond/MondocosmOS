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
package org.jdesktop.wonderland.modules.archive;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.modules.Module;
import org.jdesktop.wonderland.common.modules.ModuleInfo;
import org.jdesktop.wonderland.modules.ModulePart;
import org.jdesktop.wonderland.common.modules.ModuleRepository;
import org.jdesktop.wonderland.common.modules.ModuleRequires;
import org.jdesktop.wonderland.utils.ArchiveManifest;
import org.jdesktop.wonderland.utils.RunUtil;

/**
 * The ArchiveModule class extends the Module abstract base class and represents
 * all modules that are contained within either a JAR or ZIP archive.
 * <p>
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ArchiveModule extends Module {

    private static Logger logger = Logger.getLogger(ArchiveModule.class.getName());

    /* The manifest of the archive */
    private ArchiveManifest manifest = null;
            
    /**
     * Constructor, takes File of the module jar. Throws IOException upon
     * general I/O error reading the archive module.
     * 
     * @param file The archive module File object
     * @throw IOException Upon general I/O exception readin the module
     */
    public ArchiveModule(File file) throws IOException {
        super();
        this.setFile(file);
        this.manifest = new ArchiveManifest(file);
        
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
        InputStreamReader reader = null;
        try {
            /* Fetch the input stream, parse and return */
            InputStream is = manifest.getEntryInputStream(Module.MODULE_INFO);
            if (is == null) {
                /* This is pretty bad -- if this doesn't exist, then the module is invalid */
                logger.log(Level.WARNING, "[MODULE] Invalid Module " + this.getFile());
                return null;
            }

            /* Read in the file and parse */
            reader = new InputStreamReader(is);
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
        InputStreamReader reader = null;
        try {
            /* Fetch the input stream, parse and return */
            InputStream is = manifest.getEntryInputStream(Module.MODULE_REQUIRES);
            if (is == null) {
                /* This is not too bad if it does not exist */
                logger.log(Level.INFO, "[MODULE] No requires.xml for Module " + this.getFile());
                return null;
            }

            /* Read in the file and parse */
            reader = new InputStreamReader(is);
            return ModuleRequires.decode(reader);
        } catch (java.lang.Exception excp) {
            /* This is not too bad if it does not exist */
            logger.log(Level.INFO, "[MODULE] No requires.xml for Module " + this.getFile(), excp);
            return null;
        } finally {
            RunUtil.close(reader);
        }
    }

    /**
     * Reads the asset server info from the module.
     */
    public ModuleRepository fetchModuleRepository() {
        InputStreamReader reader = null;
        try {
            /* Fetch the input stream, parse and return */
            InputStream is = manifest.getEntryInputStream(Module.MODULE_REPOSITORY);
            if (is == null) {
                /* This is not too bad if it does not exist */
                logger.log(Level.INFO, "[MODULE] No repository.xml for Module " + this.getFile());
                return null;
            }

            /* Read in the file and parse */
            reader = new InputStreamReader(is);
            return ModuleRepository.decode(reader);
        } catch (java.lang.Exception excp) {
            /* This is not too bad if it does not exist */
            logger.log(Level.INFO, "[MODULE] No repository.xml for Module " + this.getFile(), excp);
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
        Iterator<String> it = manifest.getEntries().listIterator();

        /*
         * Loop through each entry and see if its name begins with "art/"
         * does not end with "/". If so, take the name, minus the beginning
         * "art/" part.
         */
        while (it.hasNext() == true) {
            String name = it.next();

            /* See if the name ends with a "/", then it is a directory */
            if (name.endsWith("/") == true) {
                /* Look at the part type, ignore META-INF */
                String partType = name.substring(0, name.length() - 1);
                if (partType.equals("META-INF") == true) {
                    continue;
                }

                /* Just use the jar file */
                ModulePart part = new ModulePart(partType, this.getFile());
                map.put(partType, part);
            }
        }
        return map;
    }
}
