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

/**
 * The CellDelegate interface represents the methods that must be implemented
 * for each different kind of medium for a WFS (disk, archive, memory). The
 * methods here are used to manipulate the file in the medium.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public interface CellDelegate {
    
    /**
     * Calculate the last modified date of the file this cell represents.
     * 
     * @return The time this file was last modified.
     */
    public long getLastModified();
    
    /**
     * Creates a new directory delegate for the cell directory containing the
     * child cells of this cell.
     * 
     * @return A new directory delegate.
     */
    public DirectoryDelegate createDirectoryDelegate();

    /**
     * Returns the cell's setup information, encoded as a String.
     *
     * @throw IOException Upon general I/O error
     */
    public String decode() throws IOException;
 
    /**
     * Updates the cell's setup information, encoded as a String.
     * 
     * @param cellSetup The cell setup properties
     * @throw IOException Upon general I/O error
     */
    public void encode(String cellSetup) throws IOException;
    
    /**
     * Returns true if the cell directory associated with this cell exists,
     * false if not.
     * 
     * @return True if the cell's directory exists, false if not
     */
    public boolean cellDirectoryExists();
    
    /**
     * Creates the cell's directory on the medium, if it does not exist
     */
    public void createCellDirectory();
    
    /**
     * Removes the cell's directory on the medium, if it exists.
     */
    public void removeCellDirectory();
}
