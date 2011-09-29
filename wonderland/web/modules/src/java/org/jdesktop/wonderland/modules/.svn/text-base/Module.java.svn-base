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
package org.jdesktop.wonderland.modules;

import org.jdesktop.wonderland.common.modules.ModuleInfo;
import org.jdesktop.wonderland.common.modules.ModuleRequires;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Map;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.common.modules.ModuleRepository;

/**
 * The Module class represents a single module within Wonderland. A module
 * consists of several possible subcomponents: artwork, WFSs, and plugins.
 * Artwork either includes textures, images, and 3D geometry. Modules may also
 * contain Wonderland Filesystems (WFSs) that assemble the artwork resources
 * into a subworld component. Plugins are runnable code that extend the
 * functionality of the server and/or client.
 * <p>
 * A module is stored within a jar/zip archive file. To open an existing module
 * archive file, use the ModuleFactory.open() method. Once open, users of this
 * class may query for the module's artwork, WFSs, and plugins.
 * <p>
 * Modules also have major.minor version numbers and a list of other modules
 * upon which this module depends.
 * <p>
 * This is an abstract class -- it is typically subclassed to handle whether
 * the module was loaded on disk or from an archive file.
 * <p>
 * @author Jordan Slott <jslott@dev.java.net>
 */
public abstract class Module {

    /* Useful names of files within the archive */
    public static final String MODULE_INFO       = "module.xml";
    public static final String MODULE_REQUIRES   = "requires.xml";
    public static final String MODULE_REPOSITORY = "repository.xml";
 
    /* The File root of the module */
    private File root = null;
    
    private ModuleInfo       moduleInfo       = null; /* Basic module info   */
    private ModuleRequires   moduleRequires   = null; /* Module dependencies */
    private ModuleRepository moduleRepository = null; /* Module repository   */
    
    /* A map of module parts and their objects */
    private Map<String, ModulePart> moduleParts = null;
    
    /** Default constructor */
    protected Module() {}
    
    /**
     * Returns the name of the module.
     * 
     * @return The module's name
     */
    public String getName() {
        return this.moduleInfo.getName();
    }

    /**
     * Returns the location of the module as a File.
     * 
     * @return The location of the module (either as a directory or JAR).
     */
    public File getFile() {
        return this.root;
    }
    
    /**
     * Sets the file root of the module
     */
    public void setFile(File root) {
        this.root = root;
    }
    
    /**
     * Returns the file object for one of the key files within the module.
     * 
     * @param name The name of the key module file
     * @return A new File
     */
    public File getFile(String name) {
        return new File(this.getFile(), name);
    }
    
    /**
     * Returns the basic information about a module: its name and version.
     * <p>
     * @return The basic module information
     */
    public ModuleInfo getInfo() {
        return this.moduleInfo;
    }
    
    /**
     * Sets the basic information about a module, assumes the given argument
     * is not null.
     * <p>
     * @param moduleInfo The basic module information
     */
    protected void setInfo(ModuleInfo moduleInfo) {
        this.moduleInfo = moduleInfo;
    }
    
    /**
     * Returns the module's dependencies.
     * <p>
     * @return The module's dependencies
     */
    public ModuleRequires getRequires() {
        return this.moduleRequires;
    }
    
    /**
     * Sets the module's dependencies, assumes the given argument is not null.
     * <p>
     * @param moduleRequires The module dependencies
     */
    protected void setRequires(ModuleRequires moduleRequires) {
        this.moduleRequires = moduleRequires;
    }
    
    /**
     * Returns the module's repository information.
     * <p>
     * @return The module's repository information
     */
    public ModuleRepository getRepository() {
        return this.moduleRepository;
    }
    
    /**
     * Sets the module's repository information, assumes the argument is not
     * null.
     * <p>
     * @param moduleRepository The module's repository information
     */
    protected void setRepository(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }
    
    /**
     * Returns a map of module part names and their module part objects.
     * 
     * @return A map of module parts
     */
    public Map<String, ModulePart> getParts() {
        return this.moduleParts;
    }
    
    /**
     * Sets the map of module part names and their module part objects
     */
    public void setParts(Map<String, ModulePart> moduleParts) {
        this.moduleParts = moduleParts;
    }
    
    /**
     * Returns a string representing this module.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("---------------------------------------------------------------------------\n");
        sb.append(this.getInfo().toString() + "\n");
        sb.append("Depends upon " + this.getRequires().toString() + "\n");
        sb.append("Repositories\n" + this.getRepository().toString() + "\n");
        sb.append("Parts: ");
        
        Map<String, ModulePart> parts = this.getParts();
        Iterator<Map.Entry<String, ModulePart>> it = parts.entrySet().iterator();
        while (it.hasNext() == true) {
            Map.Entry<String, ModulePart> entry = it.next();
            ModulePart part = entry.getValue();
            sb.append("\t" + part.getName() + " " + part.getFile().getAbsolutePath() + "\n");
        }
        sb.append("\n");
        sb.append("---------------------------------------------------------------------------\n");
        return sb.toString();
    }
}
