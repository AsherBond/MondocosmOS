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
package org.jdesktop.wonderland.web.help.resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import org.jdesktop.wonderland.web.help.deployer.HelpDeployer;

/**
 * The GetHelpResource class is a Jersey RESTful service that returns some
 * help page content that is contained with a module.
 * 
 * @author Jordan Slott <jslott@dev.java.net>
 */
@Path(value="/{modulename}/help/get/{path:.*}")
public class GetHelpResource {
    
    /**
     * TBD
     * 
     * @param moduleName The unique name of the module
     * @return An XML encoding of the module's basic information
     */
    @GET
    @Produces({"text/html", "images/*"})
    public Response getHelp(@PathParam("modulename") String moduleName,
            @PathParam("path") String path) {
        
        Logger logger = Logger.getLogger(GetHelpResource.class.getName());
        
        /*
         * If the path has a leading slash, then remove it (this is typically
         * the case with @PathParam).
         */
        if (path.startsWith("/") == true) {
            path = path.substring(1);
        }
        
        /*
         * Get a map of all of the File objects for each art asset. We use the
         * convention that the first element of the 'path' is the asset type
         * (e.g. art, client, etc). Look for the entry that matches.
         */
        Map<String, File> assetMap = HelpDeployer.getFileMap();
        File root = assetMap.get(moduleName);
        if (root == null) {
            /* Log an error and return an error response */
            logger.warning("[HELP] Unable to locate module " + moduleName);
            ResponseBuilder rb = Response.status(Response.Status.BAD_REQUEST);
            return rb.build();
        }
        
        File file = new File(root, path);
        if (file.exists() == false || file.isDirectory() == true) {
            /* Write an error to the log and return */
            logger.warning("[HELP] Unable to locate resource " + path +
                    " in module " + moduleName);
            ResponseBuilder rb = Response.status(Response.Status.BAD_REQUEST);
            return rb.build();
        }
        
        /* Encode in an HTTP response and send */
        try {
            InputStream is = new FileInputStream(file);
            ResponseBuilder rb = Response.ok(is);
            return rb.build();
        } catch (Exception excp) {
            logger.log(Level.WARNING, "[HELP] Unable to locate resource", excp);
            ResponseBuilder rb = Response.status(Response.Status.BAD_REQUEST);
            return rb.build();            
        }
    }
}
