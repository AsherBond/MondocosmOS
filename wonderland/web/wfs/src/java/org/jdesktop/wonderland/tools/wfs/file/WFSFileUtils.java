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

/**
 * Utilities for manipulating disk files.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class WFSFileUtils {
    /**
     * Deletes all files and subdirectories under the given directory. If a
     * deletion fails, the method stops attempting to delete and returns false.
     * <p>
     * This method was taken from the Java Developers Alamanac (e.30).
     * 
     * @param directory The directory to delete
     * @return True upon success, false upon failure
     */
    public static boolean deleteDirectory(File directory) {
        if (directory.isDirectory() == true) {
            String[] children = directory.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDirectory(new File(directory, children[i]));
                if (success == false) {
                    return false;
                }
            }
        }
    
        /* The directory is now empty so delete it */
        return directory.delete();
    }
}
