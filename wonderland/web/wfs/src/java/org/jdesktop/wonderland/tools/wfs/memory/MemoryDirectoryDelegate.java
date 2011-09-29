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
package org.jdesktop.wonderland.tools.wfs.memory;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import org.jdesktop.wonderland.tools.wfs.WFSCell;
import org.jdesktop.wonderland.tools.wfs.delegate.CellDelegate;
import org.jdesktop.wonderland.tools.wfs.delegate.DirectoryDelegate;

/**
 * The MemoryDirectoryDelegate class handles the implemenation specific aspects
 * of cell directories for WFSs contained in memory.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class MemoryDirectoryDelegate implements DirectoryDelegate {
    
    /** Default constructor */
    public MemoryDirectoryDelegate() {
    }

    /**
     * Implementation method to load all of the cell names from the WFS. This
     * is the only method in the implementation to find out what cells are in
     * a particular directory. All methods to fetch the child cells eventually
     * call this method. This method is called once to load the names the first
     * time. The names returned include the naming convention suffix (-wlc.xml).
     * 
     * @return An array of cell names, an empty array if none exist.
     */
    public String[] loadCellNames() {
        /* Do nothing */
        return new String[] {};
    }

    /**
     * Returns a new cell delegate for a cell contained within this directory,
     * given the name of the cell (without any suffix).
     * 
     * @return A new cell delegate
     */
    public CellDelegate createCellDelegate(String cellName) {
        return new MemoryCellDelegate();
    }
    
    /**
     * Cleans up the directory by removing any files or directories that exist
     * but are no longer in the WFS cell.
     */
    public void cleanupDirectory(HashMap<String, WFSCell> children) {
        /* Do nothing */
    }
    
    /**
     * Returns a writer for the given file name in the root directory.
     * This is used to write meta-data such as the version file.
     * 
     * @name The name of the file
     * @return A writer for a particular file
     */
    public Writer getWriter(String name) throws IOException {
        throw new UnsupportedOperationException("Output stream not supported for memory WFS");
    }
}
