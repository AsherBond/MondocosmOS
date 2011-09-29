/**
 * Project Wonderland
 *
 * Copyright (c) 2004-2010, Sun Microsystems, Inc., All Rights Reserved
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
package org.jdesktop.wonderland.web.checksums.resources;

import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.jdesktop.wonderland.common.checksums.ChecksumList;
import org.jdesktop.wonderland.web.checksums.ChecksumFactory;
import org.jdesktop.wonderland.web.checksums.ChecksumFactory.ChecksumAction;
import org.jdesktop.wonderland.web.checksums.ChecksumManager;
import org.jdesktop.wonderland.web.checksums.content.ContentAssetDescriptor;

/**
 * A Jersey RESTful web service that returns a Checksum for an asset stored
 * within the content repository.
 * <p>
 * The URI of this web services interface is:
 * <p>
 * http://<host>:<port>/<prefix>/<content root>/checksums/get/<asset path>
 * <p>
 * where <host> and <port> are the hostname and port of the web server, the
 * <prefix> is the standard web service prefix (e.g. wonderland-web-checksums/
 * checksums), <content root> is the path to the root of the content repository
 * (e.g. users/<user name>, system) and <asset path> is the relative path of
 * the asset beneat the root.
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@Path("/content/{contentroot:.*}/checksums/get/{assetpath:.*}")
public class GetContentChecksumResource {

    private static Logger logger = Logger.getLogger(GetContentChecksumResource.class.getName());

    @GET
    @Produces({ "application/xml", "applications/json" })
    public Response getChecksum(@PathParam("contentroot") String contentRoot,
            @PathParam("assetpath") String assetPath) {

        // Fetch the checksum directly from the manager and return it. If we
        // cannot find the factory to handle the content assets, then return
        // an error (this should never happen).
        ChecksumManager checksumManager = ChecksumManager.getChecksumManager();
        ContentAssetDescriptor cad = new ContentAssetDescriptor(contentRoot, assetPath);
        ChecksumFactory checksumFactory = checksumManager.getChecksumFactory(cad);
        if (checksumFactory == null) {
            logger.warning("Unable to find checksum factory for " + cad);
            return Response.noContent().build();
        }

        // Ask the factory for the asset. If it does not exist, then ask it to
        // create the checksum
        ChecksumList checksumList = checksumFactory.getChecksumList(cad, ChecksumAction.GENERATE);
        if (checksumList == null) {
            logger.warning("Unable to generate checksum for " + contentRoot +
                    " " + assetPath);
            return Response.noContent().build();
        }
        return Response.ok(checksumList).build();
    }
}
