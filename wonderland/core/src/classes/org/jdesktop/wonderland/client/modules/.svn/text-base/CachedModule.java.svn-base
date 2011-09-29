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
package org.jdesktop.wonderland.client.modules;

import java.util.logging.Logger;
import org.jdesktop.wonderland.common.modules.ModuleArtList;
import org.jdesktop.wonderland.common.checksums.ChecksumList;
import org.jdesktop.wonderland.common.modules.ModuleInfo;
import org.jdesktop.wonderland.common.modules.ModuleRepository;

/**
 * The CachedModule class stores information about a single module, such as
 * its basic information (ModuleInfo), its repository information, the list
 * of checksums for its assets, a list of assets, and list of WFSs.
 * <p>
 * All CacheModule objects must be created with a ModuleInfo that identifies
 * the module uniquely.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class CachedModule {

    /* The base URL of the server for this cache */
    private String serverURL = null;
    
    /* The unique name of the module */
    private String moduleName = null;
    
    private ModuleInfo       moduleInfo       = null; /* Basic module info   */
    private ModuleArtList    moduleArt        = null; /* List of module art  */
    private ChecksumList  moduleChecksums  = null; /* Module checksums    */
    private ModuleRepository moduleRepository = null; /* Module repository   */
    
    /* The error logger */
    private static Logger logger = Logger.getLogger(CachedModule.class.getName());
    
    /** Constructor, takes the essential module information */
    public CachedModule(String serverURL, ModuleInfo moduleInfo) {
        this.serverURL = serverURL;
        this.moduleInfo = moduleInfo;
        this.moduleName = moduleInfo.getName();
    }
    
    /**
     * Returns the basic module information.
     * 
     * @return The ModuleInfo object for the module
     */
    public ModuleInfo getInfo() {
        return this.moduleInfo;
    }
    
    /**
     * Returns the list of module art, loading it from the server if necessary.
     * 
     * @return A list of module art
     */
    public synchronized ModuleArtList getArt() {
        if (this.moduleArt == null) {
            this.moduleArt = ModuleUtils.fetchModuleArtList(serverURL, moduleInfo.getName());
        }
        return this.moduleArt;
    }
    
    /**
     * Returns a list of the checksums for the module assets, loading it from
     * the server if necessary.
     *
     * @return The module checksum information
     */
    public synchronized ChecksumList getModuleChecksums() {
        if (this.moduleChecksums == null) {
            this.moduleChecksums = ModuleUtils.fetchModuleChecksums(serverURL, moduleName);
        }
        return this.moduleChecksums;
    }
    
    /**
     * Returns a list of the repositories for the module, loading it from the
     * server if necessary.
     *
     * @return The module repository information
     */
    public synchronized ModuleRepository getModuleRepositories() {
        if (this.moduleRepository == null) {
            this.moduleRepository = ModuleUtils.fetchModuleRepositories(serverURL, moduleName);
        }
        return this.moduleRepository;
    }    
}
