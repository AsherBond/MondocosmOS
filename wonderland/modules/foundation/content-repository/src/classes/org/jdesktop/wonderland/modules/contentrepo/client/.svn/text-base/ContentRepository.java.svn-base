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
package org.jdesktop.wonderland.modules.contentrepo.client;

import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;


/**
 * A pluggable mechanism for storing files associated with a Wonderland
 * server.
 * <p>
 * A content repository is a set of named files that can be created, updated
 * and removed.  This is similar to a file, but accessed remotely.
 *
 * @author jkaplan
 */
public interface ContentRepository {
    /**
     * Get the root of the repository, typically the parent of the system
     * and user roots.
     * @return the root
     */
    public ContentCollection getRoot() throws ContentRepositoryException;

    /**
     * Get the root of the system content tree
     * @return the system root resource
     */
    public ContentCollection getSystemRoot() throws ContentRepositoryException;

    /**
     * Get the root for the current user, and create it if it doesn't exist.
     * Identical to calling <code>getUserRoot(true)</code>
     * @return the current user's root, creating it if it doesn't exist
     */
    public ContentCollection getUserRoot() throws ContentRepositoryException;

    /**
     * Get the root for the current user, optionally creating it if it doesn't
     * exist.
     * @param create whether or not to create the root
     * @return the current user's root or null if the current user's root
     * doesn't exist and create is false
     */
    public ContentCollection getUserRoot(boolean create)
            throws ContentRepositoryException;

    /**
     * Get the root of the content tree for a given user.
     * @return the root resource for the given user, or null if
     * there is no root resource for the given user
     */
    public ContentCollection getUserRoot(String userId)
            throws ContentRepositoryException;
}
