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
package org.jdesktop.wonderland.runner.resources;

import java.io.StringWriter;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;
import org.jdesktop.wonderland.runner.RunnerChecksums;

/**
 * The ModuleChecksumsResource class is a Jersey RESTful service that returns the
 * checksum information about all deployed runner .zips
 * <p>
 * @author kaplanj
 * @author Jordan Slott <jslott@dev.java.net>
 */
@Path("/checksums")
public class RunnerChecksumsResource {
    private static final Logger logger =
            Logger.getLogger(RunnerChecksumsResource.class.getName());

    @Context
    private UriInfo uriInfo;

    /**
     * Returns the checksums information about the deployed runners
     * @return An XML encoding of the module's basic information
     */
    @GET
    @Produces("text/plain")
    public Response getModuleChecksums() {

        RunnerChecksums checksums = RunnerChecksums.generate(
                Collections.singletonList("runner"), uriInfo.getBaseUriBuilder());

        /* Write the XML encoding to a writer and return it */
        StringWriter sw = new StringWriter();
        try {
            checksums.encode(sw);
            ResponseBuilder rb = Response.ok(sw.toString());
            return rb.build();
        } catch (javax.xml.bind.JAXBException excp) {
            /* Log an error and return an error response */
            logger.log(Level.WARNING, "[ASSET] Unable to encode checksums", excp);
            ResponseBuilder rb = Response.status(Response.Status.BAD_REQUEST);
            return rb.build();
        }
    }
}
