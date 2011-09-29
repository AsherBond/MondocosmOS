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
package org.jdesktop.wonderland.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;

/**
 * The ArchiveManifest class represents a tree of entries found in the JAR
 * file, so that its entries may be tranversed hierarchically.
 * <p>
 * This class also serves as the 'front-end' to a JAR file and returns the
 * input stream for individual entries.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ArchiveManifest {
    /* The error logger for this class */
    Logger logger = Logger.getLogger(ArchiveManifest.class.getName());
    
    /* The URL used to open the archive */
    private URL url = null;
    
    /*
     * A map that contains a series of file entries parsed into path components.
     * Each of the entries contains a String key of a directory path component,
     * and a linked-list of entries (files or directories) in that path.
     */
    private HashMap<String, LinkedList<String>> fileMap = null;
    
    /* An ordered linked list of all entries in the archive. */
    private LinkedList<String> entryList = null;
   
    /* The JAR file object association with this JAR file */
    private JarFile jarfile;
    
    /**
     * Creates a new instance of ArchiveManifest, takes the JAR file as an
     * argument, parses the manifest into a tree structure.
     * 
     * @param file The File of the JAR file
     * @throw IOException Upon failure to open the JAR file
     */
    public ArchiveManifest(File file) throws IOException {
        this.url = file.toURI().toURL();
        this.jarfile = new JarFile(file);
        this.createArchiveMap(jarfile);
    }
    
    /**
     * Creates a new instance of ArchiveManifest, takes the JAR URL as an
     * argument, parses the manifest into a tree structure.
     *
     * @param url The URL of the JAR file
     * @throw IOException Upon failure to open the JAR file URL
     */
    public ArchiveManifest(URL url) throws IOException {
        /* Open a connection to the JAR file and parse out its entries */
        JarURLConnection urlconn = (JarURLConnection) url.openConnection();
        this.url = url;
        this.jarfile = urlconn.getJarFile();
        this.createArchiveMap(this.jarfile);
    }
    
    /**
     * Returns the URL used to open the archive.
     * 
     * @return The archive URL
     */
    public URL getURL() {
        return this.url;
    }
    
    /**
     * Returns an input stream given the full pathame of the entry.
     *
     * @path The full pathname of the entry
     * @return The input stream of the entry
     * @throw IOException Upon an invalid entry name
     */
    public InputStream getEntryInputStream(String path) throws IOException {
        ZipEntry entry = this.jarfile.getEntry(path);
        if (entry == null) {
            return null;
        }
        InputStream is = jarfile.getInputStream(entry);
        return is;
    }
    
    /**
     * Returns a JarEntry class given the full pathame of the entry or null
     * if not found
     *
     * @param path The full pathame of the entry
     * @return The jar file entry, or null if not found
     */
    public JarEntry getJarEntry(String path) {
        return this.jarfile.getJarEntry(path);
    }
    
    /**
     * Closes any files associated with the manifest
     */
    public void close() {
        try {
            this.jarfile.close();
        } catch (IOException excp) {
            logger.info("Failed to close JAR file: " + excp.toString());
        }
    }
    
    /**
     * Returns a copy of all of the entries given the full path name of a
     * directory. For the entries at the root, use "" for path.
     * 
     * @param path The path name to look for entries
     * @return A linked list of entries in the given path
     */
    public LinkedList<String> getEntries(String path) {
        if (this.fileMap.containsKey(path) == true) {
            return this.fileMap.get(path);
        }
        return new LinkedList<String>();
    }
    
    /**
     * Returns a linked list of all entries in the archive. Note that entries
     * that represent directories have a final "/".
     * 
     * @return A linked list of all entries in the archive
     */
    public LinkedList<String> getEntries() {
        return new LinkedList<String>(this.entryList);
    }
    
    /**
     * Returns true if the given entry name represents a directory (that is,
     * is has a final "/".
     * 
     * @param entry The name of the entry
     * @return True if the entry is a directory, false if not
     */
    public boolean isDirectory(String entry) {
        return entry.endsWith("/");
    }
    
    /**
     * Returns true if the given complete path name exists within the JAR file,
     * false if not.
     *
     * @param path The full pathname of the entry
     * @return True if the entry exists, false if not
     */
    public boolean isValidEntry(String path) {
        return this.fileMap.containsKey(path);
    }
    
    /**
     * Takes a string of a pathname and splits it into a file name part and
     * a path part. The file name part is the part of the path following the
     * final forward slash. The path part is the part preceeding the final
     * forward slash. If there is no forward slash, the path part in "" and
     * the file name part is the contents of the string. Returns an array of
     * length two: the first element is the path part, and the second part is
     * the file name part.
     *
     * @param entry The pathname entry in the JAR file
     * @return An array of strings representing each file component
     */
    private String[] splitEntry(String entry) {
        /* Strip off the last '/' if one exists */
        if (entry.endsWith("/") == true) {
            entry = entry.substring(0, entry.length() - 1);
        }
        
        /*
         * Create an array of length two, and initialize for the case where
         * there is no forward slash present in the string.
         */
        String[] result = new String[2];
        int index = entry.lastIndexOf("/");
        result[0] = "";
        result[1] = entry;

        /* In the case where there are no more characters after the '/' */
        if (index == entry.length() - 1) {
            result[0] = entry.substring(0, index);
            result[1] = "";
        }
        else if (index != -1) {
            /* In the case where index != -1, set the path and file name parts */
            result[0] = entry.substring(0, index);
            result[1] = entry.substring(index + 1);
        }
        
        /* Else when there is no '/' in the string */
        return result;
    }
    
    /**
     * Takes a JAR file and creates a map, where each key in the map is a path
     * component, and its value is are either cell names or directories of
     * children of cells. The directory is structure is stored as follows:
     * <p>
     * 1. All directory entries do not have any trailing '/'.
     * 2. The top-level directories are stored under an empty string entry.
     * <p>
     * For example, if the following hierarchy exists in the JAR file:
     * world-wfs/
     * world-wfs/cell-wlc.xml
     * world-wfs/cell-wld/
     * world-wfs/cell-wld/a-wlc.xml
     * world-wfs/cell-wld/b-wlc.xml
     * <p>
     * then the map will contain the following entries:
     * "" : world-wfs
     * world-wfs : cell-wlc.xml, cell-wld
     * world-wfs/cell-wld : a-wlc.xml, b-wlc.xml
     * <p>
     * @param jarfile The JAR file
     */
    private void createArchiveMap(JarFile jarfile) {
        /* Initialize the tree map */
        this.fileMap = new HashMap<String, LinkedList<String>>();
        this.entryList = new LinkedList<String>();
        
        /*
         * Fetch the entries from the manifest file. Iterate over each of the
         * entries and parse the name into the path and file name components.
         * Add an entry into the map structure.
         */
        Enumeration<JarEntry> entries = jarfile.entries();
        while (entries.hasMoreElements() == true) {
            /* Fetch the next entry */
            JarEntry entry = entries.nextElement();
            
            /* Split the path name into a path and file name part */
            String entryName = entry.getName();
            String paths[] = this.splitEntry(entryName);
            
            /* Add the entry to the map, if it does not already exist */
            LinkedList<String> fnames = this.fileMap.get(paths[0]);
            if (fnames == null) {
                fnames = new LinkedList<String>();
                this.fileMap.put(paths[0], fnames);
            }
            
            /* Add the entry in the map so long as it is not empty */
            if (paths[1].equals("") == false) {
                fnames.add(paths[1]);
            }
            
            /* Always add the entry to the ordered list of entries */
            this.entryList.addLast(entryName);
        }
    }
}
