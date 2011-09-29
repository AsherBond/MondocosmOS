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
package org.jdesktop.wonderland.client.jme.artimport;

import org.jdesktop.wonderland.client.content.spi.ContentImporterSPI;

/**
 * Provides a container for a model loader, allowing multiple loaders that
 * support the same file extensions to be available in the system.
 * 
 * @author paulby
 */
public abstract class ModelLoaderFactory { //implements ContentImporterSPI {

    private boolean enabled;
    
    /**
     * @return the file extensions supported by the loader
     */
    public abstract String getFileExtension();
    
    /**
     * Return an instance of the loader. A new instance of the loader is used
     * to load each new file
     * 
     * @return
     */
    public abstract ModelLoader getLoader();
    
    public abstract String getLoaderClassname();

    /**
     * 
     * @return true if this loader is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Set whether or not this loader is enabled.
     * @param b
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
