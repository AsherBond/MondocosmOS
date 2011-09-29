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
package org.jdesktop.wonderland.modules.contentrepo.common;

import java.util.List;

/**
 * A branch node in the content structure.  Collections have a set of
 * children associated with them.
 * @author jkaplan
 */
public interface ContentCollection extends ContentNode {
    
/**
     * Get all children of this resource.  Only v
     * @return the children of this resource
     */
    public List<ContentNode> getChildren()
            throws ContentRepositoryException;

    /**
     * Get a child resource by name or path.
     * @param path the path to the given resource relative to this resource.
     * Paths are typically in Unix form, for example '/one/two/three'.
     * @return the given relative resource, or null if the resource doesn't
     * exist.
     */
    public ContentNode getChild(String path)
            throws ContentRepositoryException;


    /**
     * Add a child with the given name to this collection
     * @param name the name of this child
     * @return the newly added and empty child
     */
    public ContentNode createChild(String name, ContentNode.Type type)
            throws ContentRepositoryException;

    /**
     * Remove the child with the given name
     * @param name the name of the child to remove
     * @return the removed child, or null if no child was removed
     */
    public ContentNode removeChild(String name)
            throws ContentRepositoryException;
}
