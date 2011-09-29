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
package org.jdesktop.wonderland.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author paulby
 */
public class FileUtils {

    /**
     * Returns the extension of the filename, or null if there is no extension
     * @param filename
     * @return
     */
    public static String getFileExtension(String filename) {
        try {
            return filename.substring(filename.lastIndexOf('.')+1);        
        } catch(Exception ex) {
            return null;
        }        
    }
    
    /**
     * Copy the file from in to out
     * @param in
     * @param out
     * @throws java.io.IOException
     */
    public static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[1024*1024];

        int size;
        while ((size = in.read(buf))!=-1) {
            out.write(buf, 0, size);
        }

    }

    /**
     * Replace the extension of the filename with newExtension and return
     * @param filename
     * @param oldExtension
     * @param newExtension
     * @return
     */
    // TODO Untested
//    public static String replaceFileExtension(String filename, String oldExtension, String newExtension) {
//        if (!filename.endsWith(oldExtension))
//            throw new RuntimeException("Extension does not match");
//
//        String ret = filename.substring(0, filename.length()-oldExtension.length());
//        ret += newExtension;
//
//        return ret;
//    }

    /**
     * Recursively delete the content of the supplied directory
     * @param dir
     */
    public static void deleteDirContents(File dir) {
        for(File content : dir.listFiles()) {
            if (content.isDirectory())
                deleteDirContents(content);
            content.delete();
        }
    }


    /**
     * Traverse from start, looking for a directory named string
     * 
     * @param start
     * @param string
     * @return
     */
    public static File findDir(File start, String string) {
        if (start.getName().equals(string)) {
            return start;
        }

        if (start.isDirectory()) {
            File[] subDirs = start.listFiles();
            if (subDirs==null)
                return null;

            for(File f : subDirs) {
                File result = findDir(f, string);
                if (result!=null && result.isDirectory())
                    return result;
            }
        }

        return null;
    }
}
