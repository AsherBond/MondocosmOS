/**
 * Open Wonderland
 *
 * Copyright (c) 2010, Open Wonderland Foundation, All Rights Reserved
 *
 * Redistributions in source code form must reproduce the above
 * copyright and this condition.
 *
 * The contents of this file are subject to the GNU General Public
 * License, Version 2 (the "License"); you may not use this file
 * except in compliance with the License. A copy of the License is
 * available at http://www.opensource.org/licenses/gpl-license.php.
 *
 * The Open Wonderland Foundation designates this particular file as
 * subject to the "Classpath" exception as provided by the Open Wonderland
 * Foundation in the License file that accompanied this code.
 */

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.URIException;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentCollection;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode.Type;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentRepositoryException;

/**
 * Webdav implementation of ContentResource
 * @author jkaplan
 */
public class WebdavContentCollection extends WebdavContentNode
        implements ContentCollection
{
    private static final Logger logger =
            Logger.getLogger(WebdavContentCollection.class.getName());

    public WebdavContentCollection(AuthenticatedWebdavResource resource,
                                   WebdavContentCollection parent)
    {
        super (resource, parent);
    }

    public List<ContentNode> getChildren() throws ContentRepositoryException {
        List<ContentNode> out = new ArrayList<ContentNode>();
        try {
            AuthenticatedWebdavResource[] children = getResource().listWebdavResources();
            for (AuthenticatedWebdavResource child : children) {
                out.add(getContentNode(child));
            }

            return out;
        } catch (IOException ioe) {
            throw new ContentRepositoryException(ioe);
        }
    }

    public WebdavContentNode getChild(String path) throws ContentRepositoryException {
        // divide the path into a series of children
        String[] children = path.split("/");
        WebdavContentNode out = this;
        
        // walk each child, recursively getting the parent
        for (String child : children) {
            if (child.trim().length() == 0) {
                continue;
            }
            if (!(out instanceof WebdavContentCollection)) {
                logger.warning("In path " + path + " expected " + 
                               out.getName() + " to be a collection, but " +
                               "found a node");
                return null;
            }
            WebdavContentCollection parent = (WebdavContentCollection) out;
            out = parent.getChildNode(child);
            if (out == null) {
                logger.warning("In path " + path + " element " + child + 
                               " not found");
                return null;
            }
        }
        
        return out;
    }

    protected WebdavContentNode getChildNode(String name)
            throws ContentRepositoryException
    {
        // make sure this isn't a path
        if (name.contains("/")) {
            throw new ContentRepositoryException("Paths not allowed: " + name);
        }

        try {
            HttpURL url = getChildURL(getResource().getHttpURL(), name);
            
            logger.fine("[WebdavContentCollection] Get child " + name +
                        " returns " + url + " from " + this);

            AuthenticatedWebdavResource resource =
                    new AuthenticatedWebdavResource(getResource(), url);
            if (resource.getExistence()) {
                return getContentNode(resource);
            }

            return null;
        } catch (IOException ioe) {
            throw new ContentRepositoryException(ioe);
        }
    }

    public WebdavContentNode createChild(String name, Type type)
            throws ContentRepositoryException
    {
        // make sure this isn't a path
        if (name.contains("/")) {
            throw new ContentRepositoryException("Paths not allowed: " + name);
        }

        try {
            HttpURL newURL = getChildURL(getResource().getHttpURL(), name);

            logger.fine("[WebdavContentCollection] Create child " + name +
                        " returns " + newURL + " from " + this);

            AuthenticatedWebdavResource newResource =
                    new AuthenticatedWebdavResource(getResource(), newURL);
            if (newResource.exists()) {
                throw new ContentRepositoryException("Path " + newURL +
                                                     " already exists.");
            }
            switch (type) {
                case COLLECTION:
                    newResource.mkcolMethod();
                    break;
                case RESOURCE:
                    break;
            }

            return getContentNode(newResource);
        } catch (IOException ioe) {
            throw new ContentRepositoryException(ioe);
        }
    }

    public WebdavContentNode removeChild(String name)
            throws ContentRepositoryException
    {
        // make sure this isn't a path
        if (name.contains("/")) {
            throw new ContentRepositoryException("Paths not allowed: " + name);
        }

        try {
            HttpURL removeURL = getChildURL(getResource().getHttpURL(), name);
            AuthenticatedWebdavResource removeResource =
                    new AuthenticatedWebdavResource(getResource(), removeURL);
            if (removeResource.exists()) {
                removeResource.deleteMethod();
            }
            
            return getContentNode(removeResource);
        } catch (IOException ioe) {
            throw new ContentRepositoryException(ioe);
        }
    }

    protected WebdavContentNode getContentNode(AuthenticatedWebdavResource resource) {
        WebdavContentNode out;

        if (resource.isCollection()) {
            out = new WebdavContentCollection(resource, this);
        } else {
            out = new WebdavContentResource(resource, this);
        }

        logger.fine("[WebdavContentCollection] Get node for resource " +
                    resource + " returns " + out + " from " + this);
        return out;
    }

    protected HttpURL getChildURL(HttpURL parent, String childPath)
        throws URIException
    {
        if (childPath.startsWith("/")) {
            childPath = childPath.substring(1);
        }

        if (!parent.getPath().endsWith("/")) {
            parent.setPath(parent.getPath() + "/");
        }

        return new HttpURL(parent, childPath);
    }
}
