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
package org.jdesktop.wonderland.modules.webdav.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode.Type;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;

/**
 * Webdav implementation of ContentResource
 * @author jkaplan
 */
public class FileContentCollection extends FileContentNode
        implements ContentCollection
{
    public FileContentCollection(File file, FileContentCollection parent) {
        super (file, parent);
    }

    public List<ContentNode> getChildren() throws ContentRepositoryException {
        List<ContentNode> out = new ArrayList<ContentNode>();

        File[] children = getFile().listFiles();
        for (File child : children) {
            out.add(getContentNode(child));
        }

        return out;
    }

    public FileContentNode getChild(String path) throws ContentRepositoryException {
        FileContentNode out = this;

        String[] children = path.split("/");
        for (String child : children) {
            if (child.trim().length() == 0) {
                continue;
            }
            if (!(out instanceof FileContentCollection)) {
                return null;
            }
            out = ((FileContentCollection) out).getChildNode(child);
            if (out == null) {
                return null;
            }
        }

        return out;
    }

    public FileContentNode createChild(String name, Type type)
            throws ContentRepositoryException
    {
        File childFile = getChildFile(name);
        if (childFile.exists()) {
            throw new ContentRepositoryException("Path " + childFile.getPath() +
                                                 " already exists.");
        }

        if (type == ContentNode.Type.COLLECTION) {
            childFile.mkdir();
        }

        return getContentNode(childFile);
    }

    public FileContentNode removeChild(String name)
            throws ContentRepositoryException
    {
        File childFile = getChildFile(name);
        if (childFile.exists()) {
            childFile.delete();
        }
            
        return getContentNode(childFile);
    }

    protected FileContentNode getChildNode(String childName) {
        File childFile = getChildFile(childName);
        if (!childFile.exists()) {
            return null;
        }

        return getContentNode(childFile);
    }
    

    protected FileContentNode getContentNode(File file) {
        if (file.isDirectory()) {
            return new FileContentCollection(file, this);
        } else {
            return new FileContentResource(file, this);
        }
    }

    protected File getChildFile(String childPath) {
        // TODO : filter path to avoid things like ".."

        if (childPath.startsWith("/")) {
            childPath = childPath.substring(1);
        }

        if (File.separatorChar != '/') {
            childPath.replace('/', File.separatorChar);
        }

        return new File(getFile(), childPath);
    }
}
