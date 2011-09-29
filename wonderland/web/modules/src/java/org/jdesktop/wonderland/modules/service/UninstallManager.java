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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.common.modules.ModuleInfo;
import org.jdesktop.wonderland.utils.RunUtil;

/**
 * The Uninstall class manages all modules that are waiting to be uninstalled.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class UninstallManager {
    /* The directory in which uninstall modules are kept */
    private static final String UNINSTALL_XML = "uninstall.xml";
    private File uninstallFile = null;
    
    /* The Map of modules uninstall to be installed and their info objects */
    private Map<String, ModuleInfo> uninstallModules = null;
    private ModuleInfoList uninstallList = null;
    
    /**
     * Constructor, takes the root directory of the module manager
     */
    public UninstallManager(File root) {
        /* Create the uninstall.xml file if it does not exist */
        this.uninstallFile = new File(root, UNINSTALL_XML);
        if (this.uninstallFile.exists() == false) {
            this.uninstallList = new ModuleInfoList();
            FileWriter writer = null;
            try {
                writer = new FileWriter(uninstallFile);
                this.uninstallList.encode(writer);
            } catch (IOException ex) {
                Logger.getLogger(UninstallManager.class.getName()).log(Level.WARNING, null, ex);
            } catch (JAXBException ex) {
                Logger.getLogger(UninstallManager.class.getName()).log(Level.WARNING, null, ex);
            } finally {
                RunUtil.close(writer);
            }
            this.uninstallModules = Collections.synchronizedMap(new HashMap<String, ModuleInfo>());
        }
        else {
            this.fetchModules();
        }
    }
    
    /**
     * Returns the hashmap of uninstall modules
     */
    public Map<String, ModuleInfo> getModules() {
        return this.uninstallModules;
    }
    
    /**
     * Adds a new module to be uninstall.
     */
    public void add(String moduleName, ModuleInfo moduleInfo) {
        /* Add to the list and write out to disk */
        this.uninstallModules.put(moduleName, moduleInfo);
        ModuleInfo[] infos = this.uninstallList.getModuleInfos();
        List<ModuleInfo> list = new LinkedList(Arrays.asList(infos));
        list.add(moduleInfo);
        ModuleInfo[] newInfos = list.toArray(new ModuleInfo[] {});
        this.uninstallList.setModuleInfos(newInfos);
        write();
    }
    
    /**
     * Removes an existing module, given its name. 
     */
    public void remove(String moduleName) {
        Logger logger = ModuleManager.getLogger();
        
        /* Simple delete the entry and write out to disk */
        this.uninstallModules.remove(moduleName);
        ModuleInfo[] infos = this.uninstallList.getModuleInfos();
        List<ModuleInfo> list = new LinkedList(Arrays.asList(infos));
        Iterator<ModuleInfo> it = list.iterator();
        while (it.hasNext() == true) {
            ModuleInfo info = it.next();
            if (info.getName().equals(moduleName) == true) {
                it.remove();
                break;
            }
        }
        ModuleInfo[] newInfos = list.toArray(new ModuleInfo[] {});
        this.uninstallList.setModuleInfos(newInfos);
        write();
    }

    /**
     * Write the current state uninstall file
     */
    protected void write() {
        FileWriter writer = null;
        try {
            writer = new FileWriter(uninstallFile);
            this.uninstallList.encode(writer);
        } catch (JAXBException ex) {
            Logger.getLogger(UninstallManager.class.getName()).log(Level.WARNING, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(UninstallManager.class.getName()).log(Level.WARNING, null, ex);
        } finally {
            RunUtil.close(writer);
        }
    }

    /**
     * Returns a map of module names and objects from a given directory. If no
     * modules are present, this method returns an empty map.
     * 
     * @return An map of unique module names and their Module objects
     */
    private void fetchModules() {
        this.uninstallModules = Collections.synchronizedMap(new HashMap<String, ModuleInfo>());
        
        /* Read in the uninstall.xml file */
        FileReader reader = null;
        try {
            reader = new FileReader(uninstallFile);
            this.uninstallList = ModuleInfoList.decode(reader);
            for (ModuleInfo info : this.uninstallList.getModuleInfos()) {
                this.uninstallModules.put(info.getName(), info);
            }
        } catch (java.lang.Exception ex) {
            Logger.getLogger(UninstallManager.class.getName()).log(Level.WARNING, null, ex);
            this.uninstallList = new ModuleInfoList();
        } finally {
            RunUtil.close(reader);
        }
    }
}
