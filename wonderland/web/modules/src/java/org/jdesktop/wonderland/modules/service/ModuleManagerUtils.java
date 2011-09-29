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
package org.jdesktop.wonderland.modules.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

/**
 * The ModuleManagerUtils class contains a collection of static utility methods
 * to help module management.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ModuleManagerUtils {

    /**
     * Creates a directory, and any necessary parent directories, if it does
     * not exist. Throws IOException upon error.
     * 
     * @param root The directory to create
     */
    public static void makeDirectory(File root) throws IOException {
        try {
            /* Create the directory, if false, throw IOException */
            if (root.exists() == false) {
                if (root.mkdirs() == false) {
                    throw new IOException("Failed to create " + root.getAbsolutePath());
                }
            }
        } catch (java.lang.SecurityException excp) {
            throw new IOException(excp.toString());
        }
    }
    
    /**
     * Creates a directory if it does not exist. If it does exist, then remove
     * any exist directory contents. Returns true upon success, false upon
     * failure.
     * 
     * @param root The directory to create
     */
    public static boolean makeCleanDirectory(File root) {
        Logger logger = ModuleManager.getLogger();
        if (root.exists() == true) {
            /* Log an info message, and try to clean the existing directory */
            try {
                FileUtils.cleanDirectory(root);
                return true;
            } catch (java.io.IOException excp) {
                /* If we cannot delete the existing directory, this is fatal */
                logger.warning("[MODULES] MAKE CLEAN Failed " +  excp.toString());
                return false;
            }
        }
        
        /* Now go ahead and recreate the directory */
        try {
            root.mkdir();
        } catch (java.lang.SecurityException excp) {
            logger.warning("[MODULES] MAKE CLEAN Failed " + excp.toString());
            return false;
        }
        return true;
    }
    
    /* The chunk size to write out while expanding archive files to disk */
    private static final int CHUNK_SIZE = (8 * 1024);
    
    /**
     * Expands a jar file into a given directory, given the jar file and the
     * directory into which the contents should be written.
     * 
     * @throw IOException Upon error expanding the Jar file
     */
    public static void expandJar(JarFile jarFile, File root) throws IOException {
        /*
         * Loop through each entry, fetchs its input stream, and write to an
         * output stream for the file.
         */
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements() == true) {
            JarEntry entry = entries.nextElement();
            InputStream is = jarFile.getInputStream(entry);
            String entryName = entry.getName();
            
            /* Don't expand anything that beings with META-INF */
            if (entryName.startsWith("META-INF") == true) {
                continue;
            }
            
            /* Ignore if it is a directory, then create it */
            if (entryName.endsWith("/") == true) {
                File file = new File(root, entryName);
                file.mkdirs();
                continue;
            }
            
            /* Write out to a file in 'root' */
            File file = new File(root, entryName);
            FileOutputStream os = new FileOutputStream(file);
            byte[] b = new byte[CHUNK_SIZE];
            while (true) {
                int len = is.read(b);
                if (len == -1) {
                    break;
                }
                os.write(b, 0, len);
            }
            is.close();
            os.close();
        }
    }
}
