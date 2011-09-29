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
package org.jdesktop.wonderland.tools.wfs.delegate;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import org.jdesktop.wonderland.tools.wfs.WFSCell;

/**
 * The DirectoryDelegate interface represents the methods that must be
 * implemented for each different kind of medium for a WFS (disk, archive,
 * memory). The methods here are used to manipulate the directory in the medium.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public interface DirectoryDelegate {
    /**
     * Implementation method to load all of the cell names from the WFS. This
     * is the only method in the implementation to find out what cells are in
     * a particular directory. All methods to fetch the child cells eventually
     * call this method. This method is called once to load the names the first
     * time. The names returned include the naming convention suffix (-wlc.xml).
     * 
     * @return An array of cell names, an empty array if none exist.
     */
    public String[] loadCellNames();
    
    /**
     * Returns a new cell delegate for a cell contained within this directory,
     * given the name of the cell (without any suffix).
     * 
     * @return A new cell delegate
     */
    public CellDelegate createCellDelegate(String cellName);
    
    /**
     * Cleans up the directory by removing any files or directories that exist
     * but are no longer in the WFS cell. Takes a hash map where the keys are
     * the names of the cells that should be in the directory (without the
     * -wlc.xml suffix)
     */
    public void cleanupDirectory(HashMap<String, WFSCell> children);
    
    /**
     * Returns a writer for the given file name in the root directory.
     * This is used to write meta-data such as the version file.
     * 
     * @name The name of the file
     * @return A writer for a particular file
     */
    public Writer getWriter(String name) throws IOException;
}
