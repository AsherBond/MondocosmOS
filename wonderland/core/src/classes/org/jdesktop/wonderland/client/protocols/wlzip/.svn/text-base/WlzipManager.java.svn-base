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
package org.jdesktop.wonderland.client.protocols.wlzip;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author paulby
 */
public class WlzipManager {

    private static final Logger logger = Logger.getLogger(WlzipManager.class.getName());
    
    private static WlzipManager zipManager = null;
    private HashMap<String, ZipFile> zips = new HashMap();
    private int id = 0;
 
    public static WlzipManager getWlzipManager() {
        if (zipManager==null)
            zipManager = new WlzipManager();
        
        return zipManager;
    }
    
    /**
     * Add the zipfile to the set of managed zipfiles. Returning a unique
     * string which can be used as the host portion of the URL referring to
     * this zip
     * 
     * @param zipFile
     * @return
     */
    public String addZip(ZipFile zipFile) {
        String ret = extractName(zipFile.getName());
        
        if (zips.containsKey(ret)) {
            ret = ret.concat(Integer.toString(id++));
        }
        
        zips.put(ret, zipFile);
        
        return ret;
    }
    
    /**
     * Remove the zipFile from the set of managed files
     * @param zipFile
     */
    public void removeZip(String urlHost, ZipFile zipFile) {
        ZipFile removed = zips.remove(urlHost);
        if (removed!=null && removed!=zipFile)
            logger.severe("Removed wrong zipfile");
    }
    
    InputStream getInputStream(String urlHost, String path) throws IOException {
        ZipFile zipFile = zips.get(urlHost);
        if (zipFile==null) {
            logger.severe("Unable to find zip "+urlHost);
            return null;
        }
        
        // Trim the leading / from the path
        ZipEntry entry = zipFile.getEntry(path.substring(1));
        if (entry==null) {
            logger.warning("Unable to find texture "+path);
            return null;
        }
        return zipFile.getInputStream(entry);
    }
    
    /**
     * Return the name from the full path
     * @param path
     * @return
     */
    private String extractName(String path) {
        return path.substring(path.lastIndexOf(File.separatorChar)+1);
    }
}
