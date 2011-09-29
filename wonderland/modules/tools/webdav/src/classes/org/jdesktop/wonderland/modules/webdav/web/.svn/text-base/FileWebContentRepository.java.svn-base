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
package org.jdesktop.wonderland.modules.webdav.web;

import java.io.File;
import java.net.URL;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.web.spi.WebContentRepository;
import org.jdesktop.wonderland.modules.webdav.common.FileContentCollection;

/**
 * File-based implementation of a web content repository
 * @author jkaplan
 */
public class FileWebContentRepository implements WebContentRepository {
    private File root;
    private FileContentCollection rootCollection;

    public FileWebContentRepository(File root, final URL baseURL) {
        this.root = root;

        rootCollection = new FileContentCollection(root, null) {
            // don't include this path
            @Override
            public String getPath() {
                return "";
            }

            // add a base URL
            @Override
            public URL getBaseURL() {
                return baseURL;
            }
        };
    }

    public FileContentCollection getRoot() {
        return rootCollection;
    }

    public FileContentCollection getSystemRoot() throws ContentRepositoryException {
        return (FileContentCollection) rootCollection.getChild("system");
    }

    public FileContentCollection getUserRoot(String userId)
            throws ContentRepositoryException
    {
        return getUserRoot(userId, false);
    }

    public FileContentCollection getUserRoot(String userId, boolean create)
            throws ContentRepositoryException
    {
        FileContentCollection users = (FileContentCollection)
                rootCollection.getChild("users");
        FileContentCollection user = (FileContentCollection)
                users.getChild(userId);
        if (user == null && create) {
            user = (FileContentCollection)
                    users.createChild(userId, ContentNode.Type.COLLECTION);
        }

        return user;
    }

}
