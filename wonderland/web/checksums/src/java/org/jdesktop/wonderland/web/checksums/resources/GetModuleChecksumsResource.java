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

import java.io.File;
import java.util.Map;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.jdesktop.wonderland.common.checksums.ChecksumList;
import org.jdesktop.wonderland.web.asset.deployer.AssetDeployer;
import org.jdesktop.wonderland.web.asset.deployer.AssetDeployer.DeployedAsset;
import org.jdesktop.wonderland.web.checksums.ChecksumFactory;
import org.jdesktop.wonderland.web.checksums.ChecksumFactory.ChecksumAction;
import org.jdesktop.wonderland.web.checksums.ChecksumManager;
import org.jdesktop.wonderland.web.checksums.modules.ModuleAssetDescriptor;


/**
 * A Jersey RESTful web service that returns a Checksum for assets stored
 * within a module. Returns either a list of checksums for an entire module,
 * or just one part of a module.
 * <p>
 * The URI of this web services interface is one of two forms:
 * <p>
 * http://<host>:<port>/<prefix>/<module name>/checksums/get
 * http://<host>:<port>/<prefix>/<module name>/checksums/get/<module part>
 * <p>
 * where <host> and <port> are the hostname and port of the web server, the
 * <prefix> is the standard web service prefix (e.g. wonderland-web-checksums/
 * checksums), <module name> is the name of the module, and <module part> is
 * the name of the module part (e.g. "client", "art").
 *
 * @author Jordan Slott <jslott@dev.java.net>
 */
@Path("/modules/{modulename}/checksums/get")
public class GetModuleChecksumsResource {

    private static Logger logger = Logger.getLogger(GetModuleChecksumsResource.class.getName());

    @GET
    @Produces({ "application/xml", "application/json" })
    @Path("/{assettype}")
    public Response getChecksums(@PathParam("modulename") String moduleName,
            @PathParam("modulePart") String modulePart) {

        // Fetch all of the module parts given the module name. This case
        // handles if the module part is null (in which case all module parts)
        ChecksumManager checksumManager = ChecksumManager.getChecksumManager();
        Map<DeployedAsset, File> partMap = AssetDeployer.getFileMap(moduleName, modulePart);

        logger.warning("Fetching checksums for module " + moduleName + " part " +
                modulePart);

        // Create a checksum list from all of the individual module parts and
        // put them into a single map.
        ChecksumList checksumList = new ChecksumList();
        for (DeployedAsset deployedAsset : partMap.keySet()) {
            // Create an proper AssetDeployer using the module name and module
            // part. Add to the master list.
            ModuleAssetDescriptor mad = new ModuleAssetDescriptor(
                    deployedAsset.moduleName, deployedAsset.assetType, null);
            ChecksumFactory factory = checksumManager.getChecksumFactory(mad);
            ChecksumList partList = factory.getChecksumList(mad, ChecksumAction.DO_NOT_GENERATE);
            if (partList != null) {
                logger.info("Adding found part " + deployedAsset.assetType +
                        " with size " + partList.getChecksumMap().size());
                checksumList.putChecksums(partList.getChecksumMap());
            }
        }

        ResponseBuilder rb = Response.ok(checksumList);
        return rb.build();
    }

    @GET
    @Produces({ "application/xml", "application/json" })
    public Response getChecksums(@PathParam("modulename") String moduleName) {
        return getChecksums(moduleName, null);
    }
}
