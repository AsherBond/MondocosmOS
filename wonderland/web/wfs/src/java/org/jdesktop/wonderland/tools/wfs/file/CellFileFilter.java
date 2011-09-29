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
import java.io.FileFilter;
import org.jdesktop.wonderland.tools.wfs.WFS;

/**
 * The CellFileFilter filters out all entries in a directory unless they are
 * plain files and they conform to the Wonderland cell file naming convention.
 * The naming convention is currently '<name>-wlc.xml'.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class CellFileFilter implements FileFilter {
    
    /** Constructor */
    public CellFileFilter() {
    }
    
    /**
     * Tests whether or not the specified abstract pathname should be included in
     * a pathname list.
     */
    public boolean accept(File file) {
        return file.isFile() == true && file.getName().endsWith(WFS.CELL_FILE_SUFFIX) == true;
    }
}
