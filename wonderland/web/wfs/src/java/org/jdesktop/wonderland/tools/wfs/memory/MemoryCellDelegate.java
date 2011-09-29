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
import org.jdesktop.wonderland.tools.wfs.delegate.CellDelegate;
import org.jdesktop.wonderland.tools.wfs.delegate.DirectoryDelegate;


/**
 * The WFSMemoryCellDelegate class represents a WFS cell that resides in memory.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class MemoryCellDelegate implements CellDelegate {
    
    /**
     * Default constructor
     */
    public MemoryCellDelegate() {
    }
    
    /**
     * Calculate the last modified date of the file this cell represents -- for
     * cells in memory, this is always zero.
     * 
     * @return The time this file was last modified, always zero
     */
    public long getLastModified() {
        return 0;
    }
    
    /**
     * Creates a new directory delegate for the cell directory containing the
     * child cells of this cell.
     * 
     * @return A new directory delegate
     */
    public DirectoryDelegate createDirectoryDelegate() {
        return new MemoryDirectoryDelegate();
    }    

    /**
     * Returns the cell's setup information, encoded as a String.
     *
     * @throw IOException Upon general I/O error
     */
    public String decode() throws IOException {
        /* Return an empty string, since the cell does not exist on disk */
        return "";
    }
 
    /**
     * Updates the cell's setup information, encoded as a String.
     * 
     * @param cellSetup The cell setup properties
     * @throw IOException Upon general I/O error
     */
    public void encode(String cellSetup) throws IOException {
        throw new UnsupportedOperationException("Writing a cell to memory is not supported");
    }

    /**
     * Returns true if the cell directory associated with this cell exists,
     * false if not.
     * 
     * @return True if the cell's directory exists, false if not
     */
    public boolean cellDirectoryExists() {
        /* Always return false since it is entire in memory */
        return false;
    }
    
    /**
     * Creates the cell's directory on the medium, if it does not exist
     */
    public void createCellDirectory() {
        /* Do nothing */
    }
    
    /**
     * Removes the cell's directory on the medium, if it exists.
     */
    public void removeCellDirectory() {
        /* Do nothing */
    }
}
