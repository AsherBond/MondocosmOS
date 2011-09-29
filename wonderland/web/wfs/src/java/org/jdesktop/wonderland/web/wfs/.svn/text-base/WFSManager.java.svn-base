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
package org.jdesktop.wonderland.web.wfs;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.wonderland.common.wfs.WorldRoot;
import org.jdesktop.wonderland.utils.SystemPropertyUtil;
import org.jdesktop.wonderland.tools.wfs.WFS;
import org.jdesktop.wonderland.utils.RunUtil;

/**
 *TBD
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 * @author Bernard Horan
 */
public class WFSManager {

    /* The File objects to hold the root of the wfs, recording and recording directories */
    private File wfsFile = null;
    private File recordingFile = null;
    private File snapshotFile = null;
     
    /* The property storing the base directory for all wfs information */
    private static final String WFS_ROOT_PROPERTY = "wonderland.webserver.wfs.root";
    
    /* A map of WFS worlds (given by name and root object) */
    private Map<String, WFSRoot> wfsRoots = new LinkedHashMap();

    /* A map of all snapshots of worlds (given by name and recording object) */
    // NEED TO MAKE MT SAFE XXXX
    private Map<String, WFSSnapshot> wfsSnapshots = new LinkedHashMap();

    /* A map of all recordings of worlds (given by name and recording object) */
    // NEED TO MAKE MT SAFE XXXX
    private Map<String, WFSRecording> wfsRecordings = new LinkedHashMap();
    
    /*
     * A map of all wfs, worlds, recordings and snapshots of worlds, where the key
     * is the root path. These are of the form "worlds/default-wfs", "recordings/<date>/world-wfs" and
     * "snapshots/<date>/world-wfs". Note the "-wfs" suffix is present in these
     * names.
     */
    // NEED TO MAKE MT SAFE XXX
    private Map<String, WFS> wfsMap = new HashMap();

    /* The logger for the module manager */
    private static final Logger logger = Logger.getLogger(WFSManager.class.getName());

    /* Filter for hidden files */
    private static final FileFilter HIDDEN_FILE_FILTER = new FileFilter() {
            //We don't want invisible directories such as .DS_Store on the Mac
            public boolean accept(File aFile) {
                return !aFile.isHidden();
            }
        };

    /** Constructor */
    private WFSManager() {
        /* Load in all of the Wonderland file systems */
        this.createDirectories();
        this.loadWFSs();
        this.loadSnapshots();
        this.loadRecordings();
    }
    
    /**
     * Singleton to hold instance of ModuleManager. This holder class is loaded
     * on the first execution of ModuleManager.getModuleManager().
     */
    private static class WFSManagerHolder {
        private final static WFSManager wfsManager = new WFSManager();
    }
    
    /**
     * Returns a single instance of this class
     * <p>
     * @return Single instance of this class.
     */
    public static final WFSManager getWFSManager() {
        return WFSManagerHolder.wfsManager;
    }
    
    /**
     * Returns the error logger associated with this class.
     * 
     * @return The error logger
     */
    public static Logger getLogger() {
        return WFSManager.logger;
    }
    
    /**
     * Gets the WFS for the given name (without the '-wfs' extension) or null
     * if it does not exist.
     * 
     * @param rootPath the path to the root of this WFS
     * @return The corresponding WFS
     */
    public WFS getWFS(String rootPath) {
        loadWFSs();
        loadSnapshots();
        loadRecordings();
        return wfsMap.get(rootPath);
    }
    
    /**
     * Returns the WFS object representing the given world root path. Returns
     * null if it does not exist. Examples of world root paths include
     * "worlds/default-wfs" and "snapshots/<date>/world-wfs".
     * 
     * @param worldRoot The path to the wfs world
     * @return the WFS object
     */
    public WFS getWFS(WorldRoot worldRoot) {
        return getWFS(worldRoot.getRootPath());
    }
    
    /**
     * Returns an array of WFS root names. If there are no roots, returns null.
     * 
     * @return An array of WFS root names, or null if there are none
     */
    public List<WFSRoot> getWFSRoots() {
        loadWFSs();
        return new ArrayList<WFSRoot>(wfsRoots.values());
    }

    /**
     * Get a particular wfs root by name
     * @param name the name of the root
     * @return a root with the given name, or null if no root exists
     * with the given name
     */
    public WFSRoot getWFSRoot(String name) {
        loadWFSs();
        return wfsRoots.get(name);
    }

    /**
     * Get all WFS snapshots
     * @return a list of all WFS snapshots
     */
    public List<WFSSnapshot> getWFSSnapshots() {
        loadSnapshots();
        return new ArrayList<WFSSnapshot>(wfsSnapshots.values());
    }

    /**
     * Get all WFS recordings
     * @return a list of all WFS recordings
     */
    public List<WFSRecording> getWFSRecordings() {
        loadRecordings();
        return new ArrayList<WFSRecording>(wfsRecordings.values());
    }

    /**
     * Get a particular WFS snapshot by name
     * @param name the name of the snapshot
     * @return a snapshot with the given name, or null if no snapshot exists
     * with the given name
     */
    public WFSSnapshot getWFSSnapshot(String name) {
        loadSnapshots();
        return wfsSnapshots.get(name);
    }

    /**
     * Get a particular WFS recording by name
     * @param name the name of the recording
     * @return a recording with the given name, or null if no recording exists
     * with the given name
     */
    public WFSRecording getWFSRecording(String name) {
        WFSRecording recording = wfsRecordings.get(name);
        if (recording != null) {
            return recording;
        }
        //If it's not already loaded, try loading all the recordings from scratch
        loadRecordings();
        return wfsRecordings.get(name);
    }

    /**
     * Create a new snapshot wfs given its date. This method assumes the snapshot
     * does not already exist
     * @param name the name of the snapshot
     */
    public WFSSnapshot createWFSSnapshot(String name) {
        File snapshotDir = new File(snapshotFile, name);

        try {
            WFSSnapshot snapshot = WFSSnapshot.getInstance(snapshotDir);
            if (snapshot.getTimestamp() != null) {
                // uh-oh, snapshot already exists...
                logger.log(Level.WARNING, "[WFS] Snapshot " + name + " exists");
            } else {
                // set the timestamp to now
                snapshot.setTimestamp(new Date());

                // update our internal records
                wfsSnapshots.put(snapshot.getName(), snapshot);
                wfsMap.put(snapshot.getRootPath(), snapshot.getWfs());
            }

            return snapshot;
        } catch (java.lang.Exception excp) {
            logger.log(Level.WARNING, "[WFS] Unable to create snapshot", excp);
            return null;
        }
    }

    /**
     * Create a new recording wfs given its name. This method removes the recording
     * if it already exists
     * @param name the name of the recording
     */
    public WFSRecording createWFSRecording(String name) {
        File recordingDir = new File(recordingFile, name);

        try {
            WFSRecording recording = WFSRecording.getInstance(recordingDir);
            if (recording.getTimestamp() != null) {
                // uh-oh, recording already exists...
                logger.log(Level.WARNING, "[WFS] Recording " + name + " exists, removing");
                // remove the recording
                removeWFSRecording(name);
                // recurse
                return createWFSRecording(name);
            } else {
                // set the timestamp to now
                recording.setTimestamp(new Date());
                recording.setDescription("WFS Recording named: " + name);

                // update our internal records
                wfsRecordings.put(recording.getName(), recording);
                wfsMap.put(recording.getRootPath(), recording.getWfs());
            }

            return recording;
        } catch (java.lang.Exception excp) {
            logger.log(Level.WARNING, "[WFS] Unable to create recording", excp);
            return null;
        }
    }

    /**
     * Remove a recording from WFS
     * @param name the name of the recording to remove
     */
    public void removeWFSRecording(String name) {
        WFSRecording recording = wfsRecordings.remove(name);
        if (recording == null) {
            return;
        }

        wfsMap.remove(recording.getRootPath());

        File recordingDir = new File(recordingFile, name);
        if (recordingDir.exists()) {
            RunUtil.deleteDir(recordingDir);
        }
    }

    /**
     * Remove a snapshot from WFS
     * @param name the name of the snapshot to remove
     */
    public void removeWFSSnapshot(String name) {
        WFSSnapshot snapshot = wfsSnapshots.remove(name);
        if (snapshot == null) {
            return;
        }

        wfsMap.remove(snapshot.getRootPath());

        File snapshotDir = new File(snapshotFile, name);
        if (snapshotDir.exists()) {
            RunUtil.deleteDir(snapshotDir);
        }
    }

    /**
     * Rename a snapshot from one name to another
     * @param oldname the original name
     * @param newname the new name
     */
    void renameSnapshot(String oldname, String oldpath, WFSSnapshot snapshot) {
        // remove information about the old snapshot
        wfsSnapshots.remove(oldname);
        wfsMap.remove(oldpath);

        // add the new information
        wfsSnapshots.put(snapshot.getName(), snapshot);
        wfsMap.put(snapshot.getRootPath(), snapshot.getWfs());
    }

    /**
     * Rename a recording from one name to another
     * @param oldname the original name
     * @param newname the new name
     */
    void renameRecording(String oldname, String oldpath, WFSRecording recording) {
        // remove information about the old recording
        wfsRecordings.remove(oldname);
        wfsMap.remove(oldpath);

        // add the new information
        wfsRecordings.put(recording.getName(), recording);
        wfsMap.put(recording.getRootPath(), recording.getWfs());
    }

    /**
     * Loads in all of the Wonderland file systems specified in the roots. Adds
     * to the internal list.
     */
    private void loadWFSs() {
        // clear out the existing list -- make sure to clean up both
        // wfsRoots and the wfsMap.  Use an iterator here to avoid concurrent
        // modification problems
        for (Iterator<WFSRoot> i = wfsRoots.values().iterator(); i.hasNext();) {
            WFSRoot root = i.next();
            i.remove();
            wfsMap.remove(root.getRootPath());
        }
        
        for (File rootDir : this.getWFSRootDirectories()) {
            try {
                WFSRoot root = WFSRoot.getInstance(rootDir);

                wfsRoots.put(root.getName(), root);
                wfsMap.put(root.getRootPath(), root.getWfs());
            } catch (java.lang.Exception excp) {
                logger.log(Level.WARNING, "[WFS] Unable to create WFS", excp);
            }
        }   
    }
    
    /**
     * Loads in all of the snapshots of wfs. Adds to the internal list
     */
    private void loadSnapshots() {
        // clear out the existing list -- make sure to clean up both
        // wfsSnapshotss and the wfsMap.  Use an iterator here to avoid
        // concurrent modification problems
        for (Iterator<WFSSnapshot> i = wfsSnapshots.values().iterator();
             i.hasNext();)
        {
            WFSSnapshot snapshot = i.next();
            i.remove();
            wfsMap.remove(snapshot.getRootPath());
        }

        for (File root : this.getSnapshotDirectories()) {
            try {
                WFSSnapshot snapshot = WFSSnapshot.getInstance(root);
                
                wfsSnapshots.put(snapshot.getName(), snapshot);
                wfsMap.put(snapshot.getRootPath(), snapshot.getWfs());
            } catch (java.lang.Exception excp) {
                logger.log(Level.WARNING, "[WFS] Unable to create WFS", excp);
            }
        }
    }

    /**
     * Loads in all of the recordings of wfs. Adds to the internal list
     */
    private void loadRecordings() {
        // clear out the existing list -- make sure to clean up both
        // wfsRecordings and the wfsMap.  Use an iterator here to avoid
        // concurrent modification problems
        for (Iterator<WFSRecording> i = wfsRecordings.values().iterator();
             i.hasNext();)
        {
            WFSRecording recording = i.next();
            i.remove();
            wfsMap.remove(recording.getRootPath());
        }

        for (File root : this.getRecordingDirectories()) {
            try {
                WFSRecording recording = WFSRecording.getInstance(root);

                wfsRecordings.put(recording.getName(), recording);
                wfsMap.put(recording.getRootPath(), recording.getWfs());
            } catch (java.lang.Exception excp) {
                logger.log(Level.WARNING, "[WFS] Unable to create WFS", excp);
            }
        }
    }
    
    /**
     * Returns an array of URL object that represent the base directories for
     * each root WFS in the system.
     */
    private File[] getWFSRootDirectories() {
        /*
         * Search through the directory and get all of the files with a -wfs
         * extension. We check whether the directory is readable and whether
         * it is a valid WFS in the loadWFSs() method.
         */
        File[] res = wfsFile.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(WFS.WFS_DIRECTORY_SUFFIX);
            }
        });
        
        // return an empty array if the directory doesn't exist, otherwise
        // we get NullPointerExceptions
        if (res == null) {
            res = new File[0];
        }
        
        return res;
    }
 
    /**
     * Returns an array of file names that represent the base directories for
     * each wfs in the snapshots directory
     */
    private File[] getSnapshotDirectories() {
        // return an empty array if the directory doesn't exist, otherwise
        // we get NullPointerExceptions
        File[] res = snapshotFile.listFiles(HIDDEN_FILE_FILTER);
        if (res == null) {
            res = new File[0];
        }
        
        return res;
    }

    /**
     * Returns an array of file names that represent the base directories for
     * each wfs in the recordings directory
     */
    private File[] getRecordingDirectories() {
        // return an empty array if the directory doesn't exist, otherwise
        // we get NullPointerExceptions
        File[] res = recordingFile.listFiles(HIDDEN_FILE_FILTER);
        if (res == null) {
            res = new File[0];
        }

        return res;
    }
    
    /**
     * Creates all of the important WFS directories, if they do not already
     * exists. Open File objects for each for later use.
     */
    private void createDirectories() {
        /* Fetch the WFS base-level directory from the property */
        String baseDir = null;
        if ((baseDir = getBaseWFSDirectory()) == null) {
            logger.severe("[WFS] Invalid WFS Base Directory! Will not load WFS");
            logger.severe("[WFS] Make sure " + WFS_ROOT_PROPERTY + " property is set");
            return;
        }
        
        makeDirectory(baseDir);
        this.wfsFile = makeDirectory(baseDir + File.separator + WFSRoot.WORLDS_DIR);
        this.snapshotFile = makeDirectory(baseDir + File.separator + WFSSnapshot.SNAPSHOTS_DIR);
        this.recordingFile = makeDirectory(baseDir + File.separator + WFSRecording.RECORDINGS_DIR);
    }
    
    /**
     * Makes a directory if it does not exist and returns the file object of
     * the directory. Returns null upon error creating the directory. 
     */
    private File makeDirectory(String path) {
        try {
            File file = new File(path);
            if (file.exists() == true) {
                return file;
            }
            else if (file.mkdirs() == true) {
                return file;
            }
        } catch (java.lang.Exception excp) {
            logger.log(Level.SEVERE, "[WFS] Failed to create directory " + path, excp);
            return null;
        }
        
        logger.severe("[WFS] Failed to create directory " + path);
        logger.severe("[WFS] Make sure " + WFS_ROOT_PROPERTY + " points to a valid location");
        return null;
    }
    
    /**
     * Returns the base directory for all WFS information, or null if the
     * property is not set.
     */
    public static String getBaseWFSDirectory() {
        return SystemPropertyUtil.getProperty(WFS_ROOT_PROPERTY);
    }
}
