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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Date;
import org.jdesktop.wonderland.common.FileUtils;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;

/**
 * Webdav implementation of ContentResource
 * @author jkaplan
 */
public class FileContentResource extends FileContentNode
        implements ContentResource
{
    FileContentResource(File file, FileContentCollection parent) {
        super (file, parent);
    }

    public long getSize() {
        return getFile().length();
    }

    public Date getLastModified() {
        return new Date(getFile().lastModified());
    }

    public FileInputStream getInputStream() throws ContentRepositoryException {
        try {
            return new FileInputStream(getFile());
        } catch (IOException ioe) {
            throw new ContentRepositoryException(ioe);
        }
    }

    public URL getURL() throws ContentRepositoryException {
        try {
            String p = getPath();
            if (p.startsWith("/"))
                p = p.substring(1);
            return new URL(getBaseURL(), p);
        } catch (IOException ioe) {
            throw new ContentRepositoryException(ioe);
        }
    }

    public void get(File file) throws ContentRepositoryException, IOException {
        FileChannel in = getInputStream().getChannel();
        FileChannel out = new FileOutputStream(file).getChannel();

        in.transferTo(0, getFile().length(), out);
    }

    public void put(byte[] data) throws ContentRepositoryException {
        try {
            FileOutputStream fos = new FileOutputStream(getFile());
            fos.write(data);
        } catch (IOException ioe) {
            throw new ContentRepositoryException(ioe);
        }
    }

    public void put(File file) throws ContentRepositoryException, IOException {
        FileChannel in = new FileInputStream(file).getChannel();
        FileChannel out = new FileOutputStream(getFile()).getChannel();

        in.transferTo(0, file.length(), out);
    }

    public void put(InputStream in) throws ContentRepositoryException, IOException {
        FileOutputStream out = new FileOutputStream(getFile());
        FileUtils.copyFile(in, out);
    }
}
