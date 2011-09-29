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
import java.net.URL;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;

/**
 * Webdav implementation of ContentResource
 * @author jkaplan
 */
public class FileContentNode implements ContentNode {
    private File file;
    private FileContentCollection parent;
    private URL baseURL;

    FileContentNode(File file, FileContentCollection parent) {
        this.file = file;
        this.parent = parent;
        
        if (parent != null) {
            this.baseURL = parent.getBaseURL();
        }
    }

    public String getName() {
        return getFile().getName();
    }

    public String getPath() {
        if (getParent() == null) {
            return "/" + getName();
        } else {
            return getParent().getPath() + "/" + getName();
        }
    }

    public boolean canWrite() {
        return true;
    }

    public FileContentCollection getParent() {
        return parent;
    }

    protected File getFile() {
        return file;
    }

    protected URL getBaseURL() {
        return baseURL;
    }
}
