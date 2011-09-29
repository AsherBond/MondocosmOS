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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;

/**
 * Webdav implementation of ContentResource
 * @author jkaplan
 */
public class WebdavContentResource extends WebdavContentNode 
        implements ContentResource
{
    WebdavContentResource(AuthenticatedWebdavResource resource,
                          WebdavContentCollection parent)
    {
        super (resource, parent);
    }

    public long getSize() {
        return getResource().getGetContentLength();
    }

    public Date getLastModified() {
        return new Date(getResource().getGetLastModified());
    }

    public InputStream getInputStream() throws ContentRepositoryException {
        try {
            return getResource().getMethodData();
        } catch (IOException ioe) {
            throw new ContentRepositoryException(ioe);
        }
    }

    public URL getURL() throws ContentRepositoryException {
        try {
            return new URL(getResource().getHttpURL().toString());
        } catch (IOException ioe) {
            throw new ContentRepositoryException(ioe);
        }
    }

    public void get(File file) throws ContentRepositoryException, IOException {
        getResource().getMethod(file);
    }

    public void put(byte[] data) throws ContentRepositoryException {
        try {
            getResource().putMethod(data);
        } catch (IOException ioe) {
            throw new ContentRepositoryException(ioe);
        }
    }

    public void put(File file) throws ContentRepositoryException, IOException {
        // don't use putMethod(file), since that doesn't properly generate
        // request headers
        getResource().putMethod(new FileInputStream(file));
    }

    public void put(InputStream is) throws ContentRepositoryException, IOException {
        getResource().putMethod(is);
    }
}
