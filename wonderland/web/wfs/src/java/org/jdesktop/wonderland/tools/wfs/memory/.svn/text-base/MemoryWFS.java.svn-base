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
package org.jdesktop.wonderland.tools.wfs.memory;

import org.jdesktop.wonderland.tools.wfs.WFS;
import org.jdesktop.wonderland.tools.wfs.WFSRootDirectory;
import org.jdesktop.wonderland.tools.wfs.delegate.DirectoryDelegate;

/**
 * The MemoryWFS class extends the WFS abstract class and represents a Wonderland
 * File System that resides entire in memory.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class MemoryWFS extends WFS {
    
    /**
     * Creates a new instance of WFS.
     * 
     * @param name The name of the WFS (without any '-wfs' suffix)
     */
    public MemoryWFS(String name) {
        super(name);
        
        /* Create the proper delegate for the root directory */
        DirectoryDelegate delegate = new MemoryDirectoryDelegate();
        this.directory = new WFSRootDirectory(this, delegate);
    }
        
    /**
     * Writes the entire WFS to the underlying medium, including the meta-
     * information contains within the root directory, the cells containing
     * within the root directory, and any child directories.
     */
    @Override
    public void write() {
        /* Not supported, since nothing to write to! */
        throw new UnsupportedOperationException("Not supported. Please use WFS.writeTo()");
    }
}
