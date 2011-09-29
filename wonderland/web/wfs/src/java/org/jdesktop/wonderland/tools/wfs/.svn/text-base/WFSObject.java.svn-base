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
package org.jdesktop.wonderland.tools.wfs;

/**
 * The WFSObject class is the base class for all WFS-related component classes:
 * WFSCell and WFSCellDirectory.
 * <p>
 * This object stores whether the WFS component is "dirty" -- that is, if the
 * WFS on the underlying medium has changed and someone has issued a "reload"
 * instruction. When an object is dirty, the next time it is accessed, it is
 * re-read from the underlying medium.
 * <p>
 * This object stores whether the WFS component is "invalid" -- that is, if it
 * no longer exists in the file system. For example, a WFSCell object is no
 * longer valid if it has been removed from the file system. A WFSCellDirectory
 * object is no longer valid if it has been removed from the file system too.
 * All objects begin as "valid". The "invalid" state is the final state of the
 * object.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
public abstract class WFSObject {
    /* True if the object is "dirty", false if not */
    private boolean dirty = false;

    /* True if the object is "invalid", false if not */
    private boolean invalid = false;
    
    /**
     * Returns true if the object is dirty, false if not.
     * 
     * @return True if the object is dirty, false if not
     */
    protected boolean isDirty() {
        return this.dirty;
    }
    
    /**
     * Sets whether the object is dirty or not.
     * 
     * @param dirty True to set the object to be dirty, false if not
     */
    protected void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
    
    /**
     * Returns true if the object is no longer valid, false if not.
     * 
     * @return True if the object is invalid, false if not
     */
    protected boolean isInvalid() {
        return this.invalid;
    }
    
    /**
     * Sets the object to be invalid.
     */
    protected void setInvalid() {
        this.invalid = true;
    }
    
    /**
     * Checks whether this object is invalid, and if so, throws
     * IllegalStateException.
     * 
     * @throw IllegalStateException If the WFS object is invalid
     */
    protected void checkInvalid() {
        if (this.isInvalid() == true) {
            throw new IllegalStateException("WFS Object is invalid.");
        }
    }
}
