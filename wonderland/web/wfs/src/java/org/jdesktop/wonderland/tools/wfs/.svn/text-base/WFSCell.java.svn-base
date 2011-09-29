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
package org.jdesktop.wonderland.tools.wfs;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.tools.wfs.delegate.CellDelegate;
import org.jdesktop.wonderland.tools.wfs.delegate.DirectoryDelegate;

/**
 * The WFSCell class represents a single cell in a WFS. Each cell is identified
 * by a name that corresponds to the name given the file contained within the
 * WFS (without the '-wlc.xml' extension). The cell name is immutable--to rename
 * a cell, one must create a new WFSCell object and delete the old object. The
 * cell name is unique only to its immediate parent.
 * <p>
 * The directory in which this cell is contained is obtained via the
 * getParentCellDirectory() method and the cell's parent via the getParentCell()
 * method.
 * 
 * <h3>Canonical Name</h3>
 * 
 * A cell also has a name unique in the entire WFS. Typically, this name is the
 * hierarchical path to the cell, but the particular format of this name is
 * not guaranteed. Threads obtain the unique name via the getCanonicalName()
 * method.
 * 
 * <h3>Cell Setup Properties</h3>
 * 
 * Each cell has a collection of configuration ("setup") properties encoded as
 * a string in its disk file. Typically, this string is XML encoded, but this
 * API is not tied to one particular encoding. The properties of the cell are
 * not read until explictly asked to do so, via the getCellSetup() method. The
 * cell's setup properties may be set via the setCellSetup() method. Note that
 * a cell's setup properties are not written back out to the underlying medium
 * unless the properties were modified via the setCellSetup() method.
 * 
 * <h3>Cell Children</h3>
 *
 * If the WFSCell has children, they reside in the corresponding directory (with
 * the '-wld' extension), a handle to the directory is obtained by calling the
 * getCellDirectory() method. If the child cell directory does not exist, the
 * getCellDirectory() returns null. A child cell directory may be created via
 * the createCellDirectory() method.
 * 
 * <h3>Writing to Underlying Medium</h3>
 * 
 * The writeCell() method updates the cell data on the underlying medium. It
 * does not update any of its children. The write() method updates the cell
 * data on the underlying medium and recursively updates all of its children
 * too.
 *
 * <h3>Invalid Cells</h3>
 * 
 * Threads may have obtained a WFSCell object that has since been removed from
 * the WFS. In such a case, a cell is considered "invalid" and any calls to its
 * methods will throw an IllegalStateException.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class WFSCell extends WFSObject {
    /* The cell name without any naming convention suffix */ 
    private String cellName = null;
    
    /* The canonical (unique) name of the cell in the WFS (no suffixes) */
    private String canonicalName = null;
    
    /* The unique path name of the cell (including suffixes) */
    private String pathName = null;
    
    /* The directory of child cells, null until the directory exist. */
    protected WFSCellDirectory cellDirectory = null;
 
    /* The implementation specific cell delegate */
    private CellDelegate delegate = null;
    
    /* The cell's setup data, null if it has not yet been read */
    protected String cellSetup = null;
    
    /* True if the cell's setup should be written upon the next write() */
    private boolean cellSetupUpdate = false;
    
    /* The parent cell directory that contains this cell */
    private WFSCellDirectory parentDirectory = null;
    
    /* A weak reference to the main WFS object */
    private WeakReference<WFS> wfsRef = null;
    
    /* The time the file was last modified when it was last read */
    private long lastModifiedWhenRead = -1;
    
    /** Default constructor, should never be called */
    protected WFSCell(WFS wfs, String cellName, WFSCellDirectory parentDirectory, CellDelegate delegate) {
        this.wfsRef = new WeakReference(wfs);
        this.parentDirectory = parentDirectory;
        this.delegate = delegate;
        this.cellName = cellName;
        
        /*
         * Form the canonical name of the cell as the canonical name of the
         * parent cell, followed by the name of this cell. If there is not
         * parent cell (for the root), then just use the cell name as the
         * canonical name
         */
        WFSCell parentCell = this.parentDirectory.getAssociatedCell();
        if (parentCell != null) {
            this.canonicalName = parentCell.getCanonicalName() + "/" + cellName;
        }
        else {
            this.canonicalName = cellName;
        }
        
        /*
         * Form the path name of the cell as the path name of the parent
         * directory, followed by the name of the cell plus the name
         * convention suffix.
         */
        this.pathName = parentDirectory.getPathName() + cellName + WFS.CELL_FILE_SUFFIX;
        
        /* Create the cellDirectory object if the directory exists */
        if (this.delegate.cellDirectoryExists() == true) {
            DirectoryDelegate dirDelegate = this.delegate.createDirectoryDelegate();
            this.cellDirectory = new WFSCellDirectory(this.wfsRef.get(), this, dirDelegate);
        }
    }
    
    /**
     * Returns the parent cell directory, that is, the cell directory in which
     * this cell is contained. If this cell is contained in the root of the
     * WFS, then the root directory is returned.
     * 
     * @return The parent cell directory
     */
    public WFSCellDirectory getParentCellDirectory() {
        /* Check to see if the cell is no longer valid */
        super.checkInvalid();
        
        /*
         * There is no need to protect this with a read lock. Even though the
         * parent of a cell can change on the file system-level, that gets
         * manifested as removing a cell and re-adding it under the parent.
         * Therefore, the 'parentDirectory' member variable is invariant in
         * the WFSCell class.
         */
        return this.parentDirectory;
    }
    
    /**
     * Returns the parent cell. If this cell is in the root directory, then
     * this method returns null.
     * 
     * @return The parent cell, null if in the root directory
     */
    public WFSCell getParentCell() {
        /* Check to see if the cell is no longer valid */
        super.checkInvalid();
        
        /*
         * There is no need to protect this with a read lock. Even though the
         * parent of a cell can change on the file system-level, that gets
         * manifested as removing a cell and re-adding it under the parent.
         * Therefore, the 'parentDirectory' member variable is invariant in
         * the WFSCell class.
         */
        return this.parentDirectory.getAssociatedCell();
    }
    
    /**
     * Returns the canonical name for this cell, which is guaranteed to unique
     * identify it within the WFS.
     * 
     * @return A unique, canonical name for the cell
     */
    public String getCanonicalName() {
        /* Check to see if the cell is no longer valid */
        super.checkInvalid();
        
        /*
         * There is no need to protect this with a read lock. Even though a
         * cell can move around within a file system, this is implemented as
         * removing a cell and adding it somewhere else. The 'canonicalName'
         * member variable is invariant in the WFSCell class.
         */
        return this.canonicalName;
    }

    /**
     * Returns the name of the cell, without the standard suffix
     * 
     * @return The name of the cell file in the WFS
     */
    public String getCellName() {
        /* Check to see if the cell is no longer valid */
        super.checkInvalid();
        
        /*
         * There is no need to protect this with a read lock. Even though a
         * cell can move around within a file system, this is implemented as
         * removing a cell and adding it somewhere else. The 'canonicalName'
         * member variable is invariant in the WFSCell class.
         */
        return this.cellName;
    }

    /**
     * Returns the directory containing any children of this cell, null if no
     * such directory exists.
     * 
     * @return The child directory for this cell, null if none
     */
    public WFSCellDirectory getCellDirectory() {
        /* Check to see if the cell is no longer valid */
        super.checkInvalid();
        
        /*
         * Protect with a read lock. The cell directory is initially null and
         * is created by the public method createCellDirectory().
         */
        this.wfsRef.get().getReadLock().lock();
        try {
            return this.cellDirectory;
        } finally {
            this.wfsRef.get().getReadLock().unlock();
        }
    }
    
    /**
     * Creates a directory that will contain children of this cell. Depending
     * upon the type of WFS, this routine may either update a file system
     * immediately, or simply store the update in memory. Returns the object
     * representing the new directory. If the cell directory already exists,
     * returns the exist directory object.
     * 
     * @return A WFSCellDirectory object representing the new directory
     */
    public WFSCellDirectory createCellDirectory() {
        /* Check to see if the cell is no longer valid */
        super.checkInvalid();
        
        /* Make sure the thread has write permissions */
        this.wfsRef.get().checkOwnership();
        
        /* If the cell directory already exists, return it */
        if (this.cellDirectory != null) {
            return this.cellDirectory;
        }
        
        /*
         * Simply create the object -- the actual directory does not get
         * created until a write() happens.
         */
        DirectoryDelegate dirDelegate = this.delegate.createDirectoryDelegate();
        this.cellDirectory = new WFSCellDirectory(this.wfsRef.get(), this, dirDelegate);
        return this.cellDirectory;
    }
 
    /**
     * Removes the directory containing all of the children. If this directory
     * does not exist, then this method does nothing.
     */
    public void removeCellDirectory() {
        /* Check to see if the cell is no longer valid */
        super.checkInvalid();
        
        /* Make sure the thread has write permissions */
        this.wfsRef.get().checkOwnership();
        
        /* Tell the cell directory to invalidate itself */
        if (this.cellDirectory != null) {
            this.cellDirectory.setInvalid();
        }
        
        /* Simply set the object to null -- to be removed upon a write() */
        this.cellDirectory = null;
    }

    /**
     * Returns the time (in milliseconds since the epoch) this file was last
     * modified.
     * 
     * @return The last time the cell was modified in the WFS
     */
    public long getLastModified() {
        /* Check to see if the cell is no longer valid */
        super.checkInvalid();
        
        /*
         * There is no need to protect this with a read lock. The only way this
         * can change is if the disk file changes.
         */
        return this.delegate.getLastModified();
    }

    /**
     * Returns the cell's setup properties as a string.
     * 
     * @return A string representing the cell's properties
     * @throw IOException Upon general I/O error
     */
    public String getCellSetup() throws IOException {
        /* Check to see if the cell is no longer valid */
        super.checkInvalid();
        
        /*
         * The multi-threaded behavior of this method is a bit complicated. On
         * the one-hand, the method should try to acquire the read lock. One
         * the other head, this method can be called simultaneously by multiple
         * threads, and we want to avoid multiple decode() calls. So we will
         * acquire a read lock (that will prevent other writes to the 'cellSetup'
         * member variable) and also synchronize around cellSetup so that more
         * than one thread calling this method won't re-parse the same data.
         */        
        this.wfsRef.get().getReadLock().lock();
        
        try {
            synchronized (this) {
                /*
                 * If the cell's setup has already been read and the cell has
                 * not been told to reload itself, then return the loaded
                 * properties.
                 */
                if (this.cellSetup != null && this.isDirty() == false) {
                    return this.cellSetup;
                }
                else if (this.cellSetup != null) {
                    /*
                     * If the cell's properties have been loaded and we have
                     * been told to reload the cell's properties, check whether
                     * the last modified time has changed. If so, reload the
                     * properties
                     */
                    if (this.lastModifiedWhenRead < this.getLastModified()) {
                        this.cellSetup = this.delegate.decode();
                        this.lastModifiedWhenRead = this.getLastModified();
                        this.cellSetupUpdate = false;
                        this.wfsRef.get().fireCellAttributeUpdate(this);
                        return this.cellSetup;
                    }
                    this.setDirty(false);
                }

                /* Otherwise, read it in from disk and return it */
                try {
                    this.cellSetup = this.delegate.decode();
                    this.lastModifiedWhenRead = this.getLastModified();
                    this.cellSetupUpdate = false;
                    return this.cellSetup;
                } catch (java.lang.UnsupportedOperationException excp) {
                    /* If it is unsupported, just quietly return null */
                    return null;
                }
            }
        } finally {
            this.wfsRef.get().getReadLock().unlock();
        }
    }
    
    /**
     * Updates the cell's properties in memory.
     * 
     * @param cellSetup The cell properties class
     * @throw InvalidWFSCellException If the cell properties is invalid
     */
    public void setCellSetup(String cellSetup) {
        /* Check to see if the cell is no longer valid */
        super.checkInvalid();
        
        /* Make sure the thread has write permissions */
        this.wfsRef.get().checkOwnership();
        
        /*
         * Sets the cell setup class and indicates the cell to be updated upon
         * the next write
         */
        this.cellSetup = cellSetup;
        this.cellSetupUpdate = true;
    }
    
    /**
     * Updates the contents of the cell to the underlying medium.
     * 
     * @throw IOException Upon general I/O error
     */
    public void writeCell() throws IOException, JAXBException {
        /* Check to see if the cell is no longer valid */
        super.checkInvalid();
        
        /* Make sure the thread has write permissions */
        this.wfsRef.get().checkOwnership();
        
        /*
         * Checks to see whether we should update the cell's setup class on
         * disk. If so, then write and clear the update flag. If not, do
         * nothing.
         */
        if (this.cellSetupUpdate == true) {
            this.delegate.encode(this.cellSetup);
            this.cellSetupUpdate = false;
        }
    }
    
    /**
     * Updates the contents of the cell to the underlying medium, and recursively
     * for all of its children cells.
     * 
     * @throw IOException Upon general I/O error
     */
    public void write() throws IOException, JAXBException {
        /* Check to see if the cell is no longer valid */
        super.checkInvalid();
        
        /* Make sure the thread has write permissions */
        this.wfsRef.get().checkOwnership();
        
        /* Updates the cell's information */
        this.writeCell();
        
        /*
         * Ask the cell's directory to write itself out to disk. If it does
         * not exist, then simply remove it.
         */
        if (this.cellDirectory == null) {
            this.delegate.removeCellDirectory();
            return;
        }
        
        /*
         * Write the directory, creating it if necessary. If the directory has
         * yet to been touched, then its children are not loaded, in which case
         * we do not need to save anything. Catch the WFSCellNotLoadedException
         * and ignore.
         */
        this.delegate.createCellDirectory();
        this.cellDirectory.write();
    }
    
    /**
     * Tells the cell that the underlying medium has changed and it should set
     * its contents to "dirty" -- so the next time information is fetched, it
     * will be re-loaded from the underlying medium. Recursively sets all child
     * cells to be "dirty" too.
     */
    public void setReload() {
        /* Make sure the thread has write permissions */
        this.wfsRef.get().checkOwnership();
        
        /*
         * Mark this directory as dirty. This will cause the cell properties
         * to be reloaded if the last modified date has changed.
         */
        super.setDirty(true);
 
        /*
         * Because the cell directory is created only at object construction
         * time, we need to check whether it still exists, or whether it newly
         * exists.
         */
        if (this.cellDirectory == null && this.delegate.cellDirectoryExists() == true) {
            DirectoryDelegate dirDelegate = this.delegate.createDirectoryDelegate();
            this.cellDirectory = new WFSCellDirectory(this.wfsRef.get(), this, dirDelegate);
        }
        else if (this.cellDirectory != null && this.delegate.cellDirectoryExists() == false) {
            this.cellDirectory.setInvalid();
            this.cellDirectory = null;
        }
        
        /* Recursively tell all children that they are dirty too */
        if (this.cellDirectory != null) {
            this.cellDirectory.setReload();
        }
    }
    
    /**
     * Returns the unique path name of this cell, including all of the naming
     * convention suffixes.
     * 
     * @return The unique path name
     */
    protected String getPathName() {
        return this.pathName;
    }
    
    /**
     * Given a cell or path name with the cell naming convention suffix (-wlc.xml),
     * parses off the suffix and returns the rest. If the file name does not have
     * the propery suffix, the original name given.
     * 
     * @param fileName The file name (with suffix)
     * @return cellName The cell name (whtout suffix)
     */
    public static String stripCellFileSuffix(String fileName) {
        Logger logger = WFS.getLogger();
        try {
            int index = fileName.indexOf(WFS.CELL_FILE_SUFFIX);
            return fileName.substring(0, index);
        } catch (java.lang.IndexOutOfBoundsException excp) {
            /* Quietly return the original */
        }
        return fileName;
    }
 
    /**
     * Sets the object to be invalid.
     */
    @Override
    protected void setInvalid() {
        super.setInvalid();
        
        /* Recursively ask any cell directory to invalidate itself */
        if (this.cellDirectory != null) {
            this.cellDirectory.setInvalid();
        }
        
        /* Null out references to help out garbage collection */
        this.cellDirectory = null;
        this.cellSetup = null;
        this.parentDirectory = null;
    }
}
