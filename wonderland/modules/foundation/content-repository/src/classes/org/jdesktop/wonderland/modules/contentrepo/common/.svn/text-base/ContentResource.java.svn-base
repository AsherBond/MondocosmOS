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
package org.jdesktop.wonderland.modules.contentrepo.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * A leaf node in the content structure.  Resources have properties and
 * data associated with them.
 * @author jkaplan
 */
public interface ContentResource extends ContentNode {
    /**
     * Get the size of this resource
     * @return the file size of this resource, or -1 if the size isn't known
     */
    public long getSize();

    /**
     * Get the time this file was last modified.
     * @return the file's last modification time, in standard Java
     * Date format
     */
    public Date getLastModified();

    /**
     * Get the content of this resource as an input stream.
     * @return an input stream with content for this node
     */
    public InputStream getInputStream() throws ContentRepositoryException;

    /**
     * Get a URL for access to this resources's data.
     * @return the URL for this resource
     */
    public URL getURL() throws ContentRepositoryException;

    /**
     * Read the content of this node into the given file.
     * @param file the file to write to
     * @throws IOException if there is an error writing the file
     */
    public void get(File file) throws ContentRepositoryException, IOException;

    /**
     * Write the given data to this resource.
     * @param data the data to write
     */
    public void put(byte[] data) throws ContentRepositoryException;

    /**
     * Write the contents of the given file to this resource.
     * @param file the file to write
     * @throws IOException if there is an error reading the file
     */
    public void put(File file) throws ContentRepositoryException, IOException;

    /**
     * Write the contents of the given input stream to this resource.
     * @param is the input stream to write
     * @throws IOException if there is an error reading the file
     */
    public void put(InputStream is) throws ContentRepositoryException, IOException;
}
