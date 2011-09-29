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

/**
 * The base class for Resources and Collections
 * @author jkaplan
 */
public interface ContentNode {
    /** types of node */
    public enum Type { RESOURCE, COLLECTION };

    /**
     * Get the name of this resource
     * @return the resource's name
     */
    public String getName();

    /**
     * Get the full path of this resource from the root
     * @return the path of this resource from the root
     */
    public String getPath();

    /**
     * Whether or not the current user has permission to write this
     * resource.
     * @return true if the current user can write this resource, or false
     * if not
     */
    public boolean canWrite();

    /**
     * Get the parent of this node
     * @return the parent of this node, or null if this is the top-level
     * component
     */
    public ContentCollection getParent();
}
