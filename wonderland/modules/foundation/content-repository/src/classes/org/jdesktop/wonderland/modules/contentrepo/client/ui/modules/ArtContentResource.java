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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;

/**
 * A resources representing the first set of child nodes beneath the art/
 * directory.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
public class ArtContentResource implements ContentResource {

    // The delegate for the resource that represents the artwork
    private ContentResource artResource = null;
    private ContentCollection parentCollection = null;

    /** Default constructor */
    public ArtContentResource(ContentResource artResource,
            ContentCollection parentCollection) {

        this.artResource = artResource;
        this.parentCollection = parentCollection;
    }

    /**
     * @inheritDoc()
     */
    public String getName() {
        return artResource.getName();
    }

    /**
     * @inheritDoc()
     */
    public String getPath() {
        return parentCollection.getPath() + "/" + artResource.getName();
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

    /**
     * @inheritDoc()
     */
    public long getSize() {
        return artResource.getSize();
    }

    /**
     * @inheritDoc()
     */
    public Date getLastModified() {
        return artResource.getLastModified();
    }

    /**
     * @inheritDoc()
     */
    public InputStream getInputStream() throws ContentRepositoryException {
        return artResource.getInputStream();
    }

    /**
     * @inheritDoc()
     */
    public URL getURL() throws ContentRepositoryException {
        return artResource.getURL();
    }

    /**
     * @inheritDoc()
     */
    public void get(File file) throws ContentRepositoryException, IOException {
        artResource.get(file);
    }

    /**
     * @inheritDoc()
     */
    public void put(byte[] data) throws ContentRepositoryException {
        artResource.put(data);
    }

    /**
     * @inheritDoc()
     */
    public void put(File file) throws ContentRepositoryException, IOException {
        artResource.put(file);
    }

    /**
     * @inheritDoc()
     */
    public void put(InputStream is) throws ContentRepositoryException, IOException {
        artResource.put(is);
    }
}
