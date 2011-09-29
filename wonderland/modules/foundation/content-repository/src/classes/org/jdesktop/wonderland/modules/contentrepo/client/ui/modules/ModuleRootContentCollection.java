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
import org.jdesktop.wonderland.modules.contentrepo.client.ContentRepository;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode.Type;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;

/**
 * A collection representing the set of modules.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ModuleRootContentCollection implements ContentCollection {

    private ContentCollection rootCollection = null;

    /** Default constructor */
    public ModuleRootContentCollection(ContentRepository repository) throws ContentRepositoryException {
        // Fetch the root collection, which should be under the "modules"
        // directory
        rootCollection = (ContentCollection)repository.getRoot().getChild("/modules/installed");
    }

    /**
     * @inheritDoc()
     */
    public List<ContentNode> getChildren() throws ContentRepositoryException {
        // Loop through all of the children of the root collection and create
        // a new list of content collections wrapped specifically for modules.
        List<ContentNode> moduleList = new LinkedList();
        for (ContentNode child : rootCollection.getChildren()) {
            if (child instanceof ContentCollection) {
                // Only add the child if it contains an art/ subdirectory. In
                // order to figure this out, we need to form the wrapper for
                // the module node and see if it has children.
                ContentCollection c = (ContentCollection)child;
                ModuleContentCollection mcc = new ModuleContentCollection(c, this);
                if (mcc.getChildren().isEmpty() == false) {
                    moduleList.add(mcc);
                }
            }
        }
        return moduleList;
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
        return "Modules";
    }

    /**
     * @inheritDoc()
     */
    public String getPath() {
        return "/modules";
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
        return null;
    }
}
