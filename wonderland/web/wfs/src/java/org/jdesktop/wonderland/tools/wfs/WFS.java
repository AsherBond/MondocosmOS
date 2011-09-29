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
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.logging.Logger;
import javax.swing.event.EventListenerList;
import javax.xml.bind.JAXBException;
import org.jdesktop.wonderland.tools.wfs.event.WFSEvent;
import org.jdesktop.wonderland.tools.wfs.event.WFSListener;

/**
 * <h3>The Wonderland File System (WFS)</h3>
 * 
 * The WFS class represents a Wonderland File System (WFS). A WFS is a series
 * of directories and files (cells) that describe a world or a portion of a
 * world.
 * <p>
 * An instance of the WFS class is created through methods on the WFSFactory
 * class. Instances of WFS (or any of its subclasses) should never be created
 * directly.
 * <p>
 * The WFS class and its associated interfaces (WFSCell and WFSCellDirectory)
 * enable the reading and writing of WFSs. These sets of abstractions are meant
 * to be low-level: they are simply Java class representations of the directory
 * and file concepts defined by WFS.
 * <p>
 * The cells contained within the root WFS directory are represented by the
 * WFSRootDirectory class and obtained via the WFS.getRootDirectory() method.
 * 
 * <h3>Reading and Writing Model</h3>
 *
 * A WFS is only read in when asked (i.e. the structure of the WFS is discovered
 * progressively; it is not read all at once into memory). Once read, the WFS
 * structure resides in memory. The underlying file system may be forceably
 * re-read; otherwise changes made to the file system on the operation system
 * level will not be seen in this software layer.
 * <p>
 * The structure and content of the WFS that resides in memory may be updated
 * programmatically. Changes do not take effect to the underyling medium until
 * explictly done so. Updating the WFS to its underlying medium (e.g. disk) can
 * happen in one of several ways: only the configuration parameters of a cell
 * may be updated, or a cell and all of its children may be updated (representing
 * a subtree of the WFS), or the entire WFS tree may be updated. Updates to the
 * medium are done with the minimum amount of disruption to any existing
 * structure--new cells are added, while old cells are removed (versus
 * obliterating an existing structure and rewriting from scratch).
 * 
 * <h3>Supported Underlying Mediums</h3>
 *
 * A WFS may exist as a hierarchy of files and directories on a disk file
 * system, or a new one may be created on disk. These WFSs are created by giving
 * a URL with the 'file:' protocol. The WFSFactory.open(URL) method opens an
 * existing WFS on disk, while the WFSFactory.create(URL) creates a new WFS on
 * disk. This API supports both reading and writing for disk file systems.
 * <p>
 * A WFS may also exist as a jar file that encodes the proper directory and
 * file structure. The jar file may be located on a disk locally, or it may
 * be fetched from the network. The WFSFactory.open(URL) method opens a WFS
 * encoded as a jar file, whether on disk locally (with the 'file:' protocol)
 * or over the network (with the 'http:' protocol). See the ArchiveWFS class
 * for more details on the format of the URL in these cases. This API supports
 * only reading of jar files at this time.
 * <p>
 * In the final case, the WFS resides entirely in memory and may be serlialized
 * at once to an output stream, encoded as a jar file. This usage is typically
 * used if one wishes to create a WFS from scratch and write it out over a
 * network connection.
 *
 * <h3>Support for Multi-Threaded Access</h3>
 * 
 * This API is meant to be used in situations where multiple threads may be
 * attempting to both read and write the WFS. Synchronization is based upon
 * the concept that one thread may "own" an entire WFS tree and may read and
 * write while it owns the tree. If no thread owns the tree, then anyone may
 * read.
 * <p>
 * This locking scheme was implemented for a couple of reasons. First, updates
 * to a WFS typically happen over a series of updates such as creating new
 * cells, and deleting or modifying existing cells. Second, locking the entire
 * WFS tree simplifies what is locked.
 * <p>
 * It is expected that threads that lock a WFS only do so to update the WFS
 * tree quickly and then release the lock. It is up to the calling thread to
 * release the lock, especially under exceptional conditions it may encounter.
 * 
 * <h3>Listeners</h3>
 * 
 * Threads may register listeners for changes on a WFS. These changes include:
 * when new cells are added, when existing cells are removed, and when the
 * properties of a cell have changed. Each event is delivered in its own thread
 * of execution. If a cell is removed, only a single "remove" event is delivered
 * for that thread--cell "remove" events are not delivered for any of its child
 * cells, even though they are also implicitly removed from the WFS.
 * 
 * <h3>Reloading from the Underlying Medium</h3>
 * 
 * Normally, multiple threads interact with this WFS API to update its state
 * and write it back out to the underlying medium. The WFS may also be updated
 * directly on the underlying medium (e.g. modifying a disk directory structure
 * or editing WFS cell files). This API provides a minimum facility to
 * coordinate between the two.
 * <p>
 * It is generally expected that updates made directly to the underlying medium
 * constitute significant re-arrangements of the layout of the world and that a
 * mechanism that exists at a level higher than this API should first quiesce
 * all activity related to updating the WFS.
 * <p>
 * The WFS (or any subcomponent thereof) may be told to "reload" itself via the
 * WFS.setReload() method. (This method also exists on the WFSCell and
 * WFSCellDirectory classes). This method call has the effect of marking the
 * object, and all subobjects as "dirty", so that the next time information is
 * read by threads from the API, information is re-read from the underlying
 * medium. The objects within the API are updated intelligently: that is, when
 * cells have been added or removed from a directory, existing WFSCell objects
 * are left alone, and only the needed new cells are created or removed.
 * <p>
 * To note: the reload mechanism obeys the principle of the API to perform
 * "lazy" loads of information from the underlying medium. For example, if a
 * cell directory has been told to "reload", it does not fetch the information
 * from the directory on the underlying medium until the next getCells() method
 * call, for example.
 * <p>
 * The setReload() methods require the calling thread to own the write lock for
 * the WFS. There is still, however, some synchronization risks:
 * <p>
 * <ol>
 * <li>If a user updates the WFS on the underlying medium and before he/she has
 * the opportunity to flag a reload, a thread changes the WFS and writes the
 * changes back to the underlying medium, the changes the user made directly to
 * the underlying medium may be affected.
 * <li>If a thread makes updates to the WFS but does not write them back out to
 * the underyling medium, and a user updates the underlying medium directly and
 * flags a reload, then the changes made by the thread to the WFS may be
 * affected.
 * </ol>
 * <p>
 * These two examples illustrate the necessity of insuring that all other 
 * activity that could interact with the WFS API quiesce before updates are made
 * directly to the underlying medium. The decision on how to quiesce other
 * activity is left to high software layers.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public abstract class WFS {
    
    /* The error logger */
    private static final Logger logger = Logger.getLogger(WFS.class.getName());
    
    /* Prefix names for WFS components */
    public static final String CELL_DIRECTORY_SUFFIX = "-wld";
    public static final String CELL_FILE_SUFFIX = "-wlc.xml";
    public static final String WFS_DIRECTORY_SUFFIX = "-wfs";
    
    /* Support URL protocols for file systems */
    public static final String FILE_PROTOCOL = "file";
    public static final String JAR_PROTOCOL  = "jar";

    /* The name of the WFS, without the "-wfs" suffix */
    private String name = null;
    
    /*
     * The directory object associated with the root of the file system. This
     * is created by subclasses of the WFS object in their constructor and is
     * invariant for the life of the WFS.
     */
    protected WFSRootDirectory directory = null;
    
    /*
     * The single owner of this WFS tree. To be able to update or write to
     * the tree, threads must aquire this mutex first.
     */
    private ReentrantReadWriteLock ownerLock = new ReentrantReadWriteLock();
     
    /* A list of cell event listeners */
    private EventListenerList eventListenerList = new EventListenerList();
    
    /**
     * Creates a new instance of WFS. This constructor should not be called--
     * one of the factory methods on the WFSFactory class should be used
     * instead.
     * 
     * @param name The name of the WFS without the '-wfs' suffix
     */
    public WFS(String name) {
        this.name = name;
    }
    
    /**
     * Returns the name of the WFS (without any '-wfs' suffix).
     * 
     * @return The name of the WFS
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Close any open resource associated with the WFS
     */
    public void close() {
        // Do nothing, to be overridden perhaps
    }
   
    /**
     * Attempt to acquire the owner lock for this WFS, returning immediately
     * if the lock is not available. Returns true if the ownership was
     * successfully acquired, false if not.
     * 
     * @return True if ownership is acquired, false if not
     */
    public boolean tryAcquireOwnership() {
        return this.ownerLock.writeLock().tryLock();
    }
    
    /**
     * Attempt to acquire the owner lock for this WFS, blocking until the lock
     * is available.
     * 
     * @throw InterruptedException If the thread is interrupted while trying
     */
    public void acquireOwnership() throws java.lang.InterruptedException {
        this.ownerLock.writeLock().lock();
    }
    
    /**
     * Release the ownership lock. If the lock is not owned by this thread,
     * then do nothing
     */
    public void release() {
        /*
         * A few words are in order: since every time a thread acquires the lock,
         * a count is incremented in the lock. Upon release, the count is
         * decremented. In order to completely release the lock, the caller
         * needs to call as many releases() as it did acquires(). To make things
         * simpler, this method calls however many unlock() calls as is required
         * the release the lock.
         */
        if (this.ownerLock.isWriteLockedByCurrentThread() == true) {
            while (this.ownerLock.getWriteHoldCount() > 0) {
               this.ownerLock.writeLock().unlock();
            }
        }
    }
    
    /**
     * Checks whether the current thread has ownership of the WFS, otherwise
     * throw an IllegalStateException (a runtime exception so it does not need
     * to be declared for each method that calls this method.
     * 
     * @throw IllegalStateException If the thread is not the owner
     */
    public void checkOwnership() {
        if (this.ownerLock.isWriteLockedByCurrentThread() == false) {
            throw new IllegalStateException("Thread does not current hold ownership of WFS.");
        }
    }
    
    /**
     * Returns the root cell directory class of the WFS.
     * 
     * @return The directory containing the children in the root of the WFS
     */
    public WFSRootDirectory getRootDirectory() {
        /*
         * The root directory class object is not something that is going to
         * change, so there is no need to protect this with a read lock.
         */
        return this.directory;
    }
    
    /**
     * Returns the logger for the WFS package
     */
    public static Logger getLogger() {
        return WFS.logger;
    }
    
    /**
     * Writes the entire WFS to the underlying medium, including the meta-
     * information contained within the root directory, the cells contained
     * within the root directory, and any child directories.
     * <p>
     * @throw JAXBException Upon error writing to XML
     * @throw IOException Upon a general I/O error
     */
    public void write() throws IOException, JAXBException {
        /* Make sure the thread has write permissions */
        this.checkOwnership();
        
        this.getRootDirectory().write();
    }

    /**
     * Writes an entire WFS to an output stream, encoded as a jar file. The
     * side effect of this method is to load the entire WFS if it has not
     * already been loaded in memory.
     * 
     * @param jos The jar output stream to write to
     * @throw IOException Upon general I/O error
     * @throw JAXBException Upon error serializing to XML on disk
     */
    public void writeTo(JarOutputStream jos) throws IOException, JAXBException {
        /* We just need a read lock for this, since we aren't changing anything */
        this.getReadLock().lock();
        
        try {
            /* Fetch the root directory and write out the directory */
            WFSRootDirectory rootDir = this.getRootDirectory();
            String pathName = rootDir.getPathName();
            JarEntry je = new JarEntry(pathName + "/");
            jos.putNextEntry(je);

            /* Fetch the version class and write it out */
            WFSVersion version = rootDir.getVersion();
            if (version != null) {
                je = new JarEntry(pathName + "/" + WFSRootDirectory.VERSION);
                jos.putNextEntry(je);
                version.encode(new OutputStreamWriter(jos));
            }

            /*
             * Write out all of the individua cells in the root directory.
             * Recursively call to write out the rest of the WFS tree.
             */
            WFSCell cells[] = rootDir.getCells();
            for (WFSCell cell : cells) {
                this.writeCellTo(cell, jos);
            }

            /* Close the stream and return */
            jos.close();
        } finally {
            this.getReadLock().unlock();
        }
    }
    
    /**
     * Writes an entire WFS to an output stream, encoded as a jar file. The
     * side effect of this method is to load the entire WFS if it has not
     * already been loaded into memory.
     * 
     * @param os The output stream to write to
     * @throw IOException Upon general I/O error
     * @throw JAXBException Upon error serializing to XML on disk
     */
    public void writeTo(OutputStream os) throws IOException, JAXBException {
        this.writeTo(new JarOutputStream(os));
    }

    /**
     * Adds a listener to this WFS, for events such as cell attribute update,
     * cell children added, and cell children removed.
     * 
     * @param listener The new cell listener
     */
    public void addWFSListener(WFSListener listener) {
        this.eventListenerList.add(WFSListener.class, listener);
    }

    /**
     * Removes a listener from this WFS. If the listener does not exist, this
     * method does nothing
     * 
     * @param listener The cell listener to remove
     */
    public void removeWFSListener(WFSListener listener) {
        this.eventListenerList.remove(WFSListener.class, listener);
    }
    
    /**
     * Notify all of the registered listeners that a cell's attribute files
     * has been updated. This method notifies the listeners each in their
     * own thread.
     */
    public void fireCellAttributeUpdate(WFSCell cell) {
        /*
         * Fetch an array of pairs of { class type, listener object }, is never
         * null.
         */
        final Object[] listeners = this.eventListenerList.getListenerList();
        final WFSEvent event = new WFSEvent(cell);
        
        /*
         * Loop through each listener, from tail to head, create an event for
         * each and fire the event in its own thread.
         */
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == WFSListener.class) {
                /* Spawn a new thread for each of the listeners */
                final int j = i;
                (new Thread() {
                    @Override
                    public void run() {
                        ((WFSListener) listeners[j + 1]).cellAttributeUpdate(event);
                    }
                }).start();
            }
        }
    }
    
    /**
     * Notify all of the registered listeners that a cell has added children.
     * This method notifies the listeners each in their own thread.
     */
    public void fireCellChildrenAdded(WFSCell cell) {
        /*
         * Fetch an array of pairs of { class type, listener object }, is never
         * null.
         */
        final Object[] listeners = this.eventListenerList.getListenerList();
        final WFSEvent event = new WFSEvent(cell);
        
        /*
         * Loop through each listener, from tail to head, create an event for
         * each and fire the event in its own thread.
         */
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == WFSListener.class) {
                /* Spawn a new thread for each of the listeners */
                final int j = i;
                (new Thread() {
                    @Override
                    public void run() {
                        ((WFSListener) listeners[j + 1]).cellChildrenAdded(event);
                    }
                }).start();
            }
        }
    }
    
    /**
     * Notify all of the registered listeners that a cell has removed children.
     * This method notifies the listeners each in their own thread.
     */
    public void fireCellChildrenRemoved(WFSCell cell) {
        /*
         * Fetch an array of pairs of { class type, listener object }, is never
         * null.
         */
        final Object[] listeners = this.eventListenerList.getListenerList();
        final WFSEvent event = new WFSEvent(cell);
        
        /*
         * Loop through each listener, from tail to head, create an event for
         * each and fire the event in its own thread.
         */
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == WFSListener.class) {
                /* Spawn a new thread for each of the listeners */
                final int j = i;
                (new Thread() {
                    @Override
                    public void run() {
                        ((WFSListener) listeners[j + 1]).cellChildrenRemoved(event);
                    }
                }).start();
            }
        }
    }
    
    /**
     * Returns the read lock associated with this WFS. This method is not part
     * of the public API, it is meant only for the implementation.
     * 
     * @return The read lock for the WFS
     */
    protected Lock getReadLock() {
        return this.ownerLock.readLock();
    }
    
    /**
     * Takes the name of a WFS (with the '-wfs' suffix) and returns the name
     * of the WFS (without the '-wfs' suffix. If there is no '-wfs' suffix,
     * then this method returns the given name.
     * 
     * @param name The WFS name (with the '-wfs' suffix
     * @return The name of the WFS without the '-wfs' suffix
     */
    protected static String stripWfsSuffix(String name) {
        if (name.endsWith(WFS.WFS_DIRECTORY_SUFFIX) == true) {
            return name.substring(0, name.length() - WFS.WFS_DIRECTORY_SUFFIX.length() + 1);
        }
        return name;
    }
    
    /**
     * Takes a URL-like string of the location of the WFS and strips off
     * everything except the final name part (everything after the final "/").
     * If the given URL string does not have any "/", this method returns the
     * given URL. The name must be the last part of the string.
     * 
     * @param url The URL string of the WFS
     * @return The name of the wfs (including the '-wfx' suffix*
     */
    protected static String stripWfsName(String url) {
        int index = url.lastIndexOf("/");
        if (index == -1) {
            return url;
        }
        return url.substring(index + 1);
    }
    
    /**
     * Takes a WFSCell class and writes its contents to the given jar output
     * stream, and recursively calls to write out any child directory it may
     * have.
     */
    private void writeCellTo(WFSCell cell, JarOutputStream jos) throws IOException {
        /* First fetch the cell setup data, this may result in an read exception */
        String cellSetup = cell.getCellSetup(); 

        /* If there is a cell class, then write it out to the stream */
        if (cellSetup != null) {
            /*
             * Write the entry out to the output stream
             */
            JarEntry je = new JarEntry(cell.getPathName());
            jos.putNextEntry(je);
            jos.write(cellSetup.getBytes());

            /*
             * Recursively write out any children
             */
            if (cell.getCellDirectory() != null) {
                this.writeDirectoryTo(cell.getCellDirectory(), jos);
            }          
        }
    }
    
    /**
     * Takes a WFSCellDirectory class and writes its contents to the given
     * jar output stream. It recursively calls itself until the entire WFS
     * tree has been written.
     */
    private void writeDirectoryTo(WFSCellDirectory directory, JarOutputStream jos) throws IOException {
        /* First create the directory */
        JarEntry je = new JarEntry(directory.getPathName() + "/");
        jos.putNextEntry(je);
        
        /*
         * Write all of the individual cells in the directory out.
         * Recursively call to write out the rest of the WFS tree.
         */
        WFSCell cells[] = directory.getCells();
        for (WFSCell cell : cells) {
            this.writeCellTo(cell, jos);
        }
    }
}
