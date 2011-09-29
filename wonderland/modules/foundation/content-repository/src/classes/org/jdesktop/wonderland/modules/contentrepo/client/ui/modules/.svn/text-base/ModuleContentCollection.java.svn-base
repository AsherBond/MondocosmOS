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
package org.jdesktop.wonderland.modules.contentrepo.client.ui.modules;

import java.util.LinkedList;
import java.util.List;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode.Type;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;

/**
 * A collection representing a single module.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ModuleContentCollection implements ContentCollection {

    // The delegate for the collection that represents the module.
    private ContentCollection moduleCollection = null;
    private ContentCollection parentCollection = null;

    /** Default constructor */
    public ModuleContentCollection(ContentCollection moduleCollection,
            ContentCollection parentCollection) {

        this.moduleCollection = moduleCollection;
        this.parentCollection = parentCollection;
    }

    /**
     * @inheritDoc()
     */
    public List<ContentNode> getChildren() throws ContentRepositoryException {
        // In this case, we find the content collection with "art/" and return
        // the children of that.
        List<ContentNode> childList = new LinkedList();
        ContentNode node = moduleCollection.getChild("/art");
        if (node == null || !(node instanceof ContentCollection)) {
            return childList;
        }

        // Now find the children of the art/ subdir and return nodes that wrap
        // the children. This makes it so the art/ does not appear in the tree
        // at all.
        ContentCollection artCollection = (ContentCollection)node;
        for (ContentNode child : artCollection.getChildren()) {
            if (child instanceof ContentCollection) {
                childList.add(new ArtContentCollection((ContentCollection)child, this));
            }
            else {
                childList.add(new ArtContentResource((ContentResource)child, this));
            }
        }
        return childList;
    }

    /**
     * @inheritDoc()
     */
    public ContentNode getChild(String path) throws ContentRepositoryException {
        // XXX
        // Need to implement
        // XXX
        return null;
    }

    /**
     * @inheritDoc()
     */
    public ContentNode createChild(String name, Type type) throws ContentRepositoryException {
        throw new ContentRepositoryException("Removing child not supported");
    }

    /**
     * @inheritDoc()
     */
    public ContentNode removeChild(String name) throws ContentRepositoryException {
        throw new ContentRepositoryException("Removing child not supported");
    }

    /**
     * @inheritDoc()
     */
    public String getName() {
        return moduleCollection.getName();
    }

    /**
     * @inheritDoc()
     */
    public String getPath() {
        return parentCollection.getPath() + "/" + moduleCollection.getName();
    }

    /**
     * @inheritDoc()
     */
    public boolean canWrite() {
        return false;
    }

    /**
     * @inheritDoc()
     */
    public ContentCollection getParent() {
        return parentCollection;
    }
}
