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
package org.jdesktop.wonderland.common.wfs;

import java.io.File;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Describes the parent of a cell by a slash-separated path of parent cells.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@XmlRootElement(name="wfs-cell-parent")
public class CellPath {
    @XmlElement(name = "parent-path")
    private String parentPath = null;
    
    /** Default constructor */
    public CellPath() {
    }
    
    /** Constructor, takes the parent's path */
    public CellPath(String parentPath) {
        this.parentPath = parentPath;
    }

    /**
     * Returns a slash-separated path of parent cell names
     * @return The path of the parent cell in the wfs hierarchy
     */
    @XmlTransient
    public String getParentPath() {
        return parentPath;
    }
    
    /**
     * Returns an array of strings, each representing the name of the parents
     * @return An array of parent cell names
     */
    public String[] getParentPaths() {
        return parentPath.split(File.separator);
    }
    
    /**
     * Returns a new CellPath object that represents the path of a child of this
     * parent, given the name of the child.
     * 
     * @param childName The name of the child 
     * @return A new CellPath object for the child
     */
    public CellPath getChildPath(String childName) {
        return new CellPath(parentPath + File.separator + childName);
    }
    
    @Override
    public String toString() {
        return this.parentPath;
    }
}
