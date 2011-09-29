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
import java.io.IOException;
import org.jdesktop.wonderland.tools.wfs.WFS;
import org.jdesktop.wonderland.tools.wfs.WFSFactory;

/**
 * @author Jordan Slott <jslott@dev.java.net>
 * @author kaplanj
 */
public class WFSRoot {
    /** the worlds directory relative to the wfs base directory */
    public static final String WORLDS_DIR = "worlds";

    /** the wfs directory, a child of the worlds directory */
    private File directory;

    /** the WFS contained in the directory */
    private WFS wfs;

    protected WFSRoot() {
        // use getInstance() instead
    }

    /**
     * Get the name of this root
     * @return the name
     */
    public String getName() {
        return directory.getName();
    }

    /**
     * Get the directory this root represents
     * @return the root directory
     */
    public File getDirectory() {
        return directory;
    }
 
    /**
     * Set the directory for this root.  Only done by getInstance()
     */
    protected void setDirectory(File directory) {
        this.directory = directory;
    }
    
    /**
     * Get the full path to this root from the wfs directory
     * @return the root path
     */
    public String getRootPath() {
        return WORLDS_DIR + "/" + getName();
    }
    
    /**
     * Get the WFS associated with this snapshot
     * @return the WFS object associated with this snapshot
     */
    public WFS getWfs() {
        return wfs;
    }
   
    /**
     * Set the WFS associated with this snapshot.  Only done at restore time.
     * @param dir the base directory to read the WFS from
     */
    protected void setWfs(File wfsDir) throws IOException {
        // decode the WFS if it exists
        try {
            if (wfsDir.exists()) {
                wfs = WFSFactory.open(wfsDir.toURI().toURL());
            } else {
                wfs = WFSFactory.create(wfsDir.toURI().toURL());
            }
        } catch (Exception ex) {
            IOException ioe = new IOException("Error reading WFS from " +
                                              wfsDir);
            ioe.initCause(ex);
            throw ioe;
        }
    }
    
    /**
     * Get an instance of WFSRoot for a given directory
     * @param dir the directory to get a WFS root for
     * @throws IOException if there is an error getting the directory
     */
    public static WFSRoot getInstance(File dir) throws IOException {
        WFSRoot out = new WFSRoot();
        out.setDirectory(dir);
        out.setWfs(dir);
        
        return out;
    }
}
