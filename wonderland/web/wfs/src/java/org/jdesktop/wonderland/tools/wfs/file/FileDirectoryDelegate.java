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
package org.jdesktop.wonderland.tools.wfs.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.logging.Logger;
import org.jdesktop.wonderland.tools.wfs.WFS;
import org.jdesktop.wonderland.tools.wfs.WFSCell;
import org.jdesktop.wonderland.tools.wfs.delegate.CellDelegate;
import org.jdesktop.wonderland.tools.wfs.delegate.DirectoryDelegate;

/**
 * The FileDirectoryDelegate class implements methods to interface with WFS
 * directories on disk.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class FileDirectoryDelegate implements DirectoryDelegate {
    /* The File of the referring WFS Directory */
    private File file = null;
    
    /**
     * Creates a new instance of WFSCellDirectory, takes the File of the
     * directory. This constructor is meant for WFSRootCellDirectory classes
     * that do not have a cell associated with them
     */
    public FileDirectoryDelegate(File file) {
        this.file = file;
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
        /* Fetch the error logger */
        Logger logger = WFS.getLogger();
        
        /*
         * List all of the files in the current directory subject to the filter.
         * If the file does not exist or cannot be read, do nothing
         */
        if (this.getFile().exists() == true && this.getFile().canRead() == true) {
            File[] files = this.getFile().listFiles(FileWFS.CELL_FILE_FILTER);

            logger.info("WFS: " + files.length + " files found in " + this.getFile().getName());

            /* 
             * For each file, parse off the end of the naming convention suffix
             * and put in the linked list
             */
            String[] names = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                names[i] = files[i].getName();
            }
            return names;
        }
        else if (this.getFile().exists() == true) {
            /* If it exists but cannot be read, then print a friendly message */
            WFS.getLogger().warning("WFS: Cannot read cell directory " + this.getFile().getAbsolutePath());
        }
        return new String[]{};
    }

    /**
     * Returns a new cell delegate for a cell contained within this directory,
     * given the name of the cell (without any suffix).
     * 
     * @return A new cell delegate
     */
    public CellDelegate createCellDelegate(String cellName) {
        /*
         * Create a new WFSFileCell object, which takes a File object. We should
         * be able to create the File object even if the actual file does not
         * yet exist.
         */
        String pathName = this.getFile().getAbsolutePath();
        String cellPath = pathName + "/" + cellName + WFS.CELL_FILE_SUFFIX;
        File newFile = new File(cellPath);
        FileCellDelegate delegate = new FileCellDelegate(newFile);
        return delegate;
    }
    
    /**
     * Cleans up the directory by removing any files or directories that exist
     * but are no longer in the WFS cell. Takes an array of strings that give
     * the names of the cells that should be in the directory (without the
     * -wlc.xml suffix)
     */
    public void cleanupDirectory(HashMap<String, WFSCell> children) {
        /*
         * Delete any cells that exist within the directory, but are
         * not present in the current list of cells.
         */
        File[] files = this.getFile().listFiles(FileWFS.CELL_FILE_FILTER);
        for (File oldFile : files) {
            /*
             * Parse out the cell name and see if it exists, delete if not.
             */
            int    index    = oldFile.getName().indexOf(WFS.CELL_FILE_SUFFIX);
            String cellName = oldFile.getName().substring(0, index);
            if (children.containsKey(cellName) == false) {
                /* Ignore an errors upon deletion. */
                oldFile.delete();
                
                /*
                 * Also check to see if an associated cell directory exists
                 * and delete it too.
                 */
                File dir = new File(cellName + WFS.CELL_DIRECTORY_SUFFIX + "/");
                if (dir.exists() == true && dir.isDirectory() == true) {
                    dir.delete();
                }
            }
        }
    }

    /**
     * Returns a writer for the given file name in the root directory.
     * This is used to write meta-data such as the version file.
     * 
     * @name The name of the file
     * @return A writer for a particular file
     */
    public Writer getWriter(String name) throws IOException {
        return new FileWriter(this.getFile() + "/" + name);
    }
    
    /**
     * Returns the File object associated with this WFS directory.
     */
    private File getFile() {
        return this.file;
    }
    
    /**
     * Returns true if the directory actually exists on disk and is a directory
     */
    private boolean exists() {
        return (this.file.exists() == true && this.file.isDirectory() == true);
    }
    
    /**
     * Delete the actual directory if it does exist
     */
    private boolean delete() {
        return WFSFileUtils.deleteDirectory(this.file);
    }
    
    /**
     * Create the directory.
     */
    private boolean mkdir() {
        return this.file.mkdir();
    }
}
