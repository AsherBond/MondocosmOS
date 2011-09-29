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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;
import org.jdesktop.wonderland.tools.wfs.WFS;
import org.jdesktop.wonderland.tools.wfs.WFSCell;
import org.jdesktop.wonderland.tools.wfs.delegate.CellDelegate;
import org.jdesktop.wonderland.tools.wfs.delegate.DirectoryDelegate;


/**
 * The FileCellDelegate class implements methods to support accessing WFS files
 * on disk.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class FileCellDelegate implements CellDelegate {
    
    /* The File of the referring WFS Directory */
    private File file = null;
    
    /** Constructor, takes a new instance of the cell file */
    public FileCellDelegate(File file) {
        this.file = file;
    }
    
    /**
     * Creates a new directory delegate for the cell directory containing the
     * child cells of this cell.
     * 
     * @return A new directory delegate
     */
    public DirectoryDelegate createDirectoryDelegate() {
        /*
         * Parse off the -wlc.xml suffix from the cell file and put a -wld
         * suffix instead.
         */
        Logger logger = WFS.getLogger();
        try {
            String filePath = this.getFile().getAbsolutePath();
            int index = filePath.indexOf(WFS.CELL_FILE_SUFFIX);
            String path = filePath.substring(0, index) + WFS.CELL_DIRECTORY_SUFFIX;
            File newFile = new File(path);
            return new FileDirectoryDelegate(newFile);
        } catch (java.lang.IndexOutOfBoundsException excp) {
            /* Quietly return null */
        }
        return null;
    }

    /**
     * Calculate the last modified date of the file this cell represents
     * @return the time this file was last modified
     */
    public long getLastModified() {
        return this.getFile().lastModified();
    }

    /**
     * Returns true if the cell directory associated with this cell exists,
     * false if not.
     * 
     * @return True if the cell's directory exists, false if not
     */
    public boolean cellDirectoryExists() {
        /*
         * Parse off the cell file suffix. Create a new file object, and
         * check if it exists
         */
        String fileName = this.getFile().getAbsolutePath();
        String path = WFSCell.stripCellFileSuffix(fileName) + WFS.CELL_DIRECTORY_SUFFIX;
        File newFile = new File(path);
        return newFile.exists();
    }
    
    /**
     * Creates the cell's directory on the medium, if it does not exist
     */
    public void createCellDirectory() {
        /*
         * Parse off the cell file suffix. Create a new file object, and
         * if it does not exist, create it
         */
        String fileName = this.getFile().getAbsolutePath();
        String path = WFSCell.stripCellFileSuffix(fileName) + WFS.CELL_DIRECTORY_SUFFIX;
        File newFile = new File(path);
        if (newFile.exists() == false) {
            newFile.mkdir();
        }
    }
    
    /**
     * Removes the cell's directory on the medium, if it exists.
     */
    public void removeCellDirectory() {
        /*
         * Parse off the cell file suffix. Create a new file object, and
         * if it exists, delete it
         */
        String fileName = this.getFile().getAbsolutePath();
        String path = WFSCell.stripCellFileSuffix(fileName) + WFS.CELL_DIRECTORY_SUFFIX;
        File newFile = new File(path);
        WFSFileUtils.deleteDirectory(newFile);
    }
   
    /**
     * Returns the File associated with this WFS file
     */
    private File getFile() {
        return this.file;
    }
        
    /**
     * Returns the cell's setup information, encoded as a String.
     *
     * @throw IOException Upon general I/O error
     */
    public String decode() throws IOException {
        /*
         * Read the data from disk as a string and return
         */
        BufferedReader reader = new BufferedReader(new FileReader(this.getFile()));
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
        BufferedWriter writer = new BufferedWriter(new FileWriter(this.getFile()));
        writer.write(cellSetup, 0, cellSetup.length());
        writer.flush();
        writer.close();
    }
}
