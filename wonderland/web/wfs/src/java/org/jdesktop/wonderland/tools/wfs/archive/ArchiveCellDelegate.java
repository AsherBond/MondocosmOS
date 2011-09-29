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
package org.jdesktop.wonderland.tools.wfs.archive;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.jar.JarEntry;
import org.jdesktop.wonderland.utils.ArchiveManifest;
import org.jdesktop.wonderland.tools.wfs.WFS;
import org.jdesktop.wonderland.tools.wfs.WFSCell;
import org.jdesktop.wonderland.tools.wfs.delegate.CellDelegate;
import org.jdesktop.wonderland.tools.wfs.delegate.DirectoryDelegate;

/**
 * The ArchiveCellDelegate class implements methods to support accessing WFS
 * files from an archive.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ArchiveCellDelegate implements CellDelegate {
    
    /* The manifest file that permits access to JAR file entries */
    private ArchiveManifest manifest = null;
    
    /* The full pathname inside the maniest of the cell */
    private String pathName = null;
    
    /**
     * Creates a new instance of WFSArchive cell class. This constructor takes
     * an instance of the JAR manifest, the canonical path name of the directory
     * in which it is contained and the name of the cell (with the '-wlc.xml'
     * extension.
     */
    public ArchiveCellDelegate(ArchiveManifest manifest, String pathName) {        
        this.manifest = manifest;
        this.pathName = pathName;
    }
    
    /**
     * Calculate the last modified date of the file this cell represents
     * @return the time this file was last modified
     */
    public long getLastModified() {
        /* Fetch the entry, if it does not exist, return -1 */
        JarEntry entry = this.getArchiveManifest().getJarEntry(this.pathName);
        return (entry != null) ? entry.getTime() : -1;
    }
    
    /**
     * Creates a new directory delegate for the cell directory containing the
     * child cells of this cell.
     * 
     * @return A new directory delegate.
     */
    public DirectoryDelegate createDirectoryDelegate() {
        /*
         * Simply create the object -- the actual directory does not get
         * created until a write() happens.
         */
        return new ArchiveDirectoryDelegate(this.getArchiveManifest(), pathName);
    }

    /**
     * Returns the cell's setup information, encoded as a String.
     *
     * @throw IOException Upon general I/O error
     */
    public String decode() throws IOException {
        /*
         * Read the data from the archive as a string and return
         */
        InputStream is = this.manifest.getEntryInputStream(this.pathName);
        InputStreamReader reader = new InputStreamReader(is);
        StringBuffer data = new StringBuffer();
        char buf[] = new char[4 * 1024];
        int ret = -1;
        
        while ((ret = reader.read(buf)) != -1) {
            data.append(buf, 0, ret);
        }
        reader.close();
        return data.toString();
    }

    /**
     * Updates the cell's setup information, encoded as a String.
     * 
     * @param cellSetup The cell setup properties
     * @throw IOException Upon general I/O error
     */
    public void encode(String cellSetup) throws IOException {
        throw new UnsupportedOperationException("Writing a cell to an archive is not supported");
    }

    /**
     * Returns true if the cell directory associated with this cell exists,
     * false if not.
     * 
     * @return True if the cell's directory exists, false if not
     */
    public boolean cellDirectoryExists() {
        /* Parse off the cell file suffix. See if it exists in the manifest */
        String path = WFSCell.stripCellFileSuffix(this.pathName) + WFS.CELL_DIRECTORY_SUFFIX;
        return this.manifest.isValidEntry(path);
    }
    
    /**
     * Creates the cell's directory on the medium, if it does not exist
     */
    public void createCellDirectory() {
        throw new UnsupportedOperationException("Writing a cell to an archive is not supported");
    }
    
    /**
     * Removes the cell's directory on the medium, if it exists.
     */
    public void removeCellDirectory() {
        throw new UnsupportedOperationException("Writing a cell to an archive is not supported");
    }
    
    /**
     * Returns the manifest object of the JAR file
     */
    private ArchiveManifest getArchiveManifest() {
        return this.manifest;
    }
}
