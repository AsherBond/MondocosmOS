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
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.tools.wfs.delegate.CellDelegate;
import org.jdesktop.wonderland.tools.wfs.delegate.DirectoryDelegate;

/**
 * The WFSCellDirectory class represents a directory within the WFS that stores
 * child cells. (If the cell is named 'cell-wlc.xml' within the WFS, this
 * directory is correspondingly named 'cell-wld/' although these details are
 * hidden by the WFS API.) 
 * <p>
 * The list of child cells are obtained via two method calls: getCellNames()
 * and getCells() method. A cell may also be obtained via the getCellByName()
 * by giving the name of the cell (without the '-wlc.xml' suffix and extension).
 * The list of cells are not read until either one of these three methods is
 * invoked.
 * <p>
 * The list of child cells may be updated via the addCell() and removeCell()
 * methods. This updates the list of children in memory and is not written by
 * to the underlying medium until explicitly told to do so. These methods
 * first load all of the cells, if not already done so. This may be a time-
 * consuming task, so users of this API are strongly encouraged to call the
 * getCells() method themselves at a time of their choosing.
 * <p>
 * Threads may walk up the directory tree by finding the cell associated with
 * this directory, with the getAssociatedCell() method.
 * 
 * <h3>Writring</h3>
 * 
 * The directory on the underlying medium is updated via the write() method.
 * Each cell must have been first loaded -- either by the getCells() or the
 * getCellNames() methods -- otherwise, this method does nothing. If all cells
 * have been loaded, the method simply calls the write() method on each cell.
 * 
 * <h3>Invalid Directories</h3>
 * 
 * If a cell directory is removed (via the WFSCell.removeCellDirectory() method)
 * threads may still have a reference to the WFSCellDirectory object, yet it
 * is no longer valid. In this case, the state of the WFSCellDirectory object
 * is set to "invalid" and invocation of any of its methods will throw an
 * IllegalStateException.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class WFSCellDirectory extends WFSObject {
    /*
     * The cell associated with this directory. That is, the associated cell is
     * the parent of all cells contained within this directory. This is set
     * during object construction time and is invariant.
     */
    private WFSCell associatedCell = null;
       
    /*
     * The path name of the cell directory, including all of the naming
     * convention suffixes.
     */
    private String pathName = null;
    
    /*
     * A Hashmap of cell names (keys) and WFSCell objects (values) contained
     * within this directory. This hashmap is initially null, indicating that
     * the directory has not been read yet.
     */
    protected HashMap<String, WFSCell> children = null;
    
    /* The implementation specific delegate for directories */
    protected DirectoryDelegate delegate = null;
    
    /* A weak reference to the main WFS object */
    protected WeakReference<WFS> wfsRef = null;
    
    /**
     * Constructor, takes the implementation-specific directory delegate
     */
    public WFSCellDirectory(WFS wfs, WFSCell assocCell, DirectoryDelegate del) {
        this.wfsRef = new WeakReference(wfs);
        this.associatedCell = assocCell;
        this.delegate = del;
    
        /* Compute what the path name of this directory should be */
        try {
            String cellPathWithSuffix = associatedCell.getPathName();
            int index = cellPathWithSuffix.indexOf(WFS.CELL_FILE_SUFFIX);
            String cellPath = cellPathWithSuffix.substring(0, index);
            this.pathName = cellPath + WFS.CELL_DIRECTORY_SUFFIX + "/";
        } catch (java.lang.IndexOutOfBoundsException excp) {
            WFS.getLogger().log(Level.SEVERE, "A WFSCellDirectory class was created with an invalid cell");
            WFS.getLogger().log(Level.SEVERE, "cell path: " + associatedCell.getPathName());
        }
    }
    
    /**
     * Constructor, which takes no parent directory (if this is the root
     * directory
     */
    public WFSCellDirectory(WFS wfs, DirectoryDelegate delegate) {
        this.wfsRef = new WeakReference(wfs);
        this.associatedCell = null;
        this.delegate = delegate;
        this.pathName = "";
    }
    
    /**
     * Returns an array of strings representing the cell names in the current
     * directory. Returns an empty array if no cells exist.
     * 
     * @return An array of cell names contained within this directory
     */
    public String[] getCellNames() {
        /* Check to see if the cell is no longer valid */
        super.checkInvalid();
        
        /*
         * First attempt to read in the cells first. This simply returns if the
         * children have already been created. We don't need to explicitly
         * acquire a read lock because the loadChildCells() method does so.
         */
        this.loadChildCells();
        return this.children.keySet().toArray(new String[]{});
    }
          
    /**
     * Returns a cell given its name, returns null if it does not exist.
     * 
     * @return The cell given its name, null if it does not exist
     */
    public WFSCell getCellByName(String cellName) {
        /* Check to see if the cell is no longer valid */
        super.checkInvalid();
        
        /*
         * First attempt to read in the cells first. This simply returns if the
         * children have already been created. We don't need to explicitly
         * acquire a read lock because the loadChildCells() method does so.
         */
        this.loadChildCells();
        return this.children.get(cellName);
    }
    
    /**
     * Returns an array the WFSCell class representing all of the cells in the
     * current directory. Returns an empty array if no cells exist.
     * 
     * @return An array of cells containing within this directory
     */
    public WFSCell[] getCells() {
        /* Check to see if the cell is no longer valid */
        super.checkInvalid();
        
        /*
         * First attempt to read in the cells first. This simply returns if the
         * children have already been created. We don't need to explicitly
         * acquire a read lock because the loadChildCells() method does so.
         */
        this.loadChildCells();
        return this.children.values().toArray(new WFSCell[] {});
    }

    /**
     * Returns the cell associated with this directory. This cell is the parent
     * of all the cells contained within the directory.
     * 
     * @return The associated cell
     */
    public WFSCell getAssociatedCell() {
        /* Check to see if the cell is no longer valid */
        super.checkInvalid();
        
        /*
         * There is no need to protect this with a read lock. Even though a
         * cell can move around within a file system, this is implemented as
         * removing a cell and adding it somewhere else. The 'associatedCell'
         * member variable is invariant in the WFSDirectory class.
         */
        return this.associatedCell;
    }

    /**
     * Adds a cell to this directory. Takes the name of the cell; a new WFSCell
     * class is returned. If the cell name already exists, this method returns
     * null.
     * 
     * @param cellName The name of the new cell to add
     * @return The class representing the new cell
     */
    public WFSCell addCell(String cellName) {
        /* Check to see if the cell is no longer valid */
        super.checkInvalid();
        
        /* Make sure the thread has write permissions */
        this.wfsRef.get().checkOwnership();
        
        /*
         * First attempt to read in the cells first. This simply returns if the
         * children have already been created. We don't need to explicitly
         * acquire a read lock because: (1) we are in a write lock, (2) the
         * loadChildCells() method does so.
         */
        this.loadChildCells();
        
        /*
         * Check if the cell name already exists, if so, return the cell
         */
        if (this.children.containsKey(cellName)) {
            return children.get(cellName);
        }
        
        /* Call the implementation to create the cell */
        CellDelegate cellDelegate = this.delegate.createCellDelegate(cellName);
        WFSCell cell = new WFSCell(this.wfsRef.get(), cellName, this, cellDelegate);
        this.children.put(cellName, cell);
        
        /* Fire events to indicate a new cell has been added */
        this.wfsRef.get().fireCellChildrenAdded(this.getAssociatedCell());
        return cell;
    }
        
    /**
     * Removes a cell from this directory, if it exists. If it does not exist,
     * this method does nothing.
     * 
     * @param cell The cell to remove
     * @throw IOException Upon I/O error when removing the cell
     */
    public void removeCell(WFSCell cell) {
        /* Check to see if the cell is no longer valid */
        super.checkInvalid();
        
        /* Make sure the thread has write permissions */
        this.wfsRef.get().checkOwnership();
        
        /*
         * First attempt to read in the cells first. This simply returns if the
         * children have already been created. We don't need to explicitly
         * acquire a read lock because: (1) we are in a write lock, (2) the
         * loadChildCells() method does so.
         */
        this.loadChildCells();

        /*
         * Remove the cell from the hashmap. If it was present (non-null return)
         * then set the cell to be invalid and fire a remove event
         */
        if (this.children.remove(cell.getCellName()) != null) {
            cell.setInvalid();
            
            /*
             * Fire an event to indicate the cell has been removed.
             */
            this.wfsRef.get().fireCellChildrenRemoved(this.getAssociatedCell());
        }
    }
    
    /**
     * Writes all of the cells in this directory to the underlying medium. The
     * list of cells must first be loaded (e.g. by calling getCells()),
     * otherwise this method does nothing.
     * 
     * @throw IOException Upon general I/O error
     * @throw JAXBException Upon error writing to XML
     */
    public void write() throws IOException, JAXBException {
        /* Check to see if the cell is no longer valid */
        super.checkInvalid();
        
        /* Make sure the thread has write permissions */
        this.wfsRef.get().checkOwnership();
        
        /* If the hashmap is null, do nothing */
        if (this.children == null) {
            return;
        }

        /*
         * Iterate through all of the cells and write them out. It is ok if
         * not all of them have been parsed yet, simply fail gracefully and
         * continue where possible.
         */
        for (WFSCell cell : this.children.values()) {
            try {
                cell.write();
            } catch (java.io.IOException excp) {
                // log some error and continue
                WFS.getLogger().warning("Unable to write cell to WFS: " + cell);
            } catch (javax.xml.bind.JAXBException excp) {
                // log some error and continue
                WFS.getLogger().warning("Unable to write cell to WFS: " + cell);
            }
        }
        
        /*
         * We now need to cleanup any existing files or directories that are
         * no longer present. We ask the implementation of the cell directory
         * to do this.
         */
        this.delegate.cleanupDirectory(this.children);
    }
    
    /**
     * Tells the cell directory that the underlying medium has changed and it
     * should set its contents to "dirty" -- so the next time information is
     * fetched, it will be re-loaded from the underlying medium. Recursively
     * sets all child cells to be "dirty" too.
     */
    public void setReload() {
        /* Make sure the thread has write permissions */
        this.wfsRef.get().checkOwnership();
        
        /* Mark this directory as dirty */
        super.setDirty(true);
        
        /* Recursively tell all children that they are dirty too */
        if (this.children != null) {
            Iterator<WFSCell> it = this.children.values().iterator();
            while (it.hasNext() == true) {
                WFSCell cell = it.next();
                cell.setReload();
            }
        }
    }
    
    /**
     * Returns the unique path name of this cell directory, including all of
     * the naming convention suffixes.
     * 
     * @return The unique path name
     */
    protected String getPathName() {
        return this.pathName;
    }
    
    /**
     * Sets the object to be invalid.
     */
    @Override
    protected void setInvalid() {
        super.setInvalid();
        
        /* Recursively set all child cells to be invalid */
        if (this.children != null) {
            Iterator<WFSCell> it = this.children.values().iterator();
            while (it.hasNext() == true) {
                WFSCell cell = it.next();
                cell.setInvalid();
            }
        }
        
        /* Clean up references to help garbage collection */
        this.children = null;
        this.delegate = null;
        this.associatedCell = null;      
    }
    
    /**
     * This utility method fetches the names of the cell from the WFS. Any
     * method that returns or updates the list of child cells calls this
     * method first, which does nothing if the child cells have already been
     * loaded. This method simplifies multithreaded issues by putting the
     * loading in once place.
     */
    private void loadChildCells() {
        /*
         * The synchronization issues with this method are complex. First, this
         * method will acquire the read lock. That will allow methods to read
         * the cells (e.g. getCellNames()) to complete without any other thread
         * interfering. But it will also allow methods that write to the list
         * of children (e.g. addChild()) to first read in the list of children,
         * since a writer can acquire a read lock.
         * 
         * We also want to protect against multiple read threads from calling
         * this method at the same time. Therefore, we will also protect the
         * 'children' member variable so that only one thread can call this
         * thread at a time.
         */
        this.wfsRef.get().getReadLock().lock();
        
        try {
            synchronized(this) {
                /*
                 * If the hashmap is not null and the WFS has not been reloaded
                 * (dirty = false), then simply return. We need to make this
                 * check inside the synchronized keyword because the creation
                 * of the hashmap and filling the hashmap happen in multiple
                 * steps below.
                 */
                if (this.children != null && super.isDirty() == false) {
                    return;
                }
                else if (this.children != null) {
                    /*
                     * Otherwise if the children have already been loaded and
                     * we have asked this cell directory to reload itself, we
                     * should intelligently figure out which cells are new and
                     * which are no longer existent.
                     */
                    this.refreshCells();
                    super.setDirty(false);
                    return;
                }

                /*
                 * Call the delegate to actually load the cells. The delegate will
                 * return an array of String cell names. We need to parse off the
                 * -wlc.xml extension, and create new cells
                 */
                this.children = new HashMap<String, WFSCell>();
                String fileNames[] = this.delegate.loadCellNames();
                for (String fileName : fileNames) {
                    String name = WFSCell.stripCellFileSuffix(fileName);
                    CellDelegate del = this.delegate.createCellDelegate(name);
                    WFSCell cell = new WFSCell(this.wfsRef.get(), name, this, del);
                    this.children.put(name, cell);
                }
            }
        } finally {
            this.wfsRef.get().getReadLock().unlock();
        }
    }
    
    /**
     * Refresh the list of cells intelligently. Create new cells for those that
     * are new, remove the cell for those that have been removed. This method
     * assumes that the cells have already been loaded (this.children != null)
     */
    private void refreshCells() {
        /* Make a copy of the existing list of children */
        HashMap<String, WFSCell> oldChildren = new HashMap(this.children);
        
        /* Booleans indicating whether to fire added or removed events */
        boolean fireAdded = false;
        boolean fireRemoved = false;
        
        /*
         * Loop through and see if the cell is in the directory. If it already
         * exists in the list, do nothing. If it is new, create a new cell and
         * fire an event. Remove it from the list of "old" children.
         */
        String fileNames[] = this.delegate.loadCellNames();
        for (String fileName : fileNames) {
            String name = WFSCell.stripCellFileSuffix(fileName);
            if (oldChildren.containsKey(name) == true) {
                oldChildren.remove(name);
            }
            else {
                CellDelegate del = this.delegate.createCellDelegate(name);
                WFSCell cell = new WFSCell(this.wfsRef.get(), name, this, del);
                this.children.put(name, cell);
                fireAdded = true;   
            }
        }
        
        /*
         * This list of children that no longer exist in the directory is now
         * given by oldChildren. Remove each child, one by one, invalidate them,
         * and fire a remove event, and remove them from the list.
         */
        Iterator<String> it = oldChildren.keySet().iterator();
        while (it.hasNext() == true) {
            /*
             * Fetch the cell from the list, invalidate it (recursively), and
             * remove from the list and fire an event.
             */
            String cellName = it.next();
            WFSCell cell = this.children.get(cellName);
            cell.setInvalid();
            this.children.remove(cellName);
            fireRemoved = true;
        }
        
        /* Fire the added event if needed */
        if (fireAdded == true) {
            this.wfsRef.get().fireCellChildrenAdded(this.getAssociatedCell());
        }
        
        /* Fire the removed event if needed */
        if (fireRemoved == true) {
            this.wfsRef.get().fireCellChildrenRemoved(this.getAssociatedCell());
        }
    }
}
