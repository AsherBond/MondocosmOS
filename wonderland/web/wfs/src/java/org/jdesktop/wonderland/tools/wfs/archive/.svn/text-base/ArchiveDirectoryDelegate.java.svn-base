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
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import org.jdesktop.wonderland.utils.ArchiveManifest;
import org.jdesktop.wonderland.tools.wfs.WFS;
import org.jdesktop.wonderland.tools.wfs.WFSCell;
import org.jdesktop.wonderland.tools.wfs.delegate.CellDelegate;
import org.jdesktop.wonderland.tools.wfs.delegate.DirectoryDelegate;

/**
 * The ArchiveDirectoryDelegate class implements methods to support accessing WFS
 * directories from an archive.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ArchiveDirectoryDelegate implements DirectoryDelegate {
    /* The manifest file that permits access to JAR file entries */
    private ArchiveManifest manifest = null;
    
    /* The path name of this directory in the manifest */
    private String pathName = null;
    
    /**
     * Creates a new instance of WFSCellDirectory, takes the File of the referring
     * directory as an argument
     */
    public ArchiveDirectoryDelegate(ArchiveManifest manifest, String pathName) {
        this.manifest = manifest;
        this.pathName = pathName;
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
        /*
         * Fetch the entry in the manifest corresponding to this directory,
         * subject to the filter for the cell file suffix.
         */
        LinkedList<String> entries = manifest.getEntries(this.pathName);
        
        WFS.getLogger().log(Level.INFO, "WFS File List: " + entries.size() +
            " files found in " + pathName);
        
        /* For each entry, create the linked list and fill it in */
        LinkedList<String> names = new LinkedList<String>();
        for (String entry : entries) {
            if (entry.endsWith(WFS.CELL_FILE_SUFFIX) == true) {
                WFS.getLogger().log(Level.INFO, "Cell found " + entry);
                String cellName = WFSCell.stripCellFileSuffix(entry);
                names.add(entry);
            }
        }
        return names.toArray(new String[] {});
    }

    /**
     * Returns a new cell delegate for a cell contained within this directory,
     * given the name of the cell (without any suffix).
     * 
     * @return A new cell delegate
     */
    public CellDelegate createCellDelegate(String cellName) {
        /*
         * Create a new WFSArhiveCell object, which takes the path and a new
         * cell name.
         */
        String path = this.pathName + "/" + cellName + WFS.CELL_FILE_SUFFIX;
        return new ArchiveCellDelegate(this.manifest, path);
    }
    
    /**
     * Cleans up the directory by removing any files or directories that exist
     * but are no longer in the WFS cell. Takes a hash map where the keys are
     * the names of the cells that should be in the directory (without the
     * -wlc.xml suffix)
     */
    public void cleanupDirectory(HashMap<String, WFSCell> children) {
        throw new UnsupportedOperationException("Writing to archive files is not supported.");
    }
 
    /**
     * Returns a writer for the given file name in the root directory.
     * This is used to write meta-data such as the version file.
     * 
     * @name The name of the file
     * @return A writer for a particular file
     */
    public Writer getWriter(String name) throws IOException {
        throw new UnsupportedOperationException("Writing to archive files is not supported.");
    }
    
    /**
     * Returns true if the directory actually exists on disk and is a directory
     */
    protected boolean exists() {
        return manifest.isValidEntry(this.pathName);
    }
 }
