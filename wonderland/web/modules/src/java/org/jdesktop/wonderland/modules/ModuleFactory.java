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
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.modules.archive.ArchiveModule;
import org.jdesktop.wonderland.modules.file.FileModule;

/**
 * The ModuleFactory class creates instances of modules, either if the already
 * exist on disk as directories or JAR archive files, or whether they are
 * being created in memory.
 * <p>
 * All methods on this class are static.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public final class ModuleFactory {

    /**
     * Opens an existing module given its File. The File may either be a
     * directory or a JAR file.
     *
     * @param file The File of the Module to open
     * @throw FileNotFoundException If the Module does not exist
     * @throw IOException Upon some general I/O error reading the WFS
     * @throw JAXBException Upon error reading XML
     */
    public static final Module open(File file) throws FileNotFoundException, IOException, JAXBException {        
        
        /* If the URL points to a disk directory */
        if (file.exists() == true && file.isDirectory() == true) {
            return new FileModule(file);
        }
        else if (file.exists() == true && file.getName().endsWith(".jar") == true) {
            return new ArchiveModule(file);
        }
        else {
            throw new IOException("Invalid File for Module Given: " + file);
        }
    }
}
