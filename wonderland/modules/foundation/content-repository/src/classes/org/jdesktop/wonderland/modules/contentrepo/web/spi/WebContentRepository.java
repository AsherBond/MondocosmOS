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
package org.jdesktop.wonderland.modules.contentrepo.web.spi;

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
public interface WebContentRepository {
    /**
     * Get the root of the content tree
     * @return the root, the parent of system and users
     */
    public ContentCollection getRoot() throws ContentRepositoryException;

    /**
     * Get the root of the system content tree
     * @return the system root resource
     */
    public ContentCollection getSystemRoot() throws ContentRepositoryException;

    /**
     * Get the root of the content tree for a given user. Will not
     * create the resource if it doesn't exist.  Identical to 
     * calling <code>getUserRoot(userId, false)</code>.
     * @param userId the user to get the content root for
     * @return the root resource for the given user, or null if
     * there is no root resource for the given user
     */
    public ContentCollection getUserRoot(String userId)
            throws ContentRepositoryException;

    /**
     * Get the root of the content tree for a given user.
     * @param userId the user to get the content root for
     * @param create if true, create the user root directory if it doesn't
     * exist
     * @return the root resource for the given user, or null if
     * there is no root resource for the given user and create is false
     */
    public ContentCollection getUserRoot(String userId, boolean create)
            throws ContentRepositoryException;
}
