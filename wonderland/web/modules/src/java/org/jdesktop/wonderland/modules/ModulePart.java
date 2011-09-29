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

import java.io.File;

/**
 * The ModulePart class represents a part of a module. Examples of module parts
 * include artwork, server components, audio components, help components, etc.
 * Each module part is contained beneath a directory in the module; the name
 * of the directory determines the kind of the module part.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ModulePart {
    /* The name of the module part */
    private String name = null;
    
    /* A File referencing the base directory of the module part */
    private File file = null;
    
    /**
     * Constructor, takes the name and URL of the module part
     */
    public ModulePart(String name, File file) {
        this.name = name;
        this.file = file;
    }
    
    /**
     * Returns the name of the module part.
     * 
     * @return The name of module part
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Returns a File to the directory in the module of the part.
     * 
     * @return A File of the module part directory
     */
    public File getFile() {
        return this.file;
    }
}
