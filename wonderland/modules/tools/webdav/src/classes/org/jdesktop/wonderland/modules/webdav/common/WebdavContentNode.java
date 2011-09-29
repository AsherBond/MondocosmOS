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

import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;

/**
 * Webdav implementation of ContentResource
 * @author jkaplan
 */
public class WebdavContentNode implements ContentNode {
    private AuthenticatedWebdavResource resource;
    private WebdavContentCollection parent;

    WebdavContentNode(AuthenticatedWebdavResource resource,
                      WebdavContentCollection parent)
    {
        this.resource = resource;
        this.parent = parent;
    }

    public String getName() {
        return getResource().getName();
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

    public WebdavContentCollection getParent() {
        return parent;
    }

    protected AuthenticatedWebdavResource getResource() {
        return resource;
    }
}
