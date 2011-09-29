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
 * A collection representing the art/ directory beneath a module.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ArtContentCollection implements ContentCollection {

    // The delegate for the collection that represents the module.
    private ContentCollection artCollection = null;
    private ContentCollection parentCollection = null;

    /** Default constructor */
    public ArtContentCollection(ContentCollection artCollection,
            ContentCollection parentCollection) {

        this.artCollection = artCollection;
        this.parentCollection = parentCollection;
    }

    /**
     * @inheritDoc()
     */
    public List<ContentNode> getChildren() throws ContentRepositoryException {
        // Simply return all of the children using the ContentCollection proxy
        // object. We just return whatever object it returns since we no longer
        // need to wrap objects below this art/ directory.
//        return artCollection.getChildren();

        List<ContentNode> childList = new LinkedList();
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
        // Simply return all of the children using the ContentCollection proxy
        // object. We just return whatever object it returns since we no longer
        // need to wrap objects below this art/ directory.
        return artCollection.getChild(path);
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
        return artCollection.getName();
    }

    /**
     * @inheritDoc()
     */
    public String getPath() {
        return parentCollection.getPath() + "/" + artCollection.getName();
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
